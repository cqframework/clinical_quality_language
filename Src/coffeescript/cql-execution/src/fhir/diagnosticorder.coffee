
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
@class DiagnosticOrderEventComponent
@exports  DiagnosticOrderEventComponent as DiagnosticOrderEventComponent
###
class DiagnosticOrderEventComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The status for the event.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Additional information about the event that occurred - e.g. if the status remained unchanged.
  @returns {CodeableConcept}
  ###
  description: -> if @json['description'] then new CodeableConcept(@json['description'])
  
  ###*
  The date/time at which the event occurred.
  @returns {Array} an array of {@link Date} objects
  ###
  dateTime:-> if @json['dateTime'] then DT.DateTime.parse(@json['dateTime'])
  
  ###*
  The person who was responsible for performing or recording the action.
  @returns {Reference}
  ###
  actor: -> if @json['actor'] then new Reference(@json['actor'])
  

###* 
Embedded class
@class DiagnosticOrderItemComponent
@exports  DiagnosticOrderItemComponent as DiagnosticOrderItemComponent
###
class DiagnosticOrderItemComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A code that identifies a particular diagnostic investigation, or panel of investigations, that have been requested.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  If the item is related to a specific speciment.
  @returns {Array} an array of {@link Reference} objects
  ###
  specimen: ->
    if @json['specimen']
      for item in @json['specimen']
        new Reference(item)
  
  ###*
  Anatomical location where the request test should be performed.
  @returns {CodeableConcept}
  ###
  bodySite: -> if @json['bodySite'] then new CodeableConcept(@json['bodySite'])
  
  ###*
  The status of this individual item within the order.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  A summary of the events of interest that have occurred as this item of the request is processed.
  @returns {Array} an array of {@link DiagnosticOrderEventComponent} objects
  ###
  event: ->
    if @json['event']
      for item in @json['event']
        new DiagnosticOrderEventComponent(item)
  
###*
A request for a diagnostic investigation service to be performed.
@class DiagnosticOrder
@exports DiagnosticOrder as DiagnosticOrder
###
class DiagnosticOrder extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Who or what the investigation is to be performed on. This is usually a human patient, but diagnostic tests can also be requested on animals, groups of humans or animals, devices such as dialysis machines, or even locations (typically for environmental scans).
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The practitioner that holds legal responsibility for ordering the investigation.
  @returns {Reference}
  ###
  orderer: -> if @json['orderer'] then new Reference(@json['orderer'])
  
  ###*
  Identifiers assigned to this order by the order or by the receiver.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  An encounter that provides additional information about the healthcare context in which this request is made.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  An explanation or justification for why this diagnostic investigation is being requested.
  @returns {Array} an array of {@link String} objects
  ###
  clinicalNotes:-> @json['clinicalNotes']
  
  ###*
  Additional clinical information about the patient or specimen that may influence test interpretations.
  @returns {Array} an array of {@link Reference} objects
  ###
  supportingInformation: ->
    if @json['supportingInformation']
      for item in @json['supportingInformation']
        new Reference(item)
  
  ###*
  One or more specimens that the diagnostic investigation is about.
  @returns {Array} an array of {@link Reference} objects
  ###
  specimen: ->
    if @json['specimen']
      for item in @json['specimen']
        new Reference(item)
  
  ###*
  The status of the order.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The clinical priority associated with this order.
  @returns {Array} an array of {@link String} objects
  ###
  priority:-> @json['priority']
  
  ###*
  A summary of the events of interest that have occurred as the request is processed. E.g. when the order was made, various processing steps (specimens received), when it was completed.
  @returns {Array} an array of {@link DiagnosticOrderEventComponent} objects
  ###
  event: ->
    if @json['event']
      for item in @json['event']
        new DiagnosticOrderEventComponent(item)
  
  ###*
  The specific diagnostic investigations that are requested as part of this request. Sometimes, there can only be one item per request, but in most contexts, more than one investigation can be requested.
  @returns {Array} an array of {@link DiagnosticOrderItemComponent} objects
  ###
  item: ->
    if @json['item']
      for item in @json['item']
        new DiagnosticOrderItemComponent(item)
  



module.exports.DiagnosticOrder = DiagnosticOrder
