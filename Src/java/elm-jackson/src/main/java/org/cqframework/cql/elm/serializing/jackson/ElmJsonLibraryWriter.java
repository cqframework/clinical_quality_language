package org.cqframework.cql.elm.serializing.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.io.Writer;
import org.cqframework.cql.elm.serializing.ElmLibraryWriter;
import org.cqframework.cql.elm.serializing.LibraryWrapper;
import org.hl7.elm.r1.Library;

public class ElmJsonLibraryWriter implements ElmLibraryWriter {
    @Override
    public void write(Library library, Writer writer) throws IOException {
        LibraryWrapper wrapper = new LibraryWrapper();
        wrapper.setLibrary(library);
        ElmJsonMapper.getMapper().writeValue(writer, wrapper);
    }

    @Override
    public String writeAsString(Library library) {
        try {
            LibraryWrapper wrapper = new LibraryWrapper();
            wrapper.setLibrary(library);
            return ElmJsonMapper.getMapper().writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
