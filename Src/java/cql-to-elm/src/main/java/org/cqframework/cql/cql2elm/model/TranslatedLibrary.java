package org.cqframework.cql.cql2elm.model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.NamespaceManager;
import org.hl7.cql.model.DataType;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Tag;
import org.hl7.elm.r1.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TranslatedLibrary {
    private VersionedIdentifier identifier;
    private Library library;
    private final HashMap<String, Element> namespace = new HashMap<>();
    private final OperatorMap operators = new OperatorMap();
    private final java.util.List<Conversion> conversions = new ArrayList<>();

    public VersionedIdentifier getIdentifier() {
        return identifier;
    }
    public void setIdentifier(VersionedIdentifier identifier) {
        this.identifier = identifier;
    }

    public Library getLibrary() {
        return library;
    }
    public void setLibrary(Library library) {
        this.library = library;
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

    public void add(CodeSystemDef codesystem) {
        checkNamespace(codesystem.getName());
        namespace.put(codesystem.getName(), codesystem);
    }

    public void add(ValueSetDef valueset) {
        checkNamespace(valueset.getName());
        namespace.put(valueset.getName(), valueset);
    }

    public void add(CodeDef code) {
        checkNamespace(code.getName());
        namespace.put(code.getName(), code);
    }

    public void add(ConceptDef concept) {
        checkNamespace(concept.getName());
        namespace.put(concept.getName(), concept);
    }

    public void add(ParameterDef parameter) {
        checkNamespace(parameter.getName());
        namespace.put(parameter.getName(), parameter);
    }

    public void add(ExpressionDef expression) {
        if (expression instanceof FunctionDef) {
            // Register the operator signature
            add(Operator.fromFunctionDef((FunctionDef)expression));
        }
        else {
            checkNamespace(expression.getName());
            namespace.put(expression.getName(), expression);
        }
    }

    public void remove(ExpressionDef expression) {
        if (expression instanceof FunctionDef) {
            throw new IllegalArgumentException("FunctionDef cannot be removed.");
        }
        namespace.remove(expression.getName());
    }

    private void ensureLibrary(Operator operator) {
        // Functions can be defined in an anonymous library
        if (this.identifier != null && this.identifier.getId() != null) {
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
    }

    private void ensureResultType(Operator operator) {
        if (operator.getResultType() == null) {
            throw new IllegalArgumentException(String.format("Operator %s cannot be registered in library %s because it does not have a result type defined.",
                    operator.getName(), this.identifier.getId()));
        }
    }

    public void add(Operator operator) {
        ensureLibrary(operator);
        ensureResultType(operator);
        operators.addOperator(operator);
    }

    public boolean contains(FunctionDef functionDef) {
        return contains(Operator.fromFunctionDef(functionDef));
    }

    public boolean contains(Operator operator) {
        return operators.containsOperator(operator);
    }

    public void add(Conversion conversion) {
        if (conversion.isCast()) {
            throw new IllegalArgumentException("Casting conversions cannot be registered as part of a library.");
        }

        conversions.add(conversion);
    }

    public Element resolve(String identifier) {
        return namespace.get(identifier);
    }

    public List<Pair<String, Object>> resolveCaseIgnored(String identifier) {
        List<Pair<String, Object>> ret = new ArrayList<>();
        List<String> caseIgnoredKeyMatches = namespace.keySet().stream()
                .filter(s -> s.equalsIgnoreCase(identifier) && !s.equals(identifier))
                .collect(Collectors.toList());

        for (String key : caseIgnoredKeyMatches){
            ret.add(new ImmutablePair<>(key, namespace.get(key)));
        }
        return ret;
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

    public String resolveIncludeAlias(VersionedIdentifier identifier) {
        if (identifier != null && library != null && library.getIncludes() != null && library.getIncludes().getDef() != null) {
            String libraryPath = NamespaceManager.getPath(identifier.getSystem(), identifier.getId());
            for (IncludeDef id : library.getIncludes().getDef()) {
                if (id.getPath().equals(libraryPath)) {
                    return id.getLocalIdentifier();
                }
            }
        }

        return null;
    }

    public CodeSystemDef resolveCodeSystemRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof CodeSystemDef) {
            return (CodeSystemDef)element;
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

    public CodeDef resolveCodeRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof CodeDef) {
            return (CodeDef)element;
        }

        return null;
    }

    public ConceptDef resolveConceptRef(String identifier) {
        Element element = resolve(identifier);
        if (element instanceof ConceptDef) {
            return (ConceptDef)element;
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

    public OperatorResolution resolveCall(CallContext callContext, ConversionMap conversionMap) {
        OperatorResolution resolution = operators.resolveOperator(callContext, conversionMap);

        if (resolution != null && resolution.getOperator() != null) {
            // For backwards compatibility, a library can indicate that functions it exports are allowed to be invoked
            // with fluent syntax. This is used in FHIRHelpers to allow fluent resolution, which is implicit in 1.4.
            if (callContext.getAllowFluent() && !resolution.getOperator().getFluent()) {
                resolution.setAllowFluent(getBooleanTag("allowFluent"));
            }

            // The resolution needs to carry with it the full versioned identifier of the library so that it can be correctly
            // reflected via the alias for the library in the calling context.
            resolution.setLibraryIdentifier(this.getIdentifier());
        }

        return resolution;
    }

    public OperatorMap getOperatorMap() {
        return operators;
    }

    public Iterable<Conversion> getConversions() {
        return conversions;
    }

    private Annotation getAnnotation() {
        if (library != null && library.getAnnotation() != null) {
            for (Object o : library.getAnnotation()) {
                if (o instanceof Annotation) {
                    return (Annotation)o;
                }
            }
        }

        return null;
    }

    public String getTag(String tagName) {
        Annotation a = getAnnotation();
        if (a != null && a.getT() != null) {
            for (Tag t : a.getT()) {
                if (t.getName().equals(tagName)) {
                    return t.getValue();
                }
            }
        }

        return null;
    }

    public boolean getBooleanTag(String tagName) {
        String tagValue = getTag(tagName);
        if (tagValue != null) {
            try {
                return Boolean.valueOf(tagValue);
            }
            catch (Exception e) {
                // Do not throw
                return false;
            }
        }

        return false;
    }
}
