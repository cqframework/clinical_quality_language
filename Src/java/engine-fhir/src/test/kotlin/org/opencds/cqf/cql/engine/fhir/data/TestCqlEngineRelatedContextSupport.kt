package org.opencds.cqf.cql.engine.fhir.data

import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.*
import org.apache.commons.lang3.tuple.Pair
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.slf4j.Logger
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

        val initialContext = Pair.of<String?, Any?>(PATIENT, _PATIENT_123)

        val resultPatient = evaluate(cqlEngine, PATIENT, initialContext)

        MatcherAssert.assertThat<Any?>(
            resultPatient,
            CoreMatchers.instanceOf<Any?>(Patient::class.java),
        )
        val resultPatientCasted = resultPatient as Patient
        MatcherAssert.assertThat<String?>(
            resultPatientCasted.getId(),
            CoreMatchers.`is`<String?>(_PATIENT_123),
        )
        cqlEngine.state.clearEvaluatedResources()

        val resultPrimaryCareDoctor = evaluate(cqlEngine, PRIMARY_CARE_DOCTOR, initialContext)

        MatcherAssert.assertThat<Any?>(
            resultPrimaryCareDoctor,
            CoreMatchers.instanceOf<Any?>(Practitioner::class.java),
        )
        val resultPractitioner = resultPrimaryCareDoctor as Practitioner
        MatcherAssert.assertThat<Practitioner?>(
            resultPractitioner,
            CoreMatchers.instanceOf<Practitioner?>(Practitioner::class.java),
        )
        MatcherAssert.assertThat<String?>(
            resultPractitioner.getId(),
            CoreMatchers.`is`<String?>(XYZ),
        )
        cqlEngine.state.clearEvaluatedResources()

        val resultAllPatientForGp = evaluate(cqlEngine, ALL_PATIENT_FOR_GP, initialContext)
        cqlEngine.state.clearEvaluatedResources()

        MatcherAssert.assertThat<Any?>(
            resultAllPatientForGp,
            CoreMatchers.instanceOf<Any?>(MutableList::class.java),
        )

        val patientsForPractitioner =
            (resultAllPatientForGp as MutableList<*>)
                .filter { obj: Any? -> Patient::class.java.isInstance(obj) }
                .map { obj: Any? -> Patient::class.java.cast(obj) }

        MatcherAssert.assertThat<Int?>(patientsForPractitioner.size, CoreMatchers.`is`<Int?>(3))
        MatcherAssert.assertThat(
            patientsForPractitioner.map { obj: Patient? -> obj!!.getId() }.toSet(),
            CoreMatchers.`is`(
                setOf(PATIENT_123, PATIENT_456, PATIENT_789)
                    .map { obj: Patient? -> obj!!.getId() }
                    .toSet()
            ),
        )
    }

    private fun evaluate(
        cqlEngine: CqlEngine,
        expression: String,
        initialContext: Pair<String?, Any?>?,
    ): Any? {
        val evaluateResult =
            cqlEngine.evaluate(library!!.identifier!!, mutableSetOf(expression), initialContext)
        return evaluateResult.forExpression(expression)!!.value()
    }

    companion object {
        private val logger: Logger =
            LoggerFactory.getLogger(TestCqlEngineRelatedContextSupport::class.java)
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

        private val PRACTITIONER_XYZ: Practitioner = getPractitioner(XYZ, "Nick", "Riviera")
        private val PRACTITIONER_ZULU: Practitioner = getPractitioner("zulu", "Leonard", "McCoy")

        private val PATIENT_123: Patient =
            getPatient(_PATIENT_123, LocalDate.of(1980, Month.JANUARY, 19), PRACTITIONER_XYZ)
        private val PATIENT_456: Patient =
            getPatient("456", LocalDate.of(1985, Month.APRIL, 19), PRACTITIONER_XYZ)
        private val PATIENT_789: Patient =
            getPatient("789", LocalDate.of(1990, Month.JULY, 19), PRACTITIONER_XYZ)

        private val PATIENT_ABC: Patient =
            getPatient("abc", LocalDate.of(1970, Month.MARCH, 21), PRACTITIONER_ZULU)
        private val PATIENT_DEF: Patient =
            getPatient("def", LocalDate.of(1975, Month.AUGUST, 21), PRACTITIONER_ZULU)

        private val retrieveProvider =
            object : RetrieveProvider {
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
                        return allPatients.filter { patient -> _PATIENT_123 == patient.getId() }
                    }

                    // b) All practitioners matching XYZ and patient 123
                    if (
                        PRACTITIONER == dataType &&
                            PATIENT == context &&
                            ID == codePath &&
                            codesEqual(codes, PRACTITIONER_SLASH + XYZ)
                    ) {
                        val optPatient123 =
                            allPatients.firstOrNull { patient -> _PATIENT_123 == patient.getId() }

                        if (optPatient123 != null) {
                            val generalPractitionerIds =
                                optPatient123
                                    .getGeneralPractitioner()
                                    .map { obj -> obj!!.getReference() }
                                    .map { ref ->
                                        ref!!
                                            .split(PRACTITIONER_SLASH.toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[1]
                                    }

                            return allPractitioners.filter { practitioner ->
                                generalPractitionerIds.contains(practitioner.getId())
                            }
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
                        return allPatients.filter { patient ->
                            getMatchingPractitioners(patient).contains(PRACTITIONER_XYZ.getId())
                        }
                    }
                    return null
                }
            }

        // TODO: LD: Due to a type erasure and the CQL compiler historically being in separate
        // repositories, two different
        // code paths were merged, resulting in an insidious condition where type erasure has
        // resulted in the declared
        // variable's type being wrong in this instance:  It's actually an Iterable<String>
        private fun codesEqual(codes: Iterable<*>?, equalTo: String): Boolean {
            if (codes == null) {
                return false
            }

            val iterator = codes.iterator()

            if (!iterator.hasNext()) {
                return false
            }

            val next = iterator.next()

            // Ignore the javac warning here
            if (!String::class.java.isInstance(next)) {
                Assertions.fail<Any?>("Expected codes to contain Strings but does not: $codes")
            }

            val nextCode = next as String

            return equalTo == nextCode
        }

        private fun getMatchingPractitioners(thePatient: Patient): List<String> {
            return thePatient.getGeneralPractitioner().map { theInnerReference ->
                getIdFromReference(theInnerReference!!)
            }
        }

        private fun getIdFromReference(theInnerReference: Reference): String {
            return theInnerReference
                .getReference()
                .split(PRACTITIONER_SLASH.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        }

        private fun getPractitioner(
            practitionerId: String?,
            firstName: String?,
            lastName: String?,
        ): Practitioner {
            val practitioner = Practitioner()

            practitioner.setId(practitionerId)

            practitioner.setName(
                listOf<HumanName?>(
                    HumanName()
                        .setFamily(lastName)
                        .setGiven(listOf<StringType?>(StringType(firstName)))
                )
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
                    listOf<Reference?>(
                        Reference().setReference(PRACTITIONER_SLASH + nullablePractitioner.getId())
                    )
                )
            }

            return patient
        }
    }
}
