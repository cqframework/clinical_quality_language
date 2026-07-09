package org.cqframework.cql.cql2elm

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.And
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.Less
import org.hl7.elm.r1.LessOrEqual
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Regression test for a bug where `properly between` was treated identically to `between`. The
 * legacy translator checked `ctx.getChild(0).text == "properly"` but child 0 is the expression, not
 * the keyword — so `isProper` was always false.
 *
 * `between` should emit `And(GreaterOrEqual, LessOrEqual)`. `properly between` should emit
 * `And(Greater, Less)`.
 */
internal class ProperlyBetweenTest {

    @Test
    fun betweenUsesGreaterOrEqual() {
        val def = defs!!["TestBetween"]!!
        val and = def.expression as And
        assertThat(and.operand[0], `is`(instanceOf(GreaterOrEqual::class.java)))
        assertThat(and.operand[1], `is`(instanceOf(LessOrEqual::class.java)))
    }

    @Test
    fun properlyBetweenUsesGreater() {
        val def = defs!!["TestProperlyBetween"]!!
        val and = def.expression as And
        assertThat(and.operand[0], `is`(instanceOf(Greater::class.java)))
        assertThat(and.operand[1], `is`(instanceOf(Less::class.java)))
    }

    companion object {
        private var defs: Map<String?, ExpressionDef?>? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            val modelManager = ModelManager()
            val libraryManager = LibraryManager(modelManager)
            val translator =
                CqlTranslator.fromSource(
                    ProperlyBetweenTest::class
                        .java
                        .getResourceAsStream("ProperlyBetweenTest.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager,
                )
            defs = translator.toELM()!!.statements!!.def.associateBy { it.name }
        }
    }
}
