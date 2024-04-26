package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.opencds.cqf.cql.engine.execution.CqlEngine;

public class TestFhirExecution extends FhirExecutionTestBase {

    // TODO: fix this... I think it requires a resource to be loaded - put in init bundle
    // @Test
    public void testCoalesce() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        var results = engine.evaluate(library.getIdentifier(), Set.of("testCoalesce"));

        Object value = results.forExpression("testCoalesce").value();
        assertTrue((Integer) ((List<?>) value).get(0) == 72);
    }

    // @Test
    public void testMonthFrom() {
        CqlEngine engine = getEngine();
        engine.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        engine.getState().setParameter(null, "MAXYEAR", 2014);
        var results = engine.evaluate(library.getIdentifier(), Set.of("testMonthFrom"));
        Object value = results.forExpression("testMonthFrom").value();
        assertNotNull(value);
    }

    // @Test
    public void testMultisourceQueryCreatingDatePeriod() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        var results = engine.evaluate(library.getIdentifier(), Set.of("Immunizations in range"));
        Object value = results.forExpression("Immunizations in range").value();
        assertNotNull(value);
    }

    // @Test
    public void testIdResolution() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        var results = engine.evaluate(library.getIdentifier(), Set.of("Resource Id"));
        Object value = results.forExpression("Resource Id").value();
        assertNotNull(value);
    }
}
