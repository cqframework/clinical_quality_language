package org.cqframework.cql.cql2elm.quick.v330;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.visitFile;
import static org.cqframework.cql.cql2elm.TestUtils.visitFileLibrary;
import static org.cqframework.cql.cql2elm.matchers.Quick2DataType.quick2DataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    @BeforeClass
    public void Setup() {
        // Reset test utils to clear any models loaded by other tests
        TestUtils.reset();
    }

    //@Test
    // BTR -> The types in QUICK are collapsed so this doesn't result in a choice between equally viable alternatives
    // The test is not valid for the QUICK Model
    public void testChoiceWithAlternativeConversion() throws IOException {
        ExpressionDef def = (ExpressionDef) visitFile("quick/v330/TestChoiceTypes.cql");
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("Q"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quick2DataType("QuestionnaireResponse"));

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

    //@Test
    // QUICK types render the conversion under test unnecessary
    public void testURIConversion() throws IOException {
        // If this translates without errors, the test is successful
        ExpressionDef def = (ExpressionDef) visitFile("quick/v330/TestURIConversion.cql");
    }

    @Test
    public void testFHIRTiming() throws IOException {
        ExpressionDef def = (ExpressionDef) visitFile("quick/v330/TestFHIRTiming.cql");
        // Query->
        //  where->
        //      IncludedIn->
        //          left->
        //              As(Interval<DateTime>) ->
        //                  Property(P.performed)
        //          right-> MeasurementPeriod
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("P"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quick2DataType("Procedure"));

        // Then check that the where an IncludedIn with a Case as the left operand
        Expression where = query.getWhere();
        assertThat(where, instanceOf(IncludedIn.class));
        IncludedIn includedIn = (IncludedIn)where;
        assertThat(includedIn.getOperand().get(0), instanceOf(As.class));
        As asExpression = (As)includedIn.getOperand().get(0);
        assertThat(asExpression.getAsTypeSpecifier(), instanceOf(IntervalTypeSpecifier.class));
        IntervalTypeSpecifier intervalTypeSpecifier = (IntervalTypeSpecifier)asExpression.getAsTypeSpecifier();
        assertThat(intervalTypeSpecifier.getPointType(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier namedTypeSpecifier = (NamedTypeSpecifier)intervalTypeSpecifier.getPointType();
        assertThat(namedTypeSpecifier.getName().getLocalPart(), is("DateTime"));
        assertThat(asExpression.getOperand(), instanceOf(Property.class));
        Property property = (Property)asExpression.getOperand();
        assertThat(property.getScope(), is("P"));
        assertThat(property.getPath(), is("performed"));
    }

    @Test
    public void testEqualityWithConversions() throws IOException {
        TranslatedLibrary library = visitFileLibrary("quick/v330/EqualityWithConversions.cql");
        ExpressionDef getGender = library.resolveExpressionRef("GetGender");
        assertThat(getGender.getExpression(), instanceOf(Equal.class));
        Equal equal = (Equal)getGender.getExpression();
        assertThat(equal.getOperand().get(1), instanceOf(Literal.class));
        Literal literal = (Literal)equal.getOperand().get(1);
        assertThat(literal.getValue(), is("female"));
    }

    @Test
    public void testDoubleListPromotion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("quick/v330/TestDoubleListPromotion.cql", 0);
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
        ToList toList = (ToList)codes;
        assertThat(toList.getOperand(), instanceOf(CodeRef.class));
        CodeRef codeRef = (CodeRef)toList.getOperand();
        assertThat(codeRef.getName(), is("T0"));
    }

    @Test
    public void testIntervalImplicitConversion() throws IOException {
        TestUtils.runSemanticTest("quick/v330/TestIntervalImplicitConversion.cql", 0);
    }

    @Test
    public void testImplicitFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("quick/v330/TestImplicitFHIRHelpers.cql", 0);
    }
}
