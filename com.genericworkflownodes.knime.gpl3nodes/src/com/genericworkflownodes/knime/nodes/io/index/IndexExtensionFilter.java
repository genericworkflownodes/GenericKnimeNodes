/**
 * Copyright (c) by GKN team
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
package com.genericworkflownodes.knime.nodes.io.index;


import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * <code>IndexExtensionFilter</code> for the "IndexLoader" Node.
 * A component that allows to filter files for extensions
 *
 * @author Kerstin Neubert, FU Berlin
 */ 
public class IndexExtensionFilter extends FileFilter 
{
    private String description;
    private String[] extensions;

    protected IndexExtensionFilter(String description, String... extensions)
    {
        this.extensions = extensions;
        this.description = description;
    }

    @Override
    public boolean accept(File file)
    {
        char[] path = file.getPath().toCharArray();
        for (String extension : extensions)
        {
            if (extension.length() > path.length)
            {
                continue;
            }
            if(file.getName().endsWith(extension)) {
                return true;
            };
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }
    
    public String getDescription()
    {
        return description;
        
    }
}