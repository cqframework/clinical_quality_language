package org.opencds.cqf.cql.engine.serializing.jackson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.serializing.CqlLibraryReader;

public class JsonCqlLibraryReader implements CqlLibraryReader {

    public JsonCqlLibraryReader() {
    }

    public Library read(File file) throws IOException {
        return JsonCqlMapper.getMapper().readValue(file, Library.class);
    }

    public Library read(URL url) throws IOException {
        return JsonCqlMapper.getMapper().readValue(url, Library.class);
    }

    public Library read(URI uri) throws IOException {
        return JsonCqlMapper.getMapper().readValue(uri.toURL(), Library.class);
    }

    public Library read(String string) throws IOException {
        return JsonCqlMapper.getMapper().readValue(string, Library.class);
    }

    public Library read(InputStream inputStream) throws IOException {
        return JsonCqlMapper.getMapper().readValue(inputStream, Library.class);
    }

    public Library read(Reader reader) throws IOException {
        return JsonCqlMapper.getMapper().readValue(reader, Library.class);
    }
}
