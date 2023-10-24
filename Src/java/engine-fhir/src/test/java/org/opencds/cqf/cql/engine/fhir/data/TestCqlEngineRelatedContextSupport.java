package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.r4.model.*;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
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

import org.apache.commons.lang3.tuple.Pair;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.fail;

public class TestCqlEngineRelatedContextSupport extends FhirExecutionTestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestCqlEngineRelatedContextSupport.class);
    private static final String PATIENT = "Patient";
    private static final String PRACTITIONER = "Practitioner";
    private static final String PRACTITIONER_SLASH = PRACTITIONER + "/";
    private static final String PRIMARY_CARE_DOCTOR = "Primary Care Doctor";
    private static final String ALL_PATIENT_FOR_GP = "All Patient for GP";

    private static final String URL_FHIR = "http://hl7.org/fhir";

    private static final String GENERAL_PRACTITIONER = "generalPractitioner";
    private static final String XYZ = "xyz";
    private static final String _PATIENT_123 = "123";
    private static final String ID = "id";

    private static final Practitioner PRACTITIONER_XYZ = getPractitioner(XYZ, "Nick", "Riviera");
    private static final Practitioner PRACTITIONER_ZULU = getPractitioner("zulu", "Leonard", "McCoy");

    private static final Patient PATIENT_123 = getPatient(_PATIENT_123, LocalDate.of(1980, Month.JANUARY, 19), PRACTITIONER_XYZ);
    private static final Patient PATIENT_456 = getPatient("456", LocalDate.of(1985, Month.APRIL, 19), PRACTITIONER_XYZ);
    private static final Patient PATIENT_789 = getPatient("789", LocalDate.of(1990, Month.JULY, 19), PRACTITIONER_XYZ);

    private static final Patient PATIENT_ABC = getPatient("abc", LocalDate.of(1970, Month.MARCH, 21), PRACTITIONER_ZULU);
    private static final Patient PATIENT_DEF = getPatient("def", LocalDate.of(1975, Month.AUGUST, 21), PRACTITIONER_ZULU);

    private static final RetrieveProvider retrieveProvider = (context, contextPath, contextValue, dataType, templateId, codePath,
                                                              codes, valueSet, datePath, dateLowPath, dateHighPath, dateRange) -> {

        final Set<Patient> allPatients = Set.of(PATIENT_123, PATIENT_456, PATIENT_789, PATIENT_ABC, PATIENT_DEF);
        final Set<Practitioner> allPractitioners = Set.of(PRACTITIONER_XYZ, PRACTITIONER_ZULU);

        // a) All matching patients for the patient being searched by ID=123
        if (PATIENT.equals(dataType) && PATIENT.equals(context) && ID.equals(contextPath) && _PATIENT_123.equals(contextValue)) {
            return allPatients.stream()
                    .filter(patient -> _PATIENT_123.equals(patient.getId()))
                    .collect(Collectors.toList());
        }

        // b) All practitioners matching XYZ and patient 123
        if (PRACTITIONER.equals(dataType) && PATIENT.equals(context) && ID.equals(codePath) && codesEqual(codes, PRACTITIONER_SLASH + XYZ)) {
            final Optional<Patient> optPatient123 = allPatients.stream()
                    .filter(patient -> _PATIENT_123.equals(patient.getId()))
                    .findFirst();

            if (optPatient123.isPresent()) {
                final List<String> generalPractitionerIds = optPatient123.get()
                        .getGeneralPractitioner()
                        .stream()
                        .map(Reference::getReference)
                        .map(ref -> ref.split(PRACTITIONER_SLASH)[1])
                        .collect(Collectors.toList());

                return allPractitioners.stream()
                        .filter(practitioner -> generalPractitionerIds.contains(practitioner.getId()))
                        .collect(Collectors.toList());
            }
        }


        // c) All patients belonging to Patient 123'd generalPractitioner
        final boolean equals = "xyz".equals(contextValue.toString());
        if (PATIENT.equals(dataType) && PRACTITIONER.equals(context) && GENERAL_PRACTITIONER.equals(contextPath) && equals) {
            logger.info(">>> patients for practitioner xyz");
            return allPatients.stream()
                    .filter(patient -> getMatchingPractitioners(patient)
                            .contains(PRACTITIONER_XYZ.getId()))
                    .collect(Collectors.toList());
        }

        return null;
    };

    // TODO: LD: Due to a type erasure and the CQL compiler historically being in separate repositories, two different
    // code paths were merged, resulting in an insidious condition where type erasure has resulted in the declared
    // variable's type being wrong in this instance:  It's actually an Iterable<String>
    private static boolean codesEqual(Iterable<?> codes, String equalTo) {
        if (codes == null) {
            return false;
        }

        final Iterator<?> iterator = codes.iterator();

        if (! iterator.hasNext()) {
            return false;
        }

        final Object next = iterator.next();

        // Ignore the javac warning here
        if (! String.class.isInstance(next)) {
            fail("Expected codes to contain Strings but does not: " + codes);
        }

        final String nextCode = (String) next;

        return equalTo.equals(nextCode);
    }


    @Nonnull
    private static List<String> getMatchingPractitioners(Patient thePatient) {
        return thePatient.getGeneralPractitioner().stream()
                .map(TestCqlEngineRelatedContextSupport::getIdFromReference)
                .collect(Collectors.toList());
    }

    @Nonnull
    private static String getIdFromReference(Reference theInnerReference) {
        return theInnerReference.getReference().split(PRACTITIONER_SLASH)[1];
    }


    @Test
    public void testCqlEngineRelatedContext() {
        final CqlEngine cqlEngine = getEngine();

        cqlEngine.getState().getEnvironment().registerDataProvider(URL_FHIR, new CompositeDataProvider(r4ModelResolver, retrieveProvider));
        cqlEngine.getCache().setExpressionCaching(true);

        final Pair<String, Object> initialContext = Pair.of(PATIENT, _PATIENT_123);

        final Object resultPatient = evaluate(cqlEngine, PATIENT, initialContext);

        assertThat(resultPatient, instanceOf(Patient.class));
        final Patient resultPatientCasted = (Patient) resultPatient;
        assertThat(resultPatientCasted.getId(), is(_PATIENT_123));
        cqlEngine.getState().clearEvaluatedResources();

        final Object resultPrimaryCareDoctor = evaluate(cqlEngine, PRIMARY_CARE_DOCTOR, initialContext);

        assertThat(resultPrimaryCareDoctor, instanceOf(Practitioner.class));
        final Practitioner resultPractitioner = (Practitioner) resultPrimaryCareDoctor;
        assertThat(resultPractitioner, instanceOf(Practitioner.class));
        assertThat(resultPractitioner.getId(), is(XYZ));
        cqlEngine.getState().clearEvaluatedResources();

        final Object resultAllPatientForGp = evaluate(cqlEngine, ALL_PATIENT_FOR_GP, initialContext);
        cqlEngine.getState().clearEvaluatedResources();

        assertThat(resultAllPatientForGp, instanceOf(List.class));

        final List<Patient> patientsForPractitioner = ((List<?>)resultAllPatientForGp).stream()
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .collect(Collectors.toList());

        assertThat(patientsForPractitioner.size(), is(3));
        assertThat(patientsForPractitioner.stream().map(Patient::getId).collect(Collectors.toSet()),
                is(Set.of(PATIENT_123, PATIENT_456, PATIENT_789).stream().map(Patient::getId).collect(Collectors.toSet())));

    }

    @Nonnull
    private Object evaluate(CqlEngine cqlEngine, String expression, Pair<String, Object> initialContext) {
        final EvaluationResult evaluateResult = cqlEngine.evaluate(library.getIdentifier(),
                Set.of(expression), initialContext, null, null, null);
        return evaluateResult.forExpression(expression).value();
    }

    @Nonnull
    private static Practitioner getPractitioner(String practitionerId, String firstName, String lastName) {
        final Practitioner practitioner = new Practitioner();

        practitioner.setId(practitionerId);

        practitioner.setName(List.of(new HumanName()
                .setFamily(lastName)
                .setGiven(List.of(new StringType(firstName)))));

        return practitioner;
    }

    @Nonnull
    private static Patient getPatient(String patientId, LocalDate birthDateLocalDate, Practitioner nullablePractitioner) {
        final Patient patient = new Patient();

        patient.setId(patientId);

        patient.setBirthDate(Date.from(birthDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        if (nullablePractitioner != null) {
            patient.setGeneralPractitioner(List.of(new Reference().setReference(PRACTITIONER_SLASH + nullablePractitioner.getId())));
        }

        return patient;
    }
}
