package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.gen.cqlBaseListener;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.elm.r1.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class LibraryTests {

    ModelManager modelManager;
    LibraryManager libraryManager;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
    }

    @AfterClass
    public void tearDown() {
        libraryManager.getLibrarySourceLoader().clearProviders();
    }

    @Test
    public void testLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"), libraryManager);
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // @Test
    // public void testLibraryReferencesWithCacheDisabled() {
    // CqlTranslator translator = null;
    // try {
    // translator =
    // CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"),
    // modelManager, libraryManager.withDisableCache());
    // assertThat(translator.getErrors().size(), is(0));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    @Test
    public void testIncludedLibraryWithSignatures() {
        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info,
                SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, compilerOptions);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {
            var compiler = new CqlCompiler(libraryManager);
            compiler.run(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"));

            assertThat(compiler.getErrors().size(), is(0));

            Map<String, ExpressionDef> includedLibDefs = new HashMap<>();
            var includedLibraries = compiler.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                if (includedLibrary.getStatements() != null) {
                    for (ExpressionDef def : includedLibrary.getStatements().getDef()) {
                        includedLibDefs.put(def.getName(), def);
                    }
                }
            });

            ExpressionDef baseLibDef = includedLibDefs.get("BaseLibSum");
            assertThat(((AggregateExpression) baseLibDef.getExpression()).getSignature().size(), is(1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAlphanumericVersionIssue641() {
        // the issue identified with using DefaultLibrarySourceLoader only; thus
        // creating a fresh set below
        ModelManager modelManager = new ModelManager();

        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info,
                SignatureLevel.All);
        LibraryManager libraryManager = new LibraryManager(modelManager, compilerOptions);

        InputStream translationTestFile = LibraryTests.class.getResourceAsStream("LibraryTests/Issue641.cql");
        libraryManager.getLibrarySourceLoader().registerProvider(
                new DefaultLibrarySourceProvider(Paths.get(
                        new File(LibraryTests.class.getResource("LibraryTests/Issue641.cql").getFile()).getParent())));

        try {
            CqlCompiler compiler = new CqlCompiler(libraryManager);
            compiler.run(translationTestFile);

            System.out.println(compiler.getErrors());

            Map<String, ExpressionDef> includedLibDefs = new HashMap<>();
            var includedLibraries = compiler.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                if (includedLibrary.getStatements() != null) {
                    for (ExpressionDef def : includedLibrary.getStatements().getDef()) {
                        includedLibDefs.put(def.getName(), def);
                    }
                }
            });

            ExpressionDef baseLibDef = includedLibDefs.get("BaseLibSum");
            assertThat(((AggregateExpression) baseLibDef.getExpression()).getSignature().size(), is(1));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInvalidLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/InvalidReferencingLibrary.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidLibraryReference() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/InvalidLibraryReference.cql"), libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDuplicateExpressionLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/DuplicateExpressionLibrary.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMissingLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/MissingLibrary.cql"), libraryManager);
            assertThat(translator.getErrors().size(), is(1));
            assertThat(translator.getErrors().get(0), instanceOf(CqlCompilerException.class));
            assertThat(translator.getErrors().get(0).getCause(), instanceOf(CqlIncludeException.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidBaseLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingInvalidBaseLibrary.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(1));
            assertThat(translator.getErrors().get(0), instanceOf(CqlCompilerException.class));
            assertThat(translator.getErrors().get(0).getLocator(), notNullValue());
            assertThat(translator.getErrors().get(0).getLocator().getLibrary(), notNullValue());
            assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), is("InvalidBaseLibrary"));

            assertThat(translator.toELM(), notNullValue());
            assertThat(translator.toELM().getAnnotation(), notNullValue());
            assertThat(translator.toELM().getAnnotation().size(), greaterThan(0));
            CqlToElmError invalidBaseLibraryError = null;
            for (Object o : translator.toELM().getAnnotation()) {
                if (o instanceof CqlToElmError) {
                    invalidBaseLibraryError = (CqlToElmError) o;
                    break;
                }
            }
            assertThat(invalidBaseLibraryError, notNullValue());
            assertThat(invalidBaseLibraryError.getLibraryId(), is("InvalidBaseLibrary"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This test verifies that when a model load failure prevents proper creation of
    // the context expression, that doesn't lead to internal translator errors.
    @Test
    public void testMixedVersionModelReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator
                    .fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/TestMeasure.cql"), libraryManager);
            assertThat(translator.getErrors().size(), is(3));

            for (CqlCompilerException error : translator.getErrors()) {
                assertThat(error.getLocator(), notNullValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslatorOptionsFlowDownWithAnnotations() {
        try {
            // Test Annotations are created for both libraries
            var options = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    CqlCompilerOptions.Options.EnableAnnotations);
            libraryManager = new LibraryManager(modelManager, options);
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
            var compiler = new CqlCompiler(libraryManager);
            compiler.run(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"));

            assertThat(compiler.getErrors().size(), is(0));
            var includedLibraries = compiler.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                // Ensure that some annotations are present.
                assertTrue(includedLibrary.getStatements().getDef().stream().filter(x -> x.getAnnotation().size() > 0)
                        .count() > 0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslatorOptionsFlowDownWithoutAnnotations() {
        try {
            // Test Annotations are created for both libraries
            CqlCompiler compiler = null;
            libraryManager = new LibraryManager(modelManager, new CqlCompilerOptions());
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

            compiler = new CqlCompiler(libraryManager);
            compiler.run(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"));

            assertThat(compiler.getErrors().size(), is(0));
            var includedLibraries = compiler.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                // Ensure that no annotations are present.
                assertTrue(includedLibrary.getStatements().getDef().stream().filter(x -> x.getAnnotation().size() > 0)
                        .count() == 0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSyntaxErrorWithNoLibrary() throws IOException {
        // Syntax errors in anonymous libraries are reported with the name of the source
        // file as the library identifier
        CqlTranslator translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorWithNoLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(),
                equalTo("SyntaxErrorWithNoLibrary"));
    }

    @Test
    public void testSyntaxErrorWithNoLibraryFromStream() throws IOException {
        // Syntax errors in anonymous libraries are reported with the name of the source
        // file as the library identifier
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorWithNoLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("Anonymous"));
    }

    @Test
    public void testSyntaxErrorWithLibrary() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorWithLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    @Test
    public void testSyntaxErrorWithLibraryFromStream() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorWithLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    @Test
    public void testSyntaxErrorReferencingLibrary() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorReferencingLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(2));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(),
                equalTo("SyntaxErrorReferencingLibrary"));
        assertThat(translator.getErrors().get(1).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    @Test
    public void testSyntaxErrorReferencingLibraryFromStream() throws IOException {
        CqlTranslator translator = TestUtils
                .createTranslatorFromStream("LibraryTests/SyntaxErrorReferencingLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(2));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(),
                equalTo("SyntaxErrorReferencingLibrary"));
        assertThat(translator.getErrors().get(1).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    private ExpressionDef getExpressionDef(Library library, String name) {
        for (ExpressionDef def : library.getStatements().getDef()) {
            if (def.getName().equals(name)) {
                return def;
            }
        }
        throw new IllegalArgumentException(String.format("Could not resolve name %s", name));
    }

    @Test
    public void testFluentFunctions1() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent3.cql");
        assertThat(translator.getErrors().size(), equalTo(0));
        Library library = translator.toELM();
        ExpressionDef def = getExpressionDef(library, "Test");
        assertThat(def, notNullValue());
        Expression e = def.getExpression();
        assertThat(e, notNullValue());
        assertThat(e, instanceOf(Equal.class));
        Equal eq = (Equal) e;
        assertThat(eq.getOperand(), notNullValue());
        assertThat(eq.getOperand().size(), equalTo(2));
        assertThat(eq.getOperand().get(0), instanceOf(FunctionRef.class));
        assertThat(((FunctionRef) eq.getOperand().get(0)).getLibraryName(), equalTo("TestFluent1"));
    }

    @Test
    public void testFluentFunctions2() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent4.cql");
        assertThat(translator.getErrors().size(), equalTo(0));
        Library library = translator.toELM();
        ExpressionDef def = getExpressionDef(library, "Test");
        assertThat(def, notNullValue());
        Expression e = def.getExpression();
        assertThat(e, notNullValue());
        assertThat(e, instanceOf(Equal.class));
        Equal eq = (Equal) e;
        assertThat(eq.getOperand(), notNullValue());
        assertThat(eq.getOperand().size(), equalTo(2));
        assertThat(eq.getOperand().get(0), instanceOf(FunctionRef.class));
        assertThat(((FunctionRef) eq.getOperand().get(0)).getLibraryName(), equalTo("TestFluent2"));
    }

    @Test
    public void testFluentFunctions5() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent5.cql");
        assertThat(translator.getErrors().size(), equalTo(1)); // Expects invalid invocation
        assertThat(translator.getErrors().get(0).getMessage(), equalTo(
                "Operator invalidInvocation with signature (System.String) is a fluent function and can only be invoked with fluent syntax."));
    }

    @Test
    public void testFluentFunctions6() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent6.cql");
        assertThat(translator.getErrors().size(), equalTo(1)); // Expects invalid fluent invocation
        assertThat(translator.getErrors().get(0).getMessage(), equalTo(
                "Invocation of operator invalidInvocation with signature (System.String) uses fluent syntax, but the operator is not defined as a fluent function."));
    }

    @Test
    public void testFluentFunctions7() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent7.cql");
        assertThat(translator.getErrors().size(), equalTo(0));
        Library library = translator.toELM();
        ExpressionDef def = getExpressionDef(library, "Test");
        assertThat(def, notNullValue());
        Expression e = def.getExpression();
        assertThat(e, notNullValue());
        assertThat(e, instanceOf(Equal.class));
        Equal eq = (Equal) e;
        assertThat(eq.getOperand(), notNullValue());
        assertThat(eq.getOperand().size(), equalTo(2));
        assertThat(eq.getOperand().get(0), instanceOf(FunctionRef.class));
        assertThat(((FunctionRef) eq.getOperand().get(0)).getLibraryName(), equalTo("TF1"));
    }

    @Test
    public void testInvalidInvocation() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestInvalidFunction.cql");
        assertThat(translator.getErrors().size(), equalTo(1));
        assertThat(translator.getErrors().get(0).getMessage(),
                equalTo("Could not resolve call to operator invalidInvocation with signature ()."));
    }

    @Test
    public void testExpression() throws IOException {
        // This test checks to the that the engine can compile short snippets of CQL
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/expression.cql");
        assertThat(translator.getErrors().size(), equalTo(0));

        var compileLibrary = translator.getTranslatedLibrary().getLibrary();
        var statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(1));
    }

    @Test
    public void testExpression2() throws IOException {
        // This test checks to the that the engine can compile short snippets of CQL
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/expression2.cql");
        assertThat(translator.getErrors().size(), equalTo(0));

        var compileLibrary = translator.getTranslatedLibrary().getLibrary();
        var statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(1));
    }

    @Test
    @Ignore("bug discovered during 3.0 release, test added to track it")
    public void TestForwardDeclarations() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestForwardDeclaration.cql");
        assertThat(translator.getErrors().size(), equalTo(0));

        var compileLibrary = translator.getTranslatedLibrary().getLibrary();
        var statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(1));
    }

    // LUKETODO: test with FHIRHelpers when NOT calling overloaded method:  should still fail
    // LUKETODO: test with FHIRHelpers when calling one of overloaded ToInstant()
    @Test
    public void testForwardAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation_SignatureLevelNone() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql", SignatureLevel.None);
        // LUKETODO:  assert for the specific type of error, if possible
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testForwardAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation_SignatureLevelAll() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql", SignatureLevel.All);
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));
    }

    // LUKETODO:  parameterized
    @Test
    public void testNonForwwardAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation_SignatureLevelNone() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestNonForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql", SignatureLevel.None);
        // LUKETODO:  assert for the specific type of error, if possible
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));
    }

    @Test
    public void testNonForwardAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation_SignatureLevelAll() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestNonForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql", SignatureLevel.All);
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));
    }
}
