package org.cqframework.cql.cql2elm

/** Created by Bryn on 3/22/2017. */
object StringEscapeUtils {

    // CQL supports the following escape characters in both strings and identifiers:
    // \" - double-quote
    // \' - single-quote
    // \` - backtick
    // \\ - backslash
    // \/ - slash
    // \f - form feed
    // \n - newline
    // \r - carriage return
    // \t - tab
    // \\u - unicode hex representation (e.g. \u0020)
    private val UNESCAPE_MAP: Map<CharSequence, Char> =
        mapOf(
            "\\\"" to '\"',
            "\\'" to '\'',
            "\\`" to '`',
            "\\\\" to '\\',
            "\\/" to '/',
            "\\f" to '\u000c',
            "\\n" to '\n',
            "\\r" to '\r',
            "\\t" to '\t'
            // unicode escapes are handled separately
        )

    private val ESCAPE_MAP: Map<Char, CharSequence> =
        UNESCAPE_MAP.entries.associate { it.value to it.key }

    // Longer escape sequences should be matched first to avoid partial matches
    private val MULTI_CHAR_UNESCAPE = UNESCAPE_MAP.keys.sortedByDescending { it.length }
    private val UNESCAPE_REGEX =
        Regex(
            MULTI_CHAR_UNESCAPE.joinToString("|") { Regex.escape(it.toString()) } +
                // Unicode escape sequence
                "|\\\\u[0-9a-fA-F]{4}"
        )

    @JvmStatic
    fun escapeCql(input: String): String {
        return buildString {
            for (char in input) {
                append(
                    // Use the mapped escape sequence or
                    // default to Unicode for non-printable characters
                    // '\u0020'..'\u007E' are printable ASCII characters
                    ESCAPE_MAP[char]
                        ?: if (char.isISOControl() || char !in '\u0020'..'\u007E') {
                            "\\u%04x".format(char.code)
                        } else {
                            char
                        }
                )
            }
        }
    }

    private const val HEX_RADIX = 16

    @JvmStatic
    fun unescapeCql(input: String): String {
        return UNESCAPE_REGEX.replace(input) { matchResult ->
            val match = matchResult.value
            when {
                // Handle standard escape sequences
                match in UNESCAPE_MAP ->
                    UNESCAPE_MAP[match]?.toString()
                        ?: throw IllegalArgumentException("Invalid escape sequence: $match")

                // Handle Unicode escapes
                match.startsWith("\\u") -> {
                    val hex = match.substring(2)
                    hex.toInt(HEX_RADIX).toChar().toString()
                }
                else -> throw IllegalArgumentException("Invalid escape sequence: $match")
            }
        }
    }
}
