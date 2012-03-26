package org.ballproject.knime.base.treetabledialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class UIHelper
{
	private static final Insets insets = new Insets(2,2,2,2);

	public static void addComponent(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, float weighty) 
	{
		GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
		gridwidth, gridheight, 1.0, weighty, anchor, fill, insets, 0, 0);
		container.add(component, gbc);
	}

	public static void addComponent(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, float weightx, float weighty) 
	{
		GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
		gridwidth, gridheight, weightx, weighty, anchor, fill, insets, 0, 0);
		container.add(component, gbc);
	}
}
