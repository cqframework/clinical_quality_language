library testassociations version '0.0.000'

// Github Issue [#149](https://github.com/cqframework/clinical_quality_language/issues/149)
// On older versions of Antlr4, this library would never finish parsing

using QDM version '5.4'

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Dead (finding)": '419099009' from "SNOMEDCT" display 'Dead (finding)'
code "Duration (attribute)": '103335007' from "SNOMEDCT" display 'Duration (attribute)'
code "Patient data Document": '55188-7' from "LOINC" display 'Patient data Document'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "test1":
  exists ( ["Patient Characteristic": "Patient data Document"] Alias
      where ToDate(Alias.authorDatetime)= @2012-05-01
  )
    or exists ( ["Patient Characteristic": code in "Patient data Document"] )
    or exists ( ["Patient Characteristic": "Patient data Document"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic": "Patient data Document"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic": "Patient data Document"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Birthdate": "Birth date"] Alias
        where CalculateAgeInYearsAt(Alias.birthDatetime, start of "Measurement Period")> 21
    )
		// this line is where it looks like the issue is
        or CalculateAgeInYearsAt(Patient.birthDatetime, start of "Measurement Period")> 21
    or exists ( ["Patient Characteristic Birthdate": "Birth date"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Birthdate": "Birth date"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Birthdate": "Birth date"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Clinical Trial Participant": "Patient data Document"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Clinical Trial Participant": "Patient data Document"] Alias
        where Alias.reason is not null
    )
    or exists ( ["Patient Characteristic Clinical Trial Participant": "Patient data Document"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Clinical Trial Participant": "Patient data Document"] Alias
        where Alias.relevantPeriod is not null
    )
    or exists ( ["Patient Characteristic Clinical Trial Participant": "Patient data Document"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Ethnicity": "Ethnicity"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Ethnicity": "Ethnicity"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Ethnicity": "Ethnicity"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Expired": "Dead (finding)"] Alias
        where Alias.cause = "Duration (attribute)"
    )
    or exists ( ["Patient Characteristic Expired": "Dead (finding)"] Alias
        where Alias.expiredDatetime < @2012-06-01
    )
    or exists ( ["Patient Characteristic Expired": "Dead (finding)"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Expired": "Dead (finding)"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Expired": "Dead (finding)"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Payer": "Payer"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Payer": "Payer"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Payer": "Payer"] Alias
        where Alias.relevantPeriod during "Measurement Period"
    )
    or exists ( ["Patient Characteristic Payer": "Payer"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Race": "Race"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Race": "Race"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Race": "Race"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Patient Characteristic Sex": "ONC Administrative Sex"] Alias
        where Alias.id is not null
    )
    or exists ( ["Patient Characteristic Sex": "ONC Administrative Sex"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Patient Characteristic Sex": "ONC Administrative Sex"] Alias
        where Alias.reporter is not null
    )
    or exists ( ["Provider Characteristic": "Patient data Document"] Alias
        where Alias.authorDatetime = @2102-05-01T12:50
    )
    or exists ( ["Provider Characteristic": "Patient data Document"] Alias
        where Alias.id is not null
    )
    or exists ( ["Provider Characteristic": "Patient data Document"] Alias
        where Alias.recorder is not null
    )
    or exists ( ["Provider Characteristic": "Patient data Document"] Alias
        where Alias.reporter is not null
    )