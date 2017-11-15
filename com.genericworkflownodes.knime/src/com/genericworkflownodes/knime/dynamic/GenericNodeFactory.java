package com.genericworkflownodes.knime.dynamic;

import org.knime.core.node.DynamicNodeFactory;

import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

public abstract class GenericNodeFactory
    extends DynamicNodeFactory<DynamicGenericNodeModel> {

    protected String getIconPath() {
        return "";
    }
    
    public abstract String getId();
    
    protected abstract IPluginConfiguration getPluginConfig();
}
