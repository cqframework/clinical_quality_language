package org.opencds.cqf.cql.engine.fhir.data

import org.junit.jupiter.api.Assertions

class TestFhirExecution : FhirExecutionTestBase() {
    // TODO: fix this... I think it requires a resource to be loaded - put in init bundle
    // @Test
    fun testCoalesce() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        val results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("testCoalesce"))

        val value = results.forExpression("testCoalesce").value()
        Assertions.assertTrue((value as MutableList<*>)[0] as Int? == 72)
    }

    // @Test
    fun testMonthFrom() {
        engine.state.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        engine.state.setParameter(null, "MAXYEAR", 2014)
        val results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("testMonthFrom"))
        val value = results.forExpression("testMonthFrom").value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testMultisourceQueryCreatingDatePeriod() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        val results =
            engine.evaluate(library!!.identifier, mutableSetOf<String?>("Immunizations in range"))
        val value = results.forExpression("Immunizations in range").value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testIdResolution() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        val results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Resource Id"))
        val value = results.forExpression("Resource Id").value()
        Assertions.assertNotNull(value)
    }
}
