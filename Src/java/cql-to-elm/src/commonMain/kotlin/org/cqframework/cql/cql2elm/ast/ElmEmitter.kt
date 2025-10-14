package org.cqframework.cql.cql2elm.ast

import org.cqframework.cql.cql2elm.ast.ElmEmitter.UnsupportedNodeException
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.Definition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.UnsupportedStatement
import org.hl7.cql.ast.UsingDefinition
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal as ElmLiteral
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Converts the CQL AST into an equivalent ELM representation. The emitter focuses on structural
 * parity with the legacy parse-tree visitor and will grow to cover the full language surface area.
 */
class ElmEmitter {
    private val typesNamespace = "urn:hl7-org:elm-types:r1"

    @Suppress("MemberVisibilityCanBePrivate")
    data class Result(val library: Library)

    class UnsupportedNodeException(message: String) : RuntimeException(message)

    fun emit(astLibrary: org.hl7.cql.ast.Library): Result {
        val elmLibrary = Library()
        elmLibrary.schemaIdentifier = defaultSchemaIdentifier()
        astLibrary.name?.let {
            elmLibrary.identifier = VersionedIdentifier().apply {
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

        val usingDefs = emitUsings(astLibrary.definitions)
        if (usingDefs.isNotEmpty()) {
            elmLibrary.usings = Library.Usings().apply { def = usingDefs.toMutableList() }
        }

        val statementEmitter = StatementEmitter()
        statementEmitter.emit(astLibrary.statements)

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

    private fun emitUsings(definitions: List<Definition>): List<UsingDef> {
        return definitions
            .mapNotNull { definition ->
                when (definition) {
                    is UsingDefinition -> emitUsing(definition)
                    else -> null
                }
            }
    }

    private fun emitUsing(definition: UsingDefinition): UsingDef {
        val usingDef = UsingDef()
        val localId = definition.alias?.value ?: definition.modelIdentifier.simpleName
        usingDef.localIdentifier = localId
        usingDef.version = definition.version?.value
        val modelName = definition.modelIdentifier.simpleName
        usingDef.uri =
            when (modelName) {
                "System" -> typesNamespace
                else ->
                    throw UnsupportedNodeException(
                        "Model '$modelName' is not yet supported by the AST emitter."
                    )
            }
        return usingDef
    }

    private inner class StatementEmitter {
        val expressions = mutableListOf<ExpressionDef>()

        private var currentContext: String? = null

        fun emit(statements: List<Statement>) {
            statements.forEach { emit(it) }
        }

        private fun emit(statement: Statement) {
            when (statement) {
                is ContextDefinition -> handleContext(statement)
                is ExpressionDefinition -> expressions += emitExpressionDefinition(statement)
                is FunctionDefinition ->
                    throw UnsupportedNodeException("Function definitions are not supported yet.")
                is UnsupportedStatement ->
                    throw UnsupportedNodeException(
                        "Unsupported statement encountered: ${statement.grammarRule}"
                    )
            }
        }

        private fun handleContext(contextDefinition: ContextDefinition) {
            currentContext = contextDefinition.context.value
        }

        private fun emitExpressionDefinition(definition: ExpressionDefinition): ExpressionDef {
            val expressionDef =
                ExpressionDef().apply {
                    name = definition.name.value
                    accessLevel = ElmAccessModifier.PUBLIC
                    definition.access?.let { access ->
                        accessLevel = when (access) {
                            AstAccessModifier.PUBLIC -> ElmAccessModifier.PUBLIC
                            AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
                        }
                    }
                    currentContext?.let { context = it }
                    expression = emitExpression(definition.expression)
                }
            return expressionDef
        }
    }

    private fun emitExpression(expression: Expression): ElmExpression {
        return when (expression) {
            is LiteralExpression -> emitLiteral(expression.literal)
            is IdentifierExpression ->
                throw UnsupportedNodeException("Identifier expressions are not yet supported.")
            else ->
                throw UnsupportedNodeException(
                    "Expression '${expression::class.simpleName}' is not supported yet."
                )
        }
    }

    private fun emitLiteral(literal: Literal): ElmExpression {
        return when (literal) {
            is StringLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "String"))
                    .withValue(literal.value)
            is BooleanLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "Boolean"))
                    .withValue(literal.value.toString())
            is IntLiteral ->
                ElmLiteral().withValueType(QName(typesNamespace, "Integer")).withValue(
                    literal.value.toString()
                )
            is LongLiteral ->
                ElmLiteral().withValueType(QName(typesNamespace, "Long")).withValue(
                    literal.value.toString()
                )
            is DecimalLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "Decimal"))
                    .withValue(literal.value.toString())
            is NullLiteral -> Null()
            else ->
                throw UnsupportedNodeException(
                    "Literal '${literal::class.simpleName}' is not supported yet."
                )
        }
    }
}
