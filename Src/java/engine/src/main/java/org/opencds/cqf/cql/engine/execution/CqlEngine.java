package org.opencds.cqf.cql.engine.execution;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlIncludeException;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.debug.DebugAction;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOTE: We have updated CqlEngine to adopt a visitor pattern approach to traversing the ELM tree for execution:
 *
 * Visitor pattern reduces the process to convert EML Tree to Executable ELM tree and thus reduces a potential maintenance issue.
 *
 */
public class CqlEngine {
    private static final Logger log = LoggerFactory.getLogger(CqlEngine.class);

    public enum Options {
        EnableExpressionCaching,
        EnableValidation,
        // HEDIS Compatibility Mode changes the behavior of the CQL
        // engine to match some expected behavior of the HEDIS
        // content that is not standards-complaint.
        // Currently, this includes:
        //  1. Making the default comparison semantics for lists to be "equivalent"
        //      (the standard behavior is to use "equal" semantics - note that this is
        //      expected to be the standard behavior in a future version of the CQL spec)
        //  2. Ignoring the "all" / "distinct" modifiers for the "return" clause of queries, always return all elements
        //      (the standard behavior is to return distinct elements)
        EnableHedisCompatibilityMode,
        // Collect data on evaluation counts, timing and cache hit
        // ratio for certain elements such as expression and function
        // definitions and retrieves.
        EnableProfiling,
    }

    private final Environment environment;
    private final State state;
    private final Set<Options> engineOptions;
    private final EvaluationVisitor evaluationVisitor = new EvaluationVisitor();

    public CqlEngine(Environment environment) {
        this(environment, new HashSet<>());
    }

    public CqlEngine(Environment environment, Set<Options> engineOptions) {
        log.info("1234: Initializing CQL Engine.");
        requireNonNull(environment.getLibraryManager(), "Environment LibraryManager can not be null.");
        this.environment = environment;

        this.engineOptions = engineOptions != null ? engineOptions : EnumSet.of(Options.EnableExpressionCaching);
        this.state = new State(environment, engineOptions);

        if (this.engineOptions.contains(CqlEngine.Options.EnableExpressionCaching)) {
            this.getCache().setExpressionCaching(true);
        }
        if (this.engineOptions.contains(Options.EnableProfiling)) {
            this.state.ensureDebugResult().ensureProfile();
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    public State getState() {
        return state;
    }

    public Cache getCache() {
        return this.state.getCache();
    }

    /**
     * @deprecated this is a temporary arrangement until we further refine the relationship
     * between the engine, the environment, and the state
     * @return the internal engine visitor
     */
    @Deprecated(forRemoval = true)
    public EvaluationVisitor getEvaluationVisitor() {
        return this.evaluationVisitor;
    }

    /**
     * @deprecated I added to assist with unit testing, but really it's indicative of the fact
     * that we need to further refine the engine API. Please use this sparingly as it will go away
     * @param libraryIdentifier the library where the expression is defined
     * @param expressionName the name of the expression to evaluate
     * @param evaluationDateTime the value for "Now()"
     * @return the result of the expression
     */
    @Deprecated(forRemoval = true)
    public ExpressionResult expression(
            VersionedIdentifier libraryIdentifier, String expressionName, ZonedDateTime evaluationDateTime) {
        var set = new HashSet<String>();
        set.add(expressionName);
        var result = this.evaluate(libraryIdentifier, set, null, null, null, evaluationDateTime);
        return result.forExpression(expressionName);
    }

    /**
     * @deprecated I added to assist with unit testing, but really it's indicative of the fact
     * that we need to further refine the engine API. Please use this sparingly as it will go away
     * @param libraryIdentifier the library where the expression is defined
     * @param expressionName the name of the expression to evaluate
     * @return the result of the expression
     */
    @Deprecated(forRemoval = true)
    public ExpressionResult expression(VersionedIdentifier libraryIdentifier, String expressionName) {
        return this.expression(libraryIdentifier, expressionName, null);
    }

    // TODO: Add debugging info as a parameter.
    public EvaluationResult evaluate(String libraryName) {
        return this.evaluate(libraryName, null, null, null);
    }

    public EvaluationResult evaluate(String libraryName, Set<String> expressions) {
        return this.evaluate(libraryName, expressions, null, null);
    }

    public EvaluationResult evaluate(
            String libraryName, Set<String> expressions, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryName, expressions, contextParameter, null);
    }

    public EvaluationResult evaluate(String libraryName, Set<String> expressions, Map<String, Object> parameters) {
        return this.evaluate(libraryName, expressions, null, parameters);
    }

    public EvaluationResult evaluate(String libraryName, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryName, null, contextParameter, null);
    }

    public EvaluationResult evaluate(
            String libraryName, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        return this.evaluate(libraryName, null, contextParameter, parameters);
    }

    public EvaluationResult evaluate(String libraryName, Map<String, Object> parameters) {
        return this.evaluate(libraryName, null, null, parameters);
    }

    public EvaluationResult evaluate(
            String libraryName,
            Set<String> expressions,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters) {
        return this.evaluate(
                new VersionedIdentifier().withId(libraryName), expressions, contextParameter, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier) {
        return this.evaluate(libraryIdentifier, null, null, null, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, ZonedDateTime evaluationDateTime) {
        return this.evaluate(libraryIdentifier, null, null, null, null, evaluationDateTime);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Set<String> expressions) {
        return this.evaluate(libraryIdentifier, expressions, null, null, null);
    }

    public EvaluationResultsForMultiLib evaluate(
            List<VersionedIdentifier> libraryIdentifiers, Set<String> expressions) {
        return this.evaluate(libraryIdentifiers, expressions, null, null, null);
    }

    public EvaluationResult evaluate(
            VersionedIdentifier libraryIdentifier, Set<String> expressions, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryIdentifier, expressions, contextParameter, null, null);
    }

    public EvaluationResult evaluate(
            VersionedIdentifier libraryIdentifier, Set<String> expressions, Map<String, Object> parameters) {
        return this.evaluate(libraryIdentifier, expressions, null, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryIdentifier, null, contextParameter, null, null);
    }

    public EvaluationResult evaluate(
            VersionedIdentifier libraryIdentifier,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters) {
        return this.evaluate(libraryIdentifier, null, contextParameter, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Map<String, Object> parameters) {
        return this.evaluate(libraryIdentifier, null, null, parameters, null);
    }

    public EvaluationResult evaluate(
            VersionedIdentifier libraryIdentifier,
            Set<String> expressions,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters,
            DebugMap debugMap) {
        return this.evaluate(libraryIdentifier, expressions, contextParameter, parameters, debugMap, null);
    }

    public EvaluationResultsForMultiLib evaluate(
            List<VersionedIdentifier> libraryIdentifiers,
            Set<String> expressions,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters,
            DebugMap debugMap) {
        return this.evaluate(libraryIdentifiers, expressions, contextParameter, parameters, debugMap, null);
    }

    public EvaluationResult evaluate(
            VersionedIdentifier libraryIdentifier,
            Set<String> expressions,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters,
            DebugMap debugMap,
            ZonedDateTime evaluationDateTime) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier can not be null.");
        }

        Library library = this.loadAndValidate(libraryIdentifier);

        if (expressions == null) {
            expressions = this.getExpressionSet(library);
        }

        this.initializeState(library, debugMap, evaluationDateTime);
        this.setParametersForContext(library, contextParameter, parameters);

        return this.evaluateExpressions(expressions);
    }

    // LUKETODO: builder, immutability, immutable copies of collections, etc
    // LUKETODO: record?
    public static class EvaluationResultsForMultiLib {
        private final Map<SearchableLibraryIdentifier, EvaluationResult> results;
        // LUKETODO:  single or multiple errors per library??
        private final Map<SearchableLibraryIdentifier, String> errors;

        public EvaluationResultsForMultiLib(
                Map<SearchableLibraryIdentifier, EvaluationResult> results,
                Map<SearchableLibraryIdentifier, String> errors) {
            this.results = results;
            this.errors = errors;
        }

        // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
        public Map<SearchableLibraryIdentifier, EvaluationResult> getResults() {
            return results;
        }

        // LUKETODO:  validate this isn't empty or Optional or something
        public EvaluationResult getFirstResult() {
            return results.entrySet().iterator().next().getValue();
        }

        // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
        public Map<SearchableLibraryIdentifier, String> getErrors() {
            return errors;
        }

        public EvaluationResult getResultFor(VersionedIdentifier libraryIdentifier) {
            return results.get(SearchableLibraryIdentifier.fromIdentifier(libraryIdentifier));
        }

        public EvaluationResult getResultFor(SearchableLibraryIdentifier libraryIdentifier) {
            return results.get(libraryIdentifier);
        }
    }

    // LUKETODO: builder, immutability, immutable copies of collections, etc
    // LUKETODO: record?
    public static class LoadedLibrariesForMultiLib {
        private final LinkedHashMap<VersionedIdentifier, Library> results;
        private final LinkedHashMap<VersionedIdentifier, String> errors;

        public LoadedLibrariesForMultiLib(
                LinkedHashMap<VersionedIdentifier, Library> results,
                LinkedHashMap<VersionedIdentifier, String> errors) {
            this.results = results;
            this.errors = errors;
        }

        public LinkedHashMap<VersionedIdentifier, Library> getResults() {
            return results;
        }

        public LinkedHashMap<VersionedIdentifier, String> getErrors() {
            return errors;
        }
    }

    private static final String EXCEPTION_FOR_SUBJECT_ID_MESSAGE_TEMPLATE = "Exception for Library: %s, Message: %s";
    // LUKETODO:  Map<VersionedIdentifier, List<ExpressionResult>>???
    public EvaluationResultsForMultiLib evaluate(
            List<VersionedIdentifier> libraryIdentifiers,
            // LUKETODO:  figure out how to pass expressions later
            // LUKETODO:  need to consider scoping expresions by versioned identifier or something else
            Set<String> expressions,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters,
            DebugMap debugMap,
            ZonedDateTime nullableEvaluationDateTime) {

        if (libraryIdentifiers == null || libraryIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("libraryIdentifier can not be null or empty.");
        }

        log.info(
                "1234: Evaluating libraries: {} with contextParameter: {} and expressions: [{}]",
                libraryIdentifiers.stream().map(VersionedIdentifier::getId).toList(),
                contextParameter,
                Optional.ofNullable(expressions)
                        .map(nonNull -> String.join(", ", nonNull))
                        .orElse(""));

        var librariesByIdentifier = this.loadAndValidate(libraryIdentifiers);

        initializeEvalTime(nullableEvaluationDateTime);

        var successfullyLoadedLibraries = librariesByIdentifier.getResults();

        // here we initialize all libraries without emptying the cache for each library
        this.state.init(List.copyOf(successfullyLoadedLibraries.values()));

        // LUKETODO:  deal with this: since we need to deal with the use case of parameters
        // LUKETODO:  I think this may possibly be related to the unit test failures?
        // LUKETODO:  what does setParametersForContext() actually do, and why does it take a library that's not read?
        successfullyLoadedLibraries
                .values()
                .forEach(library -> this.setParametersForContext(library, contextParameter, parameters));

        initializeDebugMap(debugMap);

        // We need to reverse the order of Libraries since the CQL engine state has the last library first
        var reversedOrderLibraryIdentifiers = IntStream.range(0, successfullyLoadedLibraries.size())
                .map(index -> successfullyLoadedLibraries.size() - 1 - index)
                .mapToObj(index ->
                        List.copyOf(successfullyLoadedLibraries.keySet()).get(index))
                .toList();

        var evalResults = new LinkedHashMap<SearchableLibraryIdentifier, EvaluationResult>();

        // LUKETODO:  better algorithm to capture compile errors
        var errors = new LinkedHashMap<SearchableLibraryIdentifier, String>();
        for (var error : librariesByIdentifier.getErrors().entrySet()) {
            errors.put(SearchableLibraryIdentifier.fromIdentifier(error.getKey()), error.getValue());
        }

        for (var libraryIdentifier : reversedOrderLibraryIdentifiers) {
            var library = retrieveLibraryFromMap(successfullyLoadedLibraries, libraryIdentifier);
            var expressionSet = expressions == null ? this.getExpressionSet(library) : expressions;

            log.info(
                    "1234: Evaluating library: {} with expressions: [{}]",
                    libraryIdentifier.getId(),
                    String.join(", ", expressionSet));
            // LUKETODO:  I think the error handling is broken here:  we need to capture the error here and keep going
            var searchableIdentifier = SearchableLibraryIdentifier.fromIdentifier(libraryIdentifier);
            try {
                var evaluationResult = this.evaluateExpressions2(expressionSet);
                evalResults.put(searchableIdentifier, evaluationResult);
            } catch (Exception exception) {
                // LUKETODO: test this scenario, if possible
                // LUKETODO:  for now, just log and ignore this, but we need an "errors" construct
                log.error(
                        "1234: Failed to evaluate library: {} with expressions: {}", libraryIdentifier, expressionSet);
                var error = EXCEPTION_FOR_SUBJECT_ID_MESSAGE_TEMPLATE.formatted(
                        searchableIdentifier.getIdentifierId(), exception.getMessage());

                errors.put(searchableIdentifier, error);
            }
        }

        return new EvaluationResultsForMultiLib(evalResults, errors);
    }

    private Library retrieveLibraryFromMap(
            Map<VersionedIdentifier, Library> librariesByIdentifier, VersionedIdentifier libraryIdentifier) {
        if (librariesByIdentifier == null || librariesByIdentifier.isEmpty()) {
            throw new IllegalArgumentException("librariesByIdentifier can not be null or empty.");
        }
        if (libraryIdentifier.getVersion() != null) {
            if (!librariesByIdentifier.containsKey(libraryIdentifier)) {
                throw new IllegalArgumentException(
                        String.format("libraryIdentifier '%s' does not exist.", libraryIdentifier));
            }

            return librariesByIdentifier.get(libraryIdentifier);
        }

        return librariesByIdentifier.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(libraryIdentifier.getId()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("library id %s not found.", libraryIdentifier.getId())));
    }

    private void initializeEvalTime(ZonedDateTime nullableEvaluationDateTime) {
        this.state.setEvaluationDateTime(Objects.requireNonNullElseGet(nullableEvaluationDateTime, ZonedDateTime::now));
    }

    private void initializeDebugMap(DebugMap debugMap) {
        if (debugMap != null) {
            this.state.setDebugMap(debugMap);
        }
    }

    private void initializeState(Library library, DebugMap debugMap, ZonedDateTime evaluationDateTime) {
        if (evaluationDateTime == null) {
            evaluationDateTime = ZonedDateTime.now();
        }

        this.state.setEvaluationDateTime(evaluationDateTime);
        this.state.init(library);
        if (debugMap != null) {
            this.state.setDebugMap(debugMap);
        }
    }

    private EvaluationResult evaluateExpressions(Set<String> expressions) {
        EvaluationResult result = new EvaluationResult();

        this.state.beginEvaluation();
        try {
            for (String expression : expressions) {
                var currentLibrary = this.state.getCurrentLibrary();
                ExpressionDef def = Libraries.resolveExpressionRef(expression, currentLibrary);

                if (def == null) {
                    throw new CqlException(String.format("Unable to resolve expression \"%s.\"", expression));
                }

                if (def instanceof FunctionDef) {
                    continue;
                }

                try {
                    var action = getState().shouldDebug(def);
                    state.pushActivationFrame(def, def.getContext());
                    try {
                        log.info(
                                "1234: OLD visit lib: {} expression: {}",
                                currentLibrary.getIdentifier().getId(),
                                expression);
                        final var object = this.evaluationVisitor.visitExpressionDef(def, this.state);
                        result.expressionResults.put(
                                expression, new ExpressionResult(object, this.state.getEvaluatedResources()));
                        this.state.logDebugResult(def, object, action);
                    } finally {
                        this.state.popActivationFrame();
                    }
                } catch (CqlException ce) {
                    processException(ce, def);
                } catch (Exception e) {
                    processException(
                            e, def, String.format("Error evaluating expression %s: %s", expression, e.getMessage()));
                }
            }
        } finally {
            this.state.endEvaluation();
        }

        result.setDebugResult(this.state.getDebugResult());

        return result;
    }

    // LUKETODO:  this is not working and we need to meet the following requirements:
    // 1) get the current library, evaluate its expressions, and then make sure it gets popped off the stack
    // 2)
    private EvaluationResult evaluateExpressions2(Set<String> expressions) {
        EvaluationResult result = new EvaluationResult();

        this.state.beginEvaluation();
        try {
            for (String expression : expressions) {
                var currentLibrary = this.state.getCurrentLibrary();
                ExpressionDef def = Libraries.resolveExpressionRef(expression, currentLibrary);

                if (def == null) {
                    throw new CqlException(String.format("Unable to resolve expression \"%s.\"", expression));
                }

                if (def instanceof FunctionDef) {
                    continue;
                }

                try {
                    var action = getState().shouldDebug(def);
                    state.pushActivationFrame(def, def.getContext());
                    try {
                        log.info(
                                "1234: NEW visit lib: {} expression: {}",
                                currentLibrary.getIdentifier().getId(),
                                expression);
                        final var object = this.evaluationVisitor.visitExpressionDef(def, this.state);
                        result.expressionResults.put(
                                expression, new ExpressionResult(object, this.state.getEvaluatedResources()));
                        this.state.logDebugResult(def, object, action);
                    } finally {
                        this.state.popActivationFrame();
                    }
                } catch (CqlException ce) {
                    processException(ce, def);
                } catch (Exception e) {
                    processException(
                            e, def, String.format("Error evaluating expression %s: %s", expression, e.getMessage()));
                }
            }
        } finally {
            this.state.endEvaluation();
            // LUKETODO:  do we need to clear out the values() as well?
            // We are moving the evaluated resources off the stack so we can work on the next ones
            this.state.clearEvaluatedResources();
            // We are moving the library off the stack so we can work on the next one
            this.state.exitLibrary(true);
        }

        // LUKETODO:  break this out of the loop?
        result.setDebugResult(this.state.getDebugResult());

        return result;
    }

    private void setParametersForContext(
            Library library, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        if (contextParameter != null) {
            state.setContextValue(contextParameter.getLeft(), contextParameter.getRight());
        }

        state.setParameters(library, parameters);
    }

    private Library loadAndValidate(VersionedIdentifier libraryIdentifier) {

        var errors = new ArrayList<CqlCompilerException>();
        var library = this.environment
                .getLibraryManager()
                .resolveLibrary(libraryIdentifier, errors)
                .getLibrary();

        if (library == null) {
            throw new CqlException(String.format(
                    "Unable to load library %s",
                    libraryIdentifier.getId()
                            + (libraryIdentifier.getVersion() != null ? "-" + libraryIdentifier.getVersion() : "")));
        }

        if (CqlCompilerException.hasErrors(errors)) {
            throw new CqlException(String.format(
                    "library %s loaded, but had errors: %s",
                    libraryIdentifier.getId()
                            + (libraryIdentifier.getVersion() != null ? "-" + libraryIdentifier.getVersion() : ""),
                    errors.stream().map(Throwable::getMessage).collect(Collectors.joining(", "))));
        }

        if (this.engineOptions.contains(Options.EnableValidation)) {
            this.validateTerminologyRequirements(library);
            this.validateDataRequirements(library);
            // TODO: Validate Expressions as well?
        }

        // We probably want to just load all relevant libraries into
        // memory before we start evaluation. This will further separate
        // environment from state.
        if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
            for (IncludeDef include : library.getIncludes().getDef()) {
                this.loadAndValidate(new VersionedIdentifier()
                        .withSystem(NamespaceManager.getUriPart(include.getPath()))
                        .withId(NamespaceManager.getNamePart(include.getPath()))
                        .withVersion(include.getVersion()));
            }
        }

        return library;
    }

    private String showLibs(Collection<VersionedIdentifier> libraryIdentifiers) {
        return libraryIdentifiers.stream()
                .map(lib -> lib.getId() + (lib.getVersion() != null ? "-" + lib.getVersion() : ""))
                .collect(Collectors.joining(", "));
    }

    private static class LibraryValidationResult {
        private final Library library;
        private final List<CqlCompilerException> errors;

        public LibraryValidationResult(Library library, List<CqlCompilerException> errors) {
            this.library = library;
            this.errors = errors;
        }

        public Library getLibrary() {
            return library;
        }

        public List<CqlCompilerException> getErrors() {
            return errors;
        }
    }

    // LUKETODO: document error handling
    private LoadedLibrariesForMultiLib loadAndValidate(List<VersionedIdentifier> libraryIdentifiers) {

        var errorsById = new LinkedHashMap<VersionedIdentifier, List<CqlCompilerException>>();

        var resolvedLibraries = this.environment.getLibraryManager().resolveLibraries(libraryIdentifiers, errorsById);

        var idsToLibraries = getLibrariesByVersionedIdentifier(libraryIdentifiers, resolvedLibraries, errorsById);

        // We couldn't load any libraries:  instead of throwing just collect the errors now
        if (idsToLibraries.isEmpty()) {
            var errorsForLibs = new LinkedHashMap<VersionedIdentifier, String>();

            for (VersionedIdentifier libraryIdentifier : libraryIdentifiers) {
                errorsForLibs.put(
                        libraryIdentifier, "Unable to load libraries: %s".formatted(showLibs(libraryIdentifiers)));
            }

            return new LoadedLibrariesForMultiLib(
                    new LinkedHashMap<>(), // empty
                    errorsForLibs);
        }

        var errorsForLibs = new LinkedHashMap<VersionedIdentifier, String>();
        if (CqlCompilerException.hasErrors(
                errorsById.values().stream().flatMap(Collection::stream).toList())) {
            for (Map.Entry<VersionedIdentifier, List<CqlCompilerException>> entry : errorsById.entrySet()) {
                var libraryIdentifier = entry.getKey();
                var exceptions = entry.getValue();

                var joinedErrorMessages = "library %s loaded, but had errors: %s"
                        .formatted(
                                libraryIdentifier.getId(),
                                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

                errorsForLibs.put(libraryIdentifier, joinedErrorMessages);
            }
        }

        if (this.engineOptions.contains(Options.EnableValidation)) {
            idsToLibraries.values().forEach(library -> {
                this.validateDataRequirements(library);
                this.validateDataRequirements(library);
            });
            // TODO: Validate Expressions as well?
        }

        // We probably want to just load all relevant libraries into
        // memory before we start evaluation. This will further separate
        // environment from state.
        for (Library library : idsToLibraries.values()) {
            try {
                if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
                    for (IncludeDef include : library.getIncludes().getDef()) {
                        // LUKETODO: consider tweaking the error message to include the library containing the include
                        this.loadAndValidate(new VersionedIdentifier()
                                .withSystem(NamespaceManager.getUriPart(include.getPath()))
                                .withId(NamespaceManager.getNamePart(include.getPath()))
                                .withVersion(include.getVersion()));
                    }
                }
            } catch (CqlException | CqlCompilerException exception) {
                // As with previous code, per searched library identifier, this is an all or nothing operation:
                // stop at the first Exception and don't capture subsequent errors for subsequent included libraries.
                errorsForLibs.put(library.getIdentifier(), exception.getMessage());
                // LUKETODO:  this is gross:  we're effectively removing the library from the results if we get an error
                // consider a more immutable approach
                idsToLibraries.remove(library.getIdentifier());
            }
        }

        return new LoadedLibrariesForMultiLib(idsToLibraries, errorsForLibs);
    }

    //    private String formatCqlCompilerExceptionMessage(
    //            VersionedIdentifier libraryIdentifier, CqlCompilerException exception) {
    //        return "library %s loaded, but had errors: %s".formatted(libraryIdentifier.getId()
    //                + (libraryIdentifier.getVersion() != null ? "-" + libraryIdentifier.getVersion() : ""),
    //                errors.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));
    //    }

    private LinkedHashMap<VersionedIdentifier, Library> getLibrariesByVersionedIdentifier(
            List<VersionedIdentifier> libraryIdentifiersUsedToQuery,
            List<CompiledLibrary> resolvedLibraries,
            LinkedHashMap<VersionedIdentifier, List<CqlCompilerException>> errorsById) {

        // LUKETODO:  why do I get duped IDs in resolvedLibraries?
        log.info(
                "1234: Getting libraries by versioned identifier: {} and resolved: {}",
                libraryIdentifiersUsedToQuery.stream()
                        .map(VersionedIdentifier::getId)
                        .toList(),
                resolvedLibraries.stream().map(x -> x.getIdentifier().getId()).toList());

        var nonErroredIdentifiers = libraryIdentifiersUsedToQuery.stream()
                .filter(ident -> !errorsById.containsKey(ident))
                .toList();

        if (nonErroredIdentifiers.size() != resolvedLibraries.size()) {
            // LUKETODO:  why are we getting this from the FHIR multi-lib tests?
            // LUKETODO:  we get this because we're testing the cached and it only has a single library
            throw new CqlException(
                    "Something went wrong with resolving libraries: expected %d non-errored libraries, but got %d."
                            .formatted(libraryIdentifiersUsedToQuery.size(), resolvedLibraries.size()));
        }

        for (int index = 0; index < nonErroredIdentifiers.size(); index++) {
            var versionedIdentifierFromQuery = nonErroredIdentifiers.get(index);
            var compiledLibrary = resolvedLibraries.get(index);

            // LUKETODO:  handle version comparisons later:  only check if the QUERYING id contains a version
            // LUKETODO:  add testing for version mistmatches
            if (!versionedIdentifierFromQuery
                    .getId()
                    .equals(compiledLibrary.getIdentifier().getId())) {

                throw new CqlIncludeException(
                        "Library identifiers are mismatched: query id: %s vs compiled library id: %s"
                                .formatted(
                                        versionedIdentifierFromQuery.getId(),
                                        compiledLibrary.getIdentifier().getId()),
                        versionedIdentifierFromQuery.getSystem(),
                        versionedIdentifierFromQuery.getId(),
                        versionedIdentifierFromQuery.getVersion());
            }
        }

        return resolvedLibraries.stream()
                .map(CompiledLibrary::getLibrary)
                .collect(Collectors.toMap(
                        Library::getIdentifier,
                        Function.identity(),
                        (existing, replacement) -> {
                            throw new CqlException("Duplicate library identifier found: %s".formatted(existing));
                        },
                        LinkedHashMap::new));
    }

    private void validateDataRequirements(Library library) {
        // TODO: What we actually need here is a check of the actual retrieves, based on data requirements
        if (library.getUsings() != null
                && library.getUsings().getDef() != null
                && !library.getUsings().getDef().isEmpty()) {
            for (UsingDef using : library.getUsings().getDef()) {
                // Skip system using since the context automatically registers that.
                if (using.getUri().equals("urn:hl7-org:elm-types:r1")) {
                    continue;
                }

                if (this.environment.getDataProviders() == null
                        || !this.environment.getDataProviders().containsKey(using.getUri())) {
                    throw new IllegalArgumentException(String.format(
                            "Library %1$s is using %2$s and no data provider is registered for uri %2$s.",
                            this.getLibraryDescription(library.getIdentifier()), using.getUri()));
                }
            }
        }
    }

    private void validateTerminologyRequirements(Library library) {
        // TODO: Smarter validation would be to checkout and see if any retrieves
        // Use terminology, and to check for any codesystem lookups.
        if ((library.getCodeSystems() != null
                        && library.getCodeSystems().getDef() != null
                        && !library.getCodeSystems().getDef().isEmpty())
                || (library.getCodes() != null
                        && library.getCodes().getDef() != null
                        && !library.getCodes().getDef().isEmpty())
                || (library.getValueSets() != null
                        && library.getValueSets().getDef() != null
                        && !library.getValueSets().getDef().isEmpty())) {
            if (this.environment.getTerminologyProvider() == null) {
                throw new IllegalArgumentException(String.format(
                        "Library %s has terminology requirements and no terminology provider is registered.",
                        this.getLibraryDescription(library.getIdentifier())));
            }
        }
    }

    private String getLibraryDescription(VersionedIdentifier libraryIdentifier) {
        return libraryIdentifier.getId()
                + (libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
    }

    private Set<String> getExpressionSet(Library library) {
        Set<String> expressionNames = new LinkedHashSet<>();
        if (library.getStatements() != null && library.getStatements().getDef() != null) {
            for (ExpressionDef ed : library.getStatements().getDef()) {
                expressionNames.add(ed.getName());
            }
        }

        return expressionNames;
    }

    public void processException(CqlException e, Element element) {
        if (e.getSourceLocator() == null) {
            e.setSourceLocator(SourceLocator.fromNode(element, this.getState().getCurrentLibrary()));
            DebugAction action = state.shouldDebug(e);
            if (action != DebugAction.NONE) {
                state.logDebugError(e);
            }
        }

        throw e;
    }

    public void processException(Exception e, Element element, String message) {
        CqlException ce = new CqlException(
                message, e, SourceLocator.fromNode(element, this.getState().getCurrentLibrary()));
        DebugAction action = state.shouldDebug(ce);
        if (action != DebugAction.NONE) {
            state.logDebugError(ce);
        }
        throw ce;
    }
}
