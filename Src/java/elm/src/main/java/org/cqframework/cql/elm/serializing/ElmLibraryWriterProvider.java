package org.cqframework.cql.elm.serializing;

public interface ElmLibraryWriterProvider {
    ElmLibraryWriter create(String contentType);
}
