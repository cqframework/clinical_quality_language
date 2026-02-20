package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

actual fun kotlinClassToJavaClassName(kClass: KClass<*>): String {
    TODO()
}

actual val Any.javaClassName: String
    get() = TODO()

actual val Any.javaClassPackageName: String
    get() = TODO()

actual val KClass<*>.javaPackageName: String
    get() = TODO()

actual class JavaClass<T> {
    actual fun getTypeName(): String {
        TODO()
    }
}

actual val Any.javaClass: JavaClass<*>
    get() = TODO()

actual fun isIterable(clazz: KClass<*>): Boolean {
    TODO()
}

actual fun KClass<*>.isAssignableFrom(clazz: KClass<*>): Boolean {
    TODO()
}
