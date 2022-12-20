package org.opencds.cqf.cql.engine.serializing;

import static org.testng.AssertJUnit.assertNotNull;

import org.cqframework.cql.cql2elm.LibraryContentType;
import org.testng.annotations.Test;

public class ServiceLoaderTest {
    @Test
    void loaderIsAvailable() {
        assertNotNull(CqlLibraryReaderFactory.getReader(LibraryContentType.JSON.mimeType()));
        assertNotNull(CqlLibraryReaderFactory.getReader(LibraryContentType.XML.mimeType()));
    }
}
