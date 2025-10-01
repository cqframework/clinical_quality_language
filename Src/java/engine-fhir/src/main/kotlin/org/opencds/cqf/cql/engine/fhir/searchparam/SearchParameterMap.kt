package org.opencds.cqf.cql.engine.fhir.searchparam

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.api.IQueryParameterAnd
import ca.uhn.fhir.model.api.IQueryParameterOr
import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.model.api.Include
import ca.uhn.fhir.rest.api.Constants
import ca.uhn.fhir.rest.api.SearchTotalModeEnum
import ca.uhn.fhir.rest.api.SortOrderEnum
import ca.uhn.fhir.rest.api.SortSpec
import ca.uhn.fhir.rest.api.SummaryEnum
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.QuantityParam
import ca.uhn.fhir.util.UrlUtil
import java.io.Serializable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.Validate
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// This class was copied from package ca.uhn.fhir.jpa.searchparam.SearchParameterMap
// It included many unnecessary HAPI FHIR server dependencies.
// Justification for these suppressions: this is a copy of hapi-fhir code.
class SearchParameterMap : Serializable {
    private val mySearchParameterMap:
        MutableMap<String, MutableList<MutableList<IQueryParameterType>>> =
        mutableMapOf()

    var count: Int? = null
    var everythingMode: EverythingModeEnum? = null
    private var myIncludes: MutableSet<Include>? = null
    private var myLastUpdated: DateRangeParam? = null
    var isLoadSynchronous: Boolean = false
        private set

    var loadSynchronousUpTo: Int? = null
        private set

    private var myRevIncludes: MutableSet<Include>? = null
    var sort: SortSpec? = null
    var summaryMode: SummaryEnum? = null
    var searchTotalMode: SearchTotalModeEnum? = null

    constructor() : super()

    constructor(theName: String, theParam: IQueryParameterType?) {
        add(theName, theParam)
    }

    fun add(theName: String, theDateParam: DateParam?): SearchParameterMap {
        add(theName, theDateParam as IQueryParameterOr<*>?)
        return this
    }

    fun add(theName: String, theAnd: IQueryParameterAnd<*>?) {
        if (theAnd == null) {
            return
        }
        if (!containsKey(theName)) {
            put(theName, mutableListOf())
        }

        for (next in theAnd.getValuesAsQueryTokens()) {
            if (next == null) {
                continue
            }

            get(theName)!!.add((next.getValuesAsQueryTokens() as MutableList<IQueryParameterType>))
        }
    }

    fun add(theName: String, theOr: IQueryParameterOr<*>?) {
        if (theOr == null) {
            return
        }
        if (!containsKey(theName)) {
            put(theName, mutableListOf())
        }

        get(theName)!!.add((theOr.getValuesAsQueryTokens() as MutableList<IQueryParameterType>))
    }

    fun values(): Collection<MutableList<MutableList<IQueryParameterType>>> {
        return mySearchParameterMap.values
    }

    fun add(theName: String, theParam: IQueryParameterType?): SearchParameterMap {
        assert(
            Constants.PARAM_LASTUPDATED != theName // this has it's own field in the map
        )

        if (theParam == null) {
            return this
        }
        if (!containsKey(theName)) {
            put(theName, mutableListOf())
        }
        val list = ArrayList<IQueryParameterType>()
        list.add(theParam)
        get(theName)!!.add(list)

        return this
    }

    fun addInclude(theInclude: Include) {
        this.includes.add(theInclude)
    }

    private fun addLastUpdateParam(b: StringBuilder, date: DateParam?) {
        if (date != null && StringUtils.isNotBlank(date.valueAsString)) {
            addUrlParamSeparator(b)
            b.append(Constants.PARAM_LASTUPDATED)
            b.append('=')
            b.append(date.valueAsString)
        }
    }

    fun addRevInclude(theInclude: Include) {
        this.revIncludes.add(theInclude)
    }

    private fun addUrlIncludeParams(
        b: StringBuilder,
        paramName: String?,
        theList: MutableSet<Include>,
    ) {
        val list = theList.toMutableList()

        list.sortWith(IncludeComparator())
        for (nextInclude in list) {
            addUrlParamSeparator(b)
            b.append(paramName)
            b.append('=')
            b.append(UrlUtil.escapeUrlParam(nextInclude.paramType))
            b.append(':')
            b.append(UrlUtil.escapeUrlParam(nextInclude.paramName))
            if (StringUtils.isNotBlank(nextInclude.paramTargetType)) {
                b.append(':')
                b.append(nextInclude.paramTargetType)
            }
        }
    }

    private fun addUrlParamSeparator(theB: StringBuilder) {
        if (theB.isEmpty()) {
            theB.append('?')
        } else {
            theB.append('&')
        }
    }

    var includes: MutableSet<Include>
        get() {
            if (myIncludes == null) {
                myIncludes = mutableSetOf()
            }
            return myIncludes!!
        }
        set(theIncludes) {
            myIncludes = theIncludes
        }

    var lastUpdated: DateRangeParam?
        get() {
            if (myLastUpdated != null) {
                if (myLastUpdated!!.isEmpty) {
                    myLastUpdated = null
                }
            }
            return myLastUpdated
        }
        set(theLastUpdated) {
            myLastUpdated = theLastUpdated
        }

    fun setLoadSynchronousUpTo(theLoadSynchronousUpTo: Int?): SearchParameterMap {
        this.loadSynchronousUpTo = theLoadSynchronousUpTo
        if (this.loadSynchronousUpTo != null) {
            setLoadSynchronous(true)
        }
        return this
    }

    var revIncludes: MutableSet<Include>
        get() {
            if (myRevIncludes == null) {
                myRevIncludes = mutableSetOf()
            }
            return myRevIncludes!!
        }
        set(theRevIncludes) {
            myRevIncludes = theRevIncludes
        }

    val isAllParametersHaveNoModifier: Boolean
        get() {
            for (nextParamName in values()) {
                for (nextAnd in nextParamName) {
                    for (nextOr in nextAnd) {
                        if (StringUtils.isNotBlank(nextOr.queryParameterQualifier)) {
                            return false
                        }
                    }
                }
            }
            return true
        }

    fun setLoadSynchronous(theLoadSynchronous: Boolean): SearchParameterMap {
        this.isLoadSynchronous = theLoadSynchronous
        return this
    }

    fun toNormalizedQueryString(theCtx: FhirContext): String {
        val b = StringBuilder()

        val keys = keySet().sorted()
        for (nextKey in keys) {
            val nextValuesAndsIn = get(nextKey)!!
            val nextValuesAndsOut: MutableList<MutableList<IQueryParameterType>> = mutableListOf()
            for (nextValuesAndIn in nextValuesAndsIn) {
                val nextValuesOrsOut: MutableList<IQueryParameterType> = mutableListOf()
                for (nextValueOrIn in nextValuesAndIn) {
                    if (
                        nextValueOrIn.missing != null ||
                            StringUtils.isNotBlank(nextValueOrIn.getValueAsQueryToken(theCtx))
                    ) {
                        nextValuesOrsOut.add(nextValueOrIn)
                    }
                }

                nextValuesOrsOut.sortWith(QueryParameterTypeComparator(theCtx))

                if (nextValuesOrsOut.isNotEmpty()) {
                    nextValuesAndsOut.add(nextValuesOrsOut)
                }
            } // for AND

            nextValuesAndsOut.sortWith(QueryParameterOrComparator(theCtx))

            for (nextValuesAnd in nextValuesAndsOut) {
                addUrlParamSeparator(b)
                val firstValue: IQueryParameterType = nextValuesAnd[0]
                b.append(UrlUtil.escapeUrlParam(nextKey))

                if (nextKey == Constants.PARAM_HAS) {
                    b.append(':')
                }

                if (firstValue.missing != null) {
                    b.append(Constants.PARAMQUALIFIER_MISSING)
                    b.append('=')
                    if (firstValue.missing) {
                        b.append(Constants.PARAMQUALIFIER_MISSING_TRUE)
                    } else {
                        b.append(Constants.PARAMQUALIFIER_MISSING_FALSE)
                    }
                    continue
                }

                if (StringUtils.isNotBlank(firstValue.queryParameterQualifier)) {
                    b.append(firstValue.queryParameterQualifier)
                }

                b.append('=')

                for (i in nextValuesAnd.indices) {
                    val nextValueOr: IQueryParameterType = nextValuesAnd[i]
                    if (i > 0) {
                        b.append(',')
                    }
                    val valueAsQueryToken = nextValueOr.getValueAsQueryToken(theCtx)
                    b.append(UrlUtil.escapeUrlParam(valueAsQueryToken))
                }
            }
        } // for keys

        var sort = this.sort
        var first = true
        while (sort != null) {
            if (StringUtils.isNotBlank(sort.paramName)) {
                if (first) {
                    addUrlParamSeparator(b)
                    b.append(Constants.PARAM_SORT)
                    b.append('=')
                    first = false
                } else {
                    b.append(',')
                }
                if (sort.order == SortOrderEnum.DESC) {
                    b.append('-')
                }
                b.append(sort.paramName)
            }

            Validate.isTrue(sort !== sort.chain) // just in case, shouldn't happen
            sort = sort.chain
        }

        addUrlIncludeParams(b, Constants.PARAM_INCLUDE, this.includes)
        addUrlIncludeParams(b, Constants.PARAM_REVINCLUDE, this.revIncludes)

        if (this.lastUpdated != null) {
            val lb = this.lastUpdated!!.lowerBound
            addLastUpdateParam(b, lb)
            val ub = this.lastUpdated!!.upperBound
            addLastUpdateParam(b, ub)
        }

        if (this.count != null) {
            addUrlParamSeparator(b)
            b.append(Constants.PARAM_COUNT)
            b.append('=')
            b.append(this.count)
        }

        // Summary mode (_summary)
        if (this.summaryMode != null) {
            addUrlParamSeparator(b)
            b.append(Constants.PARAM_SUMMARY)
            b.append('=')
            b.append(this.summaryMode!!.code)
        }

        // Search count mode (_total)
        if (this.searchTotalMode != null) {
            addUrlParamSeparator(b)
            b.append(Constants.PARAM_SEARCH_TOTAL_MODE)
            b.append('=')
            b.append(this.searchTotalMode!!.code)
        }

        if (b.isEmpty()) {
            b.append('?')
        }

        return b.toString()
    }

    override fun toString(): String {
        val b = ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        if (!this.isEmpty) {
            b.append("params", mySearchParameterMap)
        }
        if (!this.includes.isEmpty()) {
            b.append("includes", this.includes)
        }
        return b.toString()
    }

    fun clean() {
        for (nextParamEntry in this.entrySet()) {
            val nextParamName = nextParamEntry.key
            val andOrParams: MutableList<MutableList<IQueryParameterType>> = nextParamEntry.value
            clean(nextParamName, andOrParams)
        }
    }

    private fun clean(
        theParamName: String?,
        theAndOrParams: MutableList<MutableList<IQueryParameterType>>,
    ) {
        var andListIdx = 0
        while (andListIdx < theAndOrParams.size) {
            val nextOrList: MutableList<out IQueryParameterType> = theAndOrParams[andListIdx]

            var orListIdx = 0
            while (orListIdx < nextOrList.size) {
                val nextOr: IQueryParameterType = nextOrList[orListIdx]
                var hasNoValue = false
                if (nextOr.missing != null) {
                    orListIdx++
                    continue
                }
                if (nextOr is QuantityParam) {
                    if (StringUtils.isBlank(nextOr.valueAsString)) {
                        hasNoValue = true
                    }
                }

                if (hasNoValue) {
                    ourLog.debug("Ignoring empty parameter: {}", theParamName)
                    nextOrList.removeAt(orListIdx)
                    orListIdx--
                }
                orListIdx++
            }

            if (nextOrList.isEmpty()) {
                theAndOrParams.removeAt(andListIdx)
                andListIdx--
            }
            andListIdx++
        }
    }

    enum class EverythingModeEnum(
        thePatient: Boolean,
        theEncounter: Boolean,
        theInstance: Boolean,
    ) {
        ENCOUNTER_INSTANCE(false, true, true),
        ENCOUNTER_TYPE(false, true, false),
        PATIENT_INSTANCE(true, false, true),
        PATIENT_TYPE(true, false, false);

        val isEncounter: Boolean

        val isInstance: Boolean

        val isPatient: Boolean

        init {
            assert(thePatient xor theEncounter)
            this.isPatient = thePatient
            this.isEncounter = theEncounter
            this.isInstance = theInstance
        }
    }

    class IncludeComparator : Comparator<Include> {
        override fun compare(theO1: Include, theO2: Include): Int {
            var retVal = StringUtils.compare(theO1.paramType, theO2.paramType)
            if (retVal == 0) {
                retVal = StringUtils.compare(theO1.paramName, theO2.paramName)
            }
            if (retVal == 0) {
                retVal = StringUtils.compare(theO1.paramTargetType, theO2.paramTargetType)
            }
            return retVal
        }
    }

    class QueryParameterOrComparator internal constructor(private val myCtx: FhirContext) :
        Comparator<MutableList<IQueryParameterType>> {
        override fun compare(
            theO1: MutableList<IQueryParameterType>,
            theO2: MutableList<IQueryParameterType>,
        ): Int {
            // These lists will never be empty
            return compare(myCtx, theO1[0], theO2[0])
        }
    }

    class QueryParameterTypeComparator internal constructor(private val myCtx: FhirContext) :
        Comparator<IQueryParameterType> {
        override fun compare(theO1: IQueryParameterType, theO2: IQueryParameterType): Int {
            return compare(myCtx, theO1, theO2)
        }
    }

    // Wrapper methods
    fun get(theName: String): MutableList<MutableList<IQueryParameterType>>? {
        return mySearchParameterMap[theName]
    }

    private fun put(theName: String, theParams: MutableList<MutableList<IQueryParameterType>>) {
        mySearchParameterMap[theName] = theParams
    }

    fun containsKey(theName: String?): Boolean {
        return mySearchParameterMap.containsKey(theName)
    }

    fun keySet(): Set<String> {
        return mySearchParameterMap.keys
    }

    val isEmpty: Boolean
        get() = mySearchParameterMap.isEmpty()

    fun entrySet(): Set<Map.Entry<String, MutableList<MutableList<IQueryParameterType>>>> {
        return mySearchParameterMap.entries
    }

    fun remove(theName: String): MutableList<MutableList<IQueryParameterType>>? {
        return mySearchParameterMap.remove(theName)
    }

    companion object {
        private val ourLog: Logger = LoggerFactory.getLogger(SearchParameterMap::class.java)

        private const val serialVersionUID = 1L

        private fun compare(
            theCtx: FhirContext?,
            theO1: IQueryParameterType,
            theO2: IQueryParameterType,
        ): Int {
            var retVal: Int
            retVal =
                if (theO1.missing == null && theO2.missing == null) {
                    0
                } else if (theO1.missing == null) {
                    -1
                } else if (theO2.missing == null) {
                    1
                } else if (theO1.missing == theO2.missing) {
                    0
                } else {
                    if (theO1.missing) {
                        1
                    } else {
                        -1
                    }
                }

            if (retVal == 0) {
                val q1 = theO1.queryParameterQualifier
                val q2 = theO2.queryParameterQualifier
                retVal = StringUtils.compare(q1, q2)
            }

            if (retVal == 0) {
                val v1 = theO1.getValueAsQueryToken(theCtx)
                val v2 = theO2.getValueAsQueryToken(theCtx)
                retVal = StringUtils.compare(v1, v2)
            }
            return retVal
        }
    }
}
