/* Common Terminology */
library CommonConcepts version '1.0.000'

using FHIR version '4.0.1'

codesystem "ConditionVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-ver-status'

valueset "Severe Pain Valueset": 'http://example.com/fhir/ValueSet/rc-severePain'
valueset "Functional Disability Valueset": 'http://example.com/fhir/ValueSet/rc_functionalDisabilityDJD'
valueset "Active Condition": 'http://fhir.org/guides/cqf/common/ValueSet/active-condition'

valueset "ro_bodyMassIndex_kg_per_m2": 'http://example.com/fhir/ValueSet/ro_bodyMassIndex_kg_per_m2'
valueset "ro_weight_kg": 'http://example.com/fhir/ValueSet/ro_weight_kg'
valueset "ro_weight_lb": 'http://example.com/fhir/ValueSet/ro_weight_lb'
valueset "ro_height_in": 'http://example.com/fhir/ValueSet/ro_height_in'
valueset "ro_height_cm": 'http://example.com/fhir/ValueSet/ro_height_cm'
valueset "rc_diabetesMellitusChronic": 'http://example.com/fhir/ValueSet/rc_diabetesMellitusChronic'
valueset "rc_coronaryArteryDisease": 'http://example.com/fhir/ValueSet/rc_coronaryArteryDisease'
valueset "rc_hypertension_cambia": 'http://example.com/fhir/ValueSet/rc_hypertension_cambia'
valueset "rc_obstructiveSleepApnea": 'http://example.com/fhir/ValueSet/rc_obstructiveSleepApnea'
valueset "rc_perforationOfStomach_cambia": 'http://example.com/fhir/ValueSet/rc_perforationOfStomach_cambia'
valueset "rc_gastricMucosalErosion_cambia": 'http://example.com/fhir/ValueSet/rc_gastricMucosalErosion_cambia'
valueset "rp_sleeveGastrectomy_cambia": 'http://example.com/fhir/ValueSet/rp_sleeveGastrectomy_cambia'
valueset "rp_initialAdjustableGastricBanding_cambia": 'http://example.com/fhir/ValueSet/rp_initialAdjustableGastricBanding_cambia'
valueset "rp_initialGastricBypassRouxEnYAnastomosis_cambia": 'http://example.com/fhir/ValueSet/rp_initialGastricBypassRouxEnYAnastomosis_cambia'
valueset "rc_gastricBandComplication_cambia": 'http://example.com/fhir/ValueSet/rc_gastricBandComplication_cambia'
valueset "rp_gastricBandAdjustment_cambia": 'http://example.com/fhir/ValueSet/rp_gastricBandAdjustment_cambia'
valueset "rc_gastricFistula_cambia": 'http://example.com/fhir/ValueSet/rc_gastricFistula_cambia'
valueset "rc_gastricSmallBowelObstructionAcquired_cambia": 'http://example.com/fhir/ValueSet/rc_gastricSmallBowelObstructionAcquired_cambia'
valueset "rc_gastricBandInfection_cambia": 'http://example.com/fhir/ValueSet/rc_gastricBandInfection_cambia'
valueset "rc_esophagitis_cambia": 'http://example.com/fhir/ValueSet/rc_esophagitis_cambia'
valueset "rc_barrettEsophagus": 'http://example.com/fhir/ValueSet/rc_barrettEsophagus'
valueset "rc_gastrojejunalUlcer_cambia": 'http://example.com/fhir/ValueSet/rc_gastrojejunalUlcer_cambia'
valueset "rp_biliopancreaticDiversionWithDuodenalSwitch_cambia": 'http://example.com/fhir/ValueSet/rp_biliopancreaticDiversionWithDuodenalSwitch_cambia'

/* Duplicates */
/*
valueset "rp_sleeveGastrectomy_cambia": 'http://example.com/fhir/ValueSet/rp_sleeveGastrectomy_cambia'
valueset "rp_initialAdjustableGastricBanding_cambia": 'http://example.com/fhir/ValueSet/rp_initialAdjustableGastricBanding_cambia'
valueset "rp_initialGastricBypassRouxEnYAnastomosis_cambia": 'http://example.com/fhir/ValueSet/rp_initialGastricBypassRouxEnYAnastomosis_cambia'
valueset "rp_initialGastricBypassRouxEnYAnastomosis_cambia": 'http://example.com/fhir/ValueSet/rp_initialGastricBypassRouxEnYAnastomosis_cambia'
valueset "rp_sleeveGastrectomy_cambia": 'http://example.com/fhir/ValueSet/rp_sleeveGastrectomy_cambia'
valueset "rp_initialAdjustableGastricBanding_cambia": 'http://example.com/fhir/ValueSet/rp_initialAdjustableGastricBanding_cambia'
valueset "rp_initialGastricBypassRouxEnYAnastomosis_cambia": 'http://example.com/fhir/ValueSet/rp_initialGastricBypassRouxEnYAnastomosis_cambia'
*/
/* End of Duplicates */

/*
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
valueset "": 'http://example.com/fhir/ValueSet/'
*/

code "condition-confirmed": 'confirmed' from "ConditionVerificationStatusCodes" display 'confirmed'
