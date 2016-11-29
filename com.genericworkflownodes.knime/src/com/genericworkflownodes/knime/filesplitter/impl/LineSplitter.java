package com.genericworkflownodes.knime.filesplitter.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.genericworkflownodes.knime.filesplitter.Splitter;

public class LineSplitter implements Splitter {

    @Override
    public void split(File input, File... output) throws IOException {
        String line;
        BufferedWriter[] bws = null;
        try (
            InputStream fis = new FileInputStream(input);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
        ) {
            FileWriter[] foss = new FileWriter[output.length];
            bws = new BufferedWriter[output.length];
            int j = 0;
            for (File f : output) {
                foss[j] = new FileWriter(f);
                bws[j] = new BufferedWriter(foss[j]);
                j++;
            }
            int counter = 0;
            int nrParts = output.length;
            while ((line = br.readLine()) != null) {
                bws[counter % nrParts].append(line);
                bws[counter % nrParts].newLine();
                counter++;
            }
        } finally {
            if (bws != null) {
                for (int i = 0; i < bws.length; i++) {
                    if (bws[i] != null) {
                        bws[i].close();
                    }
                }
            }
        }
    }

    @Override
    public void loadSettingsFrom(NodeSettingsRO settings) {

    }

    @Override
    public void saveSettingsTo(NodeSettingsWO settings) {

    }

}
