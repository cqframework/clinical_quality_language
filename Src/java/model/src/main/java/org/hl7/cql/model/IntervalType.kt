package org.hl7.cql.model

import java.util.*
import org.hl7.cql.model.DataType.Companion.ANY

data class IntervalType(val pointType: DataType) : BaseDataType() {
    override fun isSubTypeOf(other: DataType): Boolean {
        return if (other is IntervalType) {
            return pointType.isSubTypeOf(other.pointType)
        } else {
            super.isSubTypeOf(other)
        }
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return if (other is IntervalType) {
            return pointType.isSuperTypeOf(other.pointType)
        } else {
            super.isSuperTypeOf(other)
        }
    }

    override fun toString(): String {
        return String.format(Locale.US, "interval<%s>", pointType.toString())
    }

    override fun toLabel(): String {
        return String.format(Locale.US, "Interval of %s", pointType.toLabel())
    }

    override val isGeneric: Boolean
        get() = pointType.isGeneric

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        return when (callType) {
            ANY -> pointType.isInstantiable(callType, context)
            is IntervalType -> pointType.isInstantiable(callType.pointType, context)
            else -> {
                val instantiableElements =
                    context.getIntervalConversionTargets(callType).filter {
                        pointType.isInstantiable(it.pointType, context)
                    }
                require(instantiableElements.size <= 1) {
                    String.format(
                        Locale.US,
                        "Ambiguous generic instantiation involving %s to %s.",
                        callType.toString(),
                        instantiableElements.toString()
                    )
                }

                instantiableElements.isNotEmpty()
            }
        }
    }

    override fun instantiate(context: InstantiationContext): DataType {
        return IntervalType(pointType.instantiate(context))
    }
}
