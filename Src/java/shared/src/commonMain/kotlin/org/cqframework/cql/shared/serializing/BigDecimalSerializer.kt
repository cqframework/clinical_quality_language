package org.cqframework.cql.shared.serializing

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral
import org.cqframework.cql.shared.BigDecimal

/** A JSON serializer for [BigDecimal] as a raw JSON number (unquoted literal). */
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("org.cqframework.cql.shared.BigDecimal", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: BigDecimal) {
        require(encoder is JsonEncoder) { "BigDecimalSerializer can be used only with JSON format" }
        encoder.encodeJsonElement(JsonUnquotedLiteral(value.toPlainString()))
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        require(decoder is JsonDecoder) { "BigDecimalSerializer can be used only with JSON format" }
        val jsonElement = decoder.decodeJsonElement()
        require(jsonElement is JsonPrimitive) { "Expected JSON primitive for BigDecimal field" }
        return BigDecimal(jsonElement.content)
    }
}
