package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;
import org.hamcrest.Matchers;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.*;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TranslationTests {
    // TODO: sameXMLAs? Couldn't find such a thing in hamcrest, but I don't want this to run on the JSON, I want it to verify the actual XML.
    @Test(enabled=false)
    public void testPatientPropertyAccess() throws IOException, JAXBException {
        File expectedXmlFile = new File(Cql2ElmVisitorTest.class.getResource("PropertyTest_ELM.xml").getFile());
        String expectedXml = new Scanner(expectedXmlFile, "UTF-8").useDelimiter("\\Z").next();

        File propertyTestFile = new File(Cql2ElmVisitorTest.class.getResource("PropertyTest.cql").getFile());
        ModelManager modelManager = new ModelManager();
        String actualXml = CqlTranslator.fromFile(propertyTestFile, new LibraryManager(modelManager)).toXml();
        assertThat(actualXml, is(expectedXml));
    }

    @Test(enabled=false)
    public void testForPrintElm() throws IOException, JAXBException{
        File propertyTestFile = new File(TranslationTests.class.getResource("LibraryTests/SupplementalDataElements_FHIR4-2.0.0.cql").getFile());
        ModelManager modelManager = new ModelManager();

        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All, CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableAnnotations,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation);

        CqlTranslator translator = CqlTranslator.fromFile(propertyTestFile, new LibraryManager(modelManager, compilerOptions));
        System.out.println(translator.toJson());
    }

    @Test(enabled=false)
    public void testCMS146v2XML() throws IOException {
        String expectedXml = "";
        File cqlFile = new File(Cql2ElmVisitorTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
        ModelManager modelManager = new ModelManager();
        String actualXml = CqlTranslator.fromFile(cqlFile, new LibraryManager(modelManager)).toXml();
        assertThat(actualXml, is(expectedXml));
    }

    @Test
    public void testIdentifierLocation() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("TranslatorTests/UnknownIdentifier.cql");
        assertEquals(1, translator.getErrors().size());

        CqlCompilerException e = translator.getErrors().get(0);
        TrackBack tb = e.getLocator();

        assertEquals(6, tb.getStartLine());
        assertEquals(6, tb.getEndLine());

        assertEquals(5, tb.getStartChar());
        assertEquals(10, tb.getEndChar());
    }

    @Test
    public void testAnnotationsPresent() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("CMS146v2_Test_CQM.cql", CqlCompilerOptions.Options.EnableAnnotations);
        assertEquals(0, translator.getErrors().size());
        List<ExpressionDef> defs = translator.getTranslatedLibrary().getLibrary().getStatements().getDef();
        assertNotNull(defs.get(1).getAnnotation());
        assertTrue(defs.get(1).getAnnotation().size() > 0);
    }

    @Test
    public void testAnnotationsAbsent() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("CMS146v2_Test_CQM.cql");
        assertEquals(0, translator.getErrors().size());
        List<ExpressionDef> defs = translator.getTranslatedLibrary().getLibrary().getStatements().getDef();
        assertTrue(defs.get(1).getAnnotation().size() == 0);
    }

    @Test
    public void testTranslatorOptionsPresent() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("CMS146v2_Test_CQM.cql", CqlCompilerOptions.Options.EnableAnnotations);
        assertEquals(0, translator.getErrors().size());
        Library library = translator.getTranslatedLibrary().getLibrary();
        assertNotNull(library.getAnnotation());
        assertThat(library.getAnnotation().size(), greaterThan(0));
        assertThat(library.getAnnotation().get(0), instanceOf(CqlToElmInfo.class));
        CqlToElmInfo info = (CqlToElmInfo)library.getAnnotation().get(0);
        assertThat(info.getTranslatorOptions(), is("EnableAnnotations"));
    }

    @Test
    public void testNoImplicitCasts() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("TestNoImplicitCast.cql");
        assertEquals(0, translator.getErrors().size());
        // Gets the "TooManyCasts" define
        Expression exp = translator.getTranslatedLibrary().getLibrary().getStatements().getDef().get(2).getExpression();
        assertThat(exp, is(instanceOf(Query.class)));

        Query query = (Query)exp;
        ReturnClause returnClause = query.getReturn();
        assertNotNull(returnClause);
        assertNotNull(returnClause.getExpression());
        assertThat(returnClause.getExpression(), is(instanceOf(FunctionRef.class)));

        FunctionRef functionRef = (FunctionRef)returnClause.getExpression();
        assertEquals(1, functionRef.getOperand().size());

        // For a widening cast, no As is required, it should be a direct property access.
        Expression operand = functionRef.getOperand().get(0);
        assertThat(operand, is(instanceOf(Property.class)));

        // Gets the "NeedsACast" define
        exp = translator.getTranslatedLibrary().getLibrary().getStatements().getDef().get(4).getExpression();
        assertThat(exp, is(instanceOf(Query.class)));

        query = (Query)exp;
        returnClause = query.getReturn();
        assertNotNull(returnClause);
        assertNotNull(returnClause.getExpression());
        assertThat(returnClause.getExpression(), is(instanceOf(FunctionRef.class)));

        functionRef = (FunctionRef)returnClause.getExpression();
        assertEquals(1, functionRef.getOperand().size());

        // For narrowing choice casts, an As is expected
        operand = functionRef.getOperand().get(0);
        assertThat(operand, is(instanceOf(As.class)));

        As as = (As)operand;
        assertThat(as.getAsTypeSpecifier(), is(instanceOf(ChoiceTypeSpecifier.class)));
    }

    @Test
    public void tenDividedByTwo() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("TenDividedByTwo.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void divideMultiple() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("DivideMultiple.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void divideVariables() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("DivideVariables.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void arithmetic_Mixed() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("Arithmetic_Mixed.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void arithmetic_Parenthetical() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("Arithmetic_Parenthetical.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void roundUp() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("RoundUp.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void roundDown() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("RoundDown.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void log_BaseTen() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("Log_BaseTen.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void median_odd() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("Median_odd.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void median_dup_vals_odd() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("Median_dup_vals_odd.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void geometricMean_Zero() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("GeometricMean_Zero.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    @Ignore("Could not resolve call to operator Equal with signature (tuple{Foo:System.Any},tuple{Bar:System.Any}")
    public void tupleDifferentKeys() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("TupleDifferentKeys.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    @Ignore("Could not resolve call to operator Equal with signature (tuple{a:System.String,b:System.Any},tuple{a:System.String,c:System.Any})")
    public void uncertTuplesWithDiffNullFields() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("UncertTuplesWithDiffNullFields.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    @Ignore("Could not resolve call to operator Collapse with signature (System.Any,System.Quantity)")
    public void nullIvlCollapse_NullCollapse() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("NullIvlCollapse_NullCollapse.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void median_q_diff_units() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("Median_q_diff_units.cql");
        assertEquals("Errors: " + translator.getErrors(), 0, translator.getErrors().size());
    }

    @Test
    public void testForwardDeclarationSameTypeDifferentNamespaceNormalTypes() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("TestForwardDeclarationSameTypeDifferentNamespaceNormalTypes.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), Matchers.equalTo(0));

        Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), Matchers.equalTo(3));
    }

    @Test
    public void testForwardDeclarationSameTypeDifferentNamespaceGenericTypes() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslator("TestForwardDeclarationSameTypeDifferentNamespaceGenericTypes.cql");
        assertThat("Errors: " + translator.getErrors(), translator.getErrors().size(), Matchers.equalTo(0));

        Library compileLibrary = translator.getTranslatedLibrary().getLibrary();
        List<ExpressionDef> statements = compileLibrary.getStatements().getDef();
        assertThat(statements.size(), Matchers.equalTo(3));
    }

    // This test creates a bunch of translators on the common pool to suss out any race conditions.
    // It's not fool-proof, but is reasonably consistent on my local machine.
    @Test
    public void multiThreadedTranslation() throws IOException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    TestUtils.createTranslator("CMS146v2_Test_CQM.cql");
                }
                catch(IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        CompletableFuture<?>[] cfs = futures.toArray(new CompletableFuture[0]);

        CompletableFuture.allOf(cfs).join();
    }

    @Test
    public void testHidingVariousUseCases() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("HidingTests/TestHidingVariousUseCases.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());

        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(13));

        final List<String> distinct = warningMessages.stream().distinct().collect(Collectors.toList());

        assertThat(warningMessages.toString(), distinct.size(), is(11));

        final String hidingDefinition = "An alias identifier [Definition] is hiding another identifier of the same name. \n";
        final String hidingVarLet = "A let identifier [var] is hiding another identifier of the same name. \n";
        final String hidingContextValueSet = "An alias identifier [ValueSet] is hiding another identifier of the same name. \n";
        final String hidingLetValueSet = "A let identifier [ValueSet] is hiding another identifier of the same name. \n";
        final String hidingContextCode = "An alias identifier [Code] is hiding another identifier of the same name. \n";
        final String hidingLetCode = "A let identifier [Code] is hiding another identifier of the same name. \n";
        final String hidingContextCodeSystem = "An alias identifier [CodeSystem] is hiding another identifier of the same name. \n";
        final String hidingLetCodeSystem = "A let identifier [CodeSystem] is hiding another identifier of the same name. \n";
        final String hidingContextFhir = "An alias identifier [FHIR] is hiding another identifier of the same name. \n";
        final String hidingLetFhir = "A let identifier [FHIR] is hiding another identifier of the same name. \n";
        final String hidingAliasLet = "A let identifier [Alias] is hiding another identifier of the same name. \n";

        assertThat(distinct, containsInAnyOrder(hidingDefinition, hidingVarLet, hidingContextValueSet, hidingLetValueSet, hidingContextCode, hidingLetCode, hidingContextCodeSystem, hidingLetCodeSystem, hidingContextFhir, hidingLetFhir, hidingAliasLet));
    }
}
