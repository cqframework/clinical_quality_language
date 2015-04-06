package org.cqframework.cql.cql2elm;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SemanticTests {

    @Test
    public void testTranslations() throws IOException {
        runSemanticTest("TranslationTests.cql");
    }

    @Test
    public void testIn() throws IOException {
        runSemanticTest("InTest.cql");
    }

    @Test
    public void testProperties() throws IOException {
        runSemanticTest("PropertyTest.cql");
    }

    @Test
    public void testParameters() throws IOException {
        runSemanticTest("ParameterTest.cql");
    }

    @Test
    public void testSignatureResolution() throws IOException {
        runSemanticTest("SignatureResolutionTest.cql");
    }

    @Test
    public void testCMS146v2() throws IOException {
        runSemanticTest("CMS146v2_Test_CQM.cql");
    }
    
    @Test
    public void testAggregateOperators() throws IOException {
        runSemanticTest("OperatorTests/AggregateOperators.cql");
    }

    @Test
    public void testArithmeticOperators() throws IOException {
        runSemanticTest("OperatorTests/ArithmeticOperators.cql");
    }

    @Test
    public void testComparisonOperators() throws IOException {
        runSemanticTest("OperatorTests/ComparisonOperators.cql");
    }

    @Test
    public void testDateTimeOperators() throws IOException {
        runSemanticTest("OperatorTests/DateTimeOperators.cql");
    }

    @Test
    public void testIntervalOperators() throws IOException {
        runSemanticTest("OperatorTests/IntervalOperators.cql");
    }

    @Test
    public void testIntervalOperatorPhrases() throws IOException {
        runSemanticTest("OperatorTests/IntervalOperatorPhrases.cql");
    }

    @Test
    public void testListOperators() throws IOException {
        runSemanticTest("OperatorTests/ListOperators.cql");
    }

    @Test
    public void testLogicalOperators() throws IOException {
        runSemanticTest("OperatorTests/LogicalOperators.cql");
    }

    @Test
    public void testNullologicalOperators() throws IOException {
        runSemanticTest("OperatorTests/NullologicalOperators.cql");
    }

    @Test
    public void testStringOperators() throws IOException {
        runSemanticTest("OperatorTests/StringOperators.cql");
    }

    @Test
    public void testTimeOperators() throws IOException {
        runSemanticTest("OperatorTests/TimeOperators.cql");
    }
    @Test
    public void testTypeOperators() throws IOException {
        runSemanticTest("OperatorTests/TypeOperators.cql");
    }

    @Test
    public void testImplicitConversions() throws IOException {
        runSemanticTest("OperatorTests/ImplicitConversions.cql");
    }

    @Test
    public void testTupleAndClassConversions() throws IOException {
        runSemanticTest("OperatorTests/TupleAndClassConversions.cql");
    }

    @Test
    public void testFunctions() throws IOException {
        runSemanticTest("OperatorTests/Functions.cql");
    }

    @Test
    public void testDateTimeLiteral() throws IOException {
        runSemanticTest("DateTimeLiteralTest.cql");
    }

    @Test
    public void testCodeAndConcepts() throws IOException {
        runSemanticTest("CodeAndConceptTest.cql");
    }

    private void runSemanticTest(String testFileName) throws IOException {
        File translationTestFile = new File(Cql2ElmVisitorTest.class.getResource(testFileName).getFile());
        CqlTranslator translator = CqlTranslator.fromFile(translationTestFile);
        for (CqlTranslatorException error : translator.getErrors()) {
            System.err.println(String.format("(%d,%d): %s",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage()));
        }
        assertThat(translator.getErrors().size(), is(0));
    }
}
