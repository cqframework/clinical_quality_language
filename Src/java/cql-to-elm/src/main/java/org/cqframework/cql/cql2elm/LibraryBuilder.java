package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.model.invocation.*;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumService;
import org.hl7.cql.model.*;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.cql_annotations.r1.ErrorSeverity;
import org.hl7.cql_annotations.r1.ErrorType;
import org.hl7.elm.r1.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * Created by Bryn on 12/29/2016.
 */
public class LibraryBuilder {
    public LibraryBuilder(ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService) {
        if (modelManager == null) {
            throw new IllegalArgumentException("modelManager is null");
        }

        if (libraryManager == null) {
            throw new IllegalArgumentException("libraryManager is null");
        }

        this.modelManager = modelManager;
        this.libraryManager = libraryManager;

        this.library = of.createLibrary()
                .withSchemaIdentifier(of.createVersionedIdentifier()
                        .withId("urn:hl7-org:elm") // TODO: Pull this from the ELM library namespace
                        .withVersion("r1"));

        translatedLibrary = new TranslatedLibrary();
        translatedLibrary.setLibrary(library);

        this.ucumService = ucumService;
    }

    // Only exceptions of severity Error
    private final java.util.List<CqlTranslatorException> errors = new ArrayList<>();
    public List<CqlTranslatorException> getErrors() {
        return errors;
    }

    // Only exceptions of severity Warning
    private final java.util.List<CqlTranslatorException> warnings = new ArrayList<>();
    public List<CqlTranslatorException> getWarnings() {
        return warnings;
    }

    // Only exceptions of severity Info
    private final java.util.List<CqlTranslatorException> messages = new ArrayList<>();
    public List<CqlTranslatorException> getMessages() {
        return messages;
    }

    // All exceptions
    private final java.util.List<CqlTranslatorException> exceptions = new ArrayList<>();
    public List<CqlTranslatorException> getExceptions() {
        return exceptions;
    }

    private final Map<String, Model> models = new HashMap<>();
    private final Map<String, TranslatedLibrary> libraries = new HashMap<>();
    private final SystemFunctionResolver systemFunctionResolver = new SystemFunctionResolver(this);
    private final Stack<String> expressionContext = new Stack<>();
    private final ExpressionDefinitionContextStack expressionDefinitions = new ExpressionDefinitionContextStack();
    private final Stack<FunctionDef> functionDefs = new Stack<>();
    private ModelManager modelManager = null;
    private Model defaultModel = null;
    private LibraryManager libraryManager = null;
    private Library library = null;
    public Library getLibrary() {
        return library;
    }
    private TranslatedLibrary translatedLibrary = null;
    public TranslatedLibrary getTranslatedLibrary() {
        return translatedLibrary;
    }
    private final ConversionMap conversionMap = new ConversionMap();
    public ConversionMap getConversionMap() {
        return conversionMap;
    }
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean listTraversal = true;
    private UcumService ucumService = null;

    public void enableListTraversal() {
        listTraversal = true;
    }

    public void disableListTraversal() {
        listTraversal = false;
    }

    private CqlTranslatorException.ErrorSeverity errorLevel = CqlTranslatorException.ErrorSeverity.Info;
    public CqlTranslatorException.ErrorSeverity getErrorLevel() {
        return errorLevel;
    }
    public void setErrorLevel(CqlTranslatorException.ErrorSeverity severity) {
        errorLevel = severity;
    }

    private Model loadModel(VersionedIdentifier modelIdentifier) {
        Model model = modelManager.resolveModel(modelIdentifier);
        loadConversionMap(model);
        return model;
    }

    public Model getDefaultModel() {
        return defaultModel;
    }

    private void setDefaultModel(Model model) {
        // The default model is the first model that is not System
        if (defaultModel == null && !model.getModelInfo().getName().equals("System")) {
            defaultModel = model;
        }
    }

    public Model getModel(VersionedIdentifier modelIdentifier) {
        Model model = models.get(modelIdentifier.getId());
        if (model == null) {
            model = loadModel(modelIdentifier);
            setDefaultModel(model);
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

    private void loadConversionMap(Model model) {
        for (Conversion conversion : model.getConversions()) {
            conversionMap.add(conversion);
        }
    }

    private UsingDef buildUsingDef(VersionedIdentifier modelIdentifier, Model model) {
        UsingDef usingDef = of.createUsingDef()
                .withLocalIdentifier(modelIdentifier.getId())
                .withVersion(modelIdentifier.getVersion())
                .withUri(model.getModelInfo().getUrl());
        // TODO: Needs to write xmlns and schemalocation to the resulting ELM XML document...

        addUsing(usingDef);
        return usingDef;
    }

    public boolean hasUsings() {
        for (Model model : models.values()) {
            if (!model.getModelInfo().getName().equals("System")) {
                return true;
            }
        }

        return false;
    }

    private void addUsing(UsingDef usingDef) {
        if (library.getUsings() == null) {
            library.setUsings(of.createLibraryUsings());
        }
        library.getUsings().getDef().add(usingDef);

        translatedLibrary.add(usingDef);
    }

    public ClassType resolveLabel(String modelName, String label) {
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

    public DataType resolveTypeName(String typeName) {
        return resolveTypeName(null, typeName);
    }

    public DataType resolveTypeName(String modelName, String typeName) {
        // Attempt to resolve as a label first
        DataType result = resolveLabel(modelName, typeName);

        if (result == null) {
            if (modelName == null || modelName.equals("")) {
                // Attempt to resolve in the default model if one is available
                if (defaultModel != null) {
                    DataType modelResult = defaultModel.resolveTypeName(typeName);
                    if (modelResult != null) {
                        return modelResult;
                    }
                }

                // Otherwise, resolve across all models and throw for ambiguous resolution
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

    public DataType resolveTypeSpecifier(String typeSpecifier) {
        if (typeSpecifier == null) {
            throw new IllegalArgumentException("typeSpecifier is null");
        }

        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        if (typeSpecifier.toLowerCase().startsWith("interval<")) {
            DataType pointType = resolveTypeSpecifier(typeSpecifier.substring(typeSpecifier.indexOf('<') + 1, typeSpecifier.lastIndexOf('>')));
            return new IntervalType(pointType);
        }
        else if (typeSpecifier.toLowerCase().startsWith("list<")) {
            DataType elementType = resolveTypeName(typeSpecifier.substring(typeSpecifier.indexOf('<') + 1, typeSpecifier.lastIndexOf('>')));
            return new ListType(elementType);
        }
        else if (typeSpecifier.indexOf(".") >= 0) {
            String modelName = typeSpecifier.substring(0, typeSpecifier.indexOf("."));
            String typeName = typeSpecifier.substring(typeSpecifier.indexOf(".") + 1);
            return resolveTypeName(modelName, typeName);
        }
        else {
            return resolveTypeName(typeSpecifier);
        }
    }

    public UsingDef resolveUsingRef(String modelName) {
        return translatedLibrary.resolveUsingRef(modelName);
    }

    public SystemModel getSystemModel() {
        // TODO: Support loading different versions of the system library
        return (SystemModel)getModel(new VersionedIdentifier().withId("System"));
    }

    public Model getModel(String modelName) {
        UsingDef usingDef = resolveUsingRef(modelName);
        if (usingDef == null) {
            throw new IllegalArgumentException(String.format("Could not resolve model name %s", modelName));
        }

        return getModel(new VersionedIdentifier().withId(usingDef.getLocalIdentifier()).withVersion(usingDef.getVersion()));
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

    public TranslatedLibrary getSystemLibrary() {
        return resolveLibrary("System");
    }

    public TranslatedLibrary resolveLibrary(String identifier) {
        TranslatedLibrary result = libraries.get(identifier);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve library name %s.", identifier));
        }
        return result;
    }

    private ErrorSeverity toErrorSeverity(CqlTranslatorException.ErrorSeverity severity) {
        if (severity == CqlTranslatorException.ErrorSeverity.Info) {
            return ErrorSeverity.INFO;
        }
        else if (severity == CqlTranslatorException.ErrorSeverity.Warning) {
            return ErrorSeverity.WARNING;
        }
        else if (severity == CqlTranslatorException.ErrorSeverity.Error) {
            return ErrorSeverity.ERROR;
        }
        else {
            throw new IllegalArgumentException(String.format("Unknown error severity %s", severity.toString()));
        }
    }

    private void addException(CqlTranslatorException e) {
        // Always add to the list of all exceptions
        exceptions.add(e);

        if (e.getSeverity() == CqlTranslatorException.ErrorSeverity.Error) {
            errors.add(e);
        }
        else if (e.getSeverity() == CqlTranslatorException.ErrorSeverity.Warning) {
            warnings.add(e);
        }
        else if (e.getSeverity() == CqlTranslatorException.ErrorSeverity.Info) {
            messages.add(e);
        }
    }

    private boolean shouldReport(CqlTranslatorException.ErrorSeverity errorSeverity) {
        switch (errorLevel) {
            case Info:
                return
                        errorSeverity == CqlTranslatorException.ErrorSeverity.Info
                                || errorSeverity == CqlTranslatorException.ErrorSeverity.Warning
                                || errorSeverity == CqlTranslatorException.ErrorSeverity.Error;
            case Warning:
                return
                        errorSeverity == CqlTranslatorException.ErrorSeverity.Warning
                                || errorSeverity == CqlTranslatorException.ErrorSeverity.Error;
            case Error:
                return errorSeverity == CqlTranslatorException.ErrorSeverity.Error;
            default:
                throw new IllegalArgumentException(String.format("Unknown error severity %s", errorSeverity.toString()));
        }
    }

    /**
     * Record any errors while parsing in both the list of errors but also in the library
     * itself so they can be processed easily by a remote client
     * @param e the exception to record
     */
    public void recordParsingException(CqlTranslatorException e) {
        addException(e);
        if (shouldReport(e.getSeverity())) {
            CqlToElmError err = af.createCqlToElmError();
            err.setMessage(e.getMessage());
            err.setErrorType(e instanceof CqlSyntaxException ? ErrorType.SYNTAX : (e instanceof CqlSemanticException ? ErrorType.SEMANTIC : ErrorType.INTERNAL));
            err.setErrorSeverity(toErrorSeverity(e.getSeverity()));
            if (e.getLocator() != null) {
                err.setStartLine(e.getLocator().getStartLine());
                err.setEndLine(e.getLocator().getEndLine());
                err.setStartChar(e.getLocator().getStartChar());
                err.setEndChar(e.getLocator().getEndChar());
            }

            if (e.getCause() != null && e.getCause() instanceof CqlTranslatorIncludeException) {
                CqlTranslatorIncludeException incEx = (CqlTranslatorIncludeException) e.getCause();
                err.setTargetIncludeLibraryId(incEx.getLibraryId());
                err.setTargetIncludeLibraryVersionId(incEx.getVersionId());
                err.setErrorType(ErrorType.INCLUDE);
            }
            library.getAnnotation().add(err);
        }
    }

    private String getLibraryName() {
        String libraryName = library.getIdentifier().getId();
        if (libraryName == null) {
            libraryName = "Anonymous";
        }

        return libraryName;
    }


    public void beginTranslation() {
        loadSystemLibrary();

        libraryManager.beginTranslation(getLibraryName());
    }

    public VersionedIdentifier getLibraryIdentifier() {
        return library.getIdentifier();
    }

    public void setLibraryIdentifier(VersionedIdentifier vid) {
        library.setIdentifier(vid);
        translatedLibrary.setIdentifier(vid);
    }

    public void endTranslation() {
        libraryManager.endTranslation(getLibraryName());
    }

    public void addInclude(IncludeDef includeDef) {
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

        ArrayList<CqlTranslatorException> errors = new ArrayList<CqlTranslatorException>();
        TranslatedLibrary referencedLibrary = libraryManager.resolveLibrary(libraryIdentifier, errors);
        for (CqlTranslatorException error : errors) {
            this.addException(error);
        }
        libraries.put(includeDef.getLocalIdentifier(), referencedLibrary);
        loadConversionMap(referencedLibrary);
    }

    public void addParameter(ParameterDef paramDef) {
        if (library.getParameters() == null) {
            library.setParameters(of.createLibraryParameters());
        }
        library.getParameters().getDef().add(paramDef);

        translatedLibrary.add(paramDef);
    }

    public void addCodeSystem(CodeSystemDef cs) {
        if (library.getCodeSystems() == null) {
            library.setCodeSystems(of.createLibraryCodeSystems());
        }
        library.getCodeSystems().getDef().add(cs);

        translatedLibrary.add(cs);
    }

    public void addValueSet(ValueSetDef vs) {
        if (library.getValueSets() == null) {
            library.setValueSets(of.createLibraryValueSets());
        }
        library.getValueSets().getDef().add(vs);

        translatedLibrary.add(vs);
    }

    public void addCode(CodeDef cd) {
        if (library.getCodes() == null) {
            library.setCodes(of.createLibraryCodes());
        }
        library.getCodes().getDef().add(cd);

        translatedLibrary.add(cd);
    }

    public void addConcept(ConceptDef cd) {
        if (library.getConcepts() == null) {
            library.setConcepts(of.createLibraryConcepts());
        }
        library.getConcepts().getDef().add(cd);

        translatedLibrary.add(cd);
    }

    public void addExpression(ExpressionDef expDef) {
        if (library.getStatements() == null) {
            library.setStatements(of.createLibraryStatements());
        }
        library.getStatements().getDef().add(expDef);

        translatedLibrary.add(expDef);
    }

    public Element resolve(String identifier) {
        return translatedLibrary.resolve(identifier);
    }

    public IncludeDef resolveIncludeRef(String identifier) {
        return translatedLibrary.resolveIncludeRef(identifier);
    }

    public CodeSystemDef resolveCodeSystemRef(String identifier) {
        return translatedLibrary.resolveCodeSystemRef(identifier);
    }

    public ValueSetDef resolveValueSetRef(String identifier) {
        return translatedLibrary.resolveValueSetRef(identifier);
    }

    public CodeDef resolveCodeRef(String identifier) {
        return translatedLibrary.resolveCodeRef(identifier);
    }

    public ConceptDef resolveConceptRef(String identifier) {
        return translatedLibrary.resolveConceptRef(identifier);
    }

    public ParameterDef resolveParameterRef(String identifier) {
        return translatedLibrary.resolveParameterRef(identifier);
    }

    public ExpressionDef resolveExpressionRef(String identifier) {
        return translatedLibrary.resolveExpressionRef(identifier);
    }

    public Conversion findConversion(DataType fromType, DataType toType, boolean implicit) {
        return conversionMap.findConversion(fromType, toType, implicit, translatedLibrary.getOperatorMap());
    }

    public Expression resolveUnaryCall(String libraryName, String operatorName, UnaryExpression expression) {
        return resolveCall(libraryName, operatorName, new UnaryExpressionInvocation(expression));
    }

    public Expression resolveBinaryCall(String libraryName, String operatorName, BinaryExpression expression) {
        return resolveCall(libraryName, operatorName, new BinaryExpressionInvocation(expression));
    }

    public Expression resolveTernaryCall(String libraryName, String operatorName, TernaryExpression expression) {
        return resolveCall(libraryName, operatorName, new TernaryExpressionInvocation(expression));
    }

    public Expression resolveNaryCall(String libraryName, String operatorName, NaryExpression expression) {
        return resolveCall(libraryName, operatorName, new NaryExpressionInvocation(expression));
    }

    public Expression resolveAggregateCall(String libraryName, String operatorName, AggregateExpression expression) {
        return resolveCall(libraryName, operatorName, new AggregateExpressionInvocation(expression));
    }

    public Expression resolveIn(Expression left, Expression right) {
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

    public Expression resolveCall(String libraryName, String operatorName, Invocation invocation) {
        return resolveCall(libraryName, operatorName, invocation, true);
    }

    public Expression resolveCall(String libraryName, String operatorName, Invocation invocation, boolean mustResolve) {
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

    public OperatorResolution resolveCall(CallContext callContext) {
        OperatorResolution result = null;
        if (callContext.getLibraryName() == null || callContext.getLibraryName().equals("")) {
            result = translatedLibrary.resolveCall(callContext, conversionMap);
            if (result == null) {
                result = getSystemLibrary().resolveCall(callContext, conversionMap);
                /*
                // Implicit resolution is only allowed for the system library functions.
                for (TranslatedLibrary library : libraries.values()) {
                    OperatorResolution libraryResult = library.resolveCall(callContext, libraryBuilder.getConversionMap());
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

    public void checkOperator(CallContext callContext, OperatorResolution resolution) {
        if (resolution == null) {
            throw new IllegalArgumentException(String.format("Could not resolve call to operator %s with signature %s.",
                    callContext.getOperatorName(), callContext.getSignature()));
        }
    }

    public void checkAccessLevel(String libraryName, String objectName, AccessModifier accessModifier) {
        if (accessModifier == AccessModifier.PRIVATE) {
            throw new IllegalArgumentException(String.format("Object %s in library %s is marked private and cannot be referenced from another library.", objectName, libraryName));
        }
    }

    public Expression resolveFunction(String libraryName, String functionName, Iterable<Expression> paramList) {
        return resolveFunction(libraryName, functionName, paramList, true);
    }

    public Expression resolveFunction(String libraryName, String functionName, Iterable<Expression> paramList, boolean mustResolve) {
        FunctionRef fun = of.createFunctionRef()
                .withLibraryName(libraryName)
                .withName(functionName);

        for (Expression param : paramList) {
            fun.getOperand().add(param);
        }

        Expression systemFunction = systemFunctionResolver.resolveSystemFunction(fun);
        if (systemFunction != null) {
            return systemFunction;
        }

        fun = (FunctionRef)resolveCall(fun.getLibraryName(), fun.getName(), new FunctionRefInvocation(fun), mustResolve);

        return fun;
    }

    public void verifyComparable(DataType dataType) {
        Expression left = (Expression)of.createLiteral().withResultType(dataType);
        Expression right = (Expression)of.createLiteral().withResultType(dataType);
        BinaryExpression comparison = of.createLess().withOperand(left, right);
        resolveBinaryCall("System", "Less", comparison);
    }

    public Expression convertExpression(Expression expression, DataType targetType) {
        Conversion conversion = findConversion(expression.getResultType(), targetType, true);
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

    private void reportWarning(String message, Expression expression) {
        TrackBack trackback = expression.getTrackbacks() != null && expression.getTrackbacks().size() > 0 ? expression.getTrackbacks().get(0) : null;
        CqlSemanticException warning = new CqlSemanticException(message, CqlTranslatorException.ErrorSeverity.Warning, trackback);
        recordParsingException(warning);
    }

    private Expression demoteListExpression(Expression expression, Conversion conversion) {
        ListType fromType = (ListType)conversion.getFromType();
        DataType toType = conversion.getToType();

        SingletonFrom singletonFrom = of.createSingletonFrom().withOperand(expression);
        singletonFrom.setResultType(fromType.getElementType());
        resolveUnaryCall("System", "SingletonFrom", singletonFrom);
        reportWarning("List-valued expression was demoted to a singleton.", expression);

        if (conversion.getConversion() != null) {
            return convertExpression(singletonFrom, conversion.getConversion());
        }
        else {
            return singletonFrom;
        }
    }

    private Expression promoteListExpression(Expression expression, Conversion conversion) {
        if (conversion.getConversion() != null) {
            expression = convertExpression(expression, conversion.getConversion());
        }

        if (expression.getResultType().equals(resolveTypeName("System", "Boolean"))) {
            reportWarning("Boolean-valued expression was promoted to a list.", expression);
        }

        return resolveToList(expression);
    }

    public Expression resolveToList(Expression expression) {
        // Use a ToList operator here to avoid duplicate evaluation of the operand.
        ToList toList = of.createToList().withOperand(expression);
        toList.setResultType(new ListType(expression.getResultType()));
        return toList;
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

    public As buildAs(Expression expression, DataType asType) {
        As result = (As)of.createAs().withOperand(expression).withResultType(asType);
        if (result.getResultType() instanceof NamedType) {
            result.setAsType(dataTypeToQName(result.getResultType()));
        }
        else {
            result.setAsTypeSpecifier(dataTypeToTypeSpecifier(result.getResultType()));
        }

        return result;
    }

    public Is buildIs(Expression expression, DataType isType) {
        Is result = (Is)of.createIs().withOperand(expression).withResultType(resolveTypeName("System", "Boolean"));
        if (isType instanceof NamedType) {
            result.setIsType(dataTypeToQName(isType));
        }
        else {
            result.setIsTypeSpecifier(dataTypeToTypeSpecifier(isType));
        }

        return result;
    }

    public Null buildNull(DataType nullType) {
        Null result = (Null)of.createNull().withResultType(nullType);
        if (nullType instanceof NamedType) {
            result.setResultTypeName(dataTypeToQName(nullType));
        }
        else {
            result.setResultTypeSpecifier(dataTypeToTypeSpecifier(nullType));
        }
        return result;
    }

    public Expression convertExpression(Expression expression, Conversion conversion) {
        if (conversion.isCast()
                && (conversion.getFromType().isSuperTypeOf(conversion.getToType())
                || conversion.getFromType().isCompatibleWith(conversion.getToType()))) {
            As castedOperand = buildAs(expression, conversion.getToType());
            return castedOperand;
        }
        else if (conversion.isCast() && conversion.getConversion() != null
                && (conversion.getFromType().isSuperTypeOf(conversion.getConversion().getFromType())
                || conversion.getFromType().isCompatibleWith(conversion.getConversion().getFromType()))) {
            As castedOperand = buildAs(expression, conversion.getConversion().getFromType());

            Expression result = convertExpression(castedOperand, conversion.getConversion());

            if (conversion.hasAlternativeConversions()) {
                Case caseResult = of.createCase();
                caseResult.setResultType(result.getResultType());
                caseResult.withCaseItem(
                        of.createCaseItem()
                                .withWhen(buildIs(expression, conversion.getConversion().getFromType()))
                                .withThen(result)
                );

                for (Conversion alternative : conversion.getAlternativeConversions()) {
                    caseResult.withCaseItem(
                            of.createCaseItem()
                                .withWhen(buildIs(expression, alternative.getFromType()))
                                .withThen(convertExpression(buildAs(expression, alternative.getFromType()), alternative))
                    );
                }

                caseResult.withElse(buildNull(result.getResultType()));
                result = caseResult;
            }

            return result;
        }
        else if (conversion.isListConversion()) {
            return convertListExpression(expression, conversion);
        }
        else if (conversion.isListDemotion()) {
            return demoteListExpression(expression, conversion);
        }
        else if (conversion.isListPromotion()) {
            return promoteListExpression(expression, conversion);
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

                if (convertedOperand.getResultType() instanceof NamedType) {
                    convertedOperand.setToType(dataTypeToQName(convertedOperand.getResultType()));
                }
                else {
                    convertedOperand.setToTypeSpecifier(dataTypeToTypeSpecifier(convertedOperand.getResultType()));
                }

                return convertedOperand;
            }
        }
    }

    public void verifyType(DataType actualType, DataType expectedType) {
        if (expectedType.isSuperTypeOf(actualType) || actualType.isCompatibleWith(expectedType)) {
            return;
        }

        Conversion conversion = findConversion(actualType, expectedType, true);
        if (conversion != null) {
            return;
        }

        DataTypes.verifyType(actualType, expectedType);
    }

    public DataType findCompatibleType(DataType first, DataType second) {
        if (first == null || second == null) {
            return null;
        }

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

        Conversion conversion = findConversion(second, first, true);
        if (conversion != null) {
            return first;
        }

        conversion = findConversion(first, second, true);
        if (conversion != null) {
            return second;
        }

        return null;
    }

    public DataType ensureCompatibleTypes(DataType first, DataType second) {
        DataType compatibleType = findCompatibleType(first, second);
        if (compatibleType != null) {
            return compatibleType;
        }

        DataTypes.verifyType(second, first);
        return first;
    }

    public Expression ensureCompatible(Expression expression, DataType targetType) {
        if (targetType == null) {
            return of.createNull();
        }

        if (!targetType.isSuperTypeOf(expression.getResultType())) {
            return convertExpression(expression, targetType);
        }

        return expression;
    }

    public Literal createLiteral(String val, String type) {
        DataType resultType = resolveTypeName("System", type);
        Literal result = of.createLiteral().withValue(val).withValueType(dataTypeToQName(resultType));
        result.setResultType(resultType);
        return result;
    }

    public Literal createLiteral(String string) {
        return createLiteral(String.valueOf(string), "String");
    }

    public Literal createLiteral(Boolean bool) {
        return createLiteral(String.valueOf(bool), "Boolean");
    }

    public Literal createLiteral(Integer integer) {
        return createLiteral(String.valueOf(integer), "Integer");
    }

    public Literal createLiteral(Double value) {
        return createLiteral(String.valueOf(value), "Decimal");
    }

    public Literal createNumberLiteral(String value) {
        DataType resultType = resolveTypeName("System", value.contains(".") ? "Decimal" : "Integer");
        Literal result = of.createLiteral()
                .withValue(value)
                .withValueType(dataTypeToQName(resultType));
        result.setResultType(resultType);
        return result;
    }

    public void validateUnit(String unit) {
        switch (unit) {
            case "year":
            case "years":
            case "month":
            case "months":
            case "week":
            case "weeks":
            case "day":
            case "days":
            case "hour":
            case "hours":
            case "minute":
            case "minutes":
            case "second":
            case "seconds":
            case "millisecond":
            case "milliseconds":
                // CQL-defined temporal precisions are valid units
            break;

            default:
                if (ucumService != null) {
                    String message = ucumService.validate(unit);
                    if (message != null) {
                        throw new IllegalArgumentException(message);
                    }
                }
            break;
        }
    }

    public Quantity createQuantity(BigDecimal value, String unit) {
        validateUnit(unit);
        Quantity result = of.createQuantity().withValue(value).withUnit(unit);
        DataType resultType = resolveTypeName("System", "Quantity");
        result.setResultType(resultType);
        return result;
    }

    public Interval createInterval(Expression low, boolean lowClosed, Expression high, boolean highClosed) {
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

    public QName dataTypeToQName(DataType type) {
        if (type instanceof NamedType) {
            NamedType namedType = (NamedType)type;
            ModelInfo modelInfo = getModel(namedType.getNamespace()).getModelInfo();
            return new QName(modelInfo.getUrl(), namedType.getSimpleName());
        }

        throw new IllegalArgumentException("A named type is required in this context.");
    }

    public TypeSpecifier dataTypeToTypeSpecifier(DataType type) {
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
        else if (type instanceof ChoiceType) {
            return choiceTypeToTypeSpecifier((ChoiceType)type);
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

    private TypeSpecifier choiceTypeToTypeSpecifier(ChoiceType type) {
        return (TypeSpecifier)of.createChoiceTypeSpecifier()
                .withType(choiceTypeTypesToTypeSpecifiers(type))
                .withResultType(type);
    }

    private TypeSpecifier[] choiceTypeTypesToTypeSpecifiers(ChoiceType choiceType) {
        List<TypeSpecifier> specifiers = new ArrayList<>();

        for (DataType type : choiceType.getTypes()) {
            specifiers.add(dataTypeToTypeSpecifier(type));
        }

        return specifiers.toArray(new TypeSpecifier[specifiers.size()]);
    }

    public DataType resolvePath(DataType sourceType, String path) {
        // TODO: This is using a naive implementation for now... needs full path support (but not full FluentPath support...)
        String[] identifiers = path.split("\\.");
        for (int i = 0; i < identifiers.length; i++) {
            sourceType = resolveProperty(sourceType, identifiers[i]);
        }

        return sourceType;
    }

    public DataType resolveProperty(DataType sourceType, String identifier) {
        return resolveProperty(sourceType, identifier, true);
    }

    // TODO: Support case-insensitive models
    public DataType resolveProperty(DataType sourceType, String identifier, boolean mustResolve) {
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
            else if (currentType instanceof ChoiceType) {
                ChoiceType choiceType = (ChoiceType)currentType;
                // TODO: Issue a warning if the property does not resolve against every type in the choice

                // Resolve the property against each type in the choice
                Set<DataType> resultTypes = new HashSet<>();
                for (DataType choice : choiceType.getTypes()) {
                    DataType resultType = resolveProperty(choice, identifier, false);
                    if (resultType != null) {
                        resultTypes.add(resultType);
                    }
                }

                // The result type is a choice of all the resolved types
                if (resultTypes.size() > 1) {
                    return new ChoiceType(resultTypes);
                }

                if (resultTypes.size() == 1) {
                    for (DataType resultType : resultTypes) {
                        return resultType;
                    }
                }
            }
            else if (currentType instanceof ListType && listTraversal) {
                // NOTE: FHIRPath path traversal support
                // Resolve property as a list of items of property of the element type
                ListType listType = (ListType)currentType;
                DataType resultType = resolveProperty(listType.getElementType(), identifier);
                return new ListType(resultType);

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

    public Expression resolveIdentifier(String identifier, boolean mustResolve) {
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

        // In the case of a $this alias, names may be resolved as implicit property references
        Expression thisElement = resolveQueryThisElement(identifier);
        if (thisElement != null) {
            return thisElement;
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

        Element element = resolve(identifier);

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

        if (mustResolve) {
            throw new IllegalArgumentException(String.format("Could not resolve identifier %s in the current library.", identifier));
        }

        return null;
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
        else if (left.getResultType() instanceof ListType && listTraversal) {
            // NOTE: FHIRPath path traversal support
            // Resolve property access of a list of items as a query:
            // listValue.property ::= listValue X where X.property is not null return X.property
            ListType listType = (ListType)left.getResultType();
            DataType propertyType = resolveProperty(listType.getElementType(), memberIdentifier);
            Property accessor = of.createProperty()
                    .withSource(of.createAliasRef().withName("$this"))
                    .withPath(memberIdentifier);
            accessor.setResultType(propertyType);
            IsNull isNull = of.createIsNull().withOperand(accessor);
            isNull.setResultType(resolveTypeName("System", "Boolean"));
            Not not = of.createNot().withOperand(isNull);
            not.setResultType(resolveTypeName("System", "Boolean"));

            // Recreate property, it needs to be accessed twice
            accessor = of.createProperty()
                    .withSource(of.createAliasRef().withName("$this"))
                    .withPath(memberIdentifier);
            accessor.setResultType(propertyType);

            AliasedQuerySource source = of.createAliasedQuerySource().withExpression(left).withAlias("$this");
            source.setResultType(left.getResultType());
            Query query = of.createQuery()
                    .withSource(source)
                    .withWhere(not)
                    .withReturn(of.createReturnClause().withExpression(accessor));
            query.setResultType(new ListType(accessor.getResultType()));

            if (accessor.getResultType() instanceof ListType) {
                Flatten result = of.createFlatten().withOperand(query);
                result.setResultType(accessor.getResultType());
                return result;
            }

            return query;
        }
        else {
            Property result = of.createProperty()
                    .withSource(left)
                    .withPath(memberIdentifier);
            result.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
            return result;
        }
    }

    private IdentifierRef resolveQueryResultElement(String identifier) {
        if (inQueryContext()) {
            QueryContext query = peekQueryContext();
            if (query.inSortClause() && !query.isSingular()) {
                if (identifier.equals("$this")) {
                    IdentifierRef result = new IdentifierRef().withName(identifier);
                    result.setResultType(query.getResultElementType());
                    return result;
                }

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

    private AliasedQuerySource resolveAlias(String identifier) {
        // Need to use a for loop to go through backwards, iteration on a Stack is bottom up
        if (inQueryContext()) {
            for (int i = getScope().getQueries().size() - 1; i >= 0; i--) {
                AliasedQuerySource source = getScope().getQueries().get(i).resolveAlias(identifier);
                if (source != null) {
                    return source;
                }
            }
        }

        return null;
    }

    private Expression resolveQueryThisElement(String identifier) {
        if (inQueryContext()) {
            QueryContext query = peekQueryContext();
            if (query.isImplicit()) {
                AliasedQuerySource source = resolveAlias("$this");
                if (source != null) {
                    AliasRef aliasRef = of.createAliasRef().withName("$this");
                    if (source.getResultType() instanceof ListType) {
                        aliasRef.setResultType(((ListType)source.getResultType()).getElementType());
                    }
                    else {
                        aliasRef.setResultType(source.getResultType());
                    }

                    DataType resultType = resolveProperty(aliasRef.getResultType(), identifier, false);
                    if (resultType != null) {
                        return resolveAccessor(aliasRef, identifier);
                    }
                }
            }
        }

        return null;
    }

    private LetClause resolveQueryLet(String identifier) {
        // Need to use a for loop to go through backwards, iteration on a Stack is bottom up
        if (inQueryContext()) {
            for (int i = getScope().getQueries().size() - 1; i >= 0; i--) {
                LetClause let = getScope().getQueries().get(i).resolveLet(identifier);
                if (let != null) {
                    return let;
                }
            }
        }

        return null;
    }

    private OperandRef resolveOperandRef(String identifier) {
        if (!functionDefs.empty()) {
            for (OperandDef operand : functionDefs.peek().getOperand()) {
                if (operand.getName().equals(identifier)) {
                    return (OperandRef)of.createOperandRef()
                            .withName(identifier)
                            .withResultType(operand.getResultType());
                }
            }
        }

        return null;
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
            if (inQueryContext() && getScope().getQueries().peek().inSourceClause()) {
                getScope().getQueries().peek().referencePatientContext();
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

    private class Scope {
        private final Stack<Expression> targets = new Stack<>();
        private final Stack<QueryContext> queries = new Stack<>();

        public Stack<Expression> getTargets() {
            return targets;
        }

        public Stack<QueryContext> getQueries() {
            return queries;
        }
    }

    private class ExpressionDefinitionContext {
        public ExpressionDefinitionContext(String identifier) {
            this.identifier = identifier;
        }
        private String identifier;
        public String getIdentifier() {
            return identifier;
        }

        private Scope scope = new Scope();
        public Scope getScope() {
            return scope;
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

    public Exception determineRootCause() {
        if (!expressionDefinitions.isEmpty()) {
            ExpressionDefinitionContext currentContext = expressionDefinitions.peek();
            if (currentContext != null) {
                return currentContext.getRootCause();
            }
        }

        return null;
    }

    public void setRootCause(Exception rootCause) {
        if (!expressionDefinitions.isEmpty()) {
            ExpressionDefinitionContext currentContext = expressionDefinitions.peek();
            if (currentContext != null) {
                currentContext.setRootCause(rootCause);
            }
        }
    }

    public void pushExpressionDefinition(String identifier) {
        if (expressionDefinitions.contains(identifier)) {
            throw new IllegalArgumentException(String.format("Cannot resolve reference to expression or function %s because it results in a circular reference.", identifier));
        }
        expressionDefinitions.push(new ExpressionDefinitionContext(identifier));
    }

    public void popExpressionDefinition() {
        expressionDefinitions.pop();
    }

    private boolean hasScope() {
        return !expressionDefinitions.empty();
    }

    private Scope getScope() {
        return expressionDefinitions.peek().getScope();
    }

    public void pushExpressionContext(String context) {
        expressionContext.push(context);
    }

    public void popExpressionContext() {
        if (expressionContext.empty()) {
            throw new IllegalStateException("Expression context stack is empty.");
        }

        expressionContext.pop();
    }

    public String currentExpressionContext() {
        if (expressionContext.empty()) {
            throw new IllegalStateException("Expression context stack is empty.");
        }

        return expressionContext.peek();
    }

    public boolean inPatientContext() {
        return currentExpressionContext().equals("Patient");
    }

    public boolean inPopulationContext() {
        return currentExpressionContext().equals("Population");
    }

    public boolean inQueryContext() {
        return hasScope() && getScope().getQueries().size() > 0;
    }

    public void pushQueryContext(QueryContext context) {
        getScope().getQueries().push(context);
    }

    public QueryContext popQueryContext() {
        return getScope().getQueries().pop();
    }

    public QueryContext peekQueryContext() {
        return getScope().getQueries().peek();
    }

    public void pushExpressionTarget(Expression target) {
        getScope().getTargets().push(target);
    }

    public Expression popExpressionTarget() {
        return getScope().getTargets().pop();
    }

    public boolean hasExpressionTarget() {
        return hasScope() && !getScope().getTargets().isEmpty();
    }

    public void beginFunctionDef(FunctionDef functionDef) {
        functionDefs.push(functionDef);
    }

    public void endFunctionDef() {
        functionDefs.pop();
    }
}
