package org.cqframework.cql.elm.serializing

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.serializersModuleOf
import nl.adaptivity.xmlutil.*
import nl.adaptivity.xmlutil.serialization.*
import nl.adaptivity.xmlutil.serialization.structure.SafeParentInfo

private val defaultPolicy =
    @Suppress("DEPRECATION")
    DefaultXmlSerializationPolicy {
        // Use xsi:type for handling polymorphism
        typeDiscriminatorName = QName(XMLConstants.XSI_NS_URI, "type", XMLConstants.XSI_PREFIX)
    }

@OptIn(ExperimentalXmlUtilApi::class)
private val customPolicy =
    object : XmlSerializationPolicy by defaultPolicy {
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
            return defaultPolicy.isTransparentPolymorphic(serializerParent, tagParent)
        }
    }

// Mixed content can include text and Narrative elements
private val mixedContentSerializersModule = SerializersModule {
    polymorphic(Any::class) {
        polymorphic(Any::class, String::class, String.serializer())
        polymorphic(
            Any::class,
            org.hl7.cql_annotations.r1.Narrative::class,
            org.hl7.cql_annotations.r1.Narrative.serializer()
        )
    }
}

@OptIn(ExperimentalXmlUtilApi::class)
internal val xml =
    XML(
        XmlConfig(
            @Suppress("DEPRECATION")
            XmlConfig.Builder().apply {
                policy = customPolicy
                xmlDeclMode = XmlDeclMode.Charset
                isCachingEnabled = false
            }
        ),
        serializersModuleOf(BigDecimalXmlSerializer) +
            mixedContentSerializersModule +
            org.hl7.elm.r1.serializersModule +
            org.hl7.cql_annotations.r1.serializersModule
    )
