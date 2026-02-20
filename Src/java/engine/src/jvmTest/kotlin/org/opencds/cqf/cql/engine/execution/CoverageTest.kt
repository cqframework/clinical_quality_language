package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.hl7.elm.r1.If
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.VersionedIdentifier

class CoverageTest : CqlTestBase() {
    override val cqlSubdirectory = "CoverageTest"

    @Test
    fun exportLcovInfoTest() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableCoverageCollection)
        engine.evaluate { library("Tests") }
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

    @Test
    fun branchCollectionVisitorTest() {
        val conditionNode = Literal()
        val thenNode = Literal()
        val elseNode = Literal()
        val ifNode = If().withCondition(conditionNode).withThen(thenNode).withElse(elseNode)
        val visitor = BranchCollectionVisitor()
        val branches = visitor.visitExpression(ifNode, Unit)

        assertEquals(1, branches.size)
        assertSame(branches[0].elm, ifNode)
        assertEquals(3, branches[0].children.size)
        assertSame(conditionNode, branches[0].children[0].elm)
        assertSame(thenNode, branches[0].children[1].elm)
        assertSame(elseNode, branches[0].children[2].elm)
        assertEquals(Literal(), Literal())
    }

    private fun assertEqualsIgnoringLineEndings(expected: String, actual: String) {
        assertEquals(expected.replace("\r\n", "\n"), actual.replace("\r\n", "\n"))
    }
}
