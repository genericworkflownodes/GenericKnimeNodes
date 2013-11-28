package com.genericworkflownodes.knime.nodegeneration.templates.knime_node;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.templates.Template;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;

/**
 * Wrapper class for the NodeNameNodeFactory.xml file template.
 * 
 * @author bkahlert
 */
public class NodeFactoryXMLTemplate extends Template {

    /**
     * Joins all {@link String}s contained in the list using the provided
     * delimiter.
     * 
     * @param mts
     *            {@link List} of {@link String}s to join.
     * @param delimiter
     *            The delimiter that should be used to separate the joined
     *            {@link String}s.
     * @return A {@link String} containing all elements of the provided
     *         {@link List} separated by the delimiter.
     */
    private static String join(final List<String> mts, final String delimiter) {
        if (mts.isEmpty()) {
            return "";
        }
        Iterator<String> iter = mts.iterator();
        StringBuffer buffer = new StringBuffer(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        return buffer.toString();
    }

    /**
     * Returns the xml representation of the input ports.
     * 
     * @param nodeConfiguration
     *            The {@link INodeConfiguration} containing the port
     *            information.
     * @return A {@link String} representing the input port part of the xml
     *         config file.
     */
    private static String getInPorts(final INodeConfiguration nodeConfiguration) {
        // ports
        String ip = "\t\t<inPort index=\"__IDX__\" name=\"__PORTNAME__ [__MIMETYPE__]\">__PORTDESCR__ [__MIMETYPE____OPT__]</inPort>";
        String inPorts = "";
        int idx = 0;
        for (Port port : nodeConfiguration.getInputPorts()) {
            Parameter<?> parameter = nodeConfiguration.getParameter(port
                    .getName());
            String ipp = ip;

            ipp = ip.replace("__PORTNAME__", parameter.getKey());
            ipp = ipp.replace("__PORTDESCR__",
                    StringEscapeUtils.escapeHtml(port.getDescription()));
            ipp = ipp.replace("__IDX__", String.format("%d", idx++));
            ipp = ipp.replace("__MIMETYPE__", join(port.getMimeTypes(), ","));

            ipp = ipp.replace("__OPT__", (port.isOptional() ? ",opt." : ""));
            inPorts += ipp + "\n";
        }
        return inPorts;
    }

    /**
     * Returns the xml representation of the output ports.
     * 
     * @param nodeConfiguration
     *            The {@link INodeConfiguration} containing the port
     *            information.
     * @return A {@link String} representing the output port part of the xml
     *         config file.
     * @throws Exception
     */
    private static String getOutPorts(final INodeConfiguration nodeConfiguration)
            throws IOException {
        String op = "\t\t<outPort index=\"__IDX__\" name=\"__PORTNAME__ [__MIMETYPE__]\">__PORTDESCR__ [__MIMETYPE__]</outPort>";
        String outPorts = "";
        int idx = 0;
        for (Port port : nodeConfiguration.getOutputPorts()) {
            Parameter<?> parameter = nodeConfiguration.getParameter(port
                    .getName());

            String opp = op;

            opp = op.replace("__PORTNAME__", parameter.getKey());
            opp = opp.replace("__PORTDESCR__",
                    StringEscapeUtils.escapeHtml(port.getDescription()));
            opp = opp.replace("__IDX__", String.format("%d", idx++));
            opp = opp.replace("__MIMETYPE__", join(port.getMimeTypes(), ","));

            outPorts += opp + "\n";
        }
        return outPorts;
    }

    /**
     * Creates the option information contained in the
     * {@link INodeConfiguration}.
     * 
     * @param nodeConfiguration
     *            The {@link INodeConfiguration}.
     * @return A {@link String} representing the options of this node.
     */
    private static String getOptions(final INodeConfiguration nodeConfiguration) {
        StringBuffer sb = new StringBuffer();
        for (Parameter<?> p : nodeConfiguration.getParameters()) {
            // we ignore file parameters here, since they will be listed in the
            // port section
            if (p instanceof IFileParameter) {
                continue;
            }
            sb.append("\t\t<option name=\"" + p.getKey() + "\">"
                    + StringEscapeUtils.escapeHtml(p.getDescription())
                    + "</option>\n");
        }
        return sb.toString();
    }

    /**
     * Converts the passed {@link String} to a xml representation (e.g.,
     * newlines will be converted to HTML paragraphs).
     * 
     * @param manual
     *            The {@link String} that should be converted.
     * @return The passed {@link String} in HTML like form.
     */
    private static String prettyPrint(final String manual) {
        if (manual.equals("")) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        String[] toks = manual.split("\\n");
        for (String tok : toks) {
            sb.append("<p>" + tok + "</p>");
        }
        return sb.toString();
    }

    /**
     * Create a NodeNameNodeFactory.xml given the node name, node configuration,
     * and the path to the node icon.
     * 
     * @param nodeName
     *            Name of the node.
     * @param nodeConfiguration
     *            The node configuration.
     * @param iconPath
     *            The path to the icon of the node.
     * @throws Exception
     */
    public NodeFactoryXMLTemplate(final String nodeName,
            final INodeConfiguration nodeConfiguration, final String iconPath)
            throws IOException {
        super(
                NodeGenerator.class
                        .getResourceAsStream("templates/knime_node/NodeXMLDescriptor.template"));

        replace("__ICON__", iconPath);
        replace("__NODENAME__", nodeName);
        replace("__INPORTS__", getInPorts(nodeConfiguration));
        replace("__OUTPORTS__", getOutPorts(nodeConfiguration));
        replace("__OPTIONS__", getOptions(nodeConfiguration));
        replace("__DESCRIPTION__", nodeConfiguration.getDescription());
        String pp = prettyPrint(nodeConfiguration.getManual());

        if (!"".equals(nodeConfiguration.getDocUrl().trim())) {
            pp += String.format("\n\t\t<p>\n"
                    + "\t\t\t<a href=\"%s\">Web Documentation for %s</a>\n"
                    + "\t\t</p>\n", nodeConfiguration.getDocUrl(), nodeName);
        }
        replace("__MANUAL__", pp);
    }

}
