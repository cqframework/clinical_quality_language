
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
@class ImmunizationRecommendationRecommendationDateCriterionComponent
@exports  ImmunizationRecommendationRecommendationDateCriterionComponent as ImmunizationRecommendationRecommendationDateCriterionComponent
###
class ImmunizationRecommendationRecommendationDateCriterionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Date classification of recommendation - e.g. earliest date to give, latest date to give, etc.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  Date recommendation.
  @returns {Array} an array of {@link Date} objects
  ###
  value:-> if @json['value'] then DT.DateTime.parse(@json['value'])
  

###* 
Embedded class
@class ImmunizationRecommendationRecommendationProtocolComponent
@exports  ImmunizationRecommendationRecommendationProtocolComponent as ImmunizationRecommendationRecommendationProtocolComponent
###
class ImmunizationRecommendationRecommendationProtocolComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates the nominal position in a series of the next dose.  This is the recommended dose number as per a specified protocol.
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
Embedded class
@class ImmunizationRecommendationRecommendationComponent
@exports  ImmunizationRecommendationRecommendationComponent as ImmunizationRecommendationRecommendationComponent
###
class ImmunizationRecommendationRecommendationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The date the immunization recommendation was created.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Vaccine that pertains to the recommendation.
  @returns {CodeableConcept}
  ###
  vaccineType: -> if @json['vaccineType'] then new CodeableConcept(@json['vaccineType'])
  
  ###*
  This indicates the next recommended dose number (e.g. dose 2 is the next recommended dose).
  @returns {Array} an array of {@link Number} objects
  ###
  doseNumber:-> @json['doseNumber']
  
  ###*
  Vaccine administration status.
  @returns {CodeableConcept}
  ###
  forecastStatus: -> if @json['forecastStatus'] then new CodeableConcept(@json['forecastStatus'])
  
  ###*
  Vaccine date recommendations - e.g. earliest date to administer, latest date to administer, etc.
  @returns {Array} an array of {@link ImmunizationRecommendationRecommendationDateCriterionComponent} objects
  ###
  dateCriterion: ->
    if @json['dateCriterion']
      for item in @json['dateCriterion']
        new ImmunizationRecommendationRecommendationDateCriterionComponent(item)
  
  ###*
  Contains information about the protocol under which the vaccine was administered.
  @returns {ImmunizationRecommendationRecommendationProtocolComponent}
  ###
  protocol: -> if @json['protocol'] then new ImmunizationRecommendationRecommendationProtocolComponent(@json['protocol'])
  
  ###*
  Immunization event history that supports the status and recommendation.
  @returns {Array} an array of {@link Reference} objects
  ###
  supportingImmunization: ->
    if @json['supportingImmunization']
      for item in @json['supportingImmunization']
        new Reference(item)
  
  ###*
  Patient Information that supports the status and recommendation.  This includes patient observations, adverse reactions and allergy/intolerance information.
  @returns {Array} an array of {@link Reference} objects
  ###
  supportingPatientInformation: ->
    if @json['supportingPatientInformation']
      for item in @json['supportingPatientInformation']
        new Reference(item)
  
###*
A patient's point-of-time immunization status and recommendation with optional supporting justification.
@class ImmunizationRecommendation
@exports ImmunizationRecommendation as ImmunizationRecommendation
###
class ImmunizationRecommendation extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  A unique identifier assigned to this particular recommendation record.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The patient who is the subject of the profile.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Vaccine administration recommendations.
  @returns {Array} an array of {@link ImmunizationRecommendationRecommendationComponent} objects
  ###
  recommendation: ->
    if @json['recommendation']
      for item in @json['recommendation']
        new ImmunizationRecommendationRecommendationComponent(item)
  



module.exports.ImmunizationRecommendation = ImmunizationRecommendation
