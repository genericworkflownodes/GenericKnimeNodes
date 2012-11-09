package com.genericworkflownodes.knime.generic_node.dialogs;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

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

	public static void simulateEnterKeyPressed(final JComponent component,
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

		if (delayInMilliseconds == 0)
			pressEnter.run();
		else
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(delayInMilliseconds);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pressEnter.run();
				}
			}).start();
	}
}
