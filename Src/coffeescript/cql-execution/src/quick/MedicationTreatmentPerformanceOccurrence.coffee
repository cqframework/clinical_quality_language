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
The performance of a medication treatment action. This statement can describes the action of administering one dose, administering a course of medication, or the dispensing of medication. The attribute actionPerformed (in Performance) can distinguish these variants of the statement.

<b>Example 1</b>
<u>Expression from source knowledge artifact</u>
"Medication, Administered: Hospital measures-IV Vancomycin (route: "Hospital measures-Route IV")" 

<u>Expression in CQL+QUICK</u>
let VancoDoseAdmin =
  [MedicationTreatment, Performance: "Hospital measures-IV Vancomycin"] V 
    where
    dosage[1].route in "Hospital measures-Route IV"
      and IsEquivalent(actionPerformed, DoseAdministrationActionCode)
       
<b>Example 2</b>
<u>Expression from source knowledge artifact</u>
Medication dispensed: Medications indicative of diabetes

<u>Expression in CQL+QUICK</u>
let DiabetesMedsDispensed =
  [MedicationTreatment, Performance: "Medications indicative of diabetes"] M 
    where
      IsEquivalent(actionPerformed, MedicationDispensedActionCode)
 
###
require './Period'
require './Dispense'
require './StatementOfOccurrence'
require './StatementTopic'
require './Participant'
require './Medication'
require './Indication'
require './ActionStatus'
require './StatementModality'
require './Dosage'
require './Patient'
require './CodeableConcept'
require './Identifier'
require './Person'
require './Entity'
###*
@class MedicationTreatmentPerformanceOccurrence
@exports  MedicationTreatmentPerformanceOccurrence as MedicationTreatmentPerformanceOccurrence
###
class MedicationTreatmentPerformanceOccurrence
  constructor: (@json) ->
 
  ###*
  Attribute that specifies the component of a composite action that was performed. For instance, the fulfillment of a prescription may result in both a substance administration event and a dispense event, thus resulting in two actions being performed.

let VancoDoseAdmin =
  [MedicationTreatment, Performance: "Hospital measures-IV Vancomycin"] V 
    where
    dosage[1].route in "Hospital measures-Route IV"
      and IsEquivalent(actionPerformed, DoseAdministrationActionCode)
  ### 
  actionPerformed: -> 
    if @json['actionPerformed']
      for x in @json['actionPerformed'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Details about the clinical statement that were not represented at all or sufficiently in one of the attributes provided in a class. These may include for example a comment, an instruction, or a note associated with the statement.
  ### 
  additionalText: ->  @json['additionalText'] 
 
 
  ###*
  The status of an action. It is expected that the range of values for statusCode (i.e., the value set) will vary by the subtypes of Action. For example, Proposal might have one of its status value as Declined.
  ### 
  currentStatus: -> 
    if @json['currentStatus']
      for x in @json['currentStatus'] 
        new QUICK.ActionStatus(x)
       
  ###*
  Dispensation details to be used only when needed, e.g., as part of a statement about a prescription or a dispensation event.
  ### 
  dispense: -> 
    if @json['dispense']
      for x in @json['dispense'] 
        new QUICK.Dispense(x)
       
  ###*
  Details for the dose or doses of medication administered or to be administered to the patient
  ### 
  dosage: -> 
    if @json['dosage']
      for x in @json['dosage'] 
        new QUICK.Dosage(x)
       
  ###*
  Identifies a plan that is partly or wholly enacted by the performance of this act
  ### 
  enactsPlan: -> 
    if @json['enactsPlan']
      for x in @json['enactsPlan'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The encounter within which the clinical statement was created.
  ### 
  encounter: -> 
    if @json['encounter']
      for x in @json['encounter'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  Identifies a proposal that led to this order.
  ### 
  fromProposal: -> 
    if @json['fromProposal']
      for x in @json['fromProposal'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  Identifies an order that is partly or wholly filled by the performance of this act
  ### 
  fulfillsOrder: -> 
    if @json['fulfillsOrder']
      for x in @json['fulfillsOrder'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  <font color="#0f0f0f">A unique ID of this clinical statement for reference purposes. It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system. Does not need to be the actual ID of the source system. </font>
  ### 
  identifier: -> 
    if @json['identifier']
      for x in @json['identifier'] 
        new QUICK.Identifier(x)
       
  ###*
  Reason or justification for the action. Reasons may also be specified for not performing an action. 
  ### 
  indication: -> 
    if @json['indication']
      for x in @json['indication'] 
        new QUICK.Indication(x)
       
  ###*
  Identifies the medication being dispensed or administered.
  ### 
  medication: -> 
    if @json['medication']
      for x in @json['medication'] 
        new QUICK.Medication(x)
       
  ###*
  The modality of a Clinical Statement describes the way the topic exists, happens, or is experienced.
  ### 
  modality: -> if @json['modality'] then new QUICK.StatementModality( @json['modality'] )
 
 
  ###*
  Preferences are choices made by patients about options for care or treatment (including scheduling, care experience, and meeting of personal health goals) and the sharing and disclosure of their health information.
  ### 
  patientPreference: -> 
    if @json['patientPreference']
      for x in @json['patientPreference'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The overall time period in which the action is performed. This may be different than the scheduled time or the expected performance time. Time for different activities performed within this action can be specified as subTasks.
  ### 
  performanceTime: -> 
    if @json['performanceTime']
      for x in @json['performanceTime'] 
        new QUICK.Period(x)
       
  ###*
  The persons who perform this action, e.g., the person who administered the medication, performed the surgery. 

A performance may have many participants In comparison, an order or a plan has one participant typically. Hence, in performance many participants can be described along with their specific roles.
  ### 
  performedBy: -> 
    if @json['performedBy']
      for x in @json['performedBy'] 
        new QUICK.Participant(x)
       
  ###*
  The identifier of a set of constraints placed on a clinical statement.  If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  Provider preferences are choices made by care providers relative to options for care or treatment (including scheduling, care experience, and meeting of personal health goals).
  ### 
  providerPreference: -> 
    if @json['providerPreference']
      for x in @json['providerPreference'] 
        new QUICK.CodeableConcept(x)
       
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
  The past statuses of this action, e.g., an order may evolve from draft to placed to in progress to completed or canceled.
  ### 
  statusHistory: -> 
    if @json['statusHistory']
      for x in @json['statusHistory'] 
        new QUICK.ActionStatus(x)
       
  ###*
  The patient described by this statement.
  ### 
  subject: -> if @json['subject'] then new QUICK.Patient( @json['subject'] )
 
 
  ###*
  The subject matter of this clinical statement. The topic may be an action like medication administration, or a finding or other observations about the patient.
  ### 
  topic: -> if @json['topic'] then new QUICK.StatementTopic( @json['topic'] )
 
 

module.exports.MedicationTreatmentPerformanceOccurrence = MedicationTreatmentPerformanceOccurrence
