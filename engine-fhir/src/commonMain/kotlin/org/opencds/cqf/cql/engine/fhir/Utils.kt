package org.opencds.cqf.cql.engine.fhir

import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value

internal val Value.ktStringOrNull: kotlin.String?
    get() = (this as? String)?.value
