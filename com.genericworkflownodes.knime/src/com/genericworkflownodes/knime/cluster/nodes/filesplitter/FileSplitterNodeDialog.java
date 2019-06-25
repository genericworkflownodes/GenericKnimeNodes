package com.genericworkflownodes.knime.cluster.nodes.filesplitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

import com.genericworkflownodes.knime.cluster.filesplitter.SplitterFactory;
import com.genericworkflownodes.knime.cluster.filesplitter.SplitterFactoryManager;
import com.genericworkflownodes.util.MIMETypeHelper;

public class FileSplitterNodeDialog extends DefaultNodeSettingsPane {

    private final static String NO_FACTORY = "<no factory available>";

    DialogComponentStringSelection m_factories;

    public FileSplitterNodeDialog() {
        DialogComponentNumber numParts = new DialogComponentNumber(
                FileSplitterNodeModel.createNumPartsSettingsModel(),
                "Number of parts", 1);
        addDialogComponent(numParts);

        m_factories = new DialogComponentStringSelection(
                FileSplitterNodeModel.createFactoryIDSettingsModel(),
                "Splitter",
                Collections.singleton(NO_FACTORY));
        addDialogComponent(m_factories);
    }

    @Override
    public void loadAdditionalSettingsFrom(NodeSettingsRO settings,
            PortObjectSpec[] specs) throws NotConfigurableException {

        String factoryId;
        SettingsModelString facId = FileSplitterNodeModel.createFactoryIDSettingsModel();

        try {
            facId.loadSettingsFrom(settings);
            factoryId = facId.getStringValue();
        } catch (InvalidSettingsException e) {
            factoryId = null;
        }

        URIPortObjectSpec spec = (URIPortObjectSpec)specs[0];
        String ext = spec.getFileExtensions().get(0);
        String mimetype = MIMETypeHelper.getMIMEtypeByExtension(ext).orElse(null);

        List<SplitterFactory> factories = SplitterFactoryManager
        .getInstance()
        .getFactories(mimetype);
        List<String> facs = new ArrayList<String>();

        if (factories.size() == 0) {
            facs = Collections.singletonList(NO_FACTORY);
        }

        for (SplitterFactory f : factories) {
            facs.add(f.getID());
        }

        m_factories.replaceListItems(facs, factoryId != null ? factoryId : facs.get(0));
    }
}
