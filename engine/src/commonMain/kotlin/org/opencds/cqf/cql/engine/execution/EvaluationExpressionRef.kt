package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm.r1.TypeSpecifier
import org.opencds.cqf.cql.engine.runtime.Value

/** Represents a reference to an expression or function to be evaluated. */
@JsOnlyExport open class EvaluationExpressionRef(val name: String)

/**
 * Represents a reference to a function to be evaluated, including its signature and arguments.
 * Signature is only required when there are multiple overloads of the function.
 */
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class EvaluationFunctionRef(
    name: String,
    val signature: List<TypeSpecifier>?,
    val arguments: List<Value?>,
) : EvaluationExpressionRef(name)
