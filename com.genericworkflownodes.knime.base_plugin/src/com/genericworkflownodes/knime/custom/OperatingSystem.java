/**
 * Copyright (c) 2012, Björn Kahlert, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom;

/**
 * Abstraction of the Operating System type which includes the correct string
 * conversions for the zip files containing the binaries.
 * 
 * @author aiche
 */
enum OperatingSystem {
	WIN, UNIX, MAC;

	/**
	 * Get the Operating system based on System.getProperty("os.name").
	 * 
	 * @return The operating system the JVM is running on.
	 */
	public static OperatingSystem getOS() {
		String os = System.getProperty("os.name");
		OperatingSystem thisOS = WIN;

		if (os.toLowerCase().contains("nux")
				|| os.toLowerCase().contains("nix")) {
			thisOS = UNIX;
		}
		if (os.toLowerCase().contains("mac")) {
			thisOS = MAC;
		}
		return thisOS;
	}

	@Override
	public String toString() {
		String osAsString = "unknown";
		switch (this) {
		case WIN:
			osAsString = "win";
			break;
		case UNIX:
			osAsString = "lnx";
			break;
		case MAC:
			osAsString = "mac";
			break;
		}
		return osAsString;
	}
}