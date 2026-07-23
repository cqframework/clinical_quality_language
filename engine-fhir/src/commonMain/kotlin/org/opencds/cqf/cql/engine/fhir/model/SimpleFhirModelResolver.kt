package org.opencds.cqf.cql.engine.fhir.model

import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.hl7.cql.model.ClassType
import org.opencds.cqf.cql.engine.fhir.fhirModelId
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.anyTypeName

@JsOnlyExport
class SimpleFhirModelResolver(val model: Model) : BaseFhirModelResolver() {
    init {
        require(
            model.modelInfo.name == fhirModelId && model.modelInfo.url == fhirModelNamespaceUri
        ) {
            "Expected FHIR model, but got model with name ${model.modelInfo.name} and url ${model.modelInfo.url}"
        }
    }

    override fun getContextPath(contextType: String?, targetType: String?): String? {
        if (targetType == null || contextType == null) {
            return null
        }

        if (
            contextType == "Unfiltered" ||
                contextType == "Unspecified" ||
                contextType == "Population"
        ) {
            return null
        }

        if (targetType == contextType) {
            return "id"
        }

        if (contextType == "Patient") {
            return when (targetType) {
                "Account" -> "subject"
                "AdverseEvent" -> "subject"
                "AllergyIntolerance" -> "patient"
                "Appointment" -> "participant.actor"
                "AppointmentResponse" -> "actor"
                "AuditEvent" -> "agent.who"
                "Basic" -> "author"
                "BiologicallyDerivedProduct" -> "collection.source"
                "BodyStructure" -> "patient"
                "CarePlan" -> "subject"
                "CareTeam" -> "subject"
                "ChargeItem" -> "subject"
                "Claim" -> "patient"
                "ClaimResponse" -> "patient"
                "ClinicalImpression" -> "subject"
                "Communication" -> "subject"
                "CommunicationRequest" -> "subject"
                "Composition" -> "author"
                "Condition" -> "subject"
                "Consent" -> "patient"
                "Contract" -> "author"
                "Coverage" -> "beneficiary"
                "CoverageEligibilityRequest" -> "patient"
                "CoverageEligibilityResponse" -> "patient"
                "DetectedIssue" -> "patient"
                "Device" -> "patient"
                "DeviceRequest" -> "subject"
                "DeviceUseStatement" -> "subject"
                "DiagnosticReport" -> "subject"
                "DocumentManifest" -> "subject"
                "DocumentReference" -> "subject"
                "Encounter" -> "subject"
                "EnrollmentRequest" -> "candidate"
                "EpisodeOfCare" -> "patient"
                "ExplanationOfBenefit" -> "patient"
                "FamilyMemberHistory" -> "patient"
                "Flag" -> "subject"
                "Goal" -> "subject"
                "Group" -> "member.entity"
                "GuidanceResponse" -> "subject"
                "ImagingStudy" -> "subject"
                "Immunization" -> "patient"
                "ImmunizationEvaluation" -> "patient"
                "ImmunizationRecommendation" -> "patient"
                "Invoice" -> "subject"
                "List" -> "subject"
                "MeasureReport" -> "subject"
                "Media" -> "subject"
                "MedicationAdministration" -> "subject"
                "MedicationDispense" -> "subject"
                "MedicationRequest" -> "subject"
                "MedicationStatement" -> "subject"
                "MolecularSequence" -> "patient"
                "NutritionOrder" -> "patient"
                "Observation" -> "subject"
                "Patient" -> "id"
                "Person" -> "link.target"
                "Procedure" -> "subject"
                "Provenance" -> "agent.who"
                "QuestionnaireResponse" -> "subject"
                "RelatedPerson" -> "patient"
                "RequestGroup" -> "subject"
                "ResearchSubject" -> "individual"
                "RiskAssessment" -> "subject"
                "Schedule" -> "actor"
                "ServiceRequest" -> "subject"
                "Specimen" -> "subject"
                "SupplyDelivery" -> "patient"
                "SupplyRequest" -> "requester"
                "Task" -> "for"
                "VisionPrescription" -> "patient"
                else ->
                    throw UnsupportedOperationException(
                        "Unable to resolve context path linking $targetType to Patient"
                    )
            }
        }

        throw UnsupportedOperationException(
            "Unable to resolve context path linking $targetType to $contextType"
        )
    }

    override fun `is`(valueType: String, type: QName): Boolean? {

        // System.Any is a supertype of all types
        if (type == anyTypeName) {
            return true
        }

        if (type.getNamespaceURI() != fhirModelNamespaceUri) {
            // FHIR model types only extend System.Any or other FHIR model types
            return false
        }

        val valueDataType = model.resolveTypeName(valueType) ?: return null
        val typeClass = model.resolveTypeName(type.getLocalPart()) ?: return null

        return valueDataType.isSubTypeOf(typeClass)
    }

    override fun createInstance(typeName: String?): Value? {
        requireNotNull(typeName) { "Expected type name for FHIR type, but got null" }
        val classType = model.resolveTypeName(typeName) as? ClassType ?: return null

        return ClassInstance(
            QName(fhirModelNamespaceUri, typeName.removePrefix("FHIR."), fhirModelId),
            classType.allElements.associate { it.name to null }.toMutableMap(),
        )
    }

    override fun resolveId(target: Value?): String? {
        if (target is ClassInstance && target.type.getNamespaceURI() == fhirModelNamespaceUri) {
            val targetClassType =
                model.resolveTypeName(target.type.getLocalPart()) as? ClassType ?: return null
            val resourceClassType =
                model.resolveTypeName("Resource") as? ClassType
                    ?: error("FHIR.Resource class type not found in model")
            if (targetClassType.isSubTypeOf(resourceClassType)) {
                val id = target.elements["id"] as? ClassInstance ?: return null
                return (id.elements["value"] as? org.opencds.cqf.cql.engine.runtime.String)?.value
            }
        }
        return null
    }
}
