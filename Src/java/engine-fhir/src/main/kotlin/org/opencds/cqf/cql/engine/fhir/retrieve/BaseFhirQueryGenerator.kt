package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.api.RestSearchParameterTypeEnum
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import ca.uhn.fhir.rest.param.TokenOrListParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.param.TokenParamModifier
import java.util.*
import org.apache.commons.lang3.tuple.Pair
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.instance.model.api.ICompositeType
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

abstract class BaseFhirQueryGenerator
protected constructor(
    protected var searchParameterResolver: SearchParameterResolver,
    protected var terminologyProvider: TerminologyProvider?,
    protected var modelResolver: ModelResolver,
    fhirContext: FhirContext,
) : FhirVersionIntegrityChecker {
    protected var fhirContext: FhirContext

    var pageSize: Int? = null
        set(value) {
            require(value != null && value > 0) { "value must be a non-null integer > 0" }
            field = value
        }

    var maxCodesPerQuery: Int? = null
        set(value) {
            require(value != null && value > 0) { "value must be non-null integer > 0" }
            field = value
        }

    var queryBatchThreshold: Int? = null
        set(value) {
            require(value != null && value > 0) { "value must be non-null integer > 0" }
            field = value
        }

    // TODO: Think about how to best handle the decision to expand value sets... Should it be part
    // of the
    // terminology provider if it detects support for "code:in"? How does that feed back to the
    // retriever?
    var isExpandValueSets: Boolean = false

    init {
        this.isExpandValueSets = DEFAULT_SHOULD_EXPAND_VALUESETS

        this.fhirContext = fhirContext
        validateFhirVersionIntegrity(fetchFhirVersionEnum(this.fhirContext))
    }

    @Throws(FhirVersionMisMatchException::class)
    override fun validateFhirVersionIntegrity(fhirVersion: FhirVersionEnum) {
        require(fetchFhirVersionEnum(this.searchParameterResolver.fhirContext) == fhirVersion)
    }

    fun fetchFhirVersionEnum(fhirContext: FhirContext): FhirVersionEnum {
        return fhirContext.version.version
    }

    abstract fun generateFhirQueries(
        dataRequirement: ICompositeType?,
        evaluationDateTime: DateTime?,
        contextValues: MutableMap<String, Any?>?,
        parameters: MutableMap<String, Any?>?,
        capabilityStatement: IBaseConformance?,
    ): MutableList<String>

    protected fun getTemplateParam(
        dataType: String?,
        templateId: String?,
    ): Pair<String, IQueryParameterType>? {
        if (templateId == null || templateId == "") {
            return null
        }

        // Do something?
        return null
    }

    fun getDateRangeParam(
        dataType: String?,
        datePath: String?,
        dateLowPath: String?,
        dateHighPath: String?,
        dateRange: Interval?,
    ): Pair<String, DateRangeParam>? {
        if (dateRange == null) {
            return null
        }

        var low: DateParam? = null
        var high: DateParam? = null
        if (dateRange.low != null) {
            if (dateRange.low is Date) {
                low =
                    DateParam(
                        ParamPrefixEnum.GREATERTHAN_OR_EQUALS,
                        Date.from((dateRange.low as Date).toInstant()),
                    )
            } else if (dateRange.low is DateTime) {
                low =
                    DateParam(
                        ParamPrefixEnum.GREATERTHAN_OR_EQUALS,
                        Date.from((dateRange.low as DateTime).dateTime!!.toInstant()),
                    )
            }
        }

        if (dateRange.high != null) {
            if (dateRange.low is Date) {
                high =
                    DateParam(
                        ParamPrefixEnum.LESSTHAN_OR_EQUALS,
                        Date.from((dateRange.high as Date).toInstant()),
                    )
            } else if (dateRange.low is DateTime) {
                high =
                    DateParam(
                        ParamPrefixEnum.LESSTHAN_OR_EQUALS,
                        Date.from((dateRange.high as DateTime).dateTime!!.toInstant()),
                    )
            }
        }
        val rangeParam =
            if (high == null && low != null) {
                DateRangeParam(low)
            } else {
                DateRangeParam(low, high)
            }

        val dateParam =
            this.searchParameterResolver.getSearchParameterDefinition(
                dataType,
                datePath,
                RestSearchParameterTypeEnum.DATE,
            )

        if (dateParam == null) {
            throw UnsupportedOperationException(
                "Could not resolve a search parameter with date type for $dataType.$datePath "
            )
        }

        return Pair.of<String, DateRangeParam>(dateParam.name, rangeParam)
    }

    protected fun getContextParam(
        dataType: String?,
        context: String?,
        contextPath: String?,
        contextValue: Any?,
    ): Pair<String, IQueryParameterType>? {
        if (
            context != null && context == "Patient" && contextValue != null && contextPath != null
        ) {
            return this.searchParameterResolver.createSearchParameter(
                context,
                dataType,
                contextPath,
                contextValue as String,
            )
        }

        return null
    }

    protected fun getCodeParams(
        dataType: String?,
        codePath: String?,
        codes: Iterable<Code?>?,
        valueSet: String?,
    ): Pair<String, MutableList<TokenOrListParam>>? {
        var valueSet = valueSet
        if (valueSet != null && valueSet.startsWith("urn:oid:")) {
            valueSet = valueSet.replace("urn:oid:", "")
        }

        require(!(codePath == null && (codes != null || valueSet != null))) {
            "A code path must be provided when filtering on codes or a valueset."
        }

        if (codePath == null || codePath.isEmpty()) {
            return null
        }

        // TODO: This assumes the code path will always be a token param.
        val codeParamLists = this.getCodeParams(codes, valueSet)
        if (codeParamLists.isEmpty()) {
            return null
        }

        val codeParam =
            this.searchParameterResolver.getSearchParameterDefinition(
                dataType,
                codePath,
                RestSearchParameterTypeEnum.TOKEN,
            )

        if (codeParam == null) {
            return null
        }

        return Pair.of<String, MutableList<TokenOrListParam>>(codeParam.name, codeParamLists)
    }

    // The code params will be either the literal set of codes in the event the data server doesn't
    // have the referenced
    // ValueSet (or doesn't support pulling and caching a ValueSet). If the target server DOES
    // support that then it's
    // "dataType.codePath in ValueSet"
    protected fun getCodeParams(
        codes: Iterable<Code?>?,
        valueSet: String?,
    ): MutableList<TokenOrListParam> {
        var codes = codes
        if (valueSet != null) {
            if (!this.isExpandValueSets) {
                return mutableListOf(
                    TokenOrListParam()
                        .addOr(TokenParam(valueSet).setModifier(TokenParamModifier.IN))
                )
            } else {
                requireNotNull(terminologyProvider) {
                    "Expand value sets cannot be used without a terminology provider and no terminology provider is set."
                }
                val valueSetInfo = ValueSetInfo().withId(valueSet)
                codes = terminologyProvider!!.expand(valueSetInfo)
            }
        }

        if (codes == null) {
            return mutableListOf()
        }

        // If the number of queries generated as a result of the code list size and maxCodesPerQuery
        // constraint would
        // result in a number of queries that is greater than the queryBatchThreshold value, then
        // codeParams will
        // be omitted altogether.
        if (this.maxCodesPerQuery != null && this.queryBatchThreshold != null) {
            if (
                (getIterableSize(codes) / this.maxCodesPerQuery!!.toFloat()) >
                    this.queryBatchThreshold!!
            ) {
                return mutableListOf()
            }
        }

        val codeParamsList: MutableList<TokenOrListParam> = mutableListOf()
        var codeParams: TokenOrListParam? = null
        var codeCount = 0
        for (code in codes) {
            if (this.maxCodesPerQuery == null) {
                if (codeCount == 0 && codeParams == null) {
                    codeParams = TokenOrListParam()
                }
            } else if (codeCount % this.maxCodesPerQuery!! == 0) {
                if (codeParams != null) {
                    codeParamsList.add(codeParams)
                }

                codeParams = TokenOrListParam()
            }

            codeCount++
            // We have a couple cases where types are erased and code
            // is secretly a String, which the compiler can't detect
            // We need to fix that in the corresponding places.
            @Suppress("USELESS_IS_CHECK")
            if (code is Code) {
                codeParams!!.addOr(TokenParam(code.system, code.code))
            } else if (code is String) {
                codeParams!!.addOr(TokenParam(code))
            }
        }

        if (codeParams != null) {
            codeParamsList.add(codeParams)
        }

        return codeParamsList
    }

    protected fun setupQueries(
        context: String?,
        contextPath: String?,
        contextValue: Any?,
        dataType: String?,
        templateId: String?,
        codeFilters: MutableList<CodeFilter>?,
        dateFilters: MutableList<DateFilter>?,
    ): MutableList<SearchParameterMap> {
        val templateParam = this.getTemplateParam(dataType, templateId)

        val contextParam = this.getContextParam(dataType, context, contextPath, contextValue)

        val dateRangeParams: MutableList<Pair<String, DateRangeParam>> = mutableListOf()
        if (dateFilters != null) {
            for (df in dateFilters) {
                val dateRangeParam =
                    this.getDateRangeParam(
                        dataType,
                        df.datePath,
                        df.dateLowPath,
                        df.dateHighPath,
                        df.dateRange,
                    )
                if (dateRangeParam != null) {
                    dateRangeParams.add(dateRangeParam)
                }
            }
        }

        val codeParamList: MutableList<Pair<String, MutableList<TokenOrListParam>>> =
            ArrayList<Pair<String, MutableList<TokenOrListParam>>>()
        if (codeFilters != null) {
            for (cf in codeFilters) {
                val codeParams = this.getCodeParams(dataType, cf.codePath, cf.codes, cf.valueSet)

                if (codeParams != null) {
                    codeParamList.add(codeParams)
                }
            }
        }

        return this.innerSetupQueries(templateParam, contextParam, dateRangeParams, codeParamList)
    }

    fun setupQueries(
        context: String?,
        contextPath: String?,
        contextValue: Any?,
        dataType: String?,
        templateId: String?,
        codePath: String?,
        codes: Iterable<Code?>?,
        valueSet: String?,
        datePath: String?,
        dateLowPath: String?,
        dateHighPath: String?,
        dateRange: Interval?,
    ): MutableList<SearchParameterMap> {
        return setupQueries(
            context,
            contextPath,
            contextValue,
            dataType,
            templateId,
            if (codePath != null) mutableListOf(CodeFilter(codePath, codes, valueSet)) else null,
            if (datePath != null || dateLowPath != null || dateHighPath != null)
                mutableListOf(DateFilter(datePath, dateLowPath, dateHighPath, dateRange))
            else null,
        )
    }

    protected fun innerSetupQueries(
        templateParam: Pair<String, IQueryParameterType>?,
        contextParam: Pair<String, IQueryParameterType>?,
        dateRangeParams: MutableList<Pair<String, DateRangeParam>>?,
        codeParams: MutableList<Pair<String, MutableList<TokenOrListParam>>>?,
    ): MutableList<SearchParameterMap> {
        if (codeParams == null || codeParams.isEmpty()) {
            return mutableListOf<SearchParameterMap>(
                this.getBaseMap(templateParam, contextParam, dateRangeParams, codeParams)
            )
        }

        var chunkedCodeParam: Pair<String, MutableList<TokenOrListParam>>? = null
        for (cp in codeParams) {
            if (cp.value != null && cp.value!!.size > 1) {
                require(chunkedCodeParam == null) {
                    "Cannot evaluate multiple chunked code filters on ${chunkedCodeParam!!.key} and ${cp.key}"
                }
                chunkedCodeParam = cp
            }
        }

        if (chunkedCodeParam == null) {
            return mutableListOf(
                this.getBaseMap(templateParam, contextParam, dateRangeParams, codeParams)
            )
        }

        val maps: MutableList<SearchParameterMap> = mutableListOf()
        for (t in chunkedCodeParam.value!!) {
            val base = this.getBaseMap(templateParam, contextParam, dateRangeParams, codeParams)
            base.add(chunkedCodeParam.key!!, t)
            maps.add(base)
        }

        return maps
    }

    fun getBaseMap(
        templateParam: Pair<String, IQueryParameterType>?,
        contextParam: Pair<String, IQueryParameterType>?,
        dateRangeParams: MutableList<Pair<String, DateRangeParam>>?,
        codeParams: MutableList<Pair<String, MutableList<TokenOrListParam>>>?,
    ): SearchParameterMap {
        val baseMap = SearchParameterMap()
        baseMap.lastUpdated = DateRangeParam()

        if (this.pageSize != null) {
            baseMap.count = pageSize
        }

        if (templateParam != null) {
            baseMap.add(templateParam.key!!, templateParam.value)
        }

        if (dateRangeParams != null) {
            for (drp in dateRangeParams) {
                baseMap.add(drp.key!!, drp.value)
            }
        }

        if (codeParams != null) {
            for (cp in codeParams) {
                if (cp.value == null || cp.value!!.isEmpty() || cp.value!!.size > 1) {
                    // NOTE: Ignores "chunked" code parameters so they don't have to be removed
                    continue
                }
                baseMap.add(cp.key!!, cp.value!![0])
            }
        }

        if (contextParam != null) {
            baseMap.add(contextParam.key!!, contextParam.value)
        }

        return baseMap
    }

    companion object {
        protected const val DEFAULT_SHOULD_EXPAND_VALUESETS: Boolean = false

        private fun getIterableSize(iterable: Iterable<*>): Int {
            if (iterable is MutableCollection<*>) {
                return iterable.size
            }

            var counter = 0
            for (i in iterable) {
                counter++
            }
            return counter
        }
    }
}
