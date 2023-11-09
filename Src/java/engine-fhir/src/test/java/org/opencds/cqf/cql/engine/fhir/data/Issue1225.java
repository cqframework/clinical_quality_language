package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Patient;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

// https://github.com/cqframework/clinical_quality_language/issues/1225
public class Issue1225 extends FhirExecutionTestBase {

    @Test
    public void addressResolvesWithoutError() {
        var r = new RetrieveProvider() {
            @Override
            public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
                    String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
                    String dateLowPath, String dateHighPath, Interval dateRange) {

                if (dataType != null && dataType.equals("Patient")) {
                    var p = new Patient();
                    p.getAddress().add(new Address().addLine("123").addLine("456"));
                    return Collections.singletonList(p);
                }

                return Collections.emptyList();
            }
        };

        var engine = getEngine();
        engine.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, r));
        var result = engine.evaluate("Issue1225");

        assertEquals("123", result.forExpression("Address Line 1").value());
    }

}
