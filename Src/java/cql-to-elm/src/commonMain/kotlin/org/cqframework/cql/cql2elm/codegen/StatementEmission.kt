package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.UnsupportedStatement
import org.hl7.cql.ast.forEachChildExpression
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
    private val emittedFunctions = mutableSetOf<String>()
    private val statementContexts = mutableMapOf<String, String?>()
    private val expressionDefsByName = mutableMapOf<String, ExpressionDefinition>()
    private val functionDefsByName = mutableMapOf<String, MutableList<FunctionDefinition>>()

    companion object {
        /** Default context used by the legacy translator when no explicit context is declared. */
        const val DEFAULT_CONTEXT = "Unfiltered"
    }

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

        // The legacy translator defaults to "Unfiltered" context when no explicit context is set
        currentContext = DEFAULT_CONTEXT
        // Second pass: emit in order, resolving forward references
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
        if (ctx.modelContext.loadedModelNames.isEmpty()) return

        try {
            val retrieve = ctx.buildRetrieveForType(contextName)
            val singletonFrom = org.hl7.elm.r1.SingletonFrom()
            singletonFrom.operand = retrieve

            val exprDef = ExpressionDef()
            exprDef.name = contextName
            exprDef.context = currentContext
            exprDef.expression = singletonFrom
            expressions += exprDef
            emittedExpressions.add(contextName)
        } catch (_: ElmEmitter.UnsupportedNodeException) {
            // Context type not found in any model — skip implicit definition
        }
    }

    /** Ensure an expression definition is emitted, resolving dependencies first. */
    private fun ensureEmitted(name: String) {
        if (name in emittedExpressions) return
        val definition = expressionDefsByName[name] ?: return
        emittedExpressions.add(name) // Mark early to prevent cycles

        // Collect identifier references from the expression and emit dependencies first
        val refs = IdentifierRefCollector.collect(definition.expression)
        val funcRefs = FunctionRefCollector.collect(definition.expression)
        for (ref in refs) {
            if (ref in expressionDefsByName) {
                ensureEmitted(ref)
            }
        }
        // Emit function definitions referenced by this expression before the expression itself
        for (funcName in funcRefs) {
            ensureFunctionEmitted(funcName)
        }

        val savedContext = currentContext
        statementContexts[name]?.let { currentContext = it }
        expressions += emitExpressionDefinition(definition)
        currentContext = savedContext
    }

    /** Ensure a function definition is emitted (for forward-reference ordering). */
    private fun ensureFunctionEmitted(name: String) {
        if (name in emittedFunctions) return
        val defs = functionDefsByName[name] ?: return
        emittedFunctions.add(name)
        for (funcDef in defs) {
            // Emit dependencies of this function first
            val body = funcDef.body
            if (body is ExpressionFunctionBody) {
                val refs = IdentifierRefCollector.collect(body.expression)
                for (ref in refs) {
                    if (ref in expressionDefsByName) {
                        ensureEmitted(ref)
                    }
                }
            }
            val savedContext = currentContext
            statementContexts[funcDef.name.value]?.let { currentContext = it }
            expressions += ctx.emitFunctionDefinition(funcDef, currentContext)
            currentContext = savedContext
        }
    }

    private fun emitFunctionDef(definition: FunctionDefinition) {
        ensureFunctionEmitted(definition.name.value)
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
        val exprType = ctx.semanticModel[definition.expression]
        if (exprType != null) {
            ctx.decorate(expressionDef, exprType)
        }
        return expressionDef
    }
}

/**
 * Collects all [IdentifierExpression] names from an expression tree by recursively walking child
 * expressions via [forEachChildExpression].
 */
internal object IdentifierRefCollector {
    fun collect(expression: Expression): Set<String> {
        val refs = mutableSetOf<String>()
        walk(expression, refs)
        return refs
    }

    private fun walk(expression: Expression, refs: MutableSet<String>) {
        if (expression is IdentifierExpression) refs.add(expression.name.simpleName)
        forEachChildExpression(expression) { walk(it, refs) }
    }
}

/** Collects function names referenced via [FunctionCallExpression] in an expression tree. */
internal object FunctionRefCollector {
    fun collect(expression: Expression): Set<String> {
        val refs = mutableSetOf<String>()
        walk(expression, refs)
        return refs
    }

    private fun walk(expression: Expression, refs: MutableSet<String>) {
        if (expression is FunctionCallExpression) refs.add(expression.function.value)
        forEachChildExpression(expression) { walk(it, refs) }
    }
}
