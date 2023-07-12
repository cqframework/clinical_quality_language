package org.cqframework.cql.cql2elm;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class OptionsTests {

    @Test
    public void testTranslatorOptions() throws IOException {
        var options = CqlTranslatorOptions.defaultOptions();
        StringWriter sw = new StringWriter();
        CqlTranslatorOptionsMapper.toWriter(sw, options);
        String result = sw.toString();
        Assert.assertTrue(result != null);

        InputStream input = OptionsTests.class.getResourceAsStream("options.json");
        CqlTranslatorOptions readOptions = CqlTranslatorOptionsMapper.fromReader(new InputStreamReader(input));
        Assert.assertTrue(readOptions != null);

        StringWriter sw2 = new StringWriter();
        CqlTranslatorOptionsMapper.toWriter(sw2, readOptions);
        String result2 = sw2.toString();
        Assert.assertTrue(result.equals(result2));
    }
}
