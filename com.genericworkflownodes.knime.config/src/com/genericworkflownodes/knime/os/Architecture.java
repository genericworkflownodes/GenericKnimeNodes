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
package com.genericworkflownodes.knime.os;

/**
 * Abstraction for the current operating system architecture (32- or 64-bit).
 * The value is based on the System.getProperty("sun.arch.data.model"), so it
 * can also be unknown.
 * 
 * @author aiche
 * 
 */
public enum Architecture {
    /**
     * The enum values.
     */
    X86, X86_64, UNKNOWN;

    /**
     * Construct an {@link Architecture} value based on the
     * System.getProperty("sun.arch.data.model") property.
     * 
     * @return A new {@link Architecture} for the platform where the code is
     *         executed.
     */
    public static Architecture getArchitecture() {
        String dataModel = System.getProperty("sun.arch.data.model");
        Architecture thisArch = UNKNOWN;
        if ("64".equals(dataModel)) {
            thisArch = X86_64;
        } else if ("32".equals(dataModel)) {
            thisArch = X86;
        }

        return thisArch;
    }

    /**
     * Creates a new {@link Architecture} object from the given string.
     * 
     * @param arch
     *            The string that should be converted into an
     *            {@link Architecture}
     * @return The {@link Architecture} corresponding to the given string.
     */
    public static Architecture fromString(final String arch) {
        if ("64".equals(arch)) {
            return X86_64;
        } else if ("32".equals(arch)) {
            return X86;
        } else {
            return UNKNOWN;
        }
    }

    @Override
    public String toString() {
        String archAsString = "";

        switch (this) {
        case X86:
            archAsString = "32";
            break;
        case X86_64:
            archAsString = "64";
            break;
        default:
            break;
        }

        return archAsString;
    }

    /**
     * Returns the {@link Architecture} as an OSGI conform string.
     * 
     * @return The architecture as OSGI conform string.
     */
    public String toOsgiArch() {
        String osgiArch = "";

        switch (this) {
        case X86:
            osgiArch = "x86";
            break;
        case X86_64:
            osgiArch = "x86_64";
            break;
        default:
            break;
        }

        return osgiArch;
    }
}
