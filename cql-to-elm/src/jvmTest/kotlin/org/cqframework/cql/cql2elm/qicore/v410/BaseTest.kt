package org.cqframework.cql.cql2elm.qicore.v410

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Retrieve
import org.junit.jupiter.api.Test

internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun qICore() {
        val translator = TestUtils.runSemanticTest("qicore/v410/TestQICore.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def: ExpressionDef = defs["TestAdverseEvent"]!!
        assertThat<Expression?>(
            def.expression,
            Matchers.instanceOf<Expression?>(Retrieve::class.java),
        )
        val retrieve = def.expression as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            Matchers.`is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-adverseevent"
            ),
        )
    }

    @Test
    @Throws(IOException::class)
    fun exm124() {
        val translator = TestUtils.runSemanticTest("qicore/v410/EXM124_QICore4-8.2.000.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Initial Population"]
    }

    @Test
    @Throws(IOException::class)
    fun exm165() {
        val translator = TestUtils.runSemanticTest("qicore/v410/EXM165_QICore4-8.5.000.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Initial Population"]
    }
}
