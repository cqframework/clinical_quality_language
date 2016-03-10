package org.cqframework.cql.elm.tracking;

import java.util.*;

public class ClassType extends DataType implements NamedType {

    public ClassType(String name, DataType baseType, Collection<ClassTypeElement> elements) {
        super(baseType);

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is null");
        }

        this.name = name;

        if (elements != null) {
            this.elements.addAll(elements);
        }
    }

    public ClassType() {
        this(null, null, null);
    }

    public ClassType(String name) {
        this(name, null, null);
    }

    public ClassType(String name, DataType baseType) {
        this(name, baseType, null);
    }

    private String name;
    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        if (this.name != null) {
            int qualifierIndex = this.name.indexOf('.');
            if (qualifierIndex > 0) {
                return this.name.substring(0, qualifierIndex);
            }
        }

        return "";
    }

    public String getSimpleName() {
        if (this.name != null) {
            int qualifierIndex = this.name.indexOf('.');
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
    public void setPrimaryCodePath(String primaryCodePath) { this.primaryCodePath = primaryCodePath; }

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
            existingElement != null
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
        } else if (other instanceof ClassType) {
            ClassType classType = (ClassType)other;
            return getTupleType().equals(classType.getTupleType());            
        }

        return false;
    }

    @Override
    public boolean isGeneric() {
        for (ClassTypeElement e : elements) {
            if (e.getType().isGeneric()) {
                return true;
            }
        }

        return false;
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
