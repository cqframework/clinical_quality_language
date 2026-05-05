package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass
import kotlin.reflect.cast
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet

const val unknownPackageName = "unknown"
const val unknownSimpleName = "Unknown"

/**
 * Implements expected methods for [JavaClass] for non-Java environments. Only supports engine core
 * types.
 */
data class JavaClassJs<T : Any>(private val kClass: KClass<T>) {
    fun getTypeName(): String {
        return "${getPackageName()}.${getName()}"
    }

    fun getName(): String {
        return when (kClass) {
            Int::class -> "Integer"
            Long::class -> "Long"
            BigDecimal::class -> "BigDecimal"
            else -> kClass.simpleName ?: unknownSimpleName
        }
    }

    fun getPackageName(): String {
        return when (kClass) {
            Boolean::class,
            Int::class,
            Long::class,
            BigDecimal::class,
            String::class -> "java.lang"
            Date::class,
            DateTime::class,
            Time::class,
            Quantity::class,
            Ratio::class,
            Code::class,
            Concept::class,
            CodeSystem::class,
            ValueSet::class,
            Interval::class,
            Tuple::class -> "org.opencds.cqf.cql.engine.runtime"
            else -> unknownPackageName
        }
    }

    fun isInstance(value: Any?): Boolean {
        return kClass.isInstance(value)
    }

    fun cast(value: Any?): T {
        return kClass.cast(value)
    }

    fun isAssignableFrom(clazz: JavaClassJs<*>): Boolean {
        return kClass == clazz.kClass
    }
}
