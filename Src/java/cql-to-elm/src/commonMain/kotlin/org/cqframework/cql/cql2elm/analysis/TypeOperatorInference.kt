package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.model.DataType

/** Infer type for `is` expression — always Boolean. */
internal fun TypeResolver.inferIsType(expression: IsExpression): DataType? {
    inferType(expression.operand)
    return type("Boolean")
}

/** Infer type for `as` expression — the target type. */
internal fun TypeResolver.inferAsType(expression: AsExpression): DataType? {
    inferType(expression.operand)
    return resolveTypeSpecifier(expression.type)
}

/** Infer type for `cast` expression — the target type. */
internal fun TypeResolver.inferCastType(expression: CastExpression): DataType? {
    inferType(expression.operand)
    return resolveTypeSpecifier(expression.type)
}

/** Infer type for `convert` expression — the destination type. */
internal fun TypeResolver.inferConversionType(expression: ConversionExpression): DataType? {
    inferType(expression.operand)
    val destType = expression.destinationType ?: return null
    return resolveTypeSpecifier(destType)
}
