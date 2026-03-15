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
    val retrieve = Retrieve()
    val typeName = expression.typeSpecifier.name.simpleName
    val mm =
        modelManager
            ?: throw ElmEmitter.UnsupportedNodeException(
                "RetrieveExpression requires a ModelManager to resolve type '$typeName'."
            )

    // Find the model that defines this type by checking loaded models
    val model = resolveModelForType(typeName)
    val modelInfo = model.modelInfo
    val modelUrl = modelInfo.targetUrl ?: modelInfo.url!!

    // Resolve the type to get its ClassType identifier (templateId)
    val dataType = model.resolveTypeName(typeName)
    val classType = dataType as? ClassType

    retrieve.dataType = QName(modelUrl, typeName)
    classType?.identifier?.let { retrieve.templateId = it }

    return retrieve
}

/**
 * Resolve which model provides a given type name. Iterates through all using definitions to find
 * the model that can resolve the type name.
 */
private fun EmissionContext.resolveModelForType(
    typeName: String
): org.cqframework.cql.cql2elm.model.Model {
    val mm = modelManager!!
    // Try resolving from any loaded model. The model manager caches models after first load,
    // so we try well-known models in order.
    val modelNames = loadedModelNames
    for (modelName in modelNames) {
        val model = mm.resolveModel(modelName)
        if (model.resolveTypeName(typeName) != null) {
            return model
        }
    }
    throw ElmEmitter.UnsupportedNodeException(
        "Could not resolve type '$typeName' in any loaded model."
    )
}
