package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.cqframework.cql.cql2elm.model.serialization.LibraryWrapper;
import org.hl7.elm.r1.Library;
import org.hl7.cql_annotations.r1.*;
import org.hl7.elm.r1.ObjectFactory;
import org.hl7.elm_modelinfo.r1.TypeInfo;
import org.hl7.elm_modelinfo.r1.TypeSpecifier;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ElmXmlLibraryReader {

    static XmlMapper mapper = new XmlMapper().builder()
            .defaultUseWrapper(true)
            .defaultMergeable(true)
            .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .enable(ToXmlGenerator.Feature.WRITE_XML_1_1)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .addModule(new JaxbAnnotationModule())
            .addMixIn(TypeInfo.class, TypeInfoMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .build();

    private ElmXmlLibraryReader() {
    }

    public static Library read(File file) throws IOException {
        return mapper.readValue(file, Library.class);
    }

    public static Library read(URL url) throws IOException {
        return mapper.readValue(url, Library.class);
    }

    public static Library read(URI uri) throws IOException {
        return mapper.readValue(uri.toURL(), Library.class);
    }

    public static Library read(String string) throws IOException {
        return mapper.readValue(string, Library.class);
    }

    public static Library read(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, Library.class);
    }

    public static Library read(Reader reader) throws IOException {
        return mapper.readValue(reader, Library.class);
    }

}
