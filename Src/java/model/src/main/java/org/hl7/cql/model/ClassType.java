package org.hl7.cql.model;

import java.util.*;

public class ClassType extends DataType implements NamedType {

    public ClassType(String name, DataType baseType, Collection<ClassTypeElement> elements, Collection<TypeParameter> parameters) {
        super(baseType);

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is null");
        }

        this.name = name;

        if (parameters != null) {
            this.genericParameters.addAll(parameters);
        }

        if (elements != null) {
            this.elements.addAll(elements);
        }
    }

    public ClassType() {
        this(null, null, null, null);
    }

    public ClassType(String name) {
        this(name, null, null, null);
    }

    public ClassType(String name, DataType baseType) {
        this(name, baseType, null, null);
    }

    public ClassType(String name, DataType baseType, Collection<ClassTypeElement> elements) {
        this(name, baseType, elements, null);
    }

    private String name;
    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        if (this.name != null) {
            int qualifierIndex = this.name.indexOf('.');//TODO Should this not be the last occurrence rather than the first occurrence?
            if (qualifierIndex > 0) {
                return this.name.substring(0, qualifierIndex);
            }
        }

        return "";
    }

    public String getSimpleName() {
        if (this.name != null) {
            int qualifierIndex = this.name.indexOf('.');//TODO Should this not be the last occurrence rather than the first occurrence?
            if (qualifierIndex > 0) {
                return this.name.substring(qualifierIndex + 1);
            }
        }

        return this.name;
    }

    private String identifier;
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    private String label;
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    private boolean retrievable;
    public boolean isRetrievable() {
        return retrievable;
    }
    public void setRetrievable(boolean retrievable) {
        this.retrievable = retrievable;
    }

    private String primaryCodePath;
    public String getPrimaryCodePath() { return primaryCodePath; }
    public void setPrimaryCodePath(String primaryCodePath) {
        this.primaryCodePath = primaryCodePath;
    }

    private String primaryValueSetPath;
    public String getPrimaryValueSetPath() { return primaryValueSetPath; }
    public void setPrimaryValueSetPath(String primaryValueSetPath) {
        this.primaryValueSetPath = primaryValueSetPath;
    }

    private List<Relationship> relationships = new ArrayList<>();
    public Iterable<Relationship> getRelationships() {
        return relationships;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    private List<Relationship> targetRelationships = new ArrayList<>();
    public Iterable<Relationship> getTargetRelationships() {
        return targetRelationships;
    }

    public void addTargetRelationship(Relationship relationship) {
        targetRelationships.add(relationship);
    }

    /**
     * Generic class parameters such 'S', 'T extends MyType'.
     */
    private List<TypeParameter> genericParameters = new ArrayList<>();

    /**
     * Returns the generic parameters for the generic type. For instance,
     * for the generic type Map&lt;K,V extends Person&gt;, two generic parameters
     * will be returned: K and V extends Person. The latter parameter has a constraint
     * restricting the type of the bound type to be a valid subtype of Person.
     *
     * @return Class' generic parameters
     */
    public List<TypeParameter> getGenericParameters() {
        return genericParameters;
    }

    /**
     * Sets the generic parameters for the generic type. For instance,
     * for the generic type Map&lt;K,V extends Person&gt;, two generic parameters
     * should be set: K and V extends Person. The latter parameter has a constraint
     * restricting the type of the bound type to be a valid subtype of Person.
     *
     * @param genericParameters
     */
    public void setGenericParameters(List<TypeParameter> genericParameters) {
        this.genericParameters = genericParameters;
    }

    /**
     * Adds a parameter declaration to the generic type.
     *
     * @param genericParameter
     */
    public void addGenericParameter(TypeParameter genericParameter) {
        this.genericParameters.add(genericParameter);
    }

    /**
     * Adds collection of type parameters to existing set.
     * @param parameters
     */
    public void addGenericParameter(Collection<TypeParameter> parameters) {
        for (TypeParameter parameter : parameters) {
            internalAddParameter(parameter);
        }

        sortedElements = null;
        tupleType = null;
    }

    /**
     * Returns the parameter with the given parameter identifier.
     * If not found in the given class, it looks in the parent class.
     *
     * @param identifier
     * @return Generic parameter with the given name in the current class or in the base class. Null if none found.
     */
    public TypeParameter getGenericParameterByIdentifier(String identifier) {
        return getGenericParameterByIdentifier(identifier, false);
    }

    /**
     * Returns the parameter with the given parameter identifier.
     * If inCurrentClassOnly is false, if not found in the given class, then it looks in the parent class.
     * If inCurrentClassOnly is true, only looks for parameter in the given class.
     *
     * @param identifier
     * @param inCurrentClassOnly
     * @return Class' generic parameter
     */
    public TypeParameter getGenericParameterByIdentifier(String identifier, boolean inCurrentClassOnly) {
        TypeParameter param = null;
        for(TypeParameter genericParameter: genericParameters) {
            if(identifier.equalsIgnoreCase(genericParameter.getIdentifier())) {
                param = genericParameter;
                break;
            }
        }
        if(!inCurrentClassOnly && param == null) {
            if (param == null && getBaseType() instanceof ClassType) {
                param = ((ClassType) getBaseType()).getGenericParameterByIdentifier(identifier);
            }
        }
        return param;
    }

    private List<ClassTypeElement> elements = new ArrayList<ClassTypeElement>();
    private List<ClassTypeElement> sortedElements = null;
    private LinkedHashMap<String, ClassTypeElement> baseElementMap = null;

    public List<ClassTypeElement> getElements() {
        return elements;
    }

    private LinkedHashMap<String, ClassTypeElement> getBaseElementMap() {
        if (baseElementMap == null) {
            baseElementMap = new LinkedHashMap<>();
            if (getBaseType() instanceof ClassType) {
                ((ClassType)getBaseType()).gatherElements(baseElementMap);
            }
        }

        return baseElementMap;
    }

    private void gatherElements(LinkedHashMap<String, ClassTypeElement> elementMap) {
        if (getBaseType() instanceof ClassType) {
            ((ClassType)getBaseType()).gatherElements(elementMap);
        }

        for (ClassTypeElement element : elements) {
            elementMap.put(element.getName(), element);
        }
    }

    public List<ClassTypeElement> getAllElements() {
        // Get the baseClass elements into a map by name
        LinkedHashMap<String, ClassTypeElement> elementMap = new LinkedHashMap<>(getBaseElementMap());

        // Add this class's elements, overwriting baseClass definitions where applicable
        for (ClassTypeElement el : elements) {
            elementMap.put(el.getName(), el);
        }

        return new ArrayList<>(elementMap.values());
    }

    private void internalAddElement(ClassTypeElement element) {
        ClassTypeElement existingElement = getBaseElementMap().get(element.getName());
        if (
            existingElement != null && !(existingElement.getType() instanceof TypeParameter)
            && (
                !(
                    element.getType().isSubTypeOf(existingElement.getType())
                    || (
                        existingElement.getType() instanceof ListType
                        && element.getType().isSubTypeOf(((ListType)existingElement.getType()).getElementType())
                    )
                )
            )
        ) {
            throw new InvalidRedeclarationException(this, existingElement, element);
        }

        this.elements.add(element);
    }

    private void internalAddParameter(TypeParameter parameter) {
        //TODO Flesh out and retain method only if needed.

        this.genericParameters.add(parameter);
    }

    public void addElement(ClassTypeElement element)
    {
        internalAddElement(element);
        sortedElements = null;
        tupleType = null;
    }

    public void addElements(Collection<ClassTypeElement> elements) {
        for (ClassTypeElement element : elements) {
            internalAddElement(element);
        }

        sortedElements = null;
        tupleType = null;
    }

    private List<ClassTypeElement> getSortedElements() {
        if (sortedElements == null) {
            sortedElements = new ArrayList<>(elements);
            Collections.sort(sortedElements, (left, right) -> left.getName().compareTo(right.getName()));
        }

        return sortedElements;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClassType) {
            ClassType that = (ClassType)o;
            return this.name.equals(that.name);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String toLabel() {
        return this.label == null ? this.name : this.label;
    }

    private TupleType tupleType;
    public TupleType getTupleType() {
        if (tupleType == null) {
            tupleType = buildTupleType();
        }

        return tupleType;
    }

    private void addTupleElements(ClassType classType, LinkedHashMap<String, TupleTypeElement> elements) {
        // Add base elements first
        DataType baseType = classType.getBaseType();
        if (baseType instanceof ClassType) {
            addTupleElements((ClassType)baseType, elements);
        }

        for (ClassTypeElement element : classType.getElements()) {
            if (!element.isProhibited()) {
                TupleTypeElement tupleElement = new TupleTypeElement(element.getName(), element.getType());
                elements.put(tupleElement.getName(), tupleElement);
            }
        }
    }

    private TupleType buildTupleType() {
        LinkedHashMap<String, TupleTypeElement> tupleElements = new LinkedHashMap<>();

        addTupleElements(this, tupleElements);

        return new TupleType(tupleElements.values());
    }

    @Override
    public boolean isCompatibleWith(DataType other) {
        if (other instanceof TupleType) {
            TupleType tupleType = (TupleType)other;
            return getTupleType().equals(tupleType);
        // Github #115: It's incorrect for a class type to be considered compatible with another class type on the basis of the inferred tuple type alone.
        //} else if (other instanceof ClassType) {
        //    ClassType classType = (ClassType)other;
        //    return getTupleType().equals(classType.getTupleType());
        }

        return super.isCompatibleWith(other);
    }

    @Override
    public boolean isGeneric() {
        return genericParameters != null && genericParameters.size() > 0;
    }

    @Override
    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        if (callType instanceof ClassType) {
            ClassType classType = (ClassType)callType;
            if (elements.size() == classType.elements.size()) {
                List<ClassTypeElement> theseElements = getSortedElements();
                List<ClassTypeElement> thoseElements = classType.getSortedElements();
                for (int i = 0; i < theseElements.size(); i++) {
                    if (!(theseElements.get(i).getName().equals(thoseElements.get(i).getName())
                            && theseElements.get(i).getType().isInstantiable(thoseElements.get(i).getType(), context))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        if (!isGeneric()) {
            return this;
        }

        ClassType result = new ClassType(getName(), getBaseType());
        for (int i = 0; i < elements.size(); i++) {
            result.addElement(new ClassTypeElement(elements.get(i).getName(), elements.get(i).getType().instantiate(context)));
        }

        return result;
    }
}
