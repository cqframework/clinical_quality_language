package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Collections;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

// https://github.com/cqframework/clinical_quality_language/issues/1441
// unions without aliases are not working
class Issue1441 extends FhirExecutionTestBase {

    @Test
    void unionsWithoutAliasesAreTheSameAsUnionsWithAliases() {
        var patient = new Patient().setId("123");
        var observation = new Encounter().setId("456");
        var procedure = new Procedure().setId("789");

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
                        return Collections.singletonList(observation);
                    case "Procedure":
                        return Collections.singletonList(procedure);
                    default:
                        return Collections.emptyList();
                }
            }
        };

        var engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, r));
        var result = engine.evaluate("Issue1441");
        var x = (Iterable<?>) result.forExpression("x").value();
        var y = (Iterable<?>) result.forExpression("y").value();

        assertIterableEquals(x, y);
    }
}
