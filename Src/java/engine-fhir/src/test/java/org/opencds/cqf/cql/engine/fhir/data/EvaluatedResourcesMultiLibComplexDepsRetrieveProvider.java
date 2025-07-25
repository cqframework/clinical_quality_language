package org.opencds.cqf.cql.engine.fhir.data;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
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

    static final Encounter ENCOUNTER_ARRIVED = buildEncounter(
            "enc_arrived",
            Encounter.EncounterStatus.ARRIVED,
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalDate.of(2000, Month.DECEMBER, 31),
            "Patient/pat1");
    static final Encounter ENCOUNTER_CANCELLED = buildEncounter(
            "enc_cancelled",
            Encounter.EncounterStatus.CANCELLED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            "Patient/pat1");
    static final Encounter ENCOUNTER_FINISHED = buildEncounter(
            "enc_finished",
            Encounter.EncounterStatus.FINISHED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            "Patient/pat1");
    static final Encounter ENCOUNTER_PLANNED = buildEncounter(
            "enc_planned",
            Encounter.EncounterStatus.PLANNED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            "Patient/pat1");
    static final Encounter ENCOUNTER_TRIAGED = buildEncounter(
            "enc_triaged",
            Encounter.EncounterStatus.TRIAGED,
            LocalDate.of(2001, Month.JANUARY, 1),
            LocalDate.of(2001, Month.DECEMBER, 31),
            "Patient/pat1");

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
        return List.of(
                ENCOUNTER_ARRIVED, ENCOUNTER_CANCELLED, ENCOUNTER_FINISHED, ENCOUNTER_PLANNED, ENCOUNTER_TRIAGED);
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
