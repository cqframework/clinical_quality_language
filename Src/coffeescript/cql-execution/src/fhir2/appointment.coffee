
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
@class AppointmentParticipantComponent
@exports  AppointmentParticipantComponent as AppointmentParticipantComponent
###
class AppointmentParticipantComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Role of participant in the appointment.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  type: ->
    if @json['type']
      for item in @json['type']
        new CodeableConcept(item)
  
  ###*
  A Person of device that is participating in the appointment, usually Practitioner, Patient, RelatedPerson or Device.
  @returns {Reference}
  ###
  actor: -> if @json['actor'] then new Reference(@json['actor'])
  
  ###*
  Is this participant required to be present at the meeting. This covers a use-case where 2 doctors need to meet to discuss the results for a specific patient, and the patient is not required to be present.
  @returns {Array} an array of {@link String} objects
  ###
  required:-> @json['required']
  
  ###*
  Participation status of the Patient.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
###*
A scheduled healthcare event for a patient and/or practitioner(s) where a service may take place at a specific date/time.
@class Appointment
@exports Appointment as Appointment
###
class Appointment extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  This records identifiers associated with this appointment concern that are defined by business processed and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The priority of the appointment. Can be used to make informed decisions if needing to re-prioritize appointments. (The iCal Standard specifies 0 as undefined, 1 as highest, 9 as lowest priority) (Need to change back to CodeableConcept).
  @returns {Array} an array of {@link Number} objects
  ###
  priority:-> @json['priority']
  
  ###*
  Each of the participants has their own participation status which indicates their involvement in the process, however this status indicates the shared status.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The type of appointments that is being booked (ideally this would be an identifiable service - which is at a location, rather than the location itself).
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The reason that this appointment is being scheduled, this is more clinical than administrative.
  @returns {CodeableConcept}
  ###
  reason: -> if @json['reason'] then new CodeableConcept(@json['reason'])
  
  ###*
  The brief description of the appointment as would be shown on a subject line in a meeting request, or appointment list. Detailed or expanded information should be put in the comment field.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Date/Time that the appointment is to take place.
  @returns {Array} an array of {@link Date} objects
  ###
  start:-> if @json['start'] then DT.DateTime.parse(@json['start'])
  
  ###*
  Date/Time that the appointment is to conclude.
  @returns {Array} an array of {@link Date} objects
  ###
  end:-> if @json['end'] then DT.DateTime.parse(@json['end'])
  
  ###*
  The slot that this appointment is filling. If provided then the schedule will not be provided as slots are not recursive, and the start/end values MUST be the same as from the slot.
  @returns {Array} an array of {@link Reference} objects
  ###
  slot: ->
    if @json['slot']
      for item in @json['slot']
        new Reference(item)
  
  ###*
  The primary location that this appointment is to take place.
  @returns {Reference}
  ###
  location: -> if @json['location'] then new Reference(@json['location'])
  
  ###*
  Additional comments about the appointment.
  @returns {Array} an array of {@link String} objects
  ###
  comment:-> @json['comment']
  
  ###*
  An Order that lead to the creation of this appointment.
  @returns {Reference}
  ###
  order: -> if @json['order'] then new Reference(@json['order'])
  
  ###*
  List of participants involved in the appointment.
  @returns {Array} an array of {@link AppointmentParticipantComponent} objects
  ###
  participant: ->
    if @json['participant']
      for item in @json['participant']
        new AppointmentParticipantComponent(item)
  
  ###*
  Who recorded the appointment.
  @returns {Reference}
  ###
  lastModifiedBy: -> if @json['lastModifiedBy'] then new Reference(@json['lastModifiedBy'])
  
  ###*
  Date when the appointment was recorded.
  @returns {Array} an array of {@link Date} objects
  ###
  lastModified:-> if @json['lastModified'] then DT.DateTime.parse(@json['lastModified'])
  



module.exports.Appointment = Appointment
