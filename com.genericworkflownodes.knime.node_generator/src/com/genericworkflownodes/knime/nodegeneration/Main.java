package com.genericworkflownodes.knime.nodegeneration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator.NodeGeneratorException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.util.SanityCheck;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(NodeGenerator.class
            .getCanonicalName());

    /**
     * Note: Creates Java sources for update sites, features, plugins, fragments,
     *       based on the folder structure, contributing plugins, tool descriptors.
     * 		 The built plugin will neither be compiled nor be packaged to a jar file.
     *       Please run Maven tycho (with the tycho-pomless extension) afterwards to do that.
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
        		.help("Allows to change the last version part at generation instead of build time."
        				+ "Use version format '$major.$minor.$patch.genqualifier' in your feature/plugin.properties to replace 'genqualifier' with this."
        				+ "It will use the newest one of this and the qualifiers in potential contributing plugins. You can"
        				+ "also use the usual 'qualifier' to be replace at tycho build time later. Or just hardcode a qualifier"
        				+ "usually in the 'yyyyMMddHHmmss' format.");
        parser.addArgument("-t", "--testingFeatures").action(Arguments.storeTrue()).required(false)
        		.help("Generate a testing feature for each feature?");
        parser.addArgument("-r", "--recursive").action(Arguments.storeTrue()).required(false)
        		.help("Recursively generates features for every folder in the input folder. Recurses only one level deep.");
        parser.addArgument("-u", "--createUpdateSite").action(Arguments.storeTrue()).type(Boolean.class).required(false)
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

        String lastChangeDate = ns.getString("date");
        if (lastChangeDate.isEmpty())
        {
	        LocalDateTime now = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        lastChangeDate = now.format(formatter);
        }

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
