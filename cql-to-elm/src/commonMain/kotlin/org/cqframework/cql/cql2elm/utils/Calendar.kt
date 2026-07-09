package org.cqframework.cql.cql2elm.utils

internal fun isLeapYear(year: Int): Boolean {
    @Suppress("MagicNumber")
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
