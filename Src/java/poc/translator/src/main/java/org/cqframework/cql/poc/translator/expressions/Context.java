package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/28/14.
 */
public interface Context {

    public Object get(String key);

    public Object set(String key, Object obj);

    public Object get(Object key);

    public Object set(Object key, Object obj);

    public Callable getFunction(Object identifier);
}
