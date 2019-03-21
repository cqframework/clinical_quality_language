package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

import java.util.ArrayList;
import java.util.List;


public class CqlPreprocessorVisitor extends cqlBaseVisitor {
    private LibraryInfo libraryInfo = new LibraryInfo();
    private boolean implicitContextCreated = false;
    private String currentContext = "Unspecified";

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    @Override
    public Object visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        libraryInfo.setLibraryName(String.join(".", (Iterable<String>)visit(ctx.qualifiedIdentifier())));
        if (ctx.versionSpecifier() != null) {
            libraryInfo.setVersion((String)visit(ctx.versionSpecifier()));
        }
        return super.visitLibraryDefinition(ctx);
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        IncludeDefinitionInfo includeDefinition = new IncludeDefinitionInfo();
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        includeDefinition.setName(String.join(".", identifiers));
        if (ctx.versionSpecifier() != null) {
            includeDefinition.setVersion((String)visit(ctx.versionSpecifier()));
        }
        if (ctx.localIdentifier() != null) {
            includeDefinition.setLocalName((String)visit(ctx.localIdentifier()));
        }
        else if (identifiers.size() > 1) {
            // If the library name is qualified, use only the unqualified name as the local name
            includeDefinition.setLocalName(identifiers.get(identifiers.size() - 1));
        }
        libraryInfo.addIncludeDefinition(includeDefinition);
        return includeDefinition;
    }

    @Override
    public Object visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        UsingDefinitionInfo usingDefinition = new UsingDefinitionInfo();
        usingDefinition.setName((String)visit(ctx.modelIdentifier()));
        if (ctx.versionSpecifier() != null) {
            usingDefinition.setVersion((String)visit(ctx.versionSpecifier()));
        }
        libraryInfo.addUsingDefinition(usingDefinition);
        return usingDefinition;
    }

    @Override
    public Object visitCodesystemDefinition(@NotNull cqlParser.CodesystemDefinitionContext ctx) {
        CodesystemDefinitionInfo codesystemDefinition = new CodesystemDefinitionInfo();
        codesystemDefinition.setName((String)visit(ctx.identifier()));
        codesystemDefinition.setDefinition(ctx);
        libraryInfo.addCodesystemDefinition(codesystemDefinition);
        return codesystemDefinition;
    }

    @Override
    public Object visitValuesetDefinition(@NotNull cqlParser.ValuesetDefinitionContext ctx) {
        ValuesetDefinitionInfo valuesetDefinition = new ValuesetDefinitionInfo();
        valuesetDefinition.setName((String)visit(ctx.identifier()));
        valuesetDefinition.setDefinition(ctx);
        libraryInfo.addValuesetDefinition(valuesetDefinition);
        return valuesetDefinition;
    }

    @Override
    public Object visitCodeDefinition(@NotNull cqlParser.CodeDefinitionContext ctx) {
        CodeDefinitionInfo codeDefinition = new CodeDefinitionInfo();
        codeDefinition.setName((String)visit(ctx.identifier()));
        codeDefinition.setDefinition(ctx);
        libraryInfo.addCodeDefinition(codeDefinition);
        return codeDefinition;
    }

    @Override
    public Object visitConceptDefinition(@NotNull cqlParser.ConceptDefinitionContext ctx) {
        ConceptDefinitionInfo conceptDefinition = new ConceptDefinitionInfo();
        conceptDefinition.setName((String)visit(ctx.identifier()));
        conceptDefinition.setDefinition(ctx);
        libraryInfo.addConceptDefinition(conceptDefinition);
        return conceptDefinition;
    }

    @Override
    public Object visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDefinitionInfo parameterDefinition = new ParameterDefinitionInfo();
        parameterDefinition.setName((String)visit(ctx.identifier()));
        parameterDefinition.setDefinition(ctx);
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

        if (!implicitContextCreated && !unqualifiedContext.equals("Unspecified")) {
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
        libraryInfo.addExpressionDefinition(expressionDefinition);
        return expressionDefinition;
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDefinitionInfo functionDefinition = new FunctionDefinitionInfo();
        functionDefinition.setName((String)visit(ctx.identifier()));
        functionDefinition.setContext(currentContext);
        functionDefinition.setDefinition(ctx);
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
}
