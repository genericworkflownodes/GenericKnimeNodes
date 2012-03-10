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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.CTDNodeConfigurationReaderException;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.InvalidNodeNameException;
import org.ballproject.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import org.ballproject.knime.nodegeneration.model.PluginXmlTemplate;
import org.ballproject.knime.nodegeneration.model.directories.KNIMEPluginMeta;
import org.ballproject.knime.nodegeneration.model.directories.NodesBuildDirectory;
import org.ballproject.knime.nodegeneration.model.directories.NodesSourceDirectory;
import org.ballproject.knime.nodegeneration.model.mime.MimeType;
import org.ballproject.knime.nodegeneration.templates.TemplateResources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.core.commands.ExecutionException;
import org.jaxen.JaxenException;

public class NodeGenerator {
	private static final String PLUGIN_PROPERTIES = "plugin.properties";

	static Logger logger = Logger.getLogger(NodeGenerator.class
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

		// GO
		logger.info("Creating KNIME plugin sources in: " + buildDir.getPath());

		Document pluginXML = PluginXmlTemplate.getFromTemplate();

		try {
			installMimeTypes(pluginXML, this.buildDir.getKnimeNodesDirectory(),
					srcDir.getMimeTypes(), meta.getName());
		} catch (JaxenException e) {
			throw new DocumentException(e);
		}

		Set<String> node_names = new HashSet<String>();
		Set<String> ext_tools = new HashSet<String>();
		processDescriptors(
				node_names,
				ext_tools,
				pluginXML,
				(dynamicCTDs) ? srcDir.getExecutablesDirectory() : srcDir
						.getDescriptorsDirectory(), meta.getPackageRoot(),
				meta.getName(), this.buildDir.getKnimeNodesDirectory(),
				meta.getPackageRoot());

		// TODO
		// this.installIcon();

		fillProperties(srcDir.getProperties(),
				this.buildDir.getPackageRootDirectory());

		post(meta.getPackageRoot(), this.buildDir.getPackageRootDirectory(),
				this.buildDir.getKnimeNodesDirectory(),
				srcDir.getPayloadDirectory(), node_names, ext_tools);

		PluginXmlTemplate.saveTo(pluginXML, buildDir.getPluginXml());

		createManifest(new File(buildDir, "META-INF" + File.separator
				+ "MANIFEST.MF"), meta.getName(), meta.getVersion());
	}

	public File getPluginDirectory() {
		return this.srcDir;
	}

	public File getPreparedPluginDirectory() {
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

	public static void createManifest(File destinationManifest,
			String pluginname, String pluginversion) throws IOException {
		String manifest = IOUtils.toString(TemplateResources.class
				.getResourceAsStream("MANIFEST.MF.template"));

		manifest = manifest.replaceAll("@@pluginname@@", pluginname);
		manifest = manifest.replaceAll("@@pluginversion@@", pluginversion);

		destinationManifest.getParentFile().mkdirs();
		FileUtils.writeStringToFile(destinationManifest, manifest);
	}

	/**
	 * TODO
	 * 
	 * @param pluginXML
	 * @param destinationFQNNodeDirectory
	 * @param mimetypesXML
	 * @param packageName
	 * @throws DocumentException
	 * @throws IOException
	 * @throws JaxenException
	 */
	private static void installMimeTypes(Document pluginXML,
			File destinationFQNNodeDirectory, List<MimeType> mimeTypes,
			String packageName) throws DocumentException, IOException,
			JaxenException {

		InputStream template = TemplateResources.class
				.getResourceAsStream("MimeFileCellFactory.template");

		TemplateFiller tf = new TemplateFiller();
		tf.read(template);

		String mimeTypeAddTemplate = "\t\tmimetypes.add(new MIMEType(\"__EXT__\"));\n";
		String mimetypes_code = "";

		Set<String> mimetypes = new HashSet<String>();

		for (MimeType mimeType : mimeTypes) {
			logger.info("MIME Type read: " + mimeType.getName());

			if (mimetypes.contains(mimeType.getName())) {
				logger.log(Level.WARNING, "skipping duplicate mime type "
						+ mimeType.getName());
			}

			String s4 = mimeTypeAddTemplate.replace("__EXT__", mimeType.getExt()
					.toLowerCase());
			mimetypes_code += s4;
		}

		tf.replace("__MIMETYPES__", mimetypes_code);
		tf.replace("__BASE__", packageName);
		tf.write(new File(destinationFQNNodeDirectory, "mimetypes"
				+ File.separator + "MimeFileCellFactory.java"));
		template.close();
	}

	private static void processDescriptors(Set<String> node_names,
			Set<String> ext_tools, Document pluginXML,
			File descriptorDirectory, String nodeRepositoryRoot,
			String pluginName, File destinationFQNNodeDirectory,
			String packageName) throws IOException, DuplicateNodeNameException,
			InvalidNodeNameException, CTDNodeConfigurationReaderException,
			UnknownMimeTypeException {
		Set<String> categories = new HashSet<String>();
		for (File file : descriptorDirectory.listFiles()) {
			if (file.getName().endsWith(".ctd")) {
				logger.info("start processing node " + file);
				processNode(pluginXML, file, node_names, ext_tools,
						nodeRepositoryRoot, pluginName,
						destinationFQNNodeDirectory, categories, packageName);
			}
		}
	}

	public static void processNode(Document pluginXML, File ctdFile,
			Set<String> node_names, Set<String> ext_tools,
			String nodeRepositoryRoot, String pluginName,
			File destinationFQNNodeDirectory, Set<String> categories,
			String packageName) throws IOException, DuplicateNodeNameException,
			InvalidNodeNameException, CTDNodeConfigurationReaderException,
			UnknownMimeTypeException {

		logger.info("## processing Node " + ctdFile.getName());

		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		NodeConfiguration config = reader.read(new FileInputStream(ctdFile));

		String nodeName = config.getName();

		String oldNodeName = null;

		if (!KNIMENode.checkNodeName(nodeName)) {
			oldNodeName = nodeName;

			// we try to fix the nodename
			nodeName = KNIMENode.fixNodeName(nodeName);

			if (!KNIMENode.checkNodeName(nodeName))
				throw new InvalidNodeNameException("The node name \""
						+ nodeName + "\" is invalid.");
		}

		if (oldNodeName == null) {
			if (node_names.contains(nodeName))
				throw new DuplicateNodeNameException(nodeName);

			if (config.getStatus().equals("internal")) {
				node_names.add(nodeName);
			} else {
				ext_tools.add(nodeName);
			}
		} else {
			if (node_names.contains(oldNodeName))
				throw new DuplicateNodeNameException(nodeName);

			if (config.getStatus().equals("internal")) {
				node_names.add(oldNodeName);
			} else {
				ext_tools.add(nodeName);
			}
		}

		String cur_cat = new File("/" + nodeRepositoryRoot + "/" + pluginName,
				config.getCategory()).getPath();

		File nodeConfigDir = new File(destinationFQNNodeDirectory
				+ File.separator + nodeName + File.separator + "config");
		nodeConfigDir.mkdirs();

		Helper.copyFile(ctdFile, new File(destinationFQNNodeDirectory
				+ File.separator + nodeName + File.separator + "config"
				+ File.separator + "config.xml"));

		registerPath(cur_cat, pluginXML, categories);

		createFactory(nodeName, destinationFQNNodeDirectory, packageName);

		createDialog(nodeName, destinationFQNNodeDirectory, packageName);

		createView(nodeName, destinationFQNNodeDirectory, packageName);

		TemplateFiller curmodel_tf = createModel(nodeName,
				destinationFQNNodeDirectory, packageName);

		fillMimeTypes(config, curmodel_tf);

		TemplateFiller nodeFactoryXML = createXMLDescriptor(nodeName, config);
		nodeFactoryXML.write(destinationFQNNodeDirectory + "/" + nodeName + "/"
				+ nodeName + "NodeFactory.xml");

		writeModel(nodeName, destinationFQNNodeDirectory, curmodel_tf);

		registerNode(packageName + ".knime.nodes." + nodeName + "." + nodeName
				+ "NodeFactory", cur_cat, pluginXML, categories);

	}

	public static void registerPath(String path, Document pluginXML,
			Set<String> categories) {
		List<String> prefixes = Utils.getPathPrefixes(path);
		for (String prefix : prefixes) {
			registerPathPrefix(prefix, pluginXML, categories);
		}
	}

	public static void registerPathPrefix(String path, Document pluginXML,
			Set<String> categories) {
		// do not register any top level or root path
		if (path.equals("/") || new File(path).getParent().equals("/"))
			return;

		if (categories.contains(path))
			return;

		logger.info("registering path prefix " + path);

		categories.add(path);

		String cat_name = Utils.getPathSuffix(path);
		String path_prefix = Utils.getPathPrefix(path);

		Node node = pluginXML
				.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.categories']");

		Element elem = (Element) node;
		logger.info("name=" + cat_name);

		elem.addElement("category").addAttribute("description", path)
				.addAttribute("icon", "icons/category.png")
				.addAttribute("path", path_prefix)
				.addAttribute("name", cat_name)
				.addAttribute("level-id", cat_name);
	}

	public static void createFactory(String nodeName,
			File destinationFQNNodeDirectory, String packageName)
			throws IOException {
		InputStream template = NodeGenerator.class
				.getResourceAsStream("templates/NodeFactory.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", packageName);
		tf.write(destinationFQNNodeDirectory + "/" + nodeName + "/" + nodeName
				+ "NodeFactory.java");
	}

	public static void createDialog(String nodeName,
			File destinationFQNNodeDirectory, String packageName)
			throws IOException {
		InputStream template = NodeGenerator.class
				.getResourceAsStream("templates/NodeDialog.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", packageName);
		tf.write(destinationFQNNodeDirectory + "/" + nodeName + "/" + nodeName
				+ "NodeDialog.java");
	}

	public static String join(Collection<String> col) {
		String ret = "";
		for (String s : col) {
			ret += s + ",";
		}
		ret = ret.substring(0, ret.length() - 1);
		return ret;
	}

	public static TemplateFiller createXMLDescriptor(String nodeName,
			NodeConfiguration config) throws IOException {
		// ports
		String ip = "<inPort index=\"__IDX__\" name=\"__PORTDESCR__\"><![CDATA[__PORTDESCR__ [__MIMETYPE____OPT__]]]></inPort>";
		String inports = "";
		int idx = 0;
		for (Port port : config.getInputPorts()) {
			String ipp = ip;
			ipp = ip.replace("__PORTNAME__", port.getName());
			ipp = ipp.replace("__PORTDESCR__", port.getDescription());
			ipp = ipp.replace("__IDX__", String.format("%d", idx++));

			// fix me
			// ipp = ipp.replace("__MIMETYPE__",
			// port.getMimeTypes().get(0).getExt());
			List<String> mts = new ArrayList<String>();
			for (MIMEtype mt : port.getMimeTypes()) {
				mts.add(mt.getExt());
			}
			ipp = ipp.replace("__MIMETYPE__", join(mts));

			ipp = ipp.replace("__OPT__", (port.isOptional() ? ",opt." : ""));
			inports += ipp + "\n";
		}

		String op = "<outPort index=\"__IDX__\" name=\"__PORTDESCR__ [__MIMETYPE__]\"><![CDATA[__PORTDESCR__ [__MIMETYPE__]]]></outPort>";
		String outports = "";
		idx = 0;
		for (Port port : config.getOutputPorts()) {
			String opp = op;
			opp = op.replace("__PORTNAME__", port.getName());
			opp = opp.replace("__PORTDESCR__", port.getDescription());
			opp = opp.replace("__IDX__", String.format("%d", idx++));

			// fix me
			opp = opp.replace("__MIMETYPE__", port.getMimeTypes().get(0)
					.getExt());

			outports += opp + "\n";
		}

		StringBuffer buf = new StringBuffer();
		for (Parameter<?> p : config.getParameters()) {
			buf.append("\t\t<option name=\"" + p.getKey() + "\"><![CDATA["
					+ p.getDescription() + "]]></option>\n");
		}
		String opts = buf.toString();

		InputStream template = NodeGenerator.class
				.getResourceAsStream("templates/NodeXMLDescriptor.template");

		TemplateFiller tf = new TemplateFiller();
		tf.read(template);

		tf.replace("__NODENAME__", nodeName);
		tf.replace("__INPORTS__", inports);
		tf.replace("__OUTPORTS__", outports);
		tf.replace("__OPTIONS__", opts);
		tf.replace("__DESCRIPTION__", config.getDescription());
		String pp = prettyPrint(config.getManual());
		tf.replace("__MANUAL__", pp);
		if (!config.getDocUrl().equals("")) {
			String ahref = "<a href=\"" + config.getDocUrl()
					+ "\">Web Documentation for " + nodeName + "</a>";
			tf.replace("__DOCLINK__", ahref);
		} else {
			tf.replace("__DOCLINK__", "");
		}
		return tf;
	}

	private static String prettyPrint(String manual) {
		if (manual.equals(""))
			return "";
		StringBuffer sb = new StringBuffer();
		String[] toks = manual.split("\\n");
		for (String tok : toks) {
			sb.append("<p><![CDATA[" + tok + "]]></p>");
		}
		return sb.toString();
	}

	public static void createView(String nodeName,
			File destinationFQNNodeDirectory, String packageName)
			throws IOException {
		InputStream template = NodeGenerator.class
				.getResourceAsStream("templates/NodeView.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", packageName);
		tf.write(destinationFQNNodeDirectory + "/" + nodeName + "/" + nodeName
				+ "NodeView.java");
	}

	public static TemplateFiller createModel(String nodeName,
			File destinationFQNNodeDirectory, String packageName)
			throws IOException {
		InputStream template = NodeGenerator.class
				.getResourceAsStream("templates/NodeModel.template");
		TemplateFiller curmodel_tf = new TemplateFiller();

		curmodel_tf.read(template);
		curmodel_tf.replace("__NODENAME__", nodeName);
		curmodel_tf.replace("__BASE__", packageName);
		curmodel_tf.write(destinationFQNNodeDirectory + "/" + nodeName + "/"
				+ nodeName + "NodeModel.java");
		return curmodel_tf;
	}

	protected static void writeModel(String nodeName,
			File destinationFQNNodeDirectory, TemplateFiller curmodel_tf)
			throws IOException {
		curmodel_tf.write(destinationFQNNodeDirectory + "/" + nodeName + "/"
				+ nodeName + "NodeModel.java");
	}

	private static void fillMimeTypes(NodeConfiguration config,
			TemplateFiller curmodel_tf) throws UnknownMimeTypeException {
		String clazzez = "";
		for (Port port : config.getInputPorts()) {
			String tmp = "{";
			for (MIMEtype type : port.getMimeTypes()) {
				String ext = type.getExt().toLowerCase();
				if (ext == null)
					throw new UnknownMimeTypeException(type);
				/*
				 * if(port.isMultiFile()) tmp +=
				 * "DataType.getType(ListCell.class, DataType.getType(" + ext +
				 * "FileCell.class)),"; else tmp += "DataType.getType(" + ext +
				 * "FileCell.class),";
				 */
				tmp += "new MIMEType(\"" + ext + "\"),";
			}
			tmp = tmp.substring(0, tmp.length() - 1);
			tmp += "},";
			clazzez += tmp;
		}

		if (!clazzez.equals("")) {
			clazzez = clazzez.substring(0, clazzez.length() - 1);
		}

		clazzez += "}";
		createInClazzezModel(clazzez, curmodel_tf);

		clazzez = "";
		for (Port port : config.getOutputPorts()) {
			String tmp = "{";
			for (MIMEtype type : port.getMimeTypes()) {
				String ext = type.getExt().toLowerCase();
				if (ext == null)
					throw new UnknownMimeTypeException(type);
				/*
				 * if(port.isMultiFile()) tmp +=
				 * "DataType.getType(ListCell.class, DataType.getType(" + ext +
				 * "FileCell.class)),"; else tmp += "DataType.getType(" + ext +
				 * "FileCell.class),";
				 */
				tmp += "new MIMEType(\"" + ext + "\"),";
			}
			tmp = tmp.substring(0, tmp.length() - 1);
			tmp += "},";
			clazzez += tmp;
		}

		if (!clazzez.equals("")) {
			clazzez = clazzez.substring(0, clazzez.length() - 1);
		}

		clazzez += "}";

		createOutClazzezModel(clazzez, curmodel_tf);
	}

	public static void createInClazzezModel(String clazzez,
			TemplateFiller curmodel_tf) {
		if (clazzez.equals("")) {
			clazzez = "null";
		} else {
			clazzez = clazzez.substring(0, clazzez.length() - 1);
		}
		curmodel_tf.replace("__INCLAZZEZ__", clazzez);
	}

	public static void createOutClazzezModel(String clazzez,
			TemplateFiller curmodel_tf) {
		if (clazzez.equals("")) {
			clazzez = "null";
		} else {
			clazzez = clazzez.substring(0, clazzez.length() - 1);
		}
		curmodel_tf.replace("__OUTCLAZZEZ__", clazzez);
	}

	public static void registerNode(String clazz, String path,
			Document pluginXML, Set<String> categories) {
		logger.info("registering Node " + clazz);
		registerPath(path, pluginXML, categories);

		Node node = pluginXML
				.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.nodes']");
		Element elem = (Element) node;

		elem.addElement("node").addAttribute("factory-class", clazz)
				.addAttribute("id", clazz).addAttribute("category-path", path);
	}

	public static void post(String packageName, File destinationFQNDirectory,
			File destinationFQNNodeDirectory, File payloadDirectory,
			Set<String> node_names, Set<String> ext_tools) throws IOException {

		// prepare binary resources
		InputStream template = TemplateResources.class
				.getResourceAsStream("BinaryResources.template");
		TemplateFiller curmodel_tf = new TemplateFiller();

		curmodel_tf.read(template);
		curmodel_tf.replace("__BASE__", packageName);
		curmodel_tf.replace("__BINPACKNAME__", packageName);
		curmodel_tf.write(new File(destinationFQNNodeDirectory,
				"/binres/BinaryResources.java"));
		template.close();

		//
		String[] binFiles = payloadDirectory.list();
		for (String filename : binFiles) {
			// do not copy directories
			if (new File(payloadDirectory, filename).isDirectory()) {
				continue;
			}

			// only copy zip and ini files
			if (filename.toLowerCase().endsWith("zip")) {
				Helper.copyFile(new File(payloadDirectory, filename), new File(
						destinationFQNNodeDirectory, "binres" + File.separator
								+ filename));
				// TODO
				// verifyZip(destinationFQNNodeDirectory + pathsep + "binres"
				// + pathsep + filename);
			}
			if (filename.toLowerCase().endsWith("ini")) {
				Helper.copyFile(new File(payloadDirectory, filename), new File(
						destinationFQNNodeDirectory, "binres" + File.separator
								+ filename));
			}
		}

		template = TemplateResources.class
				.getResourceAsStream("PluginActivator.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", packageName);
		tf.replace("__NAME__", packageName);
		tf.write(destinationFQNDirectory + File.separator + "knime"
				+ File.separator + "PluginActivator.java");
		template.close();

		FileWriter ini_writer = new FileWriter(destinationFQNDirectory
				+ File.separator + "knime" + File.separator
				+ "ExternalTools.dat");
		for (String ext_tool : ext_tools) {
			ini_writer.write(ext_tool + "\n");
		}
		ini_writer.close();

		ini_writer = new FileWriter(destinationFQNDirectory + File.separator
				+ "knime" + File.separator + "InternalTools.dat");
		for (String int_tool : node_names) {
			ini_writer.write(int_tool + "\n");
		}
		ini_writer.close();
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

}
