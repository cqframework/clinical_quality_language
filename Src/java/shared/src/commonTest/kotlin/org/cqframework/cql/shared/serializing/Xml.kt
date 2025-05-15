@file:Suppress("MatchingDeclarationName")

package org.cqframework.cql.shared.serializing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.cqframework.cql.shared.QName

class XmlTest {
    @Test
    fun qNameSerializerHandlesExistingNamespacePrefix() {
        assertEquals(
            "a:local",
            qNameToXmlAttributeValue(
                QName("urn:example:one", "local"),
                mutableMapOf("a" to "urn:example:one"),
                mapOf()
            )
        )
    }

    @Test
    fun qNameSerializerUsesDefaultAvailablePrefix() {
        val namespaces = mutableMapOf<String, String>()
        val result =
            qNameToXmlAttributeValue(
                QName("urn:hl7-org:cql-annotations:r1", "CqlToElmInfo"),
                namespaces,
                mapOf("a" to "urn:hl7-org:cql-annotations:r1")
            )
        assertEquals("a:CqlToElmInfo", result)
        assertEquals("urn:hl7-org:cql-annotations:r1", namespaces["a"])
    }

    @Test
    fun qNameSerializerAssignsNewPrefix() {
        val namespaces = mutableMapOf<String, String>()
        val result =
            qNameToXmlAttributeValue(QName("urn:example:one", "local"), namespaces, mapOf())
        assertEquals("ns0:local", result)
        assertEquals("urn:example:one", namespaces["ns0"])
    }

    @Test
    fun qNameSerializerHandlesEmptyNamespace() {
        assertEquals(
            "ns0:local",
            qNameToXmlAttributeValue(QName("", "local"), mutableMapOf(), mapOf())
        )
    }

    @Test
    fun qNameParserReadsXmlAttribute() {
        assertEquals(
            QName("urn:hl7-org:cql-annotations:r1", "CqlToElmInfo"),
            xmlAttributeValueToQName(
                "a:CqlToElmInfo",
                mapOf("a" to "urn:hl7-org:cql-annotations:r1")
            )
        )
    }

    @Test
    fun qNameParserHandlesDefaultNamespace() {
        assertEquals(
            QName("urn:example:one", "local"),
            xmlAttributeValueToQName("local", mapOf("" to "urn:example:one"))
        )
    }

    @Test
    fun qNameParserThrowsWhenNamespacePrefixIsNotFound() {
        assertFailsWith<IllegalArgumentException> { xmlAttributeValueToQName("a:local", mapOf()) }
        assertFailsWith<IllegalArgumentException> { xmlAttributeValueToQName("local", mapOf()) }
    }

    @Test
    fun qNameParserThrowsWhenFormatIsInvalid() {
        assertFailsWith<IllegalArgumentException> {
            xmlAttributeValueToQName("a:b:c", mapOf("a" to "urn:example:one"))
        }
    }
}
