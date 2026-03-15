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
    expression: DateTimeComponentExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.operand, typeTable, symbolTable)
    return when (expression.component) {
        DateTimeComponent.DATE -> type("Date")
        DateTimeComponent.TIME -> type("Time")
        DateTimeComponent.TIMEZONE_OFFSET -> type("Decimal")
        else -> type("Integer")
    }
}

internal fun TypeResolver.inferDurationBetweenType(
    expression: DurationBetweenExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.lower, typeTable, symbolTable)
    inferType(expression.upper, typeTable, symbolTable)
    return type("Integer")
}

internal fun TypeResolver.inferDifferenceBetweenType(
    expression: DifferenceBetweenExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.lower, typeTable, symbolTable)
    inferType(expression.upper, typeTable, symbolTable)
    return type("Integer")
}

internal fun TypeResolver.inferDurationOfType(
    expression: DurationOfExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.operand, typeTable, symbolTable)
    return type("Integer")
}

internal fun TypeResolver.inferDifferenceOfType(
    expression: DifferenceOfExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.operand, typeTable, symbolTable)
    return type("Integer")
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferTimeBoundaryType(
    expression: TimeBoundaryExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
    if (operandType is IntervalType) return operandType.pointType
    return operandType
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferWidthType(
    expression: WidthExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
    if (operandType is IntervalType) return operandType.pointType
    return operandType
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferElementExtractorType(
    expression: ElementExtractorExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
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

internal fun TypeResolver.inferExistsType(
    expression: ExistsExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.operand, typeTable, symbolTable)
    return type("Boolean")
}

internal fun TypeResolver.inferBetweenType(
    expression: BetweenExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.input, typeTable, symbolTable)
    inferType(expression.lower, typeTable, symbolTable)
    inferType(expression.upper, typeTable, symbolTable)
    return type("Boolean")
}

internal fun TypeResolver.inferMembershipType(
    expression: MembershipExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.left, typeTable, symbolTable)
    inferType(expression.right, typeTable, symbolTable)
    return type("Boolean")
}

internal fun TypeResolver.inferIntervalRelationType(
    expression: IntervalRelationExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    inferType(expression.left, typeTable, symbolTable)
    inferType(expression.right, typeTable, symbolTable)
    return type("Boolean")
}
