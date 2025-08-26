library ParameterTestInvalid

using QDM

codesystem "Test Code System": '123'
valueset "Test Value Set": '123'
code "Test Code": '' from "Test Code System"
concept "Test Concept": { "Test Code" }

parameter "Test Parameter" Integer

parameter "Invalid Retrieve Parameter" default ["Encounter, Performed"]
parameter "Invalid Expression Parameter" default "Test Definition"
parameter "Invalid Function Parameter" default "Test Function"(1)
parameter "Invalid CodeSystem Parameter" default "Test Code System"
parameter "Invalid ValueSet Parameter" default "Test Value Set"
parameter "Invalid Code Parameter" default "Test Code"
parameter "Invalid Concept Parameter" default "Test Concept"
parameter "Invalid Reference Parameter" default "Test Parameter"

context Patient

define "Test Definition": "Test Parameter"
define function "Test Function"(value Integer): value
