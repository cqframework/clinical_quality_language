@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import org.apache.commons.text.translate.*

/** Created by Bryn on 3/22/2017. */
object StringEscapeUtils {
    /**
     * Mapping to escape the CQL control characters.
     *
     * Namely: `\n \t \f \r`
     *
     * @return the mapping table
     */
    @Suppress("FunctionNaming")
    fun CQL_CTRL_CHARS_ESCAPE(): Map<CharSequence, CharSequence> {
        return HashMap(CQL_CTRL_CHARS_ESCAPE)
    }

    private val CQL_CTRL_CHARS_ESCAPE: Map<CharSequence, CharSequence> =
        object : HashMap<CharSequence, CharSequence>() {
            init {
                put("\n", "\\n")
                put("\t", "\\t")
                put("\u000c", "\\f")
                put("\r", "\\r")
            }
        }

    /**
     * Reverse of [.CQL_CTRL_CHARS_ESCAPE] for unescaping purposes.
     *
     * @return the mapping table
     */
    @Suppress("FunctionNaming")
    fun CQL_CTRL_CHARS_UNESCAPE(): Map<CharSequence, CharSequence> {
        return HashMap(CQL_CTRL_CHARS_UNESCAPE)
    }

    private val CQL_CTRL_CHARS_UNESCAPE: Map<CharSequence, CharSequence> =
        object : HashMap<CharSequence, CharSequence>() {
            init {
                put("\\n", "\n")
                put("\\t", "\t")
                put("\\f", "\u000c")
                put("\\r", "\r")
            }
        }
    @Suppress("MagicNumber")
    val ESCAPE_CQL: CharSequenceTranslator =
        LookupTranslator(
                object : HashMap<CharSequence?, CharSequence?>() {
                    init {
                        put("\"", "\\\"")
                        put("\\", "\\\\")
                        put("'", "\\'")
                    }
                }
            )
            .with(LookupTranslator(CQL_CTRL_CHARS_ESCAPE()))
            .with(JavaUnicodeEscaper.outsideOf(32, 0x7f))
    val UNESCAPE_CQL: CharSequenceTranslator =
        AggregateTranslator(
            UnicodeUnescaper(),
            LookupTranslator(CQL_CTRL_CHARS_UNESCAPE()),
            LookupTranslator(
                object : HashMap<CharSequence?, CharSequence?>() {
                    init {
                        put("\\\\", "\\")
                        put("\\\"", "\"")
                        put("\\'", "\'")
                        put("\\`", "`")
                        put("\\/", "/")
                        put("\\", "")
                    }
                }
            )
        )

    fun escapeCql(input: String?): String {
        return ESCAPE_CQL.translate(input)
    }

    fun unescapeCql(input: String?): String? {
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
        return UNESCAPE_CQL.translate(input)
    }
}
