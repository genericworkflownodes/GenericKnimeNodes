/**
 * Copyright (c) 2011-2013, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knime.base.filehandling.mime.MIMEMap;
import org.knime.base.filehandling.mime.MIMETypeEntry;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

/**
 * <code>NodeDialog</code> for the "OutputFile" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author aiche, roettig
 */
public class OutputFileNodeDialog extends NodeDialogPane {

	private final JPanel dialogPanel;
	private final JPanel componentContainer;
	private final JTextField textField;
	private final JButton searchButton;
	private String incomingFileExtension;
	private FileNameExtensionFilter extensionFilter;
	private final String settingsName;

	/**
	 * New pane for configuring MimeFileExporter node dialog.
	 */
	public OutputFileNodeDialog(final String settingsName) {
		this.settingsName = settingsName;
		dialogPanel = new JPanel();
		componentContainer = new JPanel();
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(300, textField
				.getPreferredSize().height));
		searchButton = new JButton("Browse");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser jfc = new JFileChooser();
				if (!"".equals(textField.getText().trim())
						&& new File(textField.getText().trim()).getParent() != null) {
					jfc.setCurrentDirectory(new File(textField.getText().trim())
							.getParentFile());
				}

				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(extensionFilter);

				// int returnVal = jfc.showSaveDialog(dialogPanel);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = jfc.showDialog(dialogPanel,
						"Select output file");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// validate extension
					if (!extensionFilter.accept(jfc.getSelectedFile())) {
						// TODO: show warning
					}
					textField.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		setLayout();
		addComponents();

		addTab("Choose File", dialogPanel);
	}

	private void setLayout() {
		dialogPanel.setLayout(new FlowLayout());
	}

	private void addComponents() {
		componentContainer.add(textField);
		componentContainer.add(searchButton);
		componentContainer.setBorder(BorderFactory
				.createTitledBorder("Select output file"));
		dialogPanel.add(componentContainer);
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		settings.addString(settingsName, textField.getText().trim());
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			PortObjectSpec[] specs) throws NotConfigurableException {
		if (specs[0] == null) {
			throw new NotConfigurableException(
					"Output file type cannot be determined if the node is not connected."
							+ " Please connect the node before opening the configure dialog.");
		}

		// get information from settings and inspec
		textField.setText(settings.getString(settingsName, ""));
		incomingFileExtension = ((URIPortObjectSpec) specs[0])
				.getFileExtensions().get(0);

		// infer the valid extensions for this file type
		createFileExtensionFilter();
	}

	public void createFileExtensionFilter() {
		final String mimeType = MIMEMap.getMIMEType(incomingFileExtension);
		MIMETypeEntry[] entries = MIMEMap.getAllTypes();
		final List<String> extensions = new ArrayList<String>();
		for (MIMETypeEntry entry : entries) {
			if (mimeType.equals(entry.getType())) {
				extensions.addAll(entry.getExtensions());
				break;
			}
		}

		String[] exts = new String[extensions.size()];
		for (int i = 0; i < extensions.size(); ++i) {
			exts[i] = extensions.get(i).trim();
		}
		extensionFilter = new FileNameExtensionFilter(mimeType, exts);
	}
}
