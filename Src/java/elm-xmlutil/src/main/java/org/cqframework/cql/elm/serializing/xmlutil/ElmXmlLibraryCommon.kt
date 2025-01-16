package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.serializersModuleOf
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.serialization.XML
import org.hl7.elm_modelinfo.r1.serializing.BigDecimalXmlSerializer

val xml =
    XML(
        serializersModuleOf(BigDecimalXmlSerializer) +
            org.hl7.elm.r1.serializersModule +
            org.hl7.cql_annotations.r1.serializersModule
    ) {
        xmlDeclMode = nl.adaptivity.xmlutil.XmlDeclMode.Charset
        defaultPolicy {
            typeDiscriminatorName =
                QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi")
        }
    }
