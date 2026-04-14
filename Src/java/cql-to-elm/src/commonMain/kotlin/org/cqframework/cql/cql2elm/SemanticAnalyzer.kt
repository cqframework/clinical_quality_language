@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.model.CallContext
import org.cqframework.cql.cql2elm.model.ConversionMap
import org.cqframework.cql.cql2elm.model.Invocation
import org.cqframework.cql.cql2elm.model.Operator
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.model.invocation.AggregateExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.AnyInCodeSystemInvocation
import org.cqframework.cql.cql2elm.model.invocation.AnyInValueSetInvocation
import org.cqframework.cql.cql2elm.model.invocation.BinaryExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.FunctionRefInvocation
import org.cqframework.cql.cql2elm.model.invocation.InCodeSystemInvocation
import org.cqframework.cql.cql2elm.model.invocation.InValueSetInvocation
import org.cqframework.cql.cql2elm.model.invocation.NaryExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.TernaryExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.UnaryExpressionInvocation
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.DateTimePrecision
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Intersect
import org.hl7.elm.r1.NaryExpression
import org.hl7.elm.r1.TernaryExpression
import org.hl7.elm.r1.UnaryExpression
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.ValueSetRef

/**
 * Owns the operator resolution pipeline: wraps an [Expression] in the appropriate [Invocation],
 * builds a [CallContext], walks the library chain (current → System → fluent-including libs, or a
 * specific named library), applies conversions to operands, and returns the resolved [Invocation]
 * or ELM [Expression].
 *
 * Extracted from [Cql2ElmContext] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [Cql2ElmContext] for access to its library registry, the conversion map,
 * compatibility-level options, type-specifier conversion, and parsing diagnostics. These
 * collaborators will shrink as the retirement proceeds.
 *
 * Set-membership operators (`In`, `IncludedIn`, `ProperIn`, etc.) and the higher-level
 * `resolveFunction`/`resolveUnion`/`resolveIntersect`/`resolveContains` family remain on
 * [Cql2ElmContext] because they encode CQL-specific semantics (list-type normalization, implicit
 * coalesce, DateTimePrecision qualification) beyond raw resolution.
 */
@Suppress(
    "TooManyFunctions",
    "LongParameterList",
    "MaxLineLength",
    "LargeClass",
    "ReturnCount",
    "ForbiddenComment",
)
class SemanticAnalyzer(private val lb: Cql2ElmContext, private val of: IdObjectFactory) {
    /**
     * Resolve an operator invocation, dispatching on the runtime type of [expression] to wrap it in
     * the appropriate [Invocation].
     */
    @JsExport.Ignore
    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        expression: Expression,
    ): Expression? {
        val invocation = expressionToInvocation(expression)
        return resolveCall(
            libraryName,
            operatorName,
            invocation,
            mustResolve = true,
            allowPromotionAndDemotion = false,
            allowFluent = false,
        )
    }

    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
    ): Expression? =
        resolveCall(
            libraryName,
            operatorName,
            invocation,
            mustResolve = true,
            allowPromotionAndDemotion = false,
            allowFluent = false,
        )

    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Expression? =
        resolveCall(
            libraryName,
            operatorName,
            invocation,
            true,
            allowPromotionAndDemotion,
            allowFluent,
        )

    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Expression? =
        resolveInvocation(
                libraryName,
                operatorName,
                invocation,
                mustResolve,
                allowPromotionAndDemotion,
                allowFluent,
            )
            ?.expression

    @JsExport.Ignore
    @JvmOverloads
    fun resolveBinaryCall(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false,
    ): Expression? =
        resolveBinaryInvocation(
                libraryName,
                operatorName,
                expression,
                mustResolve,
                allowPromotionAndDemotion,
            )
            ?.expression

    @JvmOverloads
    fun resolveBinaryInvocation(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false,
    ): Invocation? =
        resolveInvocation(
            libraryName,
            operatorName,
            BinaryExpressionInvocation(expression),
            mustResolve,
            allowPromotionAndDemotion,
            false,
        )

    @JsExport.Ignore
    @Suppress("LongMethod", "CyclomaticComplexMethod", "ComplexCondition", "NestedBlockDepth")
    @JvmOverloads
    fun resolveInvocation(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false,
        allowFluent: Boolean = false,
    ): Invocation? {
        val operands: Iterable<Expression> = invocation.operands
        val callContext =
            buildCallContext(
                libraryName,
                operatorName,
                operands,
                mustResolve,
                allowPromotionAndDemotion,
                allowFluent,
            )
        val resolution = resolveCall(callContext)
        if (resolution == null && !mustResolve) return null
        checkOperator(callContext, resolution)
        resolution!!
        val convertedOperands: MutableList<Expression> = ArrayList()
        val operandIterator = operands.iterator()
        val conversionIterator =
            if (resolution.hasConversions()) resolution.conversions.iterator() else null
        while (operandIterator.hasNext()) {
            var operand = operandIterator.next()
            val conversion = conversionIterator?.next()
            if (conversion != null) {
                operand = lb.convertExpression(operand, conversion)
            }
            convertedOperands.add(operand)
        }
        invocation.operands = convertedOperands

        val signatureLevel = lb.libraryManager.cqlCompilerOptions.signatureLevel
        if (
            signatureLevel == Cql2ElmContext.SignatureLevel.All ||
                (signatureLevel == Cql2ElmContext.SignatureLevel.Differing &&
                    resolution.operator.signature != callContext.signature) ||
                signatureLevel == Cql2ElmContext.SignatureLevel.Overloads &&
                    resolution.operatorHasOverloads
        ) {
            invocation.signature =
                lb.dataTypesToTypeSpecifiers(resolution.operator.signature.operandTypes)
        } else if (resolution.operatorHasOverloads && resolution.operator.libraryName != "System") {
            // System-library operators have one runtime type representation per CQL type and
            // can't suffer ambiguous overload resolution, so the warning only applies elsewhere.
            lb.reportWarning(
                """
                    The function ${resolution.operator.libraryName}.${resolution.operator.name} has multiple overloads
                    and due to the SignatureLevel setting (${signatureLevel.name}),
                    the overload signature is not being included in the output.
                    This may result in ambiguous function resolution
                    at runtime, consider setting the SignatureLevel to Overloads or All
                    to ensure that the output includes sufficient
                    information to support correct overload selection at runtime.
                """
                    .trimIndent()
                    .replace("\n", " "),
                invocation.expression,
            )
        }
        invocation.resultType = resolution.operator.resultType
        if (resolution.libraryIdentifier != null) {
            resolution.libraryName =
                lb.compiledLibrary.resolveIncludeAlias(resolution.libraryIdentifier!!)
        }
        invocation.resolution = resolution
        return invocation
    }

    fun resolveFunctionDefinition(fd: FunctionDef): Operator? {
        val libraryName = lb.compiledLibrary.identifier!!.id
        val operatorName = fd.name
        val dataTypes: MutableList<DataType> = ArrayList()
        for (operand in fd.operand) {
            requireNotNull(operand.resultType) {
                "Could not determine signature for invocation of operator ${if (libraryName == null) "" else "$libraryName."}$operatorName."
            }
            dataTypes.add(operand.resultType!!)
        }
        val callContext =
            CallContext(
                lb.compiledLibrary.identifier!!.id,
                fd.name!!,
                false,
                fd.fluent != null && fd.fluent!!,
                false,
                dataTypes,
            )
        // Resolve exact, no conversion map.
        return lb.compiledLibrary.resolveCall(callContext, lb.conversionMap)?.operator
    }

    private fun expressionToInvocation(expression: Expression): Invocation =
        when (expression) {
            is UnaryExpression -> UnaryExpressionInvocation(expression)
            is BinaryExpression -> BinaryExpressionInvocation(expression)
            is TernaryExpression -> TernaryExpressionInvocation(expression)
            is NaryExpression -> NaryExpressionInvocation(expression)
            is AggregateExpression -> AggregateExpressionInvocation(expression)
            else ->
                throw IllegalArgumentException(
                    "Cannot resolve operator call for expression of type ${expression::class.simpleName}"
                )
        }

    private fun buildCallContext(
        libraryName: String?,
        operatorName: String,
        operands: Iterable<Expression?>,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): CallContext {
        val dataTypes: MutableList<DataType> = ArrayList()
        for (operand in operands) {
            require(operand != null && operand.resultType != null) {
                "Could not determine signature for invocation of operator ${if (libraryName == null) "" else "$libraryName."}$operatorName."
            }
            dataTypes.add(operand.resultType!!)
        }
        return CallContext(
            libraryName,
            operatorName,
            allowPromotionAndDemotion,
            allowFluent,
            mustResolve,
            dataTypes,
        )
    }

    @Suppress("NestedBlockDepth")
    private fun resolveCall(callContext: CallContext): OperatorResolution? {
        var result: OperatorResolution?
        if (callContext.libraryName.isNullOrEmpty()) {
            result = lb.compiledLibrary.resolveCall(callContext, lb.conversionMap)
            if (result == null) {
                result = lb.systemLibrary.resolveCall(callContext, lb.conversionMap)
                if (result == null && callContext.allowFluent) {
                    // First non-system inclusion that matches wins.
                    for (libCompiled in lb.libraries.values) {
                        if (libCompiled != lb.systemLibrary) {
                            result = libCompiled.resolveCall(callContext, lb.conversionMap)
                            if (result != null) break
                        }
                    }
                }
            }
        } else {
            result =
                lb.resolveLibrary(callContext.libraryName)
                    .resolveCall(callContext, lb.conversionMap)
        }
        if (result != null) {
            lb.checkAccessLevel(
                result.operator.libraryName,
                result.operator.name,
                result.operator.accessLevel,
            )
        }
        return result
    }

    private fun checkOperator(callContext: CallContext, resolution: OperatorResolution?) {
        requireNotNull(resolution) {
            "Could not resolve call to operator ${callContext.operatorName} with signature ${callContext.signature}."
        }
        require(!resolution.operator.fluent || callContext.allowFluent) {
            "Operator ${callContext.operatorName} with signature ${callContext.signature} is a fluent function and can only be invoked with fluent syntax."
        }
        require(!callContext.allowFluent || resolution.operator.fluent || resolution.allowFluent) {
            "Invocation of operator ${callContext.operatorName} with signature ${callContext.signature} uses fluent syntax, but the operator is not defined as a fluent function."
        }
    }

    // ========================================================================
    // Set-membership operators. Each of In / IncludedIn / Contains / ProperIn
    // / ProperContains / ProperIncludedIn / ProperIncludes has CQL-specific
    // resolution semantics: DateTimePrecision qualification, terminology-
    // aware variants (InValueSet / InCodeSystem / Any-variants), and
    // bidirectional tie-breaking between forms (In vs IncludedIn, Contains
    // vs Includes, ProperIn vs ProperIncludedIn, ProperContains vs
    // ProperIncludes). These cannot collapse into the generic resolveCall.
    // ========================================================================

    private class BinaryWrapper(var left: Expression, var right: Expression)

    /**
     * Normalize the result-list types of two expressions so that set operators (Union, Intersect,
     * Except) can combine them: if the two operand list types aren't mutually assignable, cast each
     * to the List<Choice<leftElements ∪ rightElements>> that covers both.
     */
    @Suppress("NestedBlockDepth")
    private fun normalizeListTypes(left: Expression, right: Expression): BinaryWrapper {
        var l = left
        var r = right
        if (l.resultType is ListType && r.resultType is ListType) {
            val leftListType = l.resultType as ListType
            val rightListType = r.resultType as ListType
            @Suppress("ComplexCondition")
            if (
                !(leftListType.isSuperTypeOf(rightListType) ||
                    rightListType.isSuperTypeOf(leftListType)) &&
                    !(leftListType.isCompatibleWith(rightListType) ||
                        rightListType.isCompatibleWith(leftListType))
            ) {
                val elementTypes: MutableSet<DataType> = HashSet()
                if (leftListType.elementType is ChoiceType) {
                    for (choice in (leftListType.elementType as ChoiceType).types) {
                        elementTypes.add(choice)
                    }
                } else {
                    elementTypes.add(leftListType.elementType)
                }
                if (rightListType.elementType is ChoiceType) {
                    for (choice in (rightListType.elementType as ChoiceType).types) {
                        elementTypes.add(choice)
                    }
                } else {
                    elementTypes.add(rightListType.elementType)
                }
                if (elementTypes.size > 1) {
                    val targetType = ListType(ChoiceType(elementTypes))
                    l =
                        of.createAs()
                            .withOperand(l)
                            .withAsTypeSpecifier(lb.dataTypeToTypeSpecifier(targetType))
                    l.resultType = targetType
                    r =
                        of.createAs()
                            .withOperand(r)
                            .withAsTypeSpecifier(lb.dataTypeToTypeSpecifier(targetType))
                    r.resultType = targetType
                }
            }
        }
        return BinaryWrapper(l, r)
    }

    fun resolveUnion(left: Expression, right: Expression): Expression {
        // Right-leaning bushy rather than left-deep, so repeated unions don't degenerate.
        var l = left
        var r = right
        if (l is Union) {
            val leftUnionLeft = l.operand[0]
            val leftUnionRight = l.operand[1]
            if (leftUnionLeft is Union && leftUnionRight !is Union) {
                l = leftUnionLeft
                r = resolveUnion(leftUnionRight, r)
            }
        }
        val wrapper = normalizeListTypes(l, r)
        val union = of.createUnion().withOperand(listOf(wrapper.left, wrapper.right))
        resolveCall("System", "Union", union)
        return union
    }

    fun resolveIntersect(left: Expression, right: Expression): Expression {
        var l = left
        var r = right
        if (l is Intersect) {
            val leftIntersectLeft = l.operand[0]
            val leftIntersectRight = l.operand[1]
            if (leftIntersectLeft is Intersect && leftIntersectRight !is Intersect) {
                l = leftIntersectLeft
                r = resolveIntersect(leftIntersectRight, r)
            }
        }
        val wrapper = normalizeListTypes(l, r)
        val intersect = of.createIntersect().withOperand(listOf(wrapper.left, wrapper.right))
        resolveCall("System", "Intersect", intersect)
        return intersect
    }

    fun resolveExcept(left: Expression, right: Expression): Expression {
        val wrapper = normalizeListTypes(left, right)
        val except = of.createExcept().withOperand(listOf(wrapper.left, wrapper.right))
        resolveCall("System", "Except", except)
        return except
    }

    @JsExport.Ignore
    @Suppress("CyclomaticComplexMethod", "ComplexCondition")
    fun resolveIn(left: Expression, right: Expression): Expression {
        if (
            right is ValueSetRef ||
                (lb.isCompatibleWith("1.5") &&
                    right.resultType!!.isCompatibleWith(
                        lb.resolveTypeName("System", "ValueSet")!!
                    ) &&
                    right.resultType != lb.resolveTypeName("System", "Any"))
        ) {
            if (left.resultType is ListType) {
                val anyIn =
                    of.createAnyInValueSet()
                        .withCodes(left)
                        .withValueset(right as? ValueSetRef)
                        .withValuesetExpression(if (right is ValueSetRef) null else right)
                resolveCall("System", "AnyInValueSet", AnyInValueSetInvocation(anyIn))
                return anyIn
            }
            val inValueSet =
                of.createInValueSet()
                    .withCode(left)
                    .withValueset(right as? ValueSetRef)
                    .withValuesetExpression(if (right is ValueSetRef) null else right)
            resolveCall("System", "InValueSet", InValueSetInvocation(inValueSet))
            return inValueSet
        }
        if (
            right is CodeSystemRef ||
                (lb.isCompatibleWith("1.5") &&
                    right.resultType!!.isCompatibleWith(
                        lb.resolveTypeName("System", "CodeSystem")!!
                    ) &&
                    right.resultType != lb.resolveTypeName("System", "Any"))
        ) {
            if (left.resultType is ListType) {
                val anyIn =
                    of.createAnyInCodeSystem()
                        .withCodes(left)
                        .withCodesystem(right as? CodeSystemRef)
                        .withCodesystemExpression(if (right is CodeSystemRef) null else right)
                resolveCall("System", "AnyInCodeSystem", AnyInCodeSystemInvocation(anyIn))
                return anyIn
            }
            val inCodeSystem =
                of.createInCodeSystem()
                    .withCode(left)
                    .withCodesystem(right as? CodeSystemRef)
                    .withCodesystemExpression(if (right is CodeSystemRef) null else right)
            resolveCall("System", "InCodeSystem", InCodeSystemInvocation(inCodeSystem))
            return inCodeSystem
        }
        val inExpression = of.createIn().withOperand(listOf(left, right))
        resolveCall("System", "In", inExpression)
        return inExpression
    }

    @JsExport.Ignore
    fun resolveContains(left: Expression, right: Expression): Expression {
        // TODO: Add terminology overloads
        val contains = of.createContains().withOperand(listOf(left, right))
        resolveCall("System", "Contains", contains)
        return contains
    }

    @JsExport.Ignore
    fun resolveIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = resolveInInvocation(left, right, dateTimePrecision)?.expression

    private fun resolveInInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Invocation? {
        val inExpression =
            of.createIn().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "In", inExpression)
    }

    fun resolveProperIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = resolveProperInInvocation(left, right, dateTimePrecision)?.expression

    private fun resolveProperInInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Invocation? {
        val properIn =
            of.createProperIn().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "ProperIn", properIn)
    }

    @JsExport.Ignore
    fun resolveContains(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = resolveContainsInvocation(left, right, dateTimePrecision)?.expression

    private fun resolveContainsInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Invocation? {
        val contains =
            of.createContains().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "Contains", contains)
    }

    fun resolveProperContains(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = resolveProperContainsInvocation(left, right, dateTimePrecision)?.expression

    private fun resolveProperContainsInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Invocation? {
        val properContains =
            of.createProperContains()
                .withOperand(listOf(left, right))
                .withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "ProperContains", properContains)
    }

    /**
     * Select the lower-cost invocation between [primary] and [secondary], falling back to operand
     * type-precedence scoring when conversion scores tie. Used by the include / includedIn /
     * contains / properIncludedIn family to pick between forward and inverse forms.
     */
    @Suppress("NestedBlockDepth", "ReturnCount")
    private fun lowestScoringInvocation(primary: Invocation?, secondary: Invocation?): Expression? {
        if (primary != null) {
            if (secondary != null) {
                if (secondary.resolution!!.score < primary.resolution!!.score)
                    return secondary.expression
                if (primary.resolution!!.score < secondary.resolution!!.score)
                    return primary.expression
                if (primary.resolution!!.score == secondary.resolution!!.score) {
                    val primaryTypeScore = getTypeScore(primary.resolution)
                    val secondaryTypeScore = getTypeScore(secondary.resolution)
                    return if (secondaryTypeScore < primaryTypeScore) secondary.expression
                    else if (primaryTypeScore < secondaryTypeScore) primary.expression
                    else {
                        val message =
                            StringBuilder("Call to operator ")
                                .append(primary.resolution!!.operator.name)
                                .append("/")
                                .append(secondary.resolution!!.operator.name)
                                .append(" is ambiguous with: ")
                                .append("\n  - ")
                                .append(primary.resolution!!.operator.name)
                                .append(primary.resolution!!.operator.signature)
                                .append("\n  - ")
                                .append(secondary.resolution!!.operator.name)
                                .append(secondary.resolution!!.operator.signature)
                        throw IllegalArgumentException(message.toString())
                    }
                }
            }
            return primary.expression
        }
        return secondary?.expression
    }

    private fun getTypeScore(resolution: OperatorResolution?): Int {
        var typeScore = ConversionMap.ConversionScore.ExactMatch.score
        for (operand in resolution!!.operator.signature.operandTypes) {
            typeScore += ConversionMap.getTypePrecedenceScore(operand)
        }
        return typeScore
    }

    fun resolveIncludes(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? {
        val includes =
            of.createIncludes().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        val includesInvocation =
            resolveBinaryInvocation(
                "System",
                "Includes",
                includes,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        val contains =
            of.createContains().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        val containsInvocation =
            resolveBinaryInvocation(
                "System",
                "Contains",
                contains,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        return lowestScoringInvocation(includesInvocation, containsInvocation)
            ?: resolveCall("System", "Includes", includes)
    }

    fun resolveProperIncludes(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? {
        val properIncludes =
            of.createProperIncludes()
                .withOperand(listOf(left, right))
                .withPrecision(dateTimePrecision)
        val properIncludesInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperIncludes",
                properIncludes,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        val properContains =
            of.createProperContains()
                .withOperand(listOf(left, right))
                .withPrecision(dateTimePrecision)
        val properContainsInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperContains",
                properContains,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        return lowestScoringInvocation(properIncludesInvocation, properContainsInvocation)
            ?: resolveCall("System", "ProperIncludes", properIncludes)
    }

    fun resolveIncludedIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? {
        val includedIn =
            of.createIncludedIn().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        val includedInInvocation =
            resolveBinaryInvocation(
                "System",
                "IncludedIn",
                includedIn,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        val inExpression =
            of.createIn().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        val inInvocation =
            resolveBinaryInvocation(
                "System",
                "In",
                inExpression,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        return lowestScoringInvocation(includedInInvocation, inInvocation)
            ?: resolveCall("System", "IncludedIn", includedIn)
    }

    fun resolveProperIncludedIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? {
        val properIncludedIn =
            of.createProperIncludedIn()
                .withOperand(listOf(left, right))
                .withPrecision(dateTimePrecision)
        val properIncludedInInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperIncludedIn",
                properIncludedIn,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        val properIn =
            of.createProperIn().withOperand(listOf(left, right)).withPrecision(dateTimePrecision)
        val properInInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperIn",
                properIn,
                mustResolve = false,
                allowPromotionAndDemotion = false,
            )
        return lowestScoringInvocation(properIncludedInInvocation, properInInvocation)
            ?: resolveCall("System", "ProperIncludedIn", properIncludedIn)
    }

    // ========================================================================
    // User- / System-function invocation. Wraps the generic resolveCall with
    // systemFunctionResolver fallback, forward-reference handling, and
    // literal-context checks.
    // ========================================================================

    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: List<Expression>,
    ): Expression? =
        resolveFunction(
                libraryName,
                functionName,
                paramList,
                mustResolve = true,
                allowPromotionAndDemotion = false,
                allowFluent = false,
            )
            ?.expression

    private fun buildFunctionRef(
        libraryName: String?,
        functionName: String,
        paramList: Iterable<Expression>,
    ): FunctionRef {
        val functionRef = of.createFunctionRef().withLibraryName(libraryName).withName(functionName)
        for (param in paramList) functionRef.operand.add(param)
        return functionRef
    }

    @JsExport.Ignore
    @Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth")
    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: List<Expression>,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Invocation? {
        var functionRef: FunctionRef? = buildFunctionRef(libraryName, functionName, paramList)
        var invocation: Invocation = FunctionRefInvocation(functionRef!!)
        functionRef =
            resolveCall(
                functionRef.libraryName,
                functionRef.name!!,
                invocation,
                false,
                allowPromotionAndDemotion,
                allowFluent,
            )
                as FunctionRef?
        if (functionRef != null) {
            if ("System" == invocation.resolution!!.operator.libraryName) {
                // Rebuild from the original arguments — otherwise resolution applied conversions.
                val systemFun = buildFunctionRef(libraryName, functionName, paramList)
                val systemFunctionInvocation =
                    lb.systemFunctionResolver.resolveSystemFunction(systemFun)
                if (systemFunctionInvocation != null) return systemFunctionInvocation
            } else {
                if (mustResolve) lb.checkLiteralContext()
            }
        }

        // Still unresolved: it's either a systemFunctionResolver-only special case, or an error.
        if (functionRef == null) {
            functionRef = buildFunctionRef(libraryName, functionName, paramList)
            invocation = FunctionRefInvocation(functionRef)
            if (!allowFluent) {
                // For non-fluent calls, try the system-function resolver's specials first.
                val systemFunction = lb.systemFunctionResolver.resolveSystemFunction(functionRef)
                if (systemFunction != null) return systemFunction
                lb.checkLiteralContext()
            }
            functionRef =
                resolveCall(
                    functionRef.libraryName,
                    functionRef.name!!,
                    invocation,
                    mustResolve,
                    allowPromotionAndDemotion,
                    allowFluent,
                )
                    as FunctionRef?
            if (functionRef == null) return null
        }
        return invocation
    }

    // ========================================================================
    // Access-level check: enforces that private operators aren't referenced
    // from another library.
    // ========================================================================

    fun checkAccessLevel(
        libraryName: String?,
        objectName: String?,
        accessModifier: AccessModifier,
    ) {
        if (
            accessModifier == AccessModifier.PRIVATE &&
                isInterFunctionAccess(lb.library.identifier!!.id!!, libraryName)
        ) {
            throw CqlSemanticException(
                "Identifier $objectName in library $libraryName is marked private and cannot be referenced from another library."
            )
        }
    }

    private fun isInterFunctionAccess(f1: String, f2: String?): Boolean =
        if (f1.isNotBlank() && !f2.isNullOrBlank()) !f1.equals(f2, ignoreCase = true) else false
}
