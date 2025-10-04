package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent

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

    override fun equivalent(other: Any?): Boolean? {
        if (this.codes == null || (other as Concept).codes == null) {
            return false
        }

        for (code in this.codes) {
            for (otherCode in other.codes!!) {
                if (equivalent(code, otherCode) == true) {
                    return true
                }
            }
        }

        return false
    }

    override fun equal(other: Any?): Boolean? {
        val codesAreEqual = equal(this.codes, (other as Concept).codes)
        var displayIsEqual = equal(this.display, other.display)
        if (displayIsEqual == null && this.display == null && other.display == null) {
            displayIsEqual = true
        }
        return if (codesAreEqual == null || displayIsEqual == null) null
        else codesAreEqual && displayIsEqual
    }

    override fun toString(): String {
        val builder = StringBuilder().append("Concept {\n")
        for (code in codes!!) {
            builder.append("\t").append(code.toString()).append("\n")
        }

        return builder.append("}").toString()
    }
}
