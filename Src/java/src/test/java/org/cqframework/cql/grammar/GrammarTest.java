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
        ParseTree tree = parseToTree("let inIPP = AgeAt(start of MeasurementPeriod) < 18");
        LogicContext logic = (LogicContext) tree.getPayload();

        LetStatementContext let = logic.statement(0).letStatement();
        assertEquals("inIPP", let.IDENTIFIER().toString());

        ExpressionContext cmpExpr = let.expression();
        assertEquals("<", cmpExpr.getChild(1).getText());

        ExpressionContext fncExpr = cmpExpr.expression(0);
        assertEquals("AgeAt", fncExpr.expressionTerm(0).expressionTerm(0).getText());
        ExpressionTermContext argExpr = fncExpr.expressionTerm(0).expression(0).expressionTerm(0);
        assertEquals("start", argExpr.getChild(0).getText());
        assertEquals("MeasurementPeriod", argExpr.expressionTerm(0).getText());

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
