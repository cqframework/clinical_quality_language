package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
class Ratio
@JvmOverloads
constructor(val numerator: Quantity? = null, val denominator: Quantity? = null) :
    StructuredValue, NamedTypeValue {
    override val type = ratioTypeName

    override val elements = mapOf("numerator" to numerator, "denominator" to denominator)

    fun withNumerator(numerator: Quantity?): Ratio {
        return Ratio(numerator, denominator)
    }

    fun withDenominator(denominator: Quantity?): Ratio {
        return Ratio(numerator, denominator)
    }

    private val hash: Int by lazy { listOf(numerator, denominator).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is Ratio && numerator == other.numerator && denominator == other.denominator)

    override fun toString(): kotlin.String {
        return "${this.numerator}:${this.denominator}"
    }
}
