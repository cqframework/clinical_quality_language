package org.cqframework.cql.cql2elm

import kotlin.test.Test
import kotlin.test.assertIs
import org.cqframework.cql.shared.TestResource
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm.r1.And
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.Less
import org.hl7.elm.r1.LessOrEqual

/**
 * Regression test for a bug where `properly between` was treated identically to `between`. The
 * legacy translator checked `ctx.getChild(0).text == "properly"` but child 0 is the expression, not
 * the keyword — so `isProper` was always false.
 *
 * `between` should emit `And(GreaterOrEqual, LessOrEqual)`. `properly between` should emit
 * `And(Greater, Less)`.
 */
class ProperlyBetweenTest {
    val defs = let {
        val modelManager =
            ModelManager().apply {
                modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            }
        val libraryManager = LibraryManager(modelManager)
        val translator =
            CqlTranslator.fromText(
                TestResource("org/cqframework/cql/cql2elm/ProperlyBetweenTest.cql").readText(),
                libraryManager,
            )
        translator.toELM()!!.statements!!.def.associateBy { it.name }
    }

    @Test
    fun betweenUsesGreaterOrEqual() {
        val def = defs["TestBetween"]!!
        val and = def.expression as And
        assertIs<GreaterOrEqual>(and.operand[0])
        assertIs<LessOrEqual>(and.operand[1])
    }

    @Test
    fun properlyBetweenUsesGreater() {
        val def = defs["TestProperlyBetween"]!!
        val and = def.expression as And
        assertIs<Greater>(and.operand[0])
        assertIs<Less>(and.operand[1])
    }
}
