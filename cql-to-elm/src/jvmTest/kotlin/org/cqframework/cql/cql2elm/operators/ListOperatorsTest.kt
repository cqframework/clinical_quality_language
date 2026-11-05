package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.cqframework.cql.cql2elm.matchers.ListOfLiterals.Companion.listOfLiterals
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.First
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.IndexOf
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.List
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Slice
import org.hl7.elm.r1.Union
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ListOperatorsTest {
    @Test
    fun indexOf() {
        val def: ExpressionDef = defs!!["ListIndexOf"]!!
        assertThat(def, hasTypeAndResult(IndexOf::class.java, "System.Integer"))

        val indexOf = def.expression as IndexOf?
        assertThat<Expression?>(indexOf!!.source, listOfLiterals(1, 2, 3))
        assertThat<Expression?>(indexOf.element, literalFor(2))
    }

    @Test
    fun first() {
        val def: ExpressionDef = defs!!["ListFirst"]!!
        assertThat(def, hasTypeAndResult(First::class.java, "System.Integer"))

        val first = def.expression as First?
        assertThat<Expression?>(first!!.source, listOfLiterals(1, 2, 3, 4, 5))
    }

    @Test
    fun last() {
        val def: ExpressionDef = defs!!["ListLast"]!!
        assertThat(def, hasTypeAndResult(Last::class.java, "System.Integer"))

        val last = def.expression as Last?
        assertThat<Expression?>(last!!.source, listOfLiterals(1, 2, 3))
    }

    @Test
    fun skip() {
        val def: ExpressionDef = defs!!["ListSkip"]!!
        assertThat(def, hasTypeAndResult(Slice::class.java, "list<System.Integer>"))

        val slice = def.expression as Slice?
        assertThat<Expression?>(slice!!.source, listOfLiterals(1, 2, 3))
        assertThat<Expression?>(slice.startIndex, literalFor(1))
        assertThat<Expression?>(slice.endIndex, Matchers.instanceOf<Expression?>(Null::class.java))
    }

    @Test
    fun tail() {
        val def: ExpressionDef = defs!!["ListTail"]!!
        assertThat(def, hasTypeAndResult(Slice::class.java, "list<System.Integer>"))

        val slice = def.expression as Slice?
        assertThat<Expression?>(slice!!.source, listOfLiterals(1, 2, 3))
        assertThat<Expression?>(slice.startIndex, literalFor(1))
        assertThat<Expression?>(slice.endIndex, Matchers.instanceOf<Expression?>(Null::class.java))
    }

    @Test
    fun take() {
        val def: ExpressionDef = defs!!["ListTake"]!!
        assertThat(def, hasTypeAndResult(Slice::class.java, "list<System.Integer>"))

        val slice = def.expression as Slice?
        assertThat<Expression?>(slice!!.source, listOfLiterals(1, 2, 3))
        assertThat<Expression?>(slice.startIndex, literalFor(0))
        val coalesce = slice.endIndex as Coalesce?
        assertThat(coalesce!!.operand.size, `is`(2))
        assertThat(coalesce.operand[0], literalFor(1))
        assertThat(coalesce.operand[1], literalFor(0))
    }

    @Test
    fun flatten() {
        val def: ExpressionDef = defs!!["ListFlatten"]!!
        val flatten = def.expression as Flatten?
        assertThat(flatten!!.operand is List, `is`(true))

        val defFlatten: ExpressionDef = defs!!["Flatten Lists and Elements"]!!
        val flatten2 = defFlatten.expression as Flatten?
        assertThat(flatten2!!.operand is List, `is`(true))
    }

    @Test
    fun length() {
        val def: ExpressionDef = defs!!["ListLength"]!!
        assertThat(def, hasTypeAndResult(Length::class.java, "System.Integer"))

        val length = def.expression as Length?
        assertThat<Expression?>(length!!.operand, listOfLiterals(1, 2, 3, 4, 5))
    }

    @Test
    fun choiceType() {
        val def: ExpressionDef? = defs!!["ListUnionWithChoice"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Union::class.java, "list<choice<System.Integer,System.String>>"),
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
                    ListOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/ListOperators.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager, CqlCompilerOptions()),
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
