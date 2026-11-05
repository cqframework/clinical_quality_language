package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.ValueSet

internal class IncludedValueSetRefTest {
    @Test
    fun all_included_valueset() {
        val libraryManager = LibraryManager(ModelManager())
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())

        val environment = Environment(libraryManager)

        val engine = CqlEngine(environment)

        val results = engine.evaluate { library("IncludedValueSetRefTest") }.onlyResultOrThrow

        val actual = results["IncludedValueSet"]!!.value as ValueSet?

        Assertions.assertNotNull(actual)
        Assertions.assertEquals("http://test/common", actual!!.id)
    }
}
