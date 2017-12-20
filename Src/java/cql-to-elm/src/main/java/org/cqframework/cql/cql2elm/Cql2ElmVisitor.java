package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.cql2elm.model.invocation.*;
import org.cqframework.cql.cql2elm.preprocessor.*;
import org.cqframework.cql.elm.tracking.*;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.model.*;
import org.hl7.cql.model.*;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Interval;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.jar.Pack200;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cql2ElmVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean annotate = false;
    private boolean locate = false;
    private boolean resultTypes = false;
    private boolean dateRangeOptimization = false;
    private boolean detailedErrors = false;
    private boolean methodInvocation = true;
    private TokenStream tokenStream;

    private final LibraryBuilder libraryBuilder;
    private final SystemMethodResolver systemMethodResolver;

    private LibraryInfo libraryInfo = null;
    public void setLibraryInfo(LibraryInfo libraryInfo) {
        if (libraryInfo == null) {
            throw new IllegalArgumentException("libraryInfo is null");
        }
        this.libraryInfo = libraryInfo;
    }

    //Put them here for now, but eventually somewhere else?
    private final Set<String> definedExpressionDefinitions = new HashSet<>();
    private final Stack<ExpressionDefinitionInfo> forwards = new Stack<>();
    private final Map<String, Set<Signature>> definedFunctionDefinitions = new HashMap<>();
    private final Stack<FunctionDefinitionInfo> forwardFunctions = new Stack<>();
    private final Stack<TimingOperatorContext> timingOperators = new Stack<>();
    private Stack<Chunk> chunks = new Stack<>();
    private String currentContext = "Patient"; // default context to patient
    private int currentToken = -1;
    private int nextLocalId = 1;
    private final List<Retrieve> retrieves = new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private boolean implicitPatientCreated = false;

    public Cql2ElmVisitor(LibraryBuilder libraryBuilder) {
        super();

        if (libraryBuilder == null) {
            throw new IllegalArgumentException("libraryBuilder is null");
        }

        this.libraryBuilder = libraryBuilder;
        this.systemMethodResolver = new SystemMethodResolver(this, libraryBuilder);
    }
    
    public void enableAnnotations() {
        annotate = true;
    }

    public void disableAnnotations() {
        annotate = false;
    }

    public void enableLocators() {
        locate = true;
    }

    public void disableLocators() {
        locate = false;
    }

    public void enableResultTypes() {
        resultTypes = true;
    }

    public void disableResultTypes() {
        resultTypes = false;
    }

    public void enableDateRangeOptimization() {
        dateRangeOptimization = true;
    }

    public void disableDateRangeOptimization() {
        dateRangeOptimization = false;
    }

    public boolean getDateRangeOptimization() {
        return dateRangeOptimization;
    }

    public void enableDetailedErrors() {
        detailedErrors = true;
    }

    public void disableDetailedErrors() {
        detailedErrors = false;
    }

    public boolean isDetailedErrorsEnabled() {
        return detailedErrors;
    }

    public void enableMethodInvocation() {
        methodInvocation = true;
    }

    public void disableMethodInvocation() {
        methodInvocation = false;
    }

    public TokenStream getTokenStream() {
        return tokenStream;
    }

    public void setTokenStream(TokenStream value) {
        tokenStream = value;
    }

    public List<Retrieve> getRetrieves() {
        return retrieves;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    private int getNextLocalId() {
        return nextLocalId++;
    }

    private void pushChunk(@NotNull ParseTree tree) {
        org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();
        Chunk chunk = new Chunk().withInterval(sourceInterval);
        chunks.push(chunk);
    }

    private void popChunk(@NotNull ParseTree tree, Object o) {
        Chunk chunk = chunks.pop();
        if (o instanceof Element) {
            Element element = (Element)o;
            if (element.getLocalId() == null) {
                element.setLocalId(Integer.toString(getNextLocalId()));
            }
            chunk.setElement(element);

            if (element instanceof ExpressionDef && !(tree instanceof cqlParser.LibraryContext)) {
                ExpressionDef expressionDef = (ExpressionDef)element;
                if (expressionDef.getAnnotation().size() == 0) {
                    expressionDef.getAnnotation().add(buildAnnotation(chunk));
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
                    }
                }
            }
            if (currentNarrative != null) {
                narrative.getContent().add(wrapNarrative(currentNarrative));
            }
        }
        else {
            narrative.getContent().add(tokenStream.getText(chunk.getInterval()));
        }

        return narrative;
    }

    private boolean hasChunks(Narrative narrative) {
        for (Serializable c : narrative.getContent()) {
            if (!(c instanceof String)) {
                return true;
            }
        }
        return false;
    }

    private Serializable wrapNarrative(Narrative narrative) {
        return new JAXBElement<>(
                new QName("urn:hl7-org:cql-annotations:r1", "s"),
                Narrative.class,
                narrative);
    }

    @Override
    public Object visit(@NotNull ParseTree tree) {
        if (annotate) {
            pushChunk(tree);
        }
        Object o = null;
        try {
            try {
                o = super.visit(tree);
            } catch (CqlTranslatorIncludeException e) {
                libraryBuilder.recordParsingException(new CqlTranslatorException(e.getMessage(), getTrackBack(tree), e));
            } catch (CqlTranslatorException e) {
                libraryBuilder.recordParsingException(e);
            } catch (Exception e) {
                CqlTranslatorException ex = null;
                if (e.getMessage() == null) {
                    ex = new CqlInternalException("Internal translator error.", getTrackBack(tree), e);
                }
                else {
                    ex = new CqlSemanticException(e.getMessage(), getTrackBack(tree), e);
                }

                Exception rootCause = libraryBuilder.determineRootCause();
                if (rootCause == null) {
                    rootCause = ex;
                    libraryBuilder.recordParsingException(ex);
                    libraryBuilder.setRootCause(rootCause);
                }
                else {
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
            if (annotate) {
                popChunk(tree, o);
            }
        }
    }

    @Override
    public Object visitLibrary(@NotNull cqlParser.LibraryContext ctx) {

        Object lastResult = null;
        // NOTE: Need to set the library identifier here so the builder can begin the translation appropriately
        libraryBuilder.setLibraryIdentifier(new VersionedIdentifier().withId(libraryInfo.getLibraryName()).withVersion(libraryInfo.getVersion()));
        libraryBuilder.beginTranslation();
        try {
            // Loop through and call visit on each child (to ensure they are tracked)
            for (int i = 0; i < ctx.getChildCount(); i++) {
                lastResult = visit(ctx.getChild(i));
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            return lastResult;
        }
        finally {
            libraryBuilder.endTranslation();
        }
    }

    @Override
    public VersionedIdentifier visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        VersionedIdentifier vid = of.createVersionedIdentifier()
                .withId(parseString(ctx.identifier()))
                .withVersion(parseString(ctx.versionSpecifier()));
        libraryBuilder.setLibraryIdentifier(vid);

        return vid;
    }

    @Override
    public UsingDef visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        Model model = getModel(parseString(ctx.modelIdentifier()), parseString(ctx.versionSpecifier()));
        return libraryBuilder.resolveUsingRef(model.getModelInfo().getName());
    }

    public Model getModel() {
        return getModel((String)null);
    }

    public Model getModel(String modelName) {
        return getModel(modelName, null);
    }

    public Model getModel(String modelName, String version) {
        if (modelName == null) {
            UsingDefinitionInfo defaultUsing = libraryInfo.getDefaultUsingDefinition();
            modelName = defaultUsing.getName();
            version = defaultUsing.getVersion();
        }

        VersionedIdentifier modelIdentifier = new VersionedIdentifier().withId(modelName).withVersion(version);
        return libraryBuilder.getModel(modelIdentifier);
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        String identifier = parseString(ctx.identifier());
        IncludeDef library = of.createIncludeDef()
                .withLocalIdentifier(ctx.localIdentifier() == null ? identifier : parseString(ctx.localIdentifier()))
                .withPath(identifier)
                .withVersion(parseString(ctx.versionSpecifier()));

        libraryBuilder.addInclude(library);

        return library;
    }

    @Override
    public ParameterDef visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDef param = of.createParameterDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withDefault(parseExpression(ctx.expression()))
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

        return param;
    }

    @Override
    public NamedTypeSpecifier visitNamedTypeSpecifier(@NotNull cqlParser.NamedTypeSpecifierContext ctx) {
        DataType resultType = libraryBuilder.resolveTypeName(parseString(ctx.modelIdentifier()), parseQualifiedString(ctx.identifier()));
        NamedTypeSpecifier result = of.createNamedTypeSpecifier()
                .withName(libraryBuilder.dataTypeToQName(resultType));

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.setResultType(resultType);

        return result;
    }

    @Override
    public TupleElementDefinition visitTupleElementDefinition(@NotNull cqlParser.TupleElementDefinitionContext ctx) {
        TupleElementDefinition result = of.createTupleElementDefinition()
                .withName(parseString(ctx.identifier()))
                .withType(parseTypeSpecifier(ctx.typeSpecifier()));

        return result;
    }

    @Override
    public Object visitTupleTypeSpecifier(@NotNull cqlParser.TupleTypeSpecifierContext ctx) {
        TupleType resultType = new TupleType();
        TupleTypeSpecifier typeSpecifier = of.createTupleTypeSpecifier();
        for (cqlParser.TupleElementDefinitionContext definitionContext : ctx.tupleElementDefinition()) {
            TupleElementDefinition element = (TupleElementDefinition)visit(definitionContext);
            resultType.addElement(new TupleTypeElement(element.getName(), element.getType().getResultType()));
            typeSpecifier.getElement().add(element);
        }

        typeSpecifier.setResultType(resultType);

        return typeSpecifier;
    }

    @Override
    public ChoiceTypeSpecifier visitChoiceTypeSpecifier(@NotNull cqlParser.ChoiceTypeSpecifierContext ctx) {
        ArrayList<TypeSpecifier> typeSpecifiers = new ArrayList<TypeSpecifier>();
        ArrayList<DataType> types = new ArrayList<DataType>();
        for (cqlParser.TypeSpecifierContext typeSpecifierContext : ctx.typeSpecifier()) {
            TypeSpecifier typeSpecifier = parseTypeSpecifier(typeSpecifierContext);
            typeSpecifiers.add(typeSpecifier);
            types.add(typeSpecifier.getResultType());
        }
        ChoiceTypeSpecifier result = of.createChoiceTypeSpecifier().withType(typeSpecifiers);
        ChoiceType choiceType = new ChoiceType(types);
        result.setResultType(choiceType);
        return result;
    }

    @Override
    public IntervalTypeSpecifier visitIntervalTypeSpecifier(@NotNull cqlParser.IntervalTypeSpecifierContext ctx) {
        IntervalTypeSpecifier result = of.createIntervalTypeSpecifier().withPointType(parseTypeSpecifier(ctx.typeSpecifier()));
        IntervalType intervalType = new IntervalType(result.getPointType().getResultType());
        result.setResultType(intervalType);
        return result;
    }

    @Override
    public ListTypeSpecifier visitListTypeSpecifier(@NotNull cqlParser.ListTypeSpecifierContext ctx) {
        ListTypeSpecifier result = of.createListTypeSpecifier().withElementType(parseTypeSpecifier(ctx.typeSpecifier()));
        ListType listType = new ListType(result.getElementType().getResultType());
        result.setResultType(listType);
        return result;
    }

    @Override
    public AccessModifier visitAccessModifier(@NotNull cqlParser.AccessModifierContext ctx) {
        switch (ctx.getText().toLowerCase()) {
            case "public" : return AccessModifier.PUBLIC;
            case "private" : return AccessModifier.PRIVATE;
            default: throw new IllegalArgumentException(String.format("Unknown access modifier %s.", ctx.getText().toLowerCase()));
        }
    }

    @Override
    public CodeSystemDef visitCodesystemDefinition(@NotNull cqlParser.CodesystemDefinitionContext ctx) {
        CodeSystemDef cs = (CodeSystemDef)of.createCodeSystemDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codesystemId()))
                .withVersion(parseString(ctx.versionSpecifier()))
                .withResultType(new ListType(libraryBuilder.resolveTypeName("System", "Code")));

        libraryBuilder.addCodeSystem(cs);
        return cs;
    }

    @Override
    public CodeSystemRef visitCodesystemIdentifier(@NotNull cqlParser.CodesystemIdentifierContext ctx) {
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
            throw new IllegalArgumentException(String.format("Could not resolve reference to code system %s.", name));
        }

        return (CodeSystemRef)of.createCodeSystemRef()
                .withLibraryName(libraryName)
                .withName(name)
                .withResultType(def.getResultType());
    }

    @Override
    public CodeRef visitCodeIdentifier(@NotNull cqlParser.CodeIdentifierContext ctx) {
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
            throw new IllegalArgumentException(String.format("Could not resolve reference to code %s.", name));
        }

        return (CodeRef)of.createCodeRef()
                .withLibraryName(libraryName)
                .withName(name)
                .withResultType(def.getResultType());
    }

    @Override
    public ValueSetDef visitValuesetDefinition(@NotNull cqlParser.ValuesetDefinitionContext ctx) {
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
        vs.setResultType(new ListType(libraryBuilder.resolveTypeName("System", "Code")));
        libraryBuilder.addValueSet(vs);

        return vs;
    }

    @Override
    public CodeDef visitCodeDefinition(@NotNull cqlParser.CodeDefinitionContext ctx) {
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

        return cd;
    }

    @Override
    public ConceptDef visitConceptDefinition(@NotNull cqlParser.ConceptDefinitionContext ctx) {
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
    public Object visitContextDefinition(@NotNull cqlParser.ContextDefinitionContext ctx) {
        currentContext = parseString(ctx.identifier());

        if (!(currentContext.equals("Patient") || currentContext.equals("Population"))) {
            throw new IllegalArgumentException(String.format("Unknown context %s.", currentContext));
        }

        // If this is the first time a context definition is encountered, output a patient definition:
        // define Patient = element of [<Patient model type>]
        if (!implicitPatientCreated) {
            if (libraryBuilder.hasUsings()) {
                ModelInfo modelInfo = libraryBuilder.getModel(libraryInfo.getDefaultModelName()).getModelInfo();
                String patientTypeName = modelInfo.getPatientClassName();
                if (patientTypeName == null || patientTypeName.equals("")) {
                    throw new IllegalArgumentException("Model definition does not contain enough information to construct a patient context.");
                }
                DataType patientType = libraryBuilder.resolveTypeName(modelInfo.getName(), patientTypeName);
                Retrieve patientRetrieve = of.createRetrieve().withDataType(libraryBuilder.dataTypeToQName(patientType));
                track(patientRetrieve, ctx);
                patientRetrieve.setResultType(new ListType(patientType));
                String patientClassIdentifier = modelInfo.getPatientClassIdentifier();
                if (patientClassIdentifier != null) {
                    patientRetrieve.setTemplateId(patientClassIdentifier);
                }

                ExpressionDef patientExpressionDef = of.createExpressionDef()
                        .withName("Patient")
                        .withContext(currentContext)
                        .withExpression(of.createSingletonFrom().withOperand(patientRetrieve));
                track(patientExpressionDef, ctx);
                patientExpressionDef.getExpression().setResultType(patientType);
                patientExpressionDef.setResultType(patientType);
                libraryBuilder.addExpression(patientExpressionDef);
            }
            else {
                ExpressionDef patientExpressionDef = of.createExpressionDef()
                        .withName("Patient")
                        .withContext(currentContext)
                        .withExpression(of.createNull());
                track(patientExpressionDef, ctx);
                patientExpressionDef.getExpression().setResultType(libraryBuilder.resolveTypeName("System", "Any"));
                patientExpressionDef.setResultType(patientExpressionDef.getExpression().getResultType());
                libraryBuilder.addExpression(patientExpressionDef);
            }

            implicitPatientCreated = true;
            return currentContext;
        }

        return currentContext;
    }

    public ExpressionDef internalVisitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        String identifier = parseString(ctx.identifier());
        ExpressionDef def = libraryBuilder.resolveExpressionRef(identifier);
        if (def == null) {
            libraryBuilder.pushExpressionContext(currentContext);
            try {
                libraryBuilder.pushExpressionDefinition(identifier);
                try {
                    def = of.createExpressionDef()
                            .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                            .withName(identifier)
                            .withContext(currentContext)
                            .withExpression((Expression) visit(ctx.expression()));
                    def.setResultType(def.getExpression().getResultType());
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
    public ExpressionDef visitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDef expressionDef = internalVisitExpressionDefinition(ctx);
        if (forwards.isEmpty() || !forwards.peek().getName().equals(expressionDef.getName())) {
            if (definedExpressionDefinitions.contains(expressionDef.getName())) {
                throw new IllegalArgumentException(String.format("Identifier %s is already in use in this library.", expressionDef.getName()));
            }

            // Track defined expression definitions locally, otherwise duplicate expression definitions will be missed because they are
            // overwritten by name when they are encountered by the preprocessor.
            definedExpressionDefinitions.add(expressionDef.getName());
        }
        return expressionDef;
    }

    @Override
    public Literal visitStringLiteral(@NotNull cqlParser.StringLiteralContext ctx) {
        return libraryBuilder.createLiteral(parseString(ctx.STRING()));
    }

    @Override
    public Literal visitBooleanLiteral(@NotNull cqlParser.BooleanLiteralContext ctx) {
        return libraryBuilder.createLiteral(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Object visitIntervalSelector(@NotNull cqlParser.IntervalSelectorContext ctx) {
        return libraryBuilder.createInterval(parseExpression(ctx.expression(0)), ctx.getChild(1).getText().equals("["),
                parseExpression(ctx.expression(1)), ctx.getChild(5).getText().equals("]"));
    }

    @Override
    public Object visitTupleElementSelector(@NotNull cqlParser.TupleElementSelectorContext ctx) {
        TupleElement result = of.createTupleElement()
                .withName(parseString(ctx.identifier()))
                .withValue(parseExpression(ctx.expression()));
        result.setResultType(result.getValue().getResultType());
        return result;
    }

    @Override
    public Object visitTupleSelector(@NotNull cqlParser.TupleSelectorContext ctx) {
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
    public Object visitInstanceElementSelector(@NotNull cqlParser.InstanceElementSelectorContext ctx) {
        InstanceElement result = of.createInstanceElement()
                .withName(parseString(ctx.identifier()))
                .withValue(parseExpression(ctx.expression()));
        result.setResultType(result.getValue().getResultType());
        return result;
    }

    @Override
    public Object visitInstanceSelector(@NotNull cqlParser.InstanceSelectorContext ctx) {
        Instance instance = of.createInstance();
        NamedTypeSpecifier classTypeSpecifier = visitNamedTypeSpecifier(ctx.namedTypeSpecifier());
        instance.setClassType(classTypeSpecifier.getName());
        instance.setResultType(classTypeSpecifier.getResultType());

        for (cqlParser.InstanceElementSelectorContext elementContext : ctx.instanceElementSelector()) {
            InstanceElement element = (InstanceElement)visit(elementContext);
            DataType propertyType = libraryBuilder.resolveProperty(classTypeSpecifier.getResultType(), element.getName());
            element.setValue(libraryBuilder.ensureCompatible(element.getValue(), propertyType));
            instance.getElement().add(element);
        }

        return instance;
    }

    @Override
    public Object visitCodeSelector(@NotNull cqlParser.CodeSelectorContext ctx) {
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
    public Object visitConceptSelector(@NotNull cqlParser.ConceptSelectorContext ctx) {
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
    public Object visitListSelector(@NotNull cqlParser.ListSelectorContext ctx) {
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
                Conversion conversion = libraryBuilder.findConversion(element.getResultType(), elementType, true);
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
    public Object visitTimeLiteral(@NotNull cqlParser.TimeLiteralContext ctx) {
        String input = ctx.getText();
        if (input.startsWith("@")) {
            input = input.substring(1);
        }

        Pattern dateTimePattern =
                Pattern.compile("T((\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?)?((Z)|(([+-])(\\d{2})(\\:?(\\d{2}))?))?");
                               //-12-------3---4-------5---6-------7---8-------------91---11-----1-------1----1------------
                               //-----------------------------------------------------0---12-----3-------4----5------------

        Matcher matcher = dateTimePattern.matcher(input);
        if (matcher.matches()) {
            try {
                Time result = of.createTime();
                int hour = Integer.parseInt(matcher.group(2));
                int minute = -1;
                int second = -1;
                int millisecond = -1;
                if (hour < 0 || hour > 24) {
                    throw new IllegalArgumentException(String.format("Invalid hour in time literal (%s).", input));
                }
                result.setHour(libraryBuilder.createLiteral(hour));

                if (matcher.group(4) != null) {
                    minute = Integer.parseInt(matcher.group(4));
                    if (minute < 0 || minute >= 60 || (hour == 24 && minute > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid minute in time literal (%s).", input));
                    }
                    result.setMinute(libraryBuilder.createLiteral(minute));
                }

                if (matcher.group(6) != null) {
                    second = Integer.parseInt(matcher.group(6));
                    if (second < 0 || second >= 60 || (hour == 24 && second > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid second in time literal (%s).", input));
                    }
                    result.setSecond(libraryBuilder.createLiteral(second));
                }

                if (matcher.group(8) != null) {
                    millisecond = Integer.parseInt(matcher.group(8));
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid millisecond in time literal (%s).", input));
                    }
                    result.setMillisecond(libraryBuilder.createLiteral(millisecond));
                }

                if (matcher.group(10) != null && matcher.group(10).equals("Z")) {
                    result.setTimezoneOffset(libraryBuilder.createLiteral(0.0));
                }

                if (matcher.group(12) != null) {
                    int offsetPolarity = matcher.group(12).equals("+") ? 1 : -1;

                    if (matcher.group(15) != null) {
                        int hourOffset = Integer.parseInt(matcher.group(13));
                        if (hourOffset < 0 || hourOffset > 14) {
                            throw new IllegalArgumentException(String.format("Timezone hour offset out of range in time literal (%s).", input));
                        }

                        int minuteOffset = Integer.parseInt(matcher.group(15));
                        if (minuteOffset < 0 || minuteOffset >= 60 || (hourOffset == 14 && minuteOffset > 0)) {
                            throw new IllegalArgumentException(String.format("Timezone minute offset out of range in time literal (%s).", input));
                        }
                        result.setTimezoneOffset(libraryBuilder.createLiteral((double)(hourOffset + (minuteOffset / 60)) * offsetPolarity));
                    }
                    else {
                        if (matcher.group(13) != null) {
                            int hourOffset = Integer.parseInt(matcher.group(13));
                            if (hourOffset < 0 || hourOffset > 14) {
                                throw new IllegalArgumentException(String.format("Timezone hour offset out of range in time literal (%s).", input));
                            }
                            result.setTimezoneOffset(libraryBuilder.createLiteral((double)(hourOffset * offsetPolarity)));
                        }
                    }
                }

                result.setResultType(libraryBuilder.resolveTypeName("System", "Time"));
                return result;
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException(String.format("Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.mmmmZhh:mm).", input), e);
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.mmmmZhh:mm).", input));
        }
    }

    @Override
    public Object visitDateTimeLiteral(@NotNull cqlParser.DateTimeLiteralContext ctx) {
        String input = ctx.getText();
        if (input.startsWith("@")) {
            input = input.substring(1);
        }

        Pattern dateTimePattern =
                Pattern.compile("(\\d{4})(-(\\d{2}))?(-(\\d{2}))?((Z)|(T((\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?)?((Z)|(([+-])(\\d{2})(\\:?(\\d{2}))?))?))?");
                               //1-------2-3---------4-5---------67---8-91-------1---1-------1---1-------1---1-------------11---12-----2-------2----2---------------
                               //----------------------------------------0-------1---2-------3---4-------5---6-------------78---90-----1-------2----3---------------

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
                if (matcher.group(3) != null) {
                    month = Integer.parseInt(matcher.group(3));
                    if (month < 0 || month > 12) {
                        throw new IllegalArgumentException(String.format("Invalid month in date/time literal (%s).", input));
                    }
                    result.setMonth(libraryBuilder.createLiteral(month));
                }

                if (matcher.group(5) != null) {
                    day = Integer.parseInt(matcher.group(5));
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

                if (matcher.group(10) != null) {
                    hour = Integer.parseInt(matcher.group(10));
                    if (hour < 0 || hour > 24) {
                        throw new IllegalArgumentException(String.format("Invalid hour in date/time literal (%s).", input));
                    }
                    result.setHour(libraryBuilder.createLiteral(hour));
                }

                if (matcher.group(12) != null) {
                    minute = Integer.parseInt(matcher.group(12));
                    if (minute < 0 || minute >= 60 || (hour == 24 && minute > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid minute in date/time literal (%s).", input));
                    }
                    result.setMinute(libraryBuilder.createLiteral(minute));
                }

                if (matcher.group(14) != null) {
                    second = Integer.parseInt(matcher.group(14));
                    if (second < 0 || second >= 60 || (hour == 24 && second > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid second in date/time literal (%s).", input));
                    }
                    result.setSecond(libraryBuilder.createLiteral(second));
                }

                if (matcher.group(16) != null) {
                    millisecond = Integer.parseInt(matcher.group(16));
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid millisecond in date/time literal (%s).", input));
                    }
                    result.setMillisecond(libraryBuilder.createLiteral(millisecond));
                }

                if ((matcher.group(7) != null && matcher.group(7).equals("Z"))
                        || ((matcher.group(18) != null) && matcher.group(18).equals("Z"))) {
                    result.setTimezoneOffset(libraryBuilder.createLiteral(0.0));
                }

                if (matcher.group(20) != null) {
                    int offsetPolarity = matcher.group(20).equals("+") ? 1 : -1;

                    if (matcher.group(23) != null) {
                        int hourOffset = Integer.parseInt(matcher.group(21));
                        if (hourOffset < 0 || hourOffset > 14) {
                            throw new IllegalArgumentException(String.format("Timezone hour offset is out of range in date/time literal (%s).", input));
                        }

                        int minuteOffset = Integer.parseInt(matcher.group(23));
                        if (minuteOffset < 0 || minuteOffset >= 60 || (hourOffset == 14 && minuteOffset > 0)) {
                            throw new IllegalArgumentException(String.format("Timezone minute offset is out of range in date/time literal (%s).", input));
                        }

                        result.setTimezoneOffset(libraryBuilder.createLiteral((double)(hourOffset + (minuteOffset / 60)) * offsetPolarity));
                    }
                    else {
                        if (matcher.group(21) != null) {
                            int hourOffset = Integer.parseInt(matcher.group(21));
                            if (hourOffset < 0 || hourOffset > 14) {
                                throw new IllegalArgumentException(String.format("Timezone hour offset is out of range in date/time literal (%s).", input));
                            }

                            result.setTimezoneOffset(libraryBuilder.createLiteral((double)(hourOffset * offsetPolarity)));
                        }
                    }
                }

                result.setResultType(libraryBuilder.resolveTypeName("System", "DateTime"));
                return result;
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException(String.format("Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.mmmmZhh:mm).", input), e);
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.mmmmZhh:mm).", input));
        }
    }

    @Override
    public Null visitNullLiteral(@NotNull cqlParser.NullLiteralContext ctx) {
        Null result = of.createNull();
        result.setResultType(libraryBuilder.resolveTypeName("System", "Any"));
        return result;
    }

    @Override
    public Expression visitNumberLiteral(@NotNull cqlParser.NumberLiteralContext ctx) {
        return libraryBuilder.createNumberLiteral(ctx.NUMBER().getText());
    }

    @Override
    public Expression visitQuantity(@NotNull cqlParser.QuantityContext ctx) {
        if (ctx.unit() != null) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setParseBigDecimal(true);
            try {
                Quantity result = of.createQuantity()
                        .withValue((BigDecimal) df.parse(ctx.NUMBER().getText()))
                        .withUnit(parseString(ctx.unit()));
                result.setResultType(libraryBuilder.resolveTypeName("System", "Quantity"));
                return result;
            } catch (ParseException e) {
                throw new IllegalArgumentException(String.format("Could not parse quantity literal: %s", ctx.getText()), e);
            }
        } else {
            return libraryBuilder.createNumberLiteral(ctx.NUMBER().getText());
        }
    }

    @Override
    public Not visitNotExpression(@NotNull cqlParser.NotExpressionContext ctx) {
        Not result = of.createNot().withOperand(parseExpression(ctx.expression()));
        libraryBuilder.resolveUnaryCall("System", "Not", result);
        return result;
    }

    @Override
    public Exists visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        Exists result = of.createExists().withOperand(parseExpression(ctx.expression()));
        libraryBuilder.resolveUnaryCall("System", "Exists", result);
        return result;
    }

    @Override
    public BinaryExpression visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
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
    public Power visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        Power power = of.createPower().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", "Power", power);

        return power;
    }

    @Override
    public Object visitPolarityExpressionTerm(@NotNull cqlParser.PolarityExpressionTermContext ctx) {
        if (ctx.getChild(0).getText().equals("+")) {
            return visit(ctx.expressionTerm());
        }

        Negate result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()));
        libraryBuilder.resolveUnaryCall("System", "Negate", result);
        return result;
    }

    @Override
    public Expression visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
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
    public Object visitPredecessorExpressionTerm(@NotNull cqlParser.PredecessorExpressionTermContext ctx) {
        Predecessor result = of.createPredecessor().withOperand(parseExpression(ctx.expressionTerm()));
        libraryBuilder.resolveUnaryCall("System", "Predecessor", result);
        return result;
    }

    @Override
    public Object visitSuccessorExpressionTerm(@NotNull cqlParser.SuccessorExpressionTermContext ctx) {
        Successor result = of.createSuccessor().withOperand(parseExpression(ctx.expressionTerm()));
        libraryBuilder.resolveUnaryCall("System", "Successor", result);
        return result;
    }

    @Override
    public Object visitElementExtractorExpressionTerm(@NotNull cqlParser.ElementExtractorExpressionTermContext ctx) {
        SingletonFrom result = of.createSingletonFrom().withOperand(parseExpression(ctx.expressionTerm()));

        if (!(result.getOperand().getResultType() instanceof ListType)) {
            throw new IllegalArgumentException("List type expected.");
        }

        result.setResultType(((ListType)result.getOperand().getResultType()).getElementType());

        libraryBuilder.resolveUnaryCall("System", "SingletonFrom", result);
        return result;
    }

    @Override
    public Object visitPointExtractorExpressionTerm(@NotNull cqlParser.PointExtractorExpressionTermContext ctx) {
        PointFrom result = of.createPointFrom().withOperand(parseExpression(ctx.expressionTerm()));

        if (!(result.getOperand().getResultType() instanceof IntervalType)) {
            throw new IllegalArgumentException("Interval type expected.");
        }

        result.setResultType(((IntervalType)result.getOperand().getResultType()).getPointType());

        libraryBuilder.resolveUnaryCall("System", "PointFrom", result);
        return result;
    }

    @Override
    public Object visitTypeExtentExpressionTerm(@NotNull cqlParser.TypeExtentExpressionTermContext ctx) {
        String extent = parseString(ctx.getChild(0));
        TypeSpecifier targetType = parseTypeSpecifier(ctx.namedTypeSpecifier());
        switch (extent) {
            case "minimum": {
                MinValue minimum = of.createMinValue();
                minimum.setValueType(libraryBuilder.dataTypeToQName(targetType.getResultType()));
                minimum.setResultType(targetType.getResultType());
                return minimum;
            }

            case "maximum": {
                MaxValue maximum = of.createMaxValue();
                maximum.setValueType(libraryBuilder.dataTypeToQName(targetType.getResultType()));
                maximum.setResultType(targetType.getResultType());
                return maximum;
            }

            default: throw new IllegalArgumentException(String.format("Unknown extent: %s", extent));
        }
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(@NotNull cqlParser.TimeBoundaryExpressionTermContext ctx) {
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

        if (!(result.getOperand().getResultType() instanceof IntervalType)) {
            throw new IllegalArgumentException("Interval type expected.");
        }

        result.setResultType(((IntervalType)result.getOperand().getResultType()).getPointType());

        libraryBuilder.resolveUnaryCall("System", operatorName, result);
        return result;
    }

    private DateTimePrecision parseDateTimePrecision(String dateTimePrecision) {
        if (dateTimePrecision == null) {
            throw new IllegalArgumentException("dateTimePrecision is null");
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
    public Object visitTimeUnitExpressionTerm(@NotNull cqlParser.TimeUnitExpressionTermContext ctx) {
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
                result = of.createTimezoneFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "TimezoneFrom";
                break;
            case "year":
            case "month":
            case "week":
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
            default:
                throw new IllegalArgumentException(String.format("Unknown precision '%s'.", component));
        }

        libraryBuilder.resolveUnaryCall("System", operatorName, result);
        return result;
    }

    @Override
    public Object visitDurationExpressionTerm(@NotNull cqlParser.DurationExpressionTermContext ctx) {
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
    public Object visitBetweenExpression(@NotNull cqlParser.BetweenExpressionContext ctx) {
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
    public Object visitDurationBetweenExpression(@NotNull cqlParser.DurationBetweenExpressionContext ctx) {
        BinaryExpression result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result);
        return result;
    }

    @Override
    public Object visitDifferenceBetweenExpression(@NotNull cqlParser.DifferenceBetweenExpressionContext ctx) {
        BinaryExpression result = of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        libraryBuilder.resolveBinaryCall("System", "DifferenceBetween", result);
        return result;
    }

    @Override
    public Object visitWidthExpressionTerm(@NotNull cqlParser.WidthExpressionTermContext ctx) {
        UnaryExpression result = of.createWidth().withOperand(parseExpression(ctx.expressionTerm()));
        libraryBuilder.resolveUnaryCall("System", "Width", result);
        return result;
    }

    @Override
    public Expression visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return parseExpression(ctx.expression());
    }

    @Override
    public Object visitMembershipExpression(@NotNull cqlParser.MembershipExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        switch (operator) {
            case "in":
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    In in = of.createIn()
                            .withPrecision(parseDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
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
                            .withPrecision(parseDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
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
    public And visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        And and = of.createAnd().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        libraryBuilder.resolveBinaryCall("System", "And", and);
        return and;
    }

    @Override
    public Expression visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
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
    public Expression visitImpliesExpression(@NotNull cqlParser.ImpliesExpressionContext ctx) {
        Implies implies = of.createImplies().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        libraryBuilder.resolveBinaryCall("System", "Implies", implies);
        return implies;
    }

    @Override
    public Object visitInFixSetExpression(@NotNull cqlParser.InFixSetExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        Expression left = parseExpression(ctx.expression(0));
        Expression right = parseExpression(ctx.expression(1));

        // for union of lists
            // collect list of types in either side
            // cast both operands to a choice type with all types

        // for intersect of lists
            // collect list of types in both sides
            // cast both operands to a choice type with all types
            // TODO: cast the result to a choice type with only types in both sides

        // for difference of lists
            // collect list of types in both sides
            // cast both operands to a choice type with all types
            // TODO: cast the result to the initial type of the left

        if (left.getResultType() instanceof ListType && right.getResultType() instanceof ListType) {
            ListType leftListType = (ListType)left.getResultType();
            ListType rightListType = (ListType)right.getResultType();

            if (!(leftListType.isSuperTypeOf(rightListType) || rightListType.isSuperTypeOf(leftListType))
                    && !(leftListType.isCompatibleWith(rightListType) || rightListType.isCompatibleWith(leftListType))) {
                Set<DataType> elementTypes = new HashSet<DataType>();
                if (leftListType.getElementType() instanceof ChoiceType) {
                    for (DataType choice : ((ChoiceType)leftListType.getElementType()).getTypes()) {
                        elementTypes.add(choice);
                    }
                }
                else {
                    elementTypes.add(leftListType.getElementType());
                }

                if (rightListType.getElementType() instanceof ChoiceType) {
                    for (DataType choice : ((ChoiceType)rightListType.getElementType()).getTypes()) {
                        elementTypes.add(choice);
                    }
                }
                else {
                    elementTypes.add(rightListType.getElementType());
                }

                if (elementTypes.size() > 1) {
                    ListType targetType = new ListType(new ChoiceType(elementTypes));
                    left = of.createAs().withOperand(left).withAsTypeSpecifier(libraryBuilder.dataTypeToTypeSpecifier(targetType));
                    track(left, ctx.expression(0));
                    left.setResultType(targetType);

                    right = of.createAs().withOperand(right).withAsTypeSpecifier(libraryBuilder.dataTypeToTypeSpecifier(targetType));
                    track(right, ctx.expression(1));
                    right.setResultType(targetType);
                }
            }
        }

        switch (operator) {
            case "|":
            case "union":
                Union union = of.createUnion().withOperand(left, right);
                libraryBuilder.resolveBinaryCall("System", "Union", union);
                return union;
            case "intersect":
                Intersect intersect = of.createIntersect().withOperand(left, right);
                libraryBuilder.resolveBinaryCall("System", "Intersect", intersect);
                return intersect;
            case "except":
                Except except = of.createExcept().withOperand(left, right);
                libraryBuilder.resolveBinaryCall("System", "Except", except);
                return except;
        }

        return of.createNull();
    }

    @Override
    public Expression visitEqualityExpression(@NotNull cqlParser.EqualityExpressionContext ctx) {
        String operator = parseString(ctx.getChild(1));
        if (operator.equals("~") || operator.equals("!~")) {
            BinaryExpression equivalent = of.createEquivalent().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));

            libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent);
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
    public BinaryExpression visitInequalityExpression(@NotNull cqlParser.InequalityExpressionContext ctx) {
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
    public List<String> visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
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
    public Object visitTermExpression(@NotNull cqlParser.TermExpressionContext ctx) {
        Object result = super.visitTermExpression(ctx);

        if (result instanceof LibraryRef) {
            throw new IllegalArgumentException(String.format("Identifier %s is a library and cannot be used as an expression.", ((LibraryRef)result).getLibraryName()));
        }

        return result;
    }

    @Override
    public Object visitTerminal(@NotNull TerminalNode node) {
        String text = node.getText();
        int tokenType = node.getSymbol().getType();
        if (cqlLexer.STRING == tokenType || cqlLexer.QUOTEDIDENTIFIER == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length() - 1);

            if (cqlLexer.STRING == tokenType) {
                text = text.replace("''", "'");
            }
            else {
                text = text.replace("\"\"", "\"");
            }
        }

        return text;
    }

    @Override
    public Object visitConversionExpressionTerm(@NotNull cqlParser.ConversionExpressionTermContext ctx) {
        TypeSpecifier targetType = parseTypeSpecifier(ctx.typeSpecifier());
        Expression operand = parseExpression(ctx.expression());
        if (!DataTypes.equal(operand.getResultType(), targetType.getResultType())) {
            Conversion conversion = libraryBuilder.findConversion(operand.getResultType(), targetType.getResultType(), false);
            if (conversion == null) {
                throw new IllegalArgumentException(String.format("Could not resolve conversion from type %s to type %s.",
                        operand.getResultType(), targetType.getResultType()));
            }

            return libraryBuilder.convertExpression(operand, conversion);
        }

        return operand;
    }

    @Override
    public Object visitTypeExpression(@NotNull cqlParser.TypeExpressionContext ctx) {
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
    public Object visitCastExpression(@NotNull cqlParser.CastExpressionContext ctx) {
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
    public Expression visitBooleanExpression(@NotNull cqlParser.BooleanExpressionContext ctx) {
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
    public Object visitTimingExpression(@NotNull cqlParser.TimingExpressionContext ctx) {
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
    public Object visitConcurrentWithIntervalOperatorPhrase(@NotNull cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
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
        if (ctx.relativeQualifier() == null) {
            if (ctx.dateTimePrecision() != null) {
                operator = of.createSameAs().withPrecision(parseDateTimePrecision(ctx.dateTimePrecision().getText()));
            } else {
                operator = of.createSameAs();
            }
            operatorName = "SameAs";
        } else {
            switch (ctx.relativeQualifier().getText()) {
                case "or after": {
                    if (ctx.dateTimePrecision() != null) {
                        operator = of.createSameOrAfter().withPrecision(parseDateTimePrecision(ctx.dateTimePrecision().getText()));
                    } else {
                        operator = of.createSameOrAfter();
                    }
                    operatorName = "SameOrAfter";
                }
                break;
                case "or before": {
                    if (ctx.dateTimePrecision() != null) {
                        operator = of.createSameOrBefore().withPrecision(parseDateTimePrecision(ctx.dateTimePrecision().getText()));
                    } else {
                        operator = of.createSameOrBefore();
                    }
                    operatorName = "SameOrBefore";
                }
                break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown relative qualifier: '%s'.", ctx.relativeQualifier().getText()));
            }
        }

        operator = operator.withOperand(timingOperator.getLeft(), timingOperator.getRight());
        libraryBuilder.resolveBinaryCall("System", operatorName, operator);

        return operator;
    }

    @Override
    public Object visitIncludesIntervalOperatorPhrase(@NotNull cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
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

        if (!isRightPoint &&
                !(timingOperator.getRight().getResultType() instanceof IntervalType
                        || timingOperator.getRight().getResultType() instanceof ListType)) {
            isRightPoint = true;
        }

        if (isRightPoint) {
            if (isProper) {
                if (dateTimePrecision != null) {
                    ProperContains properContains = of.createProperContains().withPrecision(parseDateTimePrecision(dateTimePrecision))
                            .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    libraryBuilder.resolveBinaryCall("System", "ProperContains", properContains);
                    return properContains;
                }

                ProperContains properContains = of.createProperContains()
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                libraryBuilder.resolveBinaryCall("System", "ProperContains", properContains);
                return properContains;
            }
            if (dateTimePrecision != null) {
                Contains contains = of.createContains().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                libraryBuilder.resolveBinaryCall("System", "Contains", contains);
                return contains;
            }

            Contains contains = of.createContains().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            libraryBuilder.resolveBinaryCall("System", "Contains", contains);
            return contains;
        }

        if (isProper) {

            if (dateTimePrecision != null) {
                ProperIncludes properIncludes = of.createProperIncludes().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                libraryBuilder.resolveBinaryCall("System", "ProperIncludes", properIncludes);
                return properIncludes;
            }

            ProperIncludes properIncludes = of.createProperIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            libraryBuilder.resolveBinaryCall("System", "ProperIncludes", properIncludes);
            return properIncludes;
        }

        if (dateTimePrecision != null) {
            Includes includes = of.createIncludes().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    .withOperand(timingOperator.getLeft(), timingOperator.getRight());
            libraryBuilder.resolveBinaryCall("System", "Includes", includes);
            return includes;
        }

        Includes includes = of.createIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        libraryBuilder.resolveBinaryCall("System", "Includes", includes);
        return includes;
    }

    @Override
    public Object visitIncludedInIntervalOperatorPhrase(@NotNull cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
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

        if (!isLeftPoint &&
                !(timingOperator.getLeft().getResultType() instanceof IntervalType
                        || timingOperator.getLeft().getResultType() instanceof ListType)) {
            isLeftPoint = true;
        }

        if (isLeftPoint) {
            if (isProper) {
                if (dateTimePrecision != null) {
                    ProperIn properIn = of.createProperIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                            .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    libraryBuilder.resolveBinaryCall("System", "ProperIn", properIn);
                    return properIn;
                }

                ProperIn properIn = of.createProperIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                libraryBuilder.resolveBinaryCall("System", "ProperIn", properIn);
                return properIn;
            }
            if (dateTimePrecision != null) {
                In in = of.createIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                libraryBuilder.resolveBinaryCall("System", "In", in);
                return in;
            }

            In in = of.createIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            libraryBuilder.resolveBinaryCall("System", "In", in);
            return in;
        }

        if (isProper) {
            if (dateTimePrecision != null) {
                ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                libraryBuilder.resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
                return properIncludedIn;
            }

            ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            libraryBuilder.resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
            return properIncludedIn;
        }

        if (dateTimePrecision != null) {
            IncludedIn includedIn = of.createIncludedIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    .withOperand(timingOperator.getLeft(), timingOperator.getRight());
            libraryBuilder.resolveBinaryCall("System", "IncludedIn", includedIn);
            return includedIn;
        }

        IncludedIn includedIn = of.createIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        libraryBuilder.resolveBinaryCall("System", "IncludedIn", includedIn);
        return includedIn;
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(@NotNull cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
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
        //* start of A in [start of B - 3 days, start of B)
        // A starts 3 days or less after start B
        //* start of A in (start of B, start of B + 3 days]

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
                        sameOrBefore.setPrecision(parseDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "SameOrBefore", sameOrBefore);
                    return sameOrBefore;

                } else {
                    SameOrAfter sameOrAfter = of.createSameOrAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        sameOrAfter.setPrecision(parseDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "SameOrAfter", sameOrAfter);
                    return sameOrAfter;
                }
            }
            else {
                if (isBefore) {
                    Before before = of.createBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        before.setPrecision(parseDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "Before", before);
                    return before;

                } else {
                    After after = of.createAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    if (dateTimePrecision != null) {
                        after.setPrecision(parseDateTimePrecision(dateTimePrecision));
                    }
                    libraryBuilder.resolveBinaryCall("System", "After", after);
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
                    sameAs.setPrecision(parseDateTimePrecision(dateTimePrecision));
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
                                    before.setPrecision(parseDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "Before", before);
                                return before;
                            }
                            else {
                                SameOrBefore sameOrBefore = of.createSameOrBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                                if (dateTimePrecision != null) {
                                    sameOrBefore.setPrecision(parseDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "SameOrBefore", sameOrBefore);
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
                                    after.setPrecision(parseDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "After", after);
                                return after;
                            }
                            else {
                                SameOrAfter sameOrAfter = of.createSameOrAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                                if (dateTimePrecision != null) {
                                    sameOrAfter.setPrecision(parseDateTimePrecision(dateTimePrecision));
                                }
                                libraryBuilder.resolveBinaryCall("System", "SameOrAfter", sameOrAfter);
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
                            in.setPrecision(parseDateTimePrecision(dateTimePrecision));
                        }
                        track(in, ctx.quantityOffset());
                        libraryBuilder.resolveBinaryCall("System", "In", in);
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
    public Object visitWithinIntervalOperatorPhrase(@NotNull cqlParser.WithinIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?
        // A starts within 3 days of start B
        //* start of A in [start of B - 3 days, start of B + 3 days]
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
        return in;
    }

    @Override
    public Object visitMeetsIntervalOperatorPhrase(@NotNull cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + (dateTimePrecision == null ? 0 : 1))) {
            operator = dateTimePrecision != null
                    ? of.createMeets().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    : of.createMeets();
            operatorName = "Meets";
        } else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = dateTimePrecision != null
                        ? of.createMeetsBefore().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createMeetsBefore();
                operatorName = "MeetsBefore";
            } else {
                operator = dateTimePrecision != null
                        ? of.createMeetsAfter().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createMeetsAfter();
                operatorName = "MeetsAfter";
            }
        }

        operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
        libraryBuilder.resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(@NotNull cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + (dateTimePrecision == null ? 0 : 1))) {
            operator = dateTimePrecision != null
                    ? of.createOverlaps().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    : of.createOverlaps();
            operatorName = "Overlaps";
        } else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = dateTimePrecision != null
                        ? of.createOverlapsBefore().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createOverlapsBefore();
                operatorName = "OverlapsBefore";
            } else {
                operator = dateTimePrecision != null
                        ? of.createOverlapsAfter().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createOverlapsAfter();
                operatorName = "OverlapsAfter";
            }
        }

        operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
        libraryBuilder.resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitStartsIntervalOperatorPhrase(@NotNull cqlParser.StartsIntervalOperatorPhraseContext ctx) {
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        Starts starts = (dateTimePrecision != null
                ? of.createStarts().withPrecision(parseDateTimePrecision(dateTimePrecision))
                : of.createStarts()
        ).withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());

        libraryBuilder.resolveBinaryCall("System", "Starts", starts);
        return starts;
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(@NotNull cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        Ends ends = (dateTimePrecision != null
                ? of.createEnds().withPrecision(parseDateTimePrecision(dateTimePrecision))
                : of.createEnds()
        ).withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());

        libraryBuilder.resolveBinaryCall("System", "Ends", ends);
        return ends;
    }

    public Expression resolveIfThenElse(If ifObject) {
        ifObject.setCondition(libraryBuilder.convertExpression(ifObject.getCondition(), libraryBuilder.resolveTypeName("System", "Boolean")));
        DataType resultType = libraryBuilder.ensureCompatibleTypes(ifObject.getThen().getResultType(), ifObject.getElse().getResultType());
        ifObject.setResultType(resultType);
        ifObject.setThen(libraryBuilder.ensureCompatible(ifObject.getThen(), resultType));
        ifObject.setElse(libraryBuilder.ensureCompatible(ifObject.getElse(), resultType));
        return ifObject;
    }

    @Override
    public Object visitIfThenElseExpressionTerm(@NotNull cqlParser.IfThenElseExpressionTermContext ctx) {
        If ifObject = of.createIf()
                .withCondition(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)))
                .withElse(parseExpression(ctx.expression(2)));

        return resolveIfThenElse(ifObject);
    }

    @Override
    public Object visitCaseExpressionTerm(@NotNull cqlParser.CaseExpressionTermContext ctx) {
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
    public Object visitCaseExpressionItem(@NotNull cqlParser.CaseExpressionItemContext ctx) {
        return of.createCaseItem()
                .withWhen(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)));
    }

    @Override
    public Object visitAggregateExpressionTerm(@NotNull cqlParser.AggregateExpressionTermContext ctx) {
        switch (ctx.getChild(0).getText()) {
            case "distinct":
                Distinct distinct = of.createDistinct().withOperand(parseExpression(ctx.expression()));
                libraryBuilder.resolveUnaryCall("System", "Distinct", distinct);
                return distinct;
            case "collapse":
                Collapse collapse = of.createCollapse().withOperand(parseExpression(ctx.expression()));
                libraryBuilder.resolveUnaryCall("System", "Collapse", collapse);
                return collapse;
            case "flatten":
                Flatten flatten = of.createFlatten().withOperand(parseExpression(ctx.expression()));
                libraryBuilder.resolveUnaryCall("System", "Flatten", flatten);
                return flatten;
        }

        throw new IllegalArgumentException(String.format("Unknown aggregate operator %s.", ctx.getChild(0).getText()));
    }

    @Override
    public Retrieve visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        String model = parseString(ctx.namedTypeSpecifier().modelIdentifier());
        String label = parseQualifiedString(ctx.namedTypeSpecifier().identifier());
        DataType dataType = libraryBuilder.resolveTypeName(model, label);
        if (dataType == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type name %s.", label));
        }

        if (!(dataType instanceof ClassType) || !((ClassType)dataType).isRetrievable()) {
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

        Retrieve retrieve = of.createRetrieve()
                .withDataType(libraryBuilder.dataTypeToQName((DataType)namedType))
                .withTemplateId(classType.getIdentifier());

        if (ctx.terminology() != null) {
            if (ctx.codePath() != null) {
                retrieve.setCodeProperty(parseString(ctx.codePath()));
            } else if (classType.getPrimaryCodePath() != null) {
                retrieve.setCodeProperty(classType.getPrimaryCodePath());
            }

            Property property = null;
            if (retrieve.getCodeProperty() == null) {
                libraryBuilder.recordParsingException(new CqlSemanticException("Retrieve has a terminology target but does not specify a code path and the type of the retrieve does not have a primary code path defined.",
                        useStrictRetrieveTyping ? CqlTranslatorException.ErrorSeverity.Error : CqlTranslatorException.ErrorSeverity.Warning,
                        getTrackBack(ctx)));
            }
            else {
                try {
                    DataType codeType = libraryBuilder.resolvePath((DataType) namedType, retrieve.getCodeProperty());
                    property = of.createProperty().withPath(retrieve.getCodeProperty());
                    property.setResultType(codeType);
                }
                catch (Exception e) {
                    libraryBuilder.recordParsingException(new CqlSemanticException(String.format("Could not resolve code path %s for the type of the retrieve %s.",
                            retrieve.getCodeProperty(), namedType.getName()), useStrictRetrieveTyping ? CqlTranslatorException.ErrorSeverity.Error : CqlTranslatorException.ErrorSeverity.Warning,
                            getTrackBack(ctx), e));
                }
            }

            Expression terminology = null;
            if (ctx.terminology().qualifiedIdentifier() != null) {
                List<String> identifiers = (List<String>) visit(ctx.terminology());
                terminology = resolveQualifiedIdentifier(identifiers);
            }
            else {
                terminology = parseExpression(ctx.terminology().expression());
            }

            // Resolve the terminology target using an in or = operator
            try {
                if (terminology.getResultType() instanceof ListType) {
                    Expression in = libraryBuilder.resolveIn(property, terminology);
                    if (in instanceof In) {
                        retrieve.setCodes(((In) in).getOperand().get(1));
                    } else if (in instanceof InValueSet) {
                        retrieve.setCodes(((InValueSet) in).getValueset());
                    } else if (in instanceof InCodeSystem) {
                        retrieve.setCodes(((InCodeSystem) in).getCodesystem());
                    } else {
                        libraryBuilder.recordParsingException(new CqlSemanticException(String.format("Unexpected membership operator %s in retrieve", in.getClass().getSimpleName()),
                                useStrictRetrieveTyping ? CqlTranslatorException.ErrorSeverity.Error : CqlTranslatorException.ErrorSeverity.Warning,
                                getTrackBack(ctx)));
                    }
                }
                else {
                    // Resolve with equality to verify the type of the target
                    BinaryExpression equal = of.createEqual().withOperand(property, terminology);
                    libraryBuilder.resolveBinaryCall("System", "Equal", equal);

                    // Automatically promote to a list for use in the retrieve target
                    retrieve.setCodes(libraryBuilder.resolveToList(equal.getOperand().get(1)));
                }
            }
            catch (Exception e) {
                // If something goes wrong attempting to resolve, just set to the expression and report it as a warning,
                // it shouldn't prevent translation unless the modelinfo indicates strict retrieve typing
                retrieve.setCodes(terminology);
                libraryBuilder.recordParsingException(new CqlSemanticException("Could not resolve membership operator for terminology target of the retrieve.",
                        useStrictRetrieveTyping ? CqlTranslatorException.ErrorSeverity.Error : CqlTranslatorException.ErrorSeverity.Warning,
                        getTrackBack(ctx), e));
            }
        }

        retrieves.add(retrieve);

        retrieve.setResultType(new ListType((DataType) namedType));

        return retrieve;
    }

    @Override
    public Object visitSingleSourceClause(@NotNull cqlParser.SingleSourceClauseContext ctx) {
        List<AliasedQuerySource> sources = new ArrayList<>();
        sources.add((AliasedQuerySource) visit(ctx.aliasedQuerySource()));
        return sources;
    }

    @Override
    public Object visitMultipleSourceClause(@NotNull cqlParser.MultipleSourceClauseContext ctx) {
        List<AliasedQuerySource> sources = new ArrayList<>();
        for (cqlParser.AliasedQuerySourceContext source : ctx.aliasedQuerySource()) {
            sources.add((AliasedQuerySource) visit(source));
        }
        return sources;
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
        QueryContext queryContext = new QueryContext();
        libraryBuilder.pushQueryContext(queryContext);
        try {

            List<AliasedQuerySource> sources;
            queryContext.enterSourceClause();
            try {
                sources = (List<AliasedQuerySource>)visit(ctx.sourceClause());
            }
            finally {
                queryContext.exitSourceClause();
            }

            queryContext.addPrimaryQuerySources(sources);

            // If we are evaluating a population-level query whose source ranges over any patient-context expressions,
            // then references to patient context expressions within the iteration clauses of the query can be accessed
            // at the patient, rather than the population, context.
            boolean expressionContextPushed = false;
            if (libraryBuilder.inPopulationContext() && queryContext.referencesPatientContext()) {
                libraryBuilder.pushExpressionContext("Patient");
                expressionContextPushed = true;
            }
            try {

                List<LetClause> dfcx = ctx.letClause() != null ? (List<LetClause>) visit(ctx.letClause()) : null;

                List<RelationshipClause> qicx = new ArrayList<>();
                if (ctx.queryInclusionClause() != null) {
                    for (cqlParser.QueryInclusionClauseContext queryInclusionClauseContext : ctx.queryInclusionClause()) {
                        qicx.add((RelationshipClause) visit(queryInclusionClauseContext));
                    }
                }

                Expression where = ctx.whereClause() != null ? (Expression) visit(ctx.whereClause()) : null;
                if (dateRangeOptimization && where != null) {
                    for (AliasedQuerySource aqs : sources) {
                        where = optimizeDateRangeInQuery(where, aqs);
                    }
                }

                ReturnClause ret = ctx.returnClause() != null ? (ReturnClause) visit(ctx.returnClause()) : null;
                if ((ret == null) && (sources.size() > 1)) {
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

                DataType queryResultType = ret == null ? sources.get(0).getResultType() : ret.getResultType();
                queryContext.setResultElementType(queryContext.isSingular() ? null : ((ListType)queryResultType).getElementType());
                SortClause sort = null;
                if (ctx.sortClause() != null) {
                    if (queryContext.isSingular()) {
                        throw new IllegalArgumentException("Sort clause cannot be used in a singular query.");
                    }
                    queryContext.enterSortClause();
                    try {
                        sort = (SortClause)visit(ctx.sortClause());
                        // Validate that the sort can be performed based on the existence of comparison operators for all types involved
                        for (SortByItem sortByItem : sort.getBy()) {
                            if (sortByItem instanceof ByDirection) {
                                // validate that there is a comparison operator defined for the result element type of the query context
                                libraryBuilder.verifyComparable(queryContext.getResultElementType());		                    }
                            else {
                                libraryBuilder.verifyComparable(sortByItem.getResultType());
                            }
                        }
                    }
                    finally {
                        queryContext.exitSortClause();
                    }
                }

                Query query = of.createQuery()
                        .withSource(sources)
                        .withLet(dfcx)
                        .withRelationship(qicx)
                        .withWhere(where)
                        .withReturn(ret)
                        .withSort(sort);

                query.setResultType(queryResultType);
                return query;
            }
            finally {
                if (expressionContextPushed) {
                    libraryBuilder.popExpressionContext();
                }
            }

        } finally {
            libraryBuilder.popQueryContext();
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
    public Object visitLetClause(@NotNull cqlParser.LetClauseContext ctx) {
        List<LetClause> letClauseItems = new ArrayList<>();
        for (cqlParser.LetClauseItemContext letClauseItem : ctx.letClauseItem()) {
            letClauseItems.add((LetClause) visit(letClauseItem));
        }
        return letClauseItems;
    }

    @Override
    public Object visitLetClauseItem(@NotNull cqlParser.LetClauseItemContext ctx) {
        LetClause letClause = of.createLetClause().withExpression(parseExpression(ctx.expression()))
                .withIdentifier(parseString(ctx.identifier()));
        letClause.setResultType(letClause.getExpression().getResultType());
        libraryBuilder.peekQueryContext().addLetClause(letClause);
        return letClause;
    }

    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        AliasedQuerySource source = of.createAliasedQuerySource().withExpression(parseExpression(ctx.querySource()))
                .withAlias(parseString(ctx.alias()));
        source.setResultType(source.getExpression().getResultType());
        return source;
    }

    @Override
    public Object visitWithClause(@NotNull cqlParser.WithClauseContext ctx) {
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
    public Object visitWithoutClause(@NotNull cqlParser.WithoutClauseContext ctx) {
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
    public Object visitWhereClause(@NotNull cqlParser.WhereClauseContext ctx) {
        Expression result = (Expression)visit(ctx.expression());
        DataTypes.verifyType(result.getResultType(), libraryBuilder.resolveTypeName("System", "Boolean"));
        return result;
    }

    @Override
    public Object visitReturnClause(@NotNull cqlParser.ReturnClauseContext ctx) {
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
    public SortDirection visitSortDirection(@NotNull cqlParser.SortDirectionContext ctx) {
        if (ctx.getText().equals("desc")) {
            return SortDirection.DESC;
        }

        return SortDirection.ASC;
    }

    private SortDirection parseSortDirection(cqlParser.SortDirectionContext ctx) {
        if (ctx != null) {
            return visitSortDirection(ctx);
        }

        return SortDirection.ASC;
    }

    @Override
    public SortByItem visitSortByItem(@NotNull cqlParser.SortByItemContext ctx) {
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
    public Object visitSortClause(@NotNull cqlParser.SortClauseContext ctx) {
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
    public Object visitQuerySource(@NotNull cqlParser.QuerySourceContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.retrieve() != null) {
            return visit(ctx.retrieve());
        } else {
            List<String> identifiers = (List<String>) visit(ctx.qualifiedIdentifier());
            return resolveQualifiedIdentifier(identifiers);
        }
    }

    @Override
    public Object visitIndexedExpressionTerm(@NotNull cqlParser.IndexedExpressionTermContext ctx) {
        Indexer indexer = of.createIndexer()
                .withOperand(parseExpression(ctx.expressionTerm()))
                .withOperand(parseExpression(ctx.expression()));

        // TODO: Support zero-based indexers as defined by the isZeroBased attribute
        libraryBuilder.resolveBinaryCall("System", "Indexer", indexer);
        return indexer;
    }

    @Override
    public Expression visitInvocationExpressionTerm(@NotNull cqlParser.InvocationExpressionTermContext ctx) {
        Expression left = parseExpression(ctx.expressionTerm());
        libraryBuilder.pushExpressionTarget(left);
        try {
            return (Expression)visit(ctx.invocation());
        }
        finally {
            libraryBuilder.popExpressionTarget();
        }
    }

    @Override
    public Expression visitExternalConstant(@NotNull cqlParser.ExternalConstantContext ctx) {
        return libraryBuilder.resolveIdentifier(ctx.getText(), true);
    }

    @Override
    public Expression visitThisInvocation(@NotNull cqlParser.ThisInvocationContext ctx) {
        return libraryBuilder.resolveIdentifier(ctx.getText(), true);
    }

    @Override
    public Expression visitMemberInvocation(@NotNull cqlParser.MemberInvocationContext ctx) {
        String identifier = parseString(ctx.identifier());
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

    private Expression resolveIdentifier(String identifier) {
        // If the identifier cannot be resolved in the library builder, check for forward declarations for expressions and parameters
        Expression result = libraryBuilder.resolveIdentifier(identifier, false);
        if (result == null) {
            ExpressionDefinitionInfo expressionInfo = libraryInfo.resolveExpressionReference(identifier);
            if (expressionInfo != null) {
                String saveContext = currentContext;
                currentContext = expressionInfo.getContext();
                try {
                    Stack<Chunk> saveChunks = chunks;
                    chunks = new Stack<Chunk>();
                    forwards.push(expressionInfo);
                    try {
                        // Have to call the visit to get the outer processing to occur
                        visit(expressionInfo.getDefinition());
                    }
                    finally {
                        chunks = saveChunks;
                        forwards.pop();
                    }
                } finally {
                    currentContext = saveContext;
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

    private Expression resolveFunction(String libraryName, @NotNull cqlParser.FunctionContext ctx) {
        String functionName = parseString(ctx.identifier());
        if ((libraryName == null || libraryName.equals("System")) && functionName.equals("distinct")) {
            // Because distinct can be both a keyword and the name of a method, it can be resolved by the
            // parser as a function, instead of as an aggregateExpressionTerm. In this case, the function
            // name needs to be translated to the System function name in order to resolve.
            functionName = "Distinct";
        }
        return resolveFunction(libraryName, functionName, ctx.paramList());
    }

    private Expression resolveFunction(String libraryName, String functionName, cqlParser.ParamListContext paramList) {
        List<Expression> expressions = new ArrayList<Expression>();

        if (paramList != null && paramList.expression() != null) {
            for (cqlParser.ExpressionContext expressionContext : paramList.expression()) {
                expressions.add((Expression)visit(expressionContext));
            }
        }

        // If the function cannot be resolved in the builder and the call is to a function in the current library,
        // check for forward declarations of functions
        boolean checkForward = libraryName == null || libraryName.equals("") || libraryName.equals(this.libraryInfo.getLibraryName());
        Expression result = libraryBuilder.resolveFunction(libraryName, functionName, expressions, !checkForward);
        if (result == null) {
            Iterable<FunctionDefinitionInfo> functionInfos = libraryInfo.resolveFunctionReference(functionName);
            if (functionInfos != null) {
                for (FunctionDefinitionInfo functionInfo : functionInfos) {
                    String saveContext = currentContext;
                    currentContext = functionInfo.getContext();
                    try {
                        Stack<Chunk> saveChunks = chunks;
                        chunks = new Stack<Chunk>();
                        forwardFunctions.push(functionInfo);
                        try {
                            // Have to call the visit to allow the outer processing to occur
                            visit(functionInfo.getDefinition());
                        }
                        finally {
                            forwardFunctions.pop();
                            chunks = saveChunks;
                        }
                    } finally {
                        currentContext = saveContext;
                    }
                }
            }
            result = libraryBuilder.resolveFunction(libraryName, functionName, expressions, true);
        }

        return result;
    }

    @Override
    public Expression visitFunction(@NotNull cqlParser.FunctionContext ctx) {
        if (libraryBuilder.hasExpressionTarget()) {
            Expression target = libraryBuilder.popExpressionTarget();
            try {
                // If the target is a library reference, resolve as a standard qualified call
                if (target instanceof LibraryRef) {
                    return resolveFunction(((LibraryRef)target).getLibraryName(), ctx);
                }

                // NOTE: FHIRPath method invocation
                // If the target is an expression, resolve as a method invocation
                if (target instanceof Expression && methodInvocation) {
                    return systemMethodResolver.resolveMethod((Expression)target, ctx, true);
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
            Expression result = systemMethodResolver.resolveMethod(thisRef, ctx, false);
            if (result != null) {
                return result;
            }
        }

        // If there is no target, resolve as a system function
        return resolveFunction(null, ctx);
    }

    @Override
    public Object visitFunctionBody(@NotNull cqlParser.FunctionBodyContext ctx) {
        return visit(ctx.expression());
    }

    public Object internalVisitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDef fun = of.createFunctionDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()));
        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                TypeSpecifier typeSpecifier = parseTypeSpecifier(opdef.typeSpecifier());
                fun.getOperand().add(
                        (OperandDef)of.createOperandDef()
                                .withName(parseString(opdef.identifier()))
                                .withOperandTypeSpecifier(typeSpecifier)
                                .withResultType(typeSpecifier.getResultType())
                );
            }
        }

        TypeSpecifier resultType = null;
        if (ctx.typeSpecifier() != null) {
            resultType = parseTypeSpecifier(ctx.typeSpecifier());
        }

        if (!libraryBuilder.getTranslatedLibrary().contains(fun)) {
            if (ctx.functionBody() != null) {
                libraryBuilder.beginFunctionDef(fun);
                try {
                    libraryBuilder.pushExpressionContext(currentContext);
                    try {
                        libraryBuilder.pushExpressionDefinition(String.format("%s()", fun.getName()));
                        try {
                            fun.setExpression(parseExpression(ctx.functionBody()));
                        } finally {
                            libraryBuilder.popExpressionDefinition();
                        }
                    } finally {
                        libraryBuilder.popExpressionContext();
                    }
                } finally {
                    libraryBuilder.endFunctionDef();
                }

                if (resultType != null && fun.getExpression() != null && fun.getExpression().getResultType() != null) {
                    if (!DataTypes.subTypeOf(fun.getExpression().getResultType(), resultType.getResultType())) {
                        throw new IllegalArgumentException(String.format("Function %s has declared return type %s but the function body returns incompatible type %s.",
                                fun.getName(), resultType.getResultType(), fun.getExpression().getResultType()));
                    }
                }

                fun.setResultType(fun.getExpression().getResultType());
            }
            else {
                fun.setExternal(true);
                if (resultType == null) {
                    throw new IllegalArgumentException(String.format("Function %s is marked external but does not declare a return type.", fun.getName()));
                }
                fun.setResultType(resultType.getResultType());
            }

            fun.setContext(currentContext);
            if (fun.getResultType() != null) {
                libraryBuilder.addExpression(fun);
            }
        }

        return fun;
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDef result = (FunctionDef)internalVisitFunctionDefinition(ctx);
        Operator operator = Operator.fromFunctionDef(result);
        if (forwardFunctions.isEmpty() || !forwardFunctions.peek().getName().equals(operator.getName())) {
            Set<Signature> definedSignatures = definedFunctionDefinitions.get(operator.getName());
            if (definedSignatures == null) {
                definedSignatures = new HashSet<>();
                definedFunctionDefinitions.put(operator.getName(), definedSignatures);
            }

            if (definedSignatures.contains(operator.getSignature())) {
                throw new IllegalArgumentException(String.format("A function named %s with the same type of arguments is already defined in this library.", operator.getName()));
            }

            definedSignatures.add(operator.getSignature());
        }

        return result;
    }

    private AccessModifier parseAccessModifier(ParseTree pt) {
        return pt == null ? AccessModifier.PUBLIC : (AccessModifier)visit(pt);
    }

    public String parseString(ParseTree pt) {
        return StringEscapeUtils.unescapeCql(pt == null ? null : (String) visit(pt));
    }

    public String parseQualifiedString( List<cqlParser.IdentifierContext> ids) {
    	StringBuilder sb = new StringBuilder();
    	for( cqlParser.IdentifierContext idctx : ids ) {
    		String id = parseString( idctx );
    		if ( id != null ) {
    			if ( sb.length() > 0 ) {
    				sb.append( "." );
			    }
    			sb.append( id );
		    } else {
    			return null;
		    }
	    }
	    return sb.toString();
    }

    private Expression parseExpression(ParseTree pt) {
        return pt == null ? null : (Expression) visit(pt);
    }

    private TypeSpecifier parseTypeSpecifier(ParseTree pt) {
        return pt == null ? null : (TypeSpecifier) visit(pt);
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

    private void addExpression(Expression expression) {
        expressions.add(expression);
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
        if (locate && tb != null) {
                element.setLocator(tb.toLocator());
            }

            if (resultTypes && element.getResultType() != null) {
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
