@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.shared.BigDecimal
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.NamedType
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.MaxValue
import org.hl7.elm.r1.MinValue
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio

/**
 * Builds ELM leaf nodes and simple composites: literals (string/bool/int/decimal/long/number/
 * quantity/ratio/interval), type-annotation nodes (`As`/`Is`/`Null`), null-checks, bounded min/max,
 * and predecessor/successor calls.
 *
 * Extracted from [Cql2ElmContext] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [Cql2ElmContext] for type-system queries (resolveTypeName, dataTypeToQName,
 * dataTypeToTypeSpecifier), comparison helpers (ensureCompatible[Types]), and operator resolution
 * (resolveCall). These collaborators will move to the new SemanticAnalyzer component as the
 * retirement proceeds.
 */
@Suppress("TooManyFunctions")
class ExpressionFactory(private val lb: Cql2ElmContext, private val of: IdObjectFactory) {
    // Literal construction ----------------------------------------------------

    @JsExport.Ignore
    fun createLiteral(value: String?, type: String): Literal {
        val resultType = lb.resolveTypeName("System", type)
        val result =
            of.createLiteral().withValue(value).withValueType(lb.dataTypeToQName(resultType))
        result.resultType = resultType
        return result
    }

    @JsExport.Ignore fun createLiteral(string: String?): Literal = createLiteral(string, "String")

    @JsExport.Ignore
    fun createLiteral(bool: Boolean): Literal = createLiteral(bool.toString(), "Boolean")

    @JsExport.Ignore
    fun createLiteral(integer: Int): Literal = createLiteral(integer.toString(), "Integer")

    @JsExport.Ignore
    fun createLiteral(value: Double): Literal = createLiteral(value.toString(), "Decimal")

    fun createNumberLiteral(value: String): Literal {
        val resultType =
            lb.resolveTypeName("System", if (value.contains(".")) "Decimal" else "Integer")
        val result =
            of.createLiteral().withValue(value).withValueType(lb.dataTypeToQName(resultType))
        result.resultType = resultType
        return result
    }

    fun createLongNumberLiteral(value: String?): Literal {
        val resultType = lb.resolveTypeName("System", "Long")
        val result =
            of.createLiteral().withValue(value).withValueType(lb.dataTypeToQName(resultType))
        result.resultType = resultType
        return result
    }

    fun createQuantity(value: BigDecimal?, unit: String): Quantity {
        lb.validateUnit(unit)
        val result = of.createQuantity().withValue(value).withUnit(unit)
        result.resultType = lb.resolveTypeName("System", "Quantity")
        return result
    }

    fun createRatio(numerator: Quantity?, denominator: Quantity?): Ratio {
        val result = of.createRatio().withNumerator(numerator).withDenominator(denominator)
        result.resultType = lb.resolveTypeName("System", "Ratio")
        return result
    }

    fun createInterval(
        low: Expression?,
        lowClosed: Boolean,
        high: Expression?,
        highClosed: Boolean,
    ): Interval {
        val result: Interval =
            of.createInterval()
                .withLow(low)
                .withLowClosed(lowClosed)
                .withHigh(high)
                .withHighClosed(highClosed)
        val pointType: DataType? =
            lb.ensureCompatibleTypes(result.low!!.resultType, result.high!!.resultType!!)
        result.resultType = IntervalType(pointType!!)
        result.low = lb.ensureCompatible(result.low, pointType)
        result.high = lb.ensureCompatible(result.high, pointType)
        return result
    }

    // Type annotation ---------------------------------------------------------

    fun buildAs(expression: Expression?, asType: DataType?): As {
        val result = of.createAs().withOperand(expression).withResultType(asType)
        if (result.resultType is NamedType) {
            result.asType = lb.dataTypeToQName(result.resultType)
        } else {
            result.asTypeSpecifier = lb.dataTypeToTypeSpecifier(result.resultType)
        }
        return result
    }

    fun buildIs(expression: Expression?, isType: DataType?): Is {
        val result =
            of.createIs()
                .withOperand(expression)
                .withResultType(lb.resolveTypeName("System", "Boolean"))
        if (isType is NamedType) {
            result.isType = lb.dataTypeToQName(isType)
        } else {
            result.isTypeSpecifier = lb.dataTypeToTypeSpecifier(isType)
        }
        return result
    }

    fun buildNull(nullType: DataType?): Null {
        val result = of.createNull().withResultType(nullType)
        if (nullType is NamedType) {
            result.resultTypeName = lb.dataTypeToQName(nullType)
        } else {
            result.resultTypeSpecifier = lb.dataTypeToTypeSpecifier(nullType)
        }
        return result
    }

    // Null checks -------------------------------------------------------------

    fun buildIsNull(expression: Expression?): IsNull {
        val isNull = of.createIsNull().withOperand(expression)
        isNull.resultType = lb.resolveTypeName("System", "Boolean")
        return isNull
    }

    fun buildIsNotNull(expression: Expression?): Not {
        val isNull = buildIsNull(expression)
        val not = of.createNot().withOperand(isNull)
        not.resultType = lb.resolveTypeName("System", "Boolean")
        return not
    }

    // Bounded extrema ---------------------------------------------------------

    fun buildMinimum(dataType: DataType?): MinValue {
        val minimum = of.createMinValue()
        minimum.valueType = lb.dataTypeToQName(dataType)
        minimum.resultType = dataType
        return minimum
    }

    fun buildMaximum(dataType: DataType?): MaxValue {
        val maximum = of.createMaxValue()
        maximum.valueType = lb.dataTypeToQName(dataType)
        maximum.resultType = dataType
        return maximum
    }

    // Predecessor / successor -------------------------------------------------

    fun buildPredecessor(source: Expression?): Expression {
        val result = of.createPredecessor().withOperand(source)
        lb.resolveCall("System", "Predecessor", result)
        return result
    }

    fun buildSuccessor(source: Expression?): Expression {
        val result = of.createSuccessor().withOperand(source)
        lb.resolveCall("System", "Successor", result)
        return result
    }
}
