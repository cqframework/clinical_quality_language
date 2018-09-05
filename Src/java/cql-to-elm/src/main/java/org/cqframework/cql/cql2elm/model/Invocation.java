package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TypeSpecifier;

public interface Invocation {
    Iterable<TypeSpecifier> getSignature();

    void setSignature(Iterable<TypeSpecifier> signature);

    Iterable<Expression> getOperands();

    void setOperands(Iterable<Expression> operands);

    void setResultType(DataType resultType);

    Expression getExpression();

    void setResolution(OperatorResolution resolution);

    OperatorResolution getResolution();
}
