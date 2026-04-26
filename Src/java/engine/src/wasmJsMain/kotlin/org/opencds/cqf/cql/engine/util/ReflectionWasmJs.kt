package org.opencds.cqf.cql.engine.util

actual val Any.javaClassName: String
    get() = JavaClassJs(this::class).getTypeName()
