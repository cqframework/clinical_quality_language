package org.cqframework.cql.elm.serializing.jackson;

import org.cqframework.cql.elm.serializing.ElmLibraryWriter;

public class ElmLibraryWriterProvider implements org.cqframework.cql.elm.serializing.ElmLibraryWriterProvider {
    @Override
    public ElmLibraryWriter create(String contentType) {
        if (contentType == null) {
            contentType = "application/elm+json";
        }
        switch (contentType) {
            case "application/elm+xml": return new ElmXmlLibraryWriter();
            case "application/elm+json":
            default: return new ElmJsonLibraryWriter();
        }
    }
}
