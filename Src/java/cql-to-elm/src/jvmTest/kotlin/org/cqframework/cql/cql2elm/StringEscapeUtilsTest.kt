package org.cqframework.cql.cql2elm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength")
internal class StringEscapeUtilsTest {
    @Test
    fun escape() {
        Assertions.assertEquals("Hello \\'World\\'", StringEscapeUtils.escapeCql("Hello 'World'"))
        Assertions.assertEquals(
            "Hello \\\"World\\\"",
            StringEscapeUtils.escapeCql("Hello \"World\"")
        )
        Assertions.assertEquals("Hello \\`World\\`", StringEscapeUtils.escapeCql("Hello `World`"))
        Assertions.assertEquals("Hello \\'World\\'2", StringEscapeUtils.escapeCql("Hello 'World'2"))
        Assertions.assertEquals(
            "Hello \\\"World\\\"2",
            StringEscapeUtils.escapeCql("Hello \"World\"2")
        )
        Assertions.assertEquals(
            "\\f\\n\\r\\t\\/\\\\",
            StringEscapeUtils.escapeCql("\u000c\n\r\t/\\")
        )
        Assertions.assertEquals(
            "\\u110f",
            StringEscapeUtils.escapeCql("ᄏ")
        ) // unprintable character
        Assertions.assertEquals(
            "This is an identifier with \\\"multiple\\\" embedded \\t escapes\u0020\\r\\nno really, \\r\\n\\f\\t\\/\\\\lots of them",
            StringEscapeUtils.escapeCql(
                "This is an identifier with \"multiple\" embedded \t escapes\u0020\r\nno really, \r\n\u000c\t/\\lots of them"
            )
        )
    }
}
