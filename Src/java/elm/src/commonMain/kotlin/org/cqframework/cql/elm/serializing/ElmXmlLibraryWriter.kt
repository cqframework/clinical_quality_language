package org.cqframework.cql.elm.serializing

import kotlinx.io.Sink
import kotlinx.io.writeString
import org.cqframework.cql.shared.QName
import org.cqframework.cql.shared.serializing.toXmlString
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.toXmlElement

class ElmXmlLibraryWriter : ElmLibraryWriter {
    override fun write(library: Library, sink: Sink) {
        sink.writeString(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {
        val namespaces = mutableMapOf<String, String>()
        val element =
            library.toXmlElement(
                QName("urn:hl7-org:elm:r1", "library"),
                false,
                namespaces,
                mapOf(
                    "" to "urn:hl7-org:elm:r1",
                    "t" to "urn:hl7-org:elm-types:r1",
                    "xsi" to "http://www.w3.org/2001/XMLSchema-instance",
                    "xsd" to "http://www.w3.org/2001/XMLSchema",
                    "fhir" to "http://hl7.org/fhir",
                    "qdm43" to "urn:healthit-gov:qdm:v4_3",
                    "qdm53" to "urn:healthit-gov:qdm:v5_3",
                    "a" to "urn:hl7-org:cql-annotations:r1",
                )
            )
        return toXmlString(element, namespaces)
    }
}
