package org.cqframework.cql.execution;

import org.cqframework.cql.data.DataProvider;
import org.cqframework.cql.elm.execution.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Bryn on 4/12/2016.
 */
public class Context {

    private Map<String, Object> parameters = new HashMap<>();
    private Stack<String> currentContext = new Stack<>();

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

    public ValueSetDef resolveValueSetRef(Library library, String name) {
        for (ValueSetDef valueSetDef : library.getValueSets().getDef()) {
            if (valueSetDef.getName().equals(name)) {
                return valueSetDef;
            }
        }

        throw new IllegalArgumentException(String.format("Could not resolve value set reference '%s'.", name));
    }

    public CodeSystemDef resolveCodeSystemRef(Library library, String name) {
        for (CodeSystemDef codeSystemDef : library.getCodeSystems().getDef()) {
            if (codeSystemDef.getName().equals(name)) {
                return codeSystemDef;
            }
        }

        throw new IllegalArgumentException(String.format("Could not resolve code system reference '%s'.", name));
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

    public ValueSetDef resolveValueSetRef(String library, String name) {
        // TODO: Library resolution
        if (library != null) {
            throw new IllegalArgumentException("Library resolutuion of value sets is not yet supported.");
        }

        return resolveValueSetRef(this.library, name);
    }

    private Map<String, DataProvider> dataProviders = new HashMap<>();

    public void registerDataProvider(String modelUri, DataProvider dataProvider) {
        dataProviders.put(modelUri, dataProvider);
    }

    public DataProvider resolveDataProvider(QName dataType) {
        DataProvider dataProvider = dataProviders.get(dataType.getNamespaceURI());
        if (dataProvider == null) {
            throw new IllegalArgumentException(String.format("Could not resolve data provider for model '%s'.", dataType.getNamespaceURI()));
        }

        return dataProvider;
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
}
