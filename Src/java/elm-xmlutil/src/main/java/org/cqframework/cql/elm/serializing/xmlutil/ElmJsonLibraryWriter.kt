package org.cqframework.cql.elm.serializing.xmlutil

import java.io.Writer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

class ElmJsonLibraryWriter : ElmLibraryWriter {
    override fun write(library: Library, writer: Writer) {
        writer.write(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {
        val module =
            org.hl7.elm.r1.Serializer.createSerializer() +
                org.hl7.cql_annotations.r1.Serializer.createSerializer()
        val json = Json {
            serializersModule = module
            explicitNulls = false
        }

        return json.encodeToString(LibraryWrapper.serializer(), LibraryWrapper(library))
    }
}
