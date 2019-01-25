library FHIRHelpers version '1.0.2'

using FHIR version '1.0.2'

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

define function ToString(value FHIR.uuid): value.value
define function ToString(value FHIR.ProvenanceEntityRole): value.value
define function ToString(value FHIR.UnitsOfTime): value.value
define function ToString(value FHIR.AddressType): value.value
define function ToString(value FHIR.AllergyIntoleranceCategory): value.value
define function ToString(value FHIR.SpecimenStatus): value.value
define function ToString(value FHIR.DetectedIssueSeverity): value.value
define function ToString(value FHIR.IssueSeverity): value.value
define function ToString(value FHIR.DataElementStringency): value.value
define function ToString(value FHIR.VisionEyes): value.value
define function ToString(value FHIR.StructureDefinitionKind): value.value
define function ToString(value FHIR.QuestionnaireResponseStatus): value.value
define function ToString(value FHIR.AllergyIntoleranceType): value.value
define function ToString(value FHIR.DocumentRelationshipType): value.value
define function ToString(value FHIR.ConformanceResourceStatus): value.value
define function ToString(value FHIR.CarePlanActivityStatus): value.value
define function ToString(value FHIR.ActionList): value.value
define function ToString(value FHIR.ParticipationStatus): value.value
define function ToDateTime(value FHIR.instant): value.value
define function ToDateTime(value FHIR.dateTime): value.value
define function ToDate(value FHIR.date): value.value
define function ToString(value FHIR.DocumentMode): value.value
define function ToString(value FHIR.AssertionOperatorType): value.value
define function ToString(value FHIR.DaysOfWeek): value.value
define function ToString(value FHIR.ConformanceEventMode): value.value
define function ToString(value FHIR.IssueType): value.value
define function ToString(value FHIR.TransactionMode): value.value
define function ToString(value FHIR.ContentType): value.value
define function ToString(value FHIR.FamilyHistoryStatus): value.value
define function ToInteger(value FHIR.positiveInt): value.value
define function ToString(value FHIR.AuditEventParticipantNetworkType): value.value
define function ToString(value FHIR.CommunicationStatus): value.value
define function ToString(value FHIR.ClinicalImpressionStatus): value.value
define function ToString(value FHIR.AssertionResponseTypes): value.value
define function ToString(value FHIR.DeviceUseRequestPriority): value.value
define function ToString(value FHIR.OrderStatus): value.value
define function ToString(value FHIR.NarrativeStatus): value.value
define function ToString(value FHIR.string): value.value
define function ToString(value FHIR.GuidePageKind): value.value
define function ToString(value FHIR.GuideDependencyType): value.value
define function ToString(value FHIR.ResourceVersionPolicy): value.value
define function ToString(value FHIR.MedicationAdministrationStatus): value.value
define function ToString(value FHIR.RestfulConformanceMode): value.value
define function ToString(value FHIR.NamingSystemIdentifierType): value.value
define function ToString(value FHIR.AccountStatus): value.value
define function ToString(value FHIR.ProcedureRequestPriority): value.value
define function ToString(value FHIR.MedicationDispenseStatus): value.value
define function ToString(value FHIR.IdentifierUse): value.value
define function ToString(value FHIR.DigitalMediaType): value.value
define function ToString(value FHIR.BindingStrength): value.value
define function ToString(value FHIR.ParticipantRequired): value.value
define function ToString(value FHIR.XPathUsageType): value.value
define function ToString(value FHIR.InstanceAvailability): value.value
define function ToString(value FHIR.id): value.value
define function ToString(value FHIR.FilterOperator): value.value
define function ToString(value FHIR.NamingSystemType): value.value
define function ToString(value FHIR.ExtensionContext): value.value
define function ToString(value FHIR.ClaimType): value.value
define function ToString(value FHIR.AuditEventOutcome): value.value
define function ToString(value FHIR.ConstraintSeverity): value.value
define function ToString(value FHIR.GuideResourcePurpose): value.value
define function ToString(value FHIR.ProcedureStatus): value.value
define function ToString(value FHIR.DeviceMetricCategory): value.value
define function ToString(value FHIR.QuestionnaireStatus): value.value
define function ToString(value FHIR.ResponseType): value.value
define function ToDecimal(value FHIR.decimal): value.value
define function ToString(value FHIR.AggregationMode): value.value
define function ToString(value FHIR.EventTiming): value.value
define function ToString(value FHIR.GoalStatus): value.value
define function ToString(value FHIR.SearchParamType): value.value
define function ToString(value FHIR.SystemRestfulInteraction): value.value
define function ToString(value FHIR.SubscriptionChannelType): value.value
define function ToString(value FHIR.ProcedureRequestStatus): value.value
define function ToString(value FHIR.ReferralStatus): value.value
define function ToString(value FHIR.AssertionDirectionType): value.value
define function ToString(value FHIR.SlicingRules): value.value
define function ToString(value FHIR.LinkType): value.value
define function ToString(value FHIR.AllergyIntoleranceCriticality): value.value
define function ToString(value FHIR.ConceptMapEquivalence): value.value
define function ToString(value FHIR.PropertyRepresentation): value.value
define function ToString(value FHIR.AuditEventAction): value.value
define function ToString(value FHIR.SearchModifierCode): value.value
define function ToString(value FHIR.DeviceUseRequestStatus): value.value
define function ToString(value FHIR.CompositionStatus): value.value
define function ToString(value FHIR.AppointmentStatus): value.value
define function ToString(value FHIR.MessageSignificanceCategory): value.value
define function ToString(value FHIR.OperationParameterUse): value.value
define function ToString(value FHIR.ListMode): value.value
define function ToString(value FHIR.ObservationStatus): value.value
define function ToString(value FHIR.ParticipantStatus): value.value
define function ToString(value FHIR.AdministrativeGender): value.value
define function ToString(value FHIR.ConformanceStatementKind): value.value
define function ToString(value FHIR.DiagnosticOrderStatus): value.value
define function ToBoolean(value FHIR.boolean): value.value
define function ToString(value FHIR.DeviceMetricCalibrationType): value.value
define function ToString(value FHIR.code): value.value
define function ToString(value FHIR.SupplyRequestStatus): value.value
define function ToString(value FHIR.EncounterLocationStatus): value.value
define function ToString(value FHIR.SupplyDeliveryStatus): value.value
define function ToString(value FHIR.DiagnosticReportStatus): value.value
define function ToString(value FHIR.FlagStatus): value.value
define function ToString(value FHIR.AllergyIntoleranceCertainty): value.value
define function ToString(value FHIR.CarePlanStatus): value.value
define function ToString(value FHIR.ListStatus): value.value
define function ToString(value FHIR.base64Binary): value.value
define function ToString(value FHIR.AddressUse): value.value
define function ToString(value FHIR.ConditionalDeleteStatus): value.value
define function ToString(value FHIR.ContactPointUse): value.value
define function ToString(value FHIR.DeviceMetricOperationalStatus): value.value
define function ToString(value FHIR.NutritionOrderStatus): value.value
define function ToString(value FHIR.DiagnosticOrderPriority): value.value
define function ToString(value FHIR.uri): value.value
define function ToString(value FHIR.Use): value.value
define function ToString(value FHIR.IdentityAssuranceLevel): value.value
define function ToString(value FHIR.DeviceMetricColor): value.value
define function ToString(value FHIR.SearchEntryMode): value.value
define function ToTime(value FHIR.time): value.value
define function ToString(value FHIR.ConditionVerificationStatus): value.value
define function ToString(value FHIR.AllergyIntoleranceSeverity): value.value
define function ToString(value FHIR.OperationKind): value.value
define function ToString(value FHIR.ObservationRelationshipType): value.value
define function ToInteger(value FHIR.unsignedInt): value.value
define function ToString(value FHIR.NameUse): value.value
define function ToString(value FHIR.SubscriptionStatus): value.value
define function ToString(value FHIR.CommunicationRequestStatus): value.value
define function ToString(value FHIR.DocumentReferenceStatus): value.value
define function ToString(value FHIR.LocationMode): value.value
define function ToInteger(value FHIR.integer): value.value
define function ToString(value FHIR.CarePlanRelationship): value.value
define function ToString(value FHIR.LocationStatus): value.value
define function ToString(value FHIR.UnknownContentCode): value.value
define function ToString(value FHIR.NoteType): value.value
define function ToString(value FHIR.EncounterState): value.value
define function ToString(value FHIR.EncounterClass): value.value
define function ToString(value FHIR.HTTPVerb): value.value
define function ToString(value FHIR.EpisodeOfCareStatus): value.value
define function ToString(value FHIR.RemittanceOutcome): value.value
define function ToString(value FHIR.ContactPointSystem): value.value
define function ToString(value FHIR.MedicationOrderStatus): value.value
define function ToString(value FHIR.SlotStatus): value.value
define function ToString(value FHIR.markdown): value.value
define function ToString(value FHIR.MedicationStatementStatus): value.value
define function ToString(value FHIR.QuantityComparator): value.value
define function ToString(value FHIR.oid): value.value
define function ToString(value FHIR.DeviceStatus): value.value
define function ToString(value FHIR.Measmnt_Principle): value.value
define function ToString(value FHIR.SampledDataDataType): value.value
define function ToString(value FHIR.AllergyIntoleranceStatus): value.value
define function ToString(value FHIR.CompositionAttestationMode): value.value
define function ToString(value FHIR.DeviceMetricCalibrationState): value.value
define function ToString(value FHIR.GroupType): value.value
define function ToString(value FHIR.TypeRestfulInteraction): value.value
define function ToString(value FHIR.AnswerFormat): value.value
define function ToString(value FHIR.VisionBase): value.value
define function ToString(value FHIR.BundleType): value.value
