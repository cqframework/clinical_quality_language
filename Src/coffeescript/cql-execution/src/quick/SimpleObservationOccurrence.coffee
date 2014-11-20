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
this.QUICK ||= {}
###*
An observation result that reports the value of a single observation, e.g., pulse rate, serum sodium result.

<b>Example</b>
<u>Expression from source knowledge artifact</u>
"Physical Exam, Finding: Systolic Blood Pressure (result &lt; 140 mmHg)" during MOST RECENT: "Encounter, Performed: Office Visit"

<u>Expression in CQL+QUICK</u>
let MostRecentOfficeVisit = 
       Last([Encounter, Performance: "Office Visit"] E sort by E.effectiveTime)

let LastBPNormal =
       [SimpleObservationResult: "Systolic Blood Pressure"] SBP
              with MostRecentOfficeVisit OV 
                     where SBP.observedAtTime during OV.performanceTime
              where SBP.value &lt; 140 u'mm[Hg]'
 
###
require './Period'
require './BodySite'
require './StatementOfOccurrence'
require './StatementTopic'
require './Specimen'
require './StatementModality'
require './Patient'
require './CodeableConcept'
require './Identifier'
require './RelatedObservation'
require './Element'
require './Person'
require './Entity'
###*
@class SimpleObservationOccurrence
@exports  SimpleObservationOccurrence as SimpleObservationOccurrence
###
class SimpleObservationOccurrence
  constructor: (@json) ->
 
  ###*
  Details about the clinical statement that were not represented at all or sufficiently in one of the attributes provided in a class. These may include for example a comment, an instruction, or a note associated with the statement.
  ### 
  additionalText: ->  @json['additionalText'] 
 
 
  ###*
  Indicates where on the subject's body the observation was made.
  ### 
  bodySite: -> 
    if @json['bodySite']
      for x in @json['bodySite'] 
        new QUICK.BodySite(x)
       
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
  The assessment made based on the result of the observation.
  ### 
  interpretation: -> 
    if @json['interpretation']
      for x in @json['interpretation'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The technique or mechanism used to perform the observation.
  ### 
  method: -> 
    if @json['method']
      for x in @json['method'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The modality of a Clinical Statement describes the way the topic exists, happens, or is experienced.
  ### 
  modality: -> if @json['modality'] then new QUICK.StatementModality( @json['modality'] )
 
 
  ###*
  Identifies what type of observation was performed. e.g., body temperature
  ### 
  name: -> if @json['name'] then new QUICK.CodeableConcept( @json['name'] )
 
 
  ###*
  Time when the observation was made by the statement author. This may be different than the time the observation was physically recorded (which may occur much later). This also is different time than when the observed phenomenon actually occurred. For example, a patient had a headache three days ago, but reported it to their physician today. The physician would record the observedAtTime as today.
  ### 
  observedAtTime: -> 
    if @json['observedAtTime']
      for x in @json['observedAtTime'] 
        new QUICK.Period(x)
       
  ###*
  An order placed by a provider that led to this observation result
  ### 
  order: -> 
    if @json['order']
      for x in @json['order'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The identifier of a set of constraints placed on a clinical statement.  If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  Observations related to this observation in some way, e.g., used to derive this observation, previous versions of this observation.

Related observations do not include components. Those are modeled in ObservationResultGroup. 
  ### 
  relatedObservation: -> 
    if @json['relatedObservation']
      for x in @json['relatedObservation'] 
        new QUICK.RelatedObservation(x)
       
  ###*
  An estimate of the degree to which quality issues have impacted on the value reported. e.g., result is ok, measurement still ongoing, results are questionable. Usually, unreliable results are not recorded, but that is not always possible. In such cases, this attribute makes the receiver aware of the quality of the result.
  ### 
  reliability: -> 
    if @json['reliability']
      for x in @json['reliability'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The specimen that was used when this observation was made.
Observations are not made on specimens themselves; they are made on a subject, but usually by the means of a specimen. Note that although specimens are often involved, they are not always tracked and reported explicitly. Also note that observation resources are often used in contexts that track the specimen explicity (e.g. Diagnostic Report).
  ### 
  specimen: -> 
    if @json['specimen']
      for x in @json['specimen'] 
        new QUICK.Specimen(x)
       
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
  The status of the result value. e.g., preliminary, final
  ### 
  status: -> 
    if @json['status']
      for x in @json['status'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The patient described by this statement.
  ### 
  subject: -> if @json['subject'] then new QUICK.Patient( @json['subject'] )
 
 
  ###*
  The subject matter of this clinical statement. The topic may be an action like medication administration, or a finding or other observations about the patient.
  ### 
  topic: -> if @json['topic'] then new QUICK.StatementTopic( @json['topic'] )
 
 
  ###*
  Method by which the observation result was validated, e.g., human review, sliding average.
  ### 
  validationMethod: -> 
    if @json['validationMethod']
      for x in @json['validationMethod'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The information determined as a result of making the observation. e.g., 120 mm Hg, small, 2013-11-30
  ### 
  value: -> 
    if @json['value']
      for x in @json['value'] 
        new QUICK.Element(x)
       

module.exports.SimpleObservationOccurrence = SimpleObservationOccurrence
