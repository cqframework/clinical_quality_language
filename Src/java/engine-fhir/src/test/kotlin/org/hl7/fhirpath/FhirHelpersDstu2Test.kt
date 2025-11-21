package org.hl7.fhirpath

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import org.fhir.ucum.UcumException
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.fhir.model.Dstu2FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver

class FhirHelpersDstu2Test {
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
    @Throws(UcumException::class)
    fun testFhirHelpersDstu2() {
        val cql = getStringFromResourceStream("Dstu2/TestFHIRHelpersDstu2.cql")
        val env = TranslatorHelper.environment
        TranslatorHelper.translate(cql, env.libraryManager!!)
        val engine = TranslatorHelper.getEngine(env)

        val libraryId = TranslatorHelper.toElmIdentifier("TestFHIRHelpersDstu2", "0.1.0")

        val modelResolver = Dstu2FhirModelResolver()
        val retrieveProvider =
            RestFhirRetrieveProvider(
                SearchParameterResolver(modelResolver.fhirContext),
                modelResolver,
                FhirContext.forCached(FhirVersionEnum.DSTU2).newRestfulGenericClient(""),
            )
        val provider = CompositeDataProvider(modelResolver, retrieveProvider)
        // BaseFhirDataProvider provider = new FhirDataProviderDstu2();
        engine.environment.registerDataProvider("http://hl7.org/fhir", provider)
        val results = engine.evaluate { library(libraryId) }.onlyResultOrThrow

        // TODO - millis shouldn't be populated - issue with DateTime.fromJavaDate(Date date)
        var value = results.forExpression("TestPeriodToInterval")!!.value
        //        Assertions.assertEquals(((DateTime)((Interval) value).getStart()).getPartial(),
        // new
        // Partial(DateTime.getFields(7), new int[] {2017, 5, 6, 18, 8, 0, 0}));
        //        Assertions.assertEquals(((DateTime)((Interval) value).getEnd()).getPartial(), new
        // Partial(DateTime.getFields(7), new int[] {2017, 5, 6, 19, 8, 0, 0}));
        value = results.forExpression("TestToQuantity")!!.value
        value = results.forExpression("TestRangeToInterval")!!.value
        value = results.forExpression("TestToCode")!!.value
        value = results.forExpression("TestToConcept")!!.value
        value = results.forExpression("TestToString")!!.value
        value = results.forExpression("TestRequestStatusToString")!!.value
        value = results.forExpression("TestToDateTime")!!.value
        value = results.forExpression("TestToTime")!!.value
        value = results.forExpression("TestToInteger")!!.value
        value = results.forExpression("TestToDecimal")!!.value
        value = results.forExpression("TestToBoolean")!!.value
    }
}
