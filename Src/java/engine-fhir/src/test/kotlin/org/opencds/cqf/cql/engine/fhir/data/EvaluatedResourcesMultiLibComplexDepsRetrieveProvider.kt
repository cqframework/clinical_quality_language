package org.opencds.cqf.cql.engine.fhir.data

import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.Date
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

internal enum class EvaluatedResourcesMultiLibComplexDepsRetrieveProvider : RetrieveProvider {
    INSTANCE;

    override fun retrieve(
        context: String?,
        contextPath: String?,
        contextValue: Any?,
        dataType: String,
        templateId: String?,
        codePath: String?,
        codes: Iterable<Code>?,
        valueSet: String?,
        datePath: String?,
        dateLowPath: String?,
        dateHighPath: String?,
        dateRange: Interval?,
    ): Iterable<Any?>? {
        return when (dataType) {
            "Encounter" -> this.encounters
            "Condition" -> this.conditions
            "Patient" -> this.patients
            "Procedure" -> this.procedures
            else -> mutableListOf()
        }
    }

    private val procedures: MutableList<Any?>
        get() = mutableListOf()

    private val patients: Iterable<Any?>?
        get() = null

    private val conditions: Iterable<Any>?
        get() = null

    private val encounters: Iterable<Any>
        get() = allEncounters

    companion object {
        private const val PATIENT_PAT_1 = "Patient/pat1"
        private const val PATIENT_PAT_2 = "Patient/pat2"
        private const val PATIENT_PAT_3 = "Patient/pat3"

        @JvmField
        val ENCOUNTER_ARRIVED_PAT1: Encounter =
            buildEncounter(
                "enc_arrived_pat1",
                Encounter.EncounterStatus.ARRIVED,
                LocalDate.of(2000, Month.JANUARY, 1),
                LocalDate.of(2000, Month.DECEMBER, 31),
                PATIENT_PAT_1,
            )
        val ENCOUNTER_CANCELLED_PAT1: Encounter =
            buildEncounter(
                "enc_cancelled_pat1",
                Encounter.EncounterStatus.CANCELLED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_1,
            )
        val ENCOUNTER_FINISHED_PAT1: Encounter =
            buildEncounter(
                "enc_finished_pat1",
                Encounter.EncounterStatus.FINISHED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_1,
            )
        @JvmField
        val ENCOUNTER_PLANNED_PAT1: Encounter =
            buildEncounter(
                "enc_planned_pat1",
                Encounter.EncounterStatus.PLANNED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_1,
            )
        val ENCOUNTER_TRIAGED_PAT1: Encounter =
            buildEncounter(
                "enc_triaged_pat1",
                Encounter.EncounterStatus.TRIAGED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_1,
            )

        @JvmField
        val ENCOUNTER_ARRIVED_PAT2: Encounter =
            buildEncounter(
                "enc_arrived_pat2",
                Encounter.EncounterStatus.ARRIVED,
                LocalDate.of(2000, Month.JANUARY, 1),
                LocalDate.of(2000, Month.DECEMBER, 31),
                PATIENT_PAT_2,
            )
        val ENCOUNTER_CANCELLED_PAT2: Encounter =
            buildEncounter(
                "enc_cancelled_pat2",
                Encounter.EncounterStatus.CANCELLED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_2,
            )
        val ENCOUNTER_FINISHED_PAT2: Encounter =
            buildEncounter(
                "enc_finished_pat2",
                Encounter.EncounterStatus.FINISHED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_2,
            )
        @JvmField
        val ENCOUNTER_PLANNED_PAT2: Encounter =
            buildEncounter(
                "enc_planned_pat2",
                Encounter.EncounterStatus.PLANNED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_2,
            )
        val ENCOUNTER_TRIAGED_PAT2: Encounter =
            buildEncounter(
                "enc_triaged_pat2",
                Encounter.EncounterStatus.TRIAGED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_2,
            )

        @JvmField
        val ENCOUNTER_ARRIVED_PAT3: Encounter =
            buildEncounter(
                "enc_arrived_pat3",
                Encounter.EncounterStatus.ARRIVED,
                LocalDate.of(2000, Month.JANUARY, 1),
                LocalDate.of(2000, Month.DECEMBER, 31),
                PATIENT_PAT_3,
            )
        val ENCOUNTER_CANCELLED_PAT3: Encounter =
            buildEncounter(
                "enc_cancelled_pat3",
                Encounter.EncounterStatus.CANCELLED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_3,
            )
        val ENCOUNTER_FINISHED_PAT3: Encounter =
            buildEncounter(
                "enc_finished_pat3",
                Encounter.EncounterStatus.FINISHED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_3,
            )
        @JvmField
        val ENCOUNTER_PLANNED_PAT3: Encounter =
            buildEncounter(
                "enc_planned_pat3",
                Encounter.EncounterStatus.PLANNED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_3,
            )
        val ENCOUNTER_TRIAGED_PAT3: Encounter =
            buildEncounter(
                "enc_triaged_pat3",
                Encounter.EncounterStatus.TRIAGED,
                LocalDate.of(2001, Month.JANUARY, 1),
                LocalDate.of(2001, Month.DECEMBER, 31),
                PATIENT_PAT_3,
            )

        @JvmStatic
        val allEncounters: List<Encounter>
            get() =
                listOf<Encounter>(
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
                    ENCOUNTER_TRIAGED_PAT3,
                )

        private fun buildEncounter(
            id: String?,
            status: Encounter.EncounterStatus?,
            periodStart: LocalDate,
            periodEnd: LocalDate,
            subject: String?,
        ): Encounter {
            return Encounter()
                .setStatus(status)
                .setPeriod(Period().setStart(toDate(periodStart)).setEnd(toDate(periodEnd)))
                .setSubject(Reference().setReference(subject))
                .setId(IdType(ResourceType.Encounter.name, id)) as Encounter
        }

        private fun toDate(localDate: LocalDate): Date {
            return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        }
    }
}
