/*
This library discusses references in FHIR in general and how they can be used
in CQL to query for related resources.
*/
library FHIRReferencesRevisited

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Diabetes": 'TBD'
valueset "Healthcare Provider": 'TBD'
valueset "Aspirin": 'TBD'

context Patient

/*
References in FHIR:
http://hl7.org/fhir/references.html#Reference

Many elements of FHIR resources are _references_ to other resources. A reference
is always represented in one direction, from a _source_ to a _target_.

References are made to a resource based on its _identity_, and there are several
types of identities in FHIR:
http://hl7.org/fhir/resource.html#id

* Logical id
* Business identifiers
* Canonical URLs

# Logical id
Each resource has an `id` element which contains the _logical id_ of the resource.
This id is server-specific and guaranteed unique within the server and resource type.
(It may be unique more broadly, but that cannot be assumed).
The location of a resource instance is an absolute URI constructed from as:

[base]/[resource-type]/[id]

For example:

http://test.fhir.org/rest/Patient/123

# Business identifiers
Although logical ids are server-specific, as a given resource instance
moves from server to server it refers to the same underlying concept (the
"real-world entity"), and this concept has other identifiers that consistently
identify it across different contexts. These identifiers are called "business
identifiers", and many resources have an `identifier` element that can carry
these identifiers.

# Types of references
Resources have two types of reference elements:

* Resource references
* Canonical references

# Resource references
Resource references are represented using the `reference` type, which contains
at least one of a `reference`, an `identifier`, or a `display`.

The `reference` element is either a relative or absolute URL describing the location
of the resource using its logical id:

* Relative: Patient/123
* Absolute: http://test.fhir.org/rest/Patient/123

The `identifier` element can be used to specify one of the business identifiers.

The `display` element can be used to provide a human-readable description of the referenced resource.

The base specification is intentionally flexible around how these references are used, and
it is up to implementation guides to provide more specific guidance.

US Core in particular specifies that:
http://hl7.org/fhir/us/core/general-guidance.html#referencing-us-core-profiles

The search expectations and US Core Profiles have been developed and tested using
logical FHIR ids. Therefore a reference to a US Core resource SHOULD include a
logical id (Reference.reference), not an identifier (Reference.identifier).

And further:

Many of the profiles in US Core reference other US Core profiles explicitly. For
any other references to base FHIR resources or not formally defined in US Core profiles,
the referenced resource SHOULD be a US Core profile if a US Core profile exists for
that resource type.

More detailed guidance about Must Support for references is available here:
http://hl7.org/fhir/us/core/conformance-expectations.html#must-support---resource-references

# Summary
In general, although identifiers could technically be used to follow references
(because the FHIR specification defines search parameters by identifier), whether
this would be supported in any particular context is dependent on server behavior.
To ensure consistent behavior and support, in a US-realm context, reference
SHOULD be traced by logical id only.
*/

/*
The rest of this document is a review of patterns of reference from a previous
session. Happy to re-review if there is interest.
*/

/*
Singleton element that is a reference
[MedicationRequest.encounter](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.encounter)
dataRequirement: { type: MedicationRequest, relatedDataRequirement: { type: Encounter, relatedByPath: encounter } }
*/
define MedicationRequestWithEncounter:
  [MedicationRequest] M
    with [Encounter] E
      such that E.id = Last(Split(M.encounter.reference, '/'))

/*
Singleton element that is a reference to the subject
[Patient.managingOrganization](http://hl7.org/fhir/patient-definitions.html#Patient.managingOrganization)
dataRequirement: { type: Patient, relatedDataRequirement: { type: Organization, code: type, valueset: "Healthcare Provider", relatedByPath: managingOrganization } }
*/
define PatientManagedByHealthcareProvider:
  Patient P
    with [Organization: type in "Healthcare Provider"] O
      such that O.id = Last(Split(P.managingOrganization.reference, '/'))

// http://example.org/fhir/Organization/123
// http:
//
// example.org
// fhir
// Organization
// 123

/*
Plural element that is a reference
[Encounter.reasonReference](http://hl7.org/fhir/encounter-definitions.html#Encounter.reasonReference)
*/
define EncountersWithDiabetes:
  [Encounter] E
    with [Condition: "Diabetes"] C
      such that C.id in E.reasonReference R return Last(Split(R.reference, '/'))

/*
Element that is a choice, one of which is a reference:
[MedicationRequest.medication](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.medication_x_)
*/
define MedicationRequestForAspirin:
  [MedicationRequest] R
    with [Medication] M
      such that M.id = Last(Split(R.medication.reference, '/'))
        and M.code in "Aspirin"

/*
Can also bring it in to the query with a let:
*/
define MedicationRequestWithAspirin:
  [MedicationRequest] R
    let M: [Medication: id = Last(Split(R.medication.reference, '/'))]
    where M.code in "Aspirin"

/*
1.5 Translator supports .resolve() and .reference() from FHIRPath
*/
// If a reference is resolved in the scope of a query, that resolve can be rewritten as an include in the retrieve for that source
// MedicationRequest
define TestMedicationRequest1:
  [MedicationRequest] MR
    where MR.medication.reference.resolve().as(Medication).code in "Aspirin"

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication

/*
.reference() function returns a "Reference" to a resource suitable
for comparison with equality
*/
define TestMedicationRequest1A:
  [MedicationRequest] MR
    with [Medication] M
      such that MR.medication = M.reference()
        and M.code in "Aspirin"

/*
1.5 Translator also supports related context retrieve usage:
*/
define TestMedicationRequest1B:
  [MedicationRequest] MR
    with [MR.medication -> Medication] M
      such that M.code in "Aspirin"

/*
Can also be brought in to the query with a let:
*/
define TestMedicationRequest1C:
  [MedicationRequest] MR
    let M: singleton from ([MR.medication -> Medication])
    where M.code in "Aspirin"


define TestMedicationRequirement:
  [Medication]

// If only TestBoundDataRequirement is referenced, the data requirement should only be [Medication: code]
define TestBoundDataRequirement:
  TestMedicationRequirement R
    where R.code in "Aspirin"


define TestChoiceMedicationRequest:
  [MedicationRequest: "Aspirin"]

// If the terminology target is a choice of a terminology-valued element and a reference

define TestChoiceMedicationRequestExpanded:
  [MedicationRequest: medication in "Aspirin"]
   union (
     [MedicationRequest] MR
      with [Medication] M
        such that M.id = Last(Split(MR.medication.reference, '/'))
          and M.code in "Aspirin"
  )