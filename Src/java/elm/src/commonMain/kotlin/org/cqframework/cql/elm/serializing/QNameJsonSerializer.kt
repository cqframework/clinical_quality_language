package org.cqframework.cql.elm.serializing

import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import nl.adaptivity.xmlutil.*


@OptIn(ExperimentalXmlUtilApi::class)
object QNameJsonSerializer : XmlSerializer<QName> by QNameSerializer {
    @OptIn(XmlUtilInternal::class)
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("javax.xml.namespace.QName", PrimitiveKind.STRING).xml(
            PrimitiveSerialDescriptor("javax.xml.namespace.QName", PrimitiveKind.STRING),
            QName(XMLConstants.XSD_NS_URI, "QName", XMLConstants.XSD_PREFIX)
        )

    override fun serialize(encoder: Encoder, value: QName) {
        encoder.encodeString(
            value.toString()
        )
    }

    override fun deserialize(decoder: Decoder): QName {
        // The string can be formatted as "{namespaceURI}localPart" or just "localPart"
        val str = decoder.decodeString()

        if (str.startsWith("{")) {
            val parts = str.substring(1).split("}")
            return QName(parts[0], parts[1])
        }

        return QName(str)
    }
}

