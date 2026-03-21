package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.model.ClassType
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Retrieve

/**
 * Emit a [RetrieveExpression] as an ELM [Retrieve]. Resolves the data type through the
 * [org.cqframework.cql.cql2elm.analysis.ModelContext] to determine the correct QName and
 * templateId.
 */
internal fun EmissionContext.emitRetrieve(expression: RetrieveExpression): ElmExpression {
    val retrieve = buildRetrieveForType(expression.typeSpecifier.name.simpleName)

    // Resolve codeProperty/codeComparator when a terminology restriction is present.
    // The terminology expression itself is emitted by the function definition's operand
    // binding, not inlined here — the Retrieve just needs the code path metadata.
    if (expression.terminology != null) {
        val model = modelContext.resolveModelForType(expression.typeSpecifier.name.simpleName)
        val dataType = model.resolveTypeName(expression.typeSpecifier.name.simpleName)
        val classType = dataType as? ClassType
        if (classType?.primaryCodePath != null) {
            retrieve.codeProperty = classType.primaryCodePath
            retrieve.codeComparator = "in"
        }
    }

    return retrieve
}

/**
 * Build an ELM [Retrieve] node for a given type name, resolving the model URL and templateId. This
 * is shared by [emitRetrieve] (explicit retrieves) and implicit context expression definitions
 * (e.g., `Patient = SingletonFrom([Patient])`).
 */
internal fun EmissionContext.buildRetrieveForType(typeName: String): Retrieve {
    val model = modelContext.resolveModelForType(typeName)
    val modelInfo = model.modelInfo
    val modelUrl = modelInfo.targetUrl ?: modelInfo.url!!

    val dataType = model.resolveTypeName(typeName)
    val classType = dataType as? ClassType

    return Retrieve().apply {
        this.dataType = QName(modelUrl, typeName)
        classType?.identifier?.let { templateId = it }
    }
}
