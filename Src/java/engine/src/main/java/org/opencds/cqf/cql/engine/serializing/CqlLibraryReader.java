package org.opencds.cqf.cql.engine.serializing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.cqframework.cql.elm.execution.Library;

public interface CqlLibraryReader {

    public Library read(File file) throws IOException;

    public Library read(URL url) throws IOException;

    public Library read(URI uri) throws IOException;

    public Library read(String string) throws IOException;

    public Library read(InputStream inputStream) throws IOException;

    public Library read(Reader reader) throws IOException;
}
