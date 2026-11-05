package org.cqframework.cql.cql2elm.ucum

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

    fun multiply(
        left: Pair<BigDecimal, String>,
        right: Pair<BigDecimal, String>,
    ): Pair<BigDecimal, String>

    fun divideBy(
        left: Pair<BigDecimal, String>,
        right: Pair<BigDecimal, String>,
    ): Pair<BigDecimal, String>

    /**
     * Returns true if [unit] is a UCUM "arbitrary" unit, or a unit expression involving one, such
     * as `[IU]` (international unit) or `[IU]/mL`. Arbitrary units are defined by their measurement
     * procedure (assay); UCUM declares them non-commensurable with any other unit, so operations on
     * them are constrained (see the CQL specification's "Arbitrary Units" guidance).
     *
     * The underlying `org.fhir:ucum` library does not surface the `isArbitrary` flag from
     * ucum-essence.xml, so detection uses the fixed set of arbitrary unit atoms defined by UCUM,
     * matched case-sensitively. Prefixes (`k[IU]`), exponents, and position within the expression
     * are handled, while annotations (`mg{[IU]}`) and non-arbitrary bracketed units (`[in_i]`) are
     * not matched. Platform implementations backed by a service that exposes the flag may override
     * this.
     */
    fun isArbitrary(unit: String): Boolean {
        val withoutAnnotations = unit.replace(ucumAnnotationRegex, "")
        return ucumUnitAtomRegex.findAll(withoutAnnotations).any { it.value in arbitraryUnitAtoms }
    }
}

private val ucumAnnotationRegex = Regex("\\{[^}]*\\}")
private val ucumUnitAtomRegex = Regex("\\[[^\\]]*\\]")

/**
 * The UCUM arbitrary unit atoms (units flagged `isArbitrary="yes"` in ucum-essence.xml). Matched
 * case-sensitively, since UCUM (and the CQL spec) treat e.g. `[IU]` and `[iU]` as distinct units.
 */
private val arbitraryUnitAtoms: Set<String> =
    setOf(
        "[hp_X]",
        "[hp_C]",
        "[hp_M]",
        "[hp_Q]",
        "[kp_X]",
        "[kp_C]",
        "[kp_M]",
        "[kp_Q]",
        "[iU]",
        "[IU]",
        "[arb'U]",
        "[USP'U]",
        "[GPL'U]",
        "[MPL'U]",
        "[APL'U]",
        "[beth'U]",
        "[anti'Xa'U]",
        "[todd'U]",
        "[dye'U]",
        "[smgy'U]",
        "[bdsk'U]",
        "[ka'U]",
        "[knk'U]",
        "[mclg'U]",
        "[tb'U]",
        "[CCID_50]",
        "[TCID_50]",
        "[EID_50]",
        "[PFU]",
        "[FFU]",
        "[CFU]",
        "[BAU]",
        "[AU]",
        "[Amb'a'1'U]",
        "[PNU]",
        "[Lf]",
        "[D'ag'U]",
        "[FEU]",
        "[ELU]",
        "[EU]",
    )

expect val defaultLazyUcumService: Lazy<UcumService>

/**
 * Creates a UCUM service from the provided callbacks.
 *
 * @param convertUnit a callback for converting a quantity from one UCUM unit to another.
 * @param validateUnit a callback for validating a UCUM unit. If the unit is valid, it should return
 *   null, otherwise it should return an error message.
 * @return a lazy UCUM service
 */
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
fun createUcumService(
    convertUnit: (value: String, sourceUnit: String, destUnit: String) -> String,
    validateUnit: (unit: String) -> String?,
    multiply: (Pair<BigDecimal, String>, Pair<BigDecimal, String>) -> Pair<BigDecimal, String>,
    divideBy: (Pair<BigDecimal, String>, Pair<BigDecimal, String>) -> Pair<BigDecimal, String>,
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

            override fun multiply(
                left: Pair<BigDecimal, String>,
                right: Pair<BigDecimal, String>,
            ): Pair<BigDecimal, String> {
                return multiply(left, right)
            }

            override fun divideBy(
                left: Pair<BigDecimal, String>,
                right: Pair<BigDecimal, String>,
            ): Pair<BigDecimal, String> {
                return divideBy(left, right)
            }
        }
    }
}
