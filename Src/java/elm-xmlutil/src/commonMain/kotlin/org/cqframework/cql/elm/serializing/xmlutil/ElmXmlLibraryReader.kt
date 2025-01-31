@file:Suppress("detekt:all")

package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.readString
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

class ElmXmlLibraryReader : ElmLibraryReader {
    override fun read(string: String): Library {
        return xml.decodeFromString(Library.serializer(), string)
    }
    override fun read(source: Source): Library {
        return read(source.readString())
    }
}
