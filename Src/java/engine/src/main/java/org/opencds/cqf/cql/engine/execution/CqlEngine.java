package org.opencds.cqf.cql.engine.execution;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.CompiledLibraryResult;
import org.cqframework.cql.cql2elm.CqlCompilerException;
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
 * <p/>
 * Visitor pattern reduces the process to convert EML Tree to Executable ELM tree and thus reduces a potential maintenance issue.
 *
 */
@SuppressWarnings("squid:S115")
public class CqlEngine {
    private static final Logger log = LoggerFactory.getLogger(CqlEngine.class);

    private static final String EXCEPTION_FOR_SUBJECT_ID_MESSAGE_TEMPLATE = "Exception for Library: %s, Message: %s";

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
        requireNonNull(environment.getLibraryManager(), "Environment LibraryManager can not be null.");
        this.environment = environment;

        this.engineOptions = engineOptions != null ? engineOptions : EnumSet.of(Options.EnableExpressionCaching);
        this.state = new State(environment, this.engineOptions);

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

        return evaluate(
                        Collections.singletonList(libraryIdentifier),
                        expressions,
                        contextParameter,
                        parameters,
                        debugMap,
                        evaluationDateTime)
                .getOnlyResultOrThrow();
    }

    public EvaluationResultsForMultiLib evaluate(
            List<VersionedIdentifier> libraryIdentifiers,
            Set<String> expressions,
            Pair<String, Object> contextParameter,
            Map<String, Object> parameters,
            DebugMap debugMap,
            ZonedDateTime nullableEvaluationDateTime) {

        if (libraryIdentifiers == null || libraryIdentifiers.isEmpty() || libraryIdentifiers.get(0) == null) {
            throw new IllegalArgumentException("libraryIdentifier can not be null or empty.");
        }

        var loadMultiLibResult = this.loadAndValidate(libraryIdentifiers);

        initializeEvalTime(nullableEvaluationDateTime);

        // here we initialize all libraries without emptying the cache for each library
        this.state.init(loadMultiLibResult.getAllLibraries());

        // We must do this only once per library evaluation otherwise, we may clear the cache prematurely
        if (contextParameter != null) {
            state.setContextValue(contextParameter.getLeft(), contextParameter.getRight());
        }

        loadMultiLibResult.getAllLibraries().forEach(library -> state.setParameters(library, parameters));

        initializeDebugMap(debugMap);

        // We need to reverse the order of Libraries since the CQL engine state has the last library first
        var reversedOrderLibraryIdentifiers = IntStream.range(0, loadMultiLibResult.libraryCount())
                .map(index -> loadMultiLibResult.getAllLibraryIds().size() - 1 - index)
                .mapToObj(loadMultiLibResult::getLibraryIdentifierAtIndex)
                .toList();

        var resultBuilder = EvaluationResultsForMultiLib.builder(loadMultiLibResult);

        for (var libraryIdentifier : reversedOrderLibraryIdentifiers) {
            var library = loadMultiLibResult.retrieveLibrary(libraryIdentifier);
            var expressionSet = expressions == null ? this.getExpressionSet(library) : expressions;

            var joinedExpressions = String.join(", ", expressionSet);
            log.debug("Evaluating library: {} with expressions: [{}]", libraryIdentifier.getId(), joinedExpressions);
            try {
                var evaluationResult = this.evaluateExpressions(expressionSet);
                resultBuilder.addResult(libraryIdentifier, evaluationResult);
            } catch (RuntimeException exception) {
                var error = EXCEPTION_FOR_SUBJECT_ID_MESSAGE_TEMPLATE.formatted(
                        libraryIdentifier.getId(), exception.getMessage());
                log.error(error);

                resultBuilder.addException(libraryIdentifier, exception);
            }
        }

        return resultBuilder.build();
    }

    private void initializeEvalTime(ZonedDateTime nullableEvaluationDateTime) {
        this.state.setEvaluationDateTime(Objects.requireNonNullElseGet(nullableEvaluationDateTime, ZonedDateTime::now));
    }

    private void initializeDebugMap(DebugMap debugMap) {
        if (debugMap != null) {
            this.state.setDebugMap(debugMap);
        }
    }

    /**
     * Evaluate one library within either a single library or multiple library context.
     * Once evaluation is done, ensure the stack for the library and evaluated resources is popped, so as to
     * permit evaluation of the next library, if applicable.
     * <p/>
     * @param expressions Expressions to evaluate
     * @return The EvaluationResult containing the results of the evaluated expressions for the current library.
     */
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
            // We are moving the evaluated resources off the stack so we can work on the next ones
            this.state.clearEvaluatedResources();
            // We are moving the library off the stack so we can work on the next one
            this.state.exitLibrary(true);
        }

        result.setDebugResult(this.state.getDebugResult());

        return result;
    }

    private void loadAndValidate(VersionedIdentifier libraryIdentifier) {

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
    }

    /**
     * Evaluate multiple libraries, loading and validating them as needed.
     * <p/>
     * In most cases, errors in one Library will not halt the evaluation of other libraries, but will merely be
     * captured in the result object.  However, in some cases, such as when a library cannot be loaded at all, this
     * method will throw an exception.
     * <p/>
     * @param libraryIdentifiers The list of library identifiers to load and evaluate.
     * @return A result object containing the evaluation results and any exceptions encountered during the process.
     */
    private LoadMultiLibResult loadAndValidate(List<VersionedIdentifier> libraryIdentifiers) {

        var resolvedLibraryResults = this.environment.getLibraryManager().resolveLibraries(libraryIdentifiers);

        var resultBuilder = LoadMultiLibResult.builder();

        if (CqlCompilerException.hasErrors(resolvedLibraryResults.allErrors())) {
            for (CompiledLibraryResult libraryResult : resolvedLibraryResults.allResults()) {
                if (!libraryResult.errors().isEmpty()) {
                    var identifier = libraryResult.compiledLibrary().getIdentifier();
                    resultBuilder.addException(identifier, wrapException(identifier, libraryResult.errors()));
                }
            }
        }

        var libraries = resolvedLibraryResults.allLibrariesWithoutErrorSeverity();

        validateLibrariesIfNeeded(libraries);

        // We probably want to just load all relevant libraries into
        // memory before we start evaluation. This will further separate
        // environment from state.
        for (Library library : libraries) {
            try {
                if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
                    for (IncludeDef include : library.getIncludes().getDef()) {
                        this.loadAndValidate(new VersionedIdentifier()
                                .withSystem(NamespaceManager.getUriPart(include.getPath()))
                                .withId(NamespaceManager.getNamePart(include.getPath()))
                                .withVersion(include.getVersion()));
                    }
                }
                resultBuilder.addResult(library.getIdentifier(), library);
            } catch (CqlException | CqlCompilerException exception) {
                // As with previous code, per searched library identifier, this is an all or nothing operation:
                // stop at the first Exception and don't capture subsequent errors for subsequent included libraries.
                resultBuilder.addException(library.getIdentifier(), exception);
            }
        }

        return resultBuilder.build();
    }

    private void validateLibrariesIfNeeded(List<Library> libraries) {
        if (this.engineOptions.contains(Options.EnableValidation)) {
            libraries.forEach(library -> {
                this.validateTerminologyRequirements(library);
                this.validateDataRequirements(library);
            });
            // TODO: Validate Expressions as well?
        }
    }

    private CqlException wrapException(VersionedIdentifier libraryIdentifier, List<CqlCompilerException> exceptions) {
        return new CqlException("Library %s loaded, but had errors: %s"
                .formatted(
                        libraryIdentifier.getId()
                                + (libraryIdentifier.getVersion() != null ? "-" + libraryIdentifier.getVersion() : ""),
                        exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", "))));
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
                                && !library.getValueSets().getDef().isEmpty())
                        && this.environment.getTerminologyProvider() == null) {
            throw new IllegalArgumentException(String.format(
                    "Library %s has terminology requirements and no terminology provider is registered.",
                    this.getLibraryDescription(library.getIdentifier())));
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
