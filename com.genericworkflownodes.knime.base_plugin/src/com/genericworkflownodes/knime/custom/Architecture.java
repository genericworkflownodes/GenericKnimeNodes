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
package com.genericworkflownodes.knime.custom;

/**
 * Abstraction for the current operating system architecture (32- or 64-bit).
 * The value is based on the System.getProperty("sun.arch.data.model"), so it
 * can also be unknown.
 * 
 * @author aiche
 * 
 */
enum Architecture {
	X86, X86_64, UNKNOWN;

	public static Architecture getArchitecture() {
		String data_model = System.getProperty("sun.arch.data.model");
		Architecture thisArch = UNKNOWN;
		if ("64".equals(data_model)) {
			thisArch = X86_64;
		} else if ("32".equals(data_model)) {
			thisArch = X86;
		}

		return thisArch;
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
		}

		return archAsString;
	}
}