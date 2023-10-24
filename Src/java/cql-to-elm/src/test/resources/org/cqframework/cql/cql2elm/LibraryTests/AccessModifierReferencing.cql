library AccessModifierReferencing

include AccessModifierBase called Base

//private member access for codesystem
valueset "Chlamydia Screening": '2.16.840.1.113883.3.464.1003.110.12.1052'
    codesystems { Base."ICD-10:2014" }

//private member access for function
define FunctionTestOuterPrivate: Base.f1('hello')

//private member access for expressionDef
define ReferenceExpression: Base.PrivateExpression.Id

//private member access for parameter
define "Test Definition": Base."Test Parameter"

//private member access for valueset
define GenderExpression: 'Female' in Base."Female Administrative Sex"

//private member access for code
define CodeRef: Base."XYZ Code"

//private member access for concept
define ConceptRef: Base."XYZ Concept"

