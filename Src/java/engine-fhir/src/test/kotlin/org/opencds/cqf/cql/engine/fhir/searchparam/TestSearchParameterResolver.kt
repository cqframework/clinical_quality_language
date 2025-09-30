package org.opencds.cqf.cql.engine.fhir.searchparam

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.api.RestSearchParameterTypeEnum
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TestSearchParameterResolver {
    @Test
    fun returnsNullPathReturnsNull() {
        val resolver = SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val param = resolver.getSearchParameterDefinition("Patient", null)
        Assertions.assertNull(param)
    }

    @Test
    fun nullDataTypeReturnsNull() {
        val resolver = SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val param = resolver.getSearchParameterDefinition(null, "code")
        Assertions.assertNull(param)
    }

    @Test
    fun dstu3SearchParams() {
        val resolver = SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        var param = resolver.getSearchParameterDefinition("Patient", "id")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("_id", param!!.name)

        param =
            resolver.getSearchParameterDefinition(
                "MedicationAdministration",
                "medication",
                RestSearchParameterTypeEnum.TOKEN,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("code", param.name)

        param =
            resolver.getSearchParameterDefinition(
                "MedicationAdministration",
                "medication",
                RestSearchParameterTypeEnum.REFERENCE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("medication", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "period")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("date", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "reason")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("reason", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "subject")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("patient", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "type")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("type", param.name)
    }

    @Test
    fun dstu3DateSearchParams() {
        val resolver = SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val param =
            resolver.getSearchParameterDefinition(
                "ProcedureRequest",
                "authoredOn",
                RestSearchParameterTypeEnum.DATE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("authored", param!!.name)
    }

    @Test
    fun r4SearchParams() {
        val resolver = SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.R4))

        var param = resolver.getSearchParameterDefinition("Patient", "id")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("_id", param!!.name)

        param =
            resolver.getSearchParameterDefinition(
                "MedicationAdministration",
                "medication",
                RestSearchParameterTypeEnum.TOKEN,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("code", param.name)

        param =
            resolver.getSearchParameterDefinition(
                "MedicationAdministration",
                "medication",
                RestSearchParameterTypeEnum.REFERENCE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("medication", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "period")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("date", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "reasonCode")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("reason-code", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "subject")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("subject", param.name)

        param = resolver.getSearchParameterDefinition("Encounter", "type")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("type", param.name)

        param = resolver.getSearchParameterDefinition("Observation", "code")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("code", param.name)

        param = resolver.getSearchParameterDefinition("Observation", "subject")
        Assertions.assertNotNull(param)
        Assertions.assertEquals("subject", param.name)
    }

    @Test
    fun r4DateSearchParams() {
        val resolver = SearchParameterResolver(FhirContext.forCached(FhirVersionEnum.R4))

        var param =
            resolver.getSearchParameterDefinition(
                "ServiceRequest",
                "authoredOn",
                RestSearchParameterTypeEnum.DATE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("authored", param!!.name)

        param =
            resolver.getSearchParameterDefinition(
                "Condition",
                "onset",
                RestSearchParameterTypeEnum.DATE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("onset-date", param.name)

        param =
            resolver.getSearchParameterDefinition(
                "Condition",
                "abatement",
                RestSearchParameterTypeEnum.DATE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("abatement-date", param.name)

        param =
            resolver.getSearchParameterDefinition(
                "Observation",
                "effective",
                RestSearchParameterTypeEnum.DATE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("date", param.name)

        param =
            resolver.getSearchParameterDefinition(
                "Observation",
                "value",
                RestSearchParameterTypeEnum.DATE,
            )
        Assertions.assertNotNull(param)
        Assertions.assertEquals("value-date", param.name)
    }

    @Test
    fun r4ReferenceParameter() {
        val context = FhirContext.forCached(FhirVersionEnum.R4)
        val resolver = SearchParameterResolver(context)
        val actual = resolver.createSearchParameter("Patient", "Observation", "subject", "123")

        Assertions.assertEquals("Patient/123", actual.getRight()!!.getValueAsQueryToken(context))
    }

    @Test
    fun r4TokenParameter() {
        val context = FhirContext.forCached(FhirVersionEnum.R4)
        val resolver = SearchParameterResolver(context)
        val actual = resolver.createSearchParameter("Patient", "Observation", "code", "123")

        Assertions.assertEquals("123", actual.getRight()!!.getValueAsQueryToken(context))
    }
}
