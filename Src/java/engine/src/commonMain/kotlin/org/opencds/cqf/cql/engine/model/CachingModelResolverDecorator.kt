package org.opencds.cqf.cql.engine.model

import kotlin.reflect.KClass
import org.opencds.cqf.cql.engine.util.createConcurrentHashMap

open class CachingModelResolverDecorator(val innerResolver: BaseModelResolver) : BaseModelResolver {
    @Suppress("deprecation")
    @Deprecated("use packageNames instead")
    override var packageName: String?
        get() = this.innerResolver.packageName
        set(value) {
            this.innerResolver.packageName = value
        }

    override fun resolvePath(target: Any?, path: String?): Any? {
        return this.innerResolver.resolvePath(target, path)
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        if (contextType == null) {
            return null
        }

        for (pn in this.packageNames) {
            val packageContextResolutions =
                perPackageContextResolutions.getOrPut(pn) { createConcurrentHashMap() }

            val contextTypeResolutions =
                packageContextResolutions.getOrPut(contextType) { createConcurrentHashMap() }

            val result =
                contextTypeResolutions.getOrPut(targetType) {
                    this.innerResolver.getContextPath(contextType, targetType)
                }

            if (result != null) {
                return result
            }
        }

        return null
    }

    override fun resolveKType(typeName: String?): KClass<*>? {
        if (typeName == null) {
            return null
        }

        for (pn in this.packageNames) {
            val packageTypeResolutions =
                perPackageTypeResolutionsByTypeName.getOrPut(pn) { createConcurrentHashMap() }

            val result =
                packageTypeResolutions.getOrPut(typeName) {
                    this.innerResolver.resolveKType(typeName)
                }

            if (result != null) {
                return result
            }
        }

        return null
    }

    override fun resolveKType(value: Any?): KClass<*>? {
        if (value == null) {
            return null
        }

        val valueClass = value::class
        for (pn in this.packageNames) {
            val packageTypeResolutions =
                perPackageTypeResolutionsByClass.getOrPut(pn) { createConcurrentHashMap() }

            val result =
                packageTypeResolutions.getOrPut(valueClass) {
                    this.innerResolver.resolveKType(value)
                }

            if (result != null) {
                return result
            }
        }

        return null
    }

    override fun createInstance(typeName: String?): Any? {
        return this.innerResolver.createInstance(typeName)
    }

    override fun setValue(target: Any?, path: String?, value: Any?) {
        this.innerResolver.setValue(target, path, value)
    }

    override fun objectEqual(left: Any?, right: Any?): Boolean? {
        return this.innerResolver.objectEqual(left, right)
    }

    override fun objectEquivalent(left: Any?, right: Any?): Boolean? {
        return this.innerResolver.objectEquivalent(left, right)
    }

    override fun resolveId(target: Any?): String? {
        return innerResolver.resolveId(target)
    }

    override fun `is`(value: Any?, type: KClass<*>?): Boolean? {
        return this.innerResolver.`is`(value, type)
    }

    override fun `as`(value: Any?, type: KClass<*>?, isStrict: Boolean): Any? {
        return this.innerResolver.`as`(value, type, isStrict)
    }

    companion object {
        private val perPackageContextResolutions =
            createConcurrentHashMap<String?, MutableMap<String, MutableMap<String?, Any?>>>()
        private val perPackageTypeResolutionsByTypeName =
            createConcurrentHashMap<String?, MutableMap<String, KClass<*>?>>()
        private val perPackageTypeResolutionsByClass =
            createConcurrentHashMap<String?, MutableMap<KClass<*>, KClass<*>?>>()
    }
}
