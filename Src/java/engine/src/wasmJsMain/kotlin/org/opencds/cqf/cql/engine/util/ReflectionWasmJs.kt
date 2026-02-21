package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

actual val Any.javaClassName: String
    get() = TODO()

actual val Any.javaClassPackageName: String
    get() = TODO()

actual class JavaClass<T> {
    actual fun getTypeName(): String {
        TODO()
    }

    actual fun getName(): String {
        TODO()
    }

    actual fun getPackageName(): String {
        TODO()
    }

    actual fun isInstance(value: Any?): Boolean {
        TODO()
    }

    actual fun cast(value: Any?): T {
        TODO()
    }

    actual fun isAssignableFrom(clazz: JavaClass<*>): Boolean {
        TODO()
    }
}

actual val Any.javaClass: JavaClass<*>
    get() = TODO()

actual fun kotlinClassToJavaClass(kClass: KClass<*>): JavaClass<*> {
    TODO()
}
