package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LibraryManagerTests {

    private static final VersionedIdentifier BASE_LIBRARY_ELM_IDENT =
            new VersionedIdentifier().withId("BaseLibraryElm");
    private static final VersionedIdentifier BASE_LIBRARY_ELM_OTHER_IDENT =
            new VersionedIdentifier().withId("BaseLibraryElmOther");
    private static final VersionedIdentifier BASE_LIBRARY_ELM_MISMATCH_ID_IDENT =
            new VersionedIdentifier().withId("BaseLibraryElmMismatchId");
    private static final VersionedIdentifier INVALID_IDENT = new VersionedIdentifier().withId("Invalid");
    private static LibraryManager libraryManager;
    private static LibraryManager libraryManagerVersionAgnostic;

    @BeforeAll
    static void setup() {
        var modelManager = new ModelManager();

        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider("LibraryManagerTests"));

        // Used if we want to load a library with a mismatch in the version and want to test the subsequent version
        // validation
        libraryManagerVersionAgnostic = new LibraryManager(modelManager);
        libraryManagerVersionAgnostic
                .getLibrarySourceLoader()
                .registerProvider(new TestLibrarySourceVersionAgnosticProvider("LibraryManagerTests"));
    }

    @AfterAll
    static void tearDown() {
        libraryManager.getLibrarySourceLoader().clearProviders();
    }

    @Test
    void invalidCql() {
        var lib = libraryManager.resolveLibrary(INVALID_IDENT).getLibrary();

        assertNotNull(lib);

        assertThat(libraryManager.getCompiledLibraries().values(), empty());
    }

    @Test
    void basicElmTest() {
        var lib = libraryManager.resolveLibrary(BASE_LIBRARY_ELM_IDENT).getLibrary();

        assertNotNull(lib);
        assertNotNull(lib.getStatements().getDef());
    }

    @Test
    void basicElmTestMultiLib() {
        var results = libraryManager.resolveLibraries(List.of(BASE_LIBRARY_ELM_IDENT));

        assertNotNull(results);

        var compiledLibraries = results.allCompiledLibraries();
        assertNotNull(compiledLibraries);
        assertThat(compiledLibraries.size(), equalTo(1));
        assertThat(results.allErrors(), empty());
        assertFalse(results.hasErrors());
        assertThat(results.allLibrariesWithoutErrorSeverity().size(), equalTo(1));
        assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), empty());

        var compiledLibraryFirst = compiledLibraries.get(0);
        var compiledLibraryOnlyResult = results.getOnlyResult();
        assertThat(
                compiledLibraryOnlyResult.compiledLibrary().getIdentifier(),
                equalTo(compiledLibraryFirst.getIdentifier()));

        var library = compiledLibraryOnlyResult.compiledLibrary().getLibrary();

        assertNotNull(library);
        assertNotNull(library.getStatements().getDef());
    }

    @Test
    void basicElmTestMultiLibTwoGoodLibs() {
        var results = libraryManager.resolveLibraries(List.of(BASE_LIBRARY_ELM_IDENT, BASE_LIBRARY_ELM_OTHER_IDENT));

        assertNotNull(results);

        var compiledLibraries = results.allCompiledLibraries();
        assertNotNull(compiledLibraries);
        assertThat(compiledLibraries.size(), equalTo(2));
        assertThat(results.allErrors(), empty());
        assertFalse(results.hasErrors());
        assertThat(results.allLibrariesWithoutErrorSeverity().size(), equalTo(2));
        assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), empty());

        for (CompiledLibrary compiledLibrary : compiledLibraries) {
            var library = compiledLibrary.getLibrary();

            assertNotNull(library);
            assertNotNull(library.getStatements().getDef());
        }
    }

    @Test
    void basicElmTestMultiLibOneGoodOneMismatchedLibs() {
        var cqlIncludeException = assertThrows(
                CqlIncludeException.class,
                () -> libraryManager.resolveLibraries(
                        List.of(BASE_LIBRARY_ELM_IDENT, BASE_LIBRARY_ELM_MISMATCH_ID_IDENT)));

        assertEquals(
                "Could not load source for library BaseLibraryElmMismatchId, version 1.0.1, namespace uri null.",
                //                "Library BaseLibraryElmMismatchId was included with version null, but id:
                // BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
                cqlIncludeException.getMessage());
    }

    @Test
    void basicElmTestMultiLibOneGoodOneInvalidLibs() {
        var results = libraryManager.resolveLibraries(List.of(BASE_LIBRARY_ELM_IDENT, INVALID_IDENT));
        assertNotNull(results);

        var compiledLibraries = results.allCompiledLibraries();
        assertNotNull(compiledLibraries);
        assertThat(compiledLibraries.size(), equalTo(2));
        assertThat(results.allErrors(), not(empty()));
        assertTrue(results.hasErrors());
        assertThat(results.allLibrariesWithoutErrorSeverity().size(), equalTo(1));
        assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), empty());

        var library = results.getCompiledLibraryFor(BASE_LIBRARY_ELM_IDENT);

        assertNotNull(library);
        assertNotNull(library.getLibrary().getStatements().getDef());

        var invalidIdentErrors = results.getErrorsFor(INVALID_IDENT);
        assertThat(invalidIdentErrors.size(), equalTo(1));
        var cqlCompilerException = invalidIdentErrors.get(0);

        assertThat(cqlCompilerException.getMessage(), equalTo("Syntax error at define"));
    }

    @Test
    void basicElmTestIdMismatch() {
        var versionIdentifier = new VersionedIdentifier().withId("BaseLibraryElmMismatchId");

        var cqlIncludeException = assertThrows(CqlIncludeException.class, () -> {
            libraryManager.resolveLibrary(versionIdentifier);
        });

        assertEquals(
                "Library BaseLibraryElmMismatchId was included with version null, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
                cqlIncludeException.getMessage());
    }

    @Test
    void basicElmTestIdMismatchMultiLib() {
        var cqlIncludeException = assertThrows(
                CqlIncludeException.class,
                () -> libraryManager.resolveLibraries(List.of(BASE_LIBRARY_ELM_MISMATCH_ID_IDENT)));

        assertEquals(
                "Library BaseLibraryElmMismatchId was included with version null, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
                cqlIncludeException.getMessage());
    }

    @Test
    void basicElmTestVersionMismatch() {
        var versionIdentifier = BASE_LIBRARY_ELM_MISMATCH_ID_IDENT.withVersion("1.0.1");

        var cqlIncludeException = assertThrows(
                CqlIncludeException.class, () -> libraryManagerVersionAgnostic.resolveLibrary(versionIdentifier));

        assertEquals(
                "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
                cqlIncludeException.getMessage());
    }

    @Test
    void basicElmTestVersionMismatchMultiLib() {
        var versionIdentifier = BASE_LIBRARY_ELM_MISMATCH_ID_IDENT.withVersion("1.0.1");

        var cqlIncludeException = assertThrows(
                CqlIncludeException.class,
                () -> libraryManagerVersionAgnostic.resolveLibraries(List.of(versionIdentifier)));

        assertEquals(
                "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
                cqlIncludeException.getMessage());
    }

    @Test
    void testConstructorWithNullModelManager() {
        assertThrows(IllegalArgumentException.class, () -> new LibraryManager(null));
    }

    @Test
    void testResolveLibraryIdentifierNull() {
        assertThrows(IllegalArgumentException.class, () -> libraryManager.resolveLibrary(null));
    }

    @Test
    void testResolveLibraryIdentifierIdNull() {
        var versionedIdentifier = new VersionedIdentifier().withId(null);
        assertThrows(IllegalArgumentException.class, () -> {
            libraryManager.resolveLibrary(versionedIdentifier);
        });
    }

    @Test
    void testResolveLibraryIdentifierIdEmpty() {
        var versionedIdentifier = new VersionedIdentifier().withId("");
        assertThrows(IllegalArgumentException.class, () -> {
            libraryManager.resolveLibrary(versionedIdentifier);
        });
    }

    @Test
    void testResolveLibraryFromCache() {
        VersionedIdentifier libraryIdentifier =
                new VersionedIdentifier().withId("Test").withVersion("1.0");
        CompiledLibrary cachedLibrary = new CompiledLibrary();
        cachedLibrary.setIdentifier(libraryIdentifier);
        cachedLibrary.setLibrary(new Library().withIdentifier(libraryIdentifier));
        libraryManager.getCompiledLibraries().put(libraryIdentifier, cachedLibrary);

        CompiledLibrary resolvedLibrary = libraryManager.resolveLibrary(libraryIdentifier);
        assertSame(cachedLibrary, resolvedLibrary);
    }

    @Test
    void testResolveLibraryFromCacheMultiLib() {
        VersionedIdentifier libraryIdentifier =
                new VersionedIdentifier().withId("Test").withVersion("1.0");
        CompiledLibrary cachedLibrary = new CompiledLibrary();
        cachedLibrary.setIdentifier(libraryIdentifier);
        cachedLibrary.setLibrary(new Library().withIdentifier(libraryIdentifier));
        libraryManager.getCompiledLibraries().put(libraryIdentifier, cachedLibrary);

        var results = libraryManager.resolveLibraries(List.of(libraryIdentifier));

        var compiledLibraries = results.allCompiledLibraries();
        assertNotNull(compiledLibraries);
        assertThat(compiledLibraries.size(), equalTo(1));
        assertThat(results.allErrors(), empty());
        assertFalse(results.hasErrors());
        assertThat(results.allLibrariesWithoutErrorSeverity().size(), equalTo(1));
        assertThat(results.getErrorsFor(BASE_LIBRARY_ELM_IDENT), empty());

        var resolvedLibrary = results.getOnlyResult().compiledLibrary();

        assertNotNull(resolvedLibrary);
        assertSame(cachedLibrary, resolvedLibrary);
    }

    @Test
    void libraryStatementsAreSorted() {
        // Some optimizations depend on the Library statements being sorted in lexicographic order by name
        // This test validates that they are ordered
        var lib = libraryManager
                .resolveLibrary(new VersionedIdentifier().withId("OutOfOrder"))
                .getLibrary();

        assertNotNull(lib);
        assertNotNull(lib.getStatements().getDef());

        var defs = lib.getStatements().getDef();
        assertThat(
                "The list should be larger than 3 elements to validate it actually sorted",
                defs.size(),
                greaterThan(3));

        for (int i = 0; i < defs.size() - 1; i++) {
            var left = defs.get(i);
            var right = defs.get(i + 1);

            // Ensure that the left element is always less than or equal to the right element
            // In other words, they are ordered.
            assertThat(left.getName().compareTo(right.getName()), lessThanOrEqualTo(0));
        }
    }
}
