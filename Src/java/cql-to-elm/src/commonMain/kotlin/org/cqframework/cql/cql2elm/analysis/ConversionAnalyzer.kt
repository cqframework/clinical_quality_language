@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
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
import org.hl7.cql.ast.ExternalConstantExpression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
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
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QueryChildren
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

/**
 * Populates the [SyntheticTable] with all conversion kinds: operator conversions, casts, list/
 * interval conversions, null-As wrapping, coalesce wrapping, and type unification for branches and
 * literal elements.
 *
 * Replaces the mutation-based [ConversionInserter] — records synthetics instead of modifying the
 * AST.
 *
 * **Not thread-safe.** Create a fresh instance per analysis iteration.
 */
class ConversionAnalyzer(
    private val typeTable: TypeTable,
    private val operatorRegistry: OperatorRegistry,
    private val syntheticTable: SyntheticTable,
) : ExpressionFold<Unit> {

    /** Number of new synthetics inserted during this analysis pass. */
    var newSyntheticsInserted: Int = 0
        private set

    /** Analyze all statements and definitions in a library. */
    fun analyzeLibrary(library: Library) {
        val before = syntheticTable.syntheticsInserted
        for (statement in library.statements) {
            analyzeStatement(statement)
        }
        for (definition in library.definitions) {
            analyzeDefinition(definition)
        }
        newSyntheticsInserted = syntheticTable.syntheticsInserted - before
    }

    private fun analyzeStatement(statement: Statement) {
        when (statement) {
            is ExpressionDefinition -> fold(statement.expression)
            is FunctionDefinition -> {
                val body = statement.body
                if (body is ExpressionFunctionBody) {
                    fold(body.expression)
                }
            }
            else -> {}
        }
    }

    private fun analyzeDefinition(definition: org.hl7.cql.ast.Definition) {
        when (definition) {
            is org.hl7.cql.ast.ParameterDefinition -> {
                definition.default?.let { fold(it) }
            }
            else -> {}
        }
    }

    // --- Conversion recording helpers ---

    /** Record a synthetic if not already present at the given parent/slot. */
    private fun recordIfNew(parent: Expression, slot: Slot, synthetic: Synthetic) {
        val existing = syntheticTable.get(parent, slot)
        if (existing.contains(synthetic)) return
        syntheticTable.add(parent, slot, synthetic)
    }

    /**
     * Convert a [Conversion] from an [OperatorResolution] to a [Synthetic], or null if the
     * conversion kind isn't handled by the side table.
     */
    private fun conversionToSynthetic(conversion: Conversion): Synthetic? {
        // Operator conversion (ToDecimal, ToLong, etc.)
        val operatorName = operatorRegistry.conversionOperatorName(conversion)
        if (operatorName != null) {
            return Synthetic.OperatorConversion(operatorName)
        }
        // Cast conversion
        if (conversion.isCast) {
            return Synthetic.ImplicitCast(conversion.toType)
        }
        // List conversion with operator inner conversion
        if (
            conversion.isListConversion &&
                conversion.conversion != null &&
                conversion.conversion!!.operator != null
        ) {
            return Synthetic.ListConversion(conversion.conversion!!.operator!!.name)
        }
        // Interval conversion with operator inner conversion
        if (
            conversion.isIntervalConversion &&
                conversion.conversion != null &&
                conversion.conversion!!.operator != null
        ) {
            return Synthetic.IntervalConversion(conversion.conversion!!.operator!!.name)
        }
        return null
    }

    /**
     * Record synthetics from an [OperatorResolution]'s conversions for each slot. Handles all
     * conversion kinds (operator, cast, list, interval).
     */
    private fun recordResolutionConversions(
        parent: Expression,
        resolution: OperatorResolution?,
        slots: List<Slot>,
    ) {
        if (resolution == null || !resolution.hasConversions()) return
        resolution.conversions.forEachIndexed { index, conversion ->
            if (conversion != null && index < slots.size) {
                val synthetic = conversionToSynthetic(conversion) ?: return@forEachIndexed
                recordIfNew(parent, slots[index], synthetic)
            }
        }
    }

    /** Check if an expression is a null literal. */
    private fun isNullLiteralExpr(expr: Expression): Boolean =
        expr is LiteralExpression && expr.literal is NullLiteral

    /**
     * Record a synthetic for type unification: if the child's type doesn't match [targetType],
     * record the appropriate conversion (NullAs, OperatorConversion, ChoiceAs, etc.).
     */
    private fun recordTargetTypeConversion(
        parent: Expression,
        slot: Slot,
        childExpr: Expression,
        targetType: DataType,
    ) {
        val anyType = operatorRegistry.type("Any")
        if (targetType == anyType) return

        // Null literal: record ImplicitCast
        if (isNullLiteralExpr(childExpr)) {
            recordIfNew(parent, slot, Synthetic.ImplicitCast(targetType))
            return
        }

        // Type mismatch: determine conversion
        val fromType = typeTable[childExpr] ?: return
        if (fromType == targetType) return

        // ChoiceType: record ImplicitCast (only if the type can be converted to a TypeSpecifier;
        // TupleTypes can't, so ChoiceType containing tuples skips wrapping)
        if (targetType is ChoiceType && canEmitAsTypeSpecifier(targetType)) {
            recordIfNew(parent, slot, Synthetic.ImplicitCast(targetType))
            return
        }

        // List<T> → List<Choice<...>> for union operands
        if (
            targetType is ListType &&
                targetType.elementType is ChoiceType &&
                fromType is ListType &&
                canEmitAsTypeSpecifier(targetType)
        ) {
            recordIfNew(parent, slot, Synthetic.ImplicitCast(targetType))
            return
        }

        // Implicit operator conversion (Integer→Decimal, etc.)
        val convName = implicitConversionName(fromType.toString(), targetType.toString())
        if (convName != null) {
            recordIfNew(parent, slot, Synthetic.OperatorConversion(convName))
        }
    }

    /** Map known implicit conversion type name pairs to their operator names. */
    private fun implicitConversionName(fromTypeName: String, toTypeName: String): String? =
        when {
            fromTypeName == "System.Integer" && toTypeName == "System.Long" -> "ToLong"
            fromTypeName == "System.Integer" && toTypeName == "System.Decimal" -> "ToDecimal"
            fromTypeName == "System.Long" && toTypeName == "System.Decimal" -> "ToDecimal"
            fromTypeName == "System.Code" && toTypeName == "System.Concept" -> "ToConcept"
            else -> null
        }

    /**
     * Check if a DataType can be converted to a TypeSpecifier for emission. TupleTypes and other
     * complex types cannot, so synthetics involving them are skipped (matching CI behavior).
     */
    private fun canEmitAsTypeSpecifier(type: DataType): Boolean =
        when (type) {
            is org.hl7.cql.model.SimpleType -> true
            is org.hl7.cql.model.ClassType -> true
            is ListType -> canEmitAsTypeSpecifier(type.elementType)
            is IntervalType -> canEmitAsTypeSpecifier(type.pointType)
            is ChoiceType -> type.types.all { canEmitAsTypeSpecifier(it) }
            else -> false
        }

    /** Get the element type of a list or the point type of an interval. */
    private fun elementTypeOfDataType(type: DataType?): DataType? =
        when (type) {
            is ListType -> type.elementType
            is IntervalType -> type.pointType
            else -> null
        }

    // --- ExpressionFold<Unit> implementation ---

    override fun onBinaryOperator(expr: OperatorBinaryExpression, left: Unit, right: Unit) {
        val resolution = typeTable.getOperatorResolution(expr)
        recordResolutionConversions(expr, resolution, listOf(Slot.Left, Slot.Right))

        val op = expr.operator

        // CONCAT coalescing and Add→Concatenate rewrite are structural lowering,
        // handled in emission (OperatorEmission.kt). Not conversions.

        // Union/intersect/except: choice wrapping for mismatched element types
        if (
            op == BinaryOperator.UNION ||
                op == BinaryOperator.INTERSECT ||
                op == BinaryOperator.EXCEPT
        ) {
            recordSetOperatorConversions(expr)
        }

        // Interval<Any> literal bounds: propagate point type from the typed counterpart
        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        propagateIntervalPointType(expr.left, leftType, expr.right, rightType)
        propagateIntervalPointType(expr.right, rightType, expr.left, leftType)
    }

    /** Record synthetics for union/intersect/except when element types differ. */
    @Suppress("NestedBlockDepth")
    private fun recordSetOperatorConversions(expr: OperatorBinaryExpression) {
        val anyType = operatorRegistry.type("Any")
        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        if (leftType is ListType && rightType is ListType) {
            val leftElem = leftType.elementType
            val rightElem = rightType.elementType
            if (leftElem != rightElem) {
                // Case 1: One operand is List<Any> (empty list) — list demotion
                if (rightElem == anyType && leftElem != anyType) {
                    recordIfNew(expr, Slot.Right, Synthetic.ListDemotion(leftElem, leftType))
                } else if (leftElem == anyType && rightElem != anyType) {
                    recordIfNew(expr, Slot.Left, Synthetic.ListDemotion(rightElem, rightType))
                }
                // Case 2: Different concrete types — wrap both in As(List<Choice>)
                else if (
                    leftElem !is ChoiceType &&
                        rightElem !is ChoiceType &&
                        !leftElem.isSuperTypeOf(rightElem) &&
                        !rightElem.isSuperTypeOf(leftElem)
                ) {
                    val choiceElem = ChoiceType(listOf(leftElem, rightElem).distinct())
                    val choiceListType = ListType(choiceElem)
                    recordIfNew(expr, Slot.Left, Synthetic.ImplicitCast(choiceListType))
                    recordIfNew(expr, Slot.Right, Synthetic.ImplicitCast(choiceListType))
                }
            }
        }
    }

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Unit) {
        val resolution = typeTable.getOperatorResolution(expr)
        recordResolutionConversions(expr, resolution, listOf(Slot.Operand))
    }

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: Unit?,
        arguments: List<Unit>,
    ) {
        val resolution = typeTable.getOperatorResolution(expr)
        val slots = arguments.indices.map { Slot.Argument(it) }
        recordResolutionConversions(expr, resolution, slots)

        // DateTime/Date/Time null arg wrapping
        val functionName = expr.function.value
        recordDateTimeNullArgConversions(functionName, expr)

        // Null arg collection wrapping (e.g., IndexOf(null, {}) → As(null, List<Any>))
        // cannot be a Synthetic — it changes effective types from Any to List<Any>, which
        // breaks generic resolution on re-typing (T=List<Any> instead of T=Any).
        // This wrapping must happen at emission time. See CqlListOperators KNOWN_SKIP.
    }

    /** Record NullAs synthetics for DateTime/Date/Time null arguments. */
    @Suppress("CyclomaticComplexMethod")
    private fun recordDateTimeNullArgConversions(
        functionName: String,
        expr: FunctionCallExpression,
    ) {
        val integerType = operatorRegistry.type("Integer")
        val decimalType = operatorRegistry.type("Decimal")
        val anyType = operatorRegistry.type("Any")

        when (functionName) {
            "DateTime" -> {
                for (i in expr.arguments.indices) {
                    val arg = expr.arguments[i]
                    if (isNullLiteralExpr(arg)) {
                        val targetType = if (i == 7) decimalType else integerType
                        if (targetType != anyType) {
                            recordIfNew(expr, Slot.Argument(i), Synthetic.ImplicitCast(targetType))
                        }
                    }
                }
            }
            "Date",
            "Time" -> {
                for (i in expr.arguments.indices) {
                    val arg = expr.arguments[i]
                    if (isNullLiteralExpr(arg)) {
                        if (integerType != anyType) {
                            recordIfNew(expr, Slot.Argument(i), Synthetic.ImplicitCast(integerType))
                        }
                    }
                }
            }
        }
    }

    override fun onMembership(expr: MembershipExpression, left: Unit, right: Unit) {
        val resolution = typeTable.getOperatorResolution(expr)
        recordResolutionConversions(expr, resolution, listOf(Slot.Left, Slot.Right))

        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        val anyType = operatorRegistry.type("Any")

        when (expr.operator) {
            org.hl7.cql.ast.MembershipOperator.CONTAINS -> {
                if (isNullLiteralExpr(expr.right)) {
                    val elemType = elementTypeOfDataType(leftType)
                    if (elemType != null && elemType != anyType) {
                        recordIfNew(expr, Slot.Right, Synthetic.ImplicitCast(elemType))
                    }
                }
                if (isNullLiteralExpr(expr.left) && rightType != null && rightType != anyType) {
                    recordIfNew(expr, Slot.Left, Synthetic.ImplicitCast(ListType(rightType)))
                }
            }
            org.hl7.cql.ast.MembershipOperator.IN -> {
                if (isNullLiteralExpr(expr.left) && rightType is IntervalType) {
                    val pointType = rightType.pointType
                    if (pointType != anyType) {
                        recordIfNew(expr, Slot.Left, Synthetic.ImplicitCast(pointType))
                    }
                }
                if (isNullLiteralExpr(expr.right) && leftType != null && leftType != anyType) {
                    recordIfNew(expr, Slot.Right, Synthetic.ImplicitCast(IntervalType(leftType)))
                }
            }
        }
    }

    override fun onIntervalRelation(expr: IntervalRelationExpression, left: Unit, right: Unit) {
        val resolution = typeTable.getOperatorResolution(expr)
        recordResolutionConversions(expr, resolution, listOf(Slot.Left, Slot.Right))

        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        val anyType = operatorRegistry.type("Any")

        // Null literal operands
        if (
            isNullLiteralExpr(expr.right) &&
                leftType is IntervalType &&
                leftType.pointType != anyType
        ) {
            recordIfNew(expr, Slot.Right, Synthetic.ImplicitCast(leftType.pointType))
        } else if (
            isNullLiteralExpr(expr.left) &&
                rightType is IntervalType &&
                rightType.pointType != anyType
        ) {
            recordIfNew(expr, Slot.Left, Synthetic.ImplicitCast(rightType.pointType))
        }

        // Interval<Any> operands: propagate point type from the typed counterpart into
        // the interval literal's bounds so the convergence loop picks them up.
        propagateIntervalPointType(expr.left, leftType, expr.right, rightType)
        propagateIntervalPointType(expr.right, rightType, expr.left, leftType)

        // Boundary selectors, point-interval promotion, operator rewrites, and CONCAT
        // coalescing are structural lowering — handled in emission, not here.
    }

    /**
     * When [target] is an interval literal with point type Any and [source] has a concrete point
     * type, record bound-level synthetics on the interval literal so null bounds get wrapped.
     */
    private fun propagateIntervalPointType(
        source: Expression,
        sourceType: DataType?,
        target: Expression,
        targetType: DataType?,
    ) {
        if (sourceType !is IntervalType || targetType !is IntervalType) return
        val anyType = operatorRegistry.type("Any")
        if (targetType.pointType != anyType || sourceType.pointType == anyType) return
        val pointType = sourceType.pointType
        // Target must be an interval literal to record bound-level synthetics
        if (target is LiteralExpression && target.literal is org.hl7.cql.ast.IntervalLiteral) {
            val interval = target.literal as org.hl7.cql.ast.IntervalLiteral
            recordTargetTypeConversion(target, Slot.IntervalLow, interval.lower, pointType)
            recordTargetTypeConversion(target, Slot.IntervalHigh, interval.upper, pointType)
        }
    }

    override fun onExists(expr: ExistsExpression, operand: Unit) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            recordIfNew(expr, Slot.Operand, Synthetic.ImplicitCast(ListType(anyType)))
        }
    }

    override fun onListTransform(expr: ListTransformExpression, operand: Unit) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            val targetType =
                when (expr.listTransformKind) {
                    org.hl7.cql.ast.ListTransformKind.DISTINCT -> ListType(anyType)
                    org.hl7.cql.ast.ListTransformKind.FLATTEN -> ListType(ListType(anyType))
                }
            recordIfNew(expr, Slot.Operand, Synthetic.ImplicitCast(targetType))
        }
        // Heterogeneous flatten detection is structural (lowering), not a type conversion.
    }

    override fun onExpandCollapse(expr: ExpandCollapseExpression, operand: Unit, per: Unit?) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            recordIfNew(expr, Slot.Operand, Synthetic.ImplicitCast(ListType(IntervalType(anyType))))
        }
    }

    override fun onWidth(expr: WidthExpression, operand: Unit) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            recordIfNew(expr, Slot.Operand, Synthetic.ImplicitCast(IntervalType(anyType)))
        }
    }

    override fun onIf(expr: IfExpression, condition: Unit, thenBranch: Unit, elseBranch: Unit) {
        val resultType = typeTable[expr] ?: return
        recordTargetTypeConversion(expr, Slot.ThenBranch, expr.thenBranch, resultType)
        recordTargetTypeConversion(expr, Slot.ElseBranch, expr.elseBranch, resultType)
    }

    @Suppress("CyclomaticComplexMethod")
    override fun onCase(
        expr: CaseExpression,
        comparand: Unit?,
        cases: List<CaseChildren<Unit>>,
        elseResult: Unit,
    ) {
        val anyType = operatorRegistry.type("Any")
        val resultType = typeTable[expr]?.let { if (it == anyType) null else it }

        // Comparand type for when-clause conversions
        val comparandType = expr.comparand?.let { typeTable[it] }

        // Apply type unification to each case branch
        cases.forEachIndexed { i, _ ->
            val originalItem = expr.cases[i]
            if (comparandType != null && comparandType != anyType) {
                recordTargetTypeConversion(
                    expr,
                    Slot.CaseCondition(i),
                    originalItem.condition,
                    comparandType,
                )
            }
            if (resultType != null) {
                recordTargetTypeConversion(
                    expr,
                    Slot.CaseBranch(i),
                    originalItem.result,
                    resultType,
                )
            }
        }

        // Else branch
        if (resultType != null) {
            recordTargetTypeConversion(expr, Slot.ElseBranch, expr.elseResult, resultType)
        }
    }

    override fun onLiteral(expr: LiteralExpression, children: LiteralChildren<Unit>) {
        val literal = expr.literal
        when (literal) {
            is org.hl7.cql.ast.ListLiteral -> {
                val listType = typeTable[expr]
                val elementType = if (listType is ListType) listType.elementType else null
                if (elementType != null) {
                    literal.elements.forEachIndexed { i, elem ->
                        recordTargetTypeConversion(expr, Slot.ListElement(i), elem, elementType)
                    }
                }
            }
            is org.hl7.cql.ast.IntervalLiteral -> {
                val intervalType = typeTable[expr]
                val pointType = if (intervalType is IntervalType) intervalType.pointType else null
                if (pointType != null) {
                    recordTargetTypeConversion(expr, Slot.IntervalLow, literal.lower, pointType)
                    recordTargetTypeConversion(expr, Slot.IntervalHigh, literal.upper, pointType)
                }
            }
            else -> {}
        }
    }

    // --- No-op handlers ---

    override fun onIdentifier(expr: IdentifierExpression) {}

    override fun onExternalConstant(expr: ExternalConstantExpression) {}

    override fun onBooleanTest(expr: BooleanTestExpression, operand: Unit) {}

    override fun onIs(expr: IsExpression, operand: Unit) {}

    override fun onAs(expr: AsExpression, operand: Unit) {}

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: Unit) {}

    override fun onCast(expr: CastExpression, operand: Unit) {}

    override fun onConversion(expr: ConversionExpression, operand: Unit) {}

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: Unit) {}

    override fun onIndex(expr: IndexExpression, target: Unit, index: Unit) {}

    override fun onDateTimeComponent(expr: DateTimeComponentExpression, operand: Unit) {}

    override fun onDurationBetween(expr: DurationBetweenExpression, lower: Unit, upper: Unit) {}

    override fun onDifferenceBetween(expr: DifferenceBetweenExpression, lower: Unit, upper: Unit) {}

    override fun onDurationOf(expr: DurationOfExpression, operand: Unit) {}

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: Unit) {}

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: Unit) {}

    override fun onElementExtractor(expr: ElementExtractorExpression, operand: Unit) {}

    override fun onTypeExtent(expr: TypeExtentExpression) {}

    override fun onBetween(expr: BetweenExpression, input: Unit, lower: Unit, upper: Unit) {}

    override fun onQuery(expr: QueryExpression, children: QueryChildren<Unit>) {}

    override fun onRetrieve(expr: RetrieveExpression) {}

    override fun onUnsupported(expr: UnsupportedExpression) {}

    override fun onIntervalExpression(
        expr: IntervalExpression,
        low: Unit,
        high: Unit,
        lowClosed: Unit,
        highClosed: Unit,
    ) {}
}
