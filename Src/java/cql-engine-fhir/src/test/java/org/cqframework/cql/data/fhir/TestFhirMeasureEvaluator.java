package org.cqframework.cql.data.fhir;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.execution.Context;
import org.hl7.fhir.dstu3.model.Measure;
import org.hl7.fhir.dstu3.model.Patient;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import java.io.*;
import java.net.URLDecoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by Bryn on 5/7/2016.
 */
public class TestFhirMeasureEvaluator {
    @Test
    public void TestCBP() throws UnsupportedEncodingException, FileNotFoundException {
        File xmlFile = new File(URLDecoder.decode(TestFhirLibrary.class.getResource("library-cbp.elm.xml").getFile(), "UTF-8"));
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);

        //FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        //FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhir3.healthintersections.com.au/open/");
        FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://wildfhir.aegis.net/fhir");
        context.registerDataProvider("http://hl7.org/fhir", provider);

        xmlFile = new File(URLDecoder.decode(TestFhirLibrary.class.getResource("measure-cbp.xml").getFile(), "UTF-8"));
        Measure measure = provider.getFhirClient().getFhirContext().newXmlParser().parseResource(Measure.class, new FileReader(xmlFile));

        Patient patient = provider.getFhirClient().read().resource(Patient.class).withId("pat001").execute();
        // TODO: Couldn't figure out what matcher to use here, gave up.
        if (patient == null) {
            throw new RuntimeException("Patient is null");
        }

        context.setContextValue("Patient", patient.getId());

        FhirMeasureEvaluator evaluator = new FhirMeasureEvaluator();
        org.hl7.fhir.dstu3.model.MeasureReport report = evaluator.evaluate(provider.getFhirClient(), context, measure, patient);

        if (report == null) {
            throw new RuntimeException("MeasureReport is null");
        }

        if (report.getEvaluatedResources() == null) {
            throw new RuntimeException("EvaluatedResources is null");
        }

        System.out.println(String.format("Bundle url: %s", report.getEvaluatedResources().getReference()));
    }
}
