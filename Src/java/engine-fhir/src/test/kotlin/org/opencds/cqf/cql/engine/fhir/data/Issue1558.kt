package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.hl7.fhir.r4.model.CareTeam
import org.hl7.fhir.r4.model.Patient
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List

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
            object : RetrieveProvider {
                override fun retrieve(
                    context: String?,
                    contextPath: String?,
                    contextValue: Any?,
                    dataType: String,
                    templateId: String?,
                    codePath: String?,
                    codes: Iterable<Code>?,
                    valueSet: String?,
                    datePath: String?,
                    dateLowPath: String?,
                    dateHighPath: String?,
                    dateRange: Interval?,
                ): Iterable<CqlType?>? {
                    return when (dataType) {
                        "Patient" -> mutableListOf(r4ModelResolver!!.toCqlValue(patient))
                        "CareTeam" -> mutableListOf(r4ModelResolver!!.toCqlValue(careTeam))
                        else -> mutableListOf()
                    }
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result = engine.evaluate { library("Issue1558") }.onlyResultOrThrow
        val participantList = result["Care Teams Participant.Role Issue"]!!.value
        assertIs<List>(participantList)
        assertEquals(1, participantList.count())
        val roles = participantList.elementAt(0)
        assertIs<List>(roles)
        assertEquals(2, roles.count())
    }
}
