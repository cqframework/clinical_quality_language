package org.cqframework.cql.cql2elm.utils

internal fun isDigit(c: Char): Boolean = c in '0'..'9'

internal fun isLetter(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z'

internal fun isLetterOrDigit(c: Char): Boolean = isLetter(c) || isDigit(c)
