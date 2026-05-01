package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class Concept : StructuredValue(), NamedTypeValue {
    override val type = conceptTypeName

    override val elements: MutableMap<kotlin.String, Value?>
        get() = mutableMapOf("codes" to codes?.toCqlList())

    var display: kotlin.String? = null

    fun withDisplay(display: kotlin.String?): Concept {
        this.display = display
        return this
    }

    var codes: MutableList<Code?>? = mutableListOf<Code?>()
        set(codes) {
            field!!.clear()
            if (codes != null) {
                for (code in codes) {
                    field!!.add(code)
                }
            }
        }

    @Suppress("NON_EXPORTABLE_TYPE")
    fun withCodes(codes: Iterable<Code?>?): Concept {
        this.codes = codes?.toMutableList() ?: mutableListOf()
        return this
    }

    fun withCode(code: Code?): Concept {
        codes!!.add(code!!)
        return this
    }

    override fun toString(): kotlin.String {
        return toPrettyString("Concept")
    }
}
