package org.opencds.cqf.cql.engine.execution;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.*;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.debug.DebugAction;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.debug.DebugResult;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.Severity;
import org.opencds.cqf.cql.engine.runtime.DateTime;

/**
 * State represents the internal state of the CqlEngine.
 */
public class State {

    public static class ActivationFrame {

        // Arguments and/or local variables of the active function
        // call or expression definition evaluation. This is the only
        // field that is strictly required for evaluation. The other
        // fields are for pragmatic purposes like friendly backtraces
        // and profiling.
        public Deque<Variable> variables = new ArrayDeque<>(4);

        // The expression that is being evaluated in this activation
        // frame. Either null for the "root" activation frame, an ExpressionDef (which can be a FunctionDef)
        // or a Retrieve.
        public Element element;

        public String contextName;

        // The times at which the evaluation to which this activation
        // frame belongs started and ended.
        public long startTime;
        public long endTime = 0;

        // If the activation frame belongs to an ExpressionDef that is
        // not a FunctionDef, this field Indicates whether the
        // evaluation result was computed for this activation frame or
        // taken from the cache.
        public boolean isCached = false;

        public ActivationFrame(Element element, String contextName, long startTime) {
            this.element = element;
            this.contextName = contextName;
            this.startTime = startTime;
        }

        public void setStartTime() {
            this.startTime = System.nanoTime();
        }

        @Override
        public String toString() {
            final var result = new StringBuilder().append("Frame{element=");
            if (this.element == null) {
                result.append("«root»");
            } else if (this.element instanceof ExpressionDef) {
                result.append(((ExpressionDef) this.element).getName());
            } else if (this.element instanceof Retrieve) {
                result.append(String.format(
                        "[%s]", ((Retrieve) this.element).getDataType().getLocalPart()));
            } else {
                result.append(this.element.getClass().getSimpleName());
            }
            if (this.endTime == 0) {
                result.append(", active");
            } else {
                result.append(String.format(", %,d ms", (this.endTime - this.startTime) / 1_000_000));
            }
            if (this.isCached) {
                result.append(", cached");
            }
            return result.append("}").toString();
        }
    }

    public State(Environment environment) {
        this(environment, new HashSet<>());
    }

    public State(Environment environment, Set<CqlEngine.Options> engineOptions) {
        this.environment = requireNonNull(environment);
        this.engineOptions = requireNonNull(engineOptions);
        this.setEvaluationDateTime(ZonedDateTime.now());
    }

    private final Cache cache = new Cache();
    private final Set<CqlEngine.Options> engineOptions;

    private final Environment environment;

    private final Deque<String> currentContext = new ArrayDeque<>();

    private final Deque<Library> currentLibrary = new ArrayDeque<>();

    private final Deque<ActivationFrame> stack = new ArrayDeque<>();

    private final Deque<HashSet<Object>> evaluatedResourceStack = new ArrayDeque<>();

    private final Map<String, Object> parameters = new HashMap<>();
    private Map<String, Object> contextValues = new HashMap<>();

    private ZonedDateTime evaluationZonedDateTime;
    private DateTime evaluationDateTime;

    private DebugMap debugMap;

    public Cache getCache() {
        return this.cache;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Library getCurrentLibrary() {
        return currentLibrary.peek();
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Set<CqlEngine.Options> getEngineOptions() {
        return engineOptions;
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
            String fullName = libraryName != null
                    ? String.format("%s.%s", getCurrentLibrary().getIdentifier().getId(), name)
                    : name;
            parameters.put(fullName, value);
        } finally {
            exitLibrary(enteredLibrary);
        }
    }

    public boolean enterLibrary(String libraryName) {
        if (libraryName != null) {
            IncludeDef includeDef = Libraries.resolveLibraryRef(libraryName, getCurrentLibrary());
            var identifier = Libraries.toVersionedIdentifier(includeDef);

            // We probably want to just load all relevant libraries into
            // memory before we start evaluation. This will further separate
            // environment from state.
            Library library = this.getEnvironment().resolveLibrary(identifier);

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

    public Map<String, Object> getContextValues() {
        return contextValues;
    }

    public void setContextValues(Map<String, Object> contextValues) {
        this.contextValues = contextValues;
    }

    @Deprecated
    public Deque<Deque<Variable>> getWindows() {
        final var result = new ArrayDeque<Deque<Variable>>();
        this.stack.forEach(frame -> result.push(frame.variables));
        return result;
    }

    @Deprecated
    public void setWindows(Deque<Deque<Variable>> windows) {
        throw new RuntimeException("Not supported");
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

    public DebugResult ensureDebugResult() {
        if (this.debugResult == null) {
            debugResult = new DebugResult();
        }
        return debugResult;
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

    public void setEvaluationDateTime(ZonedDateTime evaluationZonedDateTime) {
        this.evaluationZonedDateTime = evaluationZonedDateTime;
        this.evaluationDateTime = new DateTime(evaluationZonedDateTime.toOffsetDateTime());
    }

    public ZonedDateTime getEvaluationZonedDateTime() {
        return this.evaluationZonedDateTime;
    }

    public DateTime getEvaluationDateTime() {
        return this.evaluationDateTime;
    }

    public void init(Library library) {
        assert this.stack.isEmpty();

        currentLibrary.push(library);

        this.pushEvaluatedResourceStack();
    }

    public Deque<ActivationFrame> getStack() {
        return this.stack;
    }

    public void pop() {
        final var topActivationFrame = this.stack.peek();
        assert topActivationFrame != null; // stack underflow
        topActivationFrame.variables.pop();
    }

    public void push(Variable variable) {
        final var topActivationFrame = this.stack.peek();
        assert topActivationFrame != null; // stack underflow
        topActivationFrame.variables.push(variable);
    }

    public void beginEvaluation() {
        // This method must be called on an initialized but inactivate
        // state: there must be no activation frames besides the dummy
        // "root" activation frame. This method simply resets the
        // start time of the root activation frame.
        assert this.stack.isEmpty();
        pushActivationFrame(null);
    }

    public void endEvaluation() {
        assert this.stack.size() == 1;
        // TODO(jmoringe): maybe assert this.stack.getLast().variables.isEmpty();
        // Pop (and possibly process) the root activation frame.
        popActivationFrame();
    }

    public Variable resolveVariable(String name) {
        for (var frame : this.stack) {
            for (var v : frame.variables) {
                if (v.getName().equals(name)) {
                    return v;
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

    public void pushActivationFrame(Element element, String contextName, long startTime) {
        final var newActivationFrame = new ActivationFrame(element, contextName, startTime);
        this.stack.push(newActivationFrame);
        if (this.debugResult != null) {
            final var profile = this.debugResult.getProfile();
            if (profile != null) {
                profile.enter(newActivationFrame);
            }
        }
    }

    public void pushActivationFrame(Element element, String contextName) {
        pushActivationFrame(element, contextName, System.nanoTime());
    }

    public void pushActivationFrame(Element element) {
        final var contextName = this.currentContext.peekFirst();
        pushActivationFrame(element, contextName);
    }

    public void popActivationFrame() {
        final var topActivationFrame = this.stack.peek();
        if (topActivationFrame == null) {
            throw new RuntimeException("Stack underflow");
        }
        assert topActivationFrame.endTime == 0;
        // If a profile is being built (controlled via an engine
        // option), register the invocation chain that terminates at
        // topActivationFrame.
        if (this.debugResult != null) {
            final var profile = this.debugResult.getProfile();
            if (profile != null) {
                topActivationFrame.endTime = System.nanoTime();
                profile.leave(topActivationFrame);
            }
        }
        this.stack.pop();
    }

    public ActivationFrame getTopActivationFrame() {
        final var topActivationFrame = this.stack.peek();
        if (topActivationFrame == null) {
            throw new RuntimeException("Stack underflow");
        }
        return topActivationFrame;
    }

    public void setContextValue(String context, Object contextValue) {
        if (!contextValues.containsKey(context) || !contextValues.get(context).equals(contextValue)) {
            contextValues.put(context, contextValue);
            cache.getExpressions().clear();
        }
    }

    public boolean enterContext(String context) {
        if (context != null) {
            currentContext.push(context);
            return true;
        }

        return false;
    }

    public void exitContext(boolean isEnteredContext) {
        if (isEnteredContext) {
            currentContext.pop();
        }
    }

    public String getCurrentContext() {
        if (currentContext.isEmpty()) {
            return null;
        }

        return currentContext.peekFirst();
    }

    public Object getCurrentContextValue() {
        String context = getCurrentContext();
        if (context != null && this.contextValues.containsKey(context)) {
            return this.contextValues.get(context);
        }

        return null;
    }

    public Set<Object> getEvaluatedResources() {
        if (evaluatedResourceStack.isEmpty()) {
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

    // serves pop and merge to the down
    public void popEvaluatedResourceStack() {
        if (evaluatedResourceStack.isEmpty()) {
            throw new IllegalStateException("Attempted to pop the evaluatedResource stack when it's empty");
        }

        if (evaluatedResourceStack.size() == 1) {
            throw new IllegalStateException("Attempted to pop the evaluatedResource stack when only the root remains");
        }

        Set<Object> objects = evaluatedResourceStack.pop();
        var set = evaluatedResourceStack.peek();
        set.addAll(objects);
    }

    public Object resolveAlias(String name) {
        // This method needs to account for multiple variables on the stack with the same name
        ArrayList<Object> ret = new ArrayList<>();
        boolean isList = false;
        for (Variable v : getTopActivationFrame().variables) {
            if (v.getName().equals(name)) {
                if (v.isList()) isList = true;
                ret.add(v.getValue());
            }
        }
        return isList ? ret : ret.get(ret.size() - 1);
    }

    public Object resolveIdentifierRef(String name) {
        if (name != null) {
            throw new RuntimeException("I don't know when this is used: resolveIdentifierRef");
        }
        for (var frame : this.stack) {
            for (var v : frame.variables) {
                var value = v.getValue();
                if (value instanceof org.opencds.cqf.cql.engine.runtime.Tuple) {
                    for (String key : ((org.opencds.cqf.cql.engine.runtime.Tuple) value)
                            .getElements()
                            .keySet()) {
                        if (key.equals(name)) {
                            return ((org.opencds.cqf.cql.engine.runtime.Tuple) value)
                                    .getElements()
                                    .get(key);
                        }
                    }
                }
                try {
                    return environment.resolvePath(value, name);
                } catch (Exception ignored) {

                }
            }
        }

        throw new CqlException("Cannot resolve identifier " + name);
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
}
