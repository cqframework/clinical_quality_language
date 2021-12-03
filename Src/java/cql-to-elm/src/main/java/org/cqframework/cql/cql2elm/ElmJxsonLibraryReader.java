package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.cqframework.cql.cql2elm.model.serialization.LibraryWrapper;
import org.hl7.elm.r1.Library;

import java.io.IOException;
import java.io.Reader;

public class ElmJxsonLibraryReader {
    private ElmJxsonLibraryReader() {
    }

    public static Library read(Reader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        Library result = mapper.readValue(reader, LibraryWrapper.class).getLibrary();
        return result;
    }
}
