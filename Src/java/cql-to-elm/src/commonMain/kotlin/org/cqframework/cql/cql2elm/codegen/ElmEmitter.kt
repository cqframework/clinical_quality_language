package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.OperatorRegistry
import org.cqframework.cql.cql2elm.analysis.SemanticModel
import org.cqframework.cql.cql2elm.analysis.SymbolTable
import org.cqframework.cql.cql2elm.analysis.TypeTable
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Thin orchestrator that drives the CQL AST → ELM conversion.
 *
 * **Input:** a CQL AST [Library][org.hl7.cql.ast.Library] plus a [SemanticModel] (the compilation
 * database produced by the analysis phases — [SymbolTable], [TypeTable], [OperatorRegistry], and
 * SyntheticTable).
 *
 * **Output:** an ELM [Library] wrapped in a [Result].
 *
 * This class creates an [EmissionContext] (which is the [ExpressionFold] responsible for all
 * expression-level codegen) and then calls its per-section emission methods — usings, includes,
 * parameters, terminology, contexts, statements — to populate the ELM Library.
 *
 * ## How to add a new library-level section
 * 1. Add the emission method to [EmissionContext] (or an emission extension file).
 * 2. Wire it into [emit]: call the new method, check for non-empty results, and assign to the
 *    appropriate ELM [Library] field.
 *
 * ## What NOT to put here
 * - Expression-level emission logic — that belongs in [EmissionContext] and emission extension
 *   files. This class only orchestrates library-level sections.
 * - Analysis or validation — those happen before this stage in the pipeline.
 */
class ElmEmitter(semanticModel: SemanticModel) {
    /** Backward-compatible constructor accepting individual components. */
    constructor(
        symbolTable: SymbolTable = SymbolTable(),
        typeTable: TypeTable = TypeTable(),
        operatorRegistry: OperatorRegistry = OperatorRegistry.createSystemRegistry(),
    ) : this(SemanticModel(symbolTable, typeTable, operatorRegistry))

    private val ctx = EmissionContext(semanticModel)

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
