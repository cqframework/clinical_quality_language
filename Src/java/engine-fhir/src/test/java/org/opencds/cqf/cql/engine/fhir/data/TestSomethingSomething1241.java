package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.CodeableConcept;
import org.hl7.fhir.Observation;
import org.hl7.fhir.RelatedPerson;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestSomethingSomething1241 extends FhirExecutionTestBase {
    private static final String OBSERVATION = "Observation";
    private static final String URL_FHIR = "http://hl7.org/fhir";

    private static final RetrieveProvider retrieveProvider = (context, contextPath, contextValue, dataType, templateId, codePath,
                                               codes, valueSet, datePath, dateLowPath, dateHighPath, dateRange) -> {
        final RelatedPerson relatedPerson = getRelatedPerson();

        final Patient patient1 = getPatient(null);
        final Patient patient2 = getPatient(null);

        final Observation observation1 = getObservation();
        final Observation observation2 = getObservation();

        switch (dataType) {
            case "RelatedPerson":
                // LUKETODO:  we may need IDs here
                return List.of(relatedPerson);
            case "Patent":
                // LUKETODO:  we may need IDs here
                return List.of(patient1, patient2);
            case OBSERVATION:
                // LUKETODO:  we may need IDs here
                return List.of(observation1, observation2);
            case "Mother Relationship":
                throw new RuntimeException("something");
            case "Estimated Due Date Exam":
                throw new RuntimeException("somethingElse");
            default:
                break;
        }
        return null;
    };

    // LUKETODO:  better name
    // LUKETODO:  the case class resolves its CQL file from the name of the class
    /*
    Basically, you'll need a couple Patients, a RelatedPerson, and a couple Observations.
     */
    @Test
    public void test1241() {

        // LUKETODO:  private static final?

        final CqlEngine engine = getEngine();

        engine.getState().getEnvironment().registerDataProvider(URL_FHIR, new CompositeDataProvider(r4ModelResolver, retrieveProvider));
        engine.getCache().setExpressionCaching(true);

        final EvaluationResult evaluationResult = engine.evaluate(library.getIdentifier(),
                Set.of(OBSERVATION), null, null, null, null);
        final Object result = evaluationResult.forExpression(OBSERVATION).value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.forExpression(OBSERVATION).evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();
    }

    @Nonnull
    private static Observation getObservation() {
        final Observation observation = new Observation();

        return observation;
    }

    @Nonnull
    private static Patient getPatient(Date birthDate) {
        final Patient patient = new Patient();
        patient.setBirthDate(birthDate);
        return patient;
    }

    @Nonnull
    private static RelatedPerson getRelatedPerson() {
        final RelatedPerson relatedPerson = new RelatedPerson();

        final CodeableConcept motherRelationship = new CodeableConcept();
        org.hl7.fhir.String motherString = new org.hl7.fhir.String();
        motherString.setValue("Mother Relationship");
        motherRelationship.setText(motherString);

        relatedPerson.setRelationship(motherRelationship);
        return relatedPerson;
    }
}
