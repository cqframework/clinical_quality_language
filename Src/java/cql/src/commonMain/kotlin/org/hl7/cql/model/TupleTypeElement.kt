package org.hl7.cql.model

data class TupleTypeElement(
    val name: String,
    val type: DataType,
    private val oneBased: Boolean = false,
) {
    init {
        require(name.isNotEmpty()) { "name can not be empty" }
    }

    fun isSubTypeOf(that: TupleTypeElement): Boolean =
        this.name == that.name && type.isSubTypeOf(that.type)

    fun isSuperTypeOf(that: TupleTypeElement): Boolean =
        this.name == that.name && type.isSuperTypeOf(that.type)

    override fun toString(): String = "$name:$type"

    fun toLabel(): String = "$name: $type"

    @Suppress("ForbiddenComment")
    // TODO: Remove hashCode and equals. Everything works without these methods but the compiled ELM
    // is different because [org.cqframework.cql.cql2elm.LibraryBuilder.normalizeListTypes] returns
    // the choice options in a different order.
    override fun hashCode(): Int {
        return 17 * name.hashCode() + 33 * type.hashCode() + 31 * if (oneBased) 1 else 0
    }

    override fun equals(other: Any?): Boolean {
        if (other is TupleTypeElement) {
            val (name1, type1, oneBased1) = other
            return name == name1 && type == type1 && oneBased == oneBased1
        }
        return false
    }
}
