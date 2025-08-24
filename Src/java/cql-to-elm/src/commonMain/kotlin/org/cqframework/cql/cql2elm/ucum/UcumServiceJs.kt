package org.cqframework.cql.cql2elm.ucum

import org.cqframework.cql.shared.BigDecimal

/**
 * A UCUM service factory suitable for JS environments.
 *
 * @param validateUnit a callback for validating a UCUM unit. If the unit is valid, it should return
 *   null, otherwise it should return an error message.
 * @return an instance of [UcumService]
 */
internal fun createUcumService(validateUnit: (unit: String) -> String? = { null }): UcumService {
    return object : UcumService {
        override fun convert(value: BigDecimal, sourceUnit: String, destUnit: String): BigDecimal {
            // We don't expect `convert` to be called during translation
            error("Unexpected call to convert")
        }

        override fun validate(unit: String): String? {
            return validateUnit(unit)
        }
    }
}
