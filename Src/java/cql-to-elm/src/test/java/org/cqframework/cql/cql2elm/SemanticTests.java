package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        CqlTranslator translator = runSemanticTest("OperatorTests/IntervalOperatorPhrases.cql");
        Library library = translator.toELM();
        ExpressionDef pointWithin = getExpressionDef(library, "PointWithin");
        assertThat(pointWithin.getExpression(), instanceOf(And.class));
        ExpressionDef pointProperlyWithin = getExpressionDef(library, "PointProperlyWithin");
        assertThat(pointProperlyWithin.getExpression(), instanceOf(In.class));
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
    public void testCompatibilityLevel3() throws IOException {
        runSemanticTest("TestCompatibilityLevel3.cql", 1);
        runSemanticTest("TestCompatibilityLevel3.cql", 0, new CqlTranslatorOptions().withCompatibilityLevel("1.3"));
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

    @Test
    public void testIssue547() throws IOException {
        TestUtils.runSemanticTest("Issue547.cql", 3);
    }

    @Test
    public void testIssue558() throws IOException {
        TestUtils.runSemanticTest("Issue558.cql", 1);
    }

    @Test
    public void testIssue581() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue581.cql", 0);
        Library library = translator.toELM();
        assertThat(library.getStatements(), notNullValue());
        assertThat(library.getStatements().getDef(), notNullValue());
        assertThat(library.getStatements().getDef().size(), equalTo(1));
        assertThat(library.getStatements().getDef().get(0), instanceOf(FunctionDef.class));
        FunctionDef fd = (FunctionDef)library.getStatements().getDef().get(0);
        assertThat(fd.getExpression(), instanceOf(If.class));
        If i = (If)fd.getExpression();
        assertThat(i.getCondition(), instanceOf(Not.class));
    }

    @Test
    public void testIssue405() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue405.cql", 0, CqlTranslator.Options.EnableAnnotations);
        Library library = translator.toELM();
        assertThat(library.getStatements().getDef().size(), equalTo(6));
        assertThat(library.getStatements().getDef().get(3), instanceOf(ExpressionDef.class));
        ExpressionDef expressionDef = (ExpressionDef) library.getStatements().getDef().get(3);
        assertThat(expressionDef.getExpression().getLocalId(), notNullValue());

    }

    @Test
    public void testIssue395() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue395.cql", 0, CqlTranslator.Options.EnableAnnotations);
        Library library = translator.toELM();
        ExpressionDef expressionDef = (ExpressionDef) library.getStatements().getDef().get(2);
        assertThat(expressionDef.getExpression().getLocalId(), notNullValue());
    }

    @Test
    public void testIssue587() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue587.cql", 2);
        // This doesn't resolve correctly, collapse null should work, but it's related to this issue:
        // [#435](https://github.com/cqframework/clinical_quality_language/issues/435)
        // So keeping as a verification of current behavior here, will address as part of vNext
        assertThat(translator.getErrors().size(), equalTo(2));
    }

    @Test
    public void testIssue592() throws IOException {
        TestUtils.runSemanticTest("Issue592.cql", 0, new CqlTranslatorOptions().withCompatibilityLevel("1.3"));
    }

    @Test
    public void testIssue596() throws IOException {
        // NOTE: This test is susceptible to constant folding optimization...
        CqlTranslator translator = TestUtils.runSemanticTest("Issue596.cql", 0);
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("NullBeforeInterval");
        /*
        define NullBeforeInterval:
            (null as Integer) before Interval[1, 10]

          <before>
            <if>
              <isNull>
                <as>
                  <null/>
                </as>
              </isNull>
              <null>
              </null>
              <interval>
              </interval>
            </if>
            <interval>
            </interval>
          </before>
         */
        assertThat(ed.getExpression(), instanceOf(Before.class));
        Before b = (Before)ed.getExpression();
        assertThat(b.getOperand(), notNullValue());
        assertThat(b.getOperand().size(), equalTo(2));
        assertThat(b.getOperand().get(0), instanceOf(If.class));
        assertThat(b.getOperand().get(1), instanceOf(Interval.class));
        If i = (If)b.getOperand().get(0);
        assertThat(i.getCondition(), instanceOf(IsNull.class));
        assertThat(i.getThen(), instanceOf(Null.class));
        assertThat(i.getElse(), instanceOf(Interval.class));
        IsNull isNull = (IsNull)i.getCondition();
        assertThat(isNull.getOperand(), instanceOf(As.class));
        As a = (As)isNull.getOperand();
        assertThat(a.getOperand(), instanceOf(Null.class));
    }

    private CqlTranslator runSemanticTest(String testFileName) throws IOException {
        return runSemanticTest(testFileName, 0);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlTranslator.Options... options) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlTranslatorOptions options) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }
}
