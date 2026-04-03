package org.opencds.cqf.cql.engine.data

import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.runtime.*

open class SystemDataProvider : DataProvider {
    override fun retrieve(
        context: String?,
        contextPath: String?,
        contextValue: Any?,
        dataType: String,
        templateId: String?,
        codePath: String?,
        codes: Iterable<Code>?,
        valueSet: String?,
        datePath: String?,
        dateLowPath: String?,
        dateHighPath: String?,
        dateRange: Interval?,
    ): Iterable<Any?>? {
        throw IllegalArgumentException("SystemDataProvider does not support retrieval.")
    }

    /**
     * Returns true if:
     * - `type` is System._valueType_ (exact type name match), or
     * - `type` is System.Any (System.Any is a supertype of all types), or
     * - value type is System.ValueSet/System.CodeSystem and type is System.Vocabulary (ValueSet and
     *   CodeSystem are the only types in the system model that don't immediately extend
     *   System.Any).
     */
    override fun `is`(valueType: String, type: QName): Boolean {
        return type == QName(systemModelNamespaceUri, valueType) ||
            type == anyTypeName ||
            (valueType == valueSetTypeName.getLocalPart() ||
                valueType == codeSystemTypeName.getLocalPart()) && type == vocabularyTypeName
    }

    override fun createInstance(typeName: String?): Any? {
        return when (typeName) {
            "Quantity" -> Quantity()
            "Ratio" -> Ratio()
            "Code" -> Code()
            "Concept" -> Concept()
            "CodeSystem" -> CodeSystem()
            "ValueSet" -> ValueSet()
            else -> throw IllegalArgumentException("Could not create an instance of $typeName.")
        }
    }

    override fun resolveId(target: Any?): String? {
        return null
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        return null
    }
}
