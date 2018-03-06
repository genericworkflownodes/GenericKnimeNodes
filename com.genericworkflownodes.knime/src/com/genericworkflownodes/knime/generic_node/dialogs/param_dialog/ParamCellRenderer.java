package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;

import org.netbeans.swing.outline.DefaultOutlineCellRenderer;

import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.ParameterNode;



/**
* Renderer for the first parameter column. Uses different fonts for optional etc.
*/
public class ParamCellRenderer extends DefaultOutlineCellRenderer {

    private static final long serialVersionUID = -7908521959904400099L;
    private static final Font MAND_FONT = new Font("Dialog", Font.BOLD, 12);
    private static final Font OPT_FONT = new Font("Dialog", Font.ITALIC, 12);

    public ParamCellRenderer() {
        super();
    }

    protected void setItalic(boolean on) {
        if (on) {
            setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
       } else {
            setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        }
   }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof ParameterNode) {
            boolean optional = true;
            boolean advanced = false;
            boolean defaulted = false;
            ParameterNode paramnode = (ParameterNode) value;
            if (paramnode.getPayload() != null) {
                optional = paramnode.getPayload().isOptional();
                advanced = paramnode.getPayload().isAdvanced();
                defaulted = paramnode.getPayload().isDefaulted();
            }
            if (!optional) {
                setFont(MAND_FONT);
            } else {
                setFont(OPT_FONT);
            }
            
            //TODO See if you can mark the whole row pink.
            if(defaulted) {
                setBackground(Color.PINK);
            }
            
            if (!isSelected) {
                if (advanced) {
                    setForeground(Color.GRAY);
                } else {
                    setForeground(Color.BLACK);
                }
            } else {
                setForeground(Color.WHITE);
            }
            value = paramnode.getName();
        }
        setValue(value);
        return c;
}
}