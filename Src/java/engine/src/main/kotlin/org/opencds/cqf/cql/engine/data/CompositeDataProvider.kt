package org.opencds.cqf.cql.engine.data

import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

open class CompositeDataProvider(
    protected var modelResolver: ModelResolver?,
    protected var retrieveProvider: RetrieveProvider?,
) : DataProvider {
    @Deprecated("Use packageNames instead")
    override var packageName: String?
        get() = this.modelResolver!!.packageName
        set(value) {
            this.modelResolver!!.packageName = value
        }

    override var packageNames: MutableList<String?>
        get() = this.modelResolver!!.packageNames
        set(value) {
            this.modelResolver!!.packageNames = value
        }

    override fun resolvePath(target: Any?, path: String?): Any? {
        return this.modelResolver!!.resolvePath(target, path)
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        return this.modelResolver!!.getContextPath(contextType, targetType)
    }

    override fun resolveType(typeName: String?): Class<*>? {
        return this.modelResolver!!.resolveType(typeName)
    }

    override fun resolveType(value: Any?): Class<*>? {
        return this.modelResolver!!.resolveType(value)
    }

    override fun `is`(value: Any?, type: Class<*>?): Boolean? {
        return this.modelResolver!!.`is`(value, type)
    }

    override fun `as`(value: Any?, type: Class<*>?, isStrict: Boolean): Any? {
        return this.modelResolver!!.`as`(value, type, isStrict)
    }

    override fun createInstance(typeName: String?): Any? {
        return this.modelResolver!!.createInstance(typeName)
    }

    override fun setValue(target: Any?, path: String?, value: Any?) {
        this.modelResolver!!.setValue(target, path, value)
    }

    override fun objectEqual(left: Any?, right: Any?): Boolean? {
        return this.modelResolver!!.objectEqual(left, right)
    }

    override fun objectEquivalent(left: Any?, right: Any?): Boolean? {
        return this.modelResolver!!.objectEquivalent(left, right)
    }

    override fun resolveId(target: Any?): String? {
        return this.modelResolver!!.resolveId(target)
    }

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
        return this.retrieveProvider!!.retrieve(
            context,
            contextPath,
            contextValue,
            dataType,
            templateId,
            codePath,
            codes,
            valueSet,
            datePath,
            dateLowPath,
            dateHighPath,
            dateRange,
        )
    }
}
