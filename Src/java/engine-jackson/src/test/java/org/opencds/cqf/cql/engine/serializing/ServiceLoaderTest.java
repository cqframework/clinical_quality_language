package org.opencds.cqf.cql.engine.serializing;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.IntStream.range;
import static org.testng.AssertJUnit.assertNotNull;

import java.io.IOException;

import org.cqframework.cql.cql2elm.LibraryContentType;
import org.testng.annotations.Test;


public class ServiceLoaderTest {
    @Test
    void loaderIsAvailable() {
        assertNotNull(CqlLibraryReaderFactory.getReader(LibraryContentType.JSON.mimeType()));
        assertNotNull(CqlLibraryReaderFactory.getReader(LibraryContentType.XML.mimeType()));
    }

    @Test
    void multiThreadedServiceLoader() {
        range(0,100)
            .mapToObj(x -> runAsync(this::loadReader))
            .forEach(x -> x.join());;
    }

    void loadReader() {
        var reader = CqlLibraryReaderFactory.getReader(LibraryContentType.JSON.mimeType());
        try {
            reader.read(JsonCqlLibraryReaderTest.class.getResourceAsStream("EXM108.json"));
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
