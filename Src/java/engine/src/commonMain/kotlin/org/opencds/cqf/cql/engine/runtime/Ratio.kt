package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
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
