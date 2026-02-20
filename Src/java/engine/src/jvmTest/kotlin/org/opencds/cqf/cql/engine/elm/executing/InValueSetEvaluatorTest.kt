package org.opencds.cqf.cql.engine.elm.executing

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.ValueSet

class InValueSetEvaluatorTest {
    @Test
    fun issue1469FalseOnNullCode() {
        val env = Environment(null)
        val state = State(env)
        val valueSet = ValueSet()

        val actual = InValueSetEvaluator.inValueSet(null, valueSet, state)
        Assertions.assertInstanceOf(Boolean.Companion::class.java, actual)
        Assertions.assertFalse(actual as Boolean)
    }
}
