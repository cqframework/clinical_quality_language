package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.StringEscapeUtils.unescapeCql
import org.cqframework.cql.cql2elm.analysis.Resolution
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.Property

/**
 * Emit a [PropertyAccessExpression] as an ELM [Property]. Target is pre-folded.
 *
 * When the target is an identifier that resolves to a query alias, the Property uses a `scope`
 * reference (e.g., `P.gender` becomes `Property(scope="P", path="gender")`). Otherwise the target
 * is emitted as a `source` expression.
 */
internal fun EmissionContext.emitPropertyAccess(
    expression: PropertyAccessExpression,
    targetElm: ElmExpression,
): ElmExpression {
    val target = expression.target
    if (target is IdentifierExpression) {
        val resolution = semanticModel.getIdentifierResolution(target)

        // Library-qualified access: Common.SomeExpression → ExpressionRef(name, libraryName).
        if (resolution is Resolution.IncludeRef) {
            return ExpressionRef()
                .withName(unescapeCql(expression.property.value))
                .apply { libraryName = resolution.alias }
        }

        // Query-alias scoped property: P.gender → Property(scope="P", path="gender").
        if (resolution is Resolution.AliasRef) {
            return Property().apply {
                path = expression.property.value
                scope = resolution.name
            }
        }
    }

    return Property().apply {
        path = expression.property.value
        source = targetElm
    }
}
