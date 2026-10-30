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

    private val hash: Int by lazy { listOf(display, codes).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Concept && display == other.display && codes == other.codes)

    override fun toString(): kotlin.String = toStructuredValueString()
}
