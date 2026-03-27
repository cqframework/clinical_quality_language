package org.opencds.cqf.cql.engine.util

actual val Any.javaClassName: String
    get() = this::class.javaObjectType.name
