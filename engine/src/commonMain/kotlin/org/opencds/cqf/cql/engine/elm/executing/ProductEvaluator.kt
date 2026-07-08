package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*

Product(argument List<Integer>) Integer
Product(argument List<Long>) Long
Product(argument List<Decimal>) Decimal
Product(argument List<Quantity>) Quantity

The Product operator returns the geometric product of the elements in source.

If the source contains no non-null elements, null is returned.

If the source is null, the result is null.

*/
@Suppress("CyclomaticComplexMethod", "ReturnCount")
object ProductEvaluator {
    @JvmStatic
    fun product(source: Value?, state: State?): Value? {
        if (source == null) {
            return null
        }

        if (source is List) {
            var result: Value? = null
            for (element in source) {
                if (element == null) return null
                if (result == null) {
                    result = element
                    continue
                }
                if (
                    (element is Integer && result is Integer) ||
                        (element is Long && result is Long) ||
                        (element is Decimal && result is Decimal)
                ) {
                    result = MultiplyEvaluator.multiply(result, element, state)
                } else if (element is Quantity && result is Quantity) {
                    require(element.unit == result.unit) {
                        "Found different units during Quantity product evaluation: ${element.unit} and ${result.unit}"
                    }
                    result.value =
                        (MultiplyEvaluator.multiply(
                                result.value?.toCqlDecimal(),
                                element.value?.toCqlDecimal(),
                                state,
                            ) as Decimal)
                            .value
                } else {
                    throw InvalidOperatorArgument(
                        "Product(List<Integer>), Product(List<Long>), Product(List<Decimal>) or Product(List<Quantity>)",
                        "Product(List<${element.typeAsString}>)",
                    )
                }
            }

            return result
        }

        throw InvalidOperatorArgument(
            "Product(List<Integer>), Product(List<Long>), Product(List<Decimal>) or Product(List<Quantity>)",
            "Product(${source.typeAsString})",
        )
    }
}
