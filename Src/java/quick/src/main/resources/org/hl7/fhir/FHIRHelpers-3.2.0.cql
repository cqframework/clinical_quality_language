library FHIRHelpers version '3.2.0'

using FHIR version '3.2.0'

define function ToInterval(period FHIR.Period):
    Interval[period."start".value, period."end".value]

define function ToQuantity(quantity FHIR.Quantity):
    System.Quantity { value: quantity.value.value, unit: quantity.unit.value }

define function ToInterval(range FHIR.Range):
    Interval[ToQuantity(range.low), ToQuantity(range.high)]

define function ToCode(coding FHIR.Coding):
    System.Code {
      code: coding.code.value,
      system: coding.system.value,
      version: coding.version.value,
      display: coding.display.value
    }

define function ToConcept(concept FHIR.CodeableConcept):
    System.Concept {
        codes: concept.coding C return ToCode(C),
        display: concept.text.value
    }

define function ToString(value uuid): value.value
define function ToString(value TestScriptRequestMethodCode): value.value
define function ToString(value BiologicallyDerivedProductStatus): value.value
define function ToString(value RetirementStatus): value.value
define function ToString(value UnitsOfTime): value.value
define function ToString(value AddressType): value.value
define function ToString(value AllergyIntoleranceCategory): value.value
define function ToString(value IssueSeverity): value.value
define function ToString(value CareTeamStatus): value.value
define function ToString(value EncounterStatus): value.value
define function ToString(value StructureDefinitionKind): value.value
define function ToString(value PublicationStatus): value.value
define function ToString(value CarePlanActivityKind): value.value
define function ToString(value StructureMapSourceListMode): value.value
define function ToString(value RequestStatus): value.value
define function ToString(value QuestionnaireResponseStatus): value.value
define function ToString(value SearchComparator): value.value
define function ToString(value ChargeItemStatus): value.value
define function ToString(value ActionParticipantType): value.value
define function ToString(value AllergyIntoleranceType): value.value
define function ToString(value CarePlanActivityStatus): value.value
define function ToString(value InvoiceStatus): value.value
define function ToString(value ActionList): value.value
define function ToString(value ClaimProcessingCodes): value.value
define function ToString(value ParticipationStatus): value.value
define function ToString(value DocumentMode): value.value
define function ToString(value AssertionOperatorType): value.value
define function ToString(value DaysOfWeek): value.value
define function ToString(value IssueType): value.value
define function ToString(value StructureMapContextType): value.value
define function ToString(value FamilyHistoryStatus): value.value
define function ToString(value status): value.value
define function ToString(value AssertionResponseTypes): value.value
define function ToString(value RequestIntent): value.value
define function ToString(value string): value.value
define function ToString(value ActionRequiredBehavior): value.value
define function ToString(value GraphCompartmentUse): value.value
define function ToString(value AccountStatus): value.value
define function ToString(value MedicationDispenseStatus): value.value
define function ToString(value IdentifierUse): value.value
define function ToString(value StructureMapTargetListMode): value.value
define function ToString(value TestReportParticipantType): value.value
define function ToString(value BindingStrength): value.value
define function ToString(value RequestPriority): value.value
define function ToString(value ParticipantRequired): value.value
define function ToString(value XPathUsageType): value.value
define function ToString(value InstanceAvailability): value.value
define function ToString(value id): value.value
define function ToString(value FilterOperator): value.value
define function ToString(value NamingSystemType): value.value
define function ToString(value ContractResourceStatusCodes): value.value
define function ToString(value validationType): value.value
define function ToString(value ResearchSubjectStatus): value.value
define function ToString(value StructureMapTransform): value.value
define function ToString(value ResponseType): value.value
define function ToDecimal(value decimal): value.value
define function ToString(value AggregationMode): value.value
define function ToString(value SystemRestfulInteraction): value.value
define function ToString(value AdverseEventActuality): value.value
define function ToString(value SubscriptionChannelType): value.value
define function ToString(value AssertionDirectionType): value.value
define function ToString(value canPushUpdates): value.value
define function ToString(value CarePlanIntent): value.value
define function ToString(value ConsentState): value.value
define function ToString(value AllergyIntoleranceCriticality): value.value
define function ToString(value PropertyRepresentation): value.value
define function ToString(value TriggerType): value.value
define function ToString(value CompositionStatus): value.value
define function ToString(value AppointmentStatus): value.value
define function ToString(value MessageSignificanceCategory): value.value
define function ToString(value ListMode): value.value
define function ToString(value ObservationStatus): value.value
define function ToString(value ConsentScopeCodes): value.value
define function ToString(value ResourceType): value.value
define function ToBoolean(value boolean): value.value
define function ToString(value StructureMapGroupTypeMode): value.value
define function ToString(value SupplyRequestStatus): value.value
define function ToString(value EncounterLocationStatus): value.value
define function ToString(value CarePlanStatus): value.value
define function ToString(value ConditionClinicalStatusCodes): value.value
define function ToString(value ProcessOutcomeCodes): value.value
define function ToString(value ConditionalDeleteStatus): value.value
define function ToString(value NutritionOrderStatus): value.value
define function ToString(value uri): value.value
define function ToString(value Use): value.value
define function ToString(value IdentityAssuranceLevel): value.value
define function ToString(value DeviceMetricColor): value.value
define function ToTime(value time): value.value
define function ToString(value ConditionalReadStatus): value.value
define function ToString(value ConditionVerificationStatus): value.value
define function ToString(value AllergyIntoleranceSeverity): value.value
define function ToString(value FinancialResourceStatusCodes): value.value
define function ToString(value OperationKind): value.value
define function ToString(value SubscriptionStatus): value.value
define function ToString(value DocumentReferenceStatus): value.value
define function ToString(value repositoryType): value.value
define function ToString(value LocationStatus): value.value
define function ToString(value NoteType): value.value
define function ToString(value TestReportStatus): value.value
define function ToString(value CodeSystemContentMode): value.value
define function ToString(value FHIRDeviceStatus): value.value
define function ToString(value ContactPointSystem): value.value
define function ToString(value SlotStatus): value.value
define function ToString(value PropertyType): value.value
define function ToString(value TypeDerivationRule): value.value
define function ToString(value GuidanceResponseStatus): value.value
define function ToString(value RelatedArtifactType): value.value
define function ToString(value oid): value.value
define function ToString(value MediaStatus): value.value
define function ToString(value CompartmentType): value.value
define function ToString(value DeviceMetricCalibrationState): value.value
define function ToString(value InvoicePriceComponentType): value.value
define function ToString(value GroupType): value.value
define function ToString(value ImmunizationEvaluationStatusCodes): value.value
define function ToString(value ExampleScenarioActorType): value.value
define function ToString(value HazardousDutyWork): value.value
define function ToString(value ProvenanceEntityRole): value.value
define function ToString(value ContractDataMeaning): value.value
define function ToString(value SpecimenStatus): value.value
define function ToString(value RestfulCapabilityMode): value.value
define function ToString(value DetectedIssueSeverity): value.value
define function ToString(value VisionEyes): value.value
define function ToString(value ConsentDataMeaning): value.value
define function ToString(value DocumentRelationshipType): value.value
define function ToString(value AllergyIntoleranceClinicalStatus): value.value
define function ToString(value TestReportResult): value.value
define function ToString(value ConceptMapGroupUnmappedMode): value.value
define function ToDateTime(value instant): value.value
define function ToDateTime(value dateTime): value.value
define function ToDateTime(value date): value.value
define function ToInteger(value positiveInt): value.value
define function ToString(value ClinicalImpressionStatus): value.value
define function ToString(value NarrativeStatus): value.value
define function ToString(value MeasmntPrinciple): value.value
define function ToString(value EndpointStatus): value.value
define function ToString(value GuidePageKind): value.value
define function ToString(value GuideDependencyType): value.value
define function ToString(value BiologicallyDerivedProductCategory): value.value
define function ToString(value ResourceVersionPolicy): value.value
define function ToString(value MedicationRequestStatus): value.value
define function ToString(value MedicationAdministrationStatus): value.value
define function ToString(value ActionCardinalityBehavior): value.value
define function ToString(value MedicationRequestIntent): value.value
define function ToString(value NamingSystemIdentifierType): value.value
define function ToString(value ImmunizationStatusCodes): value.value
define function ToString(value ConfidentialityClassification): value.value
define function ToString(value need): value.value
define function ToString(value HistoryOfEmploymentStatus): value.value
define function ToString(value DiscriminatorType): value.value
define function ToString(value StructureMapInputMode): value.value
define function ToString(value LinkageType): value.value
define function ToString(value ReferenceHandlingPolicy): value.value
define function ToString(value MedicationStatus): value.value
define function ToString(value ResearchStudyStatus): value.value
define function ToString(value ExtensionContext): value.value
define function ToString(value FHIRDefinedType): value.value
define function ToString(value AuditEventOutcome): value.value
define function ToString(value SpecimenContainedPreference): value.value
define function ToString(value ActionRelationshipType): value.value
define function ToString(value ConstraintSeverity): value.value
define function ToString(value failureAction): value.value
define function ToString(value EventCapabilityMode): value.value
define function ToString(value CodeSearchSupport): value.value
define function ToString(value UDIEntryType): value.value
define function ToString(value UserSessionStatus): value.value
define function ToString(value UserSessionStatusSource): value.value
define function ToString(value DeviceMetricCategory): value.value
define function ToString(value TestReportActionResult): value.value
define function ToString(value CapabilityStatementKind): value.value
define function ToString(value AllergyIntoleranceVerificationStatus): value.value
define function ToString(value EventTiming): value.value
define function ToString(value GoalStatus): value.value
define function ToString(value SearchParamType): value.value
define function ToString(value ActionGroupingBehavior): value.value
define function ToString(value StructureMapModelMode): value.value
define function ToString(value TaskStatus): value.value
define function ToString(value BiologicallyDerivedProductStorageScale): value.value
define function ToString(value GraphCompartmentRule): value.value
define function ToString(value pushTypeAvailable): value.value
define function ToString(value SlicingRules): value.value
define function ToString(value ExplanationOfBenefitStatus): value.value
define function ToString(value LinkType): value.value
define function ToString(value ConceptMapEquivalence): value.value
define function ToString(value FHIRAllTypes): value.value
define function ToString(value AuditEventAction): value.value
define function ToString(value SearchModifierCode): value.value
define function ToString(value EventStatus): value.value
define function ToString(value OperationParameterUse): value.value
define function ToString(value ConsentProvisionType): value.value
define function ToString(value ActionConditionKind): value.value
define function ToString(value qualityType): value.value
define function ToString(value AdministrativeGender): value.value
define function ToString(value QuestionnaireItemType): value.value
define function ToString(value DeviceMetricCalibrationType): value.value
define function ToString(value code): value.value
define function ToString(value ActionSelectionBehavior): value.value
define function ToString(value SupplyDeliveryStatus): value.value
define function ToString(value DiagnosticReportStatus): value.value
define function ToString(value FlagStatus): value.value
define function ToString(value UsualOccupation): value.value
define function ToString(value ListStatus): value.value
define function ToBase64Binary(value base64Binary): value.value
define function ToString(value DeviceUseStatementStatus): value.value
define function ToString(value AuditEventAgentNetworkType): value.value
define function ToString(value ExpressionLanguage): value.value
define function ToString(value AddressUse): value.value
define function ToString(value ContactPointUse): value.value
define function ToString(value DeviceMetricOperationalStatus): value.value
define function ToString(value ContributorType): value.value
define function ToString(value ReferenceVersionRules): value.value
define function ToString(value MeasureReportStatus): value.value
define function ToString(value SearchEntryMode): value.value
define function ToInteger(value unsignedInt): value.value
define function ToString(value NameUse): value.value
define function ToString(value LocationMode): value.value
define function ToInteger(value integer): value.value
define function ToString(value FHIRSubstanceStatus): value.value
define function ToString(value UnknownContentCode): value.value
define function ToString(value HTTPVerb): value.value
define function ToString(value EpisodeOfCareStatus): value.value
define function ToString(value RemittanceOutcome): value.value
define function ToString(value markdown): value.value
define function ToString(value MedicationStatementStatus): value.value
define function ToString(value QuantityComparator): value.value
define function ToString(value MeasureReportType): value.value
define function ToString(value ActionPrecheckBehavior): value.value
define function ToString(value SampledDataDataType): value.value
define function ToString(value CompositionAttestationMode): value.value
define function ToString(value TypeRestfulInteraction): value.value
define function ToString(value CodeSystemHierarchyMeaning): value.value
define function ToString(value validationStatus): value.value
define function ToString(value VisionBase): value.value
define function ToString(value BundleType): value.value
define function ToString(value SystemVersionProcessingMode): value.value
