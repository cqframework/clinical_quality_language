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

    @Suppress("ForbiddenComment", "DestructuringDeclarationWithTooManyEntries")
    // TODO: Remove hashCode and equals. Everything works without these methods but the compiled ELM
    // is different because [org.cqframework.cql.cql2elm.LibraryBuilder.normalizeListTypes] returns
    // the choice options in a different order.
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is ClassTypeElement) {
            return false
        }
        val (name1, type1, prohibited1, oneBased1, target1) = o
        if (target != null && target != target1) {
            return false
        }
        if (oneBased != oneBased1) {
            return false
        }
        if (prohibited != prohibited1) {
            return false
        }
        if (name != name1) {
            return false
        }
        return if (type != type1) {
            false
        } else true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + if (prohibited) 1 else 0
        result = 31 * result + if (oneBased) 1 else 0
        if (target != null) {
            result = 31 * result + target.hashCode()
        }
        return result
    }
}
