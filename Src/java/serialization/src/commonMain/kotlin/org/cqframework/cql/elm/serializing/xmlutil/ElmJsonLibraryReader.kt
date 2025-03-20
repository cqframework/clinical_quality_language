package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.readString
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

class ElmJsonLibraryReader : ElmLibraryReader {
    override fun read(string: String): Library {
        return json.decodeFromString(LibraryWrapper.serializer(), string).library
    }

    override fun read(source: Source): Library {
        return read(source.readString())
    }
}
