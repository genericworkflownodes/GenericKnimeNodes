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
package com.genericworkflownodes.knime.config.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.test.data.TestDataSource;

/**
 * Test for {@link CTDConfigurationReader}.
 * 
 * @author aiche
 */
public class CTDConfigurationReaderTest {

    @Test
    public void testRead() throws Exception {
        CTDConfigurationReader reader = new CTDConfigurationReader();
        assertNotNull(reader);
        INodeConfiguration config = reader.read(TestDataSource.class
                .getResourceAsStream("FeatureLinkerUnlabeled.ctd"));

        assertEquals("Map Alignment", config.getCategory());
        assertNotNull(config.getParameter("FeatureLinkerUnlabeled.1.in"));
        assertTrue(config.getParameter("FeatureLinkerUnlabeled.1.in") instanceof FileListParameter);
        assertNotNull(config.getInputPortByName("FeatureLinkerUnlabeled.1.in"));
        assertEquals("1.11.0", config.getVersion());

        FileListParameter flp = (FileListParameter) config
                .getParameter("FeatureLinkerUnlabeled.1.in");
        assertEquals(0, flp.getValue().size());
    }

    @Test
    public void testReadFileFilter() throws Exception {
        CTDConfigurationReader reader = new CTDConfigurationReader();
        assertNotNull(reader);
        INodeConfiguration config = reader.read(TestDataSource.class
                .getResourceAsStream("FileFilter.ctd"));

        assertEquals("File Handling", config.getCategory());
        assertNotNull(config.getParameter("FileFilter.1.in"));
        assertTrue(config.getParameter("FileFilter.1.in") instanceof FileParameter);
        assertNotNull(config.getInputPortByName("FileFilter.1.in"));
        assertEquals("1.11.0", config.getVersion());

        FileParameter fp = (FileParameter) config
                .getParameter("FileFilter.1.in");
        assertNull(fp.getValue());

        // get list restrictions
        assertNotNull(config.getParameter("FileFilter.1.peak_options.level"));
        assertTrue(config.getParameter("FileFilter.1.peak_options.level") instanceof IntegerListParameter);
        IntegerListParameter ilp = (IntegerListParameter) config
                .getParameter("FileFilter.1.peak_options.level");
        assertEquals(Integer.valueOf(1), ilp.getLowerBound());
    }

    @Test
    public void testReadTMTAnalyzer() throws Exception {
        CTDConfigurationReader reader = new CTDConfigurationReader();
        assertNotNull(reader);
        INodeConfiguration config = reader.read(TestDataSource.class
                .getResourceAsStream("TMTAnalyzer.ctd"));
        assertEquals("Quantitation", config.getCategory());
        assertEquals("1.11.0", config.getVersion());
        assertNotNull(config.getParameter("TMTAnalyzer.1.in"));
        assertTrue(config.getParameter("TMTAnalyzer.1.in") instanceof FileParameter);

        assertTrue(config
                .getParameter("TMTAnalyzer.1.algorithm.Extraction.channel_active") instanceof StringListParameter);
        StringListParameter slp = (StringListParameter) config
                .getParameter("TMTAnalyzer.1.algorithm.Extraction.channel_active");
        assertEquals(2, slp.getValue().size());
        assertEquals("126:liver", slp.getValue().get(0));
        assertEquals("131:lung", slp.getValue().get(1));
        assertTrue(config
                .getParameter(
                        "TMTAnalyzer.1.algorithm.Quantification.isotope_correction.tmt-6plex")
                .isAdvanced());
        assertTrue(config
                .getParameter(
                        "TMTAnalyzer.1.algorithm.Quantification.isotope_correction.tmt-6plex")
                .isOptional());
        assertFalse(config.getParameter("TMTAnalyzer.1.in").isAdvanced());
        assertFalse(config.getParameter("TMTAnalyzer.1.in").isOptional());
    }

    @Test
    public void testGKNIgnoreTag() throws Exception {
        CTDConfigurationReader reader = new CTDConfigurationReader();
        assertNotNull(reader);
        INodeConfiguration config = reader.read(TestDataSource.class
                .getResourceAsStream("test_app.ctd"));
        assertEquals("SeqAn/Testing", config.getCategory());
        assertEquals("http://www.seqan.de", config.getDocUrl());

        FileParameter fp = (FileParameter) config.getParameter("test_app.out");
        assertNotNull(fp);
        assertEquals("set an output file", fp.getDescription());

        // the parameter following parameters were tagged with gkn-ignore and
        // hence should not be parsed
        assertNull(config.getParameter("test_app.write-ctd-file-ext"));
        assertNull(config.getParameter("test_app.in-file-ext"));
        assertNull(config.getParameter("test_app.out-file-ext"));
    }

    @Test
    public void testSeqAnCTD() throws Exception {
        CTDConfigurationReader reader = new CTDConfigurationReader();
        assertNotNull(reader);
        INodeConfiguration config = reader.read(TestDataSource.class
                .getResourceAsStream("sam2matrix.ctd"));
        assertEquals("Metagenomics", config.getCategory());
        assertEquals("http://www.seqan.de", config.getDocUrl());

        // <ITEM name="out" value="" type="output-file"
        // description="Output file." supported_formats="*.tsv" required="true"
        // advanced="false" />
        FileParameter fp_out = (FileParameter) config
                .getParameter("sam2matrix.out");
        assertNotNull(fp_out);
        assertEquals("Output file.", fp_out.getDescription());
        assertEquals(1, fp_out.getPort().getMimeTypes().size());
        assertEquals("tsv", fp_out.getPort().getMimeTypes().get(0));
        assertFalse(fp_out.isOptional());
        assertFalse(fp_out.isAdvanced());

        // <ITEMLIST name="mapping" type="input-file"
        // description="File containing the mappings." supported_formats="*.sam"
        // required="true" advanced="false" >
        FileListParameter flp_mapping = (FileListParameter) config
                .getParameter("sam2matrix.mapping");
        assertNotNull(flp_mapping);
        assertEquals("File containing the mappings.",
                flp_mapping.getDescription());
        assertEquals(1, flp_mapping.getPort().getMimeTypes().size());
        assertEquals("sam", flp_mapping.getPort().getMimeTypes().get(0));
        assertFalse(flp_mapping.isOptional());
        assertFalse(flp_mapping.isAdvanced());

        // <ITEM name="reads" value="" type="input-file"
        // description="File containing the reads contained in the mapping file(s)."
        // supported_formats="*.fa,*.fasta,*.fq,*.fastq" required="true"
        // advanced="false" />
        FileParameter fp_reads = (FileParameter) config
                .getParameter("sam2matrix.reads");
        assertNotNull(fp_reads);
        assertEquals(
                "File containing the reads contained in the mapping file(s).",
                fp_reads.getDescription());
        assertEquals(4, fp_reads.getPort().getMimeTypes().size());
        assertEquals("fa", fp_reads.getPort().getMimeTypes().get(0));
        assertEquals("fasta", fp_reads.getPort().getMimeTypes().get(1));
        assertEquals("fq", fp_reads.getPort().getMimeTypes().get(2));
        assertEquals("fastq", fp_reads.getPort().getMimeTypes().get(3));
        assertFalse(fp_reads.isOptional());
        assertFalse(fp_reads.isAdvanced());

        // <ITEMLIST name="reference" type="string"
        // description="Name of the file used as reference of the corresponding sam file. If not specified the names of the mapping files are taken"
        // required="false" advanced="false" >
        StringListParameter slp_reference = (StringListParameter) config
                .getParameter("sam2matrix.reference");
        assertNotNull(slp_reference);
        assertEquals(
                "Name of the file used as reference of the corresponding sam file. If not specified the names of the mapping files are taken",
                slp_reference.getDescription());
        assertTrue(slp_reference.isOptional());
        assertFalse(slp_reference.isAdvanced());

        // the parameter following parameters were tagged with gkn-ignore and
        // hence should not be parsed
        assertNull(config.getParameter("sam2matrix.write-ctd-file-ext"));
        assertNull(config.getParameter("sam2matrix.out-file-ext"));
        assertNull(config.getParameter("sam2matrix.mapping-file-ext"));
        assertNull(config.getParameter("sam2matrix.reads-file-ext"));
    }
}
