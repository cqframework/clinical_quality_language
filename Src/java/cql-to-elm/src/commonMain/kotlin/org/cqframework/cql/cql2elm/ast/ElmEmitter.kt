package org.cqframework.cql.cql2elm.ast

import org.cqframework.cql.cql2elm.frontend.SymbolTable
import org.cqframework.cql.cql2elm.frontend.TypeTable
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Converts the CQL AST into an equivalent ELM representation. The emitter reads types from the
 * [TypeTable] (populated by [org.cqframework.cql.cql2elm.frontend.TypeResolver]) and sets
 * `resultType`, `resultTypeName`, and `resultTypeSpecifier` on emitted ELM nodes to match the
 * legacy translator output.
 *
 * This is a thin orchestrator; the actual emission logic lives in extension functions on
 * [EmissionContext] in separate files:
 * - [LiteralEmission.kt][emitLiteral] -- literal type handlers
 * - [OperatorEmission.kt][emitBinaryOperator] -- binary/unary operator emission
 * - [TemporalEmission.kt][emitDateTime] -- date/time parsing
 * - [DefinitionEmission.kt][emitUsings] -- definition and statement emission
 */
class ElmEmitter(
    symbolTable: SymbolTable = SymbolTable(),
    typeTable: TypeTable = TypeTable(),
    operatorRegistry: OperatorRegistry = OperatorRegistry.createSystemRegistry(),
) {
    private val ctx = EmissionContext(typeTable, symbolTable, operatorRegistry)

    @Suppress("MemberVisibilityCanBePrivate") data class Result(val library: Library)

    class UnsupportedNodeException(message: String) : RuntimeException(message)

    fun emit(astLibrary: org.hl7.cql.ast.Library): Result {
        val elmLibrary = Library()
        elmLibrary.schemaIdentifier = defaultSchemaIdentifier()
        astLibrary.name?.let {
            elmLibrary.identifier =
                VersionedIdentifier().apply {
                    id = it.simpleName
                    if (it.parts.size > 1) {
                        system = it.parts.dropLast(1).joinToString(".")
                    }
                }
        }
        astLibrary.version?.let { version ->
            val identifier = elmLibrary.identifier ?: VersionedIdentifier()
            identifier.version = version.value
            elmLibrary.identifier = identifier
        }

        val usingDefs = ctx.emitUsings(astLibrary.definitions)
        if (usingDefs.isNotEmpty()) {
            elmLibrary.usings = Library.Usings().apply { def = usingDefs.toMutableList() }
        }

        val parameterDefs = ctx.emitParameters(astLibrary.definitions)
        if (parameterDefs.isNotEmpty()) {
            elmLibrary.parameters =
                Library.Parameters().apply { def = parameterDefs.toMutableList() }
        }

        val statementEmitter = StatementEmitter(ctx)
        statementEmitter.emit(astLibrary.statements)

        // Note: ContextDefs are not emitted here because the legacy translator only emits them
        // when models with real context types are loaded (e.g., FHIR Patient). For System-only
        // libraries, the context resolution fails silently in the legacy translator and no
        // ContextDef is added. Once model resolution is implemented, ContextDefs will be emitted.

        val expressionDefs = statementEmitter.expressions
        if (expressionDefs.isNotEmpty()) {
            elmLibrary.statements =
                Library.Statements().apply { def = expressionDefs.toMutableList() }
        }

        return Result(elmLibrary)
    }

    private fun defaultSchemaIdentifier(): VersionedIdentifier =
        VersionedIdentifier().apply {
            id = "urn:hl7-org:elm"
            version = "r1"
        }
}
