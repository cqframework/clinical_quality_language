package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal

/*
timezone from(argument DateTime) Decimal

NOTE: This operator is _not_ part of CQL 1.4, it was renamed from 1.3 and is included so that the 1.4 engine can run 1.3 ELM
*/

object TimezoneFromEvaluator {
    @JvmStatic
    fun internalEvaluate(operand: CqlType?): Decimal? {
        return TimezoneOffsetFromEvaluator.timezoneOffsetFrom(operand)
    }
}
