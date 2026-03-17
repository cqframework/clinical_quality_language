package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.CodeLiteral
import org.hl7.cql.ast.ConceptLiteral
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.InstanceLiteral
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.IntervalLiteral
import org.hl7.cql.ast.ListLiteral
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.RatioLiteral
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TupleLiteral
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Instance
import org.hl7.elm.r1.InstanceElement
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.Literal as ElmLiteral
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Tuple
import org.hl7.elm.r1.TupleElement

@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitLiteral(literal: Literal): ElmExpression {
    return when (literal) {
        is StringLiteral ->
            ElmLiteral().withValueType(QName(typesNamespace, "String")).withValue(literal.value)
        is BooleanLiteral ->
            ElmLiteral()
                .withValueType(QName(typesNamespace, "Boolean"))
                .withValue(literal.value.toString())
        is IntLiteral ->
            ElmLiteral()
                .withValueType(QName(typesNamespace, "Integer"))
                .withValue(literal.text ?: literal.value.toString())
        is LongLiteral ->
            ElmLiteral()
                .withValueType(QName(typesNamespace, "Long"))
                .withValue(literal.value.toString())
        is DecimalLiteral ->
            ElmLiteral()
                .withValueType(QName(typesNamespace, "Decimal"))
                .withValue(literal.value.toString())
        is NullLiteral -> Null()
        is QuantityLiteral -> emitQuantity(literal)
        is RatioLiteral -> emitRatio(literal)
        is org.hl7.cql.ast.DateTimeLiteral -> emitDateTime(literal)
        is org.hl7.cql.ast.TimeLiteral -> emitTime(literal)
        is IntervalLiteral -> emitInterval(literal)
        is ListLiteral -> emitList(literal)
        is TupleLiteral -> emitTuple(literal)
        is InstanceLiteral -> emitInstance(literal)
        is CodeLiteral -> emitCode(literal)
        is ConceptLiteral -> emitConcept(literal)
    }
}

internal fun EmissionContext.emitQuantity(literal: QuantityLiteral): Quantity {
    val quantity = Quantity()
    quantity.value = BigDecimal(literal.value)
    quantity.unit = literal.unit?.trim('\'')
    return quantity
}

internal fun EmissionContext.emitRatio(literal: RatioLiteral): Ratio {
    val ratio = Ratio()
    ratio.numerator = emitRatioQuantity(literal.numerator)
    ratio.denominator = emitRatioQuantity(literal.denominator)
    return ratio
}

/** Emit a quantity within a ratio. Legacy adds unit "1" to bare ratio quantities. */
private fun EmissionContext.emitRatioQuantity(literal: QuantityLiteral): Quantity {
    val quantity = Quantity()
    quantity.value = BigDecimal(literal.value)
    quantity.unit = literal.unit?.trim('\'') ?: "1"
    return quantity
}

internal fun EmissionContext.emitInterval(literal: IntervalLiteral): Interval {
    val interval = Interval()
    // Null wrapping and implicit conversions are inserted by ConversionInserter before emission
    interval.low = emitExpression(literal.lower)
    interval.high = emitExpression(literal.upper)
    interval.lowClosed = literal.lowerClosed
    interval.highClosed = literal.upperClosed
    return interval
}

internal fun EmissionContext.emitList(literal: ListLiteral): org.hl7.elm.r1.List {
    val list = org.hl7.elm.r1.List()
    // Null wrapping and implicit conversions are inserted by ConversionInserter before emission
    if (literal.elements.isNotEmpty()) {
        list.element = literal.elements.map { emitExpression(it) }.toMutableList()
    }
    return list
}

/**
 * Wrap a Null expression in an As node for the given target type. Used when a null literal appears
 * in a context where the expected type is known (e.g., list elements, interval bounds).
 */
internal fun EmissionContext.wrapNullAs(
    expression: ElmExpression,
    targetType: org.hl7.cql.model.DataType,
): ElmExpression {
    return org.hl7.elm.r1.As().apply {
        operand = expression
        if (
            targetType is org.hl7.cql.model.SimpleType || targetType is org.hl7.cql.model.ClassType
        ) {
            asType = operatorRegistry.typeBuilder.dataTypeToQName(targetType)
        } else {
            asTypeSpecifier = operatorRegistry.typeBuilder.dataTypeToTypeSpecifier(targetType)
        }
    }
}

internal fun EmissionContext.emitTuple(literal: TupleLiteral): Tuple {
    val tuple = Tuple()
    if (literal.elements.isNotEmpty()) {
        tuple.element =
            literal.elements
                .map { elem ->
                    TupleElement().apply {
                        name = elem.name.value
                        value = emitExpression(elem.expression)
                    }
                }
                .toMutableList()
    }
    return tuple
}

internal fun EmissionContext.emitInstance(literal: InstanceLiteral): Instance {
    val instance = Instance()
    literal.type?.let { typeSpec ->
        instance.classType = QName(typesNamespace, typeSpec.name.simpleName)
    }
    if (literal.elements.isNotEmpty()) {
        instance.element =
            literal.elements
                .map { elem ->
                    InstanceElement().apply {
                        name = elem.name.value
                        value = emitExpression(elem.expression)
                    }
                }
                .toMutableList()
    }
    return instance
}

internal fun EmissionContext.emitCode(literal: CodeLiteral): Code {
    val code = Code()
    code.code = literal.code
    code.display = literal.display
    code.system = CodeSystemRef().apply { name = literal.system.identifier.value }
    return code
}

internal fun EmissionContext.emitConcept(literal: ConceptLiteral): Concept {
    val concept = Concept()
    concept.display = literal.display
    if (literal.codes.isNotEmpty()) {
        concept.code = literal.codes.map { emitCode(it) }.toMutableList()
    }
    return concept
}
