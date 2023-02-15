/*
 * Copyright (c) 2011-2012, Marc Röttig.
 * Copyright (c) 2012, Björn Kahlert.
 * Copyright (c) 2012, Stephan Aiche.
 * Copyright (c) 2014-2023, Julianus Pfeuffer.
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.FragmentDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.MavenParentDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesBuildDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.UpdateSiteSourceDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.MvnDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.source.IconsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.files.CTDFile;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FeatureMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.UpdateSiteMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.UpdateSiteMeta.Category;
import com.genericworkflownodes.knime.nodegeneration.templates.BuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.GenericResourceProviderClassTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.ManifestMFTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginActivatorTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginXMLResourceOnlyTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PluginXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.PomXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.ProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeaturePomXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.feature.FeatureXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentBuildPropertiesTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentManifestMFTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentP2InfTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentPomXmlTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.fragment.FragmentProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeDialogTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeFactoryTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeFactoryXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.knime_node.NodeModelTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.mavenparent.MavenParentPomXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.mavenparent.MavenParentProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.updatesite.CategoryXMLTemplate;
import com.genericworkflownodes.knime.nodegeneration.templates.updatesite.SiteProjectTemplate;
import com.genericworkflownodes.knime.nodegeneration.util.UnZipFailureException;
import com.genericworkflownodes.knime.nodegeneration.util.Utils;
import com.genericworkflownodes.knime.nodegeneration.writer.PropertiesWriter;

/**
 * This class is responsible for generating KNIME plugins.
 * 
 * @author jpfeuffer, bkahlert
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
    private final boolean nodeGeneratorCreateTestingFeature;

	/**
	 * The full metadata for the update site, hierarchically with features, plugins and fragments.
	 */
    private final UpdateSiteMeta siteMeta;

    /**
     * The directory where all the individual, generated plugins will be
     * located.
     */
    private final Directory baseBinaryDirectory;
    
	private boolean createUpdateSite;

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
    public NodeGenerator(File sourceDir, File buildDir, String lastChangeDate,
    		boolean createTestingFeature, boolean recursive, boolean createUpdateSite)
            throws NodeGeneratorException {
        try {
            if (buildDir == null)
                throw new NodeGeneratorException("buildDir must not be null");
            
            LOGGER.info("Creating KNIME plugin sources\n\tFrom: " + sourceDir
                    + "\n\tTo: " + buildDir);

            this.createUpdateSite = createUpdateSite;
            nodeGeneratorCreateTestingFeature = createTestingFeature;
            nodeGeneratorLastChangeDate = lastChangeDate;
            
            baseBinaryDirectory = new Directory(buildDir, false);
            boolean wasCreated = baseBinaryDirectory.mkdirs();
            if (wasCreated && baseBinaryDirectory.list().length != 0) {
                LOGGER.warning("The given buildDir is not empty: Will clean the directory.");
                for (File file : baseBinaryDirectory.listFiles())
                    if (file.isDirectory())
                        FileUtils.deleteDirectory(file);
                    else
                        file.delete();
            }
            
    		MvnDirectory mvnDir = new MvnDirectory(new File(baseBinaryDirectory, ".mvn"));
    		mvnDir.mkdir();
    		copyAsset("extensions.xml", mvnDir.getAbsolutePath());

            if (recursive)
            {
            	UpdateSiteSourceDirectory udir = new UpdateSiteSourceDirectory(sourceDir);
            	siteMeta = new UpdateSiteMeta(udir, nodeGeneratorLastChangeDate);
            } else {
            	FeatureMeta featureMeta = new FeatureMeta(new NodesSourceDirectory(sourceDir), nodeGeneratorLastChangeDate);
                siteMeta = new UpdateSiteMeta(featureMeta);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            throw new NodeGeneratorException(e);
        }
    }

    public void generate() throws NodeGeneratorException, PathnameIsNoDirectoryException, IOException {
    	
    	try {
	    	if (createUpdateSite)
	    	{
	    		generateUpdateSiteRecursive(siteMeta);
	    	}
	    	else // skip the update site and iterate over the features
	    	{
	    		for (FeatureMeta fMeta : siteMeta.featureMetas)
	    		{
	    			generateFeatureRecursive(fMeta);
	    		}
	    	}
	    	
	        // create Maven parent plugin
	        generateMavenParent();
	        
	        if (nodeGeneratorCreateTestingFeature)
	        {
	            // create testing feature
	            generateTestingFeature();
	        }
	        
	        LOGGER.info("KNIME project successfully created in:\n\t"
	                + baseBinaryDirectory);
	    } catch (Exception e) {
	        LOGGER.info("KNIME project creation failed");
	        e.printStackTrace();
	        throw new NodeGeneratorException(e);
	    }
    }

    private void generateFeatureRecursive(FeatureMeta fMeta) throws PathnameIsNoDirectoryException, IOException {
		generateFeature(fMeta);
		for (GeneratedPluginMeta pMeta : fMeta.generatedPluginMetas)
		{
			generatePlugin(pMeta);
		}
		copyContributingPlugins(fMeta);
	}

	
	private void generatePlugin(GeneratedPluginMeta generatedPluginMeta){
		final NodesSourceDirectory srcDir = generatedPluginMeta.sourceDir;

		try {
			DescriptorsDirectory descriptorsDirectory = srcDir
	                .getDescriptorsDirectory();
			
			NodesBuildDirectory pluginBuildDir = new NodesBuildDirectory(
					baseBinaryDirectory,
					generatedPluginMeta);
			
			MvnDirectory mvnDir = new MvnDirectory(new File(pluginBuildDir, ".mvn"));
			mvnDir.mkdir();
			copyAsset("extensions.xml", mvnDir.getAbsolutePath());
			
	        // build.properties - only useful if you re-import the generated
	        // projects in Eclipse
	        new BuildPropertiesTemplate().write(pluginBuildDir
	                .getBuildProperties());
	
	        // META-INF/MANIFEST.MF
	        new ManifestMFTemplate(generatedPluginMeta).write(pluginBuildDir
	                .getManifestMf());
	
	        // pom.xml - We try to do pomless now
	        //new PomXMLTemplate(generatedPluginMeta).write(pluginBuildDir
	        //        .getPomXml());
	
	        if (generatedPluginMeta.isResourceOnly())
	        {
	        	PluginXMLResourceOnlyTemplate pluginXML = new PluginXMLResourceOnlyTemplate();
	        	// src/[PACKAGE]/GenericResourceProvider.java
	        	File grp = new File(pluginBuildDir.getPackageRootDirectory(),
                        "GenericResourceProvider.java");
		        new GenericResourceProviderClassTemplate(generatedPluginMeta)
                	.write(grp);
		        
		        pluginXML.registerDLLProviderClass(
		        		generatedPluginMeta.getId()+".GenericResourceProvider",
		        		generatedPluginMeta.getResourceProviderTarget());
		        
	            // plugin.xml
	            pluginXML.saveTo(pluginBuildDir.getPluginXml());
	            
	        } else {
	        	PluginXMLTemplate pluginXML = new PluginXMLTemplate();
		        // src/[PACKAGE]/knime/plugin.properties
		        final Properties toolProperites = srcDir.getToolProperites();
		        new PropertiesWriter(new File(pluginBuildDir.getKnimeDirectory(),
		                "plugin.properties")).write(new HashMap<String, String>() {
		            private static final long serialVersionUID = 1L;
		            {
		                put("executor",
		                        srcDir.getProperty("executor", "LocalToolExecutor"));
		                put("commandGenerator", srcDir.getProperty(
		                        "commandGenerator", "CLICommandGenerator"));
		                put("dockerMachine", srcDir.getProperty(
		                		"dockerMachine", "default"));
		                for(String key: toolProperites.stringPropertyNames()){
		                	put(key, ((String) toolProperites.get(key)).replace("\"",""));
		                }
		            }
		        });
		        
		        List<String> nodeNames = new LinkedList<String>();
		        List<INodeConfiguration> configurations = new ArrayList<INodeConfiguration>();
		        
		        // src/[PACKAGE]/knime/nodes/*/*
		        for (CTDFile ctdFile : descriptorsDirectory.getCTDFiles()) {
		            LOGGER.info("Start processing ctd file: " + ctdFile.getName());
		
		            configurations.add(ctdFile.getNodeConfiguration());
		            nodeNames.add(ctdFile.getNodeConfiguration().getName());
		
		            String factoryClass = copyNodeSources(ctdFile, generatedPluginMeta, pluginBuildDir);
		
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
		        copyFolderIcon(generatedPluginMeta, pluginBuildDir);
		        
	            // register the mime types
	            pluginXML.registerMIMETypeEntries(srcDir.getMIMETypes());
	            
		        // TODO split and move to pluginXML Template class
		        copyAndRegisterSplashIcon(pluginXML, generatedPluginMeta, pluginBuildDir);

	            // plugin.xml
	            pluginXML.saveTo(pluginBuildDir.getPluginXml());
	        }
	        


            // .project
            new ProjectTemplate(generatedPluginMeta.getPackageRoot())
                    .write(pluginBuildDir.getProjectFile());

            // src/[PACKAGE]/knime/nodes/binres/*.ini *.zip
            generatePayloadFragments(generatedPluginMeta);

            // copy assets
            copyAsset(".classpath", pluginBuildDir.getAbsolutePath());
	        
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownMimeTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PathnameIsNoDirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeGeneratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnZipFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void generateUpdateSiteRecursive(UpdateSiteMeta siteMeta) throws PathnameIsNoDirectoryException, IOException {
		for (FeatureMeta fMeta : siteMeta.featureMetas)
		{
			generateFeatureRecursive(fMeta);
		}
		generateUpdateSite(siteMeta);
	}

    private void generateUpdateSite(UpdateSiteMeta siteMeta) {
    	Directory siteDir;
		try {
			siteDir = new Directory(new File(baseBinaryDirectory, siteMeta.getArtifactId()), false);
			siteDir.mkdir();
			CategoryXMLTemplate catxml = new CategoryXMLTemplate();
			for (Category cat : siteMeta.getCategories())
			{
				catxml.registerCategory(cat);
			}
			for (FeatureMeta fMeta : siteMeta.featureMetas)
			{
				catxml.registerFeature(fMeta);
			}
			catxml.saveTo(new File(siteDir, "category.xml"));
			
			SiteProjectTemplate proj = new SiteProjectTemplate();
			proj.replace("%PACKAGE_NAME%", siteMeta.getArtifactId());
			proj.write(new File(siteDir, ".project"));
			
		} catch (FileNotFoundException e) {
			// should never happen since we pass required=false
			e.printStackTrace();
		} catch (PathnameIsNoDirectoryException e) {
			// should never happen since we pass required=false
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * Creates a maven parent directory with pom.xml to build everything
     * 
     * @throws NodeGeneratorException
     * @throws PathnameIsNoDirectoryException
     * @throws IOException
     */
    private void generateMavenParent()
            throws NodeGeneratorException, PathnameIsNoDirectoryException, IOException 
    {
    	
		// TODO make it work with just updateSiteMeta and collect all Metas
		// also check for duplicates somewhere. Potentially earlier when generating updateSiteMeta.
		// pom should be in the baseBinaryDirectory.
        MavenParentDirectory mavenDir = new MavenParentDirectory(baseBinaryDirectory);

        // create project file
        new MavenParentProjectTemplate().write(mavenDir.getProjectFile());

        // pom.xml
        new MavenParentPomXMLTemplate(siteMeta).write(mavenDir.getPomXml());
    }

	private void copyContributingPlugins(FeatureMeta fMeta) {
        for (ContributingPluginMeta contributingPluginMeta : fMeta.contributingPluginMetas) {
            try {
                // TODO: Handle=exclude compiled classes in bin/ or build/ (maybe check
                // build.properties for output folder)
            	File targetDir = new File(baseBinaryDirectory,
        				contributingPluginMeta.getContributingPluginDirectory().getName());
                FileUtils.copyDirectory(
                		contributingPluginMeta.getContributingPluginDirectory(), targetDir);
        		MvnDirectory mvnDir = new MvnDirectory(new File(targetDir, ".mvn"));
        		if (!mvnDir.exists())
        		{
	    			mvnDir.mkdir();
	    			copyAsset("extensions.xml", mvnDir.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (PathnameIsNoDirectoryException e) {
				e.printStackTrace();
			}
        }
    }

    /**
     * Creates a separate fragment for each binaries_*.zip file found in the
     * payload directory.
     * 
     * @throws NodeGeneratorException
     * @throws PathnameIsNoDirectoryException
     * @throws IOException
     * @throws UnZipFailureException
     */
    private void generatePayloadFragments(GeneratedPluginMeta pMeta)
            throws NodeGeneratorException, PathnameIsNoDirectoryException,
            IOException, UnZipFailureException {

        for (FragmentMeta fragmentMeta : pMeta.generatedFragmentMetas) {
            LOGGER.info(String.format("Creating binary fragment %s",
                    fragmentMeta.getId()));

            FragmentDirectory fragmentDir = new FragmentDirectory(
                    baseBinaryDirectory, fragmentMeta);
            fragmentDir.mkdir();
            
    		MvnDirectory mvnDir = new MvnDirectory(new File(fragmentDir, ".mvn"));
			mvnDir.mkdir();
			copyAsset("extensions.xml", mvnDir.getAbsolutePath());

            // create project file
            new ProjectTemplate(fragmentMeta.getId()).write(fragmentDir
                    .getProjectFile());

            // build.properties
            new FragmentBuildPropertiesTemplate().write(fragmentDir
                    .getBuildProperties());

            // manifest.mf
            new FragmentManifestMFTemplate(fragmentMeta).write(fragmentDir
                    .getManifestMf());
            // pom.xml - We try to do pomless now
            //new FragmentPomXmlTemplate(fragmentMeta).write(fragmentDir
            //        .getPomXml());

            new FragmentProjectTemplate(fragmentMeta.getId()).write(new File(
                    fragmentDir, ".project"));

            // copy the binaries
            List<String> executablePaths = fragmentDir
                    .getBinaryResourcesDirectory().copyPayload(
                            fragmentMeta.getPayloadFile());

            new FragmentP2InfTemplate(executablePaths).write(fragmentDir
                    .getP2Inf());

        }
    }

    private void generateTestingFeature()
            throws PathnameIsNoDirectoryException, IOException {
    	// TODO the full thing
    	/*
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

        // pom.xml - we try to do pomless now
        // TODO check if we need to change build.properties for pomless + testing
        //new TestingFeaturePomXMLTemplate(generatedPluginMeta).write(new File(
        //        featureDir, "pom.xml"));

        new TestingFeatureProjectTemplate(generatedPluginMeta.getPackageRoot())
                .write(new File(featureDir, ".project"));
        */
    }

    private void generateFeature(FeatureMeta fMeta) throws PathnameIsNoDirectoryException,
            IOException {
        // create feature directory
    	String featureID = fMeta.getId();
    	//generatedPluginMeta.getPackageRoot() + ".feature";
        Directory featureDir = new Directory(new File(baseBinaryDirectory, featureID), false);
        featureDir.mkdir();
        
		MvnDirectory mvnDir = new MvnDirectory(new File(featureDir, ".mvn"));
		mvnDir.mkdir();
		copyAsset("extensions.xml", mvnDir.getAbsolutePath());

        // find all packages in the current directory
        new FeatureBuildPropertiesTemplate().write(new File(featureDir,
                "build.properties"));

        new FeatureXMLTemplate(fMeta).write(
        		new File(featureDir,"feature.xml"));

        // pom.xml - We try to do pomless now
        //new FeaturePomXMLTemplate(fMeta).write(
        //		new File(featureDir, "pom.xml"));

        new FeatureProjectTemplate(fMeta.getId())
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

    public void copyFolderIcon(GeneratedPluginMeta pMeta, NodesBuildDirectory buildDir) throws IOException {
    	NodesSourceDirectory srcDir = pMeta.sourceDir;
        File categoryIcon = srcDir.getIconsDirectory().getCategoryIcon();
        if (categoryIcon != null && categoryIcon.canRead()) {
            // TODO: only set icon file in plugin.xml for categories if this
            // method was called
            FileUtils.copyFile(categoryIcon,
            		new File(buildDir.getIconsDirectory(), "category.png"));
        }
    }

    // TODO: rename to copyAndRegister and/or split
    public void copyAndRegisterSplashIcon(PluginXMLTemplate pluginXML, GeneratedPluginMeta pMeta, NodesBuildDirectory buildDir)
            throws IOException {
    	IconsDirectory icondir = pMeta.sourceDir.getIconsDirectory();
    	if (icondir != null)
    	{
            File splashIcon = icondir.getSplashIcon();
            if (splashIcon != null && splashIcon.canRead()) {
                FileUtils.copyFile(splashIcon,
                        new File(buildDir.getIconsDirectory(), "splash.png"));
                pluginXML.registerSplashIcon(pMeta, new File(
                        "icons/splash.png"));
            }
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
    public String copyNodeSources(CTDFile ctdFile, GeneratedPluginMeta pMeta, NodesBuildDirectory pluginBuildDir) throws IOException,
            UnknownMimeTypeException {

        INodeConfiguration nodeConfiguration = ctdFile.getNodeConfiguration();
        String nodeName = Utils.fixKNIMENodeName(nodeConfiguration.getName());

        File nodeSourceDir = new File(pluginBuildDir.getKnimeNodesDirectory(),
                nodeName);
        nodeSourceDir.mkdirs();

        File nodeIcon = pMeta.sourceDir.getIconsDirectory().getNodeIcon(
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
        new NodeDialogTemplate(pMeta.getPackageRoot(), nodeName)
                .write(new File(nodeSourceDir, nodeName + "NodeDialog.java"));
        //If node is a docker tool use NodeDockerModel.template instead of usual template
        //it overrides the checkIfToolExists method that checks whether a the executable defined in the CTD
        //exists in PATH on the host system
        if(pMeta.sourceDir.getProperty("commandGenerator", "CLI").endsWith("DockerCommandGenerator")){
        	new NodeModelTemplate(pMeta.getPackageRoot(), nodeName,
        			nodeConfiguration,"NodeDockerModel.template").write(new File(nodeSourceDir, nodeName
        					+ "NodeModel.java"));
        } else {
        	new NodeModelTemplate(pMeta.getPackageRoot(), nodeName,
        			nodeConfiguration, "NodeModel.template").write(new File(nodeSourceDir, nodeName
        					+ "NodeModel.java"));
        }
        new NodeFactoryXMLTemplate(nodeName, nodeConfiguration,
                nodeIcon.getName()).write(new File(nodeSourceDir, nodeName
                + "NodeFactory.xml"));
        new NodeFactoryTemplate(pMeta.getPackageRoot(), nodeName)
                .write(new File(nodeSourceDir, nodeName + "NodeFactory.java"));

        File nodeConfigDir = new File(nodeSourceDir, "config");
        nodeConfigDir.mkdirs();

        /*
         * all files placed into src/[PACKAGE]/knime/nodes/[NODE_NAME]/config
         */
        FileUtils.copyFile(ctdFile, new File(nodeConfigDir, "config.xml"));

        return pMeta.getPackageRoot() + ".knime.nodes."
                + nodeName + "." + nodeName + "NodeFactory";
    }
}
