package org.cqframework.cql.elm.tracking;

import java.util.*;

public class TupleType extends DataType implements NamedType {
    private String name;
    private List<TupleTypeElement> elements = new ArrayList<TupleTypeElement>();
    private List<TupleTypeElement> sortedElements = null;

    public TupleType(String name, DataType baseType, Collection<TupleTypeElement> elements) {
        super(baseType);

        this.name = name;
        if (elements != null) {
            this.elements.addAll(elements);
        }
    }

    public TupleType() {
        this(null, null, null);
    }

    public TupleType(String name) {
        this(name, null, null);
    }

    public TupleType(String name, DataType baseType) {
        this(name, baseType, null);
    }

    public TupleType(Collection<TupleTypeElement> elements) {
        this(null, null, elements);
    }

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

    public Iterable<TupleTypeElement> getElements() {
        return elements;
    }

    public void addElement(TupleTypeElement element)
    {
        this.elements.add(element);
        sortedElements = null;
    }

    public void addElements(Collection<TupleTypeElement> elements) {
        this.elements.addAll(elements);
        sortedElements = null;
    }

    private List<TupleTypeElement> getSortedElements() {
        if (sortedElements == null) {
            sortedElements = new ArrayList<>(elements);
            Collections.sort(sortedElements, TupleTypeElementComparator);
        }

        return sortedElements;
    }

    private static Comparator<TupleTypeElement> TupleTypeElementComparator = new Comparator<TupleTypeElement>() {
        public int compare(TupleTypeElement left, TupleTypeElement right) {
            return left.getName().compareTo(right.getName());
        }
    };

    @Override
    public int hashCode() {
        if (this.name != null) {
            return this.name.hashCode();
        }

        int result = 13;
        for (int i = 0; i < elements.size(); i++) {
            result += (37 * elements.get(i).hashCode());
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TupleType) {
            TupleType that = (TupleType)o;
            if (this.name != null && that.name != null && this.name.equals(that.name)) {
                return true;
            }

            if (this.elements.size() == that.elements.size()) {
                List<TupleTypeElement> theseElements = this.getSortedElements();
                List<TupleTypeElement> thoseElements = that.getSortedElements();
                for (int i = 0; i < theseElements.size(); i++) {
                    if (!theseElements.get(i).equals(thoseElements.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSubTypeOf(DataType other) {
        if (other instanceof TupleType) {
            TupleType that = (TupleType)other;

            if ((this.name == null || that.name == null)
                    && (this.elements.size() == that.elements.size())) {
                List<TupleTypeElement> theseElements = this.getSortedElements();
                List<TupleTypeElement> thoseElements = that.getSortedElements();
                for (int i = 0; i < theseElements.size(); i++) {
                    if (!theseElements.get(i).isSubTypeOf(thoseElements.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return super.isSubTypeOf(other);
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        if (other instanceof TupleType) {
            TupleType that = (TupleType)other;

            if ((this.name == null || that.name == null)
                    && (this.elements.size() == that.elements.size())) {
                List<TupleTypeElement> theseElements = this.getSortedElements();
                List<TupleTypeElement> thoseElements = that.getSortedElements();
                for (int i = 0; i < theseElements.size(); i++) {
                    if (!theseElements.get(i).isSuperTypeOf(thoseElements.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return super.isSuperTypeOf(other);
    }

    @Override
    public String toString() {
        if (this.name != null) {
            return this.name;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("tuple{");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(elements.get(i).toString());
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean isGeneric() {
        for (TupleTypeElement e : elements) {
            if (e.getType().isGeneric()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInstantiable(DataType callType, Map<TypeParameter, DataType> typeMap) {
        if (callType instanceof TupleType) {
            TupleType tupleType = (TupleType)callType;
            if (elements.size() == tupleType.elements.size()) {
                List<TupleTypeElement> theseElements = getSortedElements();
                List<TupleTypeElement> thoseElements = tupleType.getSortedElements();
                for (int i = 0; i < theseElements.size(); i++) {
                    if (!(theseElements.get(i).getName().equals(thoseElements.get(i).getName())
                        && theseElements.get(i).getType().isInstantiable(thoseElements.get(i).getType(), typeMap))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public DataType instantiate(Map<TypeParameter, DataType> typeMap) {
        if (!isGeneric()) {
            return this;
        }

        TupleType result = new TupleType(getName(), getBaseType());
        for (int i = 0; i < elements.size(); i++) {
            result.addElement(new TupleTypeElement(elements.get(i).getName(), elements.get(i).getType().instantiate(typeMap)));
        }

        return result;
    }
}
