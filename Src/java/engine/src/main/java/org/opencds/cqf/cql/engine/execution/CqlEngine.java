package org.opencds.cqf.cql.engine.execution;

import static org.opencds.cqf.cql.engine.execution.NamespaceHelper.getNamePart;
import static org.opencds.cqf.cql.engine.execution.NamespaceHelper.getUriPart;

import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.cqframework.cql.elm.execution.IncludeDef;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.UsingDef;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

/**
 * NOTE: Several possible approaches to traversing the ELM tree for execution:
 *
 * 1. "Executable" Node Hierarchy: Create nodes for each ELM type and deserialize into these nodes
 * This option works well, but is problematic for maintenance because Java doesn't have partial classes.
 * There also doesn't seem to be a way to tell JAXB which hierarchy to use if you have two different hierarchies
 * for the same schema (Trackable-based ELM used by the CQL-to-ELM translator, and Executable-based ELM used by the engine).
 * This could potentially be a bonus though, as it forces the engine to not take a dependency on the translator, forcing
 * a clean separation between the translator and the engine.
 *
 * 2. Visitor Pattern: This option is potentially simpler to implement, however:
 *  a. The visitor pattern doesn't lend itself well to aggregation of results, which is the real work of each node anyway
 *  b. Extensibility is compromised, difficult to introduce new nodes (unlikely to be a practical issue though)
 *  c. Without lambdas, the cost of traversal is quite high due to the expensive if-then-else chains in the visitor nodes
 *
 *  So, opting for the Executable Node Hierarchy for now, knowing that it creates a potential maintenance issue, but
 *  this is acceptable because the ELM Hierarchy is settling down, and so long as all the non-generated code is at the
 *  end, this should be easy to maintain. In addition, it will be much more performant, and lend itself much better to
 *  the aggregation of values from child nodes.
 */

public class CqlEngine {

    public enum Options {
        EnableExpressionCaching,
        EnableValidation
    }

    private LibraryLoader libraryLoader;
    private Map<String, DataProvider> dataProviders;
    private TerminologyProvider terminologyProvider;
    private EnumSet<Options> engineOptions;

    public CqlEngine(LibraryLoader libraryLoader) {
        this(libraryLoader, null, null, null);
    }

    public CqlEngine(LibraryLoader libraryLoader, Map<String, DataProvider> dataProviders, TerminologyProvider terminologyProvider) {
        this(libraryLoader, dataProviders, terminologyProvider, null);
    }

    public CqlEngine(LibraryLoader libraryLoader, EnumSet<Options> engineOptions) {
        this(libraryLoader, null, null, engineOptions);
    }

    public CqlEngine(LibraryLoader libraryLoader, Map<String, DataProvider> dataProviders, TerminologyProvider terminologyProvider, EnumSet<Options> engineOptions) {

        if (libraryLoader == null) {
            throw new IllegalArgumentException("libraryLoader can not be null.");
        }

        if (engineOptions == null) {
            engineOptions = EnumSet.of(org.opencds.cqf.cql.engine.execution.CqlEngine.Options.EnableExpressionCaching);
        }

        this.libraryLoader = libraryLoader;
        this.dataProviders = dataProviders;
        this.terminologyProvider = terminologyProvider;
        this.engineOptions = engineOptions;
    }

    // TODO: Add debugging info as a parameter.
    public EvaluationResult evaluate(String libraryName) {
        return this.evaluate(libraryName, null, null, null);
    }

    public EvaluationResult evaluate(String libraryName, Set<String> expressions) {
        return this.evaluate(libraryName, expressions, null, null);
    }

    public EvaluationResult evaluate(String libraryName, Set<String> expressions, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryName, expressions, contextParameter, null);
    }

    public EvaluationResult evaluate(String libraryName, Set<String> expressions, Map<String, Object> parameters) {
        return this.evaluate(libraryName, expressions, null, parameters);
    }

    public EvaluationResult evaluate(String libraryName, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryName, null, contextParameter, null);
    }

    public EvaluationResult evaluate(String libraryName, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        return this.evaluate(libraryName, null, contextParameter, parameters);
    }

    public EvaluationResult evaluate(String libraryName, Map<String, Object> parameters) {
        return this.evaluate(libraryName, null, null, parameters);
    }

    public EvaluationResult evaluate(String libraryName, Set<String> expressions, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        return this.evaluate(new VersionedIdentifier().withId(libraryName), expressions, contextParameter, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier) {
        return this.evaluate(libraryIdentifier, null, null, null, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Set<String> expressions) {
        return this.evaluate(libraryIdentifier, expressions, null, null, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Set<String> expressions, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryIdentifier, expressions, contextParameter, null, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Set<String> expressions, Map<String, Object> parameters) {
        return this.evaluate(libraryIdentifier, expressions, null, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Pair<String, Object> contextParameter) {
        return this.evaluate(libraryIdentifier, null, contextParameter, null, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        return this.evaluate(libraryIdentifier, null, contextParameter, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Map<String, Object> parameters) {
        return this.evaluate(libraryIdentifier, null, null, parameters, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Set<String> expressions, Pair<String, Object> contextParameter, Map<String, Object> parameters, DebugMap debugMap) {
        return this.evaluate(libraryIdentifier, expressions, contextParameter, parameters, debugMap, null);
    }

    public EvaluationResult evaluate(VersionedIdentifier libraryIdentifier, Set<String> expressions, Pair<String, Object> contextParameter, Map<String, Object> parameters, DebugMap debugMap, ZonedDateTime evaluationDateTime) {
        // TODO: Figure out way to validate / invalidate library cache
        Map<VersionedIdentifier, Library> libraryCache = new HashMap<>();

        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier can not be null.");
        }

        Library library = this.loadAndValidate(libraryCache, libraryIdentifier);

        if (expressions == null) {
            expressions = this.getExpressionSet(library);
        }

        // TODO: Some testing to see if it's more performant to reset a context rather than create a new one.
        Context context = this.initializeContext(libraryCache, library, debugMap, evaluationDateTime);
        this.setParametersForContext(library, context, contextParameter, parameters);

        return this.evaluateExpressions(context, expressions);
    }

    private EvaluationResult evaluateExpressions(Context context, Set<String> expressions) {
        EvaluationResult  result = new EvaluationResult();

        for (String expression : expressions) {
            ExpressionDef def = context.resolveExpressionRef(expression);

            if (def == null) {
                throw new CqlException(String.format("Unable to resolve expression \"%s.\"", expression));
            }

            // TODO: We should probably move this validation further up the chain.
            // For example, we should tell the user that they've tried to evaluate a function def through incorrect
            // CQL or input parameters. And the code that gather the list of expressions to evaluate together should
            // not include function refs.
            if (def instanceof FunctionDef) {
                continue;
            }

            context.enterContext(def.getContext());
            Object object = def.evaluate(context);
            result.expressionResults.put(expression,
                new ExpressionResult(object, context.getEvaluatedResources()));
        }

        result.setDebugResult(context.getDebugResult());
        context.clearExpressions();

        return result;
    }

    private void setParametersForContext(Library library, Context context, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        if (contextParameter != null) {
            context.setContextValue(contextParameter.getLeft(), contextParameter.getRight());
        }

        if (parameters != null) {
            for (Map.Entry<String, Object> parameterValue : parameters.entrySet()) {
               context.setParameter(library.getLocalId(), parameterValue.getKey(), parameterValue.getValue());
            }

            if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
                for (IncludeDef def : library.getIncludes().getDef()) {
                    String name = def.getLocalIdentifier();
                    for (Map.Entry<String, Object> parameterValue : parameters.entrySet()) {
                        context.setParameter(name, parameterValue.getKey(), parameterValue.getValue());
                    }
                }
            }
        }
    }

    private Context initializeContext(Map<VersionedIdentifier, Library> libraryCache, Library library, DebugMap debugMap, ZonedDateTime evaluationDateTime) {
        // Context requires an initial library to init properly.
        // TODO: Allow context to be initialized with multiple libraries
        Context context = evaluationDateTime == null ? new Context(library) : new Context(library, evaluationDateTime);

        // TODO: Does the context actually need a library loaded if all the libraries are prefetched?
        // We'd have to make sure we include the dependencies too.
        context.registerLibraryLoader(new InMemoryLibraryLoader(libraryCache.values()));

        if (this.engineOptions.contains(Options.EnableExpressionCaching)) {
            context.setExpressionCaching(true);
        }

        if (this.terminologyProvider != null) {
            context.registerTerminologyProvider(this.terminologyProvider);
        }

        if (this.dataProviders != null) {
            for (Map.Entry<String, DataProvider> pair : this.dataProviders.entrySet()) {
                context.registerDataProvider(pair.getKey(), pair.getValue());
            }
        }

        context.setDebugMap(debugMap);

        return context;
    }

    private Library loadAndValidate(Map<VersionedIdentifier, Library> libraryCache, VersionedIdentifier libraryIdentifier) {
        Library library;
        if (libraryCache.containsKey(libraryIdentifier)) {
            return libraryCache.get(libraryIdentifier);
        }

        library = this.libraryLoader.load(libraryIdentifier);

        if (library == null) {
            throw new IllegalArgumentException(String.format("Unable to load library %s",
                libraryIdentifier.getId() + (libraryIdentifier.getVersion() != null ? "-" + libraryIdentifier.getVersion() : "")));
        }

        if (this.engineOptions.contains(Options.EnableValidation)) {
            this.validateTerminologyRequirements(library);
            this.validateDataRequirements(library);
            // TODO: Validate Expressions as well?
        }

        if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
            for (IncludeDef include : library.getIncludes().getDef()) {
                this.loadAndValidate(libraryCache,
                    new VersionedIdentifier()
                    .withSystem(getUriPart(include.getPath()))
                    .withId(getNamePart(include.getPath()))
                    .withVersion(include.getVersion()));
            }
        }

        libraryCache.put(libraryIdentifier, library);
        return library;
    }

    private void validateDataRequirements(Library library) {
        // TODO: What we actually need here is a check of the actual retrieves, based on data requirements
        if (library.getUsings() != null && library.getUsings().getDef() != null && !library.getUsings().getDef().isEmpty())
        {
            for (UsingDef using : library.getUsings().getDef()) {
                // Skip system using since the context automatically registers that.
                if (using.getUri().equals("urn:hl7-org:elm-types:r1"))
                {
                    continue;
                }

                if (this.dataProviders == null || !this.dataProviders.containsKey(using.getUri())) {
                    throw new IllegalArgumentException(String.format("Library %1$s is using %2$s and no data provider is registered for uri %2$s.",
                    this.getLibraryDescription(library.getIdentifier()),
                    using.getUri()));
                }
            }
        }
    }

    private void validateTerminologyRequirements(Library library) {
        // TODO: Smarter validation would be to checkout and see if any retrieves
        // Use terminology, and to check for any codesystem lookups.
        if ((library.getCodeSystems() != null && library.getCodeSystems().getDef() != null && !library.getCodeSystems().getDef().isEmpty()) ||
            (library.getCodes() != null  && library.getCodes().getDef() != null && !library.getCodes().getDef().isEmpty()) ||
            (library.getValueSets() != null  && library.getValueSets().getDef() != null && !library.getValueSets().getDef().isEmpty())) {
            if (this.terminologyProvider == null) {
                throw new IllegalArgumentException(String.format("Library %s has terminology requirements and no terminology provider is registered.",
                    this.getLibraryDescription(library.getIdentifier())));
            }
        }
    }

    private String getLibraryDescription(VersionedIdentifier libraryIdentifier) {
        return libraryIdentifier.getId() + (libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
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
}
