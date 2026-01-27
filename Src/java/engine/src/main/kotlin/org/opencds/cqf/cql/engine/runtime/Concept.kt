package org.opencds.cqf.cql.engine.runtime

class Concept : CqlType {
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
        val builder = StringBuilder().append("Concept {\n")
        for (code in codes!!) {
            builder.append("\t").append(code.toString()).append("\n")
        }

        return builder.append("}").toString()
    }
}
