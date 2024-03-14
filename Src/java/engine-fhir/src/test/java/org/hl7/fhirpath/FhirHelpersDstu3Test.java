package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;

public class FhirHelpersDstu3Test {
    private String getStringFromResourceStream(String resourceName) {
        java.io.InputStream input = TestFhirPath.class.getResourceAsStream(resourceName);
        try (BufferedReader stringReader = new BufferedReader(new InputStreamReader(input))) {
            String line = null;
            StringBuilder source = new StringBuilder();
            while ((line = stringReader.readLine()) != null) {
                source.append(line);
                source.append("\n");
            }
            return source.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // @Test
    // TODO: Resolve Error: Could not load model information for model FHIR, version
    // 3.0.0 because version 1.0.2 is already loaded
    // @Test
    public void testFhirHelpersStu3() throws UcumException {
        String cql = getStringFromResourceStream("stu3/TestFHIRHelpers.cql");
        var env = TranslatorHelper.getEnvironment();
        Library library = TranslatorHelper.translate(cql, env.getLibraryManager());

        CqlEngine engine = TranslatorHelper.getEngine(env);

        VersionedIdentifier libraryId = TranslatorHelper.toElmIdentifier("TestFHIRHelpers", "0.1.0");

        Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
        FhirContext fhirContext = modelResolver.getFhirContext();
        RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(
                new SearchParameterResolver(fhirContext),
                modelResolver,
                fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"));
        CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", provider);
        var results = engine.evaluate(libraryId);

        // TODO - fix
        Object value = results.forExpression("TestPeriodToInterval").value();
        // Assert.assertEquals(((DateTime)((Interval) value).getStart()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 18, 8, 0}));
        // Assert.assertEquals(((DateTime)((Interval) value).getEnd()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 19, 8, 0}));
        value = results.forExpression("TestToQuantity").value();
        // TODO: ModelInfo bug. Not aware of SimpleQuantity
        value = results.forExpression("TestRangeToInterval").value();
        value = results.forExpression("TestToCode").value();
        value = results.forExpression("TestToConcept").value();
        value = results.forExpression("TestToString").value();
        value = results.forExpression("TestRequestStatusToString").value();
        value = results.forExpression("TestToDateTime").value();
        value = results.forExpression("TestToTime").value();
        value = results.forExpression("TestToInteger").value();
        value = results.forExpression("TestToDecimal").value();
        value = results.forExpression("TestToBoolean").value();
    }
}
