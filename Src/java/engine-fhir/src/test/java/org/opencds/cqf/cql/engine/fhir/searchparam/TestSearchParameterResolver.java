package org.opencds.cqf.cql.engine.fhir.searchparam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.RuntimeSearchParam;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.RestSearchParameterTypeEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class TestSearchParameterResolver {
    @Test
    void returnsNullPathReturnsNull() {
        SearchParameterResolver resolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        RuntimeSearchParam param = resolver.getSearchParameterDefinition("Patient", null);
        assertNull(param);
    }

    @Test
    void nullDataTypeReturnsNull() {
        SearchParameterResolver resolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        RuntimeSearchParam param = resolver.getSearchParameterDefinition(null, "code");
        assertNull(param);
    }

    @Test
    void dstu3SearchParams() {
        SearchParameterResolver resolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        RuntimeSearchParam param = resolver.getSearchParameterDefinition("Patient", "id");
        assertNotNull(param);
        assertEquals("_id", param.getName());

        param = resolver.getSearchParameterDefinition(
                "MedicationAdministration", "medication", RestSearchParameterTypeEnum.TOKEN);
        assertNotNull(param);
        assertEquals("code", param.getName());

        param = resolver.getSearchParameterDefinition(
                "MedicationAdministration", "medication", RestSearchParameterTypeEnum.REFERENCE);
        assertNotNull(param);
        assertEquals("medication", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "period");
        assertNotNull(param);
        assertEquals("date", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "reason");
        assertNotNull(param);
        assertEquals("reason", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "subject");
        assertNotNull(param);
        assertEquals("patient", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "type");
        assertNotNull(param);
        assertEquals("type", param.getName());
    }

    @Test
    void dstu3DateSearchParams() {
        SearchParameterResolver resolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        RuntimeSearchParam param = resolver.getSearchParameterDefinition(
                "ProcedureRequest", "authoredOn", RestSearchParameterTypeEnum.DATE);
        assertNotNull(param);
        assertEquals("authored", param.getName());
    }

    @Test
    void r4SearchParams() {
        SearchParameterResolver resolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.R4));

        RuntimeSearchParam param = resolver.getSearchParameterDefinition("Patient", "id");
        assertNotNull(param);
        assertEquals("_id", param.getName());

        param = resolver.getSearchParameterDefinition(
                "MedicationAdministration", "medication", RestSearchParameterTypeEnum.TOKEN);
        assertNotNull(param);
        assertEquals("code", param.getName());

        param = resolver.getSearchParameterDefinition(
                "MedicationAdministration", "medication", RestSearchParameterTypeEnum.REFERENCE);
        assertNotNull(param);
        assertEquals("medication", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "period");
        assertNotNull(param);
        assertEquals("date", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "reasonCode");
        assertNotNull(param);
        assertEquals("reason-code", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "subject");
        assertNotNull(param);
        assertEquals("subject", param.getName());

        param = resolver.getSearchParameterDefinition("Encounter", "type");
        assertNotNull(param);
        assertEquals("type", param.getName());

        param = resolver.getSearchParameterDefinition("Observation", "code");
        assertNotNull(param);
        assertEquals("code", param.getName());

        param = resolver.getSearchParameterDefinition("Observation", "subject");
        assertNotNull(param);
        assertEquals("subject", param.getName());
    }

    @Test
    void r4DateSearchParams() {
        SearchParameterResolver resolver = new SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.R4));

        RuntimeSearchParam param =
                resolver.getSearchParameterDefinition("ServiceRequest", "authoredOn", RestSearchParameterTypeEnum.DATE);
        assertNotNull(param);
        assertEquals("authored", param.getName());

        param = resolver.getSearchParameterDefinition("Condition", "onset", RestSearchParameterTypeEnum.DATE);
        assertNotNull(param);
        assertEquals("onset-date", param.getName());

        param = resolver.getSearchParameterDefinition("Condition", "abatement", RestSearchParameterTypeEnum.DATE);
        assertNotNull(param);
        assertEquals("abatement-date", param.getName());

        param = resolver.getSearchParameterDefinition("Observation", "effective", RestSearchParameterTypeEnum.DATE);
        assertNotNull(param);
        assertEquals("date", param.getName());

        param = resolver.getSearchParameterDefinition("Observation", "value", RestSearchParameterTypeEnum.DATE);
        assertNotNull(param);
        assertEquals("value-date", param.getName());
    }

    @Test
    void r4ReferenceParameter() {
        FhirContext context = FhirContext.forCached(FhirVersionEnum.R4);
        SearchParameterResolver resolver = new SearchParameterResolver(context);
        Pair<String, IQueryParameterType> actual =
                resolver.createSearchParameter("Patient", "Observation", "subject", "123");

        assertEquals("Patient/123", actual.getRight().getValueAsQueryToken(context));
    }

    @Test
    void r4TokenParameter() {
        FhirContext context = FhirContext.forCached(FhirVersionEnum.R4);
        SearchParameterResolver resolver = new SearchParameterResolver(context);
        Pair<String, IQueryParameterType> actual =
                resolver.createSearchParameter("Patient", "Observation", "code", "123");

        assertEquals("123", actual.getRight().getValueAsQueryToken(context));
    }
}
