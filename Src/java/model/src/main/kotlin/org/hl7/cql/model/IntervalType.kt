package org.hl7.cql.model

import org.hl7.cql.model.DataType.Companion.ANY

data class IntervalType(val pointType: DataType) : BaseDataType() {
    override fun isSubTypeOf(other: DataType): Boolean {
        return if (other is IntervalType) {
            return pointType.isSubTypeOf(other.pointType)
        } else super.isSubTypeOf(other)
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return if (other is IntervalType) {
            return pointType.isSuperTypeOf(other.pointType)
        } else super.isSuperTypeOf(other)
    }

    override fun toString(): String = "interval<$pointType>"

    override fun toLabel(): String = "Interval of ${pointType.toLabel()}"

    override val isGeneric: Boolean = pointType.isGeneric

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        return when (callType) {
            ANY -> pointType.isInstantiable(callType, context)
            is IntervalType -> pointType.isInstantiable(callType.pointType, context)
            else -> {
                val instantiableElements =
                    context.getIntervalConversionTargets(callType).filter {
                        pointType.isInstantiable(it.pointType, context)
                    }
                check(instantiableElements.size <= 1) {
                    "Ambiguous generic instantiation involving $callType to $instantiableElements"
                }

                instantiableElements.isNotEmpty()
            }
        }
    }

    override fun instantiate(context: InstantiationContext): DataType {
        return IntervalType(pointType.instantiate(context))
    }
}
