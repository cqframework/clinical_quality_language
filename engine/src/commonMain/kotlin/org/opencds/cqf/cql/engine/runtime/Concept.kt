package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
class Concept
@JvmOverloads
constructor(val display: kotlin.String? = null, val codes: kotlin.collections.List<Code?>? = null) :
    StructuredValue, NamedTypeValue {
    override val type = conceptTypeName

    // no display?
    override val elements = mapOf("codes" to codes?.toCqlList())

    fun withDisplay(display: kotlin.String?): Concept {
        return Concept(display, this.codes)
    }

    @Suppress("NON_EXPORTABLE_TYPE")
    fun withCodes(codes: Iterable<Code?>?): Concept {
        return Concept(display, codes?.toList())
    }

    fun withCode(code: Code?): Concept {
        return Concept(display, (codes ?: listOf()) + code)
    }

    // Computed on first use; 0 means not yet computed. Not a lazy delegate, which would retain
    // a lambda until first use.
    private var hash = 0

    override fun hashCode(): Int {
        var result = hash
        if (result == 0) {
            result = listOf(display, codes).hashCode()
            hash = result
        }
        return result
    }

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Concept && display == other.display && codes == other.codes)

    override fun toString(): kotlin.String = toStructuredValueString()
}
