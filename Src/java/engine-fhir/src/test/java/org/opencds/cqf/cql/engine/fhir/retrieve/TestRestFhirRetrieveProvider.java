package org.opencds.cqf.cql.engine.fhir.retrieve;

import static ca.uhn.fhir.util.UrlUtil.escapeUrlParam;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.fhir.R4FhirTest;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.model.*;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;

class TestRestFhirRetrieveProvider extends R4FhirTest {

    static SearchParameterResolver RESOLVER;
    static IGenericClient CLIENT;

    RestFhirRetrieveProvider provider;
    FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> modelResolver;

    @BeforeAll
    static void setUpBeforeAll() {
        CLIENT = newClient();
        RESOLVER = new SearchParameterResolver(CLIENT.getFhirContext());
    }

    @BeforeEach
    void setUp() {
        modelResolver = getModelResolver(CLIENT.getFhirContext().getVersion().getVersion());
        this.provider = new RestFhirRetrieveProvider(RESOLVER, modelResolver, CLIENT);
    }

    private FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> getModelResolver(FhirVersionEnum fhirVersionEnum) {
        if (fhirVersionEnum.equals(FhirVersionEnum.DSTU3)) {
            return new CachedDstu3FhirModelResolver();
        } else if (fhirVersionEnum.equals(FhirVersionEnum.R4)) {
            return new CachedR4FhirModelResolver();
        }
        return null;
    }

    @Test
    void noUserSpecifiedPageSizeUsesDefault() throws FhirVersionMisMatchException {
        BaseFhirQueryGenerator fhirQueryGenerator = FhirQueryGeneratorFactory.create(
                modelResolver, provider.searchParameterResolver, provider.getTerminologyProvider());

        SearchParameterMap map = fhirQueryGenerator.getBaseMap(null, null, null, null);
        assertNull(map.getCount());
    }

    @Test
    void userSpecifiedPageSizeIsUsed() throws FhirVersionMisMatchException {
        Integer expected = 100;
        provider.setPageSize(expected);
        BaseFhirQueryGenerator fhirQueryGenerator = FhirQueryGeneratorFactory.create(
                getModelResolver(CLIENT.getFhirContext().getVersion().getVersion()),
                provider.searchParameterResolver,
                provider.getTerminologyProvider(),
                null,
                null,
                expected,
                null);

        SearchParameterMap map = fhirQueryGenerator.getBaseMap(null, null, null, null);
        assertEquals(map.getCount(), expected);
    }

    @Test
    void userSpecifiedPageSizeIsUsedWhenCodeBasedQuery() {
        Code code = new Code().withSystem("http://mysystem.com").withCode("myCode");
        List<Code> codes = Collections.singletonList(code);

        mockFhirSearch("/Condition?code=" + escapeUrlParam(code.getSystem() + "|" + code.getCode()) + "&subject="
                + escapeUrlParam("Patient/123") + "&_count=500");

        provider.setPageSize(500);
        provider.retrieve("Patient", "subject", "123", "Condition", null, "code", codes, null, null, null, null, null);
    }

    @Test
    void userSpecifiedPageSizeIsUsedWhenValueSetQuery() {

        String valueSetUrl = "http://myterm.com/fhir/ValueSet/MyValueSet";

        mockFhirSearch("/Condition?code" + escapeUrlParam(":") + "in=" + escapeUrlParam(valueSetUrl) + "&subject="
                + escapeUrlParam("Patient/123") + "&_count=500");

        provider.setPageSize(500);
        provider.retrieve(
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
                null);
    }

    @Test
    void userSpecifiedPageSizeIsUsedWhenDateQuery() {
        /*
         * As best as I can tell, the date range optimized queries are
         * broken right now. See https://github.com/DBCG/cql_engine/issues/467.
         */

        OffsetDateTime start = OffsetDateTime.of(2020, 11, 12, 1, 2, 3, 0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusYears(1);

        Interval interval = new Interval(new DateTime(start), true, new DateTime(end), false);

        // The dates will be rendered in the URL in the build machine's local
        // time zone, so the URL value might fluctuate from one environment to another.
        // We could try to match that formatting logic to get an exact URL, but it isn't
        // necessary for what we are trying to achieve here, so we just accept any date
        // string.
        mockFhirInteraction(
                get(urlMatching("/Condition\\?subject=" + escapeUrlParam("Patient/123")
                        + "&onset-date=ge2020[^&]+&onset-date=le[^&]+"
                        + "&_count=500")),
                makeBundle());

        provider.setPageSize(500);
        provider.retrieve(
                "Patient", "subject", "123", "Condition", null, "code", null, null, "onset", null, null, interval);
    }

    @Test
    void userSpecifiedPageSizeNotUsedWhenIDQuery() {
        mockFhirRead("/Patient/123", new Patient());

        provider.setPageSize(500);
        provider.retrieve(
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
                null);
    }

    @Test
    void noUserSpecifiedPageSizeSpecifiedNoCountInURL() {
        Code code = new Code().withSystem("http://mysystem.com").withCode("myCode");
        List<Code> codes = Collections.singletonList(code);

        mockFhirSearch("/Condition?code=" + escapeUrlParam(code.getSystem() + "|" + code.getCode()) + "&subject="
                + escapeUrlParam("Patient/123"));

        provider.retrieve("Patient", "subject", "123", "Condition", null, "code", codes, null, null, null, null, null);
    }
}
