package org.hl7.elm_modelinfo.r1.serializing;

public interface ModelInfoReaderProvider {
    ModelInfoReader create(String contentType);
}
