package org.opencds.cqf.cql.engine.fhir;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.opencds.cqf.cql.engine.execution.CqlEngine.ExpressionText.text;

import java.util.Map;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.fhir.r4.model.Observation;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.testng.annotations.Test;

public class CqlEngineExpressionTests {

    @Test
    public void fhirExpression() {
        var env = new Environment(new LibraryManager(
            new ModelManager()),
            Map.of("http://hl7.org/fhir", new CompositeDataProvider(new R4FhirModelResolver(), null)), null);

        var engine = new CqlEngine(env);

        var results = engine
            .evaluate(text(
                "library Test\n" +
                "using FHIR version '4.0.1'\n" +
                "include FHIRHelpers version '4.0.1'\n" +
                "define \"Ten\": 5 + 5\n" +
                "define \"Eleven\": 5 + 6\n"));

        assertEquals(10, results.forExpression("Ten").value());
        assertEquals(11, results.forExpression("Eleven").value());

        results = engine
            .evaluate(text(
                "library Test\n" +
                "using FHIR version '4.0.1'\n" +
                "include FHIRHelpers version '4.0.1'\n" +
                "define \"Observation\": FHIR.Observation { id: FHIR.id { value: '123' }}"));

        var obs = results.forExpression("Observation").value();
        assertThat(obs, instanceOf(Observation.class));
        var o = (Observation)obs;
        assertEquals("123", o.getIdElement().getIdPart());
    }
}
