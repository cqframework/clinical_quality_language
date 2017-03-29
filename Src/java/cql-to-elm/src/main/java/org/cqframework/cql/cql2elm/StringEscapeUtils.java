package org.cqframework.cql.cql2elm;

import org.apache.commons.lang3.text.translate.*;

/**
 * Created by Bryn on 3/22/2017.
 */
public class StringEscapeUtils {

    /**
     * Mapping to escape the CQL control characters.
     *
     * Namely: {@code \n \t \f \r}
     * @return the mapping table
     */
    public static String[][] CQL_CTRL_CHARS_ESCAPE() { return CQL_CTRL_CHARS_ESCAPE.clone(); }
    private static final String[][] CQL_CTRL_CHARS_ESCAPE = {
            {"\n", "\\n"},
            {"\t", "\\t"},
            {"\f", "\\f"},
            {"\r", "\\r"}
    };

    /**
     * Reverse of {@link #CQL_CTRL_CHARS_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static String[][] CQL_CTRL_CHARS_UNESCAPE() { return CQL_CTRL_CHARS_UNESCAPE.clone(); }
    private static final String[][] CQL_CTRL_CHARS_UNESCAPE = EntityArrays.invert(CQL_CTRL_CHARS_ESCAPE);

    public static final CharSequenceTranslator ESACPE_CQL =
            new LookupTranslator(
                new String[][] {
                    { "\"", "\\\"" },
                    { "\\", "\\\\" },
                    { "'", "\\'" }
                }).with(
                    new LookupTranslator(CQL_CTRL_CHARS_ESCAPE())
            ).with(
                JavaUnicodeEscaper.outsideOf(32, 0x7f)
            );

    public static final CharSequenceTranslator UNESCAPE_CQL =
            new AggregateTranslator(
                    new UnicodeUnescaper(),
                    new LookupTranslator(CQL_CTRL_CHARS_UNESCAPE()),
                    new LookupTranslator(
                            new String[][] {
                                    { "\\\\", "\\" },
                                    { "\\\"", "\"" },
                                    { "\\'", "\'" },
                                    { "\\/", "/" },
                                    { "\\", "" }
                            }
                    )
            );

    public static final String escapeCql(final String input) {
        return ESACPE_CQL.translate(input);
    }

    public static final String unescapeCql(final String input) {
        // CQL supports the following escape characters in both strings and identifiers:
        // \" - double-quote
        // \' - single-quote
        // \\ - backslash
        // \/ - slash
        // \f - form feed
        // \n - newline
        // \r - carriage return
        // \t - tab
        // \\u - unicode hex representation (e.g. \u0020)
        return UNESCAPE_CQL.translate(input);
    }

}
