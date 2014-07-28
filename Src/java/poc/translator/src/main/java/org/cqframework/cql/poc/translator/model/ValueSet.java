package org.cqframework.cql.poc.translator.model;

import org.cqframework.cql.poc.translator.model.logger.Trackable;

import java.util.Collection;
import java.util.HashSet;

public class ValueSet extends Trackable {
    private final String id;
    // TODO: Add version?
    // TODO: Add authority?
    private final HashSet<String> localStringIdentifiers;

    public ValueSet(String id) {
        super();

        this.id = id;
        this.localStringIdentifiers = new HashSet<>();
    }

    public ValueSet(String id, String localStringIdentifier) {
        this(id);
        addLocalStringIdentifier(localStringIdentifier);
    }

    public String getId() {
        return id;
    }

    public Collection<String> getLocalStringIdentifiers() {
        return localStringIdentifiers;
    }

    public void addLocalStringIdentifier(String localStringIdentifier) {
        this.localStringIdentifiers.add(localStringIdentifier);
    }

    public void merge(ValueSet other) {
        super.merge(other);
        this.localStringIdentifiers.addAll(other.getLocalStringIdentifiers());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueSet valueSet = (ValueSet) o;

        if (id != null ? !id.equals(valueSet.id) : valueSet.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ValueSet{" +
                "id='" + id + '\'' +
                ", localStringIdentifiers=" + localStringIdentifiers +
                '}';
    }
}
