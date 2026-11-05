package org.cqframework.cql.cql2elm.who

import java.io.IOException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.TestUtils
import org.hl7.elm.r1.ExpressionDef
import org.junit.jupiter.api.Test

internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun who() {
        val options = CqlCompilerOptions.defaultOptions()
        val translator = TestUtils.runSemanticTest("who/TestSignature.cql", 0, options)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }
    }
}
