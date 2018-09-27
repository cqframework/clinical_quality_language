package org.hl7.cql.model;

import java.util.*;

public class TupleType extends DataType {
    private List<TupleTypeElement> elements = new ArrayList<TupleTypeElement>();
    private List<TupleTypeElement> sortedElements = null;

    public TupleType(Collection<TupleTypeElement> elements) {
        super();

        if (elements != null) {
            this.elements.addAll(elements);
        }
    }

    public TupleType() {
        this(null);
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
            Collections.sort(sortedElements, (left, right) -> left.getName().compareTo(right.getName()));
        }

        return sortedElements;
    }

    @Override
    public int hashCode() {
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

            if (this.elements.size() == that.elements.size()) {
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

            if (this.elements.size() == that.elements.size()) {
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
    public String toLabel() {
        StringBuilder builder = new StringBuilder();
        builder.append("tuple of ");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(elements.get(i).toLabel());
        }
        return builder.toString();
    }

    @Override
    public boolean isCompatibleWith(DataType other) {
        if (other instanceof ClassType) {
            ClassType classType = (ClassType)other;
            return this.equals(classType.getTupleType());
        }

        return super.isCompatibleWith(other);
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
    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        if (callType instanceof TupleType) {
            TupleType tupleType = (TupleType)callType;
            if (elements.size() == tupleType.elements.size()) {
                List<TupleTypeElement> theseElements = getSortedElements();
                List<TupleTypeElement> thoseElements = tupleType.getSortedElements();
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

        TupleType result = new TupleType();
        for (int i = 0; i < elements.size(); i++) {
            result.addElement(new TupleTypeElement(elements.get(i).getName(), elements.get(i).getType().instantiate(context)));
        }

        return result;
    }
}
