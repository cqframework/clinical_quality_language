package org.opencds.cqf.cql.engine.elm.execution;

import javax.xml.namespace.QName;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
maximum<T>() T

The maximum operator returns the maximum representable value for the given type.
The maximum operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, maximum returns the maximum signed 32-bit integer, 231 - 1.
For Long, maximum returns the maximum signed 64-bit Long, 263 - 1.
For Decimal, maximum returns the maximum representable decimal value, (1037 – 1) / 108 (9999999999999999999999999999.99999999).
For DateTime, maximum returns the maximum representable date/time value, DateTime(9999, 12, 31, 23, 59, 59, 999).
For Time, maximum returns the maximum representable time value, Time(23, 59, 59, 999).
For any other type, attempting to invoke maximum results in an error.
*/

public class MaxValueEvaluator extends org.cqframework.cql.elm.execution.MaxValue {

    public static Object maxValue(String type) {
        if (type == null) {
            return null;
        }

        if (type.endsWith("Integer")) {
            return Value.MAX_INT;
        }
        if (type.endsWith("Long")) {
            return Value.MAX_LONG;
        }
        if (type.endsWith("Decimal")) {
            return Value.MAX_DECIMAL;
        }
        if (type.endsWith("Date")) {
            return new Date(9999, 12, 31);
        }
        if (type.endsWith("DateTime")) {
            return new DateTime(null, 9999, 12, 31, 23, 59, 59, 999);
        }
        if (type.endsWith("Time")) {
            return new Time(23, 59, 59, 999);
        }
        // NOTE: Quantity max is not standard
        if (type.endsWith("Quantity")) {
            return new Quantity().withValue(Value.MAX_DECIMAL).withUnit("1");
        }

        throw new InvalidOperatorArgument(String.format("The Maximum operator is not implemented for type %s", type));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        QName valueType = context.fixupQName(this.getValueType());
        String type = valueType.getLocalPart();
        return maxValue(type);
    }
}
