package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

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
    fun product(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            var result: Any? = null
            for (element in source) {
                if (element == null) return null
                if (result == null) {
                    result = element
                    continue
                }
                if (
                    (element is Int && result is Int) ||
                        (element is Long && result is Long) ||
                        (element is BigDecimal && result is BigDecimal)
                ) {
                    result = MultiplyEvaluator.multiply(result, element, state)
                } else if (element is Quantity && result is Quantity) {
                    require(element.unit == result.unit) {
                        "Found different units during Quantity product evaluation: ${element.unit} and ${result.unit}"
                    }
                    result.value =
                        MultiplyEvaluator.multiply(result.value, element.value, state) as BigDecimal
                } else {
                    throw InvalidOperatorArgument(
                        "Product(List<Integer>), Product(List<Long>), Product(List<Decimal>) or Product(List<Quantity>)",
                        "Product(List<${element.javaClassName}>)",
                    )
                }
            }

            return result
        }

        throw InvalidOperatorArgument(
            "Product(List<Integer>), Product(List<Long>), Product(List<Decimal>) or Product(List<Quantity>)",
            "Product(${source.javaClassName})",
        )
    }
}
