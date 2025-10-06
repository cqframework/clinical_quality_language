package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.elm.executing.MultiplyEvaluator.multiply

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

    /**
     * For ratios, equivalent means that the numerator and denominator represent the same ratio
     * (e.g. 1:100 ~ 10:1000).
     */
    override fun equivalent(other: Any?): Boolean? {
        val otherRatio = other as Ratio
        return equivalent(
            multiply(this.numerator, otherRatio.denominator),
            multiply(otherRatio.numerator, this.denominator),
        )
    }

    override fun equal(other: Any?): Boolean? {
        return equal(this.numerator, (other as Ratio).numerator) == true &&
            equal(this.denominator, other.denominator) == true
    }

    override fun toString(): String {
        return "${this.numerator.toString()}:${this.denominator.toString()}"
    }
}
