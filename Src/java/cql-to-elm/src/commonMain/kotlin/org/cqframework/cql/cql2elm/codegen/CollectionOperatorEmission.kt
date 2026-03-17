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
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.Less
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
 * emit `IncludedIn(input, Interval[lower, upper])`. For scalar inputs, emit `And(GreaterOrEqual(X,
 * Y), LessOrEqual(X, Z))` or `And(Greater(X, Y), Less(X, Z))` for `properly between`.
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

    val leftCmp =
        if (expression.properly) Greater().apply { operand = mutableListOf(inputElm, lowerElm) }
        else GreaterOrEqual().apply { operand = mutableListOf(inputElm, lowerElm) }
    val rightCmp =
        if (expression.properly) Less().apply { operand = mutableListOf(inputElm, upperElm) }
        else LessOrEqual().apply { operand = mutableListOf(inputElm, upperElm) }
    return And().apply { operand = mutableListOf(leftCmp, rightCmp) }
}

/**
 * Emit an [ExpandCollapseExpression] as an ELM [Expand] or [Collapse] binary expression. The legacy
 * translator always emits two operands: [source, per]. When no per is specified, per defaults to
 * `null as System.Quantity`.
 */
internal fun EmissionContext.emitExpandCollapse(
    expression: org.hl7.cql.ast.ExpandCollapseExpression
): ElmExpression {
    val sourceElm = emitExpression(expression.operand)
    val perElm = buildPerOperand(expression)
    val operands = mutableListOf(sourceElm, perElm)
    return when (expression.expandCollapseKind) {
        org.hl7.cql.ast.ExpandCollapseKind.EXPAND ->
            org.hl7.elm.r1.Expand().apply { operand = operands }
        org.hl7.cql.ast.ExpandCollapseKind.COLLAPSE ->
            org.hl7.elm.r1.Collapse().apply { operand = operands }
    }
}

private fun EmissionContext.buildPerOperand(
    expression: org.hl7.cql.ast.ExpandCollapseExpression
): ElmExpression {
    // Explicit precision keyword: `expand X per day` → Quantity(1.0, "day")
    expression.perPrecision?.let { precision ->
        return org.hl7.elm.r1.Quantity().apply {
            value = org.cqframework.cql.shared.BigDecimal("1.0")
            unit = precision
        }
    }
    // Explicit per expression: `expand X per 1 '1'`
    expression.perExpression?.let { perExpr ->
        val emitted = emitExpression(perExpr)
        // Legacy converts integer/decimal literals to Quantity(value, "1")
        if (emitted is org.hl7.elm.r1.Literal) {
            return org.hl7.elm.r1.Quantity().apply {
                value = org.cqframework.cql.shared.BigDecimal(emitted.value!!)
                unit = "1"
            }
        }
        return emitted
    }
    // Default: null (the legacy uses `buildNull(Quantity)` which produces a typed Null)
    return org.hl7.elm.r1.Null()
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
