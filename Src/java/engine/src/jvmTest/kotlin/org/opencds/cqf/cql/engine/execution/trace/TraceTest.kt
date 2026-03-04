package org.opencds.cqf.cql.engine.execution.trace

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.hl7.elm.r1.Add
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Multiply
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
        func1ActivationFrame.variables.addFirst(Variable("a").withValue(6))
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

    /** Verify that EnableTracing alone still produces identical output (backward compatibility). */
    @Test
    fun backwardCompatibility() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableTracing)
        val rv = engine.evaluate { library("DetailedTraceTest") }
        val result = rv.results.values.first()
        val trace = result.trace
        assertNotNull(trace)

        // With basic tracing, only ExpressionDef and FunctionDef frames should appear
        assertTrue(trace.frames.all { it is ExpressionDefTraceFrame })
    }

    /**
     * Verify detailed tracing produces sub-expression frames with correct nesting, types, and
     * results for `define expr1: func1(3 + 2) * 3`.
     */
    @Test
    fun detailedTracing() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableDetailedTracing)
        val rv = engine.evaluate { library("DetailedTraceTest") }
        val result = rv.results.values.first()
        val trace = result.trace
        assertNotNull(trace)

        // Find the expr1 frame
        val expr1Frame =
            trace.frames.find { it is ExpressionDefTraceFrame && it.element.name == "expr1" }
                as ExpressionDefTraceFrame
        assertEquals(18, expr1Frame.result)

        // expr1 should have sub-expression frames (Multiply at minimum)
        assertTrue(
            expr1Frame.subframes.any { it is SubExpressionTraceFrame },
            "expr1 should have sub-expression frames in detailed mode",
        )

        // Find the Multiply sub-expression frame
        val multiplyFrame =
            expr1Frame.subframes.find { it is SubExpressionTraceFrame && it.element is Multiply }
                as? SubExpressionTraceFrame
        assertNotNull(multiplyFrame, "Should have a Multiply sub-expression frame")
        assertEquals(18, multiplyFrame.result)

        // Inside the Multiply, there should be a FunctionRef sub-expression and within it
        // an ExpressionDef for func1
        val funcRefFrame =
            multiplyFrame.subframes.find { it is SubExpressionTraceFrame }
                as? SubExpressionTraceFrame
        assertNotNull(funcRefFrame, "Multiply should contain a sub-expression frame")

        // The FunctionRef sub-expression should contain a nested ExpressionDefTraceFrame for func1
        val func1Frame =
            findFrameRecursive(multiplyFrame.subframes) {
                it is ExpressionDefTraceFrame && it.element.name == "func1"
            }
                as? ExpressionDefTraceFrame
        assertNotNull(func1Frame, "Should have a func1 ExpressionDef frame nested under expr1")
        assertEquals(6, func1Frame.result)
    }

    /**
     * Verify that when using a custom filter that traces everything (including literals), leaf
     * nodes appear in the trace.
     */
    @Test
    fun detailedTracingFilterOverride() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableDetailedTracing)
        // Override the filter to trace everything
        engine.state.traceExpressionFilter = { false }
        val rv = engine.evaluate { library("DetailedTraceTest") }
        val result = rv.results.values.first()
        val trace = result.trace
        assertNotNull(trace)

        // Find the expr1 frame and look for Literal frames
        val expr1Frame =
            trace.frames.find { it is ExpressionDefTraceFrame && it.element.name == "expr1" }
                as ExpressionDefTraceFrame

        val hasLiteral =
            containsFrameRecursive(expr1Frame.subframes) {
                it is SubExpressionTraceFrame && it.element is Literal
            }
        assertTrue(hasLiteral, "With filter override, Literal nodes should appear in trace")
    }

    /**
     * Verify that simple literal expressions produce correct traces in detailed mode. expr3 is a
     * simple literal definition.
     */
    @Test
    fun detailedTracingSimpleExpression() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableDetailedTracing)
        val rv = engine.evaluate { library("DetailedTraceTest") }
        val result = rv.results.values.first()
        val trace = result.trace
        assertNotNull(trace)

        // expr3 is a simple literal definition, it should have an ExpressionDefTraceFrame
        val expr3Frame =
            trace.frames.find { it is ExpressionDefTraceFrame && it.element.name == "expr3" }
                as? ExpressionDefTraceFrame
        assertNotNull(expr3Frame)
        assertEquals(2, expr3Frame.result)

        // expr3 body is a Literal, which is filtered by default, so no sub-expression frames
        assertTrue(
            expr3Frame.subframes.isEmpty(),
            "Literal body of expr3 should be filtered out by default",
        )
    }

    /** Verify the unit test for fromActivationFrames with detailed=true. */
    @Test
    fun traceOutputDetailed() {
        val libraryIdentifier = VersionedIdentifier().withId("Lib1")
        val contextName = "Patient"

        // Simulate sub-expression frames in the activation frame tree
        val addFrame =
            State.ActivationFrame(Add().withLocator("1:18-1:22"), libraryIdentifier, contextName, 0)
        addFrame.result = 5

        val multiplyFrame =
            State.ActivationFrame(
                Multiply().withLocator("1:15-1:28"),
                libraryIdentifier,
                contextName,
                0,
            )
        multiplyFrame.innerActivationFrames.add(addFrame)
        multiplyFrame.result = 15

        val func1ActivationFrame =
            State.ActivationFrame(
                FunctionDef().withName("func1").withOperand(listOf(OperandDef().withName("a"))),
                libraryIdentifier,
                contextName,
                0,
            )
        func1ActivationFrame.variables.addFirst(Variable("a").withValue(5))
        func1ActivationFrame.result = 6

        // Multiply contains FunctionRef (sub-expression) which in turn contains func1
        // (activation frame)
        val funcRefFrame =
            State.ActivationFrame(
                org.hl7.elm.r1.FunctionRef().withLocator("1:15-1:24"),
                libraryIdentifier,
                contextName,
                0,
            )
        funcRefFrame.innerActivationFrames.add(func1ActivationFrame)
        funcRefFrame.result = 6

        val expr1ActivationFrame =
            State.ActivationFrame(
                ExpressionDef().withName("expr1"),
                libraryIdentifier,
                contextName,
                0,
            )
        expr1ActivationFrame.innerActivationFrames.add(funcRefFrame)
        expr1ActivationFrame.innerActivationFrames.add(multiplyFrame)
        expr1ActivationFrame.result = 15

        // Test with detailed=false (backward compatible)
        val basicTrace =
            Trace.fromActivationFrames(
                listOf(expr1ActivationFrame),
                mapOf("Patient" to null),
                detailed = false,
            )
        assertEquals(
            """
            Lib1.expr1 = 15
              Lib1.func1(a = 5) = 6

            """
                .trimIndent(),
            basicTrace.toString(),
        )

        // Test with detailed=true
        val detailedTrace =
            Trace.fromActivationFrames(
                listOf(expr1ActivationFrame),
                mapOf("Patient" to null),
                detailed = true,
            )
        assertEquals(
            """
            Lib1.expr1 = 15
              FunctionRef [1:15-1:24] = 6
                Lib1.func1(a = 5) = 6
              Multiply [1:15-1:28] = 15
                Add [1:18-1:22] = 5

            """
                .trimIndent(),
            detailedTrace.toString(),
        )
    }

    /** Recursively searches for a frame matching the predicate. */
    private fun findFrameRecursive(
        frames: List<TraceFrame>,
        predicate: (TraceFrame) -> Boolean,
    ): TraceFrame? {
        for (frame in frames) {
            if (predicate(frame)) return frame
            val subframes =
                when (frame) {
                    is ExpressionDefTraceFrame -> frame.subframes
                    is SubExpressionTraceFrame -> frame.subframes
                    else -> emptyList()
                }
            val found = findFrameRecursive(subframes, predicate)
            if (found != null) return found
        }
        return null
    }

    /** Checks if any frame in the tree matches the predicate. */
    private fun containsFrameRecursive(
        frames: List<TraceFrame>,
        predicate: (TraceFrame) -> Boolean,
    ): Boolean {
        return findFrameRecursive(frames, predicate) != null
    }
}
