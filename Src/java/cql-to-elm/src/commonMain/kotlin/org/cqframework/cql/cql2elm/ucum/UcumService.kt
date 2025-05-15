package org.cqframework.cql.cql2elm.ucum

import org.cqframework.cql.shared.BigDecimal

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
