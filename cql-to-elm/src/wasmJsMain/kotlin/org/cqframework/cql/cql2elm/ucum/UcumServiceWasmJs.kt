@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm.ucum

import org.cqframework.cql.shared.BigDecimal

actual val defaultLazyUcumService =
    lazy<UcumService> { error("No default UCUM service available.") }

@JsExport
@JsName("createUcumService")
fun createUcumServiceReference(
    convertUnit: (value: String, sourceUnit: String, destUnit: String) -> String,
    validateUnit: (unit: String) -> String?,
    multiply:
        (leftValue: String, leftUnit: String, rightValue: String, rightUnit: String) -> String,
    divideBy: (leftValue: String, leftUnit: String, rightValue: String, rightUnit: String) -> String,
): JsReference<Lazy<UcumService>> {
    return createUcumService(
            convertUnit,
            validateUnit,
            { left, right ->
                // The multiply function on the JS side has to encode the result as a string
                // "<value>:<unit>" since we can
                // pass and return only primitive values.
                val result =
                    multiply(
                        left.first.toString(),
                        left.second,
                        right.first.toString(),
                        right.second,
                    )
                val valueAndUnit = result.split(":", limit = 2)
                Pair(BigDecimal(valueAndUnit[0]), valueAndUnit[1])
            },
            { left, right ->
                val result =
                    multiply(
                        left.first.toString(),
                        left.second,
                        right.first.toString(),
                        right.second,
                    )
                val valueAndUnit = result.split(":", limit = 2)
                Pair(BigDecimal(valueAndUnit[0]), valueAndUnit[1])
            },
        )
        .toJsReference()
}
