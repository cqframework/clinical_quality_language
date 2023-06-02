package org.opencds.cqf.cql.engine.runtime;

public interface CqlType {
    Boolean equivalent(Object other);
    Boolean equal(Object other);
}
