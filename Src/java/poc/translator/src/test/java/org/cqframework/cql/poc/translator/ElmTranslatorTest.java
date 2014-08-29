package org.cqframework.cql.poc.translator;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.poc.translator.model.Identifier;
import org.hl7.elm.r1.Add;
import org.hl7.elm.r1.And;
import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.Divide;
import org.hl7.elm.r1.Equal;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Greater;
import org.hl7.elm.r1.GreaterOrEqual;
import org.hl7.elm.r1.IsNull;
import org.hl7.elm.r1.Less;
import org.hl7.elm.r1.LessOrEqual;
import org.hl7.elm.r1.Modulo;
import org.hl7.elm.r1.Multiply;
import org.hl7.elm.r1.Not;
import org.hl7.elm.r1.NotEqual;
import org.hl7.elm.r1.Null;
import org.hl7.elm.r1.Or;
import org.hl7.elm.r1.Power;
import org.hl7.elm.r1.Quantity;
import org.hl7.elm.r1.Subtract;
import org.hl7.elm.r1.Xor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.poc.translator.TestUtils.*;
import static org.cqframework.cql.poc.translator.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ElmTranslatorTest {
    @Test
    public void testRhinoWorks() {
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();
            String command = "1+1";
            Object result = cx.evaluateString(scope, command, "<cmd>", 1, null);
            assertThat(Context.toString(result), is("2"));
        } finally {
            Context.exit();
        }
    }

    @Test
    public void testLet(){
        ExpressionDef let = (ExpressionDef) visitData("let b = true");
        assertThat(let.getName(), is("b"));
        assertTrackable(let);
    }

    @Test
    public void testBooleanLiteral(){
        ExpressionDef let = (ExpressionDef) visitData("let b = true");
        assertThat(let.getExpression(), literalFor(true));
        assertTrackable(let.getExpression());

        let = (ExpressionDef) visitData("let b = false");
        assertThat(let.getExpression(), literalFor(false));
    }

    @Test
    public void testStringLiteral(){
        ExpressionDef let = (ExpressionDef) visitData("let st = 'hey its a string'");
        assertThat(let.getExpression(), literalFor("hey its a string"));
        assertTrackable(let.getExpression());
    }

    @Test
    public void testNullLiteral(){
        ExpressionDef let = (ExpressionDef) visitData("let st = null");
        assertThat(let.getExpression(), instanceOf(Null.class));
        assertTrackable(let.getExpression());
    }

    @Test
    public void testQuantityLiteral(){
        ExpressionDef let = (ExpressionDef) visitData("let st = 1");
        assertThat(let.getExpression(), literalFor(1));
        assertTrackable(let.getExpression());

        let = (ExpressionDef) visitData("let st = 1.1");
        assertThat(let.getExpression(), literalFor(1.1));

        let = (ExpressionDef) visitData("let st = 1.1 u'mm'");
        Quantity quantity = (Quantity) let.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(1.1)));
        assertThat(quantity.getUnit(), is("u'mm'"));
        assertTrackable(quantity);

        let = (ExpressionDef) visitData("let st = 1.1 weeks");
        quantity = (Quantity) let.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(1.1)));
        assertThat(quantity.getUnit(), is("weeks"));
    }

    @Test
    public void testAndExpressions(){
        ExpressionDef let = (ExpressionDef) visitData("let st = true and false");
        And and = (And) let.getExpression();
        Expression left = and.getOperand().get(0);
        Expression right = and.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(and);
        assertTrackable(left);
        assertTrackable(right);
    }

    @Test
    public void testOrExpressions(){
        ExpressionDef let = (ExpressionDef) visitData("let st = true or false");
        Or or = (Or) let.getExpression();
        Expression left = or.getOperand().get(0);
        Expression right = or.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(or);
        assertTrackable(left);
        assertTrackable(right);

        let = (ExpressionDef) visitData("let st = true xor false");
        Xor xor = (Xor) let.getExpression();
        left = xor.getOperand().get(0);
        right = xor.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(or);
        assertTrackable(left);
        assertTrackable(right);
    }

    @Test
    public void testComparisonExpressions() {
        Map<String, Class> comparisons = new HashMap<String, Class>() {{
            put("<", Less.class);
            put("<=", LessOrEqual.class);
            put("=", Equal.class);
            put(">=", GreaterOrEqual.class);
            put(">", Greater.class);
            put("<>", NotEqual.class);
        }};

        for (Map.Entry<String, Class> e : comparisons.entrySet()) {
            ExpressionDef let = (ExpressionDef) visitData("let st = 1 " + e.getKey() + " 2");
            BinaryExpression binary = (BinaryExpression) let.getExpression();
            Expression left = binary.getOperand().get(0);
            Expression right = binary.getOperand().get(1);

            assertThat(binary, instanceOf(e.getValue()));
            assertThat(left, literalFor(1));
            assertThat(right, literalFor(2));

            assertTrackable(binary);
            assertTrackable(left);
            assertTrackable(right);
        }
    }

    @Test
    public void testIsTrueExpressions(){
        ExpressionDef let = (ExpressionDef) visitData("let st = X is true");
        Equal equal = (Equal) let.getExpression();
        Identifier left = (Identifier) equal.getOperand().get(0);
        Expression right = equal.getOperand().get(1);

        assertThat(left.getIdentifier(), is("X"));
        assertThat(right, literalFor(true));

        assertTrackable(equal);
        assertTrackable(left);
        //assertTrackable(right);
    }

    @Test
    public void testIsNotTrueExpressions(){
        ExpressionDef let = (ExpressionDef) visitData("let st = X is not true");
        Not not = (Not) let.getExpression();
        Equal equal = (Equal) not.getOperand();
        Identifier left = (Identifier) equal.getOperand().get(0);
        Expression right = equal.getOperand().get(1);

        assertThat(left.getIdentifier(), is("X"));
        assertThat(right, literalFor(true));

        assertTrackable(not);
        //assertTrackable(equal);
        assertTrackable(left);
        //assertTrackable(right);
    }

    @Test
    public void testIsNullExpressions(){
        ExpressionDef let = (ExpressionDef) visitData("let st = X is null");
        IsNull isNull = (IsNull) let.getExpression();
        Identifier id = (Identifier) isNull.getOperand();

        assertThat(id.getIdentifier(), is("X"));

        assertTrackable(isNull);
        assertTrackable(id);
    }

    @Test
    public void testIsNotNullExpressions(){
        ExpressionDef let = (ExpressionDef) visitData("let st = X is not null");
        // TODO: Should this really be an "Equal" expression and not an "Is" expression?
        Not not = (Not) let.getExpression();
        IsNull isNull = (IsNull) not.getOperand();
        Identifier id = (Identifier) isNull.getOperand();

        assertThat(id.getIdentifier(), is("X"));

        assertTrackable(not);
        //assertTrackable(isNull);
        assertTrackable(id);
    }

    /* TODO: Implement this after Bryn's resolution changes
    @Test
    public void testAccessorExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = X.effectiveTime");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof AccessorExpression, "Should be an AccessorExpression but was a "+let.getExpression());
            AccessorExpression ac = (AccessorExpression)let.getExpression();
            Expression ex = ac.getExpression();
            assertTrue(ex instanceof IdentifierExpression,"SHould be an IdentifierExpression but was "+ex);
            assertEquals(((IdentifierExpression) ex).getIdentifier(),"X", "Expression should be X but was "+ac.getExpression());
            assertEquals(ac.getIdentifier(),"effectiveTime", "Identifier should be effectiveTime but was "+ac.getIdentifier());
            assertFalse(ac.isValuesetAccessor(),"Should not be a valueset accessor but was");

            tree = parseData("let st = X.\"valueset identifier\"");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof AccessorExpression, "Should be an AccessorExpression but was a "+let.getExpression());
            ac = (AccessorExpression)let.getExpression();
            ex = ac.getExpression();
            assertTrue(ex instanceof IdentifierExpression,"SHould be an IdentifierExpression but was "+ex);
            assertEquals(((IdentifierExpression) ex).getIdentifier(),"X", "Expression should be X but was "+ac.getExpression());
            assertEquals(ac.getIdentifier(),"valueset identifier", "Identifier should be valueset identifier but was "+ac.getIdentifier());
            assertTrue(ac.isValuesetAccessor(),"Should  be a valueset accessor but was not");

            assertTrackable(let);
            assertTrackable(let.getExpression());
            assertTrackable(ac);
            assertTrackable(ac.getExpression());

        }catch(Exception e) {
            throw e;
        }
    }
    */

    @Test
    public void testArithmeticExpressions() {
        Map<String, Class> comparisons = new HashMap<String, Class>() {{
            put("+", Add.class);
            put("-", Subtract.class);
            put("*", Multiply.class);
            put("/", Divide.class);
            put("^", Power.class);
            put("mod", Modulo.class);
        }};

        for (Map.Entry<String, Class> e : comparisons.entrySet()) {
            ExpressionDef let = (ExpressionDef) visitData("let st = 1 " + e.getKey() + " 2");
            BinaryExpression binary = (BinaryExpression) let.getExpression();
            Expression left = binary.getOperand().get(0);
            Expression right = binary.getOperand().get(1);

            assertThat(binary, instanceOf(e.getValue()));
            assertThat(left, literalFor(1));
            assertThat(right, literalFor(2));

            assertTrackable(binary);
            assertTrackable(left);
            assertTrackable(right);
        }
    }

/* TODO: Implement these later...

    @Test
    public void testRetrieveExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = [Encounter, Performed]");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            SourceDataCriteria dc = ((RetrieveExpression)let.getExpression()).getDataCriteria();
            assertEquals("Encounter",dc.getTopic().getIdentifier());

            tree = parseData("let st = [PATIENT.Encounter, Performed]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            dc = ((RetrieveExpression)let.getExpression()).getDataCriteria();
            assertEquals("Encounter",dc.getTopic().getIdentifier());
            assertEquals("PATIENT",dc.getTopic().getQualifier());
            assertFalse(dc.getTopic().isValuesetIdentifier());

            tree = parseData("let st = [Encounter, Performed :\"Valueset Identifier\"]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            dc = ((RetrieveExpression)let.getExpression()).getDataCriteria();
            assertEquals("Valueset Identifier",dc.getValueset().getIdentifier());
            assertTrue(dc.getValueset().isValuesetIdentifier());

            tree = parseData("let st = [Encounter, Performed :Comp.\"Valueset Identifier\"]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            dc = ((RetrieveExpression)let.getExpression()).getDataCriteria();
            assertEquals("Valueset Identifier",dc.getValueset().getIdentifier());
            assertEquals("Comp",dc.getValueset().getQualifier());

            tree = parseData("let st = [Encounter, Performed : Field in Comp.\"Valueset Identifier\"]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            dc = ((RetrieveExpression)let.getExpression()).getDataCriteria();
            assertEquals("Valueset Identifier",dc.getValueset().getIdentifier());
            assertEquals("Comp",dc.getValueset().getQualifier());
            assertEquals("Field",dc.getValuesetPathIdentifier().getIdentifier());


            tree = parseData("let st = [Encounter, Performed : Field in Comp.\"Valueset Identifier\", duringPath during (null) ]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            RetrieveExpression ret = (RetrieveExpression)let.getExpression();
            dc = ret.getDataCriteria();
            assertEquals("Valueset Identifier",dc.getValueset().getIdentifier());
            assertEquals("Comp",dc.getValueset().getQualifier());
            assertEquals("Field",dc.getValuesetPathIdentifier().getIdentifier());
            assertEquals("duringPath",ret.getDuringPathIdentifier().getIdentifier());
            assertTrue(ret.getDuringExpression() instanceof  NullLiteral);

            tree = parseData("let st = [NonOccurrence of Encounter, Performed]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statement variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            dc = ((RetrieveExpression)let.getExpression()).getDataCriteria();
            assertEquals(dc.getExistence(), SourceDataCriteria.Existence.NonOccurrence);

            assertTrackable(let);
            assertTrackable(let.getExpression());
            assertTrackable(ret);
            assertTrackable(ret.getDuringExpression());
            assertTrackable(ret.getDuringPathIdentifier());
            assertTrackable(ret.getDataCriteria());


        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testQueryExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = [Encounter, Performed] R  with X.P A where A.s = R.y where R.effectiveTime = null return R");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof QueryExpression, "Should be a QueryExpression Expression but was " +let.getExpression());
            QueryExpression qe = (QueryExpression)let.getExpression();
            assertEquals("R",qe.getAliaseQuerySource().getAlias());
            assertTrue(qe.getAliaseQuerySource().getQuerySource() instanceof RetrieveExpression, "QS should be a Retrieve expression");
            assertEquals(1, qe.getQueryInclusionClauseExpressions().size());
            assertEquals("R" ,((IdentifierExpression)qe.getReturnClause()).getIdentifier());
            assertEquals("A",qe.getQueryInclusionClauseExpressions().get(0).getAliasedQuerySource().getAlias());
            assertTrue(qe.getQueryInclusionClauseExpressions().get(0).getAliasedQuerySource().getQuerySource() instanceof QualifiedIdentifier);

            assertTrackable(let);
            assertTrackable(let.getExpression());
            assertTrackable(qe);
            assertTrackable(qe.getAliaseQuerySource());
            assertTrackable(qe.getReturnClause());
            assertTrackable(qe.getSortClause());
            assertTrackable(qe.getWhereClauseExpression());
            for (QueryInclusionClauseExpression queryInclusionClauseExpression : qe.getQueryInclusionClauseExpressions()) {
                assertTrackable(queryInclusionClauseExpression);
            }

        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testMethodExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = AgeAt()");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof MethodExpression, "Should be a MethodExpression Expression but was " +let.getExpression());
            MethodExpression meth = (MethodExpression)let.getExpression();
            assertEquals("AgeAt", ((IdentifierExpression)meth.getMethod()).getIdentifier());
            assertTrue(meth.getParemeters().isEmpty());
            tree = parseData("let st = AgeAt(1,[Encounter, Perfromed])");

            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof MethodExpression, "Should be a MethodExpression Expression but was " +let.getExpression());
            meth = (MethodExpression)let.getExpression();
            assertEquals("AgeAt", ((IdentifierExpression)meth.getMethod()).getIdentifier());
            assertFalse(meth.getParemeters().isEmpty());
            assertTrue(meth.getParemeters().get(0) instanceof QuantityLiteral);
            assertTrue(meth.getParemeters().get(1) instanceof RetrieveExpression);

            tree = parseData("let st = X.AgeAt(1,[Encounter, Perfromed])");

            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof MethodExpression, "Should be a MethodExpression Expression but was " +let.getExpression());
            meth = (MethodExpression)let.getExpression();
            assertTrue(meth.getMethod() instanceof AccessorExpression, "Should be a AccessorExpression Expression but was " +meth.getMethod());;
            assertFalse(meth.getParemeters().isEmpty());
            assertTrue(meth.getParemeters().get(0) instanceof QuantityLiteral);
            assertTrue(meth.getParemeters().get(1) instanceof RetrieveExpression);
            assertEquals("X", ((IdentifierExpression) ((AccessorExpression) meth.getMethod()).getExpression()).getIdentifier());
            assertEquals("AgeAt", ((AccessorExpression)meth.getMethod()).getIdentifier());
            assertTrackable(let);
            assertTrackable(let.getExpression());
            assertTrackable(meth);
            assertTrackable(meth.getMethod());
            for (Expression expression : meth.getParemeters()) {
                assertTrackable(expression);
            }

        }catch(Exception e) {
            throw e;
        }
    }

    @Test(enabled = false, skipFailedInvocations = true)
    public void testExistanceExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = exists (null)");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ExistenceExpression, "Should be an existence expression");
            ExistenceExpression ex = (ExistenceExpression)let.getExpression();
            assertFalse(ex.isNegated(),"Existence expression should not be negated when using 'exists'");
            assertTrue(ex.getExpression() instanceof NullLiteral, "Expression should be a null literal but was a "+ex.getExpression());

            tree = parseData("let st = not (null)");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ExistenceExpression, "Should be an existence expression");
            ex = (ExistenceExpression)let.getExpression();
            assertTrue(ex.isNegated(),"Existence expression should  be negated when using 'not'");
            assertTrue(ex.getExpression() instanceof NullLiteral, "Expression should be a null literal");

        }catch(Exception e) {
            throw e;
        }
    }
    */


    private void assertTrackable(Trackable t){
        if(t == null){
            return;
        }
        assertThat(t.getTrackbacks(), not(empty()));
        assertThat(t.getTrackbacks().get(0), notNullValue());
        assertThat(t.getTrackerId(), notNullValue());
    }
}
