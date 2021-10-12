library ReferencingLibraryJsonElm

include BaseLibraryElm called Base

valueset "Chlamydia Screening": '2.16.840.1.113883.3.464.1003.110.12.1052'
    codesystems { Base."SNOMED-CT:2014", Base."ICD-9:2014" }

define ReferenceExpression: Base.BaseLibSum

