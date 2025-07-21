package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.execution.ExpressionResult;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EvaluatedResourceTestUtils {

    static final Encounter ENCOUNTER = (Encounter)new Encounter()
            .setId(new IdType(ResourceType.Encounter.name(), "Encounter1"));

    static final Condition CONDITION = (Condition)new Condition()
            .setId(new IdType(ResourceType.Condition.name(), "Condition1"));

    static final Patient PATIENT = (Patient)new Patient()
            .setId(new IdType(ResourceType.Patient.name(), "Patient1"));

    static final RetrieveProvider RETRIEVE_PROVIDER = (
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
            dateRange) -> switch (dataType) {
        case "Encounter" -> singletonList(ENCOUNTER);
        case "Condition" -> singletonList(CONDITION);
        case "Patient" -> singletonList(PATIENT);
        default -> null;
    };

    static void assertEntireEvaluationResult(
            EvaluationResult evaluationResult,
            Map<String,Collection<? extends IBaseResource>> expectedEvaluatedResources,
            Map<String,Collection<? extends IBaseResource>> expectedValues) {

//        var expressionResult = evaluationResult.forExpression(expressionName);
//        var actualEvaluatedResources = expressionResult.evaluatedResources();
//        var actualValue = expressionResult.value();
//
//        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources);
//        assertValuesEqual(expectedEvaluatedResources, actualValue);
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
                .sorted((r1, r2) -> r1.getIdElement().getIdPart().compareTo(r2.getIdElement().getIdPart()))
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
