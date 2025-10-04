package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

/*
expand(valueSet ValueSet) List<Code>

The ExpandValueSet function expands the given value set using the terminology provider.
*/
object ExpandValueSetEvaluator {
    @JvmStatic
    fun expand(valueset: Any?, state: State?): Any? {
        if (valueset == null) {
            return null
        }

        if (valueset is ValueSet) {
            val tp = state!!.environment.terminologyProvider
            return tp!!.expand(ValueSetInfo.fromValueSet(valueset))
        }

        throw InvalidOperatorArgument(
            "ExpandValueSet(ValueSet)",
            String.format("ExpandValueSet(%s)", valueset.javaClass.name),
        )
    }
}
