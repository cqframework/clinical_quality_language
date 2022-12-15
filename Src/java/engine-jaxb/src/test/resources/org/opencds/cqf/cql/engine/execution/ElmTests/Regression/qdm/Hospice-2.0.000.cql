library Hospice version '2.0.000'

using QDM version '5.4'

codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96' 

valueset "Encounter Inpatient": 'urn:oid:2.16.840.1.113883.3.666.5.307' 
valueset "Hospice care ambulatory": 'urn:oid:2.16.840.1.113762.1.4.1108.15' 

code "Discharge to healthcare facility for hospice care (procedure)": '428371000124100' from "SNOMEDCT" display 'Discharge to healthcare facility for hospice care (procedure)'
code "Discharge to home for hospice care (procedure)": '428361000124107' from "SNOMEDCT" display 'Discharge to home for hospice care (procedure)'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Has Hospice":
	exists ( ["Encounter, Performed": "Encounter Inpatient"] DischargeHospice
			where ( DischargeHospice.dischargeDisposition as Code ~ "Discharge to home for hospice care (procedure)"
					or DischargeHospice.dischargeDisposition as Code ~ "Discharge to healthcare facility for hospice care (procedure)"
			)
				and DischargeHospice.relevantPeriod ends during "Measurement Period"
	)
		or exists ( ["Intervention, Order": "Hospice care ambulatory"] HospiceOrder
				where HospiceOrder.authorDatetime during "Measurement Period"
		)
		or exists ( ["Intervention, Performed": "Hospice care ambulatory"] HospicePerformed
				where HospicePerformed.relevantPeriod overlaps "Measurement Period"
		)

