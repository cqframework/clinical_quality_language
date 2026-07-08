package org.opencds.cqf.cql.engine.retrieve

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
interface RetrieveProvider {
    @Suppress("NON_EXPORTABLE_TYPE")
    fun retrieve(
        context: kotlin.String?,
        contextPath: kotlin.String?,
        contextValue: kotlin.String?,
        dataType: kotlin.String,
        templateId: kotlin.String?,
        codePath: kotlin.String?,
        codes: Iterable<Code>?,
        valueSet: kotlin.String?,
        datePath: kotlin.String?,
        dateLowPath: kotlin.String?,
        dateHighPath: kotlin.String?,
        dateRange: Interval?,
    ): Iterable<Value?>?
}
