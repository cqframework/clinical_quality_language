package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
structured type Quantity
{
  value Decimal
  unit String
}

The Quantity type represents quantities with a specified unit within CQL.
*/
object QuantityEvaluator {
    @JvmStatic
    fun internalEvaluate(elm: org.hl7.elm.r1.Quantity, state: State?): Quantity {
        val value = DecimalHelper.verifyPrecision(elm.value!!, null)
        return Quantity().withValue(value).withUnit(elm.unit!!)
    }
}
