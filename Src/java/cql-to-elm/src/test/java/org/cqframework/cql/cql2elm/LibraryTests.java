package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.cql2elm.tracking.Trackable;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class LibraryTests {

    private static ModelManager modelManager;
    private static LibraryManager libraryManager;

    @BeforeAll
    static void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
    }

    @AfterAll
    static void tearDown() {
        libraryManager.getLibrarySourceLoader().clearProviders();
    }

    @Test
    void libraryReferences() {
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
    void includedLibraryWithSignatures() {
        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, SignatureLevel.All);
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
            assertThat(
                    ((AggregateExpression) baseLibDef.getExpression())
                            .getSignature()
                            .size(),
                    is(1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void alphanumericVersionIssue641() {
        // the issue identified with using DefaultLibrarySourceLoader only; thus
        // creating a fresh set below
        ModelManager modelManager = new ModelManager();

        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, SignatureLevel.All);
        LibraryManager libraryManager = new LibraryManager(modelManager, compilerOptions);

        InputStream translationTestFile = LibraryTests.class.getResourceAsStream("LibraryTests/Issue641.cql");
        libraryManager
                .getLibrarySourceLoader()
                .registerProvider(new DefaultLibrarySourceProvider(Paths.get(new File(LibraryTests.class
                                .getResource("LibraryTests/Issue641.cql")
                                .getFile())
                        .getParent())));

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
            assertThat(
                    ((AggregateExpression) baseLibDef.getExpression())
                            .getSignature()
                            .size(),
                    is(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void invalidLibraryReferences() {
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
    void privateAccessModifierReferencing() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/AccessModifierReferencing.cql");
        assertThat(translator.getErrors().size(), is(not(0)));

        Set<String> errors = translator.getErrors().stream()
                .map(CqlCompilerException::getMessage)
                .collect(Collectors.toSet());

        assertTrue(
                errors.contains(
                        "Identifier ICD-10:2014 in library Base is marked private and cannot be referenced from another library."));
        assertTrue(
                errors.contains(
                        "Identifier f1 in library AccessModifierBase is marked private and cannot be referenced from another library."));
        assertTrue(
                errors.contains(
                        "Identifier PrivateExpression in library Base is marked private and cannot be referenced from another library."));
        assertTrue(
                errors.contains(
                        "Identifier Test Parameter in library Base is marked private and cannot be referenced from another library."));
        assertTrue(
                errors.contains(
                        "Identifier Female Administrative Sex in library Base is marked private and cannot be referenced from another library."));
        assertTrue(
                errors.contains(
                        "Identifier XYZ Code in library Base is marked private and cannot be referenced from another library."));
        assertTrue(
                errors.contains(
                        "Identifier XYZ Concept in library Base is marked private and cannot be referenced from another library."));
    }

    @Test
    void privateAccessModifierNonReferencing() throws IOException {
        CqlTranslator translator =
                TestUtils.createTranslatorFromStream("LibraryTests/AccessModifierNonReferencing.cql");
        assertThat(translator.getErrors().size(), is(0));
    }

    @Test
    void invalidLibraryReference() {
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
    void duplicateExpressionLibrary() {
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
    void missingLibrary() {
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
    void invalidBaseLibrary() {
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
    void mixedVersionModelReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    LibraryTests.class.getResourceAsStream("LibraryTests/TestMeasure.cql"), libraryManager);
            assertThat(translator.getErrors().size(), is(3));

            for (CqlCompilerException error : translator.getErrors()) {
                assertThat(error.getLocator(), notNullValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void translatorOptionsFlowDownWithAnnotations() {
        try {
            // Test Annotations are created for both libraries
            var options = new CqlCompilerOptions(
                    CqlCompilerException.ErrorSeverity.Info,
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
                assertTrue(includedLibrary.getStatements().getDef().stream()
                                .filter(x -> x.getAnnotation().size() > 0)
                                .count()
                        > 0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void translatorOptionsFlowDownWithoutAnnotations() {
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
                assertEquals(
                        0,
                        includedLibrary.getStatements().getDef().stream()
                                .filter(x -> x.getAnnotation().size() > 0)
                                .count());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void syntaxErrorWithNoLibrary() throws IOException {
        // Syntax errors in anonymous libraries are reported with the name of the source
        // file as the library identifier
        CqlTranslator translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorWithNoLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(
                translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithNoLibrary"));
    }

    @Test
    void syntaxErrorWithNoLibraryFromStream() throws IOException {
        // Syntax errors in anonymous libraries are reported with the name of the source
        // file as the library identifier
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorWithNoLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("Anonymous"));
    }

    @Test
    void syntaxErrorWithLibrary() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorWithLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    @Test
    void syntaxErrorWithLibraryFromStream() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorWithLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(1));
        assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    @Test
    void syntaxErrorReferencingLibrary() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorReferencingLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(2));
        assertThat(
                translator.getErrors().get(0).getLocator().getLibrary().getId(),
                equalTo("SyntaxErrorReferencingLibrary"));
        assertThat(translator.getErrors().get(1).getLocator().getLibrary().getId(), equalTo("SyntaxErrorWithLibrary"));
    }

    @Test
    void syntaxErrorReferencingLibraryFromStream() throws IOException {
        CqlTranslator translator =
                TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorReferencingLibrary.cql");
        assertThat(translator.getErrors().size(), greaterThanOrEqualTo(2));
        assertThat(
                translator.getErrors().get(0).getLocator().getLibrary().getId(),
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
    void fluentFunctions1() throws IOException {
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
    void fluentFunctions2() throws IOException {
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
    void fluentFunctions5() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent5.cql");
        assertThat(translator.getErrors().size(), equalTo(1)); // Expects invalid invocation
        assertThat(
                translator.getErrors().get(0).getMessage(),
                equalTo(
                        "Operator invalidInvocation with signature (System.String) is a fluent function and can only be invoked with fluent syntax."));
    }

    @Test
    void fluentFunctions6() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent6.cql");
        assertThat(translator.getErrors().size(), equalTo(1)); // Expects invalid fluent invocation
        assertThat(
                translator.getErrors().get(0).getMessage(),
                equalTo(
                        "Invocation of operator invalidInvocation with signature (System.String) uses fluent syntax, but the operator is not defined as a fluent function."));
    }

    @Test
    void fluentFunctions7() throws IOException {
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
    void invalidInvocation() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/TestInvalidFunction.cql");
        assertThat(translator.getErrors().size(), equalTo(1));
        assertThat(
                translator.getErrors().get(0).getMessage(),
                equalTo("Could not resolve call to operator invalidInvocation with signature ()."));
    }

    @Test
    void expression() throws IOException {
        // This test checks to the that the engine can compile short snippets of CQL
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/expression.cql");
        assertThat(translator.getErrors().size(), equalTo(0));

        var compileLibrary = translator.getTranslatedLibrary().getLibrary();
        var statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(1));
    }

    @Test
    void expression2() throws IOException {
        // This test checks to the that the engine can compile short snippets of CQL
        CqlTranslator translator = TestUtils.createTranslatorFromStream("LibraryTests/expression2.cql");
        assertThat(translator.getErrors().size(), equalTo(0));

        var compileLibrary = translator.getTranslatedLibrary().getLibrary();
        var statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(1));
    }

    @Test
    void forwardDeclaration() throws IOException {
        final CqlTranslator translator =
                TestUtils.createTranslatorFromStream("LibraryTests/TestForwardDeclaration.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(2));
    }

    @Test
    @Disabled("This fails")
    void forwardDeclarationsNormalType() throws IOException {
        final CqlTranslator translator =
                TestUtils.createTranslatorFromStream("LibraryTests/TestForwardDeclarationNormalType.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(6));
    }

    @Test
    @Disabled("This fails")
    void forwardDeclarationsGenericType() throws IOException {
        final CqlTranslator translator =
                TestUtils.createTranslatorFromStream("LibraryTests/TestForwardDeclarationGenericType.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(5));
    }

    @Test
    void forwardDeclarationsImplicitConversion() throws IOException {
        final CqlTranslator translator =
                TestUtils.createTranslatorFromStream("LibraryTests/TestForwardDeclarationImplicitConversion.cql");
        assertThat(translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(3));
    }

    @Test
    void forwardDeclarationsScoringImplicitConversion() throws IOException {
        CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversion.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(3));
        final Optional<ExpressionDef> toString = statements.stream()
                .filter(statement -> statement.getName().equals("toString"))
                .findFirst();
        assertTrue(toString.isPresent());

        final Expression expression = toString.get().getExpression();
        assertNotNull(expression);
        assertTrue(expression instanceof FunctionRef);

        final FunctionRef functionRef = (FunctionRef) expression;

        assertEquals("calledFunc", functionRef.getName());
        assertEquals(1, functionRef.getOperand().size());
        var operand = functionRef.getOperand().get(0);
        var resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());
    }

    @Test
    void forwardDeclarationsScoringImplicitConversionNonRelevantFunctionFirst() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversionNonRelevantFunctionFirst.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(3));
        final Optional<ExpressionDef> toString = statements.stream()
                .filter(statement -> statement.getName().equals("toString"))
                .findFirst();
        assertTrue(toString.isPresent());

        final Expression expression = toString.get().getExpression();
        assertNotNull(expression);
        assertTrue(expression instanceof FunctionRef);

        final FunctionRef functionRef = (FunctionRef) expression;

        assertEquals("calledFunc", functionRef.getName());
        assertEquals(1, functionRef.getOperand().size());
        var operand = functionRef.getOperand().get(0);
        var resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());
    }

    @Test
    void forwardDeclarationsScoringImplicitConversionMultipleParams() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversionMultipleParams.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(4));
        final Optional<ExpressionDef> toString = statements.stream()
                .filter(statement -> statement.getName().equals("caller"))
                .findFirst();
        assertTrue(toString.isPresent());

        final Expression expression = toString.get().getExpression();
        assertNotNull(expression);
        assertTrue(expression instanceof FunctionRef);

        final FunctionRef functionRef = (FunctionRef) expression;

        assertEquals("callee", functionRef.getName());
        assertEquals(3, functionRef.getOperand().size());

        var operand = functionRef.getOperand().get(0);
        var resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());

        operand = functionRef.getOperand().get(1);
        resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());
    }

    @Test
    void forwardDeclarationsScoringImplicitConversionMultipleParamsCannotResolve() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversionMultipleParamsCannotResolve.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(1));
    }

    @Test
    void nonForwardDeclarationsScoringImplicitConversion() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestNonForwardDeclarationScoringImplicitConversion.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(3));
        final Optional<ExpressionDef> toString = statements.stream()
                .filter(statement -> statement.getName().equals("toString"))
                .findFirst();
        assertTrue(toString.isPresent());

        final Expression expression = toString.get().getExpression();
        assertNotNull(expression);
        assertTrue(expression instanceof FunctionRef);

        final FunctionRef functionRef = (FunctionRef) expression;

        assertEquals("calledFunc", functionRef.getName());
        assertEquals(1, functionRef.getOperand().size());

        var operand = functionRef.getOperand().get(0);
        var resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());
    }

    @Test
    void nonForwardDeclarationsScoringImplicitConversionMultipleParams() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestNonForwardDeclarationScoringImplicitConversionMultipleParams.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(0));

        final Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        final List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), equalTo(3));
        final Optional<ExpressionDef> toString = statements.stream()
                .filter(statement -> statement.getName().equals("caller"))
                .findFirst();
        assertTrue(toString.isPresent());

        final Expression expression = toString.get().getExpression();
        assertNotNull(expression);
        assertTrue(expression instanceof FunctionRef);

        final FunctionRef functionRef = (FunctionRef) expression;

        assertEquals("callee", functionRef.getName());
        assertEquals(2, functionRef.getOperand().size());

        var operand = functionRef.getOperand().get(0);
        var resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());

        operand = functionRef.getOperand().get(1);
        resultType = Trackable.INSTANCE.getResultType(operand);
        assertEquals("System.Decimal", resultType.toString());
    }

    @Test
    void nonForwardDeclarationsScoringImplicitConversionMultipleParamsCannotResolve() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(
                "LibraryTests/TestNonForwardDeclarationScoringImplicitConversionMultipleParamsCannotResolve.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), equalTo(1));
    }

    private static final String FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE =
            "LibraryTests/TestForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql";
    private static final String NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE =
            "LibraryTests/TestNonForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql";

    private static Object[][] sigParams() {
        return new Object[][] {
            {FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.None},
            {FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.Differing},
            {FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.Overloads},
            {FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.All},
            {NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.None},
            {NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.Differing},
            {NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.Overloads},
            {NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE, SignatureLevel.All}
        };
    }

    @ParameterizedTest
    @MethodSource("sigParams")
    void forwardAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformationSignatureLevelNone(
            String testFileName, SignatureLevel signatureLevel) throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream(testFileName, signatureLevel);
        final int expectedWarningCount =
                (SignatureLevel.None == signatureLevel || SignatureLevel.Differing == signatureLevel) ? 2 : 0;
        assertThat(
                "Warnings: " + translator.getWarnings(),
                translator.getWarnings().size(),
                equalTo(expectedWarningCount));

        if (expectedWarningCount > 0) {
            assertThat(
                    translator.getWarnings().get(0).getMessage(),
                    equalTo(String.format(
                            "The function TestAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation.TestAny has multiple overloads and due to the SignatureLevel setting (%s), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime.",
                            signatureLevel.name())));
        }
    }
}
