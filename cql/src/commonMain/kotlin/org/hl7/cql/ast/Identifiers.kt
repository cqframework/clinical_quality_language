package org.hl7.cql.ast

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** How an identifier was written. The quoting does not change which name it refers to. */
@Serializable
enum class IdentifierStyle {
    /** `Name` */
    @SerialName("plain") PLAIN,

    /** `"Name"` */
    @SerialName("quoted") QUOTED,

    /** `` `Name` `` */
    @SerialName("delimited") DELIMITED,
}

/**
 * A name as written in the source. [value] is the name with any quotes removed and escapes
 * resolved, so `"A"` and `A` have the same value and differ only in [style].
 */
@Serializable
data class Identifier(
    val value: String,
    val style: IdentifierStyle = IdentifierStyle.PLAIN,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode {
    override fun toString(): String = value
}

/** A dotted name, such as `FHIR.Patient` or `Common."Inpatient Encounter"`. */
@Serializable
data class QualifiedIdentifier(
    val parts: List<Identifier>,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode {
    init {
        require(parts.isNotEmpty()) { "QualifiedIdentifier must contain at least one part." }
    }

    val simpleName: String
        get() = parts.last().value

    /** The values of [parts], without quoting or positions. */
    val names: List<String>
        get() = parts.map { it.value }

    override fun toString(): String = names.joinToString(".")
}

@Serializable
@JvmInline
value class VersionSpecifier(val value: String) {
    override fun toString(): String = value
}

@Serializable
enum class AccessModifier {
    @SerialName("public") PUBLIC,
    @SerialName("private") PRIVATE,
}
