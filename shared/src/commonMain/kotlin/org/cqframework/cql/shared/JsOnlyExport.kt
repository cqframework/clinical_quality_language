package org.cqframework.cql.shared

import kotlin.annotation.AnnotationTarget.*

@Target(CLASS, PROPERTY, FUNCTION, FILE) expect annotation class JsOnlyExport()
