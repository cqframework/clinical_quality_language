
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
@class OrderWhenComponent
@exports  OrderWhenComponent as OrderWhenComponent
###
class OrderWhenComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code specifies when request should be done. The code may simply be a priority code.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  A formal schedule.
  @returns {Timing}
  ###
  schedule: -> if @json['schedule'] then new Timing(@json['schedule'])
  
###*
A request to perform an action.
@class Order
@exports Order as Order
###
class Order extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Identifiers assigned to this order by the orderer or by the receiver.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  When the order was made.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Patient this order is about.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Who initiated the order.
  @returns {Reference}
  ###
  source: -> if @json['source'] then new Reference(@json['source'])
  
  ###*
  Who is intended to fulfill the order.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
  ###*
  Text - why the order was made.
  @returns {CodeableConcept}
  ###
  reasonCodeableConcept: -> if @json['reasonCodeableConcept'] then new CodeableConcept(@json['reasonCodeableConcept'])
  ###*
  Text - why the order was made.
  @returns {Reference}
  ###
  reasonReference: -> if @json['reasonReference'] then new Reference(@json['reasonReference'])
  
  ###*
  If required by policy.
  @returns {Reference}
  ###
  authority: -> if @json['authority'] then new Reference(@json['authority'])
  
  ###*
  When order should be fulfilled.
  @returns {OrderWhenComponent}
  ###
  when: -> if @json['when'] then new OrderWhenComponent(@json['when'])
  
  ###*
  What action is being ordered.
  @returns {Array} an array of {@link Reference} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new Reference(item)
  



module.exports.Order = Order
