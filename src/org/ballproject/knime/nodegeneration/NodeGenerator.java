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
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.config.TTDNodeConfigurationReader;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.base.schemas.SchemaProvider;
import org.ballproject.knime.base.schemas.SchemaValidator;
import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.base.util.ToolRunner;
import org.ballproject.knime.nodegeneration.templates.TemplateResources;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

public class NodeGenerator
{	
	public static Logger logger = Logger.getLogger(NodeGenerator.class.getCanonicalName());
	
	public static Document          plugindoc;
	public static NodeConfiguration config;
	
	public static String _pluginname_;
	public static String _destdir_;
	public static String _destsrcdir_;
	public static String _pluginpackage_;
	public static String _packagedir_;
	public static String _descriptordir_;
	public static String _executabledir_;
	public static String _abspackagedir_;
	public static String _absnodedir_;
	
	public static String cur_cat;
	public static String cur_path;
	public static String _package_root_;
	public static String _BINPACKNAME_;
	public static String _payloaddir_;
	
	public static void assertRestrictedAlphaNumeric(Object obj, String id)
	{
		if(obj==null||obj.toString().equals(""))
		{
			panic(id+" was not properly defined");
		}
		String re = "^\\w+$";
		if(!obj.toString().matches(re))
		{
			panic(id+" is not a proper alpha numeric value "+obj.toString());
		}
	}
	
	public static void assertDefinition(Object obj, String id)
	{
		if(obj==null||obj.toString().equals(""))
		{
			panic(id+" was not properly defined");
		}
	}
	
	public static void assertValidPackageName(String pname, String id)
	{
		if(pname==null||pname.equals(""))
		{
			panic(id+" is no proper Java package name");
		}
		String re = "^([A-Za-z_]{1}[A-Za-z0-9_]*(\\.[A-Za-z_]{1}[A-Za-z0-9_]*)*)$";
		if(!pname.matches(re))
			panic(id+" is no proper Java package name");
	}
	
	public static void assertDirectoryExistence(String dirname, String id)
	{
		File dir = new File(dirname);
		if ( !(dir.exists() && dir.isDirectory()) )
		{
			panic(dirname+" supplied as "+id+" is no valid directory");
		}
	}
	
	public static void assertFileExistence(String filename, String id)
	{
		File f = new File(filename);
		if ( !f.exists() )
		{
			panic(filename+" supplied as "+id+" is no valid file");
		}
	}
	
	public static String makePluginName(String s)
	{
		int idx = s.lastIndexOf(".");
		if(idx==-1)
			return s;
		return s.substring(idx+1);
	}
	
	public static void main(String[] args) throws Exception
	{
		// read in properties for building the plugin
		Properties props = new Properties();
		props.load(new FileInputStream("plugin.properties"));
				
		// ... these are ..
		
		// the directory containing the payload (= ini-file,zip with binaries for each platform)
		_payloaddir_    = props.getProperty("payloaddir");
		assertDefinition(_payloaddir_,"payloaddir");
		assertDirectoryExistence(_payloaddir_,"payloaddir");
		
		// the name of the package (i.e. org.roettig.foo)
		_pluginpackage_   = props.getProperty("pluginpackage");
		assertDefinition(_pluginpackage_,"pluginpackage");
		assertValidPackageName(_pluginpackage_,"pluginpackage");
		
		_pluginname_      = props.getProperty("pluginname", makePluginName(_pluginpackage_));
		assertRestrictedAlphaNumeric(_pluginname_,"pluginname");
		
		// the root node where to attach the generated nodes 
		_package_root_    = props.getProperty("package_root");
		assertDefinition(_package_root_,"package_root");
		
		if( ! (_package_root_.equals("community")||_package_root_.equals("chemistry")) )
				panic("invalid package root given :"+_package_root_);
				
		_descriptordir_ = props.getProperty("descriptordir");
		
		// no descriptor directory supplied ...
		if(_descriptordir_==null)
		{
			// .. extract tool descriptor information from executable
			_executabledir_ = props.getProperty("executabledir");
			
			if(_executabledir_==null)
				panic("neither tool descriptors nor executables were supplied");
			
			File exedir = new File(_executabledir_); 
			
			if(!exedir.exists()||!exedir.isDirectory())
				panic("supplied executables directory does not exist");
			
			generateDescriptors(props);
		}
		
		_destdir_       = System.getProperty("java.io.tmpdir")+File.separator+"/GENERIC_KNIME_NODES_PLUGINSRC";

		// the name of the binary package is simply copied from the plugin name
		_BINPACKNAME_   = _pluginpackage_;
		_destsrcdir_    = _destdir_+"/src";
		_packagedir_    = _pluginpackage_.replace(".","/");
		_abspackagedir_ = _destsrcdir_+"/"+_packagedir_;
		_absnodedir_    = _abspackagedir_+"/knime/nodes"; 
		
		
		createPackageDirectory();
		
		pre();
		
		installMimeTypes();
		
		processDescriptors();
				
		post();		
		
	}
	

	public static void generateDescriptors(Properties props) throws Exception
	{
		String   par_switch = props.getProperty("parswitch","-write_par");
		File     bindir     = new File(_executabledir_+File.separator+"bin");
		
		if(!bindir.exists()||!bindir.isDirectory())
		{
			panic("could not find bin directory with executables at executabledir: "+_executabledir_);
		}
			
		String ttd_dir  = System.getProperty("java.io.tmpdir")+File.separator+"GENERIC_KNIME_NODES_TTD";
		
		try
		{			 
			File outdir   = new File(ttd_dir);
			outdir.mkdirs();
			outdir.deleteOnExit();
		}
		catch(Exception e)
		{
			panic("could not create temporary directory "+ttd_dir);
		}
		
		String[] exes = bindir.list();
		
		if(exes.length==0)
		{
			panic("found no executables at "+bindir);
		}
		
		for(String exe: exes)
		{
			ToolRunner tr = new ToolRunner();
			File outfile = File.createTempFile("TTD","");
			outfile.deleteOnExit();
			
			// FixMe: this is so *nix style, wont hurt on windows
			// but probably wont help either
			tr.addEnvironmentEntry("LD_LIBRARY_PATH", _executabledir_+File.separator+"lib");
			
			String cmd = _executabledir_+File.separator+"bin"+File.separator+exe+" "+par_switch+" "+outfile.getAbsolutePath();
			tr.run(cmd);
			
			if(tr.getReturnCode()==0)
			{
				copyFile(outfile,new File(ttd_dir+File.separator+outfile.getName()));
			}
			else
			{
				panic("could not execute tool : "+cmd);
			}
		}
		
		_descriptordir_ = ttd_dir;
	}
	
	private static void createPackageDirectory()
	{
		File packagedir = new File(_destsrcdir_+"/"+_packagedir_);
		Helper.deleteDirectory(packagedir);
		packagedir.mkdirs();
	}
	
	public static void pre() throws DocumentException, IOException
	{				
		copyStream(TemplateResources.class.getResourceAsStream("plugin.xml.template"),new File(_destdir_ + "/plugin.xml"));
		
		DOMDocumentFactory factory = new DOMDocumentFactory();
		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(factory);

		plugindoc = reader.read(new FileInputStream(new File(_destdir_ + "/plugin.xml")));
		
	}
	
	private static void installMimeTypes() throws DocumentException, IOException, JaxenException
	{
		assertFileExistence(_descriptordir_ + "/mimetypes.xml","mimetypes.xml");
		
		
		SchemaValidator val = new SchemaValidator();
		val.addSchema(SchemaProvider.class.getResourceAsStream("mimetypes.xsd"));
		if(!val.validates(_descriptordir_ + "/mimetypes.xml"))
		{
			panic("supplied mimetypes.xml does not conform to schema "+val.getErrorReport());
		}
		
		
		DOMDocumentFactory factory = new DOMDocumentFactory();
		SAXReader reader = new SAXReader();
		reader.setDocumentFactory(factory);
		
		Document doc = reader.read(new FileInputStream(new File(_descriptordir_ + "/mimetypes.xml")));
		
		InputStream    template = TemplateResources.class.getResourceAsStream("MimeFileCellFactory.template");
		
		TemplateFiller tf       = new TemplateFiller();
		tf.read(template);
		
		String tpl = "\t\tif(name.endsWith(\"__EXT__\"))\n\t\t{\n\t\tret = new __NAME__FileCell();\n\t\t}\n";
		String data = "";
		
		Set<String> mimetypes = new HashSet<String>();
		
		Map<String,String> map = new HashMap<String,String>();
		map.put( "bp", "http://www.ball-project.org/mimetypes");
		  
		Dom4jXPath xpath = new Dom4jXPath( "//bp:mimetype");
		xpath.setNamespaceContext( new SimpleNamespaceContext(map));

		List<Node> nodes = xpath.selectNodes(doc);
		for(Node node: nodes)
		{
			Element elem    = (Element) node;
			
			String  name    = elem.valueOf("@name");
			String  ext     = elem.valueOf("@ext");
			String  descr   = elem.valueOf("@description");
			
			logger.info("read mime type "+name);
			
			if(mimetypes.contains(name))
			{
				warn("skipping duplicate mime type "+name);
			}
			
			//createMimeTypeLoader(name, ext);
			
			createMimeCell(name, ext);
			createMimeValue(name);
		
			ext2type.put(ext,name);
			
			String s = tpl.replace("__EXT__", ext.toLowerCase());
			s = s.replace("__NAME__",name); 
			data += s;
		}
		
		tf.replace("__DATA__", data);
		tf.replace("__BASE__", _pluginpackage_);
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
			{
				logger.info("start processing node "+f);
				processNode(filename, f);
			}
		}
	}
	
	private static Set<String> node_names = new HashSet<String>();
	
	public static boolean checkNodeName(String name)
	{
		if(!name.matches("[[A-Z]|[a-z]][[0-9]|[A-Z]|[a-z]]+"))
			return false;
		return true;
	}
	
	public static String fixNodeName(String name)
	{
		logger.info("trying to fix node class name "+name);
		name = name.replace(".", "");
		name = name.replace("-", "");
		name = name.replace("_", "");
		name = name.replace("#", "");
		name = name.replace("+", "");
		name = name.replace("$", "");
		name = name.replace(":", "");
		logger.info("fixed node name "+name);
		return name;
	}

	public static String combine (String path1, String path2)
	{
	    File file1 = new File(path1);
	    File file2 = new File(file1, path2);
	    return file2.getPath();
	}
	
	public static void processNode(String name, File descriptor) throws Exception
	{
		
		logger.info("## processing Node "+name);
		
		TTDNodeConfigurationReader reader = new TTDNodeConfigurationReader();
		try
		{
			config = reader.read(new FileInputStream(descriptor));
		}
		catch(Exception e)
		{
			panic(e.getMessage());
		}
		
		String nodeName = config.getName();
		
		String oldNodeName = null;
		
		if(!checkNodeName(nodeName))
		{
			oldNodeName = nodeName;
			
			// we try to fix the nodename
			nodeName = fixNodeName(nodeName);
			
			if(!checkNodeName(nodeName))
				panic("NodeName with invalid name detected "+nodeName);
			
		}
		
		if(oldNodeName==null)
		{
			if(node_names.contains(nodeName))
			{
				warn("duplicate tool detected "+nodeName);
				return;
			}
			node_names.add(nodeName);
			
		}
		else
		{
			if(node_names.contains(oldNodeName))
			{
				warn("duplicate tool detected "+oldNodeName);
				return;
			}
			node_names.add(oldNodeName);
		}
		
		cur_cat  = combine("/"+_package_root_+"/"+_pluginname_,config.getCategory());
		
		cur_path = getPathPrefix(cur_cat); 
		
		
		File nodeConfigDir = new File(_absnodedir_ + "/" + nodeName + "/config");
		nodeConfigDir.mkdirs();

		copyFile(descriptor, new File(_absnodedir_ + "/" + nodeName + "/config/config.xml"));
		
		registerPath(cur_cat);
		
		createFactory(nodeName);
		
		createDialog(nodeName);
		
		createView(nodeName);
		
		createModel(nodeName);
		
		fillMimeTypes();
		
		createXMLDescriptor(nodeName);
		
		writeModel(nodeName);
		
		registerNode( _pluginpackage_ + ".knime.nodes." + nodeName + "." + nodeName + "NodeFactory", cur_cat);
		
	}	
	
	/**
	 * returns the prefix path of the given path.
	 * 
	 * /foo/bar/baz   ---> /foo/bar/
	 * 
	 * @param path
	 * @return
	 */
	public static String getPathPrefix(String path)
	{
		File pth = new File(path);
		return pth.getParent();
	}
	
	/**
	 * returns all prefix paths of a given path.
	 * 
	 * /foo/bar/baz --> [/foo/bar/,/foo/,/]
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> getPathPrefixes(String path)
	{
		List<String> ret = new ArrayList<String>();
		File pth = new File(path);
		ret.add(path);
		while(pth.getParent()!=null)
		{
			ret.add(pth.getParent());
			pth = pth.getParentFile();
		}
		return ret;
	}
	
	/**
	 * returns the path suffix for a given path.
	 * 
	 * /foo/bar/baz --> baz
	 * 
	 * @param path
	 * @return
	 */
	public static String getPathSuffix(String path)
	{
		File pth = new File(path);
		return pth.getName();
	}
	
	private static void createMimeCell(String name, String ext) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/MIMEFileCell.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NAME__", name);
		tf.replace("__EXT__", ext);
		tf.replace("__BASE__", _pluginpackage_);
		tf.write(_absnodedir_ + "/mimetypes/" + name + "FileCell.java");
	}

	private static void createMimeValue(String ext) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/MIMEFileValue.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NAME__", ext);
		tf.replace("__EXT__", ext);
		tf.replace("__BASE__", _pluginpackage_);
		tf.write(_absnodedir_ + "/mimetypes/" + ext + "FileValue.java");
	}

	public static Set<String> categories = new HashSet<String>();
	
	
	public static void registerPath(String path)
	{
		List<String> prefixes = getPathPrefixes(path);
		
		for(String prefix: prefixes)
		{
			registerPathPrefix(prefix);
		}
	}
	
	public static void registerPathPrefix(String path)
	{
		// do not register any top level or root path
		if(path.equals("/")||new File(path).getParent().equals("/"))
			return;
		
		if(categories.contains(path))
			return;
		
		logger.info("registering path prefix " + path);
		
		categories.add(path);
		
		String   cat_name    = getPathSuffix(path);
		String   path_prefix = getPathPrefix(path);
		
		Node node = plugindoc.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.categories']");
		
		Element elem = (Element) node;
		logger.info("name="+cat_name);
		
		elem.addElement("category").addAttribute("description", path).addAttribute("icon", "icons/category.png")
			.addAttribute("path", path_prefix).addAttribute("name", cat_name).addAttribute("level-id", cat_name);
	}
	
	public static void createFactory(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeFactory.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", _pluginpackage_);
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeFactory.java");
	}

	public static void createDialog(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeDialog.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__NODENAME__", nodeName);
		tf.replace("__BASE__", _pluginpackage_);
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
		if(!config.getDocUrl().equals(""))
		{
			String ahref = "<a href=\""+config.getDocUrl()+"\">Web Documentation for "+nodeName+"</a>";
			tf.replace("__DOCLINK__", ahref);	
		}
		else
		{
			tf.replace("__DOCLINK__", "");
		}
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeFactory.xml");

	}

	private static String prettyPrint(String manual)
	{
		if(manual.equals(""))
			return "";
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
		tf.replace("__BASE__", _pluginpackage_);
		tf.write(_absnodedir_ + "/" + nodeName + "/" + nodeName + "NodeView.java");
	}

	private static TemplateFiller curmodel_tf = null;
	
	public static void createModel(String nodeName) throws IOException
	{
		InputStream template = NodeGenerator.class.getResourceAsStream("templates/NodeModel.template");
		curmodel_tf = new TemplateFiller();

		curmodel_tf.read(template);
		curmodel_tf.replace("__NODENAME__", nodeName);
		curmodel_tf.replace("__BASE__", _pluginpackage_);
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
					panic("unknown mime type : |"+type.getExt()+"|");
				}
				tmp += "DataType.getType(" + ext + "FileCell.class),";
			}
			tmp = tmp.substring(0,tmp.length()-1);
			tmp+="},";
			clazzez += tmp;
		}
		
		if(!clazzez.equals(""))
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
					panic("unknown mime type : |"+type.getExt()+"|");
				}
				tmp += "DataType.getType(" + ext + "FileCell.class),";
			}
			tmp = tmp.substring(0,tmp.length()-1);
			tmp+="},";
			clazzez += tmp;
		}
	
		if(!clazzez.equals(""))
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
	
	public static void registerNode(String clazz, String path)
	{
		logger.info("registering Node " + clazz);
		registerPath(path);
		
		Node    node = plugindoc.selectSingleNode("/plugin/extension[@point='org.knime.workbench.repository.nodes']");
		Element elem = (Element) node;


		elem.addElement("node").addAttribute("factory-class", clazz).addAttribute("id", clazz).addAttribute("category-path", path);
	}
	
	public static void post() throws IOException
	{	
		
		//registerNode(_pluginpackage_ + ".knime.nodes.io.MimeFileExporterNodeFactory",combine("/"+_package_root_,"/"+_pluginname_+"/IO"));
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		
		XMLWriter writer = new XMLWriter( new FileWriter(_destdir_ + "/plugin.xml") , format );
        writer.write( plugindoc );

		writer.close();
		
		// prepare binary resources
		InputStream template = TemplateResources.class.getResourceAsStream("BinaryResources.template");
		curmodel_tf = new TemplateFiller();

		curmodel_tf.read(template);
		curmodel_tf.replace("__BASE__", _pluginpackage_);
		curmodel_tf.replace("__BINPACKNAME__", _BINPACKNAME_);
		curmodel_tf.write(_absnodedir_ + "/binres/BinaryResources.java");
		template.close();
		
		String pathsep = System.getProperty("file.separator");
		
		//
		String[] binFiles =  new File(_payloaddir_).list();
		for(String filename: binFiles)
		{
			// do not copy directories
			if(new File(_payloaddir_+pathsep+filename).isDirectory())
				continue;
			
			// only copy zip and ini files
			if(filename.toLowerCase().endsWith("zip"))
			{
				copyFile(new File(_payloaddir_+pathsep+filename),new File(_absnodedir_ +pathsep+"binres"+pathsep+filename));
				verifyZip(_absnodedir_ +pathsep+"binres"+pathsep+filename);
			}
			if(filename.toLowerCase().endsWith("ini"))
			{
				copyFile(new File(_payloaddir_+pathsep+filename),new File(_absnodedir_ +pathsep+"binres"+pathsep+filename));
			}
		}
		
		template = TemplateResources.class.getResourceAsStream("PluginActivator.template");
		TemplateFiller tf = new TemplateFiller();
		tf.read(template);
		tf.replace("__BASE__", _pluginpackage_);
		tf.write(_abspackagedir_ + "/knime/PluginActivator.java");
		template.close();
	}
	
	public static void verifyZip(String filename)
	{
		boolean ok = false;
		
		Set<String> found_exes = new HashSet<String>();
		
		try
		{
			ZipInputStream zin = new ZipInputStream(new FileInputStream(filename));
			ZipEntry       ze  = null;

			while ((ze = zin.getNextEntry()) != null)
			{
				if (ze.isDirectory())
				{
					// we need a bin directory at the top level
					if(ze.getName().equals("bin/") || ze.getName().equals("bin"))
					{
						ok = true;
					}
					
				}
				else
				{
					File f = new File(ze.getName());
					if((f.getParent()!=null)&&f.getParent().equals("bin"))
					{
						found_exes.add(f.getName());
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!ok)
		{
			panic("binary archive has no toplevel bin directory : "+filename);
		}
		
		for(String nodename: node_names)
		{
			boolean found = false;
			if(found_exes.contains(nodename)||found_exes.contains(nodename+".bin")||found_exes.contains(nodename+".exe"))
			{
				found = true;
			}
			if(!found)
			{
				panic("binary archive has no executable in bin directory for node : "+nodename);
			}
		}
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
	
	public static void panic(String message)
	{
		logger.severe("PANIC - "+message+" - EXITING");
		System.exit(1);
	}
	
	public static void warn(String message)
	{
		logger.warning(message);
	}

}
