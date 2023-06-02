library IntensiveCareUnitVenousThromboembolismProphylaxis version '8.2.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include VTEICU version '3.1.000' called VTEICU
include TJC_Overall version '3.6.000' called TJC

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Atrial Fibrillation/Flutter": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.202'
valueset "Comfort Measures": 'urn:oid:1.3.6.1.4.1.33895.1.3.0.45'
valueset "Direct Thrombin Inhibitor": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.205'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "General or Neuraxial Anesthesia": 'urn:oid:2.16.840.1.113883.3.666.5.1743'
valueset "General Surgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.255'
valueset "Glycoprotein IIb/IIIa Inhibitors": 'urn:oid:2.16.840.1.113762.1.4.1045.41'
valueset "Graduated compression stockings (GCS)": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.256'
valueset "Gynecological Surgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.257'
valueset "Hip Fracture Surgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.258'
valueset "Hip Replacement Surgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.259'
valueset "Injectable Factor Xa Inhibitor for VTE Prophylaxis": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.211'
valueset "INR": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.213'
valueset "Intensive Care Unit": 'urn:oid:2.16.840.1.113762.1.4.1029.206'
valueset "Intermittent pneumatic compression devices (IPC)": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.214'
valueset "Intracranial Neurosurgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.260'
valueset "Intravenous route": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.222'
valueset "Knee Replacement Surgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.261'
valueset "Low Dose Unfractionated Heparin for VTE Prophylaxis": 'urn:oid:2.16.840.1.113762.1.4.1045.39'
valueset "Low Molecular Weight Heparin for VTE Prophylaxis": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.219'
valueset "Low Risk": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.400'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.473'
valueset "Obstetrics": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.263'
valueset "Obstetrics VTE": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.264'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Oral Factor Xa Inhibitor for VTE Prophylaxis or VTE Treatment": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.134'
valueset "Patient Refusal": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.93'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Principal": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.14'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Subcutaneous route": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.223'
valueset "Unfractionated Heparin": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.218'
valueset "Urological Surgery": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.272'
valueset "Venous foot pumps (VFP)": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.230'
valueset "Venous Thromboembolism": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.279'
valueset "Warfarin": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.232'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Risk for venous thromboembolism": '72136-5' from "LOINC" display 'Risk for venous thromboembolism'

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Intervention Comfort Measures":
	["Intervention, Order": "Comfort Measures"]
		union ["Intervention, Performed": "Comfort Measures"]

define "Initial Population":
	"Encounter With Age Range and Without VTE Diagnosis or Obstetrical Conditions"

define "Encounter With ICU Location":
	"Encounter With Age Range and Without VTE Diagnosis or Obstetrical Conditions" QualifyingEncounter
		where exists ( QualifyingEncounter.facilityLocations Location
				where Location.code in "Intensive Care Unit"
					and Location.locationPeriod during QualifyingEncounter.relevantPeriod
		)

define "VTE Prophylaxis by Medication Administered or Device Applied":
	( ["Medication, Administered": "Low Dose Unfractionated Heparin for VTE Prophylaxis"] VTEMedication
			where VTEMedication.route in "Subcutaneous route"
	)
		union ["Medication, Administered": "Low Molecular Weight Heparin for VTE Prophylaxis"]
		union ["Medication, Administered": "Injectable Factor Xa Inhibitor for VTE Prophylaxis"]
		union ["Medication, Administered": "Warfarin"]
		union ["Device, Applied": "Intermittent pneumatic compression devices (IPC)"]
		union ["Device, Applied": "Venous foot pumps (VFP)"]
		union ["Device, Applied": "Graduated compression stockings (GCS)"]

define "No VTE Prophylaxis Medication Administered or Ordered":
	["Medication, Not Administered": "Low Dose Unfractionated Heparin for VTE Prophylaxis"]
		union ["Medication, Not Administered": "Low Molecular Weight Heparin for VTE Prophylaxis"]
		union ["Medication, Not Administered": "Injectable Factor Xa Inhibitor for VTE Prophylaxis"]
		union ["Medication, Not Administered": "Warfarin"]
		union ["Medication, Not Ordered": "Low Dose Unfractionated Heparin for VTE Prophylaxis"]
		union ["Medication, Not Ordered": "Low Molecular Weight Heparin for VTE Prophylaxis"]
		union ["Medication, Not Ordered": "Injectable Factor Xa Inhibitor for VTE Prophylaxis"]
		union ["Medication, Not Ordered": "Warfarin"]

define "No VTE Prophylaxis Device Applied or Ordered":
	["Device, Not Applied": "Intermittent pneumatic compression devices (IPC)"]
		union ["Device, Not Applied": "Venous foot pumps (VFP)"]
		union ["Device, Not Applied": "Graduated compression stockings (GCS)"]
		union ["Device, Not Ordered": "Intermittent pneumatic compression devices (IPC)"]
		union ["Device, Not Ordered": "Venous foot pumps (VFP)"]
		union ["Device, Not Ordered": "Graduated compression stockings (GCS)"]

define "Denominator":
	"Encounter With ICU Location"

define "Encounter With Prior or Present Procedure of Hip or Knee Replacement Surgery":
	"Encounter With ICU Location" QualifyingEncounterICU
		with ( ["Procedure, Performed": "Hip Replacement Surgery"]
			union ["Procedure, Performed": "Knee Replacement Surgery"] ) HipKneeProcedure
			such that HipKneeProcedure.relevantPeriod starts on or before 
			end of QualifyingEncounterICU.relevantPeriod

define "No VTE Prophylaxis Medication or Device Due to Patient Refusal":
	( "No VTE Prophylaxis Medication Administered or Ordered"
		union "No VTE Prophylaxis Device Applied or Ordered" ) NoVTEProphylaxis
		where NoVTEProphylaxis.negationRationale in "Patient Refusal"

define "Admission Without VTE or Obstetrical Conditions":
	Global."Inpatient Encounter" InpatientEncounter
		where not ( exists ( InpatientEncounter.diagnoses EncounterDiagnoses
					where ( EncounterDiagnoses in "Obstetrics"
							or EncounterDiagnoses in "Venous Thromboembolism"
							or EncounterDiagnoses in "Obstetrics VTE"
					)
			)
		)

define "Encounter With First ICU Location Stay Less Than 1 day":
	"Encounter With ICU Location" QualifyingEncounterICU
		where Global."LengthInDays"(VTEICU."FirstICULocationPeriod"(QualifyingEncounterICU))< 1

define "Encounter With ICU Location And Encounter Less Than 2 Days":
	"Encounter With ICU Location" QualifyingEncounterICU
		where Global."LengthInDays"(QualifyingEncounterICU.relevantPeriod)< 2

define "Encounter With Prior or Present Diagnosis of Atrial Fibrillation or VTE":
	( "Encounter With ICU Location" QualifyingEncounterICU
			with ["Diagnosis": "Atrial Fibrillation/Flutter"] AtrialFibrillation
				such that AtrialFibrillation.prevalencePeriod starts on or before 
				end of QualifyingEncounterICU.relevantPeriod
	)
		union ( "Encounter With ICU Location" QualifyingEncounterICU
				where exists ( QualifyingEncounterICU.diagnoses QualifyingEncounterDiagnoses
						where QualifyingEncounterDiagnoses in "Atrial Fibrillation/Flutter"
				)
		)
		union ( "Encounter With ICU Location" QualifyingEncounterICU
				with ["Diagnosis": "Venous Thromboembolism"] VTEDiagnosis
					such that VTEDiagnosis.prevalencePeriod starts before start of QualifyingEncounterICU.relevantPeriod
		)

define "Encounter With Low Risk for VTE or Anticoagulant Administered":
	"Low Risk for VTE or Anticoagulant Administered From Day of Start of Hospitalization To Day After First ICU Stay"
		union "Low Risk for VTE or Anticoagulant Administered on Day of or Day After Procedure"

define "Encounter With Intervention Comfort Measures on Day of or Day After Procedure":
	from
		"Encounter With ICU Location" QualifyingEncounterICU,
		["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
		"Intervention Comfort Measures" ComfortMeasures
		where AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
			and Coalesce(start of ComfortMeasures.relevantPeriod, ComfortMeasures.authorDatetime)during TJC."CalendarDayOfOrDayAfter"(
				end of AnesthesiaProcedure.relevantPeriod
			)
		return QualifyingEncounterICU

define "No VTE Prophylaxis Device Due to Medical Reason on Day of or Day After Procedure":
	from
		"Encounter With ICU Location" QualifyingEncounterICU,
		["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
		"No VTE Prophylaxis Device Applied or Ordered" NoVTEDevice
		where NoVTEDevice.negationRationale in "Medical Reason"
			and AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
			and NoVTEDevice.authorDatetime during TJC."CalendarDayOfOrDayAfter"(
				end of AnesthesiaProcedure.relevantPeriod
			)
		return QualifyingEncounterICU

define "No VTE Prophylaxis Due to Patient Refusal on Day of or Day After Procedure":
	from
		"Encounter With ICU Location" QualifyingEncounterICU,
		["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
		"No VTE Prophylaxis Medication or Device Due to Patient Refusal" PatientRefusal
		where AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
			and PatientRefusal.authorDatetime during TJC."CalendarDayOfOrDayAfter"(
				end of AnesthesiaProcedure.relevantPeriod
			)
		return QualifyingEncounterICU

define "Low Risk for VTE or Anticoagulant Administered on Day of or Day After Procedure":
	from
		"Encounter With ICU Location" QualifyingEncounterICU,
		["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
		"Is In Low Risk for VTE or On Anticoagulant" LowRiskForVTE
		where AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
			and LowRiskForVTE.authorDatetime during TJC."CalendarDayOfOrDayAfter"(
				end of AnesthesiaProcedure.relevantPeriod
			)
		return QualifyingEncounterICU

define "Encounter With Medication Oral Factor Xa Inhibitor Administered on Day of or Day After First ICU Stay or Procedure":
	( "Encounter With ICU Location" QualifyingEncounterICU
			with ["Medication, Administered": "Oral Factor Xa Inhibitor for VTE Prophylaxis or VTE Treatment"] FactorXaMedication
				such that FactorXaMedication.relevantPeriod starts during TJC."CalendarDayOfOrDayAfter"(VTEICU."StartOfFirstICU"(QualifyingEncounterICU))
	)
		union ( from
				"Encounter With ICU Location" QualifyingEncounterICU,
				["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
				["Medication, Administered": "Oral Factor Xa Inhibitor for VTE Prophylaxis or VTE Treatment"] FactorXaMedication
				where AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
					and FactorXaMedication.relevantPeriod starts during TJC."CalendarDayOfOrDayAfter"(
						end of AnesthesiaProcedure.relevantPeriod
					)
				return QualifyingEncounterICU
		)

define "No VTE Prophylaxis Device Due to Medical Reason From Day Of Start of Hospitalization To Day After First ICU Stay":
	"Encounter With ICU Location" QualifyingEncounterICU
		with "No VTE Prophylaxis Device Applied or Ordered" NoVTEDevice
			such that NoVTEDevice.negationRationale in "Medical Reason"
				and NoVTEDevice.authorDatetime during VTEICU."FromDayOfStartOfHospitalizationToDayAfterFirstICU"(QualifyingEncounterICU)

define "Low Risk for VTE or Anticoagulant Administered From Day of Start of Hospitalization To Day After First ICU Stay":
	"Encounter With ICU Location" QualifyingEncounterICU
		with "Is In Low Risk for VTE or On Anticoagulant" LowRiskForVTE
			such that LowRiskForVTE.authorDatetime during VTEICU."FromDayOfStartOfHospitalizationToDayAfterFirstICU"(QualifyingEncounterICU)

define "No VTE Prophylaxis Due to Patient Refusal From Day Of Start of Hospitalization To Day After First ICU Stay":
	"Encounter With ICU Location" QualifyingEncounterICU
		with "No VTE Prophylaxis Medication or Device Due to Patient Refusal" PatientRefusal
			such that PatientRefusal.authorDatetime during VTEICU.FromDayOfStartOfHospitalizationToDayAfterFirstICU(QualifyingEncounterICU)

define "No VTE Prophylaxis Medication Due to Medical Reason From Day Of Start of Hospitalization To Day After First ICU Stay":
	"Encounter With ICU Location" QualifyingEncounterICU
		with "No VTE Prophylaxis Medication Administered or Ordered" NoVTEMedicaion
			such that NoVTEMedicaion.negationRationale in "Medical Reason"
				and NoVTEMedicaion.authorDatetime during VTEICU."FromDayOfStartOfHospitalizationToDayAfterFirstICU"(QualifyingEncounterICU)

define "No VTE Prophylaxis Medication Due to Medical Reason on Day of or Day After Procedure":
	from
		"Encounter With ICU Location" QualifyingEncounterICU,
		["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
		"No VTE Prophylaxis Medication Administered or Ordered" NoVTEMedication
		where NoVTEMedication.negationRationale in "Medical Reason"
			and AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
			and NoVTEMedication.authorDatetime during TJC."CalendarDayOfOrDayAfter"(
				end of AnesthesiaProcedure.relevantPeriod
			)
		return QualifyingEncounterICU

define "Numerator":
	"Encounter With VTE Prophylaxis Received on Day of or Day After First ICU Stay or Procedure"
		union ( "Encounter With Medication Oral Factor Xa Inhibitor Administered on Day of or Day After First ICU Stay or Procedure"
				intersect ( "Encounter With Prior or Present Diagnosis of Atrial Fibrillation or VTE"
						union "Encounter With Prior or Present Procedure of Hip or Knee Replacement Surgery"
				)
		)
		union "Encounter With Low Risk for VTE or Anticoagulant Administered"
		union "Encounter With No VTE Prophylaxis Due to Medical Reason"
		union "Encounter With No VTE Prophylaxis Due to Patient Refusal"

define "Encounter With No VTE Prophylaxis Due to Medical Reason":
	( "No VTE Prophylaxis Medication Due to Medical Reason From Day Of Start of Hospitalization To Day After First ICU Stay"
			intersect "No VTE Prophylaxis Device Due to Medical Reason From Day Of Start of Hospitalization To Day After First ICU Stay"
	)
		union ( "No VTE Prophylaxis Medication Due to Medical Reason on Day of or Day After Procedure"
				intersect "No VTE Prophylaxis Device Due to Medical Reason on Day of or Day After Procedure"
		)

define "Encounter With No VTE Prophylaxis Due to Patient Refusal":
	"No VTE Prophylaxis Due to Patient Refusal From Day Of Start of Hospitalization To Day After First ICU Stay"
		union "No VTE Prophylaxis Due to Patient Refusal on Day of or Day After Procedure"

define "Denominator Exception":
	"Encounter With First ICU Location Stay Less Than 1 day"

define "Encounter With Intervention Comfort Measures From Day Of Start of Hospitalization To Day After First ICU Stay":
	"Encounter With ICU Location" QualifyingEncounterICU
		with "Intervention Comfort Measures" ComfortMeasures
			such that Coalesce(start of ComfortMeasures.relevantPeriod, ComfortMeasures.authorDatetime)during VTEICU."FromDayOfStartOfHospitalizationToDayAfterFirstICU"(QualifyingEncounterICU)

define "Encounter With VTE Prophylaxis Received on Day of or Day After First ICU Stay or Procedure":
	( "Encounter With ICU Location" QualifyingEncounterICU
			with "VTE Prophylaxis by Medication Administered or Device Applied" VTEProphylaxis
				such that VTEProphylaxis.relevantPeriod starts during TJC."CalendarDayOfOrDayAfter"(VTEICU."StartOfFirstICU"(QualifyingEncounterICU))
	)
		union ( from
				"Encounter With ICU Location" QualifyingEncounterICU,
				["Procedure, Performed": "General or Neuraxial Anesthesia"] AnesthesiaProcedure,
				"VTE Prophylaxis by Medication Administered or Device Applied" VTEProphylaxis
				where AnesthesiaProcedure.relevantPeriod ends 1 day after day of VTEICU."StartOfFirstICU"(QualifyingEncounterICU)
					and VTEProphylaxis.relevantPeriod starts during TJC."CalendarDayOfOrDayAfter"(
						end of AnesthesiaProcedure.relevantPeriod
					)
				return QualifyingEncounterICU
		)

define "Encounter With Age Range and Without VTE Diagnosis or Obstetrical Conditions":
	( Global."Inpatient Encounter" InpatientEncounter
			with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
				such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of InpatientEncounter.relevantPeriod)>= 18
	)
		intersect "Admission Without VTE or Obstetrical Conditions"

define "Is In Low Risk for VTE or On Anticoagulant":
	( ["Assessment, Performed": "Risk for venous thromboembolism"] VTERiskAssessment
			where VTERiskAssessment.result in "Low Risk"
	)
		union ( ["Laboratory Test, Performed": "INR"] INRLabTest
				where INRLabTest.result > 3.0
				return "Assessment, Performed" { id: INRLabTest.id, authorDatetime: INRLabTest.resultDatetime }
		)
		union ( ( ( ["Medication, Administered": "Unfractionated Heparin"] UnfractionatedHeparin
					where UnfractionatedHeparin.route in "Intravenous route"
			)
				union ["Medication, Administered": "Direct Thrombin Inhibitor"]
				union ["Medication, Administered": "Glycoprotein IIb/IIIa Inhibitors"] ) AnticoagulantMedication
				return "Assessment, Performed" { id: AnticoagulantMedication.id, authorDatetime: start of AnticoagulantMedication.relevantPeriod }
		)

define "Encounter With First ICU Stay With Principal Procedure of SCIP VTE Selected Surgery":
	"Encounter With ICU Location" QualifyingEncounterICU
		with ( "SCIP VTE Selected Surgery" Procedure
			where Procedure.ordinality in "Principal" ) SelectedSCIPProcedure
			such that SelectedSCIPProcedure.relevantPeriod ends during TJC."CalendarDayOfOrDayAfter"(VTEICU."StartOfFirstICU"(QualifyingEncounterICU))

define "Denominator Exclusions":
	"Encounter With ICU Location And Encounter Less Than 2 Days"
		union "Encounter With First ICU Stay With Principal Procedure of SCIP VTE Selected Surgery"
		union "Encounter With Intervention Comfort Measures From Day Of Start of Hospitalization To Day After First ICU Stay"
		union "Encounter With Intervention Comfort Measures on Day of or Day After Procedure"

define "SCIP VTE Selected Surgery":
	//'SCIP' stands for surgical care improvement Project
	["Procedure, Performed": "General Surgery"]
		union ["Procedure, Performed": "Gynecological Surgery"]
		union ["Procedure, Performed": "Hip Fracture Surgery"]
		union ["Procedure, Performed": "Hip Replacement Surgery"]
		union ["Procedure, Performed": "Intracranial Neurosurgery"]
		union ["Procedure, Performed": "Knee Replacement Surgery"]
		union ["Procedure, Performed": "Urological Surgery"]