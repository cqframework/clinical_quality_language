library Bonedensityevaluationforpatientswithprostatecancerandreceivingandrogendeprivationtherapy version '4.1.000'

using QDM version '5.5'

codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'
codesystem "AdministrativeGender": 'urn:oid:2.16.840.1.113883.5.1'

valueset "Androgen deprivation therapy for Urology Care": 'urn:oid:2.16.840.1.113762.1.4.1151.48'
valueset "DEXA Dual Energy Xray Absorptiometry, Bone Density for Urology Care": 'urn:oid:2.16.840.1.113762.1.4.1151.38'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Reason refused": 'urn:oid:2.16.840.1.113883.3.600.791'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Prostate Cancer": 'urn:oid:2.16.840.1.113883.3.526.3.319'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Injection of leuprolide acetate for twelve month period (regime/therapy)": '456381000124102' from "SNOMEDCT" display 'Injection of leuprolide acetate for twelve month period (regime/therapy)'
code "Male": 'M' from "AdministrativeGender" display 'Male'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Patient is Male":
  exists ["Patient Characteristic Sex": "Male"]

define "Prostate Cancer Diagnosis":
  ["Diagnosis": "Prostate Cancer"] ProstateCancer
    where ProstateCancer.prevalencePeriod starts same day or before 
    end "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "First Androgen Deprivation Therapy":
  First(["Medication, Active": "Androgen deprivation therapy for Urology Care"] InitialADTTherapy
      with "Prostate Cancer Diagnosis" ProstateCancer
        such that InitialADTTherapy.relevantPeriod starts on or before 
        end ProstateCancer.prevalencePeriod
      with ["Procedure, Order": "Injection of leuprolide acetate for twelve month period (regime/therapy)"] TwelveMonthADTTherapy
        such that InitialADTTherapy.relevantPeriod includes TwelveMonthADTTherapy.authorDatetime
          and InitialADTTherapy.relevantPeriod overlaps "Measurement Period"
      sort by start of relevantPeriod
  )

define "Has Qualifying Encounter":
  exists ["Encounter, Performed": "Office Visit"] Encounter
    where Encounter.relevantPeriod during "Measurement Period"

define "Initial Population":
  "Patient is Male"
    and "Has Qualifying Encounter"
    and "First Androgen Deprivation Therapy" is not null

define "No Bone Density Scan Ordered Due to Patient Refusal":
  from
    "First Androgen Deprivation Therapy" FirstADTTherapy,
    ["Diagnostic Study, Not Ordered": "DEXA Dual Energy Xray Absorptiometry, Bone Density for Urology Care"] DEXANotOrdered
    where ( DEXANotOrdered.authorDatetime 3 months or less after start of FirstADTTherapy.relevantPeriod
        and DEXANotOrdered.negationRationale in "Patient Reason refused"
    )
    return FirstADTTherapy

define "No Bone Density Scan Performed Due to Patient Refusal":
  from
    "First Androgen Deprivation Therapy" FirstADTTherapy,
    ["Diagnostic Study, Not Performed": "DEXA Dual Energy Xray Absorptiometry, Bone Density for Urology Care"] DEXANotPerformed
    where ( DEXANotPerformed.authorDatetime 3 months or less after start of FirstADTTherapy.relevantPeriod
        and DEXANotPerformed.negationRationale in "Patient Reason refused"
    )
    return FirstADTTherapy

define "Denominator Exception":
  exists ( "No Bone Density Scan Ordered Due to Patient Refusal" )
    or exists ( "No Bone Density Scan Performed Due to Patient Refusal" )

define "Has Baseline DEXA Scan Two Years Prior to the Start of or Less than Three Months After the Start of ADT":
  exists ( ( ["Diagnostic Study, Order": "DEXA Dual Energy Xray Absorptiometry, Bone Density for Urology Care"] DEXAOrdered
        with "First Androgen Deprivation Therapy" FirstADT
          such that DEXAOrdered.authorDatetime 3 months or less after start FirstADT.relevantPeriod
            or DEXAOrdered.authorDatetime 2 years or less before start of FirstADT.relevantPeriod
    )
      union ( ["Diagnostic Study, Performed": "DEXA Dual Energy Xray Absorptiometry, Bone Density for Urology Care"] DEXAPerformed
          with "First Androgen Deprivation Therapy" FirstADT
            such that DEXAPerformed.relevantDatetime 3 months or less after start FirstADT.relevantPeriod
              or DEXAPerformed.relevantDatetime 2 years or less before start of FirstADT.relevantPeriod
      )
  )

define "Numerator":
  "Has Baseline DEXA Scan Two Years Prior to the Start of or Less than Three Months After the Start of ADT"