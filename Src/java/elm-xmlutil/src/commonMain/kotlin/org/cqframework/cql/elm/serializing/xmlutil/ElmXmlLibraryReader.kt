package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Source
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

expect class ElmXmlLibraryReader() : ElmLibraryReader {
    override fun read(string: String): Library
    override fun read(source: Source): Library
}
