package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.DebugMap

/**
 * Parameters for the engine's evaluation request.
 *
 * @property libraryIdentifiers List of the libraries to be evaluated.
 * @property expressionRefs If null, all expressions from each library will be evaluated. Otherwise,
 *   only the referenced expressions and functions will be evaluated.
 * @property contextParameter The context parameter name and its value.
 * @property parameters Library parameters for evaluation.
 * @property debugMap Captures debug information during evaluation.
 * @property evaluationDateTime Represents the evaluation date and time.
 */
class EvaluationParams(
    val libraryIdentifiers: List<VersionedIdentifier>,
    val expressionRefs: List<EvaluationExpressionRef>? = null,
    val contextParameter: Pair<String, Any>? = null,
    val parameters: Map<String, Any?>? = null,
    val debugMap: DebugMap? = null,
    val evaluationDateTime: ZonedDateTime? = null,
) {
    class Builder {
        private val libraryIdentifiers = mutableListOf<VersionedIdentifier>()
        private var expressionRefs: MutableList<EvaluationExpressionRef>? = null

        var contextParameter: Pair<String, Any>? = null
        var parameters: Map<String, Any?>? = null
        var debugMap: DebugMap? = null
        var evaluationDateTime: ZonedDateTime? = null

        fun library(id: VersionedIdentifier) = apply { libraryIdentifiers.add(id) }

        fun library(libraryName: String) = library(VersionedIdentifier().apply { id = libraryName })

        fun expressionRef(ref: EvaluationExpressionRef) = apply {
            if (this.expressionRefs == null) {
                this.expressionRefs = mutableListOf()
            }
            this.expressionRefs!!.add(ref)
        }

        fun expression(name: String) = expressionRef(EvaluationExpressionRef(name))

        fun build(): EvaluationParams {
            return EvaluationParams(
                libraryIdentifiers = libraryIdentifiers,
                expressionRefs = expressionRefs,
                contextParameter = contextParameter,
                parameters = parameters,
                debugMap = debugMap,
                evaluationDateTime = evaluationDateTime,
            )
        }
    }
}
