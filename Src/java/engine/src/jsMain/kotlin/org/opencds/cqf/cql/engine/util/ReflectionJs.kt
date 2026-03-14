package org.opencds.cqf.cql.engine.util

import kotlin.reflect.KClass

actual val Any.javaClassName: String
    get() = kotlinClassToJavaClass(this::class).getTypeName()

actual val Any.javaClassPackageName: String
    get() = kotlinClassToJavaClass(this::class).getPackageName()

actual typealias JavaClass<T> = JavaClassJs<T>

actual val Any.javaClass: JavaClass<*>
    get() = kotlinClassToJavaClass(this::class)

actual fun kotlinClassToJavaClass(kClass: KClass<*>): JavaClass<*> {
    return JavaClass(kClass)
}
