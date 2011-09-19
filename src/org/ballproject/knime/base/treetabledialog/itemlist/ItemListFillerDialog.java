package org.ballproject.knime.base.treetabledialog.itemlist;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;


import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.treetabledialog.UIHelper;

public class ItemListFillerDialog extends JDialog implements ActionListener
{
	private JList   list;
	private JButton addButton;
	private JButton delButton;
	private JButton okButton;
	
	private final ItemListFillerDialogModel model;
	
	private String[] choices;
	
	public ItemListFillerDialog(ItemListFillerDialogModel mdl)
	{
		super();
		
		this.setTitle("List editor");
		
		if(mdl.hasRestrictedValues())
		{
			choices = mdl.getRestrictedValues();
		}
		
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		
		this.model = mdl;
		
		list = new JList(model);
		list.setFixedCellWidth(200);
		JScrollPane listScrollPane = new JScrollPane(list);
		
		UIHelper.addComponent(pane, listScrollPane, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2, 2);
				
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		
		delButton = new JButton("Del");
		delButton.addActionListener(this);

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		
		makeButtonWidthEqual(addButton,delButton,okButton);
		
		Box box = Box.createVerticalBox();
		box.add(addButton);
		box.add(delButton);
		box.add(okButton);
		box.add(Box.createVerticalGlue());
		
		UIHelper.addComponent(pane, box, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,0, 0);		
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(300, 200);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent ev)
	{	
		Object source = ev.getSource();
		if(source==addButton)
		{
			if(model.hasRestrictedValues())
			{
				Object choice = ChoiceDialog.showDialog("Please select ...", choices);
				String text   = choice.toString();
				model.addItem(text);
			}
			else
			{
				String text = JOptionPane.showInputDialog("Enter value ("+model.getValidatorName()+"):");
			
				boolean valid = model.addItem(text);
			
				if(!valid)
					JOptionPane.showMessageDialog(null, "entered value is not a valid "+model.getValidatorName()+". reason: "+model.getValidatorReason());
			}
		}
		if(source==delButton)
		{
			model.removeItems(list.getSelectedIndices());
		}
		if(source==okButton)
		{
			dispose();
		}
	}


	public static void makeButtonWidthEqual(JButton... buttons)
	{
		int maxWidth = Integer.MIN_VALUE;
		
		
		for( JButton button : buttons )
		{
			maxWidth = Math.max( maxWidth, button.getPreferredSize().width );
		}

		for( JButton button : buttons )
		{
			Dimension size = new Dimension( maxWidth, button.getPreferredSize().height );
			button.setPreferredSize( size );
			button.setMinimumSize( size );
			button.setMaximumSize( size );
		}
	}
	
	public static void main(String[] args) 
    {

        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
            	ItemListFillerDialogModel mdl = new ItemListFillerDialogModel(new String[]{"A","B","D"});
            	//mdl.setValidator(new DoubleValidator());
            	mdl.restrictValues("A","B","C","D");
            	mdl.setSetLike(true);
            	
            	ItemListFillerDialog sd = new ItemListFillerDialog(mdl);
            	sd.setVisible(true);
            }
        });
    }

}
