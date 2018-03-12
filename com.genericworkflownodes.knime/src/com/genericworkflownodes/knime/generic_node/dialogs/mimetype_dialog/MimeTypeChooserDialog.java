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

package com.genericworkflownodes.knime.generic_node.dialogs.mimetype_dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.port.Port;

public class MimeTypeChooserDialog extends JPanel implements ActionListener {
    private static final long serialVersionUID = 3102737955888696834L;

    private INodeConfiguration config;

    private JCheckBox[] chbs;
    private JComboBox[] cbsType;
    private JComboBox[] cbsLink;
    private JTextField[] textsBasenames;
    private int[] sel_ports;
    private boolean[] active_ports;
    private int[] linked_inports;
    private String[] custom_basenames;
    
    private static double[] xWeights = {0.07,0.2,0.1,0.1,0.6};

    public MimeTypeChooserDialog(INodeConfiguration config) {
        this.config = config;
        int nCB = this.config.getNumberOfOutputPorts();
        
        // Outer=full panel
        setLayout(new BorderLayout());

        // Inner panel with row for each port
        JPanel portPanel = new JPanel();
        portPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        cbsType = new JComboBox[nCB];
        cbsLink = new JComboBox[nCB];
        chbs = new JCheckBox[nCB];
        textsBasenames = new JTextField[nCB];
        
        //These arrays will be set with the settings from disk
        // when initializing the Dialog. Therefore use these arrays
        // when developing logic here, instead of the (non-final) infos in the Port objects
        // like activeness
        
        active_ports = new boolean[nCB];
        sel_ports = new int[nCB];
        linked_inports = new int[nCB];
        custom_basenames = new String[nCB];
        
        Component lab;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = xWeights[0];
        Font headingFont = new Font("Arial", Font.BOLD, 13);
        lab = new JLabel("Active?");
        lab.setFont(headingFont);
        portPanel.add(lab,c);
        c.weightx = xWeights[1];
        c.anchor = GridBagConstraints.CENTER;
        lab = new JLabel("Outport");
        lab.setFont(headingFont);
        portPanel.add(lab,c);
        c.weightx = xWeights[2];
        c.anchor = GridBagConstraints.WEST;
        lab = new JLabel("Output type");
        lab.setFont(headingFont);
        portPanel.add(lab,c);
        c.weightx = xWeights[3];
        lab = new JLabel("Link to input");
        lab.setFont(headingFont);
        portPanel.add(lab,c);
        c.weightx = xWeights[4];
        lab = new JLabel("Custom basename");
        lab.setFont(headingFont);
        portPanel.add(lab,c);

        for (int i = 0; i < nCB; i++) {
            // first row is description
            c.gridy = i+1;
            Port port = this.config.getOutputPorts().get(i);
            c.gridx = 0;
            c.weightx = xWeights[0];
            c.fill = GridBagConstraints.NONE;
            
            // Activeness checkbox
            JCheckBox chb = new JCheckBox();
            chb.setSelected(active_ports[i]);
            if(!port.isOptional()){
                chb.setEnabled(false); // Do not change required outputs
            }
            chbs[i] = chb;
            portPanel.add(chb,c);
            chb.addActionListener(this);
            
            // name label
            c.gridx++;
            c.weightx = xWeights[1];
            portPanel.add(new JLabel(port.getName()),c);
            
            // collect types
            List<String> types = port.getMimeTypes();
            String[] strs = new String[types.size()];
            int idx = 0;
            for (String type : types) {
                strs[idx++] = type;
            }
            
            // type combo box
            JComboBox cb = new JComboBox(strs);
            cb.setEnabled(strs.length > 1 && active_ports[i]);
            cbsType[i] = cb;
            c.gridx++;
            c.weightx = xWeights[2];
            portPanel.add(cb,c);
            cb.addActionListener(this);
            
            // Linked inport combobox
            String[] inports = new String[config.getInputPorts().size()+1];
            inports[0] = "auto";
            //TODO we could use the names instead here
            for (int j = 1; j < inports.length; ++j)
            {
                inports[j] = Integer.toString(j);
            }
            JComboBox cblink = new JComboBox(inports);
            cblink.setEnabled(inports.length > 2 && active_ports[i]);
            cbsLink[i] = cblink;
            c.gridx++;
            c.weightx = xWeights[3];
            portPanel.add(cblink,c);
            cblink.addActionListener(this);
            
            // Custom basename
            // Not for ListTypes
            JTextField tf = new JTextField();
            tf.setEditable(true);
            tf.setEnabled(!port.isMultiFile() && active_ports[i]);
            textsBasenames[i] = tf;
            c.gridx++;
            c.weightx = xWeights[4];
            c.fill = GridBagConstraints.HORIZONTAL;
            portPanel.add(tf,c);
            tf.addActionListener(this);
            
        }
        // To align to the top of the outer BorderLayout
        this.add(portPanel, BorderLayout.NORTH);
    }

    public int[] getSelectedTypes() {
        return sel_ports;
    }
    public boolean[] getActiveness() {
        return active_ports;
    }
    
    public int[] getLinkedInputPorts() {
        return linked_inports;
    }

    public String[] getCustomBasenames() {
        for (int i = 0; i < textsBasenames.length; i++) {
          custom_basenames[i] = textsBasenames[i].getText();
        }
        return custom_basenames;
    }

    public void setSelectedTypes(int[] sel_ports) {
        this.sel_ports = new int[sel_ports.length];
        System.arraycopy(sel_ports, 0, this.sel_ports, 0, sel_ports.length);
        for (int i = 0; i < cbsType.length; i++) {
            cbsType[i].setSelectedIndex(sel_ports[i]);
        }
    }
    
    public void setActivePorts(boolean[] act_ports) {
        this.active_ports = new boolean[act_ports.length];
        System.arraycopy(act_ports, 0, this.active_ports, 0, act_ports.length);
        for (int i = 0; i < chbs.length; i++) {
            chbs[i].setSelected(act_ports[i]);
            setEnabledRow(i, act_ports[i]);
        }
    }
    
    public void setBasenameTextboxes(String[] basenames) {
        this.custom_basenames = new String[basenames.length];
        System.arraycopy(basenames, 0, this.custom_basenames, 0, basenames.length);
        for (int i = 0; i < textsBasenames.length; i++) {
            textsBasenames[i].setText(basenames[i]);
        }
    }
    
    public void setSelectedLinkedInports(int[] sel_linked_inports) {
        this.linked_inports = new int[sel_linked_inports.length];
        System.arraycopy(sel_linked_inports, 0, this.active_ports, 0, sel_linked_inports.length);
        for (int i = 0; i < cbsLink.length; i++) {
            cbsLink[i].setSelectedIndex(sel_linked_inports[i]);
        }
    }
    
    private void setEnabledRow(int row, boolean enable) {
        cbsLink[row].setEnabled(enable && cbsLink[row].getItemCount() > 2);
        cbsType[row].setEnabled(enable && cbsType[row].getItemCount() > 1);
        textsBasenames[row].setEnabled(enable && !config.getOutputPorts().get(row).isMultiFile());
    }
    


    @Override
    public void actionPerformed(ActionEvent ev) {
        for (int i = 0; i < cbsType.length; i++) {
            if (ev.getSource() == cbsType[i]) {
                sel_ports[i] = cbsType[i].getSelectedIndex();
            }
        }
        for (int i = 0; i < chbs.length; i++) {
            if (ev.getSource() == chbs[i]) {
                active_ports[i] = chbs[i].isSelected();
                setEnabledRow(i, chbs[i].isSelected());
            }
        }
        for (int i = 0; i < cbsLink.length; i++) {
            if (ev.getSource() == cbsLink[i]) {
                linked_inports[i] = cbsLink[i].getSelectedIndex();
            }
        }
        for (int i = 0; i < textsBasenames.length; i++) {
            if (ev.getSource() == textsBasenames[i]) {
                custom_basenames[i] = textsBasenames[i].getText();
            }
        }

    }



}
