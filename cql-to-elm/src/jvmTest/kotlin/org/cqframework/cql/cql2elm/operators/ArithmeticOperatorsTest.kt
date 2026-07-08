package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import java.math.BigDecimal
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.ConvertsToDecimalFrom.Companion.convertsToDecimalFrom
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.Abs
import org.hl7.elm.r1.Ceiling
import org.hl7.elm.r1.Divide
import org.hl7.elm.r1.Exp
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Floor
import org.hl7.elm.r1.HighBoundary
import org.hl7.elm.r1.Ln
import org.hl7.elm.r1.Log
import org.hl7.elm.r1.LowBoundary
import org.hl7.elm.r1.Negate
import org.hl7.elm.r1.Precision
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Round
import org.hl7.elm.r1.Truncate
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ArithmeticOperatorsTest {
    @Test
    fun divide() {
        var def: ExpressionDef? = defs!!["IntegerDivide"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Divide::class.java, "System.Decimal"))

        def = defs!!["IntegerDivide10"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Divide::class.java, "System.Decimal"))

        def = defs!!["RealDivide"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Divide::class.java, "System.Decimal"))

        def = defs!!["QuantityRealDivide"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Divide::class.java, "System.Quantity"))

        def = defs!!["QuantityDivide"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Divide::class.java, "System.Quantity"))
    }

    @Test
    fun ceiling() {
        var def: ExpressionDef = defs!!["IntegerCeiling"]!!
        assertThat(def, hasTypeAndResult(Ceiling::class.java, "System.Integer"))

        var ceiling = def.expression as Ceiling?
        assertThat<Expression?>(ceiling!!.operand, convertsToDecimalFrom(1))

        def = defs!!["DecimalCeiling"]!!
        assertThat(def, hasTypeAndResult(Ceiling::class.java, "System.Integer"))

        ceiling = def.expression as Ceiling?
        assertThat<Expression?>(ceiling!!.operand, literalFor(1.0))
    }

    @Test
    fun floor() {
        var def: ExpressionDef = defs!!["IntegerFloor"]!!
        assertThat(def, hasTypeAndResult(Floor::class.java, "System.Integer"))

        var floor = def.expression as Floor?
        assertThat<Expression?>(floor!!.operand, convertsToDecimalFrom(1))

        def = defs!!["DecimalFloor"]!!
        assertThat(def, hasTypeAndResult(Floor::class.java, "System.Integer"))

        floor = def.expression as Floor?
        assertThat<Expression?>(floor!!.operand, literalFor(1.0))
    }

    @Test
    fun truncate() {
        var def: ExpressionDef = defs!!["IntegerTruncate"]!!
        assertThat(def, hasTypeAndResult(Truncate::class.java, "System.Integer"))

        var truncate = def.expression as Truncate?
        assertThat<Expression?>(truncate!!.operand, convertsToDecimalFrom(5))

        def = defs!!["DecimalTruncate"]!!
        assertThat(def, hasTypeAndResult(Truncate::class.java, "System.Integer"))

        truncate = def.expression as Truncate?
        assertThat<Expression?>(truncate!!.operand, literalFor(5.5))
    }

    @Test
    fun abs() {
        var def: ExpressionDef = defs!!["IntegerAbs"]!!
        assertThat(def, hasTypeAndResult(Abs::class.java, "System.Integer"))

        var abs = def.expression as Abs?
        assertThat<Expression?>(abs!!.operand, Matchers.instanceOf<Expression?>(Negate::class.java))
        assertThat<Expression?>((abs.operand as Negate).operand, literalFor(1))

        def = defs!!["DecimalAbs"]!!
        assertThat(def, hasTypeAndResult(Abs::class.java, "System.Decimal"))

        abs = def.expression as Abs?
        assertThat<Expression?>(abs!!.operand, Matchers.instanceOf<Expression?>(Negate::class.java))
        assertThat<Expression?>((abs.operand as Negate).operand, literalFor(1.0))

        def = defs!!["QuantityAbs"]!!
        assertThat(def, hasTypeAndResult(Abs::class.java, "System.Quantity"))

        abs = def.expression as Abs?
        assertThat<Expression?>(abs!!.operand, Matchers.instanceOf<Expression?>(Negate::class.java))
        val n = abs.operand as Negate?
        assertThat<Expression?>(n!!.operand, Matchers.instanceOf<Expression?>(Quantity::class.java))
        val q = n.operand as Quantity?
        assertThat<BigDecimal?>(q!!.value, `is`<BigDecimal?>(BigDecimal.valueOf(1.0)))
        assertThat<String?>(q.unit, `is`<String?>("cm"))
    }

    @Test
    fun log() {
        var def: ExpressionDef = defs!!["DecimalDecimalLog"]!!
        assertThat(def, hasTypeAndResult(Log::class.java, "System.Decimal"))

        var log = def.expression as Log?
        assertThat<MutableCollection<*>?>(log!!.operand, Matchers.hasSize<Any?>(2))
        assertThat(log.operand[0], literalFor(1000.0))
        assertThat(log.operand[1], literalFor(10.0))

        def = defs!!["DecimalIntegerLog"]!!
        assertThat(def, hasTypeAndResult(Log::class.java, "System.Decimal"))

        log = def.expression as Log?
        assertThat<MutableCollection<*>?>(log!!.operand, Matchers.hasSize<Any?>(2))
        assertThat(log.operand[0], literalFor(1000.0))
        assertThat(log.operand[1], convertsToDecimalFrom(10))

        def = defs!!["IntegerDecimalLog"]!!
        assertThat(def, hasTypeAndResult(Log::class.java, "System.Decimal"))

        log = def.expression as Log?
        assertThat<MutableCollection<*>?>(log!!.operand, Matchers.hasSize<Any?>(2))
        assertThat(log.operand[0], convertsToDecimalFrom(1000))
        assertThat(log.operand[1], literalFor(10.0))

        def = defs!!["IntegerIntegerLog"]!!
        assertThat(def, hasTypeAndResult(Log::class.java, "System.Decimal"))

        log = def.expression as Log?
        assertThat<MutableCollection<*>?>(log!!.operand, Matchers.hasSize<Any?>(2))
        assertThat(log.operand[0], convertsToDecimalFrom(1000))
        assertThat(log.operand[1], convertsToDecimalFrom(10))
    }

    @Test
    fun ln() {
        var def: ExpressionDef = defs!!["IntegerLn"]!!
        assertThat(def, hasTypeAndResult(Ln::class.java, "System.Decimal"))

        var ln = def.expression as Ln?
        assertThat<Expression?>(ln!!.operand, convertsToDecimalFrom(1000))

        def = defs!!["DecimalLn"]!!
        assertThat(def, hasTypeAndResult(Ln::class.java, "System.Decimal"))

        ln = def.expression as Ln?
        assertThat<Expression?>(ln!!.operand, literalFor(1000.0))
    }

    @Test
    fun exp() {
        var def: ExpressionDef = defs!!["IntegerExp"]!!
        assertThat(def, hasTypeAndResult(Exp::class.java, "System.Decimal"))

        var exp = def.expression as Exp?
        assertThat<Expression?>(exp!!.operand, convertsToDecimalFrom(1000))

        def = defs!!["DecimalExp"]!!
        assertThat(def, hasTypeAndResult(Exp::class.java, "System.Decimal"))

        exp = def.expression as Exp?
        assertThat<Expression?>(exp!!.operand, literalFor(1000.0))
    }

    @Test
    fun round() {
        var def: ExpressionDef = defs!!["DecimalRound"]!!
        assertThat(def, hasTypeAndResult(Round::class.java, "System.Decimal"))

        var round = def.expression as Round?
        assertThat<Expression?>(round!!.operand, literalFor(10.55))
        assertThat<Expression?>(round.precision, Matchers.nullValue())

        def = defs!!["DecimalRoundWithPrecision"]!!
        assertThat(def, hasTypeAndResult(Round::class.java, "System.Decimal"))

        round = def.expression as Round?
        assertThat<Expression?>(round!!.operand, literalFor(10.5555))
        assertThat<Expression?>(round.precision, literalFor(2))
    }

    @Test
    fun precision() {
        var def: ExpressionDef? = defs!!["DecimalPrecision"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Precision::class.java, "System.Integer"))

        def = defs!!["DatePrecision"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Precision::class.java, "System.Integer"))

        def = defs!!["DateTimePrecision"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Precision::class.java, "System.Integer"))

        def = defs!!["TimePrecision"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Precision::class.java, "System.Integer"))
    }

    @Test
    fun lowBoundary() {
        var def: ExpressionDef? = defs!!["DecimalLowBoundary"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(LowBoundary::class.java, "System.Decimal"))

        def = defs!!["DateLowBoundary"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(LowBoundary::class.java, "System.Date"))

        def = defs!!["DateTimeLowBoundary"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(LowBoundary::class.java, "System.DateTime"),
        )

        def = defs!!["TimeLowBoundary"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(LowBoundary::class.java, "System.Time"))
    }

    @Test
    fun highBoundary() {
        var def: ExpressionDef? = defs!!["DecimalHighBoundary"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(HighBoundary::class.java, "System.Decimal"),
        )

        def = defs!!["DateHighBoundary"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(HighBoundary::class.java, "System.Date"))

        def = defs!!["DateTimeHighBoundary"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(HighBoundary::class.java, "System.DateTime"),
        )

        def = defs!!["TimeHighBoundary"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(HighBoundary::class.java, "System.Time"))
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
                    ArithmeticOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/ArithmeticOperators.cql")!!
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
