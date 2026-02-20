package org.opencds.cqf.cql.engine.elm.executing

/*
is true(argument Boolean) Boolean

The is true operator determines whether or not its argument evaluates to true.
If the argument evaluates to true, the result is true; otherwise, the result is false.
*/

object IsTrueEvaluator {
    fun isTrue(operand: Boolean?): Any? {
        return true == operand
    }
}
