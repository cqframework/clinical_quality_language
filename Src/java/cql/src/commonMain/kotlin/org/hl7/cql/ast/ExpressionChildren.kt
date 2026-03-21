package org.hl7.cql.ast

/**
 * Invokes [action] on every direct child [Expression] of [expression]. The `when` is assigned to a
 * typed `val` so the sealed [Expression] hierarchy is checked for exhaustiveness at compile time —
 * adding a new Expression subtype without a branch here is a compile error.
 */
@Suppress("CyclomaticComplexity")
inline fun forEachChildExpression(expression: Expression, action: (Expression) -> Unit) {
    // .let {} forces the when to be an expression, making sealed exhaustiveness a compile error.
    when (expression) {
        // --- Leaves (no child expressions) ---
        is IdentifierExpression,
        is ExternalConstantExpression,
        is TypeExtentExpression,
        is UnsupportedExpression -> {}

        // --- Unary (operand) ---
        // ExpandCollapseExpression extends UnaryExpression but has an extra nullable child
        is ExpandCollapseExpression -> {
            action(expression.operand)
            expression.perExpression?.let(action)
        }
        // All other UnaryExpression subtypes: just operand
        is UnaryExpression -> action(expression.operand)

        // --- Binary (left, right) ---
        // IntervalRelationExpression extends BinaryExpression; phrase has no Expression children
        is BinaryExpression -> {
            action(expression.left)
            action(expression.right)
        }

        // --- Ternary / multi-child ---
        is BetweenExpression -> {
            action(expression.input)
            action(expression.lower)
            action(expression.upper)
        }
        is IfExpression -> {
            action(expression.condition)
            action(expression.thenBranch)
            action(expression.elseBranch)
        }
        is CaseExpression -> {
            expression.comparand?.let(action)
            for (case in expression.cases) {
                action(case.condition)
                action(case.result)
            }
            action(expression.elseResult)
        }
        is FunctionCallExpression -> {
            expression.target?.let(action)
            for (arg in expression.arguments) action(arg)
        }
        is PropertyAccessExpression -> action(expression.target)
        is IndexExpression -> {
            action(expression.target)
            action(expression.index)
        }
        is IntervalExpression -> {
            action(expression.low)
            action(expression.high)
            action(expression.lowClosedExpression)
            action(expression.highClosedExpression)
        }
        is LiteralExpression -> forEachLiteralChildExpression(expression.literal, action)
        is QueryExpression -> {
            for (source in expression.sources) {
                when (val qs = source.source) {
                    is ExpressionQuerySource -> action(qs.expression)
                    is RetrieveExpression -> qs.terminology?.let { action(it.terminology) }
                }
            }
            for (let in expression.lets) action(let.expression)
            for (inclusion in expression.inclusions) {
                when (inclusion) {
                    is WithClause -> {
                        when (val qs = inclusion.source.source) {
                            is ExpressionQuerySource -> action(qs.expression)
                            is RetrieveExpression -> qs.terminology?.let { action(it.terminology) }
                        }
                        action(inclusion.condition)
                    }
                    is WithoutClause -> {
                        when (val qs = inclusion.source.source) {
                            is ExpressionQuerySource -> action(qs.expression)
                            is RetrieveExpression -> qs.terminology?.let { action(it.terminology) }
                        }
                        action(inclusion.condition)
                    }
                }
            }
            expression.where?.let(action)
            expression.aggregate?.let { agg ->
                agg.starting?.let(action)
                action(agg.expression)
            }
            expression.result?.let { action(it.expression) }
            expression.sort?.let { sort -> for (item in sort.items) action(item.expression) }
        }
        is RetrieveExpression -> {
            expression.terminology?.let { action(it.terminology) }
        }

        // DurationBetweenExpression and DifferenceBetweenExpression are not BinaryExpression;
        // they have lower/upper directly.
        is DurationBetweenExpression -> {
            action(expression.lower)
            action(expression.upper)
        }
        is DifferenceBetweenExpression -> {
            action(expression.lower)
            action(expression.upper)
        }
    }.let {} // .let {} forces expression-position when → sealed exhaustiveness enforced
}

/** Walks child expressions inside a [Literal]. Most literal types have no expression children. */
@Suppress("UnusedParameter")
inline fun forEachLiteralChildExpression(literal: Literal, action: (Expression) -> Unit) {
    when (literal) {
        is IntervalLiteral -> {
            action(literal.lower)
            action(literal.upper)
        }
        is ListLiteral -> {
            for (element in literal.elements) action(element)
        }
        is TupleLiteral -> {
            for (element in literal.elements) action(element.expression)
        }
        is InstanceLiteral -> {
            for (element in literal.elements) action(element.expression)
        }
        // All other literals have no Expression children
        else -> {}
    }
}
