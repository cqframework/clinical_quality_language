package org.opencds.cqf.cql.engine.util

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import org.apache.commons.lang3.StringUtils

class StringUtilsSplitTest {
    /**
     * [#1870](https://github.com/cqframework/clinical_quality_language/issues/1870): The JVM used
     * Apache Commons Lang `StringUtils.split` before `stringUtilsSplit` was implemented for all
     * platforms, so it must keep returning the same results.
     */
    @Test
    fun matchesApacheCommonsStringUtilsSplit() {
        val characters = "ab,;/ é"
        val random = Random(1870)
        fun randomString(maxLength: Int) = buildString {
            repeat(random.nextInt(maxLength + 1)) {
                append(characters[random.nextInt(characters.length)])
            }
        }

        repeat(10_000) {
            val string = randomString(12)
            val separator = randomString(3).ifEmpty { "," }
            assertContentEquals(
                StringUtils.split(string, separator),
                stringUtilsSplit(string, separator),
                "string: \"$string\", separator: \"$separator\"",
            )
        }
    }
}
