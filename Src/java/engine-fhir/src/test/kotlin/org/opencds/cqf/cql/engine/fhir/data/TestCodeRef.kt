package org.opencds.cqf.cql.engine.fhir.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.client.api.IGenericClient
import org.junit.jupiter.api.Assertions
import org.opencds.cqf.cql.engine.fhir.terminology.Dstu3FhirTerminologyProvider

class TestCodeRef : FhirExecutionTestBase() {
    private val fhirClient: IGenericClient =
        FhirContext.forCached(FhirVersionEnum.DSTU3)
            .newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3")
    private val terminologyProvider = Dstu3FhirTerminologyProvider(fhirClient)

    // @Test
    fun CodeRefTest1() {
        val results = engine.evaluate(library!!.identifier!!, mutableSetOf("CodeRef1"))

        Assertions.assertTrue(results.forExpression("CodeRef1")!!.value() != null)
    }

    // @Test
    fun CodeRefTest2() {
        val results = engine.evaluate(library!!.identifier!!, mutableSetOf("CodeRef2"))

        Assertions.assertTrue(results.forExpression("CodeRef2")!!.value() != null)
    }
}
