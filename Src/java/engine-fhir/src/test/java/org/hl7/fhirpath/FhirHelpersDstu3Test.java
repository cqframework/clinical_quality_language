package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.elm.r1.Library;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FhirHelpersDstu3Test {
    private TranslatorHelper translator = new TranslatorHelper();

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
    //@Test
    public void testFhirHelpersStu3() throws UcumException {
        String cql = getStringFromResourceStream("stu3/TestFHIRHelpers.cql");
        Library library = translator.translate(cql);

        CqlEngine engineVisitor = TranslatorHelper.getEngineVisitor();

        VersionedIdentifier libraryId = TranslatorHelper.toElmIdentifier("TestFHIRHelpers",  "0.1.0");
        Map<VersionedIdentifier, Library> map = new HashMap<>();
        map.put(libraryId, library);

        Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
        FhirContext fhirContext = modelResolver.getFhirContext();
        RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(fhirContext),
            modelResolver, fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"));
        CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", provider);
        EvaluationResult evaluationResult = engineVisitor.evaluate(libraryId, map,
                null, null, null, null, null);

        // TODO - fix
        Object result = evaluationResult.expressionResults.get("TestPeriodToInterval").value();
        // Assert.assertEquals(((DateTime)((Interval) result).getStart()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 18, 8, 0}));
        // Assert.assertEquals(((DateTime)((Interval) result).getEnd()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 19, 8, 0}));
        result = evaluationResult.expressionResults.get("TestToQuantity").value();
        // TODO: ModelInfo bug. Not aware of SimpleQuantity
        result = evaluationResult.expressionResults.get("TestRangeToInterval").value();
        result = evaluationResult.expressionResults.get("TestToCode").value();
        result = evaluationResult.expressionResults.get("TestToConcept").value();
        result = evaluationResult.expressionResults.get("TestToString").value();
        result = evaluationResult.expressionResults.get("TestRequestStatusToString").value();
        result = evaluationResult.expressionResults.get("TestToDateTime").value();
        result = evaluationResult.expressionResults.get("TestToTime").value();
        result = evaluationResult.expressionResults.get("TestToInteger").value();
        result = evaluationResult.expressionResults.get("TestToDecimal").value();
        result = evaluationResult.expressionResults.get("TestToBoolean").value();
    }


}
