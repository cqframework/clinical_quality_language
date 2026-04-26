package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlListDistinguishedOverloadsTest : CqlTestBase() {
    @Test
    fun list_overload() {
        val compilerOptions = defaultOptions()

        val engine1 =
            getEngine(compilerOptions.withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads))
        var value = engine1.expression(library, "Test")
        assertEquals("1, 2, 3, 4, 5".toCqlString(), value)

        val engine2 =
            getEngine(compilerOptions.withSignatureLevel(LibraryBuilder.SignatureLevel.None))
        value = engine2.expression(library, "Test")
        assertEquals("1, 2, 3, 4, 5".toCqlString(), value)
    }

    companion object {
        private val library = VersionedIdentifier().withId("CqlListDistinguishedOverloads")
    }
}
