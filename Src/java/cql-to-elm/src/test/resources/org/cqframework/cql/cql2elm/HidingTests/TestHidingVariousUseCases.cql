library TestHidingVariousUseCases

using FHIR version '4.0.1'
include "FHIRHelpers" version '4.0.1'

codesystem "CodeSystem": 'ABC'
valueset "ValueSet": '123'
code "Code": 'XYZ' from "CodeSystem"

define "CodeSystemHidden":
    ({1, 2, 3}) "CodeSystem" return "CodeSystem" //Warn, hides line 6

define "CodeSystemHidden2":
    ({1, 2, 3}) X
        let "CodeSystem" : X + 1 return "CodeSystem" //Warn, hides line 6

define "ValueSetHidden":
    ({1, 2, 3}) "ValueSet" return "ValueSet" //Warn, hides line 7

define "ValueSetHidden2":
    ({1, 2, 3}) X
        let "ValueSet" : X + 1 return "ValueSet" //Warn, hides line 7

define "CodeHidden":
    ({1, 2, 3}) "Code" return "Code" //Warn, hides line 8

define "CodeHidden2":
    ({1, 2, 3}) X
        let "Code" : X + 1 return "Code" //Warn, hides line 8

define "FHIRHelpersHidden":
    ({1, 2, 3}) "Code" return "Code" //Warn, hides line 8

define "FHIRHelpersHidden2":
    ({1, 2, 3}) X
        let "Code" : X + 1 return "Code" //Warn, hides line 8

define "FHIRHidden":
    ({1, 2, 3}) FHIR return FHIR //Warn, hides line 3

define "FHIRHidden2":
    ({1, 2, 3}) X
        let FHIR : X + 1 return FHIR //Warn, hides line 3

define "Definition":
    ({1, 2, 3}) "Definition" return "Definition" //Warn, hides line 45

define "AliasHidden":
    ({1, 2, 3}) "Alias"
        let "Alias": "Alias" + 1 // Warn, hides 49
    return "Alias"

define "VariableHidden":
    ({1, 2, 3}) X
        let var : X + 1
        return
            var varalias
            let var : varalias + 2 // Warn, hides 55
            return varalias

