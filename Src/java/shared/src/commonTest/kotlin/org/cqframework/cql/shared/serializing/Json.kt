@file:Suppress("MatchingDeclarationName")

package org.cqframework.cql.shared.serializing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.cqframework.cql.shared.QName

class JsonTest {
    @Test
    fun qNameParserHandlesValueWithCurlyBraces() {
        assertEquals(
            QName("urn:hl7-org:elm-types:r1", "Integer"),
            jsonStringToQName("{urn:hl7-org:elm-types:r1}Integer")
        )
        assertEquals(QName("Integer"), jsonStringToQName("{}Integer"))
        assertEquals(
            QName("urn:hl7-org:elm-types:r1", ""),
            jsonStringToQName("{urn:hl7-org:elm-types:r1}")
        )
    }

    @Test
    fun qNameParserHandlesValueWithoutCurlyBraces() {
        assertEquals(QName("Integer"), jsonStringToQName("Integer"))
        assertEquals(QName(""), jsonStringToQName(""))
    }

    @Test
    fun qNameParserThrowsWhenClosingBraceIsMissing() {
        assertFailsWith<IllegalArgumentException> {
            jsonStringToQName("{urn:hl7-org:elm-types:r1Integer")
        }
    }
}
