package com.genericworkflownodes.knime.nodes.filesplitter;

import java.util.ArrayList;
import java.util.Collection;

import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

import com.genericworkflownodes.knime.filesplitter.SplitterFactory;
import com.genericworkflownodes.knime.filesplitter.SplitterFactoryManager;
import com.genericworkflownodes.util.MIMETypeHelper;

public class FileSplitterNodeDialog extends DefaultNodeSettingsPane {
    
    DialogComponentStringSelection m_factories;
    
    public FileSplitterNodeDialog() {
        DialogComponentNumber splits = new DialogComponentNumber(
                FileSplitterNodeModel.createNumPartsSettingsModel(), "Number of parts", 1);
        addDialogComponent(splits);
        
        m_factories = new DialogComponentStringSelection(
                FileSplitterNodeModel.createFactoryIDSettingsModel(), "Splitter", "No factory available");
        addDialogComponent(m_factories);
    }
    
    @Override
    public void loadAdditionalSettingsFrom(NodeSettingsRO settings,
            PortObjectSpec[] specs) throws NotConfigurableException {
        URIPortObjectSpec spec = (URIPortObjectSpec)specs[0];
        if (spec.getFileExtensions().size() != 1) {
            throw new NotConfigurableException("The input port must contain exactly one file.");
        }
        String mime = MIMETypeHelper.getMIMEtypeByExtension(spec.getFileExtensions().get(0));
        // Get registered splitter factories for the input mime type
        Collection<SplitterFactory> factories = SplitterFactoryManager.getInstance().getFactories(mime);
        Collection<String> factoryNames = new ArrayList<>();
        String selected = ((SettingsModelString)m_factories.getModel()).getStringValue();
        // Get factory IDs
        boolean selectedExists = false;
        for (SplitterFactory f : factories) {
            factoryNames.add(f.getID());
            if (f.getID().equals(selected)) {
                selectedExists = true;
            }
        }
        if (selectedExists) {
            selected = null;
        }
        if (factoryNames.size() == 0) {
            throw new NotConfigurableException("No splitter found for mimetype " + mime + ".");
        }
        m_factories.replaceListItems(factoryNames, selected);
        super.loadAdditionalSettingsFrom(settings, specs);
    }
}
