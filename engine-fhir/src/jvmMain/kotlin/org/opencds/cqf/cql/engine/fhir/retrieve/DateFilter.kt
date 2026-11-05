package org.opencds.cqf.cql.engine.fhir.retrieve

import org.opencds.cqf.cql.engine.runtime.Interval

class DateFilter(
    @JvmField val datePath: String?,
    @JvmField val dateLowPath: String?,
    @JvmField val dateHighPath: String?,
    @JvmField val dateRange: Interval?,
)
