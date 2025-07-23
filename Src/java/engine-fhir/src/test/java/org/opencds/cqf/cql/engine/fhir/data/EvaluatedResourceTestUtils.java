package org.opencds.cqf.cql.engine.fhir.data;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluatedResourceTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(EvaluatedResourceTestUtils.class);

    static final Encounter ENCOUNTER =
            (Encounter) new Encounter().setId(new IdType(ResourceType.Encounter.name(), "Encounter1"));

    static final Condition CONDITION =
            (Condition) new Condition().setId(new IdType(ResourceType.Condition.name(), "Condition1"));

    static final Patient PATIENT = (Patient) new Patient().setId(new IdType(ResourceType.Patient.name(), "Patient1"));

    static final Procedure PROCEDURE =
            (Procedure) new Procedure().setId(new IdType(ResourceType.Procedure.name(), "Procedure1"));

    // LUKETODO: if I make this more sophisticated, I can probably change the evaluated resources per library
    static final RetrieveProvider RETRIEVE_PROVIDER =
            (context,
                    contextPath,
                    contextValue,
                    dataType,
                    templateId,
                    codePath,
                    codes,
                    valueSet,
                    datePath,
                    dateLowPath,
                    dateHighPath,
                    dateRange) -> {
                logger.info(
                        "1234: RetrieveProvider: context: {}, contextPath: {}, contextValue: {}, dataType: {}, templateId: {}, codePath: {}, codes: {}, valueSet: {}, datePath: {}, dateLowPath: {}, dateHighPath: {}, dateRange: {}",
                        context,
                        contextPath,
                        contextValue,
                        dataType,
                        templateId,
                        codePath,
                        codes,
                        valueSet,
                        datePath,
                        dateLowPath,
                        dateHighPath,
                        dateRange);
                return switch (dataType) {
                    case "Encounter" -> singletonList(ENCOUNTER);
                    case "Condition" -> singletonList(CONDITION);
                    case "Patient" -> singletonList(PATIENT);
                    case "Procedure" -> singletonList(PROCEDURE);
                    default -> List.of();
                };
            };

    private static class TestRetrieveProvider implements RetrieveProvider {

        static final Encounter ENCOUNTER = (Encounter) new Encounter()
                .setPeriod(new Period().setStart(null).setEnd(null))
                .setId(new IdType(ResourceType.Encounter.name(), "Encounter1"));

        static final Condition CONDITION =
                (Condition) new Condition().setId(new IdType(ResourceType.Condition.name(), "Condition1"));

        static final Patient PATIENT =
                (Patient) new Patient().setId(new IdType(ResourceType.Patient.name(), "Patient1"));

        static final Procedure PROCEDURE =
                (Procedure) new Procedure().setId(new IdType(ResourceType.Procedure.name(), "Procedure1"));

        @Override
        public Iterable<Object> retrieve(
                String context,
                String contextPath,
                Object contextValue,
                String dataType,
                String templateId,
                String codePath,
                Iterable<Code> codes,
                String valueSet,
                String datePath,
                String dateLowPath,
                String dateHighPath,
                Interval dateRange) {
            return null;
        }
    }

    @Nonnull
    static CqlEngine getCqlEngineForFhir(
            CqlEngine cqlEngine,
            boolean expressionCaching,
            R4FhirModelResolver r4ModelResolver,
            RetrieveProvider retrieveProvider) {
        cqlEngine
                .getState()
                .getEnvironment()
                .registerDataProvider(
                        "http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, retrieveProvider));
        cqlEngine.getCache().setExpressionCaching(expressionCaching);
        return cqlEngine;
    }

    static void assertEntireEvaluationResult(
            EvaluationResult evaluationResult,
            Map<String, Collection<? extends IBaseResource>> expectedEvaluatedResources,
            Map<String, Collection<? extends IBaseResource>> expectedValues) {

        var expressionResults = evaluationResult.expressionResults;

        for (String expressionName : expressionResults.keySet()) {
            var expressionResult = expressionResults.get(expressionName);

            var actualEvaluatedResourcesForName = expressionResult.evaluatedResources();
            var expectedEvaluatedResourcesForName = expectedEvaluatedResources.get(expressionName);

            assertResourcesEqual(expectedEvaluatedResourcesForName, actualEvaluatedResourcesForName);

            var actualValue = expressionResult.value();
            var expectedValue = expectedValues.get(expressionName);

            assertValuesEqual(expectedValue, actualValue);
        }
    }

    static void assertEvaluationResult(
            EvaluationResult evaluationResult,
            String expressionName,
            Collection<? extends IBaseResource> expectedEvaluatedResources) {

        var expressionResult = evaluationResult.forExpression(expressionName);
        var actualEvaluatedResources = expressionResult.evaluatedResources();
        var actualValue = expressionResult.value();

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources);
        assertValuesEqual(expectedEvaluatedResources, actualValue);
    }

    static void assertEvaluationResult(
            EvaluationResult evaluationResult,
            String expressionName,
            Collection<? extends IBaseResource> expectedEvaluatedResources,
            Collection<? extends IBaseResource> expectedValue) {

        var expressionResult = evaluationResult.forExpression(expressionName);
        var actualEvaluatedResources = expressionResult.evaluatedResources();
        var actualValue = expressionResult.value();

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources);
        assertValuesEqual(expectedValue, actualValue);
    }

    private static List<IBaseResource> extractResourcesInOrder(Collection<?> resourceCandidates) {
        return resourceCandidates.stream()
                .filter(IBaseResource.class::isInstance)
                .map(IBaseResource.class::cast)
                .sorted((r1, r2) -> r1.getIdElement()
                        .getIdPart()
                        .compareTo(r2.getIdElement().getIdPart()))
                .toList();
    }

    private static void assertValuesEqual(Collection<? extends IBaseResource> expectedValue, Object actualValue) {
        assertThat(actualValue, instanceOf(List.class));
        var actualValues = (List<?>) actualValue;

        assertResourcesEqual(expectedValue, actualValues);
    }

    private static void assertResourcesEqual(Collection<?> expectedResources, Collection<?> actualResources) {
        assertThat(actualResources.size(), is(expectedResources.size()));

        var expectedResourcesList = extractResourcesInOrder(expectedResources);
        var actualResourcesList = extractResourcesInOrder(actualResources);

        for (int index = 0; index < expectedResourcesList.size(); index++) {
            var expectedResource = expectedResourcesList.get(0);
            var actualResource = actualResourcesList.get(0);

            assertResourcesEqual(expectedResource, actualResource);
        }
    }

    private static void assertResourcesEqual(IBaseResource expectedResource, IBaseResource actualResource) {
        assertThat(actualResource.getClass(), equalTo(expectedResource.getClass()));

        assertThat(actualResource.getIdElement(), equalTo(expectedResource.getIdElement()));
    }
}
