package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import javax.xml.namespace.QName
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Convert
import org.hl7.elm.r1.ConvertsToBoolean
import org.hl7.elm.r1.ConvertsToDate
import org.hl7.elm.r1.ConvertsToDateTime
import org.hl7.elm.r1.ConvertsToDecimal
import org.hl7.elm.r1.ConvertsToInteger
import org.hl7.elm.r1.ConvertsToLong
import org.hl7.elm.r1.ConvertsToQuantity
import org.hl7.elm.r1.ConvertsToRatio
import org.hl7.elm.r1.ConvertsToString
import org.hl7.elm.r1.ConvertsToTime
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.MaxValue
import org.hl7.elm.r1.MinValue
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.ToBoolean
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToDate
import org.hl7.elm.r1.ToDateTime
import org.hl7.elm.r1.ToDecimal
import org.hl7.elm.r1.ToInteger
import org.hl7.elm.r1.ToLong
import org.hl7.elm.r1.ToQuantity
import org.hl7.elm.r1.ToRatio
import org.hl7.elm.r1.ToString
import org.hl7.elm.r1.ToTime
import org.hl7.elm.r1.TypeSpecifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("LargeClass")
internal class TypeOperatorsTest {
    @Test
    fun asTest() {
        val def: ExpressionDef = defs!!["AsExpression"]!!
        assertThat(def, hasTypeAndResult(As::class.java, "System.Boolean"))
        val asDef = def.expression as As?
        assertThat<Expression?>(asDef!!.operand, Matchers.instanceOf<Expression?>(Null::class.java))
        assertThat<TypeSpecifier?>(
            asDef.asTypeSpecifier,
            Matchers.instanceOf<TypeSpecifier?>(NamedTypeSpecifier::class.java),
        )
        val spec = asDef.asTypeSpecifier as NamedTypeSpecifier?
        assertThat<QName?>(spec!!.name, `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Boolean")))

        val resultType = asDef.resultType
        assertThat(resultType.toString(), `is`("System.Boolean"))
        // assertThat(as.getAsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Test
    fun cast() {
        val def: ExpressionDef = defs!!["CastExpression"]!!
        assertThat(def, hasTypeAndResult(As::class.java, "System.Boolean"))
        val asDef = def.expression as As?
        assertThat<Expression?>(asDef!!.operand, Matchers.instanceOf<Expression?>(Null::class.java))
        assertThat<TypeSpecifier?>(
            asDef.asTypeSpecifier,
            Matchers.instanceOf<TypeSpecifier?>(NamedTypeSpecifier::class.java),
        )
        val spec = asDef.asTypeSpecifier as NamedTypeSpecifier?
        assertThat<QName?>(spec!!.name, `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Boolean")))

        val resultType = asDef.resultType
        assertThat(resultType.toString(), `is`("System.Boolean"))
        // assertThat(as.getAsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Test
    fun isExpression() {
        val def: ExpressionDef = defs!!["IsExpression"]!!
        assertThat(def, hasTypeAndResult(Is::class.java, "System.Boolean"))
        val isDef = def.expression as Is?
        assertThat<Expression?>(isDef!!.operand, Matchers.instanceOf<Expression?>(Null::class.java))
        assertThat<TypeSpecifier?>(
            isDef.isTypeSpecifier,
            Matchers.instanceOf<TypeSpecifier?>(NamedTypeSpecifier::class.java),
        )
        val spec = isDef.isTypeSpecifier as NamedTypeSpecifier?
        assertThat<QName?>(spec!!.name, `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Boolean")))

        val resultType = isDef.resultType
        assertThat(resultType.toString(), `is`("System.Boolean"))
        // assertThat(is.getIsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Suppress("LongMethod")
    @Test
    fun testToString() {
        var def: ExpressionDef = defs!!["BooleanToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        var convert = def.expression as ToString?
        assertThat<Expression?>(convert!!.operand, literalFor(false))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs!!["IntegerToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(convert!!.operand, literalFor(3))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs!!["DecimalToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(convert!!.operand, literalFor(3.0))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs!!["QuantityToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Quantity::class.java),
        )
        val q = convert.operand as Quantity?
        assertThat(q!!.value?.toDouble(), `is`(3.0))
        assertThat<String?>(q.unit, `is`<String?>("m"))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs!!["RatioToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Ratio::class.java),
        )
        val r = convert.operand as Ratio?
        assertThat(r!!.denominator!!.value?.toDouble(), `is`(180.0))
        assertThat<String?>(r.denominator!!.unit, `is`<String?>("1"))
        assertThat(r.numerator!!.value?.toDouble(), `is`(1.0))
        assertThat<String?>(r.numerator!!.unit, `is`<String?>("1"))

        def = defs!!["DateToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Date::class.java),
        )
        val d = convert.operand as Date?
        assertThat<Expression?>(d!!.year, literalFor(2014))
        assertThat<Expression?>(d.month, literalFor(1))
        assertThat<Expression?>(d.day, literalFor(1))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs!!["DateTimeToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(DateTime::class.java),
        )
        val dt = convert.operand as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, literalFor(0))
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs!!["TimeToString"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Time::class.java),
        )
        val t = convert.operand as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))
        assertThat<Expression?>(t.minute, literalFor(0))
        assertThat<Expression?>(t.second, literalFor(0))
        assertThat<Expression?>(t.millisecond, literalFor(0))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
    }

    @Suppress("LongMethod")
    @Test
    fun toStringFunction() {
        var def: ExpressionDef = defs!!["BooleanToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        var convert = def.expression as ToString?
        assertThat<Expression?>(convert!!.operand, literalFor(false))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["IntegerToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(convert!!.operand, literalFor(3))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["DecimalToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(convert!!.operand, literalFor(3.0))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["QuantityToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Quantity::class.java),
        )
        val q = convert.operand as Quantity?
        assertThat(q!!.value?.toDouble(), `is`(3.0))
        assertThat<String?>(q.unit, `is`<String?>("m"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["RatioToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Ratio::class.java),
        )
        val r = convert.operand as Ratio?
        assertThat(r!!.denominator!!.value?.toDouble(), `is`(180.0))
        assertThat<String?>(r.denominator!!.unit, `is`<String?>("1"))
        assertThat(r.numerator!!.value?.toDouble(), `is`(1.0))
        assertThat<String?>(r.numerator!!.unit, `is`<String?>("1"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["DateToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Date::class.java),
        )
        val d = convert.operand as Date?
        assertThat<Expression?>(d!!.year, literalFor(2014))
        assertThat<Expression?>(d.month, literalFor(1))
        assertThat<Expression?>(d.day, literalFor(1))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["DateTimeToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(DateTime::class.java),
        )
        val dt = convert.operand as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, literalFor(0))
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["TimeToStringFun"]!!
        assertThat(def, hasTypeAndResult(ToString::class.java, "System.String"))
        convert = def.expression as ToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Time::class.java),
        )
        val t = convert.operand as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))
        assertThat<Expression?>(t.minute, literalFor(0))
        assertThat<Expression?>(t.second, literalFor(0))
        assertThat<Expression?>(t.millisecond, literalFor(0))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Suppress("LongMethod")
    @Test
    fun convertsToString() {
        var def: ExpressionDef = defs!!["BooleanConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        var convert = def.expression as ConvertsToString?
        assertThat<Expression?>(convert!!.operand, literalFor(false))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["IntegerConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(convert!!.operand, literalFor(3))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["DecimalConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(convert!!.operand, literalFor(3.0))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["QuantityConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Quantity::class.java),
        )
        val q = convert.operand as Quantity?
        assertThat(q!!.value?.toDouble(), `is`(3.0))
        assertThat<String?>(q.unit, `is`<String?>("m"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["RatioConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Ratio::class.java),
        )
        val r = convert.operand as Ratio?
        assertThat(r!!.denominator!!.value?.toDouble(), `is`(180.0))
        assertThat<String?>(r.denominator!!.unit, `is`<String?>("1"))
        assertThat(r.numerator!!.value?.toDouble(), `is`(1.0))
        assertThat<String?>(r.numerator!!.unit, `is`<String?>("1"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["DateConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Date::class.java),
        )
        val d = convert.operand as Date?
        assertThat<Expression?>(d!!.year, literalFor(2014))
        assertThat<Expression?>(d.month, literalFor(1))
        assertThat<Expression?>(d.day, literalFor(1))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["DateTimeConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(DateTime::class.java),
        )
        val dt = convert.operand as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, literalFor(0))
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["TimeConvertsToString"]!!
        assertThat(def, hasTypeAndResult(ConvertsToString::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToString?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(Time::class.java),
        )
        val t = convert.operand as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))
        assertThat<Expression?>(t.minute, literalFor(0))
        assertThat<Expression?>(t.second, literalFor(0))
        assertThat<Expression?>(t.millisecond, literalFor(0))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toBoolean() {
        val def: ExpressionDef = defs!!["StringToBoolean"]!!
        assertThat(def, hasTypeAndResult(ToBoolean::class.java, "System.Boolean"))
        val convert = def.expression as ToBoolean?
        assertThat<Expression?>(convert!!.operand, literalFor("false"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Boolean"));
    }

    @Test
    fun toBooleanFunction() {
        val def: ExpressionDef = defs!!["StringToBooleanFun"]!!
        assertThat(def, hasTypeAndResult(ToBoolean::class.java, "System.Boolean"))
        val convert = def.expression as ToBoolean?
        assertThat<Expression?>(convert!!.operand, literalFor("false"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun convertsToBoolean() {
        val def: ExpressionDef = defs!!["StringConvertsToBoolean"]!!
        assertThat(def, hasTypeAndResult(ConvertsToBoolean::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToBoolean?
        assertThat<Expression?>(convert!!.operand, literalFor("false"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toInteger() {
        val def: ExpressionDef = defs!!["StringToInteger"]!!
        assertThat(def, hasTypeAndResult(ToInteger::class.java, "System.Integer"))
        val convert = def.expression as ToInteger?
        assertThat<Expression?>(convert!!.operand, literalFor("1"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Integer"));
    }

    @Test
    fun toIntegerFunction() {
        val def: ExpressionDef = defs!!["StringToIntegerFun"]!!
        assertThat(def, hasTypeAndResult(ToInteger::class.java, "System.Integer"))
        val convert = def.expression as ToInteger?
        assertThat<Expression?>(convert!!.operand, literalFor("1"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun convertsToInteger() {
        val def: ExpressionDef = defs!!["StringConvertsToInteger"]!!
        assertThat(def, hasTypeAndResult(ConvertsToInteger::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToInteger?
        assertThat<Expression?>(convert!!.operand, literalFor("1"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toLong() {
        val def: ExpressionDef = defs!!["StringToLong"]!!
        assertThat(def, hasTypeAndResult(ToLong::class.java, "System.Long"))
        val convert = def.expression as ToLong?
        assertThat<Expression?>(convert!!.operand, literalFor("1"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Long"));
    }

    @Test
    fun convertsToLong() {
        val def: ExpressionDef = defs!!["StringConvertsToLong"]!!
        assertThat(def, hasTypeAndResult(ConvertsToLong::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToLong?
        assertThat<Expression?>(convert!!.operand, literalFor("1"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Long")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toDecimal() {
        var def: ExpressionDef = defs!!["StringToDecimal"]!!
        assertThat(def, hasTypeAndResult(ToDecimal::class.java, "System.Decimal"))
        var convert = def.expression as ToDecimal?
        assertThat<Expression?>(convert!!.operand, literalFor("3.0"))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
        def = defs!!["IntegerToDecimal"]!!
        assertThat(def, hasTypeAndResult(ToDecimal::class.java, "System.Decimal"))
        convert = def.expression as ToDecimal?
        assertThat<Expression?>(convert!!.operand, literalFor(1))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    fun toDecimalFunction() {
        var def: ExpressionDef = defs!!["StringToDecimalFun"]!!
        assertThat(def, hasTypeAndResult(ToDecimal::class.java, "System.Decimal"))
        var convert = def.expression as ToDecimal?
        assertThat<Expression?>(convert!!.operand, literalFor("3.0"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["IntegerToDecimalFun"]!!
        assertThat(def, hasTypeAndResult(ToDecimal::class.java, "System.Decimal"))
        convert = def.expression as ToDecimal?
        assertThat<Expression?>(convert!!.operand, literalFor(1))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    fun convertsToDecimal() {
        var def: ExpressionDef = defs!!["StringConvertsToDecimal"]!!
        assertThat(def, hasTypeAndResult(ConvertsToDecimal::class.java, "System.Boolean"))
        var convert = def.expression as ConvertsToDecimal?
        assertThat<Expression?>(convert!!.operand, literalFor("3.0"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["IntegerConvertsToDecimal"]!!
        assertThat(def, hasTypeAndResult(ConvertsToDecimal::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToDecimal?
        assertThat<Expression?>(convert!!.operand, literalFor(1))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    fun toDate() {
        val def: ExpressionDef = defs!!["StringToDate"]!!
        assertThat(def, hasTypeAndResult(ToDate::class.java, "System.Date"))
        val convert = def.expression as ToDate?
        assertThat<Expression?>(convert!!.operand, literalFor("2014-01-01"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Date"));
    }

    @Test
    fun toDateFunction() {
        val def: ExpressionDef = defs!!["StringToDateFun"]!!
        assertThat(def, hasTypeAndResult(ToDate::class.java, "System.Date"))
        val convert = def.expression as ToDate?
        assertThat<Expression?>(convert!!.operand, literalFor("2014-01-01"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Date")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun convertsToDate() {
        val def: ExpressionDef = defs!!["StringConvertsToDate"]!!
        assertThat(def, hasTypeAndResult(ConvertsToDate::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToDate?
        assertThat<Expression?>(convert!!.operand, literalFor("2014-01-01"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Date")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toDateTime() {
        val def: ExpressionDef = defs!!["StringToDateTime"]!!
        assertThat(def, hasTypeAndResult(ToDateTime::class.java, "System.DateTime"))
        val convert = def.expression as ToDateTime?
        assertThat<Expression?>(convert!!.operand, literalFor("2014-01-01T00:00:00.0000+0700"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "DateTime"));
    }

    @Test
    fun toDateTimeFunction() {
        val def: ExpressionDef = defs!!["StringToDateTimeFun"]!!
        assertThat(def, hasTypeAndResult(ToDateTime::class.java, "System.DateTime"))
        val convert = def.expression as ToDateTime?
        assertThat<Expression?>(convert!!.operand, literalFor("2014-01-01T00:00:00.0000+0700"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun convertsToDateTime() {
        val def: ExpressionDef = defs!!["StringConvertsToDateTime"]!!
        assertThat(def, hasTypeAndResult(ConvertsToDateTime::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToDateTime?
        assertThat<Expression?>(convert!!.operand, literalFor("2014-01-01T00:00:00.0000+0700"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toTime() {
        val def: ExpressionDef = defs!!["StringToTime"]!!
        assertThat(def, hasTypeAndResult(ToTime::class.java, "System.Time"))
        val convert = def.expression as ToTime?
        assertThat<Expression?>(convert!!.operand, literalFor("T00:00:00.0000+0700"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Time"));
    }

    @Test
    fun toTimeFunction() {
        val def: ExpressionDef = defs!!["StringToTimeFun"]!!
        assertThat(def, hasTypeAndResult(ToTime::class.java, "System.Time"))
        val convert = def.expression as ToTime?
        assertThat<Expression?>(convert!!.operand, literalFor("T00:00:00.0000+0700"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun convertsToTime() {
        val def: ExpressionDef = defs!!["StringConvertsToTime"]!!
        assertThat(def, hasTypeAndResult(ConvertsToTime::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToTime?
        assertThat<Expression?>(convert!!.operand, literalFor("T00:00:00.0000+0700"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toQuantity() {
        var def: ExpressionDef = defs!!["StringToQuantity"]!!
        assertThat(def, hasTypeAndResult(ToQuantity::class.java, "System.Quantity"))
        var convert = def.expression as ToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor("3.0 'm'"))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
        def = defs!!["IntegerToQuantity"]!!
        assertThat(def, hasTypeAndResult(ToQuantity::class.java, "System.Quantity"))
        convert = def.expression as ToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor(1))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
        def = defs!!["DecimalToQuantity"]!!
        assertThat(def, hasTypeAndResult(ToQuantity::class.java, "System.Quantity"))
        convert = def.expression as ToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor(1.0))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
    }

    @Test
    fun toQuantityFunction() {
        var def: ExpressionDef = defs!!["StringToQuantityFun"]!!
        assertThat(def, hasTypeAndResult(ToQuantity::class.java, "System.Quantity"))
        var convert = def.expression as ToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor("3.0 'm'"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Quantity")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["IntegerToQuantityFun"]!!
        assertThat(def, hasTypeAndResult(ToQuantity::class.java, "System.Quantity"))
        convert = def.expression as ToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor(1))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
        def = defs!!["DecimalToQuantityFun"]!!
        assertThat(def, hasTypeAndResult(ToQuantity::class.java, "System.Quantity"))
        convert = def.expression as ToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor(1.0))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
    }

    @Test
    fun convertsToQuantity() {
        var def: ExpressionDef = defs!!["StringConvertsToQuantity"]!!
        assertThat(def, hasTypeAndResult(ConvertsToQuantity::class.java, "System.Boolean"))
        var convert = def.expression as ConvertsToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor("3.0 'm'"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Quantity")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["IntegerConvertsToQuantity"]!!
        assertThat(def, hasTypeAndResult(ConvertsToQuantity::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor(1))

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
        def = defs!!["DecimalConvertsToQuantity"]!!
        assertThat(def, hasTypeAndResult(ConvertsToQuantity::class.java, "System.Boolean"))
        convert = def.expression as ConvertsToQuantity?
        assertThat<Expression?>(convert!!.operand, literalFor(1.0))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
    }

    @Test
    fun toRatio() {
        val def: ExpressionDef = defs!!["StringToRatio"]!!
        assertThat(def, hasTypeAndResult(ToRatio::class.java, "System.Ratio"))
        val convert = def.expression as ToRatio?
        assertThat<Expression?>(convert!!.operand, literalFor("1:180"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Ratio"));
    }

    @Test
    fun toRatioFunction() {
        val def: ExpressionDef = defs!!["StringToRatioFun"]!!
        assertThat(def, hasTypeAndResult(ToRatio::class.java, "System.Ratio"))
        val convert = def.expression as ToRatio?
        assertThat<Expression?>(convert!!.operand, literalFor("1:180"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Ratio")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun convertsToRatio() {
        val def: ExpressionDef = defs!!["StringConvertsToRatio"]!!
        assertThat(def, hasTypeAndResult(ConvertsToRatio::class.java, "System.Boolean"))
        val convert = def.expression as ConvertsToRatio?
        assertThat<Expression?>(convert!!.operand, literalFor("1:180"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Ratio")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun toConcept() {
        var def: ExpressionDef = defs!!["CodeToConcept"]!!
        assertThat(def, hasTypeAndResult(ToConcept::class.java, "System.Concept"))
        var toConcept = def.expression as ToConcept?
        assertThat<Expression?>(
            toConcept!!.operand,
            Matchers.instanceOf<Expression?>(ExpressionRef::class.java),
        )
        var ref = toConcept.operand as ExpressionRef?
        assertThat<String?>(ref!!.name, `is`<String?>("MyCode"))

        var resultType = ref.resultType
        assertThat(resultType.toString(), `is`("System.Code"))

        // validateTyping(toConcept, new QName("urn:hl7-org:elm-types:r1", "Concept"));
        def = defs!!["CodesToConcept"]!!
        assertThat(def, hasTypeAndResult(ToConcept::class.java, "System.Concept"))
        toConcept = def.expression as ToConcept?
        assertThat<Expression?>(
            toConcept!!.operand,
            Matchers.instanceOf<Expression?>(ExpressionRef::class.java),
        )
        ref = toConcept.operand as ExpressionRef?
        assertThat<String?>(ref!!.name, `is`<String?>("MyCodes"))

        resultType = ref.resultType
        assertThat(resultType.toString(), `is`("list<System.Code>"))
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Concept"));
    }

    @Test
    fun toConceptFunction() {
        var def: ExpressionDef = defs!!["CodeToConceptFun"]!!
        assertThat(def, hasTypeAndResult(ToConcept::class.java, "System.Concept"))
        var convert = def.expression as ToConcept?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(ExpressionRef::class.java),
        )
        var ref = convert.operand as ExpressionRef?
        assertThat<String?>(ref!!.name, `is`<String?>("MyCode"))

        var resultType = ref.resultType
        assertThat(resultType.toString(), `is`("System.Code"))

        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Concept")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
        def = defs!!["CodesToConceptFun"]!!
        assertThat(def, hasTypeAndResult(ToConcept::class.java, "System.Concept"))
        convert = def.expression as ToConcept?
        assertThat<Expression?>(
            convert!!.operand,
            Matchers.instanceOf<Expression?>(ExpressionRef::class.java),
        )
        ref = convert.operand as ExpressionRef?
        assertThat<String?>(ref!!.name, `is`<String?>("MyCodes"))

        resultType = ref.resultType
        assertThat(resultType.toString(), `is`("list<System.Code>"))
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Concept")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    fun minValue() {
        var def: ExpressionDef = defs!!["MinimumInteger"]!!
        assertThat(def, hasTypeAndResult(MinValue::class.java, "System.Integer"))
        var minValue = def.expression as MinValue?
        assertThat<QName?>(
            minValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Integer")),
        )

        def = defs!!["MinimumDecimal"]!!
        assertThat(def, hasTypeAndResult(MinValue::class.java, "System.Decimal"))
        minValue = def.expression as MinValue?
        assertThat<QName?>(
            minValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Decimal")),
        )

        def = defs!!["MinimumDateTime"]!!
        assertThat(def, hasTypeAndResult(MinValue::class.java, "System.DateTime"))
        minValue = def.expression as MinValue?
        assertThat<QName?>(
            minValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "DateTime")),
        )

        def = defs!!["MinimumTime"]!!
        assertThat(def, hasTypeAndResult(MinValue::class.java, "System.Time"))
        minValue = def.expression as MinValue?
        assertThat<QName?>(
            minValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Time")),
        )
    }

    @Test
    fun maxValue() {
        var def: ExpressionDef = defs!!["MaximumInteger"]!!
        assertThat(def, hasTypeAndResult(MaxValue::class.java, "System.Integer"))
        var maxValue = def.expression as MaxValue?
        assertThat<QName?>(
            maxValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Integer")),
        )

        def = defs!!["MaximumDecimal"]!!
        assertThat(def, hasTypeAndResult(MaxValue::class.java, "System.Decimal"))
        maxValue = def.expression as MaxValue?
        assertThat<QName?>(
            maxValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Decimal")),
        )

        def = defs!!["MaximumDateTime"]!!
        assertThat(def, hasTypeAndResult(MaxValue::class.java, "System.DateTime"))
        maxValue = def.expression as MaxValue?
        assertThat<QName?>(
            maxValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "DateTime")),
        )

        def = defs!!["MaximumTime"]!!
        assertThat(def, hasTypeAndResult(MaxValue::class.java, "System.Time"))
        maxValue = def.expression as MaxValue?
        assertThat<QName?>(
            maxValue!!.valueType,
            `is`<QName?>(QName("urn:hl7-org:elm-types:r1", "Time")),
        )
    }

    companion object {
        private var defs: MutableMap<String?, ExpressionDef>? = null

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setup() {
            val modelManager = ModelManager()
            val translator =
                fromSource(
                    TypeOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/TypeOperators.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager),
                )
            assertThat(translator.errors.size, `is`(0))
            val library = translator.toELM()
            defs = HashMap()
            for (def in library!!.statements!!.def) {
                defs!![def.name] = def
            }
        }

        @Suppress("UnusedPrivateMember")
        private fun validateTyping(convert: Convert, typeName: QName?) {
            assertThat<QName?>(convert.toType, `is`<QName?>(typeName))
            Assertions.assertTrue(convert.toTypeSpecifier != null)
            Assertions.assertTrue(convert.toTypeSpecifier is NamedTypeSpecifier)
            val nts = convert.toTypeSpecifier as NamedTypeSpecifier?
            assertThat<QName?>(nts!!.name, `is`<QName?>(typeName))
        }
    }
}
