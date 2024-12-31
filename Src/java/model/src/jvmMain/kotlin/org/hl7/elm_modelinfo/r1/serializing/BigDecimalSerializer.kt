package org.hl7.elm_modelinfo.r1.serializing

import java.math.BigDecimal
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object BigDecimalSerializer : KSerializer<BigDecimal?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal?) {
        encoder.encodeString(value?.toPlainString() ?: "")
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}
