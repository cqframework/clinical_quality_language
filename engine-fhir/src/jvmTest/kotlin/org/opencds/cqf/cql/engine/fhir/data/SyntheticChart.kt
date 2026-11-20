package org.opencds.cqf.cql.engine.fhir.data

import java.math.BigDecimal
import java.util.Date
import kotlin.random.Random
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType

const val PATIENT_ID = "example"

/**
 * Builds a deterministic, reasonably realistic single-patient R4 chart: mostly lab and vital
 * observations, with encounters, conditions and medication requests spread over ten years
 * (2017-2027).
 */
internal class SyntheticChart(private val random: Random) {
    private val start = 1_500_000_000_000L
    private val day = 86_400_000L

    fun patient(): Patient =
        Patient().apply {
            id = PATIENT_ID
            addName(HumanName().setFamily("Example").addGiven("Pat"))
            gender = AdministrativeGender.FEMALE
            birthDateElement = DateType("1960-05-14")
        }

    /** [size] clinical resources for the patient, not counting the Patient itself. */
    fun resources(size: Int): List<Resource> =
        List(size) { i ->
            when (random.nextInt(100)) {
                in 0 until 60 -> observation(i)
                in 60 until 75 -> encounter(i)
                in 75 until 85 -> condition(i)
                in 85 until 95 -> medicationRequest(i)
                else -> condition(i)
            }
        }

    private fun date() = Date(start + random.nextLong(3650) * day)

    private fun translation(lang: String, content: String) =
        Extension("http://hl7.org/fhir/StructureDefinition/translation").apply {
            addExtension(Extension("lang", CodeType(lang)))
            addExtension(Extension("content", StringType(content)))
        }

    private fun dataAbsentReason(reason: String) =
        Extension("http://hl7.org/fhir/StructureDefinition/data-absent-reason", CodeType(reason))

    private fun concept(system: String, code: String, display: String) =
        CodeableConcept().addCoding(Coding(system, code, display)).setText(display)

    fun observation(i: Int): Observation {
        val (code, display, unit) = LOINC_CODES[random.nextInt(LOINC_CODES.size)]
        val effective = date()
        return Observation().apply {
            id = "obs-$i"
            meta
                .setLastUpdated(effective)
                .addProfile(
                    "http://hl7.org/fhir/us/core/StructureDefinition/us-core-observation-lab"
                )
            status = Observation.ObservationStatus.FINAL
            addCategory(
                concept(
                    "http://terminology.hl7.org/CodeSystem/observation-category",
                    "laboratory",
                    "Laboratory",
                )
            )
            this.code =
                concept(LOINC, code, display).apply {
                    // Primitives can carry extensions too
                    if (random.nextInt(5) == 0) textElement.addExtension(translation("es", display))
                }
            subject = Reference("Patient/$PATIENT_ID")
            encounter = Reference("Encounter/enc-${random.nextInt(1000)}")
            setEffective(DateTimeType(effective))
            issued = effective
            setValue(
                Quantity()
                    .setValue(BigDecimal.valueOf(random.nextInt(50, 200).toLong()))
                    .setUnit(unit)
                    .setSystem("http://unitsofmeasure.org")
                    .setCode(unit)
            )
            addInterpretation(
                concept(
                    "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation",
                    "N",
                    "Normal",
                )
            )
            addExtension(
                Extension("http://example.org/fhir/StructureDefinition/source", StringType("lab"))
            )
        }
    }

    fun encounter(i: Int): Encounter {
        val periodStart = date()
        val (code, display) = SNOMED_CONDITIONS[random.nextInt(SNOMED_CONDITIONS.size)]
        return Encounter().apply {
            id = "enc-$i"
            status = Encounter.EncounterStatus.FINISHED
            setClass_(
                Coding("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB", "ambulatory")
            )
            addType(concept(SNOMED, "185349003", "Encounter for check up"))
            subject = Reference("Patient/$PATIENT_ID")
            period = Period().setStart(periodStart).setEnd(Date(periodStart.time + 3_600_000))
            addReasonCode(concept(SNOMED, code, display))
        }
    }

    fun condition(i: Int): Condition {
        val (code, display) = SNOMED_CONDITIONS[random.nextInt(SNOMED_CONDITIONS.size)]
        return Condition().apply {
            id = "cond-$i"
            clinicalStatus =
                concept(
                    "http://terminology.hl7.org/CodeSystem/condition-clinical",
                    if (random.nextInt(4) == 0) "resolved" else "active",
                    "Active",
                )
            verificationStatus =
                concept(
                    "http://terminology.hl7.org/CodeSystem/condition-ver-status",
                    "confirmed",
                    "Confirmed",
                )
            addCategory(
                concept(
                    "http://terminology.hl7.org/CodeSystem/condition-category",
                    "problem-list-item",
                    "Problem List Item",
                )
            )
            this.code = concept(SNOMED, code, display)
            subject = Reference("Patient/$PATIENT_ID")
            setOnset(
                if (random.nextInt(10) == 0) {
                    DateTimeType().apply { addExtension(dataAbsentReason("unknown")) }
                } else {
                    DateTimeType(date())
                }
            )
            recordedDate = date()
        }
    }

    fun medicationRequest(i: Int): MedicationRequest =
        MedicationRequest().apply {
            id = "medrx-$i"
            status = MEDICATION_STATUSES[random.nextInt(MEDICATION_STATUSES.size)]
            intent = MedicationRequest.MedicationRequestIntent.ORDER
            setMedication(
                concept(
                    "http://www.nlm.nih.gov/research/umls/rxnorm",
                    "860975",
                    "metformin hydrochloride 500 MG Oral Tablet",
                )
            )
            subject = Reference("Patient/$PATIENT_ID")
            authoredOn = date()
            addDosageInstruction().setText("Take 1 tablet by mouth twice daily")
        }

    companion object {
        const val LOINC = "http://loinc.org"
        const val SNOMED = "http://snomed.info/sct"

        private val LOINC_CODES =
            listOf(
                Triple("8480-6", "Systolic blood pressure", "mm[Hg]"),
                Triple("8462-4", "Diastolic blood pressure", "mm[Hg]"),
                Triple("2339-0", "Glucose [Mass/volume] in Blood", "mg/dL"),
                Triple("4548-4", "Hemoglobin A1c/Hemoglobin.total in Blood", "%"),
                Triple("29463-7", "Body weight", "kg"),
            )

        private val SNOMED_CONDITIONS =
            listOf(
                "44054006" to "Diabetes mellitus type 2",
                "38341003" to "Hypertensive disorder",
                "195967001" to "Asthma",
                "55822004" to "Hyperlipidemia",
            )

        private val MEDICATION_STATUSES =
            listOf(
                MedicationRequest.MedicationRequestStatus.ACTIVE,
                MedicationRequest.MedicationRequestStatus.ACTIVE,
                MedicationRequest.MedicationRequestStatus.COMPLETED,
                MedicationRequest.MedicationRequestStatus.STOPPED,
            )
    }
}
