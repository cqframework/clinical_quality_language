package org.cqframework.cql.elm.serializing

import kotlinx.io.Source
import kotlinx.io.readString
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.fromXmlElement
import org.hl7.elm_modelinfo.r1.serializing.parseXml

class ElmXmlLibraryReader : ElmLibraryReader {
    override fun read(string: String): Library {
        val tree = parseXml(string)

        return Library.fromXmlElement(tree, emptyMap())
    }

    override fun read(source: Source): Library {
        return read(source.readString())
    }
}
