package org.opencds.cqf.cql.engine.fhir.retrieve

import org.opencds.cqf.cql.engine.runtime.Code

class CodeFilter(
    @JvmField val codePath: String?,
    @JvmField val codes: Iterable<Code>?,
    @JvmField val valueSet: String?,
)
