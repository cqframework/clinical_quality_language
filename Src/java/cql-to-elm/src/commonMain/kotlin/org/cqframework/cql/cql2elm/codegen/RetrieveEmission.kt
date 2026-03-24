package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.StringEscapeUtils.unescapeCql
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.model.ClassType
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ValueSetRef as ElmValueSetRef

/**
 * Emit a [RetrieveExpression] as an ELM [Retrieve]. Resolves the data type through the
 * [org.cqframework.cql.cql2elm.analysis.ModelContext] to determine the correct QName and
 * templateId.
 */
internal fun EmissionContext.emitRetrieve(expression: RetrieveExpression): ElmExpression {
    val retrieve = buildRetrieveForType(expression.typeSpecifier.name.simpleName)

    // Resolve codeProperty, codeComparator, and codes when a terminology restriction is present.
    val terminologyRestriction = expression.terminology
    if (terminologyRestriction != null) {
        val model = modelContext.resolveModelForType(expression.typeSpecifier.name.simpleName)
        val dataType = model.resolveTypeName(expression.typeSpecifier.name.simpleName)
        val classType = dataType as? ClassType
        if (classType?.primaryCodePath != null) {
            retrieve.codeProperty = classType.primaryCodePath
        }
        // Resolve the terminology reference from the symbol table.
        // RetrieveExpression is a leaf in ExpressionFold so its children are NOT pre-folded
        // by the TypeResolver. We resolve directly from the SymbolTable instead.
        val terminologyExpr = terminologyRestriction.terminology
        if (terminologyExpr is IdentifierExpression) {
            val name = terminologyExpr.name.simpleName
            // Value set reference: [Type: "MyValueSet"]
            semanticModel.resolveValueSet(name)?.let { resolution ->
                retrieve.codes =
                    ElmValueSetRef().withName(unescapeCql(resolution.definition.name.value)).apply {
                        preserve = true
                    }
            }
            // Code system reference: [Type: LOINC]
            if (retrieve.codes == null) {
                semanticModel.resolveCodeSystem(name)?.let { resolution ->
                    retrieve.codes =
                        org.hl7.elm.r1
                            .CodeSystemRef()
                            .withName(unescapeCql(resolution.definition.name.value))
                }
            }
            // Code reference: [Type: "my-code"] — single code uses Equivalent (~) + ToList
            if (retrieve.codes == null) {
                semanticModel.resolveCode(name)?.let { resolution ->
                    retrieve.codes =
                        org.hl7.elm.r1.ToList().apply {
                            operand =
                                org.hl7.elm.r1
                                    .CodeRef()
                                    .withName(unescapeCql(resolution.definition.name.value))
                        }
                }
            }
            // Concept reference — single concept uses Equivalent (~) + ToList
            if (retrieve.codes == null) {
                semanticModel.resolveConcept(name)?.let { resolution ->
                    retrieve.codes =
                        org.hl7.elm.r1.ToList().apply {
                            operand =
                                org.hl7.elm.r1
                                    .ConceptRef()
                                    .withName(unescapeCql(resolution.definition.name.value))
                        }
                }
            }
        }
        // Set codeComparator based on terminology type:
        // - Single Code/Concept wrapped in ToList: use "~" (Equivalent)
        // - ValueSet, CodeSystem, unresolved runtime terms: use "in"
        retrieve.codeComparator = if (retrieve.codes is org.hl7.elm.r1.ToList) "~" else "in"
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
