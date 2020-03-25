package org.cqframework.cql.cql2elm.fhir.stu3;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.*;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.cqframework.cql.cql2elm.matchers.QdmDataType.qdmDataType;
import static org.cqframework.cql.cql2elm.matchers.QuickDataType.quickDataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BaseTest {
    @BeforeClass
    public void Setup() {
        // Reset test utils to clear any models loaded by other tests
        TestUtils.reset();
    }

    @Test
    public void testChoiceWithAlternativeConversion() throws IOException {
        ExpressionDef def = (ExpressionDef) visitFile("fhir/stu3/TestChoiceTypes.cql");
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
        ExpressionDef def = (ExpressionDef) visitFile("fhir/stu3/TestURIConversion.cql");
    }

    @Test
    public void testFHIRTiming() throws IOException {
        ExpressionDef def = (ExpressionDef) visitFile("fhir/stu3/TestFHIRTiming.cql");
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

        // Then check that the where an IncludedIn with a Case as the left operand
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
        TranslatedLibrary library = visitFileLibrary("fhir/stu3/EqualityWithConversions.cql");
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
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/stu3/TestDoubleListPromotion.cql", 0);
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
        CqlTranslator translator = TestUtils.runSemanticTest("fhir/stu3/TestChoiceDateRangeOptimization.cql", 0, CqlTranslator.Options.EnableDateRangeOptimization);
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
    public void testIntervalImplicitConversion() throws IOException {
        TestUtils.runSemanticTest("fhir/stu3/TestIntervalImplicitConversion.cql", 0);
    }

    @Test
    public void testImplicitFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/stu3/TestImplicitFHIRHelpers.cql", 0);
    }
}
