library HIVAIDSPneumocystisJiroveciPneumoniaPCPProphylaxis version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "RXNORM": 'urn:oid:2.16.840.1.113883.6.88'

valueset "CD4+ Count": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1004'
valueset "CD4+ Percentage": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1005'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "HIV 1": 'urn:oid:2.16.840.1.113883.3.464.1003.120.12.1004'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Oral Clindamycin": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1511'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1076'
valueset "Preventive Care - Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Preventive Care- Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Primaquine Phosphate 26.3 MG Oral Tablet": '904170' from "RXNORM" display 'Primaquine Phosphate 26.3 MG Oral Tablet'

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

define "Denominator 1":
	"Initial Population 1"

define "Denominator 2":
	"Initial Population 2"

define "Denominator 3":
	"Initial Population 3"

define "Qualifying Encounters":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Preventive Care- Initial Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care - Established Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Outpatient Consultation"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "HIV Diagnosis":
	["Diagnosis": "HIV 1"] HIV
		where HIV.prevalencePeriod starts before 
		end of "Measurement Period"

define "Denominator Exclusions":
	Hospice."Has Hospice"

define "Followup HIV Visit":
	"Qualifying Encounters" FollowupVisit
		with "Qualifying Encounters" PriorVisit
			such that FollowupVisit.authorDatetime 90 days or more on or after day of PriorVisit.authorDatetime

define "Denominator Exceptions 2":
	exists ( ( ["Laboratory Test, Performed": "CD4+ Count"] FirstCD4Count
				with ["Laboratory Test, Performed": "CD4+ Count"] SecondCD4Count
					such that SecondCD4Count.authorDatetime occurs 3 months or less after day of FirstCD4Count.authorDatetime
						and SecondCD4Count.result >= 500 '/mm3'
		)
			union ( ["Laboratory Test, Performed": "CD4+ Percentage"] FirstCD4Percent
					with ["Laboratory Test, Performed": "CD4+ Percentage"] SecondCD4Percent
						such that SecondCD4Percent.authorDatetime occurs 3 months or less after FirstCD4Percent.authorDatetime
							and SecondCD4Percent.result >= 15 '%'
			)
	)

define "CD4 Count Test Under 200":
	["Laboratory Test, Performed": "CD4+ Count"] CD4CountPerformed
		where ( CD4CountPerformed.result as Quantity ) < 200 '/mm3'
			and CD4CountPerformed.relevantPeriod ends 9 months or less after start of "Measurement Period"

define "CD4 Count Test Under 500":
	["Laboratory Test, Performed": "CD4+ Count"] CD4CountTestPerformed
		where ( CD4CountTestPerformed.result as Quantity ) < 500 '/mm3'
			and CD4CountTestPerformed.relevantPeriod ends 9 months or less after start of "Measurement Period"

define "CD4 Count Test Under 500 or CD4 Percentage Test Less Than 15":
	"CD4 Count Test Under 500"
		union "CD4 Percentage Test Less Than 15"

define "CD4 Percentage Test Less Than 15":
	["Laboratory Test, Performed": "CD4+ Percentage"] CD4PercentTestPerformed
		where ( CD4PercentTestPerformed.result as Quantity ) < 15 '%'
			and CD4PercentTestPerformed.relevantPeriod ends 9 months or less after start of "Measurement Period"

define "Denominator Exceptions 1":
	exists ( "CD4 Count Test Under 200" FirstCD4Count
			with ["Laboratory Test, Performed": "CD4+ Count"] SecondCD4Count
				such that SecondCD4Count.authorDatetime occurs 3 months or less after FirstCD4Count.authorDatetime
					and SecondCD4Count.result >= 200 '/mm3'
	)

define "First HIV Diagnosis":
	First("HIV Diagnosis" FirstDiagnosisHIV
			where FirstDiagnosisHIV.prevalencePeriod starts before 
			end of "Measurement Period"
			sort by start of prevalencePeriod
	)

define "CD4 Count Test and PCP Prophylaxis Active":
	"CD4 Count Test Under 200" CD4TestUnder200
		with ["Medication, Active": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxisActive
			such that PCPProphylaxisActive.relevantPeriod overlaps CD4TestUnder200.relevantPeriod

define "CD4 Count Test and PCP Prophylaxis Order":
	"CD4 Count Test Under 200" CD4TestUnder200
		with ["Medication, Order": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxisOrder
			such that PCPProphylaxisOrder.authorDatetime 3 months or less after 
			end of CD4TestUnder200.relevantPeriod

define "CD4 Count Test Under 500 with PCP Medication Prophylaxis Order":
	"CD4 Count Test Under 500" CD4TestUnder500
		with ["Medication, Order": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxisOrder
			such that PCPProphylaxisOrder.authorDatetime occurs 3 months or less after 
			end of CD4TestUnder500.relevantPeriod

define "CD4 Count Test Under 500 with PCP Medication Prophylaxis Active":
	"CD4 Count Test Under 500" CD4TestUnder500
		with ["Medication, Active": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxisActive
			such that PCPProphylaxisActive.relevantPeriod overlaps CD4TestUnder500.relevantPeriod

define "CD4 Percentage Test Less Than 15 with PCP Medication Prophylaxis Order":
	"CD4 Percentage Test Less Than 15" CD4Percentage
		with ["Medication, Order": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxisOrder
			such that PCPProphylaxisOrder.authorDatetime occurs 3 months or less after 
			end of CD4Percentage.relevantPeriod

define "CD4 Percentage Test Less Than 15 with PCP Medication Prophylaxis Active":
	"CD4 Percentage Test Less Than 15" CD4Percentage
		with ["Medication, Active": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxisActive
			such that PCPProphylaxisActive.relevantPeriod overlaps CD4Percentage.relevantPeriod

define "Initial Population 1":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 6
			and exists ( "HIV Diagnosis" )
			and exists ( "Followup HIV Visit" )
			and exists ( "CD4 Count Test Under 200" )

define "Initial Population 2":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[1, 6 )
			and exists ( "HIV Diagnosis" )
			and exists ( "Followup HIV Visit" )
			and exists ( "CD4 Count Test Under 500 or CD4 Percentage Test Less Than 15" )

define "CD4 Count Test Under 200 With Current PCP Treatment":
	from
		"CD4 Count Test Under 200" CD4CountTestUnder200,
		["Medication, Order": "Oral Clindamycin"] ClindamycinOrder,
		["Medication, Order": "Primaquine Phosphate 26.3 MG Oral Tablet"] PrimaquineOrder
		where ClindamycinOrder.authorDatetime occurs 3 months or less after 
		end of CD4CountTestUnder200.relevantPeriod
			and PrimaquineOrder.authorDatetime occurs 3 months or less after 
			end of CD4CountTestUnder200.relevantPeriod
			and ClindamycinOrder.authorDatetime same day as PrimaquineOrder.authorDatetime
		return CD4CountTestUnder200

define "Numerator 1":
	exists "CD4 Count Test and PCP Prophylaxis Order"
		or exists "CD4 Count Test and PCP Prophylaxis Active"
		or exists "CD4 Count Test Under 200 With Current PCP Treatment"

define "Numerator 2":
	exists "CD4 Count Test Under 500 with PCP Medication Prophylaxis Active"
		or exists "CD4 Count Test Under 500 with PCP Medication Prophylaxis Order"
		or exists "CD4 Percentage Test Less Than 15 with PCP Medication Prophylaxis Active"
		or exists "CD4 Percentage Test Less Than 15 with PCP Medication Prophylaxis Order"

define "Numerator 3":
	exists ( ["Medication, Order": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxis
			with "First HIV Diagnosis" FirstHIVDiagnosis
				such that PCPProphylaxis.authorDatetime same day as start of FirstHIVDiagnosis.prevalencePeriod
	)
		or exists ( ["Medication, Active": "Pneumocystis Jiroveci Pneumonia (PCP) Prophylaxis"] PCPProphylaxis
				with "First HIV Diagnosis" FirstHIVDiagnosis
					such that PCPProphylaxis.relevantPeriod overlaps FirstHIVDiagnosis.prevalencePeriod
		)

define "Initial Population 3":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInDaysAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 42
				and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")< 1
	)
		and "First HIV Diagnosis" is not null
		and exists ( "Followup HIV Visit" )