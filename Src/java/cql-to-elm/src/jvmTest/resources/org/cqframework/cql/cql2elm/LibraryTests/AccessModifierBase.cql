library AccessModifierBase

private codesystem "ICD-10:2014": 'ICD-10' version '2014'

private codesystem "SNOMED-CT:2020": 'http://snomed.info/sct' version '2020'
private codesystem "ICD-9CM:2020": '2.16.840.1.113883.6.103' version '2020'

private code "XYZ Code": 'XYZ' from "SNOMED-CT:2020" display 'XYZ Code'
private code "ABC Code": 'ABC' from "ICD-9CM:2020" display 'ABC Code'

private concept "XYZ Concept": { "XYZ Code", "ABC Code" } display 'XYZ Concept'

private valueset "Female Administrative Sex": '2.16.840.1.113883.3.560.100.2'

private parameter "Test Parameter" Integer

define private function f1(arg String): arg

define private PrivateExpression: Tuple { Id : '12345', Name : 'John Doe' }