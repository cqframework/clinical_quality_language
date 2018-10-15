valueset "Female": '2.16.840.1.113883.3.560.100.2'

define ListOfCodes: { Code { code: 'M' } , Code { code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5' } }

define WrongListOfCodes: { Code { code: 'M' }, Code { code: 'F', system: '3.16.840.1.113883.18.2' } }

define InListOfCodes: ListOfCodes in "Female"

define InWrongListOfCodes: WrongListOfCodes in "Female"