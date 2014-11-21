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
###*
@namespacing scoping into the QUICK namespace
###
###*
A statement forecasting the course or probable outcome of a condition in a specified time period, e.g., recovery of function after a spinal cord injury, risk of heart disease in the next 10 years, survival from cancer.

<b>Example</b>
<u>Expression from source knowledge artifact</u>
Risk Category Assessment: Framingham coronary heart disease 10 year risk (result &gt; 20 %)" during "Measurement Period

<u>Expression in CQL+QUICK</u>
let FraminghamScore = 
  [Prediction: "Framingham Score"] FSP
    where 
      FSP.likelihood &gt; 20 u '%'
        and
      FSP.observedAtTime during MeasurementPeriod
        and
      duration in years of FSP.timePeriod = 10 years
 
###
require './Period'
require './Inference'
require './StatementOfOccurrence'
require './StatementTopic'
require './StatementModality'
require './Patient'
require './CodeableConcept'
require './Identifier'
require './Person'
require './Element'
require './Entity'
###*
@class PredictionOccurrence
@exports  PredictionOccurrence as PredictionOccurrence
###
class PredictionOccurrence
  constructor: (@json) ->
 
  ###*
  Details about the clinical statement that were not represented at all or sufficiently in one of the attributes provided in a class. These may include for example a comment, an instruction, or a note associated with the statement.
  ### 
  additionalText: ->  @json['additionalText'] 
 
 
  ###*
  For assessments or prognosis specific to a particular condition, indicates the condition being assessed
  ### 
  condition: -> 
    if @json['condition']
      for x in @json['condition'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The encounter within which the clinical statement was created.
  ### 
  encounter: -> 
    if @json['encounter']
      for x in @json['encounter'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  <font color="#0f0f0f">A unique ID of this clinical statement for reference purposes. It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system. Does not need to be the actual ID of the source system. </font>
  ### 
  identifier: -> 
    if @json['identifier']
      for x in @json['identifier'] 
        new QUICK.Identifier(x)
       
  ###*
  How the prognosis was estimated or inferred.
  ### 
  inference: -> 
    if @json['inference']
      for x in @json['inference'] 
        new QUICK.Inference(x)
       
  ###*
  The likelihood of acquiring the condition specified as a numeric probability (less than or equal to 1) or a coded ordinal value.
  ### 
  likelihood: -> 
    if @json['likelihood']
      for x in @json['likelihood'] 
        new QUICK.Element(x)
       
  ###*
  The modality of a Clinical Statement describes the way the topic exists, happens, or is experienced.
  ### 
  modality: -> if @json['modality'] then new StatementModality( @json['modality'] )
 
 
  ###*
  Time when the observation was made by the statement author. This may be different than the time the observation was physically recorded (which may occur much later). This also is different time than when the observed phenomenon actually occurred. For example, a patient had a headache three days ago, but reported it to their physician today. The physician would record the observedAtTime as today.
  ### 
  observedAtTime: -> 
    if @json['observedAtTime']
      for x in @json['observedAtTime'] 
        new QUICK.Period(x)
       
  ###*
  The outcomes that is being predicted for the patient (e.g. remission, death, a particular condition).
  ### 
  outcome: -> if @json['outcome'] then new CodeableConcept( @json['outcome'] )
 
 
  ###*
  The identifier of a set of constraints placed on a clinical statement.  If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  The risk assessment procedure that led to this prognosis
  ### 
  riskAssessmentProcedure: -> 
    if @json['riskAssessmentProcedure']
      for x in @json['riskAssessmentProcedure'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The person who created the statement.

The source and the author of the statement may differ. Statement source is the system from which the statement originated. This may be an EHR or it may be a medical device. 

The statement author is the person creating the statement in the medical record. This may be a person who validates the data from a device, or obtains the history from a subject, a family member, or other source.
  ### 
  statementAuthor: -> 
    if @json['statementAuthor']
      for x in @json['statementAuthor'] 
        new QUICK.Person(x)
       
  ###*
  The time at which the statement was made/recorded. This may not be the same time as the occurrence of the action or the observation event.
  ### 
  statementDateTime: ->  @json['statementDateTime'] 
 
 
  ###*
  The person, device, or other system that was the source of this statement.
  ### 
  statementSource: -> 
    if @json['statementSource']
      for x in @json['statementSource'] 
        new QUICK.Entity(x)
       
  ###*
  The patient described by this statement.
  ### 
  subject: -> if @json['subject'] then new Patient( @json['subject'] )
 
 
  ###*
  The time span within which the condition will be reached. e.g., 10 years.
  ### 
  timePeriod: -> 
    if @json['timePeriod']
      for x in @json['timePeriod'] 
        new QUICK.Period(x)
       
  ###*
  The subject matter of this clinical statement. The topic may be an action like medication administration, or a finding or other observations about the patient.
  ### 
  topic: -> if @json['topic'] then new StatementTopic( @json['topic'] )
 
 

module.exports.PredictionOccurrence = PredictionOccurrence
