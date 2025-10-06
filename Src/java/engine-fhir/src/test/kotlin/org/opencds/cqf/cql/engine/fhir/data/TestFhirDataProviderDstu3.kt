package org.opencds.cqf.cql.engine.fhir.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.client.api.IGenericClient
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.ListResource
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.retrieve.FhirBundleCursor
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.fhir.terminology.Dstu3FhirTerminologyProvider

class TestFhirDataProviderDstu3 : FhirExecutionTestBase() {
    private val fhirContext: FhirContext = FhirContext.forCached(FhirVersionEnum.DSTU3)
    private val fhirClient: IGenericClient =
        fhirContext.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3")

    // @Test
    fun testFhirClient() {
        val patients =
            fhirClient
                .search<IBaseBundle?>()
                .forResource("Patient")
                .returnBundle(Bundle::class.java)
                .execute()
        Assertions.assertTrue(patients.getEntry().isNotEmpty())
    }

    // @Test
    fun testDataProviderRetrieve() {
        val modelResolver: Dstu3FhirModelResolver = CachedDstu3FhirModelResolver()
        val retrieveProvider =
            RestFhirRetrieveProvider(
                SearchParameterResolver(fhirContext),
                modelResolver,
                fhirContext.newRestfulGenericClient(
                    "http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"
                ),
            )
        val provider = CompositeDataProvider(modelResolver, retrieveProvider)
        val contextPath: String = modelResolver.getContextPath("Patient", "Patient").toString()
        val results =
            provider.retrieve(
                "Patient",
                contextPath,
                null,
                "Patient",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            ) as FhirBundleCursor

        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
        // FhirBundleCursorStu3 results = (FhirBundleCursorStu3) provider.retrieve("Patient", null,
        // "Patient", null,
        // null, null, null, null, null, null, null);
        Assertions.assertTrue(results.iterator().hasNext())
    }

    // @Test
    fun testPatientRetrieve() {
        val modelResolver: Dstu3FhirModelResolver = CachedDstu3FhirModelResolver()
        val retrieveProvider =
            RestFhirRetrieveProvider(
                SearchParameterResolver(fhirContext),
                modelResolver,
                fhirContext.newRestfulGenericClient(
                    "http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"
                ),
            )
        val provider = CompositeDataProvider(modelResolver, retrieveProvider)
        val contextPath: String = modelResolver.getContextPath("Patient", "Patient").toString()
        val results =
            provider.retrieve(
                "Patient",
                contextPath,
                "Patient-12214",
                "Patient",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )!!

        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
        // Iterable<Object> results = provider.retrieve("Patient", "Patient-12214", "Patient", null,
        // null, null, null,
        // null, null, null, null);
        val patients: MutableList<Patient?> = ArrayList()

        var resultCount = 0
        for (o in results) {
            patients.add(o as Patient?)
            resultCount++
        }

        Assertions.assertEquals(patients.size, resultCount)
    }

    // @Test
    fun testChoiceTypes() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        val results = engine.evaluate(library!!.identifier!!, mutableSetOf("testChoiceTypes"))

        val value = results.forExpression("testChoiceTypes")!!.value()
        Assertions.assertNotNull(value)
    }

    // @Test
    fun testDateType() {
        engine.state.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        engine.state.setContextValue("Patient", "Patient-12214")
        val results = engine.evaluate(library!!.identifier!!, mutableSetOf("testDateType"))

        val value = results.forExpression("testDateType")!!.value()
        Assertions.assertNotNull(value)
    }

    @Test
    fun fhirObjectEqual() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        val results = engine.evaluate(library!!.identifier!!, mutableSetOf("testFhirObjectEqual"))
        val value = results.forExpression("testFhirObjectEqual")!!.value()
        Assertions.assertTrue((value as Boolean?)!!)
    }

    @Test
    fun fhirObjectEquivalent() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        val results =
            engine.evaluate(library!!.identifier!!, mutableSetOf("testFhirObjectEquivalent"))
        val value = results.forExpression("testFhirObjectEquivalent")!!.value()
        Assertions.assertTrue((value as Boolean?)!!)
    }

    //    TODO - fix
    //    @Test
    // public void testPostSearch() {
    //     Context context = new Context(library);
    //     String patientId = "post-search-example";
    //     Patient patient = new Patient();
    //     dstu3Provider.fhirClient.update().resource(patient).withId(patientId).execute();
    //     MedicationRequest request = new MedicationRequest();
    //     request.setIntent(MedicationRequest.MedicationRequestIntent.ORDER)
    //             .setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE)
    //             .setMedication(new CodeableConcept().addCoding(new
    // Coding().setCode("1049502").setSystem("http://www.nlm.nih.gov/research/umls/rxnorm")))
    //             .setSubject(new Reference("Patient/" + patientId))
    //             .setAuthoredOn(new Date());
    //     dstu3Provider.fhirClient.update().resource(request).withId(patientId).execute();
    //     dstu3Provider.setSearchUsingPOST(true);
    //     dstu3Provider.setTerminologyProvider(new
    // FhirTerminologyProvider().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3",
    // false));
    //     context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);
    //     context.enterContext("Patient");
    //     context.setContextValue("Patient", patientId);
    //     var value = context.resolveExpressionRef("Active Ambulatory Opioid
    // Rx").getExpression().evaluate(context);
    //     Assertions.assertTrue(value instanceof List && ((List) value).size() == 1);
    // }
    // @Test
    fun testList() {
        val modelResolver: Dstu3FhirModelResolver = CachedDstu3FhirModelResolver()
        val retrieveProvider =
            RestFhirRetrieveProvider(
                SearchParameterResolver(fhirContext),
                modelResolver,
                fhirContext.newRestfulGenericClient("http://fhir.hl7.de:8080/baseDstu3"),
            )
        val provider = CompositeDataProvider(modelResolver, retrieveProvider)
        val contextPath: String = modelResolver.getContextPath("Patient", "List").toString()
        val results =
            provider.retrieve(
                "Patient",
                contextPath,
                null,
                "List",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )!!

        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://fhir.hl7.de:8080/baseDstu3");
        // FhirBundleCursorStu3 results = (FhirBundleCursorStu3) provider.retrieve("Patient", null,
        // "List", null, null,
        // null, null, null, null, null, null);
        val lists: MutableList<ListResource?> = ArrayList()
        var resultCount = 0
        for (o in results) {
            lists.add(o as ListResource?)
            resultCount++
        }

        Assertions.assertEquals(lists.size, resultCount)
    }

    // @Test
    fun testContained() {
        val patient =
            ("{  \n" +
                "        \"resourceType\":\"Patient\",\n" +
                "        \"id\":\"81ee6581-02b9-44de-b026-7401bf36643a\",\n" +
                "        \"meta\":{  \n" +
                "          \"profile\":[  \n" +
                "            \"http://hl7.org/fhir/profiles/Patient\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"birthDate\":\"2012-01-01\"\n" +
                "      }")

        fhirClient
            .update()
            .resource(patient)
            .withId("81ee6581-02b9-44de-b026-7401bf36643a")
            .execute()

        val condition =
            ("{  \n" +
                "        \"resourceType\":\"Condition\",\n" +
                "        \"id\":\"77d90968-1965-4574-aa34-19d7d1483d8a\",\n" +
                "        \"contained\":[  \n" +
                "          {  \n" +
                "            \"resourceType\":\"Provenance\",\n" +
                "            \"id\":\"c76ceb3b-ff93-4d4a-ae1f-83b78ce39228\",\n" +
                "            \"target\":[  \n" +
                "              {  \n" +
                "                \"reference\":\"Condition/77d90968-1965-4574-aa34-19d7d1483d8a\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"entity\":[  \n" +
                "              {  \n" +
                "                \"role\":\"source\",\n" +
                "                \"whatReference\":{  \n" +
                "                  \"reference\":\"Claim/920013f1-da9b-42ec-89ec-b50069a7aa5c\"\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"clinicalStatus\":\"active\",\n" +
                "        \"verificationStatus\":\"confirmed\",\n" +
                "        \"code\":{  \n" +
                "          \"coding\":[  \n" +
                "            {  \n" +
                "              \"system\":\"ICD-10-CM\",\n" +
                "              \"code\":\"Z00.00\",\n" +
                "              \"display\":\"HEDIS2019_Ambulatory_Visits_ValueSets\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"subject\":{  \n" +
                "          \"reference\":\"Patient/81ee6581-02b9-44de-b026-7401bf36643a\"\n" +
                "        },\n" +
                "        \"onsetDateTime\":\"2018-01-01\", \n" +
                "        \"evidence\":[   \n" +
                "            {  \n" +
                "               \"detail\":{   \n" +
                "                  \"reference\": \"#c76ceb3b-ff93-4d4a-ae1f-83b78ce39228\"   \n" +
                "               }  \n" +
                "            }  \n" +
                "         ]  \n" +
                "      }")

        fhirClient
            .update()
            .resource(condition)
            .withId("77d90968-1965-4574-aa34-19d7d1483d8a")
            .execute()

        dstu3RetrieveProvider!!.terminologyProvider = (Dstu3FhirTerminologyProvider(fhirClient))
        // dstu3Provider.setTerminologyProvider(new
        // FhirTerminologyProvider().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3", false));
        engine.state.environment.registerDataProvider("http://hl7.org/fhir", dstu3Provider)
        engine.state.enterContext("Patient")
        engine.state.setContextValue("Patient", "81ee6581-02b9-44de-b026-7401bf36643a")

        val results = engine.evaluate(library!!.identifier!!, mutableSetOf("GetProvenance"))
        val value = results.forExpression("GetProvenance")!!.value()
        Assertions.assertTrue(value is MutableList<*> && value.size == 1)
    }
}
