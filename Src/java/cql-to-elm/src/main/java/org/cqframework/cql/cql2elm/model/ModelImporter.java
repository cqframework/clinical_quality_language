package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;
import org.hl7.elm_modelinfo.r1.*;

import java.util.*;

public class ModelImporter {

    private ModelInfo modelInfo;
    private Map<String, TypeInfo> typeInfoIndex;
    private Map<String, DataType> resolvedTypes;
    private List<DataType> dataTypes;
    private List<Conversion> conversions;
    private List<ModelContext> contexts;
    private ModelContext defaultContext;

    public ModelImporter(ModelInfo modelInfo, Iterable<DataType> systemTypes) {
        if (modelInfo == null) {
            throw new IllegalArgumentException("modelInfo is null");
        }

        this.modelInfo = modelInfo;
        this.typeInfoIndex = new HashMap<>();
        this.resolvedTypes = new HashMap<>();
        this.dataTypes = new ArrayList<>();
        this.conversions = new ArrayList<>();
        this.contexts = new ArrayList<>();

        // Import system types
        if (systemTypes != null) {
            for (DataType systemType : systemTypes) {
                if (systemType instanceof NamedType) {
                    NamedType namedSystemType = (NamedType)systemType;
                    this.resolvedTypes.put(namedSystemType.getName(), systemType);
                }
            }
        }

        // Import model types
        for (TypeInfo t : this.modelInfo.getTypeInfo()) {
            if (t instanceof SimpleTypeInfo) {
                typeInfoIndex.put(ensureUnqualified(((SimpleTypeInfo)t).getName()), t);
            }
            else if (t instanceof ClassInfo) {
                ClassInfo classInfo = (ClassInfo)t;
                if (classInfo.getName() != null) {
                    typeInfoIndex.put(ensureUnqualified(classInfo.getName()), classInfo);
                }
            }
        }

        // Import model conversions
        for (ConversionInfo c : this.modelInfo.getConversionInfo()) {
            DataType fromType = resolveTypeNameOrSpecifier(c.getFromType(), c.getFromTypeSpecifier());
            DataType toType = resolveTypeNameOrSpecifier(c.getToType(), c.getToTypeSpecifier());
            int qualifierIndex = c.getFunctionName().indexOf('.');
            String libraryName = qualifierIndex >= 0 ? c.getFunctionName().substring(0, qualifierIndex) : null;
            String functionName = qualifierIndex >= 0 ? c.getFunctionName().substring(qualifierIndex + 1) : null;
            Operator operator = new Operator(functionName, new Signature(fromType), toType);
            if (libraryName != null) {
                operator.setLibraryName(libraryName);
            }

            // All conversions loaded as part of a model are implicit
            Conversion conversion = new Conversion(operator, true);
            conversions.add(conversion);
        }

        // Import model contexts
        for (ContextInfo c : this.modelInfo.getContextInfo()) {
            DataType contextType = resolveTypeSpecifier(c.getContextType());
            if (!(contextType instanceof ClassType)) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Model context %s must be a class type.", c.getName()));
            }
            ModelContext modelContext = new ModelContext(c.getName(), (ClassType)contextType, Arrays.asList(c.getKeyElement().split(";")), c.getBirthDateElement());
            // TODO: Validate key elements correspond to attributes of the class type
            contexts.add(modelContext);
        }

        // For backwards compatibility with model info files that don't specify contexts, create a default context based on the patient class information if it's present
        if (contexts.size() == 0 && this.modelInfo.getPatientClassName() != null) {
            DataType contextType = resolveTypeName(this.modelInfo.getPatientClassName());
            if (contextType instanceof ClassType) {
                ModelContext modelContext = new ModelContext(((ClassType)contextType).getSimpleName(), (ClassType)contextType, Arrays.asList("id"), this.modelInfo.getPatientBirthDatePropertyName());
                contexts.add(modelContext);
                defaultContext = modelContext;
            }
        }

        for (TypeInfo t: this.modelInfo.getTypeInfo()) {
            DataType type = resolveTypeInfo(t);
            dataTypes.add(type);

            if (t instanceof ClassInfo) {
                importRelationships((ClassInfo)t, (ClassType)type);
            }
        }

        if (systemTypes != null) {
            for (DataType systemType : systemTypes) {
                if (systemType instanceof NamedType) {
                    NamedType namedSystemType = (NamedType)systemType;
                    this.resolvedTypes.remove(namedSystemType.getName());
                }
            }
        }
    }

    public Map<String, DataType> getTypes() { return resolvedTypes; }
    public Iterable<Conversion> getConversions() { return conversions; }
    public Iterable<ModelContext> getContexts() { return contexts; }

    public String getDefaultContextName() {
        if (this.modelInfo.getDefaultContext() != null) {
            return this.modelInfo.getDefaultContext();
        }

        if (this.defaultContext != null) {
            return this.defaultContext.getName();
        }

        return null;
    }

    private String casify(String typeName) {
        return casify(typeName, this.modelInfo.isCaseSensitive() != null ? this.modelInfo.isCaseSensitive() : false);
    }

    private String casify(String typeName, boolean caseSensitive) {
        return caseSensitive ? typeName.toLowerCase() : typeName;
    }

    private DataType resolveTypeInfo(TypeInfo t) {
        if (t instanceof SimpleTypeInfo) {
            return resolveSimpleType((SimpleTypeInfo)t);
        }
        else if (t instanceof ClassInfo) {
            return resolveClassType((ClassInfo)t);
        }
        else if (t instanceof TupleTypeInfo) {
            return resolveTupleType((TupleTypeInfo)t);
        }
        else if (t instanceof IntervalTypeInfo) {
            return resolveIntervalType((IntervalTypeInfo)t);
        }
        else if (t instanceof ListTypeInfo) {
            return resolveListType((ListTypeInfo)t);
        }
        else if (t instanceof ChoiceTypeInfo) {
            return resolveChoiceType((ChoiceTypeInfo)t);
        }

        return null;
    }

    private DataType resolveTypeSpecifier(TypeSpecifier typeSpecifier) {
        if (typeSpecifier == null) {
            return null;
        }

        if (typeSpecifier instanceof NamedTypeSpecifier) {
            NamedTypeSpecifier namedTypeSpecifier = (NamedTypeSpecifier)typeSpecifier;
            String qualifier = namedTypeSpecifier.getNamespace();
            if (qualifier == null || qualifier.isEmpty()) {
                qualifier = namedTypeSpecifier.getModelName(); // For backwards compatibility, modelName is deprecated in favor of namespace
            }
            if (qualifier == null || qualifier.isEmpty()) {
                qualifier = this.modelInfo.getName();
            }

            String qualifiedTypeName = String.format("%s.%s", qualifier, namedTypeSpecifier.getName());
            return resolveTypeName(qualifiedTypeName);
        }

        if (typeSpecifier instanceof IntervalTypeSpecifier) {
            IntervalTypeSpecifier intervalTypeSpecifier = (IntervalTypeSpecifier)typeSpecifier;
            DataType pointType = resolveTypeNameOrSpecifier(intervalTypeSpecifier.getPointType(), intervalTypeSpecifier.getPointTypeSpecifier());
            return new IntervalType(pointType);
        }

        if (typeSpecifier instanceof ListTypeSpecifier) {
            ListTypeSpecifier listTypeSpecifier = (ListTypeSpecifier)typeSpecifier;
            DataType elementType = resolveTypeNameOrSpecifier(listTypeSpecifier.getElementType(), listTypeSpecifier.getElementTypeSpecifier());
            if (elementType != null) {
                return new ListType(elementType);
            }
        }

        if (typeSpecifier instanceof TupleTypeSpecifier) {
            TupleTypeSpecifier tupleTypeSpecifier = (TupleTypeSpecifier)typeSpecifier;
            TupleType tupleType = new TupleType();
            for (TupleTypeSpecifierElement specifierElement : tupleTypeSpecifier.getElement()) {
                TupleTypeElement element = new TupleTypeElement(specifierElement.getName(), resolveTypeSpecifier(specifierElement.getElementType()));
                tupleType.addElement(element);
            }
        }

        if (typeSpecifier instanceof ChoiceTypeSpecifier) {
            ChoiceTypeSpecifier choiceTypeSpecifier = (ChoiceTypeSpecifier)typeSpecifier;
            List<DataType> choices = new ArrayList<>();
            for (TypeSpecifier choice : choiceTypeSpecifier.getChoice()) {
                DataType choiceType = resolveTypeSpecifier(choice);
                choices.add(choiceType);
            }
            return new ChoiceType(choices);
        }

        return null;
    }

    private DataType resolveTypeName(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        // NOTE: Preserving the ability to parse string type specifiers for backwards loading compatibility
        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        if (typeName.toLowerCase().startsWith("interval<")) {
            DataType pointType = resolveTypeName(typeName.substring(typeName.indexOf('<') + 1, typeName.lastIndexOf('>')));
            return new IntervalType(pointType);
        }
        else if (typeName.toLowerCase().startsWith("list<")) {
            DataType elementType = resolveTypeName(typeName.substring(typeName.indexOf('<') + 1, typeName.lastIndexOf('>')));
            return new ListType(elementType);
        }

        DataType result = lookupType(typeName);
        if (result == null) {
            TypeInfo typeInfo = lookupTypeInfo(ensureUnqualified(typeName));
            if (typeInfo == null) {
                throw new IllegalArgumentException(String.format("Could not resolve type info for type name %s.", typeName));
            }

            result = resolveTypeInfo(typeInfo);
        }

        return result;
    }

    private DataType resolveTypeNameOrSpecifier(String typeName, TypeSpecifier typeSpecifier) {
        if ((typeName == null || typeName.isEmpty()) && typeSpecifier == null) {
            return null;
        }

        if (typeSpecifier != null) {
            return resolveTypeSpecifier(typeSpecifier);
        }

        return resolveTypeName(typeName);
    }

    private DataType lookupType(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        return resolvedTypes.get(casify(typeName));
    }

    private TypeInfo lookupTypeInfo(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        return typeInfoIndex.get(typeName);
    }

    // This method is used to ensure backwards compatible loading, type names in model info may be qualified with the model name
    private String ensureQualified(String name) {
        String qualifier = String.format("%s.", this.modelInfo.getName());
        if (!name.startsWith(qualifier)) {
            return String.format("%s%s", qualifier, name);
        }

        return name;
    }

    // This method is used to ensure backwards compatible loading, type names in model info may be qualified with the model name
    private String ensureUnqualified(String name) {
        if (name.startsWith(String.format("%s.", this.modelInfo.getName()))) {
            return name.substring(name.indexOf('.') + 1);
        }

        return name;
    }

    private SimpleType resolveSimpleType(SimpleTypeInfo t) {
        String qualifiedTypeName = ensureQualified(t.getName());
        DataType lookupType = lookupType(qualifiedTypeName);
        if (lookupType instanceof ClassType) {
            throw new IllegalArgumentException("Expected instance of SimpleType but found instance of ClassType instead.");
        }
        SimpleType result = (SimpleType)lookupType(qualifiedTypeName);
        if (result == null) {
            if (qualifiedTypeName.equals(DataType.ANY.getName())) {
                result = DataType.ANY;
            }
            else {
                result = new SimpleType(qualifiedTypeName, resolveTypeNameOrSpecifier(t.getBaseType(), t.getBaseTypeSpecifier()));
                result.setTarget(t.getTarget());
            }
            resolvedTypes.put(casify(result.getName()), result);
        }

        return result;
    }

    private DataType resolveTypeNameOrSpecifier(TupleTypeInfoElement element) {
        DataType result = resolveTypeNameOrSpecifier(element.getElementType(), element.getElementTypeSpecifier());
        if (result == null) {
            result = resolveTypeNameOrSpecifier(element.getType(), element.getTypeSpecifier());
        }

        return result;
    }

    private Collection<TupleTypeElement> resolveTupleTypeElements(Collection<TupleTypeInfoElement> infoElements) {
        List<TupleTypeElement> elements = new ArrayList();
        for (TupleTypeInfoElement e : infoElements) {
            elements.add(new TupleTypeElement(e.getName(), resolveTypeNameOrSpecifier(e)));
        }
        return elements;
    }

    private TupleType resolveTupleType(TupleTypeInfo t) {
        TupleType result = new TupleType(resolveTupleTypeElements(t.getElement()));
        return result;
    }

    private DataType resolveTypeNameOrSpecifier(ClassInfoElement element) {
        DataType result = resolveTypeNameOrSpecifier(element.getElementType(), element.getElementTypeSpecifier());
        if (result == null) {
            result = resolveTypeNameOrSpecifier(element.getType(), element.getTypeSpecifier());
        }

        return result;
    }

    /**
     * Converts a list of GenericParameterInfo definitions into their corresponding TypeParameter representations.
     *
     * @param parameterInfoList
     * @return
     */
    private List<TypeParameter> resolveGenericParameterDeclarations(List<TypeParameterInfo> parameterInfoList) {
        List<TypeParameter> genericParameters = new ArrayList<>();
        for (TypeParameterInfo parameterInfo : parameterInfoList) {
            String constraint = parameterInfo.getConstraint();
            TypeParameter.TypeParameterConstraint typeConstraint = null;
            if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.NONE.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.NONE;
            } else if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.CLASS.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.CLASS;
            } else if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.TUPLE.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.TUPLE;
            } else if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.VALUE.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.VALUE;
            } else if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.CHOICE.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.CHOICE;
            } else if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.INTERVAL.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.INTERVAL;
            } else if(constraint.equalsIgnoreCase(TypeParameter.TypeParameterConstraint.TYPE.name())) {
                typeConstraint = TypeParameter.TypeParameterConstraint.TYPE;
            }
            genericParameters.add(new TypeParameter(parameterInfo.getName(), typeConstraint, resolveTypeName(parameterInfo.getConstraintType())));
        }
        return genericParameters;
    }

    /**
     * Method resolves the types associated with class elements (i.e., class fields).
     * If the type is not resolved, the type System.Any is assigned to this element.
     *
     * @param classType
     * @param infoElements
     * @return
     */
    private Collection<ClassTypeElement> resolveClassTypeElements(ClassType classType, Collection<ClassInfoElement> infoElements) {
        List<ClassTypeElement> elements = new ArrayList();
        for (ClassInfoElement e : infoElements) {
            DataType elementType = null;
            if(isOpenType(e)) {
                elementType = resolveOpenType(classType, e);
            } else if(isBoundParameterType(e)) {
                elementType = resolveBoundType(classType, e);
            } else {
                elementType = resolveTypeNameOrSpecifier(e);
            }
            if (elementType == null) {
                elementType = resolveTypeName("System.Any");
            }
            elements.add(new ClassTypeElement(e.getName(), elementType, e.isProhibited(), e.isOneBased(), e.getTarget()));
        }
        return elements;
    }

    /**
     * Method returns true if class element is an open element bound to a specific type.
     * For instance, if the generic class defines a field:
     * <pre><code>T field1;</code></pre>
     * A subclass my bind T to a specific type such as System.Quantity such that the definition above
     * becomes:
     * <pre><code>System.Quantity field1;</code></pre>
     *
     * @param element
     * @return
     */
    private boolean isBoundParameterType(ClassInfoElement element) {
        return element.getElementTypeSpecifier() instanceof BoundParameterTypeSpecifier;
    }

    /**
     * Method resolves the bound type declaration and returns the type if valid. Method throws an exception
     * if the type cannot be resolved (does not exist) or if the parameter that this type is bound to is not defined
     * in the generic class. Types must be bound to existing generic parameters.
     *
     * @param classType
     * @param e
     * @return
     */
    private DataType resolveBoundType(ClassType classType, ClassInfoElement e) {
        DataType boundType = null;

        BoundParameterTypeSpecifier boundParameterTypeSpecifier = (BoundParameterTypeSpecifier)e.getElementTypeSpecifier();
        String parameterName = boundParameterTypeSpecifier.getParameterName();
        TypeParameter genericParameter = classType.getGenericParameterByIdentifier(parameterName);

        if(genericParameter == null) {
            throw new RuntimeException("Unknown symbol " + parameterName);
        } else {
            boundType = resolveTypeName(boundParameterTypeSpecifier.getBoundType());
        }

        return boundType;
    }

    /**
     * Returns true if the element's type is a parameterized (non-bound, non-concrete) type
     * such as
     *
     * <pre><code>T myField;</code></pre>
     *
     * @param element
     * @return
     */
    private boolean isOpenType(ClassInfoElement element) {
        return element.getElementTypeSpecifier() instanceof ParameterTypeSpecifier;
    }

    /**
     * Method to validate open types. An open type must reference a parameter defined in the generic class by name
     * and the generic parameter must exist.
     *
     * <p>
     *     Open types are class attribute types that reference one of the generic parameter of the class
     *     and that have not been bound to a concrete type.
     * </p>
     *
     * @param classType
     * @param e
     * @return
     */
    private DataType resolveOpenType(ClassType classType, ClassInfoElement e) {
        DataType elementType;
        ParameterTypeSpecifier parameterTypeSpecifier = (ParameterTypeSpecifier) e.getElementTypeSpecifier();
        String parameterName = parameterTypeSpecifier.getParameterName();
        if(parameterName == null || parameterName.trim().length() == 0 || classType.getGenericParameterByIdentifier(parameterName) == null) {
            throw new RuntimeException("Open types must reference a valid generic parameter and cannot be null or blank");
        }
        elementType = new TypeParameter(parameterTypeSpecifier.getParameterName(), TypeParameter.TypeParameterConstraint.TYPE, null);
        return elementType;
    }

    private ClassType resolveClassType(ClassInfo t) {
        if (t.getName() == null) {
            throw new IllegalArgumentException("Class definition must have a name.");
        }

        String qualifiedName = ensureQualified(t.getName());
        ClassType result = (ClassType)lookupType(qualifiedName);

        if (result == null) {
            if (t instanceof ProfileInfo) {
                result = new ProfileType(qualifiedName, resolveTypeNameOrSpecifier(t.getBaseType(), t.getBaseTypeSpecifier()));
            }
            else {
                //Added to support generic notation in ModelInfo file for class type names (e.g., MyGeneric<T>) and base classes (e.g., Map<String,Person>).
                if(t.getName().contains("<")) {
                    result = handleGenericType(t.getName(), t.getBaseType());
                } else {
                    if(t.getBaseType() != null && t.getBaseType().contains("<")) {
                        result = handleGenericType(t.getName(), t.getBaseType());
                    } else {
                        result = new ClassType(qualifiedName, resolveTypeNameOrSpecifier(t.getBaseType(), t.getBaseTypeSpecifier()));
                    }
                }
            }

            resolvedTypes.put(casify(result.getName()), result);

            if(t.getParameter() != null) {
                result.addGenericParameter(resolveGenericParameterDeclarations(t.getParameter()));
            }

            if(t.getElement() != null) {
                result.addElements(resolveClassTypeElements(result, t.getElement()));
            }

            //Here we handle the case when a type is not a generic but its base type is a generic type whose parameters
            //have all been bound to concrete types (no remaining degrees of freedom) and is not expressed in generic notation in the model-info file.
            if(isParentGeneric(result) && !t.getBaseType().contains("<")) {
                validateFreeAndBoundParameters(result, t);
            }

            result.setIdentifier(t.getIdentifier());
            result.setLabel(t.getLabel());
            result.setTarget(t.getTarget());
            result.setRetrievable(t.isRetrievable());
            result.setPrimaryCodePath(t.getPrimaryCodePath());
        }

        return result;
    }

    private ModelContext resolveContext(String contextName) {
        for (ModelContext context : this.contexts) {
            if (context.getName().equals(contextName)) {
                return context;
            }
        }

        throw new IllegalArgumentException(String.format("Could not resolve context name %s.", contextName));

    }

    private Relationship resolveRelationship(RelationshipInfo relationshipInfo) {
        ModelContext modelContext = resolveContext(relationshipInfo.getContext());
        Relationship relationship = new Relationship(modelContext, Arrays.asList(relationshipInfo.getRelatedKeyElement().split(";")));
        // TODO: Validate relatedKeyElements match keyElements of the referenced context
        return relationship;
    }

    private void importRelationships(ClassInfo c, ClassType t) {
        for (RelationshipInfo r : c.getContextRelationship()) {
            t.addRelationship(resolveRelationship(r));
        }

        for (RelationshipInfo r : c.getTargetContextRelationship()) {
            t.addTargetRelationship(resolveRelationship(r));
        }
    }

    private IntervalType resolveIntervalType(IntervalTypeInfo t) {
        IntervalType result = new IntervalType(resolveTypeNameOrSpecifier(t.getPointType(), t.getPointTypeSpecifier()));
        return result;
    }

    private ListType resolveListType(ListTypeInfo t) {
        ListType result = new ListType(resolveTypeNameOrSpecifier(t.getElementType(), t.getElementTypeSpecifier()));
        return result;
    }

    private ChoiceType resolveChoiceType(ChoiceTypeInfo t) {
        ArrayList<DataType> types = new ArrayList<DataType>();
        if (t.getChoice() != null && t.getChoice().size() > 0) {
            for (TypeSpecifier typeSpecifier : t.getChoice()) {
                types.add(resolveTypeSpecifier(typeSpecifier));
            }
        }
        else {
            for (TypeSpecifier typeSpecifier : t.getType()) {
                types.add(resolveTypeSpecifier(typeSpecifier));
            }
        }
        return new ChoiceType(types);
    }

    /**
     * Method checks to see if a class' parameters covers its parent parameters. These represent
     * remaining degrees of freedom in the child class. For instance,
     * <pre><code>MyGeneric&lt;T&gt; extends SomeOtherGeneric&lt;String,T&gt;</code></pre>
     * All parameters in the parent class, not covered by the child class must be bound
     * to a concrete type. In the above example, the parameter S is bound to the type String.
     * <p>If a parameter in the parent type is not covered by a child parameter nor bound to
     * a concrete type, an exception is thrown indicating that the symbol is not known. In the example
     * below, T is neither covered nor bound and thus is an unknown symbol.</p>
     * <code><pre>MyGeneric extends SomeOtherGeneric&lt;String,T&gt;</pre></code>
     *
     * @param type
     * @param definition
     */
    public void validateFreeAndBoundParameters(ClassType type, ClassInfo definition) {
        List<String> coveredParameters = new ArrayList<>();
        List<String> boundParameters = new ArrayList<>();

        ((ClassType)type.getBaseType()).getGenericParameters().forEach(typeParameter -> {
            String parameterName = typeParameter.getIdentifier();
            if(type.getGenericParameterByIdentifier(parameterName, true) != null) {
                coveredParameters.add(parameterName);
            } else {
                boundParameters.add(parameterName);
            }
        });

        if(boundParameters.size() > 0) {
            if(definition.getElement() != null) {
                definition.getElement().forEach(classInfoElement -> {
                    if (classInfoElement.getElementTypeSpecifier() instanceof BoundParameterTypeSpecifier) {
                        String name = ((BoundParameterTypeSpecifier)classInfoElement.getElementTypeSpecifier()).getParameterName();
                        int paramIndex = boundParameters.indexOf(name);
                        if(paramIndex >= 0) {
                            boundParameters.remove(paramIndex);
                        }
                    }
                });
                if(boundParameters.size() > 0) {
                    throw new RuntimeException("Unknown symbols " + boundParameters);
                }
            } else {
                throw new RuntimeException("Unknown symbols " + boundParameters);
            }
        }
    }

    /**
     * Method returns true if the class' base type is a generic type.
     *
     * @param type
     * @return True if the parent of class 'type' is a generic class.
     */
    public boolean isParentGeneric(ClassType type) {
        DataType baseType = type.getBaseType();
        return baseType != null && baseType instanceof ClassType && ((ClassType)baseType).isGeneric();
    }

    /**
     * Converts a generic type declaration represented as a string into the corresponding
     * generic ClassType (i.e., a class type that specifies generic parameters).
     *
     * @param baseType The base type for the generic class type.
     * @param genericSignature The signature of the generic type such as Map&lt;K,V&gt;.
     * @return
     */
    private ClassType handleGenericType(String genericSignature, String baseType) {
        if (genericSignature == null) {
            throw new IllegalArgumentException("genericSignature is null");
        }

        GenericClassSignatureParser parser = new GenericClassSignatureParser(genericSignature, baseType, null, resolvedTypes);
        ClassType genericClassType = parser.parseGenericSignature();

        return genericClassType;
    }

    /**
     * Checks whether descendant is a valid subtype of ancestor.
     *
     * @param descendant
     * @param ancestor
     * @return
     */
    private boolean conformsTo(DataType descendant, DataType ancestor) {
        boolean conforms = false;
        if(descendant != null && ancestor != null && descendant.equals(ancestor)) {
            conforms = true;
        } else {
            conforms = conformsTo(descendant.getBaseType(), ancestor);
        }
        return conforms;
    }

}
