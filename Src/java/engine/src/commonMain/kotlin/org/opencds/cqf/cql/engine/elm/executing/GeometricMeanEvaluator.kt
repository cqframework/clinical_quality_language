package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlList

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
    fun geometricMean(source: Value?, state: State?): Value? {
        if (source == null) {
            return null
        }

        if (source is List && source.all { it is Decimal? }) {
            val cleanSource = source.filterIsInstance<Decimal>().toCqlList()
            return PowerEvaluator.power(
                ProductEvaluator.product(cleanSource, state),
                DivideEvaluator.divide(
                    BigDecimal(1).toCqlDecimal(),
                    ToDecimalEvaluator.toDecimal(CountEvaluator.count(cleanSource)),
                    state,
                ),
            )
        }

        throw InvalidOperatorArgument(
            "GeometricMean(List<Decimal>)",
            "GeometricMean(${source.typeAsString})",
        )
    }
}
