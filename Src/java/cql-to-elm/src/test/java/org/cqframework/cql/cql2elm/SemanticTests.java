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
    public void invalidEquality() throws IOException {
        runSemanticTest("InvalidEquality.cql", 1, CqlTranslator.Options.DisableListPromotion);
    }

    @Test
    public void testDoubleListPromotion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestDoubleListPromotion.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Observations");
        Retrieve retrieve = (Retrieve)def.getExpression();
        Expression codes = retrieve.getCodes();
        assertThat(codes, instanceOf(ToList.class));
        assertThat(((ToList)codes).getOperand(), instanceOf(CodeRef.class));
    }

    @Test
    public void testIntervalImplicitConversion() throws IOException {
        runSemanticTest("TestIntervalImplicitConversion.cql");
    }

    @Test
    public void testImplicitFHIRHelpers() throws IOException {
        runSemanticTest("TestImplicitFHIRHelpers.cql");
    }

    @Test
    public void testImplicitFHIRHelpers_FHIR4() throws IOException {
        runSemanticTest("TestImplicitFHIRHelpers_FHIR4.cql");
    }

    @Test
    public void testChoiceDateRangeOptimization_FHIR3() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestChoiceDateRangeOptimization_FHIR3.cql", 0, CqlTranslator.Options.EnableDateRangeOptimization);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        /*
         <expression localId="25" locator="10:23-10:81" xsi:type="Query">
            <resultTypeSpecifier xsi:type="ListTypeSpecifier">
               <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
            </resultTypeSpecifier>
            <source localId="20" locator="10:23-10:35" alias="C">
               <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                  <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
               </resultTypeSpecifier>
               <expression localId="19" locator="10:23-10:33" dataType="fhir:Condition" dateProperty="assertedDate" xsi:type="Retrieve">
                  <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                     <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
                  </resultTypeSpecifier>
                  <dateRange localId="23" locator="10:65-10:81" name="MeasurementPeriod" xsi:type="ParameterRef">
                     <resultTypeSpecifier xsi:type="IntervalTypeSpecifier">
                        <pointType name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                     </resultTypeSpecifier>
                  </dateRange>
               </expression>
            </source>
         </expression>
         */

        ExpressionDef expressionDef = defs.get("DateCondition");
        assertThat(expressionDef.getExpression(), instanceOf(Query.class));
        Query query = (Query)expressionDef.getExpression();
        assertThat(query.getSource().size(), is(1));
        assertThat(query.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        Retrieve retrieve = (Retrieve)query.getSource().get(0).getExpression();
        assertThat(retrieve.getDateProperty(), is("assertedDate"));
        assertThat(retrieve.getDateRange(), instanceOf(ParameterRef.class));

        /*
         <expression localId="35" locator="11:35-11:101" xsi:type="Query">
            <resultTypeSpecifier xsi:type="ListTypeSpecifier">
               <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
            </resultTypeSpecifier>
            <source localId="28" locator="11:35-11:47" alias="C">
               <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                  <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
               </resultTypeSpecifier>
               <expression localId="27" locator="11:35-11:45" dataType="fhir:Condition" dateProperty="onset" xsi:type="Retrieve">
                  <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                     <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
                  </resultTypeSpecifier>
                  <dateRange localId="33" locator="11:85-11:101" name="MeasurementPeriod" xsi:type="ParameterRef">
                     <resultTypeSpecifier xsi:type="IntervalTypeSpecifier">
                        <pointType name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                     </resultTypeSpecifier>
                  </dateRange>
               </expression>
            </source>
         </expression>
         */

        expressionDef = defs.get("ChoiceTypePeriodCondition");
        assertThat(expressionDef.getExpression(), instanceOf(Query.class));
        query = (Query)expressionDef.getExpression();
        assertThat(query.getSource().size(), is(1));
        assertThat(query.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve)query.getSource().get(0).getExpression();
        assertThat(retrieve.getDateProperty(), is("onset"));
        assertThat(retrieve.getDateRange(), instanceOf(ParameterRef.class));
    }

    @Test
    public void testChoiceDateRangeOptimization_FHIR4() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestChoiceDateRangeOptimization_FHIR4.cql", 0, CqlTranslator.Options.EnableDateRangeOptimization);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        /*
         <expression localId="25" locator="10:23-10:81" xsi:type="Query">
            <resultTypeSpecifier xsi:type="ListTypeSpecifier">
               <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
            </resultTypeSpecifier>
            <source localId="20" locator="10:23-10:35" alias="C">
               <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                  <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
               </resultTypeSpecifier>
               <expression localId="19" locator="10:23-10:33" dataType="fhir:Condition" dateProperty="recordedDate" xsi:type="Retrieve">
                  <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                     <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
                  </resultTypeSpecifier>
                  <dateRange localId="23" locator="10:65-10:81" name="MeasurementPeriod" xsi:type="ParameterRef">
                     <resultTypeSpecifier xsi:type="IntervalTypeSpecifier">
                        <pointType name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                     </resultTypeSpecifier>
                  </dateRange>
               </expression>
            </source>
         </expression>
         */

        ExpressionDef expressionDef = defs.get("DateCondition");
        assertThat(expressionDef.getExpression(), instanceOf(Query.class));
        Query query = (Query)expressionDef.getExpression();
        assertThat(query.getSource().size(), is(1));
        assertThat(query.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        Retrieve retrieve = (Retrieve)query.getSource().get(0).getExpression();
        assertThat(retrieve.getDateProperty(), is("recordedDate"));
        assertThat(retrieve.getDateRange(), instanceOf(ParameterRef.class));

        /*
         <expression localId="35" locator="11:35-11:101" xsi:type="Query">
            <resultTypeSpecifier xsi:type="ListTypeSpecifier">
               <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
            </resultTypeSpecifier>
            <source localId="28" locator="11:35-11:47" alias="C">
               <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                  <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
               </resultTypeSpecifier>
               <expression localId="27" locator="11:35-11:45" dataType="fhir:Condition" dateProperty="onset" xsi:type="Retrieve">
                  <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                     <elementType name="fhir:Condition" xsi:type="NamedTypeSpecifier"/>
                  </resultTypeSpecifier>
                  <dateRange localId="33" locator="11:85-11:101" name="MeasurementPeriod" xsi:type="ParameterRef">
                     <resultTypeSpecifier xsi:type="IntervalTypeSpecifier">
                        <pointType name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                     </resultTypeSpecifier>
                  </dateRange>
               </expression>
            </source>
         </expression>
         */

        expressionDef = defs.get("ChoiceTypePeriodCondition");
        assertThat(expressionDef.getExpression(), instanceOf(Query.class));
        query = (Query)expressionDef.getExpression();
        assertThat(query.getSource().size(), is(1));
        assertThat(query.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve)query.getSource().get(0).getExpression();
        assertThat(retrieve.getDateProperty(), is("onset"));
        assertThat(retrieve.getDateRange(), instanceOf(ParameterRef.class));
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
}
