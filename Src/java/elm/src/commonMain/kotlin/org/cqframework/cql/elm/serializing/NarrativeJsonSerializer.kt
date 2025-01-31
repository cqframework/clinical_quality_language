@file:Suppress("WildcardImport")

package org.cqframework.cql.elm.serializing

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.hl7.cql_annotations.r1.Narrative

@Serializable
data class NarrativeJson(
    val s: MutableList<@Serializable(NarrativeJsonContentSerializer::class) Any>?,
    val r: String?
)

// When tags appear inside mixed content in JSON, they are wrapped in this structure.
// We only support Narrative tags in mixed content, so this only handles Narratives.
@Serializable
data class NarrativeJsonWrapper(
    val name: String,
    val declaredType: String,
    val scope: String,
    @Serializable(NarrativeJsonSerializer::class) val value: Narrative,
    val globalScope: Boolean
)

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
object NarrativeJsonContentSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("NarrativeJsonContentSerializer", PolymorphicKind.SEALED)

    override fun serialize(encoder: Encoder, value: Any) {
        // Serialize strings as is, but wrap Narratives in a NarrativeJsonWrapper.
        when (value) {
            is String -> String.serializer().serialize(encoder, value)
            is Narrative ->
                NarrativeJsonWrapper.serializer()
                    .serialize(
                        encoder,
                        NarrativeJsonWrapper(
                            "{urn:hl7-org:cql-annotations:r1}s",
                            "org.hl7.cql_annotations.r1.Narrative",
                            "javax.xml.bind.JAXBElement\$GlobalScope",
                            value,
                            true
                        )
                    )
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val jsonElement = jsonDecoder.decodeJsonElement()

        // Deserialize strings as is, otherwise parse as a NarrativeJsonWrapper and extract the
        // Narrative.
        if (jsonElement is JsonPrimitive && jsonElement.jsonPrimitive.isString) {
            return jsonDecoder.json.decodeFromJsonElement(String.serializer(), jsonElement)
        }

        val narrativeJsonWrapper =
            jsonDecoder.json.decodeFromJsonElement(NarrativeJsonWrapper.serializer(), jsonElement)
        return narrativeJsonWrapper.value
    }
}

// A custom serializer for Narrative that reuses the descriptor and serializer from NarrativeJson.
object NarrativeJsonSerializer : KSerializer<Narrative> {
    override val descriptor: SerialDescriptor = NarrativeJson.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Narrative) {
        encoder.encodeSerializableValue(
            NarrativeJson.serializer(),
            NarrativeJson(value._content, value.r)
        )
    }

    override fun deserialize(decoder: Decoder): Narrative {
        val narrative = decoder.decodeSerializableValue(NarrativeJson.serializer())
        return Narrative().apply {
            _content = narrative.s
            r = narrative.r
        }
    }
}
