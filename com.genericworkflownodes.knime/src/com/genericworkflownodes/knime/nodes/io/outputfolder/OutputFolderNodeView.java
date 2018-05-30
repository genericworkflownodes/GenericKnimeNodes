package com.genericworkflownodes.knime.nodes.io.outputfolder;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;

import org.knime.core.node.NodeView;
import org.knime.core.util.FileUtil;

/**
 * <code>NodeView</code> for the "OutputFolder" Node. Writes all the incoming
 * files to the given output folder.
 * 
 * @author The GKN Team
 */
public class OutputFolderNodeView extends NodeView<OutputFolderNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel
     *            The model (class: {@link OutputFolderNodeModel})
     */
    protected OutputFolderNodeView(final OutputFolderNodeModel nodeModel) {
        super(nodeModel);
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        try {
            setShowNODATALabel(true);
            openFolder();
        } catch (IOException e) {
            getLogger().error(
                    "Could not open the folder for the selected output files.");
            getLogger().error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void openFolder() throws IOException {
        String f_name = getNodeModel().m_foldername.getStringValue();
        try {
            File f = FileUtil.resolveToPath(FileUtil.toURL(f_name)).toFile();
            Desktop.getDesktop().open(f);
        } catch (InvalidPathException | IOException | URISyntaxException e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        try {
            setShowNODATALabel(true);
            openFolder();
        } catch (IOException e) {
            getLogger().error(
                    "Could not open the folder for the selected output files.");
            getLogger().error(e.getMessage());
            e.printStackTrace();
        }
    }

}
