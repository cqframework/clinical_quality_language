package org.hl7.elm_modelinfo.r1.serializing.jackson;

import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader;

public class ModelInfoReaderProvider implements org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderProvider {
    @Override
    public ModelInfoReader create(String contentType) {
        if (contentType == null) {
            contentType = "application/xml";
        }

        switch (contentType) {
            case "application/xml": return new XmlModelInfoReader();
            default: throw new RuntimeException(String.format("ModelInfo reader content type %s not supported", contentType));
        }
    }
}
