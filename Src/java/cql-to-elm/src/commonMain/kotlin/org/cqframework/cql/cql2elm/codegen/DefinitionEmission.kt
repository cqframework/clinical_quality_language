package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.AstWalker
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Definition
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.ExternalFunctionBody
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.UnsupportedStatement
import org.hl7.cql.ast.UsingDefinition
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.OperandDef
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
    // Emit parameterTypeSpecifier for declared type
    definition.type?.let { typeSpec ->
        paramDef.parameterTypeSpecifier = emitTypeSpecifier(typeSpec)
    }
    return paramDef
}

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
private object IdentifierRefCollector {
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

/** Emit a [FunctionDefinition] as an ELM [FunctionDef]. */
internal fun EmissionContext.emitFunctionDefinition(
    definition: FunctionDefinition,
    currentContext: String?,
): FunctionDef {
    val functionDef = FunctionDef()
    functionDef.name = definition.name.value
    functionDef.accessLevel = ElmAccessModifier.PUBLIC
    definition.access?.let { access ->
        functionDef.accessLevel =
            when (access) {
                AstAccessModifier.PUBLIC -> ElmAccessModifier.PUBLIC
                AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
            }
    }
    currentContext?.let { functionDef.context = it }
    if (definition.fluent) {
        functionDef.fluent = true
    }

    // Emit operand definitions
    for (operand in definition.operands) {
        val operandDef = OperandDef()
        operandDef.name = operand.name.value
        val typeSpec = operand.type
        operandDef.operandTypeSpecifier = emitTypeSpecifier(typeSpec)
        // Set result type on operand from registry
        val resolvedType =
            operatorRegistry.type((typeSpec as? NamedTypeSpecifier)?.name?.simpleName ?: "")
        if (resolvedType != null) {
            decorate(operandDef, resolvedType)
        }
        functionDef.operand.add(operandDef)
    }

    // Emit body
    when (val body = definition.body) {
        is ExpressionFunctionBody -> {
            functionDef.expression = emitExpression(body.expression)
            // Set result type from body expression
            val bodyType = typeTable[body.expression]
            if (bodyType != null) {
                decorate(functionDef, bodyType)
            }
        }
        is ExternalFunctionBody -> {
            functionDef.external = true
            // For external functions, set result type from declared return type
            definition.returnType?.let { typeSpec ->
                if (typeSpec is NamedTypeSpecifier) {
                    val resolvedType = operatorRegistry.type(typeSpec.name.simpleName)
                    if (resolvedType != null) {
                        decorate(functionDef, resolvedType)
                    }
                }
            }
        }
    }

    return functionDef
}
