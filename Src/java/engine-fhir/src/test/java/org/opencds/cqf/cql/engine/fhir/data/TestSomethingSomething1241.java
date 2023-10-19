package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.r4.model.*;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.lang.String;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestSomethingSomething1241 extends FhirExecutionTestBase {
    private static final String PATIENT = "Patient";
    private static final String PRACTITIONER = "Practitioner";
    private static final String PRIMARY_CARE_DOCTOR = "Primary Care Doctor";
    private static final String ALL_PATIENT_FOR_GP = "All Patient for GP";

    private static final String URL_FHIR = "http://hl7.org/fhir";

    private static final RetrieveProvider retrieveProvider = (context, contextPath, contextValue, dataType, templateId, codePath,
                                                              codes, valueSet, datePath, dateLowPath, dateHighPath, dateRange) -> {
        /*

        1. Encounter context   >>> observations >>>>
        2.  Related context>>>> going from one patient context to the other:   from mother to child
        >>>> 3. Patient to practitioner to all patients for that practitioner


        How a given resource relates back to its compartment varies by its resource type

        CQL uses that compartment knowledge uses ModelInfo

        typeInfo >>> type=ClassInfo

        <contextRelationship context="patient" ...>

        context Patient

        define "Primary Care Doctor": singleton from ["Practitioner"]

        define "All Patient for GP":
            ["Primary Care Doctor" -> "Patient"]

        "Patient" -> 123
        "Primary Care Doctor" -> XYZ
        "All Patient for GP" -> [123, 456, 789]

         */

        final Practitioner practitionerXyz = getPractitioner();

        final Patient patient123 = getPatient(LocalDate.of(1980, Month.JANUARY, 19), practitionerXyz);
        final Patient patient456 = getPatient(LocalDate.of(1985, Month.APRIL, 19), practitionerXyz);
        final Patient patient789 = getPatient(LocalDate.of(1990, Month.JULY, 19), practitionerXyz);

        final Patient patientAbc = getPatient(LocalDate.of(1970, Month.MARCH, 21), practitionerXyz);
        final Patient patientDef = getPatient(LocalDate.of(1975, Month.AUGUST, 21), practitionerXyz);

        switch (dataType) {
            case PATIENT:
                return List.of(patient123, patient456, patient789, patientAbc, patientDef);
            case PRACTITIONER:
                return List.of(practitionerXyz);
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

        final EvaluationResult evaluationResultPrimaryCareDoctor = engine.evaluate(library.getIdentifier(),
                Set.of(PRIMARY_CARE_DOCTOR), null, null, null, null);
        final Object resultPrimaryCareDoctor = evaluationResultPrimaryCareDoctor.forExpression(PRIMARY_CARE_DOCTOR).value();

        assertThat(resultPrimaryCareDoctor, instanceOf(Practitioner.class));
        final Practitioner resultPractitioner = (Practitioner) resultPrimaryCareDoctor;
        /*
        assertThat(resultMotherObservation , instanceOf(List.class));
//        assertThat(evaluationResultMotherObservation .forExpression(MOTHER_OBSERVATION).evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();

        assertThat(resultChildObservation , instanceOf(List.class));
        assertThat(evaluationResultChildObservation .forExpression(CHILD_OBSERVATION).evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();
         */

        System.out.println("resultPrimaryCareDoctor = " + resultPrimaryCareDoctor);

        //        // LUKETODO:  code reuse
        final EvaluationResult evaluationResultAllPatientForGp = engine.evaluate(library.getIdentifier(),
                Set.of(ALL_PATIENT_FOR_GP), null, null, null, null);
        final Object resultAllPatientForGp = evaluationResultAllPatientForGp.forExpression(ALL_PATIENT_FOR_GP).value();

        System.out.println("resultAllPatientForGp = " + resultAllPatientForGp);

        assertThat(resultAllPatientForGp, instanceOf(List.class));

        // LUKETODO:  proper cast
        final List<Patient> patientsForPractitioner = (List<Patient>) resultAllPatientForGp;

        // LUKETODO:  the other two patients
        assertThat(patientsForPractitioner.size(), is(3));
    }

    @Nonnull
    private static Practitioner getPractitioner() {
        final Practitioner practitioner = new Practitioner();

        practitioner.setName(List.of(new HumanName()
                .setFamily("Riviera")
                .setGiven(List.of(new StringType("Nick")))));

        return practitioner;
    }

    @Nonnull
    private static Patient getPatient(LocalDate birthDateLocalDate, Practitioner nullablePractitioner) {
        final Patient patient = new Patient();

        patient.setBirthDate(Date.from(birthDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        if (nullablePractitioner != null) {
            patient.setGeneralPractitioner(List.of(new Reference().setReference("Practitioner/" + nullablePractitioner.getId())));
        }

        return patient;
    }
}
