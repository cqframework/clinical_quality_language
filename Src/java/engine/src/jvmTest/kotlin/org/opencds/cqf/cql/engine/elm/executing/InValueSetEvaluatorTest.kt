package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
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
        assertIs<Boolean>(actual)
        assertFalse(actual)
    }
}
