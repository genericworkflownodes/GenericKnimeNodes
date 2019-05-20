package com.genericworkflownodes.knime.nodes.io.index;

/**
 * <code>IndexExtensionFilter</code> for the "IndexLoader" Node.
 * 
 *
 * @author Kerstin Neubert, FU Berlin
 */


import java.io.File;
import javax.swing.filechooser.FileFilter;


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