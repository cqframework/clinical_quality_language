
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
@class StatusResponseNotesComponent
@exports  StatusResponseNotesComponent as StatusResponseNotesComponent
###
class StatusResponseNotesComponent extends BackboneElement
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
This resource provides processing status, errors and notes from the processing of a resource.
@class StatusResponse
@exports StatusResponse as StatusResponse
###
class StatusResponse extends DomainResource
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
  Transaction status: error, complete, held.
  @returns {Coding}
  ###
  outcome: -> if @json['outcome'] then new Coding(@json['outcome'])
  
  ###*
  A description of the status of the adjudication or processing.
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
  The form to be used for printing the content.
  @returns {Coding}
  ###
  form: -> if @json['form'] then new Coding(@json['form'])
  
  ###*
  Suite of processing note or additional requirements is the processing has been held.
  @returns {Array} an array of {@link StatusResponseNotesComponent} objects
  ###
  notes: ->
    if @json['notes']
      for item in @json['notes']
        new StatusResponseNotesComponent(item)
  
  ###*
  Processing errors.
  @returns {Array} an array of {@link Coding} objects
  ###
  error: ->
    if @json['error']
      for item in @json['error']
        new Coding(item)
  



module.exports.StatusResponse = StatusResponse
