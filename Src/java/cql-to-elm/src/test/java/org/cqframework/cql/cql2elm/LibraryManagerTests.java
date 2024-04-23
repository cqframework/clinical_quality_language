package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.testng.Assert.assertNotNull;

import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LibraryManagerTests {

    ModelManager modelManager;
    LibraryManager libraryManager;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider("LibraryManagerTests"));
    }

    @AfterClass
    public void tearDown() {
        libraryManager.getLibrarySourceLoader().clearProviders();
    }

    @Test
    public void testLibraryStatementsAreSorted() {
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
