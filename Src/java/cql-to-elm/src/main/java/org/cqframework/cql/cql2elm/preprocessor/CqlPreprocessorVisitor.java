package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

public class CqlPreprocessorVisitor extends cqlBaseVisitor {
    private LibraryInfo libraryInfo = new LibraryInfo();
    private boolean implicitPatientCreated = false;
    private String currentContext = "Patient";

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    @Override
    public Object visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        libraryInfo.setLibraryName((String)visit(ctx.identifier()));
        if (ctx.versionSpecifier() != null) {
            libraryInfo.setVersion((String)visit(ctx.versionSpecifier()));
        }
        return super.visitLibraryDefinition(ctx);
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        IncludeDefinitionInfo includeDefinition = new IncludeDefinitionInfo();
        includeDefinition.setName((String)visit(ctx.identifier()));
        if (ctx.versionSpecifier() != null) {
            includeDefinition.setVersion((String)visit(ctx.versionSpecifier()));
        }
        if (ctx.localIdentifier() != null) {
          includeDefinition.setLocalName((String)visit(ctx.localIdentifier()));
        }        
        libraryInfo.addIncludeDefinition(includeDefinition);
        return includeDefinition;
    }

    @Override
    public Object visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        UsingDefinitionInfo usingDefinition = new UsingDefinitionInfo();
        usingDefinition.setName((String)visit(ctx.identifier()));
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
    public Object visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDefinitionInfo parameterDefinition = new ParameterDefinitionInfo();
        parameterDefinition.setName((String)visit(ctx.identifier()));
        parameterDefinition.setDefinition(ctx);
        libraryInfo.addParameterDefinition(parameterDefinition);
        return parameterDefinition;
    }

    @Override
    public Object visitContextDefinition(@NotNull cqlParser.ContextDefinitionContext ctx) {
        currentContext = (String)visit(ctx.identifier());
        if (!implicitPatientCreated) {
            ExpressionDefinitionInfo expressionDefinition = new ExpressionDefinitionInfo();
            expressionDefinition.setName("Patient");
            expressionDefinition.setContext(currentContext);
            libraryInfo.addExpressionDefinition(expressionDefinition);
            implicitPatientCreated = true;
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
}
