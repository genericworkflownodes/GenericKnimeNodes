package com.genericworkflownodes.knime.nodes.io.outputfolder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

/**
 * <code>NodeDialog</code> for the "OutputFolder" Node. Writes all the incoming
 * files to the given output folder.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author The GKN Team
 */
public class OutputFolderNodeDialog extends NodeDialogPane {

	private final JPanel m_dialogPanel;
	private final JPanel m_componentContainer;
	private final JTextField m_textField;
	private final JButton m_searchButton;

	/**
	 * New pane for configuring the OutputFolder node.
	 */
	protected OutputFolderNodeDialog() {
		m_dialogPanel = new JPanel();
		m_componentContainer = new JPanel();
		m_textField = new JTextField();
		m_textField.setPreferredSize(new Dimension(300, m_textField
				.getPreferredSize().height));
		m_searchButton = new JButton("Browse");
		m_searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser jfc = new JFileChooser();
				if (!"".equals(m_textField.getText().trim())
						&& new File(m_textField.getText().trim()).getParent() != null) {
					jfc.setCurrentDirectory(new File(m_textField.getText()
							.trim()).getParentFile());
				}

				jfc.setAcceptAllFileFilterUsed(false);

				// int returnVal = jfc.showSaveDialog(m_dialogPanel);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = jfc.showDialog(m_dialogPanel,
						"Select output folder");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// validate extension
					m_textField
							.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		setLayout();
		addComponents();

		addTab("Choose Output Folder", m_dialogPanel);
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		settings.addString(OutputFolderNodeModel.CFG_FOLDER_NAME, m_textField
				.getText().trim());
	}

	private void setLayout() {
		m_dialogPanel.setLayout(new FlowLayout());
	}

	private void addComponents() {
		m_componentContainer.add(m_textField);
		m_componentContainer.add(m_searchButton);
		m_componentContainer.setBorder(BorderFactory
				.createTitledBorder("Selected output file:"));
		m_dialogPanel.add(m_componentContainer);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			PortObjectSpec[] specs) throws NotConfigurableException {
		// get information from settings and inspec
		m_textField.setText(settings.getString(
				OutputFolderNodeModel.CFG_FOLDER_NAME, ""));
	}

}
