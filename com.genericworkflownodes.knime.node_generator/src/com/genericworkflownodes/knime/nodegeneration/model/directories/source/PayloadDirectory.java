/**
 * Copyright (c) 2013, Stephan Aiche, Björn Kahlert.
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
package com.genericworkflownodes.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

/**
 * Abstraction of the payload directory inside the generated plugin.
 * 
 * @author bkahlert, aiche
 */
public class PayloadDirectory extends Directory {
	private static final Logger LOGGER = Logger
			.getLogger(PayloadDirectory.class.getCanonicalName());

	private static final long serialVersionUID = -400249694994228712L;

	private static final Pattern payloadFormat = Pattern
			.compile("^binaries_(mac|lnx|win)_([36][24]).zip$");

	public PayloadDirectory(File payloadDirectory)
			throws PathnameIsNoDirectoryException {
		super(payloadDirectory);
	}

	public List<FragmentMeta> getFragmentMetas(
			GeneratedPluginMeta generatedPluginMeta) {

		List<FragmentMeta> containedFragments = new ArrayList<FragmentMeta>();

		for (String payload : list()) {
			Matcher m = payloadFormat.matcher(payload);
			if (!m.find()) {
				LOGGER.warning("Ignoring incompatible file in payload directory: "
						+ payload);
				continue;
			}
			LOGGER.info("Create payload fragment for " + payload);

			OperatingSystem os = OperatingSystem.fromString(m.group(1));
			Architecture arch = Architecture.fromString(m.group(2));

			containedFragments
					.add(new FragmentMeta(generatedPluginMeta, arch, os));
		}

		return containedFragments;
	}
}
