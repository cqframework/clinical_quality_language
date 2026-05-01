package org.opencds.cqf.cql.engine.util

@OptIn(ExperimentalJsExport::class)
@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
fun <T> iterableToList(iterable: Iterable<T>) = iterable.toList()
