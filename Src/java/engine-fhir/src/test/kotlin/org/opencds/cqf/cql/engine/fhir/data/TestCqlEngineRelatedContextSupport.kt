package org.opencds.cqf.cql.engine.fhir.data

import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.cqframework.cql.shared.QName
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlString
import org.slf4j.LoggerFactory

internal class TestCqlEngineRelatedContextSupport : FhirExecutionTestBase() {
    @Test
    fun cqlEngineRelatedContext() {
        val cqlEngine = engine

        cqlEngine.environment.registerDataProvider(
            URL_FHIR,
            CompositeDataProvider(r4ModelResolver, retrieveProvider),
        )
        cqlEngine.cache.setExpressionCaching(true)

        val initialContext = PATIENT to _PATIENT_123

        val resultPatient = evaluate(cqlEngine, PATIENT, initialContext)

        assertIs<ClassInstance>(resultPatient)
        assertEquals(QName("http://hl7.org/fhir", "Patient"), resultPatient.type)
        assertEquals(
            _PATIENT_123.toCqlString(),
            (resultPatient.elements["id"] as ClassInstance).elements["value"],
        )

        cqlEngine.state.clearEvaluatedResources()

        val resultPrimaryCareDoctor = evaluate(cqlEngine, PRIMARY_CARE_DOCTOR, initialContext)
        assertIs<ClassInstance>(resultPrimaryCareDoctor)
        assertEquals(QName("http://hl7.org/fhir", "Practitioner"), resultPrimaryCareDoctor.type)
        assertEquals(
            XYZ.toCqlString(),
            (resultPrimaryCareDoctor.elements["id"] as ClassInstance).elements["value"],
        )

        cqlEngine.state.clearEvaluatedResources()

        val resultAllPatientForGp = evaluate(cqlEngine, ALL_PATIENT_FOR_GP, initialContext)
        cqlEngine.state.clearEvaluatedResources()

        assertIs<org.opencds.cqf.cql.engine.runtime.List>(resultAllPatientForGp)
        assertTrue(resultAllPatientForGp.all { it is ClassInstance })

        val patientsForPractitioner =
            resultAllPatientForGp.filterIsInstance<ClassInstance>().filter {
                it.type == QName("http://hl7.org/fhir", "Patient")
            }

        assertEquals(3, patientsForPractitioner.size)

        assertEquals(
            setOf(PATIENT_123, PATIENT_456, PATIENT_789).map { it.getId().toCqlString() }.toSet(),
            patientsForPractitioner
                .map { (it.elements["id"] as ClassInstance).elements["value"] }
                .toSet(),
        )
    }

    private fun evaluate(
        cqlEngine: CqlEngine,
        expression: String,
        initialContext: Pair<String, String?>?,
    ): Value? {
        val evaluateResult =
            cqlEngine
                .evaluate {
                    library(library!!.identifier!!) { expressions(expression) }
                    contextParameter = initialContext
                }
                .onlyResultOrThrow
        return evaluateResult[expression]!!.value
    }

    private val retrieveProvider =
        object : RetrieveProvider {
            override fun retrieve(
                context: String?,
                contextPath: String?,
                contextValue: String?,
                dataType: String,
                templateId: String?,
                codePath: String?,
                codes: Iterable<Code>?,
                valueSet: String?,
                datePath: String?,
                dateLowPath: String?,
                dateHighPath: String?,
                dateRange: Interval?,
            ): Iterable<Value?>? {
                val allPatients =
                    setOf(PATIENT_123, PATIENT_456, PATIENT_789, PATIENT_ABC, PATIENT_DEF)
                val allPractitioners = setOf(PRACTITIONER_XYZ, PRACTITIONER_ZULU)

                // a) All matching patients for the patient being searched by ID=123
                if (
                    PATIENT == dataType &&
                        PATIENT == context &&
                        ID == contextPath &&
                        _PATIENT_123 == contextValue
                ) {
                    return allPatients
                        .filter { _PATIENT_123 == it.getId() }
                        .map { r4ModelResolver!!.toCqlValue(it) }
                }

                // b) All practitioners matching XYZ and patient 123
                if (
                    PRACTITIONER == dataType &&
                        PATIENT == context &&
                        ID == codePath &&
                        codesEqual(codes, PRACTITIONER_SLASH + XYZ)
                ) {
                    val optPatient123 = allPatients.firstOrNull { _PATIENT_123 == it.getId() }

                    if (optPatient123 != null) {
                        val generalPractitionerIds =
                            optPatient123
                                .getGeneralPractitioner()
                                .map { it.getReference() }
                                .map {
                                    it.split(PRACTITIONER_SLASH.toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .elementAt(1)
                                }

                        return allPractitioners
                            .filter { generalPractitionerIds.contains(it.getId()) }
                            .map { r4ModelResolver!!.toCqlValue(it) }
                    }
                }

                // c) All patients belonging to Patient 123'd generalPractitioner
                val equals = "xyz" == contextValue.toString()
                if (
                    PATIENT == dataType &&
                        PRACTITIONER == context &&
                        GENERAL_PRACTITIONER == contextPath &&
                        equals
                ) {
                    logger.info(">>> patients for practitioner xyz")
                    return allPatients
                        .filter { getMatchingPractitioners(it).contains(PRACTITIONER_XYZ.getId()) }
                        .map { r4ModelResolver!!.toCqlValue(it) }
                }
                return null
            }
        }

    companion object {
        private val logger = LoggerFactory.getLogger(TestCqlEngineRelatedContextSupport::class.java)
        private const val PATIENT = "Patient"
        private const val PRACTITIONER = "Practitioner"
        private val PRACTITIONER_SLASH: String = "$PRACTITIONER/"
        private const val PRIMARY_CARE_DOCTOR = "Primary Care Doctor"
        private const val ALL_PATIENT_FOR_GP = "All Patient for GP"

        private const val URL_FHIR = "http://hl7.org/fhir"

        private const val GENERAL_PRACTITIONER = "generalPractitioner"
        private const val XYZ = "xyz"
        private const val _PATIENT_123 = "123"
        private const val ID = "id"

        private val PRACTITIONER_XYZ = getPractitioner(XYZ, "Nick", "Riviera")
        private val PRACTITIONER_ZULU = getPractitioner("zulu", "Leonard", "McCoy")

        private val PATIENT_123 =
            getPatient(_PATIENT_123, LocalDate.of(1980, Month.JANUARY, 19), PRACTITIONER_XYZ)
        private val PATIENT_456 =
            getPatient("456", LocalDate.of(1985, Month.APRIL, 19), PRACTITIONER_XYZ)
        private val PATIENT_789 =
            getPatient("789", LocalDate.of(1990, Month.JULY, 19), PRACTITIONER_XYZ)

        private val PATIENT_ABC =
            getPatient("abc", LocalDate.of(1970, Month.MARCH, 21), PRACTITIONER_ZULU)
        private val PATIENT_DEF =
            getPatient("def", LocalDate.of(1975, Month.AUGUST, 21), PRACTITIONER_ZULU)

        // TODO: LD: Due to a type erasure and the CQL compiler historically being in separate
        // repositories, two different
        // code paths were merged, resulting in an insidious condition where type erasure has
        // resulted in the declared
        // variable's type being wrong in this instance:  It's actually an Iterable<String>
        private fun codesEqual(codes: Iterable<Code>?, equalTo: String): Boolean {
            if (codes == null) {
                return false
            }

            val iterator = codes.iterator()

            if (!iterator.hasNext()) {
                return false
            }

            val next = iterator.next()

            return equalTo == next.code
        }

        private fun getMatchingPractitioners(thePatient: Patient): List<String> {
            return thePatient.getGeneralPractitioner().map { getIdFromReference(it) }
        }

        private fun getIdFromReference(theInnerReference: Reference): String {
            return theInnerReference
                .getReference()
                .split(PRACTITIONER_SLASH.toRegex())
                .dropLastWhile { it.isEmpty() }
                .elementAt(1)
        }

        private fun getPractitioner(
            practitionerId: String?,
            firstName: String?,
            lastName: String?,
        ): Practitioner {
            val practitioner = Practitioner()

            practitioner.setId(practitionerId)

            practitioner.setName(
                listOf(HumanName().setFamily(lastName).setGiven(listOf(StringType(firstName))))
            )

            return practitioner
        }

        private fun getPatient(
            patientId: String?,
            birthDateLocalDate: LocalDate,
            nullablePractitioner: Practitioner?,
        ): Patient {
            val patient = Patient()

            patient.setId(patientId)

            patient.setBirthDate(
                Date.from(birthDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            )

            if (nullablePractitioner != null) {
                patient.setGeneralPractitioner(
                    listOf(
                        Reference().setReference(PRACTITIONER_SLASH + nullablePractitioner.getId())
                    )
                )
            }

            return patient
        }
    }
}
