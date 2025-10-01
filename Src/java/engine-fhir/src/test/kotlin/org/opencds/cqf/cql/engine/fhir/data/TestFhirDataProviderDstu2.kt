package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.dstu2.model.Encounter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.opencds.cqf.cql.engine.execution.EvaluationResult
import org.opencds.cqf.cql.engine.fhir.retrieve.FhirBundleCursor

class TestFhirDataProviderDstu2 : FhirExecutionTestBase() {
    private var results: EvaluationResult? = null

    @BeforeEach
    fun before() {
        engine.state.environment.registerDataProvider("http://hl7.org/fhir", dstu2Provider)
        results = engine.evaluate(library!!.identifier)
        // BaseFhirDataProvider provider = new
        // FhirDataProviderDstu2().setEndpoint("http://fhirtest.uhn.ca/baseDstu2");
        //        FhirDataProviderDstu2 primitiveProvider = new
        // FhirDataProviderDstu2().withEndpoint("http://fhirtest.uhn.ca/baseDstu2").withPackageName("ca.uhn.fhir.model.primitive");
        //        context.registerDataProvider("http://hl7.org/fhir", primitiveProvider);
        //        FhirDataProviderDstu2 compositeProvider = new
        // FhirDataProviderDstu2().withEndpoint("http://fhirtest.uhn.ca/baseDstu2").withPackageName("ca.uhn.fhir.model.dstu2.composite");
        //        context.registerDataProvider("http://hl7.org/fhir", compositeProvider);
    }

    // @Test
    fun testDstu2ProviderRetrieve() {
        val contextPath: String =
            dstu2ModelResolver!!.getContextPath("Patient", "Encounter").toString()
        val results =
            dstu2Provider!!.retrieve(
                "Patient",
                contextPath,
                "2822",
                "Encounter",
                null,
                "code",
                null,
                null,
                null,
                null,
                null,
                null,
            ) as FhirBundleCursor

        for (result in results) {
            val e = result as Encounter
            if (e.getPatient().idElement.idPart != "2822") {
                Assertions.fail<Any?>("Invalid patient id in Resource")
            }
        }

        Assertions.assertTrue(true)
    }

    // @Test
    fun testDstu2ProviderString() {
        val value = results!!.forExpression("testString").value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testDstu2ProviderCode() {
        val value = results!!.forExpression("testCode").value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testDstu2ProviderDate() {
        val value = results!!.forExpression("testDate").value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testDstu2ProviderDecimal() {
        val value = results!!.forExpression("testDecimal").value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testDstu2ProviderID() {
        val value = results!!.forExpression("testID").value()
        Assertions.assertNotNull(value)
    }
}
