/**
 * Copyright (c) 2013, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildIconsDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildKnimeDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildPackageRootDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildSrcDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;

/**
 * Abstract representation of plugin project (e.g., the base plugin or the
 * fragments).
 * 
 * @author aiche
 */
public abstract class GenericPluginDirectory extends PluginDirectory {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -1643991173932449191L;

    private NodesBuildIconsDirectory iconsDirectory = null;
    private NodesBuildSrcDirectory srcDirectory = null;
    private NodesBuildPackageRootDirectory packageRootDirectory = null;
    private NodesBuildKnimeDirectory knimeDirectory = null;
    private NodesBuildKnimeNodesDirectory knimeNodesDirectory = null;

    /**
     * Constructor with the path to the location where the plugin should be
     * created.
     * 
     * @param directory
     *            The directory where the plugin is created.
     * @param packageName
     *            The name of the top-level package inside the plugin.
     * @throws FileNotFoundException
     */
    public GenericPluginDirectory(File directory, GeneratedPluginMeta meta)
            throws PathnameIsNoDirectoryException, FileNotFoundException {
        super(directory);
        init(meta);
    }

    private void init(GeneratedPluginMeta meta) throws PathnameIsNoDirectoryException {
        String packageRootPath = meta.getId().replace('.', File.separatorChar);

        new File(this, "src" + File.separator + packageRootPath).mkdirs();
        new File(this, "META-INF").mkdirs();
        
        if (!meta.isResourceOnly())
        {
        	new File(this, "icons").mkdirs();
            new File(this, "src" + File.separator + packageRootPath
                    + File.separator + "knime" + File.separator + "nodes").mkdirs();
        }

        try {
			
            srcDirectory = new NodesBuildSrcDirectory(new File(this, "src"));

            packageRootDirectory = new NodesBuildPackageRootDirectory(new File(
                    srcDirectory, packageRootPath));

            if (!meta.isResourceOnly())
            {
            iconsDirectory = new NodesBuildIconsDirectory(new File(this, "icons"));
            knimeDirectory = new NodesBuildKnimeDirectory(new File(
                    packageRootDirectory, "knime"));

            knimeNodesDirectory = new NodesBuildKnimeNodesDirectory(new File(
                    knimeDirectory, "nodes"));
            }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PathnameIsNoDirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Returns the directory where to put all icons in.
     * <p>
     * e.g. /tmp/372/icons
     * 
     * @return
     */
    public NodesBuildIconsDirectory getIconsDirectory() {
        return iconsDirectory;
    }

    /**
     * Returns the directory where to put all sources in.
     * <p>
     * e.g. /tmp/372/src
     * 
     * @return
     */
    public NodesBuildSrcDirectory getSrcDirectory() {
        return srcDirectory;
    }

    /**
     * Returns the source directory where the package root resides.
     * <p>
     * e.g. /tmp/372/src/de/fu_berlin/imp/seqan
     * 
     * @return
     */
    public NodesBuildPackageRootDirectory getPackageRootDirectory() {
        return packageRootDirectory;
    }

    /**
     * Returns the source directory where to put all KNIME classes.
     * <p>
     * e.g. /tmp/372/src/de/fu_berlin/imp/seqan/knime
     * 
     * @return
     */
    public NodesBuildKnimeDirectory getKnimeDirectory() {
        return knimeDirectory;
    }

    /**
     * Returns the source directory where to put all KNIME node classes.
     * <p>
     * e.g. /tmp/372/src/de/fu_berlin/imp/seqan/knime/nodes
     * 
     * @return
     */
    public NodesBuildKnimeNodesDirectory getKnimeNodesDirectory() {
        return knimeNodesDirectory;
    }

}
