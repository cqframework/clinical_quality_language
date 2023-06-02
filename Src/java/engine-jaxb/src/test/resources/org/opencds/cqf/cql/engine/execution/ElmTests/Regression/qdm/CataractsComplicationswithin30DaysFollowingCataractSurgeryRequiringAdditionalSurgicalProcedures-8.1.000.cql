library CataractsComplicationswithin30DaysFollowingCataractSurgeryRequiringAdditionalSurgicalProcedures version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Acute and Subacute Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1241'
valueset "Adhesions and Disruptions of Iris and Ciliary Body": 'urn:oid:2.16.840.1.113883.3.526.3.1405'
valueset "Anomalies of Pupillary Function": 'urn:oid:2.16.840.1.113883.3.526.3.1406'
valueset "Aspiration and Injection Procedures": 'urn:oid:2.16.840.1.113883.3.526.3.1408'
valueset "Burn Confined to Eye and Adnexa": 'urn:oid:2.16.840.1.113883.3.526.3.1409'
valueset "Cataract Secondary to Ocular Disorders": 'urn:oid:2.16.840.1.113883.3.526.3.1410'
valueset "Cataract Surgery": 'urn:oid:2.16.840.1.113883.3.526.3.1411'
valueset "Cataract, Congenital": 'urn:oid:2.16.840.1.113883.3.526.3.1412'
valueset "Cataract, Mature or Hypermature": 'urn:oid:2.16.840.1.113883.3.526.3.1413'
valueset "Cataract, Posterior Polar": 'urn:oid:2.16.840.1.113883.3.526.3.1414'
valueset "Central Corneal Ulcer": 'urn:oid:2.16.840.1.113883.3.526.3.1428'
valueset "Certain Types of Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1415'
valueset "Chronic Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1416'
valueset "Cloudy Cornea": 'urn:oid:2.16.840.1.113883.3.526.3.1417'
valueset "Corneal Edema": 'urn:oid:2.16.840.1.113883.3.526.3.1418'
valueset "Corneal Opacity and Other Disorders of Cornea": 'urn:oid:2.16.840.1.113883.3.526.3.1419'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Excision of Adhesions": 'urn:oid:2.16.840.1.113883.3.526.3.1422'
valueset "Glaucoma": 'urn:oid:2.16.840.1.113883.3.526.3.1423'
valueset "Glaucoma Associated with Congenital Anomalies, Dystrophies, and Systemic Syndromes": 'urn:oid:2.16.840.1.113883.3.526.3.1461'
valueset "Hereditary Corneal Dystrophies": 'urn:oid:2.16.840.1.113883.3.526.3.1424'
valueset "High Hyperopia": 'urn:oid:2.16.840.1.113883.3.526.3.1425'
valueset "Hypotony of Eye": 'urn:oid:2.16.840.1.113883.3.526.3.1426'
valueset "Injury to Optic Nerve and Pathways": 'urn:oid:2.16.840.1.113883.3.526.3.1427'
valueset "Lens Procedure": 'urn:oid:2.16.840.1.113883.3.526.3.1429'
valueset "Morgagnian Cataract": 'urn:oid:2.16.840.1.113883.3.526.3.1558'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Open Wound of Eyeball": 'urn:oid:2.16.840.1.113883.3.526.3.1430'
valueset "Other Endophthalmitis": 'urn:oid:2.16.840.1.113883.3.526.3.1473'
valueset "Pathologic Myopia": 'urn:oid:2.16.840.1.113883.3.526.3.1432'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Posterior Lenticonus": 'urn:oid:2.16.840.1.113883.3.526.3.1433'
valueset "Pseudoexfoliation Syndrome": 'urn:oid:2.16.840.1.113883.3.526.3.1435'
valueset "Purulent Endophthalmitis": 'urn:oid:2.16.840.1.113883.3.526.3.1477'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Removal Procedures": 'urn:oid:2.16.840.1.113883.3.526.3.1436'
valueset "Retinal Repair Procedures": 'urn:oid:2.16.840.1.113883.3.526.3.1437'
valueset "Retrolental Fibroplasias": 'urn:oid:2.16.840.1.113883.3.526.3.1438'
valueset "Revision Procedures": 'urn:oid:2.16.840.1.113883.3.526.3.1439'
valueset "Scleral Procedures": 'urn:oid:2.16.840.1.113883.3.526.3.1440'
valueset "Traumatic Cataract": 'urn:oid:2.16.840.1.113883.3.526.3.1443'
valueset "Use of Systemic Sympathetic alpha-1a Antagonist Medication for Treatment of Prostatic Hypertrophy": 'urn:oid:2.16.840.1.113883.3.526.3.1442'
valueset "Uveitis": 'urn:oid:2.16.840.1.113883.3.526.3.1444'
valueset "Vascular Disorders of Iris and Ciliary Body": 'urn:oid:2.16.840.1.113883.3.526.3.1445'
valueset "Vitreous Procedures": 'urn:oid:2.16.840.1.113883.3.526.3.1447'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

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

define "Cataract Surgery During Measurement Period":
	["Procedure, Performed": "Cataract Surgery"] CataractSurgery
		where CataractSurgery.relevantPeriod during "Measurement Period"

define "Denominator":
	"Initial Population"

define "Cataract Surgery with Active Systemic Sympathetic alpha 1a Antagonist Medication":
	"Cataract Surgery During Measurement Period" CataractSurgeryPerformed
		with ["Medication, Active": "Use of Systemic Sympathetic alpha-1a Antagonist Medication for Treatment of Prostatic Hypertrophy"] ActiveSympatheticAlpha1aAntagonist
			such that ActiveSympatheticAlpha1aAntagonist.relevantPeriod overlaps before CataractSurgeryPerformed.relevantPeriod

define "Denominator Exclusions":
	"Cataract Surgery with Active Systemic Sympathetic alpha 1a Antagonist Medication"
		union "Cataract Surgery with Significant Ocular Condition"

define "Cataract Surgery with Significant Ocular Condition":
	"Cataract Surgery During Measurement Period" CataractSurgeryPerformed
		with ( ["Diagnosis": "Acute and Subacute Iridocyclitis"]
			union ["Diagnosis": "Adhesions and Disruptions of Iris and Ciliary Body"]
			union ["Diagnosis": "Anomalies of Pupillary Function"]
			union ["Diagnosis": "Burn Confined to Eye and Adnexa"]
			union ["Diagnosis": "Cataract Secondary to Ocular Disorders"]
			union ["Diagnosis": "Cataract, Congenital"]
			union ["Diagnosis": "Cataract, Mature or Hypermature"]
			union ["Diagnosis": "Cataract, Posterior Polar"]
			union ["Diagnosis": "Central Corneal Ulcer"]
			union ["Diagnosis": "Certain Types of Iridocyclitis"]
			union ["Diagnosis": "Chronic Iridocyclitis"]
			union ["Diagnosis": "Cloudy Cornea"]
			union ["Diagnosis": "Corneal Edema"]
			union ["Diagnosis": "Corneal Opacity and Other Disorders of Cornea"]
			union ["Diagnosis": "Glaucoma"]
			union ["Diagnosis": "Glaucoma Associated with Congenital Anomalies, Dystrophies, and Systemic Syndromes"]
			union ["Diagnosis": "Hereditary Corneal Dystrophies"]
			union ["Diagnosis": "High Hyperopia"]
			union ["Diagnosis": "Hypotony of Eye"]
			union ["Diagnosis": "Injury to Optic Nerve and Pathways"]
			union ["Diagnosis": "Morgagnian Cataract"]
			union ["Diagnosis": "Open Wound of Eyeball"]
			union ["Diagnosis": "Other Endophthalmitis"]
			union ["Diagnosis": "Pathologic Myopia"]
			union ["Diagnosis": "Posterior Lenticonus"]
			union ["Diagnosis": "Pseudoexfoliation Syndrome"]
			union ["Diagnosis": "Purulent Endophthalmitis"]
			union ["Diagnosis": "Retrolental Fibroplasias"]
			union ["Diagnosis": "Traumatic Cataract"]
			union ["Diagnosis": "Uveitis"]
			union ["Diagnosis": "Vascular Disorders of Iris and Ciliary Body"] ) ComorbidDiagnosis
			such that ComorbidDiagnosis.prevalencePeriod overlaps before CataractSurgeryPerformed.relevantPeriod

define "Numerator":
	"Cataract Surgery During Measurement Period" CataractSurgeryPerformed
		with ( ["Procedure, Performed": "Aspiration and Injection Procedures"]
			union ["Procedure, Performed": "Excision of Adhesions"]
			union ["Procedure, Performed": "Lens Procedure"]
			union ["Procedure, Performed": "Removal Procedures"]
			union ["Procedure, Performed": "Retinal Repair Procedures"]
			union ["Procedure, Performed": "Revision Procedures"]
			union ["Procedure, Performed": "Scleral Procedures"]
			union ["Procedure, Performed": "Vitreous Procedures"] ) SurgicalComplicationProcedure
			such that SurgicalComplicationProcedure.relevantPeriod starts 30 days or less after day of 
			end of CataractSurgeryPerformed.relevantPeriod
				and SurgicalComplicationProcedure !~ CataractSurgeryPerformed

define "Initial Population":
	"Cataract Surgery During Measurement Period" CataractSurgeryPerformed
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18