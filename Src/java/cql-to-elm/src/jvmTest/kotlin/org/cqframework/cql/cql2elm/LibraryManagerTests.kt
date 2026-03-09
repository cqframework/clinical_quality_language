package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength")
internal class LibraryManagerTests {
    @Test
    fun invalidCql() {
        val lib: Library? = libraryManagerOwnCache!!.resolveLibrary(INVALID_IDENT).library

        Assertions.assertNotNull(lib)

        MatcherAssert.assertThat(
            libraryManagerOwnCache!!.compiledLibraries.values,
            Matchers.empty(),
        )
    }

    @Test
    fun resolveLibrariesErrors() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            libraryManagerOwnCache!!.resolveLibraries(mutableListOf())
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            libraryManagerOwnCache!!.resolveLibraries(listOf(VersionedIdentifier()))
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            libraryManagerOwnCache!!.resolveLibraries(listOf(VersionedIdentifier().withId(null)))
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            libraryManagerOwnCache!!.resolveLibraries(listOf(VersionedIdentifier().withId("")))
        }
    }

    @Test
    fun basicElmTest() {
        val lib: Library? = libraryManager!!.resolveLibrary(BASE_LIBRARY_ELM_IDENT).library

        Assertions.assertNotNull(lib)
        Assertions.assertNotNull(lib!!.statements!!.def)
    }

    @Test
    fun basicElmTestMultiLib() {
        val results: CompiledLibraryMultiResults =
            libraryManager!!.resolveLibraries(listOf(BASE_LIBRARY_ELM_IDENT))

        Assertions.assertNotNull(results)

        val compiledLibraries = results.allCompiledLibraries()
        Assertions.assertNotNull(compiledLibraries)
        MatcherAssert.assertThat(compiledLibraries.size, Matchers.equalTo(1))
        MatcherAssert.assertThat(results.allErrors(), Matchers.empty())
        Assertions.assertFalse(results.hasErrors())
        MatcherAssert.assertThat(results.allResultsWithoutErrorSeverity().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), Matchers.empty())

        val compiledLibraryFirst = compiledLibraries[0]
        val compiledLibraryOnlyResult = results.onlyResult
        MatcherAssert.assertThat(
            compiledLibraryOnlyResult.compiledLibrary.identifier,
            Matchers.equalTo(compiledLibraryFirst.identifier),
        )

        val library = compiledLibraryOnlyResult.compiledLibrary.library

        Assertions.assertNotNull(library)
        Assertions.assertNotNull(library!!.statements!!.def)
    }

    @Test
    fun basicElmTestMultiLibTwoGoodLibs() {
        val results: CompiledLibraryMultiResults =
            libraryManager!!.resolveLibraries(
                listOf(BASE_LIBRARY_ELM_IDENT, BASE_LIBRARY_ELM_OTHER_IDENT)
            )

        Assertions.assertNotNull(results)

        val compiledLibraries = results.allCompiledLibraries()
        Assertions.assertNotNull(compiledLibraries)
        MatcherAssert.assertThat(compiledLibraries.size, Matchers.equalTo(2))
        MatcherAssert.assertThat(results.allErrors(), Matchers.empty())
        Assertions.assertFalse(results.hasErrors())
        MatcherAssert.assertThat(results.allResultsWithoutErrorSeverity().size, Matchers.equalTo(2))
        MatcherAssert.assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), Matchers.empty())

        for (compiledLibrary in compiledLibraries) {
            val library = compiledLibrary.library

            Assertions.assertNotNull(library)
            Assertions.assertNotNull(library!!.statements!!.def)
        }
    }

    @Test
    fun basicElmTestMultiLibOneGoodOneMismatchedLibs() {
        val versionedIdentifier = listOf(BASE_LIBRARY_ELM_IDENT, BASE_LIBRARY_ELM_MISMATCH_ID_IDENT)

        val cqlIncludeException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                libraryManager!!.resolveLibraries(versionedIdentifier)
            }

        Assertions.assertEquals(
            "Could not load source for library BaseLibraryElmMismatchId, version 1.0.1, namespace uri null.", //                "Library BaseLibraryElmMismatchId was included with version null, but id:
            // BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestMultiLibOneGoodOneInvalidLibs() {
        val versionedIdentifier = listOf(BASE_LIBRARY_ELM_IDENT, INVALID_IDENT)
        val results: CompiledLibraryMultiResults =
            libraryManager!!.resolveLibraries(versionedIdentifier)
        Assertions.assertNotNull(results)

        val compiledLibraries = results.allCompiledLibraries()
        Assertions.assertNotNull(compiledLibraries)
        MatcherAssert.assertThat(compiledLibraries.size, Matchers.equalTo(2))
        MatcherAssert.assertThat(results.allErrors(), Matchers.not(Matchers.empty()))
        Assertions.assertTrue(results.hasErrors())
        MatcherAssert.assertThat(results.allResultsWithoutErrorSeverity().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), Matchers.empty())

        val library = results.getCompiledLibraryFor(BASE_LIBRARY_ELM_IDENT)

        Assertions.assertNotNull(library)
        Assertions.assertNotNull(library!!.library!!.statements!!.def)

        val invalidIdentErrors = results.getErrorsFor(INVALID_IDENT)
        MatcherAssert.assertThat(invalidIdentErrors.size, Matchers.equalTo(1))
        val cqlCompilerException = invalidIdentErrors[0]

        MatcherAssert.assertThat(
            cqlCompilerException.message,
            Matchers.equalTo("Syntax error at define"),
        )
    }

    @Test
    fun basicElmTestIdMismatch() {
        val versionIdentifier = VersionedIdentifier().withId("BaseLibraryElmMismatchId")

        val cqlIncludeException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                libraryManager!!.resolveLibrary(versionIdentifier)
            }

        Assertions.assertEquals(
            "Library BaseLibraryElmMismatchId was included with version null, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestIdMismatchMultiLib() {
        val versionedIdentifiers = listOf(BASE_LIBRARY_ELM_MISMATCH_ID_IDENT)

        val cqlIncludeException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                libraryManager!!.resolveLibraries(versionedIdentifiers)
            }

        Assertions.assertEquals(
            "Library BaseLibraryElmMismatchId was included with version null, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestVersionMismatch() {
        val versionIdentifier: VersionedIdentifier =
            BASE_LIBRARY_ELM_MISMATCH_ID_IDENT.withVersion("1.0.1")

        val cqlIncludeException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                libraryManagerVersionAgnostic!!.resolveLibrary(versionIdentifier)
            }

        Assertions.assertEquals(
            "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestVersionMismatchMultiLib() {
        val versionIdentifier: VersionedIdentifier =
            BASE_LIBRARY_ELM_MISMATCH_ID_IDENT.withVersion("1.0.1")
        val versionIdentifiers = listOf(versionIdentifier)

        val cqlIncludeException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                libraryManagerVersionAgnostic!!.resolveLibraries(versionIdentifiers)
            }

        Assertions.assertEquals(
            "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
            cqlIncludeException.message,
        )
    }

    @Test
    fun basicElmTestSkipVersionCheck() {
        // Skip version check when requesting a library without a version but the library has a
        // version
        Assertions.assertNotNull(
            libraryManagerVersionAgnostic!!.resolveLibrary(
                VersionedIdentifier().apply {
                    id = "BaseLibraryElm" // has version 1.0.0
                }
            )
        )

        // Skip version check when requesting a library with a version but the library does not have
        // a version
        Assertions.assertNotNull(
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
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            libraryManager!!.resolveLibrary(versionedIdentifier)
        }
    }

    @Test
    fun testResolveLibraryIdentifierIdEmpty() {
        val versionedIdentifier = VersionedIdentifier().withId("")
        Assertions.assertThrows(IllegalArgumentException::class.java) {
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

        val resolvedLibrary: CompiledLibrary = libraryManager!!.resolveLibrary(libraryIdentifier)
        Assertions.assertSame(cachedLibrary, resolvedLibrary)
    }

    @Test
    fun testResolveLibraryFromCacheMultiLib() {
        val libraryIdentifier = VersionedIdentifier().withId("Test").withVersion("1.0")
        val cachedLibrary = CompiledLibrary()
        cachedLibrary.identifier = libraryIdentifier
        cachedLibrary.library = Library().withIdentifier(libraryIdentifier)
        libraryManager!!.compiledLibraries[libraryIdentifier] = cachedLibrary

        val results: CompiledLibraryMultiResults =
            libraryManager!!.resolveLibraries(listOf(libraryIdentifier))

        val compiledLibraries = results.allCompiledLibraries()
        Assertions.assertNotNull(compiledLibraries)
        MatcherAssert.assertThat(compiledLibraries.size, Matchers.equalTo(1))
        MatcherAssert.assertThat(results.allErrors(), Matchers.empty())
        Assertions.assertFalse(results.hasErrors())
        MatcherAssert.assertThat(results.allResultsWithoutErrorSeverity().size, Matchers.equalTo(1))
        MatcherAssert.assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), Matchers.empty())

        val resolvedLibrary = results.onlyResult.compiledLibrary

        Assertions.assertNotNull(resolvedLibrary)
        Assertions.assertSame(cachedLibrary, resolvedLibrary)
    }

    @Test
    fun libraryStatementsAreSorted() {
        // Some optimizations depend on the Library statements being sorted in lexicographic order
        // by name
        // This test validates that they are ordered
        val lib: Library? =
            libraryManager!!.resolveLibrary(VersionedIdentifier().withId("OutOfOrder")).library

        Assertions.assertNotNull(lib)
        Assertions.assertNotNull(lib!!.statements!!.def)

        val defs = lib.statements!!.def
        MatcherAssert.assertThat(
            "The list should be larger than 3 elements to validate it actually sorted",
            defs.size,
            Matchers.greaterThan(3),
        )

        for (i in 0..<defs.size - 1) {
            val left = defs[i]
            val right = defs[i + 1]

            // Ensure that the left element is always less than or equal to the right element
            // In other words, they are ordered.
            MatcherAssert.assertThat(
                left.name!!.compareTo(right.name!!),
                Matchers.lessThanOrEqualTo(0),
            )
        }
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
            val modelManager = ModelManager()

            libraryManager = LibraryManager(modelManager)
            libraryManager!!
                .librarySourceLoader
                .registerProvider(TestLibrarySourceProvider("LibraryManagerTests"))

            // Used if we want to load a library with a mismatch in the version and want to test the
            // subsequent version
            // validation
            libraryManagerVersionAgnostic = LibraryManager(modelManager)
            libraryManagerVersionAgnostic!!
                .librarySourceLoader
                .registerProvider(TestLibrarySourceVersionAgnosticProvider("LibraryManagerTests"))

            libraryManagerOwnCache =
                LibraryManager(
                    modelManager,
                    CqlCompilerOptions.Companion.defaultOptions(),
                    HashMap(),
                )
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
