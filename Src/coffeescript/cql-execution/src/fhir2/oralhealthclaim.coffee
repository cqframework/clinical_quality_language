
# Copyright (c) 2014 The MITRE Corporation
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without modification, 
# are permitted provided that the following conditions are met:
# 
#     * Redistributions of source code must retain the above copyright notice, this 
#       list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright notice, 
#       this list of conditions and the following disclaimer in the documentation 
#       and/or other materials provided with the distribution.
#     * Neither the name of HL7 nor the names of its contributors may be used to 
#       endorse or promote products derived from this software without specific 
#       prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
# IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
# INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
# POSSIBILITY OF SUCH DAMAGE.
DT = require '../cql-datatypes'
CORE = require('./core')
Element = CORE.Element
Resource = CORE.Resource
Timing = CORE.Timing
Period = CORE.Period
Parameters = CORE.Parameters
Coding = CORE.Coding
Resource = CORE.Resource
Range = CORE.Range
Quantity = CORE.Quantity
Attachment = CORE.Attachment
BackboneElement = CORE.BackboneElement
DomainResource = CORE.DomainResource
ContactPoint = CORE.ContactPoint
ElementDefinition = CORE.ElementDefinition
Extension = CORE.Extension
HumanName = CORE.HumanName
Address = CORE.Address
Ratio = CORE.Ratio
SampledData = CORE.SampledData
Reference = CORE.Reference
CodeableConcept = CORE.CodeableConcept
Identifier = CORE.Identifier
Narrative = CORE.Narrative
Element = CORE.Element

###* 
Embedded class
@class PayeeComponent
@exports  PayeeComponent as PayeeComponent
###
class PayeeComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Party to be reimbursed: Subscriber, provider, other.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  The provider who is to be reimbursed for the claim (the party to whom any benefit is assigned).
  @returns {Reference}
  ###
  provider: -> if @json['provider'] then new Reference(@json['provider'])
  
  ###*
  The organization who is to be reimbursed for the claim (the party to whom any benefit is assigned).
  @returns {Reference}
  ###
  organization: -> if @json['organization'] then new Reference(@json['organization'])
  
  ###*
  The person other than the subscriber who is to be reimbursed for the claim (the party to whom any benefit is assigned).
  @returns {Reference}
  ###
  person: -> if @json['person'] then new Reference(@json['person'])
  

###* 
Embedded class
@class DiagnosisComponent
@exports  DiagnosisComponent as DiagnosisComponent
###
class DiagnosisComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Sequence of diagnosis.
  @returns {Array} an array of {@link Number} objects
  ###
  sequence:-> @json['sequence']
  
  ###*
  The diagnosis.
  @returns {Coding}
  ###
  diagnosis: -> if @json['diagnosis'] then new Coding(@json['diagnosis'])
  

###* 
Embedded class
@class CoverageComponent
@exports  CoverageComponent as CoverageComponent
###
class CoverageComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A service line item.
  @returns {Array} an array of {@link Number} objects
  ###
  sequence:-> @json['sequence']
  
  ###*
  The instance number of the Coverage which is the focus for adjudication, that is the Coverage to which the claim is to be adjudicated against.
  @returns {Array} an array of {@link boolean} objects
  ###
  focal:-> @json['focal']
  
  ###*
  Reference to the program or plan identification, underwriter or payor.
  @returns {Reference}
  ###
  coverage: -> if @json['coverage'] then new Reference(@json['coverage'])
  
  ###*
  The contract number of a business agrement which describes the terms and conditions.
  @returns {Array} an array of {@link String} objects
  ###
  businessArrangement:-> @json['businessArrangement']
  
  ###*
  The relationship of the patient to the subscriber.
  @returns {Coding}
  ###
  relationship: -> if @json['relationship'] then new Coding(@json['relationship'])
  
  ###*
  A list of references from the Insurer to which these services pertain.
  @returns {Array} an array of {@link String} objects
  ###
  preauthref:-> @json['preauthref']
  
  ###*
  The Coverages adjudication details.
  @returns {Reference}
  ###
  claimResponse: -> if @json['claimResponse'] then new Reference(@json['claimResponse'])
  
  ###*
  The style (standard) and version of the original material which was converted into this resource.
  @returns {Coding}
  ###
  originalRuleset: -> if @json['originalRuleset'] then new Coding(@json['originalRuleset'])
  

###* 
Embedded class
@class MissingTeethComponent
@exports  MissingTeethComponent as MissingTeethComponent
###
class MissingTeethComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The code identifying which tooth is missing.
  @returns {Coding}
  ###
  tooth: -> if @json['tooth'] then new Coding(@json['tooth'])
  
  ###*
  Missing reason may be: E-extraction, O-other.
  @returns {Coding}
  ###
  reason: -> if @json['reason'] then new Coding(@json['reason'])
  
  ###*
  The date of the extraction either known from records or patient reported estimate.
  @returns {Array} an array of {@link Date} objects
  ###
  extractiondate:-> if @json['extractiondate'] then DT.DateTime.parse(@json['extractiondate'])
  

###* 
Embedded class
@class OrthodonticPlanComponent
@exports  OrthodonticPlanComponent as OrthodonticPlanComponent
###
class OrthodonticPlanComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The intended start date for service.
  @returns {Array} an array of {@link Date} objects
  ###
  start:-> if @json['start'] then DT.DateTime.parse(@json['start'])
  
  ###*
  The estimated first examination fee.
  @returns {Money}
  ###
  examFee: -> if @json['examFee'] then new Money(@json['examFee'])
  
  ###*
  The estimated diagnostic fee.
  @returns {Money}
  ###
  diagnosticFee: -> if @json['diagnosticFee'] then new Money(@json['diagnosticFee'])
  
  ###*
  The estimated initial payment.
  @returns {Money}
  ###
  initialPayment: -> if @json['initialPayment'] then new Money(@json['initialPayment'])
  
  ###*
  The estimated treatment duration in months.
  @returns {Array} an array of {@link Number} objects
  ###
  durationMonths:-> @json['durationMonths']
  
  ###*
  The anticipated number of payments.
  @returns {Array} an array of {@link Number} objects
  ###
  paymentCount:-> @json['paymentCount']
  
  ###*
  The anticipated payment amount.
  @returns {Money}
  ###
  periodicPayment: -> if @json['periodicPayment'] then new Money(@json['periodicPayment'])
  

###* 
Embedded class
@class SubDetailComponent
@exports  SubDetailComponent as SubDetailComponent
###
class SubDetailComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A service line number.
  @returns {Array} an array of {@link Number} objects
  ###
  sequence:-> @json['sequence']
  
  ###*
  The type of product or service.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  The fee for an addtional service or product or charge.
  @returns {Coding}
  ###
  service: -> if @json['service'] then new Coding(@json['service'])
  
  ###*
  The number of repetitions of a service or product.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  The fee for an addtional service or product or charge.
  @returns {Money}
  ###
  unitPrice: -> if @json['unitPrice'] then new Money(@json['unitPrice'])
  
  ###*
  A real number that represents a multiplier used in determining the overall value of services delivered and/or goods received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
  @returns {Array} an array of {@link Number} objects
  ###
  factor:-> @json['factor']
  
  ###*
  An amount that expresses the weighting (based on difficulty, cost and/or resource intensiveness) associated with the good or service delivered. The concept of Points allows for assignment of point values for services and/or goods, such that a monetary amount can be assigned to each point.
  @returns {Array} an array of {@link Number} objects
  ###
  points:-> @json['points']
  
  ###*
  The quantity times the unit price for an addtional service or product or charge. For example, the formula: unit Quantity * unit Price (Cost per Point) * factor Number  * points = net Amount. Quantity, factor and points are assumed to be 1 if not supplied.
  @returns {Money}
  ###
  net: -> if @json['net'] then new Money(@json['net'])
  
  ###*
  List of Unique Device Identifiers associated with this line item.
  @returns {Coding}
  ###
  udi: -> if @json['udi'] then new Coding(@json['udi'])
  

###* 
Embedded class
@class DetailComponent
@exports  DetailComponent as DetailComponent
###
class DetailComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A service line number.
  @returns {Array} an array of {@link Number} objects
  ###
  sequence:-> @json['sequence']
  
  ###*
  The type of product or service.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  If a grouping item then 'GROUP' otherwise it is a node therefore a code to indicate the Professional Service or Product supplied.
  @returns {Coding}
  ###
  service: -> if @json['service'] then new Coding(@json['service'])
  
  ###*
  The number of repetitions of a service or product.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  If the item is a node then this is the fee for the product or service, otherwise this is the total of the fees for the children of the group.
  @returns {Money}
  ###
  unitPrice: -> if @json['unitPrice'] then new Money(@json['unitPrice'])
  
  ###*
  A real number that represents a multiplier used in determining the overall value of services delivered and/or goods received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
  @returns {Array} an array of {@link Number} objects
  ###
  factor:-> @json['factor']
  
  ###*
  An amount that expresses the weighting (based on difficulty, cost and/or resource intensiveness) associated with the good or service delivered. The concept of Points allows for assignment of point values for services and/or goods, such that a monetary amount can be assigned to each point.
  @returns {Array} an array of {@link Number} objects
  ###
  points:-> @json['points']
  
  ###*
  The quantity times the unit price for an addtional service or product or charge. For example, the formula: unit Quantity * unit Price (Cost per Point) * factor Number  * points = net Amount. Quantity, factor and points are assumed to be 1 if not supplied.
  @returns {Money}
  ###
  net: -> if @json['net'] then new Money(@json['net'])
  
  ###*
  List of Unique Device Identifiers associated with this line item.
  @returns {Coding}
  ###
  udi: -> if @json['udi'] then new Coding(@json['udi'])
  
  ###*
  Third tier of goods and services.
  @returns {Array} an array of {@link SubDetailComponent} objects
  ###
  subDetail: ->
    if @json['subDetail']
      for item in @json['subDetail']
        new SubDetailComponent(item)
  

###* 
Embedded class
@class ProsthesisComponent
@exports  ProsthesisComponent as ProsthesisComponent
###
class ProsthesisComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Is this the initial placement of a fixed prosthesis?.
  @returns {Array} an array of {@link boolean} objects
  ###
  initial:-> @json['initial']
  
  ###*
  Date of the initial placement.
  @returns {Array} an array of {@link Date} objects
  ###
  priorDate:-> if @json['priorDate'] then DT.DateTime.parse(@json['priorDate'])
  
  ###*
  Material of the prior denture or bridge prosthesis. (Oral).
  @returns {Coding}
  ###
  priorMaterial: -> if @json['priorMaterial'] then new Coding(@json['priorMaterial'])
  

###* 
Embedded class
@class ItemsComponent
@exports  ItemsComponent as ItemsComponent
###
class ItemsComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A service line number.
  @returns {Array} an array of {@link Number} objects
  ###
  sequence:-> @json['sequence']
  
  ###*
  The type of product or service.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  The practitioner who is responsible for the services rendered to the patient.
  @returns {Reference}
  ###
  provider: -> if @json['provider'] then new Reference(@json['provider'])
  
  ###*
  If a grouping item then 'GROUP' otherwise it is a node therefore a code to indicate the Professional Service or Product supplied.
  @returns {Coding}
  ###
  service: -> if @json['service'] then new Coding(@json['service'])
  
  ###*
  The date when the enclosed suite of services were performed or completed.
  @returns {Array} an array of {@link Date} objects
  ###
  serviceDate:-> if @json['serviceDate'] then DT.DateTime.parse(@json['serviceDate'])
  
  ###*
  The number of repetitions of a service or product.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  If the item is a node then this is the fee for the product or service, otherwise this is the total of the fees for the children of the group.
  @returns {Money}
  ###
  unitPrice: -> if @json['unitPrice'] then new Money(@json['unitPrice'])
  
  ###*
  A real number that represents a multiplier used in determining the overall value of services delivered and/or goods received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
  @returns {Array} an array of {@link Number} objects
  ###
  factor:-> @json['factor']
  
  ###*
  An amount that expresses the weighting (based on difficulty, cost and/or resource intensiveness) associated with the good or service delivered. The concept of Points allows for assignment of point values for services and/or goods, such that a monetary amount can be assigned to each point.
  @returns {Array} an array of {@link Number} objects
  ###
  points:-> @json['points']
  
  ###*
  The quantity times the unit price for an addtional service or product or charge. For example, the formula: unit Quantity * unit Price (Cost per Point) * factor Number  * points = net Amount. Quantity, factor and points are assumed to be 1 if not supplied.
  @returns {Money}
  ###
  net: -> if @json['net'] then new Money(@json['net'])
  
  ###*
  List of Unique Device Identifiers associated with this line item.
  @returns {Coding}
  ###
  udi: -> if @json['udi'] then new Coding(@json['udi'])
  
  ###*
  Physical service site on the patient (limb, tooth, etc).
  @returns {Coding}
  ###
  bodySite: -> if @json['bodySite'] then new Coding(@json['bodySite'])
  
  ###*
  A region or surface of the site, eg. limb region or tooth surface(s).
  @returns {Array} an array of {@link Coding} objects
  ###
  subsite: ->
    if @json['subsite']
      for item in @json['subsite']
        new Coding(item)
  
  ###*
  Item typification or modifiers codes, eg for Oral whether the treatment is cosmetic or associated with TMJ, or an appliance was lost or stolen.
  @returns {Array} an array of {@link Coding} objects
  ###
  modifier: ->
    if @json['modifier']
      for item in @json['modifier']
        new Coding(item)
  
  ###*
  Second tier of goods and services.
  @returns {Array} an array of {@link DetailComponent} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new DetailComponent(item)
  
  ###*
  The materials and placement date of prior fixed prosthesis.
  @returns {ProsthesisComponent}
  ###
  prosthesis: -> if @json['prosthesis'] then new ProsthesisComponent(@json['prosthesis'])
  
###*
A provider issued list of services and products provided, or to be provided, to a patient which is provided to an insurer for payment recovery.
@class OralHealthClaim
@exports OralHealthClaim as OralHealthClaim
###
class OralHealthClaim extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The business identifier for the instance: invoice number, claim number, pre-determination or pre-authorization number.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The version of the specification on which this instance relies.
  @returns {Coding}
  ###
  ruleset: -> if @json['ruleset'] then new Coding(@json['ruleset'])
  
  ###*
  The version of the specification from which the original instance was created.
  @returns {Coding}
  ###
  originalRuleset: -> if @json['originalRuleset'] then new Coding(@json['originalRuleset'])
  
  ###*
  The date when the enclosed suite of services were performed or completed.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Insurer Identifier, typical BIN number (6 digit).
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
  ###*
  The provider which is responsible for the bill, claim pre-determination, pre-authorization.
  @returns {Reference}
  ###
  provider: -> if @json['provider'] then new Reference(@json['provider'])
  
  ###*
  The organization which is responsible for the bill, claim pre-determination, pre-authorization.
  @returns {Reference}
  ###
  organization: -> if @json['organization'] then new Reference(@json['organization'])
  
  ###*
  Complete (Bill or Claim), Proposed (Pre-Authorization), Exploratory (Pre-determination).
  @returns {Array} an array of {@link String} objects
  ###
  use:-> @json['use']
  
  ###*
  Immediate (STAT), best effort (NORMAL), deferred (DEFER).
  @returns {Coding}
  ###
  priority: -> if @json['priority'] then new Coding(@json['priority'])
  
  ###*
  In the case of a Pre-Determination/Pre-Authorization the provider may request that funds in the amount of the expected Benefit be reserved ('Patient' or 'Provider') to pay for the Benefits determined on the subsequent claim(s). 'None' explicitly indicates no funds reserving is requested.
  @returns {Coding}
  ###
  fundsReserve: -> if @json['fundsReserve'] then new Coding(@json['fundsReserve'])
  
  ###*
  Person who created the invoice/claim/pre-determination or pre-authorization.
  @returns {Reference}
  ###
  enterer: -> if @json['enterer'] then new Reference(@json['enterer'])
  
  ###*
  Facility where the services were provided.
  @returns {Reference}
  ###
  facility: -> if @json['facility'] then new Reference(@json['facility'])
  
  ###*
  Theparty to be reimbused for the services.
  @returns {PayeeComponent}
  ###
  payee: -> if @json['payee'] then new PayeeComponent(@json['payee'])
  
  ###*
  The referral resource which lists the date, practitioner, reason and other supporting information.
  @returns {Reference}
  ###
  referral: -> if @json['referral'] then new Reference(@json['referral'])
  
  ###*
  Ordered list of patient diagnosis for which care is sought.
  @returns {Array} an array of {@link DiagnosisComponent} objects
  ###
  diagnosis: ->
    if @json['diagnosis']
      for item in @json['diagnosis']
        new DiagnosisComponent(item)
  
  ###*
  List of patient conditions for which care is sought.
  @returns {Array} an array of {@link Coding} objects
  ###
  condition: ->
    if @json['condition']
      for item in @json['condition']
        new Coding(item)
  
  ###*
  Patient Resource.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  Financial instrument by which payment information for health care.
  @returns {Array} an array of {@link CoverageComponent} objects
  ###
  coverage: ->
    if @json['coverage']
      for item in @json['coverage']
        new CoverageComponent(item)
  
  ###*
  Factors which may influence the applicability of coverage.
  @returns {Array} an array of {@link Coding} objects
  ###
  exception: ->
    if @json['exception']
      for item in @json['exception']
        new Coding(item)
  
  ###*
  Name of school for over-aged dependants.
  @returns {Array} an array of {@link String} objects
  ###
  school:-> @json['school']
  
  ###*
  Date of an accident which these services are addessing.
  @returns {Array} an array of {@link Date} objects
  ###
  accident:-> if @json['accident'] then DT.DateTime.parse(@json['accident'])
  
  ###*
  Type of accident: work, auto, etc.
  @returns {Coding}
  ###
  accidentType: -> if @json['accidentType'] then new Coding(@json['accidentType'])
  
  ###*
  A list of intervention and exception codes which may influence the adjudication of the claim.
  @returns {Array} an array of {@link Coding} objects
  ###
  interventionException: ->
    if @json['interventionException']
      for item in @json['interventionException']
        new Coding(item)
  
  ###*
  A list of teeth which would be expected but are not found due to having been previously  extracted or for other reasons.
  @returns {Array} an array of {@link MissingTeethComponent} objects
  ###
  missingteeth: ->
    if @json['missingteeth']
      for item in @json['missingteeth']
        new MissingTeethComponent(item)
  
  ###*
  The highlevel detail sof an Orthodonic Treatment Plan.
  @returns {OrthodonticPlanComponent}
  ###
  orthoPlan: -> if @json['orthoPlan'] then new OrthodonticPlanComponent(@json['orthoPlan'])
  
  ###*
  First tier of goods and services.
  @returns {Array} an array of {@link ItemsComponent} objects
  ###
  item: ->
    if @json['item']
      for item in @json['item']
        new ItemsComponent(item)
  
  ###*
  Code to indicate that Xrays, images, emails, documents, models or attachments are being sent in support of this submission.
  @returns {Array} an array of {@link Coding} objects
  ###
  additionalMaterials: ->
    if @json['additionalMaterials']
      for item in @json['additionalMaterials']
        new Coding(item)
  



module.exports.OralHealthClaim = OralHealthClaim
