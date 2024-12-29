package org.cqframework.cql.elm.serializing.xmlutil

import java.io.Writer
import kotlinx.serialization.modules.plus
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.serialization.XML
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

class ElmXmlLibraryWriter : ElmLibraryWriter {
    override fun write(library: Library, writer: Writer) {
        writer.write(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {
        val serializersModule =
            org.hl7.elm.r1.Serializer.createSerializer() +
                org.hl7.cql_annotations.r1.Serializer.createSerializer()

        val xml =
            XML(serializersModule) {
                xmlDeclMode = nl.adaptivity.xmlutil.XmlDeclMode.Charset
                defaultPolicy {
                    typeDiscriminatorName =
                        QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi")
                }
            }

        return xml.encodeToString(Library.serializer(), library)
    }
}
