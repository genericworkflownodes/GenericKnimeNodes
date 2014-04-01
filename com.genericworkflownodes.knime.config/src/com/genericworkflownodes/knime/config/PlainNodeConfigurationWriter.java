/**
 * Copyright (c) 2011, Marc Röttig.
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
package com.genericworkflownodes.knime.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Exports the {@link INodeConfiguration} as a list of key and value.
 * 
 * @author aiche
 */
public class PlainNodeConfigurationWriter {
    private INodeConfiguration nodeConfig;

    private static String LINESEP = System.getProperty("line.separator");

    public void init(INodeConfiguration nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    public void write(String filename) throws IOException {
        FileWriter out = new FileWriter(new File(filename));

        for (String key : nodeConfig.getParameterKeys()) {
            Parameter<?> p = nodeConfig.getParameter(key);
            StringBuffer sb = new StringBuffer();
            if (p instanceof ListParameter) {
                ListParameter lp = (ListParameter) p;
                for (String value : lp.getStrings()) {
                    sb.append(String.format("\"%s\"\t", value));
                }
            } else {
                sb.append(String.format("\"%s\"\t", p.getStringRep()));
            }
            out.write(key + ":" + sb.toString() + LINESEP);
        }
        out.close();
    }
}
