package org.ballproject.knime.base.node;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.config.TTDNodeConfigurationWriter;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.MIMEFileDelegate;
import org.ballproject.knime.base.port.MIMEtype;
import org.ballproject.knime.base.port.MimeMarker;
import org.ballproject.knime.base.port.Port;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;

/**
 * This is the model implementation of GenericKnimeNode.
 * 
 * 
 * @author
 */
public abstract class GenericKnimeNodeModel extends NodeModel
{
	private static final NodeLogger logger = NodeLogger.getLogger(GenericKnimeNodeModel.class);

	protected DataType[][] inports;
	protected DataType[][] outports;
	
	protected String binpath;
	
	public String output="";
	
	/*
	 * stores the node configuration (i.e. parameters, ports, ..) 
	 */
	protected NodeConfiguration config;
	
	/*
	 * stores general properties 
	 */
	protected Properties props;
	
	/*
	 * stores environment variables needed for program execution 
	 */
	protected Map<String,String> env;
	
	/**
	 * Constructor for the node model.
	 */
	protected GenericKnimeNodeModel(NodeConfiguration config)
	{
		super(createOPOs(config.getInputPorts()),createOPOs(config.getOutputPorts()));
		this.config = config;
		this.reset();
	}

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(BufferedDataTable.class, true);

	private static PortType[] createOPOs(Port[] ports)
	{
		PortType[] portTypes = new PortType[ports.length];
	    Arrays.fill(portTypes, BufferedDataTable.TYPE);
	    for(int i=0;i<ports.length;i++)
	    {
	    	if(ports[i].isOptional())
	    	{
	    		portTypes[i] = OPTIONAL_PORT_TYPE;
	    	}
	    }
	    return portTypes;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{
		// fetch node descriptors		
		String tmpdir  = KNIMEConstants.getKNIMETempDir();
		String exename = config.getName();
			
		// create job directory
		File   jobdir = File.createTempFile(exename, "JOBDIR", new File(tmpdir));

		
		
		System.out.println("jobdir="+jobdir);
		
		// this might be risky
		jobdir.delete();
		jobdir.mkdirs();
		
		jobdir.deleteOnExit();
		
		String FILESEP = "/";//System.getProperty("path.separator");
		
		// fill params.xml
		TTDNodeConfigurationWriter writer = new TTDNodeConfigurationWriter(config.getXML());
		
		// .. input files
		int filenum=1;
		for(int i=0;i<inData.length;i++)
		{
			// skip optional and unconnected inport ports
			if(inData[i]==null)
				continue;
			
			String   name = config.getInputPorts()[i].getName();
			DataRow  row  = inData[i].iterator().next();
			DataCell cell = row.getCell(0);
			if( cell instanceof MimeMarker)
			{
				MimeMarker mrk = (MimeMarker) cell;
				MIMEFileDelegate del = mrk.getDelegate();
				del.write(jobdir+FILESEP+filenum+"."+mrk.getExtension());
				System.out.println("< setting param "+name);
				writer.setParameterValue2(name, jobdir+FILESEP+filenum+"."+mrk.getExtension());
				filenum++;
			}
		}
		
		List<String> my_outnames = new ArrayList<String>();
		// .. output files		
		int nOut = config.getOutputPorts().length; 
		for(int i=0;i<nOut;i++)
		{
			String name = config.getOutputPorts()[i].getName();
			// fixme
			String ext  = config.getOutputPorts()[i].getMimeTypes().get(0).getExt();
			System.out.println("> setting param "+name);
			writer.setParameterValue2(name, jobdir+FILESEP+filenum+"."+ext);
			my_outnames.add(jobdir+FILESEP+filenum+"."+ext);
			filenum++;
		}
		
		// .. node parameters
		for(String key: config.getParameterKeys())
		{
			Parameter<?> param = config.getParameter(key);
			if(param.isNull())
			{
				if(param.getIsOptional())
					continue;					
			}
			System.out.println("@ setting param "+key);
			writer.setParameterValue2(key, param.getValue().toString());
		}
		
		writer.write(jobdir+FILESEP+"params.xml");

		
		// get path to executable
		String exepath = binpath+FILESEP+"bin"+FILESEP+exename+".bin";
		
		System.out.println("executing "+exepath);
		
		// build process
		ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c",exepath+" -par params.xml" );
		
		for(String key: env.keySet())
		{
			builder.environment().put(key, binpath+FILESEP+env.get(key));
		}
				
		builder.redirectErrorStream(true);
	    builder.directory( jobdir );
	    
	    // execute
	    Process p = builder.start();
	    
	    
	    // fetch output data (stdio+stderr)
	    InputStreamReader isr = new InputStreamReader(p.getInputStream());
        BufferedReader    br  = new BufferedReader(isr);
        
        String line = null;
        StringBuffer out = new StringBuffer();
        
        while ( (line = br.readLine()) != null)
        {
            out.append(line+System.getProperty("line.separator"));
        }
        
        
        // fetch return code
	    int retcode = p.waitFor();
	    
	    // .. in case of problems
	    if(retcode!=0)
	    {
	    	logger.error(out.toString());
	    	throw new Exception("execution of external tool failed");
	    }
	    
	    out.append("return code="+retcode);
        output = out.toString();

        // create output tables
        BufferedDataTable[] outtables = new BufferedDataTable[nOut];
        for(int i=0;i<nOut;i++)
        {
        	BufferedDataContainer container = exec.createDataContainer(outspec[i]);
        	
        	// fixme
        	//String ext  = config.getOutputPorts()[i].getMimeTypes().get(0).getExt();
        	
        	File f = new File(my_outnames.get(i));
        	
        	DataCell cell = this.makeDataCell(f);
        	DataRow row = new DefaultRow("Row 0", cell);
    		container.addRowToTable(row);
    		
    		container.close();
    		
    		BufferedDataTable table = container.getTable();
    		outtables[i] = table;
        }
		
		return outtables;
	}

	/**
	 * template method to be overriden by children models; gives
	 * the DataCell from a given file handle (MIMEtype based)
	 */
	public abstract DataCell makeDataCell(File f) throws IOException;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
		
		//System.out.println("## reset");
		
		
		for(Parameter<?> param: config.getParameters())
		{
			param.setValue(null);
		}
	}

	protected DataTableSpec[] outspec;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException
	{
		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message
				
		checkInput(inSpecs);

		outspec = createOutSpec();
		
		return outspec;
	}
	
	
	protected void checkInput(final DataTableSpec[] inSpecs) throws InvalidSettingsException
	{
		for(int i=0;i<config.getNumberOfInputPorts();i++)
		{
			// no connected input ports have nulls in inSpec
			if(inSpecs[i]==null)
			{
				if(config.getInputPorts()[i].isOptional())
				{
					continue;
				}
				else
					throw new InvalidSettingsException("non-optional input port not connected");
			}
			
			
			boolean ok = false;
			List<MIMEtype> types = config.getInputPorts()[i].getMimeTypes();
			
			for(int j=0;j<types.size();j++)
			{
				DataType in_type  = inSpecs[i].getColumnSpec(0).getType();
				DataType exp_type = inports[i][j];
				if(in_type.equals(exp_type))
					ok = true;
			}
			
			if(!ok)
				throw new InvalidSettingsException("invalid MIMEtype at port number "+i);			
		}
	}
	
	protected DataTableSpec[] createOutSpec()
	{
		Port[]           out_ports  = config.getOutputPorts();
		int              nOutPorts  = out_ports.length;
		DataTableSpec[]  out_spec   = new DataTableSpec[nOutPorts];
	        
		for(int i=0;i<nOutPorts;i++)
		{
			DataColumnSpec[] out_colspec = new DataColumnSpec[1];
			out_colspec[0] =  new DataColumnSpecCreator(out_ports[i].getName(), this.outports[i][0]).createSpec();
			DataTableSpec outputSpec = new DataTableSpec(out_colspec);
			out_spec[i] = outputSpec;
		}
		return out_spec;
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
	{
		//System.out.println("## saveSettingsTo");
		for(Parameter<?> param: config.getParameters())
		{
			settings.addString(param.getKey(), param.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		
		// - we know that values are validated and thus are valid
		// - we xfer the values into the corresponding model objects
		
		//System.out.println("## loadValidatedSettingsFrom");
		
		for(Parameter<?> param: config.getParameters())
		{
			String value = settings.getString(param.getKey());
			try
			{
				param.fillFromString(value);
			}
			catch (InvalidParameterValueException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		// - we validate incoming settings values here
		// - we do not xfer values to member variables
		// - we throw an exception if something is invalid
		
		//System.out.println("## validateSettings ");
		
		for(Parameter<?> param: config.getParameters())
		{
			if(!param.getIsOptional())
			{
				if(!settings.containsKey(param.getKey()))
				{
					//System.out.println("\t no key found for mand. param");
					throw new InvalidSettingsException("no value for mandatory parameter "+param.getKey()+" supplied");
				}
				if(settings.getString(param.getKey())==null)
				{
					//System.out.println("\t null value found for mand. param");
					throw new InvalidSettingsException("no value for mandatory parameter "+param.getKey()+" supplied");
				}
			}
			
			String value = settings.getString(param.getKey());
			try
			{
				param.fillFromString(value);
			}
			catch (InvalidParameterValueException e)
			{
				throw new InvalidSettingsException("invalid value for parameter "+param.getKey());
			}
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
		ZipFile zip = new ZipFile(new File(internDir,"loadeddata"));
		
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

		int    BUFFSIZE = 2048;
		byte[] BUFFER   = new byte[BUFFSIZE];
		
	    while(entries.hasMoreElements()) 
	    {
	        ZipEntry entry = (ZipEntry)entries.nextElement();
	        if(entry.getName().equals("rawdata.bin"))
	        {
	        	int  size   = (int) entry.getSize(); 
	        	byte[] data = new byte[size];
	        	InputStream in = zip.getInputStream(entry);
	        	int len;
	        	int totlen=0;
	        	while( (len=in.read(BUFFER, 0, BUFFSIZE))>=0 )
	        	{
	        		System.arraycopy(BUFFER, 0, data, totlen, len);
	        		totlen+=len;
	        	}
	        	output = new String(data);
	        }
	        if(entry.getName().equals("config.bin"))
	        {
	        	InputStream       in  = zip.getInputStream(entry);
	        	ObjectInputStream in2 = new ObjectInputStream(in);
	        	try
				{
					config = (NodeConfiguration) in2.readObject();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				in2.close();
	        }
	    }
	    zip.close();
	    
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(internDir,"loadeddata")));
		
		ZipEntry entry = new ZipEntry("rawdata.bin");
	    out.putNextEntry(entry);
	    out.write(output.getBytes());

	    ByteArrayOutputStream bos  = new ByteArrayOutputStream() ;
	    ObjectOutput          oout = new ObjectOutputStream(bos) ;
	    oout.writeObject(config);
	    oout.close();

	    // Get the bytes of the serialized object
	    byte[] buf = bos.toByteArray();
	    entry = new ZipEntry("config.bin");
	    out.putNextEntry(entry);
	    out.write(buf);
	    
	    out.close(); 
	}

}
