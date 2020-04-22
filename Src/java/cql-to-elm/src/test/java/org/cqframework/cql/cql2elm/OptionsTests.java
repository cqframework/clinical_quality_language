package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class OptionsTests {

    @Test
    public void testTranslatorOptions() throws IOException {
        CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        om.writeValue(sw, options);
        String result = sw.toString();
        Assert.assertTrue(result != null);

        InputStream input = OptionsTests.class.getResourceAsStream("options.json");
        CqlTranslatorOptions readOptions = om.readValue(new InputStreamReader(input), CqlTranslatorOptions.class);
        Assert.assertTrue(readOptions != null);

        StringWriter sw2 = new StringWriter();
        om.writeValue(sw2, readOptions);
        String result2 = sw2.toString();
        Assert.assertTrue(result.equals(result2));
    }
}
