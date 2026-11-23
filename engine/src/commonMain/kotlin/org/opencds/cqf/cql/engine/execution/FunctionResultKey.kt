package org.opencds.cqf.cql.engine.execution

import org.hl7.elm.r1.FunctionDef
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.SimpleValue
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.identityHashCode

/**
 * The inputs that stay fixed for an evaluation and that a function result can depend on: the
 * parameter values supplied by the caller and the evaluation date/time (`Now()`).
 */
class EvaluationInputs(parameters: Map<String, Value?>, private val evaluationDateTime: DateTime?) {
    private val parameters = parameters.mapValues { memoKeyOf(it.value) }
    private val hash = listOf(this.parameters, evaluationDateTime).hashCode()

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is EvaluationInputs &&
                hash == other.hash &&
                evaluationDateTime == other.evaluationDateTime &&
                parameters == other.parameters)
}

/** Identifies the result of calling [functionDef] with the given arguments. */
class FunctionResultKey(
    private val functionDef: FunctionDef,
    private val context: String?,
    private val contextValue: String?,
    private val inputs: EvaluationInputs,
    arguments: List<Value?>,
) {
    private val arguments = arguments.map(::memoKeyOf)
    private val hash =
        listOf(identityHashCode(functionDef), context, contextValue, inputs, this.arguments)
            .hashCode()

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is FunctionResultKey &&
                hash == other.hash &&
                functionDef === other.functionDef &&
                context == other.context &&
                contextValue == other.contextValue &&
                inputs == other.inputs &&
                arguments == other.arguments)
}

/**
 * Returns an object whose equality implies that [value] and the other value are interchangeable as
 * function arguments.
 */
private fun memoKeyOf(value: Value?): Any? =
    when (value) {
        null -> null
        // Data classes and DateTime compare everything observable (Decimal scale, DateTime
        // precision); Date and Time do not override equals and so compare by reference
        is SimpleValue -> value
        // Intervals are routinely constructed anew for every call
        is Interval ->
            IntervalKey(
                memoKeyOf(value.low),
                value.lowClosed,
                memoKeyOf(value.high),
                value.highClosed,
                value.isUncertain,
            )
        // Structural comparison of a FHIR resource can cost as much as the call itself, and
        // the structural equality of some values ignores parts of them (e.g. Concept.display)
        else -> IdentityKey(value)
    }

private data class IntervalKey(
    val low: Any?,
    val lowClosed: Boolean,
    val high: Any?,
    val highClosed: Boolean,
    val isUncertain: Boolean,
)

private class IdentityKey(val value: Value) {
    override fun hashCode(): Int = identityHashCode(value)

    override fun equals(other: Any?): Boolean = other is IdentityKey && other.value === value
}
