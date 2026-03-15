package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.analysis.OperatorRegistry
import org.cqframework.cql.cql2elm.analysis.SymbolTable
import org.cqframework.cql.cql2elm.analysis.TypeTable
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Converts the CQL AST into an equivalent ELM representation. The emitter reads types from the
 * [TypeTable] (populated by [org.cqframework.cql.cql2elm.analysis.TypeResolver]) and sets
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
    modelManager: ModelManager? = null,
) {
    private val ctx = EmissionContext(typeTable, symbolTable, operatorRegistry, modelManager)

    @Suppress("MemberVisibilityCanBePrivate") data class Result(val library: Library)

    class UnsupportedNodeException(message: String) : RuntimeException(message)

    fun emit(astLibrary: org.hl7.cql.ast.Library): Result {
        val elmLibrary = Library()
        elmLibrary.schemaIdentifier = defaultSchemaIdentifier()
        // The legacy translator always emits an identifier, even if no library declaration exists.
        val identifier = VersionedIdentifier()
        astLibrary.name?.let {
            identifier.id = it.simpleName
            if (it.parts.size > 1) {
                identifier.system = it.parts.dropLast(1).joinToString(".")
            }
        }
        astLibrary.version?.let { version -> identifier.version = version.value }
        elmLibrary.identifier = identifier

        val usingDefs = ctx.emitUsings(astLibrary.definitions)
        if (usingDefs.isNotEmpty()) {
            elmLibrary.usings = Library.Usings().apply { def = usingDefs.toMutableList() }
        }

        val includeDefs = ctx.emitIncludes(astLibrary.definitions)
        if (includeDefs.isNotEmpty()) {
            elmLibrary.includes = Library.Includes().apply { def = includeDefs.toMutableList() }
        }

        val parameterDefs = ctx.emitParameters(astLibrary.definitions)
        if (parameterDefs.isNotEmpty()) {
            elmLibrary.parameters =
                Library.Parameters().apply { def = parameterDefs.toMutableList() }
        }

        val codeSystemDefs = ctx.emitCodeSystemDefs(astLibrary.definitions)
        if (codeSystemDefs.isNotEmpty()) {
            elmLibrary.codeSystems =
                Library.CodeSystems().apply { def = codeSystemDefs.toMutableList() }
        }

        val valueSetDefs = ctx.emitValueSetDefs(astLibrary.definitions)
        if (valueSetDefs.isNotEmpty()) {
            elmLibrary.valueSets = Library.ValueSets().apply { def = valueSetDefs.toMutableList() }
        }

        val codeDefs = ctx.emitCodeDefs(astLibrary.definitions)
        if (codeDefs.isNotEmpty()) {
            elmLibrary.codes = Library.Codes().apply { def = codeDefs.toMutableList() }
        }

        val conceptDefs = ctx.emitConceptDefs(astLibrary.definitions)
        if (conceptDefs.isNotEmpty()) {
            elmLibrary.concepts = Library.Concepts().apply { def = conceptDefs.toMutableList() }
        }

        val statementEmitter = StatementEmitter(ctx)
        statementEmitter.emit(astLibrary.statements)

        val contextDefs = ctx.emitContextDefs(astLibrary.statements, astLibrary.definitions)
        if (contextDefs.isNotEmpty()) {
            elmLibrary.contexts = Library.Contexts().apply { def = contextDefs.toMutableList() }
        }

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
