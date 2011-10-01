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

package org.ballproject.knime.base.node;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.config.DefaultNodeConfigurationStore;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.config.CTDNodeConfigurationWriter;
import org.ballproject.knime.base.config.NodeConfigurationStore;
import org.ballproject.knime.base.mime.MIMEFileCell;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.ListParameter;
import org.ballproject.knime.base.parameter.FileListParameter;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.base.util.ToolRunner.AsyncToolRunner;
import org.ballproject.knime.base.wrapper.GenericToolWrapper;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;

/**
 * The GenericKnimeNodeModel is the base class for all derived classes within the GenericKnimeNodes system.
 * 
 * The base class is configured using a {@link NodeConfiguration} object, holding information about:
 * <ul>
 *  <li>number of input and output ports</li>
 *  <li> {@link MIMEtype}s of these ports</li>
 * </ul> 
 * 
 * @author
 */
public abstract class GenericKnimeNodeModel extends NodeModel
{
	private static final NodeLogger logger = NodeLogger.getLogger(GenericKnimeNodeModel.class);

	protected DataType[][] inports;
	protected DataType[][] outports;
	protected int[]        selected_output_type;
	protected String       binpath;
	
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
		init();
	}
	
	protected void init()
	{
		// init with [0,0,....,0]
		selected_output_type = new int[this.config.getNumberOfOutputPorts()];
	}

	protected MIMEtype getOutputType(int idx)
	{
		return this.config.getOutputPorts()[idx].getMimeTypes().get(selected_output_type[idx]);
	}
	
	protected int getOutputTypeIndex(int idx)
	{
		return selected_output_type[idx];
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
		String nodeName = config.getName();
			
		// create job directory
		File   jobdir = new File( Helper.getTemporaryDirectory(nodeName,!GenericNodesPlugin.isDebug()) );
		GenericNodesPlugin.log("jobdir="+jobdir);
		
		store = new DefaultNodeConfigurationStore();
		
		// prepare input and parameter data
		List<List<String>> output_files = outputParameters(jobdir, inData);
		
		// launch executable
		preExecute(jobdir, exec);
				
		// process result files
		BufferedDataTable[] outtables = processOutput( output_files, exec);
		
        if(!GenericNodesPlugin.isDebug())
        	Helper.deleteDirectory(jobdir);
        
		return outtables;
	}

	protected NodeConfigurationStore store = new DefaultNodeConfigurationStore();
	
	private List<List<String>> outputParameters(File jobdir, BufferedDataTable[] inData) throws IOException
	{	
		// .. input files
		for(int i=0;i<inData.length;i++)
		{
			// skip optional and unconnected inport ports
			if(inData[i]==null)
				continue;
			
			String   name = config.getInputPorts()[i].getName();
			DataRow  row  = inData[i].iterator().next();
			
			// MIMEFileCells are always stored in first column
			DataCell cell = row.getCell(0);
			
			List<MIMEFileCell> mfcs = new ArrayList<MIMEFileCell>();
			
			if(cell.getType().isCollectionType())
			{
				ListCell cells = (ListCell) cell;
				for(int j=0;j<cells.size();j++)
				{
					MIMEFileCell     mfc = (MIMEFileCell) cells.get(j);
					mfcs.add(mfc);
				}
			}
			else
			{
				MIMEFileCell     mfc = (MIMEFileCell) cell;
				mfcs.add(mfc);
			}
			
			for(MIMEFileCell mfc : mfcs)
			{
				File   tmpfile  = mfc.writeTemp(jobdir.getAbsolutePath());
				String filename = tmpfile.getAbsolutePath();
				GenericNodesPlugin.log("< setting param "+name+"->"+filename);
				//writer.setParameterValue(name, filename);
				store.setParameterValue(name, filename);
			}
		}
		
		List<List<String>> outfiles = new ArrayList<List<String>>();
		
		Map<Port,Integer>  port2slot   = new HashMap<Port,Integer>();
		
		// .. output files		
		int nOut = config.getOutputPorts().length; 
		for(int i=0;i<nOut;i++)
		{
			Port   port = config.getOutputPorts()[i];
			String name = port.getName();
			
			String ext  = this.getOutputType(i).getExt();
			
			if(port.isMultiFile())
			{
				// keep this list empty for now ...
				List<String> files = new ArrayList<String>();
				outfiles.add(files);
				// but store the slot index for later filling
				port2slot.put( port, i);
			}
			else
			{
				List<String> files = new ArrayList<String>();
				String filename = Helper.getTemporaryFilename(jobdir.getAbsolutePath(), ext, !GenericNodesPlugin.isDebug());
				GenericNodesPlugin.log("> setting param "+name+"->"+filename);
				//writer.setParameterValue(name, filename);
				store.setParameterValue(name, filename);
				files.add(filename);
				outfiles.add(files);
			}
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
			if(param instanceof ListParameter)
			{
				ListParameter lp = (ListParameter) param;
				if(param instanceof FileListParameter)
				{
					FileListParameter flp = (FileListParameter) param;
					List<String> files = lp.getStrings();
					
					int slot = port2slot.get(flp.getPort()); 
					
					String ext  = this.getOutputType(slot).getExt();
					
					for(String file: files)
					{
						String filename = jobdir.getAbsolutePath()+File.separator+file+"."+ext;
						outfiles.get(slot).add(filename);
						//writer.setMultiParameterValue(key, filename);
						store.setMultiParameterValue(key, filename);
					}
				}
				else
				{
					for(String val: lp.getStrings())
					{
						GenericNodesPlugin.log("@@ setting param "+key+"->"+val);
						//writer.setMultiParameterValue(key, val);	
						store.setMultiParameterValue(key, val);
					}	
				}
			}
			else
			{
				GenericNodesPlugin.log("@ setting param "+key+"->"+param.getValue().toString());
				//writer.setParameterValue(key, param.getValue().toString());
				store.setParameterValue(key, param.getValue().toString());
			}
		}
		
		return outfiles;
	}

	private void preExecute(final File jobdir, final ExecutionContext exec) throws Exception
	{
		// this switch is not nice, we should encapsulate this into
		// a NodeExecutor in the next release
		if(config.getStatus().equals("internal"))
		{
			// fill params.xml
			CTDNodeConfigurationWriter writer = new CTDNodeConfigurationWriter(config.getXML());
			writer.init(store);
			if(this.props.getProperty("use_ini").equals("true"))
				writer.writeINI(jobdir+FILESEP+"params.xml");
			else
				writer.write(jobdir+FILESEP+"params.xml");	
			execute(jobdir, exec);
		}
		else
		{
			executeExternal(jobdir, exec);
		}
	}
	
	private void executeExternal(final File jobdir, final ExecutionContext exec) throws Exception
	{
		String exepath = config.getCommand();
		GenericNodesPlugin.log("executing "+exepath);
		
		GenericToolWrapper wrapper = new GenericToolWrapper(config, store);
		
		
		AsyncToolRunner     t      = new AsyncToolRunner(exepath,wrapper.getSwitchesList());
		//t.getToolRunner().setJobDir(jobdir.getAbsolutePath());
		/*
		for(String key: env.keySet())
		{
			t.getToolRunner().addEnvironmentEntry(key, binpath+FILESEP+env.get(key));
			GenericNodesPlugin.log(key+"->"+binpath+FILESEP+env.get(key));
		}
		*/
		FutureTask<Integer> future = new FutureTask<Integer>(t);
			
		ExecutorService     executor = Executors.newFixedThreadPool(1);
		executor.execute(future);
		
		while (!future.isDone())
        {
            try
            {
                Thread.sleep(5000);
            } 
            catch (InterruptedException ie)
            {
            }
            
            try
            {
            	exec.checkCanceled();	
            }
            catch(CanceledExecutionException e)
            {
            	t.kill();
            	executor.shutdown();
            	throw e;
            }
        }
		
		int retcode = -1;
        try
        {
        	retcode = future.get();
        } 
        catch (ExecutionException ex)
        {
        	ex.printStackTrace();
        }
        
        executor.shutdown();
				
		output = t.getToolRunner().getOutput();
		
		GenericNodesPlugin.log(output);
		GenericNodesPlugin.log("retcode="+retcode);
		
		if(retcode!=0)
	    {
	    	logger.error(output);
	    	throw new Exception("execution of external tool failed");
	    }
	}
	
	private void execute(final File jobdir, final ExecutionContext exec) throws Exception
	{
		String nodeName = config.getName();
		
		// get executable name
		String exepath = Helper.getExecutableName(nodeName, binpath+FILESEP+"bin");
		
		if(exepath==null)
		{
			throw new Exception("execution of external tool failed: due to missing executable file");
		}
		
		GenericNodesPlugin.log("executing "+exepath);
		
		String cli_switch = props.getProperty("ini_switch","-ini");
		
		GenericNodesPlugin.log(exepath+" "+cli_switch+" params.xml");
		
		AsyncToolRunner     t      = new AsyncToolRunner(exepath,cli_switch,"params.xml");
		t.getToolRunner().setJobDir(jobdir.getAbsolutePath());
		
		for(String key: env.keySet())
		{
			t.getToolRunner().addEnvironmentEntry(key, binpath+FILESEP+env.get(key));
			GenericNodesPlugin.log(key+"->"+binpath+FILESEP+env.get(key));
		}
		
		FutureTask<Integer> future = new FutureTask<Integer>(t);
			
		ExecutorService     executor = Executors.newFixedThreadPool(1);
		executor.execute(future);
		
		while (!future.isDone())
        {
            try
            {
                Thread.sleep(5000);
            } 
            catch (InterruptedException ie)
            {
            }
            
            try
            {
            	exec.checkCanceled();	
            }
            catch(CanceledExecutionException e)
            {
            	t.kill();
            	executor.shutdown();
            	throw e;
            }
        }
		
		int retcode = -1;
        try
        {
        	retcode = future.get();
        } 
        catch (ExecutionException ex)
        {
        	ex.printStackTrace();
        }
        
        executor.shutdown();
				
		output = t.getToolRunner().getOutput();
		
		GenericNodesPlugin.log(output);
		GenericNodesPlugin.log("retcode="+retcode);
		
		if(retcode!=0)
	    {
	    	logger.error(output);
	    	throw new Exception("execution of external tool failed");
	    }
		
	}

	protected MIMEtypeRegistry resolver = GenericNodesPlugin.getMIMEtypeRegistry();
	
	private BufferedDataTable[] processOutput(List<List<String>> my_outnames, ExecutionContext exec) throws Exception
	{
		int nOut = config.getOutputPorts().length;
        // create output tables
        BufferedDataTable[] outtables = new BufferedDataTable[nOut];
        for(int i=0;i<nOut;i++)
        {
        	Port port = config.getOutputPorts()[i];
        	BufferedDataContainer container = exec.createDataContainer(outspec[i]);
        	
        	DataCell outcell = null;
        	
        	// multi output file
        	if(my_outnames.get(i).size()>1)
        	{
        		List<MIMEFileCell> files = new ArrayList<MIMEFileCell>();
        		
        		for(String filename: my_outnames.get(i))
        		{
        			File f = new File(filename);
        			MIMEFileCell cell = resolver.getCell(filename);
        			cell.read(f);
        			files.add(cell);
        		}
        			      		
        		outcell = CollectionCellFactory.createListCell(files);
        		
        	}
        	else
        	{
        		String filename = my_outnames.get(i).get(0);
        		File f = new File(filename);

        		outcell = this.makeDataCell(f);
        		
           	}
        	
        	DataRow row = new DefaultRow("Row 0", outcell);
    		container.addRowToTable(row);

    		container.close();

    		BufferedDataTable table = container.getTable();
    		outtables[i] = table;
        }
        
        return outtables;
	}

	private static String FILESEP = File.separator;
	
	
	/**
	 * template method to be overriden by children models; gives
	 * the DataCell from a given file handle (MIMEtype based)
	 */
	public abstract DataCell makeDataCell(File f) throws Exception;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
		/*
		for(Parameter<?> param: config.getParameters())
		{
			param.setValue(null);
		}
		*/
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
		// check compatability of the input types at each port
		// with the list of allowed data types 
		for(int i=0;i<config.getNumberOfInputPorts();i++)
		{
			// no connected input ports have nulls in inSpec
			if(inSpecs[i]==null)
			{
				// .. if port is optional everything is fine
				if(config.getInputPorts()[i].isOptional())
				{
					continue;
				}
				else
					throw new InvalidSettingsException("non-optional input port not connected");
			}
			
			// check compatibility of input types
			boolean ok = false;
			List<MIMEtype> types = config.getInputPorts()[i].getMimeTypes();
			
			for(int j=0;j<types.size();j++)
			{
				// the current type at input port
				DataType input_type    = inSpecs[i].getColumnSpec(0).getType();
				// a possible input type
				DataType expected_type = inports[i][j];				
				// we have found a compatible type in the list of allowed types
				if(input_type.equals(expected_type))
					ok = true;
			}
			
			// we could not find a compatible type in the list of allowed types
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
			
			out_colspec[0] =  new DataColumnSpecCreator(out_ports[i].getName(), this.outports[i][getOutputTypeIndex(i)]).createSpec();
			
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
		GenericNodesPlugin.log("## saveSettingsTo");
		/*
		for(String key: this.config.getParameterKeys())
		{
			GenericNodesPlugin.log(key+" -> "+this.config.getParameter(key).getStringRep());
		}
		GenericNodesPlugin.log("####");
		*/
		for(String key: this.config.getParameterKeys())
		{
			settings.addString(key, this.config.getParameter(key).getStringRep());
		}
		/*
		for(Parameter<?> param: this.config.getParameters())
		{
			//GenericNodesPlugin.log(param.getKey()+" -> "+param.getStringRep());
			settings.addString(param.getKey(), param.getStringRep());
		}
		*/
		for(int i=0;i<this.config.getNumberOfOutputPorts();i++)
		{
			settings.addInt("GENERIC_KNIME_NODES_outtype#"+i,this.getOutputTypeIndex(i));
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
		
		GenericNodesPlugin.log("## loadValidatedSettingsFrom");
		for(String key: this.config.getParameterKeys())
		{
			String value = settings.getString(key);
			try
			{
				this.config.getParameter(key).fillFromString(value);
			}
			catch (InvalidParameterValueException e)
			{
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<this.config.getNumberOfOutputPorts();i++)
		{
			int idx = settings.getInt("GENERIC_KNIME_NODES_outtype#"+i);
			this.selected_output_type[i] = idx; 
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

		
		GenericNodesPlugin.log("## validateSettings ");
		for(String key: this.config.getParameterKeys())
		{
			Parameter<?> param = config.getParameter(key);
			if(!param.getIsOptional())
			{
				if(!settings.containsKey(key))
				{
					GenericNodesPlugin.log("\t no key found for mand. parameter "+key);
					throw new InvalidSettingsException("no value for mandatory parameter "+key+" supplied");
				}
				if(settings.getString(key)==null)
				{
					GenericNodesPlugin.log("\t null value found for mand. parameter "+key);
					throw new InvalidSettingsException("no value for mandatory parameter "+key+" supplied");
				}
			}
			
			String value = settings.getString(key);
			try
			{
				param.fillFromString(value);
			}
			catch (InvalidParameterValueException e)
			{
				GenericNodesPlugin.log("\t invalid value for parameter "+key);
				throw new InvalidSettingsException("invalid value for parameter "+key);
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
	        
	        // store internal Node Configuration
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
