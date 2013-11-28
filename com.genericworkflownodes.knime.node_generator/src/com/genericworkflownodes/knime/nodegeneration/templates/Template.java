/*
 * Copyright (c) 2011, Marc Röttig.
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

package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * The TemplateFiller class is used to replace within a string template
 * designated tokens with variable text.
 * 
 * @author roettig
 * @author bkahlert
 * 
 */
public class Template {

    private String data;

    /**
     * reads in the template from file.
     * 
     * @param file
     *            file
     * @throws IOException
     */
    public Template(File file) throws IOException {
        data = FileUtils.readFileToString(file);
    }

    /**
     * reads in the template from input stream.
     * 
     * @param in
     *            input stream
     * @throws IOException
     */
    public Template(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        data = writer.toString();
    }

    /**
     * replaces the specified token within the template with the supplied text.
     * 
     * @param token
     *            target token
     * @param value
     *            text to fill in
     */
    public void replace(String token, String value) {
        data = data.replace(token, value);
    }

    /**
     * writes the filled template out to supplied stream.
     * 
     * @param out
     *            output stream
     * @throws IOException
     */
    public void write(OutputStream out) throws IOException {
        out.write(data.getBytes());
    }

    /**
     * writes the filled template out to supplied file.
     * 
     * @param file
     *            output file
     * @throws IOException
     */
    public void write(File file) throws IOException {
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        write(out);
        out.close();
    }
}
