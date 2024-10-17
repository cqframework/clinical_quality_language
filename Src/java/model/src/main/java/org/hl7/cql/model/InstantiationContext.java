package org.hl7.cql.model;

public interface InstantiationContext {
    boolean isInstantiable(TypeParameter parameter, DataType callType);

    DataType instantiate(TypeParameter parameter);

    Iterable<IntervalType> getIntervalConversionTargets(DataType callType);

    Iterable<ListType> getListConversionTargets(DataType callType);
}
