package org.cqframework.cql.grammar;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.testng.annotations.Test;

import static org.cqframework.cql.gen.cqlParser.*;
import static org.testng.Assert.assertEquals;

/**
 * GrammarTest ensures that the grammar (and generated parsers) work as expected.  If non-compatible changes are made
 * to the grammar, these tests should fail.  If the change is intentional, modify the tests to pass-- otherwise, fix
 * the grammar.
 */
public class GrammarTest {

    @Test
    public void ageAt() {
        ParseTree tree = parseToTree("define inIPP : AgeAt(start of MeasurementPeriod) < 18");
        LogicContext logic = (LogicContext) tree.getPayload();

        ExpressionDefinitionContext def = logic.statement(0).expressionDefinition();
        assertEquals("inIPP", def.identifier().IDENTIFIER().toString());

        InequalityExpressionContext cmpExpr = (InequalityExpressionContext) def.expression();
        assertEquals("<", cmpExpr.getChild(1).getText());

        TermExpressionContext methodExpr = (TermExpressionContext) cmpExpr.expression(0);
        InvocationExpressionTermContext methodExpressionTerm = (InvocationExpressionTermContext)methodExpr.expressionTerm();
        assertEquals("AgeAt", methodExpressionTerm.identifier().getText());

        TermExpressionContext argExpression = (TermExpressionContext) methodExpressionTerm.expression(0);
        TimeBoundaryExpressionTermContext argExpressionTerm = (TimeBoundaryExpressionTermContext) argExpression.expressionTerm();
        assertEquals("start", argExpressionTerm.getChild(0).getText());
        assertEquals("MeasurementPeriod", argExpressionTerm.expressionTerm().getText());

        ExpressionContext termExpr = cmpExpr.expression(1);
        assertEquals("18", termExpr.getText());
    }

    private ParseTree parseToTree(String logic) {
        ANTLRInputStream input = new ANTLRInputStream(logic);
        CommonTokenStream tokens = new CommonTokenStream(new cqlLexer(input));
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.logic();
    }
}
