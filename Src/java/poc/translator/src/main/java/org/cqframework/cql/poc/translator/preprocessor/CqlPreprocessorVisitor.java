package org.cqframework.cql.poc.translator.preprocessor;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

public class CqlPreprocessorVisitor extends cqlBaseVisitor {
    private LibraryInfo libraryInfo = new LibraryInfo();

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    @Override
    public Object visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        libraryInfo.setLibraryName((String)visit(ctx.IDENTIFIER()));
        if (ctx.STRING() != null) {
            libraryInfo.setVersion((String)visit(ctx.STRING()));
        }
        return super.visitLibraryDefinition(ctx);
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        IncludeDefinitionInfo includeDefinition = new IncludeDefinitionInfo();
        includeDefinition.setName((String)visit(ctx.IDENTIFIER(0)));
        if (ctx.STRING() != null) {
            includeDefinition.setVersion((String)visit(ctx.STRING()));
        }
        includeDefinition.setLocalName((String)visit(ctx.IDENTIFIER(1)));
        libraryInfo.addIncludeDefinition(includeDefinition);
        return includeDefinition;
    }

    @Override
    public Object visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        UsingDefinitionInfo usingDefinition = new UsingDefinitionInfo();
        usingDefinition.setName((String)visit(ctx.IDENTIFIER()));
        if (ctx.STRING() != null) {
            usingDefinition.setVersion((String)visit(ctx.STRING()));
        }
        libraryInfo.addUsingDefinition(usingDefinition);
        return usingDefinition;
    }

    @Override
    public Object visitValuesetDefinitionByExpression(@NotNull cqlParser.ValuesetDefinitionByExpressionContext ctx) {
        ValuesetDefinitionInfo valuesetDefinition = new ValuesetDefinitionInfo();
        valuesetDefinition.setName((String)visit(ctx.VALUESET()));
        valuesetDefinition.setDefinition(ctx);
        libraryInfo.addValuesetDefinition(valuesetDefinition);
        return valuesetDefinition;
    }

    @Override
    public Object visitValuesetDefinitionByConstructor(@NotNull cqlParser.ValuesetDefinitionByConstructorContext ctx) {
        ValuesetDefinitionInfo valuesetDefinition = new ValuesetDefinitionInfo();
        valuesetDefinition.setName((String)visit(ctx.VALUESET()));
        valuesetDefinition.setDefinition(ctx);
        libraryInfo.addValuesetDefinition(valuesetDefinition);
        return valuesetDefinition;
    }

    @Override
    public Object visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDefinitionInfo parameterDefinition = new ParameterDefinitionInfo();
        parameterDefinition.setName((String)visit(ctx.IDENTIFIER()));
        parameterDefinition.setDefinition(ctx);
        libraryInfo.addParameterDefinition(parameterDefinition);
        return parameterDefinition;
    }

    @Override
    public Object visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        LetStatementInfo letStatement = new LetStatementInfo();
        letStatement.setName((String)visit(ctx.IDENTIFIER()));
        letStatement.setDefinition(ctx);
        libraryInfo.addLetStatement(letStatement);
        return letStatement;
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDefinitionInfo functionDefinition = new FunctionDefinitionInfo();
        functionDefinition.setName((String)visit(ctx.IDENTIFIER()));
        functionDefinition.setDefinition(ctx);
        libraryInfo.addFunctionDefinition(functionDefinition);
        return functionDefinition;
    }

    @Override
    public Object visitRetrieveDefinition(@NotNull cqlParser.RetrieveDefinitionContext ctx) {
        RetrieveDefinitionInfo retrieveDefinition = new RetrieveDefinitionInfo();
        retrieveDefinition.setName((String)visit(ctx.topic())); // TODO: Needs to take all axes into account
        retrieveDefinition.setDefinition(ctx);
        libraryInfo.addRetrieveDefinition(retrieveDefinition);
        return retrieveDefinition;
    }

    @Override
    public Object visitTerminal(@NotNull TerminalNode node) {
        String text = node.getText();
        int tokenType = node.getSymbol().getType();
        if (cqlLexer.STRING == tokenType || cqlLexer.VALUESET == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length() - 1);
        }

        return text;
    }
}
