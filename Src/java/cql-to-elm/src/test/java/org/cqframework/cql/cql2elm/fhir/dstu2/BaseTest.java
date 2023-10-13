package org.cqframework.cql.cql2elm.fhir.dstu2;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.hl7.cql.model.NamespaceInfo;
import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.visitFileLibrary;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    @Test
    public void testEqualityWithConversions() throws IOException {
        CompiledLibrary library = visitFileLibrary("fhir/dstu2/EqualityWithConversions.cql");
        ExpressionDef getGender = library.resolveExpressionRef("GetGender");
        assertThat(getGender.getExpression(), instanceOf(Equal.class));
        Equal equal = (Equal)getGender.getExpression();
        assertThat(equal.getOperand().get(0), instanceOf(FunctionRef.class));
        FunctionRef functionRef = (FunctionRef)equal.getOperand().get(0);
        assertThat(functionRef.getName(), is("ToString"));
        assertThat(functionRef.getLibraryName(), is("FHIRHelpers"));
    }

    @Test
    public void testDoubleListPromotion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/dstu2/TestDoubleListPromotion.cql", 0);
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
    public void testChoiceDateRangeOptimization() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/dstu2/TestChoiceDateRangeOptimization.cql", 0, CqlCompilerOptions.Options.EnableDateRangeOptimization);
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
        assertThat(retrieve.getDateProperty(), is("onsetDateTime"));
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
        assertThat(retrieve.getDateProperty(), is("onsetPeriod"));
        assertThat(retrieve.getDateRange(), instanceOf(ParameterRef.class));
    }

    @Test
    public void testIntervalImplicitConversion() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestIntervalImplicitConversion.cql", 0);
    }

    @Test
    public void testFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestFHIRHelpers.cql", 0);
    }

    @Test
    public void testImplicitFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestImplicitFHIRHelpers.cql", 0);
    }

    @Test
    public void testParameterContext() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestParameterContext.cql", 0);
    }

    @Test
    public void testFHIR() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestFHIR.cql", 0);
    }

    @Test
    public void testFHIRWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestFHIRWithHelpers.cql", 0);
    }

    @Test
    public void testBundle() throws IOException {
        TestUtils.runSemanticTest("fhir/dstu2/TestBundle.cql", 0);
    }

    @Test
    public void testConceptConversion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/dstu2/TestConceptConversion.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
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

        ExpressionDef expressionDef = defs.get("TestCodeComparison");

        assertThat(expressionDef.getExpression(), instanceOf(Query.class));
        Query query = (Query)expressionDef.getExpression();
        assertThat(query.getWhere(), instanceOf(Equivalent.class));
        Equivalent equivalent = (Equivalent)query.getWhere();
        assertThat(equivalent.getOperand().get(0), instanceOf(FunctionRef.class));
        FunctionRef functionRef = (FunctionRef)equivalent.getOperand().get(0);
        assertThat(functionRef.getLibraryName(), is("FHIRHelpers"));
        assertThat(functionRef.getName(), is("ToConcept"));
        assertThat(equivalent.getOperand().get(1), instanceOf(ToConcept.class));

        expressionDef = defs.get("TestConceptComparison");

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

        assertThat(expressionDef.getExpression(), instanceOf(Query.class));
        query = (Query)expressionDef.getExpression();
        assertThat(query.getWhere(), instanceOf(Equivalent.class));
        equivalent = (Equivalent)query.getWhere();
        assertThat(equivalent.getOperand().get(0), instanceOf(FunctionRef.class));
        functionRef = (FunctionRef)equivalent.getOperand().get(0);
        assertThat(functionRef.getLibraryName(), is("FHIRHelpers"));
        assertThat(functionRef.getName(), is("ToConcept"));
        assertThat(equivalent.getOperand().get(1), instanceOf(ConceptRef.class));
    }

    @Test
    public void testRetrieveWithConcept() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/dstu2/TestRetrieveWithConcept.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        ExpressionDef expressionDef = library.resolveExpressionRef("Test Tobacco Smoking Status");

        assertThat(expressionDef.getExpression(), instanceOf(Retrieve.class));
        Retrieve retrieve = (Retrieve)expressionDef.getExpression();
        assertThat(retrieve.getCodes(), instanceOf(ToList.class));
        ToList toList = (ToList)retrieve.getCodes();
        assertThat(toList.getOperand(), instanceOf(CodeRef.class));
    }

    @Test
    public void testFHIRNamespaces() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest(new NamespaceInfo("Public", "http://cql.hl7.org/public"), "fhir/dstu2/TestFHIRNamespaces.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        IncludeDef includeDef = library.resolveIncludeRef("FHIRHelpers");
        assertThat(includeDef, notNullValue());
        assertThat(includeDef.getPath(), is("http://hl7.org/fhir/FHIRHelpers"));
        assertThat(includeDef.getVersion(), is("1.0.2"));
    }

    @Test
    public void testFHIRNamespacesSignatureLevelNone() throws IOException {
        TestUtils.runSemanticTest(new NamespaceInfo("Public", "http://cql.hl7.org/public"), "fhir/dstu2/TestFHIRNamespaces.cql", 0, LibraryBuilder.SignatureLevel.None);
    }

    @Test
    public void testFHIRWithoutNamespaces() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/dstu2/TestFHIRNamespaces.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        IncludeDef includeDef = library.resolveIncludeRef("FHIRHelpers");
        assertThat(includeDef, notNullValue());
        assertThat(includeDef.getPath(), is("FHIRHelpers"));
        assertThat(includeDef.getVersion(), is("1.0.2"));
    }
}
