package org.opencds.cqf.cql.engine.execution

import java.io.IOException
import kotlinx.io.Buffer
import kotlinx.io.readString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.nullValue
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
}
