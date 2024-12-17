package org.cqframework.cql.elm.serializing.xmlutil

import java.io.IOException
import java.io.Writer
import kotlinx.serialization.modules.plus
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.serialization.XML
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

/**
 * Implementation of an ELM XML serializer using the Jackson serialization framework. This
 * implementation is known non-functional, but after 3 different devs fiddling with it for untold
 * frustrating hours, we are abandoning it for now as a use case we don't care about anyway
 */
class ElmXmlLibraryWriter : ElmLibraryWriter {
    @Throws(IOException::class)
    override fun write(library: Library, writer: Writer) {
        writer.write(writeAsString(library))
        //        ElmXmlMapper.getMapper().writeValue(writer, library);
    }

    override fun writeAsString(library: Library): String {
        val serializersModule =
            org.hl7.elm.r1.Serializer.createSerializer() +
                org.hl7.cql_annotations.r1.Serializer.createSerializer()

        val xml =
            XML(serializersModule) {
                xmlDeclMode = nl.adaptivity.xmlutil.XmlDeclMode.Charset
                //                            autoPolymorphic = true
                defaultPolicy {
                    typeDiscriminatorName =
                        QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi")
                }
            }

        return xml.encodeToString(Library.serializer(), library)
        //        try {
        //            LibraryWrapper wrapper = new LibraryWrapper(library);
        //            return ElmXmlMapper.getMapper().writeValueAsString(wrapper);
        //        } catch (JsonProcessingException e) {
        //            throw new RuntimeException(e);
        //        }
    }
}
