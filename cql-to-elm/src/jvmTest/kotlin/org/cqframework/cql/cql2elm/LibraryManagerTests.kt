package org.cqframework.cql.cql2elm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

@Suppress("MaxLineLength")
internal class LibraryManagerTests {
    @Test
    fun invalidCql() {
        val lib = libraryManagerOwnCache!!.resolveLibrary(INVALID_IDENT).library

        assertNotNull(lib)
        assertTrue(libraryManagerOwnCache!!.compiledLibraries.values.isEmpty())
    }

    @Test
    fun resolveLibrariesErrors() {
        assertFailsWith<IllegalArgumentException> {
            libraryManagerOwnCache!!.resolveLibraries(mutableListOf())
        }
        assertFailsWith<IllegalArgumentException> {
            libraryManagerOwnCache!!.resolveLibraries(listOf(VersionedIdentifier()))
        }
        assertFailsWith<IllegalArgumentException> {
            libraryManagerOwnCache!!.resolveLibraries(listOf(VersionedIdentifier().withId(null)))
        }
        assertFailsWith<IllegalArgumentException> {
            libraryManagerOwnCache!!.resolveLibraries(listOf(VersionedIdentifier().withId("")))
        }
    }

    @Test
    fun basicElmTest() {
        val lib = libraryManager!!.resolveLibrary(BASE_LIBRARY_ELM_IDENT).library

        assertNotNull(lib)
        assertNotNull(lib.statements!!.def)
    }

    @Test
    fun basicElmTestMultiLib() {
        val results = libraryManager!!.resolveLibraries(listOf(BASE_LIBRARY_ELM_IDENT))

        assertNotNull(results)

        val compiledLibraries = results.allCompiledLibraries()
        assertNotNull(compiledLibraries)
        assertEquals(1, compiledLibraries.size)
        assertTrue(results.allErrors().isEmpty())
        assertFalse(results.hasErrors())
        assertEquals(1, results.allResultsWithoutErrorSeverity().size)
        assertTrue(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT).isEmpty())

        val compiledLibraryFirst = compiledLibraries[0]
        val compiledLibraryOnlyResult = results.onlyResult
        assertEquals(
            compiledLibraryFirst.identifier,
            compiledLibraryOnlyResult.compiledLibrary.identifier,
        )

        val library = compiledLibraryOnlyResult.compiledLibrary.library

        assertNotNull(library)
        assertNotNull(library.statements!!.def)
    }

    @Test
    fun basicElmTestMultiLibTwoGoodLibs() {
        val results =
            libraryManager!!.resolveLibraries(
                listOf(BASE_LIBRARY_ELM_IDENT, BASE_LIBRARY_ELM_OTHER_IDENT)
            )

        assertNotNull(results)

        val compiledLibraries = results.allCompiledLibraries()
        assertNotNull(compiledLibraries)
        assertEquals(2, compiledLibraries.size)
        assertTrue(results.allErrors().isEmpty())
        assertFalse(results.hasErrors())
        assertEquals(2, results.allResultsWithoutErrorSeverity().size)
        assertTrue(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT).isEmpty())

        for (compiledLibrary in compiledLibraries) {
            val library = compiledLibrary.library

            assertNotNull(library)
            assertNotNull(library.statements!!.def)
        }
    }

    @Test
    fun basicElmTestMultiLibOneGoodOneMismatchedLibs() {
        val versionedIdentifier = listOf(BASE_LIBRARY_ELM_IDENT, BASE_LIBRARY_ELM_MISMATCH_ID_IDENT)

        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManager!!.resolveLibraries(versionedIdentifier)
            }

        assertEquals(
            "Could not load source for library BaseLibraryElmMismatchId, version 1.0.1, namespace uri null.", //                "Library BaseLibraryElmMismatchId was included with version null, but id:
            // BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestMultiLibOneGoodOneInvalidLibs() {
        val versionedIdentifier = listOf(BASE_LIBRARY_ELM_IDENT, INVALID_IDENT)
        val results = libraryManager!!.resolveLibraries(versionedIdentifier)
        assertNotNull(results)

        val compiledLibraries = results.allCompiledLibraries()
        assertNotNull(compiledLibraries)
        assertEquals(2, compiledLibraries.size)
        assertTrue(results.allErrors().isNotEmpty())
        assertTrue(results.hasErrors())
        assertEquals(1, results.allResultsWithoutErrorSeverity().size)
        assertTrue(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT).isEmpty())

        val library = results.getCompiledLibraryFor(BASE_LIBRARY_ELM_IDENT)

        assertNotNull(library)
        assertNotNull(library.library!!.statements!!.def)

        val invalidIdentErrors = results.getErrorsFor(INVALID_IDENT)
        assertEquals(1, invalidIdentErrors.size)
        val cqlCompilerException = invalidIdentErrors[0]

        assertEquals("Syntax error at define", cqlCompilerException.message)
    }

    @Test
    fun basicElmTestIdMismatch() {
        val versionIdentifier = VersionedIdentifier().withId("BaseLibraryElmMismatchId")

        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManager!!.resolveLibrary(versionIdentifier)
            }

        assertEquals(
            "Library BaseLibraryElmMismatchId was included with version null, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestIdMismatchMultiLib() {
        val versionedIdentifiers = listOf(BASE_LIBRARY_ELM_MISMATCH_ID_IDENT)

        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManager!!.resolveLibraries(versionedIdentifiers)
            }

        assertEquals(
            "Library BaseLibraryElmMismatchId was included with version null, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestVersionMismatch() {
        val versionIdentifier = BASE_LIBRARY_ELM_MISMATCH_ID_IDENT.withVersion("1.0.1")

        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManagerVersionAgnostic!!.resolveLibrary(versionIdentifier)
            }

        assertEquals(
            "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestVersionMismatchMultiLib() {
        val versionIdentifier = BASE_LIBRARY_ELM_MISMATCH_ID_IDENT.withVersion("1.0.1")
        val versionIdentifiers = listOf(versionIdentifier)

        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManagerVersionAgnostic!!.resolveLibraries(versionIdentifiers)
            }

        assertEquals(
            "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestSkipVersionCheck() {
        // Skip version check when requesting a library without a version but the library has a
        // version
        assertNotNull(
            libraryManagerVersionAgnostic!!.resolveLibrary(
                VersionedIdentifier().apply {
                    id = "BaseLibraryElm" // has version 1.0.0
                }
            )
        )

        // Skip version check when requesting a library with a version but the library does not have
        // a version
        assertNotNull(
            libraryManagerVersionAgnostic!!.resolveLibrary(
                VersionedIdentifier().apply {
                    id = "BaseLibraryElmWithoutVersion" // does not have a version
                    version = "1.2.3"
                }
            )
        )
    }

    @Test
    fun testResolveLibraryIdentifierIdNull() {
        val versionedIdentifier = VersionedIdentifier().withId(null)
        assertFailsWith<IllegalArgumentException> {
            libraryManager!!.resolveLibrary(versionedIdentifier)
        }
    }

    @Test
    fun testResolveLibraryIdentifierIdEmpty() {
        val versionedIdentifier = VersionedIdentifier().withId("")
        assertFailsWith<IllegalArgumentException> {
            libraryManager!!.resolveLibrary(versionedIdentifier)
        }
    }

    @Test
    fun testResolveLibraryFromCache() {
        val libraryIdentifier = VersionedIdentifier().withId("Test").withVersion("1.0")
        val cachedLibrary = CompiledLibrary()
        cachedLibrary.identifier = libraryIdentifier
        cachedLibrary.library = Library().withIdentifier(libraryIdentifier)
        libraryManager!!.compiledLibraries[libraryIdentifier] = cachedLibrary

        val resolvedLibrary = libraryManager!!.resolveLibrary(libraryIdentifier)
        assertSame(cachedLibrary, resolvedLibrary)
    }

    @Test
    fun cacheModeNoneReportsDiagnosticsFromFreshCompilation() {
        val cache = mutableMapOf<VersionedIdentifier, CompiledLibrary>()
        val manager = LibraryManager(ModelManager(), libraryCache = cache)
        manager.librarySourceLoader.registerProvider(
            TestLibrarySourceProvider("LibraryManagerTests")
        )

        val cachedLibrary = CompiledLibrary()
        cachedLibrary.identifier = INVALID_IDENT
        cachedLibrary.library = Library().withIdentifier(INVALID_IDENT)
        cache[INVALID_IDENT] = cachedLibrary
        val errors = mutableListOf<CqlCompilerException>()

        val resolvedLibrary =
            manager.resolveLibrary(INVALID_IDENT, errors, LibraryManager.CacheMode.NONE)

        assertNotSame(cachedLibrary, resolvedLibrary)
        assertSame(cachedLibrary, cache[INVALID_IDENT])
        assertEquals(1, errors.size)
        assertEquals("Syntax error at define", errors[0].message)
    }

    @Test
    fun testResolveLibraryFromCacheMultiLib() {
        val libraryIdentifier = VersionedIdentifier().withId("Test").withVersion("1.0")
        val cachedLibrary = CompiledLibrary()
        cachedLibrary.identifier = libraryIdentifier
        cachedLibrary.library = Library().withIdentifier(libraryIdentifier)
        libraryManager!!.compiledLibraries[libraryIdentifier] = cachedLibrary

        val results = libraryManager!!.resolveLibraries(listOf(libraryIdentifier))

        val compiledLibraries = results.allCompiledLibraries()
        assertNotNull(compiledLibraries)
        assertEquals(1, compiledLibraries.size)
        assertTrue(results.allErrors().isEmpty())
        assertFalse(results.hasErrors())
        assertEquals(1, results.allResultsWithoutErrorSeverity().size)
        assertTrue(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT).isEmpty())

        val resolvedLibrary = results.onlyResult.compiledLibrary

        assertNotNull(resolvedLibrary)
        assertSame(cachedLibrary, resolvedLibrary)
    }

    @Test
    fun libraryStatementsAreSorted() {
        // Some optimizations depend on the Library statements being sorted in lexicographic order
        // by name
        // This test validates that they are ordered
        val lib =
            libraryManager!!.resolveLibrary(VersionedIdentifier().withId("OutOfOrder")).library

        assertNotNull(lib)
        assertNotNull(lib.statements!!.def)

        val defs = lib.statements!!.def
        assertTrue(
            defs.size > 3,
            "The list should be larger than 3 elements to validate it actually sorted",
        )

        for (i in 0..<defs.size - 1) {
            val left = defs[i]
            val right = defs[i + 1]

            // Ensure that the left element is always less than or equal to the right element
            // In other words, they are ordered.
            assertTrue(left.name!! <= right.name!!)
        }
    }

    @Test
    fun compiledLibraryWithResultTypesResolvesSuccessfully() {
        val libraryWithResultTypes =
            libraryManager!!.resolveLibrary(VersionedIdentifier().withId("LibraryWithResultTypes"))
        assertEquals("LibraryWithResultTypes", libraryWithResultTypes.library!!.identifier!!.id)
    }

    @Test
    fun compiledLibraryWithoutResultTypesIsRejected() {
        // Result types must be present for every expression, regardless of the `translatorOptions`
        // annotation value. When a compiled library is not compatible (missing result types, etc.),
        // library resolution will continue to load the library from the CQL source if it is
        // present.
        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManager!!.resolveLibrary(
                    VersionedIdentifier().withId("LibraryWithoutResultTypes")
                )
            }
        assertEquals(
            "Could not load source for library LibraryWithoutResultTypes, version null, namespace uri null.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun compiledLibraryWithIncompatibleTranslatorVersionIsRejected() {
        val cqlIncludeException =
            assertFailsWith<CqlIncludeException> {
                libraryManager!!.resolveLibrary(
                    VersionedIdentifier().withId("LibraryWithIncompatibleTranslatorVersion")
                )
            }
        assertEquals(
            "Could not load source for library LibraryWithIncompatibleTranslatorVersion, version null, namespace uri null.",
            cqlIncludeException.message,
        )
    }

    companion object {
        private val BASE_LIBRARY_ELM_IDENT = VersionedIdentifier().withId("BaseLibraryElm")
        private val BASE_LIBRARY_ELM_OTHER_IDENT =
            VersionedIdentifier().withId("BaseLibraryElmOther")
        private val BASE_LIBRARY_ELM_MISMATCH_ID_IDENT =
            VersionedIdentifier().withId("BaseLibraryElmMismatchId")
        private val INVALID_IDENT = VersionedIdentifier().withId("Invalid")
        private var libraryManager: LibraryManager? = null
        private var libraryManagerVersionAgnostic: LibraryManager? = null
        private var libraryManagerOwnCache: LibraryManager? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            val modelManager =
                ModelManager().apply {
                    modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
                    modelInfoLoader.registerModelInfoProvider(FhirModelInfoProvider())
                }

            val compilerOptions = CqlCompilerOptions(CqlCompilerOptions.Options.EnableResultTypes)

            libraryManager = LibraryManager(modelManager, compilerOptions)
            libraryManager!!
                .librarySourceLoader
                .registerProvider(TestLibrarySourceProvider("LibraryManagerTests"))

            // Used if we want to load a library with a mismatch in the version and want to test the
            // subsequent version validation
            libraryManagerVersionAgnostic = LibraryManager(modelManager, compilerOptions)
            libraryManagerVersionAgnostic!!
                .librarySourceLoader
                .registerProvider(TestLibrarySourceVersionAgnosticProvider("LibraryManagerTests"))

            libraryManagerOwnCache = LibraryManager(modelManager, compilerOptions, HashMap())
            libraryManagerOwnCache!!
                .librarySourceLoader
                .registerProvider(TestLibrarySourceVersionAgnosticProvider("LibraryManagerTests"))
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            libraryManager!!.librarySourceLoader.clearProviders()
        }
    }
}
