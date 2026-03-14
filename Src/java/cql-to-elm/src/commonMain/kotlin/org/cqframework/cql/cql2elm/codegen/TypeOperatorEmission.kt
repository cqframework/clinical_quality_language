package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.shared.QName
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

/** Emit an [IsExpression] as an ELM [Is] node with isTypeSpecifier set. */
internal fun EmissionContext.emitIsExpression(expression: IsExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return Is().apply {
        operand = operandElm
        isTypeSpecifier = emitTypeSpecifier(expression.type)
    }
}

/** Emit an [AsExpression] as an ELM [As] node with strict = false. */
internal fun EmissionContext.emitAsExpression(expression: AsExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return As().apply {
        operand = operandElm
        asTypeSpecifier = emitTypeSpecifier(expression.type)
        strict = false
    }
}

/** Emit a [CastExpression] as an ELM [As] node with strict = true. */
internal fun EmissionContext.emitCastExpression(expression: CastExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return As().apply {
        operand = operandElm
        asTypeSpecifier = emitTypeSpecifier(expression.type)
        strict = true
    }
}

/**
 * Emit a [ConversionExpression] (`convert X to Y`) as the appropriate ELM conversion function node
 * (e.g., ToString, ToDecimal). The legacy translator resolves the conversion via the ConversionMap
 * and emits the corresponding function operator.
 */
internal fun EmissionContext.emitConversionExpression(
    expression: ConversionExpression
): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    val destType = expression.destinationType
    if (destType is NamedTypeSpecifier) {
        val conversionName = typeNameToConversionOperator(destType.name.simpleName)
        if (conversionName != null) {
            return createConversionElm(conversionName, operandElm)
        }
    }
    throw ElmEmitter.UnsupportedNodeException(
        "ConversionExpression to '${expression.destinationType}' is not yet supported."
    )
}

/** Convert an AST [org.hl7.cql.ast.TypeSpecifier] to an ELM [org.hl7.elm.r1.TypeSpecifier]. */
internal fun EmissionContext.emitTypeSpecifier(
    typeSpec: org.hl7.cql.ast.TypeSpecifier
): org.hl7.elm.r1.TypeSpecifier {
    return when (typeSpec) {
        is NamedTypeSpecifier -> {
            val elmTypeSpec = org.hl7.elm.r1.NamedTypeSpecifier()
            elmTypeSpec.name = QName(typesNamespace, typeSpec.name.simpleName)
            elmTypeSpec
        }
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "TypeSpecifier '${typeSpec::class.simpleName}' is not yet supported."
            )
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
