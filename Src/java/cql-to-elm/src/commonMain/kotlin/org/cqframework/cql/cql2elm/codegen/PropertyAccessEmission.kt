package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.Resolution
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Property

/**
 * Emit a [PropertyAccessExpression] as an ELM [Property].
 *
 * When the target is an identifier that resolves to a query alias, the Property uses a `scope`
 * reference (e.g., `P.gender` becomes `Property(scope="P", path="gender")`). Otherwise the target
 * is emitted as a `source` expression.
 */
internal fun EmissionContext.emitPropertyAccess(
    expression: PropertyAccessExpression
): ElmExpression {
    val property = Property()
    property.path = expression.property.value

    val target = expression.target
    if (target is IdentifierExpression) {
        val resolution = semanticModel.getIdentifierResolution(target)
        if (resolution is Resolution.AliasRef) {
            property.scope = resolution.name
            return property
        }
    }
    property.source = emitExpression(target)
    return property
}
