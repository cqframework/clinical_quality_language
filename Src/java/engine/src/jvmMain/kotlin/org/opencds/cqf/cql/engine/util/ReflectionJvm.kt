package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

actual val Any.javaClassName: String
    get() = this::class.javaObjectType.name

actual val Any.javaClassPackageName: String
    get() = this::class.javaObjectType.`package`.name

actual typealias JavaClass<T> = Class<T>

actual val Any.javaClass: Class<*>
    get() = this::class.javaObjectType

actual fun kotlinClassToJavaClass(kClass: KClass<*>): Class<*> {
    return kClass.javaObjectType
}
