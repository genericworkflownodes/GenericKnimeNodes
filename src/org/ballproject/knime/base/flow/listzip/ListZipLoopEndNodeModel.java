/**
 * 
 */
package org.ballproject.knime.base.flow.listzip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.flow.columnmerger.ColumnMergerNodeModel;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.container.CloseableRowIterator;
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
import org.knime.core.node.workflow.LoopEndNode;
import org.knime.core.node.workflow.LoopStartNodeTerminator;

/**
 * @author roettig
 *
 */
public class ListZipLoopEndNodeModel extends NodeModel implements LoopEndNode
{
	// the logger instance
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ListZipLoopEndNodeModel.class);
	
	private BufferedDataContainer m_resultContainer;
	private int  m_count = 0;
	private long m_startTime;
	
	protected ListZipLoopEndNodeModel()
	{
		super(1, 1);
	}

	private DataTableSpec outputspec;
	private DataTableSpec inspec;
	
	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException
	{
		DataType intype = inSpecs[0].getColumnSpec(0).getType();
		
		DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
		allColSpecs[0]   =  new DataColumnSpecCreator("column 0",  ListCell.getCollectionType(intype)).createSpec();
		
		outputspec = new DataTableSpec(allColSpecs);
		inspec = inSpecs[0];
		
		return new DataTableSpec[]{outputspec};
	}

	private List<DataCell> cells = new ArrayList<DataCell>();

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception
	{
		if (!(this.getLoopStartNode() instanceof LoopStartNodeTerminator)) 
		{
            throw new IllegalStateException("Loop End is not connected"
                    + " to matching/corresponding Loop Start node. You"
                    + " are trying to create an infinite loop!");
        }
		
		if (m_resultContainer == null) 
		{
	            // first time we are getting to this: open container
	            m_startTime = System.currentTimeMillis();    
	            m_count = 0;
	            m_resultContainer = exec.createDataContainer(inspec);
	    }
		
		BufferedDataTable in = inData[0];
		for (DataRow row : in) 
		{
			//m_resultContainer.addRowToTable(new DefaultRow("Row "+m_count, row));
			cells.add(row.getCell(0));
        }
			
		boolean terminateLoop = ((LoopStartNodeTerminator)this.getLoopStartNode()).terminateLoop();
		
        if (terminateLoop) 
        {
            // this was the last iteration - close container and continue
       		m_resultContainer.close();	
                        
            BufferedDataTable outTable = m_resultContainer.getTable();
            
            
            CloseableRowIterator  iter = outTable.iterator();
            
            List<DataCell> cells = new ArrayList<DataCell>();
            while(iter.hasNext())
            {
            	DataCell dc = iter.next().getCell(0);
            	cells.add(dc);
            }
            iter.close();
            
            BufferedDataContainer cont = exec.createDataContainer(inspec);
            //ListCell lc = CollectionCellFactory.createListCell(cells);
            cont.addRowToTable(new DefaultRow("Row 1", cells.get(0)));
            
            
            cont.close();
            
            m_resultContainer = null;
            m_count = 0;
            LOGGER.debug("Total loop execution time: "
                    + (System.currentTimeMillis() - m_startTime) + "ms");
            m_startTime = 0;
            return new BufferedDataTable[]{cont.getTable()};
        } 
        else 
        {
            continueLoop();
            m_count++;
            return new BufferedDataTable[1];
        }
	}



	/* (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#loadInternals(java.io.File, org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void loadInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#loadValidatedSettingsFrom(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO arg0)
			throws InvalidSettingsException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#reset()
	 */
	@Override
	protected void reset()
	{
		m_count = 0;
		m_resultContainer = null;
	}

	/* (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#saveInternals(java.io.File, org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void saveInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#saveSettingsTo(org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#validateSettings(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void validateSettings(NodeSettingsRO arg0)
			throws InvalidSettingsException
	{
		// TODO Auto-generated method stub

	}

}
