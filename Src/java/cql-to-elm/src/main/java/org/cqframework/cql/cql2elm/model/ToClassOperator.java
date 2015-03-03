package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.TypeParameter;

public class ToClassOperator extends GenericOperator {
    public ToClassOperator(String name, Signature signature, DataType resultType, TypeParameter... typeParameters) {
        super(name, signature, resultType, typeParameters);
    }
}
