package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

actual fun kotlinClassToJavaClassName(kClass: KClass<*>): String {
    return kClass.javaObjectType.name
}

actual val Any.javaClassName: String
    get() = this::class.javaObjectType.name

actual val Any.javaClassPackageName: String
    get() = this::class.javaObjectType.`package`.name

actual val KClass<*>.javaPackageName: String
    get() = this.javaObjectType.`package`.name

actual typealias JavaClass<T> = Class<T>

actual val Any.javaClass: Class<*>
    get() = this::class.javaObjectType

actual fun isIterable(clazz: KClass<*>): Boolean {
    return Iterable::class.java.isAssignableFrom(clazz.java)
}

actual fun KClass<*>.isAssignableFrom(clazz: KClass<*>): Boolean {
    return this.javaObjectType.isAssignableFrom(clazz.java)
}
