package org.cqframework.cql.elm.tracking;

public interface InstantiationContext {
    boolean isInstantiable(TypeParameter parameter, DataType callType);
    DataType instantiate(TypeParameter parameter);
}
