package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

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

    @Test
    fun stringInValueSetTrue() {
        val terminologyProvider =
            object : TerminologyProvider {
                override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
                    throw NotImplementedError()
                }

                override fun expand(valueSet: ValueSetInfo): Iterable<Code> {
                    if (valueSet.id == "example") {
                        return listOf(
                            Code().withCode("code1").withSystem("exampleSystem"),
                            Code().withCode("code2").withSystem("exampleSystem"),
                        )
                    }
                    return emptyList()
                }

                override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code {
                    throw NotImplementedError()
                }
            }

        val environment = Environment(null, null, terminologyProvider)

        assertEquals(
            true,
            InValueSetEvaluator.inValueSet(
                "code1",
                ValueSet().withId("example"),
                State(environment),
            ),
        )
    }

    @Test
    fun stringInValueSetThrows() {
        val terminologyProvider =
            object : TerminologyProvider {
                override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
                    throw NotImplementedError()
                }

                override fun expand(valueSet: ValueSetInfo): Iterable<Code> {
                    if (valueSet.id == "example") {
                        return listOf(
                            Code().withCode("code1").withSystem("system1"),
                            Code().withCode("code2").withSystem("system2"),
                        )
                    }
                    return emptyList()
                }

                override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code {
                    throw NotImplementedError()
                }
            }

        val environment = Environment(null, null, terminologyProvider)

        assertFailsWith<CqlException> {
            InValueSetEvaluator.inValueSet(
                "code1",
                ValueSet().withId("example"),
                State(environment),
            )
        }
    }
}
