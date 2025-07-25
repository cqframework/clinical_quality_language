package org.opencds.cqf.cql.engine.fhir.data;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

enum EvaluatedResourcesMultiLibComplexDepsRetrieveProvider implements RetrieveProvider {
    INSTANCE;

    private static final String PATIENT_PAT_1 = "Patient/pat1";
    private static final String PATIENT_PAT_2 = "Patient/pat2";
    private static final String PATIENT_PAT_3 = "Patient/pat3";

    static final Encounter ENCOUNTER_ARRIVED_PAT1 = buildEncounter(
            "enc_arrived_pat1",
            Encounter.EncounterStatus.ARRIVED,
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalDate.of(2000, Month.DECEMBER, 31),
            PATIENT_PAT_1);
    static final Encounter ENCOUNTER_CANCELLED_PAT1 = buildEncounter(
            "enc_cancelled_pat1",
            Encounter.EncounterStatus.CANCELLED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_1);
    static final Encounter ENCOUNTER_FINISHED_PAT1 = buildEncounter(
            "enc_finished_pat1",
            Encounter.EncounterStatus.FINISHED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_1);
    static final Encounter ENCOUNTER_PLANNED_PAT1 = buildEncounter(
            "enc_planned_pat1",
            Encounter.EncounterStatus.PLANNED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_1);
    static final Encounter ENCOUNTER_TRIAGED_PAT1 = buildEncounter(
            "enc_triaged_pat1",
            Encounter.EncounterStatus.TRIAGED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_1);

    static final Encounter ENCOUNTER_ARRIVED_PAT2 = buildEncounter(
            "enc_arrived_pat2",
            Encounter.EncounterStatus.ARRIVED,
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalDate.of(2000, Month.DECEMBER, 31),
            PATIENT_PAT_2);
    static final Encounter ENCOUNTER_CANCELLED_PAT2 = buildEncounter(
            "enc_cancelled_pat2",
            Encounter.EncounterStatus.CANCELLED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_2);
    static final Encounter ENCOUNTER_FINISHED_PAT2 = buildEncounter(
            "enc_finished_pat2",
            Encounter.EncounterStatus.FINISHED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_2);
    static final Encounter ENCOUNTER_PLANNED_PAT2 = buildEncounter(
            "enc_planned_pat2",
            Encounter.EncounterStatus.PLANNED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_2);
    static final Encounter ENCOUNTER_TRIAGED_PAT2 = buildEncounter(
            "enc_triaged_pat2",
            Encounter.EncounterStatus.TRIAGED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_2);

    static final Encounter ENCOUNTER_ARRIVED_PAT3 = buildEncounter(
            "enc_arrived_pat3",
            Encounter.EncounterStatus.ARRIVED,
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalDate.of(2000, Month.DECEMBER, 31),
            PATIENT_PAT_3);
    static final Encounter ENCOUNTER_CANCELLED_PAT3 = buildEncounter(
            "enc_cancelled_pat3",
            Encounter.EncounterStatus.CANCELLED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_3);
    static final Encounter ENCOUNTER_FINISHED_PAT3 = buildEncounter(
            "enc_finished_pat3",
            Encounter.EncounterStatus.FINISHED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_3);
    static final Encounter ENCOUNTER_PLANNED_PAT3 = buildEncounter(
            "enc_planned_pat3",
            Encounter.EncounterStatus.PLANNED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_3);
    static final Encounter ENCOUNTER_TRIAGED_PAT3 = buildEncounter(
            "enc_triaged_pat3",
            Encounter.EncounterStatus.TRIAGED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            PATIENT_PAT_3);

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
        return switch (dataType) {
            case "Encounter" -> getEncounters();
            case "Condition" -> getConditions();
            case "Patient" -> getPatients();
            case "Procedure" -> getProcedures();
            default -> List.of();
        };
    }

    private List<Object> getProcedures() {
        return List.of();
    }

    private Iterable<Object> getPatients() {
        return null;
    }

    private Iterable<Object> getConditions() {
        return null;
    }

    private Iterable<Object> getEncounters() {
        return getAllEncounters().stream().collect(Collectors.toUnmodifiableList());
    }

    public static List<Encounter> getAllEncounters() {
        return List.of(
                ENCOUNTER_ARRIVED_PAT1,
                ENCOUNTER_CANCELLED_PAT1,
                ENCOUNTER_FINISHED_PAT1,
                ENCOUNTER_PLANNED_PAT1,
                ENCOUNTER_TRIAGED_PAT1,
                ENCOUNTER_ARRIVED_PAT2,
                ENCOUNTER_CANCELLED_PAT2,
                ENCOUNTER_FINISHED_PAT2,
                ENCOUNTER_PLANNED_PAT2,
                ENCOUNTER_TRIAGED_PAT2,
                ENCOUNTER_ARRIVED_PAT3,
                ENCOUNTER_CANCELLED_PAT3,
                ENCOUNTER_FINISHED_PAT3,
                ENCOUNTER_PLANNED_PAT3,
                ENCOUNTER_TRIAGED_PAT3);
    }

    private Patient buildPatient(String id) {
        return (Patient) new Patient()
                .setActive(true)
                .addIdentifier(new Identifier().setValue(id).setSystem("urn:system"))
                .setId(new IdType(ResourceType.Patient.name(), id));
    }

    private static Encounter buildEncounter(
            String id, Encounter.EncounterStatus status, LocalDate periodStart, LocalDate periodEnd, String subject) {
        return (Encounter) new Encounter()
                .setStatus(status)
                .setPeriod(new Period().setStart(toDate(periodStart)).setEnd(toDate(periodEnd)))
                .setSubject(new Reference().setReference(subject))
                .setId(new IdType(ResourceType.Encounter.name(), id));
    }

    private static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
