package com.genericworkflownodes.knime.nodegeneration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator.NodeGeneratorException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.util.SanityCheck;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

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
    	
        ArgumentParser parser = ArgumentParsers.newFor("GenericKNIMENodes project generator").build()
                .defaultHelp(true)
                .description("Generate tycho-enabled Eclipse RCP projects for use with the GenericKNIMENodes KNIME extension"
                		+ " from a directory layout.");
        parser.addArgument("-i", "--input").type(String.class)
                .help("Input folder with specific directory layout.");
        parser.addArgument("-o", "--output").type(String.class)
                .help("Output folder into which to put the generated projects.");
        parser.addArgument("-d", "--date").required(false).type(String.class).setDefault("")
        		.help("Last change date to use for the last version part (.qualifier/.SNAPSHOT). It will use the newest one of this and the"
        				+ " qualifiers in potential contributing plugins.");
        parser.addArgument("-t", "--testingFeatures").setDefault(Boolean.FALSE).type(Boolean.class).required(false)
        		.help("Generate a testing feature for each feature?");
        parser.addArgument("-r", "--recursive").setDefault(Boolean.FALSE).type(Boolean.class).required(false)
        		.help("Recursively generates features for every folder in the input folder. Recurses only one level deep.");
        parser.addArgument("-u", "--createUpdateSite").setDefault(Boolean.FALSE).type(Boolean.class).required(false)
			.help("Create an update site containing all generated features.");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        
        File srcDir = new File(ns.getString("input")).getAbsoluteFile().getCanonicalFile();
        File buildDir = new File(ns.getString("output")).getAbsoluteFile().getCanonicalFile();

        //TODO ACTUALLY you should have the option of an own qualifier,
        // a qualifier replaceable by buckminster ("qualifier") or tycho ("SNAPSHOT")
        // and no qualifier

        String lastChangeDate = ns.getString("date");

        // Caution: this is in beta stage. It does not collect anything yet.
        boolean createTestingFeature = ns.getBoolean("testingFeatures").booleanValue();
        boolean recursive = ns.getBoolean("recursive").booleanValue();
        boolean createUpdateSite = ns.getBoolean("createUpdateSite").booleanValue();

        try {
            NodeGenerator nodeGenerator = 
            		new NodeGenerator(srcDir, buildDir, lastChangeDate, createTestingFeature, recursive, createUpdateSite);
            nodeGenerator.generate();
        } catch (NodeGeneratorException e) {
            e.printStackTrace();
        } catch (PathnameIsNoDirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
