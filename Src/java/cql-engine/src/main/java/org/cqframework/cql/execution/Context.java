package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.ParameterDef;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bryn on 4/12/2016.
 */
public class Context {

    private Map<String, Object> parameters = new HashMap<>();

    private Library library;

    public Context(Library library) {
        this.library = library;
    }

    public ExpressionDef resolveExpressionRef(Library library, String name) {
        for (ExpressionDef expressionDef : library.getStatements().getDef()) {
            if (expressionDef.getName().equals(name)) {
                return expressionDef;
            }
        }

        throw new IllegalArgumentException(String.format("Could not resolve expression reference '%s'.", name));
    }

    public ParameterDef resolveParameterRef(Library library, String name) {
        for (ParameterDef parameterDef : library.getParameters().getDef()) {
            if (parameterDef.getName().equals(name)) {
                return parameterDef;
            }
        }

        throw new IllegalArgumentException(String.format("Could not resolve parameter reference '%s'.", name));
    }

    public Object resolveParameterRef(String library, String name) {
        // TODO: Library parameter resolution
        if (library != null) {
            throw new IllegalArgumentException("Library resolution of parameters is not yet supported.");
        }

        if (parameters.containsKey(name)) {
            return parameters.get(name);
        }

        ParameterDef parameterDef = resolveParameterRef(this.library, name);
        Object result = parameterDef.getDefault() != null ? parameterDef.getDefault().evaluate(this) : null;
        parameters.put(name, result);
        return result;
    }
}
