package com.genericworkflownodes.knime.nodegeneration.templates.knime_node;

import java.io.IOException;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;

public class NodeViewTemplate extends Template {

    public NodeViewTemplate(String packageName, String nodeName)
            throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/knime_node/NodeView.template"));

        this.replace("__BASE__", packageName);
        this.replace("__NODENAME__", nodeName);
    }

}
