package org.opencds.cqf.cql.engine.fhir.data

import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Patient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

// https://github.com/cqframework/clinical_quality_language/issues/1225
internal class Issue1225 : FhirExecutionTestBase() {
    @Test
    fun addressResolvesWithoutError() {
        val r: RetrieveProvider =
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
                ): Iterable<Any?> {
                    if (dataType != null && dataType == "Patient") {
                        val p = Patient()
                        p.getAddress().add(Address().addLine("123").addLine("456"))
                        return mutableListOf<Any?>(p)
                    }

                    return mutableListOf()
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result = engine.evaluate("Issue1225")

        Assertions.assertEquals("123", result.forExpression("Address Line 1")!!.value())
    }
}
