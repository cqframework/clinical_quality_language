@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

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
 * Coercion insertion pass: populates the [ConversionTable] with implicit type coercions that the
 * emitter applies mechanically during code generation. Implements [ExpressionFold]<[Unit]> for
 * compile-time exhaustive dispatch.
 *
 * This follows the standard compiler pattern for **coercive subtyping** (Traytel & Nipkow 2011,
 * "Extending HM with Coercive Structural Subtyping"): type inference runs first (bottom-up via
 * [TypeResolver]), then a second pass traverses the typed AST and inserts coercions at positions
 * where the synthesized type doesn't match the expected type. Coercions are recorded as metadata
 * in the [ConversionTable] rather than mutating the AST.
 *
 * In bidirectional type-checking terms, [TypeResolver] is the **synthesis** (bottom-up) pass and
 * this class provides the **checking** (top-down) pass: it inspects each parent expression's
 * expected operand types and records coercions for children whose synthesized types don't conform.
 *
 * ## Kinds of coercions recorded
 * - **Branch unification** (If/Case/List/Interval) — when branch types differ, records
 *   [ImplicitConversion.ImplicitCast] or [ImplicitConversion.OperatorConversion] to coerce to the
 *   common type.
 * - **Null-literal wrapping** — null literals in typed slots get [ImplicitConversion.ImplicitCast]
 *   to the target type (e.g., `null` in an If-then slot → `As(null, Integer)`).
 * - **ChoiceType wrapping** — union/intersect/except with mismatched element types get
 *   [ImplicitConversion.ImplicitCast] to `List<Choice<A,B>>`, or [ImplicitConversion.ListDemotion]
 *   when one operand is `List<Any>` (empty list literal).
 * - **Interval bound propagation** — `Interval<Any>` literals (one bound is null) get their point
 *   type inferred from the typed counterpart.
 * - **DateTime null-arg conversions** — null arguments in `DateTime()`/`Date()`/`Time()`
 *   constructors get [ImplicitConversion.ImplicitCast] to Integer or Decimal.
 *
 * Operator-resolution coercion recording (the "transcription" concern) happens in
 * [recordResolutionConversions] at each setOperatorResolution call site in [TypeResolver].
 *
 * ## Adding a new kind of coercion
 * 1. Define a new [ImplicitConversion] subtype in `ImplicitConversion.kt` if the existing subtypes
 *    don't cover the case.
 * 2. In the relevant `on*` handler in this class, detect the condition and call
 *    [ConversionTable.addIfAbsent] to record the coercion at the (parent, [ConversionSlot])
 *    location.
 * 3. In `EmissionContext.applyConversions`, handle the new [ImplicitConversion] subtype to produce
 *    the correct ELM wrapper node.
 *
 * ## What does NOT go here
 * - Type inference → [TypeResolver]
 * - Structural AST rewrites → [Lowering]
 * - Error detection → [SemanticValidator]
 * - ELM-specific concerns → `EmissionContext`
 *
 * **Not thread-safe.** Create a fresh instance per analysis iteration.
 */
class CoercionInserter(
    private val typeTable: TypeTable,
    private val operatorRegistry: OperatorRegistry,
    private val conversionTable: ConversionTable,
) : ExpressionFold<Unit> {

    /** Number of new conversions inserted during this analysis pass. */
    var newConversionsInserted: Int = 0
        private set

    /** Analyze all statements and definitions in a library. */
    fun analyzeLibrary(library: Library) {
        val before = conversionTable.conversionsInserted
        for (statement in library.statements) {
            analyzeStatement(statement)
        }
        for (definition in library.definitions) {
            analyzeDefinition(definition)
        }
        newConversionsInserted = conversionTable.conversionsInserted - before
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

    /** Record a conversion if not already present at the given parent/slot. */
    private fun recordIfNew(
        parent: Expression,
        slot: ConversionSlot,
        conversion: ImplicitConversion,
    ) {
        conversionTable.addIfAbsent(parent, slot, conversion)
    }

    /** Check if an expression is a null literal. */
    private fun isNullLiteralExpr(expr: Expression): Boolean =
        expr is LiteralExpression && expr.literal is NullLiteral

    /**
     * Record a model coercion (e.g., FHIRHelpers.ToConcept) at the given [slot] if [TypeResolver]
     * found a model conversion during synthesis. The conversion object is stored in
     * [TypeTable.getModelConversion] by the synthesis pass; we read it here and convert to an
     * [ImplicitConversion.LibraryConversion] in the [ConversionTable].
     */
    private fun recordModelCoercion(expr: Expression, slot: ConversionSlot) {
        val mc = typeTable.getModelConversion(expr) ?: return
        val op = mc.operator ?: return
        val libraryName = op.libraryName ?: return
        if (libraryName == "System") return
        conversionTable.addIfAbsent(
            expr,
            slot,
            ImplicitConversion.LibraryConversion(libraryName, op.name, mc.toType),
        )
    }

    /** Find a model conversion for [type] from the ConversionMap (not from TypeTable). */
    private fun findModelConversionForType(type: DataType): org.cqframework.cql.cql2elm.model.Conversion? {
        return operatorRegistry.conversionMap.getConversions(type).firstOrNull {
            it.isImplicit &&
                it.operator != null &&
                it.operator.libraryName != null &&
                it.operator.libraryName != "System"
        }
    }

    /**
     * Record a conversion for type unification: if the child's type doesn't match [targetType],
     * record the appropriate conversion (NullAs, OperatorConversion, ChoiceAs, etc.).
     */
    private fun recordTargetTypeConversion(
        parent: Expression,
        slot: ConversionSlot,
        childExpr: Expression,
        targetType: DataType,
    ) {
        val anyType = operatorRegistry.type("Any")
        if (targetType == anyType) return

        // Null literal: record ImplicitCast
        if (isNullLiteralExpr(childExpr)) {
            recordIfNew(parent, slot, ImplicitConversion.ImplicitCast(targetType))
            return
        }

        // Type mismatch: determine conversion
        val fromType = typeTable[childExpr] ?: return
        if (fromType == targetType) return

        // ChoiceType: record ImplicitCast (only if the type can be converted to a TypeSpecifier;
        // TupleTypes can't, so ChoiceType containing tuples skips wrapping)
        if (targetType is ChoiceType && canEmitAsTypeSpecifier(targetType)) {
            recordIfNew(parent, slot, ImplicitConversion.ImplicitCast(targetType))
            return
        }

        // List<T> → List<Choice<...>> for union operands
        if (
            targetType is ListType &&
                targetType.elementType is ChoiceType &&
                fromType is ListType &&
                canEmitAsTypeSpecifier(targetType)
        ) {
            recordIfNew(parent, slot, ImplicitConversion.ImplicitCast(targetType))
            return
        }

        // Implicit operator conversion (Integer→Decimal, etc.)
        val convName = implicitConversionName(fromType.toString(), targetType.toString())
        if (convName != null) {
            recordIfNew(parent, slot, ImplicitConversion.OperatorConversion(convName))
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
     * complex types cannot, so conversions involving them are skipped (matching CI behavior).
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

    /** Record conversions for union/intersect/except when element types differ. */
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
                    recordIfNew(
                        expr,
                        ConversionSlot.Right,
                        ImplicitConversion.ListDemotion(leftElem, leftType),
                    )
                } else if (leftElem == anyType && rightElem != anyType) {
                    recordIfNew(
                        expr,
                        ConversionSlot.Left,
                        ImplicitConversion.ListDemotion(rightElem, rightType),
                    )
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
                    recordIfNew(
                        expr,
                        ConversionSlot.Left,
                        ImplicitConversion.ImplicitCast(choiceListType),
                    )
                    recordIfNew(
                        expr,
                        ConversionSlot.Right,
                        ImplicitConversion.ImplicitCast(choiceListType),
                    )
                }
            }
        }
    }

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: Unit?,
        arguments: List<Unit>,
    ) {

        // DateTime/Date/Time null arg wrapping
        val functionName = expr.function.value
        recordDateTimeNullArgConversions(functionName, expr)

        // Null arg collection wrapping (e.g., IndexOf(null, {}) → As(null, List<Any>))
        // cannot be an implicit conversion — it changes effective types from Any to List<Any>,
        // which
        // breaks generic resolution on re-typing (T=List<Any> instead of T=Any).
        // This wrapping must happen at emission time. See CqlListOperators KNOWN_SKIP.
    }

    /** Record NullAs conversions for DateTime/Date/Time null arguments. */
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
                            recordIfNew(
                                expr,
                                ConversionSlot.Argument(i),
                                ImplicitConversion.ImplicitCast(targetType),
                            )
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
                            recordIfNew(
                                expr,
                                ConversionSlot.Argument(i),
                                ImplicitConversion.ImplicitCast(integerType),
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onMembership(expr: MembershipExpression, left: Unit, right: Unit) {
        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        val anyType = operatorRegistry.type("Any")

        when (expr.operator) {
            org.hl7.cql.ast.MembershipOperator.CONTAINS -> {
                if (isNullLiteralExpr(expr.right)) {
                    val elemType = elementTypeOfDataType(leftType)
                    if (elemType != null && elemType != anyType) {
                        recordIfNew(
                            expr,
                            ConversionSlot.Right,
                            ImplicitConversion.ImplicitCast(elemType),
                        )
                    }
                }
                if (isNullLiteralExpr(expr.left) && rightType != null && rightType != anyType) {
                    recordIfNew(
                        expr,
                        ConversionSlot.Left,
                        ImplicitConversion.ImplicitCast(ListType(rightType)),
                    )
                }
            }
            org.hl7.cql.ast.MembershipOperator.IN -> {
                if (isNullLiteralExpr(expr.left) && rightType is IntervalType) {
                    val pointType = rightType.pointType
                    if (pointType != anyType) {
                        recordIfNew(
                            expr,
                            ConversionSlot.Left,
                            ImplicitConversion.ImplicitCast(pointType),
                        )
                    }
                }
                if (isNullLiteralExpr(expr.right) && leftType != null && leftType != anyType) {
                    recordIfNew(
                        expr,
                        ConversionSlot.Right,
                        ImplicitConversion.ImplicitCast(IntervalType(leftType)),
                    )
                }
            }
        }
        // AnyInValueSet / AnyInCodeSystem: when the codes operand is a List<ModelType>
        // (e.g., List<FHIR.CodeableConcept>), the legacy wraps each element in a model
        // conversion (FHIRHelpers.ToConcept) via a Query. Record a ListConversion so the
        // emitter produces the same wrapping.
        val kind = typeTable.getMembershipKind(expr)
        val isContains = expr.operator == org.hl7.cql.ast.MembershipOperator.CONTAINS
        val codesExpr = if (isContains) expr.right else expr.left
        val codesSlot = if (isContains) ConversionSlot.Right else ConversionSlot.Left
        if (kind == MembershipKind.ANY_IN_VALUE_SET || kind == MembershipKind.ANY_IN_CODE_SYSTEM) {
            val codesType = typeTable[codesExpr]
            if (codesType is ListType) {
                val mc = typeTable.getModelConversion(codesExpr)
                    ?: codesExpr?.let {
                        // The codes expression might be a property whose model conversion
                        // was recorded. Check element type for a model conversion directly.
                        findModelConversionForType(codesType.elementType)
                    }
                if (mc != null) {
                    val op = mc.operator
                    if (op != null && op.libraryName != null && op.libraryName != "System") {
                        recordIfNew(
                            expr,
                            codesSlot,
                            ImplicitConversion.ListConversion(op.name, op.libraryName, mc.toType),
                        )
                    }
                }
            }
        }
    }

    override fun onIntervalRelation(expr: IntervalRelationExpression, left: Unit, right: Unit) {
        val leftType = typeTable[expr.left]
        val rightType = typeTable[expr.right]
        val anyType = operatorRegistry.type("Any")

        // Null literal operands
        if (
            isNullLiteralExpr(expr.right) &&
                leftType is IntervalType &&
                leftType.pointType != anyType
        ) {
            recordIfNew(
                expr,
                ConversionSlot.Right,
                ImplicitConversion.ImplicitCast(leftType.pointType),
            )
        } else if (
            isNullLiteralExpr(expr.left) &&
                rightType is IntervalType &&
                rightType.pointType != anyType
        ) {
            recordIfNew(
                expr,
                ConversionSlot.Left,
                ImplicitConversion.ImplicitCast(rightType.pointType),
            )
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
     * type, record bound-level conversions on the interval literal so null bounds get wrapped.
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
        // Target must be an interval literal to record bound-level conversions
        if (target is LiteralExpression && target.literal is org.hl7.cql.ast.IntervalLiteral) {
            val interval = target.literal as org.hl7.cql.ast.IntervalLiteral
            recordTargetTypeConversion(
                target,
                ConversionSlot.IntervalLow,
                interval.lower,
                pointType,
            )
            recordTargetTypeConversion(
                target,
                ConversionSlot.IntervalHigh,
                interval.upper,
                pointType,
            )
        }
    }

    override fun onExists(expr: ExistsExpression, operand: Unit) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            recordIfNew(
                expr,
                ConversionSlot.Operand,
                ImplicitConversion.ImplicitCast(ListType(anyType)),
            )
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
            recordIfNew(expr, ConversionSlot.Operand, ImplicitConversion.ImplicitCast(targetType))
        }
        // Heterogeneous flatten detection is structural (lowering), not a type conversion.
    }

    override fun onExpandCollapse(expr: ExpandCollapseExpression, operand: Unit, per: Unit?) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            recordIfNew(
                expr,
                ConversionSlot.Operand,
                ImplicitConversion.ImplicitCast(ListType(IntervalType(anyType))),
            )
            return
        }
        // List<Any> operand (e.g. collapse {null}): demote list elements to Interval<Any>
        // to match legacy behavior which wraps in Query { X = list } return As(X, Interval<Any>).
        val operandType = typeTable[expr.operand] ?: return
        val anyType = operatorRegistry.type("Any") ?: return
        if (operandType is ListType && operandType.elementType == anyType) {
            val intervalAny = IntervalType(anyType)
            recordIfNew(expr, ConversionSlot.Operand, ImplicitConversion.ListDemotion(intervalAny, ListType(intervalAny)))
        }
    }

    override fun onWidth(expr: WidthExpression, operand: Unit) {
        if (isNullLiteralExpr(expr.operand)) {
            val anyType = operatorRegistry.type("Any")
            recordIfNew(
                expr,
                ConversionSlot.Operand,
                ImplicitConversion.ImplicitCast(IntervalType(anyType)),
            )
        }
    }

    override fun onIf(expr: IfExpression, condition: Unit, thenBranch: Unit, elseBranch: Unit) {
        val resultType = typeTable[expr] ?: return
        recordTargetTypeConversion(expr, ConversionSlot.ThenBranch, expr.thenBranch, resultType)
        recordTargetTypeConversion(expr, ConversionSlot.ElseBranch, expr.elseBranch, resultType)
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
                    ConversionSlot.CaseCondition(i),
                    originalItem.condition,
                    comparandType,
                )
            }
            if (resultType != null) {
                recordTargetTypeConversion(
                    expr,
                    ConversionSlot.CaseBranch(i),
                    originalItem.result,
                    resultType,
                )
            }
        }

        // Else branch
        if (resultType != null) {
            recordTargetTypeConversion(expr, ConversionSlot.ElseBranch, expr.elseResult, resultType)
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
                        recordTargetTypeConversion(
                            expr,
                            ConversionSlot.ListElement(i),
                            elem,
                            elementType,
                        )
                    }
                }
            }
            is org.hl7.cql.ast.IntervalLiteral -> {
                val intervalType = typeTable[expr]
                val pointType = if (intervalType is IntervalType) intervalType.pointType else null
                if (pointType != null) {
                    recordTargetTypeConversion(
                        expr,
                        ConversionSlot.IntervalLow,
                        literal.lower,
                        pointType,
                    )
                    recordTargetTypeConversion(
                        expr,
                        ConversionSlot.IntervalHigh,
                        literal.upper,
                        pointType,
                    )
                }
            }
            is org.hl7.cql.ast.InstanceLiteral -> {
                // Instance element type unification: when the instance type is a ClassType,
                // check each element's value against the expected property type.
                val instanceType = typeTable[expr]
                if (instanceType is org.hl7.cql.model.ClassType) {
                    literal.elements.forEachIndexed { i, elem ->
                        val expectedType =
                            instanceType.elements.firstOrNull { it.name == elem.name.value }?.type
                        if (expectedType != null) {
                            recordTargetTypeConversion(
                                expr,
                                ConversionSlot.ListElement(i),
                                elem.expression,
                                expectedType,
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }

    // --- No-op handlers ---

    override fun onIdentifier(expr: IdentifierExpression) {}

    override fun onExternalConstant(expr: ExternalConstantExpression) {}

    override fun onBooleanTest(expr: BooleanTestExpression, operand: Unit) {}

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Unit) {}

    override fun onIs(expr: IsExpression, operand: Unit) {}

    override fun onAs(expr: AsExpression, operand: Unit) {
        recordModelCoercion(expr, ConversionSlot.Operand)
    }

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: Unit) {}

    override fun onCast(expr: CastExpression, operand: Unit) {}

    override fun onConversion(expr: ConversionExpression, operand: Unit) {}

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: Unit) {
        // If this property accessor produces the same system type as the target's model
        // conversion, the accessor IS the type transition — the model coercion is redundant.
        // e.g., FHIR.string has element value:System.String, and FHIRHelpers.ToString also
        // converts FHIR.string → System.String. Applying both would make the accessor
        // unresolvable (System.String has no .value property).
        val targetExpr = expr.target
        if (targetExpr != null) {
            val mc = typeTable.getModelConversion(targetExpr)
            if (mc != null) {
                val accessedType = (mc.fromType as? org.hl7.cql.model.ClassType)
                    ?.elements?.firstOrNull { it.name == expr.property.value }?.type
                if (accessedType != null && accessedType == mc.toType) {
                    conversionTable.remove(targetExpr, ConversionSlot.PropertyResult)
                    return
                }
            }
        }
        recordModelCoercion(expr, ConversionSlot.PropertyResult)
    }

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
