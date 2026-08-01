package org.opencds.cqf.cql.engine.data

import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value

@JsOnlyExport
open class CompositeDataProvider(
    protected var modelResolver: ModelResolver?,
    protected var retrieveProvider: RetrieveProvider?,
) : DataProvider {
    override fun getContextPath(
        contextType: kotlin.String?,
        targetType: kotlin.String?,
    ): kotlin.String? {
        return this.modelResolver!!.getContextPath(contextType, targetType)
    }

    override fun `is`(valueType: kotlin.String, type: QName): kotlin.Boolean? {
        return this.modelResolver!!.`is`(valueType, type)
    }

    override fun createInstance(typeName: kotlin.String?): Value? {
        return this.modelResolver!!.createInstance(typeName)
    }

    override fun objectEquivalent(
        left: ClassInstance,
        right: ClassInstance,
        equivalent: (l: Value?, r: Value?) -> Boolean,
    ): Boolean {
        return this.modelResolver!!.objectEquivalent(left, right, equivalent)
    }

    override fun resolveId(target: Value?): kotlin.String? {
        return this.modelResolver!!.resolveId(target)
    }

    @Suppress("NON_EXPORTABLE_TYPE")
    override fun retrieve(
        context: kotlin.String?,
        contextPath: kotlin.String?,
        contextValue: kotlin.String?,
        dataType: kotlin.String,
        templateId: kotlin.String?,
        codePath: kotlin.String?,
        codes: Iterable<Code>?,
        valueSet: kotlin.String?,
        datePath: kotlin.String?,
        dateLowPath: kotlin.String?,
        dateHighPath: kotlin.String?,
        dateRange: Interval?,
    ): Iterable<Value?>? {
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
