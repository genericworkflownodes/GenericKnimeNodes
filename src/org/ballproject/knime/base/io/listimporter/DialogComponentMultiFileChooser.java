package org.ballproject.knime.base.io.listimporter;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
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
    	MIMEtype[] mts      = new MIMEtype[files.length];
    	
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
    	
    	MIMEtype first = mts[0];
    	for(int i=1;i<mts.length;i++)
    	{
    		if(!MIMEtype.equals(first, mts[i]))
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
