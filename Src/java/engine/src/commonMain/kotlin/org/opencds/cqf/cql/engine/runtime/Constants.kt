package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.BigDecimal

object Constants {
    const val MAX_INT = kotlin.Int.MAX_VALUE
    const val MAX_LONG = kotlin.Long.MAX_VALUE

    /** Set to (10^28 - 1) / 10^8. */
    val MAX_DECIMAL = BigDecimal("99999999999999999999.99999999")

    const val MIN_INT = kotlin.Int.MIN_VALUE
    const val MIN_LONG = kotlin.Long.MIN_VALUE

    /** Set to (-10^28 + 1) / 10^8. */
    val MIN_DECIMAL: BigDecimal = BigDecimal("-99999999999999999999.99999999")
}
