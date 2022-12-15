package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.fhir.model.Dstu2FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;

public class FhirHelpersDstu2Test {
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
    public void testFhirHelpersDstu2() throws UcumException {
        String cql = getStringFromResourceStream("Dstu2/TestFHIRHelpersDstu2.cql");
        Library library = translator.translate(cql);
        Context context = new Context(library);
        context.registerLibraryLoader(translator.getLibraryLoader());
        Dstu2FhirModelResolver modelResolver = new Dstu2FhirModelResolver();
        RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(
            new org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver(modelResolver.getFhirContext()),
            modelResolver, FhirContext.forCached(FhirVersionEnum.DSTU2).newRestfulGenericClient(""));
        CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
        //BaseFhirDataProvider provider = new FhirDataProviderDstu2();
        context.registerDataProvider("http://hl7.org/fhir", provider);

        // TODO - millis shouldn't be populated - issue with DateTime.fromJavaDate(Date date)
        context.resolveExpressionRef("TestPeriodToInterval").getExpression().evaluate(context);
//        Assert.assertEquals(((DateTime)((Interval) result).getStart()).getPartial(), new Partial(DateTime.getFields(7), new int[] {2017, 5, 6, 18, 8, 0, 0}));
//        Assert.assertEquals(((DateTime)((Interval) result).getEnd()).getPartial(), new Partial(DateTime.getFields(7), new int[] {2017, 5, 6, 19, 8, 0, 0}));
        context.resolveExpressionRef("TestToQuantity").getExpression().evaluate(context);
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
