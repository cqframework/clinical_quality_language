library Hospice_QICore4 version '2.0.000'

/*
This example is a work in progress and should not be considered a final specification
or recommendation for guidance. This example will help guide and direct the process
of finding conventions and usage patterns that meet the needs of the various stakeholders
in the measure development community.
*/

/*
@update: @@BTR 2020-03-31 ->
Incremented version to 2.0.000
Updated FHIR version to 4.0.1
@@
*/

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

include MATGlobalCommonFunctions_QICore4 version '5.0.000' called Global

codesystem "SNOMEDCT:2017-09": 'http://snomed.info/sct/731000124108' version 'http://snomed.info/sct/731000124108/version/201709'

valueset "Encounter Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.5.307'
valueset "Hospice care ambulatory": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1108.15'

code "Discharge to healthcare facility for hospice care (procedure)": '428371000124100' from "SNOMEDCT:2017-09" display 'Discharge to healthcare facility for hospice care (procedure)'
code "Discharge to home for hospice care (procedure)": '428361000124107' from "SNOMEDCT:2017-09" display 'Discharge to home for hospice care (procedure)'

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0)

context Patient

define "Has Hospice":
	exists (
    [Encounter: "Encounter Inpatient"] DischargeHospice
			where DischargeHospice.status = 'finished'
		    and (
	        DischargeHospice.hospitalization.dischargeDisposition ~ "Discharge to home for hospice care (procedure)"
				    or DischargeHospice.hospitalization.dischargeDisposition ~ "Discharge to healthcare facility for hospice care (procedure)"
	    	)
				and DischargeHospice.period ends during "Measurement Period"
	)
    or exists (
      [ServiceRequest: "Hospice care ambulatory"] HospiceOrder
        where HospiceOrder.intent = 'order'
            and HospiceOrder.authoredOn in "Measurement Period"
    )
    or exists (
      [Procedure: "Hospice care ambulatory"] HospicePerformed
        where HospicePerformed.status = 'completed'
          and Global."Normalize Interval"(HospicePerformed.performed) overlaps "Measurement Period"
    )
