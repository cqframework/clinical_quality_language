package org.hl7.cql.ast

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class Library(
    val name: QualifiedIdentifier? = null,
    val version: VersionSpecifier? = null,
    val definitions: List<Definition> = emptyList(),
    val statements: List<Statement> = emptyList(),
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface Definition : AstNode

@Serializable
@SerialName("unsupported")
data class UnsupportedDefinition(
    val grammarRule: String,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
@SerialName("using")
data class UsingDefinition(
    val modelIdentifier: QualifiedIdentifier,
    val version: VersionSpecifier? = null,
    val alias: Identifier? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
@SerialName("include")
data class IncludeDefinition(
    val libraryIdentifier: QualifiedIdentifier,
    val version: VersionSpecifier? = null,
    val alias: Identifier? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
data class TerminologyReference(
    val identifier: Identifier,
    val libraryName: Identifier? = null,
    val locator: Locator = Locator.UNKNOWN,
)

@Serializable
@SerialName("codesystem")
data class CodeSystemDefinition(
    val access: AccessModifier? = null,
    val name: Identifier,
    val id: String,
    val version: VersionSpecifier? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
@SerialName("valueset")
data class ValueSetDefinition(
    val access: AccessModifier? = null,
    val name: Identifier,
    val id: String,
    val version: VersionSpecifier? = null,
    val codesystems: List<TerminologyReference> = emptyList(),
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
@SerialName("code")
data class CodeDefinition(
    val access: AccessModifier? = null,
    val name: Identifier,
    val id: String,
    val system: TerminologyReference,
    val display: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
@SerialName("concept")
data class ConceptDefinition(
    val access: AccessModifier? = null,
    val name: Identifier,
    val codes: List<TerminologyReference>,
    val display: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition

@Serializable
@SerialName("parameter")
data class ParameterDefinition(
    val access: AccessModifier? = null,
    val name: Identifier,
    val type: TypeSpecifier? = null,
    val default: Expression? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Definition


@Serializable data class ParameterRef(val name: Identifier, val locator: Locator = Locator.UNKNOWN)

@Serializable
data class OperandDefinition(
    val name: Identifier,
    val type: TypeSpecifier,
    val locator: Locator = Locator.UNKNOWN,
)

@Serializable sealed interface FunctionBody : AstNode

@Serializable
@SerialName("expressionBody")
data class ExpressionFunctionBody(
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : FunctionBody

@Serializable
@SerialName("externalBody")
data class ExternalFunctionBody(override val locator: Locator = Locator.UNKNOWN) : FunctionBody
