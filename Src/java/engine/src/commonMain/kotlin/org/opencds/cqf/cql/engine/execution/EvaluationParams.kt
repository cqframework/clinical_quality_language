package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.util.ZonedDateTime

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
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
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
         * @param libraryParams Library parameters containing expressions to evaluate.
         */
        fun library(id: VersionedIdentifier, libraryParams: LibraryParams) {
            expressions[id] = libraryParams.expressions
        }

        /** Shorthand for adding a library by name. */
        @JsName("libraryByName")
        fun library(libraryName: String, libraryParams: LibraryParams) {
            library(VersionedIdentifier().apply { id = libraryName }, libraryParams)
        }

        /** Adds a library to the evaluation. */
        @JsExport.Ignore
        fun library(id: VersionedIdentifier, block: (LibraryParams.Builder.() -> Unit)? = null) {
            val builder = LibraryParams.Builder()
            if (block != null) {
                builder.apply(block)
            }
            library(id, builder.build())
        }

        /** Shorthand for adding a library by name. */
        @JsExport.Ignore
        fun library(libraryName: String, block: (LibraryParams.Builder.() -> Unit)? = null) {
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

    /**
     * Parameters for a specific library's evaluation.
     *
     * @property expressions The list of expression refs to evaluate for this library. If the value
     *   is null, all expressions in the library will be evaluated.
     */
    class LibraryParams(val expressions: List<EvaluationExpressionRef>?) {
        class Builder {
            private var expressions: MutableList<EvaluationExpressionRef>? = null

            /** Adds expression refs to be evaluated. */
            fun expressions(vararg refs: EvaluationExpressionRef) {
                if (expressions == null) {
                    expressions = mutableListOf()
                }
                expressions!!.addAll(refs)
            }

            /** Adds expressions by name. */
            @JsExport.Ignore
            fun expressions(names: Iterable<String>) {
                if (expressions == null) {
                    expressions = mutableListOf()
                }
                for (name in names) {
                    expressions!!.add(EvaluationExpressionRef(name))
                }
            }

            /** Adds expressions by name. */
            @JsName("expressionsByName")
            fun expressions(vararg names: String) {
                if (expressions == null) {
                    expressions = mutableListOf()
                }
                for (name in names) {
                    expressions!!.add(EvaluationExpressionRef(name))
                }
            }

            fun build(): LibraryParams {
                return LibraryParams(expressions)
            }
        }
    }
}
