library NCQA_Claims version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_FHIRBase version '1.0.0' called FHIRBase
include NCQA_Terminology version '1.0.0' called Terminology

valueset "Inpatient Stay": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1395'
valueset "Nonacute Inpatient Stay": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1398'

define function "Professional or Institutional Claims"(claim List<FHIR.Claim>):
  claim MedicalClaim
    where ( FHIRHelpers.ToConcept ( MedicalClaim.type ).codes contains Terminology."Professional"
        or FHIRHelpers.ToConcept ( MedicalClaim.type ).codes contains Terminology."Institutional"
    )

define function "Pharmacy Claims"(claim List<FHIR.Claim>):
  claim PharmacyClaim
    where FHIRHelpers.ToConcept ( PharmacyClaim.type ).codes contains Terminology."Pharmacy"

define function "Professional or Institutional Claims Response"(claimResponse List<FHIR.ClaimResponse>):
  claimResponse MedicalResponse
    where ( FHIRHelpers.ToConcept ( MedicalResponse.type ).codes contains Terminology."Professional"
        or FHIRHelpers.ToConcept ( MedicalResponse.type ).codes contains Terminology."Institutional"
    )

define function "Pharmacy Claims Response"(claimResponse List<FHIR.ClaimResponse>):
  claimResponse PharmacyResponse
    where ( FHIRHelpers.ToConcept ( PharmacyResponse.type ).codes contains Terminology."Pharmacy" )

define function "Medical Claims With Procedure and POS"(claim List<FHIR.Claim>, posCodes List<System.Code>, ProductOrServiceValueSet List<System.Code>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim),
    "ProceduresAsStrings": ProductOrServiceValueSet p
      return p.code,
    "POSAsString": posCodes pos
      return pos.code
  } ) ClaimWithPosCode
    return ( ClaimWithPosCode."MedicalClaim" ClaimofInterest
      return ( Tuple {
        "Claim": ClaimofInterest,
        "LineItems": if ClaimofInterest is null then null 
          else ClaimofInterest.item ItemOnLine
          where ( ( exists ( ItemOnLine.productOrService.coding ) LineCode
                where LineCode.code.value in ClaimWithPosCode."ProceduresAsStrings"
                  or exists ( ItemOnLine.revenue.coding ) revCode
                    where revCode.code.value in ClaimWithPosCode."ProceduresAsStrings"
            )
              and ( exists ( ItemOnLine.location.coding ) PosCode
                  where PosCode.code.value in ClaimWithPosCode."POSAsString"
              )
          )
      } ) LineItemDefinition
        return ( if exists LineItemDefinition."LineItems" then ( Tuple {
              Claim: LineItemDefinition."Claim",
              ServicePeriod: LineItemDefinition.LineItems.serviced NormalDate
                return FHIRBase."Normalize Interval" ( NormalDate )
            }
          ) 
            else null
        ) ) FinalList
      where FinalList is not null

define function "Medical Claims With Procedure in Header or on Line Item"(claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim),
    "ProceduresAsStrings": ProductOrServiceValueSet p
      return p.code
  } ) ClaimWithProcedure
    return ( ClaimWithProcedure."MedicalClaim" ClaimofInterest
      return ( "Claim With Procedure"(ClaimofInterest, ClaimWithProcedure."ProceduresAsStrings")) LineItemDefinition
        return ( if exists LineItemDefinition."Items" then ( Tuple {
              Claim: LineItemDefinition."Claim",
              ServicePeriod: LineItemDefinition."Items".serviced NormalDate
                return FHIRBase."Normalize Interval" ( NormalDate )
            }
          ) 
            else null
        ) ) FinalList
      where FinalList is not null

define function "Claim With Procedure"(cList FHIR.Claim, stringList List<System.String>):
  Tuple {
    "Claim": cList,
    "Items": if cList is null then null 
      else cList.item ItemOnLine
      where ( ( exists ( ItemOnLine.productOrService.coding ) LineCode
            where LineCode.code.value in stringList
              or exists ( ItemOnLine.revenue.coding ) RevCode
                where RevCode.code.value in stringList
        )
          or exists ( ( ( cList.procedure.procedure ).coding ) HeaderCode
              where HeaderCode.code.value in stringList
          )
      )
  }

define function "Medical Claims With Diagnosis"(claim List<FHIR.Claim>, DiagnosisValueSet List<System.Code>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim),
    "DiagnosesAsStrings": DiagnosisValueSet d
      return d.code
  } ) ClaimWithDiagnosis
    return ( Tuple {
      "DiagnosisItems": if ClaimWithDiagnosis."MedicalClaim" is null then null 
        else ClaimWithDiagnosis."MedicalClaim" DiagnosisLine
        where exists ( ( ( DiagnosisLine.diagnosis.diagnosis ).coding ) HeaderCode
            where HeaderCode.code.value in ClaimWithDiagnosis."DiagnosesAsStrings"
        )
    } ) HeaderDefinition
      return ( if exists HeaderDefinition."DiagnosisItems" then ( Tuple {
          Claim: HeaderDefinition."DiagnosisItems",
          ServicePeriod: HeaderDefinition.DiagnosisItems.item NormalDate
            return FHIRBase."Normalize Interval" ( NormalDate.serviced )
        }
      ) 
        else null ) FinalList
        where FinalList is not null

define function "Pharmacy Claim With Medication"(claim List<FHIR.Claim>, MedicationCodes List<System.Code>):
  ( Tuple {
    "PharmacyClaim": "Pharmacy Claims"(claim),
    "MedicationsAsStrings": MedicationCodes p
      return p.code
  } ) ClaimWithMedication
    return all ( ClaimWithMedication."PharmacyClaim" Pharmacy
      return all ( Tuple {
        "Claim": Pharmacy,
        "LineItems": Pharmacy.item ItemOnLine
          where exists ( ( FHIRHelpers.ToConcept ( ItemOnLine.productOrService ).codes ) LineCode
              where LineCode.code in ClaimWithMedication."MedicationsAsStrings"
          )
      } ) LineItemDefinition
        return all LineItemDefinition."Claim" ClaimLines
          return all ( Tuple {
            CoveredDays: ClaimLines.item i
              where exists ( ( FHIRHelpers.ToConcept ( i.productOrService ).codes ) LineCode
                  where LineCode.code in ClaimWithMedication."MedicationsAsStrings"
              )
              return all Tuple {
                DaysSupplyInterval: ( if i.quantity is not null then if i.serviced is Period then Interval[start of FHIRBase."Normalize Interval" ( i.serviced ), start of FHIRBase."Normalize Interval" ( i.serviced ) + System.Quantity { value: i.quantity.value, unit: 'day' } - 1 day]
                    else Interval[i.serviced, i.serviced + System.Quantity { value: i.quantity.value, unit: 'day' } - 1 day]
                    else null
                )
              }
          } ) ItemCalculation
            return all if exists LineItemDefinition."LineItems" then ( Tuple {
                Claim: LineItemDefinition."Claim",
                LineItem: LineItemDefinition."LineItems",
                ServicePeriod: LineItemDefinition.LineItems.serviced NormalDate
                  return FHIRBase."Normalize Interval" ( NormalDate ),
                CoveredDays: ItemCalculation.CoveredDays d
                  return all d.DaysSupplyInterval
              }
            ) 
              else null ) FinalList
      where FinalList is not null

define function "Medical Claims With Diagnosis and Procedure"(claim List<FHIR.Claim>, DiagnosisValueSet List<System.Code>, ProductOrServiceValueSet List<System.Code>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim),
    "DiagnosesAsStrings": DiagnosisValueSet d
      return d.code,
    "ProceduresAsStrings": ProductOrServiceValueSet p
      return p.code
  } ) ClaimWithDiagnosis
    return ( Tuple {
      "DiagnosisItems": if ClaimWithDiagnosis."MedicalClaim" is null then null 
        else ClaimWithDiagnosis."MedicalClaim" DiagnosisLine
        where exists ( ( ( DiagnosisLine.diagnosis.diagnosis ).coding ) HeaderCode
            where HeaderCode.code.value in ClaimWithDiagnosis."DiagnosesAsStrings"
        )
    } ) ClaimWithProcedure
      return ( ClaimWithProcedure."DiagnosisItems" ClaimofInterest
        return ( Tuple {
          "ProcedureItems": if ClaimofInterest is null then null 
            else ClaimofInterest ItemOnLine
            where ( exists ( ( ( ItemOnLine.procedure.procedure ).coding ) ProcedureHeaderCode
                  where ProcedureHeaderCode.code.value in ClaimWithDiagnosis."ProceduresAsStrings"
              )
                or exists ( ( ItemOnLine.item.productOrService.coding ) LineCode
                    where LineCode.code.value in ClaimWithDiagnosis."ProceduresAsStrings"
                )
            )
        } ) HeaderDefinition
          return ( if HeaderDefinition."ProcedureItems" is not null then ( Tuple {
                Claim: HeaderDefinition."ProcedureItems",
                ServicePeriod: HeaderDefinition.ProcedureItems.item NormalDate
                  return FHIRBase."Normalize Interval" ( NormalDate.serviced )
              }
            ) 
              else null
          ) ) FinalList
        where FinalList is not null

define function "Medical Claims With Principal Diagnosis and Procedure"(claim List<FHIR.Claim>, DiagnosisValueSet List<System.Code>, ProductOrServiceValueSet List<System.Code>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim),
    "DiagnosesAsStrings": DiagnosisValueSet d
      return d.code,
    "ProceduresAsStrings": ProductOrServiceValueSet p
      return p.code
  } ) ClaimWithProcedure
    return ( ClaimWithProcedure."MedicalClaim" ClaimofInterest
      return ( Tuple {
        Claim: ClaimofInterest,
        "ProcedureItems": if ClaimofInterest is null then null 
          else ClaimofInterest ItemOnLine
          where exists ( ( ItemOnLine.item.productOrService.coding ) LineCode
              where LineCode.code.value in ClaimWithProcedure."ProceduresAsStrings"
          )
            or exists ( ( ( ItemOnLine.procedure.procedure ).coding ) HeaderCode
                where HeaderCode.code.value in ClaimWithProcedure."ProceduresAsStrings"
            )
      } ) DiagnosisCheck
        return DiagnosisCheck."ProcedureItems" ClaimforDiagnosis
          return ( Tuple {
            Procedure: ClaimforDiagnosis.item,
            "LineItems": if ClaimforDiagnosis is null then null 
              else ClaimforDiagnosis RightClaim
              where exists ( RightClaim.diagnosis RightDiagnosis
                  where FHIRHelpers.ToInteger ( RightDiagnosis.sequence ) = 1
                    and exists ( ( ( RightDiagnosis.diagnosis ).coding ) DiagnosisCode
                        where DiagnosisCode.code.value in ClaimWithProcedure."DiagnosesAsStrings"
                    )
              )
          } ) LineItemDefinition
            return ( if LineItemDefinition is not null
                and LineItemDefinition."LineItems" is not null then ( Tuple {
                  Claim: LineItemDefinition."LineItems",
                  ServicePeriod: LineItemDefinition."LineItems".item NormalDate
                    return all FHIRBase."Normalize Interval" ( NormalDate.serviced )
                }
              ) 
                else null
            ) ) FinalList
      where FinalList is not null

define function "Medical Claims With Principal Diagnosis"(claim List<FHIR.Claim>, DiagnosisValueSet List<System.Code>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim),
    "DiagnosesAsStrings": DiagnosisValueSet d
      return d.code
  } ) ClaimWithDiagnosis
    return ( Tuple {
      "LineItems": if ClaimWithDiagnosis."MedicalClaim" is null then null 
        else ClaimWithDiagnosis."MedicalClaim" RightClaim
        where exists ( RightClaim.diagnosis RightDiagnosis
            where FHIRHelpers.ToInteger ( RightDiagnosis.sequence ) = 1
              and exists ( ( ( RightDiagnosis.diagnosis ).coding ) DiagnosisCode
                  where DiagnosisCode.code.value in ClaimWithDiagnosis."DiagnosesAsStrings"
              )
        )
    } ) LineItemDefinition
      return ( LineItemDefinition."LineItems" ClaimWithDiagnosis
        return if ClaimWithDiagnosis is not null then ( Tuple {
            Claim: LineItemDefinition."LineItems",
            ServicePeriod: LineItemDefinition.LineItems.item NormalDate
              return FHIRBase."Normalize Interval" ( NormalDate.serviced )
          }
        ) 
          else null ) FinalList
        where FinalList is not null

define function "Get Paid Claims for Provided Service and Condition"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>, DiagnosisValueSet List<System.Code>):
  ( ( "Get All Professional and Institutional Claims and Claim Responses"(claimResponse, claim)) MedicalClaimAndResponse
    return if ( MedicalClaimAndResponse."MedicalClaimResponse" is null
        and MedicalClaimAndResponse."MedicalClaim" is null
    ) then null 
      else "Get Corresponding Claim for Services and Conditions"(MedicalClaimAndResponse."MedicalClaimResponse", MedicalClaimAndResponse."MedicalClaim", ProductOrServiceValueSet, DiagnosisValueSet)) FinalList
    where FinalList is not null

define function "Get Paid Claims for Provided Services or Conditions"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>, DiagnosisValueSet List<System.Code>):
  ( "Get All Professional and Institutional Claims and Claim Responses"(claimResponse, claim)) MedicalClaimAndResponse
    return if ( MedicalClaimAndResponse."MedicalClaimResponse" is null
        and MedicalClaimAndResponse."MedicalClaim" is null
    ) then null 
      else "Get Corresponding Claim for Services or Conditions"(MedicalClaimAndResponse."MedicalClaimResponse", MedicalClaimAndResponse."MedicalClaim", ProductOrServiceValueSet, DiagnosisValueSet)

define function "Get Paid Claims for Provided Services Only"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>):
  ( "Get All Professional and Institutional Claims and Claim Responses"(claimResponse, claim)) MedicalClaimAndResponse
    return if ( MedicalClaimAndResponse."MedicalClaimResponse" is null
        and MedicalClaimAndResponse."MedicalClaim" is null
    ) then null 
      else "Get Corresponding Claim for Services Only"(MedicalClaimAndResponse."MedicalClaimResponse", MedicalClaimAndResponse."MedicalClaim", ProductOrServiceValueSet)

define function "Get Paid Claims for Pharmacy Services"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>):
  ( "Get All Pharmacy Claims and Claim Responses"(claimResponse, claim)) PharmacyClaimAndResponse
    return if ( PharmacyClaimAndResponse."PharmacyClaimResponse" is null
        and PharmacyClaimAndResponse."PharmacyClaim" is null
    ) then null 
      else "Get Corresponding Claim for Pharmacy Services"(PharmacyClaimAndResponse."PharmacyClaimResponse", PharmacyClaimAndResponse."PharmacyClaim", ProductOrServiceValueSet)

define function "Get All Professional and Institutional Claims and Claim Responses"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>):
  ( Tuple {
      "MedicalClaimResponse": "Professional or Institutional Claims Response"(claimResponse),
      "MedicalClaim": "Professional or Institutional Claims"(claim)
    }
  )

define function "Get All Pharmacy Claims and Claim Responses"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>):
  ( Tuple {
      "PharmacyClaimResponse": "Pharmacy Claims Response"(claimResponse),
      "PharmacyClaim": "Pharmacy Claims"(claim)
    }
  )

define function "Get Corresponding Claim for Services and Conditions"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>, DiagnosisValueSet List<System.Code>):
  ( Tuple {
    "PaidMedicalClaimResponse": "Get All Paid Claim Reponses"(claimResponse),
    "MedicalClaim": "Get All Claims With Procedure and Diagnosis"(claim, ProductOrServiceValueSet, DiagnosisValueSet)
  } ) ClaimAndResponse
    return ( Tuple {
      AggregateClaim: ClaimAndResponse.MedicalClaim medClaim
        let lItem: ( medClaim.LineItems medClaimLineItem
            where exists ( ClaimAndResponse."PaidMedicalClaimResponse" pClaim
                with pClaim.LineItems pClaimLineItem
                  such that medClaim.ClaimofInterest.id = FHIRBase.GetId ( pClaim.Response.request.reference )
                    and medClaimLineItem.sequence = pClaimLineItem.itemSequence
            )
        )
        return Tuple {
          PaidClaim: medClaim,
          ClaimItem: lItem
        }
    } ) ClaimWithPaidResponse
      return ( ( if exists ClaimWithPaidResponse.AggregateClaim."ClaimItem" then ( Tuple {
            originalClaim: ClaimWithPaidResponse.AggregateClaim.PaidClaim,
            ServicePeriod: ClaimWithPaidResponse.AggregateClaim.ClaimItem PaidItem
              return FHIRBase."Normalize Interval" ( PaidItem.serviced )
          }
        ) 
          else null
      ) ) FinalList
        where FinalList is not null

define function "Get Corresponding Claim for Services or Conditions"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>, DiagnosisValueSet List<System.Code>):
  ( Tuple {
    "PaidMedicalClaimResponse": "Get All Paid Claim Reponses"(claimResponse),
    "MedicalClaim": "Get All Claims With Procedure or Diagnosis"(claim, ProductOrServiceValueSet, DiagnosisValueSet)
  } ) ClaimAndResponse
    return ( Tuple {
      AggregateClaim: ClaimAndResponse.MedicalClaim medClaim
        let lItem: ( medClaim.LineItems medClaimLineItem
            where exists ( ClaimAndResponse."PaidMedicalClaimResponse" pClaim
                with pClaim.LineItems pClaimLineItem
                  such that medClaim.ClaimofInterest.id = FHIRBase.GetId ( pClaim.Response.request.reference )
                    and medClaimLineItem.sequence = pClaimLineItem.itemSequence
            )
        )
        return Tuple {
          PaidClaim: medClaim,
          ClaimItem: lItem
        }
    } ) ClaimWithPaidResponse
      return ( ( if exists ClaimWithPaidResponse.AggregateClaim."ClaimItem" then ( Tuple {
            originalClaim: ClaimWithPaidResponse.AggregateClaim.PaidClaim,
            ServicePeriod: ClaimWithPaidResponse.AggregateClaim.ClaimItem PaidItem
              return FHIRBase."Normalize Interval" ( PaidItem.serviced )
          }
        ) 
          else null
      ) ) FinalList
        where FinalList is not null

define function "Get Corresponding Claim for Services Only"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>):
  ( Tuple {
    "PaidMedicalClaimResponse": "Get All Paid Claim Reponses"(claimResponse),
    "MedicalClaim": "Get All Claims With Procedure Only"(claim, ProductOrServiceValueSet)
  } ) ClaimAndResponse
    return ( Tuple {
      AggregateClaim: ClaimAndResponse.MedicalClaim medClaim
        let lItem: ( medClaim.LineItems medClaimLineItem
            where exists ( ClaimAndResponse."PaidMedicalClaimResponse" pClaim
                with pClaim.LineItems pClaimLineItem
                  such that medClaim.ClaimofInterest.id = FHIRBase.GetId ( pClaim.Response.request.reference )
                    and medClaimLineItem.sequence = pClaimLineItem.itemSequence
            )
        )
        return Tuple {
          PaidClaim: medClaim,
          ClaimItem: lItem
        }
    } ) ClaimWithPaidResponse
      return ( ( if exists ClaimWithPaidResponse.AggregateClaim."ClaimItem" then ( Tuple {
            originalClaim: ClaimWithPaidResponse.AggregateClaim.PaidClaim,
            ServicePeriod: ClaimWithPaidResponse.AggregateClaim.ClaimItem PaidItem
              return FHIRBase."Normalize Interval" ( PaidItem.serviced )
          }
        ) 
          else null
      ) ) FinalList
        where FinalList is not null

define function "Get Corresponding Claim for Pharmacy Services"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>):
  ( Tuple {
    "PaidPharmacyClaimResponse": "Get All Paid Claim Reponses"(claimResponse),
    "MedicalClaim": "Pharmacy Claim With Medication"(claim, ProductOrServiceValueSet)
  } ) ClaimAndResponse
    return ( Tuple {
      AggregateClaim: ClaimAndResponse.MedicalClaim medClaim
        let lItem: ( medClaim.LineItem medClaimLineItem
            where exists ( ClaimAndResponse."PaidPharmacyClaimResponse" pClaim
                with pClaim.LineItems pClaimLineItem
                  such that medClaim.Claim.id = FHIRBase.GetId ( pClaim.Response.request.reference )
                    and medClaimLineItem.sequence = pClaimLineItem.itemSequence
            )
        )
        return Tuple {
          PaidClaim: medClaim,
          ClaimItem: lItem
        }
    } ) ClaimWithPaidResponse
      return ( ( if exists ClaimWithPaidResponse.AggregateClaim."ClaimItem" then ( Tuple {
            originalClaim: ClaimWithPaidResponse.AggregateClaim.PaidClaim,
            ServicePeriod: ClaimWithPaidResponse.AggregateClaim.ClaimItem PaidItem
              return FHIRBase."Normalize Interval" ( PaidItem.serviced ),
            CoveredDays: ClaimWithPaidResponse.AggregateClaim.ClaimItem i
              let X: ( if i.quantity is not null then Interval[ToDate(start of FHIRBase."Normalize Interval"(i.serviced)), ToDate(start of FHIRBase."Normalize Interval"(i.serviced)+ System.Quantity { value: i.quantity.value, unit: 'day' } - 1 day)]
                  else null
              )
              return X
          }
        ) 
          else null
      ) ) FinalList
        where FinalList is not null

define function "Get All Paid Claim Reponses"(claimResponse List<FHIR.ClaimResponse>):
  ( Tuple {
    "PaidResponse": claimResponse ResponseItem
      where ResponseItem.outcome in { 'complete', 'partial' }
  } ) ClaimResponse
    return ClaimResponse."PaidResponse" ClmResp
      return ( Tuple {
          "Response": ClmResp,
          "ResponseID": FHIRBase."GetId" ( ClmResp.request.reference ),
          "LineItems": ClmResp.item ResponseItem
            where ( exists ( ResponseItem.adjudication.category.coding CategoryItem
                  where CategoryItem.code.value = 'benefit'
              )
                and exists ( ResponseItem.adjudication.amount DollarAmount
                    where DollarAmount.value > 0
                )
            )
        }
      )

define function "Get All Claims With Procedure and Diagnosis"(claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>, DiagnosisValueSet List<System.Code>):
  ( claim AllClaims
    where exists ( AllClaims.item.productOrService.coding ProductOrServiceCode
        where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
    )
      and exists ( ( AllClaims.diagnosis.diagnosis.coding ) DiagnosisCode
          where FHIRHelpers.ToCode ( DiagnosisCode ) in DiagnosisValueSet
      ) ) ProcedureClaims
    return ( Tuple {
        "ClaimofInterest": ProcedureClaims,
        "ClaimID": ProcedureClaims.id,
        "LineItems": ProcedureClaims.item ResponseItem
          where exists ( ResponseItem.productOrService.coding ProductOrServiceCode
              where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
          )
      }
    )

define function "Get All Claims With Procedure or Diagnosis"(claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>, DiagnosisValueSet List<System.Code>):
  ( claim AllClaims
    where exists ( AllClaims.item.productOrService.coding ProductOrServiceCode
        where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
    )
      or exists ( ( AllClaims.diagnosis.diagnosis.coding ) DiagnosisCode
          where FHIRHelpers.ToCode ( DiagnosisCode ) in DiagnosisValueSet
      ) ) ProcedureClaims
    return ( if exists ( ProcedureClaims.item ResponseItem
          where exists ( ResponseItem.productOrService.coding ProductOrServiceCode
              where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
          )
      ) then ( Tuple {
          "ClaimofInterest": ProcedureClaims,
          "ClaimID": ProcedureClaims.id,
          "LineItems": ProcedureClaims.item ResponseItem
            where exists ( ResponseItem.productOrService.coding ProductOrServiceCode
                where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
            )
        }
      ) 
        else ( Tuple {
          "ClaimofInterest": ProcedureClaims,
          "ClaimID": ProcedureClaims.id,
          "LineItems": ProcedureClaims.item ResponseItem
            where ResponseItem.sequence.value = 1
        }
      )
    )

define function "Get All Claims With Procedure Only"(claim List<FHIR.Claim>, ProductOrServiceValueSet List<System.Code>):
  ( claim AllClaims
      where exists ( AllClaims.item.productOrService.coding ProductOrServiceCode
          where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
      )
      return ( Tuple {
          "ClaimofInterest": AllClaims,
          "ClaimID": AllClaims.id,
          "LineItems": AllClaims.item ResponseItem
            where exists ( ResponseItem.productOrService.coding ProductOrServiceCode
                where FHIRHelpers.ToCode ( ProductOrServiceCode ) in ProductOrServiceValueSet
            )
        }
      )
  )

define function "Get Claim With Corresponding Claim Response"(claimResponse List<FHIR.ClaimResponse>, claim List<FHIR.Claim>):
  claim Claim
    let claimResponse: ( claimResponse CR
        where Claim.id = FHIRBase."GetId" ( CR.request.reference )
    ),
    OClaim: ( claim C
        where Claim.id = FHIRBase."GetId" ( singleton from ( claimResponse.request.reference ) )
    )
    return Tuple {
      ClaimResponse: claimResponse,
      OriginalClaim: OClaim
    }

define function "Medical Claims With Nonacute or Acute Inpatient Discharge"(claim List<FHIR.Claim>):
  ( Tuple {
    "MedicalClaim": "Professional or Institutional Claims"(claim)
  } ) ClaimWithInpatientStay
    return ( Tuple {
      "InpatientStayLineItems": if ClaimWithInpatientStay."MedicalClaim" is null then null 
        else ( ClaimWithInpatientStay."MedicalClaim" c
          where exists ( ( c.item ) i
              where exists ( i.revenue.coding ) rev
                where rev in "Inpatient Stay"
          )
      ),
      "NonacuteInpatientLineItems": if ClaimWithInpatientStay."MedicalClaim" is null then null 
        else ( ClaimWithInpatientStay."MedicalClaim" c
          where exists ( c.item i
              where exists ( i.revenue.coding ) rev
                where rev in "Nonacute Inpatient Stay"
          )
            or exists ( ( c.subType.coding ) tob
                where tob in "Nonacute Inpatient Stay"
            )
      )
    } ) LineItemDefinition
      return Tuple {
        "InpatientDischarge": LineItemDefinition."InpatientStayLineItems",
        "NonacuteInpatientDischarge": LineItemDefinition."NonacuteInpatientLineItems" nonAcuteInpatientStay
          with LineItemDefinition."InpatientStayLineItems" inpatientStay
            such that nonAcuteInpatientStay.id = inpatientStay.id,
        "AcuteInpatientDischarge": LineItemDefinition."InpatientStayLineItems" inpatientStay
          without LineItemDefinition."NonacuteInpatientLineItems" nonAcuteInpatientStay
            such that inpatientStay.id = nonAcuteInpatientStay.id
      }

define function "Get Prescriber NPI from Claims"(claim List<FHIR.Claim>):
  ( Tuple {
    CareTeams: claim C
      let singleClaim: C,
      claimcareTeam: ( C.careTeam ct
          where FHIRHelpers.ToInteger ( ct.sequence ) = 1
      )
      return Tuple {
        SingleCareTeam: singleClaim,
        CareTeamsProvider: claimcareTeam.provider,
        CareTeamsProviderID: claimcareTeam.provider p
          return all ( p.reference r
              return all FHIRBase."GetId" ( r )
          )
      }
  } ) ClaimProperties
    return ( Tuple {
      Practitioners: [Practitioner] p
        where p.id in ClaimProperties.CareTeams.CareTeamsProviderID
    } ) PractitionerMatch
      return ( Tuple {
        IdentifierTuple: PractitionerMatch.Practitioners P
          where P is not null
          return all ( Tuple {
              AllIdentifiers: ( P.identifier l
                  where l.system.value = 'http://hl7.org/fhir/sid/us-npi'
                    and l.type ~ ToConcept(Terminology."Provider number")
                    and l.value.value is not null
                  return l.value
              ),
              NullIdentifiers: ( P.identifier l
                  where l.system.value = 'http://hl7.org/fhir/sid/us-npi'
                    and l.type ~ ToConcept(Terminology."Provider number")
                    and l.value.value is null
                  return all l
              )
            }
          )
      } ) GetIdentifiers
        return ( Tuple {
            IdentifierCount: if exists ClaimProperties.CareTeams.SingleCareTeam then ( Count(ClaimProperties.CareTeams.SingleCareTeam X
                  where X.careTeam is null
              )+ Count((flatten(GetIdentifiers.IdentifierTuple X
                    return all X.AllIdentifiers
                ))X
                  return X
              )+ Count(flatten(GetIdentifiers.IdentifierTuple X
                    return all X.NullIdentifiers
                )
              )
            ) 
              else 0
          }
        )

define function "Get Pharmacy NPI from Claims"(claim List<FHIR.Claim>):
  ( Tuple {
    ItemsLocationReferences: claim C
      let singleClaim: C,
      claimItem: ( C.item i
          where FHIRHelpers.ToInteger ( i.sequence ) = 1
      )
      return Tuple {
        SingleItem: singleClaim.item,
        ItemLocation: claimItem.location l
          return all ( l as FHIR.Reference ),
        ItemLocationID: claimItem.location l
          return all ( ( ( l as FHIR.Reference ).reference ) r
              return all FHIRBase."GetId" ( r )
          )
      }
  } ) ClaimProperties
    return ( Tuple {
      CorrespondingLocations: [Location] l
        where l.id in ClaimProperties.ItemsLocationReferences.ItemLocationID
    } ) GetLocation
      return ( Tuple {
        IdentifierTuple: GetLocation.CorrespondingLocations C
          where C is not null
          return all ( Tuple {
              AllIdentifiers: ( C.identifier l
                  where l.system.value = 'http://hl7.org/fhir/sid/us-npi'
                    and l.type ~ ToConcept(Terminology."Provider number")
                    and l.value.value is not null
                  return l.value
              ),
              NullIdentifiers: ( C.identifier l
                  where l.system.value = 'http://hl7.org/fhir/sid/us-npi'
                    and l.type ~ ToConcept(Terminology."Provider number")
                    and l.value.value is null
                  return all l
              )
            }
          )
      } ) GetIdentifiers
        return ( Tuple {
            IdentifierCount: if exists ( ClaimProperties.ItemsLocationReferences.SingleItem ) then ( Count(ClaimProperties.ItemsLocationReferences.ItemLocation X
                  where X.reference.value is null
              )+ Count((flatten(GetIdentifiers.IdentifierTuple X
                    return all X.AllIdentifiers
                ))X
                  return X
              )+ Count(flatten(GetIdentifiers.IdentifierTuple X
                    return all X.NullIdentifiers
                )
              )
            ) 
              else 0
          }
        )