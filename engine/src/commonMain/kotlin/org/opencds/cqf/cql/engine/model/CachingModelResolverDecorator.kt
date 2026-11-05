package org.opencds.cqf.cql.engine.model

import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.createConcurrentHashMap

open class CachingModelResolverDecorator(val innerResolver: ModelResolver) : ModelResolver {

    override fun getContextPath(
        contextType: kotlin.String?,
        targetType: kotlin.String?,
    ): kotlin.String? {
        if (contextType == null) {
            return null
        }

        val contextTypeResolutions =
            contextResolutions.getOrPut(contextType) { createConcurrentHashMap() }

        val cached = contextTypeResolutions[targetType]
        if (cached != null) {
            return cached
        }

        val result = this.innerResolver.getContextPath(contextType, targetType)
        if (result != null) {
            contextTypeResolutions[targetType] = result
            return result
        }

        return null
    }

    override fun createInstance(typeName: kotlin.String?): Value? {
        return this.innerResolver.createInstance(typeName)
    }

    override fun objectEquivalent(
        left: ClassInstance,
        right: ClassInstance,
        equivalent: (l: Value?, r: Value?) -> Boolean,
    ): Boolean {
        return this.innerResolver.objectEquivalent(left, right, equivalent)
    }

    override fun resolveId(target: Value?): kotlin.String? {
        return innerResolver.resolveId(target)
    }

    override fun `is`(valueType: kotlin.String, type: QName): kotlin.Boolean? {
        return this.innerResolver.`is`(valueType, type)
    }

    companion object {
        private val contextResolutions =
            createConcurrentHashMap<kotlin.String, MutableMap<kotlin.String?, kotlin.String?>>()
    }
}
