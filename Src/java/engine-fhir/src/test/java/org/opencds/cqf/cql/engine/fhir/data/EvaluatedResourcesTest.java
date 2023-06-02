package org.opencds.cqf.cql.engine.fhir.data;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;



public class EvaluatedResourcesTest extends FhirExecutionTestBase {

    private static RetrieveProvider rp = new RetrieveProvider() {

        @Override
        public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
                String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
                String dateLowPath, String dateHighPath, Interval dateRange) {
            switch(dataType) {
                case "Encounter": return singletonList(new Encounter());
                case "Condition": return singletonList(new Condition());
                case "Patient": return singletonList(new Patient());
                default: break;
            }

            return null;
        }
    };

    @Test
    public void testWithCache() {
        Context context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        context.registerLibraryLoader(new TestLibraryLoader(libraries));
        context.setExpressionCaching(true);

        Object result = context.resolveExpressionRef("Union").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(2));
        context.clearEvaluatedResources();

        result = context.resolveExpressionRef("Encounter").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(1));
        context.clearEvaluatedResources();

        result = context.resolveExpressionRef("Condition").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(1));
        context.clearEvaluatedResources();

        result = context.resolveExpressionRef("Union").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(2));
        context.clearEvaluatedResources();
    }

    @Test
    public void testWithoutCache() {
        Context context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        context.registerLibraryLoader(new TestLibraryLoader(libraries));

        Object result = context.resolveExpressionRef("Union").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(2));
        context.clearEvaluatedResources();

        result = context.resolveExpressionRef("Encounter").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(1));
        context.clearEvaluatedResources();

        result = context.resolveExpressionRef("Condition").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(1));
        context.clearEvaluatedResources();

        result = context.resolveExpressionRef("Union").evaluate(context);
        assertThat(result, instanceOf(List.class));
        assertThat(context.getEvaluatedResources().size(), is(2));
        context.clearEvaluatedResources();
    }
}
