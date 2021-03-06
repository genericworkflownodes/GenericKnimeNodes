package com.genericworkflownodes.knime.generic_node.dialogs;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class UIHelper {

    private static final Insets insets = new Insets(2, 2, 2, 2);

    public static void addComponent(Container container, Component component,
            int gridx, int gridy, int gridwidth, int gridheight, int anchor,
            int fill, float weighty) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
                gridwidth, gridheight, 1.0, weighty, anchor, fill, insets, 0, 0);
        container.add(component, gbc);
    }

    public static void addComponent(Container container, Component component,
            int gridx, int gridy, int gridwidth, int gridheight, int anchor,
            int fill, float weightx, float weighty) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
                gridwidth, gridheight, weightx, weighty, anchor, fill, insets,
                0, 0);
        container.add(component, gbc);
    }

    public static void simulateEnterKeyPressed(final Component component,
            final int delayInMilliseconds) {
        final Runnable pressEnter = new Runnable() {
            @Override
            public void run() {
                KeyEvent keyEvent = new KeyEvent(component,
                        KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                        KeyEvent.VK_ENTER, (char) KeyEvent.VK_ENTER, 1);
                java.lang.reflect.Field f;
                try {
                    f = AWTEvent.class
                            .getDeclaredField("focusManagerIsDispatching");
                    f.setAccessible(true);
                    f.set(keyEvent, Boolean.TRUE);
                    component.dispatchEvent(keyEvent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        };

        invokeDelayed(delayInMilliseconds, pressEnter);
    }

    public static void invokeDelayed(final int delayInMilliseconds,
            final Runnable action) {
        if (delayInMilliseconds == 0)
            SwingUtilities.invokeLater(action);
        else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delayInMilliseconds);
                        SwingUtilities.invokeAndWait(action);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    /**
     * Resizes a {@link JDialog} by a given factor based on the client's screen
     * size. The screen is centered afterwards.
     * 
     * @param dialog
     * @param factor
     */
    public static void resizeAndCenter(JDialog dialog, double factor) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize((int) Math.round(screenSize.width * factor),
                (int) Math.round(screenSize.height * factor));
        centerDialog(dialog);
    }

    /**
     * Centers the dialog on the screen.
     * 
     * @param dialog
     */
    public static void centerDialog(JDialog dialog) {
        dialog.setLocationRelativeTo(null);
    }
}
