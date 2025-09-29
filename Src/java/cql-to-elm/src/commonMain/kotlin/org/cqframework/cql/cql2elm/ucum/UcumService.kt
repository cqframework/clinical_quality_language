package org.cqframework.cql.cql2elm.ucum

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.JsOnlyExport

interface UcumService {
    /**
     * Converts a quantity from one unit to another
     *
     * @param value the quantity to convert
     * @param sourceUnit the unit of the quantity
     * @param destUnit the unit to convert to
     * @return the converted value in terms of the destination unit
     */
    fun convert(value: BigDecimal, sourceUnit: String, destUnit: String): BigDecimal

    /**
     * Validate checks that a string is valid ucum unit
     *
     * @param unit
     * @return null if valid, error message if invalid
     */
    fun validate(unit: String): String?
}

expect val defaultLazyUcumService: Lazy<UcumService>

/**
 * Creates a UCUM service from the provided callbacks.
 *
 * @param convertUnit a callback for converting a quantity from one UCUM unit to another.
 * @param validateUnit a callback for validating a UCUM unit. If the unit is valid, it should return
 *   null, otherwise it should return an error message.
 * @return a lazy UCUM service
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
fun createUcumService(
    convertUnit: (value: String, sourceUnit: String, destUnit: String) -> String,
    validateUnit: (unit: String) -> String?,
): Lazy<UcumService> {
    return lazy {
        object : UcumService {
            override fun convert(
                value: BigDecimal,
                sourceUnit: String,
                destUnit: String,
            ): BigDecimal {
                val result = convertUnit(value.toPlainString(), sourceUnit, destUnit)
                return BigDecimal(result)
            }

            override fun validate(unit: String): String? {
                return validateUnit(unit)
            }
        }
    }
}
