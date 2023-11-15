package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.cql2elm.model.invocation.*;
import org.cqframework.cql.cql2elm.preprocessor.*;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.model.*;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Interval;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Cql2ElmVisitor extends CqlPreprocessorElmCommonVisitor {
    private static final Logger logger = LoggerFactory.getLogger(Cql2ElmVisitor.class);
    private final SystemMethodResolver systemMethodResolver;

    public void setLibraryInfo(LibraryInfo libraryInfo) {
        if (libraryInfo == null) {
            throw new IllegalArgumentException("libraryInfo is null");
        }
        this.libraryInfo = libraryInfo;
    }

    private final Set<String> definedExpressionDefinitions = new HashSet<>();
    private final Stack<ExpressionDefinitionInfo> forwards = new Stack<>();
    private final Map<cqlParser.FunctionDefinitionContext, FunctionHeader> functionHeaders = new HashMap<>();

    private final Map<FunctionDef, FunctionHeader> functionHeadersByDef = new HashMap<>();
    private final Map<FunctionHeader, cqlParser.FunctionDefinitionContext> functionDefinitions = new HashMap<>();
    private final Stack<TimingOperatorContext> timingOperators = new Stack<>();
    private final List<Retrieve> retrieves = new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private final Map<String, Element> contextDefinitions = new HashMap<>();

    public Cql2ElmVisitor(LibraryBuilder libraryBuilder) {
        super(libraryBuilder);

        if (libraryBuilder == null) {
            throw new IllegalArgumentException("libraryBuilder is null");
        }

        this.systemMethodResolver = new SystemMethodResolver(this, libraryBuilder);
    }

    public List<Retrieve> getRetrieves() {
        return retrieves;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public Object visitLibrary(cqlParser.LibraryContext ctx) {
        Object lastResult = null;

        // Loop through and call visit on each child (to ensure they are tracked)
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree tree = ctx.getChild(i);
            TerminalNode terminalNode = tree instanceof TerminalNode ? (TerminalNode)tree : null;
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
    }

    @Override
    @SuppressWarnings("unchecked")
    public VersionedIdentifier visitLibraryDefinition(cqlParser.LibraryDefinitionContext ctx) {
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        VersionedIdentifier vid = of.createVersionedIdentifier()
                .withId(identifiers.remove(identifiers.size() - 1))
                .withVersion(parseString(ctx.versionSpecifier()));
        if (!identifiers.isEmpty()) {
            vid.setSystem(libraryBuilder.resolveNamespaceUri(String.join(".", identifiers), true));
        }
        else if (libraryBuilder.getNamespaceInfo() != null) {
            vid.setSystem(libraryBuilder.getNamespaceInfo().getUri());
        }
        libraryBuilder.setLibraryIdentifier(vid);

        return vid;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UsingDef visitUsingDefinition(cqlParser.UsingDefinitionContext ctx) {
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        String unqualifiedIdentifier = identifiers.remove(identifiers.size() - 1);
        String namespaceName = !identifiers.isEmpty() ? String.join(".", identifiers) :
                libraryBuilder.isWellKnownModelName(unqualifiedIdentifier) ? null :
                        (libraryBuilder.getNamespaceInfo() != null ? libraryBuilder.getNamespaceInfo().getName() : null);

        String path = null;
        NamespaceInfo modelNamespace = null;
        if (namespaceName != null) {
            String namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true);
            path = NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier);
            modelNamespace = new NamespaceInfo(namespaceName, namespaceUri);
        }
        else {
            path = unqualifiedIdentifier;
        }

        String localIdentifier = ctx.localIdentifier() == null ? unqualifiedIdentifier : parseString(ctx.localIdentifier());
        if (!localIdentifier.equals(unqualifiedIdentifier)) {
            throw new IllegalArgumentException(
                    String.format("Local identifiers for models must be the same as the name of the model in this release of the translator (Model %s, Called %s)",
                            unqualifiedIdentifier, localIdentifier));
        }

        // The model was already calculated by CqlPreprocessorVisitor
        final UsingDef usingDef = libraryBuilder.resolveUsingRef(localIdentifier);
        libraryBuilder.pushIdentifierForHiding(localIdentifier, usingDef);
        return usingDef;
    }

    public Model getModel() {
        return getModel((String)null);
    }

    public Model getModel(String modelName) {
        return getModel(null, modelName, null, null);
    }

    public Model getModel(NamespaceInfo modelNamespace, String modelName, String version, String localIdentifier) {
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

    private String getLibraryPath(String namespaceName, String unqualifiedIdentifier) {
        if (namespaceName != null) {
            String namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true);
            return NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier);
        }

        return unqualifiedIdentifier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitIncludeDefinition(cqlParser.IncludeDefinitionContext ctx) {
        List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
        String unqualifiedIdentifier = identifiers.remove(identifiers.size() - 1);
        String namespaceName = !identifiers.isEmpty() ? String.join(".", identifiers) :
                (libraryBuilder.getNamespaceInfo() != null ? libraryBuilder.getNamespaceInfo().getName() : null);
        String path = getLibraryPath(namespaceName, unqualifiedIdentifier);
        IncludeDef library = of.createIncludeDef()
                .withLocalIdentifier(ctx.localIdentifier() == null ? unqualifiedIdentifier : parseString(ctx.localIdentifier()))
                .withPath(path)
                .withVersion(parseString(ctx.versionSpecifier()));

        // TODO: This isn't great because it complicates the loading process (and results in the source being loaded twice in the general case)
        // But the full fix is to introduce source resolution/caching to enable this layer to determine whether the library identifier resolved
        // with the namespace
        if (!libraryBuilder.canResolveLibrary(library)) {
            namespaceName = identifiers.size() > 0 ? String.join(".", identifiers) :
                    libraryBuilder.isWellKnownLibraryName(unqualifiedIdentifier) ? null :
                            (libraryBuilder.getNamespaceInfo() != null ? libraryBuilder.getNamespaceInfo().getName() : null);
            path = getLibraryPath(namespaceName, unqualifiedIdentifier);
            library = of.createIncludeDef()
                    .withLocalIdentifier(ctx.localIdentifier() == null ? unqualifiedIdentifier : parseString(ctx.localIdentifier()))
                    .withPath(path)
                    .withVersion(parseString(ctx.versionSpecifier()));
        }

        libraryBuilder.addInclude(library);
        libraryBuilder.pushIdentifierForHiding(library.getLocalIdentifier(), library);

        return library;
    }

    @Override
    public ParameterDef visitParameterDefinition(cqlParser.ParameterDefinitionContext ctx) {
        ParameterDef param = of.createParameterDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withDefault(parseLiteralExpression(ctx.expression()))
                .withParameterTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));

        DataType paramType = null;
        if (param.getParameterTypeSpecifier() != null) {
            paramType = param.getParameterTypeSpecifier().getResultType();
        }

        if (param.getDefault() != null) {
            if (paramType != null) {
                libraryBuilder.verifyType(param.getDefault().getResultType(), paramType);
            }
            else {
                paramType = param.getDefault().getResultType();
            }
        }

        if (paramType == null) {
            throw new IllegalArgumentException(String.format("Could not determine parameter type for parameter %s.", param.getName()));
        }

        param.setResultType(paramType);
        if (param.getDefault() != null) {
            param.setDefault(libraryBuilder.ensureCompatible(param.getDefault(), paramType));
        }

        libraryBuilder.addParameter(param);
        libraryBuilder.pushIdentifierForHiding(param.getName(), param);

        return param;
    }

    @Override
    public TupleElementDefinition visitTupleElementDefinition(cqlParser.TupleElementDefinitionContext ctx) {
        TupleElementDefinition result = of.createTupleElementDefinition()
                .withName(parseString(ctx.referentialIdentifier()))
                .withElementType(parseTypeSpecifier(ctx.typeSpecifier()));

        if (getIncludeDeprecatedElements()) {
            result.setType(result.getElementType());
        }

        return result;
    }

    @Override
    public AccessModifier visitAccessModifier(cqlParser.AccessModifierContext ctx) {
        switch (ctx.getText().toLowerCase()) {
            case "public" : return AccessModifier.PUBLIC;
            case "private" : return AccessModifier.PRIVATE;
            default: throw new IllegalArgumentException(String.format("Unknown access modifier %s.", ctx.getText().toLowerCase()));
        }
    }

    @Override
    public CodeSystemDef visitCodesystemDefinition(cqlParser.CodesystemDefinitionContext ctx) {
        CodeSystemDef cs = (CodeSystemDef)of.createCodeSystemDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codesystemId()))
                .withVersion(parseString(ctx.versionSpecifier()));

        if (libraryBuilder.isCompatibleWith("1.5")) {
            cs.setResultType(libraryBuilder.resolveTypeName("System", "CodeSystem"));
        }
        else {
            cs.setResultType(new ListType(libraryBuilder.resolveTypeName("System", "Code")));
        }

        libraryBuilder.addCodeSystem(cs);
        libraryBuilder.pushIdentifierForHiding(cs.getName(), cs);
        return cs;
    }

    @Override
    public CodeSystemRef visitCodesystemIdentifier(cqlParser.CodesystemIdentifierContext ctx) {
        String libraryName = parseString(ctx.libraryIdentifier());
        String name = parseString(ctx.identifier());
        CodeSystemDef def;
        if (libraryName != null) {
            def = libraryBuilder.resolveLibrary(libraryName).resolveCodeSystemRef(name);
            libraryBuilder.checkAccessLevel(libraryName, name, def.getAccessLevel());
        }
        else {
            def = libraryBuilder.resolveCodeSystemRef(name);
        }

        if (def == null) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve reference to code system %s.", name));
        }

        return (CodeSystemRef)of.createCodeSystemRef()
                .withLibraryName(libraryName)
                .withName(name)
                .withResultType(def.getResultType());
    }

    @Override
    public CodeRef visitCodeIdentifier(cqlParser.CodeIdentifierContext ctx) {
        String libraryName = parseString(ctx.libraryIdentifier());
        String name = parseString(ctx.identifier());
        CodeDef def;
        if (libraryName != null) {
            def = libraryBuilder.resolveLibrary(libraryName).resolveCodeRef(name);
            libraryBuilder.checkAccessLevel(libraryName, name, def.getAccessLevel());
        }
        else {
            def = libraryBuilder.resolveCodeRef(name);
        }

        if (def == null) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve reference to code %s.", name));
        }

        return (CodeRef)of.createCodeRef()
                .withLibraryName(libraryName)
                .withName(name)
                .withResultType(def.getResultType());
    }

    @Override
    public ValueSetDef visitValuesetDefinition(cqlParser.ValuesetDefinitionContext ctx) {
        ValueSetDef vs = of.createValueSetDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.valuesetId()))
                .withVersion(parseString(ctx.versionSpecifier()));

        if (ctx.codesystems() != null) {
            for (cqlParser.CodesystemIdentifierContext codesystem : ctx.codesystems().codesystemIdentifier()) {
                vs.getCodeSystem().add((CodeSystemRef)visit(codesystem));
            }
        }
        if (libraryBuilder.isCompatibleWith("1.5")) {
            vs.setResultType(libraryBuilder.resolveTypeName("System", "ValueSet"));
        }
        else {
            vs.setResultType(new ListType(libraryBuilder.resolveTypeName("System", "Code")));
        }
        libraryBuilder.addValueSet(vs);
        libraryBuilder.pushIdentifierForHiding(vs.getName(), vs);

        return vs;
    }

    @Override
    public CodeDef visitCodeDefinition(cqlParser.CodeDefinitionContext ctx) {
        CodeDef cd = of.createCodeDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codeId()));

        if (ctx.codesystemIdentifier() != null) {
            cd.setCodeSystem((CodeSystemRef)visit(ctx.codesystemIdentifier()));
        }

        if (ctx.displayClause() != null) {
            cd.setDisplay(parseString(ctx.displayClause().STRING()));
        }

        cd.setResultType(libraryBuilder.resolveTypeName("Code"));
        libraryBuilder.addCode(cd);
        libraryBuilder.pushIdentifierForHiding(cd.getName(), cd);

        return cd;
    }

    @Override
    public ConceptDef visitConceptDefinition(cqlParser.ConceptDefinitionContext ctx) {
        ConceptDef cd = of.createConceptDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()));

        if (ctx.codeIdentifier() != null) {
            for (cqlParser.CodeIdentifierContext ci : ctx.codeIdentifier()) {
                cd.getCode().add((CodeRef)visit(ci));
            }
        }

        if (ctx.displayClause() != null) {
            cd.setDisplay(parseString(ctx.displayClause().STRING()));
        }

        cd.setResultType(libraryBuilder.resolveTypeName("Concept"));
        libraryBuilder.addConcept(cd);

        return cd;
    }

    @Override
    public NamedTypeSpecifier visitNamedTypeSpecifier(cqlParser.NamedTypeSpecifierContext ctx) {
        List<String> qualifiers = parseQualifiers(ctx);
        String modelIdentifier = getModelIdentifier(qualifiers);
        String identifier = getTypeIdentifier(qualifiers, parseString(ctx.referentialOrTypeNameIdentifier()));

        final ResultWithPossibleError<NamedTypeSpecifier > retrievedResult = libraryBuilder.getNamedTypeSpecifierResult(String.format("%s:%s", modelIdentifier, identifier));

        if (retrievedResult != null) {
            if (retrievedResult.hasError()) {
                return null;
            }
            return retrievedResult.getUnderlyingResultIfExists();
        }

        DataType resultType = libraryBuilder.resolveTypeName(modelIdentifier, identifier);
        if (null == resultType) {
            throw new CqlCompilerException(String.format("Could not find type for model: %s and name: %s", modelIdentifier, identifier));
        }
        NamedTypeSpecifier result = of.createNamedTypeSpecifier()
                .withName(libraryBuilder.dataTypeToQName(resultType));

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.setResultType(resultType);

        return result;
    }

    private boolean isUnfilteredContext(String contextName) {
        return contextName.equals("Unfiltered") || (libraryBuilder.isCompatibilityLevel3() && contextName.equals("Population"));
    }

    @Override
    public Object visitContextDefinition(cqlParser.ContextDefinitionContext ctx) {
        String modelIdentifier = parseString(ctx.modelIdentifier());
        String unqualifiedIdentifier = parseString(ctx.identifier());

        setCurrentContext(modelIdentifier != null ? modelIdentifier + "." + unqualifiedIdentifier : unqualifiedIdentifier);

        if (!isUnfilteredContext(unqualifiedIdentifier)) {
            ModelContext modelContext = libraryBuilder.resolveContextName(modelIdentifier, unqualifiedIdentifier);

            // If this is the first time a context definition is encountered, construct a context definition:
            // define <Context> = element of [<Context model type>]
            Element modelContextDefinition = contextDefinitions.get(modelContext.getName());
            if (modelContextDefinition == null) {
                if (libraryBuilder.hasUsings()) {
                    ModelInfo modelInfo = modelIdentifier == null
                            ? libraryBuilder.getModel(libraryInfo.getDefaultModelName()).getModelInfo()
                            : libraryBuilder.getModel(modelIdentifier).getModelInfo();
                    //String contextTypeName = modelContext.getName();
                    //DataType contextType = libraryBuilder.resolveTypeName(modelInfo.getName(), contextTypeName);
                    DataType contextType = modelContext.getType();
                    modelContextDefinition = libraryBuilder.resolveParameterRef(modelContext.getName());
                    if (modelContextDefinition != null) {
                        contextDefinitions.put(modelContext.getName(), modelContextDefinition);
                    }
                    else {
                        Retrieve contextRetrieve = of.createRetrieve().withDataType(libraryBuilder.dataTypeToQName(contextType));
                        track(contextRetrieve, ctx);
                        contextRetrieve.setResultType(new ListType(contextType));
                        String contextClassIdentifier = ((ClassType) contextType).getIdentifier();
                        if (contextClassIdentifier != null) {
                            contextRetrieve.setTemplateId(contextClassIdentifier);
                        }

                        modelContextDefinition = of.createExpressionDef()
                                .withName(unqualifiedIdentifier)
                                .withContext(getCurrentContext())
                                .withExpression(of.createSingletonFrom().withOperand(contextRetrieve));
                        track(modelContextDefinition, ctx);
                        ((ExpressionDef)modelContextDefinition).getExpression().setResultType(contextType);
                        modelContextDefinition.setResultType(contextType);
                        libraryBuilder.addExpression((ExpressionDef)modelContextDefinition);
                        contextDefinitions.put(modelContext.getName(), modelContextDefinition);
                    }
                }
                else {
                    modelContextDefinition = of.createExpressionDef()
                            .withName(unqualifiedIdentifier)
                            .withContext(getCurrentContext())
                            .withExpression(of.createNull());
                    track(modelContextDefinition, ctx);
                    ((ExpressionDef)modelContextDefinition).getExpression().setResultType(libraryBuilder.resolveTypeName("System", "Any"));
                    modelContextDefinition.setResultType(((ExpressionDef)modelContextDefinition).getExpression().getResultType());
                    libraryBuilder.addExpression((ExpressionDef)modelContextDefinition);
                    contextDefinitions.put(modelContext.getName(), modelContextDefinition);
                }
            }
        }

        ContextDef contextDef = of.createContextDef().withName(getCurrentContext());
        track(contextDef, ctx);
        if (libraryBuilder.isCompatibleWith("1.5")) {
            libraryBuilder.addContext(contextDef);
        }

        return getCurrentContext();
    }

    private boolean isImplicitContextExpressionDef(ExpressionDef def) {
        for (Element e : contextDefinitions.values()) {
            if (def == e) {
                return true;
            }
        }

        return false;
    }

    private void removeImplicitContextExpressionDef(ExpressionDef def) {
        for (Map.Entry<String, Element> e : contextDefinitions.entrySet()) {
            if (def == e.getValue()) {
                contextDefinitions.remove(e.getKey());
                break;
            }
        }
    }

    public ExpressionDef internalVisitExpressionDefinition(cqlParser.ExpressionDefinitionContext ctx) {
        String identifier = parseString(ctx.identifier());
        ExpressionDef def = libraryBuilder.resolveExpressionRef(identifier);

        // lightweight ExpressionDef to be used to output a hiding warning message
        final ExpressionDef hollowExpressionDef = of.createExpressionDef()
                .withName(identifier)
                .withContext(getCurrentContext());
        libraryBuilder.pushIdentifierForHiding(identifier, hollowExpressionDef);
        if (def == null || isImplicitContextExpressionDef(def)) {
            if (def != null && isImplicitContextExpressionDef(def)) {
                libraryBuilder.removeExpression(def);
                removeImplicitContextExpressionDef(def);
                def = null;
            }
            libraryBuilder.pushExpressionContext(getCurrentContext());
            try {
                libraryBuilder.pushExpressionDefinition(identifier);
                try {
                    def = of.createExpressionDef()
                            .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                            .withName(identifier)
                            .withContext(getCurrentContext())
                            .withExpression((Expression) visit(ctx.expression()));
                    if (def.getExpression() != null) {
                        def.setResultType(def.getExpression().getResultType());
                    }
                    libraryBuilder.addExpression(def);
                } finally {
                    libraryBuilder.popExpressionDefinition();
                }
            } finally {
                libraryBuilder.popExpressionContext();
            }
        }

        return def;
    }

    @Override
    public ExpressionDef visitExpressionDefinition(cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDef expressionDef = internalVisitExpressionDefinition(ctx);
        if (forwards.isEmpty() || !forwards.peek().getName().equals(expressionDef.getName())) {
            if (definedExpressionDefinitions.contains(expressionDef.getName())) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Identifier %s is already in use in this library.", expressionDef.getName()));
            }

            // Track defined expression definitions locally, otherwise duplicate expression definitions will be missed because they are
            // overwritten by name when they are encountered by the preprocessor.
            definedExpressionDefinitions.add(expressionDef.getName());
        }
        return expressionDef;
    }

    @Override
    public Literal visitStringLiteral(cqlParser.StringLiteralContext ctx) {
        final Literal stringLiteral = libraryBuilder.createLiteral(parseString(ctx.STRING()));
        libraryBuilder.pushIdentifierForHiding(stringLiteral.getValue(), stringLiteral);
        return stringLiteral;
    }

    @Override
    public Literal visitSimpleStringLiteral(cqlParser.SimpleStringLiteralContext ctx) {
        return libraryBuilder.createLiteral(parseString(ctx.STRING()));
    }

    @Override
    public Literal visitBooleanLiteral(cqlParser.BooleanLiteralContext ctx) {
        return libraryBuilder.createLiteral(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Object visitIntervalSelector(cqlParser.IntervalSelectorContext ctx) {
        return libraryBuilder.createInterval(parseExpression(ctx.expression(0)), ctx.getChild(1).getText().equals("["),
                parseExpression(ctx.expression(1)), ctx.getChild(5).getText().equals("]"));
    }

    @Override
    public Object visitTupleElementSelector(cqlParser.TupleElementSelectorContext ctx) {
        TupleElement result = of.createTupleElement()
                .withName(parseString(ctx.referentialIdentifier()))
                .withValue(parseExpression(ctx.expression()));
        result.setResultType(result.getValue().getResultType());
        return result;
    }

    @Override
    public Object visitTupleSelector(cqlParser.TupleSelectorContext ctx) {
        Tuple tuple = of.createTuple();
        TupleType tupleType = new TupleType();
        for (cqlParser.TupleElementSelectorContext elementContext : ctx.tupleElementSelector()) {
            TupleElement element = (TupleElement)visit(elementContext);
            tupleType.addElement(new TupleTypeElement(element.getName(), element.getResultType()));
            tuple.getElement().add(element);
        }
        tuple.setResultType(tupleType);
        return tuple;
    }

    @Override
    public Object visitInstanceElementSelector(cqlParser.InstanceElementSelectorContext ctx) {
        InstanceElement result = of.createInstanceElement()
                .withName(parseString(ctx.referentialIdentifier()))
                .withValue(parseExpression(ctx.expression()));
        result.setResultType(result.getValue().getResultType());
        return result;
    }

    @Override
    public Object visitInstanceSelector(cqlParser.InstanceSelectorContext ctx) {
        Instance instance = of.createInstance();
        NamedTypeSpecifier classTypeSpecifier = (NamedTypeSpecifier)visitNamedTypeSpecifier(ctx.namedTypeSpecifier());
        instance.setClassType(classTypeSpecifier.getName());
        instance.setResultType(classTypeSpecifier.getResultType());

        for (cqlParser.InstanceElementSelectorContext elementContext : ctx.instanceElementSelector()) {
            InstanceElement element = (InstanceElement)visit(elementContext);
            PropertyResolution resolution = libraryBuilder.resolveProperty(classTypeSpecifier.getResultType(), element.getName());
            element.setValue(libraryBuilder.ensureCompatible(element.getValue(), resolution.getType()));
            element.setName(resolution.getName());
            if (resolution.getTargetMap() != null) {
                // TODO: Target mapping in instance selectors
                throw new IllegalArgumentException("Target Mapping in instance selectors not yet supported");
            }
            instance.getElement().add(element);
        }

        return instance;
    }

    @Override
    public Object visitCodeSelector(cqlParser.CodeSelectorContext ctx) {
        Code code = of.createCode();
        code.setCode(parseString(ctx.STRING()));
        code.setSystem((CodeSystemRef)visit(ctx.codesystemIdentifier()));
        if (ctx.displayClause() != null) {
            code.setDisplay(parseString(ctx.displayClause().STRING()));
        }

        code.setResultType(libraryBuilder.resolveTypeName("System", "Code"));
        return code;
    }

    @Override
    public Object visitConceptSelector(cqlParser.ConceptSelectorContext ctx) {
        Concept concept = of.createConcept();
        if (ctx.displayClause() != null) {
            concept.setDisplay(parseString(ctx.displayClause().STRING()));
        }

        for (cqlParser.CodeSelectorContext codeContext : ctx.codeSelector()) {
            concept.getCode().add((Code)visit(codeContext));
        }

        concept.setResultType(libraryBuilder.resolveTypeName("System", "Concept"));
        return concept;
    }

    @Override
    public Object visitListSelector(cqlParser.ListSelectorContext ctx) {
        TypeSpecifier elementTypeSpecifier = parseTypeSpecifier(ctx.typeSpecifier());
        org.hl7.elm.r1.List list = of.createList();
        ListType listType = null;
        if (elementTypeSpecifier != null) {
            ListTypeSpecifier listTypeSpecifier = of.createListTypeSpecifier().withElementType(elementTypeSpecifier);
            track(listTypeSpecifier, ctx.typeSpecifier());
            listType = new ListType(elementTypeSpecifier.getResultType());
            listTypeSpecifier.setResultType(listType);
        }

        DataType elementType = elementTypeSpecifier != null ? elementTypeSpecifier.getResultType() : null;
        DataType inferredElementType = null;

        List<Expression> elements = new ArrayList<>();
        for (cqlParser.ExpressionContext elementContext : ctx.expression()) {
            Expression element = parseExpression(elementContext);

            if (elementType != null) {
                libraryBuilder.verifyType(element.getResultType(), elementType);
            }
            else {
                if (inferredElementType == null) {
                    inferredElementType = element.getResultType();
                }
                else {
                    DataType compatibleType = libraryBuilder.findCompatibleType(inferredElementType, element.getResultType());
                    if (compatibleType != null) {
                        inferredElementType = compatibleType;
                    }
                    else {
                        inferredElementType = libraryBuilder.resolveTypeName("System", "Any");
                    }
                }
            }

            elements.add(element);
        }

        if (elementType == null) {
            elementType = inferredElementType == null ? libraryBuilder.resolveTypeName("System", "Any") : inferredElementType;
        }

        for (Expression element : elements) {
            if (!elementType.isSuperTypeOf(element.getResultType())) {
                Conversion conversion = libraryBuilder.findConversion(element.getResultType(), elementType, true, false);
                if (conversion != null) {
                    list.getElement().add(libraryBuilder.convertExpression(element, conversion));
                }
                else {
                    list.getElement().add(element);
                }
            }
            else {
                list.getElement().add(element);
            }
        }

        if (listType == null) {
            listType = new ListType(elementType);
        }

        list.setResultType(listType);
        return list;
    }

    @Override
    public Object visitTimeLiteral(cqlParser.TimeLiteralContext ctx) {
        String input = ctx.getText();
        if (input.startsWith("@")) {
            input = input.substring(1);
        }

        Pattern timePattern =
                Pattern.compile("T(\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?");
                               //-1-------2---3-------4---5-------6---7-----------

        Matcher matcher = timePattern.matcher(input);
        if (matcher.matches()) {
            try {
                Time result = of.createTime();
                int hour = Integer.parseInt(matcher.group(1));
                int minute = -1;
                int second = -1;
                int millisecond = -1;
                if (hour < 0 || hour > 24) {
                    throw new IllegalArgumentException(String.format("Invalid hour in time literal (%s).", input));
                }
                result.setHour(libraryBuilder.createLiteral(hour));

                if (matcher.group(3) != null) {
                    minute = Integer.parseInt(matcher.group(3));
                    if (minute < 0 || minute >= 60 || (hour == 24 && minute > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid minute in time literal (%s).", input));
                    }
                    result.setMinute(libraryBuilder.createLiteral(minute));
                }

                if (matcher.group(5) != null) {
                    second = Integer.parseInt(matcher.group(5));
                    if (second < 0 || second >= 60 || (hour == 24 && second > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid second in time literal (%s).", input));
                    }
                    result.setSecond(libraryBuilder.createLiteral(second));
                }

                if (matcher.group(7) != null) {
                    millisecond = Integer.parseInt(matcher.group(7));
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid millisecond in time literal (%s).", input));
                    }
                    result.setMillisecond(libraryBuilder.createLiteral(millisecond));
                }

                result.setResultType(libraryBuilder.resolveTypeName("System", "Time"));
                return result;
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException(String.format("Invalid time input (%s). Use ISO 8601 time representation (hh:mm:ss.fff).", input), e);
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Invalid time input (%s). Use ISO 8601 time representation (hh:mm:ss.fff).", input));
        }
    }

    private Expression parseDateTimeLiteral(String input) {
/*
DATETIME
        : '@'
            [0-9][0-9][0-9][0-9] // year
            (
                (
                    '-'[0-9][0-9] // month
                    (
                        (
                            '-'[0-9][0-9] // day
                            ('T' TIMEFORMAT?)?
                        )
                        | 'T'
                    )?
                )
                | 'T'
            )?
            ('Z' | ('+' | '-') [0-9][0-9]':'[0-9][0-9])? // timezone offset
        ;
*/

        Pattern dateTimePattern =
                Pattern.compile("(\\d{4})(((-(\\d{2}))(((-(\\d{2}))((T)((\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?)?)?)|(T))?)|(T))?((Z)|(([+-])(\\d{2})(\\:(\\d{2}))))?");
        //1-------234-5--------678-9--------11--11-------1---1-------1---1-------1---1-----------------2------2----22---22-----2-------2---2-----------
        //----------------------------------01--23-------4---5-------6---7-------8---9-----------------0------1----23---45-----6-------7---8-----------

        /*
            year - group 1
            month - group 5
            day - group 9
            day dateTime indicator - group 11
            hour - group 13
            minute - group 15
            second - group 17
            millisecond - group 19
            month dateTime indicator - group 20
            year dateTime indicator - group 21
            utc indicator - group 23
            timezone offset polarity - group 25
            timezone offset hour - group 26
            timezone offset minute - group 28
         */

/*
        Pattern dateTimePattern =
                Pattern.compile("(\\d{4})(-(\\d{2}))?(-(\\d{2}))?((Z)|(T((\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?)?((Z)|(([+-])(\\d{2})(\\:?(\\d{2}))?))?))?");
                               //1-------2-3---------4-5---------67---8-91-------1---1-------1---1-------1---1-------------11---12-----2-------2----2---------------
                               //----------------------------------------0-------1---2-------3---4-------5---6-------------78---90-----1-------2----3---------------
*/

        Matcher matcher = dateTimePattern.matcher(input);
        if (matcher.matches()) {
            try {
                GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
                DateTime result = of.createDateTime();
                int year = Integer.parseInt(matcher.group(1));
                int month = -1;
                int day = -1;
                int hour = -1;
                int minute = -1;
                int second = -1;
                int millisecond = -1;
                result.setYear(libraryBuilder.createLiteral(year));
                if (matcher.group(5) != null) {
                    month = Integer.parseInt(matcher.group(5));
                    if (month < 0 || month > 12) {
                        throw new IllegalArgumentException(String.format("Invalid month in date/time literal (%s).", input));
                    }
                    result.setMonth(libraryBuilder.createLiteral(month));
                }

                if (matcher.group(9) != null) {
                    day = Integer.parseInt(matcher.group(9));
                    int maxDay = 31;
                    switch (month) {
                        case 2: maxDay = calendar.isLeapYear(year) ? 29 : 28;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11: maxDay = 30;
                            break;
                        default:
                            break;
                    }

                    if (day < 0 || day > maxDay) {
                        throw new IllegalArgumentException(String.format("Invalid day in date/time literal (%s).", input));
                    }

                    result.setDay(libraryBuilder.createLiteral(day));
                }

                if (matcher.group(13) != null) {
                    hour = Integer.parseInt(matcher.group(13));
                    if (hour < 0 || hour > 24) {
                        throw new IllegalArgumentException(String.format("Invalid hour in date/time literal (%s).", input));
                    }
                    result.setHour(libraryBuilder.createLiteral(hour));
                }

                if (matcher.group(15) != null) {
                    minute = Integer.parseInt(matcher.group(15));
                    if (minute < 0 || minute >= 60 || (hour == 24 && minute > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid minute in date/time literal (%s).", input));
                    }
                    result.setMinute(libraryBuilder.createLiteral(minute));
                }

                if (matcher.group(17) != null) {
                    second = Integer.parseInt(matcher.group(17));
                    if (second < 0 || second >= 60 || (hour == 24 && second > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid second in date/time literal (%s).", input));
                    }
                    result.setSecond(libraryBuilder.createLiteral(second));
                }

                if (matcher.group(19) != null) {
                    millisecond = Integer.parseInt(matcher.group(19));
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid millisecond in date/time literal (%s).", input));
                    }
                    result.setMillisecond(libraryBuilder.createLiteral(millisecond));
                }

                if (matcher.group(23) != null && matcher.group(23).equals("Z")) {
                    result.setTimezoneOffset(libraryBuilder.createLiteral(0.0));
                }

                if (matcher.group(25) != null) {
                    int offsetPolarity = matcher.group(25).equals("+") ? 1 : -1;

                    if (matcher.group(28) != null) {
                        int hourOffset = Integer.parseInt(matcher.group(26));
                        if (hourOffset < 0 || hourOffset > 14) {
                            throw new IllegalArgumentException(String.format("Timezone hour offset is out of range in date/time literal (%s).", input));
                        }

                        int minuteOffset = Integer.parseInt(matcher.group(28));
                        if (minuteOffset < 0 || minuteOffset >= 60 || (hourOffset == 14 && minuteOffset > 0)) {
                            throw new IllegalArgumentException(String.format("Timezone minute offset is out of range in date/time literal (%s).", input));
                        }

                        result.setTimezoneOffset(libraryBuilder.createLiteral((double)(hourOffset + (minuteOffset / 60)) * offsetPolarity));
                    }
                    else {
                        if (matcher.group(26) != null) {
                            int hourOffset = Integer.parseInt(matcher.group(26));
                            if (hourOffset < 0 || hourOffset > 14) {
                                throw new IllegalArgumentException(String.format("Timezone hour offset is out of range in date/time literal (%s).", input));
                            }

                            result.setTimezoneOffset(libraryBuilder.createLiteral((double)(hourOffset * offsetPolarity)));
                        }
                    }
                }

                if (result.getHour() == null && matcher.group(11) == null && matcher.group(20) == null && matcher.group(21) == null) {
                    org.hl7.elm.r1.Date date = of.createDate();
                    date.setYear(result.getYear());
                    date.setMonth(result.getMonth());
                    date.setDay(result.getDay());
                    date.setResultType(libraryBuilder.resolveTypeName("System", "Date"));
                    return date;
                }

                result.setResultType(libraryBuilder.resolveTypeName("System", "DateTime"));
                return result;
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException(String.format("Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|(+/-hh:mm)).", input), e);
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|+/-hh:mm)).", input));
        }
    }

    @Override
    public Object visitDateLiteral(cqlParser.DateLiteralContext ctx) {
        String input = ctx.getText();
        if (input.startsWith("@")) {
            input = input.substring(1);
        }

        return parseDateTimeLiteral(input);
    }

    @Override
    public Object visitDateTimeLiteral(cqlParser.DateTimeLiteralContext ctx) {
        String input = ctx.getText();
        if (input.startsWith("@")) {
            input = input.substring(1);
        }

        return parseDateTimeLiteral(input);
    }

    @Override
    public Null visitNullLiteral(cqlParser.NullLiteralContext ctx) {
        Null result = of.createNull();
        result.setResultType(libraryBuilder.resolveTypeName("System", "Any"));
        return result;
    }

    @Override
    public Expression visitNumberLiteral(cqlParser.NumberLiteralContext ctx) {
        return libraryBuilder.createNumberLiteral(ctx.NUMBER().getText());
    }

    @Override
    public Expression visitSimpleNumberLiteral(cqlParser.SimpleNumberLiteralContext ctx) {
        return libraryBuilder.createNumberLiteral(ctx.NUMBER().getText());
    }

    @Override
    public Literal visitLongNumberLiteral(cqlParser.LongNumberLiteralContext ctx) {
        String input = ctx.LONGNUMBER().getText();
        if (input.endsWith("L")) {
            input = input.substring(0, input.length() - 1);
        }
        return libraryBuilder.createLongNumberLiteral(input);
    }

    private BigDecimal parseDecimal(String value) {
        try {
            BigDecimal result = new BigDecimal(value);
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Could not parse number literal: %s", value, e));
        }
    }

    @Override
    public Expression visitQuantity(cqlParser.QuantityContext ctx) {
        if (ctx.unit() != null) {
            Quantity result = libraryBuilder.createQuantity(parseDecimal(ctx.NUMBER().getText()), parseString(ctx.unit()));
            return result;
        } else {
            return libraryBuilder.createNumberLiteral(ctx.NUMBER().getText());
        }
    }

    private Quantity getQuantity(Expression source) {
        if (source instanceof Literal) {
            return libraryBuilder.createQuantity(parseDecimal(((Literal)source).getValue()), "1");
        }
        else if (source instanceof Quantity) {
            return (Quantity)source;
        }

        throw new IllegalArgumentException("Could not create quantity from source expression.");
    }

    @Override
    public Expression visitRatio(cqlParser.RatioContext ctx) {
        Quantity numerator = getQuantity((Expression)visit(ctx.quantity(0)));
        Quantity denominator = getQuantity((Expression)visit(ctx.quantity(1)));
        return libraryBuilder.createRatio(numerator, denominator);
    }

    @Override
    public Not visitNotExpression(cqlParser.NotExpressionContext ctx) {
        Not result = of.createNot().withOperand(parseExpression(ctx.expression()));
        libraryBuilder.resolveUnaryCall("System", "Not", result);
        return result;
    }

    @Override
    public Exists visitExistenceExpression(cqlParser.ExistenceExpressionContext ctx) {
        Exists result = of.createExists().withOperand(parseExpression(ctx.expression()));
        libraryBuilder.resolveUnaryCall("System", "Exists", result);
        return result;
    }

    @Override
    public BinaryExpression visitMultiplicationExpressionTerm(cqlParser.MultiplicationExpressionTermContext ctx) {
        BinaryExpression exp = null;
        String operatorName = null;
        switch (ctx.getChild(1).getText()) {
            case "*":
                exp = of.createMultiply();
                operatorName = "Multiply";
                break;
            case "/":
                exp = of.createDivide();
                operatorName = "Divide";
                break;
            case "div":
                exp = of.createTruncatedDivide();
                operatorName = "TruncatedDivide";
                break;
            case "mod":
                exp = of.createModulo();
                operatorName = "Modulo";
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported operator: %s.", ctx.getChild(1).getText()));
        }

        exp.withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", operatorName, exp);

        return exp;
    }

    @Override
    public Power visitPowerExpressionTerm(cqlParser.PowerExpressionTermContext ctx) {
        Power power = of.createPower().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", "Power", power);

        return power;
    }

    @Override
    public Object visitPolarityExpressionTerm(cqlParser.PolarityExpressionTermContext ctx) {
        if (ctx.getChild(0).getText().equals("+")) {
            return visit(ctx.expressionTerm());
        }

        Negate result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()));
        libraryBuilder.resolveUnaryCall("System", "Negate", result);
        return result;
    }

    @Override
    public Expression visitAdditionExpressionTerm(cqlParser.AdditionExpressionTermContext ctx) {
        Expression exp = null;
        String operatorName = null;
        switch (ctx.getChild(1).getText()) {
            case "+":
                exp = of.createAdd();
                operatorName = "Add";
                break;
            case "-":
                exp = of.createSubtract();
                operatorName = "Subtract";
                break;
            case "&":
                exp = of.createConcatenate();
                operatorName = "Concatenate";
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported operator: %s.", ctx.getChild(1).getText()));
        }

        if (exp instanceof BinaryExpression) {
            ((BinaryExpression)exp).withOperand(
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1)));

            libraryBuilder.resolveBinaryCall("System", operatorName, (BinaryExpression)exp);

            if (exp.getResultType() == libraryBuilder.resolveTypeName("System", "String")) {
                Concatenate concatenate = of.createConcatenate();
                concatenate.getOperand().addAll(((BinaryExpression)exp).getOperand());
                concatenate.setResultType(exp.getResultType());
                exp = concatenate;
            }
        }
        else {
            Concatenate concatenate = (Concatenate)exp;
            concatenate.withOperand(
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1)));

            for (int i = 0; i < concatenate.getOperand().size(); i++) {
                Expression operand = concatenate.getOperand().get(i);
                Literal empty = libraryBuilder.createLiteral("");
                ArrayList<Expression> params = new ArrayList<Expression>();
                params.add(operand);
                params.add(empty);
                Expression coalesce = libraryBuilder.resolveFunction("System", "Coalesce", params);
                concatenate.getOperand().set(i, coalesce);
            }
            libraryBuilder.resolveNaryCall("System", operatorName, concatenate);
        }
        return exp;
    }

    @Override
    public Object visitPredecessorExpressionTerm(cqlParser.PredecessorExpressionTermContext ctx) {
        return libraryBuilder.buildPredecessor(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Object visitSuccessorExpressionTerm(cqlParser.SuccessorExpressionTermContext ctx) {
        return libraryBuilder.buildSuccessor(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Object visitElementExtractorExpressionTerm(cqlParser.ElementExtractorExpressionTermContext ctx) {
        SingletonFrom result = of.createSingletonFrom().withOperand(parseExpression(ctx.expressionTerm()));

        libraryBuilder.resolveUnaryCall("System", "SingletonFrom", result);
        return result;
    }

    @Override
    public Object visitPointExtractorExpressionTerm(cqlParser.PointExtractorExpressionTermContext ctx) {
        PointFrom result = of.createPointFrom().withOperand(parseExpression(ctx.expressionTerm()));

        libraryBuilder.resolveUnaryCall("System", "PointFrom", result);
        return result;
    }

    @Override
    public Object visitTypeExtentExpressionTerm(cqlParser.TypeExtentExpressionTermContext ctx) {
        String extent = parseString(ctx.getChild(0));
        TypeSpecifier targetType = parseTypeSpecifier(ctx.namedTypeSpecifier());
        switch (extent) {
            case "minimum": {
                return libraryBuilder.buildMinimum(targetType.getResultType());
            }

            case "maximum": {
                return libraryBuilder.buildMaximum(targetType.getResultType());
            }

            default: throw new IllegalArgumentException(String.format("Unknown extent: %s", extent));
        }
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(cqlParser.TimeBoundaryExpressionTermContext ctx) {
        UnaryExpression result = null;
        String operatorName = null;

        if (ctx.getChild(0).getText().equals("start")) {
            result = of.createStart().withOperand(parseExpression(ctx.expressionTerm()));
            operatorName = "Start";
        }
        else {
            result = of.createEnd().withOperand(parseExpression(ctx.expressionTerm()));
            operatorName = "End";
        }

        libraryBuilder.resolveUnaryCall("System", operatorName, result);
        return result;
    }

    private DateTimePrecision parseDateTimePrecision(String dateTimePrecision) {
        return parseDateTimePrecision(dateTimePrecision, true, true);
    }

    private DateTimePrecision parseComparableDateTimePrecision(String dateTimePrecision) {
        return parseDateTimePrecision(dateTimePrecision, true, false);
    }

    private DateTimePrecision parseComparableDateTimePrecision(String dateTimePrecision, boolean precisionRequired) {
        return parseDateTimePrecision(dateTimePrecision, precisionRequired, false);
    }

    private DateTimePrecision parseDateTimePrecision(String dateTimePrecision, boolean precisionRequired, boolean allowWeeks) {
        if (dateTimePrecision == null) {
            if (precisionRequired) {
                throw new IllegalArgumentException("dateTimePrecision is null");
            }

            return null;
        }

        switch (dateTimePrecision) {
            case "a":
            case "year":
            case "years":
                return DateTimePrecision.YEAR;
            case "mo":
            case "month":
            case "months":
                return DateTimePrecision.MONTH;
            case "wk":
            case "week":
            case "weeks":
                if (!allowWeeks) {
                    throw new IllegalArgumentException("Week precision cannot be used for comparisons.");
                }
                return DateTimePrecision.WEEK;
            case "d":
            case "day":
            case "days":
                return DateTimePrecision.DAY;
            case "h":
            case "hour":
            case "hours":
                return DateTimePrecision.HOUR;
            case "min":
            case "minute":
            case "minutes":
                return DateTimePrecision.MINUTE;
            case "s":
            case "second":
            case "seconds":
                return DateTimePrecision.SECOND;
            case "ms":
            case "millisecond":
            case "milliseconds":
                return DateTimePrecision.MILLISECOND;
            default:
                throw new IllegalArgumentException(String.format("Unknown precision '%s'.", dateTimePrecision));
        }
    }

    @Override
    public Object visitTimeUnitExpressionTerm(cqlParser.TimeUnitExpressionTermContext ctx) {
        String component = ctx.dateTimeComponent().getText();

        UnaryExpression result = null;
        String operatorName = null;
        switch (component) {
            case "date":
                result = of.createDateFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "DateFrom";
                break;
            case "time":
                result = of.createTimeFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "TimeFrom";
                break;
            case "timezone":
                if (!libraryBuilder.isCompatibilityLevel3()) {
                    // ERROR:
                    throw new IllegalArgumentException("Timezone keyword is only valid in 1.3 or lower");
                }
                result = of.createTimezoneFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "TimezoneFrom";
                break;
            case "timezoneoffset":
                result = of.createTimezoneOffsetFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "TimezoneOffsetFrom";
                break;
            case "year":
            case "month":
            case "day":
            case "hour":
            case "minute":
            case "second":
            case "millisecond":
                result = of.createDateTimeComponentFrom()
                        .withOperand(parseExpression(ctx.expressionTerm()))
                        .withPrecision(parseDateTimePrecision(component));
                operatorName = "DateTimeComponentFrom";
                break;
            case "week":
                throw new IllegalArgumentException("Date/time values do not have a week component.");
            default:
                throw new IllegalArgumentException(String.format("Unknown precision '%s'.", component));
        }

        libraryBuilder.resolveUnaryCall("System", operatorName, result);
        return result;
    }

    @Override
    public Object visitDurationExpressionTerm(cqlParser.DurationExpressionTermContext ctx) {
        // duration in days of X <=> days between start of X and end of X
        Expression operand = parseExpression(ctx.expressionTerm());

        Start start = of.createStart().withOperand(operand);
        libraryBuilder.resolveUnaryCall("System", "Start", start);

        End end = of.createEnd().withOperand(operand);
        libraryBuilder.resolveUnaryCall("System", "End", end);

        DurationBetween result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(start, end);

        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result);
        return result;
    }

    @Override
    public Object visitDifferenceExpressionTerm(cqlParser.DifferenceExpressionTermContext ctx) {
        // difference in days of X <=> difference in days between start of X and end of X
        Expression operand = parseExpression(ctx.expressionTerm());

        Start start = of.createStart().withOperand(operand);
        libraryBuilder.resolveUnaryCall("System", "Start", start);

        End end = of.createEnd().withOperand(operand);
        libraryBuilder.resolveUnaryCall("System", "End", end);

        DifferenceBetween result = of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(start, end);

        libraryBuilder.resolveBinaryCall("System", "DifferenceBetween", result);
        return result;
    }

    @Override
    public Object visitBetweenExpression(cqlParser.BetweenExpressionContext ctx) {
        // X properly? between Y and Z
        Expression first = parseExpression(ctx.expression());
        Expression second = parseExpression(ctx.expressionTerm(0));
        Expression third = parseExpression(ctx.expressionTerm(1));
        boolean isProper = ctx.getChild(0).getText().equals("properly");

        if (first.getResultType() instanceof IntervalType) {
            BinaryExpression result = isProper ? of.createProperIncludedIn() : of.createIncludedIn()
                    .withOperand(first, libraryBuilder.createInterval(second, true, third, true));

            libraryBuilder.resolveBinaryCall("System", isProper ? "ProperIncludedIn" : "IncludedIn", result);
            return result;
        }
        else {
            BinaryExpression result = of.createAnd()
                    .withOperand(
                            (isProper ? of.createGreater() : of.createGreaterOrEqual())
                                    .withOperand(first, second),
                            (isProper ? of.createLess() : of.createLessOrEqual())
                                    .withOperand(first, third)
                    );

            libraryBuilder.resolveBinaryCall("System", isProper ? "Greater" : "GreaterOrEqual", (BinaryExpression) result.getOperand().get(0));
            libraryBuilder.resolveBinaryCall("System", isProper ? "Less" : "LessOrEqual", (BinaryExpression) result.getOperand().get(1));
            libraryBuilder.resolveBinaryCall("System", "And", result);
            return result;
        }
    }

    @Override
    public Object visitDurationBetweenExpression(cqlParser.DurationBetweenExpressionContext ctx) {
        BinaryExpression result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result);
        return result;
    }

    @Override
    public Object visitDifferenceBetweenExpression(cqlParser.DifferenceBetweenExpressionContext ctx) {
        BinaryExpression result = of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", "DifferenceBetween", result);
        return result;
    }

    @Override
    public Object visitWidthExpressionTerm(cqlParser.WidthExpressionTermContext ctx) {
        UnaryExpression result = of.createWidth().withOperand(parseExpression(ctx.expressionTerm()));
        libraryBuilder.resolveUnaryCall("System", "Width", result);
        return result;
    }

    @Override
    public Expression visitParenthesizedTerm(cqlParser.ParenthesizedTermContext ctx) {
        return parseExpression(ctx.expression());
    }

    @Override
    public Object visitMembershipExpression(cqlParser.MembershipExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        switch (operator) {
            case "in":
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    In in = of.createIn()
                            .withPrecision(parseComparableDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
                            .withOperand(
                                    parseExpression(ctx.expression(0)),
                                    parseExpression(ctx.expression(1))
                            );

                    libraryBuilder.resolveBinaryCall("System", "In", in);
                    return in;
                } else {
                    Expression left = parseExpression(ctx.expression(0));
                    Expression right = parseExpression(ctx.expression(1));
                    return libraryBuilder.resolveIn(left, right);
                }
            case "contains":
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    Contains contains = of.createContains()
                            .withPrecision(parseComparableDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
                            .withOperand(
                                    parseExpression(ctx.expression(0)),
                                    parseExpression(ctx.expression(1))
                            );

                    libraryBuilder.resolveBinaryCall("System", "Contains", contains);
                    return contains;
                } else {
                    Expression left = parseExpression(ctx.expression(0));
                    Expression right = parseExpression(ctx.expression(1));
                    if (left instanceof ValueSetRef) {
                        InValueSet in = of.createInValueSet()
                                .withCode(right)
                                .withValueset((ValueSetRef) left);
                        libraryBuilder.resolveCall("System", "InValueSet", new InValueSetInvocation(in));
                        return in;
                    }

                    if (left instanceof CodeSystemRef) {
                        InCodeSystem in = of.createInCodeSystem()
                                .withCode(right)
                                .withCodesystem((CodeSystemRef)left);
                        libraryBuilder.resolveCall("System", "InCodeSystem", new InCodeSystemInvocation(in));
                        return in;
                    }

                    Contains contains = of.createContains().withOperand(left, right);
                    libraryBuilder.resolveBinaryCall("System", "Contains", contains);
                    return contains;
                }
        }

        throw new IllegalArgumentException(String.format("Unknown operator: %s", operator));
    }

    @Override
    public And visitAndExpression(cqlParser.AndExpressionContext ctx) {
        And and = of.createAnd().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        libraryBuilder.resolveBinaryCall("System", "And", and);
        return and;
    }

    @Override
    public Expression visitOrExpression(cqlParser.OrExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("xor")) {
            Xor xor = of.createXor().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
            libraryBuilder.resolveBinaryCall("System", "Xor", xor);
            return xor;
        } else {
            Or or = of.createOr().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
            libraryBuilder.resolveBinaryCall("System", "Or", or);
            return or;
        }
    }

    @Override
    public Expression visitImpliesExpression(cqlParser.ImpliesExpressionContext ctx) {
        Implies implies = of.createImplies().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        libraryBuilder.resolveBinaryCall("System", "Implies", implies);
        return implies;
    }

    @Override
    public Object visitInFixSetExpression(cqlParser.InFixSetExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        Expression left = parseExpression(ctx.expression(0));
        Expression right = parseExpression(ctx.expression(1));

        switch (operator) {
            case "|":
            case "union":
                return libraryBuilder.resolveUnion(left, right);
            case "intersect":
                return libraryBuilder.resolveIntersect(left, right);
            case "except":
                return libraryBuilder.resolveExcept(left, right);
        }

        return of.createNull();
    }

    @Override
    public Expression visitEqualityExpression(cqlParser.EqualityExpressionContext ctx) {
        String operator = parseString(ctx.getChild(1));
        if (operator.equals("~") || operator.equals("!~")) {
            BinaryExpression equivalent = of.createEquivalent().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));

            libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent);

            if (isAnnotationEnabled()) {
                equivalent.setLocalId(Integer.toString(getNextLocalId()));
            }

            if (!"~".equals(parseString(ctx.getChild(1)))) {
                track(equivalent, ctx);
                Not not = of.createNot().withOperand(equivalent);
                libraryBuilder.resolveUnaryCall("System", "Not", not);
                return not;
            }

            return equivalent;
        }
        else {
            BinaryExpression equal = of.createEqual().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));

            libraryBuilder.resolveBinaryCall("System", "Equal", equal);
            if (!"=".equals(parseString(ctx.getChild(1)))) {
                track(equal, ctx);
                Not not = of.createNot().withOperand(equal);
                libraryBuilder.resolveUnaryCall("System", "Not", not);
                return not;
            }

            return equal;
        }
    }

    @Override
    public BinaryExpression visitInequalityExpression(cqlParser.InequalityExpressionContext ctx) {
        BinaryExpression exp;
        String operatorName;
        switch (parseString(ctx.getChild(1))) {
            case "<=":
                operatorName = "LessOrEqual";
                exp = of.createLessOrEqual();
                break;
            case "<":
                operatorName = "Less";
                exp = of.createLess();
                break;
            case ">":
                operatorName = "Greater";
                exp = of.createGreater();
                break;
            case ">=":
                operatorName = "GreaterOrEqual";
                exp = of.createGreaterOrEqual();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown operator: %s", ctx.getChild(1).getText()));
        }
        exp.withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        libraryBuilder.resolveBinaryCall("System", operatorName, exp);
        return exp;
    }

    @Override
    public List<String> visitQualifiedIdentifier(cqlParser.QualifiedIdentifierContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierContext qualifierContext : ctx.qualifier()) {
            String qualifier = parseString(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = parseString(ctx.identifier());
        identifiers.add(identifier);
        return identifiers;
    }

    @Override
    public List<String> visitQualifiedIdentifierExpression(cqlParser.QualifiedIdentifierExpressionContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierExpressionContext qualifierContext : ctx.qualifierExpression()) {
            String qualifier = parseString(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = parseString(ctx.referentialIdentifier());
        identifiers.add(identifier);
        return identifiers;
    }

    @Override
    public String visitSimplePathReferentialIdentifier(cqlParser.SimplePathReferentialIdentifierContext ctx) {
        return (String)visit(ctx.referentialIdentifier());
    }

    @Override
    public String visitSimplePathQualifiedIdentifier(cqlParser.SimplePathQualifiedIdentifierContext ctx) {
        return visit(ctx.simplePath()) + "." + visit(ctx.referentialIdentifier());
    }

    @Override
    public String visitSimplePathIndexer(cqlParser.SimplePathIndexerContext ctx) {
        return visit(ctx.simplePath()) + "[" + visit(ctx.simpleLiteral()) + "]";
    }

    @Override
    public Object visitTermExpression(cqlParser.TermExpressionContext ctx) {
        Object result = super.visitTermExpression(ctx);

        if (result instanceof LibraryRef) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Identifier %s is a library and cannot be used as an expression.", ((LibraryRef)result).getLibraryName()));
        }

        return result;
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        String text = node.getText();
        int tokenType = node.getSymbol().getType();

        if (cqlLexer.EOF == tokenType) {
            return null;
        }

        if (cqlLexer.STRING == tokenType || cqlLexer.QUOTEDIDENTIFIER == tokenType || cqlLexer.DELIMITEDIDENTIFIER == tokenType) {
            // chop off leading and trailing ', ", or `
            text = text.substring(1, text.length() - 1);

            // This is an alternate style of escaping that was removed when we switched to industry-standard escape sequences
            //if (cqlLexer.STRING == tokenType) {
            //    text = text.replace("''", "'");
            //}
            //else {
            //    text = text.replace("\"\"", "\"");
            //}
        }

        return text;
    }

    @Override
    public Object visitConversionExpressionTerm(cqlParser.ConversionExpressionTermContext ctx) {
        if (ctx.typeSpecifier() != null) {
            TypeSpecifier targetType = parseTypeSpecifier(ctx.typeSpecifier());
            Expression operand = parseExpression(ctx.expression());
            if (!DataTypes.equal(operand.getResultType(), targetType.getResultType())) {
                Conversion conversion = libraryBuilder.findConversion(operand.getResultType(), targetType.getResultType(), false, true);
                if (conversion == null) {
                    // ERROR:
                    throw new IllegalArgumentException(String.format("Could not resolve conversion from type %s to type %s.",
                            operand.getResultType(), targetType.getResultType()));
                }

                return libraryBuilder.convertExpression(operand, conversion);
            }

            return operand;
        }
        else {
            String targetUnit = parseString(ctx.unit());
            targetUnit = libraryBuilder.ensureUcumUnit(targetUnit);
            Expression operand = parseExpression(ctx.expression());
            Expression unitOperand = libraryBuilder.createLiteral(targetUnit);
            track(unitOperand, ctx.unit());
            ConvertQuantity convertQuantity = of.createConvertQuantity().withOperand(operand, unitOperand);
            track(convertQuantity, ctx);
            return libraryBuilder.resolveBinaryCall("System", "ConvertQuantity", convertQuantity);
        }
    }

    @Override
    public Object visitTypeExpression(cqlParser.TypeExpressionContext ctx) {
        // NOTE: These don't use the buildIs or buildAs because those start with a DataType, rather than a TypeSpecifier
        if (ctx.getChild(1).getText().equals("is")) {
            Is is = of.createIs()
                    .withOperand(parseExpression(ctx.expression()))
                    .withIsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));
            is.setResultType(libraryBuilder.resolveTypeName("System", "Boolean"));
            return is;
        }

        As as = of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(false);
        DataType targetType = as.getAsTypeSpecifier().getResultType();
        DataTypes.verifyCast(targetType, as.getOperand().getResultType());
        as.setResultType(targetType);
        return as;
    }

    @Override
    public Object visitCastExpression(cqlParser.CastExpressionContext ctx) {
        // NOTE: This doesn't use buildAs because it starts with a DataType, rather than a TypeSpecifier
        As as = of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(true);
        DataType targetType = as.getAsTypeSpecifier().getResultType();
        DataTypes.verifyCast(targetType, as.getOperand().getResultType());
        as.setResultType(targetType);
        return as;
    }

    @Override
    public Expression visitBooleanExpression(cqlParser.BooleanExpressionContext ctx) {
        UnaryExpression exp = null;
        Expression left = (Expression) visit(ctx.expression());
        String lastChild = ctx.getChild(ctx.getChildCount() - 1).getText();
        String nextToLast = ctx.getChild(ctx.getChildCount() - 2).getText();
        switch (lastChild) {
            case "null" :
                exp = of.createIsNull().withOperand(left);
                libraryBuilder.resolveUnaryCall("System", "IsNull", exp);
                break;

            case "true" :
                exp = of.createIsTrue().withOperand(left);
                libraryBuilder.resolveUnaryCall("System", "IsTrue", exp);
                break;

            case "false" :
                exp = of.createIsFalse().withOperand(left);
                libraryBuilder.resolveUnaryCall("System", "IsFalse", exp);
                break;

            default:
                throw new IllegalArgumentException(String.format("Unknown boolean test predicate %s.", lastChild));
        }

        if ("not".equals(nextToLast)) {
            track(exp, ctx);
            exp = of.createNot().withOperand(exp);
            libraryBuilder.resolveUnaryCall("System", "Not", exp);
        }

        return exp;
    }

    @Override
    public Object visitTimingExpression(cqlParser.TimingExpressionContext ctx) {
        Expression left = parseExpression(ctx.expression(0));
        Expression right = parseExpression(ctx.expression(1));
        TimingOperatorContext timingOperatorContext = new TimingOperatorContext(left, right);
        timingOperators.push(timingOperatorContext);
        try {
            return visit(ctx.intervalOperatorPhrase());
        } finally {
            timingOperators.pop();
        }
    }

    @Override
    public Object visitConcurrentWithIntervalOperatorPhrase(cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? 'same' dateTimePrecision? (relativeQualifier | 'as') ('start' | 'end')?
        TimingOperatorContext timingOperator = timingOperators.peek();
        ParseTree firstChild = ctx.getChild(0);
        if ("starts".equals(firstChild.getText())) {
            Start start = of.createStart().withOperand(timingOperator.getLeft());
            track(start, firstChild);
            libraryBuilder.resolveUnaryCall("System", "Start", start);
            timingOperator.setLeft(start);
        }

        if ("ends".equals(firstChild.getText())) {
            End end = of.createEnd().withOperand(timingOperator.getLeft());
            track(end, firstChild);
            libraryBuilder.resolveUnaryCall("System", "End", end);
            timingOperator.setLeft(end);
        }

        ParseTree lastChild = ctx.getChild(ctx.getChildCount() - 1);
        if ("start".equals(lastChild.getText())) {
            Start start = of.createStart().withOperand(timingOperator.getRight());
            track(start, lastChild);
            libraryBuilder.resolveUnaryCall("System", "Start", start);
            timingOperator.setRight(start);
        }

        if ("end".equals(lastChild.getText())) {
            End end = of.createEnd().withOperand(timingOperator.getRight());
            track(end, lastChild);
            libraryBuilder.resolveUnaryCall("System", "End", end);
            timingOperator.setRight(end);
        }

        String operatorName = null;
        BinaryExpression operator = null;
        boolean allowPromotionAndDemotion = false;
        if (ctx.relativeQualifier() == null) {
            if (ctx.dateTimePrecision() != null) {
                operator = of.createSameAs().withPrecision(parseComparableDateTimePrecision(ctx.dateTimePrecision().getText()));
            } else {
                operator = of.createSameAs();
            }
            operatorName = "SameAs";
        } else {
            switch (ctx.relativeQualifier().getText()) {
                case "or after": {
                    if (ctx.dateTimePrecision() != null) {
                        operator = of.createSameOrAfter().withPrecision(parseComparableDateTimePrecision(ctx.dateTimePrecision().getText()));
                    } else {
                        operator = of.createSameOrAfter();
                    }
                    operatorName = "SameOrAfter";
                    allowPromotionAndDemotion = true;
                }
                break;
                case "or before": {
                    if (ctx.dateTimePrecision() != null) {
                        operator = of.createSameOrBefore().withPrecision(parseComparableDateTimePrecision(ctx.dateTimePrecision().getText()));
                    } else {
                        operator = of.createSameOrBefore();
                    }
                    operatorName = "SameOrBefore";
                    allowPromotionAndDemotion = true;
                }
                break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown relative qualifier: '%s'.", ctx.relativeQualifier().getText()));
            }
        }

        operator = operator.withOperand(timingOperator.getLeft(), timingOperator.getRight());
        libraryBuilder.resolveBinaryCall("System", operatorName, operator, true, allowPromotionAndDemotion);

        return operator;
    }

    @Override
    public Object visitIncludesIntervalOperatorPhrase(cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
        // 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?
        boolean isProper = false;
        boolean isRightPoint = false;
        TimingOperatorContext timingOperator = timingOperators.peek();
        for (ParseTree pt : ctx.children) {
            if ("properly".equals(pt.getText())) {
                isProper = true;
                continue;
            }

            if ("start".equals(pt.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                track(start, pt);
                libraryBuilder.resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                isRightPoint = true;
                continue;
            }

            if ("end".equals(pt.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                track(end, pt);
                libraryBuilder.resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                isRightPoint = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        // If the right is not convertible to an interval or list
        //if (!isRightPoint &&
        //        !(timingOperator.getRight().getResultType() instanceof IntervalType
        //                || timingOperator.getRight().getResultType() instanceof ListType)) {
        //    isRightPoint = true;
        //}

        if (isRightPoint) {
            if (isProper) {
                return libraryBuilder.resolveProperContains(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
            }

            return libraryBuilder.resolveContains(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
        }

        if (isProper) {
            return libraryBuilder.resolveProperIncludes(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
        }

        return libraryBuilder.resolveIncludes(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
    }

    @Override
    public Object visitIncludedInIntervalOperatorPhrase(cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? 'properly'? ('during' | 'included in') dateTimePrecisionSpecifier?
        boolean isProper = false;
        boolean isLeftPoint = false;
        TimingOperatorContext timingOperator = timingOperators.peek();
        for (ParseTree pt : ctx.children) {
            if ("starts".equals(pt.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                track(start, pt);
                libraryBuilder.resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                isLeftPoint = true;
                continue;
            }

            if ("ends".equals(pt.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                track(end, pt);
                libraryBuilder.resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                isLeftPoint = true;
                continue;
            }

            if ("properly".equals(pt.getText())) {
                isProper = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        // If the left is not convertible to an interval or list
        //if (!isLeftPoint &&
        //        !(timingOperator.getLeft().getResultType() instanceof IntervalType
        //                || timingOperator.getLeft().getResultType() instanceof ListType)) {
        //    isLeftPoint = true;
        //}

        if (isLeftPoint) {
            if (isProper) {
                return libraryBuilder.resolveProperIn(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
            }

            return libraryBuilder.resolveIn(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
        }

        if (isProper) {
            return libraryBuilder.resolveProperIncludedIn(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
        }

        return libraryBuilder.resolveIncludedIn(timingOperator.getLeft(), timingOperator.getRight(), parseComparableDateTimePrecision(dateTimePrecision, false));
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? quantityOffset? ('before' | 'after') dateTimePrecisionSpecifier? ('start' | 'end')?

        // duration before/after
        // A starts 3 days before start B
        //* start of A same day as start of B - 3 days
        // A starts 3 days after start B
        //* start of A same day as start of B + 3 days

        // or more/less duration before/after
        // A starts 3 days or more before start B
        //* start of A <= start of B - 3 days
        // A starts 3 days or more after start B
        //* start of A >= start of B + 3 days
        // A starts 3 days or less before start B
        //* start of A in [start of B - 3 days, start of B) and B is not null
        // A starts 3 days or less after start B
        //* start of A in (start of B, start of B + 3 days] and B is not null

        // less/more than duration before/after
        // A starts more than 3 days before start B
        //* start of A < start of B - 3 days
        // A starts more than 3 days after start B
        //* start of A > start of B + 3 days
        // A starts less than 3 days before start B
        //* start of A in (start of B - 3 days, start of B)
        // A starts less than 3 days after start B
        //* start of A in (start of B, start of B + 3 days)

        TimingOperatorContext timingOperator = timingOperators.peek();
        boolean isBefore = false;
        boolean isInclusive = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                track(start, child);
                libraryBuilder.resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                continue;
            }

            if ("ends".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                track(end, child);
                libraryBuilder.resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                continue;
            }

            if ("start".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                track(start, child);
                libraryBuilder.resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                continue;
            }

            if ("end".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                track(end, child);
                libraryBuilder.resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                continue;
            }
        }

        for (ParseTree child : ctx.temporalRelationship().children) {
            if ("before".equals(child.getText())) {
                isBefore = true;
                continue;
            }

            if ("on or".equals(child.getText()) || "or on".equals(child.getText())) {
                isInclusive = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.quantityOffset() == null) {
            if (isInclusive) {
                if (isBefore) {
                    SameOrBefore sameOrBefore = of.createSameOrBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        sameOrBefore.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "SameOrBefore", sameOrBefore, true, true);
                    return sameOrBefore;

                } else {
                    SameOrAfter sameOrAfter = of.createSameOrAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        sameOrAfter.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "SameOrAfter", sameOrAfter, true, true);
                    return sameOrAfter;
                }
            }
            else {
                if (isBefore) {
                    Before before = of.createBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        before.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "Before", before, true, true);
                    return before;

                } else {
                    After after = of.createAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        after.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "After", after, true, true);
                    return after;
                }
            }
        } else {
            Quantity quantity = (Quantity)visit(ctx.quantityOffset().quantity());

            if (timingOperator.getLeft().getResultType() instanceof IntervalType) {
                if (isBefore) {
                    End end = of.createEnd().withOperand(timingOperator.getLeft());
                    track(end, timingOperator.getLeft());
                    libraryBuilder.resolveUnaryCall("System", "End", end);
                    timingOperator.setLeft(end);
                }
                else {
                    Start start = of.createStart().withOperand(timingOperator.getLeft());
                    track(start, timingOperator.getLeft());
                    libraryBuilder.resolveUnaryCall("System", "Start", start);
                    timingOperator.setLeft(start);
                }
            }

            if (timingOperator.getRight().getResultType() instanceof IntervalType) {
                if (isBefore) {
                    Start start = of.createStart().withOperand(timingOperator.getRight());
                    track(start, timingOperator.getRight());
                    libraryBuilder.resolveUnaryCall("System", "Start", start);
                    timingOperator.setRight(start);
                }
                else {
                    End end = of.createEnd().withOperand(timingOperator.getRight());
                    track(end, timingOperator.getRight());
                    libraryBuilder.resolveUnaryCall("System", "End", end);
                    timingOperator.setRight(end);
                }
            }

            if (ctx.quantityOffset().offsetRelativeQualifier() == null && ctx.quantityOffset().exclusiveRelativeQualifier() == null) {
                // Use a SameAs
                // For a Before, subtract the quantity from the right operand
                // For an After, add the quantity to the right operand
                if (isBefore) {
                    Subtract subtract = of.createSubtract().withOperand(timingOperator.getRight(), quantity);
                    track(subtract, timingOperator.getRight());
                    libraryBuilder.resolveBinaryCall("System", "Subtract", subtract);
                    timingOperator.setRight(subtract);
                }
                else {
                    Add add = of.createAdd().withOperand(timingOperator.getRight(), quantity);
                    track(add, timingOperator.getRight());
                    libraryBuilder.resolveBinaryCall("System", "Add", add);
                    timingOperator.setRight(add);
                }

                SameAs sameAs = of.createSameAs().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                if (dateTimePrecision != null) {
                    sameAs.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                }
                libraryBuilder.resolveBinaryCall("System", "SameAs", sameAs);
                return sameAs;
            }
            else {
                boolean isOffsetInclusive = ctx.quantityOffset().offsetRelativeQualifier() != null;
                String qualifier = ctx.quantityOffset().offsetRelativeQualifier() != null
                        ? ctx.quantityOffset().offsetRelativeQualifier().getText()
                        : ctx.quantityOffset().exclusiveRelativeQualifier().getText();

                switch (qualifier) {
                    case "more than":
                    case "or more":
                        // For More Than/Or More, Use a Before/After/SameOrBefore/SameOrAfter
                        // For a Before, subtract the quantity from the right operand
                        // For an After, add the quantity to the right operand
                        if (isBefore) {
                            Subtract subtract = of.createSubtract().withOperand(timingOperator.getRight(), quantity);
                            track(subtract, timingOperator.getRight());
                            libraryBuilder.resolveBinaryCall("System", "Subtract", subtract);
                            timingOperator.setRight(subtract);

                            if (!isOffsetInclusive) {
                                Before before = of.createBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                                if (dateTimePrecision != null) {
                                    before.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "Before", before, true, true);
                                return before;
                            }
                            else {
                                SameOrBefore sameOrBefore = of.createSameOrBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                                if (dateTimePrecision != null) {
                                    sameOrBefore.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "SameOrBefore", sameOrBefore, true, true);
                                return sameOrBefore;
                            }
                        }
                        else {
                            Add add = of.createAdd().withOperand(timingOperator.getRight(), quantity);
                            track(add, timingOperator.getRight());
                            libraryBuilder.resolveBinaryCall("System", "Add", add);
                            timingOperator.setRight(add);

                            if (!isOffsetInclusive) {
                                After after = of.createAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                                if (dateTimePrecision != null) {
                                    after.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "After", after, true, true);
                                return after;
                            }
                            else {
                                SameOrAfter sameOrAfter = of.createSameOrAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                                if (dateTimePrecision != null) {
                                    sameOrAfter.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "SameOrAfter", sameOrAfter, true, true);
                                return sameOrAfter;
                            }
                        }

                    case "less than":
                    case "or less":
                        // For Less Than/Or Less, Use an In
                        // For Before, construct an interval from right - quantity to right
                        // For After, construct an interval from right to right + quantity
                        Expression lowerBound = null;
                        Expression upperBound = null;
                        Expression right = timingOperator.getRight();
                        if (isBefore) {
                            lowerBound = of.createSubtract().withOperand(right, quantity);
                            track(lowerBound, right);
                            libraryBuilder.resolveBinaryCall("System", "Subtract", (BinaryExpression)lowerBound);
                            upperBound = right;
                        }
                        else {
                            lowerBound = right;
                            upperBound = of.createAdd().withOperand(right, quantity);
                            track(upperBound, right);
                            libraryBuilder.resolveBinaryCall("System", "Add", (BinaryExpression)upperBound);
                        }

                        // 3 days or less before -> [B - 3 days, B)
                        // less than 3 days before -> (B - 3 days, B)
                        // 3 days or less after -> (B, B + 3 days]
                        // less than 3 days after -> (B, B + 3 days)
                        Interval interval =
                                isBefore
                                ? libraryBuilder.createInterval(lowerBound, isOffsetInclusive, upperBound, isInclusive)
                                : libraryBuilder.createInterval(lowerBound, isInclusive, upperBound, isOffsetInclusive);

                        track(interval, ctx.quantityOffset());
                        In in = of.createIn().withOperand(timingOperator.getLeft(), interval);
                        if (dateTimePrecision != null) {
                            in.setPrecision(parseComparableDateTimePrecision(dateTimePrecision));
                        }
                        track(in, ctx.quantityOffset());
                        libraryBuilder.resolveBinaryCall("System", "In", in);

                        // if the offset or comparison is inclusive, add a null check for B to ensure correct interpretation
                        if (isOffsetInclusive || isInclusive) {
                            IsNull nullTest = of.createIsNull().withOperand(right);
                            track(nullTest, ctx.quantityOffset());
                            libraryBuilder.resolveUnaryCall("System", "IsNull", nullTest);
                            Not notNullTest = of.createNot().withOperand(nullTest);
                            track(notNullTest, ctx.quantityOffset());
                            libraryBuilder.resolveUnaryCall("System", "Not", notNullTest);
                            And and = of.createAnd().withOperand(in, notNullTest);
                            track(and, ctx.quantityOffset());
                            libraryBuilder.resolveBinaryCall("System", "And", and);
                            return and;
                        }

                        // Otherwise, return the constructed in
                        return in;
                }
            }
        }

        throw new IllegalArgumentException("Unable to resolve interval operator phrase.");
    }

    private BinaryExpression resolveBetweenOperator(String unit, Expression left, Expression right) {
        if (unit != null) {
            DurationBetween between = of.createDurationBetween().withPrecision(parseDateTimePrecision(unit)).withOperand(left, right);
            libraryBuilder.resolveBinaryCall("System", "DurationBetween", between);
            return between;
        }

        return null;
    }

    @Override
    public Object visitWithinIntervalOperatorPhrase(cqlParser.WithinIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?
        // A starts within 3 days of start B
        //* start of A in [start of B - 3 days, start of B + 3 days] and start B is not null
        // A starts within 3 days of B
        //* start of A in [start of B - 3 days, end of B + 3 days]

        TimingOperatorContext timingOperator = timingOperators.peek();
        boolean isProper = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                track(start, child);
                libraryBuilder.resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                continue;
            }

            if ("ends".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                track(end, child);
                libraryBuilder.resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                continue;
            }

            if ("start".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                track(start, child);
                libraryBuilder.resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                continue;
            }

            if ("end".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                track(end, child);
                libraryBuilder.resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                continue;
            }

            if ("properly".equals(child.getText())) {
                isProper = true;
                continue;
            }
        }

        Quantity quantity = (Quantity)visit(ctx.quantity());
        Expression lowerBound = null;
        Expression upperBound = null;
        Expression initialBound = null;
        if (timingOperator.getRight().getResultType() instanceof IntervalType) {
            lowerBound = of.createStart().withOperand(timingOperator.getRight());
            track(lowerBound, ctx.quantity());
            libraryBuilder.resolveUnaryCall("System", "Start", (Start)lowerBound);
            upperBound = of.createEnd().withOperand(timingOperator.getRight());
            track(upperBound, ctx.quantity());
            libraryBuilder.resolveUnaryCall("System", "End", (End)upperBound);
        }
        else {
            lowerBound = timingOperator.getRight();
            upperBound = timingOperator.getRight();
            initialBound = lowerBound;
        }

        lowerBound = of.createSubtract().withOperand(lowerBound, quantity);
        track(lowerBound, ctx.quantity());
        libraryBuilder.resolveBinaryCall("System", "Subtract", (BinaryExpression)lowerBound);

        upperBound = of.createAdd().withOperand(upperBound, quantity);
        track(upperBound, ctx.quantity());
        libraryBuilder.resolveBinaryCall("System", "Add", (BinaryExpression)upperBound);

        Interval interval = libraryBuilder.createInterval(lowerBound, !isProper, upperBound, !isProper);
        track(interval, ctx.quantity());

        In in = of.createIn().withOperand(timingOperator.getLeft(), interval);
        libraryBuilder.resolveBinaryCall("System", "In", in);

        // if the within is not proper and the interval is being constructed from a single point, add a null check for that point to ensure correct interpretation
        if (!isProper && (initialBound != null)) {
            IsNull nullTest = of.createIsNull().withOperand(initialBound);
            track(nullTest, ctx.quantity());
            libraryBuilder.resolveUnaryCall("System", "IsNull", nullTest);
            Not notNullTest = of.createNot().withOperand(nullTest);
            track(notNullTest, ctx.quantity());
            libraryBuilder.resolveUnaryCall("System", "Not", notNullTest);
            And and = of.createAnd().withOperand(in, notNullTest);
            track(and, ctx.quantity());
            libraryBuilder.resolveBinaryCall("System", "And", and);
            return and;
        }

        // Otherwise, return the constructed in
        return in;
    }

    @Override
    public Object visitMeetsIntervalOperatorPhrase(cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + (dateTimePrecision == null ? 0 : 1))) {
            operator = dateTimePrecision != null
                    ? of.createMeets().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                    : of.createMeets();
            operatorName = "Meets";
        } else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = dateTimePrecision != null
                        ? of.createMeetsBefore().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                        : of.createMeetsBefore();
                operatorName = "MeetsBefore";
            } else {
                operator = dateTimePrecision != null
                        ? of.createMeetsAfter().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                        : of.createMeetsAfter();
                operatorName = "MeetsAfter";
            }
        }

        operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
        libraryBuilder.resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + (dateTimePrecision == null ? 0 : 1))) {
            operator = dateTimePrecision != null
                    ? of.createOverlaps().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                    : of.createOverlaps();
            operatorName = "Overlaps";
        } else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = dateTimePrecision != null
                        ? of.createOverlapsBefore().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                        : of.createOverlapsBefore();
                operatorName = "OverlapsBefore";
            } else {
                operator = dateTimePrecision != null
                        ? of.createOverlapsAfter().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                        : of.createOverlapsAfter();
                operatorName = "OverlapsAfter";
            }
        }

        operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
        libraryBuilder.resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitStartsIntervalOperatorPhrase(cqlParser.StartsIntervalOperatorPhraseContext ctx) {
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        Starts starts = (dateTimePrecision != null
                ? of.createStarts().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                : of.createStarts()
        ).withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());

        libraryBuilder.resolveBinaryCall("System", "Starts", starts);
        return starts;
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        Ends ends = (dateTimePrecision != null
                ? of.createEnds().withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                : of.createEnds()
        ).withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());

        libraryBuilder.resolveBinaryCall("System", "Ends", ends);
        return ends;
    }

    public Expression resolveIfThenElse(If ifObject) {
        ifObject.setCondition(libraryBuilder.ensureCompatible(ifObject.getCondition(), libraryBuilder.resolveTypeName("System", "Boolean")));
        DataType resultType = libraryBuilder.ensureCompatibleTypes(ifObject.getThen().getResultType(), ifObject.getElse().getResultType());
        ifObject.setResultType(resultType);
        ifObject.setThen(libraryBuilder.ensureCompatible(ifObject.getThen(), resultType));
        ifObject.setElse(libraryBuilder.ensureCompatible(ifObject.getElse(), resultType));
        return ifObject;
    }

    @Override
    public Object visitIfThenElseExpressionTerm(cqlParser.IfThenElseExpressionTermContext ctx) {
        If ifObject = of.createIf()
                .withCondition(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)))
                .withElse(parseExpression(ctx.expression(2)));

        return resolveIfThenElse(ifObject);
    }

    @Override
    public Object visitCaseExpressionTerm(cqlParser.CaseExpressionTermContext ctx) {
        Case result = of.createCase();
        Boolean hitElse = false;
        DataType resultType = null;
        for (ParseTree pt : ctx.children) {
            if ("else".equals(pt.getText())) {
                hitElse = true;
                continue;
            }

            if (pt instanceof cqlParser.ExpressionContext) {
                if (hitElse) {
                    result.setElse(parseExpression(pt));
                    resultType = libraryBuilder.ensureCompatibleTypes(resultType, result.getElse().getResultType());
                } else {
                    result.setComparand(parseExpression(pt));
                }
            }

            if (pt instanceof cqlParser.CaseExpressionItemContext) {
                CaseItem caseItem = (CaseItem)visit(pt);
                if (result.getComparand() != null) {
                    libraryBuilder.verifyType(caseItem.getWhen().getResultType(), result.getComparand().getResultType());
                }
                else {
                    DataTypes.verifyType(caseItem.getWhen().getResultType(), libraryBuilder.resolveTypeName("System", "Boolean"));
                }

                if (resultType == null) {
                    resultType = caseItem.getThen().getResultType();
                }
                else {
                    resultType = libraryBuilder.ensureCompatibleTypes(resultType, caseItem.getThen().getResultType());
                }

                result.getCaseItem().add(caseItem);
            }
        }

        for (CaseItem caseItem : result.getCaseItem()) {
            if (result.getComparand() != null) {
                caseItem.setWhen(libraryBuilder.ensureCompatible(caseItem.getWhen(), result.getComparand().getResultType()));
            }

            caseItem.setThen(libraryBuilder.ensureCompatible(caseItem.getThen(), resultType));
        }

        result.setElse(libraryBuilder.ensureCompatible(result.getElse(), resultType));
        result.setResultType(resultType);
        return result;
    }

    @Override
    public Object visitCaseExpressionItem(cqlParser.CaseExpressionItemContext ctx) {
        return of.createCaseItem()
                .withWhen(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)));
    }

    @Override
    public Object visitAggregateExpressionTerm(cqlParser.AggregateExpressionTermContext ctx) {
        switch (ctx.getChild(0).getText()) {
            case "distinct":
                Distinct distinct = of.createDistinct().withOperand(parseExpression(ctx.expression()));
                libraryBuilder.resolveUnaryCall("System", "Distinct", distinct);
                return distinct;
            case "flatten":
                Flatten flatten = of.createFlatten().withOperand(parseExpression(ctx.expression()));
                libraryBuilder.resolveUnaryCall("System", "Flatten", flatten);
                return flatten;
        }

        throw new IllegalArgumentException(String.format("Unknown aggregate operator %s.", ctx.getChild(0).getText()));
    }

    @Override
    public Object visitSetAggregateExpressionTerm(cqlParser.SetAggregateExpressionTermContext ctx) {
        Expression source = parseExpression(ctx.expression(0));
        Expression per = null;
        if (ctx.dateTimePrecision() != null) {
            per = libraryBuilder.createQuantity(BigDecimal.valueOf(1.0), parseString(ctx.dateTimePrecision()));
        }
        else if (ctx.expression().size() > 1) {
            per = parseExpression(ctx.expression(1));
        }
        else {
            // Determine per quantity based on point type of the intervals involved
            if (source.getResultType() instanceof ListType) {
                ListType listType = (ListType)source.getResultType();
                if (listType.getElementType() instanceof IntervalType) {
                    IntervalType intervalType = (IntervalType)listType.getElementType();
                    DataType pointType = intervalType.getPointType();

                    per = libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Quantity"));

                    // TODO: Test this...
//                    // Successor(MinValue<T>) - MinValue<T>
//                    MinValue minimum = libraryBuilder.buildMinimum(pointType);
//                    track(minimum, ctx);
//
//                    Expression successor = libraryBuilder.buildSuccessor(minimum);
//                    track(successor, ctx);
//
//                    minimum = libraryBuilder.buildMinimum(pointType);
//                    track(minimum, ctx);
//
//                    Subtract subtract = of.createSubtract().withOperand(successor, minimum);
//                    libraryBuilder.resolveBinaryCall("System", "Subtract", subtract);
//                    per = subtract;
                }
            }
            else {
                per = libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Quantity"));
            }
        }

        switch (ctx.getChild(0).getText()) {
            case "expand":
                Expand expand = of.createExpand().withOperand(source, per);
                libraryBuilder.resolveBinaryCall("System", "Expand", expand);
                return expand;

            case "collapse":
                Collapse collapse = of.createCollapse().withOperand(source, per);
                libraryBuilder.resolveBinaryCall("System", "Collapse", collapse);
                return collapse;
        }

        throw new IllegalArgumentException(String.format("Unknown aggregate set operator %s.", ctx.getChild(0).getText()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression visitRetrieve(cqlParser.RetrieveContext ctx) {
        libraryBuilder.checkLiteralContext();
        List<String> qualifiers = parseQualifiers(ctx.namedTypeSpecifier());
        String model = getModelIdentifier(qualifiers);
        String label = getTypeIdentifier(qualifiers, parseString(ctx.namedTypeSpecifier().referentialOrTypeNameIdentifier()));
        DataType dataType = libraryBuilder.resolveTypeName(model, label);
        if (dataType == null) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve type name %s.", label));
        }

        if (!(dataType instanceof ClassType) || !((ClassType)dataType).isRetrievable()) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Specified data type %s does not support retrieval.", label));
        }

        ClassType classType = (ClassType)dataType;
        // BTR -> The original intent of this code was to have the retrieve return the base type, and use the "templateId"
        // element of the retrieve to communicate the "positive" or "negative" profile to the data access layer.
        // However, because this notion of carrying the "profile" through a type is not general, it causes inconsistencies
        // when using retrieve results with functions defined in terms of the same type (see GitHub Issue #131).
        // Based on the discussion there, the retrieve will now return the declared type, whether it is a profile or not.
        //ProfileType profileType = dataType instanceof ProfileType ? (ProfileType)dataType : null;
        //NamedType namedType = profileType == null ? classType : (NamedType)classType.getBaseType();
        NamedType namedType = classType;

        ModelInfo modelInfo = libraryBuilder.getModel(namedType.getNamespace()).getModelInfo();
        boolean useStrictRetrieveTyping = modelInfo.isStrictRetrieveTyping() != null && modelInfo.isStrictRetrieveTyping();

        String codePath = null;
        Property property = null;
        CqlCompilerException propertyException = null;
        Expression terminology = null;
        String codeComparator = null;
        if (ctx.terminology() != null) {
            if (ctx.codePath() != null) {
                String identifiers = (String)visit(ctx.codePath());
                codePath = identifiers;
            }
            else if (classType.getPrimaryCodePath() != null) {
                codePath = classType.getPrimaryCodePath();
            }

            if (codePath == null) {
                // ERROR:
                // WARNING:
                propertyException = new CqlSemanticException("Retrieve has a terminology target but does not specify a code path and the type of the retrieve does not have a primary code path defined.",
                        useStrictRetrieveTyping ? CqlCompilerException.ErrorSeverity.Error : CqlCompilerException.ErrorSeverity.Warning,
                        getTrackBack(ctx));
                libraryBuilder.recordParsingException(propertyException);
            }
            else {
                try {
                    DataType codeType = libraryBuilder.resolvePath((DataType) namedType, codePath);
                    property = of.createProperty().withPath(codePath);
                    property.setResultType(codeType);
                }
                catch (Exception e) {
                    // ERROR:
                    // WARNING:
                    propertyException = new CqlSemanticException(String.format("Could not resolve code path %s for the type of the retrieve %s.",
                            codePath, namedType.getName()), useStrictRetrieveTyping ? CqlCompilerException.ErrorSeverity.Error : CqlCompilerException.ErrorSeverity.Warning,
                            getTrackBack(ctx), e);
                    libraryBuilder.recordParsingException(propertyException);
                }
            }

            if (ctx.terminology().qualifiedIdentifierExpression() != null) {
                List<String> identifiers = (List<String>) visit(ctx.terminology());
                terminology = resolveQualifiedIdentifier(identifiers);
                track(terminology, ctx.terminology().qualifiedIdentifierExpression());
            }
            else {
                terminology = parseExpression(ctx.terminology().expression());
            }

            codeComparator = ctx.codeComparator() != null ? (String)visit(ctx.codeComparator()) : null;
        }

        Expression result = null;

        // Only expand a choice-valued code path if no comparator is specified
        // Otherwise, a code comparator will always choose a specific representation
        boolean hasFHIRHelpers = libraryInfo.resolveLibraryName("FHIRHelpers") != null;
        if (property != null && property.getResultType() instanceof ChoiceType && codeComparator == null) {
            for (DataType propertyType : ((ChoiceType)property.getResultType()).getTypes()) {
                if (hasFHIRHelpers && propertyType instanceof NamedType && ((NamedType)propertyType).getSimpleName().equals("Reference") && namedType.getSimpleName().equals("MedicationRequest")) {
                    // TODO: This is a model-specific special case to support QICore
                    // This functionality needs to be generalized to a retrieve mapping in the model info
                    // But that requires a model info change (to represent references, right now the model info only includes context relationships)
                    // The reference expands to [MedicationRequest] MR with [Medication] M such that M.id = Last(Split(MR.medication.reference, '/')) and M.code in <valueset>
                    Retrieve mrRetrieve = buildRetrieve(ctx, useStrictRetrieveTyping, namedType, classType, null, null, null, null, null, null);
                    retrieves.add(mrRetrieve);
                    mrRetrieve.setResultType(new ListType((DataType) namedType));
                    DataType mDataType = libraryBuilder.resolveTypeName(model, "Medication");
                    ClassType mClassType = (ClassType)mDataType;
                    NamedType mNamedType = mClassType;
                    Retrieve mRetrieve = buildRetrieve(ctx, useStrictRetrieveTyping, mNamedType, mClassType, null, null, null, null, null, null);
                    retrieves.add(mRetrieve);
                    mRetrieve.setResultType(new ListType((DataType) namedType));
                    Query q = of.createQuery();
                    AliasedQuerySource aqs = of.createAliasedQuerySource().withExpression(mrRetrieve).withAlias("MR");
                    track(aqs, ctx);
                    aqs.setResultType(aqs.getExpression().getResultType());
                    q.getSource().add(aqs);
                    track(q, ctx);
                    q.setResultType(aqs.getResultType());
                    With w = of.createWith().withExpression(mRetrieve).withAlias("M");
                    track(w, ctx);
                    w.setResultType(w.getExpression().getResultType());
                    q.getRelationship().add(w);
                    String idPath = "id";
                    DataType idType = libraryBuilder.resolvePath(mDataType, idPath);
                    Property idProperty = libraryBuilder.buildProperty("M", idPath, false, idType);
                    String refPath = "medication.reference";
                    DataType refType = libraryBuilder.resolvePath(dataType, refPath);
                    Property refProperty = libraryBuilder.buildProperty("MR", refPath, false, refType);
                    Split split = of.createSplit().withStringToSplit(refProperty).withSeparator(libraryBuilder.createLiteral("/"));
                    libraryBuilder.resolveCall("System", "Split", new SplitInvocation(split));
                    Last last = of.createLast().withSource(split);
                    libraryBuilder.resolveCall("System", "Last", new LastInvocation(last));
                    Equal e = of.createEqual().withOperand(idProperty, last);
                    libraryBuilder.resolveBinaryCall("System", "Equal", e);

                    DataType mCodeType = libraryBuilder.resolvePath((DataType)mNamedType, "code");
                    Property mProperty = of.createProperty().withPath("code");
                    mProperty.setResultType(mCodeType);
                    String mCodeComparator = "~";
                    if (terminology.getResultType() instanceof ListType) {
                        mCodeComparator = "in";

                    }
                    else if (libraryBuilder.isCompatibleWith("1.5")) {
                        mCodeComparator = terminology.getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary")) ? "in" : "~";
                    }

                    Expression terminologyComparison = null;
                    if (mCodeComparator.equals("in")) {
                        terminologyComparison = libraryBuilder.resolveIn(mProperty, terminology);
                    }
                    else {
                        BinaryExpression equivalent = of.createEquivalent().withOperand(mProperty, terminology);
                        libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent);
                        terminologyComparison = equivalent;
                    }
                    And a = of.createAnd().withOperand(e, terminologyComparison);
                    libraryBuilder.resolveBinaryCall("System", "And", a);
                    w.withSuchThat(a);

                    if (result == null) {
                        result = q;
                    }
                    else {
                        track(q, ctx);
                        result = libraryBuilder.resolveUnion(result, q);
                    }
                }
                else {
                    Retrieve retrieve = buildRetrieve(ctx, useStrictRetrieveTyping, namedType, classType, codePath,
                            codeComparator, property, propertyType, propertyException, terminology);
                    retrieves.add(retrieve);
                    retrieve.setResultType(new ListType((DataType) namedType));

                    if (result == null) {
                        result = retrieve;
                    } else {
                        // Should only include the result if it resolved appropriately with the codeComparator
                        // Allowing it to go through for now
                        //if (retrieve.getCodeProperty() != null && retrieve.getCodeComparator() != null && retrieve.getCodes() != null) {
                        track(retrieve, ctx);
                        result = libraryBuilder.resolveUnion(result, retrieve);
                        //}
                    }
                }
            }
        }
        else {
            Retrieve retrieve = buildRetrieve(ctx, useStrictRetrieveTyping, namedType, classType, codePath,
                    codeComparator, property, property != null ? property.getResultType() : null, propertyException,
                    terminology);
            retrieves.add(retrieve);
            retrieve.setResultType(new ListType((DataType) namedType));
            result = retrieve;
        }

        return result;
    }

    private Retrieve buildRetrieve(cqlParser.RetrieveContext ctx, boolean useStrictRetrieveTyping, NamedType namedType,
                                   ClassType classType, String codePath, String codeComparator, Property property,
                                   DataType propertyType, Exception propertyException, Expression terminology) {

        Retrieve retrieve = of.createRetrieve()
                .withDataType(libraryBuilder.dataTypeToQName((DataType)namedType))
                .withTemplateId(classType.getIdentifier())
                .withCodeProperty(codePath);

        if (ctx.contextIdentifier() != null) {
            @SuppressWarnings("unchecked")
            List<String> identifiers = (List<String>)visit(ctx.contextIdentifier());
            Expression contextExpression = resolveQualifiedIdentifier(identifiers);
            retrieve.setContext(contextExpression);
        }

        if (terminology != null) {
            // Resolve the terminology target using an in or ~ operator
            try {
                if (codeComparator == null) {
                    codeComparator = "~";
                    if (terminology.getResultType() instanceof ListType) {
                        codeComparator = "in";
                }
                    else if (libraryBuilder.isCompatibleWith("1.5")) {
                        if (propertyType != null && propertyType.isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary"))) {
                            codeComparator = terminology.getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary")) ? "~" : "contains";
                        }
                        else {
                            codeComparator = terminology.getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary")) ? "in" : "~";
                        }
                    }
                }

                if (property == null) {
                    throw propertyException;
                }

                switch (codeComparator) {
                    case "in": {
                        Expression in = libraryBuilder.resolveIn(property, terminology);
                        if (in instanceof In) {
                            retrieve.setCodes(((In) in).getOperand().get(1));
                        } else if (in instanceof InValueSet) {
                            retrieve.setCodes(((InValueSet) in).getValueset());
                        } else if (in instanceof InCodeSystem) {
                            retrieve.setCodes(((InCodeSystem) in).getCodesystem());
                        } else if (in instanceof AnyInValueSet) {
                            retrieve.setCodes(((AnyInValueSet) in).getValueset());
                        } else if (in instanceof AnyInCodeSystem) {
                            retrieve.setCodes(((AnyInCodeSystem) in).getCodesystem());
                        } else {
                            // ERROR:
                            // WARNING:
                            libraryBuilder.recordParsingException(new CqlSemanticException(String.format("Unexpected membership operator %s in retrieve", in.getClass().getSimpleName()),
                                    useStrictRetrieveTyping ? CqlCompilerException.ErrorSeverity.Error : CqlCompilerException.ErrorSeverity.Warning,
                                    getTrackBack(ctx)));
                        }
                    }
                    break;

                    case "contains": {
                        Expression contains = libraryBuilder.resolveContains(property, terminology);
                        if (contains instanceof Contains) {
                            retrieve.setCodes(((Contains)contains).getOperand().get(1));
                        }
                        // TODO: Introduce support for the contains operator to make this possible to support with a retrieve (direct-reference code negation)
                        // ERROR:
                        libraryBuilder.recordParsingException(new CqlSemanticException("Terminology resolution using contains is not supported at this time. Use a where clause with an in operator instead.",
                                useStrictRetrieveTyping ? CqlCompilerException.ErrorSeverity.Error : CqlCompilerException.ErrorSeverity.Warning,
                                getTrackBack(ctx)));
                    }
                    break;

                    case "~": {
                        // Resolve with equivalent to verify the type of the target
                        BinaryExpression equivalent = of.createEquivalent().withOperand(property, terminology);
                        libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent);

                        // Automatically promote to a list for use in the retrieve target
                        if (!(equivalent.getOperand().get(1).getResultType() instanceof ListType
                                || (libraryBuilder.isCompatibleWith("1.5")
                                && equivalent.getOperand().get(1).getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary"))))) {
                            retrieve.setCodes(libraryBuilder.resolveToList(equivalent.getOperand().get(1)));
                        }
                        else {
                            retrieve.setCodes(equivalent.getOperand().get(1));
                        }
                    }
                    break;

                    case "=": {
                        // Resolve with equality to verify the type of the source and target
                        BinaryExpression equal = of.createEqual().withOperand(property, terminology);
                        libraryBuilder.resolveBinaryCall("System", "Equal", equal);

                        // Automatically promote to a list for use in the retrieve target
                        if (!(equal.getOperand().get(1).getResultType() instanceof ListType
                                || (libraryBuilder.isCompatibleWith("1.5")
                                && equal.getOperand().get(1).getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary"))))) {
                            retrieve.setCodes(libraryBuilder.resolveToList(equal.getOperand().get(1)));
                        }
                        else {
                            retrieve.setCodes(equal.getOperand().get(1));
                        }
                    }
                    break;

                    default:
                        // ERROR:
                        // WARNING:
                        libraryBuilder.recordParsingException(new CqlSemanticException(String.format("Unknown code comparator % in retrieve", codeComparator),
                                useStrictRetrieveTyping ? CqlCompilerException.ErrorSeverity.Error : CqlCompilerException.ErrorSeverity.Warning,
                                getTrackBack(ctx.codeComparator())));
                }

                retrieve.setCodeComparator(codeComparator);

                // Verify that the type of the terminology target is a List<Code>
                // Due to implicit conversion defined by specific models, the resolution path above may result in a List<Concept>
                // In that case, convert to a list of code (Union the Code elements of the Concepts in the list)
                if (retrieve.getCodes() != null && retrieve.getCodes().getResultType() != null && retrieve.getCodes().getResultType() instanceof ListType
                    && ((ListType)retrieve.getCodes().getResultType()).getElementType().equals(libraryBuilder.resolveTypeName("System", "Concept"))) {
                    if (retrieve.getCodes() instanceof ToList) {
                        // ToList will always have a single argument
                        ToList toList = (ToList)retrieve.getCodes();
                        // If that argument is a ToConcept, replace the ToList argument with the code (skip the implicit conversion, the data access layer is responsible for it)
                        if (toList.getOperand() instanceof ToConcept) {
                            toList.setOperand(((ToConcept)toList.getOperand()).getOperand());
                        }
                        else {
                            // Otherwise, access the codes property of the resulting Concept
                            Expression codesAccessor = libraryBuilder.buildProperty(toList.getOperand(), "codes", false, toList.getOperand().getResultType());
                            retrieve.setCodes(codesAccessor);
                        }
                    }
                    else {
                        // WARNING:
                        libraryBuilder.recordParsingException(new CqlSemanticException("Terminology target is a list of concepts, but expects a list of codes",
                                CqlCompilerException.ErrorSeverity.Warning, getTrackBack(ctx)));
                    }
                }
            }
            catch (Exception e) {
                // If something goes wrong attempting to resolve, just set to the expression and report it as a warning,
                // it shouldn't prevent translation unless the modelinfo indicates strict retrieve typing
                if ((libraryBuilder.isCompatibleWith("1.5") && !(terminology.getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "Vocabulary"))))
                    || (!libraryBuilder.isCompatibleWith("1.5") && !(terminology.getResultType() instanceof ListType))) {
                    retrieve.setCodes(libraryBuilder.resolveToList(terminology));
                }
                else {
                    retrieve.setCodes(terminology);
                }
                retrieve.setCodeComparator(codeComparator);
                // ERROR:
                // WARNING:
                libraryBuilder.recordParsingException(new CqlSemanticException("Could not resolve membership operator for terminology target of the retrieve.",
                        useStrictRetrieveTyping ? CqlCompilerException.ErrorSeverity.Error : CqlCompilerException.ErrorSeverity.Warning,
                        getTrackBack(ctx), e));
            }
        }

        return retrieve;
    }

    @Override
    public Object visitSourceClause(cqlParser.SourceClauseContext ctx) {
        boolean hasFrom = "from".equals(ctx.getChild(0).getText());
        if (!hasFrom && isFromKeywordRequired()) {
            throw new IllegalArgumentException("The from keyword is required for queries.");
        }

        List<AliasedQuerySource> sources = new ArrayList<>();
        for (cqlParser.AliasedQuerySourceContext source : ctx.aliasedQuerySource()) {
            if (sources.size() > 0 && !hasFrom) {
                throw new IllegalArgumentException("The from keyword is required for multi-source queries.");
            }
            sources.add((AliasedQuerySource) visit(source));
        }
        return sources;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitQuery(cqlParser.QueryContext ctx) {
        QueryContext queryContext = new QueryContext();
        libraryBuilder.pushQueryContext(queryContext);
        List<AliasedQuerySource> sources = null;
        try {

            queryContext.enterSourceClause();
            try {
                sources = (List<AliasedQuerySource>)visit(ctx.sourceClause());
            }
            finally {
                queryContext.exitSourceClause();
            }

            queryContext.addPrimaryQuerySources(sources);

            for (AliasedQuerySource source : sources) {
                libraryBuilder.pushIdentifierForHiding(source.getAlias(), source);
            }

            // If we are evaluating a population-level query whose source ranges over any patient-context expressions,
            // then references to patient context expressions within the iteration clauses of the query can be accessed
            // at the patient, rather than the population, context.
            boolean expressionContextPushed = false;
            /* TODO: Address the issue of referencing multiple context expressions within a query (or even expression in general)
            if (libraryBuilder.inUnfilteredContext() && queryContext.referencesSpecificContext()) {
                libraryBuilder.pushExpressionContext("Patient");
                expressionContextPushed = true;
            }
            */
            List<LetClause> dfcx = null;
            try {
                dfcx = ctx.letClause() != null ? (List<LetClause>) visit(ctx.letClause()) : null;

                if (dfcx != null) {
                    for (LetClause letClause : dfcx) {
                        libraryBuilder.pushIdentifierForHiding(letClause.getIdentifier(), letClause);
                    }
                }

                List<RelationshipClause> qicx = new ArrayList<>();
                if (ctx.queryInclusionClause() != null) {
                    for (cqlParser.QueryInclusionClauseContext queryInclusionClauseContext : ctx.queryInclusionClause()) {
                        qicx.add((RelationshipClause) visit(queryInclusionClauseContext));
                    }
                }

                Expression where = ctx.whereClause() != null ? (Expression) visit(ctx.whereClause()) : null;
                if (getDateRangeOptimization() && where != null) {
                    for (AliasedQuerySource aqs : sources) {
                        where = optimizeDateRangeInQuery(where, aqs);
                    }
                }

                ReturnClause ret = ctx.returnClause() != null ? (ReturnClause) visit(ctx.returnClause()) : null;
                AggregateClause agg = ctx.aggregateClause() != null ? (AggregateClause) visit(ctx.aggregateClause()) : null;

                if ((agg == null) && (ret == null) && (sources.size() > 1)) {
                    ret = of.createReturnClause()
                            .withDistinct(true);

                    Tuple returnExpression = of.createTuple();
                    TupleType returnType = new TupleType();
                    for (AliasedQuerySource aqs : sources) {
                        TupleElement element =
                                of.createTupleElement()
                                        .withName(aqs.getAlias())
                                        .withValue(of.createAliasRef().withName(aqs.getAlias()));
                        DataType sourceType = aqs.getResultType() instanceof ListType ? ((ListType)aqs.getResultType()).getElementType() : aqs.getResultType();
                        element.getValue().setResultType(sourceType); // Doesn't use the fluent API to avoid casting
                        element.setResultType(element.getValue().getResultType());
                        returnType.addElement(new TupleTypeElement(element.getName(), element.getResultType()));
                        returnExpression.getElement().add(element);
                    }

                    returnExpression.setResultType(queryContext.isSingular() ? returnType : new ListType(returnType));
                    ret.setExpression(returnExpression);
                    ret.setResultType(returnExpression.getResultType());
                }

                queryContext.removeQuerySources(sources);
                if (dfcx != null) {
                    queryContext.removeLetClauses(dfcx);
                }

                DataType queryResultType = null;
                if (agg != null) {
                    queryResultType = agg.getResultType();
                }
                else if (ret != null) {
                    queryResultType = ret.getResultType();
                }
                else {
                    queryResultType = sources.get(0).getResultType();
                }

                SortClause sort = null;
                if (agg == null) {
                    queryContext.setResultElementType(queryContext.isSingular() ? null : ((ListType) queryResultType).getElementType());
                    if (ctx.sortClause() != null) {
                        if (queryContext.isSingular()) {
                            // ERROR:
                            throw new IllegalArgumentException("Sort clause cannot be used in a singular query.");
                        }
                        queryContext.enterSortClause();
                        try {
                            sort = (SortClause) visit(ctx.sortClause());
                            // Validate that the sort can be performed based on the existence of comparison operators for all types involved
                            for (SortByItem sortByItem : sort.getBy()) {
                                if (sortByItem instanceof ByDirection) {
                                    // validate that there is a comparison operator defined for the result element type of the query context
                                    libraryBuilder.verifyComparable(queryContext.getResultElementType());
                                } else {
                                    libraryBuilder.verifyComparable(sortByItem.getResultType());
                                }
                            }
                        } finally {
                            queryContext.exitSortClause();
                        }
                    }
                }
                else {
                    if (ctx.sortClause() != null) {
                        // ERROR:
                        throw new IllegalArgumentException("Sort clause cannot be used in an aggregate query.");
                    }
                }

                Query query = of.createQuery()
                        .withSource(sources)
                        .withLet(dfcx)
                        .withRelationship(qicx)
                        .withWhere(where)
                        .withReturn(ret)
                        .withAggregate(agg)
                        .withSort(sort);

                query.setResultType(queryResultType);
                return query;
            }
            finally {
                if (expressionContextPushed) {
                    libraryBuilder.popExpressionContext();
                }
                if (dfcx != null) {
                    for (LetClause letClause : dfcx) {
                        libraryBuilder.popIdentifierForHiding();
                    }
                }

            }

        } finally {
            libraryBuilder.popQueryContext();
            if (sources != null) {
                for (AliasedQuerySource source : sources) {
                    libraryBuilder.popIdentifierForHiding();
                }
            }
        }
    }

    // TODO: Expand this optimization to work the DateLow/DateHigh property attributes

    /**
     * Some systems may wish to optimize performance by restricting retrieves with available date ranges.  Specifying
     * date ranges in a retrieve was removed from the CQL grammar, but it is still possible to extract date ranges from
     * the where clause and put them in the Retrieve in ELM.  The <code>optimizeDateRangeInQuery</code> method
     * attempts to do this automatically.  If optimization is possible, it will remove the corresponding "during" from
     * the where clause and insert the date range into the Retrieve.
     *
     * @param aqs   the AliasedQuerySource containing the ClinicalRequest to possibly refactor a date range into.
     * @param where the Where clause to search for potential date range optimizations
     * @return the where clause with optimized "durings" removed, or <code>null</code> if there is no longer a Where
     * clause after optimization.
     */
    public Expression optimizeDateRangeInQuery(Expression where, AliasedQuerySource aqs) {
        if (aqs.getExpression() instanceof Retrieve) {
            Retrieve retrieve = (Retrieve) aqs.getExpression();
            String alias = aqs.getAlias();
            if ((where instanceof IncludedIn || where instanceof In) && attemptDateRangeOptimization((BinaryExpression) where, retrieve, alias)) {
                where = null;
            }
            else if (where instanceof And && attemptDateRangeOptimization((And) where, retrieve, alias)) {
                // Now optimize out the trues from the Ands
                where = consolidateAnd((And) where);
            }
        }
        return where;
    }

    /**
     * Test a <code>BinaryExpression</code> expression and determine if it is suitable to be refactored into the
     * <code>Retrieve</code> as a date range restriction.  If so, adjust the <code>Retrieve</code>
     * accordingly and return <code>true</code>.
     *
     * @param during   the <code>BinaryExpression</code> expression to potentially refactor into the <code>Retrieve</code>
     * @param retrieve the <code>Retrieve</code> to add qualifying date ranges to (if applicable)
     * @param alias    the alias of the <code>Retrieve</code> in the query.
     * @return <code>true</code> if the date range was set in the <code>Retrieve</code>; <code>false</code>
     * otherwise.
     */
    private boolean attemptDateRangeOptimization(BinaryExpression during, Retrieve retrieve, String alias) {
        if (retrieve.getDateProperty() != null || retrieve.getDateRange() != null) {
            return false;
        }

        Expression left = during.getOperand().get(0);
        Expression right = during.getOperand().get(1);

        String propertyPath = getPropertyPath(left, alias);
        if (propertyPath != null && isRHSEligibleForDateRangeOptimization(right)) {
            retrieve.setDateProperty(propertyPath);
            retrieve.setDateRange(right);
            return true;
        }

        return false;
    }

    /**
     * Collapse a property path expression back to it's qualified form for use as the path attribute of the retrieve.
     *
     * @param reference the <code>Expression</code> to collapse
     * @param alias    the alias of the <code>Retrieve</code> in the query.
     * @return The collapsed path
     * operands (or sub-operands) were modified; <code>false</code> otherwise.
     */
    private String getPropertyPath(Expression reference, String alias) {
        reference = getConversionReference(reference);
        reference = getChoiceSelection(reference);
        if (reference instanceof Property) {
            Property property = (Property)reference;
            if (alias.equals(property.getScope())) {
                return property.getPath();
            }
            else if (property.getSource() != null) {
                String subPath = getPropertyPath(property.getSource(), alias);
                if (subPath != null) {
                    return String.format("%s.%s", subPath, property.getPath());
                }
            }
        }

        return null;
    }

    /**
     * If this is a conversion operator, return the argument of the conversion, on the grounds that the date range optimization
     * should apply through a conversion (i.e. it is an order-preserving conversion)
     *
     * @param reference the <code>Expression</code> to examine
     * @return The argument to the conversion operator if there was one, otherwise, the given <code>reference</code>
     */
    private Expression getConversionReference(Expression reference) {
        if (reference instanceof FunctionRef) {
            FunctionRef functionRef = (FunctionRef)reference;
            if (functionRef.getOperand().size() == 1 && functionRef.getResultType() != null && functionRef.getOperand().get(0).getResultType() != null) {
                Operator o = this.libraryBuilder.getConversionMap().getConversionOperator(functionRef.getOperand().get(0).getResultType(), functionRef.getResultType());
                if (o != null && o.getLibraryName() != null && o.getLibraryName().equals(functionRef.getLibraryName())
                        && o.getName() != null && o.getName().equals(functionRef.getName())) {
                    return functionRef.getOperand().get(0);
                }
            }
        }

        return reference;
    }

    /**
     * If this is a choice selection, return the argument of the choice selection, on the grounds that the date range optimization
     * should apply through the cast (i.e. it is an order-preserving cast)

     * @param reference the <code>Expression</code> to examine
     * @return The argument to the choice selection (i.e. As) if there was one, otherwise, the given <code>reference</code>
     */
    private Expression getChoiceSelection(Expression reference) {
        if (reference instanceof As) {
            As as = (As)reference;
            if (as.getOperand() != null && as.getOperand().getResultType() instanceof ChoiceType) {
                return as.getOperand();
            }
        }

        return reference;
    }

    /**
     * Test an <code>And</code> expression and determine if it contains any operands (first-level or nested deeper)
     * than are <code>IncludedIn</code> expressions that can be refactored into a <code>Retrieve</code>.  If so,
     * adjust the <code>Retrieve</code> accordingly and reset the corresponding operand to a literal
     * <code>true</code>.  This <code>and</code> branch containing a <code>true</code> can be further consolidated
     * later.
     *
     * @param and      the <code>And</code> expression containing operands to potentially refactor into the
     *                 <code>Retrieve</code>
     * @param retrieve the <code>Retrieve</code> to add qualifying date ranges to (if applicable)
     * @param alias    the alias of the <code>Retrieve</code> in the query.
     * @return <code>true</code> if the date range was set in the <code>Retrieve</code> and the <code>And</code>
     * operands (or sub-operands) were modified; <code>false</code> otherwise.
     */
    private boolean attemptDateRangeOptimization(And and, Retrieve retrieve, String alias) {
        if (retrieve.getDateProperty() != null || retrieve.getDateRange() != null) {
            return false;
        }

        for (int i = 0; i < and.getOperand().size(); i++) {
            Expression operand = and.getOperand().get(i);
            if ((operand instanceof IncludedIn || operand instanceof In) && attemptDateRangeOptimization((BinaryExpression) operand, retrieve, alias)) {
                // Replace optimized part in And with true -- to be optimized out later
                and.getOperand().set(i, libraryBuilder.createLiteral(true));
                return true;
            } else if (operand instanceof And && attemptDateRangeOptimization((And) operand, retrieve, alias)) {
                return true;
            }
        }

        return false;
    }

    /**
     * If any branches in the <code>And</code> tree contain a <code>true</code>, refactor it out.
     *
     * @param and the <code>And</code> tree to attempt to consolidate
     * @return the potentially consolidated <code>And</code>
     */
    private Expression consolidateAnd(And and) {
        Expression result = and;
        Expression lhs = and.getOperand().get(0);
        Expression rhs = and.getOperand().get(1);
        if (isBooleanLiteral(lhs, true)) {
            result = rhs;
        } else if (isBooleanLiteral(rhs, true)) {
            result = lhs;
        } else if (lhs instanceof And) {
            and.getOperand().set(0, consolidateAnd((And) lhs));
        } else if (rhs instanceof And) {
            and.getOperand().set(1, consolidateAnd((And) rhs));
        }

        return result;
    }

    /**
     * Determine if the right-hand side of an <code>IncludedIn</code> expression can be refactored into the date range
     * of a <code>Retrieve</code>.  Currently, refactoring is only supported when the RHS is a literal
     * DateTime interval, a literal DateTime, a parameter representing a DateTime interval or a DateTime, or an
     * expression reference representing a DateTime interval or a DateTime.
     *
     * @param rhs the right-hand side of the <code>IncludedIn</code> to test for potential optimization
     * @return <code>true</code> if the RHS supports refactoring to a <code>Retrieve</code>, <code>false</code>
     * otherwise.
     */
    private boolean isRHSEligibleForDateRangeOptimization(Expression rhs) {
        return
            rhs.getResultType().isSubTypeOf(libraryBuilder.resolveTypeName("System", "DateTime"))
                || rhs.getResultType().isSubTypeOf(new IntervalType(libraryBuilder.resolveTypeName("System", "DateTime")));

        // BTR: The only requirement for the optimization is that the expression be of type DateTime or Interval<DateTime>
        // Whether or not the expression can be statically evaluated (literal, in the loose sense of the word) is really
        // a function of the engine in determining the "initial" data requirements, versus subsequent data requirements
//        Element targetElement = rhs;
//        if (rhs instanceof ParameterRef) {
//            String paramName = ((ParameterRef) rhs).getName();
//            for (ParameterDef def : getLibrary().getParameters().getDef()) {
//                if (paramName.equals(def.getName())) {
//                    targetElement = def.getParameterTypeSpecifier();
//                    if (targetElement == null) {
//                        targetElement = def.getDefault();
//                    }
//                    break;
//                }
//            }
//        } else if (rhs instanceof ExpressionRef && !(rhs instanceof FunctionRef)) {
//            // TODO: Support forward declaration, if necessary
//            String expName = ((ExpressionRef) rhs).getName();
//            for (ExpressionDef def : getLibrary().getStatements().getDef()) {
//                if (expName.equals(def.getName())) {
//                    targetElement = def.getExpression();
//                }
//            }
//        }
//
//        boolean isEligible = false;
//        if (targetElement instanceof DateTime) {
//            isEligible = true;
//        } else if (targetElement instanceof Interval) {
//            Interval ivl = (Interval) targetElement;
//            isEligible = (ivl.getLow() != null && ivl.getLow() instanceof DateTime) || (ivl.getHigh() != null && ivl.getHigh() instanceof DateTime);
//        } else if (targetElement instanceof IntervalTypeSpecifier) {
//            IntervalTypeSpecifier spec = (IntervalTypeSpecifier) targetElement;
//            isEligible = isDateTimeTypeSpecifier(spec.getPointType());
//        } else if (targetElement instanceof NamedTypeSpecifier) {
//            isEligible = isDateTimeTypeSpecifier(targetElement);
//        }
//        return isEligible;
    }

    private boolean isDateTimeTypeSpecifier(Element e) {
        return e.getResultType().equals(libraryBuilder.resolveTypeName("System", "DateTime"));
    }

    @Override
    public Object visitLetClause(cqlParser.LetClauseContext ctx) {
        List<LetClause> letClauseItems = new ArrayList<>();
        for (cqlParser.LetClauseItemContext letClauseItem : ctx.letClauseItem()) {
            letClauseItems.add((LetClause) visit(letClauseItem));
        }
        return letClauseItems;
    }

    @Override
    public Object visitLetClauseItem(cqlParser.LetClauseItemContext ctx) {
        LetClause letClause = of.createLetClause().withExpression(parseExpression(ctx.expression()))
                .withIdentifier(parseString(ctx.identifier()));
        letClause.setResultType(letClause.getExpression().getResultType());
        libraryBuilder.peekQueryContext().addLetClause(letClause);
        return letClause;
    }

    @Override
    public Object visitAliasedQuerySource(cqlParser.AliasedQuerySourceContext ctx) {
        AliasedQuerySource source = of.createAliasedQuerySource().withExpression(parseExpression(ctx.querySource()))
                .withAlias(parseString(ctx.alias()));
        source.setResultType(source.getExpression().getResultType());
        return source;
    }

    @Override
    public Object visitWithClause(cqlParser.WithClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        libraryBuilder.peekQueryContext().addRelatedQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            DataTypes.verifyType(expression.getResultType(), libraryBuilder.resolveTypeName("System", "Boolean"));
            RelationshipClause result = of.createWith();
            result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
            result.setResultType(aqs.getResultType());
            return result;
        } finally {
            libraryBuilder.peekQueryContext().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWithoutClause(cqlParser.WithoutClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        libraryBuilder.peekQueryContext().addRelatedQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            DataTypes.verifyType(expression.getResultType(), libraryBuilder.resolveTypeName("System", "Boolean"));
            RelationshipClause result = of.createWithout();
            result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
            result.setResultType(aqs.getResultType());
            return result;
        } finally {
            libraryBuilder.peekQueryContext().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWhereClause(cqlParser.WhereClauseContext ctx) {
        Expression result = (Expression)visit(ctx.expression());
        DataTypes.verifyType(result.getResultType(), libraryBuilder.resolveTypeName("System", "Boolean"));
        return result;
    }

    @Override
    public Object visitReturnClause(cqlParser.ReturnClauseContext ctx) {
        ReturnClause returnClause = of.createReturnClause();
        if (ctx.getChild(1) instanceof TerminalNode) {
            switch (ctx.getChild(1).getText()) {
                case "all":
                    returnClause.setDistinct(false);
                    break;
                case "distinct":
                    returnClause.setDistinct(true);
                    break;
                default:
                    break;
            }
        }

        returnClause.setExpression(parseExpression(ctx.expression()));
        returnClause.setResultType(libraryBuilder.peekQueryContext().isSingular()
                ? returnClause.getExpression().getResultType()
                : new ListType(returnClause.getExpression().getResultType()));

        return returnClause;
    }

    @Override
    public Object visitStartingClause(cqlParser.StartingClauseContext ctx) {
        if (ctx.simpleLiteral() != null) {
            return visit(ctx.simpleLiteral());
        }

        if (ctx.quantity() != null) {
            return visit(ctx.quantity());
        }

        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }

        return null;
    }

    @Override
    public Object visitAggregateClause(cqlParser.AggregateClauseContext ctx) {
        libraryBuilder.checkCompatibilityLevel("Aggregate query clause", "1.5");
        AggregateClause aggregateClause = of.createAggregateClause();
        if (ctx.getChild(1) instanceof TerminalNode) {
            switch (ctx.getChild(1).getText()) {
                case "all":
                    aggregateClause.setDistinct(false);
                    break;
                case "distinct":
                    aggregateClause.setDistinct(true);
                    break;
                default:
                    break;
            }
        }

        if (ctx.startingClause() != null) {
            aggregateClause.setStarting(parseExpression(ctx.startingClause()));
        }

        // If there is a starting, that's the type of the var
        // If there's not a starting, push an Any and then attempt to evaluate (might need a type hint here)
        aggregateClause.setIdentifier(parseString(ctx.identifier()));

        Expression accumulator = null;
        if (aggregateClause.getStarting() != null) {
            accumulator = libraryBuilder.buildNull(aggregateClause.getStarting().getResultType());
        }
        else {
            accumulator = libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Any"));
        }

        LetClause letClause = of.createLetClause().withExpression(accumulator)
                .withIdentifier(aggregateClause.getIdentifier());
        letClause.setResultType(letClause.getExpression().getResultType());
        libraryBuilder.peekQueryContext().addLetClause(letClause);

        aggregateClause.setExpression(parseExpression(ctx.expression()));
        aggregateClause.setResultType(aggregateClause.getExpression().getResultType());

        if (aggregateClause.getStarting() == null) {
            accumulator.setResultType(aggregateClause.getResultType());
            aggregateClause.setStarting(accumulator);
        }

        return aggregateClause;
    }

    @Override
    public SortDirection visitSortDirection(cqlParser.SortDirectionContext ctx) {
        return SortDirection.fromValue(ctx.getText());
    }

    private SortDirection parseSortDirection(cqlParser.SortDirectionContext ctx) {
        if (ctx != null) {
            return visitSortDirection(ctx);
        }

        return SortDirection.ASC;
    }

    @Override
    public SortByItem visitSortByItem(cqlParser.SortByItemContext ctx) {
        Expression sortExpression = parseExpression(ctx.expressionTerm());
        if (sortExpression instanceof IdentifierRef) {
            return (SortByItem)of.createByColumn()
                    .withPath(((IdentifierRef)sortExpression).getName())
                    .withDirection(parseSortDirection(ctx.sortDirection()))
                    .withResultType(sortExpression.getResultType());
        }

        return (SortByItem)of.createByExpression()
                .withExpression(sortExpression)
                .withDirection(parseSortDirection(ctx.sortDirection()))
                .withResultType(sortExpression.getResultType());
    }

    @Override
    public Object visitSortClause(cqlParser.SortClauseContext ctx) {
        if (ctx.sortDirection() != null) {
            return of.createSortClause()
                    .withBy(of.createByDirection().withDirection(parseSortDirection(ctx.sortDirection())));
        }

        List<SortByItem> sortItems = new ArrayList<>();
        if (ctx.sortByItem() != null) {
            for (cqlParser.SortByItemContext sortByItemContext : ctx.sortByItem()) {
                sortItems.add((SortByItem) visit(sortByItemContext));
            }
        }

        return of.createSortClause().withBy(sortItems);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitQuerySource(cqlParser.QuerySourceContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.retrieve() != null) {
            return visit(ctx.retrieve());
        } else {
            List<String> identifiers = (List<String>) visit(ctx.qualifiedIdentifierExpression());
            return resolveQualifiedIdentifier(identifiers);
        }
    }

    @Override
    public Object visitIndexedExpressionTerm(cqlParser.IndexedExpressionTermContext ctx) {
        Indexer indexer = of.createIndexer()
                .withOperand(parseExpression(ctx.expressionTerm()))
                .withOperand(parseExpression(ctx.expression()));

        // TODO: Support zero-based indexers as defined by the isZeroBased attribute
        libraryBuilder.resolveBinaryCall("System", "Indexer", indexer);
        return indexer;
    }

    @Override
    public Expression visitInvocationExpressionTerm(cqlParser.InvocationExpressionTermContext ctx) {
        Expression left = parseExpression(ctx.expressionTerm());
        libraryBuilder.pushExpressionTarget(left);
        try {
            return (Expression)visit(ctx.qualifiedInvocation());
        }
        finally {
            libraryBuilder.popExpressionTarget();
        }
    }

    @Override
    public Expression visitExternalConstant(cqlParser.ExternalConstantContext ctx) {
        return libraryBuilder.resolveIdentifier(ctx.getText(), true);
    }

    @Override
    public Expression visitThisInvocation(cqlParser.ThisInvocationContext ctx) {
        return libraryBuilder.resolveIdentifier(ctx.getText(), true);
    }

    @Override
    public Expression visitMemberInvocation(cqlParser.MemberInvocationContext ctx) {
        String identifier = parseString(ctx.referentialIdentifier());
        return resolveMemberIdentifier(identifier);
    }

    @Override
    public Expression visitQualifiedMemberInvocation(cqlParser.QualifiedMemberInvocationContext ctx) {
        String identifier = parseString(ctx.referentialIdentifier());
        return resolveMemberIdentifier(identifier);
    }

    public Expression resolveQualifiedIdentifier(List<String> identifiers) {
        Expression current = null;
        for (String identifier : identifiers) {
            if (current == null) {
                current = resolveIdentifier(identifier);
            } else {
                current = libraryBuilder.resolveAccessor(current, identifier);
            }
        }

        return current;
    }

    public Expression resolveMemberIdentifier(String identifier) {
        if (libraryBuilder.hasExpressionTarget()) {
            Expression target = libraryBuilder.popExpressionTarget();
            try {
                return libraryBuilder.resolveAccessor(target, identifier);
            }
            finally {
                libraryBuilder.pushExpressionTarget(target);
            }
        }

        return resolveIdentifier(identifier);
    }

    private Expression resolveIdentifier(String identifier) {
        // If the identifier cannot be resolved in the library builder, check for forward declarations for expressions and parameters
        Expression result = libraryBuilder.resolveIdentifier(identifier, false);
        if (result == null) {
            ExpressionDefinitionInfo expressionInfo = libraryInfo.resolveExpressionReference(identifier);
            if (expressionInfo != null) {
                String saveContext = saveCurrentContext(expressionInfo.getContext());
                try {
                    Stack<Chunk> saveChunks = chunks;
                    chunks = new Stack<Chunk>();
                    forwards.push(expressionInfo);
                    try {
                        if (expressionInfo.getDefinition() == null) {
                            // ERROR:
                            throw new IllegalArgumentException(String.format("Could not validate reference to expression %s because its definition contains errors.",
                                    expressionInfo.getName()));
                        }

                        // Have to call the visit to get the outer processing to occur
                        visit(expressionInfo.getDefinition());
                    }
                    finally {
                        chunks = saveChunks;
                        forwards.pop();
                    }
                } finally {
                    setCurrentContext(saveContext);
                }
            }

            ParameterDefinitionInfo parameterInfo = libraryInfo.resolveParameterReference(identifier);
            if (parameterInfo != null) {
                visitParameterDefinition(parameterInfo.getDefinition());
            }
            result = libraryBuilder.resolveIdentifier(identifier, true);
        }

        return result;
    }

    private String ensureSystemFunctionName(String libraryName, String functionName) {
        if (libraryName == null || libraryName.equals("System")) {
            // Because these functions can be both a keyword and the name of a method, they can be resolved by the
            // parser as a function, instead of as the keyword-based parser rule. In this case, the function
            // name needs to be translated to the System function name in order to resolve.
            switch (functionName) {
                case "contains": functionName = "Contains"; break;
                case "distinct": functionName = "Distinct"; break;
                case "exists": functionName = "Exists"; break;
                case "in": functionName = "In"; break;
                case "not": functionName = "Not"; break;
            }
        }

        return functionName;
    }

    private Expression resolveFunction(String libraryName, String functionName, cqlParser.ParamListContext paramList) {
        List<Expression> expressions = new ArrayList<Expression>();
        if (paramList != null && paramList.expression() != null) {
            for (cqlParser.ExpressionContext expressionContext : paramList.expression()) {
                expressions.add((Expression)visit(expressionContext));
            }
        }
        return resolveFunction(libraryName, functionName, expressions, true, false, false);
    }

    public Expression resolveFunction(String libraryName, String functionName, List<Expression> expressions, boolean mustResolve, boolean allowPromotionAndDemotion, boolean allowFluent) {
        if (allowFluent) {
            libraryBuilder.checkCompatibilityLevel("Fluent functions", "1.5");
        }

        functionName = ensureSystemFunctionName(libraryName, functionName);

        // 1. Ensure all overloads of the function are registered with the operator map
        // 2. Resolve the function, allowing for the case that operator map is a skeleton
        // 3. If the resolution from the operator map is a skeleton, compile the function body to determine the result type

        // Find all functionDefinitionInfo instances with the given name
        // register each functionDefinitionInfo
        if (libraryName == null || libraryName.equals("") || libraryName.equals(this.libraryInfo.getLibraryName())) {
            Iterable<FunctionDefinitionInfo> fdis = libraryInfo.resolveFunctionReference(functionName);
            if (fdis != null) {
                for (FunctionDefinitionInfo fdi : fdis) {
                    String saveContext = saveCurrentContext(fdi.getContext());
                    try {
                        registerFunctionDefinition(fdi.getDefinition());
                    } finally {
                        this.setCurrentContext(saveContext);
                    }
                }
            }
        }

        Invocation result = libraryBuilder.resolveFunction(libraryName, functionName, expressions, mustResolve, allowPromotionAndDemotion, allowFluent);

        if (result instanceof FunctionRefInvocation) {
            FunctionRefInvocation invocation = (FunctionRefInvocation)result;
            if (invocation.getResolution() != null
                    && invocation.getResolution().getOperator() != null
                    && (invocation.getResolution().getOperator().getLibraryName() == null
                        || invocation.getResolution().getOperator().getLibraryName().equals(libraryBuilder.getCompiledLibrary().getIdentifier().getId()))) {
                Operator op = invocation.getResolution().getOperator();
                FunctionHeader fh = getFunctionHeader(op);
                if (!fh.getIsCompiled()) {
                    cqlParser.FunctionDefinitionContext ctx = getFunctionDefinitionContext(fh);
                    String saveContext = saveCurrentContext(fh.getFunctionDef().getContext());
                    Stack<Chunk> saveChunks = chunks;
                    chunks = new Stack<Chunk>();
                    try {
                        FunctionDef fd = compileFunctionDefinition(ctx);
                        op.setResultType(fd.getResultType());
                        invocation.setResultType(op.getResultType());
                    }
                    finally {
                        setCurrentContext(saveContext);
                        this.chunks = saveChunks;
                    }
                }
            }
        }

        if (mustResolve) {
            // Extra internal error handling, these should never be hit if the two-phase operator compile is working as expected
            if (result == null) {
                throw new IllegalArgumentException("Internal error: could not resolve function");
            }

            if (result.getExpression() == null) {
                throw new IllegalArgumentException("Internal error: could not resolve invocation expression");
            }

            if (result.getExpression().getResultType() == null) {
                throw new IllegalArgumentException("Internal error: could not determine result type");
            }
        }

        if (result == null) {
            return null;
        }
        return result.getExpression();
    }

    public Expression resolveFunctionOrQualifiedFunction(String identifier, cqlParser.ParamListContext paramListCtx) {
        if (libraryBuilder.hasExpressionTarget()) {
            Expression target = libraryBuilder.popExpressionTarget();
            try {
                // If the target is a library reference, resolve as a standard qualified call
                if (target instanceof LibraryRef) {
                    return resolveFunction(((LibraryRef)target).getLibraryName(), identifier, paramListCtx);
                }

                // NOTE: FHIRPath method invocation
                // If the target is an expression, resolve as a method invocation
                if (target instanceof Expression && isMethodInvocationEnabled()) {
                    return systemMethodResolver.resolveMethod((Expression)target, identifier, paramListCtx, true);
                }

                if (!isMethodInvocationEnabled()) {
                    throw new CqlCompilerException(String.format("The identifier %s could not be resolved as an invocation because method-style invocation is disabled.", identifier), CqlCompilerException.ErrorSeverity.Error);
                }
                throw new IllegalArgumentException(String.format("Invalid invocation target: %s", target.getClass().getName()));
            }
            finally {
                libraryBuilder.pushExpressionTarget(target);
            }
        }

        // If we are in an implicit $this context, the function may be resolved as a method invocation
        Expression thisRef = libraryBuilder.resolveIdentifier("$this", false);
        if (thisRef != null) {
            Expression result = systemMethodResolver.resolveMethod(thisRef, identifier, paramListCtx, false);
            if (result != null) {
                return result;
            }
        }

        // If we are in an implicit context (i.e. a context named the same as a parameter), the function may be resolved as a method invocation
        ParameterRef parameterRef = libraryBuilder.resolveImplicitContext();
        if (parameterRef != null) {
            Expression result = systemMethodResolver.resolveMethod(parameterRef, identifier, paramListCtx, false);
            if (result != null) {
                return result;
            }
        }

        // If there is no target, resolve as a system function
        return resolveFunction(null, identifier, paramListCtx);
    }

    @Override
    public Expression visitFunction(cqlParser.FunctionContext ctx) {
        return resolveFunctionOrQualifiedFunction(parseString(ctx.referentialIdentifier()), ctx.paramList());
    }

    @Override
    public Expression visitQualifiedFunction(cqlParser.QualifiedFunctionContext ctx) {
        return resolveFunctionOrQualifiedFunction(parseString(ctx.identifierOrFunctionIdentifier()), ctx.paramList());
    }

    @Override
    public Object visitFunctionBody(cqlParser.FunctionBodyContext ctx) {
        return visit(ctx.expression());
    }

    private FunctionHeader getFunctionHeader(cqlParser.FunctionDefinitionContext ctx) {
        FunctionHeader fh = functionHeaders.get(ctx);
        if (fh == null) {

            final Stack<Chunk> saveChunks = chunks;
            chunks = new Stack<>();
            try {
                // Have to call the visit to allow the outer processing to occur
                fh = parseFunctionHeader(ctx);
            } finally {
                chunks = saveChunks;
            }

            functionHeaders.put(ctx, fh);
            functionDefinitions.put(fh, ctx);
            functionHeadersByDef.put(fh.getFunctionDef(), fh);
        }
        return fh;
    }

    private FunctionDef getFunctionDef(Operator op) {
        FunctionDef target = null;
        List<DataType> st = new ArrayList<>();
        for (DataType dt : op.getSignature().getOperandTypes()) {
            st.add(dt);
        }
        Iterable<FunctionDef> fds = libraryBuilder.getCompiledLibrary().resolveFunctionRef(op.getName(), st);
        for (FunctionDef fd : fds) {
            if (fd.getOperand().size() == op.getSignature().getSize()) {
                Iterator<DataType> signatureTypes = op.getSignature().getOperandTypes().iterator();
                boolean signaturesMatch = true;
                for (int i = 0; i < fd.getOperand().size(); i++) {
                    if (!DataTypes.equal(fd.getOperand().get(i).getResultType(), signatureTypes.next())) {
                        signaturesMatch = false;
                    }
                }
                if (signaturesMatch) {
                    if (target == null) {
                        target = fd;
                    }
                    else {
                        throw new IllegalArgumentException(String.format("Internal error attempting to resolve function header for %s", op.getName()));
                    }
                }
            }
        }

        return target;
    }

    private FunctionHeader getFunctionHeaderByDef(FunctionDef fd) {
        // Shouldn't need to do this, something about the hashCode implementation of FunctionDef is throwing this off,
        // Don't have time to investigate right now, this should work fine, could potentially be improved
        for (Map.Entry<FunctionDef, FunctionHeader> entry : functionHeadersByDef.entrySet()) {
            if (entry.getKey() == fd) {
                return entry.getValue();
            }
        }

        return null;
    }

    private FunctionHeader getFunctionHeader(Operator op) {
        FunctionDef fd = getFunctionDef(op);
        if (fd == null) {
            throw new IllegalArgumentException(String.format("Could not resolve function header for operator %s", op.getName()));
        }
        FunctionHeader result = getFunctionHeaderByDef(fd);
        //FunctionHeader result = functionHeadersByDef.get(fd);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve function header for operator %s", op.getName()));
        }
        return result;
    }

    private cqlParser.FunctionDefinitionContext getFunctionDefinitionContext(FunctionHeader fh) {
        cqlParser.FunctionDefinitionContext ctx = functionDefinitions.get(fh);
        if (ctx == null) {
            throw new IllegalArgumentException(String.format("Could not resolve function definition context for function header %s", fh.getFunctionDef().getName()));
        }
        return ctx;
    }

    public void registerFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
        FunctionHeader fh = getFunctionHeader(ctx);
        if (!libraryBuilder.getCompiledLibrary().contains(fh.getFunctionDef())) {
            libraryBuilder.addExpression(fh.getFunctionDef());
        }
    }

    public FunctionDef compileFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
        FunctionHeader fh = getFunctionHeader(ctx);

        final FunctionDef fun = fh.getFunctionDef();
        final TypeSpecifier resultType = fh.getResultType();
        final Operator op = libraryBuilder.resolveFunctionDefinition(fh.getFunctionDef());
        if (op == null) {
            throw new IllegalArgumentException(String.format("Internal error: Could not resolve operator map entry for function header %s", fh.getMangledName()));
        }
        libraryBuilder.pushIdentifierForHiding(fun.getName(), fun);
        final List<OperandDef> operand = op.getFunctionDef().getOperand();
        for (OperandDef operandDef : operand) {
            libraryBuilder.pushIdentifierForHiding(operandDef.getName(), operandDef);
        }

        if (ctx.functionBody() != null) {
            libraryBuilder.beginFunctionDef(fun);
            try {
                libraryBuilder.pushExpressionContext(getCurrentContext());
                try {
                    libraryBuilder.pushExpressionDefinition(fh.getMangledName());
                    try {
                        fun.setExpression(parseExpression(ctx.functionBody()));
                    } finally {
                        libraryBuilder.popExpressionDefinition();
                    }
                } finally {
                    libraryBuilder.popExpressionContext();
                }
            } finally {
                for (OperandDef operandDef : operand) {
                    libraryBuilder.popIdentifierForHiding();
                }
                libraryBuilder.endFunctionDef();
            }

            if (resultType != null && fun.getExpression() != null && fun.getExpression().getResultType() != null) {
                if (!DataTypes.subTypeOf(fun.getExpression().getResultType(), resultType.getResultType())) {
                    // ERROR:
                    throw new IllegalArgumentException(String.format("Function %s has declared return type %s but the function body returns incompatible type %s.",
                            fun.getName(), resultType.getResultType(), fun.getExpression().getResultType()));
                }
            }

            fun.setResultType(fun.getExpression().getResultType());
            op.setResultType(fun.getResultType());
        }
        else {
            fun.setExternal(true);
            if (resultType == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Function %s is marked external but does not declare a return type.", fun.getName()));
            }
            fun.setResultType(resultType.getResultType());
            op.setResultType(fun.getResultType());
        }

        fun.setContext(getCurrentContext());
        fh.setIsCompiled();

        return fun;
    }

    @Override
    public Object visitFunctionDefinition(cqlParser.FunctionDefinitionContext ctx) {
        registerFunctionDefinition(ctx);
        FunctionDef fun = compileFunctionDefinition(ctx);
        return fun;
    }

    private Expression parseLiteralExpression(ParseTree pt) {
        libraryBuilder.pushLiteralContext();
        try {
            return parseExpression(pt);
        }
        finally {
            libraryBuilder.popLiteralContext();
        }
    }

    private Expression parseExpression(ParseTree pt) {
        return pt == null ? null : (Expression) visit(pt);
    }

    private boolean isBooleanLiteral(Expression expression, Boolean bool) {
        boolean ret = false;
        if (expression instanceof Literal) {
            Literal lit = (Literal) expression;
            ret = lit.getValueType().equals(libraryBuilder.dataTypeToQName(libraryBuilder.resolveTypeName("System", "Boolean")));
            if (ret && bool != null) {
                ret = bool.equals(Boolean.valueOf(lit.getValue()));
            }
        }
        return ret;
    }

    private TrackBack getTrackBack(ParseTree tree) {
        if (tree instanceof ParserRuleContext) {
            return getTrackBack((ParserRuleContext)tree);
        }
        if (tree instanceof TerminalNode) {
            return getTrackBack((TerminalNode)tree);
        }
        return null;
    }

    private TrackBack getTrackBack(TerminalNode node) {
        TrackBack tb = new TrackBack(
                libraryBuilder.getLibraryIdentifier(),
                node.getSymbol().getLine(),
                node.getSymbol().getCharPositionInLine() + 1, // 1-based instead of 0-based
                node.getSymbol().getLine(),
                node.getSymbol().getCharPositionInLine() + node.getSymbol().getText().length()
        );
        return tb;
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

    private void decorate(Element element, TrackBack tb) {
        if (locatorsEnabled() && tb != null) {
            element.setLocator(tb.toLocator());
        }

        if (resultTypesEnabled() && element.getResultType() != null) {
            if (element.getResultType() instanceof NamedType) {
                element.setResultTypeName(libraryBuilder.dataTypeToQName(element.getResultType()));
            }
            else {
                element.setResultTypeSpecifier(libraryBuilder.dataTypeToTypeSpecifier(element.getResultType()));
            }
        }
    }

    private TrackBack track(Trackable trackable, ParseTree pt) {
        TrackBack tb = getTrackBack(pt);

        if (tb != null) {
            trackable.getTrackbacks().add(tb);
        }

        if (trackable instanceof Element) {
            decorate((Element)trackable, tb);
        }

        return tb;
    }

    private TrackBack track(Trackable trackable, Element from) {
        TrackBack tb = from.getTrackbacks().size() > 0 ? from.getTrackbacks().get(0) : null;

        if (tb != null) {
            trackable.getTrackbacks().add(tb);
        }

        if (trackable instanceof Element) {
            decorate((Element)trackable, tb);
        }

        return tb;
    }
}
