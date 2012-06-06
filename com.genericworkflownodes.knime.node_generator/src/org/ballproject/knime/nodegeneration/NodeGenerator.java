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

package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;
import org.ballproject.knime.nodegeneration.model.directories.NodesBuildDirectory;
import org.ballproject.knime.nodegeneration.model.directories.NodesSourceDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildIconsDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.IconsDirectory;
import org.ballproject.knime.nodegeneration.model.files.CTDFile;
import org.ballproject.knime.nodegeneration.templates.BinaryResourcesTemplate;
import org.ballproject.knime.nodegeneration.templates.BuildPropertiesTemplate;
import org.ballproject.knime.nodegeneration.templates.ManifestMFTemplate;
import org.ballproject.knime.nodegeneration.templates.MimeFileCellFactoryTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeDialogTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeFactoryTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeFactoryXMLTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeModelTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeViewTemplate;
import org.ballproject.knime.nodegeneration.templates.PluginActivatorTemplate;
import org.ballproject.knime.nodegeneration.templates.PluginXMLTemplate;
import org.ballproject.knime.nodegeneration.templates.ProjectTemplate;
import org.ballproject.knime.nodegeneration.util.FailedExecutionException;
import org.ballproject.knime.nodegeneration.util.NodeDescriptionUtils;
import org.ballproject.knime.nodegeneration.util.Utils;
import org.ballproject.knime.nodegeneration.writer.PropertiesWriter;
import org.dom4j.DocumentException;

public class NodeGenerator {
	private static final Logger LOGGER = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	private NodesSourceDirectory srcDir;
	private KNIMEPluginMeta meta;
	private NodesBuildDirectory buildDir;

	@SuppressWarnings("serial")
	public NodeGenerator(File pluginDir, File buildDir2) throws IOException,
			DocumentException, InvalidNodeNameException,
			DuplicateNodeNameException, FailedExecutionException,
			UnknownMimeTypeException {

		this.srcDir = new NodesSourceDirectory(pluginDir);
		this.meta = new KNIMEPluginMeta(srcDir.getProperties());
		if (buildDir2 != null) {
			this.buildDir = new NodesBuildDirectory(buildDir2,
					meta.getPackageRoot());
		} else {
			this.buildDir = new NodesBuildDirectory(meta.getPackageRoot());
		}

		LOGGER.info("Creating KNIME plugin sources\n\tFrom: " + this.srcDir
				+ "\n\tTo: " + this.buildDir);

		boolean dynamicCTDs = NodeDescriptionUtils
				.createCTDsIfNecessary(srcDir);
		DescriptorsDirectory descriptorsDirectory = (dynamicCTDs) ? new DescriptorsDirectory(
				srcDir.getExecutablesDirectory()) : srcDir
				.getDescriptorsDirectory();

		if (dynamicCTDs)
			LOGGER.info("Using dynamically created ctd files");
		else
			LOGGER.info("Using static ctd files");

		// build.properties - only useful if you re-import the generated node in
		// Eclipse
		new BuildPropertiesTemplate().write(buildDir.getBuildProperties());

		// META-INF/MANIFEST.MF
		new ManifestMFTemplate(meta).write(buildDir.getManifestMf());

		// src/[PACKAGE]/knime/plugin.properties
		new PropertiesWriter(new File(this.buildDir.getKnimeDirectory(),
				"plugin.properties")).write(new HashMap<String, String>() {
			{
				put("executor", srcDir.getProperty("executor", "CLIExecutor"));
				put("commandGenerator",
						srcDir.getProperty("commandGenerator", ""));
			}
		});

		// src/[PACKAGE]/knime/nodes/mimetypes/MimeFileCellFactory.java
		new MimeFileCellFactoryTemplate(meta.getPackageRoot(),
				srcDir.getMimeTypes()).write(new File(buildDir
				.getKnimeNodesDirectory(), "mimetypes" + File.separator
				+ "MimeFileCellFactory.java"));

		PluginXMLTemplate pluginXML = new PluginXMLTemplate();
		List<String> nodeNames = new LinkedList<String>();

		// src/[PACKAGE]/knime/nodes/*/*
		for (CTDFile ctdFile : descriptorsDirectory.getCTDFiles()) {
			LOGGER.info("Start processing ctd file: " + ctdFile.getName());

			nodeNames.add(ctdFile.getNodeConfiguration().getName());

			String factoryClass = copyNodeSources(ctdFile,
					srcDir.getIconsDirectory(),
					buildDir.getKnimeNodesDirectory(), meta);

			String absoluteCategory = "/" + meta.getNodeRepositoryRoot() + "/"
					+ meta.getName() + "/"
					+ ctdFile.getNodeConfiguration().getCategory();
			pluginXML.registerNode(factoryClass, absoluteCategory);
		}

		// src/[PACKAGE]/knime/PluginActivator.java
		new PluginActivatorTemplate(meta.getPackageRoot(), nodeNames)
				.write(new File(this.buildDir.getKnimeDirectory(),
						"PluginActivator.java"));

		// icons/*
		copyFolderIcon(srcDir.getIconsDirectory(), buildDir.getIconsDirectory());
		registerSplashIcon(meta, pluginXML, srcDir.getIconsDirectory(),
				buildDir.getIconsDirectory());

		// plugin.xml
		pluginXML.saveTo(buildDir.getPluginXml());

		// .project
		new ProjectTemplate(meta).write(buildDir.getProjectFile());

		// src/[PACKAGE]/knime/nodes/binres/BinaryResources.java
		new BinaryResourcesTemplate(meta.getPackageRoot()).write(new File(
				this.buildDir.getBinaryResourcesDirectory(),
				"BinaryResources.java"));

		// src/[PACKAGE]/knime/nodes/binres/*.ini *.zip
		if (srcDir.getPayloadDirectory() != null)
			srcDir.getPayloadDirectory().copyPayloadTo(
					buildDir.getBinaryResourcesDirectory());

		// copy assets
		copyAsset(".classpath");

		LOGGER.info("KNIME plugin sources successfully created in:\n\t"
				+ this.buildDir);

	}

	private void copyAsset(String assetResourcePath) throws IOException {
		InputStream in = NodeGenerator.class.getResourceAsStream("assets/"
				+ assetResourcePath);
		FileWriter fileWriter = new FileWriter(new File(buildDir,
				assetResourcePath));
		IOUtils.copy(in, fileWriter, "UTF-8");
		fileWriter.close();
	}

	public File getSourceDirectory() {
		return this.srcDir;
	}

	public File getBuildDirectory() {
		return this.buildDir;
	}

	public String getPluginName() {
		return meta.getName();
	}

	public String getPluginVersion() {
		return meta.getVersion();
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

	public static void registerSplashIcon(KNIMEPluginMeta meta,
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
			KNIMEPluginMeta pluginMeta) throws IOException,
			UnknownMimeTypeException {

		INodeConfiguration nodeConfiguration = ctdFile.getNodeConfiguration();
		String nodeName = Utils.fixKNIMENodeName(nodeConfiguration.getName());

		File nodeSourceDir = new File(nodesDir, nodeName);
		nodeSourceDir.mkdirs();

		File nodeIcon = iconsDir.getNodeIcon(nodeConfiguration);
		if (nodeIcon != null)
			FileUtils.copyFileToDirectory(nodeIcon, nodeSourceDir);

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
