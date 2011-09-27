/**
 * 
 */
package org.ballproject.knime.base.flow.column2list;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * @author roettig
 *
 */
public class ColumnToListNodeFactory extends NodeFactory<ColumnToListNodeModel>
{

	@Override
	protected NodeDialogPane createNodeDialogPane()
	{
		return null;
	}

	@Override
	public ColumnToListNodeModel createNodeModel()
	{
		return new ColumnToListNodeModel();
	}

	@Override
	public NodeView<ColumnToListNodeModel> createNodeView(int arg0,
			ColumnToListNodeModel arg1)
	{
		return null;
	}

	@Override
	protected int getNrNodeViews()
	{
		return 0;
	}

	@Override
	protected boolean hasDialog()
	{
		return false;
	}


}
