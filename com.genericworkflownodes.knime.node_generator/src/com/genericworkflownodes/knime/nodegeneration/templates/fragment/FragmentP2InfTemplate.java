/**
 * Copyright (c) 2014, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodegeneration.templates.fragment;

import java.io.IOException;
import java.util.List;

import com.genericworkflownodes.knime.nodegeneration.templates.Template;

/**
 * @author aiche
 */
public class FragmentP2InfTemplate extends Template {

    public FragmentP2InfTemplate(final List<String> files) throws IOException {
        super(FragmentP2InfTemplate.class.getResourceAsStream("p2inf.template"));

        // construct list of instructions
        StringBuilder instructions = new StringBuilder();
        for (String file : files) {
            instructions.append(String.format(
                    "org.eclipse.equinox.p2.touchpoint.eclipse.chmod(targetDir:@artifact,\\\n"
                            + "targetFile:%s,permissions:755);\n", file));
        }

        replace("__P2_INF_INSTRUCTIONS__", instructions.toString());
    }
}
