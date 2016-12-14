package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.Expression;

public interface Invocation {
    Iterable<Expression> getOperands();

    void setOperands(Iterable<Expression> operands);

    void setResultType(DataType resultType);

    Expression getExpression();
}
