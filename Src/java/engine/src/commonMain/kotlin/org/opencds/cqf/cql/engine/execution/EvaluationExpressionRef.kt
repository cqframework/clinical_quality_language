package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm.r1.TypeSpecifier

/** Represents a reference to an expression or function to be evaluated. */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
open class EvaluationExpressionRef(val name: String)

/**
 * Represents a reference to a function to be evaluated, including its signature and arguments.
 * Signature is only required when there are multiple overloads of the function.
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class EvaluationFunctionRef(
    name: String,
    val signature: List<TypeSpecifier>?,
    val arguments: List<Any?>,
) : EvaluationExpressionRef(name)
