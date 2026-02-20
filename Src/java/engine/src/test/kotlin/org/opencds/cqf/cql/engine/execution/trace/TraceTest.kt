package org.opencds.cqf.cql.engine.execution.trace

import kotlin.test.assertEquals
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable

class TraceTest : CqlTestBase() {

    /**
     * Generates a trace from a tree of activation frames that conceptually represents the
     * evaluation of the following CQL:
     *
     *     library Lib1
     *     context Patient
     *     define function func1(a Integer): a + 1
     *     define expr1: func1(Length([Encounter]) + 2) * 3
     *
     * Activation frames are also created for retrieves, but these should not appear in the trace.
     */
    @Test
    fun traceOutput() {
        val libraryIdentifier = VersionedIdentifier().withId("Lib1")
        val contextName = "Patient"

        val retrieveActivationFrame =
            State.ActivationFrame(Retrieve(), libraryIdentifier, contextName, 0)
        // Starting with a patient with 4 encounters
        retrieveActivationFrame.result = listOf(1, 2, 3, 4)

        val func1ActivationFrame =
            State.ActivationFrame(
                FunctionDef().withName("func1").withOperand(listOf(OperandDef().withName("a"))),
                libraryIdentifier,
                contextName,
                0,
            )
        func1ActivationFrame.variables.push(Variable("a").withValue(6))
        func1ActivationFrame.result = 7

        val expr1ActivationFrame =
            State.ActivationFrame(
                ExpressionDef().withName("expr1"),
                libraryIdentifier,
                contextName,
                0,
            )
        expr1ActivationFrame.innerActivationFrames.add(retrieveActivationFrame)
        expr1ActivationFrame.innerActivationFrames.add(func1ActivationFrame)
        expr1ActivationFrame.result = 21

        val trace =
            Trace.fromActivationFrames(listOf(expr1ActivationFrame), mapOf("Patient" to null))

        assertEquals(
            """
            Lib1.expr1 = 21
              Lib1.func1(a = 6) = 7

            """
                .trimIndent(),
            trace.toString(),
        )
    }

    @Test
    fun engineOption() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableTracing)
        val rv = engine.evaluate { library("TraceTest") }
        val result = rv.results.values.first()
        assertEquals(
            """
            TraceTest.expr1 = 2
            TraceTest.expr2 = 19
              TraceTest.func2(b = 5) = 19
                TraceTest.func1(a = 7) = 8
                TraceTest.func1(a = 8) = 9
                TraceTest.expr1 = 2

            """
                .trimIndent(),
            result.trace.toString(),
        )
    }
}
