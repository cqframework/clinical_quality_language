package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.DebugMap

/**
 * Parameters for the engine's evaluation request.
 *
 * @property expressions The key is the library identifier and the value is the list of expression
 *   refs to evaluate for that library. If the value is null, all expressions in that library will
 *   be evaluated.
 * @property contextParameter The context parameter name and its value.
 * @property parameters Library parameters for evaluation.
 * @property debugMap Captures debug information during evaluation.
 * @property evaluationDateTime Represents the evaluation date and time.
 */
class EvaluationParams(
    val expressions: Map<VersionedIdentifier, List<EvaluationExpressionRef>?>,
    val contextParameter: Pair<String, Any?>? = null,
    val parameters: Map<String, Any?>? = null,
    val debugMap: DebugMap? = null,
    val evaluationDateTime: ZonedDateTime? = null,
) {
    class Builder {
        private val expressions =
            mutableMapOf<VersionedIdentifier, List<EvaluationExpressionRef>?>()

        var contextParameter: Pair<String, Any?>? = null
        var parameters: Map<String, Any?>? = null
        var debugMap: DebugMap? = null
        var evaluationDateTime: ZonedDateTime? = null

        /**
         * Adds a library to the evaluation.
         *
         * @param id The VersionedIdentifier of the library.
         * @param block A DSL block to define specific expressions.
         */
        fun library(id: VersionedIdentifier, block: (LibraryScope.() -> Unit)? = null) = apply {
            if (block == null) {
                expressions[id] = null
            } else {
                val scope = LibraryScope()
                scope.block()
                expressions[id] = scope.build()
            }
        }

        /** Shorthand for adding a library by name. */
        fun library(libraryName: String, block: (LibraryScope.() -> Unit)? = null) {
            library(VersionedIdentifier().apply { id = libraryName }, block)
        }

        fun build(): EvaluationParams {
            return EvaluationParams(
                expressions = expressions,
                contextParameter = contextParameter,
                parameters = parameters,
                debugMap = debugMap,
                evaluationDateTime = evaluationDateTime,
            )
        }
    }

    /** Scopes expression calls within a library block. */
    class LibraryScope {
        private val expressions = mutableListOf<EvaluationExpressionRef>()

        /** Adds expression refs to be evaluated. */
        fun expressions(vararg refs: EvaluationExpressionRef) {
            expressions.addAll(refs)
        }

        /** Adds expressions by name. */
        fun expressions(names: Iterable<String>) {
            for (name in names) {
                expressions.add(EvaluationExpressionRef(name))
            }
        }

        /** Adds expressions by name. */
        fun expressions(vararg names: String) {
            for (name in names) {
                expressions.add(EvaluationExpressionRef(name))
            }
        }

        fun build(): List<EvaluationExpressionRef> = expressions
    }
}
