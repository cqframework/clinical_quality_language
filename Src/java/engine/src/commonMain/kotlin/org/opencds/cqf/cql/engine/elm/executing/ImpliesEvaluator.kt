package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Value

/*

implies (left Boolean, right Boolean) Boolean

The implies operator returns the logical implication of its arguments.
   This means that if the left operand evaluates to true, this operator returns
   the boolean evaluation of the right operand. If the left operand evaluates to false,
   this operator returns true. Otherwise, this operator returns true if the right operand
   evaluates to true, and null otherwise.
The following table defines the truth table for this operator:
       | TRUE	FALSE	NULL
-----------------------------
TRUE    | TRUE	FALSE	NULL
FALSE	| TRUE	TRUE	TRUE
NULL	| TRUE	NULL	NULL

*/

object ImpliesEvaluator {
    @JvmStatic
    fun implies(left: Value?, right: Value?): Boolean? {
        if (left == null) {
            return if (right == null || !(right as Boolean).value) null else Boolean.TRUE
        }

        if ((left as Boolean).value) {
            return right as Boolean?
        }

        return Boolean.TRUE
    }
}
