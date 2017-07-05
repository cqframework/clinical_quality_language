package org.cqframework.cql.tools.formatter;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlParser;

/**
 * Created by Bryn on 7/5/2017.
 */
public class CqlFormatterVisitor extends cqlBaseVisitor {
    private StringBuilder output;

    private final char space = '\u0020';
    private final char tab = '\t';
    private final String newLine = "\r\n";

    private boolean onNewLine;
    private boolean needsWhitespace;
    private int indentLevel = 0;

    private String currentSection;
    private int sectionCount = 0;
    private void newSection(String section) {
        if (hasSectionContent()) {
            resetIndentLevel();
            newLine();
        }
        sectionCount = 0;
        currentSection = section;
    }

    private boolean needsSectionSeparator(String section) {
        switch (section) {
            case "statement": return true;
            default: return false;
        }
    }

    private void ensureSectionSeparator() {
        if (needsSectionSeparator(currentSection) && hasSectionContent()) {
            resetIndentLevel();
            newLine();
        }
    }

    private void addToSection(String section) {
        if (!section.equals(currentSection)) {
            newSection(section);
        }

        ensureSectionSeparator();

        sectionCount++;
    }

    private boolean hasSectionContent() {
        return sectionCount > 0;
    }

    private int typeSpecifierLevel = 0;
    private void enterTypeSpecifier() {
        typeSpecifierLevel++;
    }

    private void exitTypeSpecifier() {
        typeSpecifierLevel--;
    }

    private boolean inTypeSpecifier() {
        return typeSpecifierLevel > 0;
    }

    private int functionDefinitionLevel = 0;
    private void enterFunctionDefinition() {
        functionDefinitionLevel++;
    }

    private void exitFunctionDefinition() {
        functionDefinitionLevel--;
    }

    private boolean inFunctionDefinition() {
        return functionDefinitionLevel > 0;
    }

    private int functionInvocationLevel = 0;
    private void enterFunctionInvocation() {
        functionInvocationLevel++;
    }

    private void exitFunctionInvocation() {
        functionInvocationLevel--;
    }

    private boolean inFunctionInvocation() {
        return functionInvocationLevel > 0;
    }

    private int retrieveLevel = 0;
    private void enterRetrieve() {
        retrieveLevel++;
    }

    private void exitRetrieve() {
        retrieveLevel--;
    }

    private boolean inRetrieve() {
        return retrieveLevel > 0;
    }

    private void enterClause() {
        increaseIndentLevel();
        newLine();
    }

    private void exitClause() {
        decreaseIndentLevel();
    }

    private boolean needsWhitespaceBefore(String terminal) {
        switch (terminal) {
            case ":": return false;
            case ".": return false;
            case "<": return !inTypeSpecifier();
            case ">": return !inTypeSpecifier();
            case "(": return !inFunctionDefinition() && !inFunctionInvocation();
            case ")": return !inFunctionDefinition() && !inFunctionInvocation();
            case "[": return inRetrieve();
            case "]": return false;
            default: return true;
        }
    }

    private boolean needsWhitespaceAfter(String terminal) {
        switch (terminal) {
            case ".": return false;
            case "<": return !inTypeSpecifier();
            case ">": return !inTypeSpecifier();
            case "(": return !inFunctionDefinition() && !inFunctionInvocation();
            case ")": return !inFunctionDefinition() && !inFunctionInvocation();
            case "[": return false;
            case "]": return inRetrieve();
            default: return true;
        }
    }

    private void appendTerminal(String terminal) {
        if (needsWhitespaceBefore(terminal)) {
            ensureWhitespace();
        }
        output.append(terminal);
        onNewLine = false;
        needsWhitespace = needsWhitespaceAfter(terminal);
    }

    private void increaseIndentLevel() {
        indentLevel++;
    }

    private void decreaseIndentLevel() {
        indentLevel--;
    }

    private void resetIndentLevel() {
        indentLevel = 0;
    }

    private void newLine() {
        output.append(newLine);
        for (int i = 0; i < indentLevel; i++) {
            output.append(tab);
        }
        onNewLine = true;
    }

    private void newConstruct(String section) {
        resetIndentLevel();
        newLine();
        addToSection(section);
    }

    private void ensureWhitespace() {
        if (!onNewLine && needsWhitespace) {
            output.append(space);
        }
    }

    private void reset() {
        resetIndentLevel();
        onNewLine = true;
        output = new StringBuilder();
    }

    @Override
    public Object visitLibrary(cqlParser.LibraryContext ctx) {
        reset();
        super.visitLibrary(ctx);
        resetIndentLevel();
        newLine();
        return output.toString();
    }

    @Override
    public Object visitLibraryDefinition(cqlParser.LibraryDefinitionContext ctx) {
        addToSection("library");
        return super.visitLibraryDefinition(ctx);
    }

    @Override
    public Object visitUsingDefinition(cqlParser.UsingDefinitionContext ctx) {
        newConstruct("using");
        return super.visitUsingDefinition(ctx);
    }

    @Override
    public Object visitIncludeDefinition(cqlParser.IncludeDefinitionContext ctx) {
        newConstruct("include");
        return super.visitIncludeDefinition(ctx);
    }

    @Override
    public Object visitLocalIdentifier(cqlParser.LocalIdentifierContext ctx) {
        return super.visitLocalIdentifier(ctx);
    }

    @Override
    public Object visitAccessModifier(cqlParser.AccessModifierContext ctx) {
        return super.visitAccessModifier(ctx);
    }

    @Override
    public Object visitParameterDefinition(cqlParser.ParameterDefinitionContext ctx) {
        newConstruct("parameter");
        return super.visitParameterDefinition(ctx);
    }

    @Override
    public Object visitCodesystemDefinition(cqlParser.CodesystemDefinitionContext ctx) {
        newConstruct("codesystem");
        return super.visitCodesystemDefinition(ctx);
    }

    @Override
    public Object visitValuesetDefinition(cqlParser.ValuesetDefinitionContext ctx) {
        newConstruct("valueset");
        return super.visitValuesetDefinition(ctx);
    }

    @Override
    public Object visitCodesystems(cqlParser.CodesystemsContext ctx) {
        return super.visitCodesystems(ctx);
    }

    @Override
    public Object visitCodesystemIdentifier(cqlParser.CodesystemIdentifierContext ctx) {
        return super.visitCodesystemIdentifier(ctx);
    }

    @Override
    public Object visitLibraryIdentifier(cqlParser.LibraryIdentifierContext ctx) {
        return super.visitLibraryIdentifier(ctx);
    }

    @Override
    public Object visitCodeDefinition(cqlParser.CodeDefinitionContext ctx) {
        newConstruct("code");
        return super.visitCodeDefinition(ctx);
    }

    @Override
    public Object visitConceptDefinition(cqlParser.ConceptDefinitionContext ctx) {
        newConstruct("concept");
        return super.visitConceptDefinition(ctx);
    }

    @Override
    public Object visitCodeIdentifier(cqlParser.CodeIdentifierContext ctx) {
        return super.visitCodeIdentifier(ctx);
    }

    @Override
    public Object visitCodesystemId(cqlParser.CodesystemIdContext ctx) {
        return super.visitCodesystemId(ctx);
    }

    @Override
    public Object visitValuesetId(cqlParser.ValuesetIdContext ctx) {
        return super.visitValuesetId(ctx);
    }

    @Override
    public Object visitVersionSpecifier(cqlParser.VersionSpecifierContext ctx) {
        return super.visitVersionSpecifier(ctx);
    }

    @Override
    public Object visitCodeId(cqlParser.CodeIdContext ctx) {
        return super.visitCodeId(ctx);
    }

    @Override
    public Object visitTypeSpecifier(cqlParser.TypeSpecifierContext ctx) {
        enterTypeSpecifier();
        try {
            return super.visitTypeSpecifier(ctx);
        }
        finally {
            exitTypeSpecifier();
        }
    }

    @Override
    public Object visitNamedTypeSpecifier(cqlParser.NamedTypeSpecifierContext ctx) {
        return super.visitNamedTypeSpecifier(ctx);
    }

    @Override
    public Object visitModelIdentifier(cqlParser.ModelIdentifierContext ctx) {
        return super.visitModelIdentifier(ctx);
    }

    @Override
    public Object visitListTypeSpecifier(cqlParser.ListTypeSpecifierContext ctx) {
        return super.visitListTypeSpecifier(ctx);
    }

    @Override
    public Object visitIntervalTypeSpecifier(cqlParser.IntervalTypeSpecifierContext ctx) {
        return super.visitIntervalTypeSpecifier(ctx);
    }

    @Override
    public Object visitTupleTypeSpecifier(cqlParser.TupleTypeSpecifierContext ctx) {
        return super.visitTupleTypeSpecifier(ctx);
    }

    @Override
    public Object visitTupleElementDefinition(cqlParser.TupleElementDefinitionContext ctx) {
        return super.visitTupleElementDefinition(ctx);
    }

    @Override
    public Object visitChoiceTypeSpecifier(cqlParser.ChoiceTypeSpecifierContext ctx) {
        return super.visitChoiceTypeSpecifier(ctx);
    }

    @Override
    public Object visitStatement(cqlParser.StatementContext ctx) {
        return super.visitStatement(ctx);
    }

    @Override
    public Object visitExpressionDefinition(cqlParser.ExpressionDefinitionContext ctx) {
        newConstruct("statement");
        Object result = defaultResult();
        int n = ctx.getChildCount();
        for (int i=0; i<n; i++) {
            if (!shouldVisitNextChild(ctx, result)) {
                break;
            }

            ParseTree c = ctx.getChild(i);
            if (c == ctx.expression()) {
                enterClause();
            }
            try {
                Object childResult = c.accept(this);
                result = aggregateResult(result, childResult);
            }
            finally {
                if (c == ctx.expression()) {
                    exitClause();
                }
            }
        }

        return result;
    }

    @Override
    public Object visitContextDefinition(cqlParser.ContextDefinitionContext ctx) {
        newConstruct("statement");
        return super.visitContextDefinition(ctx);
    }

    @Override
    public Object visitFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
        newConstruct("statement");

        Object result = defaultResult();
        int n = ctx.getChildCount();
        boolean clauseEntered = false;
        try {
            for (int i = 0; i < n; i++) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break;
                }

                ParseTree c = ctx.getChild(i);

                if (c.getText().equals("(")) {
                    enterFunctionDefinition();
                }

                Object childResult = c.accept(this);
                result = aggregateResult(result, childResult);

                if (c.getText().equals(")")) {
                    exitFunctionDefinition();
                }

                if (c.getText().equals(":")) {
                    enterClause();
                    clauseEntered = true;
                }

            }
        }
        finally {
            if (clauseEntered) {
                exitClause();
            }
        }

        return result;
    }

    @Override
    public Object visitOperandDefinition(cqlParser.OperandDefinitionContext ctx) {
        return super.visitOperandDefinition(ctx);
    }

    @Override
    public Object visitFunctionBody(cqlParser.FunctionBodyContext ctx) {
        return super.visitFunctionBody(ctx);
    }

    @Override
    public Object visitQuerySource(cqlParser.QuerySourceContext ctx) {
        return super.visitQuerySource(ctx);
    }

    @Override
    public Object visitAliasedQuerySource(cqlParser.AliasedQuerySourceContext ctx) {
        return super.visitAliasedQuerySource(ctx);
    }

    @Override
    public Object visitAlias(cqlParser.AliasContext ctx) {
        return super.visitAlias(ctx);
    }

    @Override
    public Object visitQueryInclusionClause(cqlParser.QueryInclusionClauseContext ctx) {
        enterClause();
        try {
            return super.visitQueryInclusionClause(ctx);
        }
        finally {
            exitClause();
        }
    }

    private Object visitWithOrWithoutClause(ParserRuleContext ctx) {
        Object result = defaultResult();
        int n = ctx.getChildCount();
        boolean clauseEntered = false;
        try {
            for (int i = 0; i < n; i++) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break;
                }

                ParseTree c = ctx.getChild(i);
                if (c.getText().equals("such that")) {
                    enterClause();
                    clauseEntered = true;
                }
                Object childResult = c.accept(this);
                result = aggregateResult(result, childResult);
            }
        }
        finally {
            if (clauseEntered) {
                exitClause();
            }
        }

        return result;
    }

    @Override
    public Object visitWithClause(cqlParser.WithClauseContext ctx) {
        return visitWithOrWithoutClause(ctx);
    }

    @Override
    public Object visitWithoutClause(cqlParser.WithoutClauseContext ctx) {
        return visitWithOrWithoutClause(ctx);
    }

    @Override
    public Object visitRetrieve(cqlParser.RetrieveContext ctx) {
        enterRetrieve();
        try {
            return super.visitRetrieve(ctx);
        }
        finally {
            exitRetrieve();
        }
    }

    @Override
    public Object visitCodePath(cqlParser.CodePathContext ctx) {
        return super.visitCodePath(ctx);
    }

    @Override
    public Object visitTerminology(cqlParser.TerminologyContext ctx) {
        return super.visitTerminology(ctx);
    }

    @Override
    public Object visitQualifier(cqlParser.QualifierContext ctx) {
        return super.visitQualifier(ctx);
    }

    @Override
    public Object visitQuery(cqlParser.QueryContext ctx) {
        return super.visitQuery(ctx);
    }

    @Override
    public Object visitSourceClause(cqlParser.SourceClauseContext ctx) {
        return super.visitSourceClause(ctx);
    }

    @Override
    public Object visitSingleSourceClause(cqlParser.SingleSourceClauseContext ctx) {
        return super.visitSingleSourceClause(ctx);
    }

    @Override
    public Object visitMultipleSourceClause(cqlParser.MultipleSourceClauseContext ctx) {
        return super.visitMultipleSourceClause(ctx);
    }

    @Override
    public Object visitLetClause(cqlParser.LetClauseContext ctx) {
        enterClause();
        try {
            return super.visitLetClause(ctx);
        }
        finally {
            exitClause();
        }
    }

    @Override
    public Object visitLetClauseItem(cqlParser.LetClauseItemContext ctx) {
        return super.visitLetClauseItem(ctx);
    }

    @Override
    public Object visitWhereClause(cqlParser.WhereClauseContext ctx) {
        enterClause();
        try {
            return super.visitWhereClause(ctx);
        }
        finally {
            exitClause();
        }
    }

    @Override
    public Object visitReturnClause(cqlParser.ReturnClauseContext ctx) {
        enterClause();
        try {
            return super.visitReturnClause(ctx);
        }
        finally {
            exitClause();
        }
    }

    @Override
    public Object visitSortClause(cqlParser.SortClauseContext ctx) {
        enterClause();
        try {
            return super.visitSortClause(ctx);
        }
        finally {
            exitClause();
        }
    }

    @Override
    public Object visitSortDirection(cqlParser.SortDirectionContext ctx) {
        return super.visitSortDirection(ctx);
    }

    @Override
    public Object visitSortByItem(cqlParser.SortByItemContext ctx) {
        return super.visitSortByItem(ctx);
    }

    @Override
    public Object visitQualifiedIdentifier(cqlParser.QualifiedIdentifierContext ctx) {
        return super.visitQualifiedIdentifier(ctx);
    }

    @Override
    public Object visitDurationBetweenExpression(cqlParser.DurationBetweenExpressionContext ctx) {
        return super.visitDurationBetweenExpression(ctx);
    }

    @Override
    public Object visitInFixSetExpression(cqlParser.InFixSetExpressionContext ctx) {
        return super.visitInFixSetExpression(ctx);
    }

    @Override
    public Object visitRetrieveExpression(cqlParser.RetrieveExpressionContext ctx) {
        return super.visitRetrieveExpression(ctx);
    }

    @Override
    public Object visitTimingExpression(cqlParser.TimingExpressionContext ctx) {
        return super.visitTimingExpression(ctx);
    }

    @Override
    public Object visitNotExpression(cqlParser.NotExpressionContext ctx) {
        return super.visitNotExpression(ctx);
    }

    @Override
    public Object visitQueryExpression(cqlParser.QueryExpressionContext ctx) {
        return super.visitQueryExpression(ctx);
    }

    @Override
    public Object visitBooleanExpression(cqlParser.BooleanExpressionContext ctx) {
        return super.visitBooleanExpression(ctx);
    }

    @Override
    public Object visitOrExpression(cqlParser.OrExpressionContext ctx) {
        return super.visitOrExpression(ctx);
    }

    @Override
    public Object visitCastExpression(cqlParser.CastExpressionContext ctx) {
        return super.visitCastExpression(ctx);
    }

    @Override
    public Object visitAndExpression(cqlParser.AndExpressionContext ctx) {
        return super.visitAndExpression(ctx);
    }

    @Override
    public Object visitBetweenExpression(cqlParser.BetweenExpressionContext ctx) {
        return super.visitBetweenExpression(ctx);
    }

    @Override
    public Object visitMembershipExpression(cqlParser.MembershipExpressionContext ctx) {
        return super.visitMembershipExpression(ctx);
    }

    @Override
    public Object visitDifferenceBetweenExpression(cqlParser.DifferenceBetweenExpressionContext ctx) {
        return super.visitDifferenceBetweenExpression(ctx);
    }

    @Override
    public Object visitInequalityExpression(cqlParser.InequalityExpressionContext ctx) {
        return super.visitInequalityExpression(ctx);
    }

    @Override
    public Object visitEqualityExpression(cqlParser.EqualityExpressionContext ctx) {
        return super.visitEqualityExpression(ctx);
    }

    @Override
    public Object visitExistenceExpression(cqlParser.ExistenceExpressionContext ctx) {
        return super.visitExistenceExpression(ctx);
    }

    @Override
    public Object visitImpliesExpression(cqlParser.ImpliesExpressionContext ctx) {
        return super.visitImpliesExpression(ctx);
    }

    @Override
    public Object visitTermExpression(cqlParser.TermExpressionContext ctx) {
        return super.visitTermExpression(ctx);
    }

    @Override
    public Object visitTypeExpression(cqlParser.TypeExpressionContext ctx) {
        return super.visitTypeExpression(ctx);
    }

    @Override
    public Object visitDateTimePrecision(cqlParser.DateTimePrecisionContext ctx) {
        return super.visitDateTimePrecision(ctx);
    }

    @Override
    public Object visitDateTimeComponent(cqlParser.DateTimeComponentContext ctx) {
        return super.visitDateTimeComponent(ctx);
    }

    @Override
    public Object visitPluralDateTimePrecision(cqlParser.PluralDateTimePrecisionContext ctx) {
        return super.visitPluralDateTimePrecision(ctx);
    }

    @Override
    public Object visitAdditionExpressionTerm(cqlParser.AdditionExpressionTermContext ctx) {
        return super.visitAdditionExpressionTerm(ctx);
    }

    @Override
    public Object visitIndexedExpressionTerm(cqlParser.IndexedExpressionTermContext ctx) {
        return super.visitIndexedExpressionTerm(ctx);
    }

    @Override
    public Object visitWidthExpressionTerm(cqlParser.WidthExpressionTermContext ctx) {
        return super.visitWidthExpressionTerm(ctx);
    }

    @Override
    public Object visitTimeUnitExpressionTerm(cqlParser.TimeUnitExpressionTermContext ctx) {
        return super.visitTimeUnitExpressionTerm(ctx);
    }

    @Override
    public Object visitIfThenElseExpressionTerm(cqlParser.IfThenElseExpressionTermContext ctx) {
        return super.visitIfThenElseExpressionTerm(ctx);
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(cqlParser.TimeBoundaryExpressionTermContext ctx) {
        return super.visitTimeBoundaryExpressionTerm(ctx);
    }

    @Override
    public Object visitElementExtractorExpressionTerm(cqlParser.ElementExtractorExpressionTermContext ctx) {
        return super.visitElementExtractorExpressionTerm(ctx);
    }

    @Override
    public Object visitConversionExpressionTerm(cqlParser.ConversionExpressionTermContext ctx) {
        return super.visitConversionExpressionTerm(ctx);
    }

    @Override
    public Object visitTypeExtentExpressionTerm(cqlParser.TypeExtentExpressionTermContext ctx) {
        return super.visitTypeExtentExpressionTerm(ctx);
    }

    @Override
    public Object visitPredecessorExpressionTerm(cqlParser.PredecessorExpressionTermContext ctx) {
        return super.visitPredecessorExpressionTerm(ctx);
    }

    @Override
    public Object visitPointExtractorExpressionTerm(cqlParser.PointExtractorExpressionTermContext ctx) {
        return super.visitPointExtractorExpressionTerm(ctx);
    }

    @Override
    public Object visitMultiplicationExpressionTerm(cqlParser.MultiplicationExpressionTermContext ctx) {
        return super.visitMultiplicationExpressionTerm(ctx);
    }

    @Override
    public Object visitAggregateExpressionTerm(cqlParser.AggregateExpressionTermContext ctx) {
        return super.visitAggregateExpressionTerm(ctx);
    }

    @Override
    public Object visitDurationExpressionTerm(cqlParser.DurationExpressionTermContext ctx) {
        return super.visitDurationExpressionTerm(ctx);
    }

    @Override
    public Object visitCaseExpressionTerm(cqlParser.CaseExpressionTermContext ctx) {
        return super.visitCaseExpressionTerm(ctx);
    }

    @Override
    public Object visitPowerExpressionTerm(cqlParser.PowerExpressionTermContext ctx) {
        return super.visitPowerExpressionTerm(ctx);
    }

    @Override
    public Object visitSuccessorExpressionTerm(cqlParser.SuccessorExpressionTermContext ctx) {
        return super.visitSuccessorExpressionTerm(ctx);
    }

    @Override
    public Object visitPolarityExpressionTerm(cqlParser.PolarityExpressionTermContext ctx) {
        return super.visitPolarityExpressionTerm(ctx);
    }

    @Override
    public Object visitTermExpressionTerm(cqlParser.TermExpressionTermContext ctx) {
        return super.visitTermExpressionTerm(ctx);
    }

    @Override
    public Object visitInvocationExpressionTerm(cqlParser.InvocationExpressionTermContext ctx) {
        return super.visitInvocationExpressionTerm(ctx);
    }

    @Override
    public Object visitCaseExpressionItem(cqlParser.CaseExpressionItemContext ctx) {
        return super.visitCaseExpressionItem(ctx);
    }

    @Override
    public Object visitDateTimePrecisionSpecifier(cqlParser.DateTimePrecisionSpecifierContext ctx) {
        return super.visitDateTimePrecisionSpecifier(ctx);
    }

    @Override
    public Object visitRelativeQualifier(cqlParser.RelativeQualifierContext ctx) {
        return super.visitRelativeQualifier(ctx);
    }

    @Override
    public Object visitOffsetRelativeQualifier(cqlParser.OffsetRelativeQualifierContext ctx) {
        return super.visitOffsetRelativeQualifier(ctx);
    }

    @Override
    public Object visitExclusiveRelativeQualifier(cqlParser.ExclusiveRelativeQualifierContext ctx) {
        return super.visitExclusiveRelativeQualifier(ctx);
    }

    @Override
    public Object visitQuantityOffset(cqlParser.QuantityOffsetContext ctx) {
        return super.visitQuantityOffset(ctx);
    }

    @Override
    public Object visitTemporalRelationship(cqlParser.TemporalRelationshipContext ctx) {
        return super.visitTemporalRelationship(ctx);
    }

    @Override
    public Object visitConcurrentWithIntervalOperatorPhrase(cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
        return super.visitConcurrentWithIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitIncludesIntervalOperatorPhrase(cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
        return super.visitIncludesIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitIncludedInIntervalOperatorPhrase(cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
        return super.visitIncludedInIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
        return super.visitBeforeOrAfterIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitWithinIntervalOperatorPhrase(cqlParser.WithinIntervalOperatorPhraseContext ctx) {
        return super.visitWithinIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitMeetsIntervalOperatorPhrase(cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
        return super.visitMeetsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        return super.visitOverlapsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitStartsIntervalOperatorPhrase(cqlParser.StartsIntervalOperatorPhraseContext ctx) {
        return super.visitStartsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        return super.visitEndsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitInvocationTerm(cqlParser.InvocationTermContext ctx) {
        return super.visitInvocationTerm(ctx);
    }

    @Override
    public Object visitLiteralTerm(cqlParser.LiteralTermContext ctx) {
        return super.visitLiteralTerm(ctx);
    }

    @Override
    public Object visitExternalConstantTerm(cqlParser.ExternalConstantTermContext ctx) {
        return super.visitExternalConstantTerm(ctx);
    }

    @Override
    public Object visitIntervalSelectorTerm(cqlParser.IntervalSelectorTermContext ctx) {
        return super.visitIntervalSelectorTerm(ctx);
    }

    @Override
    public Object visitTupleSelectorTerm(cqlParser.TupleSelectorTermContext ctx) {
        return super.visitTupleSelectorTerm(ctx);
    }

    @Override
    public Object visitInstanceSelectorTerm(cqlParser.InstanceSelectorTermContext ctx) {
        return super.visitInstanceSelectorTerm(ctx);
    }

    @Override
    public Object visitListSelectorTerm(cqlParser.ListSelectorTermContext ctx) {
        return super.visitListSelectorTerm(ctx);
    }

    @Override
    public Object visitCodeSelectorTerm(cqlParser.CodeSelectorTermContext ctx) {
        return super.visitCodeSelectorTerm(ctx);
    }

    @Override
    public Object visitConceptSelectorTerm(cqlParser.ConceptSelectorTermContext ctx) {
        return super.visitConceptSelectorTerm(ctx);
    }

    @Override
    public Object visitParenthesizedTerm(cqlParser.ParenthesizedTermContext ctx) {
        return super.visitParenthesizedTerm(ctx);
    }

    @Override
    public Object visitBooleanLiteral(cqlParser.BooleanLiteralContext ctx) {
        return super.visitBooleanLiteral(ctx);
    }

    @Override
    public Object visitNullLiteral(cqlParser.NullLiteralContext ctx) {
        return super.visitNullLiteral(ctx);
    }

    @Override
    public Object visitStringLiteral(cqlParser.StringLiteralContext ctx) {
        return super.visitStringLiteral(ctx);
    }

    @Override
    public Object visitNumberLiteral(cqlParser.NumberLiteralContext ctx) {
        return super.visitNumberLiteral(ctx);
    }

    @Override
    public Object visitDateTimeLiteral(cqlParser.DateTimeLiteralContext ctx) {
        return super.visitDateTimeLiteral(ctx);
    }

    @Override
    public Object visitTimeLiteral(cqlParser.TimeLiteralContext ctx) {
        return super.visitTimeLiteral(ctx);
    }

    @Override
    public Object visitQuantityLiteral(cqlParser.QuantityLiteralContext ctx) {
        return super.visitQuantityLiteral(ctx);
    }

    @Override
    public Object visitIntervalSelector(cqlParser.IntervalSelectorContext ctx) {
        return super.visitIntervalSelector(ctx);
    }

    @Override
    public Object visitTupleSelector(cqlParser.TupleSelectorContext ctx) {
        return super.visitTupleSelector(ctx);
    }

    @Override
    public Object visitTupleElementSelector(cqlParser.TupleElementSelectorContext ctx) {
        return super.visitTupleElementSelector(ctx);
    }

    @Override
    public Object visitInstanceSelector(cqlParser.InstanceSelectorContext ctx) {
        return super.visitInstanceSelector(ctx);
    }

    @Override
    public Object visitInstanceElementSelector(cqlParser.InstanceElementSelectorContext ctx) {
        return super.visitInstanceElementSelector(ctx);
    }

    @Override
    public Object visitListSelector(cqlParser.ListSelectorContext ctx) {
        return super.visitListSelector(ctx);
    }

    @Override
    public Object visitDisplayClause(cqlParser.DisplayClauseContext ctx) {
        return super.visitDisplayClause(ctx);
    }

    @Override
    public Object visitCodeSelector(cqlParser.CodeSelectorContext ctx) {
        return super.visitCodeSelector(ctx);
    }

    @Override
    public Object visitConceptSelector(cqlParser.ConceptSelectorContext ctx) {
        return super.visitConceptSelector(ctx);
    }

    @Override
    public Object visitIdentifier(cqlParser.IdentifierContext ctx) {
        return super.visitIdentifier(ctx);
    }

    @Override
    public Object visitExternalConstant(cqlParser.ExternalConstantContext ctx) {
        return super.visitExternalConstant(ctx);
    }

    @Override
    public Object visitMemberInvocation(cqlParser.MemberInvocationContext ctx) {
        return super.visitMemberInvocation(ctx);
    }

    @Override
    public Object visitFunctionInvocation(cqlParser.FunctionInvocationContext ctx) {
        enterFunctionInvocation();
        try {
            return super.visitFunctionInvocation(ctx);
        }
        finally {
            exitFunctionInvocation();
        }
    }

    @Override
    public Object visitThisInvocation(cqlParser.ThisInvocationContext ctx) {
        return super.visitThisInvocation(ctx);
    }

    @Override
    public Object visitFunction(cqlParser.FunctionContext ctx) {
        return super.visitFunction(ctx);
    }

    @Override
    public Object visitParamList(cqlParser.ParamListContext ctx) {
        return super.visitParamList(ctx);
    }

    @Override
    public Object visitQuantity(cqlParser.QuantityContext ctx) {
        return super.visitQuantity(ctx);
    }

    @Override
    public Object visitUnit(cqlParser.UnitContext ctx) {
        return super.visitUnit(ctx);
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        appendTerminal(node.getText());
        return super.visitTerminal(node);
    }
}
