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
minimum<T>() T

The minimum operator returns the minimum representable value for the given type.
The minimum operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, minimum returns the minimum signed 32-bit integer, -231.
For Long, minimum returns the minimum signed 32-bit integer, -263.
For Decimal, minimum returns the minimum representable decimal value, (-1037 – 1) / 108 (-9999999999999999999999999999.99999999).
For DateTime, minimum returns the minimum representable date/time value, DateTime(1, 1, 1, 0, 0, 0, 0).
For Time, minimum returns the minimum representable time value, Time(0, 0, 0, 0).
For any other type, attempting to invoke minimum results in an error.
*/

public class MinValueEvaluator extends org.cqframework.cql.elm.execution.MinValue {

    public static Object minValue(String type) {
        if (type == null) {
            return null;
        }

        if (type.endsWith("Integer")) {
            return Value.MIN_INT;
        }
        if (type.endsWith("Long")) {
            return Value.MIN_LONG;
        }
        if (type.endsWith("Decimal")) {
            return Value.MIN_DECIMAL;
        }
        if (type.endsWith("Date")) {
            return new Date(1, 1, 1);
        }
        if (type.endsWith("DateTime")) {
            return new DateTime(null,1, 1, 1, 0, 0, 0, 0);
        }
        if (type.endsWith("Time")) {
            return new Time(0, 0, 0, 0);
        }
        // NOTE: Quantity min is not standard
        if (type.endsWith("Quantity")) {
            return new Quantity().withValue(Value.MIN_DECIMAL).withUnit("1");
        }

        throw new InvalidOperatorArgument(String.format("The Minimum operator is not implemented for type %s", type));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        QName valueType = context.fixupQName(this.getValueType());
        String type = valueType.getLocalPart();
        return minValue(type);
    }
}
