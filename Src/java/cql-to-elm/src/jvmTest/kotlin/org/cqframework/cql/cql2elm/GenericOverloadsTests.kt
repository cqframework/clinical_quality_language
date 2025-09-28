package org.cqframework.cql.cql2elm

import java.io.IOException
import java.util.function.Consumer
import java.util.stream.Collectors
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GenericOverloadsTests {
    @Throws(IOException::class)
    private fun getLibrary(
        enableResultTypes: Boolean,
        level: LibraryBuilder.SignatureLevel
    ): Library {
        val translator: CqlTranslator = getTranslator(enableResultTypes, level)
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

    private fun stringifies(library: Library): MutableList<FunctionDef?> {
        return library.statements!!
            .def
            .stream()
            .filter { x -> "Stringify" == x.name }
            .filter { obj: Any? -> FunctionDef::class.java.isInstance(obj) }
            .map { obj: Any? -> FunctionDef::class.java.cast(obj) }
            .collect(Collectors.toList())
    }

    private fun validateResultTypes(functionDef: FunctionDef) {
        assertEquals(2, functionDef.operand.size)

        val operand = functionDef.operand[0]
        assertThat(operand.operandTypeSpecifier, Matchers.instanceOf(ListTypeSpecifier::class.java))
        var listSpecifier = operand.operandTypeSpecifier as ListTypeSpecifier
        assertThat<TypeSpecifier?>(
            listSpecifier.elementType,
            Matchers.instanceOf<TypeSpecifier?>(NamedTypeSpecifier::class.java)
        )
        var namedSpecifier = listSpecifier.elementType as NamedTypeSpecifier?
        Assertions.assertNotNull(namedSpecifier!!.name)
        Assertions.assertNotNull(namedSpecifier.resultType)

        val second = functionDef.operand[1]
        assertThat(second.operandTypeSpecifier, Matchers.instanceOf(ListTypeSpecifier::class.java))
        listSpecifier = operand.operandTypeSpecifier as ListTypeSpecifier
        assertThat<TypeSpecifier?>(
            listSpecifier.elementType,
            Matchers.instanceOf<TypeSpecifier?>(NamedTypeSpecifier::class.java)
        )
        namedSpecifier = listSpecifier.elementType as NamedTypeSpecifier?
        Assertions.assertNotNull(namedSpecifier!!.name)
        Assertions.assertNotNull(namedSpecifier.resultType)
    }

    @Test
    @Throws(IOException::class)
    fun resultTypes() {
        val library = getLibrary(true, LibraryBuilder.SignatureLevel.Overloads)

        val stringifies = stringifies(library)
        stringifies.forEach(
            Consumer { functionDef: FunctionDef? -> this.validateResultTypes(functionDef!!) }
        )
    }

    @Test
    @Throws(IOException::class)
    fun noResultTypes() {
        val library = getLibrary(false, LibraryBuilder.SignatureLevel.Overloads)

        val stringifies = stringifies(library)
        stringifies.forEach(
            Consumer { functionDef: FunctionDef? -> this.validateResultTypes(functionDef!!) }
        )
    }

    @Test
    @Throws(IOException::class)
    fun resultTypesSignatureNone() {
        val library = getLibrary(true, LibraryBuilder.SignatureLevel.None)

        val stringifies = stringifies(library)
        stringifies.forEach(
            Consumer { functionDef: FunctionDef? -> this.validateResultTypes(functionDef!!) }
        )
    }

    @Test
    @Throws(IOException::class)
    fun noResultTypesSignatureNone() {
        val library = getLibrary(false, LibraryBuilder.SignatureLevel.None)

        val stringifies = stringifies(library)
        stringifies.forEach(
            Consumer { functionDef: FunctionDef? -> this.validateResultTypes(functionDef!!) }
        )
    }

    companion object {
        private const val CQL_TEST_FILE = "SignatureTests/GenericOverloadsTests.cql"
        private var defs: MutableMap<String?, ExpressionDef?>? = null

        @Throws(IOException::class)
        private fun getTranslator(
            enableResultTypes: Boolean,
            level: LibraryBuilder.SignatureLevel
        ): CqlTranslator {
            val options = CqlCompilerOptions()
            options.options.clear()
            options.signatureLevel = level
            if (enableResultTypes) {
                options.options.add(CqlCompilerOptions.Options.EnableResultTypes)
            }

            return TestUtils.createTranslator(CQL_TEST_FILE, options)
        }
    }
}
