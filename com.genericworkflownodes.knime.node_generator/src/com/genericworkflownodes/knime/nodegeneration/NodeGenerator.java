/*
 * Copyright (c) 2011-2012, Marc Röttig.
 * Copyright (c) 2012, Björn Kahlert.
 * Copyright (c) 2012, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genericworkflownodes.knime.nodegeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.FragmentDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesBuildDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.files.CTDFile;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.BuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.ManifestMFTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginActivatorTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.ProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentManifestMFTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentP2InfTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeDialogTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeFactoryTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeFactoryXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeModelTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.testingfeature.TestingFeatureBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.testingfeature.TestingFeatureProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.testingfeature.TestingFeatureXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.util.UnZipFailureException;
import com.genericworkflownodes.knime.nodegeneration.util.Utils;
import com.genericworkflownodes.knime.nodegeneration.writer.PropertiesWriter;

/**
 * This class is responsible for generating KNIME plugins.
 * 
 * @author bkahlert
 * 
 */
public class NodeGenerator {
    private static final Logger LOGGER = Logger.getLogger(NodeGenerator.class
            .getCanonicalName());

    public static class NodeGeneratorException extends Exception {
        private static final long serialVersionUID = 1L;

        private NodeGeneratorException(Throwable t) {
            super(t);
        }

        private NodeGeneratorException(String message, Throwable t) {
            super(message, t);
        }

        private NodeGeneratorException(String message) {
            super(message);
        }
    }

    private final String nodeGeneratorLastChangeDate;

    private final NodesSourceDirectory srcDir;
    private final GeneratedPluginMeta generatedPluginMeta;
    private final FeatureMeta featureMeta;
    private List<FragmentMeta> fragmentMetas;
    private List<ContributingPluginMeta> contributingPluginMetas;
    private NodesBuildDirectory pluginBuildDir;

    /**
     * The directory where all the individual, generated plugins will be
     * located.
     */
    private final Directory baseBinaryDirectory;

    /**
     * Initializes a new {@link NodeGenerator} instance.
     * 
     * @param sourceDir
     *            directory that contains the plugin's sources.
     * @param buildDir
     *            directory that will contain the generated plugin (the
     *            directory will not be flushed before the generation)
     * @param lastChangeDate
     *            Last change date of the node generator / GKN.
     * @throws NodeGeneratorException
     */
    public NodeGenerator(File sourceDir, File buildDir, String lastChangeDate)
            throws NodeGeneratorException {
        try {
            if (buildDir == null)
                throw new NodeGeneratorException("buildDir must not be null");

            nodeGeneratorLastChangeDate = lastChangeDate;

            baseBinaryDirectory = new Directory(buildDir);
            baseBinaryDirectory.mkdir();
            if (baseBinaryDirectory.list().length != 0) {
                LOGGER.warning("The given buildDir is not empty: Will clean the directory.");
                for (File file : baseBinaryDirectory.listFiles())
                    if (file.isDirectory())
                        FileUtils.deleteDirectory(file);
                    else
                        file.delete();
            }

            srcDir = new NodesSourceDirectory(sourceDir);
            generatedPluginMeta = new GeneratedPluginMeta(srcDir,
                    nodeGeneratorLastChangeDate);
            featureMeta = new FeatureMeta(srcDir, generatedPluginMeta);
            pluginBuildDir = new NodesBuildDirectory(buildDir,
                    generatedPluginMeta.getPackageRoot());
            contributingPluginMetas = srcDir.getContributingPluginsDirectory()
                    .getContributingPluginMetas();
            fragmentMetas = new ArrayList<FragmentMeta>();
        } catch (Exception e) {
            throw new NodeGeneratorException(e);
        }
    }

    public void generate() throws NodeGeneratorException {
        LOGGER.info("Creating KNIME plugin sources\n\tFrom: " + srcDir
                + "\n\tTo: " + pluginBuildDir);

        try {
            DescriptorsDirectory descriptorsDirectory = srcDir
                    .getDescriptorsDirectory();

            // build.properties - only useful if you re-import the generated
            // node in
            // Eclipse
            new BuildPropertiesTemplate().write(pluginBuildDir
                    .getBuildProperties());

            // META-INF/MANIFEST.MF
            new ManifestMFTemplate(generatedPluginMeta).write(pluginBuildDir
                    .getManifestMf());

            // src/[PACKAGE]/knime/plugin.properties
            new PropertiesWriter(new File(pluginBuildDir.getKnimeDirectory(),
                    "plugin.properties")).write(new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put("executor",
                            srcDir.getProperty("executor", "LocalToolExecutor"));
                    put("commandGenerator", srcDir.getProperty(
                            "commandGenerator", "CLICommandGenerator"));
                }
            });

            PluginXMLTemplate pluginXML = new PluginXMLTemplate();
            List<String> nodeNames = new LinkedList<String>();
            List<INodeConfiguration> configurations = new ArrayList<INodeConfiguration>();

            // src/[PACKAGE]/knime/nodes/*/*
            for (CTDFile ctdFile : descriptorsDirectory.getCTDFiles()) {
                LOGGER.info("Start processing ctd file: " + ctdFile.getName());

                configurations.add(ctdFile.getNodeConfiguration());
                nodeNames.add(ctdFile.getNodeConfiguration().getName());

                String factoryClass = copyNodeSources(ctdFile);

                String absoluteCategory = "/"
                        + generatedPluginMeta.getNodeRepositoryRoot() + "/"
                        + generatedPluginMeta.getName() + "/"
                        + ctdFile.getNodeConfiguration().getCategory();
                pluginXML.registerNode(factoryClass, absoluteCategory);
            }

            // src/[PACKAGE]/knime/PluginActivator.java
            new PluginActivatorTemplate(generatedPluginMeta, configurations)
                    .write(new File(pluginBuildDir.getKnimeDirectory(),
                            "PluginActivator.java"));

            // icons/*
            copyFolderIcon();
            registerSplashIcon(pluginXML);

            // register the mime types
            pluginXML.registerMIMETypeEntries(srcDir.getMIMETypes());

            // plugin.xml
            pluginXML.saveTo(pluginBuildDir.getPluginXml());

            // .project
            new ProjectTemplate(generatedPluginMeta.getPackageRoot())
                    .write(pluginBuildDir.getProjectFile());

            // src/[PACKAGE]/knime/nodes/binres/*.ini *.zip
            if (srcDir.getPayloadDirectory() != null) {
                // create payload fragments
                fragmentMetas = createPayloadFragments();
            }

            // copy assets
            copyAsset(".classpath");
            copyAsset("buckminster.cspex");

            copyContributingPlugins();

            // create feature
            generateFeature();

            // create testing feature
            generateTestingFeature();

            LOGGER.info("KNIME plugin sources successfully created in:\n\t"
                    + pluginBuildDir);
        } catch (Exception e) {
            LOGGER.info("KNIME plugin source creation failed");
            throw new NodeGeneratorException(e);
        }
    }

    private void copyContributingPlugins() {
        for (ContributingPluginMeta contributingPluginMeta : contributingPluginMetas) {
            try {
                // TODO: Handle compiled classes in bin/ or build/ (maybe check
                // build.properties for output folder)
                FileUtils
                        .copyDirectory(contributingPluginMeta
                                .getContributingPluginDirectory(), new File(
                                baseBinaryDirectory, contributingPluginMeta
                                        .getContributingPluginDirectory()
                                        .getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a separate fragment for each binaries_..zip file found in the
     * payload directory.
     * 
     * @throws NodeGeneratorException
     * @throws PathnameIsNoDirectoryException
     * @throws IOException
     * @throws UnZipFailureException
     */
    private List<FragmentMeta> createPayloadFragments()
            throws NodeGeneratorException, PathnameIsNoDirectoryException,
            IOException, UnZipFailureException {

        List<FragmentMeta> createdFragments = srcDir.getPayloadDirectory()
                .getFragmentMetas(generatedPluginMeta);

        for (FragmentMeta fragmentMeta : createdFragments) {
            LOGGER.info(String.format("Creating binary fragment %s",
                    fragmentMeta.getId()));

            FragmentDirectory fragmentDir = new FragmentDirectory(
                    baseBinaryDirectory, fragmentMeta);

            // create project file
            new ProjectTemplate(fragmentMeta.getId()).write(fragmentDir
                    .getProjectFile());

            // build.properties
            new FragmentBuildPropertiesTemplate().write(fragmentDir
                    .getBuildProperties());

            // manifest.mf
            new FragmentManifestMFTemplate(fragmentMeta).write(fragmentDir
                    .getManifestMf());

            new FragmentProjectTemplate(fragmentMeta.getId()).write(new File(
                    fragmentDir, ".project"));

            // copy the binaries
            List<String> executablePaths = fragmentDir
                    .getBinaryResourcesDirectory().copyPayload(
                            fragmentMeta.getPayloadFile());

            new FragmentP2InfTemplate(executablePaths).write(fragmentDir
                    .getP2Inf());

        }

        return createdFragments;
    }

    private void generateTestingFeature()
            throws PathnameIsNoDirectoryException, IOException {
        // create feature directory
        Directory featureDir = new Directory(new File(baseBinaryDirectory,
                generatedPluginMeta.getPackageRoot() + ".testing.feature"));
        featureDir.mkdir();

        // find all packages in the current directory
        new TestingFeatureBuildPropertiesTemplate().write(new File(featureDir,
                "build.properties"));

        new TestingFeatureXMLTemplate(generatedPluginMeta, featureMeta,
                fragmentMetas, contributingPluginMetas).write(new File(
                featureDir, "feature.xml"));

        new TestingFeatureProjectTemplate(generatedPluginMeta.getPackageRoot())
                .write(new File(featureDir, ".project"));
    }

    private void generateFeature() throws PathnameIsNoDirectoryException,
            IOException {
        // create feature directory
        Directory featureDir = new Directory(new File(baseBinaryDirectory,
                generatedPluginMeta.getPackageRoot() + ".feature"));
        featureDir.mkdir();

        // find all packages in the current directory
        new FeatureBuildPropertiesTemplate().write(new File(featureDir,
                "build.properties"));

        new FeatureXMLTemplate(generatedPluginMeta, featureMeta, fragmentMetas,
                contributingPluginMetas).write(new File(featureDir,
                "feature.xml"));

        new FeatureProjectTemplate(generatedPluginMeta.getPackageRoot())
                .write(new File(featureDir, ".project"));
    }

    private void copyAsset(String assetResourcePath, String targetPath)
            throws IOException {
        InputStream in = NodeGenerator.class.getResourceAsStream("assets/"
                + assetResourcePath);

        FileOutputStream fostream = new FileOutputStream(new File(targetPath,
                assetResourcePath));
        IOUtils.copy(in, fostream);
        fostream.close();
    }

    private void copyAsset(String assetResourcePath) throws IOException {
        copyAsset(assetResourcePath, pluginBuildDir.getAbsolutePath());
    }

    public File getSourceDirectory() {
        return srcDir;
    }

    public File getBuildDirectory() {
        return pluginBuildDir;
    }

    public String getPluginName() {
        return generatedPluginMeta.getName();
    }

    public String getPluginVersion() {
        return generatedPluginMeta.getVersion();
    }

    public void copyFolderIcon() throws IOException {

        File categoryIcon = srcDir.getIconsDirectory().getCategoryIcon();
        if (categoryIcon != null && categoryIcon.canRead()) {
            // TODO: only set icon file in plugin.xml for categories if this
            // method was called
            FileUtils
                    .copyFile(categoryIcon,
                            new File(pluginBuildDir.getIconsDirectory(),
                                    "category.png"));
        }
    }

    public void registerSplashIcon(PluginXMLTemplate pluginXML)
            throws IOException {
        File splashIcon = srcDir.getIconsDirectory().getSplashIcon();
        if (splashIcon != null && splashIcon.canRead()) {
            FileUtils.copyFile(splashIcon,
                    new File(pluginBuildDir.getIconsDirectory(), "splash.png"));
            pluginXML.registerSplashIcon(generatedPluginMeta, new File(
                    "icons/splash.png"));
        }
    }

    /**
     * Copies the java sources needed to invoke a tool (described by a
     * {@link CTDFile}) to the specified {@link NodesBuildKnimeNodesDirectory}.
     * 
     * @param ctdFile
     *            which described the wrapped tool
     * @param iconsDir
     *            location where node icons reside
     * @param nodesDir
     *            location where to create a sub directory containing the
     *            generated sources
     * @param pluginMeta
     *            meta information used to adapt the java files
     * @return the fully qualified name of the NodeFactory class able to build
     *         instances of the node.
     * @throws IOException
     * @throws UnknownMimeTypeException
     */
    public String copyNodeSources(CTDFile ctdFile) throws IOException,
            UnknownMimeTypeException {

        INodeConfiguration nodeConfiguration = ctdFile.getNodeConfiguration();
        String nodeName = Utils.fixKNIMENodeName(nodeConfiguration.getName());

        File nodeSourceDir = new File(pluginBuildDir.getKnimeNodesDirectory(),
                nodeName);
        nodeSourceDir.mkdirs();

        File nodeIcon = srcDir.getIconsDirectory().getNodeIcon(
                nodeConfiguration);
        if (nodeIcon != null) {
            FileUtils.copyFileToDirectory(nodeIcon, nodeSourceDir);
        } else {
            // use generic icon
            copyAsset("generic_node.png", nodeSourceDir.getAbsolutePath());
            nodeIcon = new File(nodeSourceDir, "generic_node.png");
        }

        /*
         * all files placed into src/[PACKAGE]/knime/nodes/[NODE_NAME]
         */
        new NodeDialogTemplate(generatedPluginMeta.getPackageRoot(), nodeName)
                .write(new File(nodeSourceDir, nodeName + "NodeDialog.java"));
        new NodeModelTemplate(generatedPluginMeta.getPackageRoot(), nodeName,
                nodeConfiguration).write(new File(nodeSourceDir, nodeName
                + "NodeModel.java"));
        new NodeFactoryXMLTemplate(nodeName, nodeConfiguration,
                nodeIcon.getName()).write(new File(nodeSourceDir, nodeName
                + "NodeFactory.xml"));
        new NodeFactoryTemplate(generatedPluginMeta.getPackageRoot(), nodeName)
                .write(new File(nodeSourceDir, nodeName + "NodeFactory.java"));

        File nodeConfigDir = new File(nodeSourceDir, "config");
        nodeConfigDir.mkdirs();

        /*
         * all files placed into src/[PACKAGE]/knime/nodes/[NODE_NAME]/config
         */
        FileUtils.copyFile(ctdFile, new File(nodeConfigDir, "config.xml"));

        return generatedPluginMeta.getPackageRoot() + ".knime.nodes."
                + nodeName + "." + nodeName + "NodeFactory";
    }
}
