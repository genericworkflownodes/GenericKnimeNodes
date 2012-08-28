package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.nodegeneration.NodeGenerator;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.parameter.Parameter;

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
		String ip = "<inPort index=\"__IDX__\" name=\"__PORTNAME__\"><![CDATA[__PORTDESCR__ [__MIMETYPE____OPT__]]]></inPort>";
		String inPorts = "";
		int idx = 0;
		for (Port port : nodeConfiguration.getInputPorts()) {
			String ipp = ip;
			ipp = ip.replace("__PORTNAME__", port.getName());
			ipp = ipp.replace("__PORTDESCR__", port.getDescription());
			ipp = ipp.replace("__IDX__", String.format("%d", idx++));

			// fix me
			// ipp = ipp.replace("__MIMETYPE__",
			// port.getMimeTypes().get(0).getExt());
			List<String> mts = new ArrayList<String>();
			for (MIMEtype mt : port.getMimeTypes()) {
				mts.add(mt.getExt());
			}
			ipp = ipp.replace("__MIMETYPE__", join(mts, ","));

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
	 */
	private static String getOutPorts(final INodeConfiguration nodeConfiguration) {
		String op = "<outPort index=\"__IDX__\" name=\"__PORTNAME__ [__MIMETYPE__]\"><![CDATA[__PORTDESCR__ [__MIMETYPE__]]]></outPort>";
		String outPorts = "";
		int idx = 0;
		for (Port port : nodeConfiguration.getOutputPorts()) {
			String opp = op;
			opp = op.replace("__PORTNAME__", port.getName());
			opp = opp.replace("__PORTDESCR__", port.getDescription());
			opp = opp.replace("__IDX__", String.format("%d", idx++));

			// fix me
			opp = opp.replace("__MIMETYPE__", port.getMimeTypes().get(0)
					.getExt());

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
			sb.append("\t\t<option name=\"" + p.getKey() + "\"><![CDATA["
					+ p.getDescription() + "]]></option>\n");
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
			sb.append("<p><![CDATA[" + tok + "]]></p>");
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
	 * @throws IOException
	 *             Throws an {@link IOException} if the associated template
	 *             cannot be read.
	 */
	public NodeFactoryXMLTemplate(final String nodeName,
			final INodeConfiguration nodeConfiguration, final String iconPath)
			throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/NodeXMLDescriptor.template"));

		this.replace("__ICON__", iconPath);
		this.replace("__NODENAME__", nodeName);
		this.replace("__INPORTS__", getInPorts(nodeConfiguration));
		this.replace("__OUTPORTS__", getOutPorts(nodeConfiguration));
		this.replace("__OPTIONS__", getOptions(nodeConfiguration));
		this.replace("__DESCRIPTION__", nodeConfiguration.getDescription());
		String pp = prettyPrint(nodeConfiguration.getManual());
		this.replace("__MANUAL__", pp);
		if (!nodeConfiguration.getDocUrl().equals("")) {
			String ahref = "<a href=\"" + nodeConfiguration.getDocUrl()
					+ "\">Web Documentation for " + nodeName + "</a>";
			this.replace("__DOCLINK__", ahref);
		} else {
			this.replace("__DOCLINK__", "");
		}
	}

}
