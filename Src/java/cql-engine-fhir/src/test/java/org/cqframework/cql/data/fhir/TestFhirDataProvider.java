package org.cqframework.cql.data.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.hl7.fhir.dstu3.model.Patient;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by Bryn on 4/16/2016.
 */
public class TestFhirDataProvider {
    @Test
    public void testFhirClient() {
        FhirContext fhirContext = FhirContext.forDstu3();
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3");
    }

    @Test
    public void testPatientRetrieve() {
        FhirDataProvider provider = new FhirDataProvider().withEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        Iterable<Object> results = provider.retrieve("Patient", "Patient", null, null, null, null, null, null, null);
        List<Patient> patients = new ArrayList<>();

        int resultCount = 0;
        for (Object o : results) {
            patients.add((Patient)o);
            resultCount++;
        }

        assertTrue(patients.size() == resultCount);
    }
}
