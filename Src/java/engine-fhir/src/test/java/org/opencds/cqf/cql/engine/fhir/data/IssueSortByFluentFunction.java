package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

// Fluent functions were not sorting correctly due
// to the engine not looking in the variable stack
// when evaluating the IdentifierRef for "$this"
class IssueSortByFluentFunction extends FhirExecutionTestBase {

    @Test
    void obserationsSortedByFluentFunctionAreSorted() {
        var patient = new Patient().setId("123");
        var obs1 = new Observation();
        obs1.setId("A");
        var period1 = new Period()
                .setStartElement(new DateTimeType("2020-01-01"))
                .setEndElement(new DateTimeType("2020-01-02"));
        obs1.setEffective(period1);

        var obs2 = new Observation();
        obs2.setId("B");
        var period2 = new Period()
                .setStartElement(new DateTimeType("2020-01-03"))
                .setEndElement(new DateTimeType("2020-01-04"));
        obs2.setEffective(period2);

        var r = new RetrieveProvider() {
            @Override
            public Iterable<Object> retrieve(
                    String context,
                    String contextPath,
                    Object contextValue,
                    String dataType,
                    String templateId,
                    String codePath,
                    Iterable<Code> codes,
                    String valueSet,
                    String datePath,
                    String dateLowPath,
                    String dateHighPath,
                    Interval dateRange) {

                switch (dataType) {
                    case "Patient":
                        return Collections.singletonList(patient);
                    case "Observation":
                        return List.of(obs2, obs1); // Intentionally out of order to test sorting
                    default:
                        return Collections.emptyList();
                }
            }
        };

        var engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, r));
        var result = engine.evaluate("IssueSortByFluentFunction")
                .forExpression("Ordered Observations")
                .value();

        var obs = assertInstanceOf(List.class, result);
        assertEquals(obs1, obs.get(0));
        assertEquals(obs2, obs.get(1));
    }
}
