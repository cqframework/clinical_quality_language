package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhirpath.TranslatorHelper;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;

/**
 * Tests the implementation of {@link org.opencds.cqf.cql.engine.execution.Environment#is} and {@link org.opencds.cqf.cql.engine.execution.Environment#as} for Java Iterables.
 */
class Issue1577 {

    @Test
    void listTypeOperators() {
        var engine = TranslatorHelper.getEngine(
                """
                        library Issue1577
                        using FHIR version '4.0.1'
                        context Patient
                        define expr1: [Condition] is List<Any>
                        define expr2: [Condition] union [Observation]
                        """);

        RetrieveProvider retrieveProvider =
                (context,
                        contextPath,
                        contextValue,
                        dataType,
                        templateId,
                        codePath,
                        codes,
                        valueSet,
                        datePath,
                        dateLowPath,
                        dateHighPath,
                        dateRange) -> {
                    switch (dataType) {
                        case "Patient":
                            return List.of(new Patient().setId("pat1"));

                            // Note: returning an Iterable implementation and not a List to test handling of different
                            // Iterable types
                        case "Condition":
                            return () -> List.<Object>of(new Condition().setId("cond1"))
                                    .iterator();

                        case "Observation":
                            return () -> List.<Object>of(new Observation().setId("obs1"))
                                    .iterator();

                        default:
                            return Collections.emptyList();
                    }
                };

        engine.getState()
                .getEnvironment()
                .registerDataProvider(
                        "http://hl7.org/fhir",
                        new CompositeDataProvider(new CachedR4FhirModelResolver(), retrieveProvider));

        var evaluationResult = engine.evaluate("Issue1577");

        Object expr1Result = evaluationResult.forExpression("expr1").value();
        assertInstanceOf(Boolean.class, expr1Result);
        assertTrue((Boolean) expr1Result);

        Object expr2Result = evaluationResult.forExpression("expr2").value();
        assertInstanceOf(Iterable.class, expr2Result);

        int size = 0;
        for (Object resource : (Iterable<?>) expr2Result) {
            size++;
        }
        assertEquals(2, size);
    }
}
