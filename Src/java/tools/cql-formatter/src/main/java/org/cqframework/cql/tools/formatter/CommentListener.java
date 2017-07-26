package org.cqframework.cql.tools.formatter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseListener;
import org.cqframework.cql.gen.cqlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 7/25/2017.
 */
public class CommentListener extends cqlBaseListener {

    CommonTokenStream tokens;

    public CommentListener(CommonTokenStream tokens) {
        this.tokens = tokens;
    }

    private List<Token> getLeftHiddenChannelTokens(int start) {
        return tokens.getHiddenTokensToLeft(start, 1);
    }

    private List<Token> getRightHiddenChannelTokens(int end) {
        return tokens.getHiddenTokensToRight(end, 1);
    }

    private void includeLeftComments(ParserRuleContext ctx) {
        List<Token> hiddenTokens = getLeftHiddenChannelTokens(ctx.getStart().getTokenIndex());

        if (hiddenTokens == null || hiddenTokens.size() == 1) return;

        List<ParseTree> theKids = new ArrayList<>(ctx.children);

        // remove the cql statement
        int numKids = ctx.children.size();
        for (int i = 0; i < numKids; ++i) {
            ctx.removeLastChild();
        }

        // add the comments
        for (Token token : hiddenTokens) {
            if (token.getText().equals(" ")) continue;
            ctx.addChild(new CommonToken(0, token.getText()));
        }

        // put cql statement back
        for (ParseTree kid : theKids) {
            if (kid instanceof Token) {
                ctx.addChild((Token) kid);
            }

            else if (kid instanceof RuleContext) {
                ctx.addChild((RuleContext) kid);
            }

            else if (kid instanceof TerminalNode) {
                ctx.addChild((TerminalNode) kid);
            }
        }
    }

    public void includeRightComments(ParserRuleContext ctx) {
        List<Token> hiddenTokens = getRightHiddenChannelTokens(ctx.getStop().getTokenIndex());

        if (hiddenTokens == null) return;

        // Checking for EOF
        if (tokens.get(hiddenTokens.get(hiddenTokens.size()-1).getTokenIndex() + 1).getType() == -1) {
            for (Token token : hiddenTokens) {
                // also adding the whitespace
                ctx.addChild(token);
            }
        }
    }

//    private String formatComment(String comment) {
//        if (comment.startsWith("//")) {
//            return "// " + comment.replaceFirst("//\\s+", "");
//        }
//        else {
//            String content = comment.replaceFirst("/\\*", "").replace("*/", "").trim();
//            return "/*\n\t" + content + "\n*/";
//        }
//    }

    @Override
    public void exitStatement(cqlParser.StatementContext ctx) {
        includeLeftComments(ctx);
        // Need to account for comments at the end of a file -> look to the right
        // This is tricky because it is easy to duplicate comments looking both ways
        includeRightComments(ctx);
    }

    @Override
    public void exitTermExpression(cqlParser.TermExpressionContext ctx) {
        includeLeftComments(ctx);
    }

//    So, this is usually going to go to exitQuery - will duplicate comments...
//    TODO: find a way to include both and not dup comments
//    @Override
//    public void exitRetrieve(cqlParser.RetrieveContext ctx) {
//        includeLeftComments(ctx);
//        
//    }

//    @Override
//    public void exitQuery(cqlParser.QueryContext ctx) {
//        includeLeftComments(ctx);
//        
//    }

    @Override
    public void exitSourceClause(cqlParser.SourceClauseContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitBooleanExpression(cqlParser.BooleanExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitTypeExpression(cqlParser.TypeExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitCastExpression(cqlParser.CastExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitNotExpression(cqlParser.NotExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitExistenceExpression(cqlParser.ExistenceExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitBetweenExpression(cqlParser.BetweenExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitDurationBetweenExpression(cqlParser.DurationBetweenExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitDifferenceBetweenExpression(cqlParser.DifferenceBetweenExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitInequalityExpression(cqlParser.InequalityExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitTimingExpression(cqlParser.TimingExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitEqualityExpression(cqlParser.EqualityExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitMembershipExpression(cqlParser.MembershipExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitAndExpression(cqlParser.AndExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitOrExpression(cqlParser.OrExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitImpliesExpression(cqlParser.ImpliesExpressionContext ctx) {
        includeLeftComments(ctx);
    }

    @Override
    public void exitInFixSetExpression(cqlParser.InFixSetExpressionContext ctx) {
        includeLeftComments(ctx);
    }
}
