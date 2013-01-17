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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ballproject.knime.base.model.Directory;
import org.ballproject.knime.base.util.Helper;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;
import com.genericworkflownodes.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.FragmentDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesBuildDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildIconsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.IconsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.files.CTDFile;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.templates.BinaryResourcesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.BuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.ManifestMFTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.MimeFileCellFactoryTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginActivatorTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginPreferencePageTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.ProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.StartupTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentManifestMFTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeDialogTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeFactoryTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeFactoryXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeModelTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeViewTemplate;
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

	private final NodesSourceDirectory srcDir;
	private final GeneratedPluginMeta generatedPluginMeta;
	private final FeatureMeta featureMeta;
	private List<FragmentMeta> fragmentMetas;
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
	 * @throws NodeGeneratorException
	 */
	public NodeGenerator(File sourceDir, File buildDir)
			throws NodeGeneratorException {
		try {
			if (buildDir == null)
				throw new NodeGeneratorException("buildDir must not be null");

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
			generatedPluginMeta = new GeneratedPluginMeta(srcDir);
			featureMeta = new FeatureMeta(srcDir, generatedPluginMeta);
			pluginBuildDir = new NodesBuildDirectory(buildDir,
					generatedPluginMeta.getPackageRoot());
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
					this.put("executor",
							srcDir.getProperty("executor", "CLIExecutor"));
					this.put("commandGenerator",
							srcDir.getProperty("commandGenerator", ""));
				}
			});

			// src/[PACKAGE]/knime/nodes/mimetypes/MimeFileCellFactory.java
			new MimeFileCellFactoryTemplate(
					generatedPluginMeta.getPackageRoot(), srcDir.getMimeTypes())
					.write(new File(pluginBuildDir.getKnimeNodesDirectory(),
							"mimetypes" + File.separator
									+ "MimeFileCellFactory.java"));

			PluginXMLTemplate pluginXML = new PluginXMLTemplate();
			List<String> nodeNames = new LinkedList<String>();
			List<INodeConfiguration> configurations = new ArrayList<INodeConfiguration>();

			// src/[PACKAGE]/knime/nodes/*/*
			for (CTDFile ctdFile : descriptorsDirectory.getCTDFiles()) {
				LOGGER.info("Start processing ctd file: " + ctdFile.getName());

				configurations.add(ctdFile.getNodeConfiguration());
				nodeNames.add(ctdFile.getNodeConfiguration().getName());

				String factoryClass = copyNodeSources(ctdFile,
						srcDir.getIconsDirectory(),
						pluginBuildDir.getKnimeNodesDirectory(),
						generatedPluginMeta);

				String absoluteCategory = "/"
						+ generatedPluginMeta.getNodeRepositoryRoot() + "/"
						+ generatedPluginMeta.getName() + "/"
						+ ctdFile.getNodeConfiguration().getCategory();
				pluginXML.registerNode(factoryClass, absoluteCategory);
			}

			// src/[PACKAGE]/knime/PluginActivator.java
			new PluginActivatorTemplate(generatedPluginMeta.getPackageRoot(),
					configurations).write(new File(pluginBuildDir
					.getKnimeDirectory(), "PluginActivator.java"));

			// src/[PACKAGE]/knime/PluginActivator.java
			new StartupTemplate(generatedPluginMeta.getPackageRoot(),
					generatedPluginMeta.getName()).write(new File(
					pluginBuildDir.getKnimeDirectory(), "Startup.java"));

			// src/[PACKAGE]/knime/preferences/PluginPreferencePage.java
			new PluginPreferencePageTemplate(
					generatedPluginMeta.getPackageRoot())
					.write(new File(new File(
							pluginBuildDir.getKnimeDirectory(), "preferences"),
							"PluginPreferencePage.java"));

			// icons/*
			copyFolderIcon(srcDir.getIconsDirectory(),
					pluginBuildDir.getIconsDirectory());
			registerSplashIcon(generatedPluginMeta, pluginXML,
					srcDir.getIconsDirectory(),
					pluginBuildDir.getIconsDirectory());

			// register preference page
			pluginXML.registerPreferencePage(generatedPluginMeta);

			// register startup
			pluginXML.registerStartupClass(generatedPluginMeta);

			// plugin.xml
			pluginXML.saveTo(pluginBuildDir.getPluginXml());

			// .project
			new ProjectTemplate(generatedPluginMeta.getPackageRoot())
					.write(pluginBuildDir.getProjectFile());

			// src/[PACKAGE]/knime/nodes/binres/BinaryResources.java
			new BinaryResourcesTemplate(generatedPluginMeta.getPackageRoot())
					.write(new File(pluginBuildDir
							.getBinaryResourcesDirectory(),
							"BinaryResources.java"));

			// src/[PACKAGE]/knime/nodes/binres/*.ini *.zip
			if (srcDir.getPayloadDirectory() != null) {
				// create payload fragments
				fragmentMetas = this.createPayloadFragments();
			}

			// copy assets
			this.copyAsset(".classpath");

			// create feature
			this.generateFeature();

			LOGGER.info("KNIME plugin sources successfully created in:\n\t"
					+ pluginBuildDir);
		} catch (Exception e) {
			LOGGER.info("KNIME plugin source creation failed");
			throw new NodeGeneratorException(e);
		}
	}

	/**
	 * Creates a separate fragment for each binaries_..zip file found in the
	 * payload directory.
	 * 
	 * @throws NodeGeneratorException
	 */
	private List<FragmentMeta> createPayloadFragments()
			throws NodeGeneratorException {
		Pattern payloadFormat = Pattern
				.compile("^binaries_(mac|lnx|win)_([36][24]).zip$");

		List<FragmentMeta> createdFragments = new ArrayList<FragmentMeta>();

		for (String payload : srcDir.getPayloadDirectory().list()) {
			LOGGER.info("Create payload fragment for " + payload);

			Matcher m = payloadFormat.matcher(payload);
			if (!m.find()) {
				LOGGER.warning("Ignoring incompatible file " + payload
						+ " in payload directory.");
			}

			OperatingSystem os = OperatingSystem.fromString(m.group(1));
			Architecture arch = Architecture.fromString(m.group(2));

			try {
				FragmentDirectory fragmentDir = new FragmentDirectory(
						baseBinaryDirectory, arch, os,
						generatedPluginMeta.getPackageRoot());

				// create project file
				new ProjectTemplate(generatedPluginMeta.getPackageRoot() + "."
						+ os.toOsgiOs() + "." + arch.toOsgiArch())
						.write(fragmentDir.getProjectFile());

				// build.properties
				new FragmentBuildPropertiesTemplate().write(fragmentDir
						.getBuildProperties());

				// manifest.mf
				new FragmentManifestMFTemplate(generatedPluginMeta, os, arch)
						.write(fragmentDir.getManifestMf());

				// copy assets
				this.copyAsset(".classpath", fragmentDir.getAbsolutePath());

				// copy the binaries
				fragmentDir.getBinaryResourcesDirectory().copyPayload(
						new File(srcDir.getPayloadDirectory(), payload));

				createdFragments.add(new FragmentMeta(generatedPluginMeta
						.getId(), arch, os));
			} catch (Exception e) {
				throw new NodeGeneratorException(
						"Could not create project for payload " + payload, e);
			}
		}

		return createdFragments;
	}

	private void generateFeature() throws IOException {
		// create feature directory
		Directory featureDir = new Directory(new File(baseBinaryDirectory,
				generatedPluginMeta.getPackageRoot() + ".feature"));
		featureDir.mkdir();

		// find all packages in the current directory
		new FeatureBuildPropertiesTemplate().write(new File(featureDir,
				"build.properties"));

		new FeatureXMLTemplate(generatedPluginMeta, featureMeta, fragmentMetas)
				.write(new File(featureDir, "feature.xml"));

		new FeatureProjectTemplate(generatedPluginMeta.getPackageRoot())
				.write(new File(featureDir, ".project"));
	}

	private void copyAsset(String assetResourcePath, String targetPath)
			throws IOException {
		InputStream in = NodeGenerator.class.getResourceAsStream("assets/"
				+ assetResourcePath);
		FileWriter fileWriter = new FileWriter(new File(targetPath,
				assetResourcePath));
		IOUtils.copy(in, fileWriter, "UTF-8");
		fileWriter.close();
	}

	private void copyAsset(String assetResourcePath) throws IOException {
		this.copyAsset(assetResourcePath, pluginBuildDir.getAbsolutePath());
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

	public static void copyFolderIcon(IconsDirectory iconsSrc,
			NodesBuildIconsDirectory iconsDest) throws IOException {
		File categoryIcon = iconsSrc.getCategoryIcon();
		if (categoryIcon != null && categoryIcon.canRead()) {
			// TODO: only set icon file in plugin.xml for categories if this
			// method was called
			Helper.copyFile(categoryIcon, new File(iconsDest, "category.png"));
		}
	}

	public static void registerSplashIcon(GeneratedPluginMeta meta,
			PluginXMLTemplate pluginXML, IconsDirectory iconsSrc,
			NodesBuildIconsDirectory iconsDest) throws IOException {
		File splashIcon = iconsSrc.getSplashIcon();
		if (splashIcon != null && splashIcon.canRead()) {
			Helper.copyFile(splashIcon, new File(iconsDest, "splash.png"));
			pluginXML.registerSplashIcon(meta, new File("icons/splash.png"));
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
	public static String copyNodeSources(CTDFile ctdFile,
			IconsDirectory iconsDir, NodesBuildKnimeNodesDirectory nodesDir,
			GeneratedPluginMeta pluginMeta) throws IOException,
			UnknownMimeTypeException {

		INodeConfiguration nodeConfiguration = ctdFile.getNodeConfiguration();
		String nodeName = Utils.fixKNIMENodeName(nodeConfiguration.getName());

		File nodeSourceDir = new File(nodesDir, nodeName);
		nodeSourceDir.mkdirs();

		File nodeIcon = iconsDir.getNodeIcon(nodeConfiguration);
		if (nodeIcon != null) {
			FileUtils.copyFileToDirectory(nodeIcon, nodeSourceDir);
		}

		/*
		 * all files placed into src/[PACKAGE]/knime/nodes/[NODE_NAME]
		 */
		new NodeDialogTemplate(pluginMeta.getPackageRoot(), nodeName)
				.write(new File(nodeSourceDir, nodeName + "NodeDialog.java"));
		new NodeViewTemplate(pluginMeta.getPackageRoot(), nodeName)
				.write(new File(nodeSourceDir, nodeName + "NodeView.java"));
		new NodeModelTemplate(pluginMeta.getPackageRoot(), nodeName,
				nodeConfiguration).write(new File(nodeSourceDir, nodeName
				+ "NodeModel.java"));
		new NodeFactoryXMLTemplate(nodeName, nodeConfiguration,
				(nodeIcon != null) ? nodeIcon.getName() : "./default.png")
				.write(new File(nodeSourceDir, nodeName + "NodeFactory.xml"));
		new NodeFactoryTemplate(pluginMeta.getPackageRoot(), nodeName)
				.write(new File(nodeSourceDir, nodeName + "NodeFactory.java"));

		File nodeConfigDir = new File(nodeSourceDir, "config");
		nodeConfigDir.mkdirs();

		/*
		 * all files placed into src/[PACKAGE]/knime/nodes/[NODE_NAME]/config
		 */
		Helper.copyFile(ctdFile, new File(nodeConfigDir, "config.xml"));

		return pluginMeta.getPackageRoot() + ".knime.nodes." + nodeName + "."
				+ nodeName + "NodeFactory";
	}
}
