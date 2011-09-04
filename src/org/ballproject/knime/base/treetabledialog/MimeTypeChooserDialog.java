package org.ballproject.knime.base.treetabledialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.base.port.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.jdesktop.swingx.VerticalLayout;

public class MimeTypeChooserDialog extends JPanel implements ActionListener
{
	private NodeConfiguration config;
	
	private JComboBox[] cbs;
	private int[]       sel_ports;
	
	public MimeTypeChooserDialog(NodeConfiguration config)
	{
		this.config = config;
		
		this.setLayout(new VerticalLayout());
		
		int nCB = this.config.getNumberOfOutputPorts();
		
		cbs       = new JComboBox[nCB];
		sel_ports = new int[nCB];
		
		for(int i=0;i<nCB;i++)
		{
			Port port = this.config.getOutputPorts()[i];
			
			this.add(new JLabel(port.getName()));
			
			List<MIMEtype> types = port.getMimeTypes();
			String[] strs = new String[types.size()];
			
			int idx = 0;
			for(MIMEtype type: types)
				strs[idx++] = type.getExt();
			
			JComboBox cb = new JComboBox(strs);
			cbs[i] = cb; 
			this.add(cb);
			cb.addActionListener(this);
		}
	}
	
	public int[] getSelectedTypes()
	{
		return sel_ports;
	}
	
	public void setSelectedTypes(int[] sel_ports)
	{
		this.sel_ports = sel_ports;
		for(int i=0;i<cbs.length;i++)
		{
			cbs[i].setSelectedIndex(sel_ports[i]);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev)
	{
		for(int i=0;i<cbs.length;i++)
		{
			if(ev.getSource()==cbs[i])
			{
				sel_ports[i]=cbs[i].getSelectedIndex();
			}
		}
		
	}
	
	
}
