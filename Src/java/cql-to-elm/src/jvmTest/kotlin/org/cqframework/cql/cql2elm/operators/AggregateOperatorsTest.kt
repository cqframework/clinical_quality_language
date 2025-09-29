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
import org.cqframework.cql.cql2elm.matchers.ConvertsToDecimalFrom
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.ListOfLiterals.Companion.listOfLiterals
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.AllTrue
import org.hl7.elm.r1.AnyTrue
import org.hl7.elm.r1.Avg
import org.hl7.elm.r1.Count
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.GeometricMean
import org.hl7.elm.r1.List
import org.hl7.elm.r1.Max
import org.hl7.elm.r1.Median
import org.hl7.elm.r1.Min
import org.hl7.elm.r1.Mode
import org.hl7.elm.r1.PopulationStdDev
import org.hl7.elm.r1.PopulationVariance
import org.hl7.elm.r1.Product
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.SortClause
import org.hl7.elm.r1.StdDev
import org.hl7.elm.r1.Sum
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.ToDecimal
import org.hl7.elm.r1.Variance
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class AggregateOperatorsTest {
    @Test
    fun allTrue() {
        val def: ExpressionDef = defs!!["AllTrueExpression"]!!
        assertThat(def, hasTypeAndResult(AllTrue::class.java, "System.Boolean"))

        val exp = def.expression as AllTrue?
        assertThat<Expression?>(exp!!.source, listOfLiterals(true, true, true))
    }

    @Test
    fun anyTrue() {
        val def: ExpressionDef = defs!!["AnyTrueExpression"]!!
        assertThat(def, hasTypeAndResult(AnyTrue::class.java, "System.Boolean"))

        val exp = def.expression as AnyTrue?
        assertThat<Expression?>(exp!!.source, listOfLiterals(false, true, false))
    }

    @Test
    fun average() {
        var def: ExpressionDef = defs!!["IntegerAvg"]!!
        assertThat(def, hasTypeAndResult(Avg::class.java, "System.Decimal"))
        assertDecimalConversionForIntegerListOneToFive((def.expression as Avg).source)

        def = defs!!["DecimalAvg"]!!
        assertThat(def, hasTypeAndResult(Avg::class.java, "System.Decimal"))
        assertThat<Expression?>((def.expression as Avg).source, listOfLiterals(1.0, 2.0, 3.0, 4.0))

        def = defs!!["QuantityAvg"]!!
        assertThat(def, hasTypeAndResult(Avg::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as Avg).source)
    }

    @Test
    fun count() {
        val def: ExpressionDef = defs!!["CountExpression"]!!
        assertThat(def, hasTypeAndResult(Count::class.java, "System.Integer"))
        assertThat<Expression?>((def.expression as Count).source, listOfLiterals(1, 2, 3, 4, 5))
    }

    @Test
    fun geometricMean() {
        val def: ExpressionDef = defs!!["GeometricMeanExpression"]!!
        assertThat(def, hasTypeAndResult(GeometricMean::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as GeometricMean).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0, 5.0),
        )
    }

    @Test
    fun max() {
        var def: ExpressionDef = defs!!["IntegerMax"]!!
        assertThat(def, hasTypeAndResult(Max::class.java, "System.Integer"))
        assertThat<Expression?>((def.expression as Max).source, listOfLiterals(1, 2, 3, 4, 5))

        def = defs!!["DecimalMax"]!!
        assertThat(def, hasTypeAndResult(Max::class.java, "System.Decimal"))
        assertThat<Expression?>((def.expression as Max).source, listOfLiterals(1.0, 2.0, 3.0, 4.0))

        def = defs!!["QuantityMax"]!!
        assertThat(def, hasTypeAndResult(Max::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as Max).source)

        def = defs!!["DateTimeMax"]!!
        assertThat(def, hasTypeAndResult(Max::class.java, "System.DateTime"))
        assertDateTimeListJanOne2012to2015((def.expression as Max).source)

        def = defs!!["TimeMax"]!!
        assertThat(def, hasTypeAndResult(Max::class.java, "System.Time"))
        assertTime0to18everySixHours((def.expression as Max).source)

        def = defs!!["StringMax"]!!
        assertThat(def, hasTypeAndResult(Max::class.java, "System.String"))
        assertThat<Expression?>(
            (def.expression as Max).source,
            listOfLiterals("a", "b", "c", "d", "e"),
        )
    }

    @Test
    fun min() {
        var def: ExpressionDef = defs!!["IntegerMin"]!!
        assertThat(def, hasTypeAndResult(Min::class.java, "System.Integer"))
        assertThat<Expression?>((def.expression as Min).source, listOfLiterals(1, 2, 3, 4, 5))

        def = defs!!["DecimalMin"]!!
        assertThat(def, hasTypeAndResult(Min::class.java, "System.Decimal"))
        assertThat<Expression?>((def.expression as Min).source, listOfLiterals(1.0, 2.0, 3.0, 4.0))

        def = defs!!["QuantityMin"]!!
        assertThat(def, hasTypeAndResult(Min::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as Min).source)

        def = defs!!["DateTimeMin"]!!
        assertThat(def, hasTypeAndResult(Min::class.java, "System.DateTime"))
        assertDateTimeListJanOne2012to2015((def.expression as Min).source)

        def = defs!!["TimeMin"]!!
        assertThat(def, hasTypeAndResult(Min::class.java, "System.Time"))
        assertTime0to18everySixHours((def.expression as Min).source)

        def = defs!!["StringMin"]!!
        assertThat(def, hasTypeAndResult(Min::class.java, "System.String"))
        assertThat<Expression?>(
            (def.expression as Min).source,
            listOfLiterals("a", "b", "c", "d", "e"),
        )
    }

    @Test
    fun median() {
        var def: ExpressionDef = defs!!["IntegerMedian"]!!
        assertThat(def, hasTypeAndResult(Median::class.java, "System.Decimal"))
        assertDecimalConversionForIntegerListOneToFive((def.expression as Median).source)

        def = defs!!["DecimalMedian"]!!
        assertThat(def, hasTypeAndResult(Median::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as Median).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0),
        )

        def = defs!!["QuantityMedian"]!!
        assertThat(def, hasTypeAndResult(Median::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as Median).source)
    }

    @Test
    fun mode() {
        var def: ExpressionDef = defs!!["IntegerMode"]!!
        assertThat(def, hasTypeAndResult(Mode::class.java, "System.Integer"))
        assertThat<Expression?>((def.expression as Mode).source, listOfLiterals(1, 2, 3, 4, 5))

        def = defs!!["DecimalMode"]!!
        assertThat(def, hasTypeAndResult(Mode::class.java, "System.Decimal"))
        assertThat<Expression?>((def.expression as Mode).source, listOfLiterals(1.0, 2.0, 3.0, 4.0))
    }

    @Test
    fun populationStdDev() {
        var def: ExpressionDef = defs!!["IntegerPopulationStdDev"]!!
        assertThat(def, hasTypeAndResult(PopulationStdDev::class.java, "System.Decimal"))
        assertDecimalConversionForIntegerListOneToFive((def.expression as PopulationStdDev).source)

        def = defs!!["DecimalPopulationStdDev"]!!
        assertThat(def, hasTypeAndResult(PopulationStdDev::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as PopulationStdDev).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0),
        )

        def = defs!!["QuantityPopulationStdDev"]!!
        assertThat(def, hasTypeAndResult(PopulationStdDev::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as PopulationStdDev).source)
    }

    @Test
    fun populationVariance() {
        var def: ExpressionDef = defs!!["IntegerPopulationVariance"]!!
        assertThat(def, hasTypeAndResult(PopulationVariance::class.java, "System.Decimal"))
        assertDecimalConversionForIntegerListOneToFive(
            (def.expression as PopulationVariance).source
        )

        def = defs!!["DecimalPopulationVariance"]!!
        assertThat(def, hasTypeAndResult(PopulationVariance::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as PopulationVariance).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0),
        )

        def = defs!!["QuantityPopulationVariance"]!!
        assertThat(def, hasTypeAndResult(PopulationVariance::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as PopulationVariance).source)
    }

    @Test
    fun product() {
        var def: ExpressionDef = defs!!["IntegerProduct"]!!
        assertThat(def, hasTypeAndResult(Product::class.java, "System.Integer"))
        assertThat<Expression?>((def.expression as Product).source, listOfLiterals(1, 2, 3, 4, 5))

        def = defs!!["DecimalProduct"]!!
        assertThat(def, hasTypeAndResult(Product::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as Product).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0, 5.0),
        )
    }

    @Test
    fun stdDev() {
        var def: ExpressionDef = defs!!["IntegerStdDev"]!!
        assertThat(def, hasTypeAndResult(StdDev::class.java, "System.Decimal"))
        assertDecimalConversionForIntegerListOneToFive((def.expression as StdDev).source)

        def = defs!!["DecimalStdDev"]!!
        assertThat(def, hasTypeAndResult(StdDev::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as StdDev).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0),
        )

        def = defs!!["QuantityStdDev"]!!
        assertThat(def, hasTypeAndResult(StdDev::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as StdDev).source)
    }

    @Test
    fun sum() {
        var def: ExpressionDef = defs!!["IntegerSum"]!!
        assertThat(def, hasTypeAndResult(Sum::class.java, "System.Integer"))
        assertThat<Expression?>((def.expression as Sum).source, listOfLiterals(1, 2, 3, 4, 5))

        def = defs!!["DecimalSum"]!!
        assertThat(def, hasTypeAndResult(Sum::class.java, "System.Decimal"))
        assertThat<Expression?>((def.expression as Sum).source, listOfLiterals(1.0, 2.0, 3.0, 4.0))

        def = defs!!["QuantitySum"]!!
        assertThat(def, hasTypeAndResult(Sum::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as Sum).source)
    }

    @Test
    fun variance() {
        var def: ExpressionDef = defs!!["IntegerVariance"]!!
        assertThat(def, hasTypeAndResult(Variance::class.java, "System.Decimal"))
        assertDecimalConversionForIntegerListOneToFive((def.expression as Variance).source)

        def = defs!!["DecimalVariance"]!!
        assertThat(def, hasTypeAndResult(Variance::class.java, "System.Decimal"))
        assertThat<Expression?>(
            (def.expression as Variance).source,
            listOfLiterals(1.0, 2.0, 3.0, 4.0),
        )

        def = defs!!["QuantityVariance"]!!
        assertThat(def, hasTypeAndResult(Variance::class.java, "System.Quantity"))
        assertQuantityListOneMeterToFourMeters((def.expression as Variance).source)
    }

    private fun assertDecimalConversionForIntegerListOneToFive(source: Expression?) {
        assertThat<Expression?>(source, Matchers.instanceOf<Expression?>(Query::class.java))

        val q = source as Query
        assertThat<MutableCollection<*>?>(q.source, Matchers.hasSize<Any?>(1))
        assertThat<MutableCollection<*>?>(q.let, Matchers.hasSize<Any?>(0))
        assertThat<MutableCollection<*>?>(q.relationship, Matchers.hasSize<Any?>(0))
        assertThat<SortClause?>(q.sort, Matchers.nullValue())
        assertThat<Expression?>(q.where, Matchers.nullValue())
        val aqs = q.source[0]
        assertThat<Expression?>(aqs.expression, listOfLiterals(1, 2, 3, 4, 5))
        val alias = aqs.alias
        assertThat<Boolean?>(q.`return`!!.isDistinct(), `is`<Boolean?>(false))
        assertThat<Expression?>(
            q.`return`!!.expression,
            Matchers.instanceOf<Expression?>(ToDecimal::class.java),
        )
        assertThat<Expression?>(
            q.`return`!!.expression,
            ConvertsToDecimalFrom.convertsToDecimalFromAlias(alias!!),
        )
    }

    private fun assertQuantityListOneMeterToFourMeters(source: Expression?) {
        assertThat<Expression?>(source, Matchers.instanceOf<Expression?>(List::class.java))

        val args = source as List
        assertThat<MutableCollection<*>?>(args.element, Matchers.hasSize<Any?>(4))
        var i = 1
        for (arg in args.element) {
            assertThat<Expression?>(arg, Matchers.instanceOf<Expression?>(Quantity::class.java))
            val q = arg as Quantity
            assertThat(q.value!!.intValueExact(), `is`(i++))
            assertThat<String?>(q.unit, `is`<String?>("m"))
        }
    }

    private fun assertDateTimeListJanOne2012to2015(source: Expression?) {
        assertThat<Expression?>(source, Matchers.instanceOf<Expression?>(List::class.java))

        val args = source as List
        assertThat<MutableCollection<*>?>(args.element, Matchers.hasSize<Any?>(4))
        var i = 2012
        for (arg in args.element) {
            assertThat<Expression?>(arg, Matchers.instanceOf<Expression?>(DateTime::class.java))
            val d = arg as DateTime
            assertThat<Expression?>(d.year, literalFor(i++))
            assertThat<Expression?>(d.month, literalFor(1))
            assertThat<Expression?>(d.day, literalFor(1))
            assertThat<Expression?>(d.hour, literalFor(0))
            assertThat<Expression?>(d.minute, literalFor(0))
            assertThat<Expression?>(d.second, literalFor(0))
            assertThat<Expression?>(d.millisecond, Matchers.nullValue())
        }
    }

    private fun assertTime0to18everySixHours(source: Expression?) {
        assertThat<Expression?>(source, Matchers.instanceOf<Expression?>(List::class.java))

        val args = source as List
        assertThat<MutableCollection<*>?>(args.element, Matchers.hasSize<Any?>(4))
        var i = 0
        for (arg in args.element) {
            assertThat<Expression?>(arg, Matchers.instanceOf<Expression?>(Time::class.java))
            val t = arg as Time
            assertThat<Expression?>(t.hour, literalFor(i))
            assertThat<Expression?>(t.minute, literalFor(0))
            assertThat<Expression?>(t.second, literalFor(0))
            assertThat<Expression?>(t.millisecond, Matchers.nullValue())
            i += 6
        }
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
                    AggregateOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/AggregateOperators.cql")!!
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
            if (library!!.statements != null) {
                for (def in library.statements!!.def) {
                    defs!![def.name] = def
                }
            }
        }
    }
}
