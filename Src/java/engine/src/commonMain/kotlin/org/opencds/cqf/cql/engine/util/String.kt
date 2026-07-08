package org.opencds.cqf.cql.engine.util

fun Int.toPaddedString(length: Int): String {
    return this.toString().padStart(length, '0')
}
