package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

/*
expand(valueSet ValueSet) List<Code>

The ExpandValueSet function expands the given value set using the terminology provider.
*/
object ExpandValueSetEvaluator {
    @JvmStatic
    fun expand(valueset: Value?, state: State?): List? {
        if (valueset == null) {
            return null
        }

        if (valueset is ValueSet) {
            val tp = state!!.environment.terminologyProvider
            return tp!!.expand(ValueSetInfo.fromValueSet(valueset)).toCqlList()
        }

        throw InvalidOperatorArgument(
            "ExpandValueSet(ValueSet)",
            "ExpandValueSet(${valueset.typeAsString})",
        )
    }
}
