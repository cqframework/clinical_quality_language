@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.model.CallContext
import org.cqframework.cql.cql2elm.model.Invocation
import org.cqframework.cql.cql2elm.model.Operator
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.model.invocation.AggregateExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.BinaryExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.NaryExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.TernaryExpressionInvocation
import org.cqframework.cql.cql2elm.model.invocation.UnaryExpressionInvocation
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.NaryExpression
import org.hl7.elm.r1.TernaryExpression
import org.hl7.elm.r1.UnaryExpression

/**
 * Owns the operator resolution pipeline: wraps an [Expression] in the appropriate [Invocation],
 * builds a [CallContext], walks the library chain (current → System → fluent-including libs, or a
 * specific named library), applies conversions to operands, and returns the resolved [Invocation]
 * or ELM [Expression].
 *
 * Extracted from [LibraryBuilder] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [LibraryBuilder] for access to its library registry, the conversion map,
 * compatibility-level options, type-specifier conversion, and parsing diagnostics. These
 * collaborators will shrink as the retirement proceeds.
 *
 * Set-membership operators (`In`, `IncludedIn`, `ProperIn`, etc.) and the higher-level
 * `resolveFunction`/`resolveUnion`/`resolveIntersect`/`resolveContains` family remain on
 * [LibraryBuilder] because they encode CQL-specific semantics (list-type normalization, implicit
 * coalesce, DateTimePrecision qualification) beyond raw resolution.
 */
@Suppress("TooManyFunctions", "LongParameterList", "MaxLineLength")
class SemanticAnalyzer(private val lb: LibraryBuilder) {
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
        val convertedOperands: MutableList<Expression> = ArrayList()
        val operandIterator = operands.iterator()
        val signatureTypes = resolution!!.operator.signature.operandTypes.iterator()
        val conversionIterator =
            if (resolution.hasConversions()) resolution.conversions.iterator() else null
        while (operandIterator.hasNext()) {
            var operand = operandIterator.next()
            val conversion = conversionIterator?.next()
            if (conversion != null) {
                operand = lb.convertExpression(operand, conversion)
            }
            signatureTypes.next()
            convertedOperands.add(operand)
        }
        invocation.operands = convertedOperands

        val signatureLevel = lb.libraryManager.cqlCompilerOptions.signatureLevel
        if (
            signatureLevel == LibraryBuilder.SignatureLevel.All ||
                (signatureLevel == LibraryBuilder.SignatureLevel.Differing &&
                    resolution.operator.signature != callContext.signature) ||
                signatureLevel == LibraryBuilder.SignatureLevel.Overloads &&
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
                result = lb.systemLibraryInternal.resolveCall(callContext, lb.conversionMap)
                if (result == null && callContext.allowFluent) {
                    // First non-system inclusion that matches wins.
                    for (libCompiled in lb.libraries.values) {
                        if (libCompiled != lb.systemLibraryInternal) {
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

    @Suppress("UnusedParameter", "unused")
    private fun pruneChoices(expression: Expression, targetType: DataType): Expression = expression
}
