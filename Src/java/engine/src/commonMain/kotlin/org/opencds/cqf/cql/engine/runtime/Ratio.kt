package org.opencds.cqf.cql.engine.runtime

class Ratio : CqlType {
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

    override fun toString(): String {
        return "${this.numerator.toString()}:${this.denominator.toString()}"
    }
}
