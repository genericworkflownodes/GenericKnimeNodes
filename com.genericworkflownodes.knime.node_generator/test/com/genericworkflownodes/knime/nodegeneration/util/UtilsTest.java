package com.genericworkflownodes.knime.nodegeneration.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testGetPathPrefixes() {
        String path = "/foo/bar/baz";
        List<String> prefixList = Utils.getPathPrefixes(path);
        assertEquals(4, prefixList.size());

        assertEquals("/foo/bar/baz", prefixList.get(0));
        assertEquals("/foo/bar", prefixList.get(1));
        assertEquals("/foo", prefixList.get(2));
        assertEquals("/", prefixList.get(3));
    }

    @Test
    public void testGetPathPrefixesEndSlashBug() {
        // trailing slash will not be treated as empty path but as artifact and
        // will be removed
        String path = "/foo/bar/baz/";
        List<String> prefixList = Utils.getPathPrefixes(path);
        assertEquals(4, prefixList.size());

        assertEquals("/foo/bar/baz", prefixList.get(0));
        assertEquals("/foo/bar", prefixList.get(1));
        assertEquals("/foo", prefixList.get(2));
        assertEquals("/", prefixList.get(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathPrefixesInvalidPath() {
        Utils.getPathPrefixes("not-a-valid-path");
    }

    @Test
    public void testGetPathPrefix() {
        // /foo/bar/baz ---> /foo/bar/
        String path = "/foo/bar/baz";
        String prefix = Utils.getPathPrefix(path);
        assertEquals("/foo/bar", prefix);

        // /
        path = "/";
        prefix = Utils.getPathPrefix(path);
        assertEquals("", prefix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathPrefixInvalidPath() {
        Utils.getPathPrefix("not-a-valid-path");
    }

    @Test
    public void testGetPathSuffix() {
        // /foo/bar/baz ---> /foo/bar/
        String path = "/foo/bar/baz";
        String prefix = Utils.getPathSuffix(path);
        assertEquals("baz", prefix);

        // /
        path = "/";
        prefix = Utils.getPathSuffix(path);
        assertEquals("", prefix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathSuffixInvalidPath() {
        Utils.getPathSuffix("not-a-valid-path");
    }

    @Test
    public void testWriteDocumentTo() throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");

        root.addElement("author").addAttribute("name", "James")
                .addAttribute("location", "UK").addText("James Strachan");

        root.addElement("author").addAttribute("name", "Bob")
                .addAttribute("location", "US").addText("Bob McWhirter");

        File temp = File.createTempFile("UtilsTest_writeDocumentTo", ".xml");
        temp.deleteOnExit();
        Utils.writeDocumentTo(document, temp);
        StringWriter sw = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("dummy.xml"), sw);
        String expected = sw.toString();
        String actual = FileUtils.readFileToString(temp, "utf-8");
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckKNIMENodeName() {
        assertFalse(Utils.checkKNIMENodeName(""));
        assertFalse(Utils.checkKNIMENodeName("1knimeNode"));
        assertFalse(Utils.checkKNIMENodeName("Knime_Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime-Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime#Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime+Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime:Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime;Node"));
        assertFalse(Utils.checkKNIMENodeName("Knime.Node"));

        assertTrue(Utils.checkKNIMENodeName("KnimeNode"));
        assertTrue(Utils.checkKNIMENodeName("knimeNode"));
    }

    @Test
    public void testFixKNIMENodeName() {
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime_Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime-Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime#Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime+Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime:Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime;Node"));
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("Knime.Node"));

        // nothing to fix
        assertEquals("KnimeNode", Utils.fixKNIMENodeName("KnimeNode"));
        assertEquals("AnotherKnimeNode",
                Utils.fixKNIMENodeName("AnotherKnimeNode"));
    }

}
