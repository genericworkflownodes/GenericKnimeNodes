package org.ballproject.knime.nodegeneration.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.nodegeneration.NodeGenerator;

public class NodeFactoryXMLTemplate extends Template {

	private static String join(List<String> mts, String delimiter) {
		if (mts.isEmpty())
			return "";
		Iterator<String> iter = mts.iterator();
		StringBuffer buffer = new StringBuffer(iter.next());
		while (iter.hasNext())
			buffer.append(delimiter).append(iter.next());
		return buffer.toString();
	}

	private static String getInPorts(INodeConfiguration nodeConfiguration) {
		// ports
		String ip = "<inPort index=\"__IDX__\" name=\"__PORTDESCR__\"><![CDATA[__PORTDESCR__ [__MIMETYPE____OPT__]]]></inPort>";
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

	private static String getOutPorts(INodeConfiguration nodeConfiguration) {
		String op = "<outPort index=\"__IDX__\" name=\"__PORTDESCR__ [__MIMETYPE__]\"><![CDATA[__PORTDESCR__ [__MIMETYPE__]]]></outPort>";
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

	private static String getOptions(INodeConfiguration nodeConfiguration) {
		StringBuffer sb = new StringBuffer();
		for (Parameter<?> p : nodeConfiguration.getParameters()) {
			sb.append("\t\t<option name=\"" + p.getKey() + "\"><![CDATA["
					+ p.getDescription() + "]]></option>\n");
		}
		return sb.toString();
	}

	private static String prettyPrint(String manual) {
		if (manual.equals(""))
			return "";
		StringBuffer sb = new StringBuffer();
		String[] toks = manual.split("\\n");
		for (String tok : toks) {
			sb.append("<p><![CDATA[" + tok + "]]></p>");
		}
		return sb.toString();
	}

	public NodeFactoryXMLTemplate(String nodeName,
			INodeConfiguration nodeConfiguration) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/NodeXMLDescriptor.template"));

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
