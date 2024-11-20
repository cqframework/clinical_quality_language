package org.cqframework.cql.cql2elm.model;

import java.util.List;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TypeSpecifier;

/**
 * The Invocation interface is used to represent an invocation of an operator or function in the ELM model.
 * The ELM classes have named properties for their operands, but the Invocation interface uses a list of expressions to represent the operands.
 * The implementations of this interface are responsible for managing the mapping between the list of expressions and the
 * properties of the ELM class. For example, the DateInvocation class maps properties for year, month, and day to the first, second, and third
 * expressions in the list of operands. This allows Invocations to be handled generically in the CQL-to-ELM translation process.
 */
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
