package org.cqframework.cql.elm.serializing.jaxb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.Test;

class ElmJsonLibraryReaderTest {

    @Test
    void read() throws IOException {
        var reader = new ElmJsonLibraryReader();
        Library library = reader.read(new ByteArrayInputStream("{\"library\" : { \"type\" : \"Library\"}}".getBytes()));
        assertNotNull(library);
    }
}
