library TestCase11

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
Gather sargeable conditions as Lists of conditions.
At an AND, combine conditions from sub-nodes.
At an OR, the result is separate lists of condition lists.
At an AND, if there are already lists of lists, the condition is too complex for analysis (i.e. it's not in DNF or CNF)

If the query is in CNF or DNF, and each clause is sargeable, the requirements are totally selective
If the query is in CNF or DNF, but not every clause is sargeable, the requirements are partially selective
Otherwise, the requirements are not selective
*/

// 11a: totally selective
define TestCase11a:
  [Encounter] E
    where E.status = 'active' // DR1

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "total" },
    { url: "form", valueCode: "disjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" }
      ]
    }
  ]
}
*/

// 11b: partially selective
define TestCase11b:
  [Encounter] E
    where E.status = 'active' // DR1
      and E.period is not null // Not sargeable

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "partial" },
    { url: "form", valueCode: "conjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" }
      ]
    }
  ]
}
*/

// 11c: totally selective
define TestCase11c:
  [Encounter] E
    where E.status = 'active' // DR1
      and E.period.start same day or after @2026-01-01 // DR1

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "total" },
    { url: "form", valueCode: "disjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" }
      ]
    }
  ]
}
*/

// TODO: Currently non-functional, requires union rewrite to distribute the data requirement
// 11d: totally selective, disjunctive
define TestCase11d:
  [Encounter] E
    where E.status = 'active' // DR1
      or E.status = 'completed' // DR2

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "total" },
    { url: "form", valueCode: "disjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" }
      ]
    },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR2" }
      ]
    }
  ]
}
*/

// TODO: Currently non-functional, requires union rewrite to distribute the data requirement
// 11e: totally selective, conjunctive
define TestCase11e:
  [Encounter] E
    where (
        E.status = 'active' // DR1
          or E.status = 'completed' // DR2
      )
      and E.period.start same day or after @2026-01-01 // DR3

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" },
        { url: "dr", valueString: "DR2" }
      ]
    },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR3" }
      ]
    }
  ]
}
*/

// TODO: Currently non-functional, requires union rewrite to distribute the data requirement
// 11f: totally selective, disjunctive
define TestCase11f:
  [Encounter] E
    where E.status = 'active' // DR1
      or (E.status = 'completed' and E.period.start same day or after @2026-01-01) // DR2

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "total" },
    { url: "form", valueCode: "disjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" }
      ]
    },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR2" }
      ]
    }
  ]
}
*/

// TODO: Currently non-functional, requires union rewrite to distribute the data requirement
// 11g: not selective (not in DNF or CNF)
define TestCase11g:
  [Encounter] E
    where E.status = 'active'
      or (
        E.status = 'completed'
          and (
            E.period.start same day or after @2026-01-01
              or E.meta.lastUpdated same day or after @2026-01-01
          )
      )

/*
Expected selectivity: none
*/

// TODO: Currently non-functional, requires union rewrite to distribute the data requirement
// 11h: totally selective, disjunctive
define TestCase11h:
  [Encounter] E
    where E.status = 'active' // DR1
      or (E.status = 'completed' and E.period.start same day or after @2026-01-01) // DR2
      or (E.status = 'completed' and E.meta.lastUpdated same day or after @2026-01-01) // DR3

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "code", valueCode: "total" },
    { url: "form", valueCode: "disjunctive" },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR1" }
      ]
    },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR2" }
      ]
    },
    {
      url: "clause",
      extension: [
        { url: "dr", valueString: "DR3" }
      ]
    }
  ]
}
*/

// 11i0: totally selective, conjunctive, subject
define TestCase11i0:
  [Patient] P
    where P.gender = 'male'

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "expressionIdentifier", valueString: "TestCase11i" },
    { url: "coverage", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    {
      url: "clause",
      extension: [
        { url: "term", valueDataRequirement { type: "Patient", codeFilter: [ { path: "gender", code: "male" } ] } }
      ]
    }
  ]
}
*/

// 11i1: totally selective, conjunctive, subject
define TestCase11i1:
  Patient P
    where P.gender = 'male'

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "expressionIdentifier", valueString: "TestCase11i" },
    { url: "coverage", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    {
      url: "clause",
      extension: [
        { url: "term", valueDataRequirement { type: "Patient", codeFilter: [ { path: "gender", code: "male" } ] } }
      ]
    }
  ]
}
*/

// TODO: Doesn't work because the expression reference isn't inside a query
// Needs to perform data requirements analysis on the inferred data requirement
// 11i: totally selective, conjunctive, subject
define TestCase11i:
  Patient.gender = 'male'

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "expressionIdentifier", valueString: "TestCase11i" },
    { url: "coverage", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    {
      url: "clause",
      extension: [
        { url: "term", valueDataRequirement { type: "Patient", codeFilter: [ { path: "gender", code: "male" } ] } }
      ]
    }
  ]
}
*/

// 11j: totally selective, conjunctive, subject
define TestCase11j:
  Patient.birthDate >= @2008-01-01 // Today() - 18 years

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "expressionIdentifier", valueString: "TestCase11j" },
    { url: "coverage", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    {
      url: "clause",
      extension: [
        { url: "term", valueDataRequirement { type: "Patient", dateFilter: [ { path: "birthDate", valueLow: "2008-01-01" } ] } }
      ]
    }
  ]
}
*/

// 11k0: totally selective, conjunctive, inclusion
define TestCase11k0:
  [Patient] P
    where exists ([Encounter] E where start of E.period >= @2026-01-01) // Today() - 1 year

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "expressionIdentifier", valueString: "TestCase11k0" },
    { url: "coverage", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    { url: "inclusivity", valueCode: "inclusion" },
    {
      url: "clause",
      extension: [
        { url: "term", valueDataRequirement { type: "Encounter", dateFilter: [ { path: "period", valueLow: "2026-01-01" } ] } }
      ]
    }
  ]
}
*/


// 11k: totally selective, conjunctive, inclusion
define TestCase11k:
  exists ([Encounter] E where start of E.period >= @2026-01-01) // Today() - 1 year

/*
Expected selectivity:
{
  url: "http://hl7.org/fhir/uv/cql/StructureDefinition/cql-selectivity",
  extension: [
    { url: "expressionIdentifier", valueString: "TestCase11k" },
    { url: "coverage", valueCode: "total" },
    { url: "form", valueCode: "conjunctive" },
    { url: "inclusivity", valueCode: "inclusion" },
    {
      url: "clause",
      extension: [
        { url: "term", valueDataRequirement { type: "Encounter", dateFilter: [ { path: "period", valueLow: "2026-01-01" } ] } }
      ]
    }
  ]
}
*/

