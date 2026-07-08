package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CalculateAgeAt
import org.hl7.elm.r1.DateTimePrecision
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.ToDate
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class AgeOperatorsTest {
    @Test
    fun ageInYears() {
        val def: ExpressionDef = defs!!["TestAgeInYears"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.YEAR),
        )
        // Verify the datetime is being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(ToDate::class.java))
    }

    @Test
    fun ageInMonths() {
        val def: ExpressionDef = defs!!["TestAgeInMonths"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MONTH),
        )
        // Verify the datetime is being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(ToDate::class.java))
    }

    @Test
    fun ageInWeeks() {
        val def: ExpressionDef = defs!!["TestAgeInWeeks"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.WEEK),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(Property::class.java))
    }

    @Test
    fun ageInDays() {
        val def: ExpressionDef = defs!!["TestAgeInDays"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(Property::class.java))
    }

    @Test
    fun ageInHours() {
        val def: ExpressionDef = defs!!["TestAgeInHours"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.HOUR),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(Property::class.java))
    }

    @Test
    fun ageInMinutes() {
        val def: ExpressionDef = defs!!["TestAgeInMinutes"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MINUTE),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(Property::class.java))
    }

    @Test
    fun ageInSeconds() {
        val def: ExpressionDef = defs!!["TestAgeInSeconds"]!!
        assertThat(def, hasTypeAndResult(CalculateAge::class.java, "System.Integer"))
        val age = def.expression as CalculateAge?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.SECOND),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat<Expression?>(age.operand, Matchers.instanceOf<Expression?>(Property::class.java))
    }

    @Test
    fun ageInYearsAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInYearsAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.YEAR),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun ageInMonthsAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInMonthsAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MONTH),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun ageInWeeksAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInWeeksAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.WEEK),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun ageInDaysAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInDaysAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun ageInYearsAtDate() {
        val def: ExpressionDef = defs!!["TestAgeInYearsAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.YEAR),
        )
        // Verify the datetime is being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ToDate::class.java))
    }

    @Test
    fun ageInMonthsAtDate() {
        val def: ExpressionDef = defs!!["TestAgeInMonthsAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MONTH),
        )
        // Verify the datetime is being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ToDate::class.java))
    }

    @Test
    fun ageInWeeksAtDate() {
        val def: ExpressionDef = defs!!["TestAgeInWeeksAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.WEEK),
        )
        // Verify the datetime is being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ToDate::class.java))
    }

    @Test
    fun ageInDaysAtDate() {
        val def: ExpressionDef = defs!!["TestAgeInDaysAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        // Verify the datetime is being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ToDate::class.java))
    }

    @Test
    fun ageInHoursAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInHoursAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.HOUR),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun ageInMinutesAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInMinutesAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MINUTE),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun ageInSecondsAtDateTime() {
        val def: ExpressionDef = defs!!["TestAgeInSecondsAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.SECOND),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(Property::class.java))
    }

    @Test
    fun calculateAgeInYearsAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInYearsAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.YEAR),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInMonthsAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInMonthsAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MONTH),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInWeeksAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInWeeksAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.WEEK),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInDaysAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInDaysAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInYearsAtDate() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInYearsAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.YEAR),
        )
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInMonthsAtDate() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInMonthsAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MONTH),
        )
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInWeeksAtDate() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInWeeksAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.WEEK),
        )
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInDaysAtDate() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInDaysAtDate"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInHoursAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInHoursAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.HOUR),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInMinutesAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInMinutesAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MINUTE),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
    }

    @Test
    fun calculateAgeInSecondsAtDateTime() {
        val def: ExpressionDef = defs!!["TestCalculateAgeInSecondsAtDateTime"]!!
        assertThat(def, hasTypeAndResult(CalculateAgeAt::class.java, "System.Integer"))
        val age = def.expression as CalculateAgeAt?
        assertThat<DateTimePrecision?>(
            age!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.SECOND),
        )
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.operand[0], Matchers.instanceOf(ExpressionRef::class.java))
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
                    AgeOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/AgeOperators.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager),
                )
            assertThat(translator.errors.size, `is`(0))
            val library = translator.toELM()
            defs = HashMap()
            if (library!!.statements != null) {
                for (def in library.statements!!.def) {
                    defs!![def.name] = def
                }
            }
        }
    }
}
