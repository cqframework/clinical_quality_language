package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.javaClassName

/*

GeometricMean(argument List<Decimal>) Decimal

The GeometricMean operator returns the geometric mean of the non-null elements in the source.
    Geometric mean is defined as the Nth root of the geometric product of the elements.
        In other words:
            GeometricMean(X) = Power(Product(X), 1 / Count(X))

If the source contains no non-null elements, the result is null.

If the source is null, the result is null.

*/
object GeometricMeanEvaluator {
    fun geometricMean(source: Iterable<*>?, state: State?): BigDecimal? {
        if (source == null) {
            return null
        }

        // remove nulls - operation is on non-null list elements ... TODO: generify and move this to
        // a utility class
        val cleanSource: MutableList<BigDecimal?> = ArrayList<BigDecimal?>()
        for (element in source) {
            if (element != null) {
                if (element is BigDecimal) {
                    cleanSource.add(element)
                } else {
                    throw InvalidOperatorArgument(
                        "GeometricMean(List<Decimal>)",
                        "GeometricMean(${element.javaClassName})",
                    )
                }
            }
        }
        return PowerEvaluator.power(
            ProductEvaluator.product(cleanSource, state),
            DivideEvaluator.divide(
                BigDecimal(1),
                ToDecimalEvaluator.toDecimal(CountEvaluator.count(cleanSource)),
                state,
            ),
        ) as BigDecimal?
    }
}
