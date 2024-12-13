package org.cqframework.cql.cql2elm;

import static org.cqframework.cql.cql2elm.StringEscapeUtils.escapeCql;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringEscapeUtilsTest {

    @Test
    void escape() {
        assertEquals("Hello \\'World\\'", escapeCql("Hello 'World'"));
        assertEquals("Hello \\\"World\\\"", escapeCql("Hello \"World\""));
        assertEquals("Hello \\`World\\`", escapeCql("Hello `World`"));
        assertEquals("Hello \\'World\\'2", escapeCql("Hello 'World'2"));
        assertEquals("Hello \\\"World\\\"2", escapeCql("Hello \"World\"2"));
        assertEquals("\\f\\n\\r\\t\\/\\\\", escapeCql("\f\n\r\t/\\"));
        assertEquals("\\u110f", escapeCql("ᄏ")); // unprintable character
        assertEquals(
                "This is an identifier with \\\"multiple\\\" embedded \\t escapes\u0020\\r\\nno really, \\r\\n\\f\\t\\/\\\\lots of them",
                escapeCql(
                        "This is an identifier with \"multiple\" embedded \t escapes\u0020\r\nno really, \r\n\f\t/\\lots of them"));
    }
}
