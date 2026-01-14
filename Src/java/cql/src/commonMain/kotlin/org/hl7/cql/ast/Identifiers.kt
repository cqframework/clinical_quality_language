package org.hl7.cql.ast

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Identifier(val value: String) {
    init {
        require(value.isNotBlank()) { "Identifier must not be blank." }
    }

    override fun toString(): String = value
}

@Serializable
data class QualifiedIdentifier(val parts: List<String>) {
    init {
        require(parts.isNotEmpty()) { "QualifiedIdentifier must contain at least one part." }
    }

    val simpleName: String
        get() = parts.last()

    override fun toString(): String = parts.joinToString(".")
}

@Serializable
@JvmInline
value class VersionSpecifier(val value: String) {
    init {
        require(value.isNotEmpty()) { "VersionSpecifier must not be empty." }
    }

    override fun toString(): String = value
}

@Serializable
enum class AccessModifier {
    @SerialName("public") PUBLIC,
    @SerialName("private") PRIVATE,
}
