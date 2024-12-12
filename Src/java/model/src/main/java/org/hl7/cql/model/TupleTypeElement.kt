package org.hl7.cql.model

data class TupleTypeElement(
    val name: String,
    val type: DataType,
    private val oneBased: Boolean = false
) {
    init {
        require(name.isNotEmpty()) { "name is required" }
    }

    fun isSubTypeOf(that: TupleTypeElement): Boolean =
        this.name == that.name && type.isSubTypeOf(that.type)

    fun isSuperTypeOf(that: TupleTypeElement): Boolean =
        this.name == that.name && type.isSuperTypeOf(that.type)

    override fun toString(): String = "${name}:${this.type}"

    fun toLabel(): String = "${name}: ${this.type}"
}
