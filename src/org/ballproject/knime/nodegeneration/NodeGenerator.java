/*
 * Copyright (c) 2011, Marc Röttig.
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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.ballproject.knime.base.config.CTDNodeConfigurationReaderException;
import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import org.ballproject.knime.nodegeneration.model.KNIMEPluginMeta;
import org.ballproject.knime.nodegeneration.model.PluginXmlTemplate;
import org.ballproject.knime.nodegeneration.model.directories.NodesBuildDirectory;
import org.ballproject.knime.nodegeneration.model.directories.NodesSourceDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.DescriptorsDirectory;
import org.ballproject.knime.nodegeneration.model.files.CTDFile;
import org.ballproject.knime.nodegeneration.templates.BinaryResourcesTemplate;
import org.ballproject.knime.nodegeneration.templates.ManifestMFTemplate;
import org.ballproject.knime.nodegeneration.templates.MimeFileCellFactoryTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeDialogTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeFactoryTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeFactoryXMLTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeModelTemplate;
import org.ballproject.knime.nodegeneration.templates.NodeViewTemplate;
import org.ballproject.knime.nodegeneration.templates.PluginActivatorTemplate;
import org.dom4j.DocumentException;
import org.eclipse.core.commands.ExecutionException;

public class NodeGenerator {
	private static final String PLUGIN_PROPERTIES = "plugin.properties";

	public static Logger logger = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	private NodesSourceDirectory srcDir;
	private KNIMEPluginMeta meta;
	private NodesBuildDirectory buildDir;

	public NodeGenerator(File pluginDir) throws IOException,
			ExecutionException, DocumentException, DuplicateNodeNameException,
			InvalidNodeNameException, CTDNodeConfigurationReaderException,
			UnknownMimeTypeException {

		srcDir = new NodesSourceDirectory(pluginDir);
		meta = new KNIMEPluginMeta(srcDir.getProperties());
		boolean dynamicCTDs = NodeDescriptionGenerator
				.createCTDsIfNecessary(srcDir);
		this.buildDir = new NodesBuildDirectory(meta.getPackageRoot());

		logger.info("Creating KNIME plugin sources in: " + buildDir.getPath());

		PluginXmlTemplate pluginXML = new PluginXmlTemplate();

		new ManifestMFTemplate(meta).write(buildDir.getManifestMf());
		new PluginActivatorTemplate(meta.getPackageRoot()).write(new File(
				this.buildDir.getKnimeDirectory(), "PluginActivator.java"));
		new MimeFileCellFactoryTemplate(meta.getName(), srcDir.getMimeTypes())
				.write(new File(buildDir.getKnimeNodesDirectory(), "mimetypes"
						+ File.separator + "MimeFileCellFactory.java"));

		Set<String> node_names = new HashSet<String>();
		Set<String> ext_tools = new HashSet<String>();
		processDescriptors(
				node_names,
				ext_tools,
				pluginXML,
				(dynamicCTDs) ? new DescriptorsDirectory(srcDir
						.getExecutablesDirectory()) : srcDir
						.getDescriptorsDirectory(),
				this.buildDir.getKnimeNodesDirectory(), meta);

		// TODO
		// this.installIcon();

		fillProperties(srcDir.getProperties(),
				this.buildDir.getPackageRootDirectory());

		new BinaryResourcesTemplate(meta.getPackageRoot()).write(new File(
				this.buildDir.getBinaryResourcesDirectory(),
				"BinaryResources.java"));
		copyPayload(srcDir.getPayloadDirectory(),
				this.buildDir.getBinaryResourcesDirectory());

		new DatWriter(new File(this.buildDir.getKnimeDirectory(),
				"ExternalTools.dat")).write(ext_tools);
		new DatWriter(new File(this.buildDir.getKnimeDirectory(),
				"InternalTools.dat")).write(node_names);

		pluginXML.saveTo(buildDir.getPluginXml());
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

	public static void fillProperties(Properties props,
			File destinationFQNDirectory) throws IOException {
		Properties p = new Properties();
		p.put("use_ini", props.getProperty("use_ini", "true"));
		p.put("ini_switch", props.getProperty("ini_switch", "-ini"));
		p.store(new FileOutputStream(destinationFQNDirectory + File.separator
				+ "knime" + File.separator + PLUGIN_PROPERTIES), null);
	}

	// TODO
	// public void installIcon() throws IOException {
	// if (this._iconpath_ != null) {
	// Node node = this.plugindoc
	// .selectSingleNode("/plugin/extension[@point='org.knime.product.splashExtension']");
	// Element elem = (Element) node;
	//
	// elem.addElement("splashExtension")
	// .addAttribute("icon", "icons/logo.png")
	// .addAttribute("id", "logo");
	//
	// new File(this._destdir_ + File.separator + "icons").mkdirs();
	// Helper.copyFile(new File(this._iconpath_), new File(this._destdir_
	// + File.separator + "icons" + File.separator + "logo.png"));
	// }
	//
	// }

	private static void processDescriptors(Set<String> node_names,
			Set<String> ext_tools, PluginXmlTemplate pluginXML,
			DescriptorsDirectory descriptorDirectory,
			File destinationFQNNodeDirectory, KNIMEPluginMeta pluginMeta)
			throws IOException, DuplicateNodeNameException,
			InvalidNodeNameException, CTDNodeConfigurationReaderException,
			UnknownMimeTypeException {

		for (CTDFile ctdFile : descriptorDirectory.getCTDFiles()) {
			logger.info("Start processing ctd file: " + ctdFile.getName());
			processNode(pluginMeta, pluginXML, ctdFile, node_names, ext_tools,
					destinationFQNNodeDirectory);
		}
	}

	public static void processNode(KNIMEPluginMeta pluginMeta,
			PluginXmlTemplate pluginXML, CTDFile ctdFile,
			Set<String> node_names, Set<String> ext_tools,
			File destinationFQNNodeDirectory) throws IOException,
			DuplicateNodeNameException, InvalidNodeNameException,
			CTDNodeConfigurationReaderException, UnknownMimeTypeException {

		INodeConfiguration nodeConfiguration = ctdFile.getNodeConfiguration();
		NodeGenerator.logger.info("## processing Node "
				+ nodeConfiguration.getName());

		String nodeName = nodeConfiguration.getName();
		String oldNodeName = null;

		if (!Utils.checkKNIMENodeName(nodeName)) {
			oldNodeName = nodeName;

			// we try to fix the nodename
			nodeName = Utils.fixKNIMENodeName(nodeName);

			if (!Utils.checkKNIMENodeName(nodeName))
				throw new InvalidNodeNameException("The node name \""
						+ nodeName + "\" is invalid.");
		}

		if (oldNodeName == null) {
			if (node_names.contains(nodeName))
				throw new DuplicateNodeNameException(nodeName);

			if (nodeConfiguration.getStatus().equals("internal")) {
				node_names.add(nodeName);
			} else {
				ext_tools.add(nodeName);
			}
		} else {
			if (node_names.contains(oldNodeName))
				throw new DuplicateNodeNameException(nodeName);

			if (nodeConfiguration.getStatus().equals("internal")) {
				node_names.add(oldNodeName);
			} else {
				ext_tools.add(nodeName);
			}
		}

		String absoluteCategory = "/" + pluginMeta.getNodeRepositoryRoot()
				+ "/" + pluginMeta.getName() + "/"
				+ nodeConfiguration.getCategory();

		File nodeConfigDir = new File(destinationFQNNodeDirectory
				+ File.separator + nodeName + File.separator + "config");
		nodeConfigDir.mkdirs();
		Helper.copyFile(ctdFile, new File(nodeConfigDir, "config.xml"));

		File nodeSourceDir = new File(destinationFQNNodeDirectory, nodeName);

		new NodeFactoryTemplate(pluginMeta.getPackageRoot(), nodeName)
				.write(new File(nodeSourceDir, nodeName + "NodeFactory.java"));

		new NodeDialogTemplate(pluginMeta.getPackageRoot(), nodeName)
				.write(new File(nodeSourceDir, nodeName + "NodeDialog.java"));

		new NodeViewTemplate(pluginMeta.getPackageRoot(), nodeName)
				.write(new File(nodeSourceDir, nodeName + "NodeView.java"));

		new NodeModelTemplate(pluginMeta.getPackageRoot(), nodeName,
				nodeConfiguration).write(new File(nodeSourceDir, nodeName
				+ "NodeModel.java"));

		new NodeFactoryXMLTemplate(nodeName, nodeConfiguration).write(new File(
				nodeSourceDir, nodeName + "NodeFactory.xml"));

		pluginXML.registerNode(pluginMeta.getPackageRoot() + ".knime.nodes."
				+ nodeName + "." + nodeName + "NodeFactory", absoluteCategory);
	}

	// TODO
	// public static void verifyZip(String filename) {
	// boolean ok = false;
	//
	// Set<String> found_exes = new HashSet<String>();
	//
	// try {
	// ZipInputStream zin = new ZipInputStream(new FileInputStream(
	// filename));
	// ZipEntry ze = null;
	//
	// while ((ze = zin.getNextEntry()) != null) {
	// if (ze.isDirectory()) {
	// // we need a bin directory at the top level
	// if (ze.getName().equals("bin/")
	// || ze.getName().equals("bin")) {
	// ok = true;
	// }
	//
	// } else {
	// File f = new File(ze.getName());
	// if ((f.getParent() != null) && f.getParent().equals("bin")) {
	// found_exes.add(f.getName());
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// if (!ok) {
	// this.panic("binary archive has no toplevel bin directory : "
	// + filename);
	// }
	//
	// for (String nodename : this.node_names) {
	// boolean found = false;
	// if (found_exes.contains(nodename)
	// || found_exes.contains(nodename + ".bin")
	// || found_exes.contains(nodename + ".exe")) {
	// found = true;
	// }
	// if (!found) {
	// this.panic("binary archive has no executable in bin directory for node : "
	// + nodename);
	// }
	// }
	// }

	/**
	 * Copies all valid ini and zip files to the specified {@link File
	 * directory}.
	 * 
	 * @param srcDir
	 * @param destDir
	 * @throws IOException
	 */
	private static void copyPayload(File srcDir, File destDir)
			throws IOException {
		for (String filename : srcDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".ini"))
					return true;
				if (filename.endsWith(".zip")) {
					// verifyZip(destinationFQNNodeDirectory + pathsep +
					// "binres"
					// + pathsep + filename);
					return true;
				}
				return false;
			}
		})) {
			FileUtils.copyFileToDirectory(new File(srcDir, filename), destDir);
		}
	}

}
