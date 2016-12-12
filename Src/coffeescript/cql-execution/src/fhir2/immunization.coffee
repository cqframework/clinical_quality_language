
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
@class ImmunizationExplanationComponent
@exports  ImmunizationExplanationComponent as ImmunizationExplanationComponent
###
class ImmunizationExplanationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Reasons why a vaccine was administered.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  reason: ->
    if @json['reason']
      for item in @json['reason']
        new CodeableConcept(item)
  
  ###*
  Refusal or exemption reasons.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  refusalReason: ->
    if @json['refusalReason']
      for item in @json['refusalReason']
        new CodeableConcept(item)
  

###* 
Embedded class
@class ImmunizationReactionComponent
@exports  ImmunizationReactionComponent as ImmunizationReactionComponent
###
class ImmunizationReactionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Date of reaction to the immunization.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Details of the reaction.
  @returns {Reference}
  ###
  detail: -> if @json['detail'] then new Reference(@json['detail'])
  
  ###*
  Self-reported indicator.
  @returns {Array} an array of {@link boolean} objects
  ###
  reported:-> @json['reported']
  

###* 
Embedded class
@class ImmunizationVaccinationProtocolComponent
@exports  ImmunizationVaccinationProtocolComponent as ImmunizationVaccinationProtocolComponent
###
class ImmunizationVaccinationProtocolComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Nominal position in a series.
  @returns {Array} an array of {@link Number} objects
  ###
  doseSequence:-> @json['doseSequence']
  
  ###*
  Contains the description about the protocol under which the vaccine was administered.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Indicates the authority who published the protocol?  E.g. ACIP.
  @returns {Reference}
  ###
  authority: -> if @json['authority'] then new Reference(@json['authority'])
  
  ###*
  One possible path to achieve presumed immunity against a disease - within the context of an authority.
  @returns {Array} an array of {@link String} objects
  ###
  series:-> @json['series']
  
  ###*
  The recommended number of doses to achieve immunity.
  @returns {Array} an array of {@link Number} objects
  ###
  seriesDoses:-> @json['seriesDoses']
  
  ###*
  The targeted disease.
  @returns {CodeableConcept}
  ###
  doseTarget: -> if @json['doseTarget'] then new CodeableConcept(@json['doseTarget'])
  
  ###*
  Indicates if the immunization event should "count" against  the protocol.
  @returns {CodeableConcept}
  ###
  doseStatus: -> if @json['doseStatus'] then new CodeableConcept(@json['doseStatus'])
  
  ###*
  Provides an explanation as to why a immunization event should or should not count against the protocol.
  @returns {CodeableConcept}
  ###
  doseStatusReason: -> if @json['doseStatusReason'] then new CodeableConcept(@json['doseStatusReason'])
  
###*
Immunization event information.
@class Immunization
@exports Immunization as Immunization
###
class Immunization extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  A unique identifier assigned to this adverse reaction record.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Date vaccine administered or was to be administered.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Vaccine that was administered or was to be administered.
  @returns {CodeableConcept}
  ###
  vaccineType: -> if @json['vaccineType'] then new CodeableConcept(@json['vaccineType'])
  
  ###*
  The patient to whom the vaccine was to be administered.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Indicates if the vaccination was refused.
  @returns {Array} an array of {@link boolean} objects
  ###
  refusedIndicator:-> @json['refusedIndicator']
  
  ###*
  True if this administration was reported rather than directly administered.
  @returns {Array} an array of {@link boolean} objects
  ###
  reported:-> @json['reported']
  
  ###*
  Clinician who administered the vaccine.
  @returns {Reference}
  ###
  performer: -> if @json['performer'] then new Reference(@json['performer'])
  
  ###*
  Clinician who ordered the vaccination.
  @returns {Reference}
  ###
  requester: -> if @json['requester'] then new Reference(@json['requester'])
  
  ###*
  Name of vaccine manufacturer.
  @returns {Reference}
  ###
  manufacturer: -> if @json['manufacturer'] then new Reference(@json['manufacturer'])
  
  ###*
  The service delivery location where the vaccine administration occurred.
  @returns {Reference}
  ###
  location: -> if @json['location'] then new Reference(@json['location'])
  
  ###*
  Lot number of the  vaccine product.
  @returns {Array} an array of {@link String} objects
  ###
  lotNumber:-> @json['lotNumber']
  
  ###*
  Date vaccine batch expires.
  @returns {Array} an array of {@link Date} objects
  ###
  expirationDate:-> if @json['expirationDate'] then DT.DateTime.parse(@json['expirationDate'])
  
  ###*
  Body site where vaccine was administered.
  @returns {CodeableConcept}
  ###
  site: -> if @json['site'] then new CodeableConcept(@json['site'])
  
  ###*
  The path by which the vaccine product is taken into the body.
  @returns {CodeableConcept}
  ###
  route: -> if @json['route'] then new CodeableConcept(@json['route'])
  
  ###*
  The quantity of vaccine product that was administered.
  @returns {Quantity}
  ###
  doseQuantity: -> if @json['doseQuantity'] then new Quantity(@json['doseQuantity'])
  
  ###*
  Reasons why a vaccine was administered or refused.
  @returns {ImmunizationExplanationComponent}
  ###
  explanation: -> if @json['explanation'] then new ImmunizationExplanationComponent(@json['explanation'])
  
  ###*
  Categorical data indicating that an adverse event is associated in time to an immunization.
  @returns {Array} an array of {@link ImmunizationReactionComponent} objects
  ###
  reaction: ->
    if @json['reaction']
      for item in @json['reaction']
        new ImmunizationReactionComponent(item)
  
  ###*
  Contains information about the protocol(s) under which the vaccine was administered.
  @returns {Array} an array of {@link ImmunizationVaccinationProtocolComponent} objects
  ###
  vaccinationProtocol: ->
    if @json['vaccinationProtocol']
      for item in @json['vaccinationProtocol']
        new ImmunizationVaccinationProtocolComponent(item)
  



module.exports.Immunization = Immunization
