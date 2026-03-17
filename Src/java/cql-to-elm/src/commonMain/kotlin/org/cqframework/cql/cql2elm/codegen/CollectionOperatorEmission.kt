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

/** Emit an [ExistsExpression] as an ELM Exists node. Operand is pre-folded. */
internal fun EmissionContext.emitExists(
    expression: ExistsExpression,
    operandElm: ElmExpression,
): ElmExpression = Exists().apply { operand = operandElm }

/** Emit a [WidthExpression] as an ELM Width node. Operand is pre-folded. */
internal fun EmissionContext.emitWidth(
    expression: WidthExpression,
    operandElm: ElmExpression,
): ElmExpression = Width().apply { operand = operandElm }

/** Emit an [ElementExtractorExpression] as PointFrom or SingletonFrom. Operand is pre-folded. */
internal fun EmissionContext.emitElementExtractor(
    expression: ElementExtractorExpression,
    operandElm: ElmExpression,
): ElmExpression {
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
 * Y), LessOrEqual(X, Z))` or `And(Greater(X, Y), Less(X, Z))` for `properly between`. Children are
 * pre-folded.
 */
internal fun EmissionContext.emitBetween(
    expression: BetweenExpression,
    inputElm: ElmExpression,
    lowerElm: ElmExpression,
    upperElm: ElmExpression,
): ElmExpression {
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
 * `null as System.Quantity`. Children are pre-folded.
 */
internal fun EmissionContext.emitExpandCollapse(
    expression: org.hl7.cql.ast.ExpandCollapseExpression,
    sourceElm: ElmExpression,
    perElm: ElmExpression?,
): ElmExpression {
    val per = buildPerOperand(expression, perElm)
    val operands = mutableListOf(sourceElm, per)
    return when (expression.expandCollapseKind) {
        org.hl7.cql.ast.ExpandCollapseKind.EXPAND ->
            org.hl7.elm.r1.Expand().apply { operand = operands }
        org.hl7.cql.ast.ExpandCollapseKind.COLLAPSE ->
            org.hl7.elm.r1.Collapse().apply { operand = operands }
    }
}

private fun EmissionContext.buildPerOperand(
    expression: org.hl7.cql.ast.ExpandCollapseExpression,
    perElm: ElmExpression?,
): ElmExpression {
    // Explicit precision keyword: `expand X per day` → Quantity(1.0, "day")
    expression.perPrecision?.let { precision ->
        return org.hl7.elm.r1.Quantity().apply {
            value = org.cqframework.cql.shared.BigDecimal("1.0")
            unit = precision
        }
    }
    // Explicit per expression: `expand X per 1 '1'` — use the pre-folded ELM
    if (perElm != null) {
        // Legacy converts integer/decimal literals to Quantity(value, "1")
        if (perElm is org.hl7.elm.r1.Literal) {
            return org.hl7.elm.r1.Quantity().apply {
                value = org.cqframework.cql.shared.BigDecimal(perElm.value!!)
                unit = "1"
            }
        }
        return perElm
    }
    // Default: null (the legacy uses `buildNull(Quantity)` which produces a typed Null)
    return org.hl7.elm.r1.Null()
}

/**
 * Emit a [MembershipExpression] (in/contains) as the appropriate ELM node. Children are pre-folded.
 */
internal fun EmissionContext.emitMembership(
    expression: MembershipExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    var left = leftElm
    var right = rightElm

    val leftType = semanticModel[expression.left]
    val rightType = semanticModel[expression.right]

    // Interval<Any> expansion for membership operators:
    // In(T, Interval<Any>) → expand Interval<Any> to Interval<T>
    // Contains(Interval<Any>, T) → expand Interval<Any> to Interval<T>
    if (
        expression.operator == MembershipOperator.IN &&
            rightType is org.hl7.cql.model.IntervalType &&
            rightType.pointType.toString() == "System.Any" &&
            leftType != null
    ) {
        right = expandIntervalToPointType(right, leftType)
    } else if (
        expression.operator == MembershipOperator.CONTAINS &&
            leftType is org.hl7.cql.model.IntervalType &&
            leftType.pointType.toString() == "System.Any" &&
            rightType != null
    ) {
        left = expandIntervalToPointType(left, rightType)
    }

    val precision = expression.precision?.let { precisionStringToEnum(it) }
    return when (expression.operator) {
        MembershipOperator.IN ->
            In().apply {
                operand = mutableListOf(left, right)
                precision?.let { this.precision = it }
            }
        MembershipOperator.CONTAINS ->
            Contains().apply {
                operand = mutableListOf(left, right)
                precision?.let { this.precision = it }
            }
    }
}

/**
 * Expand an `Interval<Any>` expression by extracting Property paths and casting to the target point
 * type, producing Interval(As(T, low), lowClosed, As(T, high), highClosed).
 */
private fun EmissionContext.expandIntervalToPointType(
    intervalExpr: ElmExpression,
    targetPointType: org.hl7.cql.model.DataType,
): ElmExpression {
    val asQName = operatorRegistry.typeBuilder.dataTypeToQName(targetPointType)
    return org.hl7.elm.r1.Interval().apply {
        low =
            org.hl7.elm.r1.As().apply {
                asType = asQName
                operand =
                    org.hl7.elm.r1.Property().apply {
                        path = "low"
                        source = intervalExpr
                    }
            }
        lowClosedExpression =
            org.hl7.elm.r1.Property().apply {
                path = "lowClosed"
                source = intervalExpr
            }
        high =
            org.hl7.elm.r1.As().apply {
                asType = asQName
                operand =
                    org.hl7.elm.r1.Property().apply {
                        path = "high"
                        source = intervalExpr
                    }
            }
        highClosedExpression =
            org.hl7.elm.r1.Property().apply {
                path = "highClosed"
                source = intervalExpr
            }
    }
}
