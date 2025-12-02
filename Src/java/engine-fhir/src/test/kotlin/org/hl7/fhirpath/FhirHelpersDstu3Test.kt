package org.hl7.fhirpath

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import org.fhir.ucum.UcumException
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver

class FhirHelpersDstu3Test {
    private fun getStringFromResourceStream(resourceName: String): String? {
        val input = TestFhirPath::class.java.getResourceAsStream(resourceName)
        try {
            BufferedReader(InputStreamReader(input)).use { stringReader ->
                var line: String? = null
                val source = StringBuilder()
                while ((stringReader.readLine().also { line = it }) != null) {
                    source.append(line)
                    source.append("\n")
                }
                return source.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    // @Test
    // TODO: Resolve Error: Could not load model information for model FHIR, version
    // 3.0.0 because version 1.0.2 is already loaded
    // @Test
    @Throws(UcumException::class)
    fun testFhirHelpersStu3() {
        val cql = getStringFromResourceStream("stu3/TestFHIRHelpers.cql")
        val env = TranslatorHelper.environment
        TranslatorHelper.translate(cql, env.libraryManager!!)

        val engine = TranslatorHelper.getEngine(env)

        val libraryId = TranslatorHelper.toElmIdentifier("TestFHIRHelpers", "0.1.0")

        val modelResolver = Dstu3FhirModelResolver()
        val fhirContext = modelResolver.fhirContext
        val retrieveProvider =
            RestFhirRetrieveProvider(
                SearchParameterResolver(fhirContext),
                modelResolver,
                fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"),
            )
        val provider = CompositeDataProvider(modelResolver, retrieveProvider)
        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        engine.environment.registerDataProvider("http://hl7.org/fhir", provider)
        val results = engine.evaluate { library(libraryId) }.onlyResultOrThrow

        // TODO - fix
        var value = results["TestPeriodToInterval"]!!.value
        // Assertions.assertEquals(((DateTime)((Interval) value).getStart()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 18, 8, 0}));
        // Assertions.assertEquals(((DateTime)((Interval) value).getEnd()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 19, 8, 0}));
        value = results["TestToQuantity"]!!.value
        // TODO: ModelInfo bug. Not aware of SimpleQuantity
        value = results["TestRangeToInterval"]!!.value
        value = results["TestToCode"]!!.value
        value = results["TestToConcept"]!!.value
        value = results["TestToString"]!!.value
        value = results["TestRequestStatusToString"]!!.value
        value = results["TestToDateTime"]!!.value
        value = results["TestToTime"]!!.value
        value = results["TestToInteger"]!!.value
        value = results["TestToDecimal"]!!.value
        value = results["TestToBoolean"]!!.value
    }
}
