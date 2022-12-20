package org.opencds.cqf.cql.engine.serializing;

public interface CqlLibraryReaderProvider {
    CqlLibraryReader create(String contentType);
}
