package org.hl7.cql.model

import java.util.*

data class ClassTypeElement(
    val name: String,
    val type: DataType,
    val prohibited: Boolean = false,
    val oneBased: Boolean = false,
    val target: String? = null
) {

    init {
        require(name.isNotEmpty()) { "name can not be empty" }
    }

    fun isSubTypeOf(that: ClassTypeElement): Boolean =
        this.name == that.name && type.isSubTypeOf(that.type)

    fun isSuperTypeOf(that: ClassTypeElement): Boolean =
        this.name == that.name && type.isSuperTypeOf(that.type)

    override fun toString(): String {
        return "$name:$type$%s%s%s"
            .format(
                if (this.prohibited) " (prohibited)" else "",
                if (this.oneBased) " (one-based)" else "",
                if (this.target != null) " (target: " + this.target + ")" else ""
            )
    }
}
