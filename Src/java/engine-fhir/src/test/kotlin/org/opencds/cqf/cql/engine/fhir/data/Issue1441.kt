package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Procedure
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

// https://github.com/cqframework/clinical_quality_language/issues/1441
// unions without aliases are not working
internal class Issue1441 : FhirExecutionTestBase() {
    @Test
    fun unionsWithoutAliasesAreTheSameAsUnionsWithAliases() {
        val patient = Patient().setId("123")
        val observation = Encounter().setId("456")
        val procedure = Procedure().setId("789")

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
                    "Observation" -> mutableListOf<Any?>(observation)
                    "Procedure" -> mutableListOf<Any?>(procedure)
                    else -> mutableListOf()
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result = engine.evaluate("Issue1441")
        val x = result.forExpression("x").value() as Iterable<*>?
        val y = result.forExpression("y").value() as Iterable<*>?

        Assertions.assertIterableEquals(x, y)
    }
}
