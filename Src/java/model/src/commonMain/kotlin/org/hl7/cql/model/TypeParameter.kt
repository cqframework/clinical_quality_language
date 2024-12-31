package org.hl7.cql.model

data class TypeParameter(
    val identifier: String,
    val constraint: TypeParameterConstraint = TypeParameterConstraint.NONE,
    val constraintType: DataType? = null
) : BaseDataType() {

    // For Java compatibility. Can be deleted once tests are updated.
    constructor(identifier: String) : this(identifier, TypeParameterConstraint.NONE, null)

    init {
        require(identifier.isNotEmpty()) { "identifier can not be empty" }
        if (constraint == TypeParameterConstraint.TYPE) {
            require(constraintType != null) {
                "constraintType must be provided when constraint is TYPE"
            }
        }
    }

    enum class TypeParameterConstraint {
        /** Indicates the type parameter has no constraint and be bound to any type */
        NONE,

        /** Indicates the type parameter can only be bound to class types */
        CLASS,

        /** Indicates the type parameter can only be bound to value types (simple types) */
        VALUE,

        /** Indicates the type parameter can only be bound to tuple types */
        TUPLE,

        /** Indicates the type parameter can only be bound to interval types */
        INTERVAL,

        /** Indicates the type parameter can only be bound to choice types */
        CHOICE,

        /**
         * Indicates the type parameter can only be bound to the constraint type or a type derived
         * from the constraint type
         */
        TYPE
    }

    /**
     * @param callType
     * @return True if the given callType can be bound to this parameter (i.e. it satisfied any
     *   constraints defined for the type parameter)
     */
    fun canBind(callType: DataType): Boolean {
        return when (constraint) {
            TypeParameterConstraint.CHOICE -> callType is ChoiceType
            TypeParameterConstraint.TUPLE -> callType is TupleType
            TypeParameterConstraint.INTERVAL -> callType is IntervalType
            TypeParameterConstraint.CLASS -> callType is ClassType
            TypeParameterConstraint.VALUE -> callType is SimpleType && callType != DataType.ANY
            TypeParameterConstraint.TYPE -> callType.isSubTypeOf(constraintType!!)
            TypeParameterConstraint.NONE -> true
        }
    }

    override fun toString(): String = identifier

    override val isGeneric: Boolean = true

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean =
        context.isInstantiable(this, callType)

    override fun instantiate(context: InstantiationContext): DataType = context.instantiate(this)
}
