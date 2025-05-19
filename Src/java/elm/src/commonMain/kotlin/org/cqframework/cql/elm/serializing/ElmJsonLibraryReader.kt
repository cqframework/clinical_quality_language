package org.cqframework.cql.elm.serializing

import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.fromJsonObject

class ElmJsonLibraryReader : ElmLibraryReader {
    override fun read(string: String): Library {
        return Library.fromJsonObject(
            parseToJsonElement(string).jsonObject.get("library")!!.jsonObject
        )
    }

    override fun read(source: Source): Library {
        return read(source.readString())
    }
}
