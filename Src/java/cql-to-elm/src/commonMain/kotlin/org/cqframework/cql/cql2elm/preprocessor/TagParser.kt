package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.cql2elm.utils.isDigit
import org.cqframework.cql.cql2elm.utils.isLetter
import org.cqframework.cql.cql2elm.utils.isLetterOrDigit
import org.hl7.cql_annotations.r1.ObjectFactory
import org.hl7.cql_annotations.r1.Tag

/**
 * Parses CQL tag annotations (e.g. `@parameter: "Measurement Interval"`) out of leading-comment
 * headers attached to CQL definitions.
 *
 * Extracted from `CqlPreprocessorElmCommonVisitor` so the tag-parsing logic can be maintained
 * independently of the visitor base class.
 */
@Suppress("ReturnCount")
class TagParser {
    private val af = ObjectFactory()

    /** Parse all tags out of a header comment block. */
    fun parseTags(header: String?): List<Tag> {
        if (header == null) return emptyList()
        val normalized =
            header
                .trim { it <= ' ' }
                .split("\n[ \t]*\\*[ \t\\*]*".toRegex())
                .dropLastWhile { it.isEmpty() }
                .joinToString("\n")
        val tags = ArrayList<Tag>()
        var startFrom = 0
        while (startFrom < normalized.length) {
            val tagNamePair = lookForTagName(normalized, startFrom) ?: break
            if (tagNamePair.left.isNotEmpty() && isValidIdentifier(tagNamePair.left)) {
                var t = af.createTag().withName(tagNamePair.left)
                startFrom = tagNamePair.right
                val tagValuePair = lookForTagValue(normalized, startFrom)
                if (tagValuePair != null && tagValuePair.left.isNotEmpty()) {
                    t = t.withValue(tagValuePair.left)
                    startFrom = tagValuePair.right
                }
                tags.add(t)
            } else {
                startFrom = tagNamePair.right
            }
        }
        return tags
    }

    /** Strip comment markers from a header block, keeping only the comment body text. */
    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    fun parseComments(header: String?): String {
        if (header == null) return ""
        val result = ArrayList<String>()
        val normalized = header.replace("\r\n", "\n")
        val lines = normalized.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var inMultiline = false
        for (line in lines) {
            if (!inMultiline) {
                var start = line.indexOf("/*")
                if (start >= 0) {
                    if (line.endsWith("*/")) {
                        result.add(line.substring(start + 2, line.length - 2))
                    } else {
                        result.add(line.substring(start + 2))
                    }
                    inMultiline = true
                } else start = line.indexOf("//")
                if (start >= 0 && !inMultiline) {
                    result.add(line.substring(start + 2))
                }
            } else {
                val end = line.indexOf("*/")
                if (end >= 0) {
                    inMultiline = false
                    if (end > 0) {
                        result.add(line.substring(0, end))
                    }
                } else {
                    result.add(line)
                }
            }
        }
        return result.joinToString("\n")
    }

    data class Pair<L, R>(val left: L, val right: R) {
        companion object {
            fun <L, R> of(left: L, right: R): Pair<L, R> = Pair(left, right)
        }
    }

    private fun lookForTagName(header: String, startFrom: Int): Pair<String, Int>? {
        if (startFrom >= header.length) return null
        val start = header.indexOf("@", startFrom)
        if (start < 0) return null
        val nextTagStart = header.indexOf("@", start + 1)
        val nextColon = header.indexOf(":", start + 1)
        if (nextTagStart < 0) {
            if (nextColon < 0) {
                return Pair.of(
                    header.substring(start + 1, header.length).trim { it <= ' ' },
                    header.length,
                )
            }
        } else {
            if (nextColon < 0 || nextColon > nextTagStart) {
                return Pair.of(
                    header.substring(start + 1, nextTagStart).trim { it <= ' ' },
                    nextTagStart,
                )
            }
        }
        return Pair.of(header.substring(start + 1, nextColon).trim { it <= ' ' }, nextColon + 1)
    }

    companion object {
        @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount", "ComplexCondition")
        fun lookForTagValue(header: String, startFrom: Int): Pair<String, Int>? {
            if (startFrom >= header.length) return null
            val nextTag = header.indexOf('@', startFrom)
            val nextStartDoubleQuote = header.indexOf("\"", startFrom)
            if (
                (nextTag < 0 || nextTag > nextStartDoubleQuote) &&
                    nextStartDoubleQuote > 0 &&
                    header.length > nextStartDoubleQuote + 1
            ) {
                val nextEndDoubleQuote = header.indexOf("\"", nextStartDoubleQuote + 1)
                return if (nextEndDoubleQuote > 0) {
                    val parameterEnd = header.indexOf("\n", nextStartDoubleQuote + 1)
                    if (parameterEnd < 0) {
                        Pair.of(header.substring(nextStartDoubleQuote), header.length)
                    } else {
                        Pair.of(header.substring(nextStartDoubleQuote, parameterEnd), parameterEnd)
                    }
                } else {
                    Pair.of(header.substring(nextStartDoubleQuote), header.length)
                }
            }
            if (nextTag == startFrom && !isStartingWithDigit(header, nextTag + 1)) {
                return Pair.of("", startFrom)
            } else if (nextTag > 0) {
                val interimText = header.substring(startFrom, nextTag).trim { it <= ' ' }
                return if (isStartingWithDigit(header, nextTag + 1)) {
                    if (interimText.isNotEmpty() && interimText != ":") {
                        Pair.of(interimText, nextTag)
                    } else {
                        val nextSpace = header.indexOf(' ', nextTag)
                        val nextLine = header.indexOf("\n", nextTag)
                        val mul = nextSpace * nextLine
                        var nextDelimiterIndex = header.length
                        if (mul < 0) {
                            nextDelimiterIndex = nextLine.coerceAtLeast(nextSpace)
                        } else if (mul > 1) {
                            nextDelimiterIndex = nextLine.coerceAtMost(nextSpace)
                        }
                        Pair.of(header.substring(nextTag, nextDelimiterIndex), nextDelimiterIndex)
                    }
                } else {
                    Pair.of(interimText, nextTag)
                }
            }
            return Pair.of(header.substring(startFrom).trim { it <= ' ' }, header.length)
        }

        fun isValidIdentifier(tagName: String): Boolean {
            for (i in tagName.indices) {
                if (tagName[i] == '_') continue
                if (i == 0) {
                    if (!isLetter(tagName[i])) return false
                } else {
                    if (!isLetterOrDigit(tagName[i])) return false
                }
            }
            return true
        }

        fun isStartingWithDigit(header: String, index: Int): Boolean =
            index < header.length && isDigit(header[index])
    }
}
