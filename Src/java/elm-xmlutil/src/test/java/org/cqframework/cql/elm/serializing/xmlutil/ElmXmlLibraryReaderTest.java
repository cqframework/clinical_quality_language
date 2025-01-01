package org.cqframework.cql.elm.serializing.xmlutil;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.Test;

class ElmXmlLibraryReaderTest {

    @Test
    void read() throws IOException {
        var reader = new ElmXmlLibraryReader();
        Library library =
                reader.read(new ByteArrayInputStream("<library xmlns=\"urn:hl7-org:elm:r1\"></library>".getBytes()));
        assertNotNull(library);
    }
}
