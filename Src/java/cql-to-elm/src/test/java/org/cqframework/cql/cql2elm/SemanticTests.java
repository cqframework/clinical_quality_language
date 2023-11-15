package org.cqframework.cql.cql2elm;

import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.NamedType;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertThrows;

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
        CqlTranslator translator = runSemanticTest("OperatorTests/TypeOperators.cql");
        org.hl7.elm.r1.Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("TestIf");
        assertThat(def.getResultType(), instanceOf(ChoiceType.class));
        ChoiceType choiceType = (ChoiceType)def.getResultType();
        DataType type = null;
        for (DataType dt : choiceType.getTypes()) {
            if (type == null) {
                type = dt;
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType)dt).getName(), equalTo("System.String"));
            }
            else {
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType)dt).getName(), equalTo("System.Boolean"));
            }
        }

        def = defs.get("TestCase");
        assertThat(def.getResultType(), instanceOf(ChoiceType.class));
        choiceType = (ChoiceType)def.getResultType();
        type = null;
        for (DataType dt : choiceType.getTypes()) {
            if (type == null) {
                type = dt;
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType)dt).getName(), equalTo("System.String"));
            }
            else {
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType)dt).getName(), equalTo("System.Boolean"));
            }
        }
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
        runSemanticTest("TestCompatibilityLevel3.cql", 0, new CqlCompilerOptions().withCompatibilityLevel("1.3"));
    }

    @Test
    public void invalidEquality() throws IOException {
        runSemanticTest("InvalidEquality.cql", 1, CqlCompilerOptions.Options.DisableListPromotion);
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
    public void testIssue616() throws IOException {
        TestUtils.runSemanticTest("Issue616.cql", 1);
    }

    public void testIssue617() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue617.cql", 0);
        Library library = translator.toELM();
        assertThat(library.getStatements(), notNullValue());
        assertThat(library.getStatements().getDef(), notNullValue());
        assertThat(library.getStatements().getDef().size(), equalTo(2));
        assertThat(library.getStatements().getDef().get(1), instanceOf(ExpressionDef.class));
        ExpressionDef expressionDef = (ExpressionDef)library.getStatements().getDef().get(1);
        assertThat(expressionDef.getExpression(), instanceOf(Implies.class));
        assertThat( expressionDef.getName(), is("Boolean Implies"));
        assertThat(((Implies)expressionDef.getExpression()).getOperand().size(), is(2));
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
        CqlTranslator translator = TestUtils.runSemanticTest("Issue405.cql", 0, CqlCompilerOptions.Options.EnableAnnotations);
        Library library = translator.toELM();
        assertThat(library.getStatements().getDef().size(), equalTo(6));
        assertThat(library.getStatements().getDef().get(3), instanceOf(ExpressionDef.class));
        ExpressionDef expressionDef = (ExpressionDef) library.getStatements().getDef().get(3);
        assertThat(expressionDef.getExpression().getLocalId(), notNullValue());

    }

    @Test
    public void testIssue395() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue395.cql", 0, CqlCompilerOptions.Options.EnableAnnotations);
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
        TestUtils.runSemanticTest("Issue592.cql", 0, new CqlCompilerOptions().withCompatibilityLevel("1.3"));
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

    private FunctionDef resolveFunctionDef(Library library, String functionName) {
        FunctionDef fd = null;
        for (ExpressionDef ed : library.getStatements().getDef()) {
            if (ed instanceof FunctionDef && ed.getName().equals(functionName)) {
                return (FunctionDef)ed;
            }
        }
        return null;
    }

    @Test
    public void testIssue643() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue643.cql", 0);
        /*
            define function EncountersWithCoding(encounters List<Encounter>, valueSet System.ValueSet):
              encounters E
                where E.class in valueSet

            <query>
              <source>
                <operandRef encounters/>
              </source>
              <where>
                <inValueSet>
                  <functionRef ToConcept>
                      <property class/>
                        <aliasRef E/>
                      </property>
                  </functionRef>
                  <operandRef valueSet/>
                </inValueSet>
              </where>
            </query>

         */

        FunctionDef fd = resolveFunctionDef(translator.getTranslatedLibrary().getLibrary(), "EncountersWithCoding");
        assertThat(fd.getExpression(), instanceOf(Query.class));
        Query q = (Query)fd.getExpression();
        assertThat(q.getWhere(), instanceOf(InValueSet.class));
        InValueSet ivs = (InValueSet)q.getWhere();
        assertThat(ivs.getCode(), instanceOf(FunctionRef.class));
        assertThat(ivs.getValueset(), nullValue());
        assertThat(ivs.getValuesetExpression(), instanceOf(OperandRef.class));

        /*
            define function EncountersWithType(encounters List<Encounter>, valueSet System.ValueSet):
              encounters E
                where E.type in valueSet

         */

        fd = resolveFunctionDef(translator.getTranslatedLibrary().getLibrary(), "EncountersWithType");
        assertThat(fd.getExpression(), instanceOf(Query.class));
        q = (Query)fd.getExpression();
        assertThat(q.getWhere(), instanceOf(AnyInValueSet.class));
        AnyInValueSet aivs = (AnyInValueSet)q.getWhere();
        assertThat(aivs.getCodes(), instanceOf(Query.class));
        assertThat(aivs.getValueset(), nullValue());
        assertThat(aivs.getValuesetExpression(), instanceOf(OperandRef.class));

        /*
            define function EncountersWithServiceType(encounters List<Encounter>, valueSet System.ValueSet):
              encounters E
                where E.serviceType in valueSet
         */

        fd = resolveFunctionDef(translator.getTranslatedLibrary().getLibrary(), "EncountersWithServiceType");
        assertThat(fd.getExpression(), instanceOf(Query.class));
        q = (Query)fd.getExpression();
        assertThat(q.getWhere(), instanceOf(InValueSet.class));
        ivs = (InValueSet)q.getWhere();
        assertThat(ivs.getCode(), instanceOf(FunctionRef.class));
        assertThat(ivs.getValueset(), nullValue());
        assertThat(ivs.getValuesetExpression(), instanceOf(OperandRef.class));
    }

    @Test
    public void testIssue827() throws IOException {
        // https://github.com/cqframework/clinical_quality_language/issues/827
        TestUtils.runSemanticTest("Issue827.cql", 0);
    }


    @Test
    public void testIssueEmptySourceInterval() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("IssueEmptySourceInterval.cql", 1, CqlCompilerOptions.Options.EnableAnnotations);

        java.util.List<CqlCompilerException> exceptions = translator.getExceptions();

        assertEquals(1, exceptions.size());
    }

    @Test
    public void TestVSCastFunction14() throws IOException {
        CqlCompilerOptions options = new CqlCompilerOptions()
                .withOptions(CqlCompilerOptions.Options.EnableAnnotations, CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion, CqlCompilerOptions.Options.DisableMethodInvocation)
                .withCompatibilityLevel("1.4");
        CqlTranslator translator = TestUtils.runSemanticTest("TestVSCastFunction.cql", 0, options);
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("TestConditionsViaFunction");

        /*
          define TestConditionsViaFunction:
            "Conditions in ValueSet"([Condition], "VS Cast Function"("Narcolepsy"))

          <def localId="56" locator="26:1-27:73" name="TestConditionsViaFunction" context="Patient" accessLevel="Public">
             <expression localId="55" locator="27:3-27:73" name="Conditions in ValueSet" xsi:type="FunctionRef">
                <operand localId="52" locator="27:28-27:38" dataType="fhir:Condition" templateId="http://hl7.org/fhir/StructureDefinition/Condition" xsi:type="Retrieve"/>
                <operand localId="54" locator="27:41-27:72" name="VS Cast Function" xsi:type="FunctionRef">
                   <operand localId="53" locator="27:60-27:71" name="Narcolepsy" xsi:type="ValueSetRef"/>
                </operand>
             </expression>
          </def>
        */
        assertThat(ed.getExpression(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(FunctionRef.class));
        fr = (FunctionRef)fr.getOperand().get(1);
        assertThat(fr.getName(), equalTo("VS Cast Function"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(ValueSetRef.class));
        ValueSetRef vsr = (ValueSetRef)fr.getOperand().get(0);
        assertThat(vsr.getName(), equalTo("Narcolepsy"));
        assertThat(vsr.isPreserve(), nullValue());

        ed = translator.getTranslatedLibrary().resolveExpressionRef("TestConditionsDirectly");

        /*
          define TestConditionsDirectly:
            "Conditions in ValueSet"([Condition], "Narcolepsy")

          <def localId="60" locator="29:1-30:53" name="TestConditionsDirectly" context="Patient" accessLevel="Public">
             <expression localId="59" locator="30:3-30:53" name="Conditions in ValueSet" xsi:type="FunctionRef">
                <operand localId="57" locator="30:28-30:38" dataType="fhir:Condition" templateId="http://hl7.org/fhir/StructureDefinition/Condition" xsi:type="Retrieve"/>
                <operand localId="58" locator="30:41-30:52" name="Narcolepsy" xsi:type="ValueSetRef"/>
             </expression>
          </def>
        */
        assertThat(ed.getExpression(), instanceOf(FunctionRef.class));
        fr = (FunctionRef)ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(ValueSetRef.class));
        vsr = (ValueSetRef)fr.getOperand().get(1);
        assertThat(vsr.getName(), equalTo("Narcolepsy"));
        assertThat(vsr.isPreserve(), nullValue());
    }

    @Test
    public void TestVSCastFunction15() throws IOException {
        // TODO: This test needs to pass, most likely by implicitly converting a ValueSet to a ValueSetRef? Or maybe a new explicit ELM operation?
        CqlCompilerOptions options = new CqlCompilerOptions()
                .withOptions(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion, CqlCompilerOptions.Options.DisableMethodInvocation);
        CqlTranslator translator = TestUtils.runSemanticTest("TestVSCastFunction.cql", 0, options);
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("TestConditionsViaFunction");

        /*
          define TestConditionsViaFunction:
            "Conditions in ValueSet"([Condition], "VS Cast Function"("Narcolepsy"))

          <def localId="34" locator="38:1-39:73" name="TestConditionsViaFunction" context="Patient" accessLevel="Public">
             <expression localId="33" locator="39:3-39:73" name="Conditions in ValueSet" xsi:type="FunctionRef">
                <operand localId="30" locator="39:28-39:38" dataType="fhir:Condition" templateId="http://hl7.org/fhir/StructureDefinition/Condition" xsi:type="Retrieve"/>
                <operand localId="32" locator="39:41-39:72" name="VS Cast Function" xsi:type="FunctionRef">
                   <operand xsi:type="ExpandValueSet">
                      <operand localId="31" locator="39:60-39:71" name="Narcolepsy" preserve="true" xsi:type="ValueSetRef"/>
                   </operand>
                </operand>
             </expression>
          </def>
        */
        assertThat(ed.getExpression(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(FunctionRef.class));
        fr = (FunctionRef)fr.getOperand().get(1);
        assertThat(fr.getName(), equalTo("VS Cast Function"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(ExpandValueSet.class));
        ExpandValueSet evs = (ExpandValueSet)fr.getOperand().get(0);
        assertThat(evs.getOperand(), instanceOf(ValueSetRef.class));
        ValueSetRef vsr = (ValueSetRef)evs.getOperand();
        assertThat(vsr.getName(), equalTo("Narcolepsy"));
        assertThat(vsr.isPreserve(), equalTo(true));

        ed = translator.getTranslatedLibrary().resolveExpressionRef("TestConditionsDirectly");

        /*
          define TestConditionsDirectly:
            "Conditions in ValueSet"([Condition], "Narcolepsy")

          <def localId="38" locator="41:1-42:53" name="TestConditionsDirectly" context="Patient" accessLevel="Public">
             <expression localId="37" locator="42:3-42:53" name="Conditions in ValueSet" xsi:type="FunctionRef">
                <operand localId="35" locator="42:28-42:38" dataType="fhir:Condition" templateId="http://hl7.org/fhir/StructureDefinition/Condition" xsi:type="Retrieve"/>
                <operand xsi:type="ExpandValueSet">
                   <operand localId="36" locator="42:41-42:52" name="Narcolepsy" preserve="true" xsi:type="ValueSetRef"/>
                </operand>
             </expression>
          </def>
        */
        assertThat(ed.getExpression(), instanceOf(FunctionRef.class));
        fr = (FunctionRef)ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(ExpandValueSet.class));
        evs = (ExpandValueSet)fr.getOperand().get(1);
        assertThat(evs.getOperand(), instanceOf(ValueSetRef.class));
        vsr = (ValueSetRef)evs.getOperand();
        assertThat(vsr.getName(), equalTo("Narcolepsy"));
        assertThat(vsr.isPreserve(), equalTo(true));
    }

    @Test
    public void TestQuotedForwards() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestQuotedForwards.cql", 0);
    }

    @Test
    public void testIncorrectParameterType1204() throws IOException {
        final CqlTranslator translator = runSemanticTest("TestIncorrectParameterType1204.cql", 2);

        final List<CqlCompilerException> errors = translator.getErrors();

        assertTrue(errors.stream().map(Throwable::getMessage).collect(Collectors.toSet()).toString(), errors.stream().map(Throwable::getMessage).anyMatch("Could not find type for model: null and name: ThisTypeDoesNotExist"::equals));
    }

    @Test
    public void testIdentifierCaseMismatch() throws IOException {
        final CqlTranslator translator = runSemanticTest("TestIdentifierCaseMismatch.cql", 2);

        final List<CqlCompilerException> errors = translator.getErrors();

        // Make it clear we treat a Library type with a mismatched case the same as a non-existent type
        assertTrue(errors.stream().map(Throwable::getMessage).collect(Collectors.toSet()).toString(), errors.stream().map(Throwable::getMessage).anyMatch("Could not find type for model: FHIR and name: Code"::equals));
    }

    @Test
    public void testNonExistentFileName() {
        assertThrows(IOException.class, () -> TestUtils.runSemanticTest("ThisFileDoesNotExist.cql", 0));
    }

    @Test
    public void testCaseConditionalReturnTypes() throws IOException {
        CqlTranslator translator = runSemanticTest("Issue648.cql", 0);
        org.hl7.elm.r1.Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef caseDef = defs.get("Cases");

        assertThat(caseDef.getResultType(), instanceOf(ChoiceType.class));

        ChoiceType choiceType = (ChoiceType)caseDef.getResultType();

        Set<String> expectedChoiceTypes = new HashSet<>();
        expectedChoiceTypes.add("System.String");
        expectedChoiceTypes.add("System.Boolean");
        expectedChoiceTypes.add("System.Integer");

        Set<String> actualChoiceTypes = new HashSet<>();
        for (DataType dt : choiceType.getTypes()) {
            actualChoiceTypes.add(((NamedType)dt).getName());
        }
        assertTrue("Expected types are String, Boolean, and Integer: ", actualChoiceTypes.equals(expectedChoiceTypes));
    }

    @Test
    public void testIfConditionalReturnTypes() throws IOException {
        CqlTranslator translator = runSemanticTest("Issue648.cql", 0);
        org.hl7.elm.r1.Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef ifDef = defs.get("If");
        assertThat(ifDef.getResultType(), instanceOf(ChoiceType.class));

        ChoiceType choiceType = (ChoiceType)ifDef.getResultType();

        Set<String> expectedChoiceTypes = new HashSet<>();
        expectedChoiceTypes.add("System.String");
        expectedChoiceTypes.add("System.Boolean");

        Set<String> actualChoiceTypes = new HashSet<>();
        for (DataType dt : choiceType.getTypes()) {
                actualChoiceTypes.add(((NamedType)dt).getName());
        }
        assertTrue("Expected return types are String and Boolean: ", actualChoiceTypes.equals(expectedChoiceTypes));
    }

    @Test
    public void testIssue863() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("Issue863.cql", 0);
    }

    private CqlTranslator runSemanticTest(String testFileName) throws IOException {
        return runSemanticTest(testFileName, 0);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlCompilerOptions.Options... options) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlCompilerOptions options) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }
}
