package org.cqframework.cql.elm.serializing.jaxb;

import org.cqframework.cql.elm.serializing.ElmLibraryReader;

public class ElmLibraryReaderProvider implements org.cqframework.cql.elm.serializing.ElmLibraryReaderProvider {
    @Override
    public ElmLibraryReader create(String contentType) {
        if (contentType == null) {
            contentType = "application/elm+json";
        }

        switch (contentType) {
            case "application/elm+xml":
                return new ElmXmlLibraryReader();
            case "application/elm+json":
            default:
                return new ElmJsonLibraryReader();
        }
    }
}
