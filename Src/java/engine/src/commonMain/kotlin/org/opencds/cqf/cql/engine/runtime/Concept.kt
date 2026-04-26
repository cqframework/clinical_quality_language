package org.opencds.cqf.cql.engine.runtime

class Concept : StructuredValue(), NamedCqlType {
    override val type = conceptTypeName

    override val elements: MutableMap<String, Any?>
        get() = mutableMapOf("codes" to codes)

    var display: String? = null

    fun withDisplay(display: String?): Concept {
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

    override fun toString(): String {
        return toPrettyString("Concept")
    }
}
