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

package org.ballproject.knime.base.io.listimporter;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.knime.core.data.url.MIMEType;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObjectSpec;

public class DialogComponentMultiFileChooser extends DialogComponent
{
	private JFileChooser chooser;
	private JButton      m_browseButton;
	private File[] 		 files;
	
	public DialogComponentMultiFileChooser(SettingsModelStringArray model)
	{
		super(model);
		
		chooser = new JFileChooser();

		// enable multiple selections
		chooser.setMultiSelectionEnabled(true);
		
		getComponentPanel().setLayout(new FlowLayout());
		m_browseButton = new JButton("Browse...");
		
        final JPanel p = new JPanel();
        p.add(m_browseButton);
        
        getComponentPanel().add(p);
        
        m_browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				final int returnVal = chooser.showDialog(getComponentPanel().getParent(),null);
				if (returnVal == JFileChooser.APPROVE_OPTION) 
				{
					files = chooser.getSelectedFiles();
				}
			}
			}
        );
	}
	
	protected MIMEtypeRegistry resolver = GenericNodesPlugin.getMIMEtypeRegistry();
	
	/**
     * Transfers the value from the component into the settings model.
     *
     * @param noColoring if set true, the component will not be marked red, even
     *            if the entered value was erroneous.
     * @throws InvalidSettingsException if the entered filename is null or
     *             empty.
     */
    private void updateModel() throws InvalidSettingsException 
    {
    	/*
        final String file = m_fileComboBox.getEditor().getItem().toString();
        if ((file != null) && (file.trim().length() > 0)) {

            try {
                ((SettingsModelString)getModel()).setStringValue(file);
            } catch (final RuntimeException e) {
                // if value was not accepted by setter method
                if (!noColoring) {
                    showError(m_fileComboBox);
                }
                throw new InvalidSettingsException(e);
            }

        } else {
            if (!noColoring) {
                showError(m_fileComboBox);
            }
            throw new InvalidSettingsException("Please specify a filename.");
        }
        */
    	String[]  filenames = new String[files.length];
    	MIMEType[] mts      = new MIMEType[files.length];
    	
    	int idx = 0;
    	
    	for(File file: files)
    	{
    		String filename = file.getAbsolutePath(); 
    		filenames[idx]  = filename; 
    		mts[idx]       = resolver.getMIMEtype(filename);
    		if(mts[idx]==null)
    			throw new InvalidSettingsException("file of unknown MIMEtype selected "+filename);
    		idx++;
    	}
    	
    	MIMEType first = mts[0];
    	for(int i=1;i<mts.length;i++)
    	{
    		if(!first.equals(mts[i]))
    			throw new InvalidSettingsException("mixed set of MIMEtype files selected");
    	}
    	
    	((SettingsModelStringArray)getModel()).setStringArrayValue(filenames);
    }

	@Override
	protected void checkConfigurabilityBeforeLoad(PortObjectSpec[] arg0) throws NotConfigurableException
	{
		// we're always good - independent of the incoming spec
	}

	@Override
	protected void setEnabledComponents(boolean flag)
	{
		chooser.setEnabled(flag);
	}

	@Override
	public void setToolTipText(String tt)
	{
		chooser.setToolTipText(tt);
	}

	@Override
	protected void updateComponent()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void validateSettingsBeforeSave() throws InvalidSettingsException
	{
		updateModel();
	}

}
