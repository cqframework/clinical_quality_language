package org.opencds.cqf.cql.engine.runtime

class Ratio : StructuredValue(), NamedCqlType {
    override val type = ratioTypeName

    override val elements: MutableMap<kotlin.String, CqlType?>
        get() = mutableMapOf("numerator" to numerator, "denominator" to denominator)

    var numerator: Quantity? = null

    var denominator: Quantity? = null

    fun withNumerator(numerator: Quantity?): Ratio {
        this.numerator = numerator
        return this
    }

    fun withDenominator(denominator: Quantity?): Ratio {
        this.denominator = denominator
        return this
    }

    override fun toString(): kotlin.String {
        return "${this.numerator.toString()}:${this.denominator.toString()}"
    }
}
