package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.debug.DebugAction;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.debug.DebugResult;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.Severity;
import org.opencds.cqf.cql.engine.runtime.DateTime;

import java.time.ZonedDateTime;
import java.util.*;
/**
 * State represents the internal state of the CqlEngine.
 */
public class State {

    public State(Environment environment) {
        this.environment = environment;
        this.setEvaluationDateTime(ZonedDateTime.now());

    }

    private final Cache cache = new Cache();

    private final Environment environment;

    private Deque<String> currentContext = new ArrayDeque<>();

    private Deque<Deque<Variable> > windows = new ArrayDeque<>();
    private Deque<Library> currentLibrary = new ArrayDeque<>();

    private Deque<HashSet<Object>> evaluatedResourceStack = new ArrayDeque<>();

    private Map<String, Object> parameters = new HashMap<>();
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

    public Deque<Deque<Variable>> getWindows() {
        return windows;
    }

    public void setWindows(Deque<Deque<Variable>> windows) {
        this.windows = windows;
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
        pushWindow();

        currentLibrary.push(library);

        this.pushEvaluatedResourceStack();
    }

    public void pop() {
        if (!windows.peek().isEmpty())
            getStack().pop();
    }

    public void push(Variable variable) {
        getStack().push(variable);
    }

    public Variable resolveVariable(String name) {
        for (var window : windows) {
            for (var v : window) {
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

    public void pushWindow() {
        windows.push(new ArrayDeque<>());
    }

    public void popWindow() {
        windows.pop();
    }

    private Deque<Variable> getStack() {
        return windows.peek();
    }

    public void setContextValue(String context, Object contextValue) {
        if (! contextValues.containsKey(context) || ! contextValues.get(context).equals(contextValue)) {
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

    //serves pop and merge to the down
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
        for (Variable v : getStack()) {
            if (v.getName().equals(name)) {
                if (v.isList())
                    isList = true;
                ret.add(v.getValue());
            }
        }
        return isList ? ret : ret.get(ret.size() - 1);
    }

    public Object resolveIdentifierRef(String name) {
        for (var window : windows) {
            for (var v : window) {
                var value = v.getValue();
                if (value instanceof org.opencds.cqf.cql.engine.runtime.Tuple) {
                    for (String key : ((org.opencds.cqf.cql.engine.runtime.Tuple) value).getElements().keySet()) {
                        if (key.equals(name)) {
                            return ((org.opencds.cqf.cql.engine.runtime.Tuple) value).getElements().get(key);
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
