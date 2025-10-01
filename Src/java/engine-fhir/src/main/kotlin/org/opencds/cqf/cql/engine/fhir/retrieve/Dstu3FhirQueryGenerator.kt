package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirVersionEnum
import java.net.URLDecoder
import java.time.OffsetDateTime
import org.hl7.fhir.dstu3.model.CapabilityStatement
import org.hl7.fhir.dstu3.model.DataRequirement
import org.hl7.fhir.dstu3.model.DateTimeType
import org.hl7.fhir.dstu3.model.Duration
import org.hl7.fhir.dstu3.model.Period
import org.hl7.fhir.dstu3.model.Reference
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.instance.model.api.ICompositeType
import org.opencds.cqf.cql.engine.elm.executing.SubtractEvaluator
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

class Dstu3FhirQueryGenerator(
    searchParameterResolver: SearchParameterResolver,
    terminologyProvider: TerminologyProvider?,
    modelResolver: ModelResolver,
) :
    BaseFhirQueryGenerator(
        searchParameterResolver,
        terminologyProvider,
        modelResolver,
        searchParameterResolver.fhirContext,
    ) {
    override val fhirVersion = FhirVersionEnum.DSTU3

    override fun generateFhirQueries(
        dataRequirement: ICompositeType?,
        evaluationDateTime: DateTime?,
        contextValues: MutableMap<String, Any?>?,
        parameters: MutableMap<String, Any?>?,
        capabilityStatement: IBaseConformance?,
    ): MutableList<String> {
        require(dataRequirement is DataRequirement) {
            "dataRequirement argument must be a DataRequirement"
        }
        require(!(capabilityStatement != null && capabilityStatement !is CapabilityStatement)) {
            "capabilityStatement argument must be a CapabilityStatement"
        }

        val queries: MutableList<String> = ArrayList()

        val codeFilters: MutableList<CodeFilter> = ArrayList()

        if (dataRequirement.hasCodeFilter()) {
            for (codeFilterComponent in dataRequirement.getCodeFilter()) {
                if (!codeFilterComponent.hasPath()) {
                    continue
                }
                var codes: MutableList<Code>? = null
                var valueSet: String? = null

                val codePath = codeFilterComponent.getPath()

                // TODO: What to do if/when System is not provided...
                if (codeFilterComponent.hasValueCode()) {
                    codes = ArrayList()
                    val codeFilterValueCode = codeFilterComponent.getValueCode()
                    for (codeType in codeFilterValueCode) {
                        val code = Code()
                        code.code = codeType.asStringValue()
                        codes.add(code)
                    }
                }
                if (codeFilterComponent.hasValueCoding()) {
                    codes = ArrayList()

                    val codeFilterValueCodings = codeFilterComponent.getValueCoding()
                    for (coding in codeFilterValueCodings) {
                        if (coding.hasCode()) {
                            val code = Code()
                            code.system = coding.getSystem()
                            code.code = coding.getCode()
                            codes.add(code)
                        }
                    }
                }
                if (codeFilterComponent.hasValueCodeableConcept()) {
                    val codeFilterValueCodeableConcepts =
                        codeFilterComponent.getValueCodeableConcept()
                    for (codeableConcept in codeFilterValueCodeableConcepts) {
                        val codeFilterValueCodeableConceptCodings = codeableConcept.getCoding()
                        for (coding in codeFilterValueCodeableConceptCodings) {
                            if (coding.hasCode()) {
                                val code = Code()
                                code.system = coding.getSystem()
                                code.code = coding.getCode()
                                codes!!.add(code)
                            }
                        }
                    }
                }
                if (codeFilterComponent.hasValueSet()) {
                    if (codeFilterComponent.valueSetReference.getReference() is String) {
                        valueSet = (codeFilterComponent.getValueSet() as Reference).getReference()
                    } else if (codeFilterComponent.valueSetReference is Reference) {
                        valueSet = codeFilterComponent.valueSetReference.getReference()
                    }
                }

                codeFilters.add(CodeFilter(codePath, codes, valueSet))
            }
        }

        val dateFilters: MutableList<DateFilter> = ArrayList()

        if (dataRequirement.hasDateFilter()) {
            for (dateFilterComponent in dataRequirement.getDateFilter()) {
                if (!dateFilterComponent.hasPath()) {
                    throw UnsupportedOperationException("A path must be provided")
                }
                var dateLowPath: String? = null
                var dateHighPath: String? = null
                var dateRange: Interval? = null

                val datePath = dateFilterComponent.getPath()

                // TODO: Deal with the case that the value is expressed as an expression extension
                if (dateFilterComponent.hasValue()) {
                    when (val dateFilterValue = dateFilterComponent.getValue()) {
                        is DateTimeType if dateFilterValue.hasPrimitiveValue() -> {
                            dateLowPath = "valueDateTime"
                            dateHighPath = "valueDateTime"
                            val offsetDateTimeString = dateFilterValue.valueAsString
                            val dateTime = DateTime(OffsetDateTime.parse(offsetDateTimeString))

                            dateRange = Interval(dateTime, true, dateTime, true)
                        }

                        is Duration if dateFilterValue.hasValue() -> {
                            // If a Duration is specified, the filter will return only those data
                            // items that fall within
                            // Duration before now.
                            val dateFilterAsDuration = dateFilterValue

                            val dateFilterDurationAsCQLQuantity =
                                Quantity()
                                    .withValue(dateFilterAsDuration.getValue())
                                    .withUnit(dateFilterAsDuration.getUnit())

                            val diff =
                                (SubtractEvaluator.subtract(
                                    evaluationDateTime,
                                    dateFilterDurationAsCQLQuantity,
                                ) as DateTime?)

                            dateRange = Interval(diff, true, evaluationDateTime, true)
                        }

                        is Period if dateFilterValue.hasStart() && dateFilterValue.hasEnd() -> {
                            dateLowPath = "valueDateTime"
                            dateHighPath = "valueDateTime"
                            dateRange =
                                Interval(
                                    dateFilterValue.getStart(),
                                    true,
                                    dateFilterValue.getEnd(),
                                    true,
                                )
                        }
                    }

                    dateFilters.add(DateFilter(datePath, dateLowPath, dateHighPath, dateRange))
                }
            }
        }

        // STU3 only supported Patient context
        val contextType = "Patient"
        val contextPath = modelResolver.getContextPath(contextType, dataRequirement.getType())
        val contextValue = contextValues?.get(contextType)
        val templateId =
            if (dataRequirement.getProfile() != null && !dataRequirement.getProfile().isEmpty())
                dataRequirement.getProfile()[0].value
            else null

        val maps =
            setupQueries(
                contextType,
                contextPath as String?,
                contextValue,
                dataRequirement.getType(),
                templateId,
                codeFilters,
                dateFilters,
            )

        for (map in maps) {
            val query =
                try {
                    URLDecoder.decode(map.toNormalizedQueryString(fhirContext), "UTF-8")
                } catch (ex: Exception) {
                    map.toNormalizedQueryString(fhirContext)
                }
            queries.add(dataRequirement.getType() + query)
        }

        return queries
    }
}
