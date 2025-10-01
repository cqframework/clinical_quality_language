package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.r4.model.CareTeam
import org.hl7.fhir.r4.model.Patient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

// https://github.com/cqframework/clinical_quality_language/issues/1558
// care team cardinality bug for QI Core 6.0.0
internal class Issue1558 : FhirExecutionTestBase() {
    @Test
    fun careTeamRolesReturned() {
        val patient = Patient().setId("123")
        val careTeam = CareTeam()
        careTeam.addParticipant().addRole().setText("Care Team Role 1")
        careTeam.addParticipant().addRole().setText("Care Team Role 2")

        val r =
            RetrieveProvider {
                context,
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
                dateRange ->
                when (dataType) {
                    "Patient" -> mutableListOf<Any?>(patient)
                    "CareTeam" -> mutableListOf<Any?>(careTeam)
                    else -> mutableListOf()
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result = engine.evaluate("Issue1558")
        val x = result.forExpression("Care Teams Participant.Role Issue").value()
        val participantList = Assertions.assertInstanceOf(MutableList::class.java, x)
        Assertions.assertEquals(1, participantList!!.size.toLong())
        val roles: Any? = participantList[0]
        val roleList = Assertions.assertInstanceOf(MutableList::class.java, roles)
        Assertions.assertEquals(2, roleList!!.size.toLong())
    }
}
