package org.cqframework.cql.cql2elm.fhir.stu301

import java.io.IOException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.TestUtils
import org.cqframework.cql.cql2elm.matchers.QuickDataType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToList
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun choiceWithAlternativeConversion() {
        val def = TestUtils.visitFile("fhir/stu301/TestChoiceTypes.cql")
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, `is`("Q"))
        val request = source.expression as Retrieve?
        assertThat(request!!.dataType, QuickDataType.quickDataType("QuestionnaireResponse"))

        // Then check that the suchThat of the with is a greater with a Case as the left operand
        val relationship = query.relationship[0]
        assertThat<Expression?>(relationship.suchThat, instanceOf<Expression?>(Greater::class.java))
        val suchThat = relationship.suchThat as Greater?
        assertThat(suchThat!!.operand[0], instanceOf(Case::class.java))
        val caseExpression = suchThat.operand[0] as Case
        assertThat<MutableCollection<*>?>(caseExpression.caseItem, Matchers.hasSize<Any?>(2))
        assertThat(caseExpression.caseItem[0].`when`, instanceOf(Is::class.java))
        assertThat(caseExpression.caseItem[0].then, instanceOf(FunctionRef::class.java))
        assertThat(caseExpression.caseItem[1].`when`, instanceOf(Is::class.java))
        assertThat(caseExpression.caseItem[1].then, instanceOf(FunctionRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun uriConversion() {
        // If this translates without errors, the test is successful
        TestUtils.visitFile("fhir/stu301/TestURIConversion.cql")
    }

    @Test
    @Throws(IOException::class)
    fun fhirTiming() {
        val def = TestUtils.visitFile("fhir/stu301/TestFHIRTiming.cql")
        // Query->
        //  where->
        //      IncludedIn->
        //          left->
        //              ToInterval()
        //                  As(fhir:Period) ->
        //                      Property(P.performed)
        //          right-> MeasurementPeriod
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, `is`("P"))
        val request = source.expression as Retrieve?
        assertThat(request!!.dataType, QuickDataType.quickDataType("Procedure"))

        // Then check that the where an IncludedIn with a ToInterval as the left operand
        val where = query.where
        assertThat<Expression?>(where, instanceOf<Expression?>(In::class.java))
        val inDef = where as In?
        assertThat(inDef!!.operand[0], instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = inDef.operand[0] as FunctionRef
        assertThat(functionRef.name, `is`("ToDateTime"))
        assertThat(functionRef.operand[0], instanceOf(As::class.java))
        val asExpression = functionRef.operand[0] as As
        assertThat(asExpression.asType!!.localPart, `is`("dateTime"))
        assertThat<Expression?>(asExpression.operand, instanceOf<Expression?>(Property::class.java))
        val property = asExpression.operand as Property?
        assertThat(property!!.scope, `is`("P"))
        assertThat(property.path, `is`("performed"))
    }

    @Test
    @Throws(IOException::class)
    fun equalityWithConversions() {
        val library = TestUtils.visitFileLibrary("fhir/stu301/EqualityWithConversions.cql")
        val getGender = library!!.resolveExpressionRef("GetGender")
        assertThat<Expression?>(getGender!!.expression, instanceOf<Expression?>(Equal::class.java))
        val equal = getGender.expression as Equal?
        assertThat(equal!!.operand[0], instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = equal.operand[0] as FunctionRef
        assertThat(functionRef.name, `is`("ToString"))
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
    }

    @Test
    @Throws(IOException::class)
    fun doubleListPromotion() {
        val translator = TestUtils.runSemanticTest("fhir/stu301/TestDoubleListPromotion.cql", 0)
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
        assertThat<Expression?>(codes, instanceOf<Expression?>(ToList::class.java))
        assertThat<Expression?>(
            (codes as ToList).operand,
            instanceOf<Expression?>(CodeRef::class.java),
        )
    }

    @Test
    @Throws(IOException::class)
    fun choiceDateRangeOptimization() {
        val translator =
            TestUtils.runSemanticTest(
                "fhir/stu301/TestChoiceDateRangeOptimization.cql",
                0,
                CqlCompilerOptions.Options.EnableDateRangeOptimization,
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
        assertThat<Expression?>(
            expressionDef.expression,
            instanceOf<Expression?>(Query::class.java),
        )
        var query = expressionDef.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, instanceOf(Retrieve::class.java))
        var retrieve: Retrieve = query.source[0].expression as Retrieve
        assertThat(retrieve.dateProperty, `is`("assertedDate"))
        assertThat<Expression?>(
            retrieve.dateRange,
            instanceOf<Expression?>(ParameterRef::class.java),
        )

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
        assertThat<Expression?>(
            expressionDef.expression,
            instanceOf<Expression?>(Query::class.java),
        )
        query = expressionDef.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, instanceOf(Retrieve::class.java))
        retrieve = query.source[0].expression as Retrieve
        assertThat(retrieve.dateProperty, `is`("onset"))
        assertThat<Expression?>(
            retrieve.dateRange,
            instanceOf<Expression?>(ParameterRef::class.java),
        )
    }

    @Test
    @Throws(IOException::class)
    fun intervalImplicitConversion() {
        TestUtils.runSemanticTest("fhir/stu301/TestIntervalImplicitConversion.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirHelpers() {
        TestUtils.runSemanticTest("fhir/stu301/TestFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun implicitFHIRHelpers() {
        TestUtils.runSemanticTest("fhir/stu301/TestImplicitFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhir() {
        TestUtils.runSemanticTest("fhir/stu301/TestFHIR.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithHelpers() {
        TestUtils.runSemanticTest("fhir/stu301/TestFHIRWithHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun bundle() {
        TestUtils.runSemanticTest("fhir/stu301/TestBundle.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun conceptConversion() {
        val translator = TestUtils.runSemanticTest("fhir/stu301/TestConceptConversion.cql", 0)
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

        assertThat<Expression?>(
            expressionDef.expression,
            instanceOf<Expression?>(Query::class.java),
        )
        var query = expressionDef.expression as Query?
        assertThat<Expression?>(query!!.where, instanceOf<Expression?>(Equivalent::class.java))
        var equivalent = query.where as Equivalent?
        assertThat(equivalent!!.operand[0], instanceOf(FunctionRef::class.java))
        var functionRef: FunctionRef = equivalent.operand[0] as FunctionRef
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
        assertThat(functionRef.name, `is`("ToConcept"))
        assertThat(equivalent.operand[1], instanceOf(ToConcept::class.java))

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
        assertThat<Expression?>(
            expressionDef.expression,
            instanceOf<Expression?>(Query::class.java),
        )
        query = expressionDef.expression as Query?
        assertThat<Expression?>(query!!.where, instanceOf<Expression?>(Equivalent::class.java))
        equivalent = query.where as Equivalent?
        assertThat(equivalent!!.operand[0], instanceOf(FunctionRef::class.java))
        functionRef = equivalent.operand[0] as FunctionRef
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
        assertThat(functionRef.name, `is`("ToConcept"))
        assertThat(equivalent.operand[1], instanceOf(ConceptRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun retrieveWithConcept() {
        val translator = TestUtils.runSemanticTest("fhir/stu301/TestRetrieveWithConcept.cql", 0)
        val library = translator.translatedLibrary
        val expressionDef = library!!.resolveExpressionRef("Test Tobacco Smoking Status")

        assertThat<Expression?>(
            expressionDef!!.expression,
            instanceOf<Expression?>(Retrieve::class.java),
        )
        val retrieve = expressionDef.expression as Retrieve?
        assertThat<Expression?>(retrieve!!.codes, instanceOf<Expression?>(ToList::class.java))
        val toList = retrieve.codes as ToList?
        assertThat<Expression?>(toList!!.operand, instanceOf<Expression?>(CodeRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun fhirNamespaces() {
        val translator =
            TestUtils.runSemanticTest(
                NamespaceInfo("Public", "http://cql.hl7.org/public"),
                "fhir/stu301/TestFHIRNamespaces.cql",
                0,
            )
        val library = translator.translatedLibrary
        val includeDef = library!!.resolveIncludeRef("FHIRHelpers")
        assertThat<IncludeDef?>(includeDef, Matchers.notNullValue())
        assertThat(includeDef!!.path, `is`("http://hl7.org/fhir/FHIRHelpers"))
        assertThat(includeDef.version, `is`("3.0.1"))
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithoutNamespaces() {
        val translator = TestUtils.runSemanticTest("fhir/stu301/TestFHIRNamespaces.cql", 0)
        val library = translator.translatedLibrary
        val includeDef = library!!.resolveIncludeRef("FHIRHelpers")
        assertThat<IncludeDef?>(includeDef, Matchers.notNullValue())
        assertThat(includeDef!!.path, `is`("FHIRHelpers"))
        assertThat(includeDef.version, `is`("3.0.1"))
    }

    @Test
    @Throws(IOException::class)
    fun fhirPathLiteralStringEscapes() {
        val translator =
            TestUtils.runSemanticTest("fhir/stu301/TestFHIRPathLiteralStringEscapes.cql", 0)
        val library = translator.translatedLibrary
        val expressionDef = library!!.resolveExpressionRef("Test")
        assertThat<ExpressionDef?>(expressionDef, Matchers.notNullValue())
        val xml = translator.toXml()
        assertThat(xml, Matchers.notNullValue())
        /*
        // Doesn't work because this literal adds carriage returns
        assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<library xmlns=\"urn:hl7-org:elm:r1\" xmlns:t=\"urn:hl7-org:elm-types:r1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:fhir=\"http://hl7.org/fhir\" xmlns:qdm43=\"urn:healthit-gov:qdm:v4_3\" xmlns:qdm53=\"urn:healthit-gov:qdm:v5_3\" xmlns:a=\"urn:hl7-org:cql-annotations:r1\">\n" +
                "   <annotation translatorOptions=\"\" xsi:type=\"a:CqlToElmInfo\"/>\n" +
                "   <identifier id=\"TestFHIRPath\"/>\n" +
                "   <schemaIdentifier id=\"urn:hl7-org:elm\" version=\"r1\"/>\n" +
                "   <usings>\n" +
                "      <def localIdentifier=\"System\" uri=\"urn:hl7-org:elm-types:r1\"/>\n" +
                "      <def localIdentifier=\"FHIR\" uri=\"http://hl7.org/fhir\" version=\"4.0.0\"/>\n" +
                "   </usings>\n" +
                "   <includes>\n" +
                "      <def localIdentifier=\"FHIRHelpers\" path=\"FHIRHelpers\" version=\"4.0.0\"/>\n" +
                "   </includes>\n" +
                "   <parameters>\n" +
                "      <def name=\"Patient\" accessLevel=\"Public\">\n" +
                "         <parameterTypeSpecifier name=\"fhir:Patient\" xsi:type=\"NamedTypeSpecifier\"/>\n" +
                "      </def>\n" +
                "   </parameters>\n" +
                "   <statements>\n" +
                "      <def name=\"Test\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression xsi:type=\"ConvertsToString\">\n" +
                "            <operand valueType=\"t:String\" value=\"\\/\f&#xd;&#xa;\t&quot;`'*\" xsi:type=\"Literal\"/>\n" +
                "         </expression>\n" +
                "      </def>\n" +
                "   </statements>\n" +
                "</library>\n"));
        */
    }
}
