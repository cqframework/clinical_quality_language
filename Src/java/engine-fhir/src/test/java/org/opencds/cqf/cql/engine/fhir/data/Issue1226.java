package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Reference;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

// https://github.com/cqframework/clinical_quality_language/issues/1226
public class Issue1226 extends FhirExecutionTestBase {

    @Test
    public void medicationReferenceFound() {
        var r = new RetrieveProvider() {
            @Override
            public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
                    String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
                    String dateLowPath, String dateHighPath, Interval dateRange) {

                switch(dataType) {
                    case "Patient" : return Collections.singletonList(new Patient().setId("123"));
                    case "MedicationRequest": return Collections.singletonList(
                        new MedicationRequest()
                            .setMedication(
                                new Reference("Medication/456")));
                }

                return Collections.emptyList();
            }
        };

        var engine = getEngine();

        engine.getState().getEnvironment()
            .registerDataProvider(
                "http://hl7.org/fhir",
                new CompositeDataProvider(r4ModelResolver, r));

        var result = engine.evaluate("Issue1226")
            .forExpression("Most Recent Medication Request reference")
            .value();

        assertEquals("Medication/456", result);
    }

}
