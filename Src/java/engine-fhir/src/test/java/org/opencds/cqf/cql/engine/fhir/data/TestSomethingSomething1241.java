package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.r4.model.*;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.elm.executing.RetrieveEvaluator;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import javax.annotation.Nonnull;
import java.lang.String;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestSomethingSomething1241 extends FhirExecutionTestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestSomethingSomething1241.class);
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

        Patient 123 > Practitioner -> All Patients (123, 456, 789)
         */

        final Practitioner practitionerXyz = getPractitioner("xyz", "Nick", "Riviera");
        final Practitioner practitionerZulu = getPractitioner("zulu", "Leonard", "McCoy");

        final Patient patient123 = getPatient("123", LocalDate.of(1980, Month.JANUARY, 19), practitionerXyz);
        final Patient patient456 = getPatient("456", LocalDate.of(1985, Month.APRIL, 19), practitionerXyz);
        final Patient patient789 = getPatient("789", LocalDate.of(1990, Month.JULY, 19), practitionerXyz);

        // LUKETODO:  set up these patients with the other practitioner
        final Patient patientAbc = getPatient("abc", LocalDate.of(1970, Month.MARCH, 21), practitionerZulu);
        final Patient patientDef = getPatient("def", LocalDate.of(1975, Month.AUGUST, 21), practitionerZulu);

        final Set<Patient> allPatients = Set.of(patient123, patient456, patient789, patientAbc, patientDef);
        final Set<Practitioner> allPractitioners = Set.of(practitionerXyz, practitionerZulu);

        /*
            dataType: [Practitioner], context: [Patient], contextPath: [null], contextValue: [123]
            dataType: [Patient], context: [Patient], contextPath: [id], contextValue: [123]
         */
        final Collection<String> strings = somethingCodes(codes);

        logger.info("dataType: [{}], context: [{}], contextPath: [{}], contextValue: [{}], codePath: [{}], codes: [{}]", dataType, context, contextPath, contextValue, codePath, strings);

        // LUKETODO:  on the 3rd iteration, we're hitting this, which is wrong:
        // a)
        if (PATIENT.equals(dataType) && PATIENT.equals(context) && "id".equals(contextPath) && "123".equals(contextValue)) {
            logger.info(">>> patient 123");
            return allPatients.stream()
                    .filter(patient -> "123".equals(patient.getId()))
                    .collect(Collectors.toList());
        }

        // b)
        if (PRACTITIONER.equals(dataType) && PATIENT.equals(context) && "id".equals(codePath) && strings.contains("xyz")) {
            logger.info(">>> practitioner xyz");
            final Optional<Patient> optPatient123 = allPatients.stream()
                    .filter(patient -> "123".equals(patient.getId()))
                    .findFirst();

            if (optPatient123.isPresent()) {
                final List<String> generalPractitionerIds = optPatient123.get()
                        .getGeneralPractitioner()
                        .stream()
                        .map(Reference::getReference)
                        .map(ref -> ref.split("Practitioner/")[1])
                        .collect(Collectors.toList());

                return allPractitioners.stream()
                        .filter(practitioner -> generalPractitionerIds.contains(practitioner.getId()))
                        .collect(Collectors.toList());
            }
        }


        // c) LUKETODO:  this is what we need to return
        if (PATIENT.equals(dataType) && PRACTITIONER.equals(context) && "generalPractitioner".equals(contextPath) && strings.contains("xyz")) {
            logger.info(">>> patients for practitioner xyz");
            // LUKETODO:  inline everything
            final List<Object> collect = allPatients.stream()
                    .filter(patient -> {
                        final List<String> practitioners = patient.getGeneralPractitioner().stream()
                                .map(gp -> gp.getReference().split("Practitioner/")[1])
                                .collect(Collectors.toList());

                        return practitioners.contains(practitionerXyz.getId());
                    })
                    .collect(Collectors.toList());
            return collect;
        }

        // LUKETODO: Remove this
        if (PATIENT.equals(dataType)) {
            return new ArrayList<>(allPatients);
        }

        // LUKETODO: Remove this
        if (PRACTITIONER.equals(dataType)) {
            return List.of(practitionerXyz);
        }

        return null;
    };

    // LUKETODO:  this is gross but somehow the IDE things collect is Code yet it's an inner Collection
    private static Collection<String> somethingCodes(Iterable<Code> codes) {
        if (codes == null) {
            return null;
        }

        final List<?> collect = StreamSupport.stream(codes.spliterator(), false)
                .collect(Collectors.toList());

        if (! collect.isEmpty()) {
            final Object object = codes.iterator().next();
            if (object instanceof List) {
                List<?> objectAsList = (List<?>)object;

                if (! objectAsList.isEmpty()) {
                    final Object innerObject = objectAsList.get(0);

                    if (innerObject instanceof Reference) {
                        final Reference innerReference = (Reference) innerObject;

                        return Collections.singletonList(innerReference.getReference().split("Practitioner/")[1]);
                    }
                }
            }
        }


        return Collections.emptyList();
    }


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

        final Pair<String, Object> initialContext = Pair.of(PATIENT, "123");

        final EvaluationResult evaluationResultPatient = engine.evaluate(library.getIdentifier(),
                Set.of(PATIENT), initialContext, null, null, null);
        final Object resultPatient = evaluationResultPatient.forExpression(PATIENT).value();

        assertThat(resultPatient, instanceOf(Patient.class));
        final Patient resultPatientCasted = (Patient) resultPatient;
        assertThat(resultPatientCasted.getId(), is("123"));
        engine.getState().clearEvaluatedResources();

        final EvaluationResult evaluationResultPrimaryCareDoctor = engine.evaluate(library.getIdentifier(),
                Set.of(PRIMARY_CARE_DOCTOR), initialContext, null, null, null);
        final Object resultPrimaryCareDoctor = evaluationResultPrimaryCareDoctor.forExpression(PRIMARY_CARE_DOCTOR).value();

        assertThat(resultPrimaryCareDoctor, instanceOf(Practitioner.class));
        final Practitioner resultPractitioner = (Practitioner) resultPrimaryCareDoctor;
        assertThat(resultPractitioner, instanceOf(Practitioner.class));
        assertThat(resultPractitioner.getId(), is("xyz"));
        engine.getState().clearEvaluatedResources();

        // LUKETODO:  code reuse
        final EvaluationResult evaluationResultAllPatientForGp = engine.evaluate(library.getIdentifier(),
                Set.of(ALL_PATIENT_FOR_GP), initialContext, null, null, null);
        final Object resultAllPatientForGp = evaluationResultAllPatientForGp.forExpression(ALL_PATIENT_FOR_GP).value();
        engine.getState().clearEvaluatedResources();

        assertThat(resultAllPatientForGp, instanceOf(List.class));

        // LUKETODO:  proper cast
        final List<Patient> patientsForPractitioner = (List<Patient>) resultAllPatientForGp;

        // LUKETODO:  the other two patients
        // LUKETODO:  adjust assertions for all 3 patients
        assertThat(patientsForPractitioner.size(), is(3));

        /*
        final EvaluationResult evaluationResultCombined = engine.evaluate(library.getIdentifier(),
                Set.of(PRIMARY_CARE_DOCTOR, ALL_PATIENT_FOR_GP), null, null, null, null);
        final Object resultCombinedAllPrimaryCareDoctor = evaluationResultCombined.forExpression(PRIMARY_CARE_DOCTOR).value();
        final Object resultCombinedAllPatient = evaluationResultCombined.forExpression(ALL_PATIENT_FOR_GP).value();
        engine.getState().clearEvaluatedResources();
         */
    }

    @Nonnull
    private static Practitioner getPractitioner(String practitionerId, String firstName, String lastName) {
        final Practitioner practitioner = new Practitioner();

        practitioner.setId(practitionerId);

        practitioner.setName(List.of(new HumanName()
                .setFamily(lastName)
                .setGiven(List.of(new StringType(lastName)))));

        return practitioner;
    }

    @Nonnull
    private static Patient getPatient(String patientId, LocalDate birthDateLocalDate, Practitioner nullablePractitioner) {
        final Patient patient = new Patient();

        patient.setId(patientId);

        patient.setBirthDate(Date.from(birthDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        if (nullablePractitioner != null) {
            patient.setGeneralPractitioner(List.of(new Reference().setReference("Practitioner/" + nullablePractitioner.getId())));
        }

        return patient;
    }
}
