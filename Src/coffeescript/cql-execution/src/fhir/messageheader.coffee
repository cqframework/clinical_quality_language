
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
@class MessageHeaderResponseComponent
@exports  MessageHeaderResponseComponent as MessageHeaderResponseComponent
###
class MessageHeaderResponseComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The id of the message that this message is a response to.
  @returns {Array} an array of {@link String} objects
  ###
  identifier:-> @json['identifier']
  
  ###*
  Code that identifies the type of response to the message - whether it was successful or not, and whether it should be resent or not.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  Full details of any issues found in the message.
  @returns {Reference}
  ###
  details: -> if @json['details'] then new Reference(@json['details'])
  

###* 
Embedded class
@class MessageSourceComponent
@exports  MessageSourceComponent as MessageSourceComponent
###
class MessageSourceComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Human-readable name for the source system.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  May include configuration or other information useful in debugging.
  @returns {Array} an array of {@link String} objects
  ###
  software:-> @json['software']
  
  ###*
  Can convey versions of multiple systems in situations where a message passes through multiple hands.
  @returns {Array} an array of {@link String} objects
  ###
  version:-> @json['version']
  
  ###*
  An e-mail, phone, website or other contact point to use to resolve issues with message communications.
  @returns {ContactPoint}
  ###
  contact: -> if @json['contact'] then new ContactPoint(@json['contact'])
  
  ###*
  Identifies the routing target to send acknowledgements to.
  @returns {Array} an array of {@link String} objects
  ###
  endpoint:-> @json['endpoint']
  

###* 
Embedded class
@class MessageDestinationComponent
@exports  MessageDestinationComponent as MessageDestinationComponent
###
class MessageDestinationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Human-readable name for the target system.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Identifies the target end system in situations where the initial message transmission is to an intermediary system.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
  ###*
  Indicates where the message should be routed to.
  @returns {Array} an array of {@link String} objects
  ###
  endpoint:-> @json['endpoint']
  
###*
The header for a message exchange that is either requesting or responding to an action.  The Reference(s) that are the subject of the action as well as other Information related to the action are typically transmitted in a bundle in which the MessageHeader resource instance is the first resource in the bundle.
@class MessageHeader
@exports MessageHeader as MessageHeader
###
class MessageHeader extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The identifier of this message.
  @returns {Array} an array of {@link String} objects
  ###
  identifier:-> @json['identifier']
  
  ###*
  The time that the message was sent.
  @returns {Array} an array of {@link Date} objects
  ###
  timestamp:-> if @json['timestamp'] then DT.DateTime.parse(@json['timestamp'])
  
  ###*
  Code that identifies the event this message represents and connects it with its definition. Events defined as part of the FHIR specification have the system value "http://hl7.org/fhir/message-type".
  @returns {Coding}
  ###
  event: -> if @json['event'] then new Coding(@json['event'])
  
  ###*
  Information about the message that this message is a response to.  Only present if this message is a response.
  @returns {MessageHeaderResponseComponent}
  ###
  response: -> if @json['response'] then new MessageHeaderResponseComponent(@json['response'])
  
  ###*
  The source application from which this message originated.
  @returns {MessageSourceComponent}
  ###
  source: -> if @json['source'] then new MessageSourceComponent(@json['source'])
  
  ###*
  The destination application which the message is intended for.
  @returns {Array} an array of {@link MessageDestinationComponent} objects
  ###
  destination: ->
    if @json['destination']
      for item in @json['destination']
        new MessageDestinationComponent(item)
  
  ###*
  The person or device that performed the data entry leading to this message. Where there is more than one candidate, pick the most proximal to the message. Can provide other enterers in extensions.
  @returns {Reference}
  ###
  enterer: -> if @json['enterer'] then new Reference(@json['enterer'])
  
  ###*
  The logical author of the message - the person or device that decided the described event should happen. Where there is more than one candidate, pick the most proximal to the MessageHeader. Can provide other authors in extensions.
  @returns {Reference}
  ###
  author: -> if @json['author'] then new Reference(@json['author'])
  
  ###*
  Allows data conveyed by a message to be addressed to a particular person or department when routing to a specific application isn't sufficient.
  @returns {Reference}
  ###
  receiver: -> if @json['receiver'] then new Reference(@json['receiver'])
  
  ###*
  The person or organization that accepts overall responsibility for the contents of the message. The implication is that the message event happened under the policies of the responsible party.
  @returns {Reference}
  ###
  responsible: -> if @json['responsible'] then new Reference(@json['responsible'])
  
  ###*
  Coded indication of the cause for the event - indicates  a reason for the occurance of the event that is a focus of this message.
  @returns {CodeableConcept}
  ###
  reason: -> if @json['reason'] then new CodeableConcept(@json['reason'])
  
  ###*
  The actual data of the message - a reference to the root/focus class of the event.
  @returns {Array} an array of {@link Reference} objects
  ###
  data: ->
    if @json['data']
      for item in @json['data']
        new Reference(item)
  



module.exports.MessageHeader = MessageHeader
