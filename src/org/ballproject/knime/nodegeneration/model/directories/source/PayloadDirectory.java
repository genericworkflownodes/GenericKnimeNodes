package org.ballproject.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ballproject.knime.nodegeneration.model.directories.Directory;

public class PayloadDirectory extends Directory {

	private static final long serialVersionUID = -400249694994228712L;

	public PayloadDirectory(File payloadDirectory) throws FileNotFoundException {
		super(payloadDirectory);
	}

	/**
	 * Copies all valid ini and zip files to the specified {@link File
	 * directory}.
	 * 
	 * @param destDir
	 * @throws IOException
	 */
	public void copyPayloadTo(File destDir) throws IOException {
		for (String filename : this.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".ini"))
					return true;
				if (filename.endsWith(".zip")) {
					// verifyZip(destinationFQNNodeDirectory + pathsep +
					// "binres"
					// + pathsep + filename);
					return true;
				}
				return false;
			}
		})) {
			FileUtils.copyFileToDirectory(new File(this, filename), destDir);
		}
	}

	// TODO
	// public static void verifyZip(String filename) {
	// boolean ok = false;
	//
	// Set<String> found_exes = new HashSet<String>();
	//
	// try {
	// ZipInputStream zin = new ZipInputStream(new FileInputStream(
	// filename));
	// ZipEntry ze = null;
	//
	// while ((ze = zin.getNextEntry()) != null) {
	// if (ze.isDirectory()) {
	// // we need a bin directory at the top level
	// if (ze.getName().equals("bin/")
	// || ze.getName().equals("bin")) {
	// ok = true;
	// }
	//
	// } else {
	// File f = new File(ze.getName());
	// if ((f.getParent() != null) && f.getParent().equals("bin")) {
	// found_exes.add(f.getName());
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// if (!ok) {
	// this.panic("binary archive has no toplevel bin directory : "
	// + filename);
	// }
	//
	// for (String nodename : this.node_names) {
	// boolean found = false;
	// if (found_exes.contains(nodename)
	// || found_exes.contains(nodename + ".bin")
	// || found_exes.contains(nodename + ".exe")) {
	// found = true;
	// }
	// if (!found) {
	// this.panic("binary archive has no executable in bin directory for node : "
	// + nodename);
	// }
	// }
	// }

}
