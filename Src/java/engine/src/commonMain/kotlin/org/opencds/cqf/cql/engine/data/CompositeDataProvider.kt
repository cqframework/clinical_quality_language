package org.opencds.cqf.cql.engine.data

import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

open class CompositeDataProvider(
    protected var modelResolver: ModelResolver?,
    protected var retrieveProvider: RetrieveProvider?,
) : DataProvider {
    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        return this.modelResolver!!.getContextPath(contextType, targetType)
    }

    override fun `is`(valueType: String, type: QName): Boolean? {
        return this.modelResolver!!.`is`(valueType, type)
    }

    override fun createInstance(typeName: String?): Any? {
        return this.modelResolver!!.createInstance(typeName)
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
