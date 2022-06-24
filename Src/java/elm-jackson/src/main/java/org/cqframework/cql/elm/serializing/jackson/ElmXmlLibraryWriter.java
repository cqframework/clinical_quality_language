package org.cqframework.cql.elm.serializing.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cqframework.cql.elm.serializing.ElmLibraryWriter;
import org.cqframework.cql.elm.serializing.LibraryWrapper;
import org.hl7.elm.r1.Library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

/**
 * Implementation of an ELM XML serializer using the Jackson serialization framework.
 * This implementation is known non-functional, but after 3 different devs fiddling with it
 * for untold frustrating hours, we are abandoning it for now as a use case we don't care about anyway
 */
public class ElmXmlLibraryWriter implements ElmLibraryWriter {
    @Override
    public void write(Library library, Writer writer) throws IOException {
        ElmXmlMapper.getMapper().writeValue(writer, library);
    }

    @Override
    public String writeAsString(Library library) {
        try {
            LibraryWrapper wrapper = new LibraryWrapper();
            wrapper.setLibrary(library);
            return ElmXmlMapper.getMapper().writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
