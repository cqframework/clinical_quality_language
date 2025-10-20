package org.opencds.cqf.cql.engine.execution

import kotlin.test.assertEquals
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Test

class CoverageTest : CqlTestBase() {
    override val cqlSubdirectory = "CoverageTest"

    @Test
    fun exportLcovInfo() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableCoverageCollection)
        engine.evaluate("Tests")
        val actual =
            engine.state.globalCoverage.exportLcovInfo(
                listOf(
                    VersionedIdentifier().withId("Library1"),
                    VersionedIdentifier().withId("Library2"),
                )
            )
        val expected = this::class.java.getResource("CoverageTest/lcov.info").readText()
        assertEquals(expected, actual)
    }
}
