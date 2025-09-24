package org.cqframework.cql.elm.serializing

import kotlinx.io.Source
import org.hl7.elm.r1.Library

interface ElmLibraryReader {
    fun read(string: String): Library

    fun read(source: Source): Library
}
