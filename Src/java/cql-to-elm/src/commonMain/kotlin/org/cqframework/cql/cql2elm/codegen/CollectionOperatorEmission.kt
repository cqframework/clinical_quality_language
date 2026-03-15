package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.ElementExtractorExpression
import org.hl7.cql.ast.ElementExtractorKind
import org.hl7.cql.ast.ExistsExpression
import org.hl7.cql.ast.IntervalLiteral
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.MembershipOperator
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.TypeExtentKind
import org.hl7.cql.ast.WidthExpression
import org.hl7.elm.r1.And
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.Exists
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.LessOrEqual
import org.hl7.elm.r1.MaxValue
import org.hl7.elm.r1.MinValue
import org.hl7.elm.r1.PointFrom
import org.hl7.elm.r1.SingletonFrom
import org.hl7.elm.r1.Width

/** Emit an [ExistsExpression] as an ELM Exists node. */
internal fun EmissionContext.emitExists(expression: ExistsExpression): ElmExpression {
    return Exists().apply { operand = emitExpression(expression.operand) }
}

/** Emit a [WidthExpression] as an ELM Width node. */
internal fun EmissionContext.emitWidth(expression: WidthExpression): ElmExpression {
    return Width().apply { operand = emitExpression(expression.operand) }
}

/** Emit an [ElementExtractorExpression] as PointFrom or SingletonFrom. */
internal fun EmissionContext.emitElementExtractor(
    expression: ElementExtractorExpression
): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return when (expression.elementExtractorKind) {
        ElementExtractorKind.POINT -> PointFrom().apply { operand = operandElm }
        ElementExtractorKind.SINGLETON -> SingletonFrom().apply { operand = operandElm }
    }
}

/** Emit a [TypeExtentExpression] (minimum/maximum Type) as MinValue or MaxValue. */
internal fun EmissionContext.emitTypeExtent(expression: TypeExtentExpression): ElmExpression {
    val typeName = expression.type.name.simpleName
    val valueType = QName(typesNamespace, typeName)
    return when (expression.typeExtentKind) {
        TypeExtentKind.MINIMUM -> MinValue().apply { this.valueType = valueType }
        TypeExtentKind.MAXIMUM -> MaxValue().apply { this.valueType = valueType }
    }
}

/**
 * Emit a [BetweenExpression] (`X between Y and Z`). When the input is an interval-typed expression,
 * the legacy translator emits `IncludedIn(input, Interval[lower, upper])`. For scalar inputs, it
 * emits `And(GreaterOrEqual(X, Y), LessOrEqual(X, Z))`.
 *
 * The legacy has a bug where `isProper` is always false (it checks `ctx.getChild(0).text ==
 * "properly"` but child 0 is the expression, not the keyword). We match this behavior for parity.
 */
internal fun EmissionContext.emitBetween(expression: BetweenExpression): ElmExpression {
    val inputElm = emitExpression(expression.input)
    val lowerElm = emitExpression(expression.lower)
    val upperElm = emitExpression(expression.upper)

    // If input is an Interval, emit IncludedIn(input, Interval[lower, upper])
    val inputIsInterval =
        expression.input is LiteralExpression &&
            (expression.input as LiteralExpression).literal is IntervalLiteral
    if (inputIsInterval) {
        val boundsInterval =
            Interval().apply {
                low = lowerElm
                high = upperElm
                lowClosed = true
                highClosed = true
            }
        return IncludedIn().apply { operand = mutableListOf(inputElm, boundsInterval) }
    }

    // NOTE: properly flag intentionally ignored to match legacy bug (see comment above)
    val leftCmp = GreaterOrEqual().apply { operand = mutableListOf(inputElm, lowerElm) }
    val rightCmp = LessOrEqual().apply { operand = mutableListOf(inputElm, upperElm) }
    return And().apply { operand = mutableListOf(leftCmp, rightCmp) }
}

/** Emit a [MembershipExpression] (in/contains) as the appropriate ELM node. */
internal fun EmissionContext.emitMembership(expression: MembershipExpression): ElmExpression {
    val leftElm = emitExpression(expression.left)
    val rightElm = emitExpression(expression.right)
    val precision = expression.precision?.let { precisionStringToEnum(it) }
    return when (expression.operator) {
        MembershipOperator.IN ->
            In().apply {
                operand = mutableListOf(leftElm, rightElm)
                precision?.let { this.precision = it }
            }
        MembershipOperator.CONTAINS ->
            Contains().apply {
                operand = mutableListOf(leftElm, rightElm)
                precision?.let { this.precision = it }
            }
    }
}
