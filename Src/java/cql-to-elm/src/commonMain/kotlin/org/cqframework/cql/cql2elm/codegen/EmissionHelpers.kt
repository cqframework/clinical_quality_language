package org.cqframework.cql.cql2elm.codegen

import org.hl7.elm.r1.Expression as ElmExpression

/** Validate exactly 1 argument and apply [factory]. */
internal fun emitUnaryArg(
    args: List<ElmExpression>,
    factory: (ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 1) { "Expected 1 argument" }
    return factory(args[0])
}

/** Validate exactly 2 arguments and apply [factory]. */
internal fun emitBinaryArgs(
    args: List<ElmExpression>,
    factory: (ElmExpression, ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments" }
    return factory(args[0], args[1])
}

/** Validate exactly 0 arguments and apply [factory]. */
internal fun emitNullaryArg(
    args: List<ElmExpression>,
    factory: () -> ElmExpression,
): ElmExpression {
    require(args.isEmpty()) { "Expected 0 arguments" }
    return factory()
}
