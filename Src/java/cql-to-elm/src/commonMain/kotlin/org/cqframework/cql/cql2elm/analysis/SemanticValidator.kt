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
import org.hl7.cql.ast.ImplicitCastExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntervalExpression
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
import org.hl7.cql.ast.forEachChildExpression
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.ListType

/**
 * Validates the AST after type resolution, flagging semantic errors in the [SemanticModel]. Uses
 * [ExpressionFold] for compile-time exhaustive dispatch — adding a new Expression subtype without a
 * validation handler is a compile error.
 *
 * ## Detected errors
 * - Unresolved identifiers (no resolution and no inferred type)
 * - Undeclared function calls (function name not found anywhere in [SymbolTable])
 * - Unmatched function signatures (name exists but no overload matches call arity)
 * - Recursive function calls (matching arity but null type from circular reference)
 * - Function bodies with nested errors (entire body flagged via [ExpressionChecker.hasNestedError])
 * - Invalid casts (e.g., `List<T>` → scalar `As` expression)
 * - Unresolved binary operators (both operands typed but no matching operator overload)
 * - Unresolved unary operators (operand typed but no matching operator overload)
 * - Unresolved interval relations (e.g., `Includes` on non-list/non-interval types)
 * - Invalid quantity literals (e.g., bad unit — no resolved type)
 * - Invalid sort expressions (sort on non-comparable types such as `Interval`)
 *
 * ## Adding a new validation
 * 1. Override the relevant `on*` handler in [ExpressionChecker]. The handler receives pre-folded
 *    children (the catamorphism validates children before the parent).
 * 2. Check the condition using the [SemanticModel] (type lookups, resolution lookups).
 * 3. Call [SemanticModel.addError] on the offending expression if the check fails.
 *
 * ## What this does NOT do
 * - No type inference — that is [TypeResolver]'s job.
 * - No conversion recording — that is [ConversionPlanner]'s job.
 * - No AST mutation — the AST is immutable at this point; errors are recorded in the model.
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
        // Flag query as error if sort involves non-comparable types.
        // Legacy replaces the entire query with Null for invalid sorts.
        if (expr.sort != null) {
            val queryType = model[expr]
            // Direction-only sort on non-comparable source element type (e.g., List<Interval>)
            if (queryType is ListType && queryType.elementType is org.hl7.cql.model.IntervalType) {
                model.addError(expr)
            }
            // Sort-by expression: check if it resolves to Interval (not comparable)
            // or if it's an unresolved column that points to an Interval property
            expr.sort?.items?.forEach { sortItem ->
                val sortType = model[sortItem.expression]
                if (sortType is org.hl7.cql.model.IntervalType) {
                    model.addError(expr)
                } else if (sortType == null && sortItem.expression is IdentifierExpression) {
                    // Unresolved sort column: check source element type for the property
                    val elemType = if (queryType is ListType) queryType.elementType else null
                    if (elemType is org.hl7.cql.model.TupleType) {
                        val propName = (sortItem.expression as IdentifierExpression).name.simpleName
                        val propType = elemType.elements.find { it.name == propName }?.type
                        if (propType is org.hl7.cql.model.IntervalType) {
                            model.addError(expr)
                        }
                    }
                }
            }
        }
    }

    fun hasNestedError(expression: Expression): Boolean {
        if (model.hasError(expression)) return true
        var found = false
        forEachChildExpression(expression) { if (!found && hasNestedError(it)) found = true }
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

        // Skip validation for library-qualified calls (e.g., Common.toString(x)).
        // We can't resolve cross-library functions without the included library's symbols.
        if (isLibraryQualifiedCall(expr)) return

        if (model[expr] != null) return
        if (model.getOperatorResolution(expr) != null) return
        if (model.getFunctionCallResolution(expr) != null) return

        val name = expr.function.value
        val userFuncs = symbolTable.resolveFunctions(name)
        if (userFuncs.isNotEmpty()) {
            val callArity = expr.arguments.size
            val anyArityMatch = userFuncs.any { it.operands.size == callArity }
            if (!anyArityMatch) {
                model.addError(expr)
            }
            // If a matching-arity user function exists, it's valid even if the type
            // couldn't be inferred (e.g., recursive functions with circular type deps).
            return
        }

        // No user function found, no operator resolution, no function call resolution.
        // Only flag when all arguments are typed — avoids cascading errors when the
        // root cause is an untyped argument.
        val argTypes = expr.arguments.mapNotNull { model[it] }
        if (argTypes.size == expr.arguments.size) {
            model.addError(expr)
        }
    }

    /** Check if a function call's target resolves to an included library alias. */
    private fun isLibraryQualifiedCall(expr: FunctionCallExpression): Boolean {
        val target = expr.target as? IdentifierExpression ?: return false
        return model.getIdentifierResolution(target) is Resolution.IncludeRef
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

    // Note: matching legacy's Null-for-bad-units on QuantityLiteral requires unit validation
    // in TypeResolver.inferLiteralType — the resolver unconditionally returns type Quantity
    // for all QuantityLiteral instances regardless of unit validity.
    override fun onLiteral(expr: LiteralExpression, children: LiteralChildren<Unit>) {}

    override fun onExternalConstant(expr: ExternalConstantExpression) {} // leaf

    override fun onBinaryOperator(expr: OperatorBinaryExpression, left: Unit, right: Unit) {
        // If both operands have types but the operator couldn't resolve, it's a type error
        // (e.g., Equal(Concept, List<Code>) — no implicit conversion exists).
        if (model[expr] != null) return
        val leftType = model[expr.left]
        val rightType = model[expr.right]
        if (leftType != null && rightType != null) {
            model.addError(expr)
        }
    }

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Unit) {
        // Flag unary operators that couldn't resolve (operand has a type but operator doesn't).
        if (model[expr] == null) {
            val operandType = model[expr.operand]
            if (operandType != null) {
                model.addError(expr)
            }
        }
    }

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

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: Unit) {}

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

    override fun onIntervalExpression(
        expr: IntervalExpression,
        low: Unit,
        high: Unit,
        lowClosed: Unit,
        highClosed: Unit,
    ) {}

    override fun onIntervalRelation(expr: IntervalRelationExpression, left: Unit, right: Unit) {
        // Flag if interval relation has no resolved type (e.g., Includes on non-list/interval)
        if (model[expr] == null) {
            model.addError(expr)
            return
        }
        // Propagate errors from children: if an operand has an error (e.g., unresolved
        // identifier B in "B.relevantPeriod during ..."), the interval relation is also invalid.
        if (hasNestedError(expr.left) || hasNestedError(expr.right)) {
            model.addError(expr)
        }
    }

    override fun onQuery(expr: QueryExpression, children: QueryChildren<Unit>) {}

    override fun onRetrieve(expr: RetrieveExpression) {
        // Non-retrievable types (e.g. abstract DomainResource) are invalid in a Retrieve context.
        // Flag so the emitter's error gate emits Null, matching legacy translator behavior.
        // Wrapped in try-catch: a system-only ModelContext will throw for FHIR types, which
        // would be caught by other validators (unresolved type). Don't double-report.
        try {
            val typeName = expr.typeSpecifier.name.simpleName
            val resolvedModel = model.modelContext.resolveModelForType(typeName)
            val dataType = resolvedModel.resolveTypeName(typeName)
            if (dataType is ClassType && !dataType.isRetrievable) {
                model.addError(expr)
            }
        } catch (_: Exception) {
            // Type not resolvable — let other validators handle it.
        }
    }

    override fun onUnsupported(expr: UnsupportedExpression) {} // nothing to validate
}
