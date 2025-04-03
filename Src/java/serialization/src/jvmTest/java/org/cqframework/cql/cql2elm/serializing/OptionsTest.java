package org.cqframework.cql.cql2elm.serializing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.junit.jupiter.api.Test;

class OptionsTests {

    @Test
    void translatorOptions() throws IOException {
        var options = CqlTranslatorOptions.defaultOptions();
        StringWriter sw = new StringWriter();
        CqlTranslatorOptionsMapper.toWriter(sw, options);
        String result = sw.toString();
        assertNotNull(result);

        InputStream input = OptionsTests.class.getResourceAsStream("options.json");
        CqlTranslatorOptions readOptions = CqlTranslatorOptionsMapper.fromReader(new InputStreamReader(input));
        assertTrue(readOptions != null);

        StringWriter sw2 = new StringWriter();
        CqlTranslatorOptionsMapper.toWriter(sw2, readOptions);
        String result2 = sw2.toString();
        assertEquals(result, result2);
    }
}