package org.hl7.cql.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Identifies a location within a CQL source. */
@Serializable
data class Locator(
    val sourceId: String? = null,
    val startIndex: Int = -1,
    val stopIndex: Int = -1,
    val line: Int? = null,
    val column: Int? = null,
) {
    companion object {
        val UNKNOWN = Locator()
    }
}

/** Base contract implemented by every AST node. */
interface AstNode {
    val locator: Locator
}

@Serializable
enum class ProblemSeverity {
    @SerialName("error") ERROR,
    @SerialName("warning") WARNING,
    @SerialName("info") INFO,
}

@Serializable
data class Problem(
    val message: String,
    override val locator: Locator = Locator.UNKNOWN,
    val severity: ProblemSeverity = ProblemSeverity.ERROR,
) : AstNode

@Serializable data class LibraryResult(val library: Library, val problems: List<Problem>)

@Serializable data class ExpressionResult(val expression: Expression, val problems: List<Problem>)
