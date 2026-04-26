package org.opencds.cqf.cql.engine.model

import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.util.createConcurrentHashMap

open class CachingModelResolverDecorator(val innerResolver: ModelResolver) : ModelResolver {

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
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

    override fun createInstance(typeName: String?): Any? {
        return this.innerResolver.createInstance(typeName)
    }

    override fun resolveId(target: Any?): String? {
        return innerResolver.resolveId(target)
    }

    override fun `is`(valueType: String, type: QName): Boolean? {
        return this.innerResolver.`is`(valueType, type)
    }

    companion object {
        private val contextResolutions =
            createConcurrentHashMap<String, MutableMap<String?, Any?>>()
    }
}
