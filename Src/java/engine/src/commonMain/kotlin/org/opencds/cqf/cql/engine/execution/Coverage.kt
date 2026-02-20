package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.Location

/**
 * Represents the CQL/ELM coverage information for all libraries involved in execution. An instance
 * of this class is kept in the engine state during execution.
 */
class GlobalCoverage {
    private val libraryCoverages = mutableMapOf<VersionedIdentifier, LibraryCoverage>()

    /** Called during ELM evaluation to mark an element as visited for coverage reporting. */
    fun markElementAsVisitedForCoverageReport(elm: Element, library: Library) {
        val libraryIdentifier =
            checkNotNull(library.identifier) {
                "Current library has null identifier when marking element for coverage report"
            }
        val coverage = libraryCoverages.getOrPut(libraryIdentifier) { LibraryCoverage(library) }
        coverage.markVisited(elm)
    }

    /** Exports coverage information in LCOV format (lcov.info) for multiple libraries. */
    fun exportLcovInfo(libraryIdentifiers: List<VersionedIdentifier>): String {
        return buildString {
            for (libraryIdentifier in libraryIdentifiers) {
                val libraryCoverage = libraryCoverages[libraryIdentifier]
                if (libraryCoverage != null) {
                    append(libraryCoverage.toLcovInfo())
                }
            }
        }
    }
}

/** Represents coverage information for a single ELM library. */
internal class LibraryCoverage(val library: Library) {
    /** Keeps track of how many times each element was visited. */
    private val elementVisitCounts = mutableMapOf<Element, Int>()

    /** Marks an ELM element as visited. */
    fun markVisited(elm: Element) {
        elementVisitCounts[elm] = (elementVisitCounts[elm] ?: 0) + 1
    }

    /** Returns the visit count for a branch. */
    fun getBranchVisitCount(branch: Branch): Int {
        // ExpressionDefs (including FunctionDefs) aren't directly marked as visited. When they are
        // called or evaluated, the evaluation visitor instead visits the expression inside the
        // definition.
        if (branch.elm is ExpressionDef) {
            return branch.children.sumOf { getBranchVisitCount(it) }
        }

        return elementVisitCounts[branch.elm] ?: 0
    }

    /**
     * Calculates line coverage results from branches.
     *
     * @param branches The branches representing an ELM library.
     * @return A map of line numbers to their coverage information.
     */
    fun calculateLineCoverages(branches: List<Branch>): Map<Int, LineCoverage> {

        /** Maps line numbers to their coverage information. */
        val lineCoverages = mutableMapOf<Int, LineCoverage>()

        /** Recursively calculates line coverages for branches. */
        fun calculateLineCoveragesInner(branches: List<Branch>) {

            /** Maps line numbers to branches that cover that line. */
            val branchBlocks = mutableMapOf<Int, MutableSet<Branch>>()

            for (branch in branches) {
                val branchLocation = branch.location
                if (branchLocation != null) {
                    val visitCountForBranch = getBranchVisitCount(branch)
                    for (lineNumber in branchLocation.startLine..branchLocation.endLine) {
                        lineCoverages.getOrPut(lineNumber) { LineCoverage() }.visitCount +=
                            visitCountForBranch
                        branchBlocks.getOrPut(lineNumber) { mutableSetOf() }.add(branch)
                    }
                }
                calculateLineCoveragesInner(branch.children)
            }

            for ((lineNumber, branchBlock) in branchBlocks) {
                lineCoverages.getOrPut(lineNumber) { LineCoverage() }.branchBlocks.add(branchBlock)
            }
        }

        calculateLineCoveragesInner(branches)

        return lineCoverages
    }

    /** Exports coverage information in LCOV format (lcov.info). */
    fun toLcovInfo(): String {
        val branches = collectBranches(library)
        val lineCoverages = calculateLineCoverages(branches)
        return buildString {
            append("TN:\n")
            val libraryIdentifier = library.identifier!!
            val libraryName = buildString {
                append(libraryIdentifier.id ?: "unknown")
                if (libraryIdentifier.version != null) {
                    append("-${libraryIdentifier.version}")
                }
                append(".cql")
            }
            append("SF:$libraryName\n")
            for ((lineNumber, lineCoverage) in
                lineCoverages.toList().sortedBy { it.first }.toMap()) {
                append("DA:$lineNumber,${lineCoverage.visitCount}\n")
                for ((branchBlockIndex, branchBlock) in lineCoverage.branchBlocks.withIndex()) {
                    for ((branchIndex, branch) in branchBlock.withIndex()) {
                        val branchVisitCount = this@LibraryCoverage.getBranchVisitCount(branch)
                        append(
                            "BRDA:$lineNumber,$branchBlockIndex,$branchIndex,$branchVisitCount\n"
                        )
                    }
                }
            }
            val totalBranches = lineCoverages.values.sumOf { it.branchBlocks.sumOf { it.size } }
            append("BRF:$totalBranches\n")
            val coveredBranches =
                lineCoverages.values.sumOf { lineCoverage ->
                    lineCoverage.branchBlocks.sumOf { branchBlock ->
                        branchBlock.count { branch ->
                            val visitCount = this@LibraryCoverage.getBranchVisitCount(branch)
                            visitCount > 0
                        }
                    }
                }
            append("BRH:$coveredBranches\n")
            append("end_of_record\n")
        }
    }
}

/** Represents coverage information for a single line in a CQL source file. */
internal class LineCoverage {
    /** How many times an ELM element on this line was visited. */
    var visitCount = 0

    /** Each branch block is a collection of sibling (same-level) branches that cover this line. */
    val branchBlocks = mutableListOf<Set<Branch>>()
}

/**
 * Represents an ELM node as an execution branch. A branch can have child branches, e.g. in the case
 * of If, List, In nodes.
 */
internal class Branch(val elm: Element, val children: List<Branch>) {
    @Suppress("VariableNaming") var _location: Location? = null
    val location: Location?
        get() {
            if (_location == null) {
                val locator = elm.locator
                if (locator != null) {
                    _location = Location.fromLocator(locator)
                }
            }
            return _location
        }
}

/** Used to collect the branches of an ELM tree. */
internal class BranchCollectionVisitor : BaseElmLibraryVisitor<List<Branch>, Unit>() {
    override fun visitExpression(elm: Expression, context: Unit): List<Branch> {
        return listOf(Branch(elm, super.visitExpression(elm, context)))
    }

    override fun aggregateResult(aggregate: List<Branch>, nextResult: List<Branch>): List<Branch> {
        return aggregate + nextResult
    }

    override fun defaultResult(elm: Element, context: Unit): List<Branch> {
        return listOf()
    }
}

/** Converts an ELM tree to a [Branch] tree using [BranchCollectionVisitor]. */
internal fun collectBranches(library: Library): List<Branch> {
    return library.statements?.def?.map {
        Branch(it, BranchCollectionVisitor().visitExpression(it.expression!!, Unit))
    } ?: emptyList()
}
