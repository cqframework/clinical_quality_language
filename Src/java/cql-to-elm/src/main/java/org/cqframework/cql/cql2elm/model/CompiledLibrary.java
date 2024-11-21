package org.cqframework.cql.cql2elm.model;

import java.util.*;
import java.util.List;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Tag;
import org.hl7.elm.r1.*;

public class CompiledLibrary {
    private VersionedIdentifier identifier;
    private Library library;
    private final Map<String, Element> namespace = new HashMap<>();
    private final OperatorMap operators = new OperatorMap();
    private final Map<Operator, FunctionDef> functionDefs = new HashMap<>();
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
        final ResolvedIdentifierContext existingResolvedIdentifierContext = resolve(identifier);
        existingResolvedIdentifierContext.getExactMatchElement().ifPresent(element -> {
            throw new IllegalArgumentException(
                    String.format("Identifier %s is already in use in this library.", identifier));
        });
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
            add((FunctionDef) expression, Operator.fromFunctionDef((FunctionDef) expression));
        } else {
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
            } else {
                if (!operator.getLibraryName().equals(this.identifier.getId())) {
                    throw new IllegalArgumentException(String.format(
                            "Operator %s cannot be registered in library %s because it is defined in library %s.",
                            operator.getName(), this.identifier.getId(), operator.getLibraryName()));
                }
            }
        }
    }

    private void ensureResultType(Operator operator) {
        if (operator.getResultType() == null) {
            throw new IllegalArgumentException(String.format(
                    "Operator %s cannot be registered in library %s because it does not have a result type defined.",
                    operator.getName(), this.identifier.getId()));
        }
    }

    public void add(FunctionDef functionDef, Operator operator) {
        ensureLibrary(operator);
        // ensureResultType(operator);
        operators.addOperator(operator);
        functionDefs.put(operator, functionDef);
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

    public ResolvedIdentifierContext resolve(String identifier) {
        if (namespace.containsKey(identifier)) {
            return ResolvedIdentifierContext.exactMatch(identifier, namespace.get(identifier));
        }

        return namespace.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(identifier))
                .map(Map.Entry::getValue)
                .map(element -> ResolvedIdentifierContext.caseInsensitiveMatch(identifier, element))
                .findFirst()
                .orElse(ResolvedIdentifierContext.caseInsensitiveMatch(identifier, null));
    }

    public UsingDef resolveUsingRef(String identifier) {
        return resolveIdentifier(identifier, UsingDef.class);
    }

    public IncludeDef resolveIncludeRef(String identifier) {
        return resolveIdentifier(identifier, IncludeDef.class);
    }

    public String resolveIncludeAlias(VersionedIdentifier identifier) {
        if (identifier != null
                && library != null
                && library.getIncludes() != null
                && library.getIncludes().getDef() != null) {
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
        return resolveIdentifier(identifier, CodeSystemDef.class);
    }

    public ValueSetDef resolveValueSetRef(String identifier) {
        return resolveIdentifier(identifier, ValueSetDef.class);
    }

    public CodeDef resolveCodeRef(String identifier) {
        return resolveIdentifier(identifier, CodeDef.class);
    }

    public ConceptDef resolveConceptRef(String identifier) {
        return resolveIdentifier(identifier, ConceptDef.class);
    }

    public ParameterDef resolveParameterRef(String identifier) {
        return resolveIdentifier(identifier, ParameterDef.class);
    }

    public ExpressionDef resolveExpressionRef(String identifier) {
        return resolveIdentifier(identifier, ExpressionDef.class);
    }

    private <T extends Element> T resolveIdentifier(String identifier, Class<T> clazz) {
        return resolve(identifier).resolveIdentifier(clazz);
    }

    public Iterable<FunctionDef> resolveFunctionRef(String identifier) {
        var results = new ArrayList<FunctionDef>();
        for (ExpressionDef ed : getLibrary().getStatements().getDef()) {
            if (ed instanceof FunctionDef) {
                if (ed.getName().equals(identifier)) {
                    results.add((FunctionDef) ed);
                }
            }
        }

        return results;
    }

    public Iterable<FunctionDef> resolveFunctionRef(String identifier, List<DataType> signature) {
        if (signature == null) {
            return resolveFunctionRef(identifier);
        } else {
            CallContext cc = new CallContext(
                    this.getIdentifier().getId(), identifier, false, false, false, signature.toArray(new DataType[0]));
            OperatorResolution resolution = resolveCall(cc, null);
            var results = new ArrayList<FunctionDef>();
            if (resolution != null) {
                results.add(resolution.getOperator().getFunctionDef());
            }
            return results;
        }
    }

    public OperatorResolution resolveCall(CallContext callContext, ConversionMap conversionMap) {
        OperatorResolution resolution = operators.resolveOperator(callContext, conversionMap);

        if (resolution != null && resolution.getOperator() != null) {
            // For backwards compatibility, a library can indicate that functions it exports are allowed to be invoked
            // with fluent syntax. This is used in FHIRHelpers to allow fluent resolution, which is implicit in 1.4.
            if (callContext.getAllowFluent() && !resolution.getOperator().getFluent()) {
                resolution.setAllowFluent(getBooleanTag("allowFluent"));
            }

            // The resolution needs to carry with it the full versioned identifier of the library so that it can be
            // correctly
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
                    return (Annotation) o;
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
                return Boolean.parseBoolean(tagValue);
            } catch (Exception e) {
                // Do not throw
                return false;
            }
        }

        return false;
    }
}
