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
import org.ballproject.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;
import org.ballproject.knime.nodegeneration.model.directories.FragmentDirectory;
import org.ballproject.knime.nodegeneration.model.directories.NodesBuildDirectory;
import org.ballproject.knime.nodegeneration.model.directories.NodesSourceDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildIconsDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.IconsDirectory;
import org.ballproject.knime.nodegeneration.model.files.CTDFile;
import org.ballproject.knime.nodegeneration.templates.BinaryResourcesTemplate;
import org.ballproject.knime.nodegeneration.templates.BuildPropertiesTemplate;
import org.ballproject.knime.nodegeneration.templates.FragmentBuildPropertiesTemplate;
import org.ballproject.knime.nodegeneration.templates.FragmentManifestMFTemplate;
import org.ballproject.knime.nodegeneration.templates.ManifestMFTemplate;
import org.ballproject.knime.nodegeneration.templates.MimeFileCellFactoryTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeDialogTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeFactoryTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeFactoryXMLTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeModelTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeViewTemplate;
import org.ballproject.knime.nodegeneration.templates.PluginActivatorTemplate;
import org.ballproject.knime.nodegeneration.templates.PluginPreferencePageTemplate;
import org.ballproject.knime.nodegeneration.templates.PluginXMLTemplate;
import org.ballproject.knime.nodegeneration.templates.ProjectTemplate;
import org.ballproject.knime.nodegeneration.util.Utils;
import org.ballproject.knime.nodegeneration.writer.PropertiesWriter;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;

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
	private final KNIMEPluginMeta meta;
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

			srcDir = new NodesSourceDirectory(sourceDir);
			meta = new KNIMEPluginMeta(srcDir.getProperties());
			pluginBuildDir = new NodesBuildDirectory(buildDir,
					meta.getPackageRoot());
			baseBinaryDirectory = new Directory(buildDir);
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
			new ManifestMFTemplate(meta).write(pluginBuildDir.getManifestMf());

			// src/[PACKAGE]/knime/plugin.properties
			new PropertiesWriter(new File(pluginBuildDir.getKnimeDirectory(),
					"plugin.properties")).write(new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					put("executor",
							srcDir.getProperty("executor", "CLIExecutor"));
					put("commandGenerator",
							srcDir.getProperty("commandGenerator", ""));
				}
			});

			// src/[PACKAGE]/knime/nodes/mimetypes/MimeFileCellFactory.java
			new MimeFileCellFactoryTemplate(meta.getPackageRoot(),
					srcDir.getMimeTypes()).write(new File(pluginBuildDir
					.getKnimeNodesDirectory(), "mimetypes" + File.separator
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
						pluginBuildDir.getKnimeNodesDirectory(), meta);

				String absoluteCategory = "/" + meta.getNodeRepositoryRoot()
						+ "/" + meta.getName() + "/"
						+ ctdFile.getNodeConfiguration().getCategory();
				pluginXML.registerNode(factoryClass, absoluteCategory);
			}

			// src/[PACKAGE]/knime/PluginActivator.java
			new PluginActivatorTemplate(meta.getPackageRoot(), configurations)
					.write(new File(pluginBuildDir.getKnimeDirectory(),
							"PluginActivator.java"));

			// src/[PACKAGE]/knime/preferences/PluginPreferencePage.java
			new PluginPreferencePageTemplate(meta.getPackageRoot())
					.write(new File(new File(
							pluginBuildDir.getKnimeDirectory(), "preferences"),
							"PluginPreferencePage.java"));

			// icons/*
			copyFolderIcon(srcDir.getIconsDirectory(),
					pluginBuildDir.getIconsDirectory());
			registerSplashIcon(meta, pluginXML, srcDir.getIconsDirectory(),
					pluginBuildDir.getIconsDirectory());

			// register preference page
			pluginXML.registerPreferencePage(meta);

			// plugin.xml
			pluginXML.saveTo(pluginBuildDir.getPluginXml());

			// .project
			new ProjectTemplate(meta.getPackageRoot()).write(pluginBuildDir
					.getProjectFile());

			// src/[PACKAGE]/knime/nodes/binres/BinaryResources.java
			new BinaryResourcesTemplate(meta.getPackageRoot()).write(new File(
					pluginBuildDir.getBinaryResourcesDirectory(),
					"BinaryResources.java"));

			// src/[PACKAGE]/knime/nodes/binres/*.ini *.zip
			if (srcDir.getPayloadDirectory() != null) {
				// create payload fragments
				createPayloadFragments();
			}

			// copy assets
			copyAsset(".classpath");

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
	private void createPayloadFragments() throws NodeGeneratorException {
		Pattern payloadFormat = Pattern
				.compile("^binaries_(mac|lnx|win)_([36][24]).zip$");

		for (String payload : srcDir.getPayloadDirectory().list()) {
			LOGGER.info("Create payload fragment for " + payload);

			Matcher m = payloadFormat.matcher(payload);
			if (!m.find()) {
				LOGGER.warning("Ignoring incompatible file " + payload
						+ " in payload directory.");
			}

			OperatingSystem sys = OperatingSystem.fromString(m.group(1));
			Architecture arch = Architecture.fromString(m.group(2));

			try {
				FragmentDirectory fragmentDir = new FragmentDirectory(
						baseBinaryDirectory, arch, sys, meta.getPackageRoot());

				// create project file
				new ProjectTemplate(meta.getPackageRoot() + "."
						+ sys.toOSGIOS() + "." + arch.toOSGIArch())
						.write(fragmentDir.getProjectFile());

				// build.properties
				new FragmentBuildPropertiesTemplate().write(fragmentDir
						.getBuildProperties());

				// manifest.mf
				new FragmentManifestMFTemplate(meta, sys, arch)
						.write(fragmentDir.getManifestMf());

				// copy assets
				copyAsset(".classpath", fragmentDir.getAbsolutePath());

				// copy the binaries
				fragmentDir.getBinaryResourcesDirectory().copyPayload(
						new File(srcDir.getPayloadDirectory(), payload));

			} catch (Exception e) {
				throw new NodeGeneratorException(
						"Could not create project for payload " + payload, e);
			}

		}
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
		copyAsset(assetResourcePath, pluginBuildDir.getAbsolutePath());
	}

	public File getSourceDirectory() {
		return srcDir;
	}

	public File getBuildDirectory() {
		return pluginBuildDir;
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
