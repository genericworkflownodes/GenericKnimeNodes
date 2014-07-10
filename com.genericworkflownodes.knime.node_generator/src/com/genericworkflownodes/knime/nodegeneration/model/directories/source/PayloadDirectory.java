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

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.knime.os.Architecture;
import com.genericworkflownodes.knime.os.OperatingSystem;

/**
 * Abstraction of the payload directory inside the generated plugin.
 * 
 * @author bkahlert, aiche
 */
public class PayloadDirectory {
    private static final Logger LOGGER = Logger
            .getLogger(PayloadDirectory.class.getCanonicalName());

    private static final Pattern payloadFormat = Pattern
            .compile("^binaries_(mac|lnx|win)_([36][24]).zip$");

    private final List<FragmentMeta> containedFragments;
    private Directory payloadDirectory = null;

    public PayloadDirectory(File payloadDirectory)
            throws PathnameIsNoDirectoryException {
        containedFragments = new ArrayList<FragmentMeta>();
        try {
            this.payloadDirectory = new Directory(payloadDirectory);
        } catch (Exception e) {
            LOGGER.warning("No payload directory available.");
        }
    }

    public List<FragmentMeta> getFragmentMetas(
            GeneratedPluginMeta generatedPluginMeta) {

        String[] expectedFragments = new String[] { "binaries_mac_64.zip",
                "binaries_lnx_64.zip", "binaries_lnx_32.zip",
                "binaries_win_64.zip", "binaries_win_32.zip" };

        for (String potentialFragment : expectedFragments) {
            // get the matching properties
            Matcher m = payloadFormat.matcher(potentialFragment);
            m.find();
            OperatingSystem os = OperatingSystem.fromString(m.group(1));
            Architecture arch = Architecture.fromString(m.group(2));

            File payload = new File(payloadDirectory, potentialFragment);

            if (payload.exists()) {
                containedFragments.add(new FragmentMeta(generatedPluginMeta,
                        arch, os, payload));
            } else {
                // generate dummy fragment .. will allow users to link their
                // own stuff into the fragment
                containedFragments.add(new FragmentMeta(generatedPluginMeta,
                        arch, os, null));
            }
        }

        return containedFragments;
    }
}
