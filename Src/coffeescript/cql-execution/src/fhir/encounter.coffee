
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
@class EncounterParticipantComponent
@exports  EncounterParticipantComponent as EncounterParticipantComponent
###
class EncounterParticipantComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Role of participant in encounter.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  type: ->
    if @json['type']
      for item in @json['type']
        new CodeableConcept(item)
  
  ###*
  Persons involved in the encounter other than the patient.
  @returns {Reference}
  ###
  individual: -> if @json['individual'] then new Reference(@json['individual'])
  

###* 
Embedded class
@class EncounterHospitalizationAccomodationComponent
@exports  EncounterHospitalizationAccomodationComponent as EncounterHospitalizationAccomodationComponent
###
class EncounterHospitalizationAccomodationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The bed that is assigned to the patient.
  @returns {Reference}
  ###
  bed: -> if @json['bed'] then new Reference(@json['bed'])
  
  ###*
  Period during which the patient was assigned the bed.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  

###* 
Embedded class
@class EncounterHospitalizationComponent
@exports  EncounterHospitalizationComponent as EncounterHospitalizationComponent
###
class EncounterHospitalizationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Pre-admission identifier.
  @returns {Identifier}
  ###
  preAdmissionIdentifier: -> if @json['preAdmissionIdentifier'] then new Identifier(@json['preAdmissionIdentifier'])
  
  ###*
  The location from which the patient came before admission.
  @returns {Reference}
  ###
  origin: -> if @json['origin'] then new Reference(@json['origin'])
  
  ###*
  From where patient was admitted (physician referral, transfer).
  @returns {CodeableConcept}
  ###
  admitSource: -> if @json['admitSource'] then new CodeableConcept(@json['admitSource'])
  
  ###*
  Period during which the patient was admitted.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  Where the patient stays during this encounter.
  @returns {Array} an array of {@link EncounterHospitalizationAccomodationComponent} objects
  ###
  accomodation: ->
    if @json['accomodation']
      for item in @json['accomodation']
        new EncounterHospitalizationAccomodationComponent(item)
  
  ###*
  Dietary restrictions for the patient.
  @returns {CodeableConcept}
  ###
  diet: -> if @json['diet'] then new CodeableConcept(@json['diet'])
  
  ###*
  Special courtesies (VIP, board member).
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  specialCourtesy: ->
    if @json['specialCourtesy']
      for item in @json['specialCourtesy']
        new CodeableConcept(item)
  
  ###*
  Wheelchair, translator, stretcher, etc.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  specialArrangement: ->
    if @json['specialArrangement']
      for item in @json['specialArrangement']
        new CodeableConcept(item)
  
  ###*
  Location to which the patient is discharged.
  @returns {Reference}
  ###
  destination: -> if @json['destination'] then new Reference(@json['destination'])
  
  ###*
  Category or kind of location after discharge.
  @returns {CodeableConcept}
  ###
  dischargeDisposition: -> if @json['dischargeDisposition'] then new CodeableConcept(@json['dischargeDisposition'])
  
  ###*
  The final diagnosis given a patient before release from the hospital after all testing, surgery, and workup are complete.
  @returns {Reference}
  ###
  dischargeDiagnosis: -> if @json['dischargeDiagnosis'] then new Reference(@json['dischargeDiagnosis'])
  
  ###*
  Whether this hospitalization is a readmission.
  @returns {Array} an array of {@link boolean} objects
  ###
  reAdmission:-> @json['reAdmission']
  

###* 
Embedded class
@class EncounterLocationComponent
@exports  EncounterLocationComponent as EncounterLocationComponent
###
class EncounterLocationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The location where the encounter takes place.
  @returns {Reference}
  ###
  location: -> if @json['location'] then new Reference(@json['location'])
  
  ###*
  Time period during which the patient was present at the location.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
###*
An interaction between a patient and healthcare provider(s) for the purpose of providing healthcare service(s) or assessing the health status of a patient.
@class Encounter
@exports Encounter as Encounter
###
class Encounter extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Identifier(s) by which this encounter is known.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  planned | in progress | onleave | finished | cancelled.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  inpatient | outpatient | ambulatory | emergency +.
  @returns {Array} an array of {@link String} objects
  ###
  class:-> @json['class']
  
  ###*
  Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing, rehabilitation).
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  type: ->
    if @json['type']
      for item in @json['type']
        new CodeableConcept(item)
  
  ###*
  The patient present at the encounter.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The main practitioner responsible for providing the service.
  @returns {Array} an array of {@link EncounterParticipantComponent} objects
  ###
  participant: ->
    if @json['participant']
      for item in @json['participant']
        new EncounterParticipantComponent(item)
  
  ###*
  The appointment that scheduled this encounter.
  @returns {Reference}
  ###
  fulfills: -> if @json['fulfills'] then new Reference(@json['fulfills'])
  
  ###*
  The start and end time of the encounter.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  Quantity of time the encounter lasted. This excludes the time during leaves of absence.
  @returns {Duration}
  ###
  length: -> if @json['length'] then new Duration(@json['length'])
  
  ###*
  Reason the encounter takes place, expressed as a code. For admissions, this can be used for a coded admission diagnosis.
  @returns {CodeableConcept}
  ###
  reason: -> if @json['reason'] then new CodeableConcept(@json['reason'])
  
  ###*
  Reason the encounter takes place, as specified using information from another resource. For admissions, this is the admission diagnosis.
  @returns {Reference}
  ###
  indication: -> if @json['indication'] then new Reference(@json['indication'])
  
  ###*
  Indicates the urgency of the encounter.
  @returns {CodeableConcept}
  ###
  priority: -> if @json['priority'] then new CodeableConcept(@json['priority'])
  
  ###*
  Details about an admission to a clinic.
  @returns {EncounterHospitalizationComponent}
  ###
  hospitalization: -> if @json['hospitalization'] then new EncounterHospitalizationComponent(@json['hospitalization'])
  
  ###*
  List of locations at which the patient has been.
  @returns {Array} an array of {@link EncounterLocationComponent} objects
  ###
  location: ->
    if @json['location']
      for item in @json['location']
        new EncounterLocationComponent(item)
  
  ###*
  Department or team providing care.
  @returns {Reference}
  ###
  serviceProvider: -> if @json['serviceProvider'] then new Reference(@json['serviceProvider'])
  
  ###*
  Another Encounter of which this encounter is a part of (administratively or in time).
  @returns {Reference}
  ###
  partOf: -> if @json['partOf'] then new Reference(@json['partOf'])
  



module.exports.Encounter = Encounter
