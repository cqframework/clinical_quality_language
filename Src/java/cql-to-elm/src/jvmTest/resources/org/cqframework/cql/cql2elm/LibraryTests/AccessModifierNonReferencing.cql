library AccessModifierNonReferencing

//********private member definition begin**********
private codesystem "ICD-10:2014": 'ICD-10' version '2014'

private codesystem "SNOMED-CT:2020": 'http://snomed.info/sct' version '2020'
private codesystem "ICD-9CM:2020": '2.16.840.1.113883.6.103' version '2020'

private code "XYZ Code": 'XYZ' from "SNOMED-CT:2020" display 'XYZ Code'
private code "ABC Code": 'ABC' from "ICD-9CM:2020" display 'ABC Code'

private concept "XYZ Concept": { "XYZ Code", "ABC Code" } display 'XYZ Concept'

private valueset "Female Administrative Sex": '2.16.840.1.113883.3.560.100.2'
//private member access for codesystem
valueset "Chlamydia Screening": '2.16.840.1.113883.3.464.1003.110.12.1052'
    codesystems { "ICD-10:2014" }

private parameter "Test Parameter" Integer

define private function f1(arg String): arg

define private PrivateExpression: Tuple { Id : '12345', Name : 'John Doe' }

//********private member definition end**********

//private member access for function
define FunctionTestOuterPrivate: f1('hello')

//private member access for expressionDef
define ReferenceExpression: PrivateExpression.Id

//private member access for parameter
define "Test Definition": "Test Parameter"

//private member access for valueset
define GenderExpression: 'Female' in "Female Administrative Sex"

//private member access for code
define CodeRef: "XYZ Code"

//private member access for concept
define ConceptRef: "XYZ Concept"

