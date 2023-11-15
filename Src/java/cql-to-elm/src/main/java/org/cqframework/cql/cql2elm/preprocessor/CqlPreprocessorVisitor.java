package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CqlPreprocessorVisitor extends CqlPreprocessorElmCommonVisitor {
    static final Logger logger = LoggerFactory.getLogger(CqlPreprocessorVisitor.class);
    private int lastSourceIndex = -1;

    public CqlPreprocessorVisitor(LibraryBuilder libraryBuilder, TokenStream tokenStream) {
        super(libraryBuilder, tokenStream);
    }

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    private void processHeader(ParseTree ctx, BaseInfo info) {
        Interval header = null;
        org.antlr.v4.runtime.misc.Interval sourceInterval = ctx.getSourceInterval();
        int beforeDefinition = sourceInterval.a - 1;
        if (beforeDefinition >= lastSourceIndex) {
            header = new org.antlr.v4.runtime.misc.Interval(lastSourceIndex + 1, sourceInterval.a - 1);
            lastSourceIndex = sourceInterval.b;

            info.setHeaderInterval(header);
            info.setHeader(tokenStream.getText(header));
        }
    }

    @Override
    public Object visitLibrary(cqlParser.LibraryContext ctx) {
        Object lastResult = null;
        // NOTE: Need to set the library identifier here so the builder can begin the translation appropriately
        VersionedIdentifier identifier = new VersionedIdentifier().withId(libraryInfo.getLibraryName()).withVersion(libraryInfo.getVersion());
        if (libraryInfo.getNamespaceName() != null) {
            identifier.setSystem(libraryBuilder.resolveNamespaceUri(libraryInfo.getNamespaceName(), true));
        } else if (libraryBuilder.getNamespaceInfo() != null) {
            identifier.setSystem(libraryBuilder.getNamespaceInfo().getUri());
        }
        libraryBuilder.setLibraryIdentifier(identifier);
        libraryBuilder.beginTranslation();
        try {
            // Loop through and call visit on each child (to ensure they are tracked)
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree tree = ctx.getChild(i);
                TerminalNode terminalNode = tree instanceof TerminalNode ? (TerminalNode) tree : null;
                if (terminalNode != null && terminalNode.getSymbol().getType() == cqlLexer.EOF) {
                    continue;
                }

                Object childResult = visit(tree);
                // Only set the last result if we received something useful
                if (childResult != null) {
                    lastResult = childResult;
                }
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            return lastResult;
        } finally {
            libraryBuilder.endTranslation();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitLibraryDefinition(cqlParser.LibraryDefinitionContext ctx) {
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
    public Object visitIncludeDefinition(cqlParser.IncludeDefinitionContext ctx) {
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
            includeDefinition.setLocalName(parseString(ctx.localIdentifier()));
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
    public Object visitUsingDefinition(cqlParser.UsingDefinitionContext ctx) {
        UsingDefinitionInfo usingDefinition = new UsingDefinitionInfo();
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        final String unqualifiedIdentifier = identifiers.remove(identifiers.size() - 1);
        usingDefinition.setName(unqualifiedIdentifier);
        if (identifiers.size() > 0) {
            usingDefinition.setNamespaceName(String.join(".", identifiers));
        }
        if (ctx.versionSpecifier() != null) {
            usingDefinition.setVersion((String)visit(ctx.versionSpecifier()));
        }
        if (ctx.localIdentifier() != null) {
            usingDefinition.setLocalName(parseString(ctx.localIdentifier()));
        }
        else {
            usingDefinition.setLocalName(usingDefinition.getName());
        }
        usingDefinition.setDefinition(ctx);
        processHeader(ctx, usingDefinition);
        libraryInfo.addUsingDefinition(usingDefinition);

        final String namespaceName = !identifiers.isEmpty() ? String.join(".", identifiers) :
                libraryBuilder.isWellKnownModelName(unqualifiedIdentifier) ? null :
                        (libraryBuilder.getNamespaceInfo() != null ? libraryBuilder.getNamespaceInfo().getName() : null);

        NamespaceInfo modelNamespace = null;
        if (namespaceName != null) {
            String namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true);
            modelNamespace = new NamespaceInfo(namespaceName, namespaceUri);
        }

        String localIdentifier = ctx.localIdentifier() == null ? unqualifiedIdentifier : parseString(ctx.localIdentifier());
        if (!localIdentifier.equals(unqualifiedIdentifier)) {
            throw new IllegalArgumentException(
                    String.format("Local identifiers for models must be the same as the name of the model in this release of the translator (Model %s, Called %s)",
                            unqualifiedIdentifier, localIdentifier));
        }

        // This should only be called once, from this class, and not from Cql2ElmVisitor otherwise there will be duplicate errors sometimes
        Model model = getModel(modelNamespace, unqualifiedIdentifier, parseString(ctx.versionSpecifier()), localIdentifier);

        return usingDefinition;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitCodesystemDefinition(cqlParser.CodesystemDefinitionContext ctx) {
        CodesystemDefinitionInfo codesystemDefinition = new CodesystemDefinitionInfo();
        codesystemDefinition.setName(parseString(ctx.identifier()));
        codesystemDefinition.setDefinition(ctx);
        processHeader(ctx, codesystemDefinition);
        libraryInfo.addCodesystemDefinition(codesystemDefinition);
        return codesystemDefinition;
    }

    @Override
    public Object visitValuesetDefinition(cqlParser.ValuesetDefinitionContext ctx) {
        ValuesetDefinitionInfo valuesetDefinition = new ValuesetDefinitionInfo();
        valuesetDefinition.setName(parseString(ctx.identifier()));
        valuesetDefinition.setDefinition(ctx);
        processHeader(ctx, valuesetDefinition);
        libraryInfo.addValuesetDefinition(valuesetDefinition);
        return valuesetDefinition;
    }

    @Override
    public Object visitCodeDefinition(cqlParser.CodeDefinitionContext ctx) {
        CodeDefinitionInfo codeDefinition = new CodeDefinitionInfo();
        codeDefinition.setName(parseString(ctx.identifier()));
        codeDefinition.setDefinition(ctx);
        processHeader(ctx, codeDefinition);
        libraryInfo.addCodeDefinition(codeDefinition);
        return codeDefinition;
    }

    @Override
    public Object visitConceptDefinition(cqlParser.ConceptDefinitionContext ctx) {
        ConceptDefinitionInfo conceptDefinition = new ConceptDefinitionInfo();
        conceptDefinition.setName(parseString(ctx.identifier()));
        conceptDefinition.setDefinition(ctx);
        processHeader(ctx, conceptDefinition);
        libraryInfo.addConceptDefinition(conceptDefinition);
        return conceptDefinition;
    }

    @Override
    public Object visitParameterDefinition(cqlParser.ParameterDefinitionContext ctx) {
        ParameterDefinitionInfo parameterDefinition = new ParameterDefinitionInfo();
        parameterDefinition.setName(parseString(ctx.identifier()));
        parameterDefinition.setDefinition(ctx);
        processHeader(ctx, parameterDefinition);
        libraryInfo.addParameterDefinition(parameterDefinition);
        return parameterDefinition;
    }

    @Override
    public Object visitContextDefinition(cqlParser.ContextDefinitionContext ctx) {
        String modelIdentifier = ctx.modelIdentifier() != null ? parseString(ctx.modelIdentifier()) : null;
        String unqualifiedContext = parseString(ctx.identifier());
        if (modelIdentifier != null && !modelIdentifier.equals("")) {
            setCurrentContext(modelIdentifier + "." + unqualifiedContext);
        }
        else {
            setCurrentContext(unqualifiedContext);
        }

        ContextDefinitionInfo contextDefinition = new ContextDefinitionInfo();
        contextDefinition.setDefinition(ctx);
        processHeader(ctx, contextDefinition);
        libraryInfo.addContextDefinition(contextDefinition);

        if (!getImplicitContextCreated() && !unqualifiedContext.equals("Unfiltered")) {
            ExpressionDefinitionInfo expressionDefinition = new ExpressionDefinitionInfo();
            expressionDefinition.setName(unqualifiedContext);
            expressionDefinition.setContext(getCurrentContext());
            libraryInfo.addExpressionDefinition(expressionDefinition);
            setImplicitContextCreated(true);
        }
        return getCurrentContext();
    }

    @Override
    public Object visitExpressionDefinition(cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDefinitionInfo expressionDefinition = new ExpressionDefinitionInfo();
        expressionDefinition.setName(parseString(ctx.identifier()));
        expressionDefinition.setContext(getCurrentContext());
        expressionDefinition.setDefinition(ctx);
        processHeader(ctx, expressionDefinition);
        libraryInfo.addExpressionDefinition(expressionDefinition);
        return expressionDefinition;
    }

    @Override
    public Object visitFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
        FunctionDefinitionInfo functionDefinition = new FunctionDefinitionInfo();
        functionDefinition.setName(parseString(ctx.identifierOrFunctionIdentifier()));
        functionDefinition.setContext(getCurrentContext());
        functionDefinition.setDefinition(ctx);
        processHeader(ctx, functionDefinition);
        libraryInfo.addFunctionDefinition(functionDefinition);
        return functionDefinition;
    }

    @Override
    public NamedTypeSpecifier visitNamedTypeSpecifier(cqlParser.NamedTypeSpecifierContext ctx) {
        List<String> qualifiers = parseQualifiers(ctx);
        String modelIdentifier = getModelIdentifier(qualifiers);
        String identifier = getTypeIdentifier(qualifiers, parseString(ctx.referentialOrTypeNameIdentifier()));
        final String typeSpecifierKey = String.format("%s:%s", modelIdentifier, identifier);

        DataType resultType = libraryBuilder.resolveTypeName(modelIdentifier, identifier);
        if (null == resultType) {
            libraryBuilder.addNamedTypeSpecifierResult(typeSpecifierKey, ResultWithPossibleError.withError());
            throw new CqlCompilerException(String.format("Could not find type for model: %s and name: %s", modelIdentifier, identifier));
        }
        NamedTypeSpecifier result = of.createNamedTypeSpecifier()
                .withName(libraryBuilder.dataTypeToQName(resultType));

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.setResultType(resultType);

        libraryBuilder.addNamedTypeSpecifierResult(typeSpecifierKey, ResultWithPossibleError.withTypeSpecifier(result));

        return result;
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        String text = node.getText();
        int tokenType = node.getSymbol().getType();
        if (cqlLexer.STRING == tokenType || cqlLexer.QUOTEDIDENTIFIER == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length() - 1);
        }

        return text;
    }

    @Override
    public Object visitQualifiedIdentifier(cqlParser.QualifiedIdentifierContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierContext qualifierContext : ctx.qualifier()) {
            String qualifier = (String)visit(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = parseString(ctx.identifier());
        identifiers.add(identifier);
        return identifiers;
    }

    @Override
    public Object visitQualifiedIdentifierExpression(cqlParser.QualifiedIdentifierExpressionContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierExpressionContext qualifierContext : ctx.qualifierExpression()) {
            String qualifier = (String)visit(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = parseString(ctx.referentialIdentifier());
        identifiers.add(identifier);
        return identifiers;
    }
}
