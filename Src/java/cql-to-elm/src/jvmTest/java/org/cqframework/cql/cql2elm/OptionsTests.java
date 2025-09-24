package org.cqframework.cql.cql2elm;

import kotlinx.io.Buffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static kotlinx.io.Utf8Kt.readString;
import static org.junit.jupiter.api.Assertions.*;

class OptionsTests {

    @Test
    void translatorOptions() throws IOException {
        var options = CqlTranslatorOptions.defaultOptions();
        Buffer buffer = new Buffer();
        options.toSink(buffer);
        String result = readString(buffer);
        assertNotNull(result);

        InputStream input = OptionsTests.class.getResourceAsStream("options.json");
        CqlTranslatorOptions readOptions = CqlTranslatorOptions.fromSource(buffered(asSource(input)));
        assertTrue(readOptions != null);

        Buffer buffer2 = new Buffer();
        readOptions.toSink(buffer2);
        String result2 = readString(buffer2);
        assertEquals(result, result2);
    }
}
