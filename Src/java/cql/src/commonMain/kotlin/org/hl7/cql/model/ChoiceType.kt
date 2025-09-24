package org.hl7.cql.model

import org.hl7.cql.model.DataType.Companion.ANY

@ConsistentCopyVisibility
/** Created by Bryn on 11/8/2016. */
data class ChoiceType
private constructor(
    @Suppress("ForbiddenComment")
    // TODO: Change type to Set<DataType> to deduplicate choice options. E.g. in QICore model info,
    // Observation.effective has System.DateTime duplicated. This doesn't break anything but the
    // compiled ELM is different because there are fewer alternative conversions in
    // [org.cqframework.cql.cql2elm.LibraryBuilder.convertExpression].
    val types: List<DataType>
) : BaseDataType() {
    constructor(types: Iterable<DataType>) : this(types.flattenChoices())

    constructor(vararg types: DataType) : this(types.toList().flattenChoices())

    init {
        require(types.isNotEmpty()) { "A choice type must have at least one type." }
        require(types.none { it is ChoiceType }) {
            "A choice type cannot contain another choice type."
        }
    }

    // every type in this choice is a subtype of some type in the other choice
    fun isSubSetOf(other: ChoiceType): Boolean =
        types.all { x -> other.types.any { x.isSubTypeOf(it) } }

    fun isSuperSetOf(other: ChoiceType): Boolean = other.isSubSetOf(this)

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

    override fun toString(): String = types.joinToString(",", "choice<", ">")

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

    @Suppress("ForbiddenComment")
    // TODO: Remove hashCode and equals. Everything works without these methods but the compiled ELM
    // is different because [org.cqframework.cql.cql2elm.LibraryBuilder.normalizeListTypes] returns
    // the choice options in a different order.
    override fun hashCode(): Int {
        var result = 13
        for (i in types.indices) {
            result += 37 * types[i].hashCode()
        }
        return result
    }

    @Suppress("NestedBlockDepth")
    override fun equals(other: Any?): Boolean {
        if (other is ChoiceType) {
            val (thoseTypes) = other
            if (types.size == thoseTypes.size) {
                val theseTypes = types
                for (i in theseTypes.indices) {
                    if (theseTypes[i] != thoseTypes[i]) {
                        return false
                    }
                }
                return true
            }
        }
        return false
    }

    companion object {
        // The Iterable is flattened recursively
        private fun Iterable<DataType>.flattenChoices(): List<DataType> =
            flatMap { (it as? ChoiceType)?.types?.flattenChoices() ?: listOf(it) }.toList()
    }
}
