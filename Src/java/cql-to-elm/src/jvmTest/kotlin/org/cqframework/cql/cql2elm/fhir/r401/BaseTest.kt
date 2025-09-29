package org.cqframework.cql.cql2elm.fhir.r401

import java.io.IOException
import java.util.stream.Collectors
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.TestUtils
import org.cqframework.cql.cql2elm.matchers.QuickDataType
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.And
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
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.Search
import org.hl7.elm.r1.SingletonFrom
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.TimeOfDay
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToList
import org.hl7.elm.r1.Today
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.With
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength", "LargeClass", "LongMethod", "VariableNaming")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun choiceWithAlternativeConversion() {
        val def = TestUtils.visitFile("fhir/r401/TestChoiceTypes.cql") as ExpressionDef?
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, `is`("Q"))
        val request = source.expression as Retrieve?
        assertThat(request!!.dataType, QuickDataType.quickDataType("QuestionnaireResponse"))

        // Then check that the suchThat of the with is a greater with a Case as the left operand
        val relationship = query.relationship[0]
        assertThat(relationship.suchThat, Matchers.instanceOf(Greater::class.java))
        val suchThat = relationship.suchThat as Greater?
        assertThat(suchThat!!.operand[0], Matchers.instanceOf(Case::class.java))
        val caseExpression = suchThat.operand[0] as Case
        assertThat(caseExpression.caseItem, Matchers.hasSize<Any?>(2))
        assertThat(caseExpression.caseItem[0].`when`, Matchers.instanceOf(Is::class.java))
        assertThat(caseExpression.caseItem[0].then, Matchers.instanceOf(FunctionRef::class.java))
        assertThat(caseExpression.caseItem[1].`when`, Matchers.instanceOf(Is::class.java))
        assertThat(caseExpression.caseItem[1].then, Matchers.instanceOf(FunctionRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun uriConversion() {
        // If this translates without errors, the test is successful
        TestUtils.visitFile("fhir/r401/TestURIConversion.cql") as ExpressionDef?
    }

    @Test
    @Throws(IOException::class)
    fun fhirTiming() {
        val def = TestUtils.visitFile("fhir/r401/TestFHIRTiming.cql") as ExpressionDef?
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
        assertThat(source.alias, `is`("P"))
        val request = source.expression as Retrieve?
        assertThat(request!!.dataType, QuickDataType.quickDataType("Procedure"))

        // Then check that the where is an In with a ToDateTime as the left operand
        val where = query.where
        assertThat(where, Matchers.instanceOf(In::class.java))
        val `in` = where as In?
        assertThat(`in`!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = `in`.operand[0] as FunctionRef
        assertThat(functionRef.name, `is`<String?>("ToDateTime"))
        assertThat(functionRef.operand[0], Matchers.instanceOf(As::class.java))
        val asExpression = functionRef.operand[0] as As
        assertThat(asExpression.asType!!.localPart, `is`("dateTime"))
        assertThat(asExpression.operand, Matchers.instanceOf(Property::class.java))
        val property = asExpression.operand as Property?
        assertThat(property!!.scope, `is`("P"))
        assertThat(property.path, `is`("performed"))
    }

    @Test
    @Throws(IOException::class)
    fun equalityWithConversions() {
        val library = TestUtils.visitFileLibrary("fhir/r401/EqualityWithConversions.cql")
        val getGender = library!!.resolveExpressionRef("GetGender")
        assertThat(getGender!!.expression, Matchers.instanceOf(Equal::class.java))
        val equal = getGender.expression as Equal?
        assertThat(equal!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = equal.operand[0] as FunctionRef
        assertThat(functionRef.name, `is`<String?>("ToString"))
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
    }

    @Test
    @Throws(IOException::class)
    fun doubleListPromotion() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestDoubleListPromotion.cql", 0)
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
        assertThat(codes, Matchers.instanceOf(ToList::class.java))
        assertThat((codes as ToList).operand, Matchers.instanceOf(CodeRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun choiceDateRangeOptimization() {
        val translator =
            TestUtils.runSemanticTest(
                "fhir/r401/TestChoiceDateRangeOptimization.cql",
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
        assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        var query = expressionDef.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        var retrieve: Retrieve = query.source[0].expression as Retrieve
        assertThat(retrieve.dateProperty, `is`<String?>("recordedDate"))
        assertThat(retrieve.dateRange, Matchers.instanceOf(ParameterRef::class.java))

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
        assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        query = expressionDef.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        retrieve = query.source[0].expression as Retrieve
        assertThat(retrieve.dateProperty, `is`<String?>("onset"))
        assertThat(retrieve.dateRange, Matchers.instanceOf(ParameterRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun intervalImplicitConversion() {
        TestUtils.runSemanticTest("fhir/r401/TestIntervalImplicitConversion.cql", 0)
    }

    private fun assertResultType(
        translatedLibrary: CompiledLibrary,
        expressionName: String,
        namespace: String?,
        name: String?,
    ) {
        val ed = translatedLibrary.resolveExpressionRef(expressionName)
        val resultType = ed?.resultType
        assertThat(resultType, Matchers.instanceOf(ClassType::class.java))
        val resultClassType = resultType as ClassType?
        assertThat(resultClassType!!.namespace, Matchers.equalTo(namespace))
        assertThat(resultClassType.simpleName, Matchers.equalTo(name))
    }

    @Test
    @Throws(IOException::class)
    fun fhirHelpers() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRHelpers.cql", 0)
        val translatedLibrary = translator.translatedLibrary
        assertResultType(translatedLibrary!!, "TestExtensions", "FHIR", "Extension")
        assertResultType(translatedLibrary, "TestElementExtensions", "FHIR", "Extension")
        assertResultType(translatedLibrary, "TestModifierExtensions", "FHIR", "Extension")
        assertResultType(translatedLibrary, "TestElementModifierExtensions", "FHIR", "Extension")

        val ed = translatedLibrary.resolveExpressionRef("TestChoiceConverts")
        val resultType = ed?.resultType
        assertThat(resultType, Matchers.instanceOf(ChoiceType::class.java))
        assertThat(
            resultType.toString(),
            Matchers.equalTo(
                "choice<System.String,System.Boolean,System.Date,System.DateTime,System.Decimal,System.Integer,System.Time,System.Quantity,System.Concept,System.Code,interval<System.Quantity>,interval<System.DateTime>,System.Ratio,FHIR.Address,FHIR.Annotation,FHIR.Attachment,FHIR.ContactPoint,FHIR.HumanName,FHIR.Identifier,FHIR.Money,FHIR.Reference,FHIR.SampledData,FHIR.Signature,FHIR.Timing,FHIR.ContactDetail,FHIR.Contributor,FHIR.DataRequirement,FHIR.Expression,FHIR.ParameterDefinition,FHIR.RelatedArtifact,FHIR.TriggerDefinition,FHIR.UsageContext,FHIR.Dosage,FHIR.Meta>"
            ),
        )
    }

    @Test
    @Throws(IOException::class)
    fun implicitFHIRHelpers() {
        TestUtils.runSemanticTest("fhir/r401/TestImplicitFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun context() {
        TestUtils.runSemanticTest("fhir/r401/TestContext.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun implicitContext() {
        TestUtils.runSemanticTest("fhir/r401/TestImplicitContext.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun parameterContext() {
        TestUtils.runSemanticTest("fhir/r401/TestParameterContext.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun encounterParameterContext() {
        TestUtils.runSemanticTest("fhir/r401/TestEncounterParameterContext.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun measureParameterContext() {
        TestUtils.runSemanticTest("fhir/r401/TestMeasureParameterContext.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun trace() {
        TestUtils.runSemanticTest("fhir/r401/TestTrace.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhir() {
        TestUtils.runSemanticTest("fhir/r401/TestFHIR.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithHelpers() {
        TestUtils.runSemanticTest("fhir/r401/TestFHIRWithHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun bundle() {
        TestUtils.runSemanticTest("fhir/r401/TestBundle.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun conceptConversion() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestConceptConversion.cql", 0)
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

        assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        var query = expressionDef.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equivalent::class.java))
        var equivalent = query.where as Equivalent?
        assertThat(equivalent!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        var functionRef: FunctionRef = equivalent.operand[0] as FunctionRef
        assertThat(functionRef.libraryName, `is`<String?>("FHIRHelpers"))
        assertThat(functionRef.name, `is`("ToConcept"))
        assertThat(equivalent.operand[1], Matchers.instanceOf(ToConcept::class.java))

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
        assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        query = expressionDef.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equivalent::class.java))
        equivalent = query.where as Equivalent?
        assertThat(equivalent!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        functionRef = equivalent.operand[0] as FunctionRef
        assertThat(functionRef.libraryName, `is`<String?>("FHIRHelpers"))
        assertThat(functionRef.name, `is`("ToConcept"))
        assertThat(equivalent.operand[1], Matchers.instanceOf(ConceptRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun retrieveWithConcept() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestRetrieveWithConcept.cql", 0)
        val library = translator.translatedLibrary
        val expressionDef = library!!.resolveExpressionRef("Test Tobacco Smoking Status")

        assertThat(expressionDef!!.expression, Matchers.instanceOf(Retrieve::class.java))
        val retrieve = expressionDef.expression as Retrieve?
        assertThat(retrieve!!.codes, Matchers.instanceOf(ToList::class.java))
        val toList = retrieve.codes as ToList?
        assertThat(toList!!.operand, Matchers.instanceOf(CodeRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun fhirNamespaces() {
        val translator =
            TestUtils.runSemanticTest(
                NamespaceInfo("Public", "http://cql.hl7.org/public"),
                "fhir/r401/TestFHIRNamespaces.cql",
                0,
            )
        val library = translator.translatedLibrary
        val usingDef = library!!.resolveUsingRef("FHIR")
        assertThat<UsingDef?>(usingDef, CoreMatchers.notNullValue())
        assertThat(usingDef!!.localIdentifier, `is`("FHIR"))
        assertThat(usingDef.uri, `is`("http://hl7.org/fhir"))
        assertThat(usingDef.version, `is`("4.0.1"))
        val includeDef = library.resolveIncludeRef("FHIRHelpers")
        assertThat<IncludeDef?>(includeDef, CoreMatchers.notNullValue())
        assertThat(includeDef!!.path, `is`("http://hl7.org/fhir/FHIRHelpers"))
        assertThat(includeDef.version, `is`("4.0.1"))
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithoutNamespaces() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRNamespaces.cql", 0)
        val library = translator.translatedLibrary
        val usingDef = library!!.resolveUsingRef("FHIR")
        assertThat<UsingDef?>(usingDef, CoreMatchers.notNullValue())
        assertThat(usingDef!!.localIdentifier, `is`("FHIR"))
        assertThat(usingDef.uri, `is`("http://hl7.org/fhir"))
        assertThat(usingDef.version, `is`("4.0.1"))
        val includeDef = library.resolveIncludeRef("FHIRHelpers")
        assertThat<IncludeDef?>(includeDef, CoreMatchers.notNullValue())
        assertThat(includeDef!!.path, `is`("FHIRHelpers"))
        assertThat(includeDef.version, `is`("4.0.1"))
    }

    @Test
    @Throws(IOException::class)
    fun fhirPath() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRPath.cql", 0)
        val library = translator.translatedLibrary
        var expressionDef = library!!.resolveExpressionRef("TestNow")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        assertThat(expressionDef!!.expression, Matchers.instanceOf(Now::class.java))
        expressionDef = library.resolveExpressionRef("TestToday")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        assertThat(expressionDef!!.expression, Matchers.instanceOf(Today::class.java))
        expressionDef = library.resolveExpressionRef("TestTimeOfDay")
        assertThat(expressionDef!!.expression, Matchers.instanceOf(TimeOfDay::class.java))
        val xml = translator.toXml()
        assertThat(xml, CoreMatchers.notNullValue())
        /*
        // Doesn't work because this literal adds carriage returns
        assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<library xmlns=\"urn:hl7-org:elm:r1\" xmlns:t=\"urn:hl7-org:elm-types:r1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:fhir=\"http://hl7.org/fhir\" xmlns:qdm43=\"urn:healthit-gov:qdm:v4_3\" xmlns:qdm53=\"urn:healthit-gov:qdm:v5_3\" xmlns:a=\"urn:hl7-org:cql-annotations:r1\">\n" +
                "   <annotation translatorOptions=\"\" xsi:type=\"a:CqlToElmInfo\"/>\n" +
                "   <identifier id=\"TestFHIRPath\"/>\n" +
                "   <schemaIdentifier id=\"urn:hl7-org:elm\" version=\"r1\"/>\n" +
                "   <usings>\n" +
                "      <def localIdentifier=\"System\" uri=\"urn:hl7-org:elm-types:r1\"/>\n" +
                "      <def localIdentifier=\"FHIR\" uri=\"http://hl7.org/fhir\" version=\"4.0.1\"/>\n" +
                "   </usings>\n" +
                "   <includes>\n" +
                "      <def localIdentifier=\"FHIRHelpers\" path=\"FHIRHelpers\" version=\"4.0.1\"/>\n" +
                "   </includes>\n" +
                "   <contexts>\n" +
                "      <def name=\"Patient\"/>\n" +
                "   </contexts>\n" +
                "   <statements>\n" +
                "      <def name=\"Patient\" context=\"Patient\">\n" +
                "         <expression xsi:type=\"SingletonFrom\">\n" +
                "            <operand dataType=\"fhir:Patient\" templateId=\"http://hl7.org/fhir/StructureDefinition/Patient\" xsi:type=\"Retrieve\"/>\n" +
                "         </expression>\n" +
                "      </def>\n" +
                "      <def name=\"TestToday\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression xsi:type=\"Today\"/>\n" +
                "      </def>\n" +
                "      <def name=\"TestNow\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression xsi:type=\"Now\"/>\n" +
                "      </def>\n" +
                "      <def name=\"TestTimeOfDay\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression xsi:type=\"TimeOfDay\"/>\n" +
                "      </def>\n" +
                "      <def name=\"Encounters\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression dataType=\"fhir:Encounter\" templateId=\"http://hl7.org/fhir/StructureDefinition/Encounter\" xsi:type=\"Retrieve\"/>\n" +
                "      </def>\n" +
                "      <def name=\"TestTodayInWhere\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression xsi:type=\"Query\">\n" +
                "            <source alias=\"$this\">\n" +
                "               <expression name=\"Encounters\" xsi:type=\"ExpressionRef\"/>\n" +
                "            </source>\n" +
                "            <where xsi:type=\"And\">\n" +
                "               <operand xsi:type=\"Equal\">\n" +
                "                  <operand name=\"ToString\" libraryName=\"FHIRHelpers\" xsi:type=\"FunctionRef\">\n" +
                "                     <operand path=\"status\" scope=\"$this\" xsi:type=\"Property\"/>\n" +
                "                  </operand>\n" +
                "                  <operand valueType=\"t:String\" value=\"in-progress\" xsi:type=\"Literal\"/>\n" +
                "               </operand>\n" +
                "               <operand xsi:type=\"LessOrEqual\">\n" +
                "                  <operand name=\"ToDateTime\" libraryName=\"FHIRHelpers\" xsi:type=\"FunctionRef\">\n" +
                "                     <operand path=\"end\" xsi:type=\"Property\">\n" +
                "                        <source path=\"period\" scope=\"$this\" xsi:type=\"Property\"/>\n" +
                "                     </operand>\n" +
                "                  </operand>\n" +
                "                  <operand xsi:type=\"ToDateTime\">\n" +
                "                     <operand xsi:type=\"Subtract\">\n" +
                "                        <operand xsi:type=\"Today\"/>\n" +
                "                        <operand value=\"72\" unit=\"hours\" xsi:type=\"Quantity\"/>\n" +
                "                     </operand>\n" +
                "                  </operand>\n" +
                "               </operand>\n" +
                "            </where>\n" +
                "         </expression>\n" +
                "      </def>\n" +
                "      <def name=\"TestNowInWhere\" context=\"Patient\" accessLevel=\"Public\">\n" +
                "         <expression xsi:type=\"Query\">\n" +
                "            <source alias=\"$this\">\n" +
                "               <expression name=\"Encounters\" xsi:type=\"ExpressionRef\"/>\n" +
                "            </source>\n" +
                "            <where xsi:type=\"And\">\n" +
                "               <operand xsi:type=\"Equal\">\n" +
                "                  <operand name=\"ToString\" libraryName=\"FHIRHelpers\" xsi:type=\"FunctionRef\">\n" +
                "                     <operand path=\"status\" scope=\"$this\" xsi:type=\"Property\"/>\n" +
                "                  </operand>\n" +
                "                  <operand valueType=\"t:String\" value=\"in-progress\" xsi:type=\"Literal\"/>\n" +
                "               </operand>\n" +
                "               <operand xsi:type=\"LessOrEqual\">\n" +
                "                  <operand name=\"ToDateTime\" libraryName=\"FHIRHelpers\" xsi:type=\"FunctionRef\">\n" +
                "                     <operand path=\"end\" xsi:type=\"Property\">\n" +
                "                        <source path=\"period\" scope=\"$this\" xsi:type=\"Property\"/>\n" +
                "                     </operand>\n" +
                "                  </operand>\n" +
                "                  <operand xsi:type=\"Subtract\">\n" +
                "                     <operand xsi:type=\"Now\"/>\n" +
                "                     <operand value=\"72\" unit=\"hours\" xsi:type=\"Quantity\"/>\n" +
                "                  </operand>\n" +
                "               </operand>\n" +
                "            </where>\n" +
                "         </expression>\n" +
                "      </def>\n" +
                "   </statements>\n" +
                "</library>\n"));
         */
    }

    @Test
    @Throws(IOException::class)
    fun fhirPathLiteralStringEscapes() {
        val translator =
            TestUtils.runSemanticTest("fhir/r401/TestFHIRPathLiteralStringEscapes.cql", 0)
        val library = translator.translatedLibrary
        val expressionDef = library!!.resolveExpressionRef("Test")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        val xml = translator.toXml()
        assertThat(xml, CoreMatchers.notNullValue())
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

    @Test
    @Throws(IOException::class)
    fun searchPath() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestInclude.cql", 0)
        val library = translator.translatedLibrary
        var expressionDef = library!!.resolveExpressionRef("TestPractitionerSearch1")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        var expression = expressionDef!!.expression
        assertThat(expression, CoreMatchers.notNullValue())
        assertThat(expression, Matchers.instanceOf(Retrieve::class.java))
        assertThat((expression as Retrieve).codeProperty, Matchers.equalTo("?name"))

        expressionDef = library.resolveExpressionRef("TestPractitionerSearch1A")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        expression = expressionDef!!.expression
        assertThat(expression, CoreMatchers.notNullValue())
        assertThat(expression, Matchers.instanceOf(Query::class.java))
        assertThat((expression as Query).where, CoreMatchers.notNullValue())
        assertThat(expression.where, Matchers.instanceOf(Equal::class.java))
        val eq = expression.where as Equal?
        assertThat(eq!!.operand[0], Matchers.instanceOf(Search::class.java))
        val s: Search = eq.operand[0] as Search
        assertThat(s.path, Matchers.equalTo<String?>("name"))
    }

    @Test
    @Throws(IOException::class)
    fun include() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestInclude.cql", 0)
        val library = translator.translatedLibrary

        /*
        define TestMedicationRequest1:
          [MedicationRequest] MR
            where MR.medication.reference.resolve().as(Medication).code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

            <query>
              <retrieve>
              <where>
                <equivalent>
                  <functionref "ToConcept">
                    <property path="code">
                      <as "Medication">
                        <functionref "resolve">
                          <functionref "ToString">
                            <property path="reference">
                              <property path="medication" scope="MR"/>
                            </property>
                          </functionref>
                        </functionref>
                      </as>
                    </property>
                  </functionref>
                  <functionref "ToConcept">
                    <coderef/>
                  </functionref>
                </equivalent>
              </where>
            </query>
         */
        var expressionDef = library!!.resolveExpressionRef("TestMedicationRequest1")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        var expression = expressionDef!!.expression
        assertThat(expression, Matchers.instanceOf(Query::class.java))
        assertThat((expression as Query).where, Matchers.instanceOf(Equivalent::class.java))
        var eqv = expression.where as Equivalent?
        assertThat(eqv!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        var fr = eqv.operand[0] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("ToConcept"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        var p = fr.operand[0] as Property?
        assertThat(p!!.path, Matchers.equalTo("code"))
        assertThat(p.source, Matchers.instanceOf(As::class.java))
        var `as` = p.source as As?
        assertThat(`as`!!.asType!!.localPart, Matchers.equalTo("Medication"))
        assertThat(`as`.operand, Matchers.instanceOf(FunctionRef::class.java))
        fr = `as`.operand as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("resolve"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        fr = fr.operand[0] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("ToString"))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property?
        assertThat(p!!.path, Matchers.equalTo("reference"))
        assertThat(p.source, Matchers.instanceOf(Property::class.java))
        p = p.source as Property?
        assertThat(p!!.path, Matchers.equalTo("medication"))
        assertThat(p.scope, Matchers.equalTo("MR"))

        /*
        define TestMedicationRequest1A:
          [MedicationRequest] MR
            with [Medication] M such that
              MR.medication = M.reference()
                and M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

          <query>
            <retrieve "MedicationRequest" scope="MR"/>
            <withRelationship>
              <retrieve "Medication" scope="M"/>
              <suchThat>
                <and>
                  <equal>
                    <as type="Reference">
                      <property path="medication" scope="MR"/>
                    </as>
                    <functionref name="reference">
                      <aliasRef scope="M"/>
                    </functionref>
                  </equal>
                  <equivalent>
                    <functionref name="ToConcept">
                      <property path="code" scope="M"/>
                    </functionref>
                    <functionref name="ToConcept">
                      <coderef/>
                    </functionref>
                  </equivalent>
                </and>
              </suchThat>
            </withRelationship>
          </query>
         */
        expressionDef = library.resolveExpressionRef("TestMedicationRequest1A")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        expression = expressionDef!!.expression
        assertThat(expression, Matchers.instanceOf(Query::class.java))
        var q = expression as Query?
        assertThat<Any?>(q!!.relationship, CoreMatchers.notNullValue())
        assertThat(q.relationship.size, Matchers.equalTo(1))
        assertThat(q.relationship[0], Matchers.instanceOf(With::class.java))
        var w = q.relationship[0] as With
        assertThat(w.suchThat, CoreMatchers.notNullValue())
        assertThat(w.suchThat, Matchers.instanceOf(And::class.java))
        val a = w.suchThat as And?
        assertThat<Any?>(a!!.operand, CoreMatchers.notNullValue())
        assertThat(a.operand.size, Matchers.equalTo(2))
        assertThat(a.operand[0], Matchers.instanceOf(Equal::class.java))
        val eq = a.operand[0] as Equal
        assertThat<Any?>(eq.operand, CoreMatchers.notNullValue())
        assertThat(eq.operand.size, Matchers.equalTo(2))
        assertThat(eq.operand[0], Matchers.instanceOf(As::class.java))
        `as` = eq.operand[0] as As?
        assertThat(`as`!!.operand, Matchers.instanceOf(Property::class.java))
        p = `as`.operand as Property?
        assertThat(p!!.path, Matchers.equalTo("medication"))
        assertThat(p.scope, Matchers.equalTo("MR"))
        assertThat(eq.operand[1], Matchers.instanceOf(FunctionRef::class.java))
        fr = eq.operand[1] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("reference"))
        assertThat<Any?>(fr.operand, CoreMatchers.notNullValue())
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(AliasRef::class.java))
        val ar: AliasRef = fr.operand[0] as AliasRef
        assertThat(ar.name, Matchers.equalTo<String?>("M"))
        assertThat(a.operand[1], Matchers.instanceOf(Equivalent::class.java))
        eqv = a.operand[1] as Equivalent?
        assertThat(eqv!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        fr = eqv.operand[0] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("ToConcept"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property?
        assertThat(p!!.path, Matchers.equalTo("code"))
        assertThat(p.scope, Matchers.equalTo("M"))
        assertThat(eqv.operand[1], Matchers.instanceOf(ToConcept::class.java))

        /*
        define TestMedicationRequest1B:
          [MedicationRequest] MR
            with [MR.medication -> Medication] M
              such that M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

          <query>
            <retrieve MedicationRequest/>
            <withRelationship>
              <retrieve Medication>
                <context>
                  <property path="medication" scope="MR"/>
                </context>
              </retrieve>
              <suchThat>
                <equivalent>
                  <functionRef name="ToConcept">
                    <property path="code" scope="M"/>
                  </functionRef>
                  <functionRef name="ToConcept">
                    <codeRef/>
                  </functionRef>
                </equivalent>
              </suchThat>
            </withRelationship>
          </query>
         */
        expressionDef = library.resolveExpressionRef("TestMedicationRequest1B")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        expression = expressionDef!!.expression
        assertThat(expression, Matchers.instanceOf(Query::class.java))
        q = expression as Query?
        assertThat<Any?>(q!!.relationship, CoreMatchers.notNullValue())
        assertThat(q.relationship.size, Matchers.equalTo(1))
        assertThat(q.relationship[0], Matchers.instanceOf(With::class.java))
        w = q.relationship[0] as With
        assertThat(w.expression, Matchers.instanceOf(Retrieve::class.java))
        var r = w.expression as Retrieve?
        assertThat(r!!.context, Matchers.instanceOf(Property::class.java))
        p = r.context as Property?
        assertThat(p!!.path, Matchers.equalTo("medication"))
        assertThat(p.scope, Matchers.equalTo("MR"))
        assertThat(w.suchThat, CoreMatchers.notNullValue())
        assertThat(w.suchThat, Matchers.instanceOf(Equivalent::class.java))
        eqv = w.suchThat as Equivalent?
        assertThat(eqv!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        fr = eqv.operand[0] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("ToConcept"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property?
        assertThat(p!!.path, Matchers.equalTo("code"))
        assertThat(p.scope, Matchers.equalTo("M"))
        assertThat(eqv.operand[1], Matchers.instanceOf(ToConcept::class.java))

        /*
        define TestMedicationRequest1C:
          [MedicationRequest] MR
            let M: [MR.medication -> Medication]
            where M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

          <query>
            <retrieve MedicationRequest/>
            <let alias="M">
              <singletonFrom>
                <retrieve Medication>
                  <context>
                    <property path="medication" source="MR"/>
                  </context>
                </retrieve>
              </singletonFrom>
            </let>
            <where>
              <equivalent>
                <functionRef name="ToConcept">
                  <property path="code" scope="M"/>
                </functionRef>
                <functionRef name="ToConcept">
                  <codeRef/>
                </functionRef>
              </equivalent>
            </where>
          </query>
         */
        expressionDef = library.resolveExpressionRef("TestMedicationRequest1C")
        assertThat<ExpressionDef?>(expressionDef, CoreMatchers.notNullValue())
        expression = expressionDef!!.expression
        assertThat(expression, Matchers.instanceOf(Query::class.java))
        q = expression as Query?
        assertThat<Any?>(q!!.let, CoreMatchers.notNullValue())
        assertThat(q.let.size, Matchers.equalTo(1))
        val lc = q.let[0]
        assertThat(lc.expression, Matchers.instanceOf(SingletonFrom::class.java))
        val sf = lc.expression as SingletonFrom?
        assertThat(sf!!.operand, Matchers.instanceOf(Retrieve::class.java))
        r = sf.operand as Retrieve?
        assertThat(r!!.context, Matchers.instanceOf(Property::class.java))
        p = r.context as Property?
        assertThat(p!!.path, Matchers.equalTo("medication"))
        assertThat(p.scope, Matchers.equalTo("MR"))
        assertThat(q.where, Matchers.instanceOf(Equivalent::class.java))
        eqv = q.where as Equivalent?
        assertThat(eqv!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        fr = eqv.operand[0] as FunctionRef?
        assertThat(fr!!.name, Matchers.equalTo("ToConcept"))
        assertThat(fr.operand.size, Matchers.equalTo(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property?
        assertThat(p!!.path, Matchers.equalTo("code"))
        assertThat(p.source, Matchers.instanceOf(QueryLetRef::class.java))
        val qlr = p.source as QueryLetRef?
        assertThat(qlr!!.name, Matchers.equalTo("M"))
        assertThat(eqv.operand[1], Matchers.instanceOf(ToConcept::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun overload() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestOverload.cql", 0)
        assertThat(translator.warnings.size, `is`(2))

        val warningMessages =
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(warningMessages.toString(), translator.warnings.size, `is`(2))

        val first =
            "String literal 'Encounter' matches the identifier Encounter. Consider whether the identifier was intended instead."
        val second =
            "The function TestOverload.Stringify has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime."

        assertThat(
            warningMessages.toString(),
            warningMessages,
            Matchers.containsInAnyOrder(first, second),
        )
    }

    @Test
    @Throws(IOException::class)
    fun overloadOutput() {
        val translator =
            TestUtils.runSemanticTest(
                "fhir/r401/TestOverload.cql",
                0,
                LibraryBuilder.SignatureLevel.Overloads,
            )
        assertThat(translator.warnings.size, `is`(1))

        val warningMessages =
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(
            warningMessages.toString(),
            warningMessages,
            Matchers.contains(
                "String literal 'Encounter' matches the identifier Encounter. Consider whether the identifier was intended instead."
            ),
        )
    }

    @Test
    @Throws(IOException::class)
    fun overloadForward() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestOverloadForward.cql", 0)
        assertThat(translator.warnings.size, `is`(2))

        val warningMessages =
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(warningMessages.toString(), translator.warnings.size, `is`(2))

        val first =
            "String literal 'Encounter' matches the identifier Encounter. Consider whether the identifier was intended instead."
        val second =
            "The function TestOverloadForward.Stringify has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime."

        assertThat(
            warningMessages.toString(),
            warningMessages,
            Matchers.containsInAnyOrder(first, second),
        )
    }

    @Test
    @Throws(IOException::class)
    fun overloadForwardOutput() {
        val translator =
            TestUtils.runSemanticTest(
                "fhir/r401/TestOverloadForward.cql",
                0,
                LibraryBuilder.SignatureLevel.Overloads,
            )
        assertThat(translator.warnings.size, `is`(1))

        val warningMessages =
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(
            warningMessages.toString(),
            warningMessages,
            Matchers.contains(
                "String literal 'Encounter' matches the identifier Encounter. Consider whether the identifier was intended instead."
            ),
        )
    }

    @Test
    @Throws(IOException::class)
    fun medicationRequest() {
        val translator = TestUtils.runSemanticTest("fhir/r401/TestMedicationRequest.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        var def = defs["Antithrombotic Therapy at Discharge"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
        assertThat(def!!.expression, Matchers.instanceOf(Query::class.java))
        var q = def.expression as Query?
        assertThat(q!!.source.size, `is`(1))
        assertThat(q.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        var r = q.source[0].expression as Retrieve?
        assertThat(
            r!!.templateId,
            `is`("http://hl7.org/fhir/StructureDefinition/MedicationRequest"),
        )
        assertThat(r.codeProperty, `is`("medication"))
        assertThat(r.codeComparator, `is`("in"))
        assertThat(r.codes, Matchers.instanceOf(ValueSetRef::class.java))
        var vsr = r.codes as ValueSetRef?
        assertThat(vsr!!.name, `is`("Antithrombotic Therapy"))

        def = defs["Antithrombotic Therapy at Discharge (2)"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
        assertThat(def!!.expression, Matchers.instanceOf(Union::class.java))
        val u = def.expression as Union?
        assertThat(u!!.operand.size, `is`(2))
        assertThat(u.operand[0], Matchers.instanceOf(Retrieve::class.java))
        r = u.operand[0] as Retrieve?
        assertThat(
            r!!.templateId,
            `is`("http://hl7.org/fhir/StructureDefinition/MedicationRequest"),
        )
        assertThat(r.codeProperty, `is`("medication"))
        assertThat(r.codeComparator, `is`("in"))
        assertThat(r.codes, Matchers.instanceOf(ValueSetRef::class.java))
        vsr = r.codes as ValueSetRef?
        assertThat(vsr!!.name, `is`("Antithrombotic Therapy"))

        assertThat(u.operand[1], Matchers.instanceOf(Query::class.java))
        q = u.operand[1] as Query?
        assertThat(q!!.source.size, `is`(1))
        assertThat(q.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        r = q.source[0].expression as Retrieve?
        assertThat(
            r!!.templateId,
            `is`("http://hl7.org/fhir/StructureDefinition/MedicationRequest"),
        )
        assertThat(r.codeProperty == null, `is`(true))
        assertThat(r.codes == null, `is`(true))
        assertThat<Any?>(q.relationship, CoreMatchers.notNullValue())
        assertThat(q.relationship.size, `is`(1))
        assertThat(q.relationship[0], Matchers.instanceOf(With::class.java))
        val w = q.relationship[0] as With
        assertThat(w.expression, Matchers.instanceOf(Retrieve::class.java))
        r = w.expression as Retrieve?
        assertThat(r!!.templateId, `is`("http://hl7.org/fhir/StructureDefinition/Medication"))
        assertThat(r.codeProperty == null, `is`(true))
        assertThat(r.codes == null, `is`(true))
        val resultType = r.resultType
        assertThat(resultType, Matchers.instanceOf(ListType::class.java))
        assertThat((resultType as ListType).elementType, Matchers.instanceOf(ClassType::class.java))
        assertThat((resultType.elementType as ClassType).name, `is`("FHIR.Medication"))
        assertThat(w.suchThat, Matchers.instanceOf(And::class.java))
        val a = w.suchThat as And?
        assertThat(a!!.operand[0], Matchers.instanceOf(Equal::class.java))
        val eq = a.operand[0] as Equal
        assertThat(eq.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        var fr = eq.operand[0] as FunctionRef?
        assertThat(fr!!.libraryName, `is`("FHIRHelpers"))
        assertThat(fr.name, `is`("ToString"))
        assertThat(fr.operand.size, `is`(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        var p: Property = fr.operand[0] as Property
        assertThat(p.scope, `is`<String?>("M"))
        assertThat(p.path, `is`("id"))
        assertThat(eq.operand[1], Matchers.instanceOf(Last::class.java))
        val l = eq.operand[1] as Last
        assertThat(l.source, Matchers.instanceOf(Split::class.java))
        val s = l.source as Split?
        assertThat(s!!.stringToSplit, Matchers.instanceOf(FunctionRef::class.java))
        fr = s.stringToSplit as FunctionRef?
        assertThat(fr!!.libraryName, `is`("FHIRHelpers"))
        assertThat(fr.name, `is`("ToString"))
        assertThat(fr.operand.size, `is`(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property
        assertThat(p.scope, `is`<String?>("MR"))
        assertThat(p.path, `is`("medication.reference"))
        // assertThat(s.getSeparator(), is("/"));
        assertThat(a.operand[1], Matchers.instanceOf(InValueSet::class.java))
        val ivs: InValueSet = a.operand[1] as InValueSet
        assertThat(ivs.valueset?.name, `is`<String?>("Antithrombotic Therapy"))
        assertThat(ivs.code, Matchers.instanceOf(FunctionRef::class.java))
        fr = ivs.code as FunctionRef?
        assertThat(fr!!.libraryName, `is`("FHIRHelpers"))
        assertThat(fr.name, `is`("ToConcept"))
        assertThat(fr.operand.size, `is`(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property
        assertThat(p.scope, `is`<String?>("M"))
        assertThat(p.path, `is`("code"))
    }
}
