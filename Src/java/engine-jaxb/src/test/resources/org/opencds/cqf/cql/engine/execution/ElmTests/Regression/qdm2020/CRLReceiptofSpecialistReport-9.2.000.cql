library CRLReceiptofSpecialistReport version '9.2.000'

using QDM version '5.5'

valueset "Consultant Report": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1006'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services, Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Preventive Care, Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Referral": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1046'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Face to Face Encounter During Measurement Period":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Ophthalmological Services"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"] ) FaceToFaceEncounter
    where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Initial Population":
  exists "Face to Face Encounter During Measurement Period"
    and "First Referral During Measurement Period" is not null

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Numerator":
  exists "Referring Provider Receives Consultant Report to Close Referral Loop"

define "First Referral During Measurement Period":
  First(((["Intervention, Performed": "Referral"] ReferralPerformed
        where ReferralPerformed.relevantDatetime during "Measurement Period"
    )
      union(["Intervention, Order": "Referral"] ReferralOrder
          where ReferralOrder.authorDatetime during "Measurement Period"
          return "Intervention, Performed" { id: ReferralOrder.id, relevantDatetime: ReferralOrder.authorDatetime }
      ))ReferralInterventions
      sort by relevantDatetime
  )

define "Referring Provider Receives Consultant Report to Close Referral Loop":
  ["Communication, Performed": "Consultant Report"] ConsultantReportCommunicated
    with "First Referral During Measurement Period" FirstReferral
      such that FirstReferral.id in ConsultantReportCommunicated.relatedTo
        and ConsultantReportCommunicated.receivedDatetime after FirstReferral.relevantDatetime