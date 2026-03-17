package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
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
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFold
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.ExpressionQuerySource
import org.hl7.cql.ast.ExternalConstantExpression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
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
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QualifiedIdentifier
import org.hl7.cql.ast.QueryChildren
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.ast.WithClause
import org.hl7.cql.ast.WithoutClause
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SimpleType

/**
 * AST-to-AST transformer that inserts explicit conversion nodes based on operator resolutions
 * stored in the [TypeTable]. This moves conversion logic from the emission phase into the analysis
 * phase, making the AST self-contained.
 *
 * Implements [ExpressionFold] as a catamorphism where R = Expression. Most handlers are identity
 * transforms (reconstruct with pre-folded children). For operators and function calls with
 * conversions, operands are wrapped in [ConversionExpression] or [AsExpression] nodes.
 *
 * After conversion insertion, the TypeResolver must be re-run on the modified AST since new nodes
 * (the conversion wrappers) need type entries in the TypeTable.
 */
@Suppress("TooManyFunctions")
class ConversionInserter(
    private val typeTable: TypeTable,
    private val operatorRegistry: OperatorRegistry,
) : ExpressionFold<Expression> {

    /** Number of conversion nodes inserted during this pass. */
    var conversionsInserted: Int = 0
        private set

    /** Counts by conversion kind (operator, cast, list, interval). */
    val conversionKindCounts: MutableMap<String, Int> = mutableMapOf()

    /**
     * Convert all expressions in a [Library], returning a new Library with conversion nodes
     * inserted. Statements and definitions containing expressions are rebuilt with converted
     * expressions.
     */
    fun convertLibrary(library: Library): Library {
        val newStatements = library.statements.map { convertStatement(it) }
        val newDefinitions = library.definitions.map { convertDefinition(it) }
        return if (newStatements === library.statements && newDefinitions === library.definitions) {
            library
        } else {
            library.copy(statements = newStatements, definitions = newDefinitions)
        }
    }

    private fun convertStatement(statement: Statement): Statement =
        when (statement) {
            is ExpressionDefinition -> {
                val converted = fold(statement.expression)
                if (converted === statement.expression) statement
                else statement.copy(expression = converted)
            }
            is FunctionDefinition -> {
                val body = statement.body
                if (body is ExpressionFunctionBody) {
                    val converted = fold(body.expression)
                    if (converted === body.expression) statement
                    else statement.copy(body = body.copy(expression = converted))
                } else {
                    statement
                }
            }
            else -> statement
        }

    private fun convertDefinition(
        definition: org.hl7.cql.ast.Definition
    ): org.hl7.cql.ast.Definition =
        when (definition) {
            is ParameterDefinition -> {
                val default = definition.default
                if (default != null) {
                    val converted = fold(default)
                    if (converted === default) definition else definition.copy(default = converted)
                } else {
                    definition
                }
            }
            else -> definition
        }

    // --- Conversion insertion helpers ---

    /**
     * Apply conversions from an [OperatorResolution] to a list of operands. Returns a new list with
     * wrapped operands, or the original list if no conversions were needed.
     */
    private fun applyConversions(
        resolution: OperatorResolution?,
        operands: List<Expression>,
    ): List<Expression> {
        if (resolution == null || !resolution.hasConversions()) return operands
        val result = operands.toMutableList()
        var changed = false
        resolution.conversions.forEachIndexed { index, conversion ->
            if (conversion != null && index < result.size) {
                val wrapped = insertConversion(result[index], conversion)
                if (wrapped !== result[index]) {
                    result[index] = wrapped
                    changed = true
                }
            }
        }
        return if (changed) result else operands
    }

    /**
     * Wrap an expression in the appropriate AST conversion node based on the [Conversion] type.
     * - Operator conversions (ToDecimal, ToLong, etc.) produce [ConversionExpression]
     * - Cast conversions produce [AsExpression]
     */
    private fun insertConversion(expr: Expression, conversion: Conversion): Expression {
        // Operator-based conversion (e.g., ToDecimal, ToLong)
        val operatorName = operatorRegistry.conversionOperatorName(conversion)
        if (operatorName != null) {
            conversionsInserted++
            conversionKindCounts["operator"] = (conversionKindCounts["operator"] ?: 0) + 1
            val typeName = conversionOperatorToTypeName(operatorName) ?: return expr
            return ConversionExpression(
                operand = expr,
                destinationType = NamedTypeSpecifier(name = QualifiedIdentifier(listOf(typeName))),
                locator = expr.locator,
            )
        }
        // Cast conversion: deferred to emission because the ELM As node for implicit casts
        // must NOT set strict (matching legacy), but user-written AsExpression nodes DO set
        // strict=false. Keeping casts in emission avoids needing an implicit/explicit flag.
        if (conversion.isCast) {
            return expr
        }
        // List conversion with element-level conversion: deferred to emission because it
        // requires generating an implicit Query with alias/return, which is an ELM-level concern
        if (conversion.isListConversion && conversion.conversion != null) {
            return expr
        }
        // Interval conversion: deferred to emission because it produces Property access on
        // low/high/lowClosed/highClosed, which is an ELM-level concern
        if (conversion.isIntervalConversion && conversion.conversion != null) {
            return expr
        }
        // List/interval promotions and demotions not yet handled
        return expr
    }

    /** Convert a DataType to an AST TypeSpecifier. */
    private fun dataTypeToAstTypeSpecifier(dataType: DataType): org.hl7.cql.ast.TypeSpecifier? =
        when (dataType) {
            is SimpleType -> {
                val name = dataType.toString().removePrefix("System.")
                NamedTypeSpecifier(name = QualifiedIdentifier(listOf(name)))
            }
            is ClassType -> {
                val name = dataType.name.removePrefix("System.")
                NamedTypeSpecifier(name = QualifiedIdentifier(listOf(name)))
            }
            is ListType -> {
                val elementSpec = dataTypeToAstTypeSpecifier(dataType.elementType) ?: return null
                org.hl7.cql.ast.ListTypeSpecifier(elementType = elementSpec)
            }
            is IntervalType -> {
                val pointSpec = dataTypeToAstTypeSpecifier(dataType.pointType) ?: return null
                org.hl7.cql.ast.IntervalTypeSpecifier(pointType = pointSpec)
            }
            else -> null
        }

    /** Map conversion operator name to simple type name for ConversionExpression destination. */
    private fun conversionOperatorToTypeName(operatorName: String): String? =
        when (operatorName) {
            "ToString" -> "String"
            "ToBoolean" -> "Boolean"
            "ToInteger" -> "Integer"
            "ToLong" -> "Long"
            "ToDecimal" -> "Decimal"
            "ToDate" -> "Date"
            "ToDateTime" -> "DateTime"
            "ToTime" -> "Time"
            "ToQuantity" -> "Quantity"
            "ToRatio" -> "Ratio"
            "ToConcept" -> "Concept"
            else -> null
        }

    // --- ExpressionFold<Expression> implementation ---
    // Identity transform for most nodes. Conversion insertion happens in operator/function
    // handlers.

    override fun onBinaryOperator(
        expr: OperatorBinaryExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        val resolution = typeTable.getOperatorResolution(expr)
        val operands = applyConversions(resolution, listOf(left, right))
        val l = operands[0]
        val r = operands[1]
        // Compare against the ORIGINAL expression's children (not the pre-folded `left`/`right`
        // params) so that changes made by the recursive fold are also propagated.
        return if (l === expr.left && r === expr.right) expr else expr.copy(left = l, right = r)
    }

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Expression): Expression {
        val resolution = typeTable.getOperatorResolution(expr)
        val operands = applyConversions(resolution, listOf(operand))
        val op = operands[0]
        // Compare against original operand so recursive-fold changes are propagated.
        return if (op === expr.operand) expr else expr.copy(operand = op)
    }

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: Expression?,
        arguments: List<Expression>,
    ): Expression {
        // Function call conversions are handled in emission (FunctionEmission.kt) for now.
        // The identity check below ensures we return the original expression if no children
        // changed during the recursive fold, preserving TypeTable lookup capability.
        return if (
            target === expr.target && arguments.indices.all { arguments[it] === expr.arguments[it] }
        ) {
            expr
        } else {
            expr.copy(target = target, arguments = arguments)
        }
    }

    // --- Identity handlers: reconstruct with pre-folded children ---

    override fun onLiteral(
        expr: LiteralExpression,
        children: LiteralChildren<Expression>,
    ): Expression {
        // Reconstruct list/interval/tuple literals with pre-folded children so that
        // conversions inserted inside nested expressions (e.g., queries inside list
        // elements) are properly propagated.
        val literal = expr.literal
        return when (literal) {
            is org.hl7.cql.ast.ListLiteral -> {
                if (
                    children.elements.indices.all { children.elements[it] === literal.elements[it] }
                ) {
                    expr
                } else {
                    expr.copy(literal = literal.copy(elements = children.elements))
                }
            }
            is org.hl7.cql.ast.IntervalLiteral -> {
                val newLow = children.intervalLow
                val newHigh = children.intervalHigh
                if (newLow === literal.lower && newHigh === literal.upper) {
                    expr
                } else {
                    expr.copy(
                        literal =
                            literal.copy(
                                lower = newLow ?: literal.lower,
                                upper = newHigh ?: literal.upper,
                            )
                    )
                }
            }
            is org.hl7.cql.ast.TupleLiteral -> {
                if (
                    children.tupleElements.indices.all {
                        children.tupleElements[it] === literal.elements[it].expression
                    }
                ) {
                    expr
                } else {
                    expr.copy(
                        literal =
                            literal.copy(
                                elements =
                                    literal.elements.mapIndexed { i, elem ->
                                        if (children.tupleElements[i] === elem.expression) elem
                                        else elem.copy(expression = children.tupleElements[i])
                                    }
                            )
                    )
                }
            }
            is org.hl7.cql.ast.InstanceLiteral -> {
                if (
                    children.tupleElements.indices.all {
                        children.tupleElements[it] === literal.elements[it].expression
                    }
                ) {
                    expr
                } else {
                    expr.copy(
                        literal =
                            literal.copy(
                                elements =
                                    literal.elements.mapIndexed { i, elem ->
                                        if (children.tupleElements[i] === elem.expression) elem
                                        else elem.copy(expression = children.tupleElements[i])
                                    }
                            )
                    )
                }
            }
            else -> expr // Simple literals have no expression children
        }
    }

    override fun onIdentifier(expr: IdentifierExpression): Expression = expr

    override fun onExternalConstant(expr: ExternalConstantExpression): Expression = expr

    override fun onBooleanTest(expr: BooleanTestExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onIf(
        expr: IfExpression,
        condition: Expression,
        thenBranch: Expression,
        elseBranch: Expression,
    ): Expression {
        return if (
            condition === expr.condition &&
                thenBranch === expr.thenBranch &&
                elseBranch === expr.elseBranch
        ) {
            expr
        } else {
            expr.copy(condition = condition, thenBranch = thenBranch, elseBranch = elseBranch)
        }
    }

    override fun onCase(
        expr: CaseExpression,
        comparand: Expression?,
        cases: List<CaseChildren<Expression>>,
        elseResult: Expression,
    ): Expression {
        var changed = comparand !== expr.comparand || elseResult !== expr.elseResult
        val newCases =
            if (
                !changed &&
                    cases.indices.all {
                        cases[it].condition === expr.cases[it].condition &&
                            cases[it].result === expr.cases[it].result
                    }
            ) {
                expr.cases
            } else {
                changed = true
                cases.mapIndexed { i, c ->
                    expr.cases[i].copy(condition = c.condition, result = c.result)
                }
            }
        return if (!changed) expr
        else expr.copy(comparand = comparand, cases = newCases, elseResult = elseResult)
    }

    override fun onIs(expr: IsExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onAs(expr: AsExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onCast(expr: CastExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onConversion(expr: ConversionExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: Expression): Expression {
        return if (target === expr.target) expr else expr.copy(target = target)
    }

    override fun onIndex(expr: IndexExpression, target: Expression, index: Expression): Expression {
        return if (target === expr.target && index === expr.index) expr
        else expr.copy(target = target, index = index)
    }

    override fun onExists(expr: ExistsExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onMembership(
        expr: MembershipExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        val resolution = typeTable.getOperatorResolution(expr)
        val operands = applyConversions(resolution, listOf(left, right))
        val l = operands[0]
        val r = operands[1]
        return if (l === expr.left && r === expr.right) expr else expr.copy(left = l, right = r)
    }

    override fun onListTransform(expr: ListTransformExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: Expression,
        per: Expression?,
    ): Expression {
        return if (operand === expr.operand && per === expr.perExpression) expr
        else expr.copy(operand = operand, perExpression = per)
    }

    override fun onDateTimeComponent(
        expr: DateTimeComponentExpression,
        operand: Expression,
    ): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onDurationBetween(
        expr: DurationBetweenExpression,
        lower: Expression,
        upper: Expression,
    ): Expression {
        return if (lower === expr.lower && upper === expr.upper) expr
        else expr.copy(lower = lower, upper = upper)
    }

    override fun onDifferenceBetween(
        expr: DifferenceBetweenExpression,
        lower: Expression,
        upper: Expression,
    ): Expression {
        return if (lower === expr.lower && upper === expr.upper) expr
        else expr.copy(lower = lower, upper = upper)
    }

    override fun onDurationOf(expr: DurationOfExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onWidth(expr: WidthExpression, operand: Expression): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onElementExtractor(
        expr: ElementExtractorExpression,
        operand: Expression,
    ): Expression {
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    override fun onTypeExtent(expr: TypeExtentExpression): Expression = expr

    override fun onBetween(
        expr: BetweenExpression,
        input: Expression,
        lower: Expression,
        upper: Expression,
    ): Expression {
        return if (input === expr.input && lower === expr.lower && upper === expr.upper) expr
        else expr.copy(input = input, lower = lower, upper = upper)
    }

    override fun onIntervalRelation(
        expr: IntervalRelationExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        val resolution = typeTable.getOperatorResolution(expr)
        val operands = applyConversions(resolution, listOf(left, right))
        val l = operands[0]
        val r = operands[1]
        return if (l === expr.left && r === expr.right) expr else expr.copy(left = l, right = r)
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    override fun onQuery(expr: QueryExpression, children: QueryChildren<Expression>): Expression {
        // Reconstruct query with converted children from the catamorphism.
        // The fold already pre-converted all child expressions via fold() calls.
        var changed = false

        val newSources =
            expr.sources.mapIndexed { i, src ->
                val newSourceExpr = children.sourceExpressions[i]
                val source = src.source
                if (
                    newSourceExpr != null &&
                        source is ExpressionQuerySource &&
                        newSourceExpr !== source.expression
                ) {
                    changed = true
                    src.copy(source = source.copy(expression = newSourceExpr))
                } else {
                    src
                }
            }

        val newLets =
            expr.lets.mapIndexed { i, let ->
                val newExpr = children.letExpressions[i]
                if (newExpr !== let.expression) {
                    changed = true
                    let.copy(expression = newExpr)
                } else {
                    let
                }
            }

        val newInclusions =
            expr.inclusions.mapIndexed { i, inc ->
                val newCondition = children.inclusionConditions[i]
                val newSourceExpr = children.inclusionSourceExpressions[i]
                when (inc) {
                    is WithClause -> {
                        val source = inc.source.source
                        val srcChanged =
                            newSourceExpr != null &&
                                source is ExpressionQuerySource &&
                                newSourceExpr !== source.expression
                        val condChanged = newCondition !== inc.condition
                        if (srcChanged || condChanged) {
                            changed = true
                            val newSrc =
                                if (srcChanged) {
                                    inc.source.copy(
                                        source =
                                            (source as ExpressionQuerySource).copy(
                                                expression = newSourceExpr!!
                                            )
                                    )
                                } else {
                                    inc.source
                                }
                            inc.copy(
                                source = newSrc,
                                condition = if (condChanged) newCondition else inc.condition,
                            )
                        } else {
                            inc
                        }
                    }
                    is WithoutClause -> {
                        val source = inc.source.source
                        val srcChanged =
                            newSourceExpr != null &&
                                source is ExpressionQuerySource &&
                                newSourceExpr !== source.expression
                        val condChanged = newCondition !== inc.condition
                        if (srcChanged || condChanged) {
                            changed = true
                            val newSrc =
                                if (srcChanged) {
                                    inc.source.copy(
                                        source =
                                            (source as ExpressionQuerySource).copy(
                                                expression = newSourceExpr!!
                                            )
                                    )
                                } else {
                                    inc.source
                                }
                            inc.copy(
                                source = newSrc,
                                condition = if (condChanged) newCondition else inc.condition,
                            )
                        } else {
                            inc
                        }
                    }
                }
            }

        val newWhere = children.where
        val whereChanged = newWhere !== expr.where

        val newResult =
            if (children.returnExpression != null && expr.result != null) {
                if (children.returnExpression !== expr.result!!.expression) {
                    changed = true
                    expr.result!!.copy(expression = children.returnExpression!!)
                } else {
                    expr.result
                }
            } else {
                expr.result
            }

        val newAggregate =
            if (expr.aggregate != null) {
                val newAggExpr = children.aggregateExpression
                val newStarting = children.aggregateStarting
                val aggExprChanged = newAggExpr !== expr.aggregate!!.expression
                val startingChanged = newStarting !== expr.aggregate!!.starting
                if (aggExprChanged || startingChanged) {
                    changed = true
                    expr.aggregate!!.copy(
                        expression = newAggExpr ?: expr.aggregate!!.expression,
                        starting = if (startingChanged) newStarting else expr.aggregate!!.starting,
                    )
                } else {
                    expr.aggregate
                }
            } else {
                expr.aggregate
            }

        if (whereChanged) changed = true

        return if (!changed) {
            expr
        } else {
            expr.copy(
                sources = newSources,
                lets = newLets,
                inclusions = newInclusions,
                where = newWhere,
                result = newResult ?: expr.result,
                aggregate = newAggregate,
            )
        }
    }

    override fun onRetrieve(expr: RetrieveExpression): Expression = expr

    override fun onUnsupported(expr: UnsupportedExpression): Expression = expr
}
