package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.files.MimeTypesFile.MIMETypeEntry;

public class MimeFileCellFactoryTemplate extends Template {

    private static final Logger LOGGER = Logger
            .getLogger(MimeFileCellFactoryTemplate.class.getCanonicalName());

    public MimeFileCellFactoryTemplate(String packageName,
            List<MIMETypeEntry> mimeTypes) throws IOException {
        super(NodeGenerator.class
                .getResourceAsStream("templates/MimeFileCellFactory.template"));

        String mimeTypeAddTemplateCodeLine = "\t\tmimetypes.add(\"__EXT__\");\n";
        String mimeTypeAddCode = "";

        Set<MIMETypeEntry> processedMimeTypes = new HashSet<MIMETypeEntry>();

        for (MIMETypeEntry mimeType : mimeTypes) {
            LOGGER.info("MIME Type read: " + mimeType.getType());

            if (processedMimeTypes.contains(mimeType)) {
                LOGGER.log(Level.WARNING, "skipping duplicate mime type "
                        + mimeType.getType());
            } else {
                processedMimeTypes.add(mimeType);
            }

            mimeTypeAddCode += mimeTypeAddTemplateCodeLine.replace("__EXT__",
                    mimeType.getExtensions().get(0).toLowerCase());
        }

        this.replace("__MIMETYPES__", mimeTypeAddCode);
        this.replace("__BASE__", packageName);
    }
}
