package org.cqframework.cql.cql2elm;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class LibraryTests {

    @BeforeClass
    public void setup() {
        LibrarySourceLoader.registerProvider(new TestLibrarySourceProvider());
    }

    @AfterClass
    public void tearDown() {
        LibrarySourceLoader.clearProviders();
    }

    @Test
    public void testLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"));
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/InvalidReferencingLibrary.cql"));
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
