package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Quantity;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Value;

import java.math.BigDecimal;

/*
structured type Quantity
{
  value Decimal
  unit String
}

The Quantity type represents quantities with a specified unit within CQL.
*/

public class QuantityEvaluator {
    public static Object internalEvaluate(Quantity elm, State state) {
        BigDecimal value = Value.verifyPrecision(elm.getValue(), null);
        return new org.opencds.cqf.cql.engine.runtime.Quantity().withValue(value).withUnit(elm.getUnit());
    }
}
