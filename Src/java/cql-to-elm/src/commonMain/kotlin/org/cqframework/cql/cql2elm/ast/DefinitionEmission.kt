package org.cqframework.cql.cql2elm.ast

import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Definition
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.UnsupportedStatement
import org.hl7.cql.ast.UsingDefinition
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.UsingDef

internal fun EmissionContext.emitUsings(definitions: List<Definition>): List<UsingDef> {
    return definitions.mapNotNull { definition ->
        when (definition) {
            is UsingDefinition -> emitUsing(definition)
            else -> null
        }
    }
}

internal fun EmissionContext.emitUsing(definition: UsingDefinition): UsingDef {
    val usingDef = UsingDef()
    val localId = definition.alias?.value ?: definition.modelIdentifier.simpleName
    usingDef.localIdentifier = localId
    usingDef.version = definition.version?.value
    val modelName = definition.modelIdentifier.simpleName
    usingDef.uri =
        when (modelName) {
            "System" -> typesNamespace
            else ->
                throw ElmEmitter.UnsupportedNodeException(
                    "Model '$modelName' is not yet supported by the AST emitter."
                )
        }
    return usingDef
}

internal fun EmissionContext.emitParameters(definitions: List<Definition>): List<ParameterDef> {
    return definitions.mapNotNull { definition ->
        when (definition) {
            is ParameterDefinition -> emitParameter(definition)
            else -> null
        }
    }
}

internal fun EmissionContext.emitParameter(definition: ParameterDefinition): ParameterDef {
    val paramDef = ParameterDef()
    paramDef.name = definition.name.value
    paramDef.accessLevel = ElmAccessModifier.PUBLIC
    definition.access?.let { access ->
        paramDef.accessLevel =
            when (access) {
                AstAccessModifier.PUBLIC -> ElmAccessModifier.PUBLIC
                AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
            }
    }
    definition.default?.let { defaultExpr -> paramDef.default = emitExpression(defaultExpr) }
    // parameterType and parameterTypeSpecifier are set during type resolution
    // which requires model resolution - not yet supported
    return paramDef
}

/**
 * Emits statement-level constructs (context definitions, expression definitions). Tracks current
 * context for expression definitions.
 */
internal class StatementEmitter(private val ctx: EmissionContext) {
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
                throw ElmEmitter.UnsupportedNodeException(
                    "Function definitions are not supported yet."
                )
            is UnsupportedStatement ->
                throw ElmEmitter.UnsupportedNodeException(
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
                    accessLevel =
                        when (access) {
                            AstAccessModifier.PUBLIC -> ElmAccessModifier.PUBLIC
                            AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
                        }
                }
                currentContext?.let { context = it }
                expression = ctx.emitExpression(definition.expression)
            }
        // Set result type on the ExpressionDef from the expression's type
        val exprType = ctx.typeTable[definition.expression]
        if (exprType != null) {
            ctx.decorate(expressionDef, exprType)
        }
        return expressionDef
    }
}
