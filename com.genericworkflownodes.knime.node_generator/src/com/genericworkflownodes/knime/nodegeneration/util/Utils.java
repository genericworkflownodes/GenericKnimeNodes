package com.genericworkflownodes.knime.nodegeneration.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Utility class for the KNIME node generation.
 * 
 * @author bkahlert, aiche
 */
public final class Utils {

    /**
     * Private c'tor to avoid instantiation of util class.
     */
    private Utils() {
    }

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(Utils.class
            .getCanonicalName());

    /**
     * returns all prefix paths of a given path. This are only abstract,
     * non-system paths. Therefore they always need to have the forward slash as
     * separator.
     * 
     * /foo/bar/baz --> [/foo/bar, /foo, /]
     * 
     * @note The path needs to start with a "/" (forward slash) otherwise the
     *       method method will
     * @param path
     * @return A list of all valid path prefixes
     */
    public static List<String> getPathPrefixes(String path) {
        // otherwise it is not a path
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path needs to start with \"/\"");
        }

        List<String> prefixList = new ArrayList<String>();

        // remove trailing '/' as it would lead to an incorrect prefix
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String prefix = path;
        // will abort as soon as prefix == "/"
        while (prefix.length() > 1) {
            prefixList.add(prefix);
            int lastIndexOfFS = prefix.lastIndexOf("/");
            prefix = prefix.substring(0, lastIndexOfFS);
        }

        // "/" is a special prefix, therefore we add it separatly
        prefixList.add("/");

        return prefixList;
    }

    /**
     * returns the prefix path of the given path.
     * 
     * /foo/bar/baz ---> /foo/bar/
     * 
     * @param path
     * @return
     */
    public static String getPathPrefix(String path) {
        // otherwise it is not a path
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path needs to start with \"/\"");
        }

        return path.substring(0, path.lastIndexOf("/"));
    }

    /**
     * returns the path suffix for a given path.
     * 
     * /foo/bar/baz --> baz
     * 
     * @param path
     * @return
     */
    public static String getPathSuffix(String path) {
        // otherwise it is not a path
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path needs to start with \"/\"");
        }

        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * Formats a {@link Document} as XML and writes it to the given {@link File}
     * .
     * 
     * @param doc
     *            The XML document to store.
     * @param dest
     *            The file where it should be stored.
     * @throws IOException
     *             In case of IO errors.
     */
    public static void writeDocumentTo(Document doc, File dest)
            throws IOException {
        XMLWriter writer = new XMLWriter(new FileWriter(dest),
                OutputFormat.createPrettyPrint());
        writer.write(doc);
        writer.close();
    }

    public static boolean checkKNIMENodeName(String name) {
        if (!name.matches("[[A-Z]|[a-z]][[0-9]|[A-Z]|[a-z]]+")) {
            return false;
        }
        return true;
    }

    public static String fixKNIMENodeName(String name) {
        name = name.replace(" ", "");
        name = name.replace(".", "");
        name = name.replace("-", "");
        name = name.replace("_", "");
        name = name.replace("#", "");
        name = name.replace("+", "");
        name = name.replace("$", "");
        name = name.replace(":", "");
        name = name.replace(";", "");
        return name;
    }
}
