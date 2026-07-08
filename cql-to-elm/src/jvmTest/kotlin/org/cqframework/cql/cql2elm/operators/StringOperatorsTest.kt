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
import org.cqframework.cql.cql2elm.matchers.ListOfLiterals.Companion.listOfLiterals
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.EndsWith
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.LastPositionOf
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.Lower
import org.hl7.elm.r1.Matches
import org.hl7.elm.r1.PositionOf
import org.hl7.elm.r1.ReplaceMatches
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.StartsWith
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.Upper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class StringOperatorsTest {
    @Test
    fun add() {
        val def: ExpressionDef? = defs!!["StringAdd"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Concatenate::class.java, "System.String"))
    }

    @Test
    fun concatenate() {
        val def: ExpressionDef? = defs!!["StringConcatenate"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Concatenate::class.java, "System.String"))
    }

    @Test
    fun concatenateWithAmpersand() {
        val def: ExpressionDef = defs!!["StringConcatenateWithAmpersand"]!!
        assertThat(def, hasTypeAndResult(Concatenate::class.java, "System.String"))

        val concatenate = def.expression as Concatenate?
        for (operand in concatenate!!.operand) {
            assertThat(operand.javaClass === Coalesce::class.java, `is`(true))
        }
    }

    @Test
    fun combine() {
        var def: ExpressionDef = defs!!["StringCombine"]!!
        assertThat(def, hasTypeAndResult(Combine::class.java, "System.String"))
        var combine = def.expression as Combine?
        assertThat<Expression?>(
            combine!!.source,
            listOfLiterals("First", "Second", "Third", "Fourth"),
        )
        assertThat<Expression?>(combine.separator, literalFor(","))

        def = defs!!["StringCombineNoSeparator"]!!
        assertThat(def, hasTypeAndResult(Combine::class.java, "System.String"))
        combine = def.expression as Combine?
        assertThat<Expression?>(combine!!.source, listOfLiterals("abc", "def", "ghi", "jkl"))
        assertThat<Expression?>(combine.separator, Matchers.nullValue())
    }

    @Test
    fun split() {
        val def: ExpressionDef = defs!!["StringSplit"]!!
        assertThat(def, hasTypeAndResult(Split::class.java, "list<System.String>"))
        val split = def.expression as Split?
        assertThat<Expression?>(split!!.stringToSplit, literalFor("First,Second,Third,Fourth"))
        assertThat<Expression?>(split.separator, literalFor(","))
    }

    @Test
    fun splitOnMatches() {
        val def: ExpressionDef = defs!!["StringSplitOnMatches"]!!
        assertThat(def, hasTypeAndResult(SplitOnMatches::class.java, "list<System.String>"))
        val splitOnMatches = def.expression as SplitOnMatches?
        assertThat<Expression?>(
            splitOnMatches!!.stringToSplit,
            literalFor("First,Second,Third,Fourth"),
        )
        assertThat<Expression?>(splitOnMatches.separatorPattern, literalFor(","))
    }

    @Test
    fun upper() {
        val def: ExpressionDef = defs!!["StringUpper"]!!
        assertThat(def, hasTypeAndResult(Upper::class.java, "System.String"))
        val upper = def.expression as Upper?
        assertThat<Expression?>(upper!!.operand, literalFor("John"))
    }

    @Test
    fun lower() {
        val def: ExpressionDef = defs!!["StringLower"]!!
        assertThat(def, hasTypeAndResult(Lower::class.java, "System.String"))
        val lower = def.expression as Lower?
        assertThat<Expression?>(lower!!.operand, literalFor("John"))
    }

    @Test
    fun positionOf() {
        val def: ExpressionDef = defs!!["StringPositionOf"]!!
        assertThat(def, hasTypeAndResult(PositionOf::class.java, "System.Integer"))
        val positionOf = def.expression as PositionOf?
        assertThat<Expression?>(positionOf!!.pattern, literalFor("J"))
        assertThat<Expression?>(positionOf.string, literalFor("John"))
    }

    @Test
    fun lastPositionOf() {
        val def: ExpressionDef = defs!!["StringLastPositionOf"]!!
        assertThat(def, hasTypeAndResult(LastPositionOf::class.java, "System.Integer"))
        val lastPositionOf = def.expression as LastPositionOf?
        assertThat<Expression?>(lastPositionOf!!.pattern, literalFor("J"))
        assertThat<Expression?>(lastPositionOf.string, literalFor("John"))
    }

    @Test
    fun substring() {
        var def: ExpressionDef = defs!!["StringSubstring"]!!
        assertThat(def, hasTypeAndResult(Substring::class.java, "System.String"))
        var substring = def.expression as Substring?
        // Note: these casts to Expression are necessary because of bug in expression.xsd (DSTU
        // comment #824)
        assertThat<Expression?>(substring!!.stringToSub, literalFor("JohnDoe"))
        assertThat<Expression?>(substring.startIndex, literalFor(5))

        def = defs!!["StringSubstringWithLength"]!!
        assertThat(def, hasTypeAndResult(Substring::class.java, "System.String"))
        substring = def.expression as Substring?
        assertThat<Expression?>(substring!!.stringToSub, literalFor("JohnDoe"))
        assertThat<Expression?>(substring.startIndex, literalFor(1))
        assertThat<Expression?>(substring.length, literalFor(4))
    }

    @Test
    fun length() {
        val def: ExpressionDef = defs!!["StringLength"]!!
        assertThat(def, hasTypeAndResult(Length::class.java, "System.Integer"))

        val length = def.expression as Length?
        assertThat<Expression?>(length!!.operand, literalFor("John"))
    }

    @Test
    fun startsWith() {
        val def: ExpressionDef? = defs!!["StringStartsWith"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(StartsWith::class.java, "System.Boolean"))
    }

    @Test
    fun endsWith() {
        val def: ExpressionDef? = defs!!["StringEndsWith"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(EndsWith::class.java, "System.Boolean"))
    }

    @Test
    fun matches() {
        val def: ExpressionDef? = defs!!["StringMatches"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Matches::class.java, "System.Boolean"))
    }

    @Test
    fun replaceMatches() {
        val def: ExpressionDef? = defs!!["StringReplaceMatches"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(ReplaceMatches::class.java, "System.String"),
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
                    StringOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/StringOperators.cql")!!
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
