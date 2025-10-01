package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.client.api.IGenericClient
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DataRequirement
import org.hl7.fhir.dstu3.model.DateTimeType
import org.hl7.fhir.dstu3.model.Duration
import org.hl7.fhir.dstu3.model.Reference
import org.hl7.fhir.dstu3.model.ValueSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.fhir.Dstu3FhirTest
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.fhir.terminology.Dstu3FhirTerminologyProvider
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

internal class TestDstu3FhirQueryGenerator : Dstu3FhirTest() {
    var generator: Dstu3FhirQueryGenerator? = null
    var evaluationOffsetDateTime: OffsetDateTime? = null
    var evaluationDateTime: DateTime? = null
    var contextValues: MutableMap<String?, Any?>? = null
    var parameters: MutableMap<String?, Any?>? = null

    @BeforeEach
    @Throws(FhirVersionMisMatchException::class)
    fun setUp() {
        val searchParameterResolver =
            SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))
        val terminologyProvider: TerminologyProvider = Dstu3FhirTerminologyProvider(CLIENT)
        val modelResolver: Dstu3FhirModelResolver = CachedDstu3FhirModelResolver()
        this.generator =
            Dstu3FhirQueryGenerator(searchParameterResolver, terminologyProvider, modelResolver)
        this.evaluationOffsetDateTime =
            OffsetDateTime.of(2018, 11, 19, 9, 0, 0, 0, ZoneOffset.ofHours(-7))
        this.evaluationDateTime = DateTime(evaluationOffsetDateTime)
        this.contextValues = HashMap<String?, Any?>()
        this.parameters = HashMap<String?, Any?>()
    }

    private fun getTestValueSet(id: String?, numberOfCodesToInclude: Int): ValueSet {
        val valueSetUrl = String.format("http://myterm.com/fhir/ValueSet/%s", id)
        val valueSet = ValueSet()
        valueSet.setId("MyValueSet")
        valueSet.setUrl(valueSetUrl)

        val contains: MutableList<ValueSet.ValueSetExpansionContainsComponent?> =
            ArrayList<ValueSet.ValueSetExpansionContainsComponent?>()
        for (i in 0..<numberOfCodesToInclude) {
            val expansionContainsComponent = ValueSet.ValueSetExpansionContainsComponent()
            expansionContainsComponent.setSystem(
                String.format("http://myterm.com/fhir/CodeSystem/%s", id)
            )
            expansionContainsComponent.setCode("code$i")
            contains.add(expansionContainsComponent)
        }

        val expansion = ValueSet.ValueSetExpansionComponent()
        expansion.setContains(contains)
        valueSet.setExpansion(expansion)

        return valueSet
    }

    private fun getCodeFilteredDataRequirement(
        resourceType: String?,
        path: String?,
        valueSet: ValueSet,
    ): DataRequirement {
        val dataRequirement = DataRequirement()
        dataRequirement.setType(resourceType)
        val categoryCodeFilter = DataRequirement.DataRequirementCodeFilterComponent()
        categoryCodeFilter.setPath(path)
        val valueSetReference = Reference(valueSet.getUrl())
        categoryCodeFilter.setValueSet(valueSetReference)
        dataRequirement.setCodeFilter(
            listOf<DataRequirement.DataRequirementCodeFilterComponent?>(categoryCodeFilter)
        )

        return dataRequirement
    }

    @Test
    fun getFhirQueriesObservation() {
        val valueSet = getTestValueSet("MyValueSet", 3)

        val valueSetBundle = Bundle()
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET)

        val entry = Bundle.BundleEntryComponent()
        entry.setResource(valueSet)
        valueSetBundle.addEntry(entry)

        /* spell-checker: disable */
        mockFhirRead(
            "/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet",
            valueSetBundle,
        )

        val dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet)

        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val actualQuery = actual[0]
        val expectedQuery =
            "Observation?category:in=http://myterm.com/fhir/ValueSet/MyValueSet&patient=Patient/{{context.patientId}}"

        Assertions.assertEquals(actualQuery, expectedQuery)
    }

    @Test
    fun getFhirQueriesCodeInValueSet() {
        val valueSet = getTestValueSet("MyValueSet", 500)

        val valueSetBundle = Bundle()
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET)

        val entry = Bundle.BundleEntryComponent()
        entry.setResource(valueSet)
        valueSetBundle.addEntry(entry)

        /* spell-checker: disable */
        mockFhirRead(
            "/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet",
            valueSetBundle,
        )

        val dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet)

        this.generator!!.setMaxCodesPerQuery(4)
        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val actualQuery = actual[0]
        val expectedQuery =
            "Observation?category:in=http://myterm.com/fhir/ValueSet/MyValueSet&patient=Patient/{{context.patientId}}"

        Assertions.assertEquals(actualQuery, expectedQuery)
    }

    @Test
    fun getFhirQueriesAppointment() {
        val dataRequirement = DataRequirement()
        dataRequirement.setType("Appointment")

        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val actualQuery = actual[0]
        val expectedQuery = "Appointment?actor=Patient/{{context.patientId}}"

        Assertions.assertEquals(actualQuery, expectedQuery)
    }

    @Test
    fun getFhirQueriesAppointmentWithDate() {
        val dataRequirement = DataRequirement()
        dataRequirement.setType("Appointment")
        val dateFilterComponent = DataRequirement.DataRequirementDateFilterComponent()
        dateFilterComponent.setPath("start")

        val evaluationDateTimeAsLocal =
            OffsetDateTime.ofInstant(
                evaluationOffsetDateTime!!.toInstant(),
                TimeZone.getDefault().toZoneId(),
            )

        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
        val dateTimeString = dateTimeFormatter.format(evaluationDateTimeAsLocal)

        dateFilterComponent.setValue(DateTimeType(dateTimeString))
        dataRequirement.setDateFilter(
            mutableListOf<DataRequirement.DataRequirementDateFilterComponent?>(dateFilterComponent)
        )

        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val actualQuery = actual[0]
        val expectedQuery =
            String.format(
                "Appointment?actor=Patient/{{context.patientId}}&date=ge%s&date=le%s",
                dateTimeString,
                dateTimeString,
            )

        Assertions.assertEquals(actualQuery, expectedQuery)
    }

    @Test
    fun getFhirQueriesObservationWithDuration() {
        val dataRequirement = DataRequirement()
        dataRequirement.setType("Observation")
        val dateFilterComponent = DataRequirement.DataRequirementDateFilterComponent()
        dateFilterComponent.setPath("effective")
        val duration = Duration()
        duration.setValue(90).setCode("d").setUnit("days")
        dateFilterComponent.setValue(duration)
        dataRequirement.setDateFilter(
            mutableListOf<DataRequirement.DataRequirementDateFilterComponent?>(dateFilterComponent)
        )

        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val evaluationDateTimeAsLocal =
            OffsetDateTime.ofInstant(
                evaluationOffsetDateTime!!.toInstant(),
                TimeZone.getDefault().toZoneId(),
            )
        val expectedRangeStartDateTime =
            Date.from(evaluationDateTimeAsLocal.minusDays(90).toInstant())

        val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx")

        val actualQuery = actual[0]
        val expectedQuery =
            String.format(
                    "Observation?date=ge%s&date=le%s&patient=Patient/{{context.patientId}}",
                    simpleDateFormatter.format(expectedRangeStartDateTime),
                    dateTimeFormatter.format(evaluationDateTimeAsLocal),
                )
                .replace("Z", "+00:00")

        Assertions.assertEquals(actualQuery, expectedQuery)
    }

    @Test
    fun codesExceedMaxCodesPerQuery() {
        val valueSet = getTestValueSet("MyValueSet", 8)

        val valueSetBundle = Bundle()
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET)

        val entry = Bundle.BundleEntryComponent()
        entry.setResource(valueSet)
        valueSetBundle.addEntry(entry)

        /* spell-checker: disable */
        mockFhirRead(
            "/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet",
            valueSetBundle,
        )
        mockFhirRead($$"/ValueSet/MyValueSet/$expand", valueSet)

        val dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet)

        this.generator!!.setMaxCodesPerQuery(4)
        this.generator!!.isExpandValueSets = true
        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val expectedQuery1 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code0,http://myterm.com/fhir/CodeSystem/MyValueSet|code1,http://myterm.com/fhir/CodeSystem/MyValueSet|code2,http://myterm.com/fhir/CodeSystem/MyValueSet|code3&patient=Patient/{{context.patientId}}"
        val expectedQuery2 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code4,http://myterm.com/fhir/CodeSystem/MyValueSet|code5,http://myterm.com/fhir/CodeSystem/MyValueSet|code6,http://myterm.com/fhir/CodeSystem/MyValueSet|code7&patient=Patient/{{context.patientId}}"

        Assertions.assertNotNull(actual)
        Assertions.assertEquals(2, actual.size)
        Assertions.assertEquals(actual[0], expectedQuery1)
        Assertions.assertEquals(actual[1], expectedQuery2)
    }

    @Test
    fun maxQueryThresholdExceeded() {
        val valueSet = getTestValueSet("MyValueSet", 21)

        val valueSetBundle = Bundle()
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET)

        val entry = Bundle.BundleEntryComponent()
        entry.setResource(valueSet)
        valueSetBundle.addEntry(entry)

        mockFhirRead(
            "/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet",
            valueSetBundle,
        )
        mockFhirRead($$"/ValueSet/MyValueSet/$expand", valueSet)

        val dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet)

        this.generator!!.setMaxCodesPerQuery(4)
        this.generator!!.isExpandValueSets = true
        this.generator!!.setQueryBatchThreshold(5)
        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        Assertions.assertNotNull(actual)
        Assertions.assertEquals(1, actual.size)
    }

    @Test
    fun maxQueryThreshold() {
        val valueSet = getTestValueSet("MyValueSet", 21)

        val valueSetBundle = Bundle()
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET)

        val entry = Bundle.BundleEntryComponent()
        entry.setResource(valueSet)
        valueSetBundle.addEntry(entry)

        /* spell-checker: disable */
        mockFhirRead(
            "/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet",
            valueSetBundle,
        )
        mockFhirRead($$"/ValueSet/MyValueSet/$expand", valueSet)

        val dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet)

        this.generator!!.setMaxCodesPerQuery(5)
        this.generator!!.isExpandValueSets = true
        this.generator!!.setQueryBatchThreshold(5)
        this.contextValues!!["Patient"] = "{{context.patientId}}"
        val actual =
            this.generator!!.generateFhirQueries(
                dataRequirement,
                this.evaluationDateTime,
                this.contextValues,
                this.parameters,
                null,
            )

        val expectedQuery1 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code0,http://myterm.com/fhir/CodeSystem/MyValueSet|code1,http://myterm.com/fhir/CodeSystem/MyValueSet|code2,http://myterm.com/fhir/CodeSystem/MyValueSet|code3,http://myterm.com/fhir/CodeSystem/MyValueSet|code4&patient=Patient/{{context.patientId}}"
        val expectedQuery2 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code5,http://myterm.com/fhir/CodeSystem/MyValueSet|code6,http://myterm.com/fhir/CodeSystem/MyValueSet|code7,http://myterm.com/fhir/CodeSystem/MyValueSet|code8,http://myterm.com/fhir/CodeSystem/MyValueSet|code9&patient=Patient/{{context.patientId}}"
        val expectedQuery3 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code10,http://myterm.com/fhir/CodeSystem/MyValueSet|code11,http://myterm.com/fhir/CodeSystem/MyValueSet|code12,http://myterm.com/fhir/CodeSystem/MyValueSet|code13,http://myterm.com/fhir/CodeSystem/MyValueSet|code14&patient=Patient/{{context.patientId}}"
        val expectedQuery4 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code15,http://myterm.com/fhir/CodeSystem/MyValueSet|code16,http://myterm.com/fhir/CodeSystem/MyValueSet|code17,http://myterm.com/fhir/CodeSystem/MyValueSet|code18,http://myterm.com/fhir/CodeSystem/MyValueSet|code19&patient=Patient/{{context.patientId}}"
        val expectedQuery5 =
            "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code20&patient=Patient/{{context.patientId}}"

        Assertions.assertNotNull(actual)
        Assertions.assertEquals(5, actual.size)
        Assertions.assertEquals(actual[0], expectedQuery1)
        Assertions.assertEquals(actual[1], expectedQuery2)
        Assertions.assertEquals(actual[2], expectedQuery3)
        Assertions.assertEquals(actual[3], expectedQuery4)
        Assertions.assertEquals(actual[4], expectedQuery5)
    }

    @Test
    @Throws(ParseException::class)
    fun getDateRangeParamWithDateInterval() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        val low = formatter.parse("2023-01-01")
        val high = formatter.parse("2023-02-06")
        val interval = Interval(low, true, high, true)

        val rangeParam =
            this.generator!!.getDateRangeParam(
                "Condition",
                "onset",
                "valueDate",
                "valueDate",
                interval,
            )

        Assertions.assertNotNull(rangeParam)
        Assertions.assertEquals(rangeParam!!.value!!.lowerBound.value, low)
        Assertions.assertEquals(rangeParam.value!!.upperBound.value, high)
    }

    @Test
    @Throws(ParseException::class)
    fun getDateRangeParamWithDateTimeInterval() {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

        val low = formatter.parse("2023-01-01T12:01:02-0700")
        val high = formatter.parse("2023-02-06T12:01:02-0700")
        val interval = Interval(low, true, high, true)

        val rangeParam =
            this.generator!!.getDateRangeParam(
                "Condition",
                "onset",
                "valueDate",
                "valueDate",
                interval,
            )

        Assertions.assertNotNull(rangeParam)
        Assertions.assertEquals(rangeParam!!.value!!.lowerBound.value, low)
        Assertions.assertEquals(rangeParam.value!!.upperBound.value, high)
    }

    companion object {
        var CLIENT: IGenericClient = newClient()
    }
}
