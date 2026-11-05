library BundleHelpers

using FHIR version '1.0.2'

define TestBundle:
  Bundle {
    id: FHIR.id { value: 'x' }
  }

define TestBundleEntry:
  TestBundle.entry

define function Bundle(Resources List<Resource>):
  Bundle {
    entry:
      Resources R
        return FHIR.Bundle.Entry {
          resource: ResourceContainer(R)
        }
  }

define function ResourceContainer(Resource Resource):
  ResourceContainer {
    AllergyIntolerance: Resource as AllergyIntolerance,
    Appointment: Resource as Appointment,
    AppointmentResponse: Resource as AppointmentResponse,
    AuditEvent: Resource as AuditEvent,
    Basic: Resource as Basic,
    Binary: Resource as Binary,
    BodySite: Resource as BodySite,
    Bundle: Resource as Bundle,
    CarePlan: Resource as CarePlan,
    Claim: Resource as Claim,
    ClaimResponse: Resource as ClaimResponse,
    ClinicalImpression: Resource as ClinicalImpression,
    Communication: Resource as Communication,
    CommunicationRequest: Resource as CommunicationRequest,
    Composition: Resource as Composition,
    ConceptMap: Resource as ConceptMap,
    Condition: Resource as Condition,
    Conformance: Resource as Conformance,
    Contract: Resource as Contract,
    Coverage: Resource as Coverage,
    DataElement: Resource as DataElement,
    DetectedIssue: Resource as DetectedIssue,
    Device: Resource as Device,
    DeviceComponent: Resource as DeviceComponent,
    DeviceMetric: Resource as DeviceMetric,
    DeviceUseRequest: Resource as DeviceUseRequest,
    DeviceUseStatement: Resource as DeviceUseStatement,
    DiagnosticReport: Resource as DiagnosticReport,
    DocumentManifest: Resource as DocumentManifest,
    DocumentReference: Resource as DocumentReference,
    EligibilityRequest: Resource as EligibilityRequest,
    EligibilityResponse: Resource as EligibilityResponse,
    Encounter: Resource as Encounter,
    EnrollmentRequest: Resource as EnrollmentRequest,
    EnrollmentResponse: Resource as EnrollmentResponse,
    EpisodeOfCare: Resource as EpisodeOfCare,
    ExplanationOfBenefit: Resource as ExplanationOfBenefit,
    FamilyMemberHistory: Resource as FamilyMemberHistory,
    Flag: Resource as Flag,
    Goal: Resource as Goal,
    Group: Resource as Group,
    HealthcareService: Resource as HealthcareService,
    ImagingObjectSelection: Resource as ImagingObjectSelection,
    ImagingStudy: Resource as ImagingStudy,
    Immunization: Resource as Immunization,
    ImmunizationRecommendation: Resource as ImmunizationRecommendation,
    ImplementationGuide: Resource as ImplementationGuide,
    "List": Resource as "List",
    Location: Resource as Location,
    Media: Resource as Media,
    Medication: Resource as Medication,
    MedicationAdministration: Resource as MedicationAdministration,
    MedicationDispense: Resource as MedicationDispense,
    MedicationOrder: Resource as MedicationOrder,
    MedicationStatement: Resource as MedicationStatement,
    MessageHeader: Resource as MessageHeader,
    NamingSystem: Resource as NamingSystem,
    NutritionOrder: Resource as NutritionOrder,
    Observation: Resource as Observation,
    OperationDefinition: Resource as OperationDefinition,
    OperationOutcome: Resource as OperationOutcome,
    Order: Resource as Order,
    OrderResponse: Resource as OrderResponse,
    Organization: Resource as Organization,
    Parameters: Resource as Parameters,
    Patient: Resource as Patient,
    PaymentNotice: Resource as PaymentNotice,
    PaymentReconciliation: Resource as PaymentReconciliation,
    Person: Resource as Person,
    Practitioner: Resource as Practitioner,
    Procedure: Resource as Procedure,
    ProcedureRequest: Resource as ProcedureRequest,
    ProcessRequest: Resource as ProcessRequest,
    ProcessResponse: Resource as ProcessResponse,
    Provenance: Resource as Provenance,
    Questionnaire: Resource as Questionnaire,
    QuestionnaireResponse: Resource as QuestionnaireResponse,
    ReferralRequest: Resource as ReferralRequest,
    RelatedPerson: Resource as RelatedPerson,
    RiskAssessment: Resource as RiskAssessment,
    Schedule: Resource as Schedule,
    SearchParameter: Resource as SearchParameter,
    Slot: Resource as Slot,
    Specimen: Resource as Specimen,
    StructureDefinition: Resource as StructureDefinition,
    Subscription: Resource as Subscription,
    Substance: Resource as Substance,
    SupplyDelivery: Resource as SupplyDelivery,
    SupplyRequest: Resource as SupplyRequest,
    TestScript: Resource as TestScript,
    ValueSet: Resource as ValueSet,
    VisionPrescription: Resource as VisionPrescription
  }

