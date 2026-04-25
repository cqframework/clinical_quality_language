package org.opencds.cqf.cql.engine.retrieve

import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval

interface RetrieveProvider {
    fun retrieve(
        context: kotlin.String?,
        contextPath: kotlin.String?,
        contextValue: Any?,
        dataType: kotlin.String,
        templateId: kotlin.String?,
        codePath: kotlin.String?,
        codes: Iterable<Code>?,
        valueSet: kotlin.String?,
        datePath: kotlin.String?,
        dateLowPath: kotlin.String?,
        dateHighPath: kotlin.String?,
        dateRange: Interval?,
    ): Iterable<CqlType?>?
}
