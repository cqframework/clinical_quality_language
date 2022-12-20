package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        Context context = new Context(library);
        context.registerLibraryLoader(translator.getLibraryLoader());

        Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
        FhirContext fhirContext = modelResolver.getFhirContext();
        RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(fhirContext),
            modelResolver, fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"));
        CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
        // BaseFhirDataProvider provider = new
        // FhirDataProviderStu3().setEndpoint("http://fhirtest.uhn.ca/baseDstu3");
        context.registerDataProvider("http://hl7.org/fhir", provider);

        // TODO - fix
        context.resolveExpressionRef("TestPeriodToInterval").getExpression().evaluate(context);
        // Assert.assertEquals(((DateTime)((Interval) result).getStart()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 18, 8, 0}));
        // Assert.assertEquals(((DateTime)((Interval) result).getEnd()).getPartial(),
        // new Partial(DateTime.getFields(6), new int[] {2017, 5, 6, 19, 8, 0}));
        context.resolveExpressionRef("TestToQuantity").getExpression().evaluate(context);
        // TODO: ModelInfo bug. Not aware of SimpleQuantity
        context.resolveExpressionRef("TestRangeToInterval").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToCode").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToConcept").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToString").getExpression().evaluate(context);
        context.resolveExpressionRef("TestRequestStatusToString").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToDateTime").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToTime").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToInteger").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToDecimal").getExpression().evaluate(context);
        context.resolveExpressionRef("TestToBoolean").getExpression().evaluate(context);
    }


}
