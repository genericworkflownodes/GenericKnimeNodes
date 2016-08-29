/**
 * Copyright (c) 2012, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.config.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import com.genericworkflownodes.knime.cliwrapper.CLIElement;
import com.genericworkflownodes.knime.cliwrapper.CLIMapping;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.reader.handler.ParamHandler;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleListParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.relocator.Relocator;
import com.genericworkflownodes.util.StringUtils;

/**
 * @author aiche
 */
public class CTDConfigurationWriter {

    private static final String REMOVE_TRAILING_DOT_RE = "\\.$";
    private static final String REMOVE_TRAILING_0_RE = "0*$";
    private BufferedWriter outputWriter;
    private int currentIndent;
    private List<String> currentNodeState;
    private INodeConfiguration currentConfig;

    private String xmlEscapeText(String t) {
        return StringEscapeUtils.escapeXml(t);
    }

    /**
     * C'tor for CTDConfigurationWriter.
     * 
     * @param target
     *            The file where the ctd will be stored.
     * @throws IOException
     *             If io operations fail.
     */
    public CTDConfigurationWriter(File target) throws IOException {
        outputWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(target), "UTF-8"));
        currentIndent = 0;
        currentNodeState = null;
    }

    /**
     * Construct writer directly using a {@link BufferedWriter}.
     * 
     * @param out
     *            The {@link BufferedWriter} where the content will be written.
     */
    public CTDConfigurationWriter(BufferedWriter out) {
        outputWriter = out;
        currentIndent = 0;
        currentNodeState = null;
    }

    /**
     * Adds the opening tags to the document.
     * 
     * @throws IOException
     */
    private void openCTDDocument() throws IOException {
        streamPut("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        streamPut(String
                .format("<tool name=\"%s\" version=\"%s\" docurl=\"%s\" category=\"%s\">",
                        xmlEscapeText(currentConfig.getName()),
                        xmlEscapeText(currentConfig.getVersion()),
                        xmlEscapeText(currentConfig.getDocUrl()),
                        xmlEscapeText(currentConfig.getCategory())));
        indent();
    }

    /**
     * Adds the closing tag to the written document.
     * 
     * @throws IOException
     */
    private void closeCTDDocument() throws IOException {
        outdent();
        streamPut("</tool>");
    }

    private void streamPut(String text) throws IOException {
        for (int i = 0; i < currentIndent; ++i) {
            outputWriter.write('\t');
        }

        outputWriter.write(text);
        outputWriter.write('\n');
    }

    /**
     * 
     * @param config
     */
    private void writeHeader() throws IOException {
        streamPut(String.format("<description>%s</description>",
                xmlEscapeText(currentConfig.getDescription()))); // description
        streamPut(String.format("<manual>%s</manual>",
                xmlEscapeText(currentConfig.getManual()))); // manual

        if (currentConfig.getExecutableName() != null) {
            streamPut(String.format("<executableName>%s</executableName>",
                    xmlEscapeText(currentConfig.getExecutableName())));
        }

        if (currentConfig.getExecutablePath() != null) {
            streamPut(String.format("<executablePath>%s</executablePath>",
                    xmlEscapeText(currentConfig.getExecutablePath())));
        }

        if (currentConfig.getCLI() != null
                && currentConfig.getCLI().getCLIElement().size() > 0) {
            writeCLI();
        }

        if (currentConfig.getRelocators().size() != 0) {
            writeRelocators();
        }
    }

    private void writeRelocators() throws IOException {
        streamPut("<relocators>");
        indent();

        for (Relocator rel : currentConfig.getRelocators()) {
            streamPut(String.format(
                    "<relocator reference=\"%s\" locations=\"%s\" />",
                    rel.getReferencedParamter(), rel.getLocation()));
        }

        outdent();
        streamPut("</relocators>");
    }

    private void writeCLI() throws IOException {
        streamPut("<cli>");
        indent();

        for (CLIElement elem : currentConfig.getCLI().getCLIElement()) {
            streamPut(String.format(
                    "<clielement optionIdentifier=\"%s\" isList=\"%s\">", elem
                            .getOptionIdentifier(), (elem.isList() ? "true"
                            : "false")));

            indent();
            for (CLIMapping mapping : elem.getMapping()) {
                streamPut(String.format("<mapping referenceName=\"%s\" />",
                        mapping.getReferenceName()));
            }
            outdent();

            streamPut("</clielement>");
        }

        outdent();
        streamPut("</cli>");
    }

    private void outdent() {
        --currentIndent;
    }

    private void indent() {
        ++currentIndent;
    }

    private List<String> getPrefix(String paramKey) {
        String[] keys = paramKey.split("\\.");
        assert keys.length > 1;
        List<String> prefixes = new ArrayList<String>();
        for (int i = 0; i < (keys.length - 1); ++i) {
            prefixes.add(keys[i]);
        }

        return prefixes;
    }

    private void writeParamXML() throws IOException {

        currentNodeState = new ArrayList<String>();

        streamPut("<PARAMETERS version=\"1.6.2\" "
                + "xsi:noNamespaceSchemaLocation=\"http://open-ms.sourceforge.net/schemas/Param_1_6_2.xsd\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        indent();

        for (String key : currentConfig.getParameterKeys()) {
            List<String> prefix = getPrefix(key);
            updateNodes(prefix);

            // output the actual parameter
            Parameter<?> p = currentConfig.getParameter(key);
            // handle depending on list or not
            if (p instanceof ListParameter) {
                writeItemList(key, p);
            } else {
                writeItem(key, p);
            }
        }

        closeRemainingNodes();

        outdent();
        streamPut("</PARAMETERS>");
    }

    private void writeItem(String key, Parameter<?> p) throws IOException {
        if (p instanceof FileParameter) {
        	// Omit complete parameter when its value is null (e.g. not connected)
        	if (p.isNull()) {
        		return;
        	}
        	// If it is an optional outport, check activation
            if (p.isOptional() && currentConfig.getOutputPortByName(key) != null) {
            	if (((FileParameter) p).getPort().isActive()) {
            		//skip addition of inactive Ports. They should not have any meaningful filenames/values anyway.
            		return;
            	}
            }
        }
        // construct parameter entry
        StringBuffer item = new StringBuffer();
        item.append("<ITEM name=\"");
        item.append(p.getKey());
        item.append('\"');

        // value
        item.append(" value=\"");
        if (p.getValue() != null && p.getValue().toString() != null) {
            item.append(xmlEscapeText(p.getValue().toString()));
        }
        item.append('\"');

        // type
        addItemType(key, p, item);

        // description
        addDescription(p, item);

        // add tags
        addParameterAttributes(p, item);

        // restrictions
        if (p instanceof BoolParameter) {
            addBoolRestrictions(item);
        } else if (p instanceof DoubleParameter
                || p instanceof IntegerParameter) {
            addNumberRestrictions(item, p);
        } else if (p instanceof StringChoiceParameter) {
            addStringChoices(item, p);
        } else if (p instanceof FileParameter) {
            addMimeTypeRestrictions(item, p);
        }

        item.append(" />");
        streamPut(item.toString());
    }

    private void writeItemList(String key, Parameter<?> p) throws IOException {
        StringBuffer item = new StringBuffer();
        item.append("<ITEMLIST name=\"");
        item.append(p.getKey());
        item.append('\"');

        // type
        addListType(key, p, item);

        // description
        addDescription(p, item);

        // add restrictions
        if (p instanceof DoubleListParameter
                || p instanceof IntegerListParameter) {
            addNumberRestrictions(item, p);
        } else if (p instanceof FileListParameter) {
            addMimeTypeRestrictions(item, p);
        }

        // add tags
        addParameterAttributes(p, item);

        item.append('>');
        streamPut(item.toString());

        indent();
        for (String val : ((ListParameter) p).getStrings()) {
            streamPut(String.format("<LISTITEM value=\"%s\"/>",
                    xmlEscapeText(val)));
        }
        outdent();
        streamPut("</ITEMLIST>");
    }

    private void addItemType(String key, Parameter<?> p, StringBuffer item) {
        item.append(" type=\"");
        if (p instanceof FileParameter) {
            addFileType(key, item);
        } else if (p instanceof BoolParameter || p instanceof StringParameter
                || p instanceof StringChoiceParameter) {
            item.append("string");
        } else if (p instanceof DoubleParameter) {
            item.append("double");
        } else if (p instanceof IntegerParameter) {
            item.append("int");
        }
        item.append('\"');
    }

    private void addFileType(String key, StringBuffer item) {
        if (currentConfig.getInputPortByName(key) != null) {
            if (currentConfig.getInputPortByName(key).isPrefix()) {
                item.append("input-prefix");
            } else {
                item.append("input-file");
            }
        } else if (currentConfig.getOutputPortByName(key) != null) {
            if (currentConfig.getOutputPortByName(key).isPrefix()) {
                item.append("output-prefix");
            } else {
                item.append("output-file");
            }
        }
    }

    private void addListType(String key, Parameter<?> p, StringBuffer item) {
        item.append(" type=\"");
        if (p instanceof FileListParameter) {
            addFileType(key, item);
        } else if (p instanceof StringListParameter) {
            item.append("string");
        } else if (p instanceof DoubleListParameter) {
            item.append("double");
        } else if (p instanceof IntegerListParameter) {
            item.append("int");
        }
        item.append('\"');
    }

    private void addMimeTypeRestrictions(StringBuffer item, Parameter<?> p) {
        Port associatedPort = ((IFileParameter) p).getPort();

        item.append(" supported_formats=\"");
        String sep = "";
        for (String mt : associatedPort.getMimeTypes()) {
            item.append(sep);
            sep = ",";
            item.append(String.format("*.%s", mt));
        }
        item.append('\"');
    }

    private void addParameterAttributes(Parameter<?> p, StringBuffer item) {
        item.append(" advanced=\"" + (p.isAdvanced() ? "true" : "false") + "\"");
        item.append(" required=\"" + (p.isOptional() ? "false" : "true") + "\"");
    }

    private void addDescription(Parameter<?> p, StringBuffer item) {
        item.append(" description=\"");
        if (p.getDescription() != null) {
            item.append(xmlEscapeText(p.getDescription()));
        }
        item.append('\"');
    }

    private void addStringChoices(StringBuffer item, Parameter<?> p) {
        StringChoiceParameter scp = (StringChoiceParameter) p;
        // the list of allowed values will contain the empty string depending on
        // whether the parameter is optional or not
        List<String> values = scp.getAllowedValues();
        item.append(" restrictions=\"");
        String sep = "";
        for (String rv : values) {
            if (scp.isOptional() && "".equals(rv)) {
                continue;
            }
            item.append(sep);
            item.append(rv);
            sep = ",";
        }
        item.append('\"');
    }

    private void addNumberRestrictions(StringBuffer item, Parameter<?> p) {
        StringBuffer restriction = new StringBuffer();
        if (p instanceof DoubleParameter) {
            addDoubleParameterRestrictions(p, restriction);
        } else if (p instanceof IntegerParameter) {
            addIntegerParameterRestrictions(p, restriction);
        } else if (p instanceof IntegerListParameter) {
            addIntegerListParameterRestrictions(p, restriction);
        } else if (p instanceof DoubleListParameter) {
            addDoubleListParameterRestrictions(p, restriction);
        }
        if (!"".equals(restriction.toString())) {
            item.append(" restrictions=\"");
            item.append(restriction.toString());
            item.append('\"');
        }
    }

    private void addDoubleListParameterRestrictions(Parameter<?> p,
            StringBuffer restriction) {
        DoubleListParameter dlp = (DoubleListParameter) p;
        boolean lbSet = Double.NEGATIVE_INFINITY != dlp.getLowerBound()
                .doubleValue();
        boolean ubSet = Double.POSITIVE_INFINITY != dlp.getUpperBound()
                .doubleValue();
        if (lbSet) {
            restriction.append(String
                    .format(Locale.ENGLISH, "%f", dlp.getLowerBound())
                    .replaceAll(REMOVE_TRAILING_0_RE, "")
                    .replaceAll(REMOVE_TRAILING_DOT_RE, ""));
        }
        if (ubSet || lbSet) {
            restriction.append(':');
        }
        if (ubSet) {
            restriction.append(String
                    .format(Locale.ENGLISH, "%f", dlp.getUpperBound())
                    .replaceAll(REMOVE_TRAILING_0_RE, "")
                    .replaceAll(REMOVE_TRAILING_DOT_RE, ""));
        }
    }

    private void addIntegerListParameterRestrictions(Parameter<?> p,
            StringBuffer restriction) {
        IntegerListParameter ilp = (IntegerListParameter) p;
        boolean lbSet = Integer.MIN_VALUE != ilp.getLowerBound().doubleValue();
        boolean ubSet = Integer.MAX_VALUE != ilp.getUpperBound().doubleValue();

        if (lbSet) {
            restriction.append(String.format("%d", ilp.getLowerBound()));
        }
        if (ubSet || lbSet) {
            restriction.append(':');
        }
        if (ubSet) {
            restriction.append(String.format("%d", ilp.getUpperBound()));
        }
    }

    private void addIntegerParameterRestrictions(Parameter<?> p,
            StringBuffer restriction) {
        IntegerParameter ip = (IntegerParameter) p;
        boolean lbSet = Integer.MIN_VALUE != ip.getLowerBound().doubleValue();
        boolean ubSet = Integer.MAX_VALUE != ip.getUpperBound().doubleValue();
        if (lbSet) {
            restriction.append(String.format("%d", ip.getLowerBound()));
        }
        if (ubSet || lbSet) {
            restriction.append(':');
        }
        if (ubSet) {
            restriction.append(String.format("%d", ip.getUpperBound()));
        }
    }

    private void addDoubleParameterRestrictions(Parameter<?> p,
            StringBuffer restriction) {
        DoubleParameter dp = (DoubleParameter) p;
        boolean lbSet = Double.NEGATIVE_INFINITY != dp.getLowerBound()
                .doubleValue();
        boolean ubSet = Double.POSITIVE_INFINITY != dp.getUpperBound()
                .doubleValue();
        if (lbSet) {
            restriction.append(String
                    .format(Locale.ENGLISH, "%f", dp.getLowerBound())
                    .replaceAll(REMOVE_TRAILING_0_RE, "")
                    .replaceAll(REMOVE_TRAILING_DOT_RE, ""));
        }
        if (ubSet || lbSet) {
            restriction.append(':');
        }
        if (ubSet) {
            restriction.append(String
                    .format(Locale.ENGLISH, "%f", dp.getUpperBound())
                    .replaceAll(REMOVE_TRAILING_0_RE, "")
                    .replaceAll(REMOVE_TRAILING_DOT_RE, ""));
        }
    }

    private void addBoolRestrictions(StringBuffer item) {
        item.append(" restrictions=\"true,false\"");
    }

    private void closeRemainingNodes() throws IOException {
        // close remaining prefixes
        for (int i = 0; i < currentNodeState.size(); ++i) {
            outdent();
            streamPut("</NODE>");
        }
        // clean up
        currentNodeState = null;
    }

    private void updateNodes(List<String> prefix) throws IOException {
        // find matching prefix
        int commonPrefix = 0;
        int smallerListSize = Math.min(prefix.size(), currentNodeState.size());
        for (; commonPrefix < smallerListSize; ++commonPrefix) {
            if (!prefix.get(commonPrefix).equals(
                    currentNodeState.get(commonPrefix))) {
                break;
            }
        }

        for (int i = commonPrefix; i < currentNodeState.size(); ++i) {
            outdent();
            streamPut("</NODE>");
        }
        for (int i = commonPrefix; i < prefix.size(); ++i) {
            String description = currentConfig
                    .getSectionDescription(concatenate(prefix, i));
            if (description == null) {
                description = "";
            }

            streamPut(String.format("<NODE name=\"%s\" description=\"%s\">",
                    prefix.get(i), xmlEscapeText(description)));
            indent();
        }
        currentNodeState = prefix;
    }

    private String concatenate(List<String> prefix, int end) {
        return StringUtils.join(
                prefix.subList(0,
                        ((end + 1) < prefix.size() ? end + 1 : prefix.size())),
                String.valueOf(ParamHandler.PATH_SEPARATOR));
    }

    /**
     * Writes the given {@link INodeConfiguration} to the stream.
     * 
     * @param config
     *            The config that should be written to the stream.
     * @throws IOException
     *             If io operations fail.
     */
    public void write(INodeConfiguration config) throws IOException {
        currentIndent = 0;
        currentConfig = config;

        // prepare the document
        openCTDDocument();

        // write the CTD-header information
        writeHeader();

        // add the param-xml part to the file
        writeParamXML();

        closeCTDDocument();
        outputWriter.close();
    }

}
