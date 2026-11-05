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
import org.hl7.elm.r1.Union
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/** Created by Bryn on 12/30/2016. */
internal class CqlListOperatorsTest {
    @Test
    fun union() {
        val def: ExpressionDef? = defs!!["Union123AndEmpty"]
        assertThat<ExpressionDef?>(def, hasTypeAndResult(Union::class.java, "list<System.Integer>"))
    }

    companion object {
        // NOTE: The CQL for this test is taken from an engine testing suite that produced a
        // particular issue with ambiguous
        // conversions.
        private var defs: MutableMap<String?, ExpressionDef?>? = null

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setup() {
            val modelManager = ModelManager()
            val translator =
                fromSource(
                    CqlListOperatorsTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/CqlListOperators.cql")!!
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
