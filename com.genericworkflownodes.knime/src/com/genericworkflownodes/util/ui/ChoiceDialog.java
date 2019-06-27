package com.genericworkflownodes.util.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jdesktop.swingx.VerticalLayout;

public final class ChoiceDialog extends JPanel implements ActionListener {
    private static final long serialVersionUID = 6562469097273188630L;
    private final JComboBox comboBox;
    private ChoiceDialogListener listener;

    public ChoiceDialog(ComboBoxModel model) {
        setLayout(new VerticalLayout());
        comboBox = new JComboBox(model);
        add(comboBox);
        comboBox.addActionListener(this);
    }

    public void registerChoiceListener(ChoiceDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (listener != null) {
            listener.onChoice(comboBox.getSelectedIndex());
        }
    }
}
