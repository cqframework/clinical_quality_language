package org.opencds.cqf.cql.engine.model

import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class CachingModelResolverDecorator(val innerResolver: ModelResolver) : ModelResolver {
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
                perPackageContextResolutions.computeIfAbsent(pn) { p ->
                    ConcurrentHashMap<String, MutableMap<String?, Optional<Any>>>()
                }

            val contextTypeResolutions =
                packageContextResolutions.computeIfAbsent(contextType) {
                    ConcurrentHashMap<String?, Optional<Any>>()
                }

            val result =
                contextTypeResolutions.computeIfAbsent(targetType) { t ->
                    Optional.ofNullable(this.innerResolver.getContextPath(contextType, t))
                }

            if (result.isPresent) {
                return result.get()
            }
        }

        return null
    }

    override fun resolveType(typeName: String?): Class<*>? {
        if (typeName == null) {
            return null
        }

        for (pn in this.packageNames) {
            val packageTypeResolutions =
                perPackageTypeResolutionsByTypeName.computeIfAbsent(pn) {
                    ConcurrentHashMap<String, Optional<Class<*>>>()
                }

            val result =
                packageTypeResolutions.computeIfAbsent(typeName) { t ->
                    Optional.ofNullable(this.innerResolver.resolveType(t))
                }

            if (result.isPresent) {
                return result.get()
            }
        }

        return null
    }

    override fun resolveType(value: Any?): Class<*>? {
        if (value == null) {
            return null
        }

        val valueClass = value.javaClass
        for (pn in this.packageNames) {
            val packageTypeResolutions =
                perPackageTypeResolutionsByClass.computeIfAbsent(pn) { p ->
                    ConcurrentHashMap<Class<*>, Optional<Class<*>>>()
                }

            val result =
                packageTypeResolutions.computeIfAbsent(valueClass) { t ->
                    Optional.ofNullable(this.innerResolver.resolveType(value))
                }

            if (result.isPresent) {
                return result.get()
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

    override fun `is`(value: Any?, type: Class<*>?): Boolean? {
        return this.innerResolver.`is`(value, type)
    }

    override fun `as`(value: Any?, type: Class<*>?, isStrict: Boolean): Any? {
        return this.innerResolver.`as`(value, type, isStrict)
    }

    companion object {
        private val perPackageContextResolutions =
            ConcurrentHashMap<String?, MutableMap<String, MutableMap<String?, Optional<Any>>>>()
        private val perPackageTypeResolutionsByTypeName =
            ConcurrentHashMap<String?, MutableMap<String, Optional<Class<*>>>>()
        private val perPackageTypeResolutionsByClass =
            ConcurrentHashMap<String?, MutableMap<Class<*>, Optional<Class<*>>>>()
    }
}
