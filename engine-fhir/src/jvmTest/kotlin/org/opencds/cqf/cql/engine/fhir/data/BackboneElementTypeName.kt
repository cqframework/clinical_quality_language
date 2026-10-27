package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.shared.QName
import org.hl7.fhir.r4.model.Claim
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender
import org.hl7.fhir.r4.model.ExplanationOfBenefit
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Quantity
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.EvaluationResult
import org.opencds.cqf.cql.engine.fhir.fhirModelId
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/**
 * Backbone elements should be converted with their model info type names (e.g. Patient.Contact),
 * but are currently converted with the simple HAPI class name (e.g. ContactComponent).
 *
 * Those names aren't unique: Claim and ExplanationOfBenefit (among others) both have an
 * ItemComponent, so their items get the same type and resolving it back to a class picks whichever
 * match the FHIR context finds first. Type tests on those elements then give wrong answers.
 */
internal class BackboneElementTypeName : FhirExecutionTestBase() {
    private fun evaluate(): EvaluationResult {
        val retrieveProvider =
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
                ): Iterable<Value?> =
                    when (dataType) {
                        "Patient" ->
                            listOf(
                                Patient().apply {
                                    id = "example"
                                    addContact().gender = AdministrativeGender.FEMALE
                                }
                            )
                        "Observation" ->
                            listOf(Observation().apply { addReferenceRange().low = Quantity(70) })
                        "Claim" -> listOf(Claim().apply { addItem().sequence = 1 })
                        "ExplanationOfBenefit" ->
                            listOf(ExplanationOfBenefit().apply { addItem().sequence = 1 })
                        else -> emptyList()
                    }.map { r4ModelResolver!!.toCqlValue(it) }
            }

        engine.state.environment.registerDataProvider(
            fhirModelNamespaceUri,
            CompositeDataProvider(r4ModelResolver, retrieveProvider),
        )
        return engine.evaluate { library("BackboneElementTypeName") }.onlyResultOrThrow
    }

    @Test
    fun contactHasModelInfoTypeName() {
        val contact = evaluate()["Contact"]!!.value
        assertEquals(
            QName(fhirModelNamespaceUri, "Patient.Contact", fhirModelId),
            (contact as ClassInstance).type,
        )
    }

    @Test
    fun referenceRangeHasModelInfoTypeName() {
        val referenceRange = evaluate()["Reference Range"]!!.value
        assertEquals(
            QName(fhirModelNamespaceUri, "Observation.ReferenceRange", fhirModelId),
            (referenceRange as ClassInstance).type,
        )
    }

    @Test
    fun contactIsPatientContact() {
        assertEquals(true.toCqlBoolean(), evaluate()["Contact Is Patient.Contact"]!!.value)
    }

    @Test
    fun contactIsBackboneElement() {
        assertEquals(true.toCqlBoolean(), evaluate()["Contact Is BackboneElement"]!!.value)
    }

    @Test
    fun contactCastsToPatientContact() {
        val contacts = evaluate()["Contacts As Patient.Contact"]!!.value
        assertEquals(1, (contacts as List).count())
    }

    @Test
    fun referenceRangeIsObservationReferenceRange() {
        assertEquals(
            true.toCqlBoolean(),
            evaluate()["Reference Range Is Observation.ReferenceRange"]!!.value,
        )
    }

    @Test
    fun claimItemIsClaimItem() {
        assertEquals(true.toCqlBoolean(), evaluate()["Claim Item Is Claim.Item"]!!.value)
    }

    @Test
    fun claimItemIsNotExplanationOfBenefitItem() {
        assertEquals(
            false.toCqlBoolean(),
            evaluate()["Claim Item Is ExplanationOfBenefit.Item"]!!.value,
        )
    }

    @Test
    fun explanationOfBenefitItemIsExplanationOfBenefitItem() {
        assertEquals(
            true.toCqlBoolean(),
            evaluate()["EOB Item Is ExplanationOfBenefit.Item"]!!.value,
        )
    }

    @Test
    fun explanationOfBenefitItemIsNotClaimItem() {
        assertEquals(false.toCqlBoolean(), evaluate()["EOB Item Is Claim.Item"]!!.value)
    }
}
