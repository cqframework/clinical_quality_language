package org.cqframework.cql.elm.serializing

import kotlinx.io.Source
import org.hl7.elm.r1.Library

expect class ElmXmlLibraryReader() : ElmLibraryReader {
    override fun read(string: String): Library

    override fun read(source: Source): Library
}
