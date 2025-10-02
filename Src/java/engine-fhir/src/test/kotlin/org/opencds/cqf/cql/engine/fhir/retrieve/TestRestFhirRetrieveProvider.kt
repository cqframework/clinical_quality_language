package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.util.UrlUtil
import com.github.tomakehurst.wiremock.client.WireMock
import java.time.OffsetDateTime
import java.time.ZoneOffset
import org.hl7.fhir.r4.model.Patient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.fhir.R4FhirTest
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval

internal class TestRestFhirRetrieveProvider : R4FhirTest() {
    var provider: RestFhirRetrieveProvider? = null
    val modelResolver = getModelResolver(CLIENT.fhirContext.version.version)

    @BeforeEach
    fun setUp() {
        this.provider = RestFhirRetrieveProvider(RESOLVER, modelResolver, CLIENT)
    }

    private fun getModelResolver(
        fhirVersionEnum: FhirVersionEnum
    ): FhirModelResolver<*, *, *, *, *, *, *, *> {
        if (fhirVersionEnum == FhirVersionEnum.DSTU3) {
            return CachedDstu3FhirModelResolver()
        } else if (fhirVersionEnum == FhirVersionEnum.R4) {
            return CachedR4FhirModelResolver()
        }
        throw FhirVersionMisMatchException(
            "The FHIR version $fhirVersionEnum is not supported by this retrieve provider."
        )
    }

    @Test
    @Throws(FhirVersionMisMatchException::class)
    fun noUserSpecifiedPageSizeUsesDefault() {
        val fhirQueryGenerator =
            FhirQueryGeneratorFactory.create(
                modelResolver,
                provider!!.searchParameterResolver,
                provider!!.getTerminologyProvider(),
            )

        val map = fhirQueryGenerator.getBaseMap(null, null, null, null)
        Assertions.assertNull(map.count)
    }

    @Test
    @Throws(FhirVersionMisMatchException::class)
    fun userSpecifiedPageSizeIsUsed() {
        val expected = 100
        provider!!.pageSize = expected
        val fhirQueryGenerator =
            FhirQueryGeneratorFactory.create(
                getModelResolver(CLIENT.fhirContext.version.version),
                provider!!.searchParameterResolver,
                provider!!.getTerminologyProvider(),
                null,
                null,
                expected,
                null,
            )

        val map = fhirQueryGenerator.getBaseMap(null, null, null, null)
        Assertions.assertEquals(map.count, expected)
    }

    @Test
    fun userSpecifiedPageSizeIsUsedWhenCodeBasedQuery() {
        val code = Code().withSystem("http://mysystem.com").withCode("myCode")
        val codes = mutableListOf<Code>(code)

        mockFhirSearch(
            ("/Condition?code=" +
                UrlUtil.escapeUrlParam(code.system + "|" + code.code) +
                "&subject=" +
                UrlUtil.escapeUrlParam("Patient/123") +
                "&_count=500")
        )

        provider!!.pageSize = 500
        provider!!.retrieve(
            "Patient",
            "subject",
            "123",
            "Condition",
            null,
            "code",
            codes,
            null,
            null,
            null,
            null,
            null,
        )
    }

    @Test
    fun userSpecifiedPageSizeIsUsedWhenValueSetQuery() {
        val valueSetUrl = "http://myterm.com/fhir/ValueSet/MyValueSet"

        mockFhirSearch(
            ("/Condition?code" +
                UrlUtil.escapeUrlParam(":") +
                "in=" +
                UrlUtil.escapeUrlParam(valueSetUrl) +
                "&subject=" +
                UrlUtil.escapeUrlParam("Patient/123") +
                "&_count=500")
        )

        provider!!.pageSize = 500
        provider!!.retrieve(
            "Patient",
            "subject",
            "123",
            "Condition",
            "http://hl7.org/fhir/StructureDefinition/Condition",
            "code",
            null,
            valueSetUrl,
            null,
            null,
            null,
            null,
        )
    }

    @Test
    fun userSpecifiedPageSizeIsUsedWhenDateQuery() {
        /*
         * As best as I can tell, the date range optimized queries are
         * broken right now. See https://github.com/DBCG/cql_engine/issues/467.
         */

        val start = OffsetDateTime.of(2020, 11, 12, 1, 2, 3, 0, ZoneOffset.UTC)
        val end = start.plusYears(1)

        val interval = Interval(DateTime(start), true, DateTime(end), false)

        // The dates will be rendered in the URL in the build machine's local
        // time zone, so the URL value might fluctuate from one environment to another.
        // We could try to match that formatting logic to get an exact URL, but it isn't
        // necessary for what we are trying to achieve here, so we just accept any date
        // string.
        mockFhirInteraction(
            WireMock.get(
                WireMock.urlMatching(
                    ("/Condition\\?" +
                        ".*onset-date=ge2020[^&]+.*&onset-date=le[^&]+" +
                        ".*subject=" +
                        UrlUtil.escapeUrlParam("Patient/123") +
                        ".*_count=500")
                )
            ),
            makeBundle(),
        )

        provider!!.pageSize = 500
        provider!!.retrieve(
            "Patient",
            "subject",
            "123",
            "Condition",
            null,
            "code",
            null,
            null,
            "onset",
            null,
            null,
            interval,
        )
    }

    @Test
    fun userSpecifiedPageSizeNotUsedWhenIDQuery() {
        mockFhirRead("/Patient/123", Patient())

        provider!!.pageSize = 500
        provider!!.retrieve(
            "Patient",
            "id",
            "123",
            "Patient",
            "http://hl7.org/fhir/StructureDefinition/Patient",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )
    }

    @Test
    fun noUserSpecifiedPageSizeSpecifiedNoCountInURL() {
        val code = Code().withSystem("http://mysystem.com").withCode("myCode")
        val codes = mutableListOf<Code>(code)

        mockFhirSearch(
            ("/Condition?code=" +
                UrlUtil.escapeUrlParam(code.system + "|" + code.code) +
                "&subject=" +
                UrlUtil.escapeUrlParam("Patient/123"))
        )

        provider!!.retrieve(
            "Patient",
            "subject",
            "123",
            "Condition",
            null,
            "code",
            codes,
            null,
            null,
            null,
            null,
            null,
        )
    }

    companion object {
        val CLIENT: IGenericClient = newClient()
        val RESOLVER: SearchParameterResolver = SearchParameterResolver(CLIENT.fhirContext)
    }
}
