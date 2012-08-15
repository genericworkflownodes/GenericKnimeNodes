/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.mime.demangler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.url.MIMEType;
import org.knime.core.node.NodeLogger;

/**
 * Concrete implementation of an {@link IDemanglerRegistry}.
 * 
 * @author aiche
 */
public class DemanglerRegistry implements IDemanglerRegistry {

	/**
	 * The id of the used extension point.
	 */
	private static final String EXTENSION_POINT_ID = "com.genericworkflownodes.knime.mime.demangler.Demangler";

	/**
	 * The central static logger.
	 */
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(DemanglerRegistry.class);

	@Override
	public List<IDemangler> getDemangler(final MIMEType mType) {
		List<IDemangler> availableDemangler = getAvailableDemangler();
		List<IDemangler> candidateDemanger = new ArrayList<IDemangler>();

		for (IDemangler demangler : availableDemangler) {
			if (demangler.getMIMEType().equals(mType)) {
				candidateDemanger.add(demangler);
			}
		}

		return availableDemangler;
	}

	/**
	 * Searchs through the eclipse extension point registry for registered
	 * {@link IDemangler}s.
	 * 
	 * @return A list of available {@link IDemangler}s.
	 */
	private List<IDemangler> getAvailableDemangler() {
		List<IDemangler> availableDemangler = new ArrayList<IDemangler>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg
				.getConfigurationElementsFor(EXTENSION_POINT_ID);
		try {
			for (IConfigurationElement elem : elements) {
				final Object o = elem.createExecutableExtension("class");
				// cast is guaranteed to work based on the extension point
				// definition
				availableDemangler.add((IDemangler) o);
			}
		} catch (CoreException e) {
			LOGGER.warn(e.getMessage());
		}
		return availableDemangler;
	}

	@Override
	public List<IDemangler> getMangler(final DataTableSpec spec) {
		List<IDemangler> availableDemangler = getAvailableDemangler();
		List<IDemangler> candidateDemanger = new ArrayList<IDemangler>();

		for (IDemangler demangler : availableDemangler) {
			if (isContainedIn(demangler.getTableSpec(), spec)) {
				candidateDemanger.add(demangler);
			}
		}

		return candidateDemanger;
	}

	/**
	 * Checks if the first {@link DataTableSpec} is contained in to the second
	 * one. Is contained in means, that for every row in the first
	 * {@link DataTableSpec} exists a row with equal name {@link DataTableSpec}
	 * and type in the second {@link DataTableSpec}. Note that this operation is
	 * not symmetric, i.e., a is contained in b does not mean that b is in a.
	 * 
	 * @param specToCheck
	 *            The {@link DataTableSpec} that should be contained.
	 * @param containingSpec
	 *            The {@link DataTableSpec} should contain the other
	 *            {@link DataTableSpec}
	 * @return True if {@code specToCheck} is contained in
	 *         {@code containingSpec}.
	 */
	private boolean isContainedIn(final DataTableSpec specToCheck,
			final DataTableSpec containingSpec) {

		if (specToCheck.equalStructure(containingSpec)) {
			return true;
		} else {
			boolean isEqual = true;
			for (DataColumnSpec col : specToCheck) {
				// find spec with equal name
				DataColumnSpec potentialMatch = containingSpec
						.getColumnSpec(col.getName());
				if (potentialMatch == null) {
					isEqual = false;
					break;
				} else {
					if (!potentialMatch.getType().equals(col.getType())) {
						isEqual = false;
						break;
					}
				}
			}
			return isEqual;
		}
	}

}
