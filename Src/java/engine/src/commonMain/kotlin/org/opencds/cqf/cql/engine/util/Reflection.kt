package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

/**
 * Used in error messages. Note: Returns `java.lang.Integer` for integer values (including
 * non-nullable integers).
 */
expect val Any.javaClassName: String

expect val Any.javaClassPackageName: String

expect class JavaClass<T> {
    fun getTypeName(): String

    fun getName(): String

    fun getPackageName(): String

    fun isInstance(value: Any?): Boolean

    fun cast(value: Any?): T

    fun isAssignableFrom(clazz: JavaClass<*>): Boolean
}

expect val Any.javaClass: JavaClass<*>

expect fun kotlinClassToJavaClass(kClass: KClass<*>): JavaClass<*>
