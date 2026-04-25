package org.opencds.cqf.cql.engine.runtime

class Concept : StructuredValue(), NamedCqlType {
    override val type = conceptTypeName

    override val elements: MutableMap<kotlin.String, CqlType?>
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
