package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
Avg(argument List<Decimal>) Decimal
Avg(argument List<Quantity>) Quantity

* The Avg operator returns the average of the non-null elements in the source.
* If the source contains no non-null elements, null is returned.
* If the source is null, the result is null.
* Returns values of type BigDecimal or Quantity
*/
object AvgEvaluator {
    @JvmStatic
    fun avg(source: CqlType?, state: State?): CqlType? {
        if (source == null) {
            return null
        }

        if (source is List) {
            var avg: CqlType? = null
            var size = 1

            for (element in source) {
                if (element == null) {
                    continue
                }

                if (element is Decimal || element is Quantity) {
                    if (avg == null) {
                        avg = element
                    } else {
                        ++size
                        avg = AddEvaluator.add(avg, element, state)
                    }
                } else {
                    throw InvalidOperatorArgument(
                        "Avg(List<Decimal>), Avg(List<Quantity>)",
                        "Avg(List<${source.typeAsString}>)",
                    )
                }
            }

            return DivideEvaluator.divide(avg, BigDecimal(size).toCqlDecimal(), state)
        }

        throw InvalidOperatorArgument(
            "Avg(List<Decimal>), Avg(List<Quantity>)",
            "Avg(${source.typeAsString})",
        )
    }
}
