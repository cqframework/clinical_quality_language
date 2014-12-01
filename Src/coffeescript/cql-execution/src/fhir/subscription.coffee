
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
@class SubscriptionChannelComponent
@exports  SubscriptionChannelComponent as SubscriptionChannelComponent
###
class SubscriptionChannelComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  url:-> @json['url']
  
  ###*
  ToDo.
  @returns {Array} an array of {@link String} objects
  ###
  payload:-> @json['payload']
  
  ###*
  Usage depends on the channel type.
  @returns {Array} an array of {@link String} objects
  ###
  header:-> @json['header']
  

###* 
Embedded class
@class SubscriptionTagComponent
@exports  SubscriptionTagComponent as SubscriptionTagComponent
###
class SubscriptionTagComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  term:-> @json['term']
  
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  scheme:-> @json['scheme']
  
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
###*
Todo.
@class Subscription
@exports Subscription as Subscription
###
class Subscription extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  criteria:-> @json['criteria']
  
  ###*
  Todo.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  contact: ->
    if @json['contact']
      for item in @json['contact']
        new ContactPoint(item)
  
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  reason:-> @json['reason']
  
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Todo.
  @returns {Array} an array of {@link String} objects
  ###
  error:-> @json['error']
  
  ###*
  Todo.
  @returns {SubscriptionChannelComponent}
  ###
  channel: -> if @json['channel'] then new SubscriptionChannelComponent(@json['channel'])
  
  ###*
  Todo.
  @returns {Array} an array of {@link Date} objects
  ###
  end:-> if @json['end'] then DT.DateTime.parse(@json['end'])
  
  ###*
  Todo.
  @returns {Array} an array of {@link SubscriptionTagComponent} objects
  ###
  tag: ->
    if @json['tag']
      for item in @json['tag']
        new SubscriptionTagComponent(item)
  



module.exports.Subscription = Subscription
