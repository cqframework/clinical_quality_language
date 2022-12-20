package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import javax.xml.namespace.QName;

import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.InvalidLiteral;
import org.opencds.cqf.cql.engine.execution.Context;

public class LiteralEvaluator extends org.cqframework.cql.elm.execution.Literal {

    @Override
    protected Object internalEvaluate(Context context) {
        QName valueType = context.fixupQName(this.getValueType());
        switch (valueType.getLocalPart()) {
            case "Boolean": return Boolean.parseBoolean(this.getValue());
            case "Integer":
                int intValue;
                try {
                    intValue = Integer.parseInt(this.getValue());
                } catch(NumberFormatException e){
                    throw new CqlException("Bad format for Integer literal");
                }
                return intValue;
            case "Long":
                long longValue;
                try {
                    longValue = Long.parseLong(this.getValue());
                } catch(NumberFormatException e){
                    throw new CqlException("Bad format for Long literal");
                }
                return longValue;
            case "Decimal":
                BigDecimal bigDecimalValue;

                try {
                    bigDecimalValue = new BigDecimal(this.getValue());
                } catch (NumberFormatException nfe) {
                    throw new CqlException(nfe.getMessage());
                }
                return bigDecimalValue;
            case "String": return this.getValue();
            default: throw new InvalidLiteral(String.format("Cannot construct literal value for type '%s'.", valueType.toString()));
        }
    }
}
