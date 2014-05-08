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
package com.genericworkflownodes.knime.base.data.port;

import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.node.util.ConvenienceMethods;

/**
 * Collection of utility methods for our custom port objects.
 * 
 * @author aiche
 */
public class CustomPortObjectUtils {

    /**
     * The number of extensions to show if string representation is generated.
     */
    private static final int NUMBER_OF_EXTENSIONS_TO_SHOW = 3;

    /**
     * Avoid instantiation.
     */
    private CustomPortObjectUtils() {
    }

    public static String getSummary(final IURIPortObject po) {
        StringBuilder b = new StringBuilder();
        int size = po.getURIContents().size();
        b.append(size);
        b.append(size == 1 ? " file (extension: " : " files (extensions: ");
        b.append(ConvenienceMethods.getShortStringFrom(po.getSpec()
                .getFileExtensions(), NUMBER_OF_EXTENSIONS_TO_SHOW));
        b.append(")");
        return b.toString();
    }

}
