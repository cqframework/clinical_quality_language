package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateFrom
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.TimeFrom
import org.hl7.elm.r1.TimeOfDay
import org.hl7.elm.r1.Today
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class DateTimeOperatorsTest {
    @Suppress("LongMethod")
    @Test
    fun dateTime() {
        var def: ExpressionDef = defs!!["DateTimeYear"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        var dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, Matchers.nullValue())
        assertThat<Expression?>(dt.day, Matchers.nullValue())
        assertThat<Expression?>(dt.hour, Matchers.nullValue())
        assertThat<Expression?>(dt.minute, Matchers.nullValue())
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeMonth"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, Matchers.nullValue())
        assertThat<Expression?>(dt.hour, Matchers.nullValue())
        assertThat<Expression?>(dt.minute, Matchers.nullValue())
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeDay"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, Matchers.nullValue())
        assertThat<Expression?>(dt.minute, Matchers.nullValue())
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeHour"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, Matchers.nullValue())
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeMinute"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeSecond"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeMillisecond"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, literalFor(0))
        assertThat<Expression?>(dt.timezoneOffset, Matchers.nullValue())

        def = defs!!["DateTimeMillisecondOffset"]!!
        assertThat(def, hasTypeAndResult(DateTime::class.java, "System.DateTime"))
        dt = def.expression as DateTime?
        assertThat<Expression?>(dt!!.year, literalFor(2014))
        assertThat<Expression?>(dt.month, literalFor(1))
        assertThat<Expression?>(dt.day, literalFor(1))
        assertThat<Expression?>(dt.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, literalFor(0))
        assertThat<Expression?>(dt.timezoneOffset, literalFor(5.5))
    }

    @Test
    fun date() {
        var def: ExpressionDef = defs!!["DateYear"]!!
        assertThat(def, hasTypeAndResult(Date::class.java, "System.Date"))
        var d = def.expression as Date?
        assertThat<Expression?>(d!!.year, literalFor(2014))
        assertThat<Expression?>(d.month, Matchers.nullValue())
        assertThat<Expression?>(d.day, Matchers.nullValue())

        def = defs!!["DateMonth"]!!
        assertThat(def, hasTypeAndResult(Date::class.java, "System.Date"))
        d = def.expression as Date?
        assertThat<Expression?>(d!!.year, literalFor(2014))
        assertThat<Expression?>(d.month, literalFor(1))
        assertThat<Expression?>(d.day, Matchers.nullValue())

        def = defs!!["DateDay"]!!
        assertThat(def, hasTypeAndResult(Date::class.java, "System.Date"))
        d = def.expression as Date?
        assertThat<Expression?>(d!!.year, literalFor(2014))
        assertThat<Expression?>(d.month, literalFor(1))
        assertThat<Expression?>(d.day, literalFor(1))
    }

    @Test
    fun time() {
        var def: ExpressionDef = defs!!["TimeHour"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        var dt = def.expression as Time?
        assertThat<Expression?>(dt!!.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, Matchers.nullValue())
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())

        def = defs!!["TimeMinute"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        dt = def.expression as Time?
        assertThat<Expression?>(dt!!.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, Matchers.nullValue())
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())

        def = defs!!["TimeSecond"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        dt = def.expression as Time?
        assertThat<Expression?>(dt!!.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, Matchers.nullValue())

        def = defs!!["TimeMillisecond"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        dt = def.expression as Time?
        assertThat<Expression?>(dt!!.hour, literalFor(0))
        assertThat<Expression?>(dt.minute, literalFor(0))
        assertThat<Expression?>(dt.second, literalFor(0))
        assertThat<Expression?>(dt.millisecond, literalFor(0))
    }

    @Test
    fun dateExtractor() {
        val def: ExpressionDef? = defs!!["DateExtractor"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(DateFrom::class.java, "System.Date"))
    }

    @Test
    fun timeExtractor() {
        val def: ExpressionDef? = defs!!["TimeExtractor"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(TimeFrom::class.java, "System.Time"))
    }

    @Test
    fun now() {
        val def: ExpressionDef? = defs!!["NowExpression"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Now::class.java, "System.DateTime"))
    }

    @Test
    fun today() {
        val def: ExpressionDef? = defs!!["TodayExpression"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Today::class.java, "System.Date"))
    }

    @Test
    fun timeOfDay() {
        val def: ExpressionDef? = defs!!["TimeOfDayExpression"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(TimeOfDay::class.java, "System.Time"))
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
                    DateTimeOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/DateTimeOperators.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(
                        modelManager,
                        CqlCompilerOptions(
                            CqlCompilerException.ErrorSeverity.Warning,
                            SignatureLevel.None,
                        ),
                    ),
                )
            assertThat(translator.errors.size, `is`(0))
            val library = translator.toELM()
            defs = HashMap()
            for (def in library!!.statements!!.def) {
                defs!![def.name] = def
            }
        }
    }
}
