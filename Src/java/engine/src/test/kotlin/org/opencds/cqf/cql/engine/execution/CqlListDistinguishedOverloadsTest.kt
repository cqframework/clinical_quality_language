package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.CqlException

internal class CqlListDistinguishedOverloadsTest : CqlTestBase() {
    @Test
    fun list_overload() {
        val compilerOptions = defaultOptions()

        val engine1 =
            getEngine(compilerOptions.withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads))
        val value = engine1.expression(library, "Test")
        Assertions.assertEquals("1, 2, 3, 4, 5", value)

        val engine2 =
            getEngine(compilerOptions.withSignatureLevel(LibraryBuilder.SignatureLevel.None))
        val cqlException =
            Assertions.assertThrows(CqlException::class.java) {
                engine2.expression(library, "Test")
            }
        Assertions.assertEquals(
            "Ambiguous call to operator 'toString(java.util.List)' in library 'CqlListDistinguishedOverloads'.",
            cqlException.message,
        )
    }

    companion object {
        private val library = VersionedIdentifier().withId("CqlListDistinguishedOverloads")
    }
}
