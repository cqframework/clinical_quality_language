package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.CaseChildren
import org.hl7.cql.ast.CaseExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.DateTimeComponentExpression
import org.hl7.cql.ast.DifferenceBetweenExpression
import org.hl7.cql.ast.DifferenceOfExpression
import org.hl7.cql.ast.DurationBetweenExpression
import org.hl7.cql.ast.DurationOfExpression
import org.hl7.cql.ast.ElementExtractorExpression
import org.hl7.cql.ast.ExistsExpression
import org.hl7.cql.ast.ExpandCollapseExpression
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionFold
import org.hl7.cql.ast.ExternalConstantExpression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.LiteralChildren
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QueryChildren
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.ListType

/**
 * Validates the AST after type resolution, flagging semantic errors in the [SemanticModel]. Uses
 * [ExpressionFold] for compile-time exhaustive dispatch — adding a new Expression subtype without a
 * validation handler is a compile error.
 *
 * Detected errors:
 * - Unresolved identifiers (no resolution and no inferred type)
 * - Undeclared function calls (function name not found anywhere)
 * - Unmatched function signatures (name exists but arity doesn't match)
 * - Recursive function calls (matching arity but null type from circular reference)
 * - Function bodies with nested errors (entire body flagged)
 * - Invalid casts (e.g., list → scalar As expression)
 */
class SemanticValidator {
    fun validate(library: Library, symbolTable: SymbolTable, semanticModel: SemanticModel) {
        val checker = ExpressionChecker(symbolTable, semanticModel)
        for ((_, exprDef) in symbolTable.expressionDefinitions) {
            checker.fold(exprDef.expression)
        }
        for ((_, funcDefs) in symbolTable.functionDefinitions) {
            for (funcDef in funcDefs) {
                val body = funcDef.body
                if (body is org.hl7.cql.ast.ExpressionFunctionBody) {
                    checker.fold(body.expression)
                    if (checker.hasNestedError(body.expression)) {
                        semanticModel.addError(body.expression)
                    }
                }
            }
        }
        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { checker.fold(it) }
        }
    }
}

/**
 * Fold-based expression checker. Each handler validates its expression type; children are
 * pre-folded by the catamorphism before the handler is called. The fold ensures exhaustive coverage
 * — new expression types must be handled.
 */
@Suppress("TooManyFunctions")
private class ExpressionChecker(
    private val symbolTable: SymbolTable,
    private val model: SemanticModel,
) : ExpressionFold<Unit> {

    /**
     * Override [fold] to handle [QueryExpression] specially. The catamorphism's default fold
     * pre-folds ALL query children including sort-by expressions, but sort items use a separate
     * emission path (ByColumn/ByDirection) and their identifiers (e.g., `$this`) are not resolvable
     * references — validating them would produce false errors. We manually recurse into query
     * children, skipping sort items, matching the pre-catamorphism behavior.
     */
    override fun fold(expr: Expression): Unit =
        if (expr is QueryExpression) {
            validateQuery(expr)
        } else {
            super<ExpressionFold>.fold(expr)
        }

    private fun validateQuery(expr: QueryExpression) {
        for (source in expr.sources) {
            val qs = source.source
            if (qs is org.hl7.cql.ast.ExpressionQuerySource) fold(qs.expression)
        }
        expr.where?.let { fold(it) }
        expr.result?.let { fold(it.expression) }
        expr.lets.forEach { fold(it.expression) }
        expr.inclusions.forEach {
            when (it) {
                is org.hl7.cql.ast.WithClause -> {
                    val qs = it.source.source
                    if (qs is org.hl7.cql.ast.ExpressionQuerySource) fold(qs.expression)
                    fold(it.condition)
                }
                is org.hl7.cql.ast.WithoutClause -> {
                    val qs = it.source.source
                    if (qs is org.hl7.cql.ast.ExpressionQuerySource) fold(qs.expression)
                    fold(it.condition)
                }
            }
        }
        expr.aggregate?.let {
            it.starting?.let { s -> fold(s) }
            fold(it.expression)
        }
        // Skip sort items — they use ByColumn/ByDirection emission, not identifier resolution
    }

    fun hasNestedError(expression: Expression): Boolean {
        if (model.hasError(expression)) return true
        // Use AstWalker for deep check since it handles all node types including non-expressions
        var found = false
        val walker =
            object : org.hl7.cql.ast.AstWalker() {
                override fun visitExpression(expression: Expression) {
                    if (model.hasError(expression)) {
                        found = true
                    }
                    if (!found) super.visitExpression(expression)
                }
            }
        walker.visitExpression(expression)
        return found
    }

    // --- Validation for specific expression types ---

    override fun onIdentifier(expr: IdentifierExpression) {
        if (model.getIdentifierResolution(expr) != null) return
        if (model[expr] == null) {
            model.addError(expr)
        }
    }

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: Unit?,
        arguments: List<Unit>,
    ) {
        // Children are already validated by the catamorphism (pre-folded).

        if (model[expr] != null) return
        if (model.getOperatorResolution(expr) != null) return

        val name = expr.function.value
        val userFuncs = symbolTable.resolveFunctions(name)
        if (userFuncs.isNotEmpty()) {
            val callArity = expr.arguments.size
            val anyArityMatch = userFuncs.any { it.operands.size == callArity }
            if (!anyArityMatch) {
                model.addError(expr)
            } else if (model[expr] == null) {
                model.addError(expr)
            }
            return
        }

        val argTypes = expr.arguments.mapNotNull { model[it] }
        if (argTypes.size == expr.arguments.size) {
            model.addError(expr)
        }
    }

    override fun onAs(expr: AsExpression, operand: Unit) {
        // Child already validated by the catamorphism.
        val operandType = model[expr.operand]
        val targetType = model[expr]
        if (operandType != null && targetType != null) {
            if (operandType is ListType && targetType !is ListType) {
                model.addError(expr)
            }
        }
    }

    // --- Default handlers: children are pre-folded, nothing more to do ---

    override fun onLiteral(expr: LiteralExpression, children: LiteralChildren<Unit>) {} // leaf

    override fun onExternalConstant(expr: ExternalConstantExpression) {} // leaf

    override fun onBinaryOperator(expr: OperatorBinaryExpression, left: Unit, right: Unit) {}

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Unit) {}

    override fun onBooleanTest(expr: BooleanTestExpression, operand: Unit) {}

    override fun onIf(expr: IfExpression, condition: Unit, thenBranch: Unit, elseBranch: Unit) {}

    override fun onCase(
        expr: CaseExpression,
        comparand: Unit?,
        cases: List<CaseChildren<Unit>>,
        elseResult: Unit,
    ) {}

    override fun onIs(expr: IsExpression, operand: Unit) {}

    override fun onCast(expr: CastExpression, operand: Unit) {}

    override fun onConversion(expr: ConversionExpression, operand: Unit) {}

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: Unit) {}

    override fun onIndex(expr: IndexExpression, target: Unit, index: Unit) {}

    override fun onExists(expr: ExistsExpression, operand: Unit) {}

    override fun onMembership(expr: MembershipExpression, left: Unit, right: Unit) {}

    override fun onListTransform(expr: ListTransformExpression, operand: Unit) {}

    override fun onExpandCollapse(expr: ExpandCollapseExpression, operand: Unit, per: Unit?) {}

    override fun onDateTimeComponent(expr: DateTimeComponentExpression, operand: Unit) {}

    override fun onDurationBetween(expr: DurationBetweenExpression, lower: Unit, upper: Unit) {}

    override fun onDifferenceBetween(expr: DifferenceBetweenExpression, lower: Unit, upper: Unit) {}

    override fun onDurationOf(expr: DurationOfExpression, operand: Unit) {}

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: Unit) {}

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: Unit) {}

    override fun onWidth(expr: WidthExpression, operand: Unit) {}

    override fun onElementExtractor(expr: ElementExtractorExpression, operand: Unit) {}

    override fun onTypeExtent(expr: TypeExtentExpression) {} // leaf

    override fun onBetween(expr: BetweenExpression, input: Unit, lower: Unit, upper: Unit) {}

    override fun onIntervalRelation(expr: IntervalRelationExpression, left: Unit, right: Unit) {}

    override fun onQuery(expr: QueryExpression, children: QueryChildren<Unit>) {}

    override fun onRetrieve(expr: RetrieveExpression) {} // leaf (for now)

    override fun onUnsupported(expr: UnsupportedExpression) {} // nothing to validate
}
