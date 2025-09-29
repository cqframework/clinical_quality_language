package org.cqframework.cql.cql2elm

import java.io.IOException
import java.math.BigDecimal
import kotlin.collections.set
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hl7.elm.r1.ConvertQuantity
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("LongMethod")
/** Created by Bryn on 11/21/2017. */
internal class LiteralTests {
    @Test
    @Throws(IOException::class)
    fun dateTimeLiteralTests() {
        val translator = TestUtils.runSemanticTest("DateTimeLiteralTest.cql", 0)
        val library = translator.toELM()
        defs = HashMap()
        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs!![def.name] = def
            }
        }

        var def: ExpressionDef = defs!!["TimeZoneDateTimeLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        var dateTime = def.expression as DateTime?
        assertThat<Expression?>(dateTime!!.timezoneOffset, literalFor(-7.0))

        def = defs!!["TimeZonePositiveDateTimeLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dateTime = def.expression as DateTime?
        assertThat<Expression?>(dateTime!!.timezoneOffset, literalFor(7.0))

        def = defs!!["YearLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Date::class.java, "System.Date"))

        def = defs!!["DateTimeYearLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))

        def = defs!!["UTCYearLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))

        def = defs!!["YearMonthLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Date::class.java, "System.Date"))

        def = defs!!["DateTimeYearMonthLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))

        def = defs!!["UTCYearMonthLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))

        def = defs!!["DateLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Date::class.java, "System.Date"))

        def = defs!!["DateTimeDateLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))

        def = defs!!["UTCDateLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))

        def = defs!!["TimeZoneHalfHourLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dateTime = def.expression as DateTime?
        assertThat<Expression?>(dateTime!!.timezoneOffset, literalFor(1.5))
    }

    @Test
    @Throws(IOException::class)
    fun quantityLiteralTests() {
        val translator = TestUtils.runSemanticTest("QuantityLiteralTest.cql", 1)
        val library = translator.toELM()
        defs = HashMap()
        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs!![def.name] = def
            }
        }

        var def: ExpressionDef = defs!!["ValidQuantityLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Quantity::class.java, "System.Quantity"))
        var quantity = def.expression as Quantity?
        assertThat<BigDecimal?>(
            quantity!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(10))
        )
        assertThat<String?>(quantity.unit, Matchers.`is`<String?>("mm[Hg]"))

        def = defs!!["InvalidQuantityLiteral"]!!
        assertThat("Invalid quantity literal is returned as a Null", def.expression is Null)

        def = defs!!["UnitQuantityLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Quantity::class.java, "System.Quantity"))
        quantity = def.expression as Quantity?
        assertThat<BigDecimal?>(
            quantity!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(10))
        )
        assertThat<String?>(quantity.unit, Matchers.`is`<String?>("1"))

        def = defs!!["AnnotationQuantityLiteral"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Quantity::class.java, "System.Quantity"))
        quantity = def.expression as Quantity?
        assertThat<BigDecimal?>(
            quantity!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(10))
        )
        assertThat<String?>(quantity.unit, Matchers.`is`<String?>("{shab-shab-shab}"))

        def = defs!!["QuantityConversionTest"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(ConvertQuantity::class.java, "System.Quantity")
        )
        var convertQuantity = def.expression as ConvertQuantity?
        assertThat(convertQuantity!!.operand[0], Matchers.instanceOf(Quantity::class.java))
        quantity = convertQuantity.operand[0] as Quantity?
        assertThat<BigDecimal?>(quantity!!.value, Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(5)))
        assertThat<String?>(quantity.unit, Matchers.`is`<String?>("mg"))
        assertThat(convertQuantity.operand[1], Matchers.instanceOf(Literal::class.java))
        var literal: Literal = convertQuantity.operand[1] as Literal
        assertThat(literal.value, Matchers.`is`("g"))

        def = defs!!["QuantityConversionWeekTest"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(ConvertQuantity::class.java, "System.Quantity")
        )
        convertQuantity = def.expression as ConvertQuantity?
        assertThat(convertQuantity!!.operand[0], Matchers.instanceOf(Quantity::class.java))
        quantity = convertQuantity.operand[0] as Quantity?
        assertThat<BigDecimal?>(
            quantity!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(28))
        )
        assertThat<String?>(quantity.unit, Matchers.`is`<String?>("days"))
        assertThat(convertQuantity.operand[1], Matchers.instanceOf(Literal::class.java))
        literal = convertQuantity.operand[1] as Literal
        assertThat(literal.value, Matchers.`is`("wk"))
    }

    private fun getDefs(library: Library): MutableMap<String?, ExpressionDef> {
        val result: MutableMap<String?, ExpressionDef> = HashMap()
        if (library.statements != null) {
            for (def in library.statements!!.def) {
                result[def.name] = def
            }
        }
        return result
    }

    @Test
    @Throws(IOException::class)
    fun ratioLiteralTests() {
        val translator = TestUtils.runSemanticTest("RatioLiteralTest.cql", 0)
        val library = translator.toELM()
        defs = getDefs(library!!)

        var def: ExpressionDef = defs!!["SimpleRatio"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Ratio::class.java, "System.Ratio"))
        var ratio = def.expression as Ratio?
        assertThat<BigDecimal?>(
            ratio!!.numerator!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(5))
        )
        assertThat<BigDecimal?>(
            ratio.denominator!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(5))
        )

        def = defs!!["QuantityRatio"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Ratio::class.java, "System.Ratio"))
        ratio = def.expression as Ratio?
        assertThat<BigDecimal?>(
            ratio!!.numerator!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(5))
        )
        assertThat<String?>(ratio.numerator!!.unit, Matchers.`is`<String?>("mg"))
        assertThat<BigDecimal?>(
            ratio.denominator!!.value,
            Matchers.`is`<BigDecimal?>(BigDecimal.valueOf(100))
        )
        assertThat<String?>(ratio.denominator!!.unit, Matchers.`is`<String?>("mL"))
    }

    @Test
    @Throws(IOException::class)
    fun decimal() {
        val translator = TestUtils.createTranslatorFromText("define TestDecimal: 1.5")
        val library = translator.toELM()
        defs = getDefs(library!!)

        val def: ExpressionDef = defs!!["TestDecimal"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.Decimal"))

        val literal = def.expression as Literal?
        Assertions.assertEquals("1.5", literal!!.value)
    }

    @Test
    @Throws(IOException::class)
    fun string() {
        val translator = TestUtils.createTranslatorFromText("define TestString: '12345''")
        val library = translator.toELM()
        defs = getDefs(library!!)

        val def: ExpressionDef = defs!!["TestString"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        val literal = def.expression as Literal?
        Assertions.assertEquals("12345", literal!!.value)
    }

    @Test
    @Throws(IOException::class)
    fun integer() {
        val translator = TestUtils.createTranslatorFromText("define TestInteger: 12345")
        val library = translator.toELM()
        defs = getDefs(library!!)

        val def: ExpressionDef = defs!!["TestInteger"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.Integer"))

        val literal = def.expression as Literal?
        Assertions.assertEquals("12345", literal!!.value)
    }

    @Test
    @Throws(IOException::class)
    fun longInteger() {
        val translator = TestUtils.createTranslatorFromText("define TestLongInteger: 12345L")
        val library = translator.toELM()
        defs = getDefs(library!!)

        val def: ExpressionDef = defs!!["TestLongInteger"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.Long"))

        val literal = def.expression as Literal?
        Assertions.assertEquals("12345", literal!!.value)
    }

    @Test
    @Throws(IOException::class)
    fun tokenRecognitionErrorTest() {
        val translator = TestUtils.runSemanticTest("TokenRecognitionError.cql", 1)
        assertThat(translator.errors.size, Matchers.equalTo(1))
    }

    companion object {
        private var defs: MutableMap<String?, ExpressionDef>? = null
    }
}
