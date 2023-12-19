package org.cqframework.cql.elm.serializing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import org.hl7.elm.r1.Library;

public interface ElmLibraryReader {

    public Library read(File file) throws IOException;

    public Library read(URL url) throws IOException;

    public Library read(URI uri) throws IOException;

    public Library read(String string) throws IOException;

    public Library read(InputStream inputStream) throws IOException;

    public Library read(Reader reader) throws IOException;
}
