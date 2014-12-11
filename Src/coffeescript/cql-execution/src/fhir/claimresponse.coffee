
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
@class ItemAdjudicationComponent
@exports  ItemAdjudicationComponent as ItemAdjudicationComponent
###
class ItemAdjudicationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code indicating: Co-Pay, deductable, elegible, benefit, tax, etc.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  
  ###*
  Monitory amount associated with the code.
  @returns {Money}
  ###
  amount: -> if @json['amount'] then new Money(@json['amount'])
  
  ###*
  A non-monitary value for example a percentage. Mutually exclusive to the amount element above.
  @returns {Array} an array of {@link Number} objects
  ###
  value:-> @json['value']
  

###* 
Embedded class
@class DetailAdjudicationComponent
@exports  DetailAdjudicationComponent as DetailAdjudicationComponent
###
class DetailAdjudicationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code indicating: Co-Pay, deductable, elegible, benefit, tax, etc.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  
  ###*
  Monitory amount associated with the code.
  @returns {Money}
  ###
  amount: -> if @json['amount'] then new Money(@json['amount'])
  
  ###*
  A non-monitary value for example a percentage. Mutually exclusive to the amount element above.
  @returns {Array} an array of {@link Number} objects
  ###
  value:-> @json['value']
  

###* 
Embedded class
@class SubdetailAdjudicationComponent
@exports  SubdetailAdjudicationComponent as SubdetailAdjudicationComponent
###
class SubdetailAdjudicationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code indicating: Co-Pay, deductable, elegible, benefit, tax, etc.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  
  ###*
  Monitory amount associated with the code.
  @returns {Money}
  ###
  amount: -> if @json['amount'] then new Money(@json['amount'])
  
  ###*
  A non-monitary value for example a percentage. Mutually exclusive to the amount element above.
  @returns {Array} an array of {@link Number} objects
  ###
  value:-> @json['value']
  

###* 
Embedded class
@class ItemSubdetailComponent
@exports  ItemSubdetailComponent as ItemSubdetailComponent
###
class ItemSubdetailComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A service line number.
  @returns {Array} an array of {@link Number} objects
  ###
  sequenceLinkId:-> @json['sequenceLinkId']
  
  ###*
  The adjudications results.
  @returns {Array} an array of {@link SubdetailAdjudicationComponent} objects
  ###
  adjudication: ->
    if @json['adjudication']
      for item in @json['adjudication']
        new SubdetailAdjudicationComponent(item)
  

###* 
Embedded class
@class ItemDetailComponent
@exports  ItemDetailComponent as ItemDetailComponent
###
class ItemDetailComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A service line number.
  @returns {Array} an array of {@link Number} objects
  ###
  sequenceLinkId:-> @json['sequenceLinkId']
  
  ###*
  The adjudications results.
  @returns {Array} an array of {@link DetailAdjudicationComponent} objects
  ###
  adjudication: ->
    if @json['adjudication']
      for item in @json['adjudication']
        new DetailAdjudicationComponent(item)
  
  ###*
  The third tier service adjudications for submitted services.
  @returns {Array} an array of {@link ItemSubdetailComponent} objects
  ###
  subdetail: ->
    if @json['subdetail']
      for item in @json['subdetail']
        new ItemSubdetailComponent(item)
  

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
  sequenceLinkId:-> @json['sequenceLinkId']
  
  ###*
  A list of note references to the notes provided below.
  @returns {Array} an array of {@link Number} objects
  ###
  noteNumber:-> @json['noteNumber']
  
  ###*
  The adjudications results.
  @returns {Array} an array of {@link ItemAdjudicationComponent} objects
  ###
  adjudication: ->
    if @json['adjudication']
      for item in @json['adjudication']
        new ItemAdjudicationComponent(item)
  
  ###*
  The second tier service adjudications for submitted services.
  @returns {Array} an array of {@link ItemDetailComponent} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new ItemDetailComponent(item)
  

###* 
Embedded class
@class AddedItemAdjudicationComponent
@exports  AddedItemAdjudicationComponent as AddedItemAdjudicationComponent
###
class AddedItemAdjudicationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code indicating: Co-Pay, deductable, elegible, benefit, tax, etc.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  
  ###*
  Monitory amount associated with the code.
  @returns {Money}
  ###
  amount: -> if @json['amount'] then new Money(@json['amount'])
  
  ###*
  A non-monitary value for example a percentage. Mutually exclusive to the amount element above.
  @returns {Array} an array of {@link Number} objects
  ###
  value:-> @json['value']
  

###* 
Embedded class
@class AddedItemDetailAdjudicationComponent
@exports  AddedItemDetailAdjudicationComponent as AddedItemDetailAdjudicationComponent
###
class AddedItemDetailAdjudicationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code indicating: Co-Pay, deductable, elegible, benefit, tax, etc.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  
  ###*
  Monitory amount associated with the code.
  @returns {Money}
  ###
  amount: -> if @json['amount'] then new Money(@json['amount'])
  
  ###*
  A non-monitary value for example a percentage. Mutually exclusive to the amount element above.
  @returns {Array} an array of {@link Number} objects
  ###
  value:-> @json['value']
  

###* 
Embedded class
@class AddedItemsDetailComponent
@exports  AddedItemsDetailComponent as AddedItemsDetailComponent
###
class AddedItemsDetailComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A code to indicate the Professional Service or Product supplied.
  @returns {Coding}
  ###
  service: -> if @json['service'] then new Coding(@json['service'])
  
  ###*
  The fee charged for the professional service or product..
  @returns {Money}
  ###
  fee: -> if @json['fee'] then new Money(@json['fee'])
  
  ###*
  The adjudications results.
  @returns {Array} an array of {@link AddedItemDetailAdjudicationComponent} objects
  ###
  adjudication: ->
    if @json['adjudication']
      for item in @json['adjudication']
        new AddedItemDetailAdjudicationComponent(item)
  

###* 
Embedded class
@class AddedItemComponent
@exports  AddedItemComponent as AddedItemComponent
###
class AddedItemComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  List of input service items which this service line is intended to replace.
  @returns {Array} an array of {@link Number} objects
  ###
  sequenceLinkId:-> @json['sequenceLinkId']
  
  ###*
  A code to indicate the Professional Service or Product supplied.
  @returns {Coding}
  ###
  service: -> if @json['service'] then new Coding(@json['service'])
  
  ###*
  The fee charged for the professional service or product..
  @returns {Money}
  ###
  fee: -> if @json['fee'] then new Money(@json['fee'])
  
  ###*
  A list of note references to the notes provided below.
  @returns {Array} an array of {@link Number} objects
  ###
  noteNumberLinkId:-> @json['noteNumberLinkId']
  
  ###*
  The adjudications results.
  @returns {Array} an array of {@link AddedItemAdjudicationComponent} objects
  ###
  adjudication: ->
    if @json['adjudication']
      for item in @json['adjudication']
        new AddedItemAdjudicationComponent(item)
  
  ###*
  The second tier service adjudications for payor added services.
  @returns {Array} an array of {@link AddedItemsDetailComponent} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new AddedItemsDetailComponent(item)
  

###* 
Embedded class
@class ErrorsComponent
@exports  ErrorsComponent as ErrorsComponent
###
class ErrorsComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The sequence number of the line item submitted which contains the error. This value is ommitted when the error is elsewhere.
  @returns {Array} an array of {@link Number} objects
  ###
  sequenceLinkId:-> @json['sequenceLinkId']
  
  ###*
  The sequence number of the addition within the line item submitted which contains the error. This value is ommitted when the error is not related to an Addition.
  @returns {Array} an array of {@link Number} objects
  ###
  detailSequenceLinkId:-> @json['detailSequenceLinkId']
  
  ###*
  The sequence number of the addition within the line item submitted which contains the error. This value is ommitted when the error is not related to an Addition.
  @returns {Array} an array of {@link Number} objects
  ###
  subdetailSequenceLinkId:-> @json['subdetailSequenceLinkId']
  
  ###*
  An error code,froma specified code system, which details why the claim could not be adjudicated.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  

###* 
Embedded class
@class NotesComponent
@exports  NotesComponent as NotesComponent
###
class NotesComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  An integer associated with each note which may be referred to from each service line item.
  @returns {Array} an array of {@link Number} objects
  ###
  number:-> @json['number']
  
  ###*
  The note purpose: Print/Display.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  The note text.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  
###*
This resource provides the adjudication details from the processing of a Claim resource.
@class ClaimResponse
@exports ClaimResponse as ClaimResponse
###
class ClaimResponse extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The Response Business Identifier.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Original request resource referrence.
  @returns {Reference}
  ###
  request: -> if @json['request'] then new Reference(@json['request'])
  
  ###*
  The version of the style of resource contents. This should be mapped to the allowable profiles for this and supporting resources.
  @returns {Coding}
  ###
  ruleset: -> if @json['ruleset'] then new Coding(@json['ruleset'])
  
  ###*
  The style (standard) and version of the original material which was converted into this resource.
  @returns {Coding}
  ###
  originalRuleset: -> if @json['originalRuleset'] then new Coding(@json['originalRuleset'])
  
  ###*
  The date when the enclosed suite of services were performed or completed.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  The Insurer who produced this adjudicated response.
  @returns {Reference}
  ###
  organization: -> if @json['organization'] then new Reference(@json['organization'])
  
  ###*
  The practitioner who is responsible for the services rendered to the patient.
  @returns {Reference}
  ###
  requestProvider: -> if @json['requestProvider'] then new Reference(@json['requestProvider'])
  
  ###*
  The organization which is responsible for the services rendered to the patient.
  @returns {Reference}
  ###
  requestOrganization: -> if @json['requestOrganization'] then new Reference(@json['requestOrganization'])
  
  ###*
  Transaction status: error, complete.
  @returns {Array} an array of {@link String} objects
  ###
  outcome:-> @json['outcome']
  
  ###*
  A description of the status of the adjudication.
  @returns {Array} an array of {@link String} objects
  ###
  disposition:-> @json['disposition']
  
  ###*
  Party to be reimbursed: Subscriber, provider, other.
  @returns {Coding}
  ###
  payeeType: -> if @json['payeeType'] then new Coding(@json['payeeType'])
  
  ###*
  The first tier service adjudications for submitted services.
  @returns {Array} an array of {@link ItemsComponent} objects
  ###
  item: ->
    if @json['item']
      for item in @json['item']
        new ItemsComponent(item)
  
  ###*
  The first tier service adjudications for payor added services.
  @returns {Array} an array of {@link AddedItemComponent} objects
  ###
  additem: ->
    if @json['additem']
      for item in @json['additem']
        new AddedItemComponent(item)
  
  ###*
  Mutually exclusive with Services Provided (Item).
  @returns {Array} an array of {@link ErrorsComponent} objects
  ###
  error: ->
    if @json['error']
      for item in @json['error']
        new ErrorsComponent(item)
  
  ###*
  The total cost of the services reported.
  @returns {Money}
  ###
  totalCost: -> if @json['totalCost'] then new Money(@json['totalCost'])
  
  ###*
  The amount of deductable applied which was not allocated to any particular service line.
  @returns {Money}
  ###
  unallocDeductable: -> if @json['unallocDeductable'] then new Money(@json['unallocDeductable'])
  
  ###*
  Total amount of benefit payable (Equal to sum of the Benefit amounts from all detail lines and additions less the Unallocated Deductable).
  @returns {Money}
  ###
  totalBenefit: -> if @json['totalBenefit'] then new Money(@json['totalBenefit'])
  
  ###*
  Adjustment to the payment of this transaction which is not related to adjudication of this transaction.
  @returns {Money}
  ###
  paymentAdjustment: -> if @json['paymentAdjustment'] then new Money(@json['paymentAdjustment'])
  
  ###*
  Reason for the payment adjustment.
  @returns {Coding}
  ###
  paymentAdjustmentReason: -> if @json['paymentAdjustmentReason'] then new Coding(@json['paymentAdjustmentReason'])
  
  ###*
  Estimated payment data.
  @returns {Array} an array of {@link Date} objects
  ###
  paymentDate:-> if @json['paymentDate'] then DT.DateTime.parse(@json['paymentDate'])
  
  ###*
  Payable less any payment adjustment.
  @returns {Money}
  ###
  paymentAmount: -> if @json['paymentAmount'] then new Money(@json['paymentAmount'])
  
  ###*
  Payment identifer.
  @returns {Identifier}
  ###
  paymentRef: -> if @json['paymentRef'] then new Identifier(@json['paymentRef'])
  
  ###*
  Status of funds reservation (For provider, for Patient, None).
  @returns {Coding}
  ###
  reserved: -> if @json['reserved'] then new Coding(@json['reserved'])
  
  ###*
  The form to be used for printing the content.
  @returns {Coding}
  ###
  form: -> if @json['form'] then new Coding(@json['form'])
  
  ###*
  Note text.
  @returns {Array} an array of {@link NotesComponent} objects
  ###
  note: ->
    if @json['note']
      for item in @json['note']
        new NotesComponent(item)
  



module.exports.ClaimResponse = ClaimResponse
