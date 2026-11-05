package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Overlaps
import org.hl7.elm.r1.OverlapsAfter
import org.hl7.elm.r1.OverlapsBefore
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/** Created by Bryn on 12/30/2016. */
internal class CqlIntervalOperatorsTest {
    // Ignored, see comment in test source
    //    @Test
    //    public void testAfter() {
    //        ExpressionDef def = defs.get("TestAfterNull");
    //        assertThat(def, hasTypeAndResult(After.class, "System.Boolean"));
    //    }
    @Test
    fun overlapsDay() {
        val def: ExpressionDef? = defs!!["TestOverlapsDay"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Overlaps::class.java, "System.Boolean"))
    }

    @Test
    fun overlapsDayBefore() {
        val def: ExpressionDef? = defs!!["TestOverlapsDayBefore"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(OverlapsBefore::class.java, "System.Boolean"),
        )
    }

    @Test
    fun overlapsDayAfter() {
        val def: ExpressionDef? = defs!!["TestOverlapsDayAfter"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(OverlapsAfter::class.java, "System.Boolean"),
        )
    }

    companion object {
        // NOTE: The CQL for this test is taken from an engine testing suite that produced a
        // particular issue with generic
        // instantiations.
        // This library will not translate successfully without the proper fix in place.
        // So this test only needs to validate that the library translates successfully.
        private var defs: MutableMap<String?, ExpressionDef?>? = null

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setup() {
            val modelManager = ModelManager()
            val translator =
                fromSource(
                    CqlIntervalOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/CqlIntervalOperators.cql")!!
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
