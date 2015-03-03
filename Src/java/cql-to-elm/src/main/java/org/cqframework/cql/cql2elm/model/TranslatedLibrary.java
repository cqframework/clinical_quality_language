package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm.r1.*;

import java.util.*;

public class TranslatedLibrary {
    private VersionedIdentifier identifier;
    private final HashMap<String, Element> namespace = new HashMap<>();
    private final OperatorMap operators = new OperatorMap();
    private final java.util.List<Conversion> conversions = new ArrayList<>();

    public VersionedIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(VersionedIdentifier identifier) {
        this.identifier = identifier;
    }

    private void checkNamespace(String identifier) {
        Element existingElement = resolve(identifier);
        if (existingElement != null) {
            throw new IllegalArgumentException(String.format("Identifier %s is already in use in this library.", identifier));
        }
    }

    public void add(UsingDef using) {
        checkNamespace(using.getLocalIdentifier());
        namespace.put(using.getLocalIdentifier(), using);
    }

    public void add(IncludeDef include) {
        checkNamespace(include.getLocalIdentifier());
        namespace.put(include.getLocalIdentifier(), include);
    }

    public void add(ValueSetDef valueset) {
        checkNamespace(valueset.getName());
        namespace.put(valueset.getName(), valueset);
    }

    public void add(ParameterDef parameter) {
        checkNamespace(parameter.getName());
        namespace.put(parameter.getName(), parameter);
    }

    public void add(ExpressionDef expression) {
        if (expression instanceof FunctionDef) {
            // Register the operator signature
            add(expressionDefToOperator((FunctionDef)expression));
        }
        else {
            checkNamespace(expression.getName());
            namespace.put(expression.getName(), expression);
        }
    }

    private Operator expressionDefToOperator(FunctionDef functionDef) {
        java.util.List<DataType> operandTypes = new ArrayList<>();
        return new Operator(functionDef.getName(), new Signature(operandTypes.toArray(new DataType[operandTypes.size()])), functionDef.getResultType());
    }

    private void ensureLibrary(Operator operator) {
        if (operator.getLibraryName() == null) {
            operator.setLibraryName(this.identifier.getId());
        }
        else {
            if (!operator.getLibraryName().equals(this.identifier.getId())) {
                throw new IllegalArgumentException(String.format("Operator %s cannot be registered in library %s because it is defined in library %s.",
                        operator.getName(), this.identifier.getId(), operator.getLibraryName()));
            }
        }
    }

    public void add(Operator operator) {
        ensureLibrary(operator);
        operators.addOperator(operator);
    }

    public void add(Conversion conversion) {
        conversions.add(conversion);
    }

    public Element resolve(String identifier) {
        return namespace.get(identifier);
    }

    public UsingDef resolveUsingRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof UsingDef) {
            return (UsingDef)element;
        }

        return null;
    }

    public IncludeDef resolveIncludeRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof IncludeDef) {
            return (IncludeDef)element;
        }

        return null;
    }

    public ValueSetDef resolveValueSetRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof ValueSetDef) {
            return (ValueSetDef)element;
        }

        return null;
    }

    public ParameterDef resolveParameterRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof ParameterDef) {
            return (ParameterDef)element;
        }

        return null;
    }

    public ExpressionDef resolveExpressionRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof ExpressionDef) {
            return (ExpressionDef)element;
        }

        return null;
    }

    public Operator resolveCall(CallContext callContext, ConversionMap conversionMap) {
        return operators.resolveOperator(callContext, conversionMap);
    }
}
