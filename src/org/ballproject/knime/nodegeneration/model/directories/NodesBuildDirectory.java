package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildPackageRootDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildSrcDirectory;

/**
 * {@link Directory} where the creation of the KNIME nodes occurs.
 * 
 * @author bkahlert
 * 
 */
public class NodesBuildDirectory extends TempDirectory {

	private static final long serialVersionUID = -2772836144406225644L;
	private NodesBuildSrcDirectory srcDirectory = null;
	private NodesBuildPackageRootDirectory packageRootDirectory = null;
	private NodesBuildKnimeNodesDirectory nodesBuildKnimeNodesDirectory = null;
	private File pluginXml;
	private File manifestMf;

	public NodesBuildDirectory(String packageRoot) throws FileNotFoundException {
		super("GKN-pluginsource");

		String packageRootPath = packageRoot.replace('.', File.separatorChar);

		new File(this, "src" + File.separator + packageRootPath
				+ File.separator + "knime" + File.separator + "nodes").mkdirs();
		new File(this, "META-INF").mkdirs();

		this.srcDirectory = new NodesBuildSrcDirectory(new File(this, "src"));

		this.packageRootDirectory = new NodesBuildPackageRootDirectory(
				new File(this.srcDirectory, packageRootPath));

		this.nodesBuildKnimeNodesDirectory = new NodesBuildKnimeNodesDirectory(
				new File(new File(this.packageRootDirectory, "knime"), "nodes"));

		this.pluginXml = new File(this, "plugin.xml");
		this.manifestMf = new File(this, "META-INF" + File.separator
				+ "plugin.xml");
	}

	/**
	 * Returns the directory where to put all sources in.
	 * <p>
	 * e.g. /tmp/372/src
	 * 
	 * @return
	 */
	public NodesBuildSrcDirectory getSrcDirectory() {
		return srcDirectory;
	}

	/**
	 * Returns the source directory where the package root resides.
	 * <p>
	 * e.g. /tmp/372/src/de/fu_berlin/imp/seqan
	 * 
	 * @return
	 */
	public NodesBuildPackageRootDirectory getPackageRootDirectory() {
		return packageRootDirectory;
	}

	/**
	 * Returns the source directory where to put all KNIME node classes.
	 * <p>
	 * e.g. /tmp/372/src/de/fu_berlin/imp/seqan/knime/nodes
	 * 
	 * @return
	 */
	public NodesBuildKnimeNodesDirectory getKnimeNodesDirectory() {
		return nodesBuildKnimeNodesDirectory;
	}

	public File getPluginXml() {
		return pluginXml;
	}

	public File getManifestMf() {
		return manifestMf;
	}
}
