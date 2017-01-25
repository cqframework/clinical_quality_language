
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
@class DetailsComponent
@exports  DetailsComponent as DetailsComponent
###
class DetailsComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code to indicate the nature of the payment, adjustment, funds advance, etc.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  The claim or financial resource.
  @returns {Reference}
  ###
  request: -> if @json['request'] then new Reference(@json['request'])
  
  ###*
  The claim response resource.
  @returns {Reference}
  ###
  responce: -> if @json['responce'] then new Reference(@json['responce'])
  
  ###*
  The Organization which submitted the invoice or financial transaction.
  @returns {Reference}
  ###
  submitter: -> if @json['submitter'] then new Reference(@json['submitter'])
  
  ###*
  The organization which is receiving the payment.
  @returns {Reference}
  ###
  payee: -> if @json['payee'] then new Reference(@json['payee'])
  
  ###*
  The date of the invoice or financial resource.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Amount paid for this detail.
  @returns {Money}
  ###
  amount: -> if @json['amount'] then new Money(@json['amount'])
  

###* 
Embedded class
@class NotesComponent
@exports  NotesComponent as NotesComponent
###
class NotesComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
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
This resource provides payment details supporting a bulk payment, or the errors in,  processing a ReconciliationRequest resource.
@class PaymentReconciliation
@exports PaymentReconciliation as PaymentReconciliation
###
class PaymentReconciliation extends DomainResource
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
  List of individual settlement amounts and the corresponding transaction.
  @returns {Array} an array of {@link DetailsComponent} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new DetailsComponent(item)
  
  ###*
  The form to be used for printing the content.
  @returns {Coding}
  ###
  form: -> if @json['form'] then new Coding(@json['form'])
  
  ###*
  Total payment amount.
  @returns {Money}
  ###
  total: -> if @json['total'] then new Money(@json['total'])
  
  ###*
  List of errors detected in the request.
  @returns {Array} an array of {@link Coding} objects
  ###
  error: ->
    if @json['error']
      for item in @json['error']
        new Coding(item)
  
  ###*
  Suite of notes.
  @returns {Array} an array of {@link NotesComponent} objects
  ###
  note: ->
    if @json['note']
      for item in @json['note']
        new NotesComponent(item)
  



module.exports.PaymentReconciliation = PaymentReconciliation
