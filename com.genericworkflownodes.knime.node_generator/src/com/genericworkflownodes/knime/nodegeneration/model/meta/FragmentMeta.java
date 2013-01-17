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
package com.genericworkflownodes.knime.nodegeneration.model.meta;

import com.genericworkflownodes.knime.custom.Architecture;
import com.genericworkflownodes.knime.custom.OperatingSystem;

/**
 * 
 * Meta information of one generated fragment. Fragments contain
 * platform-specific binaries.
 * 
 * @author aiche, bkahlert
 */
public class FragmentMeta {

	private final String parentId;
	private final Architecture arch;
	private final OperatingSystem os;

	/**
	 * Constructs the fragment meta information given a base id, the
	 * architecture and operating system this fragment can be executed on.
	 * 
	 * @param parentId
	 * @param arch
	 * @param os
	 */
	public FragmentMeta(String parentId, Architecture arch, OperatingSystem os) {
		this.parentId = parentId;
		this.arch = arch;
		this.os = os;
	}

	/**
	 * Returns the id of the parent bundle.
	 * 
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * Returns the architecture of this fragment.
	 * 
	 * @return
	 */
	public Architecture getArch() {
		return arch;
	}

	/**
	 * Returns the operating system of this fragment.
	 * 
	 * @return
	 */
	public OperatingSystem getOs() {
		return os;
	}

	/**
	 * Returns the final id of the fragment.
	 * 
	 * @return
	 */
	public String getId() {
		return String.format("%s.%s.%s", parentId, os.toOsgiOs(),
				arch.toOsgiArch());
	}
}
