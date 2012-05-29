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
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.ballproject.knime.base.model.TempDirectory;
import org.ballproject.knime.base.util.ZipUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.data.url.MIMEType;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolFinderService;
import com.genericworkflownodes.knime.toolfinderservice.IToolFinderService.ToolPathType;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolFinder;

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
	}

	/**
	 * Registers all nodes included in the plugin as external tools in the
	 * PluginPreferenceToolFinder.
	 * 
	 * @see com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolFinder
	 */
	public void registerNodes() {
		// TODO: get reference from service API
		PluginPreferenceToolFinder toolFinder = PluginPreferenceToolFinder
				.getInstance();

		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		PluginPreferenceToolFinder.getInstance().init(store);

		String knimelessPackageName = getKNIMELessPackageName();

		for (String nodeName : this.getNodeNames()) {
			toolFinder.registerTool(new ExternalTool(knimelessPackageName,
					nodeName));
		}

		// registerExtractedBinaries
		registerExtractedBinaries();
	}

	/**
	 * Extracts the package name from the plugin data.
	 * 
	 * @return
	 */
	private String getKNIMELessPackageName() {
		String packageName = this.getClass().getPackage().getName();
		String knimelessPackageName = packageName.substring(0,
				packageName.lastIndexOf(".knime"));
		return knimelessPackageName;
	}

	/**
	 * Adds all extracted binaries to the tool registry.
	 */
	private void registerExtractedBinaries() {

		IToolFinderService toolFinder = PluginPreferenceToolFinder
				.getInstance();

		// get binary path
		String binaryPath = this.getPreferenceStore()
				.getString("binaries_path");
		// we manually add the "bin" here since the binaries_path is pointing to
		// the top-level directory where all content of the payload zip was
		// extracted
		File binaryDirectory = new File(binaryPath, "bin");

		// abort execution if we do not have a valid binary directory
		if (!binaryDirectory.exists())
			return;

		// get package name
		String knimelessPackageName = getKNIMELessPackageName();

		// for each node find the executable
		for (String node : getNodeNames()) {
			File executable = getExecutableName(binaryDirectory, node);
			if (executable != null) {
				ExternalTool currentNode = new ExternalTool(
						knimelessPackageName, node);
				// register executalbe in the ToolFinder
				toolFinder.setToolPath(currentNode, executable,
						ToolPathType.SHIPPED);

				try {
					// check if we need to adjust the type
					if (toolFinder.getConfiguredToolPathType(currentNode) == ToolPathType.UNKNOWN) {
						toolFinder.updateToolPathType(currentNode,
								ToolPathType.SHIPPED);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	private File getExecutableName(File binDir, String nodename) {
		for (String extension : new String[] { "", ".bin", ".exe" }) {
			File binFile = new File(binDir, nodename + extension);
			if (binFile.canExecute())
				return binFile;
		}
		return null;
	}

	public Properties getProperties() {
		return this.props;
	}

	public Map<String, String> getEnvironment() {
		return this.env;
	}

	public abstract List<String> getNodeNames();
}
