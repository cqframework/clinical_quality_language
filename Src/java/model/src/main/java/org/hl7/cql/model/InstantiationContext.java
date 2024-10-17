package org.hl7.cql.model;

public interface InstantiationContext {

    DataType instantiate(TypeParameter parameter, DataType callType);

    Iterable<IntervalType> getIntervalConversionTargets(DataType callType);

    Iterable<ListType> getListConversionTargets(DataType callType);
}
