package org.cqframework.cql.cql2elm.fhir.r401;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.hl7.cql.model.NamespaceInfo;
import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.ClassType;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.cqframework.cql.cql2elm.TestUtils.visitFile;
import static org.cqframework.cql.cql2elm.TestUtils.visitFileLibrary;
import static org.cqframework.cql.cql2elm.matchers.QuickDataType.quickDataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    @Test
    public void testChoiceWithAlternativeConversion() throws IOException {
        ExpressionDef def = (ExpressionDef) visitFile("fhir/r401/TestChoiceTypes.cql");
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("Q"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quickDataType("QuestionnaireResponse"));

        // Then check that the suchThat of the with is a greater with a Case as the left operand
        RelationshipClause relationship = query.getRelationship().get(0);
        assertThat(relationship.getSuchThat(), instanceOf(Greater.class));
        Greater suchThat = (Greater)relationship.getSuchThat();
        assertThat(suchThat.getOperand().get(0), instanceOf(Case.class));
        Case caseExpression = (Case)suchThat.getOperand().get(0);
        assertThat(caseExpression.getCaseItem(), hasSize(2));
        assertThat(caseExpression.getCaseItem().get(0).getWhen(), instanceOf(Is.class));
        assertThat(caseExpression.getCaseItem().get(0).getThen(), instanceOf(FunctionRef.class));
        assertThat(caseExpression.getCaseItem().get(1).getWhen(), instanceOf(Is.class));
        assertThat(caseExpression.getCaseItem().get(1).getThen(), instanceOf(FunctionRef.class));
    }

    @Test
    public void testURIConversion() throws IOException {
        // If this translates without errors, the test is successful
        ExpressionDef def = (ExpressionDef) visitFile("fhir/r401/TestURIConversion.cql");
    }

    @Test
    public void testFHIRTiming() throws IOException {
        ExpressionDef def = (ExpressionDef) visitFile("fhir/r401/TestFHIRTiming.cql");
        // Query->
        //  where->
        //      IncludedIn->
        //          left->
        //              ToInterval()
        //                  As(fhir:Period) ->
        //                      Property(P.performed)
        //          right-> MeasurementPeriod
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("P"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quickDataType("Procedure"));

        // Then check that the where an IncludedIn with a ToInterval as the left operand
        Expression where = query.getWhere();
        assertThat(where, instanceOf(IncludedIn.class));
        IncludedIn includedIn = (IncludedIn)where;
        assertThat(includedIn.getOperand().get(0), instanceOf(FunctionRef.class));
        FunctionRef functionRef = (FunctionRef)includedIn.getOperand().get(0);
        assertThat(functionRef.getName(), is("ToInterval"));
        assertThat(functionRef.getOperand().get(0), instanceOf(As.class));
        As asExpression = (As)functionRef.getOperand().get(0);
        assertThat(asExpression.getAsType().getLocalPart(), is("Period"));
        assertThat(asExpression.getOperand(), instanceOf(Property.class));
        Property property = (Property)asExpression.getOperand();
        assertThat(property.getScope(), is("P"));
        assertThat(property.getPath(), is("performed"));
    }

    @Test
    public void testEqualityWithConversions() throws IOException {
        CompiledLibrary library = visitFileLibrary("fhir/r401/EqualityWithConversions.cql");
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
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestDoubleListPromotion.cql", 0);
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
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestChoiceDateRangeOptimization.cql", 0, CqlCompilerOptions.Options.EnableDateRangeOptimization);
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

    @Test
    public void testIntervalImplicitConversion() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestIntervalImplicitConversion.cql", 0);
    }

    private void assertResultType(CompiledLibrary translatedLibrary, String expressionName, String namespace, String name) {
        ExpressionDef ed = translatedLibrary.resolveExpressionRef(expressionName);
        DataType resultType = ed.getExpression().getResultType();
        assertThat(resultType, instanceOf(ClassType.class));
        ClassType resultClassType = (ClassType)resultType;
        assertThat(resultClassType.getNamespace(), equalTo(namespace));
        assertThat(resultClassType.getSimpleName(), equalTo(name));
    }

    @Test
    public void testFHIRHelpers() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRHelpers.cql", 0);
        CompiledLibrary translatedLibrary = translator.getTranslatedLibrary();
        assertResultType(translatedLibrary, "TestExtensions", "FHIR", "Extension");
        assertResultType(translatedLibrary, "TestElementExtensions", "FHIR", "Extension");
        assertResultType(translatedLibrary, "TestModifierExtensions", "FHIR", "Extension");
        assertResultType(translatedLibrary, "TestElementModifierExtensions", "FHIR", "Extension");

        ExpressionDef ed = translatedLibrary.resolveExpressionRef("TestChoiceConverts");
        DataType resultType = ed.getExpression().getResultType();
        assertThat(resultType, instanceOf(ChoiceType.class));
        assertThat(resultType.toString(), equalTo("choice<System.String,System.Boolean,System.Date,System.DateTime,System.Decimal,System.Integer,System.Time,System.Quantity,System.Concept,System.Code,interval<System.Quantity>,interval<System.DateTime>,System.Ratio,FHIR.Address,FHIR.Annotation,FHIR.Attachment,FHIR.ContactPoint,FHIR.HumanName,FHIR.Identifier,FHIR.Money,FHIR.Reference,FHIR.SampledData,FHIR.Signature,FHIR.Timing,FHIR.ContactDetail,FHIR.Contributor,FHIR.DataRequirement,FHIR.Expression,FHIR.ParameterDefinition,FHIR.RelatedArtifact,FHIR.TriggerDefinition,FHIR.UsageContext,FHIR.Dosage,FHIR.Meta>"));
    }

    @Test
    public void testImplicitFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestImplicitFHIRHelpers.cql", 0);
    }

    @Test
    public void testContext() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestContext.cql", 0);
    }

    @Test
    public void testImplicitContext() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestImplicitContext.cql", 0);
    }

    @Test
    public void testParameterContext() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestParameterContext.cql", 0);
    }

    @Test
    public void testEncounterParameterContext() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestEncounterParameterContext.cql", 0);
    }

    @Test
    public void testMeasureParameterContext() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestMeasureParameterContext.cql", 0);
    }

    @Test
    public void testTrace() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestTrace.cql", 0);
    }

    @Test
    public void testFHIR() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestFHIR.cql", 0);
    }

    @Test
    public void testFHIRWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestFHIRWithHelpers.cql", 0);
    }

    @Test
    public void testBundle() throws IOException {
        TestUtils.runSemanticTest("fhir/r401/TestBundle.cql", 0);
    }

    @Test
    public void testConceptConversion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestConceptConversion.cql", 0);
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
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestRetrieveWithConcept.cql", 0);
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
        CqlTranslator translator = TestUtils.runSemanticTest(new NamespaceInfo("Public", "http://cql.hl7.org/public"), "fhir/r401/TestFHIRNamespaces.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        UsingDef usingDef = library.resolveUsingRef("FHIR");
        assertThat(usingDef, notNullValue());
        assertThat(usingDef.getLocalIdentifier(), is("FHIR"));
        assertThat(usingDef.getUri(), is ("http://hl7.org/fhir"));
        assertThat(usingDef.getVersion(), is("4.0.1"));
        IncludeDef includeDef = library.resolveIncludeRef("FHIRHelpers");
        assertThat(includeDef, notNullValue());
        assertThat(includeDef.getPath(), is("http://hl7.org/fhir/FHIRHelpers"));
        assertThat(includeDef.getVersion(), is("4.0.1"));
    }

    @Test
    public void testFHIRWithoutNamespaces() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRNamespaces.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        UsingDef usingDef = library.resolveUsingRef("FHIR");
        assertThat(usingDef, notNullValue());
        assertThat(usingDef.getLocalIdentifier(), is("FHIR"));
        assertThat(usingDef.getUri(), is ("http://hl7.org/fhir"));
        assertThat(usingDef.getVersion(), is("4.0.1"));
        IncludeDef includeDef = library.resolveIncludeRef("FHIRHelpers");
        assertThat(includeDef, notNullValue());
        assertThat(includeDef.getPath(), is("FHIRHelpers"));
        assertThat(includeDef.getVersion(), is("4.0.1"));
    }

    @Test
    public void testFHIRPath() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRPath.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        ExpressionDef expressionDef = library.resolveExpressionRef("TestNow");
        assertThat(expressionDef, notNullValue());
        assertThat(expressionDef.getExpression(), instanceOf(Now.class));
        expressionDef = library.resolveExpressionRef("TestToday");
        assertThat(expressionDef, notNullValue());
        assertThat(expressionDef.getExpression(), instanceOf(Today.class));
        expressionDef = library.resolveExpressionRef("TestTimeOfDay");
        assertThat(expressionDef.getExpression(), instanceOf(TimeOfDay.class));
        String xml = translator.toXml();
        assertThat(xml, notNullValue());
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
    public void testFHIRPathLiteralStringEscapes() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestFHIRPathLiteralStringEscapes.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        ExpressionDef expressionDef = library.resolveExpressionRef("Test");
        assertThat(expressionDef, notNullValue());
        String xml = translator.toXml();
        assertThat(xml, notNullValue());
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
    public void testSearchPath() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestInclude.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        ExpressionDef expressionDef = library.resolveExpressionRef("TestPractitionerSearch1");
        assertThat(expressionDef, notNullValue());
        Expression expression = expressionDef.getExpression();
        assertThat(expression, notNullValue());
        assertThat(expression, instanceOf(Retrieve.class));
        assertThat(((Retrieve)expression).getCodeProperty(), equalTo("?name"));

        expressionDef = library.resolveExpressionRef("TestPractitionerSearch1A");
        assertThat(expressionDef, notNullValue());
        expression = expressionDef.getExpression();
        assertThat(expression, notNullValue());
        assertThat(expression, instanceOf(Query.class));
        assertThat(((Query)expression).getWhere(), notNullValue());
        assertThat(((Query)expression).getWhere(), instanceOf(Equal.class));
        Equal eq = (Equal)((Query)expression).getWhere();
        assertThat(eq.getOperand().get(0), instanceOf(Search.class));
        Search s = (Search)eq.getOperand().get(0);
        assertThat(s.getPath(), equalTo("name"));
    }

    @Test
    public void testInclude() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestInclude.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();

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
        ExpressionDef expressionDef = library.resolveExpressionRef("TestMedicationRequest1");
        assertThat(expressionDef, notNullValue());
        Expression expression = expressionDef.getExpression();
        assertThat(expression, instanceOf(Query.class));
        assertThat(((Query)expression).getWhere(), instanceOf(Equivalent.class));
        Equivalent eqv = (Equivalent)((Query)expression).getWhere();
        assertThat(eqv.getOperand().get(0), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)eqv.getOperand().get(0);
        assertThat(fr.getName(), equalTo("ToConcept"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        Property p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("code"));
        assertThat(p.getSource(), instanceOf(As.class));
        As as = (As)p.getSource();
        assertThat(as.getAsType().getLocalPart(), equalTo("Medication"));
        assertThat(as.getOperand(), instanceOf(FunctionRef.class));
        fr = (FunctionRef)as.getOperand();
        assertThat(fr.getName(), equalTo("resolve"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef)fr.getOperand().get(0);
        assertThat(fr.getName(), equalTo("ToString"));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("reference"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property)p.getSource();
        assertThat(p.getPath(), equalTo("medication"));
        assertThat(p.getScope(), equalTo("MR"));

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
        expressionDef = library.resolveExpressionRef("TestMedicationRequest1A");
        assertThat(expressionDef, notNullValue());
        expression = expressionDef.getExpression();
        assertThat(expression, instanceOf(Query.class));
        Query q = (Query)expression;
        assertThat(q.getRelationship(), notNullValue());
        assertThat(q.getRelationship().size(), equalTo(1));
        assertThat(q.getRelationship().get(0), instanceOf(With.class));
        With w = (With)q.getRelationship().get(0);
        assertThat(w.getSuchThat(), notNullValue());
        assertThat(w.getSuchThat(), instanceOf(And.class));
        And a = (And)w.getSuchThat();
        assertThat(a.getOperand(), notNullValue());
        assertThat(a.getOperand().size(), equalTo(2));
        assertThat(a.getOperand().get(0), instanceOf(Equal.class));
        Equal eq = (Equal)a.getOperand().get(0);
        assertThat(eq.getOperand(), notNullValue());
        assertThat(eq.getOperand().size(), equalTo(2));
        assertThat(eq.getOperand().get(0), instanceOf(As.class));
        as = (As)eq.getOperand().get(0);
        assertThat(as.getOperand(), instanceOf(Property.class));
        p = (Property)as.getOperand();
        assertThat(p.getPath(), equalTo("medication"));
        assertThat(p.getScope(), equalTo("MR"));
        assertThat(eq.getOperand().get(1), instanceOf(FunctionRef.class));
        fr = (FunctionRef)eq.getOperand().get(1);
        assertThat(fr.getName(), equalTo("reference"));
        assertThat(fr.getOperand(), notNullValue());
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(AliasRef.class));
        AliasRef ar = (AliasRef)fr.getOperand().get(0);
        assertThat(ar.getName(), equalTo("M"));
        assertThat(a.getOperand().get(1), instanceOf(Equivalent.class));
        eqv = (Equivalent)a.getOperand().get(1);
        assertThat(eqv.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef)eqv.getOperand().get(0);
        assertThat(fr.getName(), equalTo("ToConcept"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("code"));
        assertThat(p.getScope(), equalTo("M"));
        assertThat(eqv.getOperand().get(1), instanceOf(ToConcept.class));

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
        expressionDef = library.resolveExpressionRef("TestMedicationRequest1B");
        assertThat(expressionDef, notNullValue());
        expression = expressionDef.getExpression();
        assertThat(expression, instanceOf(Query.class));
        q = (Query)expression;
        assertThat(q.getRelationship(), notNullValue());
        assertThat(q.getRelationship().size(), equalTo(1));
        assertThat(q.getRelationship().get(0), instanceOf(With.class));
        w = (With)q.getRelationship().get(0);
        assertThat(w.getExpression(), instanceOf(Retrieve.class));
        Retrieve r = (Retrieve)w.getExpression();
        assertThat(r.getContext(), instanceOf(Property.class));
        p = (Property)r.getContext();
        assertThat(p.getPath(), equalTo("medication"));
        assertThat(p.getScope(), equalTo("MR"));
        assertThat(w.getSuchThat(), notNullValue());
        assertThat(w.getSuchThat(), instanceOf(Equivalent.class));
        eqv = (Equivalent)w.getSuchThat();
        assertThat(eqv.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef)eqv.getOperand().get(0);
        assertThat(fr.getName(), equalTo("ToConcept"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("code"));
        assertThat(p.getScope(), equalTo("M"));
        assertThat(eqv.getOperand().get(1), instanceOf(ToConcept.class));

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
        expressionDef = library.resolveExpressionRef("TestMedicationRequest1C");
        assertThat(expressionDef, notNullValue());
        expression = expressionDef.getExpression();
        assertThat(expression, instanceOf(Query.class));
        q = (Query)expression;
        assertThat(q.getLet(), notNullValue());
        assertThat(q.getLet().size(), equalTo(1));
        LetClause lc = q.getLet().get(0);
        assertThat(lc.getExpression(), instanceOf(SingletonFrom.class));
        SingletonFrom sf = (SingletonFrom)lc.getExpression();
        assertThat(sf.getOperand(), instanceOf(Retrieve.class));
        r = (Retrieve)sf.getOperand();
        assertThat(r.getContext(), instanceOf(Property.class));
        p = (Property)r.getContext();
        assertThat(p.getPath(), equalTo("medication"));
        assertThat(p.getScope(), equalTo("MR"));
        assertThat(q.getWhere(), instanceOf(Equivalent.class));
        eqv = (Equivalent)q.getWhere();
        assertThat(eqv.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef)eqv.getOperand().get(0);
        assertThat(fr.getName(), equalTo("ToConcept"));
        assertThat(fr.getOperand().size(), equalTo(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("code"));
        assertThat(p.getSource(), instanceOf(QueryLetRef.class));
        QueryLetRef qlr = (QueryLetRef)p.getSource();
        assertThat(qlr.getName(), equalTo("M"));
        assertThat(eqv.getOperand().get(1), instanceOf(ToConcept.class));
    }

    @Test
    public void testOverload() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestOverload.cql", 0);
        assertThat(translator.getWarnings().size(), is(2));

        final List<String> warningMessages = translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(2));

        final String first = "You used a string literal: [Encounter] here that matches an identifier in scope: [Encounter]. Did you mean to use the identifier instead? \n";
        final String second = "The function TestOverload.Stringify has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime.";

        assertThat(warningMessages.toString(), warningMessages, containsInAnyOrder(first, second));
    }

    @Test
    public void testOverloadOutput() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestOverload.cql", 0, LibraryBuilder.SignatureLevel.Overloads);
        assertThat(translator.getWarnings().size(), is(1));

        final List<String> warningMessages = translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warningMessages, contains("You used a string literal: [Encounter] here that matches an identifier in scope: [Encounter]. Did you mean to use the identifier instead? \n"));
    }

    @Test
    public void testOverloadForward() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestOverloadForward.cql", 0);
        assertThat(translator.getWarnings().size(), is(2));

        final List<String> warningMessages = translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(2));

        final String first = "You used a string literal: [Encounter] here that matches an identifier in scope: [Encounter]. Did you mean to use the identifier instead? \n";
        final String second = "The function TestOverloadForward.Stringify has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime.";

        assertThat(warningMessages.toString(), warningMessages, containsInAnyOrder(first, second));
    }

    @Test
    public void testOverloadForwardOutput() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/r401/TestOverloadForward.cql", 0, LibraryBuilder.SignatureLevel.Overloads);
        assertThat(translator.getWarnings().size(), is(1));

        final List<String> warningMessages = translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warningMessages, contains("You used a string literal: [Encounter] here that matches an identifier in scope: [Encounter]. Did you mean to use the identifier instead? \n"));
    }
}
