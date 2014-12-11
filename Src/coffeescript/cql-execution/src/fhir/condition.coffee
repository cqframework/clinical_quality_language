
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
@class ConditionStageComponent
@exports  ConditionStageComponent as ConditionStageComponent
###
class ConditionStageComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A simple summary of the stage such as "Stage 3". The determination of the stage is disease-specific.
  @returns {CodeableConcept}
  ###
  summary: -> if @json['summary'] then new CodeableConcept(@json['summary'])
  
  ###*
  Reference to a formal record of the evidence on which the staging assessment is based.
  @returns {Array} an array of {@link Reference} objects
  ###
  assessment: ->
    if @json['assessment']
      for item in @json['assessment']
        new Reference(item)
  

###* 
Embedded class
@class ConditionEvidenceComponent
@exports  ConditionEvidenceComponent as ConditionEvidenceComponent
###
class ConditionEvidenceComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A manifestation or symptom that led to the recording of this condition.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  Links to other relevant information, including pathology reports.
  @returns {Array} an array of {@link Reference} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new Reference(item)
  

###* 
Embedded class
@class ConditionLocationComponent
@exports  ConditionLocationComponent as ConditionLocationComponent
###
class ConditionLocationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code that identifies the structural location.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  Detailed anatomical location information.
  @returns {Array} an array of {@link String} objects
  ###
  detail:-> @json['detail']
  

###* 
Embedded class
@class ConditionDueToComponent
@exports  ConditionDueToComponent as ConditionDueToComponent
###
class ConditionDueToComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code that identifies the target of this relationship. The code takes the place of a detailed instance target.
  @returns {CodeableConcept}
  ###
  codeableConcept: -> if @json['codeableConcept'] then new CodeableConcept(@json['codeableConcept'])
  
  ###*
  Target of the relationship.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  

###* 
Embedded class
@class ConditionOccurredFollowingComponent
@exports  ConditionOccurredFollowingComponent as ConditionOccurredFollowingComponent
###
class ConditionOccurredFollowingComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Code that identifies the target of this relationship. The code takes the place of a detailed instance target.
  @returns {CodeableConcept}
  ###
  codeableConcept: -> if @json['codeableConcept'] then new CodeableConcept(@json['codeableConcept'])
  
  ###*
  Target of the relationship.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
###*
Use to record detailed information about conditions, problems or diagnoses recognized by a clinician. There are many uses including: recording a Diagnosis during an Encounter; populating a problem List or a Summary Statement, such as a Discharge Summary.
@class Condition
@exports Condition as Condition
###
class Condition extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  This records identifiers associated with this condition that are defined by business processed and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Indicates the patient who the condition record is associated with.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Encounter during which the condition was first asserted.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  Person who takes responsibility for asserting the existence of the condition as part of the electronic record.
  @returns {Reference}
  ###
  asserter: -> if @json['asserter'] then new Reference(@json['asserter'])
  
  ###*
  Estimated or actual date the condition/problem/diagnosis was first detected/suspected.
  @returns {Array} an array of {@link Date} objects
  ###
  dateAsserted:-> if @json['dateAsserted'] then DT.DateTime.parse(@json['dateAsserted'])
  
  ###*
  Identification of the condition, problem or diagnosis.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  A category assigned to the condition. E.g. complaint | symptom | finding | diagnosis.
  @returns {CodeableConcept}
  ###
  category: -> if @json['category'] then new CodeableConcept(@json['category'])
  
  ###*
  The clinical status of the condition.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The degree of confidence that this condition is correct.
  @returns {CodeableConcept}
  ###
  certainty: -> if @json['certainty'] then new CodeableConcept(@json['certainty'])
  
  ###*
  A subjective assessment of the severity of the condition as evaluated by the clinician.
  @returns {CodeableConcept}
  ###
  severity: -> if @json['severity'] then new CodeableConcept(@json['severity'])
  
  ###*
  Estimated or actual date or date-time  the condition began, in the opinion of the clinician.
  @returns {Array} an array of {@link Date} objects
  ###
  onsetDateTime:-> if @json['onsetDateTime'] then DT.DateTime.parse(@json['onsetDateTime'])
  onsetAge:->  new Quantity(@json['onsetAge'])
  
  ###*
  The date or estimated date that the condition resolved or went into remission. This is called "abatement" because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never really resolved, but they can abate.
  @returns {Array} an array of {@link Date} objects
  ###
  abatementDate:-> if @json['abatementDate'] then DT.DateTime.parse(@json['abatementDate'])
  abatementAge:->  new Quantity(@json['abatementAge'])
  ###*
  The date or estimated date that the condition resolved or went into remission. This is called "abatement" because of the many overloaded connotations associated with "remission" or "resolution" - Conditions are never really resolved, but they can abate.
  @returns {Array} an array of {@link boolean} objects
  ###
  abatementBoolean:-> @json['abatementBoolean']
  
  ###*
  Clinical stage or grade of a condition. May include formal severity assessments.
  @returns {ConditionStageComponent}
  ###
  stage: -> if @json['stage'] then new ConditionStageComponent(@json['stage'])
  
  ###*
  Supporting Evidence / manifestations that are the basis on which this condition is suspected or confirmed.
  @returns {Array} an array of {@link ConditionEvidenceComponent} objects
  ###
  evidence: ->
    if @json['evidence']
      for item in @json['evidence']
        new ConditionEvidenceComponent(item)
  
  ###*
  The anatomical location where this condition manifests itself.
  @returns {Array} an array of {@link ConditionLocationComponent} objects
  ###
  location: ->
    if @json['location']
      for item in @json['location']
        new ConditionLocationComponent(item)
  
  ###*
  Further conditions, problems, diagnoses, procedures or events or the substance that caused/triggered this Condition.
  @returns {Array} an array of {@link ConditionDueToComponent} objects
  ###
  dueTo: ->
    if @json['dueTo']
      for item in @json['dueTo']
        new ConditionDueToComponent(item)
  
  ###*
  Further conditions, problems, diagnoses, procedures or events or the substance that preceded this Condition.
  @returns {Array} an array of {@link ConditionOccurredFollowingComponent} objects
  ###
  occurredFollowing: ->
    if @json['occurredFollowing']
      for item in @json['occurredFollowing']
        new ConditionOccurredFollowingComponent(item)
  
  ###*
  Additional information about the Condition. This is a general notes/comments entry  for description of the Condition, its diagnosis and prognosis.
  @returns {Array} an array of {@link String} objects
  ###
  notes:-> @json['notes']
  



module.exports.Condition = Condition
