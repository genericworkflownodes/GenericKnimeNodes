package org.ballproject.knime.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class IDBExporter
{

	private static Document document;
	private static Element  root;
	
	public static void main(String[] args) throws FileNotFoundException, Exception
	{
		
		if(args.length!=2)
		{
			System.exit(1);
		}
		
		
		Namespace idbns  = new Namespace("idb","http://www.fz-juelich.de/unicore/xnjs/idb");
		Namespace jsdlns = new Namespace("jsdl","http://schemas.ggf.org/jsdl/2005/11/jsdl-posix");

		document = DocumentHelper.createDocument();
		root     = document.addElement( "root" );

		document.getRootElement().add(idbns);
		document.getRootElement().add(jsdlns);

		File in = new File(args[0]);
		
		// if input path is directory ..
		if(in.isDirectory())
		{
			// iterate over all ..
			File[] files = in.listFiles();
			
			for(File file: files)
			{
				if(!file.getAbsolutePath().endsWith(".ctd"))
					continue;
				// CTD files a process those
				processCTD(file.getAbsolutePath());
			}
		}
		else
		{
			// process single CTD file
			processCTD(in.getAbsolutePath());
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter( new FileWriter( args [1] ), format );
		writer.write( document );
		writer.close();
	}

	public static void processCTD(String filename) throws FileNotFoundException, Exception
	{
		NodeConfiguration config = null;
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		config = reader.read(new FileInputStream(filename));
		
		Element root_ = root.addElement( "idb:IDBApplication" );
		root_.addElement("idb:ApplicationName" ).addText(config.getName());
		root_.addElement("idb:ApplicationVersion").addText(config.getVersion());

		Element appl = root_.addElement("jsdl:POSIXApplication");
		appl.addElement("jsdl:Executable").addText("$SUITEROOT/"+config.getName());

		for(Parameter<?> param : config.getParameters())
		{
			appl.addElement("jsdl:Argument").addText(String.format("-%s $%s?",param.getKey(),param.getKey()));
		}
		
		for(Port port: config.getInputPorts())
		{
			String[] toks = port.getName().split("\\.");
			String   name = toks[toks.length-1]; 
			appl.addElement("jsdl:Argument").addText(String.format("-%s $%s?",name,name));
		}
		
		for(Port port: config.getOutputPorts())
		{
			String[] toks = port.getName().split("\\.");
			String   name = toks[toks.length-1]; 
			appl.addElement("jsdl:Argument").addText(String.format("-%s $%s?",name,name));
		}
	}

}

