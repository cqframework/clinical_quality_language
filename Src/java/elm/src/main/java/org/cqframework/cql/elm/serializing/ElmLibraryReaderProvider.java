package org.cqframework.cql.elm.serializing;

public interface ElmLibraryReaderProvider {
    ElmLibraryReader create(String contentType);
}
