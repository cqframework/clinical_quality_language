package org.opencds.cqf.cql.engine.elm.executing

import org.hl7.elm.r1.ValueSetRef
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.ValueSet

class AnyInValueSetEvaluatorTest {
    @Test
    fun issue1469FalseOnNullCode() {
        val env = Environment(null)
        val state = State(env)
        val valueSet = ValueSet()
        val valueSetRef = ValueSetRef()

        val actual = AnyInValueSetEvaluator.internalEvaluate(null, valueSetRef, valueSet, state)
        Assertions.assertInstanceOf(Boolean.Companion::class.java, actual)
        Assertions.assertFalse(actual as Boolean)
    }
}
