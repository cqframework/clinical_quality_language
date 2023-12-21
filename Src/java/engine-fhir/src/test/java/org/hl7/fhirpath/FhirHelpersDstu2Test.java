package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.model.Dstu2FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;

public class FhirHelpersDstu2Test {

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
    public void testFhirHelpersDstu2() throws UcumException {
        String cql = getStringFromResourceStream("Dstu2/TestFHIRHelpersDstu2.cql");
        var env = TranslatorHelper.getEnvironment();
        Library library = TranslatorHelper.translate(cql, env.getLibraryManager());
        CqlEngine engine = TranslatorHelper.getEngine(env);

        VersionedIdentifier libraryId = TranslatorHelper.toElmIdentifier("TestFHIRHelpersDstu2", "0.1.0");

        Dstu2FhirModelResolver modelResolver = new Dstu2FhirModelResolver();
        RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(
                new org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver(modelResolver.getFhirContext()),
                modelResolver,
                FhirContext.forCached(FhirVersionEnum.DSTU2).newRestfulGenericClient(""));
        CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
        // BaseFhirDataProvider provider = new FhirDataProviderDstu2();
        engine.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", provider);
        EvaluationResult evaluationResult = engine.evaluate(libraryId, null, null, null, null, null);

        // TODO - millis shouldn't be populated - issue with DateTime.fromJavaDate(Date date)
        Object result = evaluationResult.forExpression("TestPeriodToInterval").value();
        //        Assert.assertEquals(((DateTime)((Interval) result).getStart()).getPartial(), new
        // Partial(DateTime.getFields(7), new int[] {2017, 5, 6, 18, 8, 0, 0}));
        //        Assert.assertEquals(((DateTime)((Interval) result).getEnd()).getPartial(), new
        // Partial(DateTime.getFields(7), new int[] {2017, 5, 6, 19, 8, 0, 0}));
        result = evaluationResult.forExpression("TestToQuantity").value();
        result = evaluationResult.forExpression("TestRangeToInterval").value();
        result = evaluationResult.forExpression("TestToCode").value();
        result = evaluationResult.forExpression("TestToConcept").value();
        result = evaluationResult.forExpression("TestToString").value();
        result = evaluationResult.forExpression("TestRequestStatusToString").value();
        result = evaluationResult.forExpression("TestToDateTime").value();
        result = evaluationResult.forExpression("TestToTime").value();
        result = evaluationResult.forExpression("TestToInteger").value();
        result = evaluationResult.forExpression("TestToDecimal").value();
        result = evaluationResult.forExpression("TestToBoolean").value();
    }
}
