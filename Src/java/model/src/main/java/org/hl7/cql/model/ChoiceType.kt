package org.hl7.cql.model

import org.hl7.cql.model.DataType.Companion.ANY

@ConsistentCopyVisibility
/** Created by Bryn on 11/8/2016. */
data class ChoiceType private constructor(val types: Set<DataType>) : BaseDataType() {
    constructor(types: Iterable<DataType>) : this(types.flattenChoices())

    constructor(vararg types: DataType) : this(types.toList().flattenChoices())

    init {
        require(types.isNotEmpty()) { "A choice type must have at least one type." }
        require(types.none { it is ChoiceType }) {
            "A choice type cannot contain another choice type."
        }
    }

    fun isSubSetOf(other: ChoiceType): Boolean {
        for (type in types) {
            var currentIsSubType = false
            for (otherType in other.types) {
                currentIsSubType = type.isSubTypeOf(otherType)
                if (currentIsSubType) {
                    break
                }
            }

            if (!currentIsSubType) {
                return false
            }
        }

        return true
    }

    fun isSuperSetOf(other: ChoiceType): Boolean {
        return other.isSubSetOf(this)
    }

    override fun isCompatibleWith(other: DataType): Boolean {
        // This type is compatible with the other type if
        // The other type is a subtype of one of the choice types
        // The other type is a choice type and all the components of this choice are a subtype of
        // some component of the
        // other type
        return when {
            other is ChoiceType -> this.isSubSetOf(other) || this.isSuperSetOf(other)
            types.any { other.isCompatibleWith(it) } -> true
            else -> super.isCompatibleWith(other)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("choice<")
        var first = true
        for (type in types) {
            if (first) {
                first = false
            } else {
                sb.append(",")
            }
            sb.append(type.toString())
        }
        sb.append(">")
        return sb.toString()
    }

    override val isGeneric: Boolean = types.any { it.isGeneric }

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        // Call isInstantiable recursively to make sure that type parameters (if present) are bound
        if (callType == ANY) {
            return types.any { !it.isInstantiable(callType, context) }
        }
        return isSuperTypeOf(callType)
    }

    override fun instantiate(context: InstantiationContext): DataType {
        return this
    }

    companion object {
        private fun Iterable<DataType>.flattenChoices(): Set<DataType> =
            flatMap { (it as? ChoiceType)?.types ?: setOf(it) }.toSet()
    }
}
