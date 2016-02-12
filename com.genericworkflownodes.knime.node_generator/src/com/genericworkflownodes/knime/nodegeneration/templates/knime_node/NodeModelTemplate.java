package com.genericworkflownodes.knime.nodegeneration.templates.knime_node;

import java.io.IOException;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.exceptions.UnknownMimeTypeException;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;
import com.genericworkflownodes.knime.port.Port;

public class NodeModelTemplate extends Template {

    public NodeModelTemplate(String packageName, String nodeName,
            INodeConfiguration nodeConfiguration, String nodeTemplate) throws IOException,
            UnknownMimeTypeException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/knime_node/"+nodeTemplate));

        replace("__BASE__", packageName);
        replace("__NODENAME__", nodeName);

        fillMimeTypes(nodeConfiguration);
    }

    private void fillMimeTypes(INodeConfiguration config)
            throws UnknownMimeTypeException {
        String clazzez = "";
        for (Port port : config.getInputPorts()) {
            if (port.getMimeTypes().isEmpty()) {
                clazzez += "{}, ";
            } else {
                String tmp = "{";
                for (String type : port.getMimeTypes()) {
                    String ext = type.toLowerCase();
                    if (ext == null) {
                        throw new UnknownMimeTypeException(type);
                    }
                    tmp += "\"" + ext + "\" ,";
                }
                tmp = tmp.substring(0, tmp.length() - 1);
                tmp += "}, ";
                clazzez += tmp;
            }
        }

        if (!clazzez.equals("")) {
            clazzez = clazzez.substring(0, clazzez.length() - 1);
        }

        clazzez += "}";
        createInClazzezModel(clazzez);

        clazzez = "";
        for (Port port : config.getOutputPorts()) {
            if (port.getMimeTypes().isEmpty()) {
                clazzez += "{},";
            } else {
                String tmp = "{";
                for (String type : port.getMimeTypes()) {
                    String ext = type.toLowerCase();
                    if (ext == null) {
                        throw new UnknownMimeTypeException(type);
                    }
                    tmp += "\"" + ext + "\" ,";
                }
                tmp = tmp.substring(0, tmp.length() - 1);
                tmp += "}, ";
                clazzez += tmp;
            }
        }

        if (!clazzez.equals("")) {
            clazzez = clazzez.substring(0, clazzez.length() - 1);
        }

        clazzez += "}";

        createOutClazzezModel(clazzez);
    }

    private void createInClazzezModel(String clazzez) {
        if (clazzez.equals("")) {
            clazzez = "null";
        } else {
            clazzez = clazzez.substring(0, clazzez.length() - 1);
        }
        replace("__INCLAZZEZ__", clazzez);
    }

    private void createOutClazzezModel(String clazzez) {
        if (clazzez.equals("")) {
            clazzez = "null";
        } else {
            clazzez = clazzez.substring(0, clazzez.length() - 1);
        }
        replace("__OUTCLAZZEZ__", clazzez);
    }

}
