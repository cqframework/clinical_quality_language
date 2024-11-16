package org.cqframework.cql.cql2elm.model;

import java.util.List;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TypeSpecifier;

public interface Invocation {
    List<TypeSpecifier> getSignature();

    void setSignature(List<TypeSpecifier> signature);

    List<Expression> getOperands();

    void setOperands(List<Expression> operands);

    void setResultType(DataType resultType);

    Expression getExpression();

    void setResolution(OperatorResolution resolution);

    OperatorResolution getResolution();
}
