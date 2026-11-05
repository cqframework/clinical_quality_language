package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.io.Source
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.utils.asSource
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class SplitTest {
    private val librarySourceProvider =
        object : LibrarySourceProvider {
            override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
                if (libraryIdentifier.id != "SplitProbe") {
                    return null
                }
                return """
                    library SplitProbe
                    define "Split on a separator": Split('Patient/123', '/')
                    define "Last of Split": Last(Split('Patient/123', '/'))
                    define "Split with a null separator": Split('a,b', null)
                """
                    .trimIndent()
                    .asSource()
            }
        }

    /**
     * [#1870](https://github.com/cqframework/clinical_quality_language/issues/1870): Split with a
     * separator works on JS and Wasm, not just on the JVM
     */
    @Test
    fun splitWithSeparatorTest() {
        val modelManager = ModelManager()
        modelManager.modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
        val libraryManager = LibraryManager(modelManager)
        libraryManager.librarySourceLoader.registerProvider(librarySourceProvider)
        val results =
            CqlEngine(Environment(libraryManager))
                .evaluate { library("SplitProbe") }
                .onlyResultOrThrow

        assertEquals(
            listOf("Patient".toCqlString(), "123".toCqlString()).toCqlList(),
            results["Split on a separator"]?.value,
        )
        assertEquals("123".toCqlString(), results["Last of Split"]?.value)
        assertEquals(
            listOf("a,b".toCqlString()).toCqlList(),
            results["Split with a null separator"]?.value,
        )
    }
}
