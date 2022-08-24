package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.model.invocation.*;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumService;
import org.hl7.cql.model.*;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.cql_annotations.r1.ErrorSeverity;
import org.hl7.cql_annotations.r1.ErrorType;
import org.hl7.elm.r1.*;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;


/**
 * Created by Bryn on 12/29/2016.
 */
public class LibraryBuilder implements ModelResolver {

    public static final String SYSTEM = "System";
    public static final String INTEGER = "Integer";
    public static final String DECIMAL = "Decimal";
    public static final String $_THIS = "$this";
    public static final String HIGH = "high";
    public static final String HIGH_CLOSED = "highClosed";
    public static final String COMPATIBILITY_LEVEL_1_5 = "1.5";
    public static final String $_INDEX = "$index";
    public static final String $_TOTAL = "$total";
    public static final String S_RESOLVED_AS_AN_EXPRESSION_DEFINITION = "%s resolved as an expression definition";
    public static final String S_RESOLVED_AS_A_PARAMETER = "%s resolved as a parameter";
    public static final String S_RESOLVED_AS_A_VALUE_SET = "%s resolved as a value set";
    public static final String S_RESOLVED_AS_A_CODE_SYSTEM = "%s resolved as a code system";
    public static final String S_RESOLVED_AS_A_CODE = "%s resolved as a code";
    public static final String S_RESOLVED_AS_A_CONCEPT = "%s resolved as a concept";
    public static final String S_RESOLVED_AS_A_LIBRARY = "%s resolved as a library";
    public static final String S_RESOLVED_MORE_THAN_ONCE = "%s resolved more than once: ";
    public static final String S_RESOLVED_AS_AN_ELEMENT_OF_THE_RESULT_OF_A_QUERY = "%s resolved as an element of the result of a query";
    public static final String S_RESOLVED_AS_THE_INDEX_ITERATION_ACCESSOR = "%s resolved as the index iteration accessor";
    public static final String S_RESOLVED_AS_THE_TOTAL_AGGREGATION_ACCESSOR = "%s resolved as the total aggregation accessor";
    public static final String S_RESOLVED_AS_AN_ALIAS_OF_A_QUERY = "%s resolved as an alias of a query";
    public static final String S_RESOLVED_AS_A_LET_OF_A_QUERY = "%s resolved as a let of a query";
    public static final String S_RESOLVED_AS_AN_OPERAND_TO_A_FUNCTION = "%s resolved as an operand to a function";
    public static final String S_RESOLVED_AS_A_CONTEXT_ACCESSOR = "%s resolved as a context accessor";
    public static final String S_RESOLVED_AS_A_POTENTIAL_TYPE_NAME = "%s resolved as a potential type name";
    public static final String COULD_NOT_VALIDATE_REFERENCE_TO_PARAMETER_S_BECAUSE_ITS_DEFINITION_CONTAINS_ERRORS = "Could not validate reference to parameter %s because its definition contains errors.";
    public static final String COULD_NOT_RESOLVE_IDENTIFIER_S_IN_THE_CURRENT_LIBRARY = "Could not resolve identifier %s in the current library.";


    public static enum SignatureLevel {
        /*
        Indicates signatures will never be included in operator invocations
         */
        None,

        /*
        Indicates signatures will only be included in invocations if the declared signature of the resolve operator is different than the invocation signature
         */
        Differing,

        /*
        Indicates signatures will only be included in invocations if the function has multiple overloads with the same number of arguments as the invocation
         */
        Overloads,

        /*
        Indicates signatures will always be included in invocations
         */
        All
    }

    public LibraryBuilder(ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService) {
        this(null, modelManager, libraryManager, ucumService);
    }

    public LibraryBuilder(NamespaceInfo namespaceInfo, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService) {
        if (modelManager == null) {
            throw new IllegalArgumentException("modelManager is null");
        }

        if (libraryManager == null) {
            throw new IllegalArgumentException("libraryManager is null");
        }

        this.namespaceInfo = namespaceInfo; // Note: allowed to be null, implies global namespace
        this.modelManager = modelManager;
        this.libraryManager = libraryManager;
        this.typeBuilder = new TypeBuilder(of, this);

        this.library = of.createLibrary()
                .withSchemaIdentifier(of.createVersionedIdentifier()
                        .withId("urn:hl7-org:elm") // TODO: Pull this from the ELM library namespace
                        .withVersion("r1"));

        this.cqlToElmInfo = af.createCqlToElmInfo();
        this.cqlToElmInfo.setTranslatorVersion(LibraryBuilder.class.getPackage().getImplementationVersion());
        this.library.getAnnotation().add(this.cqlToElmInfo);

        compiledLibrary = new CompiledLibrary();
        compiledLibrary.setLibrary(library);

        this.ucumService = ucumService;
    }

    // Only exceptions of severity Error
    private final java.util.List<CqlCompilerException> errors = new ArrayList<>();
    public List<CqlCompilerException> getErrors() {
        return errors;
    }

    // Only exceptions of severity Warning
    private final java.util.List<CqlCompilerException> warnings = new ArrayList<>();
    public List<CqlCompilerException> getWarnings() {
        return warnings;
    }

    // Only exceptions of severity Info
    private final java.util.List<CqlCompilerException> messages = new ArrayList<>();
    public List<CqlCompilerException> getMessages() {
        return messages;
    }

    // All exceptions
    private final java.util.List<CqlCompilerException> exceptions = new ArrayList<>();
    public List<CqlCompilerException> getExceptions() {
        return exceptions;
    }

    private final Map<String, Model> models = new LinkedHashMap<>();
    private final Map<String, CompiledLibrary> libraries = new LinkedHashMap<>();
    private final SystemFunctionResolver systemFunctionResolver = new SystemFunctionResolver(this);
    private final Stack<String> expressionContext = new Stack<>();
    private final ExpressionDefinitionContextStack expressionDefinitions = new ExpressionDefinitionContextStack();
    private final Stack<FunctionDef> functionDefs = new Stack<>();
    private int literalContext = 0;
    private int typeSpecifierContext = 0;
    private NamespaceInfo namespaceInfo = null;
    private ModelManager modelManager = null;
    private Model defaultModel = null;
    private LibraryManager libraryManager = null;
    private Library library = null;
    public Library getLibrary() {
        return library;
    }
    private CompiledLibrary compiledLibrary = null;
    public CompiledLibrary getCompiledLibrary() {
        return compiledLibrary;
    }
    private final ConversionMap conversionMap = new ConversionMap();
    public ConversionMap getConversionMap() {
        return conversionMap;
    }
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean listTraversal = true;
    private UcumService ucumService = null;
    private CqlTranslatorOptions options;
    private CqlToElmInfo cqlToElmInfo = null;
    private TypeBuilder typeBuilder = null;
    private Cql2ElmVisitor visitor = null;

    public void enableListTraversal() {
        listTraversal = true;
    }

    public void setTranslatorOptions(CqlTranslatorOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("Options cannot be null");
        }

        this.options = options;
        if (options.getOptions().contains(CqlTranslatorOptions.Options.DisableListTraversal)) {
            this.listTraversal = false;
        }
        if (options.getOptions().contains(CqlTranslatorOptions.Options.DisableListDemotion)) {
            this.getConversionMap().disableListDemotion();
        }
        if (options.getOptions().contains(CqlTranslatorOptions.Options.DisableListPromotion)) {
            this.getConversionMap().disableListPromotion();
        }
        if (options.getOptions().contains(CqlTranslatorOptions.Options.EnableIntervalDemotion)) {
            this.getConversionMap().enableIntervalDemotion();
        }
        if (options.getOptions().contains(CqlTranslatorOptions.Options.EnableIntervalPromotion)) {
            this.getConversionMap().enableIntervalPromotion();
        }
        setCompatibilityLevel(options.getCompatibilityLevel());
        this.cqlToElmInfo.setTranslatorOptions(options.toString());
    }

    public void setVisitor(Cql2ElmVisitor visitor) {
        this.visitor = visitor;
    }

    private String compatibilityLevel = null;
    public boolean isCompatibilityLevel3() {
        return "1.3".equals(compatibilityLevel);
    }

    public boolean isCompatibilityLevel4() {
        return "1.4".equals(compatibilityLevel);
    }

    private Version compatibilityVersion;
    public String getCompatibilityLevel() {
        return this.compatibilityLevel;
    }
    public void setCompatibilityLevel(String compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
        if (compatibilityLevel != null) {
            this.compatibilityVersion = new Version(compatibilityLevel);
        }
    }

    public boolean isCompatibleWith(String sinceCompatibilityLevel) {
        if (compatibilityVersion == null) {
            // No compatibility version is specified, assume latest functionality
            return true;
        }

        if (sinceCompatibilityLevel == null || sinceCompatibilityLevel.isEmpty()) {
            throw new IllegalArgumentException("Internal Translator Error: compatibility level is required to perform a compatibility check");
        }

        Version sinceVersion = new Version(sinceCompatibilityLevel);
        return compatibilityVersion.compatibleWith(sinceVersion);
    }

    public void checkCompatibilityLevel(String featureName, String sinceCompatibilityLevel) {
        if (featureName == null || featureName.isEmpty()) {
            throw new IllegalArgumentException("Internal Translator Error: feature name is required to perform a compatibility check");
        }

        if (!isCompatibleWith(sinceCompatibilityLevel)) {
            throw new IllegalArgumentException(String.format("Feature %s was introduced in version %s and so cannot be used at compatibility level %s", featureName, sinceCompatibilityLevel, compatibilityLevel));
        }
    }

    /*
    A "well-known" model name is one that is allowed to resolve without a namespace in a namespace-aware context
     */
    public boolean isWellKnownModelName(String unqualifiedIdentifier) {
        if (namespaceInfo == null) {
            return false;
        }

        return modelManager.isWellKnownModelName(unqualifiedIdentifier);
    }

    /*
    A "well-known" library name is a library name that is allowed to resolve without a namespace in a namespace-aware context
     */
    public boolean isWellKnownLibraryName(String unqualifiedIdentifier) {
        if (namespaceInfo == null) {
            return false;
        }

        return libraryManager.isWellKnownLibraryName(unqualifiedIdentifier);
    }

    public NamespaceInfo getNamespaceInfo() {
        return this.namespaceInfo;
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

    public Model getModel(VersionedIdentifier modelIdentifier, String localIdentifier) {
        Model model = models.get(localIdentifier);
        if (model == null) {
            model = loadModel(modelIdentifier);
            setDefaultModel(model);
            models.put(localIdentifier, model);
            // Add the model using def to the output
            buildUsingDef(modelIdentifier, model, localIdentifier);
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

    private UsingDef buildUsingDef(VersionedIdentifier modelIdentifier, Model model, String localIdentifier) {
        UsingDef usingDef = of.createUsingDef()
                .withLocalIdentifier(localIdentifier)
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

        compiledLibrary.add(usingDef);
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

    public ModelContext resolveContextName(String modelName, String contextName) {
        // Attempt to resolve as a label first
        ModelContext result = null;

        if (modelName == null || modelName.equals("")) {
            // Attempt to resolve in the default model if one is available
            if (defaultModel != null) {
                ModelContext modelResult = defaultModel.resolveContextName(contextName);
                if (modelResult != null) {
                    return modelResult;
                }
            }

            // Otherwise, resolve across all models and throw for ambiguous resolution
            for (Model model : models.values()) {
                ModelContext modelResult = model.resolveContextName(contextName);
                if (modelResult != null) {
                    if (result != null) {
                        throw new IllegalArgumentException(String.format("Context name %s is ambiguous between %s and %s.",
                                contextName, result.getName(), modelResult.getName()));
                    }

                    result = modelResult;
                }
            }
        } else {
            result = getModel(modelName).resolveContextName(contextName);
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

        // Types introduced in 1.5: Long, Vocabulary, ValueSet, CodeSystem
        if (result != null && result instanceof NamedType) {
            switch (((NamedType)result).getName()) {
                case "System.Long":
                case "System.Vocabulary":
                case "System.CodeSystem":
                case "System.ValueSet":
                    if (!isCompatibleWith("1.5")) {
                        throw new IllegalArgumentException(String.format("The type %s was introduced in CQL 1.5 and cannot be referenced at compatibility level %s",
                                ((NamedType)result).getName(), getCompatibilityLevel()));
                    }
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
        return compiledLibrary.resolveUsingRef(modelName);
    }

    public SystemModel getSystemModel() {
        // TODO: Support loading different versions of the system library
        return (SystemModel)getModel(new VersionedIdentifier().withId("System"), "System");
    }

    public Model getModel(String modelName) {
        UsingDef usingDef = resolveUsingRef(modelName);
        if (usingDef == null) {
            throw new IllegalArgumentException(String.format("Could not resolve model name %s", modelName));
        }

        return getModel(usingDef);
    }

    public Model getModel(UsingDef usingDef) {
        if (usingDef == null) {
            throw new IllegalArgumentException("usingDef required");
        }

        return getModel(new VersionedIdentifier()
                        .withSystem(NamespaceManager.getUriPart(usingDef.getUri()))
                        .withId(NamespaceManager.getNamePart(usingDef.getUri()))
                        .withVersion(usingDef.getVersion()),
                usingDef.getLocalIdentifier());
    }

    private void loadSystemLibrary() {
        CompiledLibrary systemLibrary = SystemLibraryHelper.load(getSystemModel(), typeBuilder);
        libraries.put(systemLibrary.getIdentifier().getId(), systemLibrary);
        loadConversionMap(systemLibrary);
    }

    private void loadConversionMap(CompiledLibrary library) {
        for (Conversion conversion : library.getConversions()) {
            conversionMap.add(conversion);
        }
    }

    public CompiledLibrary getSystemLibrary() {
        return resolveLibrary("System");
    }

    public CompiledLibrary resolveLibrary(String identifier) {

        if (!identifier.equals("System")) {
            checkLiteralContext();
        }

        CompiledLibrary result = libraries.get(identifier);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve library name %s.", identifier));
        }
        return result;
    }

    public String resolveNamespaceUri(String namespaceName, boolean mustResolve) {
        String namespaceUri = libraryManager.getNamespaceManager().resolveNamespaceUri(namespaceName);

        if (namespaceUri == null && mustResolve) {
            throw new IllegalArgumentException(String.format("Could not resolve namespace name %s", namespaceName));
        }

        return namespaceUri;
    }

    private ErrorSeverity toErrorSeverity(CqlCompilerException.ErrorSeverity severity) {
        if (severity == CqlCompilerException.ErrorSeverity.Info) {
            return ErrorSeverity.INFO;
        }
        else if (severity == CqlCompilerException.ErrorSeverity.Warning) {
            return ErrorSeverity.WARNING;
        }
        else if (severity == CqlCompilerException.ErrorSeverity.Error) {
            return ErrorSeverity.ERROR;
        }
        else {
            throw new IllegalArgumentException(String.format("Unknown error severity %s", severity.toString()));
        }
    }

    private void addException(CqlCompilerException e) {
        // Always add to the list of all exceptions
        exceptions.add(e);

        if (e.getSeverity() == CqlCompilerException.ErrorSeverity.Error) {
            errors.add(e);
        }
        else if (e.getSeverity() == CqlCompilerException.ErrorSeverity.Warning) {
            warnings.add(e);
        }
        else if (e.getSeverity() == CqlCompilerException.ErrorSeverity.Info) {
            messages.add(e);
        }
    }

    private boolean shouldReport(CqlCompilerException.ErrorSeverity errorSeverity) {
        switch (options.getErrorLevel()) {
            case Info:
                return
                        errorSeverity == CqlCompilerException.ErrorSeverity.Info
                                || errorSeverity == CqlCompilerException.ErrorSeverity.Warning
                                || errorSeverity == CqlCompilerException.ErrorSeverity.Error;
            case Warning:
                return
                        errorSeverity == CqlCompilerException.ErrorSeverity.Warning
                                || errorSeverity == CqlCompilerException.ErrorSeverity.Error;
            case Error:
                return errorSeverity == CqlCompilerException.ErrorSeverity.Error;
            default:
                throw new IllegalArgumentException(String.format("Unknown error severity %s", errorSeverity.toString()));
        }
    }

    /**
     * Record any errors while parsing in both the list of errors but also in the library
     * itself so they can be processed easily by a remote client
     * @param e the exception to record
     */
    public void recordParsingException(CqlCompilerException e) {
        addException(e);
        if (shouldReport(e.getSeverity())) {
            CqlToElmError err = af.createCqlToElmError();
            err.setMessage(e.getMessage());
            err.setErrorType(e instanceof CqlSyntaxException ? ErrorType.SYNTAX : (e instanceof CqlSemanticException ? ErrorType.SEMANTIC : ErrorType.INTERNAL));
            err.setErrorSeverity(toErrorSeverity(e.getSeverity()));
            if (e.getLocator() != null) {
                if (e.getLocator().getLibrary() != null) {
                    err.setLibrarySystem(e.getLocator().getLibrary().getSystem());
                    err.setLibraryId(e.getLocator().getLibrary().getId());
                    err.setLibraryVersion(e.getLocator().getLibrary().getVersion());
                }
                err.setStartLine(e.getLocator().getStartLine());
                err.setEndLine(e.getLocator().getEndLine());
                err.setStartChar(e.getLocator().getStartChar());
                err.setEndChar(e.getLocator().getEndChar());
            }

            if (e.getCause() != null && e.getCause() instanceof CqlTranslatorIncludeException) {
                CqlTranslatorIncludeException incEx = (CqlTranslatorIncludeException) e.getCause();
                err.setTargetIncludeLibrarySystem(incEx.getLibrarySystem());
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

        if (library.getIdentifier().getSystem() != null) {
            libraryName = library.getIdentifier().getSystem() + "/" + libraryName;
        }

        return libraryName;
    }


    public void beginTranslation() {
        loadSystemLibrary();

        libraryManager.beginCompilation(getLibraryName());
    }

    public VersionedIdentifier getLibraryIdentifier() {
        return library.getIdentifier();
    }

    public void setLibraryIdentifier(VersionedIdentifier vid) {
        library.setIdentifier(vid);
        compiledLibrary.setIdentifier(vid);
    }

    public void endTranslation() {
        applyTargetModelMaps();
        libraryManager.endCompilation(getLibraryName());
    }

    public boolean canResolveLibrary(IncludeDef includeDef) {
        VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.getPath()))
                .withId(NamespaceManager.getNamePart(includeDef.getPath()))
                .withVersion(includeDef.getVersion());
        return libraryManager.canResolveLibrary(libraryIdentifier);
    }

    public void addInclude(IncludeDef includeDef) {
        if (library.getIdentifier() == null || library.getIdentifier().getId() == null) {
            throw new IllegalArgumentException("Unnamed libraries cannot reference other libraries.");
        }

        if (library.getIncludes() == null) {
            library.setIncludes(of.createLibraryIncludes());
        }
        library.getIncludes().getDef().add(includeDef);

        compiledLibrary.add(includeDef);

        VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.getPath()))
                .withId(NamespaceManager.getNamePart(includeDef.getPath()))
                .withVersion(includeDef.getVersion());

        ArrayList<CqlCompilerException> errors = new ArrayList<CqlCompilerException>();
        CompiledLibrary referencedLibrary = libraryManager.resolveLibrary(libraryIdentifier, this.options, errors);
        for (CqlCompilerException error : errors) {
            this.recordParsingException(error);
        }

        // Note that translation of a referenced library may result in implicit specification of the namespace
        // In this case, the referencedLibrary will have a namespaceUri different than the currently resolved namespaceUri
        // of the IncludeDef.
        String currentNamespaceUri = NamespaceManager.getUriPart(includeDef.getPath());
        if ((currentNamespaceUri == null && libraryIdentifier.getSystem() != null)
                || (currentNamespaceUri != null && !currentNamespaceUri.equals(libraryIdentifier.getSystem()))) {
            includeDef.setPath(NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId()));
        }

        libraries.put(includeDef.getLocalIdentifier(), referencedLibrary);
        loadConversionMap(referencedLibrary);
    }

    public void addParameter(ParameterDef paramDef) {
        if (library.getParameters() == null) {
            library.setParameters(of.createLibraryParameters());
        }
        library.getParameters().getDef().add(paramDef);

        compiledLibrary.add(paramDef);
    }

    public void addCodeSystem(CodeSystemDef cs) {
        if (library.getCodeSystems() == null) {
            library.setCodeSystems(of.createLibraryCodeSystems());
        }
        library.getCodeSystems().getDef().add(cs);

        compiledLibrary.add(cs);
    }

    public void addValueSet(ValueSetDef vs) {
        if (library.getValueSets() == null) {
            library.setValueSets(of.createLibraryValueSets());
        }
        library.getValueSets().getDef().add(vs);

        compiledLibrary.add(vs);
    }

    public void addCode(CodeDef cd) {
        if (library.getCodes() == null) {
            library.setCodes(of.createLibraryCodes());
        }
        library.getCodes().getDef().add(cd);

        compiledLibrary.add(cd);
    }

    public void addConcept(ConceptDef cd) {
        if (library.getConcepts() == null) {
            library.setConcepts(of.createLibraryConcepts());
        }
        library.getConcepts().getDef().add(cd);

        compiledLibrary.add(cd);
    }

    public void addContext(ContextDef cd) {
        if (library.getContexts() == null) {
            library.setContexts(of.createLibraryContexts());
        }
        library.getContexts().getDef().add(cd);
    }

    public List<Pair<String, Object>> resolveCaseIgnored(String identifier) {
        return compiledLibrary.resolveCaseIgnored(identifier);
    }

    public void addExpression(ExpressionDef expDef) {
        if (library.getStatements() == null) {
            library.setStatements(of.createLibraryStatements());
        }
        library.getStatements().getDef().add(expDef);

        compiledLibrary.add(expDef);
    }

    public void removeExpression(ExpressionDef expDef) {
        if (library.getStatements() != null) {
            library.getStatements().getDef().remove(expDef);
            compiledLibrary.remove(expDef);
        }
    }

    public Element resolve(String identifier) {
        return compiledLibrary.resolve(identifier);
    }

    public IncludeDef resolveIncludeRef(String identifier) {
        return compiledLibrary.resolveIncludeRef(identifier);
    }

    public String resolveIncludeAlias(VersionedIdentifier libraryIdentifier) {
        return compiledLibrary.resolveIncludeAlias(libraryIdentifier);
    }

    public CodeSystemDef resolveCodeSystemRef(String identifier) {
        return compiledLibrary.resolveCodeSystemRef(identifier);
    }

    public ValueSetDef resolveValueSetRef(String identifier) {
        return compiledLibrary.resolveValueSetRef(identifier);
    }

    public CodeDef resolveCodeRef(String identifier) {
        return compiledLibrary.resolveCodeRef(identifier);
    }

    public ConceptDef resolveConceptRef(String identifier) {
        return compiledLibrary.resolveConceptRef(identifier);
    }

    public ParameterDef resolveParameterRef(String identifier) {
        checkLiteralContext();
        return compiledLibrary.resolveParameterRef(identifier);
    }

    public ExpressionDef resolveExpressionRef(String identifier) {
        checkLiteralContext();
        return compiledLibrary.resolveExpressionRef(identifier);
    }

    public Conversion findConversion(DataType fromType, DataType toType, boolean implicit, boolean allowPromotionAndDemotion) {
        return conversionMap.findConversion(fromType, toType, implicit, allowPromotionAndDemotion, compiledLibrary.getOperatorMap());
    }

    public Expression resolveUnaryCall(String libraryName, String operatorName, UnaryExpression expression) {
        return resolveCall(libraryName, operatorName, new UnaryExpressionInvocation(expression), false, false);
    }

    public Invocation resolveBinaryInvocation(String libraryName, String operatorName, BinaryExpression expression) {
        return resolveBinaryInvocation(libraryName, operatorName, expression, true, false);
    }

    public Expression resolveBinaryCall(String libraryName, String operatorName, BinaryExpression expression) {
        Invocation invocation = resolveBinaryInvocation(libraryName, operatorName, expression);
        return invocation != null ? invocation.getExpression() : null;
    }

    public Invocation resolveBinaryInvocation(String libraryName, String operatorName, BinaryExpression expression, boolean mustResolve, boolean allowPromotionAndDemotion) {
        return resolveInvocation(libraryName, operatorName, new BinaryExpressionInvocation(expression), mustResolve, allowPromotionAndDemotion, false);
    }

    public Expression resolveBinaryCall(String libraryName, String operatorName, BinaryExpression expression, boolean mustResolve, boolean allowPromotionAndDemotion) {
        Invocation invocation = resolveBinaryInvocation(libraryName, operatorName, expression, mustResolve, allowPromotionAndDemotion);
        return invocation != null ? invocation.getExpression() : null;
    }

    public Expression resolveTernaryCall(String libraryName, String operatorName, TernaryExpression expression) {
        return resolveCall(libraryName, operatorName, new TernaryExpressionInvocation(expression), false, false);
    }

    public Expression resolveNaryCall(String libraryName, String operatorName, NaryExpression expression) {
        return resolveCall(libraryName, operatorName, new NaryExpressionInvocation(expression), false, false);
    }

    public Expression resolveAggregateCall(String libraryName, String operatorName, AggregateExpression expression) {
        return resolveCall(libraryName, operatorName, new AggregateExpressionInvocation(expression), false, false);
    }

    private class BinaryWrapper {
        public Expression left;
        public Expression right;

        public BinaryWrapper(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
    }

    private BinaryWrapper normalizeListTypes(Expression left, Expression right) {
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
                    left = of.createAs().withOperand(left).withAsTypeSpecifier(dataTypeToTypeSpecifier(targetType));
                    left.setResultType(targetType);

                    right = of.createAs().withOperand(right).withAsTypeSpecifier(dataTypeToTypeSpecifier(targetType));
                    right.setResultType(targetType);
                }
            }
        }

        return new BinaryWrapper(left, right);
    }

    public Expression resolveUnion(Expression left, Expression right) {
        // Create right-leaning bushy instead of left-deep
        if (left instanceof Union) {
            Union leftUnion = (Union)left;
            Expression leftUnionLeft = leftUnion.getOperand().get(0);
            Expression leftUnionRight = leftUnion.getOperand().get(1);
            if (leftUnionLeft instanceof Union && !(leftUnionRight instanceof Union)) {
                left = leftUnionLeft;
                right = resolveUnion(leftUnionRight, right);
            }
        }

        // TODO: Take advantage of nary unions
        BinaryWrapper wrapper = normalizeListTypes(left, right);
        Union union = of.createUnion().withOperand(wrapper.left, wrapper.right);

        if (visitor != null && visitor.isAnnotationEnabled()) {
            union.setLocalId(Integer.toString(visitor.getNextLocalId()));
        }

        resolveNaryCall("System", "Union", union);
        return union;
    }

    public Expression resolveIntersect(Expression left, Expression right) {
        // Create right-leaning bushy instead of left-deep
        if (left instanceof Intersect) {
            Intersect leftIntersect = (Intersect)left;
            Expression leftIntersectLeft = leftIntersect.getOperand().get(0);
            Expression leftIntersectRight = leftIntersect.getOperand().get(1);
            if (leftIntersectLeft instanceof Intersect && !(leftIntersectRight instanceof Intersect)) {
                left = leftIntersectLeft;
                right = resolveIntersect(leftIntersectRight, right);
            }
        }

        // TODO: Take advantage of nary intersect
        BinaryWrapper wrapper = normalizeListTypes(left, right);
        Intersect intersect = of.createIntersect().withOperand(wrapper.left, wrapper.right);
        resolveNaryCall("System", "Intersect", intersect);
        return intersect;
    }

    public Expression resolveExcept(Expression left, Expression right) {
        BinaryWrapper wrapper = normalizeListTypes(left, right);
        Except except = of.createExcept().withOperand(wrapper.left, wrapper.right);
        resolveNaryCall("System", "Except", except);
        return except;
    }

    public Expression resolveIn(Expression left, Expression right) {
        if (right instanceof ValueSetRef || (isCompatibleWith("1.5") && right.getResultType().isSubTypeOf(resolveTypeName("System", "ValueSet")))) {
            if (left.getResultType() instanceof ListType) {
                AnyInValueSet anyIn = of.createAnyInValueSet()
                        .withCodes(left)
                        .withValueset(right instanceof ValueSetRef ? (ValueSetRef)right : null)
                        .withValuesetExpression(right instanceof ValueSetRef ? null : right);

                resolveCall("System", "AnyInValueSet", new AnyInValueSetInvocation(anyIn));
                return anyIn;
            }

            InValueSet in = of.createInValueSet()
                    .withCode(left)
                    .withValueset(right instanceof ValueSetRef ? (ValueSetRef)right : null)
                    .withValuesetExpression(right instanceof ValueSetRef ? null : right);
            resolveCall("System", "InValueSet", new InValueSetInvocation(in));
            return in;
        }

        if (right instanceof CodeSystemRef || (isCompatibleWith("1.5") && right.getResultType().isSubTypeOf(resolveTypeName("System", "CodeSystem")))) {
            if (left.getResultType() instanceof ListType) {
                AnyInCodeSystem anyIn = of.createAnyInCodeSystem()
                        .withCodes(left)
                        .withCodesystem(right instanceof CodeSystemRef ? (CodeSystemRef)right : null)
                        .withCodesystemExpression(right instanceof CodeSystemRef ? null : right);
                resolveCall("System", "AnyInCodeSystem", new AnyInCodeSystemInvocation(anyIn));
                return anyIn;
            }

            InCodeSystem in = of.createInCodeSystem()
                    .withCode(left)
                    .withCodesystem(right instanceof CodeSystemRef ? (CodeSystemRef)right : null)
                    .withCodesystemExpression(right instanceof CodeSystemRef ? null : right);
            resolveCall("System", "InCodeSystem", new InCodeSystemInvocation(in));
            return in;
        }

        In in = of.createIn().withOperand(left, right);
        resolveBinaryCall("System", "In", in);
        return in;
    }

    public Expression resolveIn(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        Invocation result = resolveInInvocation(left, right, dateTimePrecision);
        return result != null ? result.getExpression() : null;
    }

    public Invocation resolveInInvocation(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        In in = of.createIn().withOperand(left, right).withPrecision(dateTimePrecision);
        return resolveBinaryInvocation("System", "In", in);
    }

    public Expression resolveProperIn(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        Invocation result = resolveProperInInvocation(left, right, dateTimePrecision);
        return result != null ? result.getExpression() : null;
    }

    public Invocation resolveProperInInvocation(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        ProperIn properIn = of.createProperIn().withOperand(left, right).withPrecision(dateTimePrecision);
        return resolveBinaryInvocation("System", "ProperIn", properIn);
    }

    public Expression resolveContains(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        Invocation result = resolveContainsInvocation(left, right, dateTimePrecision);
        return result != null ? result.getExpression() : null;
    }

    public Invocation resolveContainsInvocation(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        Contains contains = of.createContains().withOperand(left, right).withPrecision(dateTimePrecision);
        return resolveBinaryInvocation("System", "Contains", contains);
    }

    public Expression resolveProperContains(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        Invocation result = resolveProperContainsInvocation(left, right, dateTimePrecision);
        return result != null ? result.getExpression() : null;
    }

    public Invocation resolveProperContainsInvocation(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        ProperContains properContains = of.createProperContains().withOperand(left, right).withPrecision(dateTimePrecision);
        return resolveBinaryInvocation("System", "ProperContains", properContains);
    }

    private Expression lowestScoringInvocation(Invocation primary, Invocation secondary) {
        if (primary != null) {
            if (secondary != null) {
                if (secondary.getResolution().getScore() < primary.getResolution().getScore()) {
                    return secondary.getExpression();
                }
            }

            return primary.getExpression();
        }

        if (secondary != null) {
            return secondary.getExpression();
        }

        return null;
    }

    public Expression resolveIncludes(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        Includes includes = of.createIncludes().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation includesInvocation = resolveBinaryInvocation("System", "Includes", includes, false, false);

        Contains contains = of.createContains().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation containsInvocation = resolveBinaryInvocation("System", "Contains", contains, false, false);

        Expression result = lowestScoringInvocation(includesInvocation, containsInvocation);
        if (result != null) {
            return result;
        }

        // Neither operator resolved, so force a resolve to throw
        return resolveBinaryCall("System", "Includes", includes);
    }

    public Expression resolveProperIncludes(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        ProperIncludes properIncludes = of.createProperIncludes().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation properIncludesInvocation = resolveBinaryInvocation("System", "ProperIncludes", properIncludes, false, false);

        ProperContains properContains = of.createProperContains().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation properContainsInvocation = resolveBinaryInvocation("System", "ProperContains", properContains, false, false);

        Expression result = lowestScoringInvocation(properIncludesInvocation, properContainsInvocation);
        if (result != null) {
            return result;
        }

        // Neither operator resolved, so force a resolve to throw
        return resolveBinaryCall("System", "ProperIncludes", properIncludes);
    }

    public Expression resolveIncludedIn(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        IncludedIn includedIn = of.createIncludedIn().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation includedInInvocation = resolveBinaryInvocation("System", "IncludedIn", includedIn, false, false);

        In in = of.createIn().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation inInvocation = resolveBinaryInvocation("System", "In", in, false, false);

        Expression result = lowestScoringInvocation(includedInInvocation, inInvocation);
        if (result != null) {
            return result;
        }

        // Neither operator resolved, so force a resolve to throw
        return resolveBinaryCall("System", "IncludedIn", includedIn);
    }

    public Expression resolveProperIncludedIn(Expression left, Expression right, DateTimePrecision dateTimePrecision) {
        ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation properIncludedInInvocation = resolveBinaryInvocation("System", "ProperIncludedIn", properIncludedIn, false, false);

        ProperIn properIn = of.createProperIn().withOperand(left, right).withPrecision(dateTimePrecision);
        Invocation properInInvocation = resolveBinaryInvocation("System", "ProperIn", properIn, false, false);

        Expression result = lowestScoringInvocation(properIncludedInInvocation, properInInvocation);
        if (result != null) {
            return result;
        }

        // Neither operator resolved, so force a resolve to throw
        return resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
    }

    public Expression resolveCall(String libraryName, String operatorName, Invocation invocation) {
        return resolveCall(libraryName, operatorName, invocation, true, false, false);
    }

    public Expression resolveCall(String libraryName, String operatorName, Invocation invocation, boolean allowPromotionAndDemotion, boolean allowFluent) {
        return resolveCall(libraryName, operatorName, invocation, true, allowPromotionAndDemotion, allowFluent);
    }

    public Expression resolveCall(String libraryName, String operatorName, Invocation invocation, boolean mustResolve, boolean allowPromotionAndDemotion, boolean allowFluent) {
        Invocation result = resolveInvocation(libraryName, operatorName, invocation, mustResolve, allowPromotionAndDemotion, allowFluent);
        return result != null ? result.getExpression() : null;
    }

    public Invocation resolveInvocation(String libraryName, String operatorName, Invocation invocation, boolean mustResolve, boolean allowPromotionAndDemotion, boolean allowFluent) {
        Iterable<Expression> operands = invocation.getOperands();
        List<DataType> dataTypes = new ArrayList<>();
        for (Expression operand : operands) {
            if (operand == null || operand.getResultType() == null) {
                throw new IllegalArgumentException(String.format("Could not determine signature for invocation of operator %s%s.",
                        libraryName == null ? "" : libraryName + ".", operatorName));
            }
            dataTypes.add(operand.getResultType());
        }

        CallContext callContext = new CallContext(libraryName, operatorName, allowPromotionAndDemotion, allowFluent, mustResolve, dataTypes.toArray(new DataType[dataTypes.size()]));
        OperatorResolution resolution = resolveCall(callContext);
        if (resolution != null || mustResolve) {
            checkOperator(callContext, resolution);

            List<Expression> convertedOperands = new ArrayList<>();
            Iterator<Expression> operandIterator = operands.iterator();
            Iterator<DataType> signatureTypes = resolution.getOperator().getSignature().getOperandTypes().iterator();
            Iterator<Conversion> conversionIterator = resolution.hasConversions() ? resolution.getConversions().iterator() : null;
            while (operandIterator.hasNext()) {
                Expression operand = operandIterator.next();
                Conversion conversion = conversionIterator != null ? conversionIterator.next() : null;
                if (conversion != null) {
                    operand = convertExpression(operand, conversion);
                }

                DataType signatureType = signatureTypes.next();
                operand = pruneChoices(operand, signatureType);

                convertedOperands.add(operand);
            }

            invocation.setOperands(convertedOperands);

            if (options.getSignatureLevel() == SignatureLevel.All || (options.getSignatureLevel() == SignatureLevel.Differing
                    && !resolution.getOperator().getSignature().equals(callContext.getSignature()))
                    || (options.getSignatureLevel() == SignatureLevel.Overloads && resolution.getOperatorHasOverloads())) {
                invocation.setSignature(dataTypesToTypeSpecifiers(resolution.getOperator().getSignature().getOperandTypes()));
            }

            invocation.setResultType(resolution.getOperator().getResultType());
            if (resolution.getLibraryIdentifier() != null) {
                resolution.setLibraryName(resolveIncludeAlias(resolution.getLibraryIdentifier()));
            }
            invocation.setResolution(resolution);
            return invocation;
        }
        return null;
    }

    private Expression pruneChoices(Expression expression, DataType targetType) {
        // TODO: In theory, we could collapse expressions that are unnecessarily broad, given the targetType (type leading)
        // This is a placeholder for where this functionality would be added in the future.
        return expression;
    }

    public OperatorResolution resolveCall(CallContext callContext) {
        OperatorResolution result = null;
        if (callContext.getLibraryName() == null || callContext.getLibraryName().equals("")) {
            result = compiledLibrary.resolveCall(callContext, conversionMap);
            if (result == null) {
                result = getSystemLibrary().resolveCall(callContext, conversionMap);
                if (result == null && callContext.getAllowFluent()) {
                    // attempt to resolve in each non-system included library, in order of inclusion, first resolution wins
                    for (CompiledLibrary library : libraries.values()) {
                        if (!library.equals(getSystemLibrary())) {
                            result = library.resolveCall(callContext, conversionMap);
                            if (result != null) {
                                break;
                            }
                        }
                    }
                }
                /*
                // Implicit resolution is only allowed for the system library functions.
                for (CompiledLibrary library : libraries.values()) {
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
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve call to operator %s with signature %s.",
                    callContext.getOperatorName(), callContext.getSignature()));
        }

        if (resolution.getOperator().getFluent() && !callContext.getAllowFluent()) {
            throw new IllegalArgumentException(String.format("Operator %s with signature %s is a fluent function and can only be invoked with fluent syntax.",
                    callContext.getOperatorName(), callContext.getSignature()));
        }

        if (callContext.getAllowFluent() && !resolution.getOperator().getFluent() && !resolution.getAllowFluent()) {
            throw new IllegalArgumentException(String.format("Invocation of operator %s with signature %s uses fluent syntax, but the operator is not defined as a fluent function.",
                    callContext.getOperatorName(), callContext.getSignature()));
        }
    }

    public void checkAccessLevel(String libraryName, String objectName, AccessModifier accessModifier) {
        if (accessModifier == AccessModifier.PRIVATE) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Object %s in library %s is marked private and cannot be referenced from another library.", objectName, libraryName));
        }
    }

    public Expression resolveFunction(String libraryName, String functionName, Iterable<Expression> paramList) {
        return resolveFunction(libraryName, functionName, paramList, true, false, false);
    }

    private FunctionRef buildFunctionRef(String libraryName, String functionName, Iterable<Expression> paramList) {
        FunctionRef fun = of.createFunctionRef()
                .withLibraryName(libraryName)
                .withName(functionName);

        for (Expression param : paramList) {
            fun.getOperand().add(param);
        }

        return fun;
    }

    public Expression resolveFunction(String libraryName, String functionName, Iterable<Expression> paramList, boolean mustResolve, boolean allowPromotionAndDemotion, boolean allowFluent) {
        FunctionRef fun = buildFunctionRef(libraryName, functionName, paramList);

        // First attempt to resolve as a system or local function
        FunctionRefInvocation invocation = new FunctionRefInvocation(fun);
        fun = (FunctionRef)resolveCall(fun.getLibraryName(), fun.getName(), invocation, mustResolve, allowPromotionAndDemotion, allowFluent);
        if (fun != null) {
            if ("System".equals(invocation.getResolution().getOperator().getLibraryName())) {
                FunctionRef systemFun = buildFunctionRef(libraryName, functionName, paramList); // Rebuild the fun from the original arguments, otherwise it will resolve with conversions in place
                Expression systemFunction = systemFunctionResolver.resolveSystemFunction(systemFun);
                if (systemFunction != null) {
                    return systemFunction;
                }
            }
            else {
                // If the invocation is to a local function or a function in a non-system library, check literal context
                if (mustResolve) {
                    checkLiteralContext();
                }
            }
        }

        // If it didn't resolve, there are two possibilities
        // 1. It is a special system function resolution that only resolves with the systemFunctionResolver
        // 2. It is an error condition that needs to be reported
        if (fun == null) {
            fun = buildFunctionRef(libraryName, functionName, paramList);

            if (!allowFluent) {
                // Only attempt to resolve as a system function if this is not a fluent call or it is a required resolution
                Expression systemFunction = systemFunctionResolver.resolveSystemFunction(fun);
                if (systemFunction != null) {
                    return systemFunction;
                }

                checkLiteralContext();
            }

            fun = (FunctionRef)resolveCall(fun.getLibraryName(), fun.getName(), new FunctionRefInvocation(fun), mustResolve, allowPromotionAndDemotion, allowFluent);
        }

        return fun;
    }

    public void verifyComparable(DataType dataType) {
        Expression left = (Expression)of.createLiteral().withResultType(dataType);
        Expression right = (Expression)of.createLiteral().withResultType(dataType);
        BinaryExpression comparison = of.createLess().withOperand(left, right);
        resolveBinaryCall("System", "Less", comparison);
    }

    public Expression convertExpression(Expression expression, DataType targetType) {
        return convertExpression(expression, targetType, true);
    }

    public Expression convertExpression(Expression expression, DataType targetType, boolean implicit) {
        Conversion conversion = findConversion(expression.getResultType(), targetType, implicit, false);
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
        CqlSemanticException warning = new CqlSemanticException(message, CqlCompilerException.ErrorSeverity.Warning, trackback);
        recordParsingException(warning);
    }

    private Expression demoteListExpression(Expression expression, Conversion conversion) {
        ListType fromType = (ListType)conversion.getFromType();
        DataType toType = conversion.getToType();

        SingletonFrom singletonFrom = of.createSingletonFrom().withOperand(expression);
        singletonFrom.setResultType(fromType.getElementType());
        resolveUnaryCall("System", "SingletonFrom", singletonFrom);
        // WARNING:
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
            // WARNING:
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

    private Expression demoteIntervalExpression(Expression expression, Conversion conversion) {
        IntervalType fromType = (IntervalType)conversion.getFromType();
        DataType toType = conversion.getToType();

        PointFrom pointFrom = of.createPointFrom().withOperand(expression);
        pointFrom.setResultType(fromType.getPointType());
        resolveUnaryCall("System", "PointFrom", pointFrom);
        // WARNING:
        reportWarning("Interval-valued expression was demoted to a point.", expression);

        if (conversion.getConversion() != null) {
            return convertExpression(pointFrom, conversion.getConversion());
        }
        else {
            return pointFrom;
        }
    }

    private Expression promoteIntervalExpression(Expression expression, Conversion conversion) {
        if (conversion.getConversion() != null) {
            expression = convertExpression(expression, conversion.getConversion());
        }

        return resolveToInterval(expression);
    }

    // When promoting a point to an interval, if the point is null, the result is null, rather than constructing an interval
    // with null boundaries
    public Expression resolveToInterval(Expression expression) {
        If condition = of.createIf();
        condition.setCondition(buildIsNull(expression));
        condition.setThen(buildNull(new IntervalType(expression.getResultType())));
        Interval toInterval = of.createInterval().withLow(expression).withHigh(expression).withLowClosed(true).withHighClosed(true);
        toInterval.setResultType(new IntervalType(expression.getResultType()));
        condition.setElse(toInterval);
        condition.setResultType(resolveTypeName("System", "Boolean"));
        return condition;
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

    public IsNull buildIsNull(Expression expression) {
        IsNull isNull = of.createIsNull().withOperand(expression);
        isNull.setResultType(resolveTypeName("System", "Boolean"));
        return isNull;
    }

    public Not buildIsNotNull(Expression expression) {
        IsNull isNull = buildIsNull(expression);
        Not not = of.createNot().withOperand(isNull);
        not.setResultType(resolveTypeName("System", "Boolean"));
        return not;
    }

    public MinValue buildMinimum(DataType dataType) {
        MinValue minimum = of.createMinValue();
        minimum.setValueType(dataTypeToQName(dataType));
        minimum.setResultType(dataType);
        return minimum;
    }

    public MaxValue buildMaximum(DataType dataType) {
        MaxValue maximum = of.createMaxValue();
        maximum.setValueType(dataTypeToQName(dataType));
        maximum.setResultType(dataType);
        return maximum;
    }

    public Expression buildPredecessor(Expression source) {
        Predecessor result = of.createPredecessor().withOperand(source);
        resolveUnaryCall("System", "Predecessor", result);
        return result;
    }

    public Expression buildSuccessor(Expression source) {
        Successor result = of.createSuccessor().withOperand(source);
        resolveUnaryCall("System", "Successor", result);
        return result;
    }

    public Expression convertExpression(Expression expression, Conversion conversion) {
        if (conversion.isCast()
                && (conversion.getFromType().isSuperTypeOf(conversion.getToType())
                || conversion.getFromType().isCompatibleWith(conversion.getToType()))) {
            if (conversion.getFromType() instanceof ChoiceType && conversion.getToType() instanceof ChoiceType) {
                if (((ChoiceType)conversion.getFromType()).isSubSetOf((ChoiceType)conversion.getToType())) {
                    // conversion between compatible choice types requires no cast (i.e. choice<Integer, String> can be safely passed to choice<Integer, String, DateTime>
                    return expression;
                }
                // Otherwise, the choice is narrowing and a run-time As is required (to use only the expected target types)
            }
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
        else if (conversion.isIntervalDemotion()) {
            return demoteIntervalExpression(expression, conversion);
        }
        else if (conversion.isIntervalPromotion()) {
            return promoteIntervalExpression(expression, conversion);
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

            resolveCall(functionRef.getLibraryName(), functionRef.getName(), new FunctionRefInvocation(functionRef), false, false);

            return functionRef;
        }
        else {
            if (conversion.getToType().equals(resolveTypeName("System", "Boolean"))) {
                return (Expression)of.createToBoolean().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Integer"))) {
                return (Expression)of.createToInteger().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Long"))) {
                return (Expression)of.createToLong().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Decimal"))) {
                return (Expression)of.createToDecimal().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "String"))) {
                return (Expression)of.createToString().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Date"))) {
                return (Expression)of.createToDate().withOperand(expression).withResultType(conversion.getToType());
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
            else if (conversion.getToType().equals(resolveTypeName("System", "Ratio"))) {
                return (Expression)of.createToRatio().withOperand(expression).withResultType(conversion.getToType());
            }
            else if (conversion.getToType().equals(resolveTypeName("System", "Concept"))) {
                return (Expression)of.createToConcept().withOperand(expression).withResultType(conversion.getToType());
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

        Conversion conversion = findConversion(actualType, expectedType, true, false);
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

        // If either side is a choice type, don't allow conversions because they will incorrectly eliminate choices based on convertibility
        if (!(first instanceof ChoiceType || second instanceof ChoiceType)) {
            Conversion conversion = findConversion(second, first, true, false);
            if (conversion != null) {
                return first;
            }

            conversion = findConversion(first, second, true, false);
            if (conversion != null) {
                return second;
            }
        }

        return null;
    }

    public DataType ensureCompatibleTypes(DataType first, DataType second) {
        DataType compatibleType = findCompatibleType(first, second);
        if (compatibleType != null) {
            return compatibleType;
        }

        if (!second.isSubTypeOf(first)) {
            return new ChoiceType(Arrays.asList(first, second));
        }

        // The above construction of a choice type guarantees this will never be hit
        DataTypes.verifyType(second, first);
        return first;
    }

    public Expression ensureCompatible(Expression expression, DataType targetType) {
        if (targetType == null) {
            return of.createNull();
        }

        if (!targetType.isSuperTypeOf(expression.getResultType())) {
            return convertExpression(expression, targetType, true);
        }

        return expression;
    }

    public Expression enforceCompatible(Expression expression, DataType targetType) {
        if (targetType == null) {
            return of.createNull();
        }

        if (!targetType.isSuperTypeOf(expression.getResultType())) {
            return convertExpression(expression, targetType, false);
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

    public Literal createLongNumberLiteral(String value) {
        DataType resultType = resolveTypeName("System", "Long");
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
                validateUcumUnit(unit);
                break;
        }
    }

    public String ensureUcumUnit(String unit) {
        switch (unit) {
            case "year":
            case "years":
                return "a";
            case "month":
            case "months":
                return "mo";
            case "week":
            case "weeks":
                return "wk";
            case "day":
            case "days":
                return "d";
            case "hour":
            case "hours":
                return "h";
            case "minute":
            case "minutes":
                return "min";
            case "second":
            case "seconds":
                return "s";
            case "millisecond":
            case "milliseconds":
                return "ms";

            default:
                validateUcumUnit(unit);
                break;
        }

        return unit;
    }

    private void validateUcumUnit(String unit) {
        if (ucumService != null) {
            String message = ucumService.validate(unit);
            if (message != null) {
                // ERROR:
                throw new IllegalArgumentException(message);
            }
        }
    }

    public Quantity createQuantity(BigDecimal value, String unit) {
        validateUnit(unit);
        Quantity result = of.createQuantity().withValue(value).withUnit(unit);
        DataType resultType = resolveTypeName("System", "Quantity");
        result.setResultType(resultType);
        return result;
    }

    public Ratio createRatio(Quantity numerator, Quantity denominator) {
        Ratio result = of.createRatio().withNumerator(numerator).withDenominator(denominator);
        DataType resultType = resolveTypeName("System", "Ratio");
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
        return typeBuilder.dataTypeToQName(type);
    }

    public Iterable<TypeSpecifier> dataTypesToTypeSpecifiers(Iterable<DataType> types) {
        return typeBuilder.dataTypesToTypeSpecifiers(types);
    }

    public TypeSpecifier dataTypeToTypeSpecifier(DataType type) {
        return typeBuilder.dataTypeToTypeSpecifier(type);
    }

    public DataType resolvePath(DataType sourceType, String path) {
        // TODO: This is using a naive implementation for now... needs full path support (but not full FluentPath support...)
        String[] identifiers = path.split("\\.");
        for (int i = 0; i < identifiers.length; i++) {
            PropertyResolution resolution = resolveProperty(sourceType, identifiers[i]);
            sourceType = resolution.getType();
            // Actually, this doesn't matter for this call, we're just resolving the type...
            //if (!resolution.getTargetMap().equals(identifiers[i])) {
            //    throw new IllegalArgumentException(String.format("Identifier %s references an element with a target mapping defined and cannot be resolved as part of a path", identifiers[i]));
            //}
        }

        return sourceType;
    }

    public PropertyResolution resolveProperty(DataType sourceType, String identifier) {
        return resolveProperty(sourceType, identifier, true);
    }

    // TODO: Support case-insensitive models
    public PropertyResolution resolveProperty(DataType sourceType, String identifier, boolean mustResolve) {
        DataType currentType = sourceType;
        while (currentType != null) {
            if (currentType instanceof ClassType) {
                ClassType classType = (ClassType)currentType;
                if (identifier.startsWith("?") && isCompatibleWith("1.5")) {
                    String searchPath = identifier.substring(1);
                    for (SearchType s : classType.getSearches()) {
                        if (s.getName().equals(searchPath)) {
                            return new PropertyResolution(s);
                        }
                    }
                }
                else {
                    for (ClassTypeElement e : classType.getElements()) {
                        if (e.getName().equals(identifier)) {
                            if (e.isProhibited()) {
                                throw new IllegalArgumentException(String.format("Element %s cannot be referenced because it is marked prohibited in type %s.", e.getName(), ((ClassType) currentType).getName()));
                            }

                            return new PropertyResolution(e);
                        }
                    }
                }
            }
            else if (currentType instanceof TupleType) {
                TupleType tupleType = (TupleType)currentType;
                for (TupleTypeElement e : tupleType.getElements()) {
                    if (e.getName().equals(identifier)) {
                        return new PropertyResolution(e);
                    }
                }
            }
            else if (currentType instanceof IntervalType) {
                IntervalType intervalType = (IntervalType)currentType;
                switch (identifier) {
                    case "low":
                    case "high":
                        return new PropertyResolution(intervalType.getPointType(), identifier);
                    case "lowClosed":
                    case "highClosed":
                        return new PropertyResolution(resolveTypeName("System", "Boolean"), identifier);
                    default:
                        // ERROR:
                        throw new IllegalArgumentException(String.format("Invalid interval property name %s.", identifier));
                }
            }
            else if (currentType instanceof ChoiceType) {
                ChoiceType choiceType = (ChoiceType)currentType;
                // TODO: Issue a warning if the property does not resolve against every type in the choice

                // Resolve the property against each type in the choice
                Set<DataType> resultTypes = new HashSet<>();
                Map<DataType, String> resultTargetMaps = new HashMap<DataType, String>();
                String name = null;
                for (DataType choice : choiceType.getTypes()) {
                    PropertyResolution resolution = resolveProperty(choice, identifier, false);
                    if (resolution != null) {
                        resultTypes.add(resolution.getType());
                        if (resolution.getTargetMap() != null) {
                            if (resultTargetMaps.containsKey(resolution.getType())) {
                                if (!resultTargetMaps.get(resolution.getType()).equals(resolution.getTargetMap())) {
                                    throw new IllegalArgumentException(String.format("Inconsistent target maps %s and %s for choice type %s",
                                            resultTargetMaps.get(resolution.getType()), resolution.getTargetMap(), resolution.getType().toString()));
                                }
                            }
                            else {
                                resultTargetMaps.put(resolution.getType(), resolution.getTargetMap());
                            }
                        }

                        if (name == null) {
                            name = resolution.getName();
                        }
                        else if (!name.equals(resolution.getName())) {
                            throw new IllegalArgumentException(String.format("Inconsistent property resolution for choice type %s (was %s, is %s)",
                                    choice.toString(), name, resolution.getName()));
                        }

                        if (name == null) {
                            name = resolution.getName();
                        }
                        else if (!name.equals(resolution.getName())) {
                            throw new IllegalArgumentException(String.format("Inconsistent property resolution for choice type %s (was %s, is %s)",
                                    choice.toString(), name, resolution.getName()));
                        }
                    }
                }

                // The result type is a choice of all the resolved types
                if (resultTypes.size() > 1) {
                    return new PropertyResolution(new ChoiceType(resultTypes), name, resultTargetMaps);
                }

                if (resultTypes.size() == 1) {
                    for (DataType resultType : resultTypes) {
                        return new PropertyResolution(resultType, resultTargetMaps.containsKey(resultType) ? resultTargetMaps.get(resultType) : name);
                    }
                }
            }
            else if (currentType instanceof ListType && listTraversal) {
                // NOTE: FHIRPath path traversal support
                // Resolve property as a list of items of property of the element type
                ListType listType = (ListType)currentType;
                PropertyResolution resolution = resolveProperty(listType.getElementType(), identifier);
                return new PropertyResolution(new ListType(resolution.getType()), resolution.getTargetMap());
            }

            if (currentType.getBaseType() != null) {
                currentType = currentType.getBaseType();
            }
            else {
                break;
            }
        }

        if (mustResolve) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Member %s not found for type %s.", identifier, sourceType != null ? sourceType.toLabel() : null));
        }

        return null;
    }


    // TODO: Support case-insensitive models
    public List<IdentifierResolution> resolveProperties(DataType sourceType, String identifier, boolean mustResolve) {
        List<IdentifierResolution> matchList = new ArrayList<>();

        DataType currentType = sourceType;
        while (currentType != null) {
            if (currentType instanceof ClassType) {
                ClassType classType = (ClassType)currentType;
                if (identifier.startsWith("?") && isCompatibleWith(COMPATIBILITY_LEVEL_1_5)) {
                    String searchPath = identifier.substring(1);
                    for (SearchType s : classType.getSearches()) {
                        matchList.add(IdentifierResolution.getMatchType(identifier, s.getName(), searchPath, new PropertyResolution(s)));
                    }
                }
                else {
                    for (ClassTypeElement e : classType.getElements()) {
                        IdentifierResolution mi = IdentifierResolution.getMatchType(identifier, e.getName(), identifier, new PropertyResolution(e));
                        if (mi.getMatchType() != MatchType.NONE){
                            if (e.isProhibited()) {
                                throw new IllegalArgumentException(String.format("Element %s cannot be referenced because it is marked prohibited in type %s.", e.getName(), ((ClassType) currentType).getName()));
                            }
                            matchList.add(mi);
                        }
                    }
                }
            }
            else if (currentType instanceof TupleType) {
                TupleType tupleType = (TupleType)currentType;
                for (TupleTypeElement e : tupleType.getElements()) {
                    matchList.add(IdentifierResolution.getMatchType(identifier, e.getName(), identifier, new PropertyResolution(e)));
                }
            }
            else if (currentType instanceof IntervalType) {
                IntervalType intervalType = (IntervalType)currentType;

                IdentifierResolution high = IdentifierResolution.getMatchType(identifier, identifier, HIGH, new PropertyResolution(intervalType.getPointType(), identifier));
                IdentifierResolution highClosed = IdentifierResolution.getMatchType(identifier, identifier, HIGH_CLOSED, new PropertyResolution(resolveTypeName("System", "Boolean"), identifier));

                if (high.getMatchType().equals(MatchType.NONE) && highClosed.getMatchType().equals(MatchType.NONE)){
                    throw new IllegalArgumentException(String.format("Invalid interval property name %s.", identifier));
                }
                matchList.add(high);
                matchList.add(highClosed);

            }
            else if (currentType instanceof ChoiceType) {
                ChoiceType choiceType = (ChoiceType)currentType;
                // TODO: Issue a warning if the property does not resolve against every type in the choice

                // Resolve the property against each type in the choice
                Set<DataType> resultTypes = new HashSet<>();
                Map<DataType, String> resultTargetMaps = new HashMap<DataType, String>();
                String name = null;
                for (DataType choice : choiceType.getTypes()) {
                    PropertyResolution resolution = resolveProperty(choice, identifier, false);
                    if (resolution != null) {
                        resultTypes.add(resolution.getType());
                        if (resolution.getTargetMap() != null) {
                            if (resultTargetMaps.containsKey(resolution.getType())) {
                                if (!resultTargetMaps.get(resolution.getType()).equals(resolution.getTargetMap())) {
                                    throw new IllegalArgumentException(String.format("Inconsistent target maps %s and %s for choice type %s",
                                            resultTargetMaps.get(resolution.getType()), resolution.getTargetMap(), resolution.getType().toString()));
                                }
                            }
                            else {
                                resultTargetMaps.put(resolution.getType(), resolution.getTargetMap());
                            }
                        }

                        if (name == null) {
                            name = resolution.getName();
                        }
                        else if (!name.equals(resolution.getName())) {
                            throw new IllegalArgumentException(String.format("Inconsistent property resolution for choice type %s (was %s, is %s)",
                                    choice.toString(), name, resolution.getName()));
                        }

                        if (name == null) {
                            name = resolution.getName();
                        }
                        else if (!name.equals(resolution.getName())) {
                            throw new IllegalArgumentException(String.format("Inconsistent property resolution for choice type %s (was %s, is %s)",
                                    choice.toString(), name, resolution.getName()));
                        }
                    }
                }

                // The result type is a choice of all the resolved types
                if (resultTypes.size() > 1) {
                    matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, new PropertyResolution(new ChoiceType(resultTypes), name, resultTargetMaps)));

                }

                if (resultTypes.size() == 1) {
                    for (DataType resultType : resultTypes) {
                        matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, new PropertyResolution(resultType, resultTargetMaps.containsKey(resultType) ? resultTargetMaps.get(resultType) : name)));
                    }
                }
            }
            else if (currentType instanceof ListType && listTraversal) {
                // NOTE: FHIRPath path traversal support
                // Resolve property as a list of items of property of the element type
                ListType listType = (ListType)currentType;
                PropertyResolution resolution = resolveProperty(listType.getElementType(), identifier);

                matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, new PropertyResolution(new ListType(resolution.getType()), resolution.getTargetMap())));
            }

            if (currentType.getBaseType() != null) {
                currentType = currentType.getBaseType();
            }
            else {
                break;
            }
        }

        if (mustResolve && IdentifierResolution.getFirstCaseMatch(matchList) == null) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Member %s not found for type %s.", identifier, sourceType != null ? sourceType.toLabel() : null));
        }

        return matchList;
    }

    /**
     * resolveIdentifier serves the function of both attempting to resolve an identifier as well as
     * inform the user when various scenarios occur such as no resolution, multiple resolutions, or a
     * case mis-matched resolution occurred.
     *
     * @param identifier  String value of identifier to be resolved
     * @param mustResolve boolean determining whether IllegalArgumentException should be thrown if no match made
     * @return
     */
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
        // 10: The name of a property on a specific context
        // 11: An unresolved identifier error is thrown

        // In a type specifier context, return the identifier as a Literal for resolution as a type by the caller

        List<IdentifierResolution> matchList = new ArrayList<>();

        if (inTypeSpecifierContext()) {
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, this.createLiteral(identifier)));
        }

        matchList.addAll(resolveQueryResultElements(identifier));

        // In the case of a $this alias, names may be resolved as implicit property references
        matchList.addAll(resolveQueryThisElements(identifier));

        if (MatchType.checkMatch(identifier, $_INDEX) != MatchType.NONE) {
            Iteration result = of.createIteration();
            result.setResultType(resolveTypeName(SYSTEM, INTEGER));
            matchList.add(IdentifierResolution.getMatchType(identifier, identifier, $_INDEX, result));
        }

        if (MatchType.checkMatch(identifier, $_TOTAL) != MatchType.NONE) {
            Total result = of.createTotal();
            result.setResultType(resolveTypeName(SYSTEM, DECIMAL)); // TODO: This isn't right, but we don't set up a query for the Aggregate operator right now...
            matchList.add(IdentifierResolution.getMatchType(identifier, identifier, $_TOTAL, result));
        }

        List<IdentifierResolution> aliasMatches = resolveAliases(identifier);
        IdentifierResolution aliasFirstCaseMatch = IdentifierResolution.getFirstCaseMatch(aliasMatches);
        if (aliasFirstCaseMatch != null) {
            AliasRef result = of.createAliasRef().withName(identifier);
            AliasedQuerySource aqs = (AliasedQuerySource) aliasFirstCaseMatch.getResolvedElement();
            if (aqs.getResultType() instanceof ListType) {
                result.setResultType(((ListType) aqs.getResultType()).getElementType());
            } else {
                result.setResultType(aqs.getResultType());
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, result));
        }
        matchList.addAll(aliasMatches);


        List<IdentifierResolution> letsMatches = resolveQueryLets(identifier);
        IdentifierResolution letsFirstCaseMatch = IdentifierResolution.getFirstCaseMatch(letsMatches);
        if (letsFirstCaseMatch != null) {
            QueryLetRef result = of.createQueryLetRef().withName(identifier);
            LetClause let = (LetClause) letsFirstCaseMatch.getResolvedElement();
            result.setResultType(let.getResultType());
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, result));
        }
        matchList.addAll(letsMatches);

        List<IdentifierResolution> operandRefMatches = resolveOperandRefs(identifier);
        IdentifierResolution operandFirstCaseMatch = IdentifierResolution.getFirstCaseMatch(operandRefMatches);
        if (operandFirstCaseMatch != null) {
            OperandRef operandRef = (OperandRef) operandFirstCaseMatch.getResolvedElement();
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, operandRef));
        }
        matchList.addAll(operandRefMatches);


        List<IdentifierResolution> resolvedElementsMatches = resolveElements(identifier);
        IdentifierResolution resolvedElementsFirstCaseMatch = IdentifierResolution.getFirstCaseMatch(resolvedElementsMatches);
        if (resolvedElementsFirstCaseMatch != null) {
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, resolvedElementsFirstCaseMatch.getResolvedElement()));
        }
        matchList.addAll(resolvedElementsMatches);

        // If no other resolution occurs, and we are in a specific context, and there is a parameter with the same name as the context,
        // the identifier may be resolved as an implicit property reference on that context.
        if (!inLiteralContext() && inSpecificContext()) {
            Element contextElement = resolve(currentExpressionContext());
            if (contextElement instanceof ParameterDef) {
                ParameterDef contextParameter = (ParameterDef) contextElement;

                checkLiteralContext();
                ParameterRef parameterRef = of.createParameterRef().withName(contextParameter.getName());
                parameterRef.setResultType(contextParameter.getResultType());
                if (parameterRef.getResultType() == null) {
                    // ERROR:
                    throw new IllegalArgumentException(String.format(COULD_NOT_VALIDATE_REFERENCE_TO_PARAMETER_S_BECAUSE_ITS_DEFINITION_CONTAINS_ERRORS,
                            parameterRef.getName()));
                }

                PropertyResolution resolution = resolveProperty(parameterRef.getResultType(), identifier, false);
                if (resolution != null) {
                    Expression contextAccessor = buildProperty(parameterRef, resolution.getName(), resolution.isSearch(), resolution.getType());
                    contextAccessor = applyTargetMap(contextAccessor, resolution.getTargetMap());
                    matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, contextAccessor));
                }
            }
        }

        IdentifierResolution firstCaseMatch = IdentifierResolution.getFirstCaseMatchExpression(matchList);
        if (firstCaseMatch != null) {

            List<IdentifierResolution> allHiddenCaseMatches = IdentifierResolution.getAllMatches(matchList, MatchType.EXACT);
            //remove first element to filter out our "first come first serve" rule in deciding a match:
            allHiddenCaseMatches.remove(0);

//            List<IdentifierResolution> allCaseIgnoredMatches = IdentifierResolution.getAllMatches(matchList, MatchType.CASE_IGNORED);
//            List<MatchIdentifier> allSoundsLikeMatches = MatchIdentifier.getAllMatches(matchList, Match.SOUNDS_LIKE);

            //issue warning that multiple matches occurred:
            if (allHiddenCaseMatches.size() >= 1) {
                this.reportWarning("Identifier hiding detected: " +
                                "Identifier" + (allHiddenCaseMatches.size() > 1 ? "s" : "") + " in a broader scope hidden: " +
                                this.formatMatchedMessage(allHiddenCaseMatches),
                        (Expression) firstCaseMatch.getResolvedElement());
            }
            //return first match:
            return (Expression) firstCaseMatch.getResolvedElement();
        } else if (mustResolve) {
            // ERROR:
            throw new IllegalArgumentException(String.format(COULD_NOT_RESOLVE_IDENTIFIER_S_IN_THE_CURRENT_LIBRARY, identifier));
        }

        return null;

    }

    private String lookupElementWarning(Object element) {
        if (element instanceof ExpressionDef) {
            return S_RESOLVED_AS_AN_EXPRESSION_DEFINITION;
        }
        else if (element instanceof ParameterDef) {
            return S_RESOLVED_AS_A_PARAMETER;
        }
        else if (element instanceof ValueSetDef) {
            return S_RESOLVED_AS_A_VALUE_SET;
        }
        else if (element instanceof CodeSystemDef) {
            return S_RESOLVED_AS_A_CODE_SYSTEM;
        }
        else if (element instanceof CodeDef) {
            return S_RESOLVED_AS_A_CODE;
        }
        else if (element instanceof ConceptDef) {
            return S_RESOLVED_AS_A_CONCEPT;
        }
        else if (element instanceof IncludeDef) {
            return S_RESOLVED_AS_A_LIBRARY;
        }
        else if (element instanceof IdentifierRef) {
            return S_RESOLVED_AS_AN_ELEMENT_OF_THE_RESULT_OF_A_QUERY;
        }
        else if (element instanceof Iteration) {
            return S_RESOLVED_AS_THE_INDEX_ITERATION_ACCESSOR;
        }
        else if (element instanceof Total) {
            return S_RESOLVED_AS_THE_TOTAL_AGGREGATION_ACCESSOR;
        }
        else if (element instanceof AliasRef) {
            return S_RESOLVED_AS_AN_ALIAS_OF_A_QUERY;
        }
        else if (element instanceof QueryLetRef ) {
            return S_RESOLVED_AS_A_LET_OF_A_QUERY;
        }
        else if (element instanceof OperandRef ) {
            return S_RESOLVED_AS_AN_OPERAND_TO_A_FUNCTION;
        }
        else if (element instanceof Literal) {
            return S_RESOLVED_AS_A_POTENTIAL_TYPE_NAME;
        }
        else if (element instanceof Expression ) {
            return S_RESOLVED_AS_A_CONTEXT_ACCESSOR;
        }
        //default message if no match is made:
        return S_RESOLVED_MORE_THAN_ONCE + element.getClass();
    }

    private List<IdentifierResolution> resolveElements(String identifier) {

        List<IdentifierResolution> matchList = new ArrayList<>();


        Element element = resolve(identifier);
        List<Pair<String, Object>> caseIgnoredElements = resolveCaseIgnored(identifier);

        if (element instanceof ExpressionDef) {
            checkLiteralContext();
            ExpressionRef expressionRef = of.createExpressionRef().withName(((ExpressionDef) element).getName());
            expressionRef.setResultType(getExpressionDefResultType((ExpressionDef)element));
            if (expressionRef.getResultType() == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not validate reference to expression %s because its definition contains errors.",
                        expressionRef.getName()));
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, expressionRef));

            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));
        }

        if (element instanceof ParameterDef) {
            checkLiteralContext();
            ParameterRef parameterRef = of.createParameterRef().withName(((ParameterDef) element).getName());
            parameterRef.setResultType(element.getResultType());
            if (parameterRef.getResultType() == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not validate reference to parameter %s because its definition contains errors.",
                        parameterRef.getName()));
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, parameterRef));

            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));

        }

        if (element instanceof ValueSetDef) {
            checkLiteralContext();
            ValueSetRef valuesetRef = of.createValueSetRef().withName(((ValueSetDef) element).getName());
            valuesetRef.setResultType(element.getResultType());
            if (valuesetRef.getResultType() == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not validate reference to valueset %s because its definition contains errors.",
                        valuesetRef.getName()));
            }
            if (isCompatibleWith("1.5")) {
                valuesetRef.setPreserve(true);
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, valuesetRef));

            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));

        }

        if (element instanceof CodeSystemDef) {
            checkLiteralContext();
            CodeSystemRef codesystemRef = of.createCodeSystemRef().withName(((CodeSystemDef) element).getName());
            codesystemRef.setResultType(element.getResultType());
            if (codesystemRef.getResultType() == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not validate reference to codesystem %s because its definition contains errors.",
                        codesystemRef.getName()));
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, codesystemRef));

            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));


        }

        if (element instanceof CodeDef) {
            checkLiteralContext();
            CodeRef codeRef = of.createCodeRef().withName(((CodeDef)element).getName());
            codeRef.setResultType(element.getResultType());
            if (codeRef.getResultType() == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not validate reference to code %s because its definition contains errors.",
                        codeRef.getName()));
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, codeRef));

            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));

        }

        if (element instanceof ConceptDef) {
            checkLiteralContext();
            ConceptRef conceptRef = of.createConceptRef().withName(((ConceptDef)element).getName());
            conceptRef.setResultType(element.getResultType());
            if (conceptRef.getResultType() == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not validate reference to concept %s because its definition contains error.",
                        conceptRef.getName()));
            }
            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, conceptRef));

            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));

        }

        if (element instanceof IncludeDef) {
            checkLiteralContext();
            LibraryRef libraryRef = new LibraryRef();
            libraryRef.setLibraryName(((IncludeDef) element).getLocalIdentifier());

            matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, libraryRef));
            matchList.addAll(IdentifierResolution.miListFromPairList(caseIgnoredElements, MatchType.CASE_IGNORED));

        }

        return matchList;
    }

    private String formatPairedMessage(List<Pair<String, Object>> list) {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, Object> p : list){
            sb.append(String.format(lookupElementWarning(p.getRight()), p.getLeft()) + "\n");
        }
        return sb.toString();
    }

    private String formatMatchedMessage(List<IdentifierResolution> list) {
        StringBuilder sb = new StringBuilder();

        for (IdentifierResolution p : list){
            sb.append(String.format(lookupElementWarning(p.getResolvedElement()), p.getIdentifier()) + "\n");
        }
        return sb.toString();
    }

    /**
     * An implicit context is one where the context has the same name as a parameter. Implicit contexts are used to
     * allow FHIRPath expressions to resolve on the implicit context of the expression
     *
     * For example, in a Patient context, with a parameter of type Patient, the expression `birthDate` resolves to a property reference.
     * @return A reference to the parameter providing the implicit context value
     */
    public ParameterRef resolveImplicitContext() {
        if (!inLiteralContext() && inSpecificContext()) {
            Element contextElement = resolve(currentExpressionContext());
            if (contextElement instanceof ParameterDef) {
                ParameterDef contextParameter = (ParameterDef)contextElement;

                checkLiteralContext();
                ParameterRef parameterRef = of.createParameterRef().withName(contextParameter.getName());
                parameterRef.setResultType(contextParameter.getResultType());
                if (parameterRef.getResultType() == null) {
                    // ERROR:
                    throw new IllegalArgumentException(String.format("Could not validate reference to parameter %s because its definition contains errors.",
                            parameterRef.getName()));
                }
                return parameterRef;
            }
        }

        return null;
    }

    public Property buildProperty(String scope, String path, boolean isSearch, DataType resultType) {
        if (isSearch) {
            Search result = of.createSearch()
                    .withScope(scope)
                    .withPath(path);
            result.setResultType(resultType);
            return result;
        }
        else {
            Property result = of.createProperty()
                    .withScope(scope)
                    .withPath(path);
            result.setResultType(resultType);
            return result;
        }
    }

    public Property buildProperty(Expression source, String path, boolean isSearch, DataType resultType) {
        if (isSearch) {
            Search result = of.createSearch().withSource(source).withPath(path);
            result.setResultType(resultType);
            return result;
        }
        else {
            Property result = of.createProperty().withSource(source).withPath(path);
            result.setResultType(resultType);
            return result;
        }
    }

    private VersionedIdentifier getModelMapping(Expression sourceContext) {
        VersionedIdentifier result = null;
        if (library.getUsings() != null && library.getUsings().getDef() != null) {
            for (UsingDef usingDef : library.getUsings().getDef()) {
                Model model = getModel(usingDef);
                if (model.getModelInfo().getTargetUrl() != null) {
                    if (result != null) {
                        this.reportWarning(String.format("Duplicate mapped model %s:%s%s", model.getModelInfo().getName(),
                                model.getModelInfo().getTargetUrl(), model.getModelInfo().getTargetVersion() != null
                                        ? ("|" + model.getModelInfo().getTargetVersion()) : ""),
                                sourceContext
                        );
                    }
                    result = of.createVersionedIdentifier().withId(model.getModelInfo().getName())
                            .withSystem(model.getModelInfo().getTargetUrl())
                            .withVersion(model.getModelInfo().getTargetVersion());
                }
            }
        }

        return result;
    }

    private void ensureLibraryIncluded(String libraryName, Expression sourceContext) {
        IncludeDef includeDef = compiledLibrary.resolveIncludeRef(libraryName);
        if (includeDef == null) {
            VersionedIdentifier modelMapping = getModelMapping(sourceContext);
            String path = libraryName;
            if (this.getNamespaceInfo() != null && modelMapping != null && modelMapping.getSystem() != null) {
                path = NamespaceManager.getPath(modelMapping.getSystem(), path);
            }
            includeDef = of.createIncludeDef().withLocalIdentifier(libraryName).withPath(path);
            if (modelMapping != null) {
                includeDef.setVersion(modelMapping.getVersion());
            }
            compiledLibrary.add(includeDef);
        }
    }

    private void applyTargetModelMaps() {
        if (library.getUsings() != null && library.getUsings().getDef() != null) {
            for (UsingDef usingDef : library.getUsings().getDef()) {
                Model model = getModel(usingDef);
                if (model.getModelInfo().getTargetUrl() != null) {
                    usingDef.setUri(model.getModelInfo().getTargetUrl());
                    usingDef.setVersion(model.getModelInfo().getTargetVersion());
                }
            }
        }
    }

    public Expression applyTargetMap(Expression source, String targetMap) {
        if (targetMap == null || targetMap.equals("null")) {
            return source;
        }

        // TODO: This only works for simple mappings, nested mappings will require the targetMap.g4 parser
        // Supported target mapping syntax:
        // %value.<property name>
        // Resolves as a property accessor with the given source and <property name> as the path
        // <qualified function name>(%value)
        // Resolves as a function ref with the given function name and the source as an operand
        // <type name>:<map>;<type name>:<map>...
        // Semi-colon delimited list of type names and associated maps
        // Resolves as a case with whens for each type, with target mapping applied per the target map for that type
        // %parent.<qualified path>[<key path>=<key value>,<key path>=<key value>,...].<qualified path>
        // Resolves as a replacement of the property on which it appears
        // Replaces the path of the property on which it appears with the given qualified path, which then becomes the
        // source of a query with a where clause with criteria built for each comparison in the indexer
        // If there is a trailing qualified path, the query is wrapped in a singletonFrom and a property access
        // Any other target map results in an exception

        if (targetMap.contains(";")) {
            String[] typeCases = targetMap.split(";");
            Case c = of.createCase();
            for (String typeCase : typeCases) {
                if (!typeCase.isEmpty()) {
                    String[] caseElements = typeCase.split(":");
                    if (caseElements.length != 2) {
                        throw new IllegalArgumentException(String.format("Malformed type case in targetMap %s", targetMap));
                    }
                    CaseItem ci = of.createCaseItem().withWhen(of.createIs().withOperand(applyTargetMap(source, caseElements[1])).withIsType(dataTypeToQName(resolveTypeName(caseElements[0]))))
                            .withThen(applyTargetMap(source, caseElements[1]));
                    c.getCaseItem().add(ci);
                }
            }
            if (c.getCaseItem().size() == 0) {
                return this.buildNull(source.getResultType());
            }
            else if (c.getCaseItem().size() == 1) {
                return c.getCaseItem().get(0).getThen();
            }
            else {
                c.setElse(this.buildNull(source.getResultType()));
                c.setResultType(source.getResultType());
                return c;
            }
        }
        else if (targetMap.contains("(")) {
            int invocationStart = targetMap.indexOf("(");
            String qualifiedFunctionName = targetMap.substring(0, invocationStart);
            String[] nameParts = qualifiedFunctionName.split("\\.");
            String libraryName = null;
            String functionName = qualifiedFunctionName;
            if (nameParts.length == 2) {
                libraryName = nameParts[0];
                functionName = nameParts[1];

                ensureLibraryIncluded(libraryName, source);
            }

            String functionArgument = targetMap.substring(invocationStart + 1, targetMap.lastIndexOf(')'));
            Expression argumentSource = functionArgument.equals("%value") ? source : applyTargetMap(source, functionArgument);
            if (argumentSource.getResultType() instanceof ListType) {
                Query query = of.createQuery().withSource(of.createAliasedQuerySource().withExpression(argumentSource).withAlias("$this"));
                FunctionRef fr = of.createFunctionRef().withLibraryName(libraryName).withName(functionName).withOperand(of.createAliasRef().withName("$this"));
                query.setReturn(of.createReturnClause().withDistinct(false).withExpression(fr));
                query.setResultType(source.getResultType());
                return query;
            }
            else {
                FunctionRef fr = of.createFunctionRef()
                        .withLibraryName(libraryName).withName(functionName)
                        .withOperand(argumentSource);
                fr.setResultType(source.getResultType());
                return fr;
            }
        }
        else if (targetMap.contains("[")) {
            int indexerStart = targetMap.indexOf("[");
            int indexerEnd = targetMap.indexOf("]");
            String indexer = targetMap.substring(indexerStart + 1, indexerEnd);
            String indexerPath = targetMap.substring(0, indexerStart);

            Expression result = null;

            // Apply sourcePaths to get to the indexer
            String[] indexerPaths = indexerPath.split("\\.");
            for (String path : indexerPaths) {
                if (path.equals("%parent")) {
                    if (!(source instanceof Property)) {
                        throw new IllegalArgumentException(String.format("Cannot expand target map %s for non-property-accessor type %s",
                                targetMap, source.getClass().getSimpleName()));
                    }
                    Property sourceProperty = (Property)source;
                    if (sourceProperty.getSource() != null) {
                        result = sourceProperty.getSource();
                    }
                    else if (sourceProperty.getScope() != null) {
                        result = resolveIdentifier(sourceProperty.getScope(), true);
                    }
                    else {
                        throw new IllegalArgumentException(String.format("Cannot resolve %parent reference in targetMap %s",
                                targetMap));
                    }
                }
                else {
                    Property p = of.createProperty().withSource(result).withPath(path);
                    result = p;
                }
            }

            // Build a query with the current result as source and the indexer content as criteria in the where clause
            AliasedQuerySource querySource = of.createAliasedQuerySource().withExpression(result).withAlias("$this");

            Expression criteria = null;
            for (String indexerItem : indexer.split(",")) {
                String[] indexerItems = indexerItem.split("=");
                if (indexerItems.length != 2) {
                    throw new IllegalArgumentException(String.format("Invalid indexer item %s in targetMap %s", indexerItem, targetMap));
                }

                Expression left = null;
                for (String path : indexerItems[0].split("\\.")) {
                    if (left == null) {
                        left = of.createProperty().withScope("$this").withPath(path);
                    }
                    else {
                        left = of.createProperty().withSource(left).withPath(path);
                    }

                    // HACK: Workaround the fact that we don't have type information for the mapping expansions...
                    if (path.equals("coding")) {
                        left = of.createFirst().withSource(left);
                    }
                    if (path.equals("url")) {
                        left = of.createFunctionRef().withLibraryName("FHIRHelpers").withName("ToString").withOperand(left);
                    }
                }

                // HACK: Workaround the fact that we don't have type information for the mapping expansions...
                // These hacks will be removed when addressed by the model info
                if (indexerItems[0].equals("code.coding.system") || indexerItems[0].equals("code.coding.code")) {
                    left = of.createFunctionRef().withLibraryName("FHIRHelpers").withName("ToString").withOperand(left);
                }

                String rightValue = indexerItems[1].substring(1, indexerItems[1].length() - 1);
                Expression right = this.createLiteral(StringEscapeUtils.unescapeCql(rightValue));

                Expression criteriaItem = of.createEqual().withOperand(left, right);
                if (criteria == null) {
                    criteria = criteriaItem;
                }
                else {
                    criteria = of.createAnd().withOperand(criteria, criteriaItem);
                }
            }

            Query query = of.createQuery().withSource(querySource).withWhere(criteria);
            result = query;

            if (indexerEnd + 1 < targetMap.length()) {
                // There are additional paths following the indexer, apply them
                String targetPath = targetMap.substring(indexerEnd + 1);
                if (targetPath.startsWith(".")) {
                    targetPath = targetPath.substring(1);
                }

                if (!targetPath.isEmpty()) {
                    query.setReturn(of.createReturnClause()
                            .withDistinct(false).withExpression(of.createProperty()
                                    .withSource(of.createAliasRef().withName("$this")).withPath(targetPath)));
                }

                // The value reference should go inside the query, rather than being applied as a property outside of it
                //for (String path : targetPath.split("\\.")) {
                //    result = of.createProperty().withSource(result).withPath(path);
                //}
            }

            if (!(source.getResultType() instanceof ListType)) {
                // Use a singleton from since the source of the query is a list
                result = of.createSingletonFrom().withOperand(result);
            }

            result.setResultType(source.getResultType());
            return result;
        }
        else if (targetMap.contains("%value.")) {
            String propertyName = targetMap.substring(7);
            Property p = of.createProperty().withSource(source).withPath(propertyName);
            p.setResultType(source.getResultType());
            return p;
        }

        throw new IllegalArgumentException(String.format("TargetMapping not implemented: %s", targetMap));
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
            CompiledLibrary referencedLibrary = resolveLibrary(libraryName);

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

            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve identifier %s in library %s.",
                    memberIdentifier, referencedLibrary.getIdentifier().getId()));
        }
        else if (left instanceof AliasRef) {
            PropertyResolution resolution = resolveProperty(left.getResultType(), memberIdentifier);
            Expression result = buildProperty(((AliasRef)left).getName(), resolution.getName(), resolution.isSearch(), resolution.getType());
            return applyTargetMap(result, resolution.getTargetMap());
        }
        else if (left.getResultType() instanceof ListType && listTraversal) {
            // NOTE: FHIRPath path traversal support
            // Resolve property access of a list of items as a query:
            // listValue.property ::= listValue X where X.property is not null return all X.property
            ListType listType = (ListType)left.getResultType();
            PropertyResolution resolution = resolveProperty(listType.getElementType(), memberIdentifier);
            Expression accessor = buildProperty(of.createAliasRef().withName("$this"), resolution.getName(), resolution.isSearch(), resolution.getType());
            accessor = applyTargetMap(accessor, resolution.getTargetMap());
            Expression not = buildIsNotNull(accessor);

            // Recreate property, it needs to be accessed twice
            accessor = buildProperty(of.createAliasRef().withName("$this"), resolution.getName(), resolution.isSearch(), resolution.getType());
            accessor = applyTargetMap(accessor, resolution.getTargetMap());

            AliasedQuerySource source = of.createAliasedQuerySource().withExpression(left).withAlias("$this");
            source.setResultType(left.getResultType());
            Query query = of.createQuery()
                    .withSource(source)
                    .withWhere(not)
                    .withReturn(of.createReturnClause().withDistinct(false).withExpression(accessor));
            query.setResultType(new ListType(accessor.getResultType()));

            if (accessor.getResultType() instanceof ListType) {
                Flatten result = of.createFlatten().withOperand(query);
                result.setResultType(accessor.getResultType());
                return result;
            }

            return query;
        }
        else {
            PropertyResolution resolution = resolveProperty(left.getResultType(), memberIdentifier);
            Expression result = buildProperty(left, resolution.getName(), resolution.isSearch(), resolution.getType());
            result = applyTargetMap(result, resolution.getTargetMap());
            return result;
        }
    }

    private Expression resolveQueryResultElement(String identifier) {
        if (inQueryContext()) {
            QueryContext query = peekQueryContext();
            if (query.inSortClause() && !query.isSingular()) {
                if (identifier.equals("$this")) {
                    IdentifierRef result = new IdentifierRef().withName(identifier);
                    result.setResultType(query.getResultElementType());
                    return result;
                }

                PropertyResolution resolution = resolveProperty(query.getResultElementType(), identifier, false);
                if (resolution != null) {
                    IdentifierRef result = new IdentifierRef().withName(resolution.getName());
                    result.setResultType(resolution.getType());
                    return applyTargetMap(result, resolution.getTargetMap());
                }
            }
        }

        return null;
    }

    private List<IdentifierResolution> resolveQueryResultElements(String identifier) {

        List<IdentifierResolution> matchList = new ArrayList<>();

        if (inQueryContext()) {
            QueryContext query = peekQueryContext();
            if (query.inSortClause() && !query.isSingular()) {
                {
                    IdentifierRef result = new IdentifierRef().withName(identifier);
                    result.setResultType(query.getResultElementType());
                    matchList.add(IdentifierResolution.getMatchType(identifier, identifier, $_THIS, result));
                }

                IdentifierResolution propertyMatch = IdentifierResolution.getFirstCaseMatch(resolveProperties(query.getResultElementType(), identifier, false));

                if (propertyMatch != null) {
                    PropertyResolution pr = (PropertyResolution) propertyMatch.getResolvedElement();
                    IdentifierRef result = new IdentifierRef().withName(pr.getName());
                    result.setResultType(pr.getType());
                    if (pr.getTargetMap() != null &&
                            //matches have occurred, it's ok that this fails
                            (matchList.size() == 0)
                    ) {
                        throw new IllegalArgumentException("Target mapping not supported in this context");
                    }
                    matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, result));
                }
            }
        }

        return matchList;
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

    private List<IdentifierResolution> resolveAliases(String identifier) {
        List<IdentifierResolution> matchList = new ArrayList<>();
        // Need to use a for loop to go through backwards, iteration on a Stack is bottom up
        if (inQueryContext()) {
            for (int i = getScope().getQueries().size() - 1; i >= 0; i--) {
                AliasedQuerySource source = getScope().getQueries().get(i).resolveAlias(identifier);
                if (source != null) {
                    matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, source));
                }
                matchList.addAll(getScope().getQueries().get(i).resolveCaseIgnoredAliases(identifier));
            }
        }

        return matchList;
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

                    PropertyResolution result = resolveProperty(aliasRef.getResultType(), identifier, false);
                    if (result != null) {
                        return resolveAccessor(aliasRef, identifier);
                    }
                }
            }
        }

        return null;
    }

    private List<IdentifierResolution> resolveQueryThisElements(String identifier) {

        List<IdentifierResolution> matchList = new ArrayList<>();

        if (inQueryContext()) {
            QueryContext query = peekQueryContext();
            if (query.isImplicit()) {

                List<IdentifierResolution> aliasMatches =  resolveAliases($_THIS);

                IdentifierResolution aliasCaseMatch = IdentifierResolution.getFirstCaseMatch(aliasMatches);

                if (aliasCaseMatch != null) {

                    AliasedQuerySource src = (AliasedQuerySource) aliasCaseMatch.getResolvedElement();

                    AliasRef aliasRef = of.createAliasRef().withName($_THIS);
                    if (src.getResultType() instanceof ListType) {
                        aliasRef.setResultType(((ListType)src.getResultType()).getElementType());
                    }
                    else {
                        aliasRef.setResultType(src.getResultType());
                    }

                    PropertyResolution result = resolveProperty(aliasRef.getResultType(), identifier, false);
                    if (result != null) {
                        matchList.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, resolveAccessor(aliasRef, identifier)));
                    }

                }

                matchList.addAll(aliasMatches);
            }
        }

        return matchList;
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

    private List<IdentifierResolution> resolveQueryLets(String identifier) {
        List<IdentifierResolution> ri = new ArrayList<>();
        // Need to use a for loop to go through backwards, iteration on a Stack is bottom up
        if (inQueryContext()) {
            for (int i = getScope().getQueries().size() - 1; i >= 0; i--) {
                LetClause source = getScope().getQueries().get(i).resolveLet(identifier);
                if (source != null) {
                    ri.add(IdentifierResolution.createMatch(identifier, MatchType.EXACT, source));
                }
                ri.addAll(getScope().getQueries().get(i).resolveCaseIgnoredLets(identifier));
            }
        }

        return ri;
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

    private List<IdentifierResolution> resolveOperandRefs(String identifier) {
        List<IdentifierResolution> ri = new ArrayList<>();
        if (!functionDefs.empty()) {
            for (OperandDef operand : functionDefs.peek().getOperand()) {

                ri.add(IdentifierResolution.getMatchType(identifier, operand.getName(), identifier, of.createOperandRef()
                        .withName(identifier)
                        .withResultType(operand.getResultType())));

            }
        }

        return ri;
    }

    private DataType getExpressionDefResultType(ExpressionDef expressionDef) {
        // If the current expression context is the same as the expression def context, return the expression def result type.
        if (currentExpressionContext().equals(expressionDef.getContext())) {
            return expressionDef.getResultType();
        }

        // If the current expression context is specific, a reference to an unfiltered context expression will indicate a full
        // evaluation of the population context expression, and the result type is the same.
        if (inSpecificContext()) {
            return expressionDef.getResultType();
        }

        // If the current expression context is unfiltered, a reference to a specific context expression will need to be
        // performed for every context in the system, so the result type is promoted to a list (if it is not already).
        if (inUnfilteredContext()) {
            // If we are in the source clause of a query, indicate that the source references patient context
            if (inQueryContext() && getScope().getQueries().peek().inSourceClause()) {
                getScope().getQueries().peek().referenceSpecificContext();
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
            // ERROR:
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

    public boolean inSpecificContext() {
        return !inUnfilteredContext();
    }

    public boolean inUnfilteredContext() {
        return currentExpressionContext().equals("Unfiltered")
                || (isCompatibilityLevel3() && currentExpressionContext().equals("Population"));
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

    public void pushLiteralContext() {
        literalContext++;
    }

    public void popLiteralContext() {
        if (!inLiteralContext()) {
            throw new IllegalStateException("Not in literal context");
        }

        literalContext--;
    }

    public boolean inLiteralContext() {
        return literalContext > 0;
    }

    public void checkLiteralContext() {
        if (inLiteralContext()) {
            // ERROR:
            throw new IllegalStateException("Expressions in this context must be able to be evaluated at compile-time.");
        }
    }

    public void pushTypeSpecifierContext() {
        typeSpecifierContext++;
    }

    public void popTypeSpecifierContext() {
        if (!inTypeSpecifierContext()) {
            throw new IllegalStateException("Not in type specifier context");
        }

        typeSpecifierContext--;
    }

    public boolean inTypeSpecifierContext() {
        return typeSpecifierContext > 0;
    }
}