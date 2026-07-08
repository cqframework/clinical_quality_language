package org.hl7.cql.ast

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface TypeSpecifier : AstNode

@Serializable
@SerialName("named")
data class NamedTypeSpecifier(
    val name: QualifiedIdentifier,
    override val locator: Locator = Locator.UNKNOWN,
) : TypeSpecifier

@Serializable
@SerialName("list")
data class ListTypeSpecifier(
    val elementType: TypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : TypeSpecifier

@Serializable
@SerialName("interval")
data class IntervalTypeSpecifier(
    val pointType: TypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : TypeSpecifier

@Serializable
data class TupleElement(
    val name: Identifier,
    val type: TypeSpecifier,
    val locator: Locator = Locator.UNKNOWN,
)

@Serializable
@SerialName("tuple")
data class TupleTypeSpecifier(
    val elements: List<TupleElement>,
    override val locator: Locator = Locator.UNKNOWN,
) : TypeSpecifier

@Serializable
@SerialName("choice")
data class ChoiceTypeSpecifier(
    val choices: List<TypeSpecifier>,
    override val locator: Locator = Locator.UNKNOWN,
) : TypeSpecifier
