package com.genericworkflownodes.knime.nodegeneration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator.NodeGeneratorException;
import com.genericworkflownodes.knime.nodegeneration.util.SanityCheck;

public class Main {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(NodeGenerator.class
            .getCanonicalName());

    /**
     * 
     * @param args
     *            <li>#1: directory in which the plugin's sources reside;</li>
     *            <li>#2: directory to where to put the plugins (base + payload
     *            fragments)</li>
     *            <li>#3: (optional) last change date, otherwise an unspecified "qualifier" is used</li>
     *            <p>
     *            Note: The built plugin will neither be compiled nor be
     *            packaged to a jar file.
     */
    public static void main(String[] args) throws IOException {
        File srcDir = new File((args.length > 0) ? args[0] : ".")
                .getAbsoluteFile().getCanonicalFile();

        // perform some sanity checks before continuing
        final SanityCheck sanityCheck = new SanityCheck(
                srcDir.getAbsolutePath());
        final Collection<String> warnings = sanityCheck.getWarnings();
        final Collection<String> errors = sanityCheck.getErrors();
        final String newLine = System.getProperty("line.separator");
        if (!warnings.isEmpty()) {
            final StringBuilder builder = new StringBuilder(
                    "Warning! You MIGHT need to fix the following issues:");
            for (final String warning : warnings) {
                builder.append(newLine).append("* ").append(warning);
            }
            builder.append(newLine);
            System.err.println(builder);
        }
        if (!errors.isEmpty()) {
            final StringBuilder builder = new StringBuilder(
                    "Severe error! Node generation cannot continue until the following problems are fixed:");
            for (final String error : errors) {
                builder.append(newLine).append("* ").append(error);
            }
            builder.append(newLine);
            System.err.println(builder);
            // we cannot continue, so abort now
            System.exit(-1);
        }

        File buildDir = (args.length > 1) ? new File(args[1]).getAbsoluteFile()
                .getCanonicalFile() : null;
        if (buildDir != null) {
            buildDir.mkdirs();
        }

        //TODO ACTUALLY you should have the option of an own qualifier,
        // a qualifier replaceable by buckminster ("qualifier") or tycho ("SNAPSHOT")
        // and no qualifier
        
        // check if we have a third argument -> last change date
        String lastChangeDate = "";
        if (args.length > 2) {
            lastChangeDate = args[2];
        }

        // check for a fourth argument if you want to generate a testing feature
        // Caution: this is in beta stage. It does not collect anything yet.
        // see
        boolean createTestingFeature = false;
        if (args.length > 3) {
            createTestingFeature = args[3] == "withTests";
        }

        try {
            NodeGenerator nodeGenerator = new NodeGenerator(srcDir, buildDir,
                    lastChangeDate, createTestingFeature);
            nodeGenerator.generate();
        } catch (NodeGeneratorException e) {
            e.printStackTrace();
        }
    }
}
