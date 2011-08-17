package org.ballproject.knime.nodegeneration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.config.TTDNodeConfigurationReader;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.nodegeneration.templates.TemplateResources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class NodeGenerator
{	
	public static Document          plugindoc;
	public static NodeConfiguration config;
	
	public static String _destdir_;
	public static String _destsrcdir_;
	public static String _packagename_;
	public static String _packagedir_;
	public static String _modulename_;
	public static String _descriptordir_;
	public static String _abspackagedir_;
	public static String _absnodedir_;
	
	public static String cur_cat;
	public static String cur_path;
	public static String package_root;
	public static String _BINPACKNAME_;
	public static String _payloaddir_;
	
	public static void main(String[] args) throws Exception
	{
		Properties props = new Properties();
		props.load(new FileInputStream("plugin.properties"));
			
		_destdir_       = props.getProperty("destdir");
		_payloaddir_    = props.getProperty("payloaddir");
		_packagename_   = props.getProperty("packagename");
		_modulename_    = props.getProperty("modulename");
		package_root    = props.getProperty("package_root");
		_BINPACKNAME_   = props.getProperty("BINPACKNAME");
		_descriptordir_ = props.getProperty("descriptordir");
				
		_destsrcdir_    = _destdir_+"/src";
		_packagedir_    = _packagename_.replace(".","/");
		_abspackagedir_ = _destsrcdir_+"/"+_packagedir_;
		_absnodedir_    = _abspackagedir_+"/knime/nodes"; 
		
		createPackageDirectory();
		
		pre();
		
		installMimeTypes();
		processDescriptors();
				
		post();
		
		
		
		///
		
		InputStream template = TemplateResources.class.getResourceAsStream("PluginActivator.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.replace("__NAME__", _modulename_);
		//tf.write(_abspackagedir_ + "/knime/"+_modulename_+"PluginActivator.java");
		tf.write(_abspackagedir_ + "/knime/PluginActivator.java");
		template.close();
		
		
	}
	


	private static void createPackageDirectory()
	{
		File packagedir = new File(_destsrcdir_+"/"+_packagedir_);
		deleteDirectory(packagedir);
		packagedir.mkdirs();
	}
	
	public static void pre() throws DocumentException, IOException
	{
		

		InputStream template = TemplateResources.class.getResourceAsStream("io/exporter/MimeFileExporterNodeDialog.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/io/MimeFileExporterNodeDialog.java");
		template.close();
		
		template = TemplateResources.class.getResourceAsStream("io/exporter/MimeFileExporterNodeModel.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/io/MimeFileExporterNodeModel.java");
		template.close();
		
		template = TemplateResources.class.getResourceAsStream("io/exporter/MimeFileExporterNodeView.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/io/MimeFileExporterNodeView.java");
		template.close();
		
		template = TemplateResources.class.getResourceAsStream("io/exporter/MimeFileExporterNodeFactory.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/io/MimeFileExporterNodeFactory.java");
		template.close();
		
		template = TemplateResources.class.getResourceAsStream("io/exporter/MimeFileExporterNodeFactory.template.xml");
		tf = new TemplateFiller();
		tf.read(template);
		//tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/io/MimeFileExporterNodeFactory.xml");
		template.close();
		
		/*
		template = TemplateResources.class.getResourceAsStream("MimeFileCellFactory.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.write(_abspackagedir_ + "/knime/nodes/io/MimeFileCellFactory.java");
		template.close();
		*/
		//copyStream(TemplateResources.class.getResourceAsStream("MimeFileCellFactory.template"),new File(_abspackagedir_ + "/knime/nodes/io/MimeFileCellFactory.java"));
		
		copyStream(TemplateResources.class.getResourceAsStream("plugin.xml.template"),new File(_destdir_ + "/plugin.xml"));
		
		DOMDocumentFactory factory = new DOMDocumentFactory();
		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(factory);

		plugindoc = reader.read(new FileInputStream(new File(_destdir_ + "/plugin.xml")));
		
	}
	
	private static void installMimeTypes() throws DocumentException, IOException
	{
		DOMDocumentFactory factory = new DOMDocumentFactory();
		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(factory);

		Document doc = reader.read(new FileInputStream(new File(_descriptordir_ + "/mimetypes.xml")));
		
		InputStream template = TemplateResources.class.getResourceAsStream("MimeFileCellFactory.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		
		String tpl = "\t\tif(f.getName().endsWith(\"__EXT__\"))\n\t\t{\n\t\tret = __NAME__FileCell.createMimeFileCell(f);\n\t\t}\n";
		String data = "";
		
		List<Node> nodes = doc.selectNodes("//mimetype");
		for(Node node: nodes)
		{
			Element elem    = (Element) node;
			
			String  name    = elem.valueOf("@name");
			String  ext     = elem.valueOf("@ext");
			String  descr   = elem.valueOf("@description");
			
			System.out.println(name+" "+ext+" "+descr);
			
			createMimeTypeLoader(name, ext);
			createMimeCell(name, ext);
			createMimeValue(name);
		
			ext2type.put(ext,name);
			
			String s = tpl.replace("__EXT__", ext.toLowerCase());
			s = s.replace("__NAME__",name); 
			data += s;
		}
		
		tf.replace("__DATA__", data);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/mimetypes/MimeFileCellFactory.java");
		template.close();
	}
	
	private static void processDescriptors() throws Exception
	{
		File files[] = (new File(_descriptordir_)).listFiles();
		for (File f : files)
		{
			String filename = f.getName();
			
			if (filename.endsWith(".ttd"))
				processNode(filename, f);
			
		}
	}
	
	public static void processNode(String name, File descriptor) throws Exception
	{

		TTDNodeConfigurationReader reader = new TTDNodeConfigurationReader();
		config = reader.read(new FileInputStream(descriptor));

		String nodeName = config.getName();
		System.out.println("## processing Node "+nodeName);
		
		cur_cat  = config.getCategory();
		cur_path = getPathPrefix(cur_cat); 
		
		
		File nodeConfigDir = new File(_absnodedir_ + "/" + nodeName + "/config");
		nodeConfigDir.mkdirs();

		copyFile(descriptor, new File(_absnodedir_ + "/" + nodeName + "/config/config.xml"));
		
		
		registerCategoryPath(cur_cat);
		
		
		createFactory(nodeName);
		
		createDialog(nodeName);
		createView(nodeName);
		
		createModel(nodeName);
		fillMimeTypes();
		createXMLDescriptor(nodeName);
		writeModel(nodeName);
		
		registerNode( _packagename_ + ".knime.nodes." + nodeName + "." + nodeName + "NodeFactory");
	}
	
	static public boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}	
	
	public static String getPathPrefix(String path)
	{
		String ret = "";
		String[] toks = path.split("/");
		for(int i=0;i<(toks.length-1);i++)
			ret += toks[i]+"/";
		return ret;
	}
	
	public static List<String> getPathPrefixes(String path)
	{
		List<String> ret = new ArrayList<String>();
		String[] toks = path.split("/");
		String prefix="";
		for(int i=0;i<toks.length;i++)
		{
			prefix+=toks[i]+"/";
			ret.add(prefix);
		}
		return ret;
	}
	
	public static String getPathSuffix(String path)
	{
		String[] toks = path.split("/");
		return toks[toks.length-1];
	}
	
	private static Set<String> ext_loaders = new HashSet<String>();
	
	private static void createMimeTypeLoader(String name, String ext) throws IOException
	{
		if(ext_loaders.contains(ext))
			return;
		ext_loaders.add(ext);
		
		//String extension = ext.toUpperCase();
		InputStream template = TemplateResources.class.getResourceAsStream("io/MimeFileImporterNodeDialog.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.replace("__NAME__", name);
		tf.replace("__EXT__", ext.toLowerCase());
		tf.write(_absnodedir_ + "/io/" + name + "FileImporterNodeDialog.java");

		template = TemplateResources.class.getResourceAsStream("io/MimeFileImporterNodeModel.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.replace("__NAME__", name);
		tf.write(_absnodedir_ + "/io/" + name + "FileImporterNodeModel.java");

		template = TemplateResources.class.getResourceAsStream("io/MimeFileImporterNodeFactory.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.replace("__NAME__", name);
		tf.write(_absnodedir_ + "/io/" + name + "FileImporterNodeFactory.java");

		template = TemplateResources.class.getResourceAsStream("io/MimeFileImporterNodeView.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.replace("__NAME__", name);
		tf.write(_absnodedir_ + "/io/" + name + "FileImporterNodeView.java");

		template = TemplateResources.class.getResourceAsStream("io/MimeFileImporterNodeFactory.xml.template");
		tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _packagename_);
		tf.replace("__NAME__", name);
		tf.replace("__EXT__", ext.toLowerCase());
		tf.write(_absnodedir_ + "/io/" + name + "FileImporterNodeFactory.xml");

		registerNode(_packagename_ + ".knime.nodes.io." + name + "FileImporterNodeFactory",package_root+"/IO");
	}

	private static void createMimeCell(String name, String ext) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/MIMEFileCell.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NAME__", name);
		tf.replace("__EXT__", ext);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/mimetypes/" + name + "FileCell.java");
	}

	private static void createMimeValue(String ext) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/MIMEFileValue.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NAME__", ext);
		tf.replace("__EXT__", ext);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/mimetypes/" + ext + "FileValue.java");
	}

	public static Set<String> categories = new HashSet<String>();
	
	
	public static void registerCategoryPath(String path)
	{
		List<String> prefixes = getPathPrefixes(path);
		for(String prefix: prefixes)
		{
			registerCategory(prefix);
		}
	}
	
	public static void registerCategory(String path)
	{
		System.out.println("registering Category " + path);
	
		if(categories.contains(path))
			return;

		categories.add(path);
		
		String   cat_name    = getPathSuffix(path);
		String   path_prefix = getPathPrefix(path);
		
		Node node = plugindoc.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.categories']");
		
		Element elem = (Element) node;
		
		elem.addElement("category").addAttribute("description", path).addAttribute("icon", "icons/category.png")
				.addAttribute("path", "/community/"+path_prefix).addAttribute("name", cat_name).addAttribute("level-id", cat_name);
	}
	
	public static void createFactory(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeFactory.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeFactory.java");
	}

	public static void createDialog(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeDialog.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeDialog.java");
	}

	public static String join(Collection<String> col)
	{
		String ret = "";
		for(String s: col)
		{
			ret += s+",";
		}
		ret = ret.substring(0,ret.length()-1);
		return ret;
	}
	
	public static void createXMLDescriptor(String nodeName) throws IOException
	{
		// ports
		//String ip = "<inPort index=\"__IDX__\" name=\"__PORTDESCR__ [__MIMETYPE____OPT__]\">__PORTDESCR__ [__MIMETYPE____OPT__]</inPort>";
		String ip = "<inPort index=\"__IDX__\" name=\"__PORTDESCR__\">__PORTDESCR__ [__MIMETYPE____OPT__]</inPort>";
		String inports = "";
		int idx = 0;
		for (Port port : config.getInputPorts())
		{
			String ipp = ip;
			ipp = ip.replace("__PORTNAME__", port.getName());
			ipp = ipp.replace("__PORTDESCR__", port.getDescription());
			ipp = ipp.replace("__IDX__", String.format("%d", idx++));
			
			// fix me
			//ipp = ipp.replace("__MIMETYPE__", port.getMimeTypes().get(0).getExt());
			List<String> mts = new ArrayList<String>();
			for(MIMEtype mt: port.getMimeTypes())
			{
				mts.add(mt.getExt());
			}
			ipp = ipp.replace("__MIMETYPE__", join(mts));
			
			ipp = ipp.replace("__OPT__", (port.isOptional()?",opt.":""));
			inports += ipp + "\n";
		}

		String op = "<outPort index=\"__IDX__\" name=\"__PORTDESCR__ [__MIMETYPE__]\">__PORTDESCR__ [__MIMETYPE__]</outPort>";
		String outports = "";
		idx = 0;
		for (Port port : config.getOutputPorts())
		{
			String opp = op;
			opp = op.replace("__PORTNAME__", port.getName());
			opp = opp.replace("__PORTDESCR__", port.getDescription());
			opp = opp.replace("__IDX__", String.format("%d", idx++));
			
			// fix me
			opp = opp.replace("__MIMETYPE__", port.getMimeTypes().get(0).getExt());
			
			outports += opp + "\n";
		}

		StringBuffer buf = new StringBuffer();
		for (Parameter<?> p : config.getParameters())
		{
			buf.append("\t\t<option name=\"" + p.getKey() + "\">" + p.getDescription() + "</option>\n");
		}
		String opts = buf.toString();

		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeXMLDescriptor.template");

		TemplateFiller tf = new TemplateFiller();
		tf.read(template);

		tf.replace("__NODENAME__", nodeName);
		tf.replace("__INPORTS__", inports);
		tf.replace("__OUTPORTS__", outports);
		tf.replace("__OPTIONS__", opts);
		tf.replace("__DESCRIPTION__", config.getDescription());
		String pp = prettyPrint(config.getManual());
		tf.replace("__MANUAL__", pp);
		tf.replace("__DOCURL__", config.getDocUrl());
		
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeFactory.xml");

	}

	private static String prettyPrint(String manual)
	{
		StringBuffer sb = new StringBuffer();
		String[] toks = manual.split("\\n");
		for(String tok: toks)
		{
			sb.append("<p><![CDATA["+tok+"]]></p>");
		}
		return sb.toString();
	}

	public static void createView(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeView.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", _packagename_);
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeView.java");
	}

	private static TemplateFiller curmodel_tf = null;
	
	public static void createModel(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeModel.template");
		curmodel_tf = new TemplateFiller();

		curmodel_tf.read(template);
		curmodel_tf.replace("__NODENAME__", nodeName);
		curmodel_tf.replace("__BASE__", _packagename_);
		curmodel_tf.replace("__NAME__", _modulename_);
		curmodel_tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeModel.java");
	}

	protected static void writeModel(String nodeName) throws IOException
	{
		curmodel_tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeModel.java");
	}
	
	
	public static Map<String,String> ext2type = new HashMap<String,String>();
	
	private static void fillMimeTypes() throws IOException
	{
		String clazzez = "";
		for (Port port : config.getInputPorts())
		{
			String tmp = "{";
			for(MIMEtype type: port.getMimeTypes())
			{
				String ext = ext2type.get(type.getExt());
				if(ext==null)
				{
					System.out.println("unknown mime type : |"+type.getExt()+"|");
					System.exit(1);
				}
				tmp += "DataType.getType(" + ext + "FileCell.class),";
			}
			tmp = tmp.substring(0,tmp.length()-1);
			tmp+="},";
			clazzez += tmp;
		}
		
		clazzez = clazzez.substring(0,clazzez.length()-1);
		clazzez += "}";
		createInClazzezModel(clazzez);

		clazzez = "";
		for (Port port : config.getOutputPorts())
		{
			String tmp = "{";
			for(MIMEtype type: port.getMimeTypes())
			{
				String ext = ext2type.get(type.getExt());
				if(ext==null)
				{
					System.out.println("unknown mime type : "+type.getExt());
					System.exit(1);
				}
				tmp += "DataType.getType(" + ext + "FileCell.class),";
			}
			tmp = tmp.substring(0,tmp.length()-1);
			tmp+="},";
			clazzez += tmp;
		}
		clazzez = clazzez.substring(0,clazzez.length()-1);
		clazzez += "}";
		
		createOutClazzezModel(clazzez);
	}
	
	public static void makeMimeFileCellFactory()
	{
		
		
	}
	
	public static void createInClazzezModel(String clazzez) throws IOException
	{
		if (clazzez.equals(""))
			clazzez = "null";
		else
			clazzez = clazzez.substring(0, clazzez.length() - 1);
		curmodel_tf.replace("__INCLAZZEZ__", clazzez);
	}

	public static void createOutClazzezModel(String clazzez) throws IOException
	{
		if (clazzez.equals(""))
			clazzez = "null";
		else
			clazzez = clazzez.substring(0, clazzez.length() - 1);
		curmodel_tf.replace("__OUTCLAZZEZ__", clazzez);
	}
	
	public static void registerNode(String clazz)
	{
		System.out.println("registering Node " + clazz);
		Node node = plugindoc.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.nodes']");
		Element elem = (Element) node;

		elem.addElement("node").addAttribute("factory-class", clazz).addAttribute("id", clazz).addAttribute("category-path", "/community/"+cur_cat);
	}
	
	public static void post() throws IOException
	{	
		registerNode(_packagename_ + ".knime.nodes.io.MimeFileExporterNodeFactory",package_root+"/IO");
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		
		XMLWriter writer = new XMLWriter( new FileWriter(_destdir_ + "/plugin.xml") , format );
        writer.write( plugindoc );

		writer.close();
		
		// prepare binary resources
		InputStream template = TemplateResources.class.getResourceAsStream("BinaryResources.template");
		curmodel_tf = new TemplateFiller();

		curmodel_tf.read(template);
		curmodel_tf.replace("__BASE__", _packagename_);
		curmodel_tf.replace("__BINPACKNAME__", _BINPACKNAME_);
		curmodel_tf.write(_absnodedir_ + "/binres/BinaryResources.java");
		template.close();
		
		//
		copyFile(new File(_payloaddir_+"/binaries_lnx_x64.zip"),new File(_absnodedir_ + "/binres/binaries_lnx_x64.zip"));
		copyFile(new File(_payloaddir_+"/binaries_lnx_x64.ini"),new File(_absnodedir_ + "/binres/binaries_lnx_x64.ini"));
		
		copyFile(new File(_payloaddir_+"/binaries_mac_x64.zip"),new File(_absnodedir_ + "/binres/binaries_mac_x64.zip"));
		copyFile(new File(_payloaddir_+"/binaries_mac_x64.ini"),new File(_absnodedir_ + "/binres/binaries_mac_x64.ini"));
	}
	
	public static void registerNode(String clazz, String cat)
	{
		System.out.println("registering Node " + clazz);
		registerCategoryPath(cat);
		
		Node node = plugindoc.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.nodes']");
		Element elem = (Element) node;

		elem.addElement("node").addAttribute("factory-class", clazz).addAttribute("id", clazz).addAttribute("category-path", "/community/"+cat);
	}


	public static void copyStream(InputStream in, File dest) throws IOException
	{
		FileOutputStream    out = new FileOutputStream(dest);
		BufferedInputStream bin = new BufferedInputStream(in);
		byte[] buffer = new byte[2048];
		int len;
 		while((len=bin.read(buffer, 0, 2048))!=-1)
 		{
 			out.write(buffer,0,len);
 		}
 		out.close();
 		bin.close();
	}
	
	public static void copyFile(File in, File out) throws IOException
	{
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////

}
