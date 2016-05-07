package org.cqframework.cql.data.fhir;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.execution.Context;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.RiskAssessment;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

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
        //FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhir3.healthintersections.com.au/open/");
        //FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://wildfhir.aegis.net/fhir");
        context.registerDataProvider("http://hl7.org/fhir", provider);

        Object result = context.resolveExpressionRef(library, "BP: Systolic").evaluate(context);
        assertThat(result, instanceOf(Iterable.class));
        for (Object element : (Iterable)result) {
            assertThat(element, instanceOf(Observation.class));
            Observation observation = (Observation)element;
            assertThat(observation.getCode().getCoding().get(0).getCode(), is("8480-6"));
        }

        result = context.resolveExpressionRef(library, "BP: Diastolic").evaluate(context);
        assertThat(result, instanceOf(Iterable.class));
        for (Object element : (Iterable)result) {
            assertThat(element, instanceOf(Observation.class));
            Observation observation = (Observation)element;
            assertThat(observation.getCode().getCoding().get(0).getCode(), is("8462-4"));
        }
    }

    @Test
    public void TestCMS9v4_CQM() throws UnsupportedEncodingException {
        File xmlFile = new File(URLDecoder.decode(TestFhirLibrary.class.getResource("CMS9v4_CQM.xml").getFile(), "UTF-8"));
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);

        FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        //FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhir3.healthintersections.com.au/open/");
        //FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://wildfhir.aegis.net/fhir");
        context.registerDataProvider("http://hl7.org/fhir", provider);

        Object result = context.resolveExpressionRef(library, "Breastfeeding Intention Assessment").evaluate(context);
        assertThat(result, instanceOf(Iterable.class));
        for (Object element : (Iterable)result) {
            assertThat(element, instanceOf(RiskAssessment.class));
        }
    }
}
