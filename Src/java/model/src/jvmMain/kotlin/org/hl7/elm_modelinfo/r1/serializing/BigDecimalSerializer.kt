package org.hl7.elm_modelinfo.r1.serializing

import java.math.BigDecimal
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object BigDecimalXmlSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return decoder.decodeString().toBigDecimal()
    }
}

// We use JSON numbers, not strings to serialize BigDecimals in JSON.
object BigDecimalJsonSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeDouble(value.toDouble())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return decoder.decodeDouble().toBigDecimal()
    }
}