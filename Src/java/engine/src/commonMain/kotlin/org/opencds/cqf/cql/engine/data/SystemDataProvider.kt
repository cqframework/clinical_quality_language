package org.opencds.cqf.cql.engine.data

import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.anyTypeName
import org.opencds.cqf.cql.engine.runtime.codeSystemTypeName
import org.opencds.cqf.cql.engine.runtime.systemModelNamespaceUri
import org.opencds.cqf.cql.engine.runtime.valueSetTypeName
import org.opencds.cqf.cql.engine.runtime.vocabularyTypeName

open class SystemDataProvider : DataProvider {
    override fun retrieve(
        context: kotlin.String?,
        contextPath: kotlin.String?,
        contextValue: Any?,
        dataType: kotlin.String,
        templateId: kotlin.String?,
        codePath: kotlin.String?,
        codes: Iterable<Code>?,
        valueSet: kotlin.String?,
        datePath: kotlin.String?,
        dateLowPath: kotlin.String?,
        dateHighPath: kotlin.String?,
        dateRange: Interval?,
    ): Iterable<CqlType?>? {
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

    override fun createInstance(typeName: String?): CqlType? {
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

    override fun resolveId(target: CqlType?): String? {
        return null
    }

    override fun getContextPath(contextType: String?, targetType: String?): String? {
        return null
    }
}
