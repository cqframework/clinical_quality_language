package org.cqframework.cql.elm.serializing.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.cqframework.cql.elm.serializing.ElmLibraryReader;
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
