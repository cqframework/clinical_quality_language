package org.cqframework.cql.elm.serializing;

import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ElmJsonLibraryReaderTest {

    @Test
    void read() {
        var reader = new ElmJsonLibraryReader();
        Library library = reader.read("{\"library\" : { \"type\" : \"Library\"}}");
        assertNotNull(library);
    }
}
