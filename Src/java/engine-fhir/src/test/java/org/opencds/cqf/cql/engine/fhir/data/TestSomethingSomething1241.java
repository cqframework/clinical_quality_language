package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.CodeableConcept;
import org.hl7.fhir.Observation;
import org.hl7.fhir.Reference;
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

        final Reference reference1 = new Reference().withReference(new org.hl7.fhir.String().withValue("Patient/123"));
        final Reference reference2 = new Reference().withReference(new org.hl7.fhir.String().withValue("Patient/456"));;
        final Observation observation1 = getObservation(reference1);
        final Observation observation2 = getObservation(reference2);

        switch (dataType) {
            case "RelatedPerson":
                // LUKETODO:  we may need IDs here
                return List.of(relatedPerson);
            case OBSERVATION:
                // LUKETODO:  we may need IDs here
                return List.of(observation1, observation2);
            default:
                break;
        }
        return null;
    };
    private static final String MOTHER_OBSERVATION = "Mother Observation";
    private static final String CHILD_OBSERVATION = "Child Observation";

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

        // LUKETODO:  code reuse
        final EvaluationResult evaluationResultMotherObservation = engine.evaluate(library.getIdentifier(),
                Set.of(MOTHER_OBSERVATION), null, null, null, null);
        final Object resultMotherObservation  = evaluationResultMotherObservation.forExpression(MOTHER_OBSERVATION).value();

        final EvaluationResult evaluationResultChildObservation = engine.evaluate(library.getIdentifier(),
                Set.of(CHILD_OBSERVATION), null, null, null, null);
        final Object resultChildObservation  = evaluationResultChildObservation.forExpression(CHILD_OBSERVATION).value();

        assertThat(resultMotherObservation , instanceOf(List.class));
//        assertThat(evaluationResultMotherObservation .forExpression(MOTHER_OBSERVATION).evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();

        assertThat(resultChildObservation , instanceOf(List.class));
        assertThat(evaluationResultChildObservation .forExpression(CHILD_OBSERVATION).evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();
    }

    @Nonnull
    private static Observation getObservation(Reference reference) {
        final Observation observation = new Observation();

        observation.setSubject(reference);

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
