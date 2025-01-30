package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.serializersModuleOf
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XMLConstants
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.structure.SafeParentInfo
import org.hl7.elm_modelinfo.r1.serializing.BigDecimalXmlSerializer

val builder =
    DefaultXmlSerializationPolicy.Builder().apply {
        // Use xsi:type for handling polymorphism
        typeDiscriminatorName = QName(XMLConstants.XSI_NS_URI, "type", XMLConstants.XSI_PREFIX)
    }

@OptIn(ExperimentalSerializationApi::class)
val customPolicy =
    object : DefaultXmlSerializationPolicy(builder) {
        override fun isTransparentPolymorphic(
            serializerParent: SafeParentInfo,
            tagParent: SafeParentInfo
        ): Boolean {
            // Switch on transparent polymorphic mode for mixed content
            if (
                serializerParent.elementSerialDescriptor.serialName ==
                    "kotlinx.serialization.Polymorphic<Any>"
            ) {
                return true
            }
            return super.isTransparentPolymorphic(serializerParent, tagParent)
        }
    }

// Mixed content can include text and Narrative elements
val mixedContentSerializersModule = SerializersModule {
    polymorphic(Any::class) {
        polymorphic(Any::class, String::class, String.serializer())
        polymorphic(
            Any::class,
            org.hl7.cql_annotations.r1.Narrative::class,
            org.hl7.cql_annotations.r1.Narrative.serializer()
        )
    }
}

val xml =
    XML(
        serializersModuleOf(BigDecimalXmlSerializer) +
            mixedContentSerializersModule +
            org.hl7.elm.r1.serializersModule +
            org.hl7.cql_annotations.r1.serializersModule
    ) {
        policy = customPolicy
        xmlDeclMode = nl.adaptivity.xmlutil.XmlDeclMode.Charset
    }
