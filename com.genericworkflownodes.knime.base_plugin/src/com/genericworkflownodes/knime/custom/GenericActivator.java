package com.genericworkflownodes.knime.custom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.external.ExternalToolDB;
import org.ballproject.knime.base.external.ExternalTool;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.ballproject.knime.base.model.TempDirectory;
import org.ballproject.knime.base.util.ZipUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.data.url.MIMEType;
import org.osgi.framework.BundleContext;

public abstract class GenericActivator extends AbstractUIPlugin {
	private static final Logger LOGGER = Logger
			.getLogger(GenericActivator.class.getCanonicalName());

	private static GenericActivator plugin;

	private Properties props = new Properties();
	private Map<String, String> env = new HashMap<String, String>();

	public GenericActivator() {
		super();
		plugin = this;
	}

	public static GenericActivator getDefault() {
		return plugin;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public void extractBinaries(Class<?> binaryLocation) throws IOException {
		TempDirectory nodeBinariesDir = new TempDirectory(this.getBundle()
				.getSymbolicName());
		String os = System.getProperty("os.name");

		props.load(this.getClass().getResourceAsStream("plugin.properties"));
		if (GenericNodesPlugin.isDebug()) {
			GenericNodesPlugin
					.log("com.genericworkflownodes.sample.copyfasta plugin properties are ... ");
			for (Object key : props.keySet()) {
				GenericNodesPlugin.log(key + " -> " + props.get(key));
			}
		}

		// default platform
		String OS = "win";

		if (os.toLowerCase().contains("nux")
				|| os.toLowerCase().contains("nix")) {
			OS = "lnx";
		}
		if (os.toLowerCase().contains("mac")) {
			OS = "mac";
		}

		// get word size of JVM as a proxy for native word size of OS
		String data_model = System.getProperty("sun.arch.data.model");
		if (!data_model.equals("64") && !data_model.equals("32"))
			LOGGER.warning("Unexpected architecure detected: " + data_model
					+ "; falling back to 32 bit");

		boolean use64 = false;
		boolean use32 = false;
		if (data_model.equals("64")) {
			use64 = tryExtractPayloadZIP(binaryLocation, nodeBinariesDir, OS,
					"64");
		}
		if (!use64) {
			use32 = tryExtractPayloadZIP(binaryLocation, nodeBinariesDir, OS,
					"32");
		}

		if (use32 || use64) {
			makeExtractedBinariesExecutable(nodeBinariesDir);
		} else {
			LOGGER.info("No binaries could be found. "
					+ "In order to execute the containing nodes you need "
					+ "to configure their binary locations in the Eclipse configuration.");
		}

		final IPreferenceStore pStore = this.getPreferenceStore();
		pStore.setValue("binaries_path", nodeBinariesDir.getCanonicalPath());

		for (Object key : props.keySet()) {
			String k = key.toString();
			String v = props.getProperty(k);
			env.put(k, v);
		}
	}

	/**
	 * @param nodeBinariesDir
	 * @throws FileNotFoundException
	 */
	private void makeExtractedBinariesExecutable(TempDirectory nodeBinariesDir)
			throws FileNotFoundException {
		try {
			for (File execFile : new File(nodeBinariesDir, "bin").listFiles()) {
				execFile.setExecutable(true);
			}
		} catch (NullPointerException e) {
			throw new FileNotFoundException(
					"No \"bin\" directory was found in the shipped binaries. "
							+ "Please make sure your binary zip file contains a \"bin\" directory.\n"
							+ "See payload.README for further instructions.");
		}
	}

	/**
	 * Tests if a zip file with the name binaries_{@p OS}_{@p data_model}.zip is
	 * available and extracts it.
	 * 
	 * @param binaryLocation
	 *            Class which is needed to find the binaries inside the jar
	 * @param nodeBinariesDir
	 *            Target directory where it should be extracted to
	 * @param OS
	 *            Identifier of the operating system to extract the appropriate
	 *            zip file
	 * @return true if the specified zip file was found and extracted correctly.
	 * @throws IOException
	 */
	private boolean tryExtractPayloadZIP(Class<?> binaryLocation,
			TempDirectory nodeBinariesDir, String OS, String data_model)
			throws IOException {
		if (binaryLocation.getResourceAsStream("binaries_" + OS + "_"
				+ data_model + ".zip") != null) {
			ZipUtils.decompressTo(
					nodeBinariesDir,
					binaryLocation.getResourceAsStream("binaries_" + OS + "_"
							+ data_model + ".zip"));
			props.load(binaryLocation.getResourceAsStream("binaries_" + OS
					+ "_" + data_model + ".ini"));
			return true;
		} else {
			return false;
		}
	}

	public void registerMimeTypes(List<MIMEType> mimeTypes) {
		MIMEtypeRegistry registry = GenericNodesPlugin.getMIMEtypeRegistry();

		for (MIMEType mimeType : mimeTypes) {
			registry.registerMIMEtype(mimeType);
		}

		ExternalToolDB toolDB = ExternalToolDB.getInstance();
		String packageName = this.getClass().getPackage().getName();
		String knimelessPackageName = packageName.substring(0,
				packageName.lastIndexOf(".knime"));
		for (String nodeName : this.getNodeNames()) {
			toolDB.registerTool(new ExternalTool(knimelessPackageName, nodeName));
		}

		IPreferenceStore store = this.getPreferenceStore();
		ExternalToolDB.getInstance().init(store);
	}

	public Properties getProperties() {
		return this.props;
	}

	public Map<String, String> getEnvironment() {
		return this.env;
	}

	public abstract List<String> getNodeNames();
}
