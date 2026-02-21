package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.AbsEvaluator
import org.opencds.cqf.cql.engine.elm.executing.AddEvaluator
import org.opencds.cqf.cql.engine.exception.CqlException

internal class CqlArithmeticFunctionsTest : CqlTestBase() {
    @Test
    fun abs() {
        // error testing

        try {
            AbsEvaluator.abs("This is an error")
            Assertions.fail<Any?>()
        } catch (e: CqlException) {
            // pass
        }
    }

    /** [org.opencds.cqf.cql.engine.elm.executing.AddEvaluator.add] */
    @Test
    fun add() {
        // error testing

        try {
            // Passing null as the state argument to the subtract method is fine here since that
            // method only uses the state when it has to convert Quantities with different units
            // which cannot happen here.
            AddEvaluator.add("This is an error", 404, null)
            Assertions.fail<Any?>()
        } catch (e: CqlException) {
            // pass
        }
    }
}
