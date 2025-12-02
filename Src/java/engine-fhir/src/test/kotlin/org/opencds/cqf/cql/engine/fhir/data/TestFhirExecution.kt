package org.opencds.cqf.cql.engine.fhir.data

import org.junit.jupiter.api.Assertions

class TestFhirExecution : FhirExecutionTestBase() {
    // TODO: fix this... I think it requires a resource to be loaded - put in init bundle
    // @Test
    fun testCoalesce() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        val results =
            engine
                .evaluate { library(library!!.identifier!!) { expressions("testCoalesce") } }
                .onlyResultOrThrow

        val value = results["testCoalesce"]!!.value
        Assertions.assertTrue((value as MutableList<*>)[0] as Int? == 72)
    }

    // @Test
    fun testMonthFrom() {
        engine.state.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        engine.state.setParameter(null, "MAXYEAR", 2014)
        val results =
            engine
                .evaluate { library(library!!.identifier!!) { expressions("testMonthFrom") } }
                .onlyResultOrThrow
        val value = results["testMonthFrom"]!!.value
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testMultisourceQueryCreatingDatePeriod() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        val results =
            engine
                .evaluate {
                    library(library!!.identifier!!) { expressions("Immunizations in range") }
                }
                .onlyResultOrThrow
        val value = results["Immunizations in range"]!!.value
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testIdResolution() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        val results =
            engine
                .evaluate { library(library!!.identifier!!) { expressions("Resource Id") } }
                .onlyResultOrThrow
        val value = results["Resource Id"]!!.value
        Assertions.assertNotNull(value)
    }
}
