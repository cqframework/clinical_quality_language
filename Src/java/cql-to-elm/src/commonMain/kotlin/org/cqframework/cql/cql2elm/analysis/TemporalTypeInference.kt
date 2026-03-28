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
    // operand pre-folded by catamorphism; type available from typeTable if needed
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
    // lower and upper pre-folded by catamorphism
    return type("Integer")
}

internal fun TypeResolver.inferDifferenceBetweenType(
    expression: DifferenceBetweenExpression
): DataType? {
    // lower and upper pre-folded by catamorphism
    return type("Integer")
}

internal fun TypeResolver.inferDurationOfType(expression: DurationOfExpression): DataType? {
    // operand pre-folded by catamorphism
    return type("Integer")
}

internal fun TypeResolver.inferDifferenceOfType(expression: DifferenceOfExpression): DataType? {
    // operand pre-folded by catamorphism
    return type("Integer")
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferTimeBoundaryType(
    expression: TimeBoundaryExpression,
    operandType: DataType?,
): DataType? {
    if (operandType == null) return null
    if (operandType is IntervalType) return operandType.pointType
    return operandType
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferWidthType(
    expression: WidthExpression,
    operandType: DataType?,
): DataType? {
    if (operandType == null) return null
    if (operandType is IntervalType) return operandType.pointType
    return operandType
}

@Suppress("ReturnCount")
internal fun TypeResolver.inferElementExtractorType(
    expression: ElementExtractorExpression,
    operandType: DataType?,
): DataType? {
    if (operandType == null) return null
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
    // operand pre-folded by catamorphism
    return type("Boolean")
}

internal fun TypeResolver.inferBetweenType(expression: BetweenExpression): DataType? {
    // input, lower, upper pre-folded by catamorphism
    return type("Boolean")
}

internal fun TypeResolver.inferMembershipType(expression: MembershipExpression): DataType? {
    // left, right pre-folded by catamorphism.
    val leftType = typeTable[expression.left]
    val rightType = typeTable[expression.right]

    val vsType =
        try {
            type("ValueSet")
        } catch (_: IllegalArgumentException) {
            null
        }
    val csType =
        try {
            type("CodeSystem")
        } catch (_: IllegalArgumentException) {
            null
        }

    // Terminology specialization: when one operand is a ValueSet or CodeSystem,
    // pre-decide the membership variant. No operator resolution needed — these map
    // directly to InValueSet/AnyInValueSet/InCodeSystem/AnyInCodeSystem ELM nodes.
    // This prevents the generic In resolution from recording ExpandValueSet conversions.
    if (expression.operator == org.hl7.cql.ast.MembershipOperator.IN) {
        if (rightType == vsType) {
            val kind =
                if (leftType is ListType) MembershipKind.ANY_IN_VALUE_SET
                else MembershipKind.IN_VALUE_SET
            typeTable.setMembershipKind(expression, kind)
            return type("Boolean")
        }
        if (rightType == csType) {
            val kind =
                if (leftType is ListType) MembershipKind.ANY_IN_CODE_SYSTEM
                else MembershipKind.IN_CODE_SYSTEM
            typeTable.setMembershipKind(expression, kind)
            return type("Boolean")
        }
    }
    if (expression.operator == org.hl7.cql.ast.MembershipOperator.CONTAINS) {
        if (leftType == vsType) {
            val kind =
                if (rightType is ListType) MembershipKind.ANY_IN_VALUE_SET
                else MembershipKind.IN_VALUE_SET
            typeTable.setMembershipKind(expression, kind)
            return type("Boolean")
        }
        if (leftType == csType) {
            val kind =
                if (rightType is ListType) MembershipKind.ANY_IN_CODE_SYSTEM
                else MembershipKind.IN_CODE_SYSTEM
            typeTable.setMembershipKind(expression, kind)
            return type("Boolean")
        }
    }

    // Non-terminology: set plain variant and resolve through operator map to record
    // implicit conversions (e.g., Date→DateTime for `In(Date, Interval<DateTime>)`).
    val plainKind =
        when (expression.operator) {
            org.hl7.cql.ast.MembershipOperator.IN -> MembershipKind.PLAIN_IN
            org.hl7.cql.ast.MembershipOperator.CONTAINS -> MembershipKind.PLAIN_CONTAINS
        }
    typeTable.setMembershipKind(expression, plainKind)

    if (leftType != null && rightType != null) {
        val opName =
            when (expression.operator) {
                org.hl7.cql.ast.MembershipOperator.IN -> "In"
                org.hl7.cql.ast.MembershipOperator.CONTAINS -> "Contains"
            }
        val resolution = operatorRegistry.resolve(opName, listOf(leftType, rightType))
        // Record if the resolution has non-trivial conversions:
        // - operator conversions (e.g., ToDateTime)
        // - cast + inner operator (choice narrowing: As + FHIRHelpers)
        // - list/interval conversions with inner operators (element-wise model conversion)
        if (
            resolution != null &&
                resolution.hasConversions() &&
                resolution.conversions.any { c ->
                    c != null &&
                        (c.operator != null ||
                            (c.isCast && c.conversion?.operator != null) ||
                            (c.isListConversion && c.conversion?.operator != null) ||
                            (c.isIntervalConversion && c.conversion?.operator != null))
                }
        ) {
            recordResolution(
                expression,
                resolution,
                listOf(ConversionSlot.Left, ConversionSlot.Right),
            )
            return resolution.operator.resultType
        }
    }
    return type("Boolean")
}

internal fun TypeResolver.inferExpandCollapseType(
    expression: org.hl7.cql.ast.ExpandCollapseExpression,
    operandType: DataType?,
): DataType? {
    // per expression pre-folded by catamorphism
    // Expand/Collapse return the same type as their operand (list of intervals)
    return operandType
}

@Suppress("ReturnCount", "CyclomaticComplexMethod")
internal fun TypeResolver.inferIntervalRelationType(
    expression: IntervalRelationExpression
): DataType? {
    // left, right pre-folded by catamorphism
    val leftType = typeTable[expression.left]
    val rightType = typeTable[expression.right]

    // Resolve through the OperatorMap ONLY when both operands are intervals with different
    // point types (interval type promotion, e.g., Interval<Date> → Interval<DateTime>).
    // For other cases (scalar/null operands, same-type intervals), the existing lowering +
    // CoercionInserter path handles everything correctly.
    val opName = intervalPhraseToOperatorName(expression.phrase)
    if (
        opName != null &&
            leftType is org.hl7.cql.model.IntervalType &&
            rightType is org.hl7.cql.model.IntervalType &&
            leftType.pointType != rightType.pointType
    ) {
        val effectiveLeft =
            conversionTable?.effectiveType(
                expression,
                ConversionSlot.Left,
                leftType,
                operatorRegistry,
            ) ?: leftType
        val effectiveRight =
            conversionTable?.effectiveType(
                expression,
                ConversionSlot.Right,
                rightType,
                operatorRegistry,
            ) ?: rightType
        val resolution = operatorRegistry.resolve(opName, listOf(effectiveLeft, effectiveRight))
        if (resolution != null) {
            recordResolution(
                expression,
                resolution,
                listOf(ConversionSlot.Left, ConversionSlot.Right),
            )
            return resolution.operator.resultType
        }
    }

    // Fallback: basic validation for includes/includedIn (at least one collection operand required)
    val phrase = expression.phrase
    if (
        phrase is org.hl7.cql.ast.IncludesIntervalPhrase ||
            phrase is org.hl7.cql.ast.IncludedInIntervalPhrase
    ) {
        val leftIsCollection =
            leftType is org.hl7.cql.model.ListType || leftType is org.hl7.cql.model.IntervalType
        val rightIsCollection =
            rightType is org.hl7.cql.model.ListType || rightType is org.hl7.cql.model.IntervalType
        if (!leftIsCollection && !rightIsCollection) return null
    }

    return type("Boolean")
}

/**
 * Map an interval phrase to the system operator name used for resolution. Returns null if the
 * phrase type doesn't have a direct operator mapping (e.g., complex phrases that are lowered into
 * operator trees before emission).
 */
private fun intervalPhraseToOperatorName(phrase: org.hl7.cql.ast.IntervalOperatorPhrase): String? =
    when (phrase) {
        is org.hl7.cql.ast.IncludedInIntervalPhrase ->
            if (phrase.proper) "ProperIncludedIn" else "IncludedIn"
        is org.hl7.cql.ast.IncludesIntervalPhrase ->
            if (phrase.proper) "ProperIncludes" else "Includes"
        is org.hl7.cql.ast.ConcurrentIntervalPhrase -> null // lowered to boundary comparisons
        is org.hl7.cql.ast.BeforeOrAfterIntervalPhrase -> null // normalized by Lowering
        is org.hl7.cql.ast.WithinIntervalPhrase -> null // normalized by Lowering
        else -> null
    }
