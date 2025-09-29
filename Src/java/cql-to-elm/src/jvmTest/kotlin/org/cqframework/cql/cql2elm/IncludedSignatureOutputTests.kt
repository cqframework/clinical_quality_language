package org.cqframework.cql.cql2elm

import java.io.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Library
import org.junit.jupiter.api.Test

/** Created by Bryn on 4/12/2018. */
internal class IncludedSignatureOutputTests {
    @Throws(IOException::class)
    private fun getLibrary(signatureLevel: LibraryBuilder.SignatureLevel): Library {
        val translator: CqlTranslator = getTranslator(signatureLevel)
        assertThat(translator.errors.size, `is`(0))
        val library: Library = translator.toELM()!!
        if (library.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name!!] = def
            }
        }
        return library
    }

    @Test
    @Throws(IOException::class)
    fun none() {
        val translator: CqlTranslator = getTranslator(LibraryBuilder.SignatureLevel.None)
        assertThat(translator.warnings.size, Matchers.greaterThan(1))
        assertThat(
            translator.warnings[0].message,
            Matchers.equalTo(
                @Suppress("MaxLineLength")
                "The function SignatureOutputTests.MultipleOverloadTest has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun differing() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.Differing)

        var def = defs["TestOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadOneInt"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadOneDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadTwoInts"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadTwoDecimals"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadOneIntOneDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadOneIntTwoDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun overloads() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.Overloads)

        var def = defs["TestOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadOneInt"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs["TestOverloadOneDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs["TestOverloadTwoInts"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadTwoDecimals"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadOneIntOneDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadOneIntTwoDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun all() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.All)

        var def: ExpressionDef = defs["TestOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs["TestOverloadOneInt"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs["TestOverloadOneDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs["TestOverloadTwoInts"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadTwoDecimals"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadOneIntOneDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs["TestOverloadOneIntTwoDecimal"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(3))
    }

    companion object {
        private const val CQL_TEST_FILE = "SignatureTests/IncludedSignatureOutputTests.cql"
        private const val LIBRARY_SOURCE_PROVIDER = "SignatureTests"
        private var defs = mutableMapOf<String, ExpressionDef>()

        @Throws(IOException::class)
        private fun getTranslator(signatureLevel: LibraryBuilder.SignatureLevel): CqlTranslator {
            return TestUtils.getTranslator(CQL_TEST_FILE, LIBRARY_SOURCE_PROVIDER, signatureLevel)
        }
    }
}
