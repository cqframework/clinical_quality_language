package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import org.hl7.elm.r1.Message
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.trace.ExpressionDefTraceFrame
import org.opencds.cqf.cql.engine.execution.trace.TraceFrame
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

class BacktraceTest : CqlTestBase() {

    @Test
    @Suppress("LongMethod")
    fun backtraceSmoke() {
        val cqlException =
            assertFailsWith<CqlException> {
                engine.evaluate { library("BacktraceTest") }.onlyResultOrThrow
            }
        val backtrace = cqlException.backtrace
        assertNotNull(backtrace)

        val libraryIdentifier = VersionedIdentifier().withId("BacktraceTest")

        val e2Frame = backtrace.frame
        assertIs<ExpressionDefTraceFrame>(e2Frame)
        assertEquals("E2", e2Frame.element.name)
        assertEquals(emptyList(), e2Frame.arguments)
        assertEquals(emptyList(), e2Frame.variables)
        assertEquals(libraryIdentifier, e2Frame.library)
        assertEquals("Unfiltered" to null, e2Frame.context)

        val gFrame = e2Frame.subframes.single()
        assertIs<ExpressionDefTraceFrame>(gFrame)
        assertEquals("G", gFrame.element.name)
        assertEquals(
            listOf(
                Variable("Y").withValue(2.toCqlInteger()),
                Variable("Z").withValue("hi".toCqlString()),
            ),
            gFrame.arguments,
        )
        assertEquals(
            listOf(
                Variable("Y").withValue(2.toCqlInteger()),
                Variable("Z").withValue("hi".toCqlString()),
            ),
            gFrame.variables,
        )
        assertEquals(libraryIdentifier, gFrame.library)
        assertEquals("Unfiltered" to null, gFrame.context)

        val fFrame = gFrame.subframes.single()
        assertIs<ExpressionDefTraceFrame>(fFrame)
        assertEquals("F", fFrame.element.name)
        assertEquals(listOf(Variable("X").withValue(3.toCqlInteger())), fFrame.arguments)
        assertEquals(
            listOf(
                Variable("X").withValue(3.toCqlInteger()),
                Variable("_").withValue(Integer.ONE),
                Variable("A").withValue(7.toCqlInteger()),
            ),
            fFrame.variables,
        )
        assertEquals(libraryIdentifier, fFrame.library)
        assertEquals("Unfiltered" to null, fFrame.context)

        val messageFrame = fFrame.subframes.single()
        assertIs<TraceFrame>(messageFrame)
        val messageElement = messageFrame.element
        assertIs<Message>(messageElement)
        assertEquals(
            listOf(
                Variable("X").withValue(3.toCqlInteger()),
                Variable("_").withValue(Integer.ONE),
                Variable("A").withValue(7.toCqlInteger()),
            ),
            messageFrame.variables,
        )
        assertEquals(libraryIdentifier, messageFrame.library)
        assertEquals("Unfiltered" to null, messageFrame.context)

        assertEquals(
            """
            BacktraceTest.E2
              BacktraceTest.G(Y = 2, Z = hi)
                BacktraceTest.F(X = 3)
                  Message

            """
                .trimIndent(),
            backtrace.toString(),
        )
    }
}
