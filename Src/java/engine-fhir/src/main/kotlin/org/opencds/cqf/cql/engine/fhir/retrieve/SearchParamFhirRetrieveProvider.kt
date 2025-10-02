package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

abstract class SearchParamFhirRetrieveProvider
protected constructor(
    val searchParameterResolver: SearchParameterResolver,
    val modelResolver: ModelResolver,
) : TerminologyAwareRetrieveProvider() {
    protected val fhirContext: FhirContext
        get() = searchParameterResolver.fhirContext

    var fhirQueryGenerator: BaseFhirQueryGenerator? = null

    var pageSize: Int? = null
        set(value) {
            require(value == null || value > 0) { "value must be a non-null integer > 0" }
            field = value
        }

    var maxCodesPerQuery: Int? = null
        set(value) {
            require(value != null && value >= 1) { "value must be a non-null integer > 0" }
            field = value
        }

    var queryBatchThreshold: Int? = null
        set(value) {
            require(value != null && value >= 1) { "value must be a non-null integer > 0" }
            field = value
        }

    protected abstract fun executeQueries(
        dataType: String,
        queries: MutableList<SearchParameterMap>,
    ): Iterable<Any?>?

    override fun retrieve(
        context: String?,
        contextPath: String?,
        contextValue: Any?,
        dataType: String?,
        templateId: String?,
        codePath: String?,
        codes: Iterable<Code>?,
        valueSet: String?,
        datePath: String?,
        dateLowPath: String?,
        dateHighPath: String?,
        dateRange: Interval?,
    ): Iterable<Any?>? {

        try {
            if (this.fhirContext.version.version == FhirVersionEnum.DSTU3) {
                fhirQueryGenerator =
                    Dstu3FhirQueryGenerator(
                        searchParameterResolver,
                        terminologyProvider,
                        this.modelResolver,
                    )
            } else if (this.fhirContext.version.version == FhirVersionEnum.R4) {
                fhirQueryGenerator =
                    R4FhirQueryGenerator(
                        searchParameterResolver,
                        terminologyProvider,
                        this.modelResolver,
                    )
            }
        } catch (exception: FhirVersionMisMatchException) {
            throw RuntimeException(exception.message)
        }
        fhirQueryGenerator!!.isExpandValueSets = isExpandValueSets

        if (maxCodesPerQuery != null) {
            fhirQueryGenerator!!.maxCodesPerQuery = maxCodesPerQuery
        }
        if (queryBatchThreshold != null) {
            fhirQueryGenerator!!.queryBatchThreshold = queryBatchThreshold
        }
        if (pageSize != null) {
            fhirQueryGenerator!!.pageSize = pageSize
        }

        val queries =
            fhirQueryGenerator!!.setupQueries(
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

        return this.executeQueries(dataType!!, queries!!)
    }
}
