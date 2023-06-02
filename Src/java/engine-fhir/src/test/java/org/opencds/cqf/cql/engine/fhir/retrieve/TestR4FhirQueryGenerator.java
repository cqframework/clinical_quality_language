package org.opencds.cqf.cql.engine.fhir.retrieve;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.r4.model.*;
import org.opencds.cqf.cql.engine.fhir.R4FhirTest;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.fhir.terminology.R4FhirTerminologyProvider;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.testng.Assert.*;

public class TestR4FhirQueryGenerator extends R4FhirTest {
    static IGenericClient CLIENT;

    R4FhirQueryGenerator generator;
    OffsetDateTime evaluationOffsetDateTime;
    DateTime evaluationDateTime;
    Map<String, Object> contextValues;
    Map<String, Object> parameters;

    @BeforeClass
    public void setUpBeforeClass() {
        CLIENT = newClient();
    }

    @BeforeMethod
    public void setUp() throws FhirVersionMisMatchException {
        SearchParameterResolver searchParameterResolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.R4));
        TerminologyProvider terminologyProvider = new R4FhirTerminologyProvider(CLIENT);
        R4FhirModelResolver modelResolver = new CachedR4FhirModelResolver();
        this.generator = new R4FhirQueryGenerator(searchParameterResolver, terminologyProvider, modelResolver);
        this.evaluationOffsetDateTime = OffsetDateTime.of(2018, 11, 19, 9, 0, 0, 000, ZoneOffset.ofHours(-7));
        this.evaluationDateTime = new DateTime(evaluationOffsetDateTime);
        this.contextValues = new HashMap<String, Object>();
        this.parameters = new HashMap<String, Object>();
    }

    @Test
    public void Test() {
        DateTime evaluationDateTime = new DateTime(evaluationOffsetDateTime);
        OffsetDateTime evaluationDateTimeAsLocal =
                OffsetDateTime.ofInstant(evaluationOffsetDateTime.toInstant(), java.util.TimeZone.getTimeZone("UTC").toZoneId());
        Date expectedRangeStartDateTime = Date.from(evaluationDateTimeAsLocal.minusDays(90).toInstant());

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String expectedQuery = String.format("date=%s", simpleDateFormatter.format(expectedRangeStartDateTime));
    }

    private ValueSet getTestValueSet(String id, int numberOfCodesToInclude) {
        String valueSetUrl = String.format("http://myterm.com/fhir/ValueSet/%s", id);
        ValueSet valueSet = new ValueSet();
        valueSet.setId("MyValueSet");
        valueSet.setUrl(valueSetUrl);

        List<ValueSet.ValueSetExpansionContainsComponent> contains = new ArrayList<ValueSet.ValueSetExpansionContainsComponent>();
        for (int i = 0; i < numberOfCodesToInclude; i++) {
            ValueSet.ValueSetExpansionContainsComponent expansionContainsComponent = new ValueSet.ValueSetExpansionContainsComponent();
            expansionContainsComponent.setSystem(String.format("http://myterm.com/fhir/CodeSystem/%s", id));
            expansionContainsComponent.setCode("code" + i);
            contains.add(expansionContainsComponent);
        }

        ValueSet.ValueSetExpansionComponent expansion = new ValueSet.ValueSetExpansionComponent();
        expansion.setContains(contains);
        valueSet.setExpansion(expansion);

        return valueSet;
    }

    private DataRequirement getCodeFilteredDataRequirement(String resourceType, String path, ValueSet valueSet) {
        DataRequirement dataRequirement = new DataRequirement();
        dataRequirement.setType(resourceType);
        DataRequirement.DataRequirementCodeFilterComponent categoryCodeFilter = new DataRequirement.DataRequirementCodeFilterComponent();
        categoryCodeFilter.setPath(path);
        org.hl7.fhir.r4.model.CanonicalType valueSetReference = new org.hl7.fhir.r4.model.CanonicalType(valueSet.getUrl());
        categoryCodeFilter.setValueSetElement(valueSetReference);
        dataRequirement.setCodeFilter(java.util.Arrays.asList(categoryCodeFilter));

        return dataRequirement;
    }

    @Test
    void testGetFhirQueriesPatientWithNoFilters() {

        DataRequirement dataRequirement = new DataRequirement();
        dataRequirement.setType("Patient");

        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String actualQuery = actual.get(0);
        String expectedQuery = "Patient?_id={{context.patientId}}";

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testGetFhirQueriesConditionWithNoFilters() {

        DataRequirement dataRequirement = new DataRequirement();
        dataRequirement.setType("Condition");

        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String actualQuery = actual.get(0);
        String expectedQuery = "Condition?subject=Patient/{{context.patientId}}";

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testGetFhirQueriesObservation() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 3);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String actualQuery = actual.get(0);
        String expectedQuery = "Observation?category:in=http://myterm.com/fhir/ValueSet/MyValueSet&subject=Patient/{{context.patientId}}";

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testGetFhirQueriesCodeInValueSet() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 500);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.generator.setMaxCodesPerQuery(4);
        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String actualQuery = actual.get(0);
        String expectedQuery = "Observation?category:in=http://myterm.com/fhir/ValueSet/MyValueSet&subject=Patient/{{context.patientId}}";

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testGetFhirQueriesAppointment() {
        DataRequirement dataRequirement = new DataRequirement();
        dataRequirement.setType("Appointment");
        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String actualQuery = actual.get(0);
        String expectedQuery = "Appointment?actor=Patient/{{context.patientId}}";

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testGetFhirQueriesAppointmentWithDate() {
        DataRequirement dataRequirement = new DataRequirement();
        dataRequirement.setType("Appointment");
        DataRequirement.DataRequirementDateFilterComponent dateFilterComponent = new DataRequirement.DataRequirementDateFilterComponent();
        dateFilterComponent.setSearchParam("date");

        OffsetDateTime evaluationDateTimeAsLocal = OffsetDateTime.ofInstant(evaluationOffsetDateTime.toInstant(),
                java.util.TimeZone.getDefault().toZoneId());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");
        String dateTimeString = dateTimeFormatter.format(evaluationDateTimeAsLocal);

        dateFilterComponent.setValue(new DateTimeType(dateTimeString));
        dataRequirement.setDateFilter(Collections.singletonList(dateFilterComponent));

        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String actualQuery = actual.get(0);
        String expectedQuery = String.format("Appointment?actor=Patient/{{context.patientId}}&date=ge%s&date=le%s", dateTimeString, dateTimeString);

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testGetFhirQueriesObservationWithDuration() {
        DataRequirement dataRequirement = new DataRequirement();
        dataRequirement.setType("Observation");
        DataRequirement.DataRequirementDateFilterComponent dateFilterComponent = new DataRequirement.DataRequirementDateFilterComponent();
        dateFilterComponent.setSearchParam("date");
        Duration duration = new Duration();
        duration.setValue(90).setCode("d").setUnit("days");
        dateFilterComponent.setValue(duration);
        dataRequirement.setDateFilter(Collections.singletonList(dateFilterComponent));

        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        OffsetDateTime evaluationDateTimeAsLocal = OffsetDateTime.ofInstant(evaluationOffsetDateTime.toInstant(),
                java.util.TimeZone.getDefault().toZoneId());
        Date expectedRangeStartDateTime = Date.from(evaluationDateTimeAsLocal.minusDays(90).toInstant());

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");

        String actualQuery = actual.get(0);
        String expectedQuery =
            String.format(
                "Observation?date=ge%s&date=le%s&subject=Patient/{{context.patientId}}",
                simpleDateFormatter.format(expectedRangeStartDateTime),
                dateTimeFormatter.format(evaluationDateTimeAsLocal)
            ).replace("Z", "+00:00");

        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    void testCodesExceedMaxCodesPerQuery() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 8);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);
        mockFhirRead("/ValueSet/MyValueSet/$expand", valueSet);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.generator.setMaxCodesPerQuery(4);
        this.generator.setExpandValueSets(true);
        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String expectedQuery1 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code0,http://myterm.com/fhir/CodeSystem/MyValueSet|code1,http://myterm.com/fhir/CodeSystem/MyValueSet|code2,http://myterm.com/fhir/CodeSystem/MyValueSet|code3&subject=Patient/{{context.patientId}}";
        String expectedQuery2 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code4,http://myterm.com/fhir/CodeSystem/MyValueSet|code5,http://myterm.com/fhir/CodeSystem/MyValueSet|code6,http://myterm.com/fhir/CodeSystem/MyValueSet|code7&subject=Patient/{{context.patientId}}";

        assertNotNull(actual);
        assertEquals(actual.size(), 2);
        assertEquals(actual.get(0), expectedQuery1);
        assertEquals(actual.get(1), expectedQuery2);
    }

    @Test
    void testQueryBatchThresholdExceeded() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 21);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);
        mockFhirRead("/ValueSet/MyValueSet/$expand", valueSet);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.generator.setMaxCodesPerQuery(4);
        this.generator.setExpandValueSets(true);
        this.generator.setQueryBatchThreshold(5);
        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        assertNotNull(actual);
        assertEquals(actual.size(), 1);
    }

    @Test
    void testQueryBatchThreshold() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 21);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);
        mockFhirRead("/ValueSet/MyValueSet/$expand", valueSet);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.generator.setMaxCodesPerQuery(5);
        this.generator.setExpandValueSets(true);
        this.generator.setQueryBatchThreshold(5);
        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String expectedQuery1 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code0,http://myterm.com/fhir/CodeSystem/MyValueSet|code1,http://myterm.com/fhir/CodeSystem/MyValueSet|code2,http://myterm.com/fhir/CodeSystem/MyValueSet|code3,http://myterm.com/fhir/CodeSystem/MyValueSet|code4&subject=Patient/{{context.patientId}}";
        String expectedQuery2 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code5,http://myterm.com/fhir/CodeSystem/MyValueSet|code6,http://myterm.com/fhir/CodeSystem/MyValueSet|code7,http://myterm.com/fhir/CodeSystem/MyValueSet|code8,http://myterm.com/fhir/CodeSystem/MyValueSet|code9&subject=Patient/{{context.patientId}}";
        String expectedQuery3 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code10,http://myterm.com/fhir/CodeSystem/MyValueSet|code11,http://myterm.com/fhir/CodeSystem/MyValueSet|code12,http://myterm.com/fhir/CodeSystem/MyValueSet|code13,http://myterm.com/fhir/CodeSystem/MyValueSet|code14&subject=Patient/{{context.patientId}}";
        String expectedQuery4 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code15,http://myterm.com/fhir/CodeSystem/MyValueSet|code16,http://myterm.com/fhir/CodeSystem/MyValueSet|code17,http://myterm.com/fhir/CodeSystem/MyValueSet|code18,http://myterm.com/fhir/CodeSystem/MyValueSet|code19&subject=Patient/{{context.patientId}}";
        String expectedQuery5 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code20&subject=Patient/{{context.patientId}}";

        assertNotNull(actual);
        assertEquals(actual.size(), 5);
        assertEquals(actual.get(0), expectedQuery1);
        assertEquals(actual.get(1), expectedQuery2);
        assertEquals(actual.get(2), expectedQuery3);
        assertEquals(actual.get(3), expectedQuery4);
        assertEquals(actual.get(4), expectedQuery5);
    }

    @Test
    void testMaxCodesPerQueryNull() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 21);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);
        mockFhirRead("/ValueSet/MyValueSet/$expand", valueSet);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.generator.setExpandValueSets(true);
        this.generator.setQueryBatchThreshold(5);
        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        String expectedQuery1 = "Observation?category=http://myterm.com/fhir/CodeSystem/MyValueSet|code0,http://myterm.com/fhir/CodeSystem/MyValueSet|code1,http://myterm.com/fhir/CodeSystem/MyValueSet|code10,http://myterm.com/fhir/CodeSystem/MyValueSet|code11,http://myterm.com/fhir/CodeSystem/MyValueSet|code12,http://myterm.com/fhir/CodeSystem/MyValueSet|code13,http://myterm.com/fhir/CodeSystem/MyValueSet|code14,http://myterm.com/fhir/CodeSystem/MyValueSet|code15,http://myterm.com/fhir/CodeSystem/MyValueSet|code16,http://myterm.com/fhir/CodeSystem/MyValueSet|code17,http://myterm.com/fhir/CodeSystem/MyValueSet|code18,http://myterm.com/fhir/CodeSystem/MyValueSet|code19,http://myterm.com/fhir/CodeSystem/MyValueSet|code2,http://myterm.com/fhir/CodeSystem/MyValueSet|code20,http://myterm.com/fhir/CodeSystem/MyValueSet|code3,http://myterm.com/fhir/CodeSystem/MyValueSet|code4,http://myterm.com/fhir/CodeSystem/MyValueSet|code5,http://myterm.com/fhir/CodeSystem/MyValueSet|code6,http://myterm.com/fhir/CodeSystem/MyValueSet|code7,http://myterm.com/fhir/CodeSystem/MyValueSet|code8,http://myterm.com/fhir/CodeSystem/MyValueSet|code9&subject=Patient/{{context.patientId}}";

        assertNotNull(actual);
        assertEquals(actual.size(), 1);
        assertEquals(actual.get(0), expectedQuery1);
    }

    @Test
    void testBatchQueryThresholdNull() {
        ValueSet valueSet = getTestValueSet("MyValueSet", 21);

        org.hl7.fhir.r4.model.Bundle valueSetBundle = new org.hl7.fhir.r4.model.Bundle();
        valueSetBundle.setType(Bundle.BundleType.SEARCHSET);

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(valueSet);
        valueSetBundle.addEntry(entry);

        mockFhirRead("/ValueSet?url=http%3A%2F%2Fmyterm.com%2Ffhir%2FValueSet%2FMyValueSet", valueSetBundle);
        mockFhirRead("/ValueSet/MyValueSet/$expand", valueSet);

        DataRequirement dataRequirement = getCodeFilteredDataRequirement("Observation", "category", valueSet);

        this.generator.setExpandValueSets(true);
        this.generator.setMaxCodesPerQuery(2);

        this.contextValues.put("Patient", "{{context.patientId}}");
        java.util.List<String> actual = this.generator.generateFhirQueries(dataRequirement, this.evaluationDateTime, this.contextValues, this.parameters, null);

        assertNotNull(actual);
        assertEquals(actual.size(), 11);
    }

    @Test
    void testGetDateRangeParamWithDateType() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Date low = formatter.parse("2023-01-01");
        Date high = formatter.parse("2023-02-06");
        Interval interval = new Interval(low, true, high, true);

        Pair<String, DateRangeParam> rangeParam = this.generator.getDateRangeParam("Condition", "onset", "valueDate", "valueDate", interval);

        assertNotNull(rangeParam);
        assertTrue(rangeParam.getValue().getLowerBound().getValue().equals(low));
        assertTrue(rangeParam.getValue().getUpperBound().getValue().equals(high));
    }

    @Test
    void testGetDateRangeParamWithDateTimeType() throws ParseException {
        DateTime low = new DateTime(OffsetDateTime.parse("2023-01-01T12:01:56-07:00"));
        DateTime high = new DateTime(OffsetDateTime.parse("2023-02-06T12:08:56-07:00"));
        Interval interval = new Interval(low, true, high, true);

        Pair<String, DateRangeParam> rangeParam = this.generator.getDateRangeParam("Condition", "onset", "valueDateTime", "valueDateTime", interval);

        assertNotNull(rangeParam);
        assertTrue(rangeParam.getValue().getLowerBound().getValue().equals(low.toJavaDate()));
        assertTrue(rangeParam.getValue().getUpperBound().getValue().equals(high.toJavaDate()));
    }
}