package org.cqframework.cql.cql2elm.fhir.r4

import java.io.IOException
import javax.xml.namespace.QName
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.TestUtils
import org.cqframework.cql.cql2elm.matchers.QuickDataType
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.In
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun choiceWithAlternativeConversion() {
        val def = TestUtils.visitFile("fhir/r4/TestChoiceTypes.cql") as ExpressionDef?
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        MatcherAssert.assertThat(source.alias, Matchers.`is`("Q"))
        val request = source.expression as Retrieve?
        MatcherAssert.assertThat<QName>(
            request!!.dataType,
            QuickDataType.quickDataType("QuestionnaireResponse")
        )

        // Then check that the suchThat of the with is a greater with a Case as the left operand
        val relationship = query.relationship[0]
        MatcherAssert.assertThat(relationship.suchThat, Matchers.instanceOf(Greater::class.java))
        val suchThat = relationship.suchThat as Greater?
        MatcherAssert.assertThat(suchThat!!.operand[0], Matchers.instanceOf(Case::class.java))
        val caseExpression = suchThat.operand[0] as Case
        MatcherAssert.assertThat<MutableCollection<*>?>(
            caseExpression.caseItem,
            Matchers.hasSize<Any?>(2)
        )
        MatcherAssert.assertThat(
            caseExpression.caseItem[0].`when`,
            Matchers.instanceOf(Is::class.java)
        )
        MatcherAssert.assertThat(
            caseExpression.caseItem[0].then,
            Matchers.instanceOf(FunctionRef::class.java)
        )
        MatcherAssert.assertThat(
            caseExpression.caseItem[1].`when`,
            Matchers.instanceOf(Is::class.java)
        )
        MatcherAssert.assertThat(
            caseExpression.caseItem[1].then,
            Matchers.instanceOf(FunctionRef::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun uriConversion() {
        // If this translates without errors, the test is successful
        TestUtils.visitFile("fhir/r4/TestURIConversion.cql") as ExpressionDef?
    }

    @Test
    @Throws(IOException::class)
    fun fhirTiming() {
        val def = TestUtils.visitFile("fhir/r4/TestFHIRTiming.cql") as ExpressionDef?
        // Query->
        //  where->
        //      In->
        //          left->
        //              ToDateTime()
        //                  As(fhir:dateTime) ->
        //                      Property(P.performed)
        //          right-> MeasurementPeriod
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        MatcherAssert.assertThat(source.alias, Matchers.`is`("P"))
        val request = source.expression as Retrieve?
        MatcherAssert.assertThat<QName>(
            request!!.dataType,
            QuickDataType.quickDataType("Procedure")
        )

        // Then check that the where is an In with a ToDateTime as the left operand
        val where = query.where
        MatcherAssert.assertThat(where, Matchers.instanceOf(In::class.java))
        val inDef = where as In?
        MatcherAssert.assertThat(inDef!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = inDef.operand[0] as FunctionRef
        MatcherAssert.assertThat(functionRef.name, Matchers.`is`("ToDateTime"))
        MatcherAssert.assertThat(functionRef.operand[0], Matchers.instanceOf(As::class.java))
        val asExpression = functionRef.operand[0] as As
        MatcherAssert.assertThat(asExpression.asType!!.localPart, Matchers.`is`("dateTime"))
        MatcherAssert.assertThat(asExpression.operand, Matchers.instanceOf(Property::class.java))
        val property = asExpression.operand as Property?
        MatcherAssert.assertThat(property!!.scope, Matchers.`is`("P"))
        MatcherAssert.assertThat(property.path, Matchers.`is`("performed"))
    }

    @Test
    @Throws(IOException::class)
    fun equalityWithConversions() {
        val library = TestUtils.visitFileLibrary("fhir/r4/EqualityWithConversions.cql")
        val getGender = library!!.resolveExpressionRef("GetGender")
        MatcherAssert.assertThat(getGender!!.expression, Matchers.instanceOf(Equal::class.java))
        val equal = getGender.expression as Equal?
        MatcherAssert.assertThat(equal!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = equal.operand[0] as FunctionRef
        MatcherAssert.assertThat(functionRef.name, Matchers.`is`("ToString"))
        MatcherAssert.assertThat(functionRef.libraryName, Matchers.`is`("FHIRHelpers"))
    }

    @Test
    @Throws(IOException::class)
    fun doubleListPromotion() {
        val translator = TestUtils.runSemanticTest("fhir/r4/TestDoubleListPromotion.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def: ExpressionDef = defs["Observations"]!!
        val retrieve = def.expression as Retrieve?
        val codes = retrieve!!.codes
        MatcherAssert.assertThat(codes, Matchers.instanceOf(ToList::class.java))
        MatcherAssert.assertThat(
            (codes as ToList).operand,
            Matchers.instanceOf(CodeRef::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun choiceDateRangeOptimization() {
        val translator =
            TestUtils.runSemanticTest(
                "fhir/r4/TestChoiceDateRangeOptimization.cql",
                0,
                CqlCompilerOptions.Options.EnableDateRangeOptimization
            )
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
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
        var expressionDef: ExpressionDef = defs["DateCondition"]!!
        MatcherAssert.assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        var query = expressionDef.expression as Query?
        MatcherAssert.assertThat(query!!.source.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            query.source[0].expression,
            Matchers.instanceOf(Retrieve::class.java)
        )
        var retrieve: Retrieve = query.source[0].expression as Retrieve
        MatcherAssert.assertThat(retrieve.dateProperty, Matchers.`is`("recordedDate"))
        MatcherAssert.assertThat(retrieve.dateRange, Matchers.instanceOf(ParameterRef::class.java))

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
        expressionDef = defs["ChoiceTypePeriodCondition"]!!
        MatcherAssert.assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        query = expressionDef.expression as Query?
        MatcherAssert.assertThat(query!!.source.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            query.source[0].expression,
            Matchers.instanceOf(Retrieve::class.java)
        )
        retrieve = query.source[0].expression as Retrieve
        MatcherAssert.assertThat(retrieve.dateProperty, Matchers.`is`("onset"))
        MatcherAssert.assertThat(retrieve.dateRange, Matchers.instanceOf(ParameterRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun intervalImplicitConversion() {
        TestUtils.runSemanticTest("fhir/r4/TestIntervalImplicitConversion.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirHelpers() {
        TestUtils.runSemanticTest("fhir/r4/TestFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun implicitFHIRHelpers() {
        TestUtils.runSemanticTest("fhir/r4/TestImplicitFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhir() {
        TestUtils.runSemanticTest("fhir/r4/TestFHIR.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithHelpers() {
        TestUtils.runSemanticTest("fhir/r4/TestFHIRWithHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun bundle() {
        TestUtils.runSemanticTest("fhir/r4/TestBundle.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun conceptConversion() {
        val translator = TestUtils.runSemanticTest("fhir/r4/TestConceptConversion.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        /*
                <expression localId="18" locator="15:3-16:42" xsi:type="Query">
                   <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                      <elementType name="fhir:Observation" xsi:type="NamedTypeSpecifier"/>
                   </resultTypeSpecifier>
                   <source localId="13" locator="15:3-15:17" alias="O">
                      <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                         <elementType name="fhir:Observation" xsi:type="NamedTypeSpecifier"/>
                      </resultTypeSpecifier>
                      <expression localId="12" locator="15:3-15:15" dataType="fhir:Observation" xsi:type="Retrieve">
                         <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                            <elementType name="fhir:Observation" xsi:type="NamedTypeSpecifier"/>
                         </resultTypeSpecifier>
                      </expression>
                   </source>
                   <where localId="17" locator="16:5-16:42" resultTypeName="t:Boolean" xsi:type="Equivalent">
                      <operand name="ToConcept" libraryName="FHIRHelpers" xsi:type="FunctionRef">
                         <operand localId="15" locator="16:11-16:16" resultTypeName="fhir:CodeableConcept" path="code" scope="O" xsi:type="Property"/>
                      </operand>
                      <operand xsi:type="ToConcept">
                         <operand localId="16" locator="16:20-16:42" resultTypeName="t:Code" name="ECOG performance code" xsi:type="CodeRef"/>
                      </operand>
                   </where>
                </expression>
        */
        var expressionDef: ExpressionDef = defs["TestCodeComparison"]!!

        MatcherAssert.assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        var query = expressionDef.expression as Query?
        MatcherAssert.assertThat(query!!.where, Matchers.instanceOf(Equivalent::class.java))
        var equivalent = query.where as Equivalent?
        MatcherAssert.assertThat(
            equivalent!!.operand[0],
            Matchers.instanceOf(FunctionRef::class.java)
        )
        var functionRef: FunctionRef = equivalent.operand[0] as FunctionRef
        MatcherAssert.assertThat(functionRef.libraryName, Matchers.`is`("FHIRHelpers"))
        MatcherAssert.assertThat(functionRef.name, Matchers.`is`("ToConcept"))
        MatcherAssert.assertThat(equivalent.operand[1], Matchers.instanceOf(ToConcept::class.java))

        expressionDef = defs["TestConceptComparison"]!!

        /*
                <expression localId="26" locator="19:3-20:43" xsi:type="Query">
                   <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                      <elementType name="fhir:Observation" xsi:type="NamedTypeSpecifier"/>
                   </resultTypeSpecifier>
                   <source localId="21" locator="19:3-19:17" alias="O">
                      <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                         <elementType name="fhir:Observation" xsi:type="NamedTypeSpecifier"/>
                      </resultTypeSpecifier>
                      <expression localId="20" locator="19:3-19:15" dataType="fhir:Observation" xsi:type="Retrieve">
                         <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                            <elementType name="fhir:Observation" xsi:type="NamedTypeSpecifier"/>
                         </resultTypeSpecifier>
                      </expression>
                   </source>
                   <where localId="25" locator="20:5-20:43" resultTypeName="t:Boolean" xsi:type="Equivalent">
                      <operand name="ToConcept" libraryName="FHIRHelpers" xsi:type="FunctionRef">
                         <operand localId="23" locator="20:11-20:16" resultTypeName="fhir:CodeableConcept" path="code" scope="O" xsi:type="Property"/>
                      </operand>
                      <operand localId="24" locator="20:20-20:43" resultTypeName="t:Concept" name="ECOG performance score" xsi:type="ConceptRef"/>
                   </where>
                </expression>
        */
        MatcherAssert.assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        query = expressionDef.expression as Query?
        MatcherAssert.assertThat(query!!.where, Matchers.instanceOf(Equivalent::class.java))
        equivalent = query.where as Equivalent?
        MatcherAssert.assertThat(
            equivalent!!.operand[0],
            Matchers.instanceOf(FunctionRef::class.java)
        )
        functionRef = equivalent.operand[0] as FunctionRef
        MatcherAssert.assertThat(functionRef.libraryName, Matchers.`is`("FHIRHelpers"))
        MatcherAssert.assertThat(functionRef.name, Matchers.`is`("ToConcept"))
        MatcherAssert.assertThat(equivalent.operand[1], Matchers.instanceOf(ConceptRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun retrieveWithConcept() {
        val translator = TestUtils.runSemanticTest("fhir/r4/TestRetrieveWithConcept.cql", 0)
        val library = translator.translatedLibrary
        val expressionDef = library!!.resolveExpressionRef("Test Tobacco Smoking Status")

        MatcherAssert.assertThat(
            expressionDef!!.expression,
            Matchers.instanceOf(Retrieve::class.java)
        )
        val retrieve = expressionDef.expression as Retrieve?
        MatcherAssert.assertThat(retrieve!!.codes, Matchers.instanceOf(ToList::class.java))
        val toList = retrieve.codes as ToList?
        MatcherAssert.assertThat(toList!!.operand, Matchers.instanceOf(CodeRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun exm108IdentifierHiding() {
        val translator =
            TestUtils.runSemanticTest(
                "fhir/r4/exm108/EXM108.cql",
                0,
                LibraryBuilder.SignatureLevel.All
            )
        // Should only be one identifier being hid after fixes, "Warafin"
        Assertions.assertEquals(1, translator.exceptions.size)
    }
}
