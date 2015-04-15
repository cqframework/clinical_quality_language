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

    public List<ClassTypeElement> getElements() {
        return elements;
    }

    public List<ClassTypeElement> getAllElements() {
        LinkedHashMap<String, ClassTypeElement> elementMap = new LinkedHashMap<>();

        // Get the baseClass elements into a map by name
        if (getBaseType() instanceof ClassType) {
            List<ClassTypeElement> baseElements = ((ClassType) getBaseType()).getAllElements();
            for (ClassTypeElement el : baseElements) {
                elementMap.put(el.getName(), el);
            }
        }

        // Add this class's elements, overwriting baseClass definitions where applicable
        for (ClassTypeElement el : elements) {
            elementMap.put(el.getName(), el);
        }

        return new ArrayList<>(elementMap.values());
    }

    public void addElement(ClassTypeElement element)
    {
        this.elements.add(element);
        sortedElements = null;
        tupleType = null;
    }

    public void addElements(Collection<ClassTypeElement> elements) {
        this.elements.addAll(elements);
        sortedElements = null;
        tupleType = null;
    }

    private List<ClassTypeElement> getSortedElements() {
        if (sortedElements == null) {
            sortedElements = new ArrayList<>(elements);
            Collections.sort(sortedElements, ClassTypeElementComparator);
        }

        return sortedElements;
    }

    private static Comparator<ClassTypeElement> ClassTypeElementComparator = new Comparator<ClassTypeElement>() {
        public int compare(ClassTypeElement left, ClassTypeElement right) {
            return left.getName().compareTo(right.getName());
        }
    };

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
    private TupleType getTupleType() {
        if (tupleType == null) {
            tupleType = buildTupleType();
        }

        return tupleType;
    }

    private TupleType buildTupleType() {
        List<TupleTypeElement> tupleElements = new ArrayList<>();
        DataType currentType = this;
        while (currentType != null) {
            if (currentType instanceof ClassType) {
                ClassType classType = (ClassType)currentType;
                for (ClassTypeElement element : classType.getElements()) {
                    TupleTypeElement tupleElement = new TupleTypeElement(element.getName(), element.getType());
                    tupleElements.add(tupleElement);
                }
            }

            currentType = currentType.getBaseType();
        }

        return new TupleType(tupleElements);
    }

    @Override
    public boolean isCompatibleWith(DataType other) {
        // TODO: Class types are compatible with tuple types...
        if (other instanceof TupleType) {
            TupleType tupleType = (TupleType)other;
            return getTupleType().equals(tupleType);
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
