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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.port.Port;

public class MimeTypeChooserDialog extends JPanel implements ActionListener {
    private static final long serialVersionUID = 3102737955888696834L;

    private INodeConfiguration config;

    private JComboBox[] cbs;
    private JCheckBox[] chbs;
    private int[] sel_ports;
    private boolean[] active_ports;

    public MimeTypeChooserDialog(INodeConfiguration config) {
        this.config = config;
        int nCB = this.config.getNumberOfOutputPorts();
        
        // Outer=full panel
        setLayout(new BorderLayout());

        // Inner panel with row for each port
        JPanel portPanel = new JPanel();
        portPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        cbs = new JComboBox[nCB];
        chbs = new JCheckBox[nCB];
        sel_ports = new int[nCB];
        active_ports = new boolean[nCB];

        for (int i = 0; i < nCB; i++) {
            // row
            c.gridy = i;
            Port port = this.config.getOutputPorts().get(i);
            
            // name label
            c.gridx = 0;
            c.weightx = 0.2;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
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
            cbs[i] = cb;
            c.gridx++;
            c.weightx = 0.6;
            portPanel.add(cb,c);
            cb.addActionListener(this);
            
            // Active? label
            c.gridx++;
            c.weightx = 0.1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            portPanel.add(new JLabel("Active?"),c);
            
            // Activeness checkbox
            JCheckBox chb = new JCheckBox();
            chb.setSelected(true);
            if(!port.isOptional()){
                chb.setEnabled(false); // Do not change required inputs
            }
            chbs[i] = chb;
            c.gridx++;
            c.anchor = GridBagConstraints.CENTER;
            portPanel.add(chb,c);
            chb.addActionListener(this);
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

    public void setSelectedTypes(int[] sel_ports) {
        this.sel_ports = new int[sel_ports.length];
        System.arraycopy(sel_ports, 0, this.sel_ports, 0, sel_ports.length);
        for (int i = 0; i < cbs.length; i++) {
            cbs[i].setSelectedIndex(sel_ports[i]);
        }
    }
    
    public void setActivePorts(boolean[] act_ports) {
        this.active_ports = new boolean[act_ports.length];
        System.arraycopy(act_ports, 0, this.active_ports, 0, act_ports.length);
        for (int i = 0; i < chbs.length; i++) {
            chbs[i].setSelected(act_ports[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (int i = 0; i < cbs.length; i++) {
            if (ev.getSource() == cbs[i]) {
                sel_ports[i] = cbs[i].getSelectedIndex();
            }
        }
        for (int i = 0; i < chbs.length; i++) {
            if (ev.getSource() == chbs[i]) {
                active_ports[i] = chbs[i].isSelected();
            }
        }

    }

}
