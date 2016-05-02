package org.cqframework.cql.data.fhir;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.execution.Context;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Bryn on 5/1/2016.
 */
public class TestFhirLibrary {
    @Test
    public void TestCBP() throws UnsupportedEncodingException {
        File xmlFile = new File(URLDecoder.decode(TestFhirLibrary.class.getResource("library-cbp.elm.xml").getFile(), "UTF-8"));
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        context.registerDataProvider("http://hl7.org/fhir", provider);
        Object result = context.resolveExpressionRef(library, "BP: Systolic").getExpression().evaluate(context);
    }
}
