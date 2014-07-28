package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.translator.expressions.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TranslatorTest {
    @Test
    public void testRhinoWorks() {
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();
            String command = "1+1";
            Object result = cx.evaluateString(scope, command, "<cmd>", 1, null);
            assertEquals(Context.toString(result), "2");
        } finally {
            Context.exit();
        }
    }

    @Test
    public void testBooleanLiteral(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let b = true");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"b");
            assertTrue(let.getExpression() instanceof BooleanLiteral, "Should be boolean literal");
            assertTrue(((BooleanLiteral) let.getExpression()).getValue(), "Value should be true");

            tree = parseData("let b = false");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"b");
            assertTrue(let.getExpression() instanceof BooleanLiteral, "Should be boolean literal");
            assertTrue(((BooleanLiteral) let.getExpression()).getValue() == false, "Value should be false");

        }catch(Exception e) {
            throw e;
        }
    }


    @Test
    public void testStringLiteral(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = 'hey its a string'");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof StringLiteral, "Should be a string literal");
            assertEquals(((StringLiteral) let.getExpression()).getValue(), "hey its a string", "Value for string literal should be match (hey its a string)");

        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testNullLiteral(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = null");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof NullLiteral, "Should be a null literal");
        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testQuantityLiteral(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = 1");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof QuantityLiteral, "Should be a quantity literal");
            QuantityLiteral lit = (QuantityLiteral)let.getExpression();
            assertEquals(null, lit.getUnit(), "Units should be null");
            assertEquals(lit.getQuantity(),1.0);

            tree = parseData("let st = 1.1");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof QuantityLiteral, "Should be a quantity literal");
            lit = (QuantityLiteral)let.getExpression();
            assertEquals(null, lit.getUnit(), "Units should be null");
            assertEquals(lit.getQuantity(),1.1);

            tree = parseData("let st = 1.1 u'mm'");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof QuantityLiteral, "Should be a quantity literal");
            lit = (QuantityLiteral)let.getExpression();
            assertEquals(lit.getUnit(), "mm", "Units should match expected mm");
            assertEquals(lit.getQuantity(),1.1);

            tree = parseData("let st = 1.1 weeks");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof QuantityLiteral, "Should be a quantity literal");
            lit = (QuantityLiteral)let.getExpression();
            assertEquals(lit.getUnit(),"weeks", "Units should match expected weeks");
            assertEquals(lit.getQuantity(),1.1);

        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testAndExpressions(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = true and false");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof AndExpression, "Should be an AndExpression but was a "+let.getExpression());
            AndExpression and = (AndExpression)let.getExpression();
            assertTrue(and.getLeft() instanceof BooleanLiteral, "Left hand should be Boolean literal but was a "+and.getLeft());
            assertTrue(and.getRight() instanceof BooleanLiteral, "Right hand should be Boolean literal but was a "+and.getLeft());
            assertTrue(((BooleanLiteral) and.getLeft()).getValue(),"Boolean value of left hand side should be true but was "+((BooleanLiteral) and.getLeft()).getValue());
            assertFalse(((BooleanLiteral) and.getRight()).getValue(), "Boolean value of right hand side should be false but was " + ((BooleanLiteral) and.getRight()).getValue());

        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testOrExpressions(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = true or false");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof OrExpression, "Should be an OrExpression but was a "+let.getExpression());
            OrExpression or = (OrExpression)let.getExpression();
            assertTrue(or.getLeft() instanceof BooleanLiteral, "Left hand should be Boolean literal but was a "+or.getLeft());
            assertTrue(or.getRight() instanceof BooleanLiteral, "Right hand should be Boolean literal but was a "+or.getLeft());
            assertTrue(((BooleanLiteral) or.getLeft()).getValue(),"Boolean value of left hand side should be true but was "+((BooleanLiteral) or.getLeft()).getValue());
            assertFalse(((BooleanLiteral) or.getRight()).getValue(),"Boolean value of right hand side should be false but was "+((BooleanLiteral) or.getRight()).getValue());
            assertFalse(or.isXor(),"Or expressions should not be an oxr but was");

            tree = parseData("let st = true xor false");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof OrExpression, "Should be an OrExpression but was a "+let.getExpression());
            or = (OrExpression)let.getExpression();
            assertTrue(or.isXor(), "Or expressions should be an oxr but was not");
        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testComparisonExpressions(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            for (ComparisionExpression.Comparator operator : ComparisionExpression.Comparator.values()) {
                ParseTree tree = parseData("let st = 1 "+operator.symbol()+" 1");
                LetStatement let = (LetStatement)visitor.visit(tree);
                assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
                assertTrue(let.getExpression() instanceof ComparisionExpression, "Expected expression to be of type ComparisionExpression but was of type "+let.getExpression().getClass());
            }

        }catch(Exception e) {
            throw e;
        }
    }

    @Test
    public void testBooleanExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = X is null");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ComparisionExpression, "Expected expression to be of type ComparisionExpression but was of type "+let.getExpression().getClass());
            ComparisionExpression comp = (ComparisionExpression)let.getExpression();
            assertTrue(comp.getLeft() instanceof IdentifierExpression,"Left should be an IdentifierExpression but was "+comp.getLeft());
            assertTrue(comp.getRight() instanceof NullLiteral,"Right should be  NullLiteral but was "+comp.getRight());
            assertTrue(comp.getComp().equals(ComparisionExpression.Comparator.EQ), "Comparator should be = but was "+ comp.getComp());

            tree = parseData("let st = X is not null");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(), "st", "let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ComparisionExpression, "Expected expression to be of type ComparisionExpression but was of type "+let.getExpression().getClass());
            comp = (ComparisionExpression)let.getExpression();
            assertTrue(comp.getLeft() instanceof IdentifierExpression,"Left should be an IdentifierExpression but was "+comp.getLeft());
            assertTrue(comp.getRight() instanceof NullLiteral,"Right should be  NullLiteral but was "+comp.getRight());
            assertTrue(comp.getComp().equals(ComparisionExpression.Comparator.NOT_EQ), "Comparator should be <> but was "+ comp.getComp());

            tree = parseData("let st = X is not true");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ComparisionExpression, "Expected expression to be of type ComparisionExpression but was of type "+let.getExpression().getClass());
            comp = (ComparisionExpression)let.getExpression();
            assertTrue(comp.getLeft() instanceof IdentifierExpression,"Left should be an IdentifierExpression but was "+comp.getLeft());
            assertTrue(comp.getRight() instanceof BooleanLiteral,"Right should be  BooleanLiteral but was "+comp.getRight());
            assertTrue(((BooleanLiteral) comp.getRight()).getValue(),"Value for boolean literal should be true");
            assertTrue(comp.getComp().equals(ComparisionExpression.Comparator.NOT_EQ), "Comparator should be <> but was "+ comp.getComp());

            tree = parseData("let st = X is not false");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ComparisionExpression, "Expected expression to be of type ComparisionExpression but was of type "+let.getExpression().getClass());
            comp = (ComparisionExpression)let.getExpression();
            assertTrue(comp.getLeft() instanceof IdentifierExpression,"Left should be an IdentifierExpression but was "+comp.getLeft());
            assertTrue(comp.getRight() instanceof BooleanLiteral,"Right should be  BooleanLiteral but was "+comp.getRight());
            assertFalse(((BooleanLiteral) comp.getRight()).getValue(), "Value for boolean literal should be false");
            assertTrue(comp.getComp().equals(ComparisionExpression.Comparator.NOT_EQ), "Comparator should be <> but was " + comp.getComp());

            tree = parseData("let st = X is  true");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof ComparisionExpression, "Expected expression to be of type ComparisionExpression but was of type "+let.getExpression().getClass());
            comp = (ComparisionExpression)let.getExpression();
            assertTrue(comp.getLeft() instanceof IdentifierExpression,"Left should be an IdentifierExpression but was "+comp.getLeft());
            assertTrue(comp.getRight() instanceof BooleanLiteral,"Right should be  BooleanLiteral but was "+comp.getRight());
            assertTrue(((BooleanLiteral) comp.getRight()).getValue(),"Value for boolean literal should be true");
            assertTrue(comp.getComp().equals(ComparisionExpression.Comparator.EQ), "Comparator should be = but was "+ comp.getComp());

        }catch(Exception e) {
            throw e;
        }
    }


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

        }catch(Exception e) {
            throw e;
        }
    }


    @Test
    public void testArithmaticExpressions(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            for (ArithmaticExpression.Operator operator : ArithmaticExpression.Operator.values()) {
                ParseTree tree = parseData("let st = 1 "+operator.symbol()+" 1");
                LetStatement let = (LetStatement)visitor.visit(tree);
                assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
                assertTrue(let.getExpression() instanceof ArithmaticExpression, "Expected expression to be of type ArithmaticExpression but was of type "+let.getExpression().getClass());
            }

        }catch(Exception e) {
            throw e;
        }
    }


    @Test
    public void testRetrieveExpression(){
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        try{
            ParseTree tree = parseData("let st = [Encounter, Performed]");
            LetStatement let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            RetrieveExpression ret = (RetrieveExpression)let.getExpression();
            assertEquals("Encounter",ret.getTopic().getIdentifier());

            tree = parseData("let st = [PATIENT.Encounter, Performed]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            ret = (RetrieveExpression)let.getExpression();
            assertEquals("Encounter",ret.getTopic().getIdentifier());
            assertEquals("PATIENT",ret.getTopic().getQualifier());
            assertFalse(ret.getTopic().isValuesetIdentifier());

            tree = parseData("let st = [Encounter, Performed :\"Valueset Identifier\"]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            ret = (RetrieveExpression)let.getExpression();
            assertEquals("Valueset Identifier",ret.getValueset().getIdentifier());
            assertTrue(ret.getValueset().isValuesetIdentifier());

            tree = parseData("let st = [Encounter, Performed :Comp.\"Valueset Identifier\"]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            ret = (RetrieveExpression)let.getExpression();
            assertEquals("Valueset Identifier",ret.getValueset().getIdentifier());
            assertEquals("Comp",ret.getValueset().getQualifier());

            tree = parseData("let st = [Encounter, Performed : Field in Comp.\"Valueset Identifier\"]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            ret = (RetrieveExpression)let.getExpression();
            assertEquals("Valueset Identifier",ret.getValueset().getIdentifier());
            assertEquals("Comp",ret.getValueset().getQualifier());
            assertEquals("Field",ret.getValuesetPathIdentifier().getIdentifier());


            tree = parseData("let st = [Encounter, Performed : Field in Comp.\"Valueset Identifier\", duringPath during (null) ]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            ret = (RetrieveExpression)let.getExpression();
            assertEquals("Valueset Identifier",ret.getValueset().getIdentifier());
            assertEquals("Comp",ret.getValueset().getQualifier());
            assertEquals("Field",ret.getValuesetPathIdentifier().getIdentifier());
            assertEquals("duringPath",ret.getDuringPathIdentifier().getIdentifier());
            assertTrue(ret.getDuringExpression() instanceof  NullLiteral);

            tree = parseData("let st = no [Encounter, Performed]");
            let = (LetStatement)visitor.visit(tree);
            assertEquals(let.getIdentifier(),"st","let statment variable name should be st");
            assertTrue(let.getExpression() instanceof RetrieveExpression, "Should be a Retrieve Expression literal");
            ret = (RetrieveExpression)let.getExpression();
            assertEquals(ret.getExistenceModifier(), RetrieveExpression.ExModifier.no);



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
            assertEquals(1,qe.getQueryInclusionClauseExpressions().size());
            assertEquals("R" ,((IdentifierExpression)qe.getReturnClause()).getIdentifier());
            assertEquals("A",qe.getQueryInclusionClauseExpressions().get(0).getAliasedQuerySource().getAlias());
            assertTrue(qe.getQueryInclusionClauseExpressions().get(0).getAliasedQuerySource().getQuerySource() instanceof QualifiedIdentifier);

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
            assertEquals("X", ((IdentifierExpression)((AccessorExpression)meth.getMethod()).getExpression()).getIdentifier());
            assertEquals("AgeAt", ((AccessorExpression)meth.getMethod()).getIdentifier());
        }catch(Exception e) {
            throw e;
        }
    }
    @Test(skipFailedInvocations = true)
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
            assertFalse(ex.isNegated(),"Existence expression should  be negated when using 'not'");
            assertTrue(ex.getExpression() instanceof NullLiteral, "Expression should be a null literal");


        }catch(Exception e) {
            throw e;
        }
    }



    private Object parseFile(String fileName)throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        StringBuffer buff = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
            buff.append(line);
        }
        return parseData(buff.toString());
    }

    private ParseTree parseData(String cql_data){
        ANTLRInputStream input = new ANTLRInputStream(cql_data);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.logic();
        return tree;
    }
}
