package org.cqframework.cql.elm.serializing.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.cqframework.cql.elm.serializing.ElmLibraryReader;
import org.cqframework.cql.elm.serializing.LibraryWrapper;
import org.cqframework.cql.elm.serializing.jackson.mixins.CqlToElmBaseMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TypeSpecifierMixIn;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.TypeSpecifier;

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
