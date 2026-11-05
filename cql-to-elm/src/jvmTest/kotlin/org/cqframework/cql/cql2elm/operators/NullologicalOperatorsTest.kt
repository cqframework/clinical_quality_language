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
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.List
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class NullologicalOperatorsTest {
    @Test
    fun isNull() {
        val def: ExpressionDef = defs!!["IsNullExpression"]!!
        assertThat(def, hasTypeAndResult(IsNull::class.java, "System.Boolean"))

        val isNull = def.expression as IsNull?
        assertThat<Expression?>(isNull!!.operand, literalFor(1))
    }

    @Test
    fun coalesce() {
        var def: ExpressionDef = defs!!["CoalesceList"]!!
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Coalesce::class.java, "System.Integer"))

        val coalesce = def.expression as Coalesce?
        assertThat<MutableCollection<*>?>(coalesce!!.operand, Matchers.hasSize<Any?>(1))
        assertThat(coalesce.operand[0], Matchers.instanceOf(List::class.java))
        val args = coalesce.operand[0] as List
        assertThat<MutableCollection<*>?>(args.element, Matchers.hasSize<Any?>(5))
        var i = 1
        for (arg in args.element) {
            assertThat<Expression?>(arg, literalFor(i++))
        }

        def = defs!!["CoalesceTwoArgument"]!!
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Coalesce::class.java, "System.Integer"))
        assertIntegerArgs((def.expression as Coalesce?)!!, 1, 2)

        def = defs!!["CoalesceThreeArgument"]!!
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Coalesce::class.java, "System.Integer"))
        assertIntegerArgs((def.expression as Coalesce?)!!, 1, 2, 3)

        def = defs!!["CoalesceFourArgument"]!!
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Coalesce::class.java, "System.Integer"))
        assertIntegerArgs((def.expression as Coalesce?)!!, 1, 2, 3, 4)

        def = defs!!["CoalesceFiveArgument"]!!
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Coalesce::class.java, "System.Integer"))
        assertIntegerArgs((def.expression as Coalesce?)!!, 1, 2, 3, 4, 5)
    }

    private fun assertIntegerArgs(coalesce: Coalesce, vararg ints: Int?) {
        assertThat<MutableCollection<*>?>(coalesce.operand, Matchers.hasSize<Any?>(ints.size))
        for (i in ints.indices) {
            assertThat(coalesce.operand[i], literalFor(ints[i]!!))
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
                    NullologicalOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/NullologicalOperators.cql")!!
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
