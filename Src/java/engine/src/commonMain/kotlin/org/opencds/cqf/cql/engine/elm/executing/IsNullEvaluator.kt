package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
is null(argument Any) Boolean

The is null operator determines whether or not its argument evaluates to null.
If the argument evaluates to null, the result is true; otherwise, the result is false.
*/

object IsNullEvaluator {
    @JvmStatic
    fun isNull(operand: CqlType?): Boolean {
        return (operand == null).toCqlBoolean()
    }
}
