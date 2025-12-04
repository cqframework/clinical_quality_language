package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Procedure
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

// https://github.com/cqframework/clinical_quality_language/issues/1441
// unions without aliases are not working
internal class Issue1441 : FhirExecutionTestBase() {
    @Test
    fun unionsWithoutAliasesAreTheSameAsUnionsWithAliases() {
        val patient = Patient().setId("123")
        val observation = Encounter().setId("456")
        val procedure = Procedure().setId("789")

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
                ): Iterable<Any?>? {
                    return when (dataType) {
                        "Patient" -> mutableListOf(patient)
                        "Observation" -> mutableListOf(observation)
                        "Procedure" -> mutableListOf(procedure)
                        else -> mutableListOf()
                    }
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result = engine.evaluate { library("Issue1441") }.onlyResultOrThrow
        val x = result["x"]!!.value as Iterable<*>?
        val y = result["y"]!!.value as Iterable<*>?

        Assertions.assertIterableEquals(x, y)
    }
}
