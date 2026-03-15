package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.AstWalker
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.UnsupportedStatement
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.ExpressionDef

/**
 * Emits statement-level constructs (context definitions, expression definitions, function
 * definitions). Tracks current context for expression definitions. Handles forward references by
 * emitting referenced definitions before the referencing definition, matching the legacy
 * translator's output order.
 */
internal class StatementEmitter(private val ctx: EmissionContext) {
    val expressions = mutableListOf<ExpressionDef>()

    private var currentContext: String? = null
    private val emittedExpressions = mutableSetOf<String>()
    private val statementContexts = mutableMapOf<String, String?>()
    private val expressionDefsByName = mutableMapOf<String, ExpressionDefinition>()
    private val functionDefsByName = mutableMapOf<String, MutableList<FunctionDefinition>>()

    fun emit(statements: List<Statement>) {
        // First pass: collect all definitions and track contexts
        var ctx: String? = null
        for (statement in statements) {
            when (statement) {
                is ContextDefinition -> ctx = statement.context.value
                is ExpressionDefinition -> {
                    expressionDefsByName[statement.name.value] = statement
                    statementContexts[statement.name.value] = ctx
                }
                is FunctionDefinition -> {
                    functionDefsByName
                        .getOrPut(statement.name.value) { mutableListOf() }
                        .add(statement)
                    statementContexts[statement.name.value] = ctx
                }
                else -> {}
            }
        }

        // Second pass: emit in order, resolving forward references
        currentContext = null
        for (statement in statements) {
            emit(statement)
        }
    }

    private fun emit(statement: Statement) {
        when (statement) {
            is ContextDefinition -> handleContext(statement)
            is ExpressionDefinition -> ensureEmitted(statement.name.value)
            is FunctionDefinition -> emitFunctionDef(statement)
            is UnsupportedStatement ->
                throw ElmEmitter.UnsupportedNodeException(
                    "Unsupported statement encountered: ${statement.grammarRule}"
                )
        }
    }

    private fun handleContext(contextDefinition: ContextDefinition) {
        currentContext = contextDefinition.context.value
        emitImplicitContextDef(contextDefinition)
    }

    /**
     * Emit the implicit context expression definition that the legacy translator creates. For
     * `context Patient`, this produces: `define Patient = SingletonFrom([Patient])`.
     */
    private fun emitImplicitContextDef(contextDefinition: ContextDefinition) {
        val contextName = contextDefinition.context.value
        if (contextName in emittedExpressions) return
        val mm = ctx.modelManager ?: return
        if (ctx.loadedModelNames.isEmpty()) return

        // Resolve the context type from loaded models
        for (modelName in ctx.loadedModelNames) {
            val model = mm.resolveModel(modelName)
            val contextType = model.resolveContextName(contextName, mustResolve = false) ?: continue
            val classType = contextType.type as? org.hl7.cql.model.ClassType ?: continue

            val modelInfo = model.modelInfo
            val modelUrl = modelInfo.targetUrl ?: modelInfo.url!!
            val retrieve = org.hl7.elm.r1.Retrieve()
            retrieve.dataType = org.cqframework.cql.shared.QName(modelUrl, contextName)
            classType.identifier?.let { retrieve.templateId = it }

            val singletonFrom = org.hl7.elm.r1.SingletonFrom()
            singletonFrom.operand = retrieve

            val exprDef = ExpressionDef()
            exprDef.name = contextName
            exprDef.context = currentContext
            exprDef.expression = singletonFrom
            expressions += exprDef
            emittedExpressions.add(contextName)
            return
        }
    }

    /** Ensure an expression definition is emitted, resolving dependencies first. */
    private fun ensureEmitted(name: String) {
        if (name in emittedExpressions) return
        val definition = expressionDefsByName[name] ?: return
        emittedExpressions.add(name) // Mark early to prevent cycles

        // Collect identifier references from the expression and emit dependencies first
        val refs = IdentifierRefCollector.collect(definition.expression)
        for (ref in refs) {
            if (ref in expressionDefsByName) {
                ensureEmitted(ref)
            }
        }

        val savedContext = currentContext
        statementContexts[name]?.let { currentContext = it }
        expressions += emitExpressionDefinition(definition)
        currentContext = savedContext
    }

    private fun emitFunctionDef(definition: FunctionDefinition) {
        // Ensure any expression definitions referenced by the function body are emitted first
        val body = definition.body
        if (body is ExpressionFunctionBody) {
            val refs = IdentifierRefCollector.collect(body.expression)
            for (ref in refs) {
                if (ref in expressionDefsByName) {
                    ensureEmitted(ref)
                }
            }
        }

        val savedContext = currentContext
        statementContexts[definition.name.value]?.let { currentContext = it }
        expressions += ctx.emitFunctionDefinition(definition, currentContext)
        currentContext = savedContext
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
        val exprType = ctx.typeTable[definition.expression]
        if (exprType != null) {
            ctx.decorate(expressionDef, exprType)
        }
        return expressionDef
    }
}

/**
 * Collects all [IdentifierExpression] names from an expression tree using [AstWalker]. This ensures
 * complete coverage of all AST expression types as new ones are added.
 */
internal object IdentifierRefCollector {
    fun collect(expression: org.hl7.cql.ast.Expression): Set<String> {
        val refs = mutableSetOf<String>()
        val walker =
            object : AstWalker() {
                override fun visitIdentifierExpression(expression: IdentifierExpression) {
                    refs.add(expression.name.simpleName)
                }
            }
        walker.visitExpression(expression)
        return refs
    }
}
