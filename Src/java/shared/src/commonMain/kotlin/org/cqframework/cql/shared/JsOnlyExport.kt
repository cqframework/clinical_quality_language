package org.cqframework.cql.shared

import kotlin.annotation.AnnotationTarget.*
import kotlin.js.ExperimentalJsExport

@OptIn(ExperimentalJsExport::class)
@Target(CLASS, PROPERTY, FUNCTION, FILE)
expect annotation class JsOnlyExport()
