package org.cqframework.cql.elm

import org.hl7.elm.r1.Expression

actual fun IdObjectFactory.createExpression(expressionType: String): Expression {
    return this.commonCreateExpression(expressionType)
}
