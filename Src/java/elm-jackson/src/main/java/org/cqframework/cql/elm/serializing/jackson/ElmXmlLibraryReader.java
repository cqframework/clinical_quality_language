package org.cqframework.cql.elm.serializing.jackson;

import org.cqframework.cql.elm.serializing.ElmLibraryReader;
import org.hl7.elm.r1.Library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public class ElmXmlLibraryReader implements ElmLibraryReader {

    public ElmXmlLibraryReader() {
    }

    public Library read(File file) throws IOException {
        return ElmXmlMapper.getMapper().readValue(file, Library.class);
    }

    public Library read(URL url) throws IOException {
        return ElmXmlMapper.getMapper().readValue(url, Library.class);
    }

    public Library read(URI uri) throws IOException {
        return ElmXmlMapper.getMapper().readValue(uri.toURL(), Library.class);
    }

    public Library read(String string) throws IOException {
        return ElmXmlMapper.getMapper().readValue(string, Library.class);
    }

    public Library read(InputStream inputStream) throws IOException {
        return ElmXmlMapper.getMapper().readValue(inputStream, Library.class);
    }

    public Library read(Reader reader) throws IOException {
        return ElmXmlMapper.getMapper().readValue(reader, Library.class);
    }
}
