
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
@class RiskAssessmentPredictionComponent
@exports  RiskAssessmentPredictionComponent as RiskAssessmentPredictionComponent
###
class RiskAssessmentPredictionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  One of the potential outcomes for the patient (e.g. remission, death,  a particular condition).
  @returns {CodeableConcept}
  ###
  outcome: -> if @json['outcome'] then new CodeableConcept(@json['outcome'])
  
  ###*
  How likely is the outcome (in the specified timeframe).
  @returns {Array} an array of {@link Number} objects
  ###
  probabilityDecimal:-> @json['probabilityDecimal']
  ###*
  How likely is the outcome (in the specified timeframe).
  @returns {Range}
  ###
  probabilityRange: -> if @json['probabilityRange'] then new Range(@json['probabilityRange'])
  ###*
  How likely is the outcome (in the specified timeframe).
  @returns {CodeableConcept}
  ###
  probabilityCodeableConcept: -> if @json['probabilityCodeableConcept'] then new CodeableConcept(@json['probabilityCodeableConcept'])
  
  ###*
  Indicates the risk for this particular subject (with their specific characteristics) divided by the risk of the population in general.  (Numbers greater than 1 = higher risk than the population, numbers less than 1 = lower risk.).
  @returns {Array} an array of {@link Number} objects
  ###
  relativeRisk:-> @json['relativeRisk']
  
  ###*
  Indicates the period of time or age range of the subject to which the specified probability applies.
  @returns {Period}
  ###
  whenPeriod: -> if @json['whenPeriod'] then new Period(@json['whenPeriod'])
  ###*
  Indicates the period of time or age range of the subject to which the specified probability applies.
  @returns {Range}
  ###
  whenRange: -> if @json['whenRange'] then new Range(@json['whenRange'])
  
  ###*
  Additional information explaining the basis for the prediction.
  @returns {Array} an array of {@link String} objects
  ###
  rationale:-> @json['rationale']
  
###*
An assessment of the likely outcome(s) for a patient or other subject as well as the likelihood of each outcome.
@class RiskAssessment
@exports RiskAssessment as RiskAssessment
###
class RiskAssessment extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The patient or group the risk assessment applies to.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The date (and possibly time) the risk assessment was performed.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  For assessments or prognosis specific to a particular condition, indicates the condition being assessed.
  @returns {Reference}
  ###
  condition: -> if @json['condition'] then new Reference(@json['condition'])
  
  ###*
  The provider or software application that performed the assessment.
  @returns {Reference}
  ###
  performer: -> if @json['performer'] then new Reference(@json['performer'])
  
  ###*
  Business identifier assigned to the risk assessment.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  The algorithm, processs or mechanism used to evaluate the risk.
  @returns {CodeableConcept}
  ###
  method: -> if @json['method'] then new CodeableConcept(@json['method'])
  
  ###*
  Indicates the source data considered as part of the assessment (FamilyHistory, Observations, Procedures, Conditions, etc.).
  @returns {Array} an array of {@link Reference} objects
  ###
  basis: ->
    if @json['basis']
      for item in @json['basis']
        new Reference(item)
  
  ###*
  Describes the expected outcome for the subject.
  @returns {Array} an array of {@link RiskAssessmentPredictionComponent} objects
  ###
  prediction: ->
    if @json['prediction']
      for item in @json['prediction']
        new RiskAssessmentPredictionComponent(item)
  
  ###*
  A description of the steps that might be taken to reduce the identified risk(s).
  @returns {Array} an array of {@link String} objects
  ###
  mitigation:-> @json['mitigation']
  



module.exports.RiskAssessment = RiskAssessment
