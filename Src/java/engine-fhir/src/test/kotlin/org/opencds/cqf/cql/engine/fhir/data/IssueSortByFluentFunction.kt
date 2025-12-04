package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

// Fluent functions were not sorting correctly due
// to the engine not looking in the variable stack
// when evaluating the IdentifierRef for "$this"
internal class IssueSortByFluentFunction : FhirExecutionTestBase() {
    @Test
    fun observationsSortedByFluentFunctionAreSorted() {
        val patient = Patient().setId("123")
        val obs1 = Observation()
        obs1.setId("A")
        val period1 =
            Period()
                .setStartElement(DateTimeType("2020-01-01"))
                .setEndElement(DateTimeType("2020-01-02"))
        obs1.setEffective(period1)

        val obs2 = Observation()
        obs2.setId("B")
        val period2 =
            Period()
                .setStartElement(DateTimeType("2020-01-03"))
                .setEndElement(DateTimeType("2020-01-04"))
        obs2.setEffective(period2)

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
                        "Observation" ->
                            listOf(obs2, obs1) // Intentionally out of order to test sorting
                        else -> mutableListOf()
                    }
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result =
            engine
                .evaluate { library("IssueSortByFluentFunction") }
                .onlyResultOrThrow["Ordered Observations"]!!
                .value

        val obs = Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(obs1, obs!![0])
        Assertions.assertEquals(obs2, obs[1])
    }
}
