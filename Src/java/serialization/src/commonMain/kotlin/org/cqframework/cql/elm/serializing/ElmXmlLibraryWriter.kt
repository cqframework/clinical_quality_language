package org.cqframework.cql.elm.serializing

import kotlinx.io.Sink
import kotlinx.io.writeString
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.toXmlElement
import org.hl7.elm_modelinfo.r1.serializing.toXmlString

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
                    "xsi" to "http://www.w3.org/2001/XMLSchema-instance",
                    "a" to "urn:hl7-org:cql-annotations:r1",
                    "t" to "urn:hl7-org:elm-types:r1",
                    "fhir" to "http://hl7.org/fhir",
                )
            )
        return toXmlString(element, namespaces)
    }
}
