package org.ballproject.knime.nodegeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ballproject.knime.base.util.Helper;
import org.ballproject.knime.base.util.ToolRunner;
import org.ballproject.knime.nodegeneration.model.directories.NodesSourceDirectory;
import org.ballproject.knime.nodegeneration.model.directories.source.ExecutablesDirectory;
import org.eclipse.core.commands.ExecutionException;

public class NodeDescriptionGenerator {

	private static Logger logger = Logger
			.getLogger(NodeDescriptionGenerator.class.getCanonicalName());

	/**
	 * Checks whether ctd files should be generated dynamically.
	 * <p>
	 * If ctd files should be generated automatically all executables in
	 * <code>{@link NodesSourceDirectory#getExecutablesDirectory()}/bin</code>
	 * are called with the given flag.
	 * <p>
	 * Eventually created ctd files can be found in
	 * {@link NodesSourceDirectory#getExecutablesDirectory()};
	 * 
	 * @param srcDir
	 * @return true if ctd files where generated; false otherwise
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	public static boolean createCTDsIfNecessary(NodesSourceDirectory srcDir)
			throws FileNotFoundException, IOException, ExecutionException {
		if (srcDir.getDescriptorsDirectory() != null) {
			if (srcDir.getExecutablesDirectory() != null) {
				logger.log(
						Level.WARNING,
						"Both directories \""
								+ srcDir.getDescriptorsDirectory().getPath()
								+ "\" and \""
								+ srcDir.getExecutablesDirectory()
								+ "\" exists. The latter will be ignored and the provided *.ctd files will be used.");
			} else {
				return false;
			}
		} else {
			if (srcDir.getExecutablesDirectory() == null)
				throw new FileNotFoundException("Neither the directory \""
						+ srcDir.getDescriptorsDirectory().getPath()
						+ "\" nor \""
						+ srcDir.getExecutablesDirectory().getPath()
						+ "\" exists.");

			generateDescriptors(srcDir.getExecutablesDirectory(), srcDir
					.getProperties().getProperty("parswitch", "-write_par"));
			return true;
		}
		return false;
	}

	/**
	 * Creates a ctd file for each binary found in the given
	 * {@link ExecutablesDirectory} in a temporary directory by calling each
	 * binary with the given switch (e.g. <code>-ctd-write</code>).
	 * 
	 * @param executablesDirectory
	 * @param ctdWriteSwitch
	 * @throws IOException
	 * @throws ExecutionException
	 */
	public static void generateDescriptors(
			ExecutablesDirectory executablesDirectory, String ctdWriteSwitch)
			throws IOException, ExecutionException {

		String[] exes = executablesDirectory.getBin().list();

		if (exes.length == 0)
			throw new FileNotFoundException(
					"Could not find any executables in " + executablesDirectory);

		for (String exe : exes) {
			ToolRunner tr = new ToolRunner();
			File outfile = File.createTempFile("CTD", "");
			outfile.deleteOnExit();

			// FIXME: this is so *nix style, wont hurt on windows
			// but probably wont help either
			tr.addEnvironmentEntry("LD_LIBRARY_PATH", new File(
					executablesDirectory, "lib").getAbsolutePath());

			String cmd = executablesDirectory.getBin().getAbsolutePath()
					+ File.separator + exe + " " + ctdWriteSwitch + " "
					+ outfile.getAbsolutePath();
			try {
				tr.run(cmd);

				if (tr.getReturnCode() != 0) {
					Helper.copyFile(outfile,
							executablesDirectory.getCTD(outfile.getName()));
				} else
					throw new ExecutionException("Tool \"" + cmd
							+ "\" returned with " + tr.getReturnCode());
			} catch (Exception e) {
				throw new ExecutionException("Could not execute tool: " + cmd,
						e);
			}
		}
	}
}
