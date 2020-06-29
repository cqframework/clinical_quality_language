package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
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
    public void testInValueSet() throws IOException {
        runSemanticTest("InValueSetTest.cql");
    }

    @Test
    public void testTerminologyReferences() throws IOException {
        runSemanticTest("OperatorTests/TerminologyReferences.cql", 0);
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
    public void testInvalidParameters() throws IOException {
        runSemanticTest("ParameterTestInvalid.cql", 17);
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

    @Test
    public void testInvalidCastExpression() throws IOException {
        runSemanticTest("OperatorTests/InvalidCastExpression.cql", 1);
    }

    @Test
    public void testForwardReferences() throws IOException {
        runSemanticTest("OperatorTests/ForwardReferences.cql", 0);
    }

    @Test
    public void testRecursiveFunctions() throws IOException {
        runSemanticTest("OperatorTests/RecursiveFunctions.cql", 1);
    }

    @Test
    public void testNameHiding() throws IOException {
        runSemanticTest("OperatorTests/NameHiding.cql", 1);
    }

    @Test
    public void testSorting() throws IOException {
        runSemanticTest("OperatorTests/Sorting.cql", 1);
    }

    @Test
    public void testInvalidSortClauses() throws IOException {
        runSemanticTest("OperatorTests/InvalidSortClauses.cql", 3);
    }

    @Test
    public void testUndeclaredForward() throws IOException {
        runSemanticTest("OperatorTests/UndeclaredForward.cql", 1);
    }

    @Test
    public void testUndeclaredSignature() throws IOException {
        runSemanticTest("OperatorTests/UndeclaredSignature.cql", 1);
    }

    @Test
    public void testMessageOperators() throws IOException {
        runSemanticTest("OperatorTests/MessageOperators.cql", 0);
    }

    @Test
    public void testMultiSourceQuery() throws IOException {
        runSemanticTest("OperatorTests/MultiSourceQuery.cql", 0);
    }

    @Test
    public void testQuery() throws IOException {
        runSemanticTest("OperatorTests/Query.cql", 0);
    }

    // NOTE: This test is commented out to an issue with the ANTLR tooling. In 4.5, this test documents the
    // unacceptable performance of the parser. In 4.6+, the parser does not correctly resolve some types of
    // expressions (see TricksyParse and ShouldFail). See Github issue [#343](https://github.com/cqframework/clinical_quality_language/issues/343)
    // for more detail.
    //@Test
    //public void testParserPerformance() throws IOException {
    //    runSemanticTest("ParserPerformance.cql");
    //}

    @Test
    public void tricksyParse() throws IOException {
        runSemanticTest("TricksyParse.cql");
    }

    @Test
    public void shouldFail() throws IOException {
        runSemanticTest("ShouldFail.cql", 1);
    }

    @Test
    public void invalidEquality() throws IOException {
        runSemanticTest("InvalidEquality.cql", 1, CqlTranslator.Options.DisableListPromotion);
    }

    @Test
    public void testRelatedContextRetrieve() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestRelatedContextRetrieve.cql", 0);
        org.hl7.elm.r1.Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Estimated Due Date");
        Last last = (Last)def.getExpression();
        Query query = (Query)last.getSource();
        AliasedQuerySource source = query.getSource().get(0);
        Retrieve retrieve = (Retrieve)source.getExpression();
        ExpressionRef mother = (ExpressionRef)retrieve.getContext();
        assertThat(mother.getName(), is("Mother"));
    }

    private void runSemanticTest(String testFileName) throws IOException {
        runSemanticTest(testFileName, 0);
    }

    private void runSemanticTest(String testFileName, int expectedErrors) throws IOException {
        TestUtils.runSemanticTest(testFileName, expectedErrors);
    }

    private void runSemanticTest(String testFileName, int expectedErrors, CqlTranslator.Options... options) throws IOException {
        TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }
}
