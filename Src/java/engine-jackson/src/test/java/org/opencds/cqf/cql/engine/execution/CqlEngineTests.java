package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;

public class CqlEngineTests extends TranslatingTestBase {


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_nullLibraryLoader_throwsException() {
        new CqlEngine(null);
    }

    @Test
    public void test_simpleLibrary_returnsResult() throws IOException {
        Library library = this.toLibrary("library Test version '1.0.0'\ndefine X:\n5+5");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        EvaluationResult result = engine.evaluate("Test");


        ExpressionResult expResult = result.forExpression("X");

        assertThat(expResult.value(), is(10));
    }

    @Test
    public void test_simpleLibraryWithParam_returnsParamValue() throws IOException {
        Library library = this.toLibrary("library Test version '1.0.0'\nparameter IntValue Integer\ndefine X:\nIntValue");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("IntValue", 10);

        EvaluationResult result = engine.evaluate("Test", parameters);
        ExpressionResult expResult = result.forExpression("X");

        assertThat(expResult.value(), is(10));
    }


    //@Test(expected = IllegalArgumentException.class)
    public void test_dataLibrary_noProvider_throwsException() throws IOException {
        Library library = this.toLibrary("library Test version '1.0.0'\nusing FHIR version '3.0.0'\ndefine X:\n5+5");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        engine.evaluate("Test");
    }

    @Test
    public void test_twoExpressions_byLibrary_allReturned() throws IOException {
        Library library = this.toLibrary("library Test version '1.0.0'\ndefine X:\n5+5\ndefine Y: 2 + 2");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        EvaluationResult result = engine.evaluate("Test");

        assertNotNull(result);

        ExpressionResult expResult = result.forExpression("X");
        assertThat(expResult.value(), is(10));

        expResult = result.forExpression("Y");
        assertThat(expResult.value(), is(4));
    }

    @Test
    public void test_twoExpressions_oneRequested_oneReturned() throws IOException {
        Library library = this.toLibrary("library Test version '1.0.0'\ndefine X:\n5+5\ndefine Y: 2 + 2");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        EvaluationResult result = engine.evaluate("Test", new HashSet<>(Arrays.asList("Y")));

        assertNotNull(result);

        ExpressionResult expResult = result.forExpression("Y");
        assertThat(expResult.value(), is(4));
    }

    @Test
    public void test_twoLibraries_expressionsForEach() throws IOException {

        Map<org.hl7.elm.r1.VersionedIdentifier, String> libraries = new HashMap<>();
        libraries.put(this.toElmIdentifier("Common", "1.0.0"),
            "library Common version '1.0.0'\ndefine Z:\n5+5\n");
        libraries.put(toElmIdentifier("Test", "1.0.0"),
            "library Test version '1.0.0'\ninclude Common version '1.0.0' named \"Common\"\ndefine X:\n5+5\ndefine Y: 2 + 2\ndefine W: \"Common\".Z + 5");

        LibraryManager libraryManager = this.toLibraryManager(libraries);
        List<CqlCompilerException> errors = new ArrayList<>();
        List<Library> executableLibrariesJson = new ArrayList<>();
        for (org.hl7.elm.r1.VersionedIdentifier id : libraries.keySet()) {
            CompiledLibrary compiled = libraryManager.resolveLibrary(id, CqlTranslatorOptions.defaultOptions(), errors);
            executableLibrariesJson.add(this.readJson(this.convertToJson(compiled.getLibrary())));
        }

        // Testing JSON Export/Import
        CqlEngine engine = new CqlEngine(new InMemoryLibraryLoader(executableLibrariesJson));
        EvaluationResult result = engine.evaluate("Test", new HashSet<>(Arrays.asList("X", "Y", "W")));

        assertNotNull(result);
        assertEquals(result.expressionResults.size(), 3);
        assertThat(result.forExpression("X").value(), is(10));
        assertThat(result.forExpression("Y").value(), is(4));
        assertThat(result.forExpression("W").value(), is(15));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void validationEnabled_validatesTerminology() throws IOException  {
        Library library = this.toLibrary("library Test version '1.0.0'\ncodesystem \"X\" : 'http://example.com'\ndefine X:\n5+5\ndefine Y: 2 + 2");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader, EnumSet.of(CqlEngine.Options.EnableValidation));
        engine.evaluate("Test");
    }

    @Test
    public void validationDisabled_doesNotValidateTerminology() throws IOException {
        Library library = this.toLibrary("library Test version '1.0.0'\ncodesystem \"X\" : 'http://example.com'\ndefine X:\n5+5\ndefine Y: 2 + 2");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);
        engine.evaluate("Test");
    }
}
