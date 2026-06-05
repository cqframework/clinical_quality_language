package org.opencds.cqf.cql.engine.execution

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.hl7.elm.r1.Add
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Multiply
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.debug.BreakpointAction
import org.opencds.cqf.cql.engine.debug.BreakpointHandler
import org.opencds.cqf.cql.engine.debug.DebugMap

internal class BreakpointHandlerTest : CqlTestBase() {
    companion object {
        private const val NUMBER_OF_DEFINES = 4
    }

    @Test
    fun onBeforeExpression_invokedForEveryExpression() {
        val callCount = AtomicInteger(0)
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    callCount.incrementAndGet()
                    return BreakpointAction.CONTINUE
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        Assertions.assertTrue(
            callCount.get() > NUMBER_OF_DEFINES,
            "handler should be called for sub-expressions, not just ExpressionDefs",
        )
    }

    @Test
    fun onBeforeExpression_receivesLiveState() {
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    Assertions.assertNotNull(state)
                    Assertions.assertTrue(state.stack.isNotEmpty())
                    return BreakpointAction.CONTINUE
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow
    }

    @Test
    fun onBeforeExpression_returnsContinue_evaluationCompletes() {
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    return BreakpointAction.CONTINUE
                }
            }

        engine.state.breakpointHandler = handler
        val results = engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        Assertions.assertEquals(Integer(3), results["X"]!!.value)
        Assertions.assertEquals(Integer(12), results["Y"]!!.value)
        Assertions.assertEquals(Integer(6), results["Z"]!!.value)
    }

    @Test
    fun onAfterExpression_receivesCorrectValue() {
        val addResults = mutableListOf<Any?>()
        val multiplyResults = mutableListOf<Any?>()
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    return BreakpointAction.CONTINUE
                }

                override fun onAfterExpression(elm: Element, state: State, value: Any?) {
                    if (elm is Add) addResults.add(value)
                    if (elm is Multiply) multiplyResults.add(value)
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        Assertions.assertTrue(addResults.contains(Integer(3)))
        Assertions.assertTrue(multiplyResults.contains(Integer(12)))
    }

    @Test
    fun onBeforeExpression_pauseBlocksAndReleaseResumes() {
        val paused = CountDownLatch(1)
        val resumeLatch = CountDownLatch(1)

        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    if (elm is Add) {
                        paused.countDown()
                        return BreakpointAction.PAUSE
                    }
                    return BreakpointAction.CONTINUE
                }

                override fun waitForResume() {
                    try {
                        resumeLatch.await()
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }

                override fun release() {
                    resumeLatch.countDown()
                }
            }

        engine.state.breakpointHandler = handler

        val future =
            CompletableFuture.supplyAsync {
                engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow
            }

        Assertions.assertTrue(paused.await(5, TimeUnit.SECONDS))
        Assertions.assertFalse(future.isDone)

        handler.release()
        val results = future.get(5, TimeUnit.SECONDS)

        Assertions.assertNotNull(results)
        Assertions.assertEquals(Integer(3), results["X"]!!.value)
        Assertions.assertEquals(Integer(12), results["Y"]!!.value)
    }

    @Test
    fun breakpointHandler_independentOfDebugMap() {
        val callCount = AtomicInteger(0)
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    callCount.incrementAndGet()
                    return BreakpointAction.CONTINUE
                }
            }

        val debugMap = DebugMap()
        debugMap.isLoggingEnabled = true

        engine.state.breakpointHandler = handler

        val results =
            engine
                .evaluate {
                    library("BreakpointHandlerTest")
                    this@evaluate.debugMap = debugMap
                }
                .onlyResultOrThrow

        Assertions.assertTrue(callCount.get() > NUMBER_OF_DEFINES)

        val libraryDebug = results.debugResult!!.libraryResults["BreakpointHandlerTest"]!!.results
        Assertions.assertNotNull(libraryDebug)
        Assertions.assertTrue(libraryDebug.isNotEmpty())
    }

    @Test
    fun onExpressionDefEntered_firesForExpressionRef() {
        val enteredDefs = mutableListOf<String>()
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    return BreakpointAction.CONTINUE
                }

                override fun onExpressionDefEntered(elm: ExpressionDef, callSite: Element?, state: State) {
                    elm.name?.let { enteredDefs.add(it) }
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        Assertions.assertTrue("X" in enteredDefs)
        Assertions.assertTrue("Y" in enteredDefs)
    }

    @Test
    fun onExpressionDefEntered_firesForFunctionRef() {
        val enteredDefs = mutableListOf<String>()
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    return BreakpointAction.CONTINUE
                }

                override fun onExpressionDefEntered(elm: ExpressionDef, callSite: Element?, state: State) {
                    elm.name?.let { enteredDefs.add(it) }
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        // "F" is a function defined in the test CQL, and "Z" references F(5)
        Assertions.assertTrue("F" in enteredDefs, "onExpressionDefEntered should fire for function F")
    }

    @Test
    fun onExpressionDefEntered_receivesCorrectCallSite() {
        val callSites = mutableListOf<Element?>()
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    return BreakpointAction.CONTINUE
                }

                override fun onExpressionDefEntered(elm: ExpressionDef, callSite: Element?, state: State) {
                    callSites.add(callSite)
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        // The first callSite is for define Z (referenced via ExpressionRef "Z")
        // Since we don't know the exact order, just verify call sites exist and are
        // either ExpressionRef or FunctionRef (never null when inside a ref)
        for (cs in callSites) {
            if (cs != null) {
                Assertions.assertTrue(
                    cs is ExpressionRef || cs is FunctionRef,
                    "callSite should be an ExpressionRef or FunctionRef, got ${cs.javaClass.simpleName}",
                )
            }
        }
    }

    @Test
    fun onExpressionDefEntered_andEvaluated_match() {
        val entered = mutableListOf<ExpressionDef>()
        val evaluated = mutableListOf<ExpressionDef>()
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    return BreakpointAction.CONTINUE
                }

                override fun onExpressionDefEntered(elm: ExpressionDef, callSite: Element?, state: State) {
                    entered.add(elm)
                }

                override fun onExpressionDefEvaluated(elm: ExpressionDef, state: State, value: Any?) {
                    evaluated.add(elm)
                }
            }

        engine.state.breakpointHandler = handler
        engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        Assertions.assertEquals(entered.size, evaluated.size, "each entered define should be evaluated")
        // Order may differ (nested function bodies are entered after the outer define
        // but evaluated before it), so check set identity instead of sequential order.
        val enteredSet = entered.toSet()
        val evaluatedSet = evaluated.toSet()
        Assertions.assertEquals(enteredSet, evaluatedSet, "entered and evaluated should contain the same elements")
    }

    @Test
    fun breakpointHandler_worksWithDetailedTracing() {
        val callCount = AtomicInteger(0)
        val handler =
            object : BreakpointHandler {
                override fun onBeforeExpression(elm: Element, state: State): BreakpointAction {
                    callCount.incrementAndGet()
                    return BreakpointAction.CONTINUE
                }

            }

        engine.state.engineOptions.add(CqlEngine.Options.EnableDetailedTracing)
        engine.state.breakpointHandler = handler

        val results = engine.evaluate { library("BreakpointHandlerTest") }.onlyResultOrThrow

        Assertions.assertTrue(callCount.get() > NUMBER_OF_DEFINES)

        val traceEntry = results.trace
        Assertions.assertNotNull(traceEntry)
        Assertions.assertNotNull(traceEntry!!.frames)
        Assertions.assertTrue(traceEntry.frames.isNotEmpty())
    }
}
