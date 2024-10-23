package org.hl7.cql.model;

public interface InstantiationContext extends ResolutionContext {
    boolean isInstantiable(TypeParameter parameter, DataType callType);

    DataType instantiate(TypeParameter parameter);

    Iterable<SimpleType> getSimpleConversionTargets(DataType callType);

    Iterable<IntervalType> getIntervalConversionTargets(DataType callType);

    Iterable<ListType> getListConversionTargets(DataType callType);
}
