package org.cqframework.cql.tools.formatter;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.cqframework.cql.gen.cqlBaseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 7/25/2017.
 */
public class CommentListener extends cqlBaseListener {

    CommonTokenStream tokens;
    List<Token> commentsAtTop;

    public CommentListener(CommonTokenStream tokens) {
        this.tokens = tokens;
        this.commentsAtTop = new ArrayList<>();
    }

    public void rewriteTokens() {
        for (int i = 0; i < tokens.getTokens().size(); ++i) {
            Token token = tokens.getTokens().get(i);
            if (token.getChannel() == 1 && token.getText().startsWith("//") || token.getText().startsWith("/*")) {
                ((CommonToken) token).setChannel(0);
                if (i == commentsAtTop.size()) {
                    commentsAtTop.add(token);
                }

                // preserve whitespace
                if (i > 0) {
                    if (tokens.getTokens().get(i - 1).getText().trim().isEmpty()) {
                        ((CommonToken) tokens.getTokens().get(i - 1)).setChannel(0);
                    }
                }
                if (i < tokens.getTokens().size() - 2) {
                    if (tokens.getTokens().get(i + 1).getText().trim().isEmpty()) {
                        ((CommonToken) tokens.getTokens().get(i + 1)).setChannel(0);
                    }
                }
            }
        }
    }

    // Easier to format some things post hoc
    public String refineOutput(String output) {
        // Case where comments are at top of library - before any other constructs
        for (Token token : commentsAtTop) {
            output = token.getText() + "\r\n" + output;
        }

        // Comments preserve whitespace, which can lead to extra newlines before statements
        return output.replaceAll("\\r\\n\\r\\n\\r\\n", "\r\n");
    }

//    This is a more complex method, which includes comments by accessing the hidden channel
//    private List<Token> getLeftHiddenChannelTokens(int start) {
//        return tokens.getHiddenTokensToLeft(start, 1);
//    }
//
//    private List<Token> getRightHiddenChannelTokens(int end) {
//        return tokens.getHiddenTokensToRight(end, 1);
//    }
//
//    private void includeLeftComments(ParserRuleContext ctx) {
//        List<Token> hiddenTokens = getLeftHiddenChannelTokens(ctx.getStart().getTokenIndex());
//
//        if (hiddenTokens == null || hiddenTokens.size() == 1) return;
//
//        List<ParseTree> theKids = new ArrayList<>(ctx.children);
//
//        // remove the cql statement
//        int numKids = ctx.children.size();
//        for (int i = 0; i < numKids; ++i) {
//            ctx.removeLastChild();
//        }
//
//        // add the comments
//        for (Token token : hiddenTokens) {
//            if (token.getText().trim().isEmpty()) continue;
//            ctx.addChild(new CommonToken(0, token.getText()));
//        }
//
//        // put cql statement back
//        for (ParseTree kid : theKids) {
//            if (kid instanceof Token) {
//                ctx.addChild((Token) kid);
//            }
//
//            else if (kid instanceof RuleContext) {
//                ctx.addChild((RuleContext) kid);
//            }
//
//            else if (kid instanceof TerminalNode) {
//                ctx.addChild((TerminalNode) kid);
//            }
//        }
//    }
//
//    public void includeRightComments(ParserRuleContext ctx) {
//        List<Token> hiddenTokens = getRightHiddenChannelTokens(ctx.getStop().getTokenIndex());
//
//        if (hiddenTokens == null) return;
//
//        // Checking for EOF
//        if (tokens.get(hiddenTokens.get(hiddenTokens.size()-1).getTokenIndex() + 1).getType() == -1) {
//            for (Token token : hiddenTokens) {
//                // also adding the whitespace
//                ctx.addChild(token);
//            }
//        }
//    }
//
//    @Override public void exitLibrary(cqlParser.LibraryContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitLibraryDefinition(cqlParser.LibraryDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitUsingDefinition(cqlParser.UsingDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIncludeDefinition(cqlParser.IncludeDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitLocalIdentifier(cqlParser.LocalIdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitAccessModifier(cqlParser.AccessModifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitParameterDefinition(cqlParser.ParameterDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodesystemDefinition(cqlParser.CodesystemDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitValuesetDefinition(cqlParser.ValuesetDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodesystems(cqlParser.CodesystemsContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodesystemIdentifier(cqlParser.CodesystemIdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitLibraryIdentifier(cqlParser.LibraryIdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodeDefinition(cqlParser.CodeDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitConceptDefinition(cqlParser.ConceptDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodeIdentifier(cqlParser.CodeIdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodesystemId(cqlParser.CodesystemIdContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitValuesetId(cqlParser.ValuesetIdContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitVersionSpecifier(cqlParser.VersionSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodeId(cqlParser.CodeIdContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTypeSpecifier(cqlParser.TypeSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitNamedTypeSpecifier(cqlParser.NamedTypeSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitModelIdentifier(cqlParser.ModelIdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitListTypeSpecifier(cqlParser.ListTypeSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIntervalTypeSpecifier(cqlParser.IntervalTypeSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTupleTypeSpecifier(cqlParser.TupleTypeSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTupleElementDefinition(cqlParser.TupleElementDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitChoiceTypeSpecifier(cqlParser.ChoiceTypeSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitStatement(cqlParser.StatementContext ctx) {
//        includeLeftComments(ctx);
//        // Need to account for comments at the end of a file -> look to the right
//        // This is tricky because it is easy to duplicate comments looking both ways
//        includeRightComments(ctx);
//    }
//    @Override public void exitExpressionDefinition(cqlParser.ExpressionDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitContextDefinition(cqlParser.ContextDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitOperandDefinition(cqlParser.OperandDefinitionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitFunctionBody(cqlParser.FunctionBodyContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQuerySource(cqlParser.QuerySourceContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitAliasedQuerySource(cqlParser.AliasedQuerySourceContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitAlias(cqlParser.AliasContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQueryInclusionClause(cqlParser.QueryInclusionClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitWithClause(cqlParser.WithClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitWithoutClause(cqlParser.WithoutClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitRetrieve(cqlParser.RetrieveContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodePath(cqlParser.CodePathContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTerminology(cqlParser.TerminologyContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQualifier(cqlParser.QualifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQuery(cqlParser.QueryContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitSourceClause(cqlParser.SourceClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitSingleSourceClause(cqlParser.SingleSourceClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitMultipleSourceClause(cqlParser.MultipleSourceClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitLetClause(cqlParser.LetClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitLetClauseItem(cqlParser.LetClauseItemContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitWhereClause(cqlParser.WhereClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitReturnClause(cqlParser.ReturnClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitSortClause(cqlParser.SortClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitSortDirection(cqlParser.SortDirectionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitSortByItem(cqlParser.SortByItemContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQualifiedIdentifier(cqlParser.QualifiedIdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDurationBetweenExpression(cqlParser.DurationBetweenExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInFixSetExpression(cqlParser.InFixSetExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitRetrieveExpression(cqlParser.RetrieveExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTimingExpression(cqlParser.TimingExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitNotExpression(cqlParser.NotExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQueryExpression(cqlParser.QueryExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitBooleanExpression(cqlParser.BooleanExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitOrExpression(cqlParser.OrExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCastExpression(cqlParser.CastExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitAndExpression(cqlParser.AndExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitBetweenExpression(cqlParser.BetweenExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitMembershipExpression(cqlParser.MembershipExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDifferenceBetweenExpression(cqlParser.DifferenceBetweenExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInequalityExpression(cqlParser.InequalityExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitEqualityExpression(cqlParser.EqualityExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitExistenceExpression(cqlParser.ExistenceExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitImpliesExpression(cqlParser.ImpliesExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTermExpression(cqlParser.TermExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTypeExpression(cqlParser.TypeExpressionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDateTimePrecision(cqlParser.DateTimePrecisionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDateTimeComponent(cqlParser.DateTimeComponentContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitPluralDateTimePrecision(cqlParser.PluralDateTimePrecisionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitAdditionExpressionTerm(cqlParser.AdditionExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIndexedExpressionTerm(cqlParser.IndexedExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitWidthExpressionTerm(cqlParser.WidthExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTimeUnitExpressionTerm(cqlParser.TimeUnitExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIfThenElseExpressionTerm(cqlParser.IfThenElseExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTimeBoundaryExpressionTerm(cqlParser.TimeBoundaryExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitElementExtractorExpressionTerm(cqlParser.ElementExtractorExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitConversionExpressionTerm(cqlParser.ConversionExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTypeExtentExpressionTerm(cqlParser.TypeExtentExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitPredecessorExpressionTerm(cqlParser.PredecessorExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitPointExtractorExpressionTerm(cqlParser.PointExtractorExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitMultiplicationExpressionTerm(cqlParser.MultiplicationExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitAggregateExpressionTerm(cqlParser.AggregateExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDurationExpressionTerm(cqlParser.DurationExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCaseExpressionTerm(cqlParser.CaseExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitPowerExpressionTerm(cqlParser.PowerExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitSuccessorExpressionTerm(cqlParser.SuccessorExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitPolarityExpressionTerm(cqlParser.PolarityExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTermExpressionTerm(cqlParser.TermExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInvocationExpressionTerm(cqlParser.InvocationExpressionTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCaseExpressionItem(cqlParser.CaseExpressionItemContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDateTimePrecisionSpecifier(cqlParser.DateTimePrecisionSpecifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitRelativeQualifier(cqlParser.RelativeQualifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitOffsetRelativeQualifier(cqlParser.OffsetRelativeQualifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitExclusiveRelativeQualifier(cqlParser.ExclusiveRelativeQualifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQuantityOffset(cqlParser.QuantityOffsetContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTemporalRelationship(cqlParser.TemporalRelationshipContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitConcurrentWithIntervalOperatorPhrase(cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIncludesIntervalOperatorPhrase(cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIncludedInIntervalOperatorPhrase(cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitBeforeOrAfterIntervalOperatorPhrase(cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitWithinIntervalOperatorPhrase(cqlParser.WithinIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitMeetsIntervalOperatorPhrase(cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitOverlapsIntervalOperatorPhrase(cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitStartsIntervalOperatorPhrase(cqlParser.StartsIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitEndsIntervalOperatorPhrase(cqlParser.EndsIntervalOperatorPhraseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInvocationTerm(cqlParser.InvocationTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitLiteralTerm(cqlParser.LiteralTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitExternalConstantTerm(cqlParser.ExternalConstantTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIntervalSelectorTerm(cqlParser.IntervalSelectorTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTupleSelectorTerm(cqlParser.TupleSelectorTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInstanceSelectorTerm(cqlParser.InstanceSelectorTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitListSelectorTerm(cqlParser.ListSelectorTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodeSelectorTerm(cqlParser.CodeSelectorTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitConceptSelectorTerm(cqlParser.ConceptSelectorTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitParenthesizedTerm(cqlParser.ParenthesizedTermContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitBooleanLiteral(cqlParser.BooleanLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitNullLiteral(cqlParser.NullLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitStringLiteral(cqlParser.StringLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitNumberLiteral(cqlParser.NumberLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDateTimeLiteral(cqlParser.DateTimeLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTimeLiteral(cqlParser.TimeLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQuantityLiteral(cqlParser.QuantityLiteralContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIntervalSelector(cqlParser.IntervalSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTupleSelector(cqlParser.TupleSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitTupleElementSelector(cqlParser.TupleElementSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInstanceSelector(cqlParser.InstanceSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitInstanceElementSelector(cqlParser.InstanceElementSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitListSelector(cqlParser.ListSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitDisplayClause(cqlParser.DisplayClauseContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitCodeSelector(cqlParser.CodeSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitConceptSelector(cqlParser.ConceptSelectorContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitIdentifier(cqlParser.IdentifierContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitExternalConstant(cqlParser.ExternalConstantContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitMemberInvocation(cqlParser.MemberInvocationContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitFunctionInvocation(cqlParser.FunctionInvocationContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitThisInvocation(cqlParser.ThisInvocationContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitFunction(cqlParser.FunctionContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitParamList(cqlParser.ParamListContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitQuantity(cqlParser.QuantityContext ctx) {
//        includeLeftComments(ctx);
//    }
//    @Override public void exitUnit(cqlParser.UnitContext ctx) {
//        includeLeftComments(ctx);
//    }
}
