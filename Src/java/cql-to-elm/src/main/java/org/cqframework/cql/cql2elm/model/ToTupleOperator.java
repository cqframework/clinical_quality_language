package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.cql.model.TypeParameter;

public class ToTupleOperator extends GenericOperator {
    public ToTupleOperator(String name, Signature signature, DataType resultType, TypeParameter... typeParameters) {
        super(name, signature, resultType, typeParameters);
    }
}
