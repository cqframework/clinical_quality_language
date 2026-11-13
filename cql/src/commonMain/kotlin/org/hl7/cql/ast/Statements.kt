package org.hl7.cql.ast

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface Statement : Declaration

@Serializable
@SerialName("unsupported")
data class UnsupportedStatement(
    val grammarRule: String,
    override val comments: List<Comment> = emptyList(),
    override val tags: List<Tag> = emptyList(),
    override val locator: Locator = Locator.UNKNOWN,
) : Statement

@Serializable
@SerialName("context")
data class ContextDefinition(
    val model: Identifier? = null,
    val context: Identifier,
    override val comments: List<Comment> = emptyList(),
    override val tags: List<Tag> = emptyList(),
    override val locator: Locator = Locator.UNKNOWN,
) : Statement

@Serializable
@SerialName("expression")
data class ExpressionDefinition(
    val access: AccessModifier? = null,
    val name: Identifier,
    val expression: Expression,
    override val comments: List<Comment> = emptyList(),
    override val tags: List<Tag> = emptyList(),
    override val locator: Locator = Locator.UNKNOWN,
) : Statement

@Serializable
@SerialName("function")
data class FunctionDefinition(
    val access: AccessModifier? = null,
    val fluent: Boolean = false,
    val name: Identifier,
    val operands: List<OperandDefinition> = emptyList(),
    val returnType: TypeSpecifier? = null,
    val body: FunctionBody,
    override val comments: List<Comment> = emptyList(),
    override val tags: List<Tag> = emptyList(),
    override val locator: Locator = Locator.UNKNOWN,
) : Statement
