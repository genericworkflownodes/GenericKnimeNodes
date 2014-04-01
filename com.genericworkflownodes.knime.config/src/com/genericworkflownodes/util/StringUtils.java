/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Collection of utility methods and classes for Strings.
 * 
 * @author aiche et al.
 * 
 */
public final class StringUtils {

    /**
     * C'tor. Prohibit initialization since it is a utility class.
     */
    private StringUtils() {
    }

    /**
     * Joins all elements of the {@link Collection} into a single string,
     * separating them by the passed delimiter.
     * 
     * @param collection
     *            The collection to join.
     * @param delimiter
     *            The used delimiter.
     * @return A string containing all elements of the collection separated by
     *         the delimiter.
     */
    public static String join(final Collection<?> collection,
            final String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = collection.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
}
