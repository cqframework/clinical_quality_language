package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.cql.ast.AliasedQuerySource
import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BinaryOperator
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
import org.hl7.cql.ast.Identifier
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
import org.hl7.cql.ast.ReturnClause
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.StringLiteral
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
     * - List conversions (element-level conversion) produce a synthetic [QueryExpression]
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
        // Cast conversion: insert AsExpression(implicit=true) so the emitter knows to use
        // asType (no strict field), matching legacy behavior for internally-generated casts.
        if (conversion.isCast) {
            val typeSpec = dataTypeToAstTypeSpecifier(conversion.toType) ?: return expr
            conversionsInserted++
            conversionKindCounts["cast"] = (conversionKindCounts["cast"] ?: 0) + 1
            return AsExpression(
                operand = expr,
                type = typeSpec,
                implicit = true,
                locator = expr.locator,
            )
        }
        // List conversion with operator-based element-level conversion: build a synthetic
        // QueryExpression that applies the inner conversion to each element.
        // Produces: (X in list return <innerConversion>(X))
        // Only handles operator-based inner conversions (ToDecimal, ToLong, etc.);
        // cast-inner list conversions (List<Any>→List<T> via As) are deferred to
        // the set-operator emission path (emitSetOperator) which applies them directly.
        if (
            conversion.isListConversion &&
                conversion.conversion != null &&
                conversion.conversion!!.operator != null
        ) {
            return buildListConversionQuery(expr, conversion.conversion!!, conversion.toType)
        }
        // Interval conversion: deferred to emission because it produces Property access on
        // low/high/lowClosed/highClosed using ELM-specific lowClosedExpression which cannot
        // be expressed in the AST IntervalLiteral (which only has static lowerClosed booleans).
        if (conversion.isIntervalConversion && conversion.conversion != null) {
            return expr
        }
        // List/interval promotions and demotions not yet handled
        return expr
    }

    /**
     * Build a synthetic [QueryExpression] that applies [elementConversion] to each element of
     * [listExpr]. Produces the AST equivalent of: (X in listExpr return <elementConversion>(X))
     *
     * The alias identifier is registered in the [TypeTable] so the emitter can resolve it to an ELM
     * [AliasRef] rather than an [IdentifierRef]. The resulting query's type is also registered as
     * [resultListType] so downstream code (e.g., union choice-type wrapping) can still determine
     * the element type.
     */
    private fun buildListConversionQuery(
        listExpr: Expression,
        elementConversion: Conversion,
        resultListType: DataType,
    ): Expression {
        val aliasName = "X"
        // The element type is the source type of the inner conversion
        val elementType = elementConversion.fromType

        // Create the alias reference expression and register its resolution in the TypeTable
        // so that the emitter produces AliasRef("X"), not IdentifierRef("X").
        val aliasRefExpr =
            IdentifierExpression(
                name = QualifiedIdentifier(listOf(aliasName)),
                locator = listExpr.locator,
            )
        typeTable.setIdentifierResolution(aliasRefExpr, Resolution.AliasRef(aliasName, elementType))

        // Apply the inner conversion to the alias reference
        val convertedElement = insertConversion(aliasRefExpr, elementConversion)

        conversionsInserted++
        conversionKindCounts["list"] = (conversionKindCounts["list"] ?: 0) + 1

        val queryExpr =
            QueryExpression(
                sources =
                    listOf(
                        AliasedQuerySource(
                            source = ExpressionQuerySource(expression = listExpr),
                            alias = Identifier(aliasName),
                            locator = listExpr.locator,
                        )
                    ),
                lets = emptyList(),
                inclusions = emptyList(),
                result =
                    ReturnClause(
                        // all=true → emitter sets ElmReturnClause.distinct=false,
                        // matching the legacy translator's withDistinct(false) on implicit
                        // list conversion queries.
                        all = true,
                        distinct = false,
                        expression = convertedElement,
                        locator = listExpr.locator,
                    ),
                locator = listExpr.locator,
            )
        // Register the result type so the emitter can determine element types for
        // union/intersect/except choice-type wrapping (semanticModel[queryExpr]).
        typeTable[queryExpr] = resultListType
        return queryExpr
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

    /**
     * Wrap an expression in an [AsExpression] targeting [targetType]. Used to insert null-As
     * wrapping (for null literals in typed contexts) into the AST before emission.
     */
    private fun wrapNullAsAst(expr: Expression, targetType: DataType): Expression {
        val typeSpec = dataTypeToAstTypeSpecifier(targetType) ?: return expr
        conversionsInserted++
        conversionKindCounts["nullAs"] = (conversionKindCounts["nullAs"] ?: 0) + 1
        return AsExpression(
            operand = expr,
            type = typeSpec,
            implicit = true,
            locator = expr.locator,
        )
    }

    /**
     * Wrap an expression in a [ConversionExpression] to apply an implicit type conversion from
     * [fromType] to [toType]. Used for element-level type promotion (e.g., Integer→Decimal) in
     * lists, intervals, and case branches.
     */
    private fun applyImplicitConversionAst(
        expr: Expression,
        fromType: DataType,
        toType: DataType,
    ): Expression {
        if (fromType == toType) return expr
        val convName =
            implicitConversionNameForTypes(fromType.toString(), toType.toString()) ?: return expr
        val typeName = conversionOperatorToTypeName(convName) ?: return expr
        conversionsInserted++
        conversionKindCounts["implicit"] = (conversionKindCounts["implicit"] ?: 0) + 1
        return ConversionExpression(
            operand = expr,
            destinationType = NamedTypeSpecifier(name = QualifiedIdentifier(listOf(typeName))),
            locator = expr.locator,
        )
    }

    /** Map known implicit conversion type name pairs to their operator names. */
    private fun implicitConversionNameForTypes(fromTypeName: String, toTypeName: String): String? =
        when {
            fromTypeName == "System.Integer" && toTypeName == "System.Long" -> "ToLong"
            fromTypeName == "System.Integer" && toTypeName == "System.Decimal" -> "ToDecimal"
            fromTypeName == "System.Long" && toTypeName == "System.Decimal" -> "ToDecimal"
            fromTypeName == "System.Code" && toTypeName == "System.Concept" -> "ToConcept"
            else -> null
        }

    /**
     * Determine whether an expression is a null literal. Used to decide whether to apply null-As
     * wrapping.
     */
    private fun isNullLiteralExpr(expr: Expression): Boolean =
        expr is org.hl7.cql.ast.LiteralExpression && expr.literal is org.hl7.cql.ast.NullLiteral

    /**
     * Apply null-As or implicit conversion wrapping to an expression if its type differs from
     * [targetType]. Returns a (possibly wrapped) expression and records whether a wrapping was
     * applied.
     * - If [originalExpr] is a null literal and [targetType] != Any → wrap in AsExpression
     * - If [fromType] != null and [fromType] != [targetType] → wrap in ConversionExpression
     */
    private fun maybeWrapForTargetType(
        foldedExpr: Expression,
        originalExpr: Expression,
        targetType: DataType,
    ): Expression {
        val anyType = operatorRegistry.type("Any")
        if (targetType == anyType) return foldedExpr
        // Null literal: wrap in AsExpression(null, targetType)
        if (isNullLiteralExpr(originalExpr)) {
            return wrapNullAsAst(foldedExpr, targetType)
        }
        // Type mismatch: apply implicit conversion (e.g., Integer→Decimal)
        val fromType = typeTable[originalExpr]
        if (fromType != null && fromType != targetType) {
            return applyImplicitConversionAst(foldedExpr, fromType, targetType)
        }
        return foldedExpr
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
        var l = operands[0]
        var r = operands[1]

        // CONCAT (&) operator: wrap each operand in Coalesce(operand, '') to match legacy
        // translator behavior. This matches EmissionContext.emitConcatenate.
        if (expr.operator == BinaryOperator.CONCAT) {
            l = wrapInCoalesceWithEmptyString(l)
            r = wrapInCoalesceWithEmptyString(r)
        }

        // Compare against the ORIGINAL expression's children (not the pre-folded `left`/`right`
        // params) so that changes made by the recursive fold are also propagated.
        return if (l === expr.left && r === expr.right) expr else expr.copy(left = l, right = r)
    }

    /**
     * Wrap an expression in Coalesce(expr, '') — used for the CONCAT (&) operator to match the
     * legacy translator's behavior of coalescing null string operands to empty string.
     */
    private fun wrapInCoalesceWithEmptyString(expr: Expression): Expression {
        val emptyString =
            LiteralExpression(literal = StringLiteral(value = ""), locator = expr.locator)
        return FunctionCallExpression(
            target = null,
            function = Identifier("Coalesce"),
            arguments = listOf(expr, emptyString),
            locator = expr.locator,
        )
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
        // Insert operator-based conversions (ToDecimal, ToLong, etc.) for function arguments.
        // Cast, list, and interval conversions remain in FunctionEmission.kt (emission phase)
        // because they produce ELM-level structures (As, Query, Interval) not AST nodes.
        val resolution = typeTable.getOperatorResolution(expr)
        var convertedArgs = applyConversions(resolution, arguments)

        // DateTime/Date/Time constructor null arguments: wrap null args in AsExpression
        // to match legacy behavior where null args are typed as Integer (or Decimal for offset).
        val functionName = expr.function.value
        convertedArgs = applyDateTimeNullArgConversions(functionName, convertedArgs, expr.arguments)
        convertedArgs = applyCoalesceNullArgConversions(expr, functionName, convertedArgs)

        // Compare against the ORIGINAL expression's children so that changes made by the
        // recursive fold are also propagated.
        val argsChanged = convertedArgs.indices.any { convertedArgs[it] !== expr.arguments[it] }
        val targetChanged = target !== expr.target
        return if (!targetChanged && !argsChanged) {
            expr
        } else {
            expr.copy(target = target, arguments = convertedArgs)
        }
    }

    /**
     * For DateTime/Date/Time constructor functions, wrap null arguments in AsExpression to match
     * legacy behavior. Integer args get As(Integer), the timezoneOffset (last arg of DateTime) gets
     * As(Decimal).
     */
    @Suppress("CyclomaticComplexMethod")
    private fun applyDateTimeNullArgConversions(
        functionName: String,
        foldedArgs: List<Expression>,
        originalArgs: List<Expression>,
    ): List<Expression> {
        val integerType = operatorRegistry.type("Integer")
        val decimalType = operatorRegistry.type("Decimal")

        fun wrapNullArgAs(index: Int, targetType: DataType): Expression {
            val original = originalArgs[index]
            val folded = foldedArgs[index]
            // Skip if already wrapped (e.g., by applyConversions cast handling above)
            if (folded !== original) return folded
            return if (isNullLiteralExpr(original) && targetType != operatorRegistry.type("Any")) {
                wrapNullAsAst(folded, targetType)
            } else {
                folded
            }
        }

        return when (functionName) {
            "DateTime" -> {
                if (foldedArgs.isEmpty()) return foldedArgs
                val result = foldedArgs.toMutableList()
                // args: year, month, day, hour, minute, second, millisecond → Integer
                // arg 7 (index 7): timezoneOffset → Decimal
                for (i in result.indices) {
                    result[i] =
                        if (i == 7) wrapNullArgAs(i, decimalType) else wrapNullArgAs(i, integerType)
                }
                result
            }
            "Date" -> {
                if (foldedArgs.isEmpty()) return foldedArgs
                val result = foldedArgs.toMutableList()
                for (i in result.indices) {
                    result[i] = wrapNullArgAs(i, integerType)
                }
                result
            }
            "Time" -> {
                if (foldedArgs.isEmpty()) return foldedArgs
                val result = foldedArgs.toMutableList()
                for (i in result.indices) {
                    result[i] = wrapNullArgAs(i, integerType)
                }
                result
            }
            else -> foldedArgs
        }
    }

    /**
     * For Coalesce function calls, wrap null arguments in AsExpression(resultType) to match legacy
     * translator behavior. The legacy wraps each null arg with an implicit As cast to the overall
     * Coalesce result type.
     */
    private fun applyCoalesceNullArgConversions(
        expr: FunctionCallExpression,
        functionName: String,
        foldedArgs: List<Expression>,
    ): List<Expression> {
        if (functionName != "Coalesce") return foldedArgs
        // Look up the result type of the Coalesce expression
        val resultType = typeTable[expr] ?: return foldedArgs
        val anyType = operatorRegistry.type("Any")
        if (resultType == anyType) return foldedArgs

        var changed = false
        val result = foldedArgs.toMutableList()
        expr.arguments.forEachIndexed { i, originalArg ->
            // Skip if already wrapped (e.g., by applyConversions cast handling above)
            if (result[i] !== originalArg && isNullLiteralExpr(originalArg)) return@forEachIndexed
            if (isNullLiteralExpr(originalArg)) {
                result[i] = wrapNullAsAst(result[i], resultType)
                changed = true
            }
        }
        return if (changed) result else foldedArgs
    }

    // --- Identity handlers: reconstruct with pre-folded children ---

    override fun onLiteral(
        expr: LiteralExpression,
        children: LiteralChildren<Expression>,
    ): Expression {
        // Reconstruct list/interval/tuple literals with pre-folded children so that
        // conversions inserted inside nested expressions (e.g., queries inside list
        // elements) are properly propagated. Also insert null-As and implicit type-promotion
        // wrappers for list elements and interval bounds when needed.
        val literal = expr.literal
        return when (literal) {
            is org.hl7.cql.ast.ListLiteral -> {
                // Determine the list's inferred element type from the type table
                val listType = typeTable[expr]
                val elementType = if (listType is ListType) listType.elementType else null
                val newElements =
                    if (elementType != null) {
                        literal.elements.indices.map { i ->
                            maybeWrapForTargetType(
                                children.elements[i],
                                literal.elements[i],
                                elementType,
                            )
                        }
                    } else {
                        children.elements
                    }
                if (newElements.indices.all { newElements[it] === literal.elements[it] }) {
                    expr
                } else {
                    expr.copy(literal = literal.copy(elements = newElements))
                }
            }
            is org.hl7.cql.ast.IntervalLiteral -> {
                val newLow = children.intervalLow
                val newHigh = children.intervalHigh
                // Determine the interval's inferred point type from the type table
                val intervalType = typeTable[expr]
                val pointType = if (intervalType is IntervalType) intervalType.pointType else null
                val wrappedLow =
                    if (newLow != null && pointType != null) {
                        maybeWrapForTargetType(newLow, literal.lower, pointType)
                    } else newLow
                val wrappedHigh =
                    if (newHigh != null && pointType != null) {
                        maybeWrapForTargetType(newHigh, literal.upper, pointType)
                    } else newHigh
                if (wrappedLow === literal.lower && wrappedHigh === literal.upper) {
                    expr
                } else {
                    expr.copy(
                        literal =
                            literal.copy(
                                lower = wrappedLow ?: literal.lower,
                                upper = wrappedHigh ?: literal.upper,
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
        // Determine the case expression's overall result type for branch type-promotion
        val anyType = operatorRegistry.type("Any")
        val resultType = typeTable[expr]?.let { if (it == anyType) null else it }

        // For comparand-style cases, determine comparand type for when-clause conversions
        val comparandType = expr.comparand?.let { typeTable[it] }

        var changed = comparand !== expr.comparand || elseResult !== expr.elseResult

        // Apply null-wrapping and type-promotion to when-clauses and branch results
        val newCases =
            cases.mapIndexed { i, c ->
                val originalItem = expr.cases[i]
                // Comparand when-clause: convert when value to comparand type
                val newCondition =
                    if (comparandType != null && comparandType != anyType) {
                        maybeWrapForTargetType(c.condition, originalItem.condition, comparandType)
                    } else {
                        c.condition
                    }
                // Branch result: apply null-wrapping or type-promotion toward resultType
                val newResult =
                    if (resultType != null) {
                        maybeWrapForTargetType(c.result, originalItem.result, resultType)
                    } else {
                        c.result
                    }
                val condChanged = newCondition !== originalItem.condition
                val resChanged = newResult !== originalItem.result
                if (condChanged || resChanged) {
                    changed = true
                    originalItem.copy(condition = newCondition, result = newResult)
                } else if (
                    c.condition !== originalItem.condition || c.result !== originalItem.result
                ) {
                    changed = true
                    originalItem.copy(condition = c.condition, result = c.result)
                } else {
                    originalItem
                }
            }

        // Apply null-wrapping or type-promotion to else branch
        val newElse =
            if (resultType != null) {
                maybeWrapForTargetType(elseResult, expr.elseResult, resultType)
            } else {
                elseResult
            }
        if (newElse !== expr.elseResult) changed = true

        return if (!changed) expr
        else expr.copy(comparand = comparand, cases = newCases, elseResult = newElse)
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
        // Null-As wrapping: exists null → wrap null as List<Any>
        val wrappedOperand =
            if (isNullLiteralExpr(expr.operand)) {
                val anyType = operatorRegistry.type("Any")
                wrapNullAsAst(operand, ListType(anyType))
            } else {
                operand
            }
        return if (wrappedOperand === expr.operand) expr else expr.copy(operand = wrappedOperand)
    }

    override fun onMembership(
        expr: MembershipExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        val resolution = typeTable.getOperatorResolution(expr)
        val operands = applyConversions(resolution, listOf(left, right))
        var l = operands[0]
        var r = operands[1]

        // Null-As wrapping for membership operators (mirrors CollectionOperatorEmission logic)
        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        val anyType = operatorRegistry.type("Any")

        when (expr.operator) {
            org.hl7.cql.ast.MembershipOperator.CONTAINS -> {
                // Contains(collection, element) - wrap null element based on collection's element
                // type
                if (isNullLiteralExpr(expr.right)) {
                    val elemType = elementTypeOfDataType(leftType)
                    if (elemType != null && elemType != anyType) {
                        r = wrapNullAsAst(r, elemType)
                    }
                }
                // Contains(null, element) - wrap null collection based on element type
                if (isNullLiteralExpr(expr.left) && rightType != null && rightType != anyType) {
                    l = wrapNullAsAst(l, ListType(rightType))
                }
            }
            org.hl7.cql.ast.MembershipOperator.IN -> {
                // In(element, interval) - wrap null element based on interval's point type
                if (isNullLiteralExpr(expr.left) && rightType is IntervalType) {
                    val pointType = rightType.pointType
                    if (pointType != anyType) {
                        l = wrapNullAsAst(l, pointType)
                    }
                }
                // In(element, null) - wrap null collection as interval based on element type
                if (isNullLiteralExpr(expr.right) && leftType != null && leftType != anyType) {
                    r = wrapNullAsAst(r, IntervalType(leftType))
                }
            }
        }

        return if (l === expr.left && r === expr.right) expr else expr.copy(left = l, right = r)
    }

    /** Get the element type of a list or the point type of an interval. */
    private fun elementTypeOfDataType(type: DataType?): DataType? =
        when (type) {
            is ListType -> type.elementType
            is IntervalType -> type.pointType
            else -> null
        }

    override fun onListTransform(expr: ListTransformExpression, operand: Expression): Expression {
        // Null-As wrapping for null operands
        val anyType = operatorRegistry.type("Any")
        val wrappedOperand =
            if (isNullLiteralExpr(expr.operand)) {
                when (expr.listTransformKind) {
                    // distinct null → As(List<Any>)
                    org.hl7.cql.ast.ListTransformKind.DISTINCT ->
                        wrapNullAsAst(operand, ListType(anyType))
                    // flatten null → As(List<List<Any>>)
                    org.hl7.cql.ast.ListTransformKind.FLATTEN ->
                        wrapNullAsAst(operand, ListType(ListType(anyType)))
                }
            } else {
                operand
            }
        return if (wrappedOperand === expr.operand) expr else expr.copy(operand = wrappedOperand)
    }

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: Expression,
        per: Expression?,
    ): Expression {
        // Null-As wrapping: when the source is null, wrap in As(List<Interval<Any>>)
        val wrappedOperand =
            if (isNullLiteralExpr(expr.operand)) {
                val anyType = operatorRegistry.type("Any")
                wrapNullAsAst(operand, ListType(IntervalType(anyType)))
            } else {
                operand
            }
        return if (wrappedOperand === expr.operand && per === expr.perExpression) expr
        else expr.copy(operand = wrappedOperand, perExpression = per)
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
        // Null-As wrapping: width of null → wrap null as Interval<Any>
        val wrappedOperand =
            if (isNullLiteralExpr(expr.operand)) {
                val anyType = operatorRegistry.type("Any")
                wrapNullAsAst(operand, IntervalType(anyType))
            } else {
                operand
            }
        return if (wrappedOperand === expr.operand) expr else expr.copy(operand = wrappedOperand)
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
        var l = operands[0]
        var r = operands[1]

        // Null-As wrapping for interval relation operands (mirrors IntervalOperatorEmission logic)
        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        val anyType = operatorRegistry.type("Any")

        if (
            isNullLiteralExpr(expr.right) &&
                leftType is IntervalType &&
                leftType.pointType != anyType
        ) {
            // Right operand is null, left is an interval → wrap null as point type
            r = wrapNullAsAst(r, leftType.pointType)
        } else if (
            isNullLiteralExpr(expr.left) &&
                rightType is IntervalType &&
                rightType.pointType != anyType
        ) {
            // Left operand is null, right is an interval → wrap null as point type
            l = wrapNullAsAst(l, rightType.pointType)
        }

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
