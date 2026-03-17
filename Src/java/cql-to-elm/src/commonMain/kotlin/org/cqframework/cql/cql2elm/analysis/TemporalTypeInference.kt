package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.DateTimeComponent
import org.hl7.cql.ast.DateTimeComponentExpression
import org.hl7.cql.ast.DifferenceBetweenExpression
import org.hl7.cql.ast.DifferenceOfExpression
import org.hl7.cql.ast.DurationBetweenExpression
import org.hl7.cql.ast.DurationOfExpression
import org.hl7.cql.ast.ElementExtractorExpression
import org.hl7.cql.ast.ExistsExpression
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

@Suppress("CyclomaticComplexMethod")
internal fun TypeResolver.inferDateTimeComponentType(
    expression: DateTimeComponentExpression
): DataType? {
    inferType(expression.operand)
    return when (expression.component) {
        DateTimeComponent.DATE -> type("Date")
        DateTimeComponent.TIME -> type("Time")
        DateTimeComponent.TIMEZONE_OFFSET -> type("Decimal")
        else -> type("Integer")
    }
}

internal fun TypeResolver.inferDurationBetweenType(
    expression: DurationBetweenExpression
): DataType? {
    inferType(expression.lower)
    inferType(expression.upper)
    return type("Integer")
}

internal fun TypeResolver.inferDifferenceBetweenType(
    expression: DifferenceBetweenExpression
): DataType? {
    inferType(expression.lower)
    inferType(expression.upper)
    return type("Integer")
}

internal fun TypeResolver.inferDurationOfType(expression: DurationOfExpression): DataType? {
    inferType(expression.operand)
    return type("Integer")
}

internal fun TypeResolver.inferDifferenceOfType(expression: DifferenceOfExpression): DataType? {
    inferType(expression.operand)
    return type("Integer")
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferTimeBoundaryType(expression: TimeBoundaryExpression): DataType? {
    val operandType = inferType(expression.operand) ?: return null
    if (operandType is IntervalType) return operandType.pointType
    return operandType
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferWidthType(expression: WidthExpression): DataType? {
    val operandType = inferType(expression.operand) ?: return null
    if (operandType is IntervalType) return operandType.pointType
    return operandType
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferElementExtractorType(
    expression: ElementExtractorExpression
): DataType? {
    val operandType = inferType(expression.operand) ?: return null
    return when (expression.elementExtractorKind) {
        org.hl7.cql.ast.ElementExtractorKind.SINGLETON -> {
            if (operandType is ListType) operandType.elementType else operandType
        }
        org.hl7.cql.ast.ElementExtractorKind.POINT -> {
            if (operandType is IntervalType) operandType.pointType else operandType
        }
    }
}

internal fun TypeResolver.inferTypeExtentType(expression: TypeExtentExpression): DataType? {
    return resolveTypeSpecifier(expression.type)
}

internal fun TypeResolver.inferExistsType(expression: ExistsExpression): DataType? {
    inferType(expression.operand)
    return type("Boolean")
}

internal fun TypeResolver.inferBetweenType(expression: BetweenExpression): DataType? {
    inferType(expression.input)
    inferType(expression.lower)
    inferType(expression.upper)
    return type("Boolean")
}

internal fun TypeResolver.inferMembershipType(expression: MembershipExpression): DataType? {
    inferType(expression.left)
    inferType(expression.right)
    return type("Boolean")
}

internal fun TypeResolver.inferExpandCollapseType(
    expression: org.hl7.cql.ast.ExpandCollapseExpression
): DataType? {
    val operandType = inferType(expression.operand) ?: return null
    expression.perExpression?.let { inferType(it) }
    // Expand/Collapse return the same type as their operand (list of intervals)
    return operandType
}

internal fun TypeResolver.inferIntervalRelationType(
    expression: IntervalRelationExpression
): DataType? {
    inferType(expression.left)
    inferType(expression.right)
    return type("Boolean")
}
