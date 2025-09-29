package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlin.collections.set
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Library
import org.junit.jupiter.api.Test

internal class IncludedSignatureWithAliasOutputTests {
    @Throws(IOException::class)
    private fun getLibrary(signatureLevel: LibraryBuilder.SignatureLevel): Library {
        val translator: CqlTranslator = getTranslator(signatureLevel)
        MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(0))
        defs = HashMap()
        val library: Library = translator.toELM()!!
        if (library.statements != null) {
            for (def in library.statements!!.def) {
                defs!![def.name] = def
            }
        }
        return library
    }

    @Test
    @Throws(IOException::class)
    fun none() {
        val translator: CqlTranslator = getTranslator(LibraryBuilder.SignatureLevel.None)
        MatcherAssert.assertThat(translator.warnings.size, Matchers.greaterThan(1))
        MatcherAssert.assertThat(
            translator.warnings[0].message,
            @Suppress("MaxLineLength")
            Matchers.equalTo(
                "The function SignatureOutputTests.MultipleOverloadTest has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun differing() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.Differing)

        var def: ExpressionDef = defs!!["TestOverload"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadOneInt"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadOneDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadTwoInts"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadTwoDecimals"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadOneIntOneDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadOneIntTwoDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun overloads() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.Overloads)

        var def: ExpressionDef = defs!!["TestOverload"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadOneInt"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(1))

        def = defs!!["TestOverloadOneDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(1))

        def = defs!!["TestOverloadTwoInts"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadTwoDecimals"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadOneIntOneDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadOneIntTwoDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun all() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.All)

        var def: ExpressionDef = defs!!["TestOverload"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(0))

        def = defs!!["TestOverloadOneInt"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(1))

        def = defs!!["TestOverloadOneDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(1))

        def = defs!!["TestOverloadTwoInts"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadTwoDecimals"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadOneIntOneDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(2))

        def = defs!!["TestOverloadOneIntTwoDecimal"]!!
        MatcherAssert.assertThat((def.expression as FunctionRef).signature.size, Matchers.`is`(3))
    }

    companion object {
        private const val CQL_TEST_FILE = "SignatureTests/IncludedSignatureWithAliasOutputTests.cql"
        private const val LIBRARY_SOURCE_PROVIDER = "SignatureTests"
        private var defs: MutableMap<String?, ExpressionDef>? = null

        @Throws(IOException::class)
        private fun getTranslator(signatureLevel: LibraryBuilder.SignatureLevel): CqlTranslator {
            return TestUtils.getTranslator(CQL_TEST_FILE, LIBRARY_SOURCE_PROVIDER, signatureLevel)
        }
    }
}
