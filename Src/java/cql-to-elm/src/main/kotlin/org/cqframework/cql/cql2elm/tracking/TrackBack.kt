package org.cqframework.cql.cql2elm.tracking

import java.util.*
import org.hl7.elm.r1.VersionedIdentifier

data class TrackBack(
    val library: VersionedIdentifier?,
    val startLine: Int,
    val startChar: Int,
    val endLine: Int,
    val endChar: Int
) {

    override fun toString(): String {
        return ("TrackBack{" +
            "library='" +
            library +
            '\'' +
            ", startLine=" +
            startLine +
            ", startChar=" +
            startChar +
            ", endLine=" +
            endLine +
            ", endChar=" +
            endChar +
            '}')
    }

    fun toLocator(): String {
        return if (startLine == endLine && startChar == endChar)
            String.format(Locale.US, "%s:%s", startLine, startChar)
        else String.format(Locale.US, "%s:%s-%s:%s", startLine, startChar, endLine, endChar)
    }
}
