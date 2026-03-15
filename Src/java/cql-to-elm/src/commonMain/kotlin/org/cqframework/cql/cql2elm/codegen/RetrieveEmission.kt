package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.model.ClassType
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Retrieve

/**
 * Emit a [RetrieveExpression] as an ELM [Retrieve]. Resolves the data type through the
 * [org.cqframework.cql.cql2elm.ModelManager] to determine the correct QName and templateId.
 */
internal fun EmissionContext.emitRetrieve(expression: RetrieveExpression): ElmExpression {
    return buildRetrieveForType(expression.typeSpecifier.name.simpleName)
}

/**
 * Build an ELM [Retrieve] node for a given type name, resolving the model URL and templateId. This
 * is shared by [emitRetrieve] (explicit retrieves) and implicit context expression definitions
 * (e.g., `Patient = SingletonFrom([Patient])`).
 */
internal fun EmissionContext.buildRetrieveForType(typeName: String): Retrieve {
    val model = resolveModelForType(typeName)
    val modelInfo = model.modelInfo
    val modelUrl = modelInfo.targetUrl ?: modelInfo.url!!

    val dataType = model.resolveTypeName(typeName)
    val classType = dataType as? ClassType

    return Retrieve().apply {
        this.dataType = QName(modelUrl, typeName)
        classType?.identifier?.let { templateId = it }
    }
}

/**
 * Resolve which model provides a given type name. Iterates through loaded model names to find the
 * model that can resolve the type.
 */
private fun EmissionContext.resolveModelForType(
    typeName: String
): org.cqframework.cql.cql2elm.model.Model {
    val mm =
        modelManager
            ?: throw ElmEmitter.UnsupportedNodeException(
                "RetrieveExpression requires a ModelManager to resolve type '$typeName'."
            )
    for (modelName in loadedModelNames) {
        val model = mm.resolveModel(modelName)
        if (model.resolveTypeName(typeName) != null) {
            return model
        }
    }
    throw ElmEmitter.UnsupportedNodeException(
        "Could not resolve type '$typeName' in any loaded model."
    )
}
