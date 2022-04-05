library Ind2E31A37EB104A7D1 version '1.0.000'

using FHIR version '4.0.1'

include FHIRCommon version '4.0.1' called FHIRCommon
include FHIRHelpers version '4.0.1' called FHIRHelpers
include CommonConcepts version '1.0.000' called CommonCx
include CommonElements version '1.0.000' called CommonEl

context Patient

/* "Id": "2E31A37EB104A7D1" */
define "4. Biliopancreatic bypass with duodenal switch in patients ages greater than or equal to 18 years with BMI greater than or equal to 50 kg/(meter squared)":
  "ageYears" >= 18
    and "bpd"
    and
    (
      ("BMI" >= 50)
        or (("wt"/(("ht" / 100)^2)) >= 50)
        or ((("wt_lb" / 2.2) / (("ht" / 100)^2)) >= 50)
        or ((("wt_lb" / 2.2) / (("ht_in" * (2.54/100))^2)) >= 50)
    )

define "ageYears":
  AgeInYearsAt(Today())

define "bpd":
  exists (
    [ServiceRequest] SR
      where SR.code in CommonCx."rp_biliopancreaticDiversionWithDuodenalSwitch_cambia"
        and FHIRCommon.ToInterval(SR.authoredOn) starts 30 days or less before Today()
  )

define "BMI":
  Max(
    (CommonEl."Get Qualified Observations"(CommonEl."sVitalSigns")) VS
      where VS.code in CommonCx."ro_bodyMassIndex_kg_per_m2"
        and FHIRCommon.ToInterval(VS.effective) starts 180 days or less before Today()
      return VS.value as integer
  )

define "wt": //'kg'
  Max(
    (CommonEl."Get Qualified Observations"(CommonEl."sVitalSigns")) VS
      where VS.code in CommonCx."ro_weight_kg"
        and FHIRCommon.ToInterval(VS.effective) starts 180 days or less before Today()
      return VS.value as integer
  )

define "wt_lb": //'[lb_av]'
  Max(
    (CommonEl."Get Qualified Observations"(CommonEl."sVitalSigns")) VS
      where VS.code in CommonCx."ro_weight_lb"
        and FHIRCommon.ToInterval(VS.effective) starts 180 days or less before Today()
      return VS.value as integer
  )

define "ht": //'cm'
  First(
    ((CommonEl."Get Qualified Observations"(CommonEl."sVitalSigns")) VS
      where VS.code in CommonCx."ro_height_cm"
        and FHIRCommon.ToInterval(VS.effective) starts 1 year or less before Today()
      return FHIRHelpers.ToInteger(VS.value as integer)) VSQuantities
      sort descending
  )

define "ht_in": //'in'
  First(
    ((CommonEl."Get Qualified Observations"(CommonEl."sVitalSigns")) VS
      where VS.code in CommonCx."ro_height_in"
        and FHIRCommon.ToInterval(VS.effective) starts 1 year or less before Today()
      return FHIRHelpers.ToInteger(VS.value as integer)) VSQuantities
      sort descending
)
