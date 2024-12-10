package org.hl7.cql.model;

public interface DataType {
    DataType getBaseType();

    String toLabel();

    boolean isSubTypeOf(DataType other);

    boolean isSuperTypeOf(DataType other);

    DataType getCommonSuperTypeOf(DataType other);

    // Note that this is not how implicit/explicit conversions are defined, the notion of
    // type compatibility is used to support implicit casting, such as casting a "null"
    // literal to any other type, or casting a class to an equivalent tuple.
    boolean isCompatibleWith(DataType other);

    boolean isGeneric();

    boolean isInstantiable(DataType callType, InstantiationContext context);

    DataType instantiate(InstantiationContext context);

    SimpleType ANY = new SimpleType("System.Any");
}
