package org.opencds.cqf.cql.engine.execution

import java.io.IOException
import kotlin.test.assertFailsWith
import kotlinx.io.Buffer
import kotlinx.io.readString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.nullValue
import org.hl7.elm.r1.ExpressionDef
import org.junit.jupiter.api.Test

class ProfilingTest : CqlTestBase() {
    private fun produceProfile(): Profile {
        val environment = Environment(libraryManager)
        val engine =
            CqlEngine(
                environment,
                mutableSetOf(
                    CqlEngine.Options.EnableExpressionCaching,
                    CqlEngine.Options.EnableProfiling,
                ),
            ) // TODO: engine options to CqlTestBase.getEngine instead?
        val result = engine.evaluate { library("ProfilingTest") }.onlyResultOrThrow
        val debugResult = result.debugResult
        return debugResult!!.profile!!
    }

    @Test
    fun profilingSmoke() {
        val profile = produceProfile()
        val tree = profile.tree
        assertThat(tree.expression, nullValue())

        assertThat(
            tree,
            NodeMatcher(
                nullValue(String::class.java),
                equalTo(1L),
                greaterThanOrEqualTo(0L),
                equalTo(1L),
                mapOf(
                    "Unfiltered" to
                        mapOf(
                            "E1" to
                                NodeMatcher(
                                    equalTo("Unfiltered"),
                                    equalTo(1L),
                                    greaterThanOrEqualTo(0L),
                                    equalTo(1L),
                                    mapOf(
                                        "Unfiltered" to
                                            mapOf(
                                                "G" to
                                                    NodeMatcher(
                                                        equalTo("Unfiltered"),
                                                        equalTo(1L),
                                                        greaterThanOrEqualTo(0L),
                                                        equalTo(1L),
                                                        mapOf(
                                                            "Unfiltered" to
                                                                mapOf(
                                                                    "F" to
                                                                        NodeMatcher(
                                                                            equalTo("Unfiltered"),
                                                                            equalTo(12L),
                                                                            greaterThanOrEqualTo(
                                                                                0L
                                                                            ),
                                                                            equalTo(12L),
                                                                        )
                                                                )
                                                        ),
                                                    )
                                            )
                                    ),
                                ),
                            "E2" to
                                NodeMatcher(
                                    equalTo("Unfiltered"),
                                    equalTo(1L),
                                    greaterThanOrEqualTo(0L),
                                    equalTo(1L),
                                    mapOf(
                                        "Unfiltered" to
                                            mapOf(
                                                "G" to
                                                    NodeMatcher(
                                                        equalTo("Unfiltered"),
                                                        equalTo(1L),
                                                        greaterThanOrEqualTo(0L),
                                                        equalTo(1L),
                                                        mapOf(
                                                            "Unfiltered" to
                                                                mapOf(
                                                                    "F" to
                                                                        NodeMatcher(
                                                                            equalTo("Unfiltered"),
                                                                            equalTo(22L),
                                                                            greaterThanOrEqualTo(
                                                                                0L
                                                                            ),
                                                                            equalTo(22L),
                                                                        )
                                                                )
                                                        ),
                                                    )
                                            )
                                    ),
                                ),
                            "E3" to
                                NodeMatcher(
                                    equalTo("Unfiltered"),
                                    equalTo(1L),
                                    greaterThanOrEqualTo(0L),
                                    equalTo(1L),
                                    mapOf(
                                        "Unfiltered" to
                                            mapOf(
                                                "E2" to
                                                    NodeMatcher(
                                                        equalTo("Unfiltered"),
                                                        equalTo(1L),
                                                        greaterThanOrEqualTo(0L),
                                                        equalTo(0L),
                                                    )
                                            )
                                    ),
                                ),
                            "E4" to
                                NodeMatcher(
                                    equalTo("Unfiltered"),
                                    equalTo(1L),
                                    greaterThanOrEqualTo(0L),
                                    equalTo(1L),
                                    mapOf(
                                        "Unfiltered" to
                                            mapOf(
                                                "E2" to
                                                    NodeMatcher(
                                                        equalTo("Unfiltered"),
                                                        equalTo(1L),
                                                        greaterThanOrEqualTo(0L),
                                                        equalTo(0L),
                                                    )
                                            )
                                    ),
                                ),
                        )
                ),
            ),
        )
    }

    @Test
    @Throws(IOException::class)
    fun renderSmoke() {
        val profile = produceProfile()
        val buffer = Buffer()
        profile.render(buffer)
        val svgString = buffer.readString()
        assertThat(svgString, Matchers.startsWith("<svg>"))
        assertThat(svgString, Matchers.endsWith("</svg>\n"))
    }

    @Test
    fun failedProfilingLeavesActivationFrame() {
        val engine =
            CqlEngine(Environment(libraryManager), mutableSetOf(CqlEngine.Options.EnableProfiling))
        val state = engine.state
        state.ensureDebugResult().ensureProfile()
        state.beginEvaluation()
        val outer = ExpressionDef().withName("Outer")
        state.pushActivationFrame(outer, "Patient")
        // A start time after the end time makes the profile reject the frame's duration
        state.pushActivationFrame(
            ExpressionDef().withName("Inner"),
            "Patient",
            State.frameTimeMillis() + 60_000,
        )
        assertFailsWith<IllegalStateException> { state.popActivationFrame() }

        // The enclosing frames still unwind, in the engine stack and the profile
        state.popActivationFrame()
        state.endEvaluation()
        assertThat(state.stack.size, equalTo(0))
        assertThat(
            state.debugResult!!.profile!!.tree.children["Patient"]!![outer]!!.count,
            equalTo(1L),
        )
    }
}
