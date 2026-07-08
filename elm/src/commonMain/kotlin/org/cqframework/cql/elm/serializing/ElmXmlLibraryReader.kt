package org.cqframework.cql.elm.serializing

import kotlinx.io.Source
import kotlinx.io.readString
import org.cqframework.cql.shared.serializing.parseXml
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.fromXmlElement

class ElmXmlLibraryReader : ElmLibraryReader {
    override fun read(string: String): Library {
        val tree = parseXml(string)

        return Library.fromXmlElement(tree, emptyMap())
    }

    override fun read(source: Source): Library {
        return read(source.readString())
    }
}
