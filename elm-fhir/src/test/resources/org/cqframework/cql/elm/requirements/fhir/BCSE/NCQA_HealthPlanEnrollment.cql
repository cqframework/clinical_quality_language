library NCQA_HealthPlanEnrollment version '1.0.0'

using FHIR version '4.0.1'

include NCQA_CQLBase version '1.0.0' called CQLBase
include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_Terminology version '1.0.0' called Terminology
include NCQA_FHIRBase version '1.0.0' called FHIRBase

define function "CoverageIntervals"(Coverage List<FHIR.Coverage>, participationPeriod Interval<Date>):
  Coverage C
    return Interval[date from start of FHIRBase."Normalize Interval" ( C.period ), date from 
    end of FHIRBase."Normalize Interval" ( C.period )]
      intersect Interval[start of participationPeriod, 
      end of participationPeriod]

define function "Collapsed Coverage Intervals"(Intervals List<Interval<Date>>):
  CQLBase."Collapse Date Interval Workaround" ( Intervals )

define function "Collapsed Coverage Adjacent Intervals"(Intervals List<Interval<Date>>):
  from
    Intervals Coverage1,
    Intervals Coverage2
    where 
    end of Coverage1 within 1 day of start of Coverage2
    return Interval[start of Coverage1, 
    end of Coverage2]

define function "Collapsed Final Coverage Intervals"(collapsedI List<Interval<Date>>, adjacentI List<Interval<Date>>):
  CQLBase."Collapse Date Interval Workaround" ( "Collapsed Coverage Intervals"(collapsedI)
    union "Collapsed Coverage Adjacent Intervals"(adjacentI))

define function "All Coverage Info"(Coverage List<FHIR.Coverage>, participationPeriod Interval<Date>):
  Coverage C
    let IntervalInfo: "CoverageIntervals"(Coverage, participationPeriod),
    Collapsed: "Collapsed Coverage Intervals"(IntervalInfo),
    Adjacent: "Collapsed Coverage Adjacent Intervals"(Collapsed),
    CollapsedFinal: "Collapsed Final Coverage Intervals"(Collapsed, Adjacent)
    return Tuple {
      IntervalInfo: IntervalInfo,
      Collapsed: Collapsed,
      Adjacent: Adjacent,
      CollapsedFinal: CollapsedFinal
    }

define function "Health Plan Enrollment Criteria"(Coverage List<FHIR.Coverage>, AnchorDate Date, participationPeriod Interval<Date>, AllowedGapDays Integer):
  ( CQLBase."Date Interval Gaps Relative to Base Interval Stats" ( participationPeriod, "All Coverage Info"("Health Plan Coverage Resources"(Coverage), participationPeriod).CollapsedFinal ) ) GapsInEnrollment
    return ( GapsInEnrollment."Interval Count" <= 1
        and GapsInEnrollment."Total Days In Longest Interval" <= AllowedGapDays
        and "Health Plan Anchor Date Criteria"(Coverage, AnchorDate, participationPeriod)
    )

define function "Health Plan Coverage Resources"(Coverage List<FHIR.Coverage>):
  ( Coverage C
    where exists C.type.coding cTypeCoding
      where ( FHIRHelpers.ToCode ( cTypeCoding ) ~ Terminology."managed care policy"
          or FHIRHelpers.ToCode ( cTypeCoding ) ~ Terminology."retiree health program"
          or FHIRHelpers.ToCode ( cTypeCoding ) ~ Terminology."subsidized health program"
      ) ) HPCoverageResource
    where HPCoverageResource is not null

define function "Pharmacy Benefit Enrollment Criteria"(PharmCoverage List<FHIR.Coverage>, AnchorDate Date, participationPeriod Interval<Date>, AllowedGapDays Integer):
  ( CQLBase."Date Interval Gaps Relative to Base Interval Stats" ( participationPeriod, "All Coverage Info"("Pharmacy Benefit Coverage Resources"(PharmCoverage), participationPeriod).CollapsedFinal ) ) GapsInEnrollment
    return ( GapsInEnrollment."Interval Count" <= 1
        and GapsInEnrollment."Total Days In Longest Interval" <= AllowedGapDays
        and "Pharmacy Anchor Date Criteria"(PharmCoverage, AnchorDate, participationPeriod)
    )

define function "Pharmacy Benefit Coverage Resources"(Coverage List<FHIR.Coverage>):
  ( Coverage C
    where exists ( C.type.coding cTypeCoding
        where FHIRHelpers.ToCode ( cTypeCoding ) ~ Terminology."drug policy"
    ) ) pharmacyCoverageResource
    where pharmacyCoverageResource is not null

define function "Mental Health Benefit Enrollment Criteria"(MHCoverage List<FHIR.Coverage>, AnchorDate Date, participationPeriod Interval<Date>, AllowedGapDays Integer):
  ( CQLBase."Date Interval Gaps Relative to Base Interval Stats" ( participationPeriod, "All Coverage Info"("Mental Health Benefit Coverage Resources"(MHCoverage), participationPeriod).CollapsedFinal ) ) GapsInEnrollment
    return ( GapsInEnrollment."Interval Count" <= 1
        and GapsInEnrollment."Total Days In Longest Interval" <= AllowedGapDays
        and "Mental Health Anchor Date Criteria"(MHCoverage, AnchorDate, participationPeriod)
    )

define function "Mental Health Benefit Coverage Resources"(Coverage List<FHIR.Coverage>):
  ( Coverage C
    where exists ( C.type.coding cTypeCoding
        where FHIRHelpers.ToCode ( cTypeCoding ) ~ Terminology."mental health policy"
    ) ) mentalHealthCoverageResource
    where mentalHealthCoverageResource is not null

define function "Health Plan Anchor Date Criteria"(Coverage List<FHIR.Coverage>, AnchorDate Date, participationPeriod Interval<Date>):
  if AnchorDate during participationPeriod then exists ( ( "All Coverage Info"("Health Plan Coverage Resources"(Coverage), participationPeriod).CollapsedFinal ) FinalInterval
      where AnchorDate in FinalInterval
  ) 
    else if not ( AnchorDate during participationPeriod ) then exists ( ( "Health Plan Coverage Resources"(Coverage).period ) Cperiod
      where AnchorDate during Interval[date from start of FHIRBase."Normalize Interval" ( Cperiod ), date from 
      end of FHIRBase."Normalize Interval" ( Cperiod )]
  ) 
    else if AnchorDate is null then true 
    else false

define function "Pharmacy Anchor Date Criteria"(Coverage List<FHIR.Coverage>, AnchorDate Date, participationPeriod Interval<Date>):
  if AnchorDate during participationPeriod then exists ( ( "All Coverage Info"("Pharmacy Benefit Coverage Resources"(Coverage), participationPeriod).CollapsedFinal ) FinalInterval
      where AnchorDate in FinalInterval
  ) 
    else if not ( AnchorDate during participationPeriod ) then exists ( ( "Pharmacy Benefit Coverage Resources"(Coverage).period ) Cperiod
      where AnchorDate during Interval[date from start of FHIRBase."Normalize Interval" ( Cperiod ), date from 
      end of FHIRBase."Normalize Interval" ( Cperiod )]
  ) 
    else if AnchorDate is null then true 
    else false

define function "Mental Health Anchor Date Criteria"(Coverage List<FHIR.Coverage>, AnchorDate Date, participationPeriod Interval<Date>):
  if AnchorDate during participationPeriod then exists ( ( "All Coverage Info"("Mental Health Benefit Coverage Resources"(Coverage), participationPeriod).CollapsedFinal ) FinalInterval
      where AnchorDate in FinalInterval
  ) 
    else if not ( AnchorDate during participationPeriod ) then exists ( ( "Mental Health Benefit Coverage Resources"(Coverage).period ) Cperiod
      where AnchorDate during Interval[date from start of FHIRBase."Normalize Interval" ( Cperiod ), date from 
      end of FHIRBase."Normalize Interval" ( Cperiod )]
  ) 
    else if AnchorDate is null then true 
    else false