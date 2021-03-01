package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

import java.util.ArrayList;
import java.util.List;

public class CqlPreprocessorVisitor extends cqlBaseVisitor {
    private LibraryInfo libraryInfo = new LibraryInfo();
    private boolean implicitContextCreated = false;
    private String currentContext = "Unfiltered";
    private int lastSourceIndex = 0;
    private TokenStream tokenStream;

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    public TokenStream getTokenStream() {
        return tokenStream;
    }

    public void setTokenStream(TokenStream value) {
        tokenStream = value;
    }

    @Override
    public Object visit(@NotNull ParseTree tree) {
        try {
            return super.visit(tree);
        }
        finally {
            //org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();
            //lastSourceIndex = sourceInterval.b;
        }
    }

    private void processHeader(@NotNull ParseTree ctx, BaseInfo info) {
        Interval header = null;
        org.antlr.v4.runtime.misc.Interval sourceInterval = ctx.getSourceInterval();
        int beforeDefinition = sourceInterval.a - 1;
        if (beforeDefinition >= lastSourceIndex) {
            header = new org.antlr.v4.runtime.misc.Interval(lastSourceIndex, sourceInterval.a - 1);
            lastSourceIndex = sourceInterval.b + 1;

            info.setHeaderInterval(header);
            info.setHeader(tokenStream.getText(header));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        libraryInfo.setLibraryName(identifiers.remove(identifiers.size() - 1));
        if (identifiers.size() > 0) {
            libraryInfo.setNamespaceName(String.join(".", identifiers));
        }
        if (ctx.versionSpecifier() != null) {
            libraryInfo.setVersion((String)visit(ctx.versionSpecifier()));
        }
        libraryInfo.setDefinition(ctx);
        processHeader(ctx, libraryInfo);
        return super.visitLibraryDefinition(ctx);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        IncludeDefinitionInfo includeDefinition = new IncludeDefinitionInfo();
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        includeDefinition.setName(identifiers.remove(identifiers.size() - 1));
        if (identifiers.size() > 0) {
            includeDefinition.setNamespaceName(String.join(".", identifiers));
        }
        if (ctx.versionSpecifier() != null) {
            includeDefinition.setVersion((String)visit(ctx.versionSpecifier()));
        }
        if (ctx.localIdentifier() != null) {
            includeDefinition.setLocalName((String)visit(ctx.localIdentifier()));
        }
        else {
            includeDefinition.setLocalName(includeDefinition.getName());
        }
        includeDefinition.setDefinition(ctx);
        processHeader(ctx, includeDefinition);
        libraryInfo.addIncludeDefinition(includeDefinition);
        return includeDefinition;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        UsingDefinitionInfo usingDefinition = new UsingDefinitionInfo();
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        usingDefinition.setName(identifiers.remove(identifiers.size() - 1));
        if (identifiers.size() > 0) {
            usingDefinition.setNamespaceName(String.join(".", identifiers));
        }
        if (ctx.versionSpecifier() != null) {
            usingDefinition.setVersion((String)visit(ctx.versionSpecifier()));
        }
        if (ctx.localIdentifier() != null) {
            usingDefinition.setLocalName((String)visit(ctx.localIdentifier()));
        }
        else {
            usingDefinition.setLocalName(usingDefinition.getName());
        }
        usingDefinition.setDefinition(ctx);
        processHeader(ctx, usingDefinition);
        libraryInfo.addUsingDefinition(usingDefinition);
        return usingDefinition;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitCodesystemDefinition(@NotNull cqlParser.CodesystemDefinitionContext ctx) {
        CodesystemDefinitionInfo codesystemDefinition = new CodesystemDefinitionInfo();
        codesystemDefinition.setName((String)visit(ctx.identifier()));
        codesystemDefinition.setDefinition(ctx);
        processHeader(ctx, codesystemDefinition);
        libraryInfo.addCodesystemDefinition(codesystemDefinition);
        return codesystemDefinition;
    }

    @Override
    public Object visitValuesetDefinition(@NotNull cqlParser.ValuesetDefinitionContext ctx) {
        ValuesetDefinitionInfo valuesetDefinition = new ValuesetDefinitionInfo();
        valuesetDefinition.setName((String)visit(ctx.identifier()));
        valuesetDefinition.setDefinition(ctx);
        processHeader(ctx, valuesetDefinition);
        libraryInfo.addValuesetDefinition(valuesetDefinition);
        return valuesetDefinition;
    }

    @Override
    public Object visitCodeDefinition(@NotNull cqlParser.CodeDefinitionContext ctx) {
        CodeDefinitionInfo codeDefinition = new CodeDefinitionInfo();
        codeDefinition.setName((String)visit(ctx.identifier()));
        codeDefinition.setDefinition(ctx);
        processHeader(ctx, codeDefinition);
        libraryInfo.addCodeDefinition(codeDefinition);
        return codeDefinition;
    }

    @Override
    public Object visitConceptDefinition(@NotNull cqlParser.ConceptDefinitionContext ctx) {
        ConceptDefinitionInfo conceptDefinition = new ConceptDefinitionInfo();
        conceptDefinition.setName((String)visit(ctx.identifier()));
        conceptDefinition.setDefinition(ctx);
        processHeader(ctx, conceptDefinition);
        libraryInfo.addConceptDefinition(conceptDefinition);
        return conceptDefinition;
    }

    @Override
    public Object visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDefinitionInfo parameterDefinition = new ParameterDefinitionInfo();
        parameterDefinition.setName((String)visit(ctx.identifier()));
        parameterDefinition.setDefinition(ctx);
        processHeader(ctx, parameterDefinition);
        libraryInfo.addParameterDefinition(parameterDefinition);
        return parameterDefinition;
    }

    @Override
    public Object visitContextDefinition(@NotNull cqlParser.ContextDefinitionContext ctx) {
        String modelIdentifier = ctx.modelIdentifier() != null ? (String)visit(ctx.modelIdentifier()) : null;
        String unqualifiedContext = (String)visit(ctx.identifier());
        if (modelIdentifier != null && !modelIdentifier.equals("")) {
            currentContext = modelIdentifier + "." + unqualifiedContext;
        }
        else {
            currentContext = unqualifiedContext;
        }

        ContextDefinitionInfo contextDefinition = new ContextDefinitionInfo();
        contextDefinition.setDefinition(ctx);
        processHeader(ctx, contextDefinition);
        libraryInfo.addContextDefinition(contextDefinition);

        if (!implicitContextCreated && !unqualifiedContext.equals("Unfiltered")) {
            ExpressionDefinitionInfo expressionDefinition = new ExpressionDefinitionInfo();
            expressionDefinition.setName(unqualifiedContext);
            expressionDefinition.setContext(currentContext);
            libraryInfo.addExpressionDefinition(expressionDefinition);
            implicitContextCreated = true;
        }
        return currentContext;
    }

    @Override
    public Object visitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDefinitionInfo expressionDefinition = new ExpressionDefinitionInfo();
        expressionDefinition.setName((String)visit(ctx.identifier()));
        expressionDefinition.setContext(currentContext);
        expressionDefinition.setDefinition(ctx);
        processHeader(ctx, expressionDefinition);
        libraryInfo.addExpressionDefinition(expressionDefinition);
        return expressionDefinition;
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDefinitionInfo functionDefinition = new FunctionDefinitionInfo();
        functionDefinition.setName((String)visit(ctx.identifierOrFunctionIdentifier()));
        functionDefinition.setContext(currentContext);
        functionDefinition.setDefinition(ctx);
        processHeader(ctx, functionDefinition);
        libraryInfo.addFunctionDefinition(functionDefinition);
        return functionDefinition;
    }

    @Override
    public Object visitTerminal(@NotNull TerminalNode node) {
        String text = node.getText();
        int tokenType = node.getSymbol().getType();
        if (cqlLexer.STRING == tokenType || cqlLexer.QUOTEDIDENTIFIER == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length() - 1);
        }

        return text;
    }

    @Override
    public Object visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierContext qualifierContext : ctx.qualifier()) {
            String qualifier = (String)visit(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = (String)visit(ctx.identifier());
        identifiers.add(identifier);
        return identifiers;
    }

    @Override
    public Object visitQualifiedIdentifierExpression(@NotNull cqlParser.QualifiedIdentifierExpressionContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierExpressionContext qualifierContext : ctx.qualifierExpression()) {
            String qualifier = (String)visit(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = (String)visit(ctx.referentialIdentifier());
        identifiers.add(identifier);
        return identifiers;
    }
}
