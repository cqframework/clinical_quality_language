package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.hl7.elm.r1.*;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider;
import org.opencds.cqf.cql.engine.debug.DebugAction;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.debug.DebugResult;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.Severity;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencds.cqf.cql.engine.execution.NamespaceHelper.getNamePart;
import static org.opencds.cqf.cql.engine.execution.NamespaceHelper.getUriPart;

public class State {
    private static Logger logger = LoggerFactory.getLogger(State.class);

    private CqlEngineVisitor visitor;
    private Map<String, Object> parameters = new HashMap<>();
    private Stack<String> currentContext = new Stack<>();
    private Map<String, Object> contextValues = new HashMap<>();
    private Stack<Stack<Variable> > windows = new Stack<>();
    private Map<String, Library> libraries = new HashMap<>();
    private Stack<Library> currentLibrary = new Stack<>();

    private Stack<HashSet<Object>> evaluatedResourceStack = new Stack<>();

    private ZonedDateTime evaluationZonedDateTime;
    private OffsetDateTime evaluationOffsetDateTime;
    private DateTime evaluationDateTime;

    private LibraryManager libraryManager;
    private Environment environment;

    private Cache cache;

    private CqlTranslatorOptions translatorOptions;

    private UcumService ucumService;

    private static UcumService sharedUcumService;


    private Map<String, DataProvider> dataProviders = new HashMap<>();
    private Map<String, DataProvider> packageMap = new HashMap<>();

    private DebugMap debugMap;

    public CqlEngineVisitor getVisitor() {
        return visitor;
    }

    public void setVisitor(CqlEngineVisitor visitor) {
        this.visitor = visitor;
    }

    public void registerDataProvider(String modelUri, DataProvider dataProvider) {
        dataProviders.put(modelUri, dataProvider);
        dataProvider.getPackageNames().forEach( pn -> packageMap.put( pn, dataProvider ) );
    }


    public void setCurrentContext(Stack<String> currentContext) {
        this.currentContext = currentContext;
    }


    public Library getCurrentLibrary() {
        return currentLibrary.peek();
    }

    public void setCurrentLibrary(Stack<Library> currentLibrary) {
        this.currentLibrary = currentLibrary;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }


    public void setParameters(Library library, Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> parameterValue : parameters.entrySet()) {
                setParameter(null, parameterValue.getKey(), parameterValue.getValue());
            }
        }
    }

    public void setParameter(String libraryName, String name, Object value) {
        boolean enteredLibrary = enterLibrary(libraryName);
        try {
            String fullName = libraryName != null ? String.format("%s.%s", getCurrentLibrary().getIdentifier().getId(), name) : name;
            parameters.put(fullName, value);
        }
        finally {
            exitLibrary(enteredLibrary);
        }
    }

    public boolean enterLibrary(String libraryName) {
        if (libraryName != null) {
            IncludeDef includeDef = resolveLibraryRef(libraryName);
            Library library = resolveIncludeDef(includeDef);
            currentLibrary.push(library);
            return true;
        }

        return false;
    }

    public void exitLibrary(boolean enteredLibrary) {
        if (enteredLibrary) {
            currentLibrary.pop();
        }
    }

    private IncludeDef resolveLibraryRef(String libraryName) {
        for (IncludeDef includeDef : getCurrentLibrary().getIncludes().getDef()) {
            if (includeDef.getLocalIdentifier().equals(libraryName)) {
                return includeDef;
            }
        }

        throw new CqlException(String.format("Could not resolve library reference '%s'.", libraryName));
    }

    private Library resolveIncludeDef(IncludeDef includeDef) {
        VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
                .withSystem(getUriPart(includeDef.getPath()))
                .withId(getNamePart(includeDef.getPath()))
                .withVersion(includeDef.getVersion());

        Library library = libraries.get(libraryIdentifier.getId());
        if (library == null) {
            ArrayList<CqlCompilerException> errors = new ArrayList<CqlCompilerException>();
            library = libraryManager.resolveLibrary(libraryIdentifier, translatorOptions, errors).getLibrary();
            libraries.put(libraryIdentifier.getId(), library);
        }

        if (libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new CqlException(String.format("Could not load library '%s' version '%s' because version '%s' is already loaded.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion(), library.getIdentifier().getVersion()));
        }

        return library;
    }

    public Map<String, Object> getContextValues() {
        return contextValues;
    }

    public void setContextValues(Map<String, Object> contextValues) {
        this.contextValues = contextValues;
    }

    public Stack<Stack<Variable>> getWindows() {
        return windows;
    }

    public void setWindows(Stack<Stack<Variable>> windows) {
        this.windows = windows;
    }

    public Map<String, Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(Map<String, Library> libraries) {
        this.libraries = libraries;
    }




    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public void setLibraryManager(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
    }

    public DebugMap getDebugMap() {
        return this.debugMap;
    }

    public void setDebugMap(DebugMap debugMap) {
        this.debugMap = debugMap;
    }


    private DebugResult debugResult;
    public DebugResult getDebugResult() {
        return this.debugResult;
    }

    public DebugAction shouldDebug(Exception e) {
        if (this.debugMap == null) {
            return DebugAction.NONE;
        }

        return debugMap.shouldDebug(e);
    }

    public DebugAction shouldDebug(Element node) {
        if (this.debugMap == null) {
            return DebugAction.NONE;
        }

        return debugMap.shouldDebug(node, this.getCurrentLibrary());
    }

    private void ensureDebugResult() {
        if (this.debugResult == null) {
            debugResult = new DebugResult();
        }
    }

    public Cache getCache() { return cache; }

    public void setCache(Cache cache) { this.cache = cache; }

    public void setEvaluationDateTime(ZonedDateTime evaluationZonedDateTime) {
        this.evaluationZonedDateTime = evaluationZonedDateTime;
        this.evaluationOffsetDateTime = evaluationZonedDateTime.toOffsetDateTime();
        this.evaluationDateTime = new DateTime(evaluationOffsetDateTime);
    }

    public ZonedDateTime getEvaluationZonedDateTime() {
        return this.evaluationZonedDateTime;
    }

    public OffsetDateTime getEvaluationOffsetDateTime() {
        return this.evaluationOffsetDateTime;
    }

    public DateTime getEvaluationDateTime() {
        return this.evaluationDateTime;
    }

    public void init(Library library, DataProvider systemDataProvider, UcumService ucumService) {
        pushWindow();
        registerDataProvider("urn:hl7-org:elm-types:r1", systemDataProvider);

        if (library.getIdentifier() != null) {
            libraries.put(library.getIdentifier().getId(), library);
        }

        currentLibrary.push(library);

        if (ucumService != null) {
            this.ucumService = ucumService;
        }
        else {
            this.ucumService = getSharedUcumService();
        }

        this.pushEvaluatedResourceStack();
    }

    public void pop() {
        if (!windows.peek().empty())
            getStack().pop();
    }

    public void push(Variable variable) {
        getStack().push(variable);
    }

    public Variable resolveVariable(String name) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            for (int j = 0; j < windows.get(i).size(); j++) {
                if (windows.get(i).get(j).getName().equals(name)) {
                    return windows.get(i).get(j);
                }
            }
        }

        return null;
    }

    public Variable resolveVariable(String name, boolean mustResolve) {
        Variable result = resolveVariable(name);
        if (mustResolve && result == null) {
            throw new CqlException(String.format("Could not resolve variable reference %s", name));
        }

        return result;
    }

    public void pushWindow() {
        windows.push(new Stack<>());
    }

    public void popWindow() {
        windows.pop();
    }

    private Stack<Variable> getStack() {
        return windows.peek();
    }

    public void setContextValue(String context, Object contextValue) {
        if (hasContextValueChanged(context, contextValue)) {
            clearExpressions();
        }

        contextValues.put(context, contextValue);
    }

    public void clearExpressions() {
        this.cache.getExpressions().clear();
    }

    public void enterContext(String context) {
        currentContext.push(context);
    }

    public void exitContext() {
        currentContext.pop();
    }

    public String getCurrentContext() {
        if (currentContext.empty()) {
            return null;
        }

        return currentContext.peek();
    }

    private boolean hasContextValueChanged(String context, Object contextValue) {
        if (contextValues.containsKey(context)) {
            return !contextValues.get(context).equals(contextValue);
        }
        return true;
    }

    public Object getCurrentContextValue() {
        String context = getCurrentContext();
        if (context != null && this.contextValues.containsKey(context)) {
            return this.contextValues.get(context);
        }

        return null;
    }

    public UcumService getUcumService() {
        return ucumService;
    }

    public void setUcumService(UcumService ucumService) {
        this.ucumService = ucumService;
    }

    protected synchronized UcumService getSharedUcumService() {
        if (sharedUcumService == null) {
            try {
                sharedUcumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
            }
            catch (UcumException e) {
                logger.warn("Error creating shared UcumService", e);
            }
        }

        return sharedUcumService;
    }

    public Set<Object> getEvaluatedResources() {
        if (evaluatedResourceStack.empty()) {
            throw new IllegalStateException("Attempted to get the evaluatedResource stack when it's empty");
        }

        return this.evaluatedResourceStack.peek();
    }

    public void clearEvaluatedResources() {
        this.evaluatedResourceStack.clear();
        this.pushEvaluatedResourceStack();
    }

    public void pushEvaluatedResourceStack() {
        evaluatedResourceStack.push(new HashSet<>());
    }

    //serves pop and merge to the down
    public void popEvaluatedResourceStack() {
        if (evaluatedResourceStack.empty()) {
            throw new IllegalStateException("Attempted to pop the evaluatedResource stack when it's empty");
        }

        if (evaluatedResourceStack.size() == 1) {
            throw new IllegalStateException("Attempted to pop the evaluatedResource stack when only the root remains");
        }

        Set<Object> objects = evaluatedResourceStack.pop();
        var set = evaluatedResourceStack.peek();
        set.addAll(objects);
    }


    public CqlTranslatorOptions getTranslatorOptions() {
        return translatorOptions;
    }

    public void setTranslatorOptions(CqlTranslatorOptions translatorOptions) {
        this.translatorOptions = translatorOptions;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public QName fixupQName(QName typeName) {
        // When a Json library is deserialized on Android
        if (typeName.getNamespaceURI() == null || typeName.getNamespaceURI().isEmpty()) {
            if (typeName.getLocalPart() != null && typeName.getLocalPart().startsWith("{")) {
                int closeIndex =  typeName.getLocalPart().indexOf('}');
                if (closeIndex > 0 && typeName.getLocalPart().length() > closeIndex) {
                    return new QName(typeName.getLocalPart().substring(1, closeIndex), typeName.getLocalPart().substring(closeIndex + 1));
                }
            }
        }

        return typeName;
    }

    public ExpressionDef resolveExpressionRef(String name) {

        for (ExpressionDef expressionDef : getCurrentLibrary().getStatements().getDef()) {
            if (expressionDef.getName().equals(name)) {
                return expressionDef;
            }
        }

        throw new CqlException(String.format("Could not resolve expression reference '%s' in library '%s'.",
                name, getCurrentLibrary().getIdentifier().getId()));
    }

    public Object resolveAlias(String name) {
        // This method needs to account for multiple variables on the stack with the same name
        ArrayList<Object> ret = new ArrayList<>();
        boolean isList = false;
        for (Variable v : getStack()) {
            if (v.getName().equals(name)) {
                if (v.isList())
                    isList = true;
                ret.add(v.getValue());
            }
        }
        return isList ? ret : ret.get(ret.size() - 1);
    }

    public CodeSystemDef resolveCodeSystemRef(String name) {
        for (CodeSystemDef codeSystemDef : getCurrentLibrary().getCodeSystems().getDef()) {
            if (codeSystemDef.getName().equals(name)) {
                return codeSystemDef;
            }
        }

        throw new CqlException(String.format("Could not resolve code system reference '%s' in library '%s'.",
                name, getCurrentLibrary().getIdentifier().getId()));
    }

    public void registerTerminologyProvider(TerminologyProvider tp) {
        this.environment.setTerminologyProvider(tp);
    }
    public TerminologyProvider resolveTerminologyProvider() {
        return this.environment.getTerminologyProvider();
    }

    public ValueSetDef resolveValueSetRef(String name) {
        for (ValueSetDef valueSetDef : getCurrentLibrary().getValueSets().getDef()) {
            if (valueSetDef.getName().equals(name)) {
                return valueSetDef;
            }
        }

        throw new CqlException(String.format("Could not resolve value set reference '%s' in library '%s'.",
                name, getCurrentLibrary().getIdentifier().getId()));
    }

    public Object resolvePath(Object target, String path) {

        if (target == null) {
            return null;
        }

        // TODO: Path may include .'s and []'s.
        // For now, assume no qualifiers or indexers...
        Class<?> clazz = target.getClass();

        if (clazz.getPackage().getName().startsWith("java.lang")) {
            throw new CqlException(String.format("Invalid path: %s for type: %s - this is likely an issue with the data model.", path, clazz.getName()));
        }

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        return dataProvider.resolvePath(target, path);
    }

    public Object as(Object operand, Class<?> type, boolean isStrict) {
        if (operand == null) {
            return null;
        }

        if (type.isAssignableFrom(operand.getClass())) {
            return operand;
        }

        DataProvider provider = resolveDataProvider(type.getPackage().getName(), false);
        if (provider != null) {
            return provider.as(operand, type, isStrict);
        }

        return null;
    }

    public Boolean objectEqual(Object left, Object right) {
        if (left == null) {
            return null;
        }

        Class<? extends Object> clazz = left.getClass();

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        return dataProvider.objectEqual(left, right);
    }

    public Boolean objectEquivalent(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return true;
        }

        if (left == null) {
            return false;
        }

        Class<? extends Object> clazz = left.getClass();

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        return dataProvider.objectEquivalent(left, right);
    }

    public DataProvider resolveDataProvider(QName dataType) {
        dataType = fixupQName(dataType);
        DataProvider dataProvider = dataProviders.get(dataType.getNamespaceURI());
        if (dataProvider == null) {
            throw new CqlException(String.format("Could not resolve data provider for model '%s'.", dataType.getNamespaceURI()));
        }

        return dataProvider;
    }

    public DataProvider resolveDataProviderByModelUri(String modelUri) {
        DataProvider dataProvider = dataProviders.get(modelUri);
        if (dataProvider == null) {
            throw new CqlException(String.format("Could not resolve data provider for model '%s'.", modelUri));
        }

        return dataProvider;
    }

    public DataProvider resolveDataProvider(String packageName) {
        return resolveDataProvider(packageName, true);
    }

    public DataProvider resolveDataProvider(String packageName, boolean mustResolve) {
        DataProvider dataProvider = packageMap.get(packageName);
        if (dataProvider == null && mustResolve) {
            throw new CqlException(String.format("Could not resolve data provider for package '%s'.", packageName));
        }

        return dataProvider;
    }

    public Class<?> resolveType(QName typeName) {
        typeName = fixupQName(typeName);
        DataProvider dataProvider = resolveDataProvider(typeName);
        return dataProvider.resolveType(typeName.getLocalPart());
    }

    public Class<?> resolveType(org.cqframework.cql.elm.execution.TypeSpecifier typeSpecifier) {
        if (typeSpecifier instanceof org.cqframework.cql.elm.execution.NamedTypeSpecifier) {
            return resolveType(((org.cqframework.cql.elm.execution.NamedTypeSpecifier)typeSpecifier).getName());
        }
        else if (typeSpecifier instanceof org.cqframework.cql.elm.execution.ListTypeSpecifier) {
            // TODO: This doesn't allow for list-distinguished overloads...
            return List.class;
            //return resolveType(((ListTypeSpecifier)typeSpecifier).getElementType());
        }
        else if (typeSpecifier instanceof org.cqframework.cql.elm.execution.IntervalTypeSpecifier) {
            return org.opencds.cqf.cql.engine.runtime.Interval.class;
        }
        else if (typeSpecifier instanceof org.cqframework.cql.elm.execution.ChoiceTypeSpecifier) {
            // TODO: This doesn't allow for choice-distinguished overloads...
            return Object.class;
        }
        else {
            // TODO: This doesn't allow for tuple-distinguished overloads....
            return org.opencds.cqf.cql.engine.runtime.Tuple.class;
        }
    }

    public Class<?> resolveType(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof TypeSpecifier) {
            return resolveType((TypeSpecifier) value);
        }

        String packageName = value.getClass().getPackage().getName();

        // May not be necessary, idea is to sync with the use of List.class for ListTypeSpecifiers in the resolveType above
        if (value instanceof Iterable) {
            return List.class;
        }

        if (value instanceof Tuple) {
            return org.opencds.cqf.cql.engine.runtime.Tuple.class;
        }

        // Primitives should just use the type
        // BTR: Well, we should probably be explicit about all and only the types we expect
        if (packageName.startsWith("java")) {
            return value.getClass();
        }

        DataProvider dataProvider = resolveDataProvider(value.getClass().getPackage().getName());
        return dataProvider.resolveType(value);
    }

    public CodeDef resolveCodeRef(String name) {
        for (CodeDef codeDef : getCurrentLibrary().getCodes().getDef()) {
            if (codeDef.getName().equals(name)) {
                return codeDef;
            }
        }

        throw new CqlException(String.format("Could not resolve code reference '%s'.", name));
    }

    public CodeDef resolveCodeRef(String libraryName, String name) {
        boolean enteredLibrary = enterLibrary(libraryName);
        try {
            return resolveCodeRef(name);
        }
        finally {
            exitLibrary(enteredLibrary);
        }
    }

    public ConceptDef resolveConceptRef(String name) {
        for (ConceptDef conceptDef : getCurrentLibrary().getConcepts().getDef()) {
            if (conceptDef.getName().equals(name)) {
                return conceptDef;
            }
        }

        throw new CqlException(String.format("Could not resolve concept reference '%s'.", name));
    }

    public ConceptDef resolveConceptRef(String libraryName, String name) {
        boolean enteredLibrary = enterLibrary(libraryName);
        try {
            return resolveConceptRef(name);
        }
        finally {
            exitLibrary(enteredLibrary);
        }
    }

    private String getMangledFunctionName(String libraryName, String name) {
        return (libraryName == null ? getCurrentLibrary().getIdentifier().getId() : libraryName) + "." + name;
    }

    private Map<String, List<FunctionDef>> functionDefsByMangledName = new HashMap<>();

    public FunctionDef resolveFunctionRef(final String libraryName, final String name, final List<Object> arguments, final List<TypeSpecifier> signature) {
        FunctionDef ret;

        final List<? extends Object> types = signature.isEmpty() ? arguments : signature;

        ret = getResolvedFunctionDef(libraryName, name, types, !signature.isEmpty());

        if (ret != null) {
            return ret;
        }

        throw new CqlException(String.format("Could not resolve call to operator '%s(%s)' in library '%s'.",
                name, getUnresolvedMessage(types, name), getCurrentLibrary().getIdentifier().getId()));
    }

    private FunctionDef getResolvedFunctionDef(final String libraryName, final String name, final List<? extends Object> types, final boolean hasSignature) {
        String mangledFunctionName = getMangledFunctionName(libraryName, name);
        List<FunctionDef> namedDefs = this.functionDefsByMangledName
                .computeIfAbsent(mangledFunctionName, x -> this.getFunctionDefs(name));

        var candidateDefs = namedDefs
                .stream()
                .filter(x -> x.getOperand().size() == types.size())
                .collect(Collectors.toList());

        if (candidateDefs.size() == 1) {
            return candidateDefs.get(0);
        }

        if (candidateDefs.size() > 1 && !hasSignature) {
            logger.debug("Using runtime function resolution for '{}'. It's recommended to always include signatures in ELM", mangledFunctionName);
        }

        return candidateDefs.stream().filter(x -> matchesTypes(x, types)).findFirst().orElse(null);
    }

    private List<FunctionDef> getFunctionDefs(final String name) {
        final var statements = getCurrentLibrary().getStatements();
        if (statements == null) {
            return Collections.emptyList();
        }

        return statements.getDef().stream()
                .filter(x -> x.getName().equals(name))
                .filter(FunctionDef.class::isInstance)
                .map(FunctionDef.class::cast)
                .collect(Collectors.toList());
    }

    private String getUnresolvedMessage(List<? extends Object> arguments, String name) {
        StringBuilder argStr = new StringBuilder();
        if (arguments != null) {
            arguments.forEach(a -> argStr.append((argStr.length() > 0) ? ", " : "").append(resolveType(a).getName()));
        }

        return argStr.toString();
    }

    private boolean isType(Class<?> argumentType, Class<?> operandType) {
        return argumentType == null || operandType.isAssignableFrom(argumentType);
    }

    private boolean matchesTypes(FunctionDef functionDef, List<? extends Object> arguments) {
        boolean isMatch = true;

        var operands = functionDef.getOperand();

        // if argument length is mismatched, don't compare
        if (arguments.size() != operands.size()) {
            return false;
        }

        for (var i = 0; i < arguments.size(); i++) {
            isMatch = isType(resolveType(arguments.get(i)), this.resolveOperandType(operands.get(i)));
            if (!isMatch) {
                break;
            }
        }

        return isMatch;
    }

    static class FunctionDesc {
        public FunctionDesc(FunctionDef functionDef, List<Class<?>> operandTypes) {
            this.functionDef = functionDef;
            this.operandTypes = operandTypes;
        }

        private FunctionDef functionDef;
        private List<Class<?>> operandTypes;

        public FunctionDef functionDef() {
            return this.functionDef;
        }

        public List<Class<?>> operandTypes() {
            return this.operandTypes;
        }
    }

    private FunctionDesc createFunctionDesc(FunctionDef functionDef) {
        var operandTypes = new ArrayList<Class<?>>(functionDef.getOperand().size());
        for (var op : functionDef.getOperand()) {
            operandTypes.add(this.resolveOperandType(op));
        }

        return new FunctionDesc(functionDef, operandTypes);
    }

    private Class<?> resolveOperandType(OperandDef operandDef) {
        if (operandDef.getOperandTypeSpecifier() != null) {
            return resolveType(operandDef.getOperandTypeSpecifier());
        }
        else {
            return resolveType(operandDef.getOperandType());
        }
    }

    private Map<VersionedIdentifier, ExternalFunctionProvider> externalFunctionProviders = new HashMap<>();

    public void registerExternalFunctionProvider(VersionedIdentifier identifier, ExternalFunctionProvider provider) {
        externalFunctionProviders.put(identifier, provider);
    }

    public ExternalFunctionProvider getExternalFunctionProvider() {
        Library currentLibrary = getCurrentLibrary();
        VersionedIdentifier identifier = currentLibrary.getIdentifier();
        ExternalFunctionProvider provider = externalFunctionProviders.get(identifier);
        if (provider == null) {
            throw new CqlException(String.format(
                    "Could not resolve external function provider for library '%s'.", identifier));
        }
        return provider;
    }

    public Object resolveIdentifierRef(String name) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            for (int j = 0; j < windows.get(i).size(); j++) {
                Object value = windows.get(i).get(j).getValue();
                if (value instanceof org.opencds.cqf.cql.engine.runtime.Tuple) {
                    for (String key : ((org.opencds.cqf.cql.engine.runtime.Tuple) value).getElements().keySet()) {
                        if (key.equals(name)) {
                            return ((org.opencds.cqf.cql.engine.runtime.Tuple) value).getElements().get(key);
                        }
                    }
                }
                try {
                    return resolvePath(value, name);
                } catch (Exception ignored) {

                }
            }
        }

        throw new CqlException("Cannot resolve identifier " + name);
    }

    public Object createInstance(QName typeName) {
        typeName = fixupQName(typeName);
        DataProvider dataProvider = resolveDataProvider(typeName);
        return dataProvider.createInstance(typeName.getLocalPart());
    }

    public void setValue(Object target, String path, Object value) {
        if (target == null) {
            return;
        }

        Class<? extends Object> clazz = target.getClass();

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        dataProvider.setValue(target, path, value);
    }

    public Boolean is(Object operand, Class<?> type) {
        if (operand == null) {
            return null;
        }

        if (type.isAssignableFrom(operand.getClass())) {
            return true;
        }

        DataProvider provider = resolveDataProvider(type.getPackage().getName(), false);
        if (provider != null) {
            return provider.is(operand, type);
        }

        return false;
    }

    public void logDebugResult(Element node, Object result, DebugAction action) {
        ensureDebugResult();
        debugResult.logDebugResult(node, this.getCurrentLibrary(), result, action);
    }

    public void logDebugMessage(SourceLocator locator, String message) {
        ensureDebugResult();
        debugResult.logDebugError(new CqlException(message, locator, Severity.MESSAGE));
    }

    public void logDebugWarning(SourceLocator locator, String message) {
        ensureDebugResult();
        debugResult.logDebugError(new CqlException(message, locator, Severity.WARNING));
    }

    public void logDebugTrace(SourceLocator locator, String message) {
        ensureDebugResult();
        debugResult.logDebugError(new CqlException(message, locator, Severity.TRACE));
    }

    public void logDebugError(CqlException e) {
        ensureDebugResult();
        debugResult.logDebugError(e);
    }

    public ParameterDef resolveParameterRef(String name) {
        for (ParameterDef parameterDef : getCurrentLibrary().getParameters().getDef()) {
            if (parameterDef.getName().equals(name)) {
                return parameterDef;
            }
        }

        throw new CqlException(String.format("Could not resolve parameter reference '%s' in library '%s'.",
                name, getCurrentLibrary().getIdentifier().getId()));
    }

    public Object resolveParameterRef(String libraryName, String name, CqlEngineVisitor visitor) {
        boolean enteredLibrary = enterLibrary(libraryName);
        try {
            String fullName = libraryName != null ? String.format("%s.%s", getCurrentLibrary().getIdentifier().getId(), name) : name;
            if (parameters.containsKey(fullName)) {
                return parameters.get(fullName);
            }

            ParameterDef parameterDef = resolveParameterRef(name);
            Object result = parameterDef.getDefault() != null ? visitor.visitExpression(parameterDef.getDefault(),this) : null;
            parameters.put(fullName, result);
            return result;
        }
        finally {
            exitLibrary(enteredLibrary);
        }
    }

}
