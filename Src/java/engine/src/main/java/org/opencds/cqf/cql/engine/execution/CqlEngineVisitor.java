package org.opencds.cqf.cql.engine.execution;

import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Date;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.opencds.cqf.cql.engine.debug.DebugAction;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.elm.visiting.*;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

import static org.opencds.cqf.cql.engine.execution.NamespaceHelper.getNamePart;
import static org.opencds.cqf.cql.engine.execution.NamespaceHelper.getUriPart;

/**
 * NOTE: Several possible approaches to traversing the ELM tree for execution:
 * <p>
 * 1. "Executable" Node Hierarchy: Create nodes for each ELM type and deserialize into these nodes
 * This option works well, but is problematic for maintenance because Java doesn't have partial classes.
 * There also doesn't seem to be a way to tell JAXB which hierarchy to use if you have two different hierarchies
 * for the same schema (Trackable-based ELM used by the CQL-to-ELM translator, and Executable-based ELM used by the engine).
 * This could potentially be a bonus though, as it forces the engine to not take a dependency on the translator, forcing
 * a clean separation between the translator and the engine.
 * <p>
 * 2. Visitor Pattern: This option is potentially simpler to implement, however:
 * a. The visitor pattern doesn't lend itself well to aggregation of results, which is the real work of each node anyway
 * b. Extensibility is compromised, difficult to introduce new nodes (unlikely to be a practical issue though)
 * c. Without lambdas, the cost of traversal is quite high due to the expensive if-then-else chains in the visitor nodes
 * <p>
 * So, opting for the Executable Node Hierarchy for now, knowing that it creates a potential maintenance issue, but
 * this is acceptable because the ELM Hierarchy is settling down, and so long as all the non-generated code is at the
 * end, this should be easy to maintain. In addition, it will be much more performant, and lend itself much better to
 * the aggregation of values from child nodes.
 */

public class CqlEngineVisitor extends ElmBaseLibraryVisitor<Object, State> {

    public enum Options {
        EnableExpressionCaching, EnableValidation
    }

    private Environment environment;
    private State state;
    private Cache cache;
    private EnumSet<Options> engineOptions;
    private CqlTranslatorOptions translatorOptions;


    public CqlEngineVisitor(Environment environment) {
        this(environment, null, null, null, null);
    }

    public CqlEngineVisitor(Environment environment, EnumSet<Options> engineOptions, CqlTranslatorOptions translatorOptions) {
        this(environment, null, null, engineOptions, translatorOptions);
    }

    public CqlEngineVisitor(Environment environment, State state, Cache cache, EnumSet<Options> engineOptions, CqlTranslatorOptions translatorOptions) {

        if (environment.getLibraryManager() == null) {
            throw new IllegalArgumentException("libraryLoader can not be null.");
        }

        if (engineOptions == null) {
            engineOptions = EnumSet.of(CqlEngineVisitor.Options.EnableExpressionCaching);
        }

        if (translatorOptions == null) {
            this.translatorOptions = createOptionsMin();
        } else {
            this.translatorOptions = translatorOptions;
        }

        this.environment = environment;
        if (state != null) {
            this.state = state;
        } else {
            this.state = new State();
        }

        this.state.setEnvironment(this.environment);

        if (cache != null) {
            this.cache = cache;
        } else {
            this.cache = new Cache();
        }

        this.state.setCache(this.cache);

        this.state.setVisitor(this);

        if (engineOptions != null) {
            this.engineOptions = engineOptions;
        }
    }

    public CqlTranslatorOptions createOptionsMin() {
        CqlTranslatorOptions result = new CqlTranslatorOptions();
        result.setOptions(CqlTranslatorOptions.Options.EnableDateRangeOptimization, CqlTranslatorOptions.Options.EnableLocators, CqlTranslatorOptions.Options.EnableResultTypes, CqlTranslatorOptions.Options.DisableListDemotion, CqlTranslatorOptions.Options.DisableListPromotion, CqlTranslatorOptions.Options.DisableMethodInvocation);

        return result;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public State getState() {
        return state;
    }

    public Cache getCache() {
        return cache;
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
        this.initializeState(libraryCache, library, debugMap, evaluationDateTime);
        this.setParametersForContext(library, contextParameter, parameters);

        return this.evaluateExpressions(expressions);
    }

    private void initializeState(Map<VersionedIdentifier, Library> libraryCache, Library library, DebugMap debugMap, ZonedDateTime evaluationDateTime) {
        if (evaluationDateTime != null) {
            this.state.setEvaluationDateTime(evaluationDateTime);
        } else {
            this.state.setEvaluationDateTime(ZonedDateTime.now());
        }

        this.state.init(library, new SystemDataProvider(), this.environment.getUcumService());

        this.state.setLibraryManager(this.environment.getLibraryManager());
        this.state.setTranslatorOptions(this.translatorOptions);

        if (this.engineOptions.contains(Options.EnableExpressionCaching)) {
            this.state.getCache().setExpressionCaching(true);
        }


        if (this.environment.getDataProviders() != null) {
            for (Map.Entry<String, DataProvider> pair : this.environment.getDataProviders().entrySet()) {
                this.state.registerDataProvider(pair.getKey(), pair.getValue());
            }
        }

        this.state.setDebugMap(debugMap);
    }

    private EvaluationResult evaluateExpressions(Set<String> expressions) {
        EvaluationResult result = new EvaluationResult();

        for (String expression : expressions) {
            ExpressionDef def = resolveExpressionRef(expression);

            if (def == null) {
                throw new CqlException(String.format("Unable to resolve expression \"%s.\"", expression));
            }

            if (def instanceof FunctionDef) {
                continue;
            }

            //this.state.enterContext(def.getContext());
            Object object = visitExpressionDef(def, this.state);
            //Object object = def.evaluate(context);
            result.expressionResults.put(expression, new ExpressionResult(object, this.state.getEvaluatedResources()));
        }

        System.out.println("printing keys:");
        for (Map.Entry superEntry : this.state.getCache().getExpressions().entrySet()) {
            System.out.println(superEntry.getKey());
            LinkedHashMap map = ((LinkedHashMap) superEntry.getValue());
            for (Object entry : map.entrySet()) {
                System.out.println("key:" + ((Map.Entry) entry).getKey() + "| value:" + ((ExpressionResult) ((Map.Entry) entry).getValue()).value);
            }
        }

        //result.setDebugResult(context.getDebugResult());
        this.state.clearExpressions();

        return result;
    }

    public ExpressionDef resolveExpressionRef(String name) {

        for (ExpressionDef expressionDef : state.getCurrentLibrary().getStatements().getDef()) {
            if (expressionDef.getName().equals(name)) {
                return expressionDef;
            }
        }

        throw new CqlException(String.format("Could not resolve expression reference '%s' in library '%s'.", name, state.getCurrentLibrary().getIdentifier().getId()));
    }

    private void setParametersForContext(Library library, Pair<String, Object> contextParameter, Map<String, Object> parameters) {
        if (contextParameter != null) {
            state.setContextValue(contextParameter.getLeft(), contextParameter.getRight());
        }

        state.setParameters(library, parameters);
    }

    private Library loadAndValidate(Map<VersionedIdentifier, Library> libraryCache, VersionedIdentifier libraryIdentifier) {
        Library library;
        if (libraryCache.containsKey(libraryIdentifier)) {
            return libraryCache.get(libraryIdentifier);
        }

        ArrayList<CqlCompilerException> errors = new ArrayList<CqlCompilerException>();
        library = this.environment.getLibraryManager().resolveLibrary(libraryIdentifier, translatorOptions, errors).getLibrary();

        if (library == null) {
            throw new IllegalArgumentException(String.format("Unable to load library %s", libraryIdentifier.getId() + (libraryIdentifier.getVersion() != null ? "-" + libraryIdentifier.getVersion() : "")));
        }

        if (this.engineOptions.contains(Options.EnableValidation)) {
            this.validateTerminologyRequirements(library);
            this.validateDataRequirements(library);
            // TODO: Validate Expressions as well?
        }

        if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
            for (IncludeDef include : library.getIncludes().getDef()) {
                this.loadAndValidate(libraryCache, new VersionedIdentifier().withSystem(getUriPart(include.getPath())).withId(getNamePart(include.getPath())).withVersion(include.getVersion()));
            }
        }

        libraryCache.put(libraryIdentifier, library);
        return library;
    }

    private void validateDataRequirements(Library library) {
        // TODO: What we actually need here is a check of the actual retrieves, based on data requirements
        if (library.getUsings() != null && library.getUsings().getDef() != null && !library.getUsings().getDef().isEmpty()) {
            for (UsingDef using : library.getUsings().getDef()) {
                // Skip system using since the context automatically registers that.
                if (using.getUri().equals("urn:hl7-org:elm-types:r1")) {
                    continue;
                }

                if (this.environment.getDataProviders() == null || !this.environment.getDataProviders().containsKey(using.getUri())) {
                    throw new IllegalArgumentException(String.format("Library %1$s is using %2$s and no data provider is registered for uri %2$s.", this.getLibraryDescription(library.getIdentifier()), using.getUri()));
                }
            }
        }
    }

    private void validateTerminologyRequirements(Library library) {
        // TODO: Smarter validation would be to checkout and see if any retrieves
        // Use terminology, and to check for any codesystem lookups.
        if ((library.getCodeSystems() != null && library.getCodeSystems().getDef() != null && !library.getCodeSystems().getDef().isEmpty()) || (library.getCodes() != null && library.getCodes().getDef() != null && !library.getCodes().getDef().isEmpty()) || (library.getValueSets() != null && library.getValueSets().getDef() != null && !library.getValueSets().getDef().isEmpty())) {
            if (this.environment.getTerminologyProvider() == null) {
                throw new IllegalArgumentException(String.format("Library %s has terminology requirements and no terminology provider is registered.", this.getLibraryDescription(library.getIdentifier())));
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

    private void processException(Exception e, Element element) {
        if (e instanceof CqlException) {
            CqlException ce = (CqlException) e;
            if (ce.getSourceLocator() == null) {
                ce.setSourceLocator(SourceLocator.fromNode(element, null));
                DebugAction action = state.shouldDebug(ce);
                if (action != DebugAction.NONE) {
                    state.logDebugError(ce);
                }
            }
            throw (RuntimeException) (e);
        } else {
            CqlException ce = new CqlException(e, SourceLocator.fromNode(element, null));
            DebugAction action = state.shouldDebug(ce);
            if (action != DebugAction.NONE) {
                state.logDebugError(ce);
            }
            throw (RuntimeException) ce;
        }
    }

    @Override
    public Object visitExpressionDef(ExpressionDef expressionDef, State state) {

        System.out.println("visit expression def:" + expressionDef.getName());

        if (expressionDef.getContext() != null) {
            state.enterContext(expressionDef.getContext());
        }
        try {
            state.pushEvaluatedResourceStack();
            VersionedIdentifier libraryId = state.getCurrentLibrary().getIdentifier();
            if (state.getCache().isExpressionCachingEnabled() && state.getCache().isExpressionCached(libraryId, expressionDef.getName())) {
                var er = state.getCache().getCachedExpression(libraryId, expressionDef.getName());
                state.getEvaluatedResources().addAll(er.evaluatedResources());
                System.out.println("value in cache:" + er.value());
                return er.value();
            }

            Object value = visitExpression(expressionDef.getExpression(), state);

            if (value instanceof ExpressionDef) {
                value = visitExpressionDef((ExpressionDef) value, state);
            }

            if (state.getCache().isExpressionCachingEnabled()) {
                System.out.println("Expression is cached:" + libraryId.getId() + "|" + expressionDef.getName());
                var er = new ExpressionResult(value, state.getEvaluatedResources());
                state.getCache().cacheExpression(libraryId, expressionDef.getName(), er);
            }

            return value;

        } catch (Exception e) {
            processException(e, expressionDef);
        } finally {
            state.popEvaluatedResourceStack();
            if (expressionDef.getContext() != null) {
                state.exitContext();
            }
        }
        return null;
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression elm, State state) {
        if (elm instanceof ExpandValueSet) {
            return visitExpandValueSet((ExpandValueSet) elm, state);
        } else {
            return super.visitUnaryExpression(elm, state);
        }
    }

    @Override
    public Object visitExpressionRef(ExpressionRef expressionRef, State state) {
        if(expressionRef instanceof FunctionRef) {
            return visitFunctionRef((FunctionRef) expressionRef, state);
        }
        return ExpressionRefEvaluator.internalEvaluate(expressionRef, state);
    }

    @Override
    public Object visitFunctionRef(FunctionRef elm, State state) {
        return new FunctionRefEvaluator().internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitAdd(Add add, State state) {
        Object left = visitExpression(add.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(add.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return AddEvaluator.add(left, right);
    }

    @Override
    public Object visitAbs(Abs abs, State state) {
        Object operand = visitExpression(abs.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return AbsEvaluator.abs(operand);
    }

    @Override
    public Object visitAfter(After after, State state) {
        Object left = visitExpression(after.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(after.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = after.getPrecision() == null ? null : after.getPrecision().value();

        return AfterEvaluator.after(left, right, precision, state);
    }

    @Override
    public Object visitAliasRef(AliasRef aliasRef, State state) {
        return AliasRefEvaluator.internalEvaluate(aliasRef.getName(), state);
    }

    @Override
    public Object visitAllTrue(AllTrue allTrue, State state) {
        Object src = visitExpression(allTrue.getSource(), state);
        return AllTrueEvaluator.allTrue(src);
    }


    @Override
    public Object visitAnd(And and, State state) {
        Object left = visitExpression(and.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(and.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        
        return AndEvaluator.and(left, right);
    }

    public Object visitAnyInCodeSystem(AnyInCodeSystem anyInCodeSystem, State state) {
        Object codes = visitExpression(anyInCodeSystem.getCodes(), state);
        Object codeSystem = visitExpression(anyInCodeSystem.getCodesystemExpression(), state);
        return AnyInCodeSystemEvaluator.internalEvaluate(codes, anyInCodeSystem.getCodesystem(), codeSystem, state);
    }

    @Override
    public Object visitInCodeSystem(InCodeSystem inCodeSystem, State state) {
        Object code = visitExpression(inCodeSystem.getCode(), state);
        Object cs = null;
        if (inCodeSystem.getCodesystem() != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(inCodeSystem.getCodesystem(), state);
        } else if (inCodeSystem.getCodesystemExpression() != null) {
            cs = visitExpression(inCodeSystem.getCodesystemExpression(), state);
        }

        return InCodeSystemEvaluator.inCodeSystem(code, cs, state);
    }

    @Override
    public Object visitAnyInValueSet(AnyInValueSet anyInValueSet, State state) {
        Object codes = visitExpression(anyInValueSet.getCodes(), state);
        Object valueset = visitExpression(anyInValueSet.getValuesetExpression(), state);

        return AnyInValueSetEvaluator.internalEvaluate(codes, anyInValueSet.getValueset(), valueset, state);
    }

    @Override
    public Object visitInValueSet(InValueSet inValueSet, State state) {

        Object code = visitExpression(inValueSet.getCode(), state);
        Object vs = null;
        if (inValueSet.getValueset() != null) {
            vs = ValueSetRefEvaluator.toValueSet(state, inValueSet.getValueset());
        } else if (inValueSet.getValuesetExpression() != null) {
            vs = visitExpression(inValueSet.getValuesetExpression(), state);
        }
        return InValueSetEvaluator.inValueSet(code, vs, state);
    }

    @Override
    public Object visitValueSetRef(ValueSetRef elm, State state) {
        return ValueSetRefEvaluator.internalEvaluate(state, elm);
    }

    @Override
    public Object visitXor(Xor elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return XorEvaluator.xor(left, right);
    }


    @Override
    public Object visitWidth(Width elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return WidthEvaluator.width(operand);
    }

    @Override
    public Object visitVariance(Variance variance, State state) {
        Object source = visitExpression(variance.getSource(), state);
        return VarianceEvaluator.variance(source, state);
    }

    @Override
    public Object visitAvg(Avg avg, State state) {
        Object src = visitExpression(avg.getSource(), state);
        return AvgEvaluator.avg(src, state);
    }

    @Override
    public Object visitDivide(Divide elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return DivideEvaluator.divide(left, right, state);
    }


    @Override
    public Object visitUpper(Upper elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return UpperEvaluator.upper(operand);
    }

    @Override
    public Object visitUnion(Union elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return UnionEvaluator.union(left, right, state);
    }

    @Override
    public Object visitGreater(Greater elm, State state) {
        try {
            System.out.println("visit greater:");
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }

            return GreaterEvaluator.greater(left, right, state);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitMeets(Meets elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return MeetsEvaluator.meets(left, right, precision, state);
    }

    @Override
    public Object visitDistinct(Distinct elm, State state) {
        Object value = visitExpression(elm.getOperand(), state);
        return DistinctEvaluator.distinct((Iterable<?>) value, state);
    }

    @Override
    public Object visitMeetsAfter(MeetsAfter elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return MeetsAfterEvaluator.meetsAfter(left, right, precision, state);
    }

    //SameAs

    @Override
    public Object visitMeetsBefore(MeetsBefore elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return MeetsBeforeEvaluator.meetsBefore(left, right, precision, state);
    }

    @Override
    public Object visitSameAs(SameAs elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return SameAsEvaluator.sameAs(left, right, precision, state);
    }


    @Override
    public Object visitSameOrAfter(SameOrAfter elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return SameOrAfterEvaluator.sameOrAfter(left, right, precision, state);
    }

    @Override
    public Object visitSameOrBefore(SameOrBefore elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return SameOrBeforeEvaluator.sameOrBefore(left, right, precision, state);
    }

    @Override
    public Object visitGreaterOrEqual(GreaterOrEqual elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }

        return GreaterOrEqualEvaluator.greaterOrEqual(left, right, state);
    }

    @Override
    public Object visitSingletonFrom(SingletonFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return SingletonFromEvaluator.singletonFrom(operand);
    }

    @Override
    public Object visitSize(Size elm, State state) {
        Object argument = visitExpression(elm.getOperand(), state);
        return SizeEvaluator.size(argument);
    }

    @Override
    public Object visitSlice(Slice elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        Integer start = (Integer) visitExpression(elm.getStartIndex(), state);
        Integer end = elm.getEndIndex() == null ? null : (Integer) visitExpression(elm.getEndIndex(), state);

        return SliceEvaluator.slice(source, start, end);
    }

    @Override
    public Object visitSplit(Split elm, State state) {
        Object stringToSplit = visitExpression(elm.getStringToSplit(), state);
        Object separator = visitExpression(elm.getSeparator(), state);

        return SplitEvaluator.split(stringToSplit, separator);
    }

    @Override
    public Object visitSplitOnMatches(SplitOnMatches elm, State state) {
        Object stringToSplit = visitExpression(elm.getStringToSplit(), state);
        Object separator = visitExpression(elm.getSeparatorPattern(), state);

        return SplitOnMatchesEvaluator.splitOnMatches(stringToSplit, separator);
    }

    @Override
    public Object visitStart(Start elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return StartEvaluator.start(operand);
    }

    @Override
    public Object visitStarts(Starts elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return StartsEvaluator.starts(left, right, precision, state);
    }

    @Override
    public Object visitStartsWith(StartsWith elm, State state) {
        Object argument = visitExpression(elm.getOperand().get(0), state);
        Object prefix = visitExpression(elm.getOperand().get(1), state);

        return StartsWithEvaluator.startsWith(argument, prefix);
    }

    @Override
    public Object visitStdDev(StdDev elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return StdDevEvaluator.stdDev(source, state);
    }

    @Override
    public Object visitSubstring(Substring elm, State state) {
        Object stringValue = visitExpression(elm.getStringToSub(), state);
        Object startIndexValue = visitExpression(elm.getStartIndex(), state);
        Object lengthValue = elm.getLength() == null ? null : visitExpression(elm.getLength(), state);

        return SubstringEvaluator.substring(stringValue, startIndexValue, lengthValue);
    }

    @Override
    public Object visitSubtract(Subtract elm, State state) {
        try {
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }

            return SubtractEvaluator.subtract(left, right);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitSuccessor(Successor elm, State state) {
        Object value = visitExpression(elm.getOperand(), state);
        return SuccessorEvaluator.successor(value);
    }

    @Override
    public Object visitSum(Sum elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return SumEvaluator.sum(source);
    }

    @Override
    public Object visitTime(Time elm, State state) {
        if (elm.getHour() == null) {
            return null;
        }

        Integer hour = elm.getHour() == null ? null : (Integer) visitExpression(elm.getHour(), state);
        Integer minute = elm.getMinute() == null ? null : (Integer) visitExpression(elm.getMinute(), state);
        Integer second = elm.getSecond() == null ? null : (Integer) visitExpression(elm.getSecond(), state);
        Integer miliSecond = elm.getMillisecond() == null ? null : (Integer) visitExpression(elm.getMillisecond(), state);

        return TimeEvaluator.time(hour, minute, second, miliSecond);
    }

    @Override
    public Object visitTimeFrom(TimeFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return TimeFromEvaluator.timeFrom(operand);
    }

    @Override
    public Object visitTimeOfDay(TimeOfDay elm, State state) {
        return TimeOfDayEvaluator.internalEvaluate(state);
    }

    @Override
    public Object visitTimezoneFrom(TimezoneFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return TimezoneFromEvaluator.internalEvaluate(operand);
    }

    @Override
    public Object visitTimezoneOffsetFrom(TimezoneOffsetFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return TimezoneOffsetFromEvaluator.timezoneOffsetFrom(operand);
    }

    @Override
    public Object visitToBoolean(ToBoolean elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToBooleanEvaluator.toBoolean(operand);
    }

    @Override
    public Object visitToConcept(ToConcept elm, State state) {
        try {
            Object operand = visitExpression(elm.getOperand(), state);
            if (operand instanceof ExpressionDef) {
                operand = visitExpressionDef((ExpressionDef) operand, state);
            }
            return ToConceptEvaluator.toConcept(operand);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitToDate(ToDate elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToDateEvaluator.toDate(operand);
    }

    @Override
    public Object visitToDateTime(ToDateTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToDateTimeEvaluator.ToDateTime(operand, state);
    }

    @Override
    public Object visitToday(Today elm, State state) {
        return TodayEvaluator.today(state);
    }

    @Override
    public Object visitToDecimal(ToDecimal elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToDecimalEvaluator.toDecimal(operand);
    }

    @Override
    public Object visitToInteger(ToInteger elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToIntegerEvaluator.toInteger(operand);
    }

    @Override
    public Object visitToList(ToList elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToListEvaluator.toList(operand);
    }

    @Override
    public Object visitToLong(ToLong elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToLongEvaluator.toLong(operand);
    }

    @Override
    public Object visitToQuantity(ToQuantity elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToQuantityEvaluator.toQuantity(operand, state);
    }

    @Override
    public Object visitToRatio(ToRatio elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToRatioEvaluator.toRatio(operand);
    }

    @Override
    public Object visitToString(ToString elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToStringEvaluator.toString(operand);
    }

    @Override
    public Object visitToTime(ToTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ToTimeEvaluator.toTime(operand);
    }

    @Override
    public Object visitTruncatedDivide(TruncatedDivide elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return TruncatedDivideEvaluator.div(left, right, state);
    }

    @Override
    public Object visitMedian(Median elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return MedianEvaluator.median(source, state);
    }

    @Override
    public Object visitTruncate(Truncate elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return TruncateEvaluator.truncate(operand);
    }

    @Override
    public Object visitTuple(Tuple elm, State state) {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
        for (TupleElement element : elm.getElement()) {
            ret.put(element.getName(), visitExpression(element.getValue(), state));
        }
        return TupleEvaluator.internalEvaluate(ret, state);
    }


    @Override
    public Object visitAnyTrue(AnyTrue elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return AnyTrueEvaluator.anyTrue(source);
    }

    @Override
    public Object visitAs(As elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return AsEvaluator.internalEvaluate(operand, elm, elm.isStrict(), state);
    }

    @Override
    public Object visitBefore(Before elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return BeforeEvaluator.before(left, right, precision, state);
    }

    @Override
    public Object visitCalculateAgeAt(CalculateAgeAt elm, State state) {
        Object birthDate = visitExpression(elm.getOperand().get(0), state);
        Object asOf = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision().value();
        return CalculateAgeAtEvaluator.calculateAgeAt(birthDate, asOf, precision);
    }

    @Override
    public Object visitCalculateAge(CalculateAge elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        String precision = elm.getPrecision().value();
        return CalculateAgeEvaluator.internalEvaluate(operand, precision, state);
    }

    @Override
    public Object visitCase(Case elm, State state) {
        try {
            System.out.println("visiting Case");
            if (elm.getComparand() == null) {
                for (CaseItem caseItem : elm.getCaseItem()) {
                    Boolean when = (Boolean) visitExpression(caseItem.getWhen(), state);

                    if (when == null) {
                        continue;
                    }

                    if (when) {
                        return visitExpression(caseItem.getThen(), state);
                    }
                }
                return visitElement(elm.getElse(), state);

            } else {
                Object comparand = visitExpression(elm.getComparand(), state);
                if (comparand instanceof ExpressionDef) {
                    comparand = visitExpressionDef((ExpressionDef) comparand, state);
                }

                for (CaseItem caseItem : elm.getCaseItem()) {
                    Object when = visitExpression(caseItem.getWhen(), state);
                    Boolean check = EquivalentEvaluator.equivalent(comparand, when, state);
                    if (check == null) {
                        continue;
                    }

                    if (check) {
                        return visitElement(caseItem.getThen(), state);
                    }
                }

                return visitElement(elm.getElse(), state);
            }
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitCeiling(Ceiling elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return CeilingEvaluator.ceiling(operand);
    }

    @Override
    public Object visitChildren(Children elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return ChildrenEvaluator.children(source);
    }

    @Override
    public Object visitCoalesce(Coalesce elm, State state) {
        List<Object> operands = new ArrayList<>();
        for (Expression operand : elm.getOperand()) {
            operands.add(visitExpression(operand, state));
        }
        return CoalesceEvaluator.coalesce(operands);
    }

    @Override
    public Object visitCode(Code elm, State state) {
        System.out.println("visiting code");
        return CodeEvaluator.internalEvaluate(elm.getSystem(), elm.getCode(), elm.getDisplay(), state);
    }

    @Override
    public Object visitCodeRef(CodeRef elm, State state) {
        boolean enteredLibrary = false;
        if (elm.getLibraryName() != null ) {
            state.enterLibrary(elm.getLibraryName());
            enteredLibrary = true;
        }
        CodeDef cd = state.resolveCodeRef(elm.getName());
        CodeSystem cs = CodeSystemRefEvaluator.toCodeSystem(cd.getCodeSystem(), state);
        if (enteredLibrary) {
            state.exitLibrary(true);
        }
        return CodeRefEvaluator.toCode(elm, cs, state);
    }

    @Override
    public Object visitConcept(Concept elm, State state) {
        System.out.println("visiting Concept");
        ArrayList<org.opencds.cqf.cql.engine.runtime.Code> codes = new ArrayList<>();
        for (int i = 0; i < elm.getCode().size(); ++i) {
            codes.add((org.opencds.cqf.cql.engine.runtime.Code) visitExpression(elm.getCode().get(i), state));
        }

        return ConceptEvaluator.internalEvaluate(codes, elm.getDisplay());
    }

    @Override
    public Object visitConceptRef(ConceptRef elm, State state) {
        return ConceptRefEvaluator.toConcept(elm, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitCollapse(Collapse elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }

        Iterable<org.opencds.cqf.cql.engine.runtime.Interval> list = (Iterable<org.opencds.cqf.cql.engine.runtime.Interval>) left;
        org.opencds.cqf.cql.engine.runtime.Quantity per = (org.opencds.cqf.cql.engine.runtime.Quantity) right;

        return CollapseEvaluator.collapse(list, per, state);
    }

    @Override
    public Object visitCombine(Combine elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        String separator = elm.getSeparator() == null ? "" : (String) visitExpression(elm.getSeparator(), state);

        return CombineEvaluator.combine(source, separator);
    }

    @Override
    public Object visitConcatenate(Concatenate elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }

        return ConcatenateEvaluator.concatenate(left, right);
    }

    @Override
    public Object visitContains(Contains elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return ContainsEvaluator.internalEvaluate(left, right, elm.getOperand().get(0), precision, state);
    }

    @Override
    public Object visitConvert(Convert elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertEvaluator.internalEvaluate(operand, elm.getToType(), elm.getToTypeSpecifier(), state);
    }

    @Override
    public Object visitConvertQuantity(ConvertQuantity elm, State state) {
        Object argument = visitExpression(elm.getOperand().get(0), state);
        Object unit = visitExpression(elm.getOperand().get(1), state);
        return ConvertQuantityEvaluator.convertQuantity(argument, unit, state.getUcumService());
    }

    @Override
    public Object visitConvertsToBoolean(ConvertsToBoolean elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToBooleanEvaluator.convertsToBoolean(operand);
    }

    @Override
    public Object visitConvertsToDate(ConvertsToDate elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToDateEvaluator.convertsToDate(operand);
    }

    @Override
    public Object visitConvertsToDateTime(ConvertsToDateTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToDateTimeEvaluator.convertsToDateTime(operand, null);
    }

    @Override
    public Object visitConvertsToDecimal(ConvertsToDecimal elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToDecimalEvaluator.convertsToDecimal(operand);
    }

    @Override
    public Object visitConvertsToInteger(ConvertsToInteger elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToIntegerEvaluator.convertsToInteger(operand);
    }

    @Override
    public Object visitConvertsToLong(ConvertsToLong elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToLongEvaluator.convertsToLong(operand);
    }

    @Override
    public Object visitConvertsToQuantity(ConvertsToQuantity elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToQuantityEvaluator.convertsToQuantity(operand, state);
    }

    @Override
    public Object visitConvertsToString(ConvertsToString elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToStringEvaluator.convertsToString(operand);
    }

    @Override
    public Object visitConvertsToTime(ConvertsToTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ConvertsToTimeEvaluator.convertsToTime(operand);
    }

    @Override
    public Object visitCount(Count elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return CountEvaluator.count(source);
    }

    @Override
    public Object visitDate(Date elm, State state) {
        Integer year = elm.getYear() == null ? null : (Integer) visitExpression(elm.getYear(), state);
        Integer month = elm.getMonth() == null ? null : (Integer) visitExpression(elm.getMonth(), state);
        Integer day = elm.getDay() == null ? null : (Integer) visitExpression(elm.getDay(), state);
        return DateEvaluator.internalEvaluate(year, month, day);
    }

    @Override
    public Object visitDateFrom(DateFrom elm, State state) {
        try {
            Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
            if (operand instanceof ExpressionDef) {
                operand = visitExpressionDef((ExpressionDef) operand, state);
            }
            return DateFromEvaluator.dateFrom(operand);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitDateTimeComponentFrom(DateTimeComponentFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        String precision = elm.getPrecision().value();
        return DateTimeComponentFromEvaluator.dateTimeComponentFrom(operand, precision);
    }

    @Override
    public Object visitDateTime(DateTime elm, State state) {
        Integer year = elm.getYear() == null ? null : (Integer) visitExpression(elm.getYear(), state);
        Integer month = elm.getMonth() == null ? null : (Integer) visitExpression(elm.getMonth(), state);
        Integer day = elm.getDay() == null ? null : (Integer) visitExpression(elm.getDay(), state);
        Integer hour = elm.getHour() == null ? null : (Integer) visitExpression(elm.getHour(), state);
        Integer minute = elm.getMinute() == null ? null : (Integer) visitExpression(elm.getMinute(), state);
        Integer second = elm.getSecond() == null ? null : (Integer) visitExpression(elm.getSecond(), state);
        Integer milliSecond = elm.getMillisecond() == null ? null : (Integer) visitExpression(elm.getMillisecond(), state);
        BigDecimal timeZoneOffset = elm.getTimezoneOffset() == null ? null : (BigDecimal) visitExpression(elm.getTimezoneOffset(), state);
        return DateTimeEvaluator.internalEvaluate(year, month, day, hour, minute, second, milliSecond, timeZoneOffset);
    }

    @Override
    public Object visitDescendents(Descendents elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return DescendentsEvaluator.descendents(source);
    }

    @Override
    public Object visitDifferenceBetween(DifferenceBetween elm, State state) {
        try {
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }

            String precision = elm.getPrecision().value();
            return DifferenceBetweenEvaluator.difference(left, right, org.opencds.cqf.cql.engine.runtime.Precision.fromString(precision));
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitDurationBetween(DurationBetween elm, State state) {
        try {
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }


            String precision = elm.getPrecision().value();
            return DurationBetweenEvaluator.duration(left, right, org.opencds.cqf.cql.engine.runtime.Precision.fromString(precision));
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitEnd(End elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return EndEvaluator.end(operand);
    }

    @Override
    public Object visitEnds(Ends elm, State state) {
        try {
            System.out.println("visiting Ends");
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }
            String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
            return EndsEvaluator.ends(left, right, precision, state);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitEndsWith(EndsWith elm, State state) {
        String argument = (String) visitExpression(elm.getOperand().get(0), state);
        String suffix = (String) visitExpression(elm.getOperand().get(1), state);
        return EndsWithEvaluator.endsWith(argument, suffix);
    }

    @Override
    public Object visitEqual(Equal elm, State state) {
        try {
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }


            return EqualEvaluator.equal(left, right, state);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitEquivalent(Equivalent elm, State state) {
        try {
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }

            return EquivalentEvaluator.equivalent(left, right, state);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitExcept(Except elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return ExceptEvaluator.except(left, right, state);
    }

    @Override
    public Object visitExists(Exists elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ExistsEvaluator.exists(operand);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitExpand(Expand elm, State state) {
        Iterable<org.opencds.cqf.cql.engine.runtime.Interval> list = (Iterable<org.opencds.cqf.cql.engine.runtime.Interval>) visitExpression(elm.getOperand().get(0), state);
        org.opencds.cqf.cql.engine.runtime.Quantity per = (org.opencds.cqf.cql.engine.runtime.Quantity) visitExpression(elm.getOperand().get(1), state);
        return ExpandEvaluator.expand(list, per, state);
    }

    public Object visitExpandValueSet(ExpandValueSet elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ExpandValueSetEvaluator.expand(operand, state);
    }

    @Override
    public Object visitExp(Exp elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return ExpEvaluator.exp(operand);
    }

    @Override
    public Object visitFilter(Filter elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        Object condition = visitExpression(elm.getCondition(), state);
        return FilterEvaluator.filter(elm, source, condition, state);
    }

    @Override
    public Object visitFirst(First elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return FirstEvaluator.first(source);
    }

    @Override
    public Object visitFlatten(Flatten elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return FlattenEvaluator.flatten(operand);
    }

    @Override
    public Object visitFloor(Floor elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return FloorEvaluator.floor(operand);
    }

    @Override
    public Object visitForEach(ForEach elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        Object element = visitExpression(elm.getElement(), state);
        return ForEachEvaluator.forEach(source, element, state);
    }


    @Override
    public Object visitGeometricMean(GeometricMean elm, State state) {
        Iterable<?> source = (Iterable<?>) visitExpression(elm.getSource(), state);
        return GeometricMeanEvaluator.geometricMean(source, state);
    }


    @Override
    public Object visitHighBoundary(HighBoundary elm, State state) {
        Object input = visitExpression(elm.getOperand().get(0), state);
        Object precision = visitExpression(elm.getOperand().get(1), state);
        return HighBoundaryEvaluator.highBoundary(input, precision);
    }

    @Override
    public Object visitIdentifierRef(IdentifierRef elm, State state) {
        return IdentifierRefEvaluator.internalEvaluate(elm.getName(), state);
    }

    @Override
    public Object visitIf(If elm, State state) {
        return IfEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitImplies(Implies elm, State state) {
        Boolean left = (Boolean) visitExpression(elm.getOperand().get(0), state);
        Boolean right = (Boolean) visitExpression(elm.getOperand().get(1), state);
        return ImpliesEvaluator.implies(left, right);
    }

    @Override
    public Object visitIncludedIn(IncludedIn elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return IncludedInEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitIncludes(Includes elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return IncludesEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitIndexOf(IndexOf elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        Object element = visitExpression(elm.getElement(), state);
        return IndexOfEvaluator.indexOf(source, element, state);
    }

    @Override
    public Object visitIndexer(Indexer elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return IndexerEvaluator.indexer(left, right);
    }

    @Override
    public Object visitIn(In elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return InEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitInstance(Instance elm, State state) {
        try {
            System.out.println("visiting Instance");
            return new InstanceEvaluator().internalEvaluate(elm, state, this);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }


    @Override
    public Object visitIntersect(Intersect elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return IntersectEvaluator.intersect(left, right, state);
    }

    @Override
    public Object visitInterval(Interval elm, State state) {
        return IntervalEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitIs(Is elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return IsEvaluator.internalEvaluate(elm, operand, state);
    }

    @Override
    public Object visitIsFalse(IsFalse elm, State state) {
        Boolean operand = (Boolean) visitExpression(elm.getOperand(), state);
        return IsFalseEvaluator.isFalse(operand);
    }

    @Override
    public Object visitIsNull(IsNull elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return IsNullEvaluator.isNull(operand);
    }

    @Override
    public Object visitIsTrue(IsTrue elm, State state) {
        Boolean operand = (Boolean) visitExpression(elm.getOperand(), state);
        return IsTrueEvaluator.isTrue(operand);
    }

    @Override
    public Object visitLast(Last elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return LastEvaluator.last(source);
    }

    @Override
    public Object visitLastPositionOf(LastPositionOf elm, State state) {
        Object string = visitExpression(elm.getString(), state);
        Object pattern = visitExpression(elm.getPattern(), state);
        return LastPositionOfEvaluator.lastPositionOf(string, pattern);
    }

    @Override
    public Object visitLength(Length elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return LengthEvaluator.internalEvaluate(operand, elm, state);
    }

    @Override
    public Object visitLess(Less elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return LessEvaluator.less(left, right, state);
    }

    @Override
    public Object visitLessOrEqual(LessOrEqual elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }

        return LessOrEqualEvaluator.lessOrEqual(left, right, state);
    }

    @Override
    public Object visitLiteral(Literal literal, State state) {
        System.out.println("visit literal:" + literal.getValueType());
        return LiteralEvaluator.internalEvaluate(literal.getValueType(), literal.getValue(), state);
    }

    @Override
    public Object visitList(org.hl7.elm.r1.List elm, State state) {
        try {
            System.out.println("visiting list");
            return ListEvaluator.internalEvaluate(elm, state, this);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitLn(Ln elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return LnEvaluator.ln(operand);
    }

    @Override
    public Object visitLog(Log elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return LogEvaluator.log(left, right);
    }

    @Override
    public Object visitLowBoundary(LowBoundary elm, State state) {
        Object input = visitExpression(elm.getOperand().get(0), state);
        Object precision = visitExpression(elm.getOperand().get(1), state);
        return LowBoundaryEvaluator.lowBoundary(input, precision);
    }

    @Override
    public Object visitLower(Lower elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return LowerEvaluator.lower(operand);
    }

    @Override
    public Object visitMatches(Matches elm, State state) {
        String argument = (String) visitExpression(elm.getOperand().get(0), state);
        String pattern = (String) visitExpression(elm.getOperand().get(1), state);
        return MatchesEvaluator.matches(argument, pattern);
    }

    @Override
    public Object visitMax(Max elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return MaxEvaluator.max(source, state);
    }

    @Override
    public Object visitMaxValue(MaxValue elm, State state) {
        return MaxValueEvaluator.internalEvaluate(elm.getValueType(), state);
    }

    @Override
    public Object visitMessage(Message elm, State state) {
        return MessageEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitMin(Min elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return MinEvaluator.min(source, state);
    }

    @Override
    public Object visitMinValue(MinValue elm, State state) {
        return MinValueEvaluator.internalEvaluate(elm.getValueType(), state);
    }

    @Override
    public Object visitMode(Mode elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return ModeEvaluator.mode(source, state);
    }

    @Override
    public Object visitModulo(Modulo elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return ModuloEvaluator.modulo(left, right);
    }

    @Override
    public Object visitMultiply(Multiply elm, State state) {
        try {
            Object left = visitExpression(elm.getOperand().get(0), state);
            if (left instanceof ExpressionDef) {
                left = visitExpressionDef((ExpressionDef) left, state);
            }
            Object right = visitExpression(elm.getOperand().get(1), state);
            if (right instanceof ExpressionDef) {
                right = visitExpressionDef((ExpressionDef) right, state);
            }

            return MultiplyEvaluator.multiply(left, right);
        } catch (Exception e) {
            processException(e, elm);
        }
        return null;
    }

    @Override
    public Object visitNegate(Negate elm, State state) {
        return NegateEvaluator.internalEvaluate(elm.getOperand(), state, this);
    }

    @Override
    public Object visitNotEqual(NotEqual elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return NotEqualEvaluator.notEqual(left, right, state);
    }

    @Override
    public Object visitNot(Not elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return NotEvaluator.not(operand);
    }

    @Override
    public Object visitNow(Now elm, State state) {
        return NowEvaluator.internalEvaluate(state);
    }

    @Override
    public Object visitNull(Null elm, State state) {
        return NullEvaluator.internalEvaluate(state);
    }

    @Override
    public Object visitOperandRef(OperandRef elm, State state) {
        return OperandRefEvaluator.internalEvaluate(elm, state);
    }

    @Override
    public Object visitOr(Or elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return OrEvaluator.or(left, right);
    }

    @Override
    public Object visitOverlapsAfter(OverlapsAfter elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return OverlapsAfterEvaluator.overlapsAfter(left, right, precision, state);
    }

    @Override
    public Object visitOverlapsBefore(OverlapsBefore elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return OverlapsBeforeEvaluator.overlapsBefore(left, right, precision, state);
    }

    @Override
    public Object visitOverlaps(Overlaps elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return OverlapsEvaluator.overlaps(left, right, precision, state);
    }

    @Override
    public Object visitParameterRef(ParameterRef elm, State state) {
        return ParameterRefEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitPointFrom(PointFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        return PointFromEvaluator.pointFrom(operand, state);
    }

    @Override
    public Object visitPopulationStdDev(PopulationStdDev elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return PopulationStdDevEvaluator.popStdDev(source, state);
    }

    @Override
    public Object visitPopulationVariance(PopulationVariance elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return PopulationVarianceEvaluator.popVariance(source, state);
    }

    @Override
    public Object visitPositionOf(PositionOf elm, State state) {
        Object pattern = visitExpression(elm.getPattern(), state);
        Object string = visitExpression(elm.getString(), state);
        return PositionOfEvaluator.positionOf(pattern, string);
    }

    @Override
    public Object visitPower(Power elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        return PowerEvaluator.power(left, right);
    }

    @Override
    public Object visitPrecision(Precision elm, State state) {
        Object argument = visitExpression(elm.getOperand(), state);
        return PrecisionEvaluator.precision(argument);
    }

    @Override
    public Object visitPredecessor(Predecessor elm, State state) {
        Object argument = visitExpression(elm.getOperand(), state);
        return PredecessorEvaluator.predecessor(argument);
    }

    @Override
    public Object visitProduct(Product elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        return ProductEvaluator.product(source);
    }

    @Override
    public Object visitProperContains(ProperContains elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperContainsEvaluator.properContains(left, right, precision, state);
    }

    @Override
    public Object visitProperIncludedIn(ProperIncludedIn elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperIncludedInEvaluator.properlyIncludedIn(left, right, precision, state);
    }

    @Override
    public Object visitProperIncludes(ProperIncludes elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperIncludesEvaluator.properlyIncludes(left, right, precision, state);
    }

    @Override
    public Object visitProperIn(ProperIn elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        if (left instanceof ExpressionDef) {
            left = visitExpressionDef((ExpressionDef) left, state);
        }
        Object right = visitExpression(elm.getOperand().get(1), state);
        if (right instanceof ExpressionDef) {
            right = visitExpressionDef((ExpressionDef) right, state);
        }
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperInEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitProperty(Property elm, State state) {
        return PropertyEvaluator.internalEvaluate(elm, state, this);
    }

    @Override

    public Object visitQuantity(Quantity elm, State state) {
        return QuantityEvaluator.internalEvaluate(elm, state);
    }

    @Override
    public Object visitRound(Round elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        if (operand instanceof ExpressionDef) {
            operand = visitExpressionDef((ExpressionDef) operand, state);
        }
        Object precision = elm.getPrecision() == null ? null : visitExpression(elm.getPrecision(), state);
        return RoundEvaluator.round(operand, precision);
    }

    @Override
    public Object visitRetrieve(Retrieve elm, State state) {
        return RetrieveEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitReplaceMatches(ReplaceMatches elm, State state) {
        String argument = (String) visitExpression(elm.getOperand().get(0), state);
        String pattern = (String) visitExpression(elm.getOperand().get(1), state);
        String substitution = (String) visitExpression(elm.getOperand().get(2), state);
        return ReplaceMatchesEvaluator.replaceMatches(argument, pattern, substitution);
    }

    @Override
    public Object visitRepeat(Repeat elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        if (source instanceof ExpressionDef) {
            source = visitExpressionDef((ExpressionDef) source, state);
        }
        Object element = visitExpression(elm.getElement(), state);
        String scope = elm.getScope();
        return RepeatEvaluator.internalEvaluate(source, element, scope, state);
    }

    @Override
    public Object visitRatio(Ratio elm, State state) {
        return RatioEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitQueryLetRef(QueryLetRef elm, State state) {
        return QueryLetRefEvaluator.internalEvaluate(elm, state);
    }

    @Override
    public Object visitQuery(Query elm, State state) {
        return new QueryEvaluator().internalEvaluate(elm, state, this) ;
    }

}