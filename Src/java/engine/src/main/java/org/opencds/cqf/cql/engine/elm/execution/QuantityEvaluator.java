package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
structured type Quantity
{
  value Decimal
  unit String
}

The Quantity type represents quantities with a specified unit within CQL.
*/

public class QuantityEvaluator extends org.cqframework.cql.elm.execution.Quantity {

    @Override
    protected Object internalEvaluate(Context context) {
        BigDecimal value = Value.verifyPrecision(this.getValue(), null);
        return new org.opencds.cqf.cql.engine.runtime.Quantity().withValue(value).withUnit(this.getUnit());
    }
}
