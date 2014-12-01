
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
@class CarePlanParticipantComponent
@exports  CarePlanParticipantComponent as CarePlanParticipantComponent
###
class CarePlanParticipantComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates specific responsibility of an individual within the care plan.  E.g. "Primary physician", "Team coordinator", "Caregiver", etc.
  @returns {CodeableConcept}
  ###
  role: -> if @json['role'] then new CodeableConcept(@json['role'])
  
  ###*
  The specific person or organization who is participating/expected to participate in the care plan.
  @returns {Reference}
  ###
  member: -> if @json['member'] then new Reference(@json['member'])
  

###* 
Embedded class
@class CarePlanGoalComponent
@exports  CarePlanGoalComponent as CarePlanGoalComponent
###
class CarePlanGoalComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Human-readable description of a specific desired objective of the care plan.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Indicates whether the goal has been reached and is still considered relevant.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Any comments related to the goal.
  @returns {Array} an array of {@link String} objects
  ###
  notes:-> @json['notes']
  
  ###*
  The identified conditions that this goal relates to - the condition that caused it to be created, or that it is intended to address.
  @returns {Array} an array of {@link Reference} objects
  ###
  concern: ->
    if @json['concern']
      for item in @json['concern']
        new Reference(item)
  

###* 
Embedded class
@class CarePlanActivitySimpleComponent
@exports  CarePlanActivitySimpleComponent as CarePlanActivitySimpleComponent
###
class CarePlanActivitySimpleComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  High-level categorization of the type of activity in a care plan.
  @returns {Array} an array of {@link String} objects
  ###
  category:-> @json['category']
  
  ###*
  Detailed description of the type of activity.  E.g. What lab test, what procedure, what kind of encounter.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  The period, timing or frequency upon which the described activity is to occur.
  @returns {Timing}
  ###
  scheduledTiming: -> if @json['scheduledTiming'] then new Timing(@json['scheduledTiming'])
  ###*
  The period, timing or frequency upon which the described activity is to occur.
  @returns {Period}
  ###
  scheduledPeriod: -> if @json['scheduledPeriod'] then new Period(@json['scheduledPeriod'])
  ###*
  The period, timing or frequency upon which the described activity is to occur.
  @returns {Array} an array of {@link String} objects
  ###
  scheduledString:-> @json['scheduledString']
  
  ###*
  Identifies the facility where the activity will occur.  E.g. home, hospital, specific clinic, etc.
  @returns {Reference}
  ###
  location: -> if @json['location'] then new Reference(@json['location'])
  
  ###*
  Identifies who's expected to be involved in the activity.
  @returns {Array} an array of {@link Reference} objects
  ###
  performer: ->
    if @json['performer']
      for item in @json['performer']
        new Reference(item)
  
  ###*
  Identifies the food, drug or other product being consumed or supplied in the activity.
  @returns {Reference}
  ###
  product: -> if @json['product'] then new Reference(@json['product'])
  
  ###*
  Identifies the quantity expected to be consumed in a given day.
  @returns {Quantity}
  ###
  dailyAmount: -> if @json['dailyAmount'] then new Quantity(@json['dailyAmount'])
  
  ###*
  Identifies the quantity expected to be supplied.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  This provides a textual description of constraints on the activity occurrence, including relation to other activities.  It may also include objectives, pre-conditions and end-conditions.  Finally, it may convey specifics about the activity such as body site, method, route, etc.
  @returns {Array} an array of {@link String} objects
  ###
  details:-> @json['details']
  

###* 
Embedded class
@class CarePlanActivityComponent
@exports  CarePlanActivityComponent as CarePlanActivityComponent
###
class CarePlanActivityComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Internal reference that identifies the goals that this activity is intended to contribute towards meeting.
  @returns {Array} an array of {@link String} objects
  ###
  goal:-> @json['goal']
  
  ###*
  Identifies what progress is being made for the specific activity.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  If true, indicates that the described activity is one that must NOT be engaged in when following the plan.
  @returns {Array} an array of {@link boolean} objects
  ###
  prohibited:-> @json['prohibited']
  
  ###*
  Resources that describe follow-on actions resulting from the plan, such as drug prescriptions, encounter records, appointments, etc.
  @returns {Array} an array of {@link Reference} objects
  ###
  actionResulting: ->
    if @json['actionResulting']
      for item in @json['actionResulting']
        new Reference(item)
  
  ###*
  Notes about the execution of the activity.
  @returns {Array} an array of {@link String} objects
  ###
  notes:-> @json['notes']
  
  ###*
  The details of the proposed activity represented in a specific resource.
  @returns {Reference}
  ###
  detail: -> if @json['detail'] then new Reference(@json['detail'])
  
  ###*
  A simple summary of details suitable for a general care plan system (e.g. form driven) that doesn't know about specific resources such as procedure etc.
  @returns {CarePlanActivitySimpleComponent}
  ###
  simple: -> if @json['simple'] then new CarePlanActivitySimpleComponent(@json['simple'])
  
###*
Describes the intention of how one or more practitioners intend to deliver care for a particular patient for a period of time, possibly limited to care for a specific condition or set of conditions.
@class CarePlan
@exports CarePlan as CarePlan
###
class CarePlan extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  This records identifiers associated with this care plan that are defined by business processed and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Identifies the patient/subject whose intended care is described by the plan.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  Indicates whether the plan is currently being acted upon, represents future intentions or is now just historical record.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Indicates when the plan did (or is intended to) come into effect and end.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  Identifies the most recent date on which the plan has been revised.
  @returns {Array} an array of {@link Date} objects
  ###
  modified:-> if @json['modified'] then DT.DateTime.parse(@json['modified'])
  
  ###*
  Identifies the conditions/problems/concerns/diagnoses/etc. whose management and/or mitigation are handled by this plan.
  @returns {Array} an array of {@link Reference} objects
  ###
  concern: ->
    if @json['concern']
      for item in @json['concern']
        new Reference(item)
  
  ###*
  Identifies all people and organizations who are expected to be involved in the care envisioned by this plan.
  @returns {Array} an array of {@link CarePlanParticipantComponent} objects
  ###
  participant: ->
    if @json['participant']
      for item in @json['participant']
        new CarePlanParticipantComponent(item)
  
  ###*
  Describes the intended objective(s) of carrying out the Care Plan.
  @returns {Array} an array of {@link CarePlanGoalComponent} objects
  ###
  goal: ->
    if @json['goal']
      for item in @json['goal']
        new CarePlanGoalComponent(item)
  
  ###*
  Identifies a planned action to occur as part of the plan.  For example, a medication to be used, lab tests to perform, self-monitoring, education, etc.
  @returns {Array} an array of {@link CarePlanActivityComponent} objects
  ###
  activity: ->
    if @json['activity']
      for item in @json['activity']
        new CarePlanActivityComponent(item)
  
  ###*
  General notes about the care plan not covered elsewhere.
  @returns {Array} an array of {@link String} objects
  ###
  notes:-> @json['notes']
  



module.exports.CarePlan = CarePlan
