package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.elm.r1.As
import org.hl7.elm.r1.ConvertsToBoolean
import org.hl7.elm.r1.ConvertsToDate
import org.hl7.elm.r1.ConvertsToDateTime
import org.hl7.elm.r1.ConvertsToDecimal
import org.hl7.elm.r1.ConvertsToInteger
import org.hl7.elm.r1.ConvertsToLong
import org.hl7.elm.r1.ConvertsToQuantity
import org.hl7.elm.r1.ConvertsToRatio
import org.hl7.elm.r1.ConvertsToString
import org.hl7.elm.r1.ConvertsToTime
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.ToBoolean
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToDate
import org.hl7.elm.r1.ToDateTime
import org.hl7.elm.r1.ToDecimal
import org.hl7.elm.r1.ToInteger
import org.hl7.elm.r1.ToLong
import org.hl7.elm.r1.ToQuantity
import org.hl7.elm.r1.ToRatio
import org.hl7.elm.r1.ToString
import org.hl7.elm.r1.ToTime

/** Emit an [IsExpression] as an ELM [Is] node with isTypeSpecifier set. Operand is pre-folded. */
internal fun EmissionContext.emitIsExpression(
    expression: IsExpression,
    operandElm: ElmExpression,
): ElmExpression {
    val isNode =
        Is().apply {
            operand = operandElm
            isTypeSpecifier = emitTypeSpecifier(expression.type)
        }
    // The `negated` field is not produced by the grammar for type tests (`is not null/true/false`
    // is handled by BooleanTestExpression), but handle it defensively.
    if (expression.negated) {
        return org.hl7.elm.r1.Not().apply { operand = isNode }
    }
    return isNode
}

/**
 * Emit an [AsExpression] as an ELM [As] node.
 * - Implicit casts (inserted by ConversionInserter for null wrapping): emit with `asType` (a QName)
 *   for named types or `asTypeSpecifier` for complex types. No `strict` field. This matches legacy
 *   behavior for internally generated null-As nodes.
 * - Explicit casts (user-written `x as T`): emit with `asTypeSpecifier` and `strict = false`.
 */
/** Emit an explicit user-written `x as T` expression. */
internal fun EmissionContext.emitAsExpression(
    expression: AsExpression,
    operandElm: ElmExpression,
): ElmExpression {
    return As().apply {
        operand = operandElm
        asTypeSpecifier = emitTypeSpecifier(expression.type)
        strict = false
    }
}

/** Emit an implicit cast (from analysis/normalization). Uses asType for named types, no strict. */
internal fun EmissionContext.emitImplicitCastExpression(
    expression: org.hl7.cql.ast.ImplicitCastExpression,
    operandElm: ElmExpression,
): ElmExpression {
    val typeSpec = expression.type
    return As().apply {
        operand = operandElm
        if (typeSpec is NamedTypeSpecifier) {
            asType = resolveTypeQName(typeSpec.name)
        } else {
            asTypeSpecifier = emitTypeSpecifier(typeSpec)
        }
    }
}

/** Emit a [CastExpression] as an ELM [As] node with strict = true. Operand is pre-folded. */
internal fun EmissionContext.emitCastExpression(
    expression: CastExpression,
    operandElm: ElmExpression,
): ElmExpression {
    return As().apply {
        operand = operandElm
        asTypeSpecifier = emitTypeSpecifier(expression.type)
        strict = true
    }
}

/**
 * Emit a [ConversionExpression] (`convert X to Y`) as the appropriate ELM conversion function node
 * (e.g., ToString, ToDecimal) for system types, or as an [As] cast for class/tuple conversions. The
 * legacy translator resolves the conversion via the ConversionMap and emits the corresponding
 * function operator or cast. Operand is pre-folded.
 */
internal fun EmissionContext.emitConversionExpression(
    expression: ConversionExpression,
    operandElm: ElmExpression,
): ElmExpression {
    if (expression.destinationUnit != null) {
        return org.hl7.elm.r1.ConvertQuantity().apply {
            operand =
                mutableListOf(
                    operandElm,
                    org.hl7.elm.r1.Literal().apply {
                        valueType =
                            org.cqframework.cql.shared.QName("urn:hl7-org:elm-types:r1", "String")
                        value = expression.destinationUnit
                    },
                )
        }
    }
    val destType = expression.destinationType
    if (destType is NamedTypeSpecifier) {
        val conversionName = typeNameToConversionOperator(destType.name.simpleName)
        if (conversionName != null) {
            return createConversionElm(conversionName, operandElm)
        }
        // For non-system types (e.g., class/tuple conversions), emit as an As (cast) node.
        // The legacy translator resolves these as cast conversions via buildAs, which uses
        // asType (QName) for named types. No strict flag is set.
        return As().apply {
            operand = operandElm
            asType = resolveTypeQName(destType.name)
        }
    }
    // Non-named destination types (e.g., TupleTypeSpecifier): emit as cast with asTypeSpecifier
    if (destType != null) {
        return As().apply {
            operand = operandElm
            asTypeSpecifier = emitTypeSpecifier(destType)
        }
    }
    throw ElmEmitter.UnsupportedNodeException(
        "ConversionExpression with no destination type is not supported."
    )
}

/** Convert an AST [org.hl7.cql.ast.TypeSpecifier] to an ELM [org.hl7.elm.r1.TypeSpecifier]. */
internal fun EmissionContext.emitTypeSpecifier(
    typeSpec: org.hl7.cql.ast.TypeSpecifier
): org.hl7.elm.r1.TypeSpecifier {
    return when (typeSpec) {
        is NamedTypeSpecifier -> {
            val elmTypeSpec = org.hl7.elm.r1.NamedTypeSpecifier()
            elmTypeSpec.name = resolveTypeQName(typeSpec.name)
            elmTypeSpec
        }
        is org.hl7.cql.ast.ListTypeSpecifier ->
            org.hl7.elm.r1.ListTypeSpecifier().apply {
                elementType = emitTypeSpecifier(typeSpec.elementType)
            }
        is org.hl7.cql.ast.IntervalTypeSpecifier ->
            org.hl7.elm.r1.IntervalTypeSpecifier().apply {
                pointType = emitTypeSpecifier(typeSpec.pointType)
            }
        is org.hl7.cql.ast.ChoiceTypeSpecifier ->
            org.hl7.elm.r1.ChoiceTypeSpecifier().apply {
                choice = typeSpec.choices.map { emitTypeSpecifier(it) }.toMutableList()
            }
        is org.hl7.cql.ast.TupleTypeSpecifier ->
            org.hl7.elm.r1.TupleTypeSpecifier().apply {
                element =
                    typeSpec.elements
                        .map { elem ->
                            org.hl7.elm.r1.TupleElementDefinition().apply {
                                name = elem.name.value
                                elementType = emitTypeSpecifier(elem.type)
                            }
                        }
                        .toMutableList()
            }
    }
}

/** Map a simple type name (e.g., "String") to the corresponding conversion operator name. */
private fun typeNameToConversionOperator(typeName: String): String? =
    when (typeName) {
        "String" -> "ToString"
        "Boolean" -> "ToBoolean"
        "Integer" -> "ToInteger"
        "Long" -> "ToLong"
        "Decimal" -> "ToDecimal"
        "Date" -> "ToDate"
        "DateTime" -> "ToDateTime"
        "Time" -> "ToTime"
        "Quantity" -> "ToQuantity"
        "Ratio" -> "ToRatio"
        "Concept" -> "ToConcept"
        else -> null
    }

/**
 * Create the appropriate ELM conversion/ConvertsTo unary expression node. This is shared by
 * [wrapConversion] (implicit conversions) and explicit conversion emission.
 */
@Suppress("CyclomaticComplexMethod")
internal fun createConversionElm(operatorName: String, operand: ElmExpression): ElmExpression {
    return when (operatorName) {
        "ToString" -> ToString().apply { this.operand = operand }
        "ToBoolean" -> ToBoolean().apply { this.operand = operand }
        "ToInteger" -> ToInteger().apply { this.operand = operand }
        "ToLong" -> ToLong().apply { this.operand = operand }
        "ToDecimal" -> ToDecimal().apply { this.operand = operand }
        "ToDate" -> ToDate().apply { this.operand = operand }
        "ToDateTime" -> ToDateTime().apply { this.operand = operand }
        "ToTime" -> ToTime().apply { this.operand = operand }
        "ToQuantity" -> ToQuantity().apply { this.operand = operand }
        "ToRatio" -> ToRatio().apply { this.operand = operand }
        "ToConcept" -> ToConcept().apply { this.operand = operand }
        "ExpandValueSet" -> org.hl7.elm.r1.ExpandValueSet().apply { this.operand = operand }
        "ConvertsToString" -> ConvertsToString().apply { this.operand = operand }
        "ConvertsToBoolean" -> ConvertsToBoolean().apply { this.operand = operand }
        "ConvertsToInteger" -> ConvertsToInteger().apply { this.operand = operand }
        "ConvertsToLong" -> ConvertsToLong().apply { this.operand = operand }
        "ConvertsToDecimal" -> ConvertsToDecimal().apply { this.operand = operand }
        "ConvertsToDate" -> ConvertsToDate().apply { this.operand = operand }
        "ConvertsToDateTime" -> ConvertsToDateTime().apply { this.operand = operand }
        "ConvertsToTime" -> ConvertsToTime().apply { this.operand = operand }
        "ConvertsToQuantity" -> ConvertsToQuantity().apply { this.operand = operand }
        "ConvertsToRatio" -> ConvertsToRatio().apply { this.operand = operand }
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "Conversion '$operatorName' is not yet supported."
            )
    }
}
