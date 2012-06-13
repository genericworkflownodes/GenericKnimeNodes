/**
 * 
 */
package org.ballproject.knime.base.flow.listzip;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * @author roettig
 * 
 */
public class ListZipLoopEndNodeFactory extends
		NodeFactory<ListZipLoopEndNodeModel> {

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return null;
	}

	@Override
	public ListZipLoopEndNodeModel createNodeModel() {
		return new ListZipLoopEndNodeModel();
	}

	@Override
	public NodeView<ListZipLoopEndNodeModel> createNodeView(int arg0,
			ListZipLoopEndNodeModel arg1) {
		return null;
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	protected boolean hasDialog() {
		return false;
	}

}
