package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.Except
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.Intersect
import org.hl7.elm.r1.Union

/** Emit a set operator (union/intersect/except) as the corresponding ELM NaryExpression. */
internal fun EmissionContext.emitSetOperator(expression: OperatorBinaryExpression): ElmExpression {
    val leftElm = emitExpression(expression.left)
    val rightElm = emitExpression(expression.right)
    val operands = mutableListOf(leftElm, rightElm)
    return when (expression.operator) {
        BinaryOperator.UNION -> Union().apply { operand = operands }
        BinaryOperator.INTERSECT -> Intersect().apply { operand = operands }
        BinaryOperator.EXCEPT -> Except().apply { operand = operands }
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "Set operator '${expression.operator.name}' is not supported."
            )
    }
}

/** Emit a [ListTransformExpression] (distinct/flatten) as the corresponding ELM unary node. */
internal fun EmissionContext.emitListTransform(expression: ListTransformExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return when (expression.listTransformKind) {
        ListTransformKind.DISTINCT -> Distinct().apply { operand = operandElm }
        ListTransformKind.FLATTEN -> Flatten().apply { operand = operandElm }
    }
}
