package org.cqframework.cql.cql2elm;


import org.apache.commons.text.translate.*;

import java.util.HashMap;
import java.util.Map;

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
    public static Map<CharSequence, CharSequence> CQL_CTRL_CHARS_ESCAPE() {
        return new HashMap<CharSequence, CharSequence>(CQL_CTRL_CHARS_ESCAPE);
    }
    private static final Map<CharSequence, CharSequence> CQL_CTRL_CHARS_ESCAPE = new HashMap<CharSequence, CharSequence>() {{
        put("\n", "\\n");
        put("\t", "\\t");
        put("\f", "\\f");
        put("\r", "\\r");
    }};

    /**
     * Reverse of {@link #CQL_CTRL_CHARS_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static Map<CharSequence, CharSequence> CQL_CTRL_CHARS_UNESCAPE() {
        return new HashMap<CharSequence, CharSequence>(CQL_CTRL_CHARS_UNESCAPE);
    }
    private static final Map<CharSequence, CharSequence> CQL_CTRL_CHARS_UNESCAPE = new HashMap<CharSequence, CharSequence>() {{
        put("\\n", "\n");
        put("\\t", "\t");
        put("\\f", "\f");
        put("\\r", "\r");
    }};

    public static final CharSequenceTranslator ESACPE_CQL =
            new LookupTranslator(
                    new HashMap<CharSequence, CharSequence>() {{
                        put( "\"", "\\\"" );
                        put( "\\", "\\\\" );
                        put( "'", "\\'" );
                    }}
                ).with(
                    new LookupTranslator(CQL_CTRL_CHARS_ESCAPE())
            ).with(
                JavaUnicodeEscaper.outsideOf(32, 0x7f)
            );

    public static final CharSequenceTranslator UNESCAPE_CQL =
            new AggregateTranslator(
                    new UnicodeUnescaper(),
                    new LookupTranslator(CQL_CTRL_CHARS_UNESCAPE()),
                    new LookupTranslator(
                            new HashMap<CharSequence, CharSequence>() {{
                                put( "\\\\", "\\" );
                                put( "\\\"", "\"" );
                                put( "\\'", "\'");
                                put( "\\`", "`");
                                put( "\\/", "/");
                                put( "\\", "");
                            }}
                    )
            );

    public static final String escapeCql(final String input) {
        return ESACPE_CQL.translate(input);
    }

    public static final String unescapeCql(final String input) {
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
        return UNESCAPE_CQL.translate(input);
    }

}
