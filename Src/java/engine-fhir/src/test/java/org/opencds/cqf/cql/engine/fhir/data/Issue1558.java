package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

// https://github.com/cqframework/clinical_quality_language/issues/1558
// care team cardinality bug for QI Core 6.0.0
class Issue1558 extends FhirExecutionTestBase {

    @Test
    void careTeamRolesReturned() {
        var patient = new Patient().setId("123");
        var careTeam = new CareTeam();
        careTeam.addParticipant().addRole().setText("Care Team Role 1");
        careTeam.addParticipant().addRole().setText("Care Team Role 2");

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
                    case "CareTeam":
                        return Collections.singletonList(careTeam);
                    default:
                        return Collections.emptyList();
                }
            }
        };

        var engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, r));
        var result = engine.evaluate("Issue1558");
        var x = result.forExpression("Care Teams Participant.Role Issue").value();
        var participantList = assertInstanceOf(List.class, x);
        assertEquals(1, participantList.size());
        var roles = participantList.get(0);
        var roleList = assertInstanceOf(List.class, roles);
        assertEquals(2, roleList.size());
    }
}
