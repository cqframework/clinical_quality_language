package org.cqframework.cql.elm.serializing

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ElmJsonLibraryReaderTest {
    @Test
    fun read() {
        val reader = ElmJsonLibraryReader()
        val library = reader.read("{\"library\" : { \"type\" : \"Library\"}}")
        Assertions.assertNotNull(library)
    }
}
