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

package org.ballproject.knime.base.treetabledialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.jdesktop.swingx.VerticalLayout;

public class MimeTypeChooserDialog extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3102737955888696834L;

	private INodeConfiguration config;

	private JComboBox[] cbs;
	private int[] sel_ports;

	public MimeTypeChooserDialog(INodeConfiguration config) {
		this.config = config;

		this.setLayout(new VerticalLayout());

		int nCB = this.config.getNumberOfOutputPorts();

		cbs = new JComboBox[nCB];
		sel_ports = new int[nCB];

		for (int i = 0; i < nCB; i++) {
			Port port = this.config.getOutputPorts()[i];

			this.add(new JLabel(port.getName()));

			List<MIMEtype> types = port.getMimeTypes();
			String[] strs = new String[types.size()];

			int idx = 0;
			for (MIMEtype type : types)
				strs[idx++] = type.getExt();

			JComboBox cb = new JComboBox(strs);
			cbs[i] = cb;
			this.add(cb);
			cb.addActionListener(this);
		}
	}

	public int[] getSelectedTypes() {
		return sel_ports;
	}

	public void setSelectedTypes(int[] sel_ports) {
		this.sel_ports = sel_ports;
		for (int i = 0; i < cbs.length; i++) {
			cbs[i].setSelectedIndex(sel_ports[i]);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		for (int i = 0; i < cbs.length; i++) {
			if (ev.getSource() == cbs[i]) {
				sel_ports[i] = cbs[i].getSelectedIndex();
			}
		}

	}

}
