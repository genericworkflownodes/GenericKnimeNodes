package com.genericworkflownodes.knime.dynamic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.knime.base.node.util.exttool.ExtToolStderrNodeView;
import org.knime.base.node.util.exttool.ExtToolStdoutNodeView;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeDescription28Proxy;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeView;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.knime.node.v28.FullDescriptionDocument.FullDescription;
import org.knime.node.v28.InPortDocument.InPort;
import org.knime.node.v28.IntroDocument.Intro;
import org.knime.node.v28.KnimeNodeDocument;
import org.knime.node.v28.KnimeNodeDocument.KnimeNode;
import org.knime.node.v28.OptionDocument.Option;
import org.knime.node.v28.OutPortDocument.OutPort;
import org.knime.node.v28.PortsDocument.Ports;
import org.knime.node.v28.ViewDocument.View;
import org.knime.node.v28.ViewsDocument.Views;
import org.osgi.framework.Version;
import org.w3c.dom.Document;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.CTDConfigurationReader;
import com.genericworkflownodes.knime.config.reader.InvalidCTDFileException;
import com.genericworkflownodes.knime.custom.config.BinaryManager;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration.VersionDisplayLayer;
import com.genericworkflownodes.knime.generic_node.GenericKnimeNodeDialog;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;

/**
 * <code>NodeFactory</code> for Generic KNIME Nodes that are loaded dynamically.
 *
 * @author Fillbrunn, Alexander
 */
public abstract class DynamicGenericNodeFactory extends GenericNodeFactory {

    /**
     * The configuration key for the ctd file.
     */
    public static final String CTD_FILE_CFG_KEY = "ctdFile";

    /**
     * The configuration key for the node id.
     */
    public static final String ID_CFG_KEY = "id";

    /**
     * The configuration key for the node set factory id.
     */
    public static final String NSFID_CFG_KEY = "nsfid";

    /**
     * The key for storing whether the factory is deprecated.
     */
    public static final String DEPRECATION_CFG_KEY = "deprecated";

    private static final NodeLogger logger = NodeLogger.getLogger(DynamicGenericNodeFactory.class);

    private String m_filename;
    private String m_nsfid;
    private String m_id;
    private String m_ctdFile;
    private INodeConfiguration m_config;
    private boolean m_deprecated;

    /**
     * @return The icon path relative to the payload folder.
     */
    @Override
    protected String getIconPath() {
        return "";
    }

    /**
     * @return the file name of the tool.
     */
    public String getFileName() {
        return m_filename;
    }

    /**
     * @return the tool ID.
     */
    @Override
    public String getId() {
        return m_id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DynamicGenericNodeModel createNodeModel() {
        INodeConfiguration tmpConfig;
        try {
            tmpConfig = getNodeConfiguration();
            
            String[][] inputs = new String[tmpConfig.getInputPorts().size()][];
            String[][] outputs = new String[tmpConfig.getOutputPorts().size()][];
            int i = 0;
            for (Port p : tmpConfig.getInputPorts()) {
                inputs[i++] = p.getMimeTypes().toArray(new String[0]);
            }
            i = 0;
            for (Port p : tmpConfig.getOutputPorts()) {
                outputs[i++] = p.getMimeTypes().toArray(new String[0]);
            }
            
            return new DynamicGenericNodeModel(tmpConfig, getPluginConfig(), inputs, outputs);
        } catch (Exception e) {
            logger.error("Dynamic node model instantiation failed", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        // Stderr and Stdout
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<DynamicGenericNodeModel> createNodeView(final int viewIndex,
            final DynamicGenericNodeModel nodeModel) {
        if (viewIndex == 0) {
            return new ExtToolStdoutNodeView<DynamicGenericNodeModel>(nodeModel);
        } else if (viewIndex == 1) {
            return new ExtToolStderrNodeView<DynamicGenericNodeModel>(nodeModel);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        try {
            return new GenericKnimeNodeDialog(getNodeConfiguration());
        } catch (Exception e) {
            logger.error("Dynamic node view instantiation failed", e);
        }
        return null;
    }
    
    @Override
    public void loadAdditionalFactorySettings(ConfigRO config)
            throws InvalidSettingsException {
        m_ctdFile = config.getString(CTD_FILE_CFG_KEY);
        m_filename = FilenameUtils.removeExtension(Paths.get(m_ctdFile).getFileName().toString());
        m_id = config.getString(ID_CFG_KEY);
        m_nsfid = config.getString(NSFID_CFG_KEY);
        try {
            m_deprecated = VersionedNodeSetFactoryManager.isFactoryDeprecated(m_nsfid);
        } catch (InterruptedException e) {
            // This means that loading the nodes was interrupted.
            // We simply leave the node non-deprecated.
            logger.error("Could not load additional factory settings.", e);
        }
        super.loadAdditionalFactorySettings(config);
    }
    
    @Override
    public void saveAdditionalFactorySettings(ConfigWO config) {
        config.addString(CTD_FILE_CFG_KEY, m_ctdFile);
        config.addString(ID_CFG_KEY, m_id);
        config.addString(NSFID_CFG_KEY, m_nsfid);
        super.saveAdditionalFactorySettings(config);
    }

    @Override
    protected NodeDescription createNodeDescription() {
        try {
            INodeConfiguration cfg = getNodeConfiguration();
            KnimeNodeDocument doc = org.knime.node.v28.KnimeNodeDocument.Factory.newInstance();
            Document domDoc = (Document)doc.getDomNode();

            // Node
            KnimeNode node = doc.addNewKnimeNode();
            node.setDeprecated(m_deprecated);

            // Add version to the node description if set in plug-in properties.

            if (getPluginConfig()
                    .getVersionDisplayLayer() == VersionDisplayLayer.NODE) {
                Version version = getPluginConfig().getRawPluginVersion();
                String node_version = version.getMajor() + "."
                        + version.getMinor() + "." + version.getMicro();

                if (version.getQualifier().contains("nightly")) {
                    node_version = node_version
                            .concat("." + version.getQualifier());
                }

                node.setName(cfg.getName() + " [" + node_version + "]");
            } else {
                node.setName(cfg.getName());
            }
            String iconPath = getIconPath();
            if (!getPluginConfig().getBinaryManager().fileExists(iconPath)) {
                logger.debug("Icon for tool " + getId() + " not found.");
                iconPath = "";
            } else {
                iconPath = "/" + BinaryManager.BUNDLE_PATH + "/" + iconPath;
            }
            node.setIcon(iconPath);
            node.setType(KnimeNode.Type.MANIPULATOR);
            
            node.setShortDescription(cfg.getDescription());
            Views views = node.addNewViews();
            View v0 = views.addNewView();
            v0.setName("Standard output");
            v0.setIndex(new BigInteger("0"));
            v0.getDomNode().appendChild(domDoc.createTextNode("The output channel of the tool as seen on the command line."));
            View v1 = views.addNewView();
            v1.setName("Standard error");
            v1.setIndex(new BigInteger("1"));
            v1.getDomNode().appendChild(domDoc.createTextNode("The error channel of the tool as seen on the command line."));
            
            FullDescription fullDescr = node.addNewFullDescription();
            
            // Intro
            Intro intro = fullDescr.addNewIntro();
            intro.addNewP().getDomNode().appendChild(domDoc.createTextNode(cfg.getManual()));
            
            // Ports
            ArrayList<String> portParamsToSkip = new ArrayList<String>();
            Ports ports = node.addNewPorts();
            int index = 0;
            for (Port p : cfg.getInputPorts()) {
                portParamsToSkip.add(p.getName());
                InPort ip = ports.addNewInPort();
                ip.setIndex(new BigInteger(Integer.toString(index++)));
                String mimetypes = mimetypes2String(p.getMimeTypes());
                ip.setName(p.getName() + mimetypes);
                ip.getDomNode().appendChild(domDoc.createTextNode(p.getDescription() + mimetypes));
            }
            
            index = 0;
            for (Port p : cfg.getOutputPorts()) {
                portParamsToSkip.add(p.getName());
                OutPort op = ports.addNewOutPort();
                op.setIndex(new BigInteger(Integer.toString(index++)));
                String mimetypes = mimetypes2String(p.getMimeTypes());
                String optional = p.isOptional() ? "(Optional) " : "";
                op.setName(optional + p.getName() + mimetypes);
                op.getDomNode().appendChild(domDoc.createTextNode(p.getDescription() + mimetypes));
            }
            
            // Options (skip ports)
            for (Parameter<?> p : cfg.getParameters()) {
                if (!portParamsToSkip.contains(p.getKey()))
                {
                    Option option = fullDescr.addNewOption();
                    option.setName(p.getKey());
                    option.getDomNode().appendChild(domDoc.createTextNode(p.getDescription()));
                }
            }
            
            return new NodeDescription28Proxy(doc);
        } catch (Exception e) {
            logger.error("Dynamic node description instantiation failed", e);
        }
        return null;
    }
    
    private INodeConfiguration getNodeConfiguration()
            throws InvalidCTDFileException, FileNotFoundException, IOException {
        if (m_config == null) {
            try(InputStream cfgStream = getConfigAsStream()) {
                m_config = new CTDConfigurationReader().read(cfgStream);
            }
        }
        return m_config;
    }
    
    private InputStream getConfigAsStream() throws FileNotFoundException {
        return new FileInputStream(getPluginConfig().getBinaryManager().resolveToolDescriptorPath(m_ctdFile));
    }
    
    private String mimetypes2String(List<String> mt) {
        StringBuilder mimetypes = new StringBuilder().append(" [");
        for (int i = 0; i < mt.size(); i++) {
            if (i != 0) {
                mimetypes.append(", ");
            }
            mimetypes.append(mt.get(i));
        }
        mimetypes.append("]");
        return mimetypes.toString();
    }
}
