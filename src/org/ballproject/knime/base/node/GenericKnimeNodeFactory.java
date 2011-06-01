package org.ballproject.knime.base.node;


import org.ballproject.knime.base.config.NodeConfiguration;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "GenericKnimeNode" Node.
 * 
 * 
 * @author
 */
public abstract class GenericKnimeNodeFactory extends NodeFactory<GenericKnimeNodeModel>
{

	protected NodeConfiguration config;
	
	public GenericKnimeNodeFactory() 
	{
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract GenericKnimeNodeModel createNodeModel();
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews()
	{
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<GenericKnimeNodeModel> createNodeView(final int viewIndex, final GenericKnimeNodeModel nodeModel)
	{
		return new GenericKnimeNodeView(nodeModel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract NodeDialogPane createNodeDialogPane();

}
