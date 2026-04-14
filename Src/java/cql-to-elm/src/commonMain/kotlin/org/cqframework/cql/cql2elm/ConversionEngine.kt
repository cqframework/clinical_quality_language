@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.invocation.FunctionRefInvocation
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.Null

/**
 * Owns the conversion-map lookup and ELM-rewriting logic that realizes implicit and explicit
 * conversions. Handles simple casts (choice↔choice, `As` wrapping), list promotion/demotion,
 * interval promotion/demotion, OperatorConversion function calls, and the per-literal
 * constant-folding optimization for interval conversions.
 *
 * Extracted from [LibraryBuilder] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [LibraryBuilder] for the conversion map + operator map, type-name resolution,
 * expression-factory helpers (`buildAs`, `buildIs`, `buildNull`, `buildIsNull`), operator
 * resolution (`resolveCall`), the SystemFunctionResolver, and parsing-warning reporting.
 */
@Suppress("TooManyFunctions")
class ConversionEngine(private val lb: LibraryBuilder, private val of: IdObjectFactory) {
    fun findConversion(
        fromType: DataType,
        toType: DataType,
        implicit: Boolean,
        allowPromotionAndDemotion: Boolean,
    ): Conversion? =
        lb.conversionMap.findConversion(
            fromType,
            toType,
            implicit,
            allowPromotionAndDemotion,
            lb.compiledLibrary.operatorMap,
        )

    fun convertExpression(
        expression: Expression,
        targetType: DataType,
        implicit: Boolean = true,
    ): Expression {
        val conversion = findConversion(expression.resultType!!, targetType, implicit, false)
        if (conversion != null) return convertExpression(expression, conversion)
        DataTypes.verifyType(expression.resultType, targetType)
        return expression
    }

    @JsExport.Ignore
    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
    fun convertExpression(expression: Expression, conversion: Conversion): Expression {
        return when (conversion) {
            is Conversion.Cast -> {
                if (conversion.fromType is ChoiceType && conversion.toType is ChoiceType) {
                    if (conversion.fromType.isSubSetOf(conversion.toType)) {
                        // choice→choice where the source is a subset: no cast needed.
                        return expression
                    }
                    // Narrowing choice→choice still needs a run-time As.
                }
                collapseTypeCase(lb.buildAs(expression, conversion.toType))
            }
            is Conversion.ChoiceNarrowingCast -> {
                val castedOperand = lb.buildAs(expression, conversion.innerConversion.fromType)
                var result = convertExpression(castedOperand, conversion.innerConversion)
                if (conversion.alternativeConversions.isNotEmpty()) {
                    val caseResult = of.createCase()
                    caseResult.resultType = result.resultType
                    caseResult.caseItem.add(
                        of.createCaseItem()
                            .withWhen(lb.buildIs(expression, conversion.innerConversion.fromType))
                            .withThen(result)
                    )
                    for (alternative in conversion.alternativeConversions) {
                        caseResult.caseItem.add(
                            of.createCaseItem()
                                .withWhen(lb.buildIs(expression, alternative.fromType))
                                .withThen(
                                    convertExpression(
                                        lb.buildAs(expression, alternative.fromType),
                                        alternative,
                                    )
                                )
                        )
                    }
                    caseResult.withElse(lb.buildNull(result.resultType))
                    result = caseResult
                }
                result
            }
            is Conversion.ChoiceWideningCast -> {
                val castedOperand = lb.buildAs(expression, conversion.innerConversion.fromType)
                convertExpression(castedOperand, conversion.innerConversion)
            }
            is Conversion.ListConversion -> convertListExpression(expression, conversion)
            is Conversion.ListDemotion -> demoteListExpression(expression, conversion)
            is Conversion.ListPromotion -> promoteListExpression(expression, conversion)
            is Conversion.IntervalConversion -> convertIntervalExpression(expression, conversion)
            is Conversion.IntervalDemotion -> demoteIntervalExpression(expression, conversion)
            is Conversion.IntervalPromotion -> promoteIntervalExpression(expression, conversion)
            is Conversion.OperatorConversion -> {
                val functionRef =
                    of.createFunctionRef()
                        .withLibraryName(conversion.operator.libraryName)
                        .withName(conversion.operator.name)
                        .withOperand(listOf(expression))
                val systemFunctionInvocation =
                    lb.systemFunctionResolverInternal.resolveSystemFunction(functionRef)
                if (systemFunctionInvocation != null) {
                    systemFunctionInvocation.expression
                } else {
                    lb.resolveCall(
                        functionRef.libraryName,
                        functionRef.name!!,
                        FunctionRefInvocation(functionRef),
                        allowPromotionAndDemotion = false,
                        allowFluent = false,
                    )
                    functionRef
                }
            }
        }
    }

    fun resolveToList(expression: Expression?): Expression {
        // Use a ToList operator here to avoid duplicate evaluation of the operand.
        val toList = of.createToList().withOperand(expression)
        toList.resultType = ListType(expression!!.resultType!!)
        return toList
    }

    private fun convertListExpression(
        expression: Expression?,
        conversion: Conversion.ListConversion,
    ): Expression {
        return of.createQuery()
            .withSource(
                listOf(
                    of.createAliasedQuerySource()
                        .withAlias("X")
                        .withExpression(expression)
                        .withResultType(conversion.fromType)
                )
            )
            .withReturn(
                of.createReturnClause()
                    .withDistinct(false)
                    .withExpression(
                        convertExpression(
                            of.createAliasRef()
                                .withName("X")
                                .withResultType(conversion.fromType.elementType),
                            conversion.elementConversion,
                        )
                    )
                    .withResultType(conversion.toType)
            )
            .withResultType(conversion.toType)
    }

    private fun demoteListExpression(
        expression: Expression?,
        conversion: Conversion.ListDemotion,
    ): Expression {
        val singletonFrom = of.createSingletonFrom().withOperand(expression)
        singletonFrom.resultType = conversion.fromType.elementType
        lb.resolveCall("System", "SingletonFrom", singletonFrom)
        lb.reportWarning("List-valued expression was demoted to a singleton.", expression)
        val inner = conversion.elementConversion
        return if (inner != null) convertExpression(singletonFrom, inner) else singletonFrom
    }

    private fun promoteListExpression(
        expression: Expression,
        conversion: Conversion.ListPromotion,
    ): Expression {
        var result = expression
        val inner = conversion.elementConversion
        if (inner != null) result = convertExpression(result, inner)
        if (result.resultType == lb.resolveTypeName("System", "Boolean")) {
            lb.reportWarning("Boolean-valued expression was promoted to a list.", result)
        }
        return resolveToList(result)
    }

    private fun demoteIntervalExpression(
        expression: Expression?,
        conversion: Conversion.IntervalDemotion,
    ): Expression {
        val pointFrom = of.createPointFrom().withOperand(expression)
        pointFrom.resultType = conversion.fromType.pointType
        lb.resolveCall("System", "PointFrom", pointFrom)
        lb.reportWarning("Interval-valued expression was demoted to a point.", expression)
        val inner = conversion.pointConversion
        return if (inner != null) convertExpression(pointFrom, inner) else pointFrom
    }

    private fun promoteIntervalExpression(
        expression: Expression,
        conversion: Conversion.IntervalPromotion,
    ): Expression {
        var result = expression
        val inner = conversion.pointConversion
        if (inner != null) result = convertExpression(result, inner)
        return resolveToInterval(result)
    }

    /**
     * Promote a point to an interval, but guard with a null check so a null point becomes a null
     * interval (rather than an interval with null boundaries).
     */
    private fun resolveToInterval(expression: Expression?): Expression {
        val condition = of.createIf()
        condition.condition = lb.expressionFactoryInternal.buildIsNull(expression)
        condition.then = lb.buildNull(IntervalType(expression!!.resultType!!))
        val toInterval =
            of.createInterval()
                .withLow(expression)
                .withHigh(expression)
                .withLowClosed(true)
                .withHighClosed(true)
        toInterval.resultType = IntervalType(expression.resultType!!)
        condition.`else` = toInterval
        condition.resultType = lb.resolveTypeName("System", "Boolean")
        return condition
    }

    private fun convertIntervalExpression(
        expression: Expression?,
        conversion: Conversion.IntervalConversion,
    ): Expression {
        // Constant-folding optimization: when the expression is an Interval literal (possibly
        // wrapped in an As cast from an upstream cast+conversion chain), convert the bounds
        // directly and preserve the lowClosed/highClosed booleans. This avoids emitting
        // runtime Property accesses for values known at compile time.
        val interval = unwrapIntervalLiteral(expression)
        if (interval != null) return constantFoldIntervalConversion(interval, conversion)

        // For non-literal intervals, extract bounds via Property access at runtime.
        return of.createInterval()
            .withLow(
                convertExpression(
                    of.createProperty()
                        .withSource(expression)
                        .withPath("low")
                        .withResultType(conversion.fromType.pointType),
                    conversion.pointConversion,
                )
            )
            .withLowClosedExpression(
                of.createProperty()
                    .withSource(expression)
                    .withPath("lowClosed")
                    .withResultType(lb.resolveTypeName("System", "Boolean"))
            )
            .withHigh(
                convertExpression(
                    of.createProperty()
                        .withSource(expression)
                        .withPath("high")
                        .withResultType(conversion.fromType.pointType),
                    conversion.pointConversion,
                )
            )
            .withHighClosedExpression(
                of.createProperty()
                    .withSource(expression)
                    .withPath("highClosed")
                    .withResultType(lb.resolveTypeName("System", "Boolean"))
            )
            .withResultType(conversion.toType)
    }

    /** Unwrap an expression to find an Interval literal, including through As casts. */
    private fun unwrapIntervalLiteral(expression: Expression?): org.hl7.elm.r1.Interval? =
        when (expression) {
            is org.hl7.elm.r1.Interval -> expression
            is As -> expression.operand as? org.hl7.elm.r1.Interval
            else -> null
        }

    /** Constant-fold an interval conversion by directly converting the literal's bounds. */
    private fun constantFoldIntervalConversion(
        interval: org.hl7.elm.r1.Interval,
        conversion: Conversion.IntervalConversion,
    ): Expression {
        val low = interval.low
        val high = interval.high
        return of.createInterval()
            .withLow(if (low != null) convertExpression(low, conversion.pointConversion) else null)
            .withLowClosed(interval.lowClosed)
            .withHigh(
                if (high != null) convertExpression(high, conversion.pointConversion) else null
            )
            .withHighClosed(interval.highClosed)
            .withResultType(conversion.toType)
    }

    /**
     * If the operand of an `As` is a type-case Case expression (each `when X is T then X as T`),
     * and one of those cases matches the `As` target, return that case's `then` directly — avoiding
     * the runtime type dispatch entirely.
     */
    @Suppress("NestedBlockDepth")
    private fun collapseTypeCase(asExpression: As): Expression {
        if (asExpression.operand is Case) {
            val c = asExpression.operand as Case
            if (isTypeCase(c)) {
                for (ci in c.caseItem) {
                    if (DataTypes.equal(asExpression.resultType, ci.then!!.resultType)) {
                        return ci.then!!
                    }
                }
            }
        }
        return asExpression
    }

    @Suppress("ReturnCount")
    private fun isTypeCase(c: Case): Boolean {
        if (c.comparand != null) return false
        for (ci in c.caseItem) {
            if (ci.`when` !is Is) return false
            if (ci.then!!.resultType == null) return false
        }
        if (c.`else` !is Null) return false
        return c.resultType is ChoiceType
    }
}
