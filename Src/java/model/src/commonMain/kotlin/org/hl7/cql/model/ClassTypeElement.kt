package org.hl7.cql.model

data class ClassTypeElement(
    val name: String,
    val type: DataType,
    val prohibited: Boolean = false,
    val oneBased: Boolean = false,
    val target: String? = null
) {

    // For Java compatibility. Can be deleted once tests are updated.
    constructor(name: String, type: DataType) : this(name, type, false, false, null)

    init {
        require(name.isNotEmpty()) { "name can not be empty" }
    }

    fun isSubTypeOf(that: ClassTypeElement): Boolean =
        this.name == that.name && type.isSubTypeOf(that.type)

    fun isSuperTypeOf(that: ClassTypeElement): Boolean =
        this.name == that.name && type.isSuperTypeOf(that.type)

    override fun toString(): String {
        return """$name:$type
            |${if (this.prohibited) " (prohibited)" else ""}
            |${if (this.oneBased) " (one-based)" else ""}
            |${if (this.target != null) " (target: " + this.target + ")" else ""}"""
            .trimMargin()
            .replace("\n", "")
    }
}
