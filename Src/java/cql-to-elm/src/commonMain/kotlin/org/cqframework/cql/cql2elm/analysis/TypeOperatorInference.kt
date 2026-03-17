package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.model.DataType

/** Infer type for `is` expression — always Boolean. Operand pre-folded by catamorphism. */
internal fun TypeResolver.inferIsType(expression: IsExpression): DataType? {
    return type("Boolean")
}

/** Infer type for `as` expression — the target type. Operand pre-folded by catamorphism. */
internal fun TypeResolver.inferAsType(expression: AsExpression): DataType? {
    return resolveTypeSpecifier(expression.type)
}

/** Infer type for `cast` expression — the target type. Operand pre-folded by catamorphism. */
internal fun TypeResolver.inferCastType(expression: CastExpression): DataType? {
    return resolveTypeSpecifier(expression.type)
}

/**
 * Infer type for `convert` expression — the destination type. Operand pre-folded by catamorphism.
 */
internal fun TypeResolver.inferConversionType(expression: ConversionExpression): DataType? {
    val destType = expression.destinationType ?: return null
    return resolveTypeSpecifier(destType)
}
