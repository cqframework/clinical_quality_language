package org.cqframework.cql.cql2elm

import java.io.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.OperatorExpression
import org.junit.jupiter.api.Test

/** Created by Bryn on 4/12/2018. */
internal class SignatureOutputTests {
    @Throws(IOException::class)
    private fun getLibrary(signatureLevel: LibraryBuilder.SignatureLevel): Library {
        val translator: CqlTranslator = getTranslator(signatureLevel)
        assertThat(translator.errors.size, `is`(0))
        defs = HashMap()
        val library: Library = translator.toELM()!!
        if (library.statements != null) {
            for (def in library.statements!!.def) {
                defs!![def.name] = def
            }
        }
        return library
    }

    @Suppress("MaxLineLength")
    @Test
    @Throws(IOException::class)
    fun none() {
        val translator: CqlTranslator = getTranslator(LibraryBuilder.SignatureLevel.None)
        assertThat(translator.warnings.size, Matchers.greaterThan(1))
        assertThat(
            translator.warnings[0].message,
            Matchers.equalTo(
                "The function SignatureOutputTests.MultipleOverloadTest has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime."
            ),
        )
    }

    @Test
    @Throws(IOException::class)
    fun differing() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.Differing)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerOverload->OverloadTest->signature(1)
        var def: ExpressionDef = defs!!["TestAdd"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(0))

        def = defs!!["TestDateAdd"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(0))

        def = defs!!["TestDateTime"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(0))

        def = defs!!["TestAvg"]!!
        assertThat((def.expression as AggregateExpression).signature.size, `is`(1))

        def = defs!!["TestDivide"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestIntegerOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs!!["TestDecimalOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestIntegerMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestDecimalMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestIntegerAndDecimalMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun overloads() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.Overloads)
        // TestAdd->operand->signature(2)
        // TestDateAdd->operand->signature(2)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerMultipleOverload->MultipleOverloadTest->signature(1)
        // TestDecimalMultipleOverload->MultipleOverloadTest->signature(2)
        var def: ExpressionDef = defs!!["TestAdd"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestDateAdd"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestDateTime"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(0))

        def = defs!!["TestAvg"]!!
        assertThat((def.expression as AggregateExpression).signature.size, `is`(1))

        def = defs!!["TestDivide"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestIntegerOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestDecimalOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestIntegerMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs!!["TestDecimalMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs!!["TestIntegerAndDecimalMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun all() {
        val library = getLibrary(LibraryBuilder.SignatureLevel.All)
        // TestAdd->operand->signature(2)
        // TestDateAdd->operand->signature(2)
        // TestDateTime->DateTime->signature(3)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerOverload->OverloadTest->signature(1)
        // TestDecimalOverload->OverloadTest->signature(1)
        // TestIntegerMultipleOverload->MultipleOverloadTest->signature(1)
        // TestDecimalMultipleOverload->MultipleOverloadTest->signature(2)
        var def: ExpressionDef = defs!!["TestAdd"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestDateAdd"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestDateTime"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(3))

        def = defs!!["TestAvg"]!!
        assertThat((def.expression as AggregateExpression).signature.size, `is`(1))

        def = defs!!["TestDivide"]!!
        assertThat((def.expression as OperatorExpression).signature.size, `is`(2))

        def = defs!!["TestIntegerOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs!!["TestDecimalOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs!!["TestMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(0))

        def = defs!!["TestIntegerMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(1))

        def = defs!!["TestDecimalMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(2))

        def = defs!!["TestIntegerAndDecimalMultipleOverload"]!!
        assertThat((def.expression as FunctionRef).signature.size, `is`(3))
    }

    companion object {
        private const val CQL_TEST_FILE = "SignatureTests/SignatureOutputTests.cql"
        private var defs: MutableMap<String?, ExpressionDef>? = null

        @Throws(IOException::class)
        private fun getTranslator(signatureLevel: LibraryBuilder.SignatureLevel): CqlTranslator {
            return TestUtils.getTranslator(CQL_TEST_FILE, null, signatureLevel)
        }
    }
}
