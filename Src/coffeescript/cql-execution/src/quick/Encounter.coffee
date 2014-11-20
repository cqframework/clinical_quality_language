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
Description of an interaction between a patient and healthcare provider(s) for the purpose of providing healthcare service(s) or assessing the health status of a patient.
 
###
require './StatementOfOccurrence'
require './Quantity'
require './Schedule'
require './Hospitalization'
require './EncounterCondition'
require './Organization'
require './Location'
require './CodeableConcept'
###*
@class Encounter
@exports  Encounter as Encounter
###
class Encounter
  constructor: (@json) ->
 
  ###*
  Classification of the encounter. For example, inpatient, outpatient, virtual.

(see http://hl7.org/fhir/encounter-class for values)
  ### 
  class: ->  @json['class'] 
 
 
  ###*
  If the encounter is repeated, the frequency pattern for repetitions.
  ### 
  encounterSchedule: -> 
    if @json['encounterSchedule']
      for x in @json['encounterSchedule'] 
        new QUICK.Schedule(x)
       
  ###*
  Details about an admission to a clinic
  ### 
  hospitalization: -> 
    if @json['hospitalization']
      for x in @json['hospitalization'] 
        new QUICK.Hospitalization(x)
       
  ###*
  Quantity of time the encounter lasted.
  ### 
  length: -> 
    if @json['length']
      for x in @json['length'] 
        new QUICK.Quantity(x)
       
  ###*
  The location the encounter takes place, e.g., clinic location, hospital bed
  ### 
  location: -> 
    if @json['location']
      for x in @json['location'] 
        new QUICK.Location(x)
       
  ###*
  Another Encounter of which this encounter is a part of (administratively or in time).
  ### 
  partOf: -> 
    if @json['partOf']
      for x in @json['partOf'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The conditions considered and cared for within this encounter. This includes items such as reason for visit, admission diagnosis, discharge diagnosis, chief complaint. Do not use reason or indication in the Action class to specify encounter related diagnoses.
  ### 
  relatedCondition: -> 
    if @json['relatedCondition']
      for x in @json['relatedCondition'] 
        new QUICK.EncounterCondition(x)
       
  ###*
  Department or team providing care.
  ### 
  serviceProvider: -> 
    if @json['serviceProvider']
      for x in @json['serviceProvider'] 
        new QUICK.Organization(x)
       
  ###*
  The type of service provided during the encounter. For example, surgery, rehabilitation, annual physical exam.

Value Set http://hl7.org/fhir/vs/encounter-type
  ### 
  serviceType: -> 
    if @json['serviceType']
      for x in @json['serviceType'] 
        new QUICK.CodeableConcept(x)
       

module.exports.Encounter = Encounter
