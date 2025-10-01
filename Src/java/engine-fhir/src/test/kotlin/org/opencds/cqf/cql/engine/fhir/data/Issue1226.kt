package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

// https://github.com/cqframework/clinical_quality_language/issues/1226
internal class Issue1226 : FhirExecutionTestBase() {
    @Test
    fun medicationReferenceFound() {
        val r: RetrieveProvider =
            object : RetrieveProvider {
                override fun retrieve(
                    context: String?,
                    contextPath: String?,
                    contextValue: Any?,
                    dataType: String,
                    templateId: String?,
                    codePath: String?,
                    codes: Iterable<Code?>?,
                    valueSet: String?,
                    datePath: String?,
                    dateLowPath: String?,
                    dateHighPath: String?,
                    dateRange: Interval?,
                ): Iterable<Any?> {
                    when (dataType) {
                        "Patient" -> return mutableListOf<Any?>(Patient().setId("123"))
                        "MedicationRequest" ->
                            return mutableListOf<Any?>(
                                MedicationRequest().setMedication(Reference("Medication/456"))
                            )
                    }

                    return mutableListOf()
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )

        val result =
            engine
                .evaluate("Issue1226")
                .forExpression("Most Recent Medication Request reference")
                .value()

        Assertions.assertEquals("Medication/456", result)
    }
}
