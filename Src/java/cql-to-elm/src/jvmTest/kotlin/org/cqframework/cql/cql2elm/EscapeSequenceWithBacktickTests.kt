package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Literal
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("LongMethod", "MaxLineLength")
internal class EscapeSequenceWithBacktickTests {
    @Test
    fun identifier() {
        var def: ExpressionDef = defs[""]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        var literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["Hello 'World'"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["Hello \"World\""]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["Hello `World`"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["Hello 'World'2"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["Hello \"World\"2"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\n"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\u000c"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\r"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\t"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["/"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\\"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\u000c\n\r\t/\\"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def = defs["\u0020"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))

        def =
            defs[
                "This is an identifier with \"multiple\" embedded \t escapes\u0020\r\nno really, \r\n\u000c\t/\\lots of them"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Literal::class.java, "System.String"))

        literal = def.expression as Literal?
        assertThat(literal!!.value, `is`(def.name))
    }

    companion object {
        private var defs: MutableMap<String, ExpressionDef> = HashMap()

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setup() {
            val modelManager = ModelManager()
            val libraryManager = LibraryManager(modelManager)
            val translator =
                CqlTranslator.fromSource(
                    EscapeSequenceTests::class
                        .java
                        .getResourceAsStream("EscapeSequenceWithBacktickTests.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager
                )
            assertThat(translator.errors.size, `is`(0))
            val library = translator.toELM()
            for (def in library!!.statements!!.def) {
                defs[def.name!!] = def
            }
        }
    }
}
