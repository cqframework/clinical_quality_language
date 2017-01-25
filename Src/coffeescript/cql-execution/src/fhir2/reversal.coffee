
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
@class ReversalCoverageComponent
@exports  ReversalCoverageComponent as ReversalCoverageComponent
###
class ReversalCoverageComponent extends BackboneElement
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
  The contract number of a business agreement which describes the terms and conditions.
  @returns {Array} an array of {@link String} objects
  ###
  businessArrangement:-> @json['businessArrangement']
  
  ###*
  The relationship of the patient to the subscriber.
  @returns {Coding}
  ###
  relationship: -> if @json['relationship'] then new Coding(@json['relationship'])
  
###*
This resource provides the request and response details for the request for which all actions are to be reversed or terminated.
@class Reversal
@exports Reversal as Reversal
###
class Reversal extends DomainResource
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
  The date when this resource was created.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  The Insurer who is target  of the request.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
  ###*
  The practitioner who is responsible for the services rendered to the patient.
  @returns {Reference}
  ###
  provider: -> if @json['provider'] then new Reference(@json['provider'])
  
  ###*
  The organization which is responsible for the services rendered to the patient.
  @returns {Reference}
  ###
  organization: -> if @json['organization'] then new Reference(@json['organization'])
  
  ###*
  Reference of resource to reverse.
  @returns {Reference}
  ###
  request: -> if @json['request'] then new Reference(@json['request'])
  
  ###*
  Reference of response to resource to reverse.
  @returns {Reference}
  ###
  response: -> if @json['response'] then new Reference(@json['response'])
  
  ###*
  Payee information suypplied for matching purposes.
  @returns {PayeeComponent}
  ###
  payee: -> if @json['payee'] then new PayeeComponent(@json['payee'])
  
  ###*
  Financial instrument by which payment information for health care.
  @returns {ReversalCoverageComponent}
  ###
  coverage: -> if @json['coverage'] then new ReversalCoverageComponent(@json['coverage'])
  
  ###*
  If true remove all history excluding audit.
  @returns {Array} an array of {@link boolean} objects
  ###
  nullify:-> @json['nullify']
  



module.exports.Reversal = Reversal
