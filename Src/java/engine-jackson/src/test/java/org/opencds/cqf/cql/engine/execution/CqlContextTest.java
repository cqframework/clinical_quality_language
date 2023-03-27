package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.TimeZone;

//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.nullValue;

public class CqlContextTest {

//    @Test
//    public void test_simpleLibrary_returnsResult() throws IOException, UcumException {
//        Library library = translate("portable/CqlOverloadingTestSuite.cql");
//        Context context = new Context(library, ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));
//        Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
//        System.out.println("result:" + result);
//    }

    @Test
    public void test_method_overload_signature_none() throws IOException {
        TranslatorHelper translatorHelper = new TranslatorHelper();
        Library library = translatorHelper.readJson(translatorHelper.readFromInputStream(CqlContextTest.class.getResourceAsStream("methodOverloadSingatureNone.json")));
        //Library library = translatorHelper.readJson(translatorHelper.readFromInputStream(CqlContextTest.class.getResourceAsStream("methodOverloadSingatureOverload.json")));
        Context context = new Context(library, ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));
        Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        System.out.println("result:" + result);
    }

    private static Library translate(String file)  throws UcumException, IOException {
        return new TranslatorHelper().translate(file);
    }
}
