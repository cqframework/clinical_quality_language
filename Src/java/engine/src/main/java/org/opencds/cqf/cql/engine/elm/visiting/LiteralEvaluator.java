package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.InvalidLiteral;
import org.opencds.cqf.cql.engine.execution.State;

import javax.xml.namespace.QName;
import java.math.BigDecimal;

public class LiteralEvaluator {
    public static Object internalEvaluate(QName valueT, String value, State state) {
        QName valueType = state.fixupQName(valueT);
        switch (valueType.getLocalPart()) {
            case "Boolean": return Boolean.parseBoolean(value);
            case "Integer":
                int intValue;
                try {
                    intValue = Integer.parseInt(value);
                } catch(NumberFormatException e){
                    throw new CqlException("Bad format for Integer literal");
                }
                return intValue;
            case "Long":
                long longValue;
                try {
                    longValue = Long.parseLong(value);
                } catch(NumberFormatException e){
                    throw new CqlException("Bad format for Long literal");
                }
                return longValue;
            case "Decimal":
                BigDecimal bigDecimalValue;

                try {
                    bigDecimalValue = new BigDecimal(value);
                } catch (NumberFormatException nfe) {
                    throw new CqlException(nfe.getMessage());
                }
                return bigDecimalValue;
            case "String": return value;
            default: throw new InvalidLiteral(String.format("Cannot construct literal value for type '%s'.", valueType.toString()));
        }
    }
}
