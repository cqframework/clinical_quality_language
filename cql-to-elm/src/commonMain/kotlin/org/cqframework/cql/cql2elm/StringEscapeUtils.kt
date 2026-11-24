package org.cqframework.cql.cql2elm

import kotlin.jvm.JvmStatic

/** Retained for API compatibility; delegates to [org.cqframework.cql.shared.StringEscapeUtils]. */
object StringEscapeUtils {
    @JvmStatic
    fun escapeCql(input: String): String =
        org.cqframework.cql.shared.StringEscapeUtils.escapeCql(input)

    @JvmStatic
    fun unescapeCql(input: String): String =
        org.cqframework.cql.shared.StringEscapeUtils.unescapeCql(input)
}
