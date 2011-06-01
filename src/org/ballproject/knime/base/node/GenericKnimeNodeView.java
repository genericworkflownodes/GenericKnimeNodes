package org.ballproject.knime.base.node;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "GenericKnimeNode" Node.
 * 
 * 
 * @author
 */
public class GenericKnimeNodeView extends NodeView<GenericKnimeNodeModel>
{

	/**
	 * Creates a new view.
	 * 
	 * @param nodeModel
	 *            The model (class: {@link GenericKnimeNodeModel})
	 */
	protected GenericKnimeNodeView(final GenericKnimeNodeModel nodeModel)
	{
		super(nodeModel);

		JTextArea text = new JTextArea(nodeModel.output, 40, 80);
		JScrollPane scrollpane = new JScrollPane(text);
		text.setFont(new Font("Monospaced", Font.BOLD, 12));
		setComponent(scrollpane);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged()
	{

		// TODO retrieve the new model from your nodemodel and
		// update the view.
		GenericKnimeNodeModel nodeModel = (GenericKnimeNodeModel) getNodeModel();
		assert nodeModel != null;

		// be aware of a possibly not executed nodeModel! The data you retrieve
		// from your nodemodel could be null, emtpy, or invalid in any kind.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose()
	{

		// TODO things to do when closing the view
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen()
	{

		// TODO things to do when opening the view
	}

}
