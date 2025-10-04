package org.opencds.cqf.cql.engine.elm.executing

/*
is false(argument Boolean) Boolean

The is false operator determines whether or not its argument evaluates to false.
If the argument evaluates to false, the result is true; otherwise, the result is false.
*/

object IsFalseEvaluator {
    fun isFalse(operand: Boolean?): Any? {
        return false == operand
    }
}
