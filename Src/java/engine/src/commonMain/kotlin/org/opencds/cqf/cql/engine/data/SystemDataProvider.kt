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

    override fun `is`(valueType: String, type: QName): Boolean? {
        return type.getNamespaceURI() == "urn:hl7-org:elm-types:r1" &&
            type.getLocalPart() == valueType ||
            valueType == "Any" ||
            type == QName("urn:hl7-org:elm-types:r1", "Any")
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
