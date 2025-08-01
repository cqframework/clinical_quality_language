package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LibraryManagerTests {

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
    void basicElmTest() {
        var versionIdentifier = new VersionedIdentifier().withId("BaseLibraryElm");

        var lib = libraryManager.resolveLibrary(versionIdentifier).getLibrary();

        assertNotNull(lib);
        assertNotNull(lib.getStatements().getDef());
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
    void basicElmTestVersionMismatch() {
        var versionIdentifier =
                new VersionedIdentifier().withId("BaseLibraryElmMismatchId").withVersion("1.0.1");

        var cqlIncludeException = assertThrows(
                CqlIncludeException.class, () -> libraryManagerVersionAgnostic.resolveLibrary(versionIdentifier));

        assertEquals(
                "Library BaseLibraryElmMismatchId was included with version 1.0.1, but id: BaseLibraryElmIdMismatch and version 1.0.0 of the library was found.",
                cqlIncludeException.getMessage());
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
