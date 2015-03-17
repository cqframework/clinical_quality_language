###############################################################################
# EXAMPLES REQUESTED BY THE QUICK TEAM                                        #
###############################################################################

# 2

valueset "Acute Myocardial Infarction" : '2.16.840.1.113883.3.464.1003.104.12.1001'
define st : [Condition: "Acute Myocardial Infarction"] C
  where C.onsetDateTime starts 12 months or less before start of "Measurement Period"

#3

valueset "Percutaneous Coronary Interventions" : '2.16.840.1.113883.3.464.1003.104.12.1010'
define st : [Procedure: "Percutaneous Coronary Interventions"] P
  where P.date ends 12 months or less before start of "Measurement Period"

#4

define st : [MedicationStatement: "Aspirin and Other Anti-thrombotics"] M
  where M.whenGiven ends before start of "Measurement Period"

#5

define st : from
  [Encounter: "Non-Elective Inpatient Encounter"] I,
  [Encounter: "Emergency Department Visit"] E
where I.period starts 1 hour or less after end of E.period
return I

define st : [Encounter: "Non-Elective Inpatient Encounter"] I
  with [Encounter: "Emergency Department Visit"] E such that
    I.period starts 1 hour or less after end of E.period

#6

define st : [Observation: "Group A Streptococcus Test"] T
  with [Encounter: "Ambulatory/ED Visit"] E such that
    T.issued starts 3 days or less before start of E.period
    or T.issued starts during E.period
  where T.value is not null

#7

define st : [Observation: "Best Corrected Visual Acuity"] T
  with [Procedure: "Cataract Surgery"] P such that
    T.issued starts 90 days or less after end of P.date
  where T.valueCodeableConcept in "Visual acuity 20/40 or Better"

#8

define LastOfficeVisit : Last([Encounter: "Office Visit"])
define st : [Observation: "Systolic Blood Pressure"] S
  where S.issued during LastOfficeVisit.period
  and T.valueQuantity < 140 'mmHg'

#9

define st : [Observation: "HbA1c Laboratory Test"] S
  where S.issued during "Measurement Period"
  and T.valueQuantity > 9 '%'

#10

# Using not-yet-developed ProcedureRejected (profile on ProcedureRequest w/ status = 'rejected')

define st : [ProcedureRejected] P
  where p.encounter.type in "Encounter Inpatient"

#16

# The following assumes that "substance" is mapped as primary code attribute of AllergyIntolerance

valueset "Inhaled Corticosteroids" : '2.16.840.1.113883.3.464.1003.196.11.1085'
define st : [AllergyIntolerance: "Inhaled Corticosteroids"]

# If you want it spelled out

valueset "Inhaled Corticosteroids" : '2.16.840.1.113883.3.464.1003.196.11.1085'
define st : [AllergyIntolerance: substance in "Inhaled Corticosteroids"]

#17

# The following assumes that "substance" is mapped as primary code attribute of AllergyIntoleranceRefuted

valueset "ACE Inhibitor or ARB" : '2.16.840.1.113883.3.526.3.1139'
define st : [AllergyIntoleranceRefuted: "ACE Inhibitor or ARB"]

# If you want it spelled out:

valueset "ACE Inhibitor or ARB" : '2.16.840.1.113883.3.526.3.1139'
define st : [AllergyIntoleranceRefuted: substance in "ACE Inhibitor or ARB"]

#19

valueset "Breast Cancer" : '2.16.840.1.113883.3.526.3.389'
define st : [FamilyHistory] F
  where exists (
    F.relation R where R.relationship = 'NMTH' and exists (
      R.condition C where C.type in "Breast Cancer"
    )
  )

#29

# If QUICK uses only one form of abatement[x] use that, otherwise test all three to be sure

valueset "All Cancer" : '2.16.840.1.113883.3.464.1003.108.12.1011'
define st : [Condition: "All Cancer"] C
  where C.abatementDateTime is null
  and C.abatementAge is null
  and not C.abatementBoolean is true

#39

# Note: This finds all statements on non-pregnancy, but to be complete you
# should probably make sure there isn't a more recent statement of pregnancy

valueset "Pregnancy" : '2.16.840.1.113883.3.526.3.378'
define st : [ConditionRefuted: "Pregnancy"]

# Here is the more complex example making sure there is not a more recent
# assertion of pregnancy

valueset "Pregnancy" : '2.16.840.1.113883.3.526.3.378'
define "Last Assertion of Pregnancy" : Last([Condition: "Pregnancy"])
define "Last Refutal of Pregnancy" : Last([ConditionRefuted: "Pregnancy"])
define lastAssertionIsRefutal : "Last Refutal of Pregnancy" is not null
  and (
    "Last Assertion of Pregnancy" is null
    or "Last Assertion of Pregnancy".dateAsserted before "Last Refutal of Pregancy".dateAsserted
  )

#49

# Note: CommunicationRequest CQF profile doesn't exist yet, so this is best guess based on FHIR resource.
# Also note that this is a *query* for a statement that a communication occurred.  It is not an imperative
# order to indicate a communication *should* occur.  Lastly, matching on message text is probably not the
# best approach-- using a code or value set would likely be better.

define st : [CommunicationRequest] C
  where C.mode = 'ordered' and sender.role = 'nurse' and recipient.role = 'doctor'
  and exists (C.messagePart M where M.contentString = 'temperature above 104 F')

# 50

# There is no QUICK FHIR Profile to represent unknown occurrence.  The best we can do is determine that
# there are no statements one way or the other.  I don't know if that's what you want, but here it is.
# Also note that I had to make up a value set for Penicillin (it doesn't already exist in VSAC).

valueset "Penicillin" : '1.2.3.4.5.6.7.8.9'
define "known to be allergic" : exists([AllergyIntolerance: "Penicillin"])
define "known to be tolerant" : exists([AllergyIntoleranceRefuted: "Penicillin"])
define st : not "known to be allergic" and not "known to be tolerant"

# 57

# Note: Might be better to use a valueset than a code, but there isn't a valueset for it.
# Also note that this is not a proposal-- it is an order.

define "Protein Supplementation" : Code('226282004', '2.16.840.1.113883.6.96')
define st : [NutritionOrder] N
  where exists(N.item I where I.supplement.type = "Protein Supplementation")

#60

# NOTE: Had to make up a valueset for HBV Case Management Program

valueset "HBV Case Management Program" : '1.2.3.4.5.6.7.8.9'
define st : [ReferralRequest: serviceRequested in "HBV Case Management Program"]

#61

# NOTE: Assuming 'inactive' is different than 'abated'.  In that case, FHIR doesn't represent that,
# so you might need to use a value set to capture it.  I made one up for this example.

valueset "Inactive Cystic Fibrosis" : '1.2.3.4.5.6.7.8.9'
define st : [Condition: "Inactive Cystic Fibrosis"]

#65

# The following assumes that "type" is mapped as primary code attribute of Procedure

valueset "Counseling for Physical Activity" : '2.16.840.1.113883.3.464.1003.118.12.1035'
define st : [Procedure: "Counseling for Physical Activity"]

# If you want it spelled out

valueset "Counseling for Physical Activity" : '2.16.840.1.113883.3.464.1003.118.12.1035'
define st : [Procedure: type in "Counseling for Physical Activity"]

# 67

# Had to make up valueset for Coronary Heart Disease

parameter "Measurement Period" interval<DateTime>
valueset "Framingham coronary heart disease 10 year risk" : '2.16.840.1.113883.3.600.1561'
valueset "Coronary Heart Disease" : '1.2.3.4.5.6.7.8.9'
define st : [RiskAssessment: method in "Framingham coronary heart disease 10 year risk"] R
  where R.date during "Measurement Period"
  and exists (
    R.prediction P
      where P.outcome in "Coronary Heart Disease"
      and P.probabilityDecimal > 20
      and P.whenPeriod ends 10 years or less after R.date
  )

# ??

define st : [DiagnosticOrder] D
  where D.status = 'completed'
  return tuple {
    OrderIds: D.identifier,
    CompletedOn: Last(D.event E where E.status = 'completed' return E.dateTime)
  }

[Order] O
  where exists (
    O.details D
    where exists (O.item I where I.code in "Pregnancy Test")
  )
  and O.date during MeasurementPeriod

exists ( [DiagnosticOrder: "Pregnancy Test"] O
  where O.status = 'completed'
  and exists (
    O.event E where E.status = 'completed' and E.dateTime during MeasurementPeriod
  )
)

###############################################################################
# ADDITIONAL EXAMPLES REQUESTED IN CQL COMMENT-ONLY BALLOT
###############################################################################

#7: Alternate Data Model (QDM): Also see CMS146v2_using_QDM.cql in Examples

["Encounter, Performed": "Ambulatory/ED Visit"] E
  with ["Diagnosis, Active": "Acute Pharyngitis"] P
    such that
      interval[P."start datetime", P."stop datetime") overlaps after interval[E."start datetime", E."stop datetime")

#10: Value Sets w/ versions and code versions

valueset "Diabetes" : '2.16.840.1.113883.3.464.1003.103.12.1001' version '20140501'

valueset "Diabetes" : '2.16.840.1.113883.3.464.1003.103.12.1001' version '20140501'
  code systems (
    '2.16.840.1.113883.6.96' version '2013-09',
    '2.16.840.1.113883.6.103' version '2014',
    '2.16.840.1.113883.6.90' version '2014' )

#15: Episode of Care: See CMS26v2_CQM.cql in Examples

#18: More Complex Timing Examples: taken care of elsewhere

#21: Organizational Measurements: Deferred for Now

#29: Population-Based Query (e.g., Cancer Registry)

context Population

define "Number of Cancer Patients" : Count(
  [Patient] P
    with [Condition: "Cancer"] C such that C.subject.identifier = P.identifier
)

define "Number of Female Cancer Patients" : Count(
  [Patient] P
    with [Condition: "Cancer"] C such that C.subject.identifier = P.identifier
    where P.gender = 'female'
)

define "Number of Female Breast Cancer Patients Age 40-49" : Count(
  [Patient] P
    with [Condition: "Breast Cancer"] C such that C.subject.identifier = P.identifier
    where P.gender = 'female' and CalculateAgeInYears(P.birthDate) between 40 and 49
)

#82: Example for Every Op in Spec (TBD)

#7 No Operation Left Behind

# These other the ones that seemed most needed

# 2.3 Queries: Query not starting with retrieve

({ 1, 2, 3, 4, 5}) L return L * 2

# Or ObjectRedefine

define FirstInpatientEncounter : First([Encounter] E where E.class = 'inpatient')
define RedefinedEncounter : FirstInpatientEncounter E
  return tuple {
    type: E.type,
    admissionDate: E.period.start
    dischargeDate: E.period.end
  }

define FirstInpatientEncounter : First([Encounter] E where E.class = 'inpatient')
define RedefinedEncounter : tuple {
    type: FirstInpatientEncounter.type,
    admissionDate: FirstInpatientEncounter.period.start
    dischargeDate: FirstInpatientEncounter.period.end
  }

# 2.5.1 Comparison Operators: between

4 between 2 and 8

# 2.5.2.1 And

define IsTrue : true and true
define IsFalse : true and false
define IsAlsoFalse : false and null
define IsNull : true and null

# 2.5.2.2 Or

define IsTrue : true or false
define IsAlsoTrue : true or null
define IsFalse : false or false
define IsNull : false or null

# 2.5.6.1 Operating on Lists: First and Last

First({ 1, 2, 3, 4, 5 })
Last({ 1, 2, 3, 4, 5 })
First({})
Last({})

# 2.5.6.2 Comparing Lists: includes, included in

{ 1, 2, 3, 4, 5 } includes { 5, 2, 3 }
{ 5, 2, 3 } included in { 1, 2, 3, 4, 5 }
{ 1, 2, 3, 4, 5 } includes { 4, 5, 6 }
{ 4, 5, 6 } included in { 1, 2, 3, 4, 5 }

#120: Refers to non-existent section in spec.

#135: Complex Where Clause (multiple conditions)

# NOTE: I made up the "Fever" valueset

define st : [CommunicationRequest] C
  where C.mode = 'ordered'
    and C.sender.role = 'nurse'
    and C.recipient.role = 'doctor'
    and C.indication in "Fever"

#146: Proposal Construction (DRAFT)

define ProposalForProteinSupplement : tuple {
  item: [
    supplement {
      type: [ Code('226282004', '2.16.840.1.113883.6.96', '2014_09_01') ]
    }
  ]
} as NutritionOrder

#180: Inclusion (With) Using Non-Date Attributes

define DispensesForActivePrescription : [MedicationDispense: "Warfarin"] D
  with [MedicationPrescription: "Warfarin"] P
    such that P.status = 'active' and P.identifier = D.authorizingPrescription.identifier

#185: Examples of Occurrence, NonOccurrence, and Not Exists

# The following is true if and only if the EHR contains a record confirming Diabetes
define ConditionKnownToExist : exists ( [Condition: "Diabetes"] )

# The following is true if and only if the EHR contains a record refuting Diabetes
define ConditionKnownToBeAbsent : exists ( [ConditonRefuted: "Diabetes"] )

# The following is true if and only if the EHR does not contain a record confirming Diabetes
# Note: This does not imply that a doctor ever refuted the condition of Diabetes
define ConditionNotKnownToExist : not exists ( [Condition: "Diabetes"] )

# The following is true if and only if the EHR does not contain a record refuting Diabetes
# Note: This does not imply that a doctor ever diagnosed the condition of Diabetes
define ConditionNotKnownToBeAbsent : not exists ( [ConditionRefuted: "Diabetes"] )

# The following is true if and only if the EHR has no records at all concerning Diabetes
define NoInformationOnCondition : not exists ( [Condition: "Diabetes"] union [ConditionRefuted: "Diabetes"] )
