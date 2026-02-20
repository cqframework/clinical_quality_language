package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

/** Used in error messages. */
expect fun kotlinClassToJavaClassName(kClass: KClass<*>): String

/**
 * Used in error messages. Note: Returns `java.lang.Integer` for integer values (including
 * non-nullable integers).
 */
expect val Any.javaClassName: String

/** Used when resolving data providers. */
expect val Any.javaClassPackageName: String

/** Used when resolving data providers. */
expect val KClass<*>.javaPackageName: String

/** Used for point types in intervals. */
expect class JavaClass<T> {
    fun getTypeName(): String
}

/** Used for point types in intervals. */
expect val Any.javaClass: JavaClass<*>

expect fun isIterable(clazz: KClass<*>): Boolean

expect fun KClass<*>.isAssignableFrom(clazz: KClass<*>): Boolean
