package org.cqframework.cql.elm.serializing;

import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ElmXmlLibraryReaderTest {

    @Test
    void read() {
        var reader = new ElmXmlLibraryReader();
        Library library =
                reader.read("<library xmlns=\"urn:hl7-org:elm:r1\"></library>");
        assertNotNull(library);
    }
}
