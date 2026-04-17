@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package org.opencds.cqf.cql.engine.util

fun isIterable(obj: Any?) = obj is Iterable<*>

@Suppress("NON_EXPORTABLE_TYPE") fun iterableToList(iterable: Iterable<*>) = iterable.toList()
