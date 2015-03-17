library BaseLibrary

codesystem "SNOMED-CT:2014": 'SNOMED-CT' version '2014'
codesystem "ICD-9:2014": 'ICD-9' version '2014'

valueset "Female Administrative Sex" : '2.16.840.1.113883.3.560.100.2'
valueset "Pregnancy Test" : '2.16.840.1.113883.3.464.1003.111.12.1011' codesystems ( "SNOMED-CT:2014", "ICD-9:2014" )

define BaseExpression : tuple { Id : '12345', Name : 'John Doe' }
