package org.cqframework.cql.grammar;

import static org.cqframework.cql.gen.cqlParser.*;
import static org.testng.Assert.assertEquals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.testng.annotations.Test;

/**
 * GrammarTest ensures that the grammar (and generated parsers) work as expected.  If non-compatible changes are made
 * to the grammar, these tests should fail.  If the change is intentional, modify the tests to pass-- otherwise, fix
 * the grammar.
 */
public class GrammarTest {

    @Test
    public void ageAt() {
        ParseTree tree = parseToTree("define inIPP : AgeAt(start of MeasurementPeriod) < 18");
        LibraryContext logic = (LibraryContext) tree.getPayload();

        ExpressionDefinitionContext def = logic.statement(0).expressionDefinition();
        assertEquals("inIPP", def.identifier().IDENTIFIER().toString());

        InequalityExpressionContext cmpExpr = (InequalityExpressionContext) def.expression();
        assertEquals("<", cmpExpr.getChild(1).getText());

        TermExpressionContext termExpression = (TermExpressionContext) cmpExpr.expression(0);
        TermExpressionTermContext termExpressionTerm = (TermExpressionTermContext) termExpression.expressionTerm();
        InvocationTermContext invocationTerm = (InvocationTermContext) termExpressionTerm.term();
        FunctionInvocationContext functionInvocation = (FunctionInvocationContext) invocationTerm.invocation();
        assertEquals(
                "AgeAt", functionInvocation.function().referentialIdentifier().getText());

        TermExpressionContext argExpression = (TermExpressionContext)
                functionInvocation.function().paramList().expression(0);
        TimeBoundaryExpressionTermContext argExpressionTerm =
                (TimeBoundaryExpressionTermContext) argExpression.expressionTerm();
        assertEquals("start", argExpressionTerm.getChild(0).getText());
        assertEquals("MeasurementPeriod", argExpressionTerm.expressionTerm().getText());

        ExpressionContext termExpr = cmpExpr.expression(1);
        assertEquals("18", termExpr.getText());
    }

    private ParseTree parseToTree(String logic) {
        CharStream input = CharStreams.fromString(logic);
        CommonTokenStream tokens = new CommonTokenStream(new cqlLexer(input));
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.library();
    }
}
