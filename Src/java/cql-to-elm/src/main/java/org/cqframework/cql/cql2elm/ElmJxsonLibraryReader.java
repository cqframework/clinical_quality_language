package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.cqframework.cql.cql2elm.model.serialization.LibraryWrapper;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.Library;
import org.hl7.elm_modelinfo.r1.TypeInfo;
import org.hl7.elm_modelinfo.r1.TypeSpecifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public class ElmJxsonLibraryReader {
    static JsonMapper mapper = new JsonMapper().builder()
            .defaultMergeable(true)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .addModule(new JaxbAnnotationModule())
            .addMixIn(TypeInfo.class, TypeInfoMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .build();

    private ElmJxsonLibraryReader() {
    }

    public static Library read(File file) throws IOException {
        return mapper.readValue(file, LibraryWrapper.class).getLibrary();
    }

    public static Library read(URL url) throws IOException {
        return mapper.readValue(url, LibraryWrapper.class).getLibrary();
    }

    public static Library read(URI uri) throws IOException {
        return mapper.readValue(uri.toURL(), LibraryWrapper.class).getLibrary();
    }

    public static Library read(String string) throws IOException {
        return mapper.readValue(string, LibraryWrapper.class).getLibrary();
    }

    public static Library read(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, LibraryWrapper.class).getLibrary();
    }

    public static Library read(Reader reader) throws IOException {
        return mapper.readValue(reader, LibraryWrapper.class).getLibrary();
    }
}
