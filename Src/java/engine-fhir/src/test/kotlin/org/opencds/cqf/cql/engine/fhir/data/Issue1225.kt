package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.Test
import kotlin.test.assertEquals
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Patient
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlString

// https://github.com/cqframework/clinical_quality_language/issues/1225
internal class Issue1225 : FhirExecutionTestBase() {
    @Test
    fun addressResolvesWithoutError() {
        val r: RetrieveProvider =
            object : RetrieveProvider {
                override fun retrieve(
                    context: String?,
                    contextPath: String?,
                    contextValue: String?,
                    dataType: String,
                    templateId: String?,
                    codePath: String?,
                    codes: Iterable<Code>?,
                    valueSet: String?,
                    datePath: String?,
                    dateLowPath: String?,
                    dateHighPath: String?,
                    dateRange: Interval?,
                ): Iterable<Value?> {
                    if (dataType == "Patient") {
                        val p = Patient()
                        p.getAddress().add(Address().addLine("123").addLine("456"))
                        return mutableListOf(r4ModelResolver!!.toCqlValue(p))
                    }

                    return mutableListOf()
                }
            }

        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, r),
        )
        val result = engine.evaluate { library("Issue1225") }.onlyResultOrThrow

        assertEquals("123".toCqlString(), result["Address Line 1"]!!.value)
    }
}
