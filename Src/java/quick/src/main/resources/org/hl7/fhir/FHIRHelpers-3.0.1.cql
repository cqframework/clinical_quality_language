library FHIRHelpers version '3.0.1'

using FHIR version '3.0.1'

define function ToInterval(period FHIR.Period):
    if period is null then
        null
    else
        Interval[period."start".value, period."end".value]

define function ToQuantity(quantity FHIR.Quantity):
    if quantity is null then
        null
    else
        System.Quantity { value: quantity.value.value, unit: quantity.unit.value }

define function ToInterval(range FHIR.Range):
    if range is null then
        null
    else
        Interval[ToQuantity(range.low), ToQuantity(range.high)]

define function ToCode(coding FHIR.Coding):
    if coding is null then
        null
    else
        System.Code {
          code: coding.code.value,
          system: coding.system.value,
          version: coding.version.value,
          display: coding.display.value
        }

define function ToConcept(concept FHIR.CodeableConcept):
    if concept is null then
        null
    else
        System.Concept {
            codes: concept.coding C return ToCode(C),
            display: concept.text.value
        }

define function Tobase64Binary(value FHIR.base64Binary): value.value
define function ToBoolean(value FHIR.boolean): value.value
define function ToDate(value FHIR.date): value.value
define function ToDateTime(value FHIR.instant): value.value
define function ToDateTime(value FHIR.dateTime): value.value
define function ToDecimal(value FHIR.decimal): value.value
define function ToInteger(value FHIR.positiveInt): value.value
define function ToInteger(value FHIR.unsignedInt): value.value
define function ToInteger(value FHIR.integer): value.value
define function ToTime(value FHIR.time): value.value

define function ToString(value FHIR.id): value.value
define function ToString(value FHIR.code): value.value
define function ToString(value FHIR.oid): value.value
define function ToString(value FHIR.uri): value.value
define function ToString(value FHIR.uuid): value.value

define function ToString(value AccountStatus): value.value
define function ToString(value ActionCardinalityBehavior): value.value
define function ToString(value ActionConditionKind): value.value
define function ToString(value ActionGroupingBehavior): value.value
define function ToString(value ActionList): value.value
define function ToString(value ActionParticipantType): value.value
define function ToString(value ActionPrecheckBehavior): value.value
define function ToString(value ActionRelationshipType): value.value
define function ToString(value ActionRequiredBehavior): value.value
define function ToString(value ActionSelectionBehavior): value.value
define function ToString(value ActivityDefinitionKind): value.value
define function ToString(value ActivityParticipantType): value.value
define function ToString(value AddressType): value.value
define function ToString(value AddressUse): value.value
define function ToString(value AdministrativeGender): value.value
define function ToString(value AdverseEventCategory): value.value
define function ToString(value AdverseEventCausality): value.value
define function ToString(value AggregationMode): value.value
define function ToString(value AllergyIntoleranceCategory): value.value
define function ToString(value AllergyIntoleranceClinicalStatus): value.value
define function ToString(value AllergyIntoleranceCriticality): value.value
define function ToString(value AllergyIntoleranceSeverity): value.value
define function ToString(value AllergyIntoleranceType): value.value
define function ToString(value AllergyIntoleranceVerificationStatus): value.value
define function ToString(value AppointmentStatus): value.value
define function ToString(value AssertionDirectionType): value.value
define function ToString(value AssertionOperatorType): value.value
define function ToString(value AssertionResponseTypes): value.value
define function ToString(value AuditEventAction): value.value
define function ToString(value AuditEventAgentNetworkType): value.value
define function ToString(value AuditEventOutcome): value.value
define function ToString(value BindingStrength): value.value
define function ToString(value BundleType): value.value
define function ToString(value CapabilityStatementKind): value.value
define function ToString(value CarePlanActivityStatus): value.value
define function ToString(value CarePlanIntent): value.value
define function ToString(value CarePlanStatus): value.value
define function ToString(value CareTeamStatus): value.value
define function ToString(value ChargeItemStatus): value.value
define function ToString(value ClaimResponseStatus): value.value
define function ToString(value ClaimStatus): value.value
define function ToString(value ClinicalImpressionStatus): value.value
define function ToString(value CodeSystemContentMode): value.value
define function ToString(value CodeSystemHierarchyMeaning): value.value
define function ToString(value CommunicationPriority): value.value
define function ToString(value CommunicationRequestStatus): value.value
define function ToString(value CommunicationStatus): value.value
define function ToString(value CompartmentCode): value.value
define function ToString(value CompartmentType): value.value
define function ToString(value CompositionAttestationMode): value.value
define function ToString(value CompositionStatus): value.value
define function ToString(value ConceptMapEquivalence): value.value
define function ToString(value ConceptMapGroupUnmappedMode): value.value
define function ToString(value ConditionClinicalStatus): value.value
define function ToString(value ConditionVerificationStatus): value.value
define function ToString(value ConditionalDeleteStatus): value.value
define function ToString(value ConditionalReadStatus): value.value
define function ToString(value ConsentDataMeaning): value.value
define function ToString(value ConsentExceptType): value.value
define function ToString(value ConsentState): value.value
define function ToString(value ConstraintSeverity): value.value
define function ToString(value ContactPointSystem): value.value
define function ToString(value ContactPointUse): value.value
define function ToString(value ContentType): value.value
define function ToString(value ContractStatus): value.value
define function ToString(value ContributorType): value.value
define function ToString(value CoverageStatus): value.value
define function ToString(value DataElementStringency): value.value
define function ToString(value DayOfWeek): value.value
define function ToString(value DaysOfWeek): value.value
define function ToString(value DetectedIssueSeverity): value.value
define function ToString(value DetectedIssueStatus): value.value
define function ToString(value DeviceMetricCalibrationState): value.value
define function ToString(value DeviceMetricCalibrationType): value.value
define function ToString(value DeviceMetricCategory): value.value
define function ToString(value DeviceMetricColor): value.value
define function ToString(value DeviceMetricOperationalStatus): value.value
define function ToString(value DeviceRequestStatus): value.value
define function ToString(value DeviceUseStatementStatus): value.value
define function ToString(value DiagnosticReportStatus): value.value
define function ToString(value DigitalMediaType): value.value
define function ToString(value DiscriminatorType): value.value
define function ToString(value DocumentConfidentiality): value.value
define function ToString(value DocumentMode): value.value
define function ToString(value DocumentReferenceStatus): value.value
define function ToString(value DocumentRelationshipType): value.value
define function ToString(value EligibilityRequestStatus): value.value
define function ToString(value EligibilityResponseStatus): value.value
define function ToString(value EncounterLocationStatus): value.value
define function ToString(value EncounterStatus): value.value
define function ToString(value EndpointStatus): value.value
define function ToString(value EnrollmentRequestStatus): value.value
define function ToString(value EnrollmentResponseStatus): value.value
define function ToString(value EpisodeOfCareStatus): value.value
define function ToString(value EventCapabilityMode): value.value
define function ToString(value EventTiming): value.value
define function ToString(value ExplanationOfBenefitStatus): value.value
define function ToString(value ExtensionContext): value.value
define function ToString(value FHIRAllTypes): value.value
define function ToString(value FHIRDefinedType): value.value
define function ToString(value FHIRDeviceStatus): value.value
define function ToString(value FHIRSubstanceStatus): value.value
define function ToString(value FamilyHistoryStatus): value.value
define function ToString(value FilterOperator): value.value
define function ToString(value FlagStatus): value.value
define function ToString(value GoalStatus): value.value
define function ToString(value GraphCompartmentRule): value.value
define function ToString(value GroupType): value.value
define function ToString(value GuidanceResponseStatus): value.value
define function ToString(value GuideDependencyType): value.value
define function ToString(value GuidePageKind): value.value
define function ToString(value HTTPVerb): value.value
define function ToString(value IdentifierUse): value.value
define function ToString(value IdentityAssuranceLevel): value.value
define function ToString(value ImmunizationStatus): value.value
define function ToString(value InstanceAvailability): value.value
define function ToString(value IssueSeverity): value.value
define function ToString(value IssueType): value.value
define function ToString(value LinkType): value.value
define function ToString(value LinkageType): value.value
define function ToString(value ListMode): value.value
define function ToString(value ListStatus): value.value
define function ToString(value LocationMode): value.value
define function ToString(value LocationStatus): value.value
define function ToString(value MeasmntPrinciple): value.value
define function ToString(value MeasureReportStatus): value.value
define function ToString(value MeasureReportType): value.value
define function ToString(value MedicationAdministrationStatus): value.value
define function ToString(value MedicationDispenseStatus): value.value
define function ToString(value MedicationRequestIntent): value.value
define function ToString(value MedicationRequestPriority): value.value
define function ToString(value MedicationRequestStatus): value.value
define function ToString(value MedicationStatementStatus): value.value
define function ToString(value MedicationStatementTaken): value.value
define function ToString(value MedicationStatus): value.value
define function ToString(value MessageSignificanceCategory): value.value
define function ToString(value MimeType): value.value
define function ToString(value NameUse): value.value
define function ToString(value NamingSystemIdentifierType): value.value
define function ToString(value NamingSystemType): value.value
define function ToString(value NarrativeStatus): value.value
define function ToString(value NutritionOrderStatus): value.value
define function ToString(value ObservationRelationshipType): value.value
define function ToString(value ObservationStatus): value.value
define function ToString(value OperationKind): value.value
define function ToString(value OperationParameterUse): value.value
define function ToString(value ParameterUse): value.value
define function ToString(value ParticipantRequired): value.value
define function ToString(value ParticipantStatus): value.value
define function ToString(value ParticipationStatus): value.value
define function ToString(value PaymentNoticeStatus): value.value
define function ToString(value PaymentReconciliationStatus): value.value
define function ToString(value ProcedureRequestIntent): value.value
define function ToString(value ProcedureRequestPriority): value.value
define function ToString(value ProcedureRequestStatus): value.value
define function ToString(value ProcedureStatus): value.value
define function ToString(value ProcessRequestStatus): value.value
define function ToString(value ProcessResponseStatus): value.value
define function ToString(value PropertyRepresentation): value.value
define function ToString(value PropertyType): value.value
define function ToString(value ProvenanceEntityRole): value.value
define function ToString(value PublicationStatus): value.value
define function ToString(value QuantityComparator): value.value
define function ToString(value QuestionnaireItemType): value.value
define function ToString(value QuestionnaireResponseStatus): value.value
define function ToString(value ReferenceHandlingPolicy): value.value
define function ToString(value ReferenceVersionRules): value.value
define function ToString(value ReferralCategory): value.value
define function ToString(value ReferralPriority): value.value
define function ToString(value ReferralRequestStatus): value.value
define function ToString(value ReferredDocumentStatus): value.value
define function ToString(value RelatedArtifactType): value.value
define function ToString(value RequestIntent): value.value
define function ToString(value RequestPriority): value.value
define function ToString(value RequestStatus): value.value
define function ToString(value ResearchStudyStatus): value.value
define function ToString(value ResearchSubjectStatus): value.value
define function ToString(value ResourceType): value.value
define function ToString(value ResourceVersionPolicy): value.value
define function ToString(value ResponseType): value.value
define function ToString(value RestfulCapabilityMode): value.value
define function ToString(value RiskAssessmentStatus): value.value
define function ToString(value SearchComparator): value.value
define function ToString(value SearchEntryMode): value.value
define function ToString(value SearchModifierCode): value.value
define function ToString(value SearchParamType): value.value
define function ToString(value SectionMode): value.value
define function ToString(value SlicingRules): value.value
define function ToString(value SlotStatus): value.value
define function ToString(value SpecimenStatus): value.value
define function ToString(value StructureDefinitionKind): value.value
define function ToString(value StructureMapContextType): value.value
define function ToString(value StructureMapGroupTypeMode): value.value
define function ToString(value StructureMapInputMode): value.value
define function ToString(value StructureMapModelMode): value.value
define function ToString(value StructureMapSourceListMode): value.value
define function ToString(value StructureMapTargetListMode): value.value
define function ToString(value StructureMapTransform): value.value
define function ToString(value SubscriptionChannelType): value.value
define function ToString(value SubscriptionStatus): value.value
define function ToString(value SupplyDeliveryStatus): value.value
define function ToString(value SupplyRequestStatus): value.value
define function ToString(value SystemRestfulInteraction): value.value
define function ToString(value SystemVersionProcessingMode): value.value
define function ToString(value TaskIntent): value.value
define function ToString(value TaskPriority): value.value
define function ToString(value TaskStatus): value.value
define function ToString(value TestReportActionResult): value.value
define function ToString(value TestReportParticipantType): value.value
define function ToString(value TestReportResult): value.value
define function ToString(value TestReportStatus): value.value
define function ToString(value TestScriptRequestMethodCode): value.value
define function ToString(value TriggerType): value.value
define function ToString(value TypeDerivationRule): value.value
define function ToString(value TypeRestfulInteraction): value.value
define function ToString(value UDIEntryType): value.value
define function ToString(value UnitsOfTime): value.value
define function ToString(value UnknownContentCode): value.value
define function ToString(value Use): value.value
define function ToString(value VisionBase): value.value
define function ToString(value VisionEyes): value.value
define function ToString(value VisionStatus): value.value
define function ToString(value XPathUsageType): value.value
define function ToString(value qualityType): value.value
define function ToString(value repositoryType): value.value
