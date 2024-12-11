package org.hl7.cql.model

import java.util.*

data class TupleTypeElement(
    val name: String,
    val type: DataType,
    private val oneBased: Boolean = false
) {
    init {
        require(name.isNotEmpty()) { "name is required" }
    }

    fun isSubTypeOf(that: TupleTypeElement): Boolean {
        return this.name == that.name && type.isSubTypeOf(that.type)
    }

    fun isSuperTypeOf(that: TupleTypeElement): Boolean {
        return this.name == that.name && type.isSuperTypeOf(that.type)
    }

    override fun toString(): String {
        return String.format(Locale.US, "%s:%s", this.name, type.toString())
    }

    fun toLabel(): String {
        return String.format(Locale.US, "%s: %s", this.name, type.toLabel())
    }
}
