package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.Quantity
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

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
    fun internalEvaluate(elm: Quantity, state: State?): Any? {
        val value = Value.verifyPrecision(elm.value!!, null)
        return org.opencds.cqf.cql.engine.runtime.Quantity().withValue(value).withUnit(elm.unit!!)
    }
}
