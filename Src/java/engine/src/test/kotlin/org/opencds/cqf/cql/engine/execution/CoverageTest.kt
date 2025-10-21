package org.opencds.cqf.cql.engine.execution

import kotlin.test.assertEquals
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Test

class CoverageTest : CqlTestBase() {
    override val cqlSubdirectory = "CoverageTest"

    @Test
    fun exportLcovInfoTest() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableCoverageCollection)
        engine.evaluate("Tests")
        val actual =
            engine.state.globalCoverage.exportLcovInfo(
                listOf(
                    VersionedIdentifier().withId("Library1"),
                    VersionedIdentifier().withId("Library2"),
                )
            )
        // Git converts \n to \r\n on checkout on Windows
        val expected = this::class.java.getResource("CoverageTest/lcov.info").readText()
        assertEqualsIgnoringLineEndings(expected, actual)
    }

    @Test
    fun branchVisitCountTest() {
        val lib = Library()
        val coverage = LibraryCoverage(lib)
        val elm = Literal()
        coverage.markVisited(elm)
        coverage.markVisited(elm)
        val branch = Branch(elm, emptyList())

        assertEquals(2, coverage.getBranchVisitCount(branch))
    }

    private fun assertEqualsIgnoringLineEndings(expected: String, actual: String) {
        assertEquals(expected.replace("\r\n", "\n"), actual.replace("\r\n", "\n"))
    }
}
