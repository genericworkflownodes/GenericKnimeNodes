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

import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to compare and get Index types from file names.
 * 
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexTypeHelper {

    /**
     * Utility class should have private c'tor.
     */
    private IndexTypeHelper() {
    }

    /**
     * Extracts the {@link IndexTypeEntry} from the given filename.
     * 
     * @param filename
     *            The file for which the index type should be extracted.
     * @return The Index type of the file.
     */
    public static String getIndextype(String filename) {
        String type = null;
        String foundExtension = "";
        
        for (IndexTypeEntry entry : IndexMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                if (filename.toLowerCase().endsWith(
                        "." + ext.trim().toLowerCase())
                        && ext.length() > foundExtension.length()) {
                    type = entry.getType();
                    foundExtension = ext.trim();
                }
            }
        }
        
        return type;
    }

    /**
     * Extracts the {@link IndexTypeEntry} from the given extension.
     * 
     * @param extension
     *            The file extension for which the index type should be
     *            extracted.
     * 
     * @return The Index type of the file.
     */
    public static String getIndextypeByExtension(String extension) {
        // check existing types
        String type = null;
        for (IndexTypeEntry entry : IndexMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                // some types are stored with spaces around them so .trim()
                if (extension.toLowerCase().equals(ext.trim())) {
                    return entry.getType();
                }
            }
        }
        return type;
    }

    /**
     * Extracts the file extension that was used to get the
     * {@link IndexTypeEntry} from the given filename.
     * 
     * @param filename
     *            The file for which the extension should be extracted.
     * @return The Index type extension of the file, null if no Index type was
     *         extracted.
     */
    public static String getIndextypeExtension(String filename) {
        // check existing mimetypes
        String type = null;
        String foundExtension = "";
        for (IndexTypeEntry entry : IndexMap.getAllTypes()) {
            for (String ext : entry.getExtensions()) {
                if (filename.toLowerCase().endsWith(
                        "." + ext.trim().toLowerCase())
                        && ext.length() > foundExtension.length()) {
                    type = entry.getType();
                    foundExtension = ext.trim();
                }
            }
        }
        return (type != null ? foundExtension : null);
    }

    /**
     * Extracts the file extensions
     * from the given {@link IndexTypeEntry}.
     * 
     * @param Index type
     *            The Index type for which the extensions should be extracted.
     * @return The Index type extensions
     */
    public static String[] getExtensionsByIndexType (String index_type) {
         
        IndexTypeEntry[] types = IndexMap.getAllTypes();
        List<String> ext_list = new ArrayList<String>();
        
        for (int i = 0; i < types.length; i++) {
            String index = types[i].getType();
            List<String> ext = types[i].getExtensions();
            if (index_type != null && index.compareTo(index_type) == 0) {
                ext_list.addAll(ext);
            }
               
        }      
        String[] index_array = new String[ext_list.size()];
        ext_list.toArray(index_array);
        
        return index_array;
        
    } 
    
    /**
     * Extracts all available Index types from the extension point.
     *  
     * @return The Index types
     */
    public static String[] getAllIndexTypes() {
        
        IndexTypeEntry[] types = IndexMap.getAllTypes();
        List<String> available_index_types = new ArrayList<String>();

        available_index_types.add(""); // all types
        for (int i = 0; i < types.length; i++) {
            String index = types[i].getType();
            List<String> ext = types[i].getExtensions();
            if (ext.size() > 0) {  
                available_index_types.add(index);  
            }
        }
        String[] index_types = new String[available_index_types.size()];
        available_index_types.toArray(index_types);
        
        return index_types;
    }
    
    /**
     * Extracts all representative extensions for all Index types from the extension point.
     *  
     * @return The file extensions (first one for each index type)
     */
    public static String[] getRepresentativeExtensions() {
        
        IndexTypeEntry[] types = IndexMap.getAllTypes();
        String[] valid_extensions = new String[types.length];
        
        for (int i = 0; i < types.length; i++) {
            List<String> ext = types[i].getExtensions();
            String extension = ext.get(0);
            //'lf.drp' : does not search for this extension, only 'drp' works --> cut extension before "."
            int lastDot = extension.lastIndexOf('.') == -1 ? 0 : extension.lastIndexOf('.') + 1;
            extension = extension.substring(lastDot);
            valid_extensions[i] = extension;

        }
        
        return valid_extensions;
    }
    
    /**
     * Extracts the extension from the given path.
     * 
     * @param path
     *            The path from which the extension should be extracted.
     * @return The file extension of the given path.
     */
    public static String getExtension(String path) {
        if (path.lastIndexOf('.') == -1) {
            return "";
        } else {
            return path.substring(path.lastIndexOf('.') + 1);
        }
    }
}

