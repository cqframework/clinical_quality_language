package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.TimeOfDay
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class TimeOperatorsTest {
    @Test
    fun time() {
        var def: ExpressionDef = defs!!["TimeHour"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        var t = def.expression as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))

        def = defs!!["TimeMinute"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        t = def.expression as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))
        assertThat<Expression?>(t.minute, literalFor(0))

        def = defs!!["TimeSecond"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        t = def.expression as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))
        assertThat<Expression?>(t.minute, literalFor(0))
        assertThat<Expression?>(t.second, literalFor(0))

        def = defs!!["TimeMillisecond"]!!
        assertThat(def, hasTypeAndResult(Time::class.java, "System.Time"))
        t = def.expression as Time?
        assertThat<Expression?>(t!!.hour, literalFor(0))
        assertThat<Expression?>(t.minute, literalFor(0))
        assertThat<Expression?>(t.second, literalFor(0))
        assertThat<Expression?>(t.millisecond, literalFor(0))
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
                    TimeOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/TimeOperators.cql")!!
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
    }
}
