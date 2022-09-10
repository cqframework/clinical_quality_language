package org.cqframework.cql.elm.tags;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class ForwardingSet<T> implements Set<T> {

    protected abstract Set<T> delegate();

    @Override
    public boolean add(T value) {
        return this.delegate().add(value);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return this.delegate().addAll(collection);
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public boolean contains(Object value) {
        return this.delegate().contains(value);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.delegate().containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.delegate().iterator();
    }

    @Override
    public boolean remove(Object value) {
        return this.delegate().remove(value);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return this.delegate().removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return this.delegate().retainAll(collection);
    }

    @Override
    public int size() {
        return this.delegate().size();
    }

    @Override
    public Object[] toArray() {
        return this.delegate().toArray();
    }

    @Override
    public <K> K[] toArray(K[] array) {
        return this.delegate().toArray(array);
    }
}
