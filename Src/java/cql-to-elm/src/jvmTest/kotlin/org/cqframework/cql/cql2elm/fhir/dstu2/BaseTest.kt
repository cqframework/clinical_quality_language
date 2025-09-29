package org.cqframework.cql.cql2elm.fhir.dstu2

import java.io.IOException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.TestUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToList
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun equalityWithConversions() {
        val library = TestUtils.visitFileLibrary("fhir/dstu2/EqualityWithConversions.cql")
        val getGender = library!!.resolveExpressionRef("GetGender")
        assertThat(getGender!!.expression, Matchers.instanceOf(Equal::class.java))
        val equal = getGender.expression as Equal?
        assertThat(equal!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = equal.operand[0] as FunctionRef
        assertThat(functionRef.name, `is`("ToString"))
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
    }

    @Test
    @Throws(IOException::class)
    fun doubleListPromotion() {
        val translator = TestUtils.runSemanticTest("fhir/dstu2/TestDoubleListPromotion.cql", 0)
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
                "fhir/dstu2/TestChoiceDateRangeOptimization.cql",
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
        assertThat(expressionDef.expression, Matchers.instanceOf(Query::class.java))
        var query = expressionDef.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        var retrieve: Retrieve = query.source[0].expression as Retrieve
        assertThat(retrieve.dateProperty, `is`("onsetDateTime"))
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
        assertThat(retrieve.dateProperty, `is`("onsetPeriod"))
        assertThat(retrieve.dateRange, Matchers.instanceOf(ParameterRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun intervalImplicitConversion() {
        TestUtils.runSemanticTest("fhir/dstu2/TestIntervalImplicitConversion.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirHelpers() {
        TestUtils.runSemanticTest("fhir/dstu2/TestFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun implicitFHIRHelpers() {
        TestUtils.runSemanticTest("fhir/dstu2/TestImplicitFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun parameterContext() {
        TestUtils.runSemanticTest("fhir/dstu2/TestParameterContext.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhir() {
        TestUtils.runSemanticTest("fhir/dstu2/TestFHIR.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithHelpers() {
        TestUtils.runSemanticTest("fhir/dstu2/TestFHIRWithHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun bundle() {
        TestUtils.runSemanticTest("fhir/dstu2/TestBundle.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun conceptConversion() {
        val translator = TestUtils.runSemanticTest("fhir/dstu2/TestConceptConversion.cql", 0)
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
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
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
        assertThat(functionRef.libraryName, `is`("FHIRHelpers"))
        assertThat(functionRef.name, `is`("ToConcept"))
        assertThat(equivalent.operand[1], Matchers.instanceOf(ConceptRef::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun retrieveWithConcept() {
        val translator = TestUtils.runSemanticTest("fhir/dstu2/TestRetrieveWithConcept.cql", 0)
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
                "fhir/dstu2/TestFHIRNamespaces.cql",
                0
            )
        val library = translator.translatedLibrary
        val includeDef = library!!.resolveIncludeRef("FHIRHelpers")
        assertThat<IncludeDef?>(includeDef, Matchers.notNullValue())
        assertThat(includeDef!!.path, `is`("http://hl7.org/fhir/FHIRHelpers"))
        assertThat(includeDef.version, `is`("1.0.2"))
    }

    @Test
    @Throws(IOException::class)
    fun fhirNamespacesSignatureLevelNone() {
        TestUtils.runSemanticTest(
            NamespaceInfo("Public", "http://cql.hl7.org/public"),
            "fhir/dstu2/TestFHIRNamespaces.cql",
            0,
            LibraryBuilder.SignatureLevel.None
        )
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithoutNamespaces() {
        val translator = TestUtils.runSemanticTest("fhir/dstu2/TestFHIRNamespaces.cql", 0)
        val library = translator.translatedLibrary
        val includeDef = library!!.resolveIncludeRef("FHIRHelpers")
        assertThat<IncludeDef?>(includeDef, Matchers.notNullValue())
        assertThat(includeDef!!.path, `is`("FHIRHelpers"))
        assertThat(includeDef.version, `is`("1.0.2"))
    }
}
