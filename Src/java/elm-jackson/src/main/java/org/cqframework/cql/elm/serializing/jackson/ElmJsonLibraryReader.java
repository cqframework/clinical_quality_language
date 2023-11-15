package org.cqframework.cql.elm.serializing.jackson;

import org.cqframework.cql.elm.serializing.ElmLibraryReader;
import org.cqframework.cql.elm.serializing.LibraryWrapper;
import org.hl7.elm.r1.Library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public class ElmJsonLibraryReader implements ElmLibraryReader {

    public ElmJsonLibraryReader() {
    }


    public Library read(File file) throws IOException {
        return ElmJsonMapper.getMapper().readValue(file, LibraryWrapper.class).getLibrary();
    }

    public Library read(URL url) throws IOException {
        return ElmJsonMapper.getMapper().readValue(url, LibraryWrapper.class).getLibrary();
    }

    public Library read(URI uri) throws IOException {
        return ElmJsonMapper.getMapper().readValue(uri.toURL(), LibraryWrapper.class).getLibrary();
    }

    public Library read(String string) throws IOException {
        return ElmJsonMapper.getMapper().readValue(string, LibraryWrapper.class).getLibrary();
    }

    public Library read(InputStream inputStream) throws IOException {
        return ElmJsonMapper.getMapper().readValue(inputStream, LibraryWrapper.class).getLibrary();
    }

    public Library read(Reader reader) throws IOException {
        return ElmJsonMapper.getMapper().readValue(reader, LibraryWrapper.class).getLibrary();
    }
}
