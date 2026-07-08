package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.junit.jupiter.api.BeforeAll

/** Created by Bryn on 12/30/2016. */
internal object CqlComparisonOperatorsTest {
    // NOTE: The CQL for this test is taken from an engine testing suite that produced a particular
    // issue with the
    // operatorMap. This library will not translate successfully without the proper fix in place.
    // So this test only needs to validate that the library translates successfully.
    private var defs: MutableMap<String?, ExpressionDef?>? = null

    @JvmStatic
    @BeforeAll
    @Throws(IOException::class)
    fun setup() {
        val modelManager = ModelManager()
        val translator =
            fromSource(
                CqlComparisonOperatorsTest::class
                    .java
                    .getResourceAsStream("../OperatorTests/CqlComparisonOperators.cql")!!
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
    } // @Test
    // public void testEqual() {
    //    ExpressionDef def = defs.get("SimpleEqNullNull");
    //    assertThat(def, hasTypeAndResult(Equal.class, "System.Boolean"));
    // }
}
