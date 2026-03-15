package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Definition
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.ExternalFunctionBody
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.UsingDefinition
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.UsingDef

internal fun EmissionContext.emitUsings(definitions: List<Definition>): List<UsingDef> {
    val usingDefs =
        definitions.mapNotNull { definition ->
            when (definition) {
                is UsingDefinition -> emitUsing(definition)
                else -> null
            }
        }
    // The legacy translator always emits the System using first. When the CQL has non-System
    // usings (e.g., FHIR) without an explicit `using System`, we prepend the implicit one.
    val hasSystem = usingDefs.any { it.localIdentifier == "System" }
    val hasNonSystem = usingDefs.any { it.localIdentifier != "System" }
    if (hasNonSystem && !hasSystem) {
        val systemUsing = UsingDef()
        systemUsing.localIdentifier = "System"
        systemUsing.uri = typesNamespace
        return listOf(systemUsing) + usingDefs
    }
    return usingDefs
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
            else -> {
                val mm = modelManager
                if (mm != null) {
                    val model =
                        mm.resolveModel(ModelIdentifier(modelName, null, definition.version?.value))
                    loadedModelNames.add(modelName)
                    model.modelInfo.url
                } else {
                    throw ElmEmitter.UnsupportedNodeException(
                        "Model '$modelName' requires a ModelManager."
                    )
                }
            }
        }
    return usingDef
}

/** Emit [ContextDef] nodes for context definitions that can be resolved against loaded models. */
internal fun EmissionContext.emitContextDefs(
    statements: List<Statement>,
    definitions: List<Definition>,
): List<ContextDef> {
    if (modelManager == null) return emptyList()
    // Check if there are any non-System using definitions (i.e., real models loaded)
    val hasNonSystemUsings =
        definitions.any { it is UsingDefinition && it.modelIdentifier.simpleName != "System" }
    if (!hasNonSystemUsings) return emptyList()
    return statements.filterIsInstance<ContextDefinition>().map { ctx ->
        ContextDef().withName(ctx.context.value)
    }
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
