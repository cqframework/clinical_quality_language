package org.cqframework.cql.elm.serializing.jaxb;

import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.hl7.elm.r1.Library;
import org.testng.annotations.Test;

public class ElmXmlLibraryReaderTest {

    @Test
    public void testRead() throws IOException {
        var reader = new ElmXmlLibraryReader();
        Library library =
                reader.read(new ByteArrayInputStream("<library xmlns=\"urn:hl7-org:elm:r1\"></library>".getBytes()));
        assertNotNull(library);
    }
}
