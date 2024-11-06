package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.NamedType;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.Test;

public class SemanticTests {

    @Test
    void translations() throws IOException {
        runSemanticTest("TranslationTests.cql");
    }

    @Test
    void in() throws IOException {
        runSemanticTest("InTest.cql");
    }

    @Test
    void inValueSet() throws IOException {
        runSemanticTest("InValueSetTest.cql");
    }

    @Test
    void terminologyReferences() throws IOException {
        runSemanticTest("OperatorTests/TerminologyReferences.cql", 0);
    }

    @Test
    void properties() throws IOException {
        runSemanticTest("PropertyTest.cql");
    }

    @Test
    void parameters() throws IOException {
        runSemanticTest("ParameterTest.cql");
    }

    @Test
    void invalidParameters() throws IOException {
        runSemanticTest("ParameterTestInvalid.cql", 17);
    }

    @Test
    void signatureResolution() throws IOException {
        runSemanticTest("SignatureResolutionTest.cql");
    }

    @Test
    void cms146v2() throws IOException {
        runSemanticTest("CMS146v2_Test_CQM.cql");
    }

    @Test
    void aggregateOperators() throws IOException {
        runSemanticTest("OperatorTests/AggregateOperators.cql");
    }

    @Test
    void arithmeticOperators() throws IOException {
        runSemanticTest("OperatorTests/ArithmeticOperators.cql");
    }

    @Test
    void comparisonOperators() throws IOException {
        runSemanticTest("OperatorTests/ComparisonOperators.cql");
    }

    @Test
    void dateTimeOperators() throws IOException {
        runSemanticTest("OperatorTests/DateTimeOperators.cql");
    }

    @Test
    void intervalOperators() throws IOException {
        runSemanticTest("OperatorTests/IntervalOperators.cql");
    }

    @Test
    void intervalOperatorPhrases() throws IOException {
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
    void listOperators() throws IOException {
        runSemanticTest("OperatorTests/ListOperators.cql");
    }

    @Test
    void logicalOperators() throws IOException {
        runSemanticTest("OperatorTests/LogicalOperators.cql");
    }

    @Test
    void nullologicalOperators() throws IOException {
        runSemanticTest("OperatorTests/NullologicalOperators.cql");
    }

    @Test
    void stringOperators() throws IOException {
        runSemanticTest("OperatorTests/StringOperators.cql");
    }

    @Test
    void timeOperators() throws IOException {
        runSemanticTest("OperatorTests/TimeOperators.cql");
    }

    @Test
    void typeOperators() throws IOException {
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
        ChoiceType choiceType = (ChoiceType) def.getResultType();
        DataType type = null;
        for (DataType dt : choiceType.getTypes()) {
            if (type == null) {
                type = dt;
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType) dt).getName(), equalTo("System.String"));
            } else {
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType) dt).getName(), equalTo("System.Boolean"));
            }
        }

        def = defs.get("TestCase");
        assertThat(def.getResultType(), instanceOf(ChoiceType.class));
        choiceType = (ChoiceType) def.getResultType();
        type = null;
        for (DataType dt : choiceType.getTypes()) {
            if (type == null) {
                type = dt;
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType) dt).getName(), equalTo("System.String"));
            } else {
                assertThat(dt, instanceOf(NamedType.class));
                assertThat(((NamedType) dt).getName(), equalTo("System.Boolean"));
            }
        }
    }

    @Test
    void implicitConversions() throws IOException {
        runSemanticTest("OperatorTests/ImplicitConversions.cql");
    }

    @Test
    void tupleAndClassConversions() throws IOException {
        runSemanticTest("OperatorTests/TupleAndClassConversions.cql");
    }

    @Test
    void functions() throws IOException {
        runSemanticTest("OperatorTests/Functions.cql");
    }

    @Test
    void dateTimeLiteral() throws IOException {
        runSemanticTest("DateTimeLiteralTest.cql");
    }

    @Test
    void codeAndConcepts() throws IOException {
        runSemanticTest("CodeAndConceptTest.cql");
    }

    @Test
    void invalidCastExpression() throws IOException {
        runSemanticTest("OperatorTests/InvalidCastExpression.cql", 1);
    }

    @Test
    void forwardReferences() throws IOException {
        runSemanticTest("OperatorTests/ForwardReferences.cql", 0);
    }

    @Test
    void recursiveFunctions() throws IOException {
        runSemanticTest("OperatorTests/RecursiveFunctions.cql", 1);
    }

    @Test
    void nameHiding() throws IOException {
        runSemanticTest("OperatorTests/NameHiding.cql", 1);
    }

    @Test
    void sorting() throws IOException {
        runSemanticTest("OperatorTests/Sorting.cql", 1);
    }

    @Test
    void invalidSortClauses() throws IOException {
        runSemanticTest("OperatorTests/InvalidSortClauses.cql", 3);
    }

    @Test
    void undeclaredForward() throws IOException {
        runSemanticTest("OperatorTests/UndeclaredForward.cql", 1);
    }

    @Test
    void undeclaredSignature() throws IOException {
        runSemanticTest("OperatorTests/UndeclaredSignature.cql", 1);
    }

    @Test
    void messageOperators() throws IOException {
        runSemanticTest("OperatorTests/MessageOperators.cql", 0);
    }

    @Test
    void multiSourceQuery() throws IOException {
        runSemanticTest("OperatorTests/MultiSourceQuery.cql", 0);
    }

    @Test
    void query() throws IOException {
        runSemanticTest("OperatorTests/Query.cql", 0);
    }

    // NOTE: This test is commented out to an issue with the ANTLR tooling. In 4.5, this test documents the
    // unacceptable performance of the parser. In 4.6+, the parser does not correctly resolve some types of
    // expressions (see TricksyParse and ShouldFail). See Github issue
    // [#343](https://github.com/cqframework/clinical_quality_language/issues/343)
    // for more detail.
    // @Test
    // public void testParserPerformance() throws IOException {
    //    runSemanticTest("ParserPerformance.cql");
    // }

    @Test
    void tricksyParse() throws IOException {
        runSemanticTest("TricksyParse.cql");
    }

    @Test
    void shouldFail() throws IOException {
        runSemanticTest("ShouldFail.cql", 1);
    }

    @Test
    void compatibilityLevel3() throws IOException {
        runSemanticTest("TestCompatibilityLevel3.cql", 1);
        runSemanticTest("TestCompatibilityLevel3.cql", 0, new CqlCompilerOptions().withCompatibilityLevel("1.3"));
    }

    @Test
    void invalidEquality() throws IOException {
        runSemanticTest("InvalidEquality.cql", 1, CqlCompilerOptions.Options.DisableListPromotion);
    }

    @Test
    void relatedContextRetrieve() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestRelatedContextRetrieve.cql", 0);
        org.hl7.elm.r1.Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Estimated Due Date");
        Last last = (Last) def.getExpression();
        Query query = (Query) last.getSource();
        AliasedQuerySource source = query.getSource().get(0);
        Retrieve retrieve = (Retrieve) source.getExpression();
        ExpressionRef mother = (ExpressionRef) retrieve.getContext();
        assertThat(mother.getName(), is("Mother"));
    }

    @Test
    void issue616() throws IOException {
        TestUtils.runSemanticTest("Issue616.cql", 1);
    }

    public void testIssue617() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue617.cql", 0);
        Library library = translator.toELM();
        assertThat(library.getStatements(), notNullValue());
        assertThat(library.getStatements().getDef(), notNullValue());
        assertThat(library.getStatements().getDef().size(), equalTo(2));
        assertThat(library.getStatements().getDef().get(1), instanceOf(ExpressionDef.class));
        ExpressionDef expressionDef =
                (ExpressionDef) library.getStatements().getDef().get(1);
        assertThat(expressionDef.getExpression(), instanceOf(Implies.class));
        assertThat(expressionDef.getName(), is("Boolean Implies"));
        assertThat(((Implies) expressionDef.getExpression()).getOperand().size(), is(2));
    }

    @Test
    void issue547() throws IOException {
        TestUtils.runSemanticTest("Issue547.cql", 3);
    }

    @Test
    void issue558() throws IOException {
        TestUtils.runSemanticTest("Issue558.cql", 1);
    }

    @Test
    void issue581() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue581.cql", 0);
        Library library = translator.toELM();
        assertThat(library.getStatements(), notNullValue());
        assertThat(library.getStatements().getDef(), notNullValue());
        assertThat(library.getStatements().getDef().size(), equalTo(1));
        assertThat(library.getStatements().getDef().get(0), instanceOf(FunctionDef.class));
        FunctionDef fd = (FunctionDef) library.getStatements().getDef().get(0);
        assertThat(fd.getExpression(), instanceOf(If.class));
        If i = (If) fd.getExpression();
        assertThat(i.getCondition(), instanceOf(Not.class));
    }

    @Test
    void issue405() throws IOException {
        CqlTranslator translator =
                TestUtils.runSemanticTest("Issue405.cql", 0, CqlCompilerOptions.Options.EnableAnnotations);
        Library library = translator.toELM();
        assertThat(library.getStatements().getDef().size(), equalTo(6));
        assertThat(library.getStatements().getDef().get(3), instanceOf(ExpressionDef.class));
        ExpressionDef expressionDef =
                (ExpressionDef) library.getStatements().getDef().get(3);
        assertThat(expressionDef.getExpression().getLocalId(), notNullValue());
    }

    @Test
    void issue395() throws IOException {
        CqlTranslator translator =
                TestUtils.runSemanticTest("Issue395.cql", 0, CqlCompilerOptions.Options.EnableAnnotations);
        Library library = translator.toELM();
        ExpressionDef expressionDef =
                (ExpressionDef) library.getStatements().getDef().get(2);
        assertThat(expressionDef.getExpression().getLocalId(), notNullValue());
    }

    @Test
    void issue435() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("Issue435.cql", 2);
        // [#435](https://github.com/cqframework/clinical_quality_language/issues/435)
        assertThat(translator.getErrors().size(), equalTo(2));
    }

    @Test
    void issue587() throws IOException {
        // Both `collapse null` and `collapse { null }` now translate with no errors (expectedErrors = 0).
        // The old errors were related to [#435](https://github.com/cqframework/clinical_quality_language/issues/435)
        // and fixed by [#1428](https://github.com/cqframework/clinical_quality_language/pull/1428) and
        // [#1425](https://github.com/cqframework/clinical_quality_language/pull/1425).
        TestUtils.runSemanticTest("Issue587.cql", 0);
    }

    @Test
    void issue592() throws IOException {
        TestUtils.runSemanticTest("Issue592.cql", 0, new CqlCompilerOptions().withCompatibilityLevel("1.3"));
    }

    @Test
    void issue596() throws IOException {
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
        Before b = (Before) ed.getExpression();
        assertThat(b.getOperand(), notNullValue());
        assertThat(b.getOperand().size(), equalTo(2));
        assertThat(b.getOperand().get(0), instanceOf(If.class));
        assertThat(b.getOperand().get(1), instanceOf(Interval.class));
        If i = (If) b.getOperand().get(0);
        assertThat(i.getCondition(), instanceOf(IsNull.class));
        assertThat(i.getThen(), instanceOf(Null.class));
        assertThat(i.getElse(), instanceOf(Interval.class));
        IsNull isNull = (IsNull) i.getCondition();
        assertThat(isNull.getOperand(), instanceOf(As.class));
        As a = (As) isNull.getOperand();
        assertThat(a.getOperand(), instanceOf(Null.class));
    }

    private FunctionDef resolveFunctionDef(Library library, String functionName) {
        FunctionDef fd = null;
        for (ExpressionDef ed : library.getStatements().getDef()) {
            if (ed instanceof FunctionDef && ed.getName().equals(functionName)) {
                return (FunctionDef) ed;
            }
        }
        return null;
    }

    @Test
    void issue643() throws IOException {
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
        Query q = (Query) fd.getExpression();
        assertThat(q.getWhere(), instanceOf(InValueSet.class));
        InValueSet ivs = (InValueSet) q.getWhere();
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
        q = (Query) fd.getExpression();
        assertThat(q.getWhere(), instanceOf(AnyInValueSet.class));
        AnyInValueSet aivs = (AnyInValueSet) q.getWhere();
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
        q = (Query) fd.getExpression();
        assertThat(q.getWhere(), instanceOf(InValueSet.class));
        ivs = (InValueSet) q.getWhere();
        assertThat(ivs.getCode(), instanceOf(FunctionRef.class));
        assertThat(ivs.getValueset(), nullValue());
        assertThat(ivs.getValuesetExpression(), instanceOf(OperandRef.class));
    }

    @Test
    void issue827() throws IOException {
        // https://github.com/cqframework/clinical_quality_language/issues/827
        TestUtils.runSemanticTest("Issue827.cql", 0);
    }

    @Test
    void issueEmptySourceInterval() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest(
                "IssueEmptySourceInterval.cql", 1, CqlCompilerOptions.Options.EnableAnnotations);

        java.util.List<CqlCompilerException> exceptions = translator.getExceptions();

        assertEquals(1, exceptions.size());
    }

    @Test
    void vSCastFunction14() throws IOException {
        CqlCompilerOptions options = new CqlCompilerOptions()
                .withOptions(
                        CqlCompilerOptions.Options.EnableAnnotations,
                        CqlCompilerOptions.Options.DisableListDemotion,
                        CqlCompilerOptions.Options.DisableListPromotion,
                        CqlCompilerOptions.Options.DisableMethodInvocation)
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
        FunctionRef fr = (FunctionRef) ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(FunctionRef.class));
        fr = (FunctionRef) fr.getOperand().get(1);
        assertThat(fr.getName(), equalTo("VS Cast Function"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(ValueSetRef.class));
        ValueSetRef vsr = (ValueSetRef) fr.getOperand().get(0);
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
        fr = (FunctionRef) ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(ValueSetRef.class));
        vsr = (ValueSetRef) fr.getOperand().get(1);
        assertThat(vsr.getName(), equalTo("Narcolepsy"));
        assertThat(vsr.isPreserve(), nullValue());
    }

    @Test
    void vSCastFunction15() throws IOException {
        // TODO: This test needs to pass, most likely by implicitly converting a ValueSet to a ValueSetRef? Or maybe a
        // new explicit ELM operation?
        CqlCompilerOptions options = new CqlCompilerOptions()
                .withOptions(
                        CqlCompilerOptions.Options.DisableListDemotion,
                        CqlCompilerOptions.Options.DisableListPromotion,
                        CqlCompilerOptions.Options.DisableMethodInvocation);
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
        FunctionRef fr = (FunctionRef) ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(FunctionRef.class));
        fr = (FunctionRef) fr.getOperand().get(1);
        assertThat(fr.getName(), equalTo("VS Cast Function"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(ExpandValueSet.class));
        ExpandValueSet evs = (ExpandValueSet) fr.getOperand().get(0);
        assertThat(evs.getOperand(), instanceOf(ValueSetRef.class));
        ValueSetRef vsr = (ValueSetRef) evs.getOperand();
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
        fr = (FunctionRef) ed.getExpression();
        assertThat(fr.getName(), equalTo("Conditions in ValueSet"));
        assertThat(fr.getOperand().size(), equalTo(2));
        assertThat(fr.getOperand().get(1), instanceOf(ExpandValueSet.class));
        evs = (ExpandValueSet) fr.getOperand().get(1);
        assertThat(evs.getOperand(), instanceOf(ValueSetRef.class));
        vsr = (ValueSetRef) evs.getOperand();
        assertThat(vsr.getName(), equalTo("Narcolepsy"));
        assertThat(vsr.isPreserve(), equalTo(true));
    }

    @Test
    void quotedForwards() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestQuotedForwards.cql", 0);
    }

    @Test
    void incorrectParameterType1204() throws IOException {
        final CqlTranslator translator = runSemanticTest("TestIncorrectParameterType1204.cql", 2);

        final List<CqlCompilerException> errors = translator.getErrors();

        assertTrue(
                errors.stream()
                        .map(Throwable::getMessage)
                        .anyMatch("Could not find type for model: null and name: ThisTypeDoesNotExist"::equals),
                errors.stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.toSet())
                        .toString());
    }

    @Test
    void identifierCaseMismatch() throws IOException {
        final CqlTranslator translator = runSemanticTest("TestIdentifierCaseMismatch.cql", 2);

        final List<CqlCompilerException> errors = translator.getErrors();

        // Make it clear we treat a Library type with a mismatched case the same as a non-existent type
        assertTrue(
                errors.stream()
                        .map(Throwable::getMessage)
                        .anyMatch("Could not find type for model: FHIR and name: Code"::equals),
                errors.stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.toSet())
                        .toString());
    }

    @Test
    void nonExistentFileName() {
        assertThrows(IOException.class, () -> TestUtils.runSemanticTest("ThisFileDoesNotExist.cql", 0));
    }

    @Test
    void caseConditionalReturnTypes() throws IOException {
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

        ChoiceType choiceType = (ChoiceType) caseDef.getResultType();

        Set<String> expectedChoiceTypes = new HashSet<>();
        expectedChoiceTypes.add("System.String");
        expectedChoiceTypes.add("System.Boolean");
        expectedChoiceTypes.add("System.Integer");

        Set<String> actualChoiceTypes = new HashSet<>();
        for (DataType dt : choiceType.getTypes()) {
            actualChoiceTypes.add(((NamedType) dt).getName());
        }
        assertEquals(actualChoiceTypes, expectedChoiceTypes, "Expected types are String, Boolean, and Integer: ");
    }

    @Test
    void ifConditionalReturnTypes() throws IOException {
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

        ChoiceType choiceType = (ChoiceType) ifDef.getResultType();

        Set<String> expectedChoiceTypes = new HashSet<>();
        expectedChoiceTypes.add("System.String");
        expectedChoiceTypes.add("System.Boolean");

        Set<String> actualChoiceTypes = new HashSet<>();
        for (DataType dt : choiceType.getTypes()) {
            actualChoiceTypes.add(((NamedType) dt).getName());
        }
        assertEquals(actualChoiceTypes, expectedChoiceTypes, "Expected return types are String and Boolean: ");
    }

    @Test
    public void testIdentifierDoesNotResolveCaseMismatchExistIdentifier() throws IOException {
        final CqlTranslator translator =
                runSemanticTest("IdentifierDoesNotResolveCaseMismatchExistIdentifier_Issue598.cql", 2);

        final List<String> errorMessages =
                translator.getErrors().stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(
                errorMessages,
                contains(
                        "Could not resolve identifier NonExistent in the current library.",
                        "Could not find identifier: [IaMaDiFeReNtCaSe].  Did you mean [iAmAdIfErEnTcAsE]?"));

        final List<String> warnings =
                translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warnings, hasSize(0));
    }

    @Test
    void issue1407() throws IOException {
        assertNull(issue1407GetIsPreserve("1.4"));
        assertTrue(issue1407GetIsPreserve("1.5"));
    }

    private Boolean issue1407GetIsPreserve(String compatibilityLevel) throws IOException {
        CqlTranslator translator = runSemanticTest(
                "LibraryTests/Issue1407.cql", 0, new CqlCompilerOptions().withCompatibilityLevel(compatibilityLevel));
        var library = translator.toELM();
        var testExpression = library.getStatements().getDef().stream()
                .filter(def -> def.getName().equals("TestStatement"))
                .findFirst()
                .orElseThrow()
                .getExpression();

        assertThat(testExpression, instanceOf(ValueSetRef.class));
        return ((ValueSetRef) testExpression).isPreserve();
    }

    private CqlTranslator runSemanticTest(String testFileName) throws IOException {
        return runSemanticTest(testFileName, 0);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors);
    }

    private CqlTranslator runSemanticTest(
            String testFileName, int expectedErrors, CqlCompilerOptions.Options... options) throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }

    private CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlCompilerOptions options)
            throws IOException {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options);
    }
}
