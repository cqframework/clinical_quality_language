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
            val value = AbsEvaluator.abs("This is an error")
            Assertions.fail<Any?>()
        } catch (e: CqlException) {
            // pass
        }
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.AddEvaluator.evaluate] */
    @Test
    fun add() {
        // error testing

        try {
            val value = AddEvaluator.add("This is an error", 404)
            Assertions.fail<Any?>()
        } catch (e: CqlException) {
            // pass
        }
    }
}
