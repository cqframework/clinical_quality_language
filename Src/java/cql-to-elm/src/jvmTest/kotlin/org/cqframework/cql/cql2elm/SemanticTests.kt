package org.cqframework.cql.cql2elm

import java.io.IOException
import java.util.stream.Collectors
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.NamedType
import org.hl7.elm.r1.And
import org.hl7.elm.r1.AnyInValueSet
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.ExpandValueSet
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.Implies
import org.hl7.elm.r1.In
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ValueSetRef
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("LargeClass", "MaxLineLength")
class SemanticTests {
    @Test
    @Throws(IOException::class)
    fun translations() {
        runSemanticTest("TranslationTests.cql")
    }

    @Test
    @Throws(IOException::class)
    fun convert() {
        runSemanticTest("ConvertTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun inCodeSystem() {
        runSemanticTest("InCodeSystemTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun `in`() {
        runSemanticTest("InTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun inValueSet() {
        runSemanticTest("InValueSetTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun terminologyReferences() {
        runSemanticTest("OperatorTests/TerminologyReferences.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun properties() {
        runSemanticTest("PropertyTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun parameters() {
        runSemanticTest("ParameterTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun invalidParameters() {
        runSemanticTest("ParameterTestInvalid.cql", 17)
    }

    @Test
    @Throws(IOException::class)
    fun signatureResolution() {
        runSemanticTest("SignatureResolutionTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun cms146v2() {
        runSemanticTest("CMS146v2_Test_CQM.cql")
    }

    @Test
    @Throws(IOException::class)
    fun aggregateOperators() {
        runSemanticTest("OperatorTests/AggregateOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun arithmeticOperators() {
        runSemanticTest("OperatorTests/ArithmeticOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun comparisonOperators() {
        runSemanticTest("OperatorTests/ComparisonOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun dateTimeOperators() {
        runSemanticTest("OperatorTests/DateTimeOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun intervalOperators() {
        runSemanticTest("OperatorTests/IntervalOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun intervalOperatorPhrases() {
        val translator = runSemanticTest("OperatorTests/IntervalOperatorPhrases.cql")
        val library = translator.toELM()
        val pointWithin = getExpressionDef(library!!, "PointWithin")
        assertThat(pointWithin.expression, Matchers.instanceOf(And::class.java))
        val pointProperlyWithin = getExpressionDef(library, "PointProperlyWithin")
        assertThat(pointProperlyWithin.expression, Matchers.instanceOf(In::class.java))
    }

    private fun getExpressionDef(library: Library, name: String?): ExpressionDef {
        for (def in library.statements!!.def) {
            if (def.name.equals(name)) {
                return def
            }
        }
        throw IllegalArgumentException("Could not resolve name $name")
    }

    @Test
    @Throws(IOException::class)
    fun listOperators() {
        runSemanticTest("OperatorTests/ListOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun logicalOperators() {
        runSemanticTest("OperatorTests/LogicalOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun nullologicalOperators() {
        runSemanticTest("OperatorTests/NullologicalOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun stringOperators() {
        runSemanticTest("OperatorTests/StringOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun timeOperators() {
        runSemanticTest("OperatorTests/TimeOperators.cql")
    }

    @Test
    @Throws(IOException::class)
    fun typeOperators() {
        val translator = runSemanticTest("OperatorTests/TypeOperators.cql")
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        var def: ExpressionDef = defs["TestIf"]!!
        var defResultType = def.resultType
        assertThat(defResultType, Matchers.instanceOf(ChoiceType::class.java))
        var choiceType = defResultType as ChoiceType?
        var type: DataType? = null
        for (dt in choiceType!!.types) {
            if (type == null) {
                type = dt
                assertThat(dt, Matchers.instanceOf(NamedType::class.java))
                assertThat((dt as NamedType).name, Matchers.equalTo("System.String"))
            } else {
                assertThat(dt, Matchers.instanceOf(NamedType::class.java))
                assertThat((dt as NamedType).name, Matchers.equalTo("System.Boolean"))
            }
        }

        def = defs["TestCase"]!!
        defResultType = def.resultType
        assertThat(defResultType, Matchers.instanceOf(ChoiceType::class.java))
        choiceType = defResultType as ChoiceType?
        type = null
        for (dt in choiceType!!.types) {
            if (type == null) {
                type = dt
                assertThat(dt, Matchers.instanceOf(NamedType::class.java))
                assertThat((dt as NamedType).name, Matchers.equalTo("System.String"))
            } else {
                assertThat(dt, Matchers.instanceOf(NamedType::class.java))
                assertThat((dt as NamedType).name, Matchers.equalTo("System.Boolean"))
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun implicitConversions() {
        runSemanticTest("OperatorTests/ImplicitConversions.cql")
    }

    @Test
    @Throws(IOException::class)
    fun tupleAndClassConversions() {
        runSemanticTest("OperatorTests/TupleAndClassConversions.cql")
    }

    @Test
    @Throws(IOException::class)
    fun functions() {
        runSemanticTest("OperatorTests/Functions.cql")
    }

    @Test
    @Throws(IOException::class)
    fun dateTimeLiteral() {
        runSemanticTest("DateTimeLiteralTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun codeAndConcepts() {
        runSemanticTest("CodeAndConceptTest.cql")
    }

    @Test
    @Throws(IOException::class)
    fun invalidCastExpression() {
        runSemanticTest("OperatorTests/InvalidCastExpression.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun forwardReferences() {
        runSemanticTest("OperatorTests/ForwardReferences.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun recursiveFunctions() {
        runSemanticTest("OperatorTests/RecursiveFunctions.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun nameHiding() {
        runSemanticTest("OperatorTests/NameHiding.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun sorting() {
        runSemanticTest("OperatorTests/Sorting.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun invalidSortClauses() {
        runSemanticTest("OperatorTests/InvalidSortClauses.cql", 3)
    }

    @Test
    @Throws(IOException::class)
    fun undeclaredForward() {
        runSemanticTest("OperatorTests/UndeclaredForward.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun undeclaredSignature() {
        runSemanticTest("OperatorTests/UndeclaredSignature.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun messageOperators() {
        runSemanticTest("OperatorTests/MessageOperators.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun multiSourceQuery() {
        runSemanticTest("OperatorTests/MultiSourceQuery.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun query() {
        runSemanticTest("OperatorTests/Query.cql", 0)
    }

    // NOTE: This test is commented out to an issue with the ANTLR tooling. In 4.5, this test
    // documents the
    // unacceptable performance of the parser. In 4.6+, the parser does not correctly resolve some
    // types of
    // expressions (see TricksyParse and ShouldFail). See GitHub issue
    // [#343](https://github.com/cqframework/clinical_quality_language/issues/343)
    // for more detail.
    // @Test
    // public void testParserPerformance() throws IOException {
    //    runSemanticTest("ParserPerformance.cql");
    // }
    @Test
    @Throws(IOException::class)
    fun tricksyParse() {
        runSemanticTest("TricksyParse.cql")
    }

    @Test
    @Throws(IOException::class)
    fun shouldFail() {
        runSemanticTest("ShouldFail.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun compatibilityLevel3() {
        runSemanticTest("TestCompatibilityLevel3.cql", 1)
        runSemanticTest(
            "TestCompatibilityLevel3.cql",
            0,
            CqlCompilerOptions().withCompatibilityLevel("1.3"),
        )
    }

    @Test
    @Throws(IOException::class)
    fun invalidEquality() {
        runSemanticTest("InvalidEquality.cql", 1, CqlCompilerOptions.Options.DisableListPromotion)
    }

    @Test
    @Throws(IOException::class)
    fun relatedContextRetrieve() {
        val translator = TestUtils.runSemanticTest("TestRelatedContextRetrieve.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def: ExpressionDef = defs["Estimated Due Date"]!!
        val last = def.expression as Last?
        val query = last!!.source as Query?
        val source = query!!.source[0]
        val retrieve = source.expression as Retrieve?
        val mother = retrieve!!.context as ExpressionRef?
        assertThat(mother!!.name, `is`("Mother"))
    }

    @Test
    @Throws(IOException::class)
    fun issue616() {
        TestUtils.runSemanticTest("Issue616.cql", 1)
    }

    @Throws(IOException::class)
    fun testIssue617() {
        val translator = TestUtils.runSemanticTest("Issue617.cql", 0)
        val library = translator.toELM()
        assertThat<Library.Statements?>(library!!.statements, Matchers.notNullValue())
        assertThat<Any?>(library.statements!!.def, Matchers.notNullValue())
        assertThat(library.statements!!.def.size, Matchers.equalTo(2))
        assertThat(library.statements!!.def[1], Matchers.instanceOf(ExpressionDef::class.java))
        val expressionDef = library.statements!!.def[1]
        assertThat(expressionDef.expression, Matchers.instanceOf(Implies::class.java))
        assertThat(expressionDef.name, `is`("Boolean Implies"))
        assertThat((expressionDef.expression as Implies).operand.size, `is`(2))
    }

    @Test
    @Throws(IOException::class)
    fun issue547() {
        TestUtils.runSemanticTest("Issue547.cql", 3)
    }

    @Test
    @Throws(IOException::class)
    fun issue558() {
        TestUtils.runSemanticTest("Issue558.cql", 1)
    }

    @Test
    @Throws(IOException::class)
    fun issue581() {
        val translator = TestUtils.runSemanticTest("Issue581.cql", 0)
        val library = translator.toELM()
        assertThat<Library.Statements?>(library!!.statements, Matchers.notNullValue())
        assertThat<Any?>(library.statements!!.def, Matchers.notNullValue())
        assertThat(library.statements!!.def.size, Matchers.equalTo(1))
        assertThat(library.statements!!.def[0], Matchers.instanceOf(FunctionDef::class.java))
        val fd = library.statements!!.def[0] as FunctionDef
        assertThat(fd.expression, Matchers.instanceOf(If::class.java))
        val i = fd.expression as If?
        assertThat(i!!.condition, Matchers.instanceOf(Not::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun issue405() {
        val translator =
            TestUtils.runSemanticTest(
                "Issue405.cql",
                0,
                CqlCompilerOptions.Options.EnableAnnotations,
            )
        val library = translator.toELM()
        assertThat(library!!.statements!!.def.size, Matchers.equalTo(6))
        assertThat(library.statements!!.def[3], Matchers.instanceOf(ExpressionDef::class.java))
        val expressionDef = library.statements!!.def[3]
        assertThat<Any?>(expressionDef.expression!!.localId, Matchers.notNullValue())
    }

    @Test
    @Throws(IOException::class)
    fun issue395() {
        val translator =
            TestUtils.runSemanticTest(
                "Issue395.cql",
                0,
                CqlCompilerOptions.Options.EnableAnnotations,
            )
        val library = translator.toELM()
        val expressionDef = library!!.statements!!.def[2]
        assertThat<Any?>(expressionDef.expression!!.localId, Matchers.notNullValue())
    }

    @Test
    @Throws(IOException::class)
    fun issue435() {
        val translator = TestUtils.runSemanticTest("Issue435.cql", 2)
        // [#435](https://github.com/cqframework/clinical_quality_language/issues/435)
        assertThat(translator.errors.size, Matchers.equalTo(2))
    }

    @Test
    @Throws(IOException::class)
    fun issue587() {
        // Both `collapse null` and `collapse { null }` now translate with no errors (expectedErrors
        // = 0).
        // The old errors were related to
        // [#435](https://github.com/cqframework/clinical_quality_language/issues/435)
        // and fixed by [#1428](https://github.com/cqframework/clinical_quality_language/pull/1428)
        // and
        // [#1425](https://github.com/cqframework/clinical_quality_language/pull/1425).
        TestUtils.runSemanticTest("Issue587.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun issue592() {
        TestUtils.runSemanticTest(
            "Issue592.cql",
            0,
            CqlCompilerOptions().withCompatibilityLevel("1.3"),
        )
    }

    @Test
    @Throws(IOException::class)
    fun issue596() {
        // NOTE: This test is susceptible to constant folding optimization...
        val translator = TestUtils.runSemanticTest("Issue596.cql", 0)
        val ed = translator.translatedLibrary!!.resolveExpressionRef("NullBeforeInterval")
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
        assertThat(ed!!.expression, Matchers.instanceOf(Before::class.java))
        val b = ed.expression as Before?
        assertThat<Any?>(b!!.operand, Matchers.notNullValue())
        assertThat(b.operand.size, Matchers.equalTo(2))
        assertThat(b.operand[0], Matchers.instanceOf(If::class.java))
        assertThat(b.operand[1], Matchers.instanceOf(Interval::class.java))
        val i = b.operand[0] as If
        assertThat(i.condition, Matchers.instanceOf(IsNull::class.java))
        assertThat(i.then, Matchers.instanceOf(Null::class.java))
        assertThat(i.`else`, Matchers.instanceOf(Interval::class.java))
        val isNull = i.condition as IsNull?
        assertThat(isNull!!.operand, Matchers.instanceOf(As::class.java))
        val a = isNull.operand as As?
        assertThat(a!!.operand, Matchers.instanceOf(Null::class.java))
    }

    private fun resolveFunctionDef(library: Library, functionName: String?): FunctionDef? {
        for (ed in library.statements!!.def) {
            if (ed is FunctionDef && ed.name.equals(functionName)) {
                return ed
            }
        }
        return null
    }

    @Test
    @Throws(IOException::class)
    fun issue643() {
        val translator = TestUtils.runSemanticTest("Issue643.cql", 0)

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
        var fd =
            resolveFunctionDef(translator.translatedLibrary!!.library!!, "EncountersWithCoding")
        assertThat(fd!!.expression, Matchers.instanceOf(Query::class.java))
        var q = fd.expression as Query?
        assertThat(q!!.where, Matchers.instanceOf(InValueSet::class.java))
        var ivs = q.where as InValueSet?
        assertThat(ivs!!.code, Matchers.instanceOf(FunctionRef::class.java))
        assertThat<ValueSetRef?>(ivs.valueset, Matchers.nullValue())
        assertThat(ivs.valuesetExpression, Matchers.instanceOf(OperandRef::class.java))

        /*
           define function EncountersWithType(encounters List<Encounter>, valueSet System.ValueSet):
             encounters E
               where E.type in valueSet

        */
        fd = resolveFunctionDef(translator.translatedLibrary!!.library!!, "EncountersWithType")
        assertThat(fd!!.expression, Matchers.instanceOf(Query::class.java))
        q = fd.expression as Query?
        assertThat(q!!.where, Matchers.instanceOf(AnyInValueSet::class.java))
        val aivs = q.where as AnyInValueSet?
        assertThat(aivs!!.codes, Matchers.instanceOf(Query::class.java))
        assertThat<ValueSetRef?>(aivs.valueset, Matchers.nullValue())
        assertThat(aivs.valuesetExpression, Matchers.instanceOf(OperandRef::class.java))

        /*
           define function EncountersWithServiceType(encounters List<Encounter>, valueSet System.ValueSet):
             encounters E
               where E.serviceType in valueSet
        */
        fd =
            resolveFunctionDef(
                translator.translatedLibrary!!.library!!,
                "EncountersWithServiceType",
            )
        assertThat(fd!!.expression, Matchers.instanceOf(Query::class.java))
        q = fd.expression as Query?
        assertThat(q!!.where, Matchers.instanceOf(InValueSet::class.java))
        ivs = q.where as InValueSet?
        assertThat(ivs!!.code, Matchers.instanceOf(FunctionRef::class.java))
        assertThat<ValueSetRef?>(ivs.valueset, Matchers.nullValue())
        assertThat(ivs.valuesetExpression, Matchers.instanceOf(OperandRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun issue827() {
        // https://github.com/cqframework/clinical_quality_language/issues/827
        TestUtils.runSemanticTest("Issue827.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun issueEmptySourceInterval() {
        val translator =
            TestUtils.runSemanticTest(
                "IssueEmptySourceInterval.cql",
                1,
                CqlCompilerOptions.Options.EnableAnnotations,
            )

        val exceptions = translator.exceptions

        Assertions.assertEquals(1, exceptions.size)
    }

    @Test
    @Throws(IOException::class)
    fun vSCastFunction14() {
        val options =
            CqlCompilerOptions()
                .withOptions(
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation,
                )
                .withCompatibilityLevel("1.4")
        val translator = TestUtils.runSemanticTest("TestVSCastFunction.cql", 0, options)
        var ed = translator.translatedLibrary!!.resolveExpressionRef("TestConditionsViaFunction")

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
        assertThat(ed!!.expression, Matchers.instanceOf(FunctionRef::class.java))
        var fr = ed.expression as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("Conditions in ValueSet"))
        assertThat(fr.operand.size, Matchers.equalTo(2))
        assertThat(fr.operand[1], Matchers.instanceOf(FunctionRef::class.java))
        fr = fr.operand[1] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("VS Cast Function"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(ValueSetRef::class.java))
        var vsr: ValueSetRef = fr.operand[0] as ValueSetRef
        assertThat(vsr.name, Matchers.equalTo("Narcolepsy"))
        assertThat<Any?>(vsr.isPreserve(), Matchers.nullValue())

        ed = translator.translatedLibrary!!.resolveExpressionRef("TestConditionsDirectly")

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
        assertThat(ed!!.expression, Matchers.instanceOf(FunctionRef::class.java))
        fr = ed.expression as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("Conditions in ValueSet"))
        assertThat(fr.operand.size, Matchers.equalTo(2))
        assertThat(fr.operand[1], Matchers.instanceOf(ValueSetRef::class.java))
        vsr = fr.operand[1] as ValueSetRef
        assertThat(vsr.name, Matchers.equalTo("Narcolepsy"))
        assertThat<Any?>(vsr.isPreserve(), Matchers.nullValue())
    }

    @Suppress("ForbiddenComment")
    @Test
    @Throws(IOException::class)
    fun vSCastFunction15() {
        // TODO: This test needs to pass, most likely by implicitly converting a ValueSet to a
        // ValueSetRef? Or maybe a
        // new explicit ELM operation?
        val options =
            CqlCompilerOptions()
                .withOptions(
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation,
                )
        val translator = TestUtils.runSemanticTest("TestVSCastFunction.cql", 0, options)
        var ed = translator.translatedLibrary!!.resolveExpressionRef("TestConditionsViaFunction")

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
        assertThat(ed!!.expression, Matchers.instanceOf(FunctionRef::class.java))
        var fr = ed.expression as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("Conditions in ValueSet"))
        assertThat(fr.operand.size, Matchers.equalTo(2))
        assertThat(fr.operand[1], Matchers.instanceOf(FunctionRef::class.java))
        fr = fr.operand[1] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("VS Cast Function"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(ExpandValueSet::class.java))
        var evs = fr.operand[0] as ExpandValueSet
        assertThat(evs.operand, Matchers.instanceOf(ValueSetRef::class.java))
        var vsr = evs.operand as ValueSetRef?
        assertThat(vsr!!.name, Matchers.equalTo("Narcolepsy"))
        assertThat<Boolean?>(vsr.isPreserve(), Matchers.equalTo<Boolean?>(true))

        ed = translator.translatedLibrary!!.resolveExpressionRef("TestConditionsDirectly")

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
        assertThat(ed!!.expression, Matchers.instanceOf(FunctionRef::class.java))
        fr = ed.expression as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("Conditions in ValueSet"))
        assertThat(fr.operand.size, Matchers.equalTo(2))
        assertThat(fr.operand[1], Matchers.instanceOf(ExpandValueSet::class.java))
        evs = fr.operand[1] as ExpandValueSet
        assertThat(evs.operand, Matchers.instanceOf(ValueSetRef::class.java))
        vsr = evs.operand as ValueSetRef?
        assertThat(vsr!!.name, Matchers.equalTo("Narcolepsy"))
        assertThat<Boolean?>(vsr.isPreserve(), Matchers.equalTo<Boolean?>(true))
    }

    @Test
    @Throws(IOException::class)
    fun quotedForwards() {
        TestUtils.runSemanticTest("TestQuotedForwards.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun incorrectParameterType1204() {
        val translator = runSemanticTest("TestIncorrectParameterType1204.cql", 2)

        val errors = translator.errors

        Assertions.assertTrue(
            errors
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .anyMatch { anObject ->
                    "Could not find type for model: null and name: ThisTypeDoesNotExist" == anObject
                },
            errors
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toSet())
                .toString(),
        )
    }

    @Test
    @Throws(IOException::class)
    fun identifierCaseMismatch() {
        val translator = runSemanticTest("TestIdentifierCaseMismatch.cql", 2)

        val errors = translator.errors

        // Make it clear we treat a Library type with a mismatched case the same as a non-existent
        // type
        Assertions.assertTrue(
            errors
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .anyMatch { anObject ->
                    "Could not find type for model: FHIR and name: Code" == anObject
                },
            errors
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toSet())
                .toString(),
        )
    }

    @Test
    fun nonExistentFileName() {
        Assertions.assertThrows(IOException::class.java) {
            TestUtils.runSemanticTest("ThisFileDoesNotExist.cql", 0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun caseConditionalReturnTypes() {
        val translator = runSemanticTest("Issue648.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val caseDef: ExpressionDef = defs["Cases"]!!

        val defResultType = caseDef.resultType
        assertThat(defResultType, Matchers.instanceOf(ChoiceType::class.java))

        val choiceType = defResultType as ChoiceType?

        val expectedChoiceTypes = mutableSetOf<String>()
        expectedChoiceTypes.add("System.String")
        expectedChoiceTypes.add("System.Boolean")
        expectedChoiceTypes.add("System.Integer")

        val actualChoiceTypes = mutableSetOf<String>()
        for (dt in choiceType!!.types) {
            actualChoiceTypes.add((dt as NamedType).name)
        }
        assertEquals(
            actualChoiceTypes,
            expectedChoiceTypes,
            "Expected types are String, Boolean, and Integer: ",
        )
    }

    @Test
    @Throws(IOException::class)
    fun ifConditionalReturnTypes() {
        val translator = runSemanticTest("Issue648.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val ifDef: ExpressionDef = defs["If"]!!
        val defResultType = ifDef.resultType
        assertThat(defResultType, Matchers.instanceOf(ChoiceType::class.java))

        val choiceType = defResultType as ChoiceType?

        val expectedChoiceTypes = mutableSetOf<String>()
        expectedChoiceTypes.add("System.String")
        expectedChoiceTypes.add("System.Boolean")

        val actualChoiceTypes = mutableSetOf<String>()
        for (dt in choiceType!!.types) {
            actualChoiceTypes.add((dt as NamedType).name)
        }
        assertEquals(
            actualChoiceTypes,
            expectedChoiceTypes,
            "Expected return types are String and Boolean: ",
        )
    }

    @Test
    @Throws(IOException::class)
    fun testIdentifierDoesNotResolveCaseMismatchExistIdentifier() {
        val translator =
            runSemanticTest("IdentifierDoesNotResolveCaseMismatchExistIdentifier_Issue598.cql", 2)

        val errorMessages =
            translator.errors
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(
            errorMessages,
            Matchers.contains(
                "Could not resolve identifier NonExistent in the current library.",
                "Could not resolve identifier IaMaDiFeReNtCaSe. Consider whether the identifier iAmAdIfErEnTcAsE (differing only in case) was intended.",
            ),
        )

        val warnings =
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(warnings, Matchers.hasSize(0))
    }

    @Test
    @Throws(IOException::class)
    fun issue1407() {
        Assertions.assertNull(issue1407GetIsPreserve("1.4"))
        Assertions.assertTrue(issue1407GetIsPreserve("1.5")!!)
    }

    @Throws(IOException::class)
    private fun issue1407GetIsPreserve(compatibilityLevel: String): Boolean? {
        val translator =
            runSemanticTest(
                "LibraryTests/Issue1407.cql",
                0,
                CqlCompilerOptions().withCompatibilityLevel(compatibilityLevel),
            )
        val library = translator.toELM()
        val testExpression =
            library!!
                .statements!!
                .def
                .stream()
                .filter { def -> def.name.equals("TestStatement") }
                .findFirst()
                .orElseThrow()
                .expression

        assertThat<Any?>(testExpression, Matchers.instanceOf<Any?>(ValueSetRef::class.java))
        return (testExpression as ValueSetRef).isPreserve()
    }

    @Test
    @Throws(IOException::class)
    fun issue1504() {
        val translator = runSemanticTest("LibraryTests/Issue1504.cql", 1)
        for (error in translator.errors) {
            Assertions.assertNotNull(error.locator)
            Assertions.assertNotNull(error.locator!!.library)
            assertEquals("Issue1504", error.locator!!.library!!.id)
            assertEquals("2.0.000", error.locator!!.library!!.version)
        }
    }

    @Throws(IOException::class)
    private fun runSemanticTest(testFileName: String, expectedErrors: Int = 0): CqlTranslator {
        return TestUtils.runSemanticTest(testFileName, expectedErrors)
    }

    @Throws(IOException::class)
    private fun runSemanticTest(
        testFileName: String,
        expectedErrors: Int,
        vararg options: CqlCompilerOptions.Options,
    ): CqlTranslator {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, *options)
    }

    @Throws(IOException::class)
    private fun runSemanticTest(
        testFileName: String,
        expectedErrors: Int,
        options: CqlCompilerOptions,
    ): CqlTranslator {
        return TestUtils.runSemanticTest(testFileName, expectedErrors, options)
    }
}
