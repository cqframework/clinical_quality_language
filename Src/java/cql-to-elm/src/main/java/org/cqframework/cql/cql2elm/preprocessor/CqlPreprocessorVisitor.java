package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.model.Chunk;
import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql.model.*;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.cql_annotations.r1.Tag;
import org.hl7.elm.r1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class CqlPreprocessorVisitor extends cqlBaseVisitor {
    static final Logger logger = LoggerFactory.getLogger(CqlPreprocessorVisitor.class);
    private final ObjectFactory of = new ObjectFactory();
    private LibraryBuilder libraryBuilder;
    private LibraryInfo libraryInfo = new LibraryInfo();
    private boolean implicitContextCreated = false;
    private String currentContext = "Unfiltered";
    private int lastSourceIndex = -1;
    private TokenStream tokenStream;

    public CqlPreprocessorVisitor(LibraryBuilder theLibraryBuilder) {
        libraryBuilder = theLibraryBuilder;
    }

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
    public Object visit(ParseTree tree) {
        boolean pushedChunk = pushChunk(tree);
        Object o = null;
        try {
            // ERROR:
            try {
                o = super.visit(tree);
            } catch (CqlIncludeException e) {
                CqlCompilerException translatorException = new CqlCompilerException(e.getMessage(), getTrackBack(tree), e);
                if (translatorException.getLocator() == null) {
                    throw translatorException;
                }
                libraryBuilder.recordParsingException(translatorException);
            } catch (CqlCompilerException e) {
                if (e.getLocator() == null) {
                    if (tree == null) {
                        throw e;
                    }
                    e.setLocator(getTrackBack(tree));
                }
                libraryBuilder.recordParsingException(e);
            } catch (Exception e) {
                CqlCompilerException ex = null;
                if (e.getMessage() == null) {
                    ex = new CqlInternalException("Internal translator error.", getTrackBack(tree), e);
                    if (tree == null) {
                        throw ex;
                    }
                } else {
                    ex = new CqlSemanticException(e.getMessage(), getTrackBack(tree), e);
                }

                Exception rootCause = libraryBuilder.determineRootCause();
                if (rootCause == null) {
                    rootCause = ex;
                    libraryBuilder.recordParsingException(ex);
                    libraryBuilder.setRootCause(rootCause);
                } else {
                    if (detailedErrors) {
                        libraryBuilder.recordParsingException(ex);
                    }
                }
                o = of.createNull();
            }

            if (o instanceof Trackable && !(tree instanceof cqlParser.LibraryContext)) {
                this.track((Trackable) o, tree);
            }
            if (o instanceof Expression) {
                addExpression((Expression) o);
            }

            return o;
        } finally {
            popChunk(tree, o, pushedChunk);
            processTags(tree, o);
        }
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

    private String parseString(ParseTree pt) {
        return StringEscapeUtils.unescapeCql(pt == null ? null : (String)visit(pt));
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

        // LUKETODO: start new code
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

        Model model = getModel(modelNamespace, unqualifiedIdentifier, parseString(ctx.versionSpecifier()), localIdentifier);
        // LUKETODO: end new code

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
    public Object visitExpressionDefinition(cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDefinitionInfo expressionDefinition = new ExpressionDefinitionInfo();
        expressionDefinition.setName(parseString(ctx.identifier()));
        expressionDefinition.setContext(currentContext);
        expressionDefinition.setDefinition(ctx);
        processHeader(ctx, expressionDefinition);
        libraryInfo.addExpressionDefinition(expressionDefinition);
        return expressionDefinition;
    }

    @Override
    public Object visitFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
        final PreCompileOutput preCompileOutput = preCompile(ctx);
        FunctionDefinitionInfo functionDefinition = new FunctionDefinitionInfo();
        functionDefinition.setName(parseString(ctx.identifierOrFunctionIdentifier()));
        functionDefinition.setContext(currentContext);
        functionDefinition.setDefinition(ctx);
        functionDefinition.setPreCompileOutput(preCompileOutput);
        processHeader(ctx, functionDefinition);
        libraryInfo.addFunctionDefinition(functionDefinition);

        return functionDefinition;
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

    // LUKETODO:  start new methods
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private final Stack<Chunk> chunks = new Stack<>();
    private boolean annotate = false;
    private boolean detailedErrors = false;
    private int nextLocalId = 1;
    private boolean locate = false;
    private boolean resultTypes = false;
    private final List<Expression> expressions = new ArrayList<>();
    private boolean includeDeprecatedElements = false;
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
    public NamedTypeSpecifier visitNamedTypeSpecifier(cqlParser.NamedTypeSpecifierContext ctx) {
        List<String> qualifiers = parseQualifiers(ctx);
        String modelIdentifier = getModelIdentifier(qualifiers);
        String identifier = getTypeIdentifier(qualifiers, parseString(ctx.referentialOrTypeNameIdentifier()));

        DataType resultType = libraryBuilder.resolveTypeName(modelIdentifier, identifier);
        NamedTypeSpecifier result = of.createNamedTypeSpecifier().withName(libraryBuilder.dataTypeToQName(resultType));

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.setResultType(resultType);

        return result;
    }

    @Override
    public TupleElementDefinition visitTupleElementDefinition(cqlParser.TupleElementDefinitionContext ctx) {
        TupleElementDefinition result = of.createTupleElementDefinition()
                .withName(parseString(ctx.referentialIdentifier()))
                .withElementType(parseTypeSpecifier(ctx.typeSpecifier()));

        if (includeDeprecatedElements) {
            result.setType(result.getElementType());
        }

        return result;
    }

    @Override
    public Object visitTupleTypeSpecifier(cqlParser.TupleTypeSpecifierContext ctx) {
        TupleType resultType = new TupleType();
        TupleTypeSpecifier typeSpecifier = of.createTupleTypeSpecifier();
        for (cqlParser.TupleElementDefinitionContext definitionContext : ctx.tupleElementDefinition()) {
            TupleElementDefinition element = (TupleElementDefinition)visit(definitionContext);
            resultType.addElement(new TupleTypeElement(element.getName(), element.getElementType().getResultType()));
            typeSpecifier.getElement().add(element);
        }

        typeSpecifier.setResultType(resultType);

        return typeSpecifier;
    }

    @Override
    public ChoiceTypeSpecifier visitChoiceTypeSpecifier(cqlParser.ChoiceTypeSpecifierContext ctx) {
        ArrayList<TypeSpecifier> typeSpecifiers = new ArrayList<TypeSpecifier>();
        ArrayList<DataType> types = new ArrayList<DataType>();
        for (cqlParser.TypeSpecifierContext typeSpecifierContext : ctx.typeSpecifier()) {
            TypeSpecifier typeSpecifier = parseTypeSpecifier(typeSpecifierContext);
            typeSpecifiers.add(typeSpecifier);
            types.add(typeSpecifier.getResultType());
        }
        ChoiceTypeSpecifier result = of.createChoiceTypeSpecifier().withChoice(typeSpecifiers);
        if (includeDeprecatedElements) {
            result.getType().addAll(typeSpecifiers);
        }
        ChoiceType choiceType = new ChoiceType(types);
        result.setResultType(choiceType);
        return result;
    }

    @Override
    public IntervalTypeSpecifier visitIntervalTypeSpecifier(cqlParser.IntervalTypeSpecifierContext ctx) {
        IntervalTypeSpecifier result = of.createIntervalTypeSpecifier().withPointType(parseTypeSpecifier(ctx.typeSpecifier()));
        IntervalType intervalType = new IntervalType(result.getPointType().getResultType());
        result.setResultType(intervalType);
        return result;
    }

    @Override
    public ListTypeSpecifier visitListTypeSpecifier(cqlParser.ListTypeSpecifierContext ctx) {
        ListTypeSpecifier result = of.createListTypeSpecifier().withElementType(parseTypeSpecifier(ctx.typeSpecifier()));
        ListType listType = new ListType(result.getElementType().getResultType());
        result.setResultType(listType);
        return result;
    }

    private List<String> parseQualifiers(cqlParser.NamedTypeSpecifierContext ctx) {
        List<String> qualifiers = new ArrayList<>();
        if (ctx.qualifier() != null) {
            for (cqlParser.QualifierContext qualifierContext : ctx.qualifier()) {
                String qualifier = parseString(qualifierContext);
                qualifiers.add(qualifier);
            }
        }
        return qualifiers;
    }

    private String getModelIdentifier(List<String> qualifiers) {
        return qualifiers.size() > 0 ? qualifiers.get(0) : null;
    }

    private String getTypeIdentifier(List<String> qualifiers, String identifier) {
        if (qualifiers.size() > 1) {
            String result = null;
            for (int i = 1; i < qualifiers.size(); i++) {
                result = result == null ? qualifiers.get(i) : (result + "." + qualifiers.get(i));
            }
            return result + "." + identifier;
        }

        return identifier;
    }

    @SuppressWarnings("unchecked")
    private UsingDef visitUsingDefinitionFromElm(cqlParser.UsingDefinitionContext ctx) {
        List<String> identifiers = (List<String>) visit(ctx.qualifiedIdentifier());
        String unqualifiedIdentifier = identifiers.remove(identifiers.size() - 1);
        String namespaceName = !identifiers.isEmpty() ? String.join(".", identifiers) : libraryBuilder.isWellKnownModelName(unqualifiedIdentifier) ? null : (libraryBuilder.getNamespaceInfo() != null ? libraryBuilder.getNamespaceInfo().getName() : null);

        String path = null;
        NamespaceInfo modelNamespace = null;
        if (namespaceName != null) {
            String namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true);
            path = NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier);
            modelNamespace = new NamespaceInfo(namespaceName, namespaceUri);
        } else {
            path = unqualifiedIdentifier;
        }

        String localIdentifier = ctx.localIdentifier() == null ? unqualifiedIdentifier : parseString(ctx.localIdentifier());
        if (!localIdentifier.equals(unqualifiedIdentifier)) {
            throw new IllegalArgumentException(String.format("Local identifiers for models must be the same as the name of the model in this release of the translator (Model %s, Called %s)", unqualifiedIdentifier, localIdentifier));
        }

        Model model = getModel(modelNamespace, unqualifiedIdentifier, parseString(ctx.versionSpecifier()), localIdentifier);

        return libraryBuilder.resolveUsingRef(localIdentifier);
    }

    private PreCompileOutput preCompile(cqlParser.FunctionDefinitionContext ctx) {
        final FunctionDef fun = of.createFunctionDef().withAccessLevel(parseAccessModifier(ctx.accessModifier())).withName(parseString(ctx.identifierOrFunctionIdentifier()));

        if (ctx.fluentModifier() != null) {
            libraryBuilder.checkCompatibilityLevel("Fluent functions", "1.5");
            fun.setFluent(true);
        }

        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                TypeSpecifier typeSpecifier = parseTypeSpecifier(opdef.typeSpecifier());
                fun.getOperand().add((OperandDef) of.createOperandDef().withName(parseString(opdef.referentialIdentifier())).withOperandTypeSpecifier(typeSpecifier).withResultType(typeSpecifier.getResultType()));
            }
        }

        final cqlParser.TypeSpecifierContext typeSpecifierContext = ctx.typeSpecifier();

        // LUKETODO: I don't think this is ever non-null
        if (typeSpecifierContext != null) {
            return PreCompileOutput.withReturnType(fun, parseTypeSpecifier(typeSpecifierContext));
        }

        return PreCompileOutput.noReturnType(fun);
    }

    private Model getModel(NamespaceInfo modelNamespace, String modelName, String version, String localIdentifier) {
        if (modelName == null) {
            var defaultUsing = libraryInfo.getDefaultUsingDefinition();
            modelName = defaultUsing.getName();
            version = defaultUsing.getVersion();
        }

        var modelIdentifier = new ModelIdentifier().withId(modelName).withVersion(version);
        if (modelNamespace != null) {
            modelIdentifier.setSystem(modelNamespace.getUri());
        }
        return libraryBuilder.getModel(modelIdentifier, localIdentifier);
    }

    private AccessModifier parseAccessModifier(ParseTree pt) {
        return pt == null ? AccessModifier.PUBLIC : (AccessModifier) visit(pt);
    }

    private TypeSpecifier parseTypeSpecifier(ParseTree pt) {
        return pt == null ? null : (TypeSpecifier) visit(pt);
    }

    private boolean pushChunk(ParseTree tree) {
        if (!isAnnotationEnabled()) {
            return false;
        }

        org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();

        // An interval of i..i-1 indicates an empty interval at position i in the input stream,
        if (sourceInterval.b < sourceInterval.a) {
            return false;
        }

        Chunk chunk = new Chunk().withInterval(sourceInterval);
        chunks.push(chunk);
        return true;
    }

    public int getNextLocalId() {
        return nextLocalId++;
    }

    private void popChunk(ParseTree tree, Object o, boolean pushedChunk) {
        if (!pushedChunk) {
            return;
        }

        Chunk chunk = chunks.pop();
        if (o instanceof Element) {
            Element element = (Element) o;
            if (element.getLocalId() == null) {
                element.setLocalId(Integer.toString(getNextLocalId()));
            }
            chunk.setElement(element);

            if (!(tree instanceof cqlParser.LibraryContext)) {
                if (element instanceof UsingDef || element instanceof IncludeDef || element instanceof CodeSystemDef || element instanceof ValueSetDef || element instanceof CodeDef || element instanceof ConceptDef || element instanceof ParameterDef || element instanceof ContextDef || element instanceof ExpressionDef) {
                    Annotation a = getAnnotation(element);
                    if (a == null || a.getS() == null) {
                        // Add header information (comments prior to the definition)
                        BaseInfo definitionInfo = libraryInfo.resolveDefinition(tree);
                        if (definitionInfo != null && definitionInfo.getHeaderInterval() != null) {
                            Chunk headerChunk = new Chunk().withInterval(definitionInfo.getHeaderInterval()).withIsHeaderChunk(true);
                            Chunk newChunk = new Chunk().withInterval(new org.antlr.v4.runtime.misc.Interval(headerChunk.getInterval().a, chunk.getInterval().b));
                            newChunk.addChunk(headerChunk);
                            newChunk.setElement(chunk.getElement());
                            for (Chunk c : chunk.getChunks()) {
                                newChunk.addChunk(c);
                            }
                            chunk = newChunk;
                        }
                        if (a == null) {
                            element.getAnnotation().add(buildAnnotation(chunk));
                        } else {
                            addNarrativeToAnnotation(a, chunk);
                        }
                    }
                }
            } else {
                if (libraryInfo.getDefinition() != null && libraryInfo.getHeaderInterval() != null) {
                    Chunk headerChunk = new Chunk().withInterval(libraryInfo.getHeaderInterval()).withIsHeaderChunk(true);
                    Chunk definitionChunk = new Chunk().withInterval(libraryInfo.getDefinition().getSourceInterval());
                    Chunk newChunk = new Chunk().withInterval(new org.antlr.v4.runtime.misc.Interval(headerChunk.getInterval().a, definitionChunk.getInterval().b));
                    newChunk.addChunk(headerChunk);
                    newChunk.addChunk(definitionChunk);
                    newChunk.setElement(chunk.getElement());
                    chunk = newChunk;
                    Annotation a = getAnnotation(libraryBuilder.getLibrary());
                    if (a == null) {
                        libraryBuilder.getLibrary().getAnnotation().add(buildAnnotation(chunk));
                    } else {
                        addNarrativeToAnnotation(a, chunk);
                    }
                }
            }
        }

        if (!chunks.isEmpty()) {
            chunks.peek().addChunk(chunk);
        }
    }

    private Annotation buildAnnotation(Chunk chunk) {
        Annotation annotation = af.createAnnotation();
        annotation.setS(buildNarrative(chunk));
        return annotation;
    }

    private void addNarrativeToAnnotation(Annotation annotation, Chunk chunk) {
        annotation.setS(buildNarrative(chunk));
    }

    private Narrative buildNarrative(Chunk chunk) {
        Narrative narrative = af.createNarrative();
        if (chunk.getElement() != null) {
            narrative.setR(chunk.getElement().getLocalId());
        }

        if (chunk.hasChunks()) {
            Narrative currentNarrative = null;
            for (Chunk childChunk : chunk.getChunks()) {
                Narrative chunkNarrative = buildNarrative(childChunk);
                if (hasChunks(chunkNarrative)) {
                    if (currentNarrative != null) {
                        narrative.getContent().add(wrapNarrative(currentNarrative));
                        currentNarrative = null;
                    }
                    narrative.getContent().add(wrapNarrative(chunkNarrative));
                }
                else {
                    if (currentNarrative == null) {
                        currentNarrative = chunkNarrative;
                    }
                    else {
                        currentNarrative.getContent().addAll(chunkNarrative.getContent());
                        if (currentNarrative.getR() == null) {
                            currentNarrative.setR(chunkNarrative.getR());
                        }
                    }
                }
            }
            if (currentNarrative != null) {
                narrative.getContent().add(wrapNarrative(currentNarrative));
            }
        }
        else {
            String chunkContent = tokenStream.getText(chunk.getInterval());
            if (chunk.isHeaderChunk()) {
                chunkContent = stripLeading(chunkContent);
            }
            chunkContent = normalizeWhitespace(chunkContent);
            narrative.getContent().add(chunkContent);
        }

        return narrative;
    }

    private Serializable wrapNarrative(Narrative narrative) {
        /*
        TODO: Should be able to collapse narrative if the span doesn't have an attribute
        That's what this code is doing, but it doesn't work and I don't have time to debug it
        if (narrative.getR() == null) {
            StringBuilder content = new StringBuilder();
            boolean onlyStrings = true;
            for (Serializable s : narrative.getContent()) {
                if (s instanceof String) {
                    content.append((String)s);
                }
                else {
                    onlyStrings = false;
                }
            }
            if (onlyStrings) {
                return content.toString();
            }
        }
        */
        return new JAXBElement<>(
                new QName("urn:hl7-org:cql-annotations:r1", "s"),
                Narrative.class,
                narrative);
    }

    public boolean isAnnotationEnabled() {
        return annotate;
    }

    private TrackBack getTrackBack(ParseTree tree) {
        if (tree instanceof ParserRuleContext) {
            return getTrackBack((ParserRuleContext) tree);
        }
        if (tree instanceof TerminalNode) {
            return getTrackBack((TerminalNode) tree);
        }
        return null;
    }

    private TrackBack getTrackBack(ParserRuleContext ctx) {
        TrackBack tb = new TrackBack(
                libraryBuilder.getLibraryIdentifier(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine() + 1, // 1-based instead of 0-based
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() // 1-based instead of 0-based
        );
        return tb;
    }

    private TrackBack track(Trackable trackable, ParseTree pt) {
        TrackBack tb = getTrackBack(pt);

        if (tb != null) {
            trackable.getTrackbacks().add(tb);
        }

        if (trackable instanceof Element) {
            decorate((Element) trackable, tb);
        }

        return tb;
    }

    private void decorate(Element element, TrackBack tb) {
        if (locate && tb != null) {
            element.setLocator(tb.toLocator());
        }

        if (resultTypes && element.getResultType() != null) {
            if (element.getResultType() instanceof NamedType) {
                element.setResultTypeName(libraryBuilder.dataTypeToQName(element.getResultType()));
            } else {
                element.setResultTypeSpecifier(libraryBuilder.dataTypeToTypeSpecifier(element.getResultType()));
            }
        }
    }

    private boolean hasChunks(Narrative narrative) {
        for (Serializable c : narrative.getContent()) {
            if (!(c instanceof String)) {
                return true;
            }
        }
        return false;
    }

    private Annotation getAnnotation(Element element) {
        for (Object o : element.getAnnotation()) {
            if (o instanceof Annotation) {
                return (Annotation)o;
            }
        }

        return null;
    }

    // TODO: Should just use String.stripLeading() but that is only available in 11+
    private String stripLeading(String s) {
        int index = 0;
        while (index < s.length()) {
            if (!Character.isWhitespace(s.charAt(index))) {
                break;
            }
            index++;
        }
        if (index == s.length()) {
            return "";
        }
        return s.substring(index);
    }

    private void processTags(ParseTree tree, Object o) {
        if (libraryBuilder.isCompatibleWith("1.5")) {
            if (o instanceof Element) {
                Element element = (Element) o;
                if (!(tree instanceof cqlParser.LibraryContext)) {
                    if (element instanceof UsingDef || element instanceof IncludeDef || element instanceof CodeSystemDef || element instanceof ValueSetDef || element instanceof CodeDef || element instanceof ConceptDef || element instanceof ParameterDef || element instanceof ContextDef || element instanceof ExpressionDef) {
                        List<Tag> tags = getTags(tree);
                        if (tags != null && tags.size() > 0) {
                            Annotation a = getAnnotation(element);
                            if (a == null) {
                                a = buildAnnotation();
                                element.getAnnotation().add(a);
                            }
                            // If the definition was processed as a forward declaration, the tag processing will already have occurred
                            // and just adding tags would duplicate them here. This doesn't account for the possibility that
                            // tags would be added for some other reason, but I didn't want the overhead of checking for existing
                            // tags, and there is currently nothing that would add tags other than being processed from comments
                            if (a.getT().size() == 0) {
                                a.getT().addAll(tags);
                            }
                        }
                    }
                } else {
                    if (libraryInfo.getDefinition() != null && libraryInfo.getHeaderInterval() != null) {
                        List<Tag> tags = getTags(libraryInfo.getHeader());
                        if (tags != null && tags.size() > 0) {
                            Annotation a = getAnnotation(libraryBuilder.getLibrary());
                            if (a == null) {
                                a = buildAnnotation();
                                libraryBuilder.getLibrary().getAnnotation().add(a);
                            }
                            a.getT().addAll(tags);
                        }
                    }
                }
            }
        }
    }

    private Annotation buildAnnotation() {
        Annotation annotation = af.createAnnotation();
        return annotation;
    }

    private List<Tag> getTags(ParseTree tree) {
        BaseInfo bi = libraryInfo.resolveDefinition(tree);
        if (bi != null) {
            return getTags(bi.getHeader());
        }

        return null;
    }

    private List<Tag> getTags(String header) {
        if (header != null) {
            header = parseComments(header);
            return parseTags(header);
        }

        return null;
    }

    private String parseComments(String header) {
        List<String> result = new ArrayList<>();
        if (header != null) {
            header = header.replace("\r\n", "\n");
            String[] lines = header.split("\n");
            boolean inMultiline = false;
            for (String line : lines) {
                if (!inMultiline) {
                    int start = line.indexOf("/*");
                    if (start >= 0) {
                        if (line.endsWith("*/")) {
                            result.add(line.substring(start + 2, line.length() - 2));
                        } else {
                            result.add(line.substring(start + 2));
                        }
                        inMultiline = true;
                    }
                    else start = line.indexOf("//");
                    if (start >= 0 && !inMultiline ) {
                        result.add(line.substring(start + 2));
                    }
                }
                else {
                    int end = line.indexOf("*/");
                    if (end >= 0) {
                        inMultiline = false;
                        if (end > 0) {
                            result.add(line.substring(0, end));
                        }
                    }
                    else {
                        result.add(line);
                    }
                }
            }
        }
        return String.join("\n", result);
    }

    private List<Tag> parseTags(String header) {
        header = String.join("\n", Arrays.asList(header.trim().split("\n[ \t]*\\*[ \t\\*]*")));
        List<Tag> tags = new ArrayList<>();

        int startFrom = 0;
        while (startFrom < header.length()) {
            Pair<String, Integer> tagNamePair = lookForTagName(header, startFrom);
            if (tagNamePair != null) {
                if (tagNamePair.getLeft().length() > 0 && isValidIdentifier(tagNamePair.getLeft())) {
                    Tag t = af.createTag().withName(tagNamePair.getLeft());
                    startFrom = tagNamePair.getRight();
                    Pair<String, Integer> tagValuePair = lookForTagValue(header, startFrom);
                    if (tagValuePair != null) {
                        if (tagValuePair.getLeft().length() > 0) {
                            t = t.withValue(tagValuePair.getLeft());
                            startFrom = tagValuePair.getRight();
                        }
                    }
                    tags.add(t);
                } else {
                    startFrom = tagNamePair.getRight();
                }
            } else {  // no name tag found, no need to traverse more
                break;
            }
        }
        return tags;
    }

    // this method returns Pair<tag value, next tag name lookup index> starting from startFrom
    // can return null in cases.
    // for @1980-12-01, it will potentially check to be treated as value date
    // it looks for parameter in double quotes, e.g. @parameter: "Measurement Interval" [@2019,@2020]
    private Pair<String, Integer> lookForTagValue(String header, int startFrom) {

        if(startFrom>= header.length()) {
            return null;
        }
        int nextTag = header.indexOf('@', startFrom);
        int nextStartDoubleQuote = header.indexOf("\"", startFrom);
        if ((nextTag < 0 || nextTag > nextStartDoubleQuote) && nextStartDoubleQuote > 0 &&
                (header.length() > (nextStartDoubleQuote + 1))) {
            int nextEndDoubleQuote = header.indexOf("\"", nextStartDoubleQuote + 1);
            if (nextEndDoubleQuote > 0) {
                int parameterEnd = header.indexOf("\n", (nextStartDoubleQuote + 1));
                if (parameterEnd < 0) {
                    return Pair.of(header.substring(nextStartDoubleQuote), header.length());
                } else {
                    return Pair.of(header.substring(nextStartDoubleQuote, parameterEnd), parameterEnd);
                }
            } else {  //branch where the 2nd double quote is missing
                return Pair.of(header.substring(nextStartDoubleQuote), header.length());
            }
        }
        if(nextTag == startFrom && !isStartingWithDigit(header, nextTag +1)) {  //starts with `@` and not potential date value
            return Pair.of("",startFrom);
        } else if (nextTag > 0) {   // has some text before tag
            String interimText = header.substring(startFrom, nextTag).trim();
            if (isStartingWithDigit(header, nextTag + 1)) {  // next `@` is a date value
                if (interimText.length() > 0 && !interimText.equals(":")) {  // interim text has value, regards interim text
                    return Pair.of(interimText, nextTag);
                } else {
                    int nextSpace = header.indexOf(' ', nextTag);
                    int nextLine = header.indexOf("\n", nextTag);
                    int mul = nextSpace * nextLine;
                    int nextDelimeterIndex = header.length();

                    if (mul < 0) {
                        nextDelimeterIndex = Math.max(nextLine, nextSpace);
                    } else if(mul > 1) {
                        nextDelimeterIndex = Math.min(nextLine, nextSpace);
                    }

                    return Pair.of(header.substring(nextTag, nextDelimeterIndex), nextDelimeterIndex );
                }
            } else {   //next `@` is not date
                return Pair.of(interimText, nextTag);
            }
        }

        return Pair.of(header.substring(startFrom).trim(), header.length());
    }

    private Pair<String, Integer> lookForTagName(String header, int startFrom) {

        if(startFrom>= header.length()){
            return null;
        }
        int start = header.indexOf("@", startFrom);
        if (start < 0) {
            return null;
        }
        int nextTagStart = header.indexOf("@", start + 1);
        int nextColon = header.indexOf(":", start + 1);

        if (nextTagStart < 0) {  // no next tag , no next colon
            if (nextColon < 0) {
                return Pair.of(header.substring(start + 1, header.length()).trim(), header.length());
            }
        } else {
            if (nextColon < 0 || nextColon > nextTagStart) {  //(has next tag and no colon) or (has next tag and next colon belongs to next tag)
                return Pair.of(header.substring(start + 1, nextTagStart).trim(), nextTagStart);
            }
        }
        return Pair.of(header.substring(start + 1, nextColon).trim(), nextColon + 1);
    }

    private boolean isValidIdentifier(String tagName) {
        for (int i = 0; i < tagName.length(); i++) {
            if (tagName.charAt(i) == '_') {
                continue;
            }

            if (i == 0) {
                if (!Character.isLetter(tagName.charAt(i))) {
                    return false;
                }
            }
            else {
                if (!Character.isLetterOrDigit(tagName.charAt(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    // LUKETODO: consider moving these methods into a common library
    private String normalizeWhitespace(String input) {
        return input.replace("\r\n", "\n");
    }

    private void addExpression(Expression expression) {
        expressions.add(expression);
    }

    private boolean isStartingWithDigit(String header, int index) {
        return (index < header.length()) && Character.isDigit(header.charAt(index));
    }
    // LUKETODO:  end new methods
}
