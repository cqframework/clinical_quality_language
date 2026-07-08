package org.cqframework.cql.elm.serializing

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ElmXmlLibraryReaderTest {
    @Test
    fun read() {
        val reader = ElmXmlLibraryReader()
        val library = reader.read("<library xmlns=\"urn:hl7-org:elm:r1\"></library>")
        Assertions.assertNotNull(library)
    }
}
