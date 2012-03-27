package org.ballproject.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildBinaryResourcesDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildIconsDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildKnimeDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildKnimeNodesDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildPackageRootDirectory;
import org.ballproject.knime.nodegeneration.model.directories.build.NodesBuildSrcDirectory;

/**
 * {@link Directory} where the creation of the KNIME nodes occurs.
 * 
 * @author bkahlert
 * 
 */
public class NodesBuildDirectory extends Directory {

	private static final long serialVersionUID = -2772836144406225644L;
	private NodesBuildIconsDirectory iconsDirectory = null;
	private NodesBuildSrcDirectory srcDirectory = null;
	private NodesBuildPackageRootDirectory packageRootDirectory = null;
	private NodesBuildKnimeDirectory knimeDirectory = null;
	private NodesBuildKnimeNodesDirectory knimeNodesDirectory = null;
	private NodesBuildBinaryResourcesDirectory binaryResourcesDirectory = null;
	private File buildProperties;
	private File pluginXml;
	private File manifestMf;
	private File projectFile;

	public NodesBuildDirectory(File buildDir, String packageRoot)
			throws FileNotFoundException {
		super(buildDir);
		init(packageRoot);
	}

	public NodesBuildDirectory(String packageRoot) throws FileNotFoundException {
		super("GKN-pluginsource");
		init(packageRoot);
	}

	private void init(String packageRoot) throws FileNotFoundException {
		String packageRootPath = packageRoot.replace('.', File.separatorChar);

		new File(this, "icons").mkdirs();
		new File(this, "src" + File.separator + packageRootPath
				+ File.separator + "knime" + File.separator + "nodes"
				+ File.separator + "binres").mkdirs();
		new File(this, "META-INF").mkdirs();

		this.iconsDirectory = new NodesBuildIconsDirectory(new File(this,
				"icons"));

		this.srcDirectory = new NodesBuildSrcDirectory(new File(this, "src"));

		this.packageRootDirectory = new NodesBuildPackageRootDirectory(
				new File(this.srcDirectory, packageRootPath));

		this.knimeDirectory = new NodesBuildKnimeDirectory(new File(
				this.packageRootDirectory, "knime"));

		this.knimeNodesDirectory = new NodesBuildKnimeNodesDirectory(new File(
				this.knimeDirectory, "nodes"));

		this.binaryResourcesDirectory = new NodesBuildBinaryResourcesDirectory(
				new File(this.knimeNodesDirectory, "binres"));

		this.buildProperties = new File(this, "build.properties");
		this.pluginXml = new File(this, "plugin.xml");
		this.manifestMf = new File(this, "META-INF" + File.separator
				+ "MANIFEST.MF");

		this.projectFile = new File(this, ".project");
	}

	/**
	 * Returns the directory where to put all icons in.
	 * <p>
	 * e.g. /tmp/372/icons
	 * 
	 * @return
	 */
	public NodesBuildIconsDirectory getIconsDirectory() {
		return iconsDirectory;
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
	 * Returns the source directory where to put all KNIME classes.
	 * <p>
	 * e.g. /tmp/372/src/de/fu_berlin/imp/seqan/knime
	 * 
	 * @return
	 */
	public NodesBuildKnimeDirectory getKnimeDirectory() {
		return knimeDirectory;
	}

	/**
	 * Returns the source directory where to put all KNIME node classes.
	 * <p>
	 * e.g. /tmp/372/src/de/fu_berlin/imp/seqan/knime/nodes
	 * 
	 * @return
	 */
	public NodesBuildKnimeNodesDirectory getKnimeNodesDirectory() {
		return knimeNodesDirectory;
	}

	/**
	 * Returns the source directory where to put all binary resources like the
	 * shipped executables.
	 * <p>
	 * e.g. /tmp/372/src/de/fu_berlin/imp/seqan/knime/nodes/binres
	 * 
	 * @return
	 */
	public NodesBuildBinaryResourcesDirectory getBinaryResourcesDirectory() {
		return binaryResourcesDirectory;
	}

	public File getBuildProperties() {
		return buildProperties;
	}

	public File getPluginXml() {
		return pluginXml;
	}

	public File getManifestMf() {
		return manifestMf;
	}

	public File getProjectFile() {
		return projectFile;
	}
}
