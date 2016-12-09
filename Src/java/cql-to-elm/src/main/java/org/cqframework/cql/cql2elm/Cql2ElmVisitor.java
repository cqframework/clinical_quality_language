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
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.cql_annotations.r1.ErrorType;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Interval;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cql2ElmVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean annotate = false;
    private boolean dateRangeOptimization = false;
    private boolean detailedErrors = false;

    private TokenStream tokenStream;

    private LibraryManager libraryManager = null;
    private LibraryInfo libraryInfo = null;
    private Library library = null;
    private TranslatedLibrary translatedLibrary = null;
    private String currentContext = "Patient"; // default context to patient
    private final SystemFunctionResolver systemFunctionResolver = new SystemFunctionResolver(this);

    //Put them here for now, but eventually somewhere else?
    private final Map<String, TranslatedLibrary> libraries = new HashMap<>();
    private final ConversionMap conversionMap = new ConversionMap();
    private final ExpressionDefinitionContextStack expressionDefinitions = new ExpressionDefinitionContextStack();
    private final Set<String> definedExpressionDefinitions = new HashSet<>();
    private final Map<String, Set<Signature>> definedFunctionDefinitions = new HashMap<>();
    private final Stack<QueryContext> queries = new Stack<>();
    private final Stack<String> expressionContext = new Stack<>();
    private final Stack<TimingOperatorContext> timingOperators = new Stack<>();
    private final Stack<Narrative> narratives = new Stack<>();
    private final Stack<Expression> targets = new Stack<>();
    private FunctionDef currentFunctionDef = null;
    private int currentToken = -1;
    private int nextLocalId = 1;
    private final List<Retrieve> retrieves = new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private final List<CqlTranslatorException> errors = new ArrayList<>();
    private final Map<String, Model> models = new HashMap<>();
    private boolean implicitPatientCreated = false;

    public Cql2ElmVisitor(LibraryManager libraryManager) {
      super();
      this.libraryManager = libraryManager;
    }

    /**
     * Record any errors while parsing in both the list of errors but also in the library
     * itself so they can be processed easily by a remote client
     * @param e the exception to record
     */
    public void recordParsingException(CqlTranslatorException e) {
      errors.add(e);
      CqlToElmError err = af.createCqlToElmError();
      err.setMessage(e.getMessage());
      err.setErrorType(e instanceof CqlSyntaxException ? ErrorType.SYNTAX : ErrorType.SEMANTIC);
      if (e.getLocator() != null) {
        err.setStartLine(e.getLocator().getStartLine());
        err.setEndLine(e.getLocator().getEndLine());
        err.setStartChar(e.getLocator().getStartChar());
        err.setEndChar(e.getLocator().getEndChar());
      }
      if (e.getCause() != null && e.getCause() instanceof CqlTranslatorIncludeException) {
        CqlTranslatorIncludeException incEx = (CqlTranslatorIncludeException)e.getCause();
        err.setTargetIncludeLibraryId(incEx.getLibraryId());
        err.setTargetIncludeLibraryVersionId(incEx.getVersionId());
        err.setErrorType(ErrorType.INCLUDE);
      }
      getOrInitializeLibrary().getAnnotation().add(err);
    }

    public void enableAnnotations() {
        annotate = true;
    }

    public void disableAnnotations() {
        annotate = false;
    }

    public void enableDateRangeOptimization() {
        dateRangeOptimization = true;
    }

    public void disableDateRangeOptimization() {
        dateRangeOptimization = false;
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

    public TokenStream getTokenStream() {
        return tokenStream;
    }

    public void setTokenStream(TokenStream value) {
        tokenStream = value;
    }

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    public void setLibraryInfo(LibraryInfo value) {
        libraryInfo = value;
    }

    public Library getLibrary() {
        return library;
    }

    public TranslatedLibrary getTranslatedLibrary() {
        return translatedLibrary;
    }

    public List<Retrieve> getRetrieves() {
        return retrieves;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public List<CqlTranslatorException> getErrors() {
        return errors;
    }

    private int getNextLocalId() {
        return nextLocalId++;
    }

    private void pushNarrative(@NotNull ParseTree tree) {
        org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();
        // Debugging line for Github issues #61 & 86
        //System.out.println(String.format("Push: %d..%d, Tree: %s", sourceInterval.a, sourceInterval.b, tree.getText()));

        // If there is a parent narrative
        // add the text from the current text pointer to the start of the new source context to the narrative
        Narrative parentNarrative = narratives.isEmpty() ? null : narratives.peek();
        if (parentNarrative != null && sourceInterval.a - 1 - currentToken >= 0) {
            org.antlr.v4.runtime.misc.Interval tokenInterval =
                    new org.antlr.v4.runtime.misc.Interval(currentToken, sourceInterval.a - 1);
            String content = tokenStream.getText(tokenInterval);
            // Debugging line for Github issues #61 & 86
            //System.out.println(String.format("CurrentToken: %d, Content: %s", currentToken, content));
            parentNarrative.getContent().add(content);
        }

        // advance the token pointer to the start of the new source context
        currentToken = sourceInterval.a;

        // Create a new narrative
        // add it to the parent narrative, if there is one
        // push it on the narrative stack
        Narrative newNarrative = af.createNarrative();
        narratives.push(newNarrative);
    }

    private Narrative popNarrative(@NotNull ParseTree tree, Object o) {
        org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();
        // Debugging line for Github issues #61 & 86
        //System.out.println(String.format("Pop: %d..%d, Tree: %s", sourceInterval.a, sourceInterval.b, tree.getText()));

        // Pop the narrative off the narrative stack
        Narrative currentNarrative = narratives.pop();

        // Add the text from the current token pointer to the end of the current source context to the narrative
        if (sourceInterval.b - currentToken >= 0) {
            org.antlr.v4.runtime.misc.Interval tokenInterval =
                    new org.antlr.v4.runtime.misc.Interval(currentToken, sourceInterval.b);
            String content = tokenStream.getText(tokenInterval);
            // Debugging line for Github issues #61 & 86
            //System.out.println(String.format("CurrentToken: %d, Content: %s", currentToken, content));
            currentNarrative.getContent().add(content);
        }

        // Advance the token pointer after the end of the current source context
        currentToken = sourceInterval.b + 1;

        // If the narrative corresponds to an element returned by the parser
        //   if the element doesn't have a localId
        //     set the narrative's reference id
        //     if there is a parent narrative
        //       add this narrative to the content of the parent
        // else
        //   if there is a parent narrative
        //     add the contents of this narrative to that narrative
        if (o instanceof Element) {
            Element element = (Element) o;
            if (element.getLocalId() == null) {
                element.setLocalId(Integer.toString(getNextLocalId()));
                currentNarrative.setR(element.getLocalId());

                if (!narratives.isEmpty()) {
                    Narrative parentNarrative = narratives.peek();
                    parentNarrative.getContent().add(
                            new JAXBElement<>(
                                    new QName("urn:hl7-org:cql-annotations:r1", "s"),
                                    Narrative.class,
                                    currentNarrative));
                }

                // If the current element is an expression def, set the narrative as the annotation
                if (o instanceof ExpressionDef) {
                    // Github issues #61 & #86 -> Full resolution requires rethinking the way this narrative is produced
                    // Emitting with a single span for now to address both issues until we have a need to emit with links
                    // to the local ids.
                    //expressionDef.getAnnotation().add(af.createAnnotation().withS(currentNarrative));
                    ExpressionDef expressionDef = (ExpressionDef) o;
                    Narrative defNarrative = af.createNarrative();
                    String content = tokenStream.getText(sourceInterval);
                    defNarrative.getContent().add(content);
                    expressionDef.getAnnotation().add(af.createAnnotation().withS(defNarrative));
                }
            } else {
                if (!narratives.isEmpty()) {
                    Narrative parentNarrative = narratives.peek();
                    parentNarrative.getContent().addAll(currentNarrative.getContent());
                }
            }
        } else {
            if (!narratives.isEmpty()) {
                Narrative parentNarrative = narratives.peek();
                parentNarrative.getContent().addAll(currentNarrative.getContent());
            }
        }

        return currentNarrative;
    }

    @Override
    public Object visit(@NotNull ParseTree tree) {
        if (annotate) {
            pushNarrative(tree);
        }
        Object o = null;
        try {
            try {
                o = super.visit(tree);
            } catch (CqlTranslatorIncludeException e) {
                recordParsingException(new CqlTranslatorException(e.getMessage(), getTrackBack((ParserRuleContext) tree), e));
            } catch (CqlTranslatorException e) {
                recordParsingException(e);
            } catch (Exception e) {
                CqlTranslatorException ex = new CqlSemanticException(
                        e.getMessage() == null ? "Internal translator error." : e.getMessage(),
                        tree instanceof ParserRuleContext ? getTrackBack((ParserRuleContext) tree) : null,
                        e);

                Exception rootCause = determineRootCause();
                if (rootCause == null) {
                    rootCause = ex;
                    recordParsingException(ex);
                    setRootCause(rootCause);
                }
                else {
                    if (detailedErrors) {
                        recordParsingException(ex);
                    }
                }
                o = of.createNull();
            }
        } finally {
            if (annotate) {
                popNarrative(tree, o);
            }
        }

        if (o instanceof Trackable && tree instanceof ParserRuleContext && !(tree instanceof cqlParser.LibraryContext)) {
            this.track((Trackable) o, (ParserRuleContext) tree);
        }
        if (o instanceof Expression) {
            addExpression((Expression) o);
        }

        return o;
    }

    private Library getOrInitializeLibrary() {
      if (library == null) {
        library = of.createLibrary()
                .withSchemaIdentifier(of.createVersionedIdentifier()
                        .withId("urn:hl7-org:elm") // TODO: Pull this from the ELM library namespace
                        .withVersion("r1"));
      }
      return library;
    }

    @Override
    public Object visitLibrary(@NotNull cqlParser.LibraryContext ctx) {
        getOrInitializeLibrary();
        translatedLibrary = new TranslatedLibrary();
        translatedLibrary.setLibrary(library);

        loadSystemLibrary();

        if (this.libraryInfo.getLibraryName() == null) {
            this.libraryInfo.setLibraryName("Anonymous");
        }

        Object lastResult = null;
        libraryManager.beginTranslation(this.libraryInfo.getLibraryName());
        try {
            // Loop through and call visit on each child (to ensure they are tracked)
            for (int i = 0; i < ctx.getChildCount(); i++) {
                lastResult = visit(ctx.getChild(i));
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            return lastResult;
        }
        finally {
            libraryManager.endTranslation(this.libraryInfo.getLibraryName());
        }
    }

    @Override
    public VersionedIdentifier visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        VersionedIdentifier vid = of.createVersionedIdentifier()
                .withId(parseString(ctx.identifier()))
                .withVersion(parseString(ctx.versionSpecifier()));
        setLibraryIdentifier(vid);

        return vid;
    }

    @Override
    public UsingDef visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        Model model = getModel(parseString(ctx.modelIdentifier()), parseString(ctx.versionSpecifier()));
        return translatedLibrary.resolveUsingRef(model.getModelInfo().getName());
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        IncludeDef library = of.createIncludeDef()
                .withLocalIdentifier(parseString(ctx.localIdentifier()))
                .withPath(parseString(ctx.identifier()))
                .withVersion(parseString(ctx.versionSpecifier()));

        addToLibrary(library);

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
                verifyType(param.getDefault().getResultType(), paramType);
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
            param.setDefault(ensureCompatible(param.getDefault(), paramType));
        }

        addToLibrary(param);

        return param;
    }

    @Override
    public NamedTypeSpecifier visitNamedTypeSpecifier(@NotNull cqlParser.NamedTypeSpecifierContext ctx) {
        DataType resultType = resolveTypeName(parseString(ctx.modelIdentifier()), parseString(ctx.identifier()));
        NamedTypeSpecifier result = of.createNamedTypeSpecifier()
                .withName(dataTypeToQName(resultType));

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
                .withResultType(new ListType(resolveTypeName("System", "Code")));

        addToLibrary(cs);
        return cs;
    }

    @Override
    public CodeSystemRef visitCodesystemIdentifier(@NotNull cqlParser.CodesystemIdentifierContext ctx) {
        String libraryName = parseString(ctx.libraryIdentifier());
        String name = parseString(ctx.identifier());
        CodeSystemDef def;
        if (libraryName != null) {
            def = resolveLibrary(libraryName).resolveCodeSystemRef(name);
            checkAccessLevel(libraryName, name, def.getAccessLevel());
        }
        else {
            def = translatedLibrary.resolveCodeSystemRef(name);
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
            def = resolveLibrary(libraryName).resolveCodeRef(name);
            checkAccessLevel(libraryName, name, def.getAccessLevel());
        }
        else {
            def = translatedLibrary.resolveCodeRef(name);
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
        vs.setResultType(new ListType(resolveTypeName("System", "Code")));
        addToLibrary(vs);

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

        cd.setResultType(resolveTypeName("Code"));
        addToLibrary(cd);

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

        cd.setResultType(resolveTypeName("Concept"));
        addToLibrary(cd);

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
            if (hasUsings()) {
                String patientTypeName = getModel().getModelInfo().getPatientClassName();
                if (patientTypeName == null || patientTypeName.equals("")) {
                    throw new IllegalArgumentException("Model definition does not contain enough information to construct a patient context.");
                }
                DataType patientType = resolveTypeName(getModel().getModelInfo().getName(), patientTypeName);
                Retrieve patientRetrieve = of.createRetrieve().withDataType(dataTypeToQName(patientType));
                patientRetrieve.setResultType(new ListType(patientType));
                String patientClassIdentifier = getModel().getModelInfo().getPatientClassIdentifier();
                if (patientClassIdentifier != null) {
                    patientRetrieve.setTemplateId(patientClassIdentifier);
                }

                ExpressionDef patientExpressionDef = of.createExpressionDef()
                        .withName("Patient")
                        .withContext(currentContext)
                        .withExpression(of.createSingletonFrom().withOperand(patientRetrieve));
                patientExpressionDef.getExpression().setResultType(patientType);
                patientExpressionDef.setResultType(patientType);
                addToLibrary(patientExpressionDef);
            }
            else {
                ExpressionDef patientExpressionDef = of.createExpressionDef()
                        .withName("Patient")
                        .withContext(currentContext)
                        .withExpression(of.createNull());
                patientExpressionDef.getExpression().setResultType(resolveTypeName("System", "Any"));
                patientExpressionDef.setResultType(patientExpressionDef.getExpression().getResultType());
                addToLibrary(patientExpressionDef);
            }

            implicitPatientCreated = true;
            return currentContext;
        }

        return currentContext;
    }

    public ExpressionDef internalVisitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        String identifier = parseString(ctx.identifier());
        ExpressionDef def = translatedLibrary.resolveExpressionRef(identifier);
        if (def == null) {
            pushExpressionDefinition(identifier);
            pushExpressionContext(currentContext);
            try {
                def = of.createExpressionDef()
                        .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                        .withName(identifier)
                        .withContext(currentContext)
                        .withExpression((Expression) visit(ctx.expression()));
                def.setResultType(def.getExpression().getResultType());
                addToLibrary(def);
            }
            finally {
                popExpressionDefinition();
                popExpressionContext();
            }
        }

        return def;
    }

    @Override
    public ExpressionDef visitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDef expressionDef = internalVisitExpressionDefinition(ctx);
        if (definedExpressionDefinitions.contains(expressionDef.getName())) {
            throw new IllegalArgumentException(String.format("Identifier %s is already in use in this library.", expressionDef.getName()));
        }

        // Track defined expression definitions locally, otherwise duplicate expression definitions will be missed because they are
        // overwritten by name when they are encountered by the preprocessor.
        definedExpressionDefinitions.add(expressionDef.getName());
        return expressionDef;
    }

    @Override
    public Literal visitStringLiteral(@NotNull cqlParser.StringLiteralContext ctx) {
        return createLiteral(parseString(ctx.STRING()));
    }

    @Override
    public Literal visitBooleanLiteral(@NotNull cqlParser.BooleanLiteralContext ctx) {
        return createLiteral(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Object visitIntervalSelector(@NotNull cqlParser.IntervalSelectorContext ctx) {
        return createInterval(parseExpression(ctx.expression(0)), ctx.getChild(1).getText().equals("["),
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
            DataType propertyType = resolveProperty(classTypeSpecifier.getResultType(), element.getName());
            element.setValue(ensureCompatible(element.getValue(), propertyType));
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

        code.setResultType(resolveTypeName("System", "Code"));
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

        concept.setResultType(resolveTypeName("System", "Concept"));
        return concept;
    }

    @Override
    public Object visitListSelector(@NotNull cqlParser.ListSelectorContext ctx) {
        TypeSpecifier elementTypeSpecifier = parseTypeSpecifier(ctx.typeSpecifier());
        org.hl7.elm.r1.List list = of.createList();
        ListType listType = null;
        if (elementTypeSpecifier != null) {
            ListTypeSpecifier listTypeSpecifier = of.createListTypeSpecifier().withElementType(elementTypeSpecifier);
            listType = new ListType(elementTypeSpecifier.getResultType());
            listTypeSpecifier.setResultType(listType);
        }

        DataType elementType = elementTypeSpecifier != null ? elementTypeSpecifier.getResultType() : null;
        DataType inferredElementType = null;

        List<Expression> elements = new ArrayList<>();
        for (cqlParser.ExpressionContext elementContext : ctx.expression()) {
            Expression element = parseExpression(elementContext);

            if (elementType != null) {
                verifyType(element.getResultType(), elementType);
            }
            else {
                if (inferredElementType == null) {
                    inferredElementType = element.getResultType();
                }
                else {
                    inferredElementType = ensureCompatibleTypes(inferredElementType, element.getResultType());
                }
            }

            elements.add(element);
        }

        if (elementType == null) {
            elementType = inferredElementType == null ? resolveTypeName("System", "Any") : inferredElementType;
        }

        for (Expression element : elements) {
            if (!elementType.isSuperTypeOf(element.getResultType())) {
                list.getElement().add(convertExpression(element, elementType));
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
                result.setHour(createLiteral(hour));

                if (matcher.group(4) != null) {
                    minute = Integer.parseInt(matcher.group(4));
                    if (minute < 0 || minute >= 60 || (hour == 24 && minute > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid minute in time literal (%s).", input));
                    }
                    result.setMinute(createLiteral(minute));
                }

                if (matcher.group(6) != null) {
                    second = Integer.parseInt(matcher.group(6));
                    if (second < 0 || second >= 60 || (hour == 24 && second > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid second in time literal (%s).", input));
                    }
                    result.setSecond(createLiteral(second));
                }

                if (matcher.group(8) != null) {
                    millisecond = Integer.parseInt(matcher.group(8));
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid millisecond in time literal (%s).", input));
                    }
                    result.setMillisecond(createLiteral(millisecond));
                }

                if (matcher.group(10) != null && matcher.group(10).equals("Z")) {
                    result.setTimezoneOffset(createLiteral(0.0));
                }

                if (matcher.group(12) != null) {
                    int offsetPolarity = matcher.group(12).equals("+") ? 1 : 0;

                    if (matcher.group(15) != null) {
                        int hourOffset = Integer.parseInt(matcher.group(13));
                        if (hourOffset < 0 || hourOffset > 14) {
                            throw new IllegalArgumentException(String.format("Timezone hour offset out of range in time literal (%s).", input));
                        }

                        int minuteOffset = Integer.parseInt(matcher.group(15));
                        if (minuteOffset < 0 || minuteOffset >= 60 || (hourOffset == 14 && minuteOffset > 0)) {
                            throw new IllegalArgumentException(String.format("Timezone minute offset out of range in time literal (%s).", input));
                        }
                        result.setTimezoneOffset(createLiteral((double)(hourOffset + (minuteOffset / 60)) * offsetPolarity));
                    }
                    else {
                        if (matcher.group(13) != null) {
                            int hourOffset = Integer.parseInt(matcher.group(13));
                            if (hourOffset < 0 || hourOffset > 14) {
                                throw new IllegalArgumentException(String.format("Timezone hour offset out of range in time literal (%s).", input));
                            }
                            result.setTimezoneOffset(createLiteral((double)(hourOffset * offsetPolarity)));
                        }
                    }
                }

                result.setResultType(resolveTypeName("System", "Time"));
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
                result.setYear(createLiteral(year));
                if (matcher.group(3) != null) {
                    month = Integer.parseInt(matcher.group(3));
                    if (month < 0 || month > 12) {
                        throw new IllegalArgumentException(String.format("Invalid month in date/time literal (%s).", input));
                    }
                    result.setMonth(createLiteral(month));
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

                    result.setDay(createLiteral(day));
                }

                if (matcher.group(10) != null) {
                    hour = Integer.parseInt(matcher.group(10));
                    if (hour < 0 || hour > 24) {
                        throw new IllegalArgumentException(String.format("Invalid hour in date/time literal (%s).", input));
                    }
                    result.setHour(createLiteral(hour));
                }

                if (matcher.group(12) != null) {
                    minute = Integer.parseInt(matcher.group(12));
                    if (minute < 0 || minute >= 60 || (hour == 24 && minute > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid minute in date/time literal (%s).", input));
                    }
                    result.setMinute(createLiteral(minute));
                }

                if (matcher.group(14) != null) {
                    second = Integer.parseInt(matcher.group(14));
                    if (second < 0 || second >= 60 || (hour == 24 && second > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid second in date/time literal (%s).", input));
                    }
                    result.setSecond(createLiteral(second));
                }

                if (matcher.group(16) != null) {
                    millisecond = Integer.parseInt(matcher.group(16));
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw new IllegalArgumentException(String.format("Invalid millisecond in date/time literal (%s).", input));
                    }
                    result.setMillisecond(createLiteral(millisecond));
                }

                if ((matcher.group(7) != null && matcher.group(7).equals("Z"))
                        || ((matcher.group(18) != null) && matcher.group(18).equals("Z"))) {
                    result.setTimezoneOffset(createLiteral(0.0));
                }

                if (matcher.group(20) != null) {
                    int offsetPolarity = matcher.group(20).equals("+") ? 1 : 0;

                    if (matcher.group(23) != null) {
                        int hourOffset = Integer.parseInt(matcher.group(21));
                        if (hourOffset < 0 || hourOffset > 14) {
                            throw new IllegalArgumentException(String.format("Timezone hour offset is out of range in date/time literal (%s).", input));
                        }

                        int minuteOffset = Integer.parseInt(matcher.group(23));
                        if (minuteOffset < 0 || minuteOffset >= 60 || (hourOffset == 14 && minuteOffset > 0)) {
                            throw new IllegalArgumentException(String.format("Timezone minute offset is out of range in date/time literal (%s).", input));
                        }

                        result.setTimezoneOffset(createLiteral((double)(hourOffset + (minuteOffset / 60)) * offsetPolarity));
                    }
                    else {
                        if (matcher.group(21) != null) {
                            int hourOffset = Integer.parseInt(matcher.group(21));
                            if (hourOffset < 0 || hourOffset > 14) {
                                throw new IllegalArgumentException(String.format("Timezone hour offset is out of range in date/time literal (%s).", input));
                            }

                            result.setTimezoneOffset(createLiteral((double)(hourOffset * offsetPolarity)));
                        }
                    }
                }

                result.setResultType(resolveTypeName("System", "DateTime"));
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
        result.setResultType(resolveTypeName("System", "Any"));
        return result;
    }

    @Override
    public Expression visitNumberLiteral(@NotNull cqlParser.NumberLiteralContext ctx) {
        return createNumberLiteral(ctx.NUMBER().getText());
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
                result.setResultType(resolveTypeName("System", "Quantity"));
                return result;
            } catch (ParseException e) {
                throw new IllegalArgumentException(String.format("Could not parse quantity literal: %s", ctx.getText()), e);
            }
        } else {
            return createNumberLiteral(ctx.NUMBER().getText());
        }
    }

    @Override
    public Not visitNotExpression(@NotNull cqlParser.NotExpressionContext ctx) {
        Not result = of.createNot().withOperand(parseExpression(ctx.expression()));
        resolveUnaryCall("System", "Not", result);
        return result;
    }

    @Override
    public Exists visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        Exists result = of.createExists().withOperand(parseExpression(ctx.expression()));
        resolveUnaryCall("System", "Exists", result);
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

        resolveBinaryCall("System", operatorName, exp);

        return exp;
    }

    @Override
    public Power visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        Power power = of.createPower().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", "Power", power);

        return power;
    }

    @Override
    public Object visitPolarityExpressionTerm(@NotNull cqlParser.PolarityExpressionTermContext ctx) {
        if (ctx.getChild(0).getText().equals("+")) {
            return visit(ctx.expressionTerm());
        }

        Negate result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Negate", result);
        return result;
    }

    @Override
    public BinaryExpression visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        BinaryExpression exp = null;
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
            default:
                throw new IllegalArgumentException(String.format("Unsupported operator: %s.", ctx.getChild(1).getText()));
        }

        exp.withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", operatorName, exp);
        return exp;
    }

    @Override
    public Object visitPredecessorExpressionTerm(@NotNull cqlParser.PredecessorExpressionTermContext ctx) {
        Predecessor result = of.createPredecessor().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Predecessor", result);
        return result;
    }

    @Override
    public Object visitSuccessorExpressionTerm(@NotNull cqlParser.SuccessorExpressionTermContext ctx) {
        Successor result = of.createSuccessor().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Successor", result);
        return result;
    }

    @Override
    public Object visitElementExtractorExpressionTerm(@NotNull cqlParser.ElementExtractorExpressionTermContext ctx) {
        SingletonFrom result = of.createSingletonFrom().withOperand(parseExpression(ctx.expressionTerm()));

        if (!(result.getOperand().getResultType() instanceof ListType)) {
            throw new IllegalArgumentException("List type expected.");
        }

        result.setResultType(((ListType)result.getOperand().getResultType()).getElementType());

        resolveUnaryCall("System", "SingletonFrom", result);
        return result;
    }

    @Override
    public Object visitTypeExtentExpressionTerm(@NotNull cqlParser.TypeExtentExpressionTermContext ctx) {
        String extent = parseString(ctx.getChild(0));
        TypeSpecifier targetType = parseTypeSpecifier(ctx.namedTypeSpecifier());
        switch (extent) {
            case "minimum": {
                MinValue minimum = of.createMinValue();
                minimum.setValueType(dataTypeToQName(targetType.getResultType()));
                minimum.setResultType(targetType.getResultType());
                return minimum;
            }

            case "maximum": {
                MaxValue maximum = of.createMaxValue();
                maximum.setValueType(dataTypeToQName(targetType.getResultType()));
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

        resolveUnaryCall("System", operatorName, result);
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

        resolveUnaryCall("System", operatorName, result);
        return result;
    }

    @Override
    public Object visitDurationExpressionTerm(@NotNull cqlParser.DurationExpressionTermContext ctx) {
        // duration in days of X <=> days between start of X and end of X
        Expression operand = parseExpression(ctx.expressionTerm());

        Start start = of.createStart().withOperand(operand);
        resolveUnaryCall("System", "Start", start);

        End end = of.createEnd().withOperand(operand);
        resolveUnaryCall("System", "End", end);

        DurationBetween result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(start, end);

        resolveBinaryCall("System", "DurationBetween", result);
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
                    .withOperand(first, createInterval(second, true, third, true));

            resolveBinaryCall("System", isProper ? "ProperIncludedIn" : "IncludedIn", result);
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

            resolveBinaryCall("System", isProper ? "Greater" : "GreaterOrEqual", (BinaryExpression) result.getOperand().get(0));
            resolveBinaryCall("System", isProper ? "Less" : "LessOrEqual", (BinaryExpression) result.getOperand().get(1));
            resolveBinaryCall("System", "And", result);
            return result;
        }
    }

    @Override
    public Object visitDurationBetweenExpression(@NotNull cqlParser.DurationBetweenExpressionContext ctx) {
        BinaryExpression result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", "DurationBetween", result);
        return result;
    }

    @Override
    public Object visitDifferenceBetweenExpression(@NotNull cqlParser.DifferenceBetweenExpressionContext ctx) {
        BinaryExpression result = of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", "DifferenceBetween", result);
        return result;
    }

    @Override
    public Object visitWidthExpressionTerm(@NotNull cqlParser.WidthExpressionTermContext ctx) {
        UnaryExpression result = of.createWidth().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Width", result);
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

                    resolveBinaryCall("System", "In", in);
                    return in;
                } else {
                    Expression left = parseExpression(ctx.expression(0));
                    Expression right = parseExpression(ctx.expression(1));
                    if (right instanceof ValueSetRef) {
                        InValueSet in = of.createInValueSet()
                                .withCode(left)
                                .withValueset((ValueSetRef) right);
                        resolveCall("System", "InValueSet", new InValueSetInvocation(in));
                        return in;
                    }

                    if (right instanceof CodeSystemRef) {
                        InCodeSystem in = of.createInCodeSystem()
                                .withCode(left)
                                .withCodesystem((CodeSystemRef)right);
                        resolveCall("System", "InCodeSystem", new InCodeSystemInvocation(in));
                        return in;
                    }

                    In in = of.createIn().withOperand(left, right);
                    resolveBinaryCall("System", "In", in);
                    return in;
                }
            case "contains":
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    Contains contains = of.createContains()
                            .withPrecision(parseDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
                            .withOperand(
                                    parseExpression(ctx.expression(0)),
                                    parseExpression(ctx.expression(1))
                            );

                    resolveBinaryCall("System", "Contains", contains);
                    return contains;
                } else {
                    Expression left = parseExpression(ctx.expression(0));
                    Expression right = parseExpression(ctx.expression(1));
                    if (left instanceof ValueSetRef) {
                        InValueSet in = of.createInValueSet()
                                .withCode(right)
                                .withValueset((ValueSetRef) left);
                        resolveCall("System", "InValueSet", new InValueSetInvocation(in));
                        return in;
                    }

                    if (left instanceof CodeSystemRef) {
                        InCodeSystem in = of.createInCodeSystem()
                                .withCode(right)
                                .withCodesystem((CodeSystemRef)left);
                        resolveCall("System", "InCodeSystem", new InCodeSystemInvocation(in));
                        return in;
                    }

                    Contains contains = of.createContains().withOperand(left, right);
                    resolveBinaryCall("System", "Contains", contains);
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

        resolveBinaryCall("System", "And", and);
        return and;
    }

    @Override
    public Expression visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("xor")) {
            Xor xor = of.createXor().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
            resolveBinaryCall("System", "Xor", xor);
            return xor;
        } else {
            Or or = of.createOr().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
            resolveBinaryCall("System", "Or", or);
            return or;
        }
    }

    public Expression visitImpliesExpression(@NotNull cqlParser.ImpliesExpressionContext ctx) {
        Implies implies = of.createImplies().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        resolveBinaryCall("System", "Implies", implies);
        return implies;
    }

    @Override
    public Object visitInFixSetExpression(@NotNull cqlParser.InFixSetExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        switch (operator) {
            case "union":
                Union union = of.createUnion().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
                resolveBinaryCall("System", "Union", union);
                return union;
            case "intersect":
                Intersect intersect = of.createIntersect().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
                resolveBinaryCall("System", "Intersect", intersect);
                return intersect;
            case "except":
                Except except = of.createExcept().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
                resolveBinaryCall("System", "Except", except);
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

            resolveBinaryCall("System", "Equivalent", equivalent);
            if (!"~".equals(parseString(ctx.getChild(1)))) {
                Not not = of.createNot().withOperand(equivalent);
                resolveUnaryCall("System", "Not", not);
                return not;
            }

            return equivalent;
        }
        else {
            BinaryExpression equal = of.createEqual().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));

            resolveBinaryCall("System", "Equal", equal);
            if (!"=".equals(parseString(ctx.getChild(1)))) {
                Not not = of.createNot().withOperand(equal);
                resolveUnaryCall("System", "Not", not);
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

        resolveBinaryCall("System", operatorName, exp);
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

    public Expression resolveAccessor(Expression left, String memberIdentifier) {
        // if left is a LibraryRef
        // if right is an identifier
        // right may be an ExpressionRef, a CodeSystemRef, a ValueSetRef, a CodeRef, a ConceptRef, or a ParameterRef -- need to resolve on the referenced library
        // if left is an ExpressionRef
        // if right is an identifier
        // return a Property with the ExpressionRef as source and identifier as Path
        // if left is a Property
        // if right is an identifier
        // modify the Property to append the identifier to the path
        // if left is an AliasRef
        // return a Property with a Path and no source, and Scope set to the Alias
        // if left is an Identifier
        // return a new Identifier with left as a qualifier
        // else
        // throws an error as an unresolved identifier

        if (left instanceof LibraryRef) {
            String libraryName = ((LibraryRef)left).getLibraryName();
            TranslatedLibrary referencedLibrary = resolveLibrary(libraryName);

            Element element = referencedLibrary.resolve(memberIdentifier);

            if (element instanceof ExpressionDef) {
                checkAccessLevel(libraryName, memberIdentifier, ((ExpressionDef)element).getAccessLevel());
                Expression result = of.createExpressionRef()
                        .withLibraryName(libraryName)
                        .withName(memberIdentifier);
                result.setResultType(getExpressionDefResultType((ExpressionDef)element));
                return result;
            }

            if (element instanceof ParameterDef) {
                checkAccessLevel(libraryName, memberIdentifier, ((ParameterDef)element).getAccessLevel());
                Expression result = of.createParameterRef()
                        .withLibraryName(libraryName)
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            if (element instanceof ValueSetDef) {
                checkAccessLevel(libraryName, memberIdentifier, ((ValueSetDef)element).getAccessLevel());
                ValueSetRef result = of.createValueSetRef()
                        .withLibraryName(libraryName)
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            if (element instanceof CodeSystemDef) {
                checkAccessLevel(libraryName, memberIdentifier, ((CodeSystemDef)element).getAccessLevel());
                CodeSystemRef result = of.createCodeSystemRef()
                        .withLibraryName(libraryName)
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            if (element instanceof CodeDef) {
                checkAccessLevel(libraryName, memberIdentifier, ((CodeDef)element).getAccessLevel());
                CodeRef result = of.createCodeRef()
                        .withLibraryName(libraryName)
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            if (element instanceof ConceptDef) {
                checkAccessLevel(libraryName, memberIdentifier, ((ConceptDef)element).getAccessLevel());
                ConceptRef result = of.createConceptRef()
                        .withLibraryName(libraryName)
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            throw new IllegalArgumentException(String.format("Could not resolve identifier %s in library %s.",
                    memberIdentifier, referencedLibrary.getIdentifier().getId()));
        }
        else if (left instanceof AliasRef) {
            Property result = of.createProperty()
                    .withScope(((AliasRef) left).getName())
                    .withPath(memberIdentifier);
            result.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
            return result;
        }
        else {
            Property result = of.createProperty()
                    .withSource(left)
                    .withPath(memberIdentifier);
            result.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
            return result;
        }
    }

    public Expression resolveIdentifier(String identifier) {
        // An Identifier will always be:
        // 1: The name of an alias
        // 2: The name of a query define clause
        // 3: The name of an expression
        // 4: The name of a parameter
        // 5: The name of a valueset
        // 6: The name of a codesystem
        // 7: The name of a code
        // 8: The name of a concept
        // 9: The name of a library
        // 10: An unresolved identifier error is thrown

        // In the sort clause of a plural query, names may be resolved based on the result type of the query
        IdentifierRef resultElement = resolveQueryResultElement(identifier);
        if (resultElement != null) {
            return resultElement;
        }

        AliasedQuerySource alias = resolveAlias(identifier);
        if (alias != null) {
            AliasRef result = of.createAliasRef().withName(identifier);
            if (alias.getResultType() instanceof ListType) {
                result.setResultType(((ListType)alias.getResultType()).getElementType());
            }
            else {
                result.setResultType(alias.getResultType());
            }
            return result;
        }

        LetClause let = resolveQueryLet(identifier);
        if (let != null) {
            QueryLetRef result = of.createQueryLetRef().withName(identifier);
            result.setResultType(let.getResultType());
            return result;
        }

        OperandRef operandRef = resolveOperandRef(identifier);
        if (operandRef != null) {
            return operandRef;
        }

        Element element = translatedLibrary.resolve(identifier);
        if (element == null) {
            ExpressionDefinitionInfo expressionInfo = libraryInfo.resolveExpressionReference(identifier);

            if (expressionInfo != null) {
                String saveContext = currentContext;
                currentContext = expressionInfo.getContext();
                try {
                    ExpressionDef expressionDef = internalVisitExpressionDefinition(expressionInfo.getDefinition());
                    element = expressionDef;
                } finally {
                    currentContext = saveContext;
                }
            }

            ParameterDefinitionInfo parameterInfo = libraryInfo.resolveParameterReference(identifier);
            if (parameterInfo != null) {
                ParameterDef parameterDef = visitParameterDefinition(parameterInfo.getDefinition());
                element = parameterDef;
            }
        }

        if (element instanceof ExpressionDef) {
            ExpressionRef expressionRef = of.createExpressionRef().withName(((ExpressionDef) element).getName());
            expressionRef.setResultType(getExpressionDefResultType((ExpressionDef)element));
            if (expressionRef.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not validate reference to expression %s because its definition contains errors.",
                        expressionRef.getName()));
            }
            return expressionRef;
        }

        if (element instanceof ParameterDef) {
            ParameterRef parameterRef = of.createParameterRef().withName(((ParameterDef) element).getName());
            parameterRef.setResultType(element.getResultType());
            if (parameterRef.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not validate reference to parameter %s because its definition contains errors.",
                        parameterRef.getName()));
            }
            return parameterRef;
        }

        if (element instanceof ValueSetDef) {
            ValueSetRef valuesetRef = of.createValueSetRef().withName(((ValueSetDef) element).getName());
            valuesetRef.setResultType(element.getResultType());
            if (valuesetRef.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not validate reference to valueset %s because its definition contains errors.",
                        valuesetRef.getName()));
            }
            return valuesetRef;
        }

        if (element instanceof CodeSystemDef) {
            CodeSystemRef codesystemRef = of.createCodeSystemRef().withName(((CodeSystemDef) element).getName());
            codesystemRef.setResultType(element.getResultType());
            if (codesystemRef.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not validate reference to codesystem %s because its definition contains errors.",
                        codesystemRef.getName()));
            }
            return codesystemRef;

        }

        if (element instanceof CodeDef) {
            CodeRef codeRef = of.createCodeRef().withName(((CodeDef)element).getName());
            codeRef.setResultType(element.getResultType());
            if (codeRef.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not validate reference to code %s because its definition contains errors.",
                        codeRef.getName()));
            }
            return codeRef;
        }

        if (element instanceof ConceptDef) {
            ConceptRef conceptRef = of.createConceptRef().withName(((ConceptDef)element).getName());
            conceptRef.setResultType(element.getResultType());
            if (conceptRef.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not validate reference to concept %s because its definition contains error.",
                        conceptRef.getName()));
            }
            return conceptRef;
        }

        if (element instanceof IncludeDef) {
            LibraryRef libraryRef = new LibraryRef();
            libraryRef.setLibraryName(((IncludeDef) element).getLocalIdentifier());
            return libraryRef;
        }

        throw new IllegalArgumentException(String.format("Could not resolve identifier %s in the current library.", identifier));
    }

    public Expression resolveQualifiedIdentifier(List<String> identifiers) {
        Expression current = null;
        for (String identifier : identifiers) {
            if (current == null) {
                current = resolveIdentifier(identifier);
            } else {
                current = resolveAccessor(current, identifier);
            }
        }

        return current;
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
        Conversion conversion = conversionMap.findConversion(operand.getResultType(), targetType.getResultType(), false);
        if (conversion == null) {
            throw new IllegalArgumentException(String.format("Could not resolve conversion from type %s to type %s.",
                    operand.getResultType(), targetType.getResultType()));
        }

        return convertExpression(operand, conversion);
    }

    @Override
    public Object visitTypeExpression(@NotNull cqlParser.TypeExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("is")) {
            Is is = of.createIs()
                    .withOperand(parseExpression(ctx.expression()))
                    .withIsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));
            is.setResultType(resolveTypeName("System", "Boolean"));
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
                resolveUnaryCall("System", "IsNull", exp);
                break;

            case "true" :
                exp = of.createIsTrue().withOperand(left);
                resolveUnaryCall("System", "IsTrue", exp);
                break;

            case "false" :
                exp = of.createIsFalse().withOperand(left);
                resolveUnaryCall("System", "IsFalse", exp);
                break;

            default:
                throw new IllegalArgumentException(String.format("Unknown boolean test predicate %s.", lastChild));
        }

        if ("not".equals(nextToLast)) {
            exp = of.createNot().withOperand(exp);
            resolveUnaryCall("System", "Not", exp);
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
            resolveUnaryCall("System", "Start", start);
            timingOperator.setLeft(start);
        }

        if ("ends".equals(firstChild.getText())) {
            End end = of.createEnd().withOperand(timingOperator.getLeft());
            resolveUnaryCall("System", "End", end);
            timingOperator.setLeft(end);
        }

        ParseTree lastChild = ctx.getChild(ctx.getChildCount() - 1);
        if ("start".equals(lastChild.getText())) {
            Start start = of.createStart().withOperand(timingOperator.getRight());
            resolveUnaryCall("System", "Start", start);
            timingOperator.setRight(start);
        }

        if ("end".equals(lastChild.getText())) {
            End end = of.createEnd().withOperand(timingOperator.getRight());
            resolveUnaryCall("System", "End", end);
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
        resolveBinaryCall("System", operatorName, operator);

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
                resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                isRightPoint = true;
                continue;
            }

            if ("end".equals(pt.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "End", end);
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
                    resolveBinaryCall("System", "ProperContains", properContains);
                    return properContains;
                }

                ProperContains properContains = of.createProperContains()
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "ProperContains", properContains);
                return properContains;
            }
            if (dateTimePrecision != null) {
                Contains contains = of.createContains().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "Contains", contains);
                return contains;
            }

            Contains contains = of.createContains().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "Contains", contains);
            return contains;
        }

        if (isProper) {

            if (dateTimePrecision != null) {
                ProperIncludes properIncludes = of.createProperIncludes().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "ProperIncludes", properIncludes);
                return properIncludes;
            }

            ProperIncludes properIncludes = of.createProperIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "ProperIncludes", properIncludes);
            return properIncludes;
        }

        if (dateTimePrecision != null) {
            Includes includes = of.createIncludes().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    .withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "Includes", includes);
            return includes;
        }

        Includes includes = of.createIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        resolveBinaryCall("System", "Includes", includes);
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
                resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                isLeftPoint = true;
                continue;
            }

            if ("ends".equals(pt.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "End", end);
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
                    resolveBinaryCall("System", "ProperIn", properIn);
                    return properIn;
                }

                ProperIn properIn = of.createProperIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "ProperIn", properIn);
                return properIn;
            }
            if (dateTimePrecision != null) {
                In in = of.createIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "In", in);
                return in;
            }

            In in = of.createIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "In", in);
            return in;
        }

        if (isProper) {
            if (dateTimePrecision != null) {
                ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
                return properIncludedIn;
            }

            ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
            return properIncludedIn;
        }

        if (dateTimePrecision != null) {
            IncludedIn includedIn = of.createIncludedIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    .withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "IncludedIn", includedIn);
            return includedIn;
        }

        IncludedIn includedIn = of.createIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        resolveBinaryCall("System", "IncludedIn", includedIn);
        return includedIn;
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(@NotNull cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? quantityOffset? ('before' | 'after') dateTimePrecisionSpecifier? ('start' | 'end')?

        // duration before/after
        // A starts 3 days before start B
        // days between start of A and start of B = 3
        // A starts 3 days after start B
        // days between start of A and start of B = -3

        // or more/less duration before/after
        // A starts 3 days or more before start B
        // days between start of A and start of B >= 3
        // A starts 3 days or more after start B
        // days between start of A and start of B <= -3
        // A starts 3 days or less before start B
        // days between start of A and start of B in (0, 3]
        // A starts 3 days or less after start B
        // days between start of A and start of B in [-3, 0)

        TimingOperatorContext timingOperator = timingOperators.peek();
        Boolean isBefore = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                continue;
            }

            if ("ends".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                continue;
            }

            if ("start".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                continue;
            }

            if ("end".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                continue;
            }

            if ("before".equals(child.getText())) {
                isBefore = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.quantityOffset() == null) {
            if (isBefore) {
                if (dateTimePrecision != null) {
                    Before before = of.createBefore()
                            .withPrecision(parseDateTimePrecision(dateTimePrecision))
                            .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    resolveBinaryCall("System", "Before", before);
                    return before;
                }

                Before before = of.createBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "Before", before);
                return before;
            } else {
                if (dateTimePrecision != null) {
                    After after = of.createAfter()
                            .withPrecision(parseDateTimePrecision(dateTimePrecision))
                            .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    resolveBinaryCall("System", "After", after);
                    return after;
                }

                After after = of.createAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "After", after);
                return after;
            }
        } else {
            Quantity quantity = (Quantity)visit(ctx.quantityOffset().quantity());
            Literal quantityLiteral = createLiteral(quantity.getValue().intValueExact());
            Expression lowerBound = null;
            Expression upperBound = null;

            if (timingOperator.getLeft().getResultType() instanceof IntervalType) {
                if (isBefore) {
                    End end = of.createEnd().withOperand(timingOperator.getLeft());
                    resolveUnaryCall("System", "End", end);
                    lowerBound = end;
                }
                else {
                    Start start = of.createStart().withOperand(timingOperator.getLeft());
                    resolveUnaryCall("System", "Start", start);
                    lowerBound = start;
                }
            }
            else {
                lowerBound = timingOperator.getLeft();
            }

            if (timingOperator.getRight().getResultType() instanceof IntervalType) {
                if (isBefore) {
                    Start start = of.createStart().withOperand(timingOperator.getRight());
                    resolveUnaryCall("System", "Start", start);
                    upperBound = start;
                }
                else {
                    End end = of.createEnd().withOperand(timingOperator.getRight());
                    resolveUnaryCall("System", "End", end);
                    upperBound = end;
                }
            }
            else {
                upperBound = timingOperator.getRight();
            }

            BinaryExpression betweenOperator = resolveBetweenOperator(quantity.getUnit(), lowerBound, upperBound);
            if (betweenOperator != null) {
                if (ctx.quantityOffset().offsetRelativeQualifier() == null) {
                    if (isBefore) {
                        Equal equal = of.createEqual().withOperand(betweenOperator, quantityLiteral);
                        resolveBinaryCall("System", "Equal", equal);
                        return equal;
                    } else {
                        Negate negate = of.createNegate().withOperand(quantityLiteral);
                        resolveUnaryCall("System", "Negate", negate);
                        Equal equal = of.createEqual().withOperand(betweenOperator, negate);
                        resolveBinaryCall("System", "Equal", equal);
                        return equal;
                    }
                } else {
                    switch (ctx.quantityOffset().offsetRelativeQualifier().getText()) {
                        case "or more":
                            if (isBefore) {
                                GreaterOrEqual greaterOrEqual = of.createGreaterOrEqual().withOperand(
                                        betweenOperator,
                                        quantityLiteral
                                );
                                resolveBinaryCall("System", "GreaterOrEqual", greaterOrEqual);
                                return greaterOrEqual;
                            } else {
                                Negate negate = of.createNegate().withOperand(quantityLiteral);
                                resolveUnaryCall("System", "Negate", negate);
                                LessOrEqual lessOrEqual = of.createLessOrEqual().withOperand(betweenOperator, negate);
                                resolveBinaryCall("System", "LessOrEqual", lessOrEqual);
                                return lessOrEqual;
                            }
                        case "or less":
                            if (isBefore) {
                                Interval quantityInterval = of.createInterval()
                                        .withLow(createLiteral(0)).withLowClosed(false)
                                        .withHigh(quantityLiteral).withHighClosed(true);
                                quantityInterval.setResultType(new IntervalType(quantityInterval.getLow().getResultType()));
                                In in = of.createIn().withOperand(betweenOperator, quantityInterval);
                                resolveBinaryCall("System", "In", in);
                                return in;
                            } else {
                                Negate negate = of.createNegate().withOperand(quantityLiteral);
                                resolveUnaryCall("System", "Negate", negate);
                                Interval quantityInterval = of.createInterval()
                                        .withLow(negate).withLowClosed(true)
                                        .withHigh(createLiteral(0)).withHighClosed(false);
                                quantityInterval.setResultType(new IntervalType(quantityInterval.getLow().getResultType()));
                                In in = of.createIn().withOperand(betweenOperator, quantityInterval);
                                resolveBinaryCall("System", "In", in);
                                return in;
                            }
                    }
                }
            }
        }

        throw new IllegalArgumentException("Unable to resolve interval operator phrase.");
    }

    private BinaryExpression resolveBetweenOperator(String unit, Expression left, Expression right) {
        if (unit != null) {
            DurationBetween between = of.createDurationBetween().withPrecision(parseDateTimePrecision(unit)).withOperand(left, right);
            resolveBinaryCall("System", "DurationBetween", between);
            return between;
        }

        return null;
    }

    @Override
    public Object visitWithinIntervalOperatorPhrase(@NotNull cqlParser.WithinIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?
        // A starts within 3 days of start B
        // days between start of A and start of B >= -3 and days between start of A and start of B <= 3
        // A starts within 3 days of B
        // days between start of A and start of B >= -3 and days between start of A and end of B <= 3

        TimingOperatorContext timingOperator = timingOperators.peek();
        boolean isProper = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                continue;
            }

            if ("ends".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                continue;
            }

            if ("start".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                continue;
            }

            if ("end".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                continue;
            }

            if ("properly".equals(child.getText())) {
                isProper = true;
                continue;
            }
        }

        Quantity quantity = (Quantity)visit(ctx.quantity());
        Literal quantityLiteral = createLiteral(quantity.getValue().intValueExact());
        Negate negativeQuantityLiteral = of.createNegate().withOperand(createLiteral(quantity.getValue().intValueExact()));
        resolveUnaryCall("System", "Negate", negativeQuantityLiteral);

        Expression lowerBound = null;
        Expression upperBound = null;
        if (timingOperator.getRight().getResultType() instanceof IntervalType) {
            lowerBound = of.createStart().withOperand(timingOperator.getRight());
            resolveUnaryCall("System", "Start", (Start)lowerBound);
            upperBound = of.createEnd().withOperand(timingOperator.getRight());
            resolveUnaryCall("System", "End", (End)upperBound);
        }
        else {
            lowerBound = timingOperator.getRight();
            upperBound = timingOperator.getRight();
        }

        BinaryExpression leftBetween = resolveBetweenOperator(quantity.getUnit(), timingOperator.getLeft(), lowerBound);
        BinaryExpression rightBetween = resolveBetweenOperator(quantity.getUnit(), timingOperator.getLeft(), upperBound);

        BinaryExpression leftCompare = (isProper ? of.createGreater() : of.createGreaterOrEqual())
                .withOperand(leftBetween, negativeQuantityLiteral);
        resolveBinaryCall("System", isProper ? "Greater" : "GreaterOrEqual", leftCompare);

        BinaryExpression rightCompare = (isProper ? of.createLess() : of.createLessOrEqual())
                .withOperand(rightBetween, quantityLiteral);
        resolveBinaryCall("System", isProper ? "Less" : "LessOrEqual", rightCompare);

        And result = of.createAnd().withOperand(leftCompare, rightCompare);
        resolveBinaryCall("System", "And", result);
        return result;
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
        resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(@NotNull cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + dateTimePrecision == null ? 0 : 1)) {
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
        resolveBinaryCall("System", operatorName, operator);
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

        resolveBinaryCall("System", "Starts", starts);
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

        resolveBinaryCall("System", "Ends", ends);
        return ends;
    }

    @Override
    public Object visitIfThenElseExpressionTerm(@NotNull cqlParser.IfThenElseExpressionTermContext ctx) {
        If ifObject = of.createIf()
                .withCondition(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)))
                .withElse(parseExpression(ctx.expression(2)));

        DataTypes.verifyType(ifObject.getCondition().getResultType(), resolveTypeName("System", "Boolean"));
        DataType resultType = ensureCompatibleTypes(ifObject.getThen().getResultType(), ifObject.getElse().getResultType());
        ifObject.setResultType(resultType);
        ifObject.setThen(ensureCompatible(ifObject.getThen(), resultType));
        ifObject.setElse(ensureCompatible(ifObject.getElse(), resultType));
        return ifObject;
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
                    resultType = ensureCompatibleTypes(resultType, result.getElse().getResultType());
                } else {
                    result.setComparand(parseExpression(pt));
                }
            }

            if (pt instanceof cqlParser.CaseExpressionItemContext) {
                CaseItem caseItem = (CaseItem)visit(pt);
                if (result.getComparand() != null) {
                    verifyType(caseItem.getWhen().getResultType(), result.getComparand().getResultType());
                }
                else {
                    DataTypes.verifyType(caseItem.getWhen().getResultType(), resolveTypeName("System", "Boolean"));
                }

                if (resultType == null) {
                    resultType = caseItem.getThen().getResultType();
                }
                else {
                    resultType = ensureCompatibleTypes(resultType, caseItem.getThen().getResultType());
                }

                result.getCaseItem().add(caseItem);
            }
        }

        for (CaseItem caseItem : result.getCaseItem()) {
            if (result.getComparand() != null) {
                caseItem.setWhen(ensureCompatible(caseItem.getWhen(), result.getComparand().getResultType()));
            }

            caseItem.setThen(ensureCompatible(caseItem.getThen(), resultType));
        }

        result.setElse(ensureCompatible(result.getElse(), resultType));
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
                resolveUnaryCall("System", "Distinct", distinct);
                return distinct;
            case "collapse":
                Collapse collapse = of.createCollapse().withOperand(parseExpression(ctx.expression()));
                resolveUnaryCall("System", "Collapse", collapse);
                return collapse;
            case "flatten":
                Flatten flatten = of.createFlatten().withOperand(parseExpression(ctx.expression()));
                resolveUnaryCall("System", "Flatten", flatten);
                return flatten;
        }

        throw new IllegalArgumentException(String.format("Unknown aggregate operator %s.", ctx.getChild(0).getText()));
    }

    @Override
    public Retrieve visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        String model = parseString(ctx.namedTypeSpecifier().modelIdentifier());
        String label = parseString(ctx.namedTypeSpecifier().identifier());
        DataType dataType = resolveTypeName(model, label);
        if (dataType == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type name %s.", label));
        }

        if (!(dataType instanceof ClassType) || !((ClassType)dataType).isRetrievable()) {
            throw new IllegalArgumentException(String.format("Specified data type %s does not support retrieval.", label));
        }

        ClassType classType = (ClassType)dataType;
        ProfileType profileType = dataType instanceof ProfileType ? (ProfileType)dataType : null;
        NamedType namedType = profileType == null ? classType : (NamedType)classType.getBaseType();

        Retrieve retrieve = of.createRetrieve()
                .withDataType(dataTypeToQName((DataType)namedType))
                .withTemplateId(classType.getIdentifier());

        if (ctx.terminology() != null) {
            if (ctx.codePath() != null) {
                retrieve.setCodeProperty(parseString(ctx.codePath()));
            } else if (classType.getPrimaryCodePath() != null) {
                retrieve.setCodeProperty(classType.getPrimaryCodePath());
            }

            List<String> identifiers = (List<String>) visit(ctx.terminology());
            retrieve.setCodes(resolveQualifiedIdentifier(identifiers));
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

    private void verifyComparable(DataType dataType) {
        Expression left = (Expression)of.createLiteral().withResultType(dataType);
        Expression right = (Expression)of.createLiteral().withResultType(dataType);
        BinaryExpression comparison = of.createLess().withOperand(left, right);
        resolveBinaryCall("System", "Less", comparison);
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
        QueryContext queryContext = new QueryContext();
        queries.push(queryContext);
        try {

            List<AliasedQuerySource> sources;
            queryContext.enterSourceClause();
            try {
                sources = (List<AliasedQuerySource>)visit(ctx.sourceClause());
            }
            finally {
                queryContext.exitSourceClause();
            }

            queryContext.addQuerySources(sources);

            // If we are evaluating a population-level query whose source ranges over any patient-context expressions,
            // then references to patient context expressions within the iteration clauses of the query can be accessed
            // at the patient, rather than the population, context.
            boolean expressionContextPushed = false;
            if (inPopulationContext() && queryContext.referencesPatientContext()) {
                pushExpressionContext("Patient");
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
                        element.getValue().setResultType(aqs.getResultType()); // Doesn't use the fluent API to avoid casting
                        element.setResultType(element.getValue().getResultType());
                        returnType.addElement(new TupleTypeElement(element.getName(), element.getResultType()));
                        returnExpression.getElement().add(element);
                    }

                    returnExpression.setResultType(queryContext.isSingular() ? returnType : new ListType(returnType));
                    ret.setExpression(returnExpression);
                    ret.setResultType(returnExpression.getResultType());
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
                                verifyComparable(queryContext.getResultElementType());
                            }
                            else {
                                verifyComparable(sortByItem.getResultType());
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
                    popExpressionContext();
                }
            }

        } finally {
            queries.pop();
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
    private Expression optimizeDateRangeInQuery(Expression where, AliasedQuerySource aqs) {
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
                and.getOperand().set(i, createLiteral(true));
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
            rhs.getResultType().isSubTypeOf(getSystemModel().getDateTime())
                || rhs.getResultType().isSubTypeOf(new IntervalType(getSystemModel().getDateTime()));

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
        return e.getResultType().equals(resolveTypeName("System", "DateTime"));
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
        queries.peek().addLetClause(letClause);
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
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            DataTypes.verifyType(expression.getResultType(), resolveTypeName("System", "Boolean"));
            RelationshipClause result = of.createWith();
            result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
            result.setResultType(aqs.getResultType());
            return result;
        } finally {
            queries.peek().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWithoutClause(@NotNull cqlParser.WithoutClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            DataTypes.verifyType(expression.getResultType(), resolveTypeName("System", "Boolean"));
            RelationshipClause result = of.createWithout();
            result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
            result.setResultType(aqs.getResultType());
            return result;
        } finally {
            queries.peek().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWhereClause(@NotNull cqlParser.WhereClauseContext ctx) {
        Expression result = (Expression)visit(ctx.expression());
        DataTypes.verifyType(result.getResultType(), resolveTypeName("System", "Boolean"));
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
        returnClause.setResultType(queries.peek().isSingular()
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
        resolveBinaryCall("System", "Indexer", indexer);
        return indexer;
    }

    @Override
    public Expression visitInvocationExpressionTerm(@NotNull cqlParser.InvocationExpressionTermContext ctx) {
        Expression left = parseExpression(ctx.expressionTerm());
        targets.push(left);
        try {
            return (Expression)visit(ctx.invocation());
        }
        finally {
            targets.pop();
        }
    }

    @Override
    public Expression visitExternalConstant(@NotNull cqlParser.ExternalConstantContext ctx) {
        return (Expression)new IdentifierRef().withName(ctx.getText()).withResultType(getSystemModel().getVoid());
    }

    @Override
    public Expression visitThisInvocation(@NotNull cqlParser.ThisInvocationContext ctx) {
        return (Expression)new IdentifierRef().withName(ctx.getText()).withResultType(getSystemModel().getVoid());
    }

    @Override
    public Expression visitMemberInvocation(@NotNull cqlParser.MemberInvocationContext ctx) {
        String identifier = parseString(ctx.identifier());
        if (!targets.empty()) {
            Expression target = targets.pop();
            try {
                return resolveAccessor(target, identifier);
            }
            finally {
                targets.push(target);
            }
        }
        return resolveIdentifier(identifier);
    }

    private Expression resolveFunction(String libraryName, @NotNull cqlParser.FunctionContext ctx) {
        FunctionRef fun = of.createFunctionRef()
                .withLibraryName(libraryName)
                .withName(parseString(ctx.identifier()));

        if (ctx.paramList() != null && ctx.paramList().expression() != null) {
            for (cqlParser.ExpressionContext expressionContext : ctx.paramList().expression()) {
                fun.getOperand().add((Expression) visit(expressionContext));
            }
        }

        Expression systemFunction = systemFunctionResolver.resolveSystemFunction(fun);
        if (systemFunction != null) {
            return systemFunction;
        }

        resolveFunction(fun.getLibraryName(), fun.getName(), new FunctionRefInvocation(fun));

        return fun;
    }

    @Override
    public Expression visitFunction(@NotNull cqlParser.FunctionContext ctx) {
        if (!targets.empty()) {
            Expression target = targets.pop();
            try {
                // If the target is a library reference, resolve as a standard qualified call
                if (target instanceof LibraryRef) {
                    return resolveFunction(((LibraryRef)target).getLibraryName(), ctx);
                }

                // If the target is an expression, resolve as a method invocation
                if (target instanceof Expression) {
                    // Need a way to support rewriting method invocations....
                    throw new IllegalArgumentException("Method rewrite not yet supported.");
                }

                throw new IllegalArgumentException(String.format("Invalid invocation target: %s", target.getClass().getName()));
            }
            finally {
                targets.push(target);
            }
        }

        // If there is no target, resolve as a system function
        return resolveFunction(null, ctx);
    }

    @Override
    public Object visitFunctionBody(@NotNull cqlParser.FunctionBodyContext ctx) {
        return visit(ctx.expression());
    }

    private Object internalVisitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
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

        if (!translatedLibrary.contains(fun)) {
            currentFunctionDef = fun;
            pushExpressionContext(currentContext);
            try {
                fun.setExpression(parseExpression(ctx.functionBody()));
            } finally {
                currentFunctionDef = null;
                popExpressionContext();
            }

            fun.setContext(currentContext);
            fun.setResultType(fun.getExpression().getResultType());
            addToLibrary(fun);
        }

        return fun;
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDef result = (FunctionDef)internalVisitFunctionDefinition(ctx);
        Operator operator = Operator.fromFunctionDef(result);
        Set<Signature> definedSignatures = definedFunctionDefinitions.get(operator.getName());
        if (definedSignatures == null) {
            definedSignatures = new HashSet<>();
            definedFunctionDefinitions.put(operator.getName(), definedSignatures);
        }

        if (definedSignatures.contains(operator.getSignature())) {
            throw new IllegalArgumentException(String.format("A function named %s with the same type of arguments is already defined in this library.", operator.getName()));
        }

        definedSignatures.add(operator.getSignature());

        return result;
    }

    private UsingDef buildUsingDef(VersionedIdentifier modelIdentifier, Model model) {
        UsingDef usingDef = of.createUsingDef()
                .withLocalIdentifier(modelIdentifier.getId())
                .withVersion(modelIdentifier.getVersion())
                .withUri(model.getModelInfo().getUrl());
        // TODO: Needs to write xmlns and schemalocation to the resulting ELM XML document...

        addToLibrary(usingDef);
        return usingDef;
    }

    private Model buildModel(VersionedIdentifier identifier) {
        Model model = null;
        try {
            ModelInfoProvider provider = ModelInfoLoader.getModelInfoProvider(identifier);
            if (identifier.getId().equals("System")) {
                model = new SystemModel(provider.load());
            }
            else {
                model = new Model(provider.load(), getModel("System"));
                loadConversionMap(model);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s.",
                    identifier.getId(), identifier.getVersion()));
        }

        return model;
    }

    private boolean hasUsings() {
        for (Model model : models.values()) {
            if (!model.getModelInfo().getName().equals("System")) {
                return true;
            }
        }

        return false;
    }

    private String getDefaultModelName() {
        String modelName = null;
        for (Model model : models.values()) {
            if (!model.getModelInfo().getName().equals("System")) {
                if (modelName != null) {
                    throw new IllegalArgumentException(String.format("Could not resolve a default model between %s and %s.",
                            modelName, model.getModelInfo().getName()));
                }

                modelName = model.getModelInfo().getName();
            }
        }

        if (modelName == null) {
            throw new IllegalArgumentException("Could not determine a default model because no usings have been defined.");
        }

        return modelName;
    }

    protected Model getModel() {
        return getModel((String)null);
    }

    private Model getModel(String modelName) {
        return getModel(modelName, null);
    }

    private Model getModel(String modelName, String version) {
        if (modelName == null) {
            modelName = getDefaultModelName();
        }

        VersionedIdentifier modelIdentifier = new VersionedIdentifier().withId(modelName).withVersion(version);
        return getModel(modelIdentifier);
    }

    private Model getModel(VersionedIdentifier modelIdentifier) {
        Model model = models.get(modelIdentifier.getId());
        if (model == null) {
            model = buildModel(modelIdentifier);
            models.put(modelIdentifier.getId(), model);
            // Add the model using def to the output
            buildUsingDef(modelIdentifier, model);
        }

        if (modelIdentifier.getVersion() != null && !modelIdentifier.getVersion().equals(model.getModelInfo().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s because version %s is already loaded.",
                    modelIdentifier.getId(), modelIdentifier.getVersion(), model.getModelInfo().getVersion()));
        }

        return model;
    }

    private AccessModifier parseAccessModifier(ParseTree pt) {
        return pt == null ? AccessModifier.PUBLIC : (AccessModifier)visit(pt);
    }

    private String parseString(ParseTree pt) {
        return StringEscapeUtils.unescapeCql(pt == null ? null : (String) visit(pt));
    }

    private Expression parseExpression(ParseTree pt) {
        return pt == null ? null : (Expression) visit(pt);
    }

    private TypeSpecifier parseTypeSpecifier(ParseTree pt) {
        return pt == null ? null : (TypeSpecifier) visit(pt);
    }

    protected QName dataTypeToQName(DataType type) {
        if (type instanceof NamedType) {
            NamedType namedType = (NamedType)type;
            org.hl7.elm_modelinfo.r1.ModelInfo modelInfo = getModel(namedType.getNamespace()).getModelInfo();
            return new QName(modelInfo.getUrl(), namedType.getSimpleName());
        }

        throw new IllegalArgumentException("A named type is required in this context.");
    }

    private TypeSpecifier dataTypeToTypeSpecifier(DataType type) {
        // Convert the given type into an ELM TypeSpecifier representation.
        if (type instanceof NamedType) {
            return (TypeSpecifier)of.createNamedTypeSpecifier().withName(dataTypeToQName(type)).withResultType(type);
        }
        else if (type instanceof ListType) {
            return listTypeToTypeSpecifier((ListType)type);
        }
        else if (type instanceof IntervalType) {
            return intervalTypeToTypeSpecifier((IntervalType)type);
        }
        else if (type instanceof TupleType) {
            return tupleTypeToTypeSpecifier((TupleType)type);
        }
        else {
            throw new IllegalArgumentException(String.format("Could not convert type %s to a type specifier.", type));
        }
    }

    private TypeSpecifier listTypeToTypeSpecifier(ListType type) {
        return (TypeSpecifier)of.createListTypeSpecifier()
                .withElementType(dataTypeToTypeSpecifier(type.getElementType()))
                .withResultType(type);
    }

    private TypeSpecifier intervalTypeToTypeSpecifier(IntervalType type) {
        return (TypeSpecifier)of.createIntervalTypeSpecifier()
                .withPointType(dataTypeToTypeSpecifier(type.getPointType()))
                .withResultType(type);
    }

    private TypeSpecifier tupleTypeToTypeSpecifier(TupleType type) {
        return (TypeSpecifier)of.createTupleTypeSpecifier()
                .withElement(tupleTypeElementsToTupleElementDefinitions(type.getElements()))
                .withResultType(type);
    }

    private TupleElementDefinition[] tupleTypeElementsToTupleElementDefinitions(Iterable<TupleTypeElement> elements) {
        List<TupleElementDefinition> definitions = new ArrayList<>();

        for (TupleTypeElement element : elements) {
            definitions.add(of.createTupleElementDefinition()
                    .withName(element.getName())
                    .withType(dataTypeToTypeSpecifier(element.getType())));
        }

        return definitions.toArray(new TupleElementDefinition[definitions.size()]);
    }

    private ClassType resolveLabel(String modelName, String label) {
        ClassType result = null;
        if (modelName == null || modelName.equals("")) {
            for (Model model : models.values()) {
                ClassType modelResult = model.resolveLabel(label);
                if (modelResult != null) {
                    if (result != null) {
                        throw new IllegalArgumentException(String.format("Label %s is ambiguous between %s and %s.",
                                label, result.getLabel(), modelResult.getLabel()));
                    }

                    result = modelResult;
                }
            }
        }
        else {
            result = getModel(modelName).resolveLabel(label);
        }

        return result;
    }

    private DataType resolveTypeName(String typeName) {
        return resolveTypeName(null, typeName);
    }

    private DataType resolveTypeName(String modelName, String typeName) {
        // Attempt to resolve as a label first
        DataType result = resolveLabel(modelName, typeName);

        if (result == null) {
            if (modelName == null || modelName.equals("")) {
                for (Model model : models.values()) {
                    DataType modelResult = model.resolveTypeName(typeName);
                    if (modelResult != null) {
                        if (result != null) {
                            throw new IllegalArgumentException(String.format("Type name %s is ambiguous between %s and %s.",
                                    typeName, ((NamedType) result).getName(), ((NamedType) modelResult).getName()));
                        }

                        result = modelResult;
                    }
                }
            } else {
                result = getModel(modelName).resolveTypeName(typeName);
            }
        }

        return result;
    }

    protected DataType resolvePath(DataType sourceType, String path) {
        // TODO: This is using a naive implementation for now... needs full path support (but not full FluentPath support...)
        String[] identifiers = path.split("\\.");
        for (int i = 0; i < identifiers.length; i++) {
            sourceType = resolveProperty(sourceType, identifiers[i]);
        }

        return sourceType;
    }

    protected DataType resolveProperty(DataType sourceType, String identifier) {
        return resolveProperty(sourceType, identifier, true);
    }

    // TODO: Support case-insensitive models
    protected DataType resolveProperty(DataType sourceType, String identifier, boolean mustResolve) {
        DataType currentType = sourceType;
        while (currentType != null) {
            if (currentType instanceof ClassType) {
                ClassType classType = (ClassType)currentType;
                for (ClassTypeElement e : classType.getElements()) {
                    if (e.getName().equals(identifier)) {
                        if (e.isProhibited()) {
                            throw new IllegalArgumentException(String.format("Element %s cannot be referenced because it is marked prohibited in type %s.", e.getName(), ((ClassType) currentType).getName()));
                        }

                        return e.getType();
                    }
                }
            }
            else if (currentType instanceof TupleType) {
                TupleType tupleType = (TupleType)currentType;
                for (TupleTypeElement e : tupleType.getElements()) {
                    if (e.getName().equals(identifier)) {
                        return e.getType();
                    }
                }
            }
            else if (currentType instanceof IntervalType) {
                IntervalType intervalType = (IntervalType)currentType;
                switch (identifier) {
                    case "low":
                    case "high":
                        return intervalType.getPointType();
                    case "lowClosed":
                    case "highClosed":
                        return resolveTypeName("System", "Boolean");
                    default:
                        throw new IllegalArgumentException(String.format("Invalid interval property name %s.", identifier));
                }
            }

            if (currentType.getBaseType() != null) {
                currentType = currentType.getBaseType();
            }
            else {
                break;
            }
        }

        if (mustResolve) {
            throw new IllegalArgumentException(String.format("Member %s not found for type %s.", identifier, sourceType != null ? sourceType.toLabel() : null));
        }

        return null;
    }

    protected Expression resolveFunction(String libraryName, String operatorName, Invocation invocation) {
        // If the function cannot be resolved in the builder and the call is to a function in the current library,
        // check for forward declarations of functions
        boolean checkForward = libraryName == null || libraryName.equals("") || libraryName.equals(this.libraryInfo.getLibraryName());
        Expression result = resolveCall(libraryName, operatorName, invocation, !checkForward);
        if (result == null) {
            Iterable<FunctionDefinitionInfo> functionInfos = libraryInfo.resolveFunctionReference(operatorName);
            if (functionInfos != null) {
                for (FunctionDefinitionInfo functionInfo : functionInfos) {
                    internalVisitFunctionDefinition(functionInfo.getDefinition());
                }
            }
            result = resolveCall(libraryName, operatorName, invocation, true);
        }

        return result;
    }

    protected Expression resolveCall(String libraryName, String operatorName, Invocation invocation) {
        return resolveCall(libraryName, operatorName, invocation, true);
    }

    protected Expression resolveCall(String libraryName, String operatorName, Invocation invocation, boolean mustResolve) {
        Iterable<Expression> operands = invocation.getOperands();
        List<DataType> dataTypes = new ArrayList<>();
        for (Expression operand : operands) {
            if (operand.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not determine signature for invocation of operator %s%s.",
                        libraryName == null ? "" : libraryName + ".", operatorName));
            }
            dataTypes.add(operand.getResultType());
        }

        CallContext callContext = new CallContext(libraryName, operatorName, dataTypes.toArray(new DataType[dataTypes.size()]));
        OperatorResolution resolution = resolveCall(callContext);
        if (resolution != null || mustResolve) {
            checkOperator(callContext, resolution);

            if (resolution.hasConversions()) {
                List<Expression> convertedOperands = new ArrayList<>();
                Iterator<Expression> operandIterator = operands.iterator();
                Iterator<Conversion> conversionIterator = resolution.getConversions().iterator();
                while (operandIterator.hasNext()) {
                    Expression operand = operandIterator.next();
                    Conversion conversion = conversionIterator.next();
                    if (conversion != null) {
                        convertedOperands.add(convertExpression(operand, conversion));
                    } else {
                        convertedOperands.add(operand);
                    }
                }

                invocation.setOperands(convertedOperands);
            }
            invocation.setResultType(resolution.getOperator().getResultType());
            return invocation.getExpression();
        }
        return null;
    }

    private Expression ensureCompatible(Expression expression, DataType targetType) {
        if (!targetType.isSuperTypeOf(expression.getResultType())) {
            return convertExpression(expression, targetType);
        }

        return expression;
    }

    private Expression convertExpression(Expression expression, DataType targetType) {
        Conversion conversion = conversionMap.findConversion(expression.getResultType(), targetType, true);
        if (conversion != null) {
            return convertExpression(expression, conversion);
        }

        DataTypes.verifyType(expression.getResultType(), targetType);
        return expression;
    }

    private Expression convertListExpression(Expression expression, Conversion conversion) {
        ListType fromType = (ListType)conversion.getFromType();
        ListType toType = (ListType)conversion.getToType();

        Query query = (Query)of.createQuery()
                .withSource((AliasedQuerySource) of.createAliasedQuerySource()
                        .withAlias("X")
                        .withExpression(expression)
                        .withResultType(fromType))
                .withReturn((ReturnClause) of.createReturnClause()
                        .withDistinct(false)
                        .withExpression(convertExpression((AliasRef) of.createAliasRef()
                                        .withName("X")
                                        .withResultType(fromType.getElementType()),
                                conversion.getConversion()))
                        .withResultType(toType))
                .withResultType(toType);
        return query;
    }

    private Expression convertIntervalExpression(Expression expression, Conversion conversion) {
        IntervalType fromType = (IntervalType)conversion.getFromType();
        IntervalType toType = (IntervalType)conversion.getToType();
        Interval interval = (Interval)of.createInterval()
                .withLow(convertExpression((Property)of.createProperty()
                        .withSource(expression)
                        .withPath("low")
                        .withResultType(fromType.getPointType()),
                        conversion.getConversion()))
                .withLowClosedExpression((Property) of.createProperty()
                        .withSource(expression)
                        .withPath("lowClosed")
                        .withResultType(resolveTypeName("System", "Boolean")))
                .withHigh(convertExpression((Property) of.createProperty()
                                .withSource(expression)
                                .withPath("high")
                                .withResultType(fromType.getPointType()),
                        conversion.getConversion()))
                .withHighClosedExpression((Property) of.createProperty()
                        .withSource(expression)
                        .withPath("highClosed")
                        .withResultType(resolveTypeName("System", "Boolean")))
                .withResultType(toType);
        return interval;
    }

    private Expression convertExpression(Expression expression, Conversion conversion) {
        if (conversion.isCast() && conversion.getFromType().isSuperTypeOf(conversion.getToType())) {
            As castedOperand = (As)of.createAs()
                    .withOperand(expression)
                    .withResultType(conversion.getToType());

            castedOperand.setAsTypeSpecifier(dataTypeToTypeSpecifier(castedOperand.getResultType()));
            if (castedOperand.getResultType() instanceof NamedType) {
                castedOperand.setAsType(dataTypeToQName(castedOperand.getResultType()));
            }

            return castedOperand;
        }
        else if (conversion.isListConversion()) {
            return convertListExpression(expression, conversion);
        }
        else if (conversion.isIntervalConversion()) {
            return convertIntervalExpression(expression, conversion);
        }
        else if (conversion.getOperator() != null) {
            FunctionRef functionRef = (FunctionRef)of.createFunctionRef()
                    .withLibraryName(conversion.getOperator().getLibraryName())
                    .withName(conversion.getOperator().getName())
                    .withOperand(expression);

            Expression systemFunction = systemFunctionResolver.resolveSystemFunction(functionRef);
            if (systemFunction != null) {
                return systemFunction;
            }

            resolveCall(functionRef.getLibraryName(), functionRef.getName(), new FunctionRefInvocation(functionRef));

            return functionRef;
        }
        else {
            if (conversion.getToType().equals(resolveTypeName("System", "Boolean"))) {
                return (Expression)of.createToBoolean().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Integer"))) {
                return (Expression)of.createToInteger().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Decimal"))) {
                return (Expression)of.createToDecimal().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "String"))) {
                return (Expression)of.createToString().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "DateTime"))) {
                return (Expression)of.createToDateTime().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Time"))) {
                return (Expression)of.createToTime().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Quantity"))) {
                return (Expression)of.createToQuantity().withOperand(expression).withResultType(conversion.getToType());
            }
            else {
                Convert convertedOperand = (Convert)of.createConvert()
                        .withOperand(expression)
                        .withResultType(conversion.getToType());

                convertedOperand.setToTypeSpecifier(dataTypeToTypeSpecifier(convertedOperand.getResultType()));
                if (convertedOperand.getResultType() instanceof NamedType) {
                    convertedOperand.setToType(dataTypeToQName(convertedOperand.getResultType()));
                }

                return convertedOperand;
            }
        }
    }

    private void verifyType(DataType actualType, DataType expectedType) {
        if (expectedType.isSuperTypeOf(actualType) || actualType.isCompatibleWith(expectedType)) {
            return;
        }

        Conversion conversion = conversionMap.findConversion(actualType, expectedType, true);
        if (conversion != null) {
            return;
        }

        DataTypes.verifyType(actualType, expectedType);
    }

    private DataType ensureCompatibleTypes(DataType first, DataType second) {
        if (first.equals(DataType.ANY)) {
            return second;
        }

        if (second.equals(DataType.ANY)) {
            return first;
        }

        if (first.isSuperTypeOf(second) || second.isCompatibleWith(first)) {
            return first;
        }

        if (second.isSuperTypeOf(first) || first.isCompatibleWith(second)) {
            return second;
        }

        Conversion conversion = conversionMap.findConversion(second, first, true);
        if (conversion != null) {
            return first;
        }

        conversion = conversionMap.findConversion(first, second, true);
        if (conversion != null) {
            return second;
        }

        DataTypes.verifyType(second, first);
        return first;
    }

    protected Expression resolveUnaryCall(String libraryName, String operatorName, UnaryExpression expression) {
        return resolveCall(libraryName, operatorName, new UnaryExpressionInvocation(expression));
    }

    protected Expression resolveBinaryCall(String libraryName, String operatorName, BinaryExpression expression) {
        return resolveCall(libraryName, operatorName, new BinaryExpressionInvocation(expression));
    }

    protected Expression resolveAggregateCall(String libraryName, String operatorName, AggregateExpression expression) {
        return resolveCall(libraryName, operatorName, new AggregateExpressionInvocation(expression));
    }

    private OperatorResolution resolveCall(CallContext callContext) {
        OperatorResolution result = null;
        if (callContext.getLibraryName() == null || callContext.getLibraryName().equals("")) {
            result = translatedLibrary.resolveCall(callContext, conversionMap);
            if (result == null) {
                result = getSystemLibrary().resolveCall(callContext, conversionMap);
                /*
                // Implicit resolution is only allowed for the system library functions.
                for (TranslatedLibrary library : libraries.values()) {
                    OperatorResolution libraryResult = library.resolveCall(callContext, conversionMap);
                    if (libraryResult != null) {
                        if (result != null) {
                            throw new IllegalArgumentException(String.format("Operator name %s is ambiguous between %s and %s.",
                                    callContext.getOperatorName(), result.getOperator().getName(), libraryResult.getOperator().getName()));
                        }

                        result = libraryResult;
                    }
                }
                */

                if (result != null) {
                    checkAccessLevel(result.getOperator().getLibraryName(), result.getOperator().getName(),
                            result.getOperator().getAccessLevel());
                }
            }
        }
        else {
            result = resolveLibrary(callContext.getLibraryName()).resolveCall(callContext, conversionMap);
        }

        return result;
    }

    private void checkOperator(CallContext callContext, OperatorResolution resolution) {
        if (resolution == null) {
            throw new IllegalArgumentException(String.format("Could not resolve call to operator %s with signature %s.",
                    callContext.getOperatorName(), callContext.getSignature()));
        }
    }

    private void checkAccessLevel(String libraryName, String objectName, AccessModifier accessModifier) {
        if (accessModifier == AccessModifier.PRIVATE) {
            throw new IllegalArgumentException(String.format("Object %s in library %s is marked private and cannot be referenced from another library.", objectName, libraryName));
        }
    }

    private Literal createLiteral(String val, String type) {
        DataType resultType = resolveTypeName("System", type);
        Literal result = of.createLiteral().withValue(val).withValueType(dataTypeToQName(resultType));
        result.setResultType(resultType);
        return result;
    }

    private Literal createLiteral(String string) {
        return createLiteral(String.valueOf(string), "String");
    }

    private Literal createLiteral(Boolean bool) {
        return createLiteral(String.valueOf(bool), "Boolean");
    }

    private Literal createLiteral(Integer integer) {
        return createLiteral(String.valueOf(integer), "Integer");
    }

    private Literal createLiteral(Double value) {
        return createLiteral(String.valueOf(value), "Decimal");
    }

    private Literal createNumberLiteral(String value) {
        DataType resultType = resolveTypeName("System", value.contains(".") ? "Decimal" : "Integer");
        Literal result = of.createLiteral()
                .withValue(value)
                .withValueType(dataTypeToQName(resultType));
        result.setResultType(resultType);
        return result;
    }

    private Interval createInterval(Expression low, boolean lowClosed, Expression high, boolean highClosed) {
        Interval result = of.createInterval()
                .withLow(low)
                .withLowClosed(lowClosed)
                .withHigh(high)
                .withHighClosed(highClosed);

        DataType pointType = ensureCompatibleTypes(result.getLow().getResultType(), result.getHigh().getResultType());
        result.setResultType(new IntervalType(pointType));

        result.setLow(ensureCompatible(result.getLow(), pointType));
        result.setHigh(ensureCompatible(result.getHigh(), pointType));

        return result;
    }

    private boolean isBooleanLiteral(Expression expression, Boolean bool) {
        boolean ret = false;
        if (expression instanceof Literal) {
            Literal lit = (Literal) expression;
            ret = lit.getValueType().equals(dataTypeToQName(resolveTypeName("System", "Boolean")));
            if (ret && bool != null) {
                ret = bool.equals(Boolean.valueOf(lit.getValue()));
            }
        }
        return ret;
    }

    private AliasedQuerySource resolveAlias(String identifier) {
        for (QueryContext query : queries) {
            AliasedQuerySource source = query.resolveAlias(identifier);
            if (source != null) {
                return source;
            }
        }

        return null;
    }

    private IdentifierRef resolveQueryResultElement(String identifier) {
        if (queries.size() > 0) {
            QueryContext query = queries.peek();
            if (query.inSortClause() && !query.isSingular()) {
                DataType sortColumnType = resolveProperty(query.getResultElementType(), identifier, false);
                if (sortColumnType != null) {
                    IdentifierRef result = new IdentifierRef().withName(identifier);
                    result.setResultType(sortColumnType);
                    return result;
                }
            }
        }

        return null;
    }

    private LetClause resolveQueryLet(String identifier) {
        for (QueryContext query : queries) {
            LetClause let = query.resolveLet(identifier);
            if (let != null) {
                return let;
            }
        }

        return null;
    }

    private OperandRef resolveOperandRef(String identifier) {
        if (currentFunctionDef != null) {
            for (OperandDef operand : currentFunctionDef.getOperand()) {
                if (operand.getName().equals(identifier)) {
                    return (OperandRef)of.createOperandRef()
                            .withName(identifier)
                            .withResultType(operand.getResultType());
                }
            }
        }

        return null;
    }

    private void setLibraryIdentifier(VersionedIdentifier vid) {
        library.setIdentifier(vid);
        translatedLibrary.setIdentifier(vid);
    }

    private void addToLibrary(UsingDef usingDef) {
        if (library.getUsings() == null) {
            library.setUsings(of.createLibraryUsings());
        }
        library.getUsings().getDef().add(usingDef);

        translatedLibrary.add(usingDef);
    }

    private void addToLibrary(CodeSystemDef cs) {
        if (library.getCodeSystems() == null) {
            library.setCodeSystems(of.createLibraryCodeSystems());
        }
        library.getCodeSystems().getDef().add(cs);

        translatedLibrary.add(cs);
    }

    private void addToLibrary(ValueSetDef vs) {
        if (library.getValueSets() == null) {
            library.setValueSets(of.createLibraryValueSets());
        }
        library.getValueSets().getDef().add(vs);

        translatedLibrary.add(vs);
    }

    private void addToLibrary(CodeDef cd) {
        if (library.getCodes() == null) {
            library.setCodes(of.createLibraryCodes());
        }
        library.getCodes().getDef().add(cd);

        translatedLibrary.add(cd);
    }

    private void addToLibrary(ConceptDef cd) {
        if (library.getConcepts() == null) {
            library.setConcepts(of.createLibraryConcepts());
        }
        library.getConcepts().getDef().add(cd);

        translatedLibrary.add(cd);
    }

    private void addToLibrary(ParameterDef paramDef) {
        if (library.getParameters() == null) {
            library.setParameters(of.createLibraryParameters());
        }
        library.getParameters().getDef().add(paramDef);

        translatedLibrary.add(paramDef);
    }

    private void addToLibrary(ExpressionDef expDef) {
        if (library.getStatements() == null) {
            library.setStatements(of.createLibraryStatements());
        }
        library.getStatements().getDef().add(expDef);

        translatedLibrary.add(expDef);
    }

    private class ExpressionDefinitionContext {
        public ExpressionDefinitionContext(String identifier) {
            this.identifier = identifier;
        }
        private String identifier;
        public String getIdentifier() {
            return identifier;
        }

        private Exception rootCause;
        public Exception getRootCause() {
            return rootCause;
        }

        public void setRootCause(Exception rootCause) {
            this.rootCause = rootCause;
        }
    }

    private class ExpressionDefinitionContextStack extends Stack<ExpressionDefinitionContext> {
        public boolean contains(String identifier) {
            for (int i = 0; i < this.elementCount; i++) {
                if (this.elementAt(i).getIdentifier().equals(identifier)) {
                    return true;
                }
            }

            return false;
        }
    }

    private Exception determineRootCause() {
        if (!expressionDefinitions.isEmpty()) {
            ExpressionDefinitionContext currentContext = expressionDefinitions.peek();
            if (currentContext != null) {
                return currentContext.getRootCause();
            }
        }

        return null;
    }

    private void setRootCause(Exception rootCause) {
        if (!expressionDefinitions.isEmpty()) {
            ExpressionDefinitionContext currentContext = expressionDefinitions.peek();
            if (currentContext != null) {
                currentContext.setRootCause(rootCause);
            }
        }
    }

    private void pushExpressionDefinition(String identifier) {
        if (expressionDefinitions.contains(identifier)) {
            throw new IllegalArgumentException(String.format("Cannot resolve reference to expression %s because it results in a circular reference.", identifier));
        }
        expressionDefinitions.push(new ExpressionDefinitionContext(identifier));
    }

    private void popExpressionDefinition() {
        expressionDefinitions.pop();
    }

    private DataType getExpressionDefResultType(ExpressionDef expressionDef) {
        // If the current expression context is the same as the expression def context, return the expression def result type.
        if (currentExpressionContext().equals(expressionDef.getContext())) {
            return expressionDef.getResultType();
        }

        // If the current expression context is patient, a reference to a population context expression will indicate a full
        // evaluation of the population context expression, and the result type is the same.
        if (inPatientContext()) {
            return expressionDef.getResultType();
        }

        // If the current expression context is population, a reference to a patient context expression will need to be
        // performed for every patient in the population, so the result type is promoted to a list (if it is not already).
        if (inPopulationContext()) {
            // If we are in the source clause of a query, indicate that the source references patient context
            if (!queries.empty() && queries.peek().inSourceClause()) {
                queries.peek().referencePatientContext();
            }

            DataType resultType = expressionDef.getResultType();
            if (!(resultType instanceof ListType)) {
                return new ListType(resultType);
            }
            else {
                return resultType;
            }
        }

        throw new IllegalArgumentException(String.format("Invalid context reference from %s context to %s context.", currentExpressionContext(), expressionDef.getContext()));
    }

    private void pushExpressionContext(String context) {
        expressionContext.push(context);
    }

    private void popExpressionContext() {
        if (expressionContext.empty()) {
            throw new IllegalStateException("Expression context stack is empty.");
        }

        expressionContext.pop();
    }

    private String currentExpressionContext() {
        if (expressionContext.empty()) {
            throw new IllegalStateException("Expression context stack is empty.");
        }

        return expressionContext.peek();
    }

    private boolean inPatientContext() {
        return currentExpressionContext().equals("Patient");
    }

    private boolean inPopulationContext() {
        return currentExpressionContext().equals("Population");
    }

    private void addToLibrary(IncludeDef includeDef) {
        if (library.getIdentifier() == null || library.getIdentifier().getId() == null) {
            throw new IllegalArgumentException("Unnamed libraries cannot reference other libraries.");
        }

        if (library.getIncludes() == null) {
            library.setIncludes(of.createLibraryIncludes());
        }
        library.getIncludes().getDef().add(includeDef);

        translatedLibrary.add(includeDef);

        VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
                .withId(includeDef.getPath())
                .withVersion(includeDef.getVersion());

        TranslatedLibrary referencedLibrary = libraryManager.resolveLibrary(libraryIdentifier, errors);
        libraries.put(includeDef.getLocalIdentifier(), referencedLibrary);
        loadConversionMap(referencedLibrary);
    }

    protected SystemModel getSystemModel() {
        return (SystemModel)getModel("System");
    }

    private void loadSystemLibrary() {
        TranslatedLibrary systemLibrary = SystemLibraryHelper.load(getSystemModel());
        libraries.put(systemLibrary.getIdentifier().getId(), systemLibrary);
        loadConversionMap(systemLibrary);
    }

    private void loadConversionMap(TranslatedLibrary library) {
        for (Conversion conversion : library.getConversions()) {
            conversionMap.add(conversion);
        }
    }

    private void loadConversionMap(Model model) {
        for (Conversion conversion : model.getConversions()) {
            conversionMap.add(conversion);
        }
    }

    private TranslatedLibrary getSystemLibrary() {
        return resolveLibrary("System");
    }

    private TranslatedLibrary resolveLibrary(String identifier) {
        TranslatedLibrary result = libraries.get(identifier);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve library name %s.", identifier));
        }
        return result;
    }

    private void addExpression(Expression expression) {
        expressions.add(expression);
    }

    private TrackBack getTrackBack(ParserRuleContext ctx) {
        TrackBack tb = new TrackBack(
                library.getIdentifier(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine() + 1, // 1-based instead of 0-based
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() // 1-based instead of 0-based
        );
        return tb;
    }

    private TrackBack track(Trackable trackable, ParserRuleContext ctx) {
        TrackBack tb = getTrackBack(ctx);

        trackable.getTrackbacks().add(tb);

        return tb;
    }
}
