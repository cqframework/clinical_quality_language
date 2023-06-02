package org.opencds.cqf.cql.engine.serializing.jaxb;

import org.opencds.cqf.cql.engine.serializing.CqlLibraryReader;

public class CqlLibraryReaderProvider implements org.opencds.cqf.cql.engine.serializing.CqlLibraryReaderProvider {
    @Override
    public CqlLibraryReader create(String contentType) {
        if (contentType == null) {
            contentType = "application/elm+json";
        }

        switch (contentType) {
            case "application/elm+xml": return new XmlCqlLibraryReader();
            case "application/elm+json":
            default: return new JsonCqlLibraryReader();
        }
    }
}
