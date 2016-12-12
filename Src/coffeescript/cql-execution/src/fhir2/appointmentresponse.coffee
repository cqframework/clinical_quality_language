
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
A reply to an appointment request for a patient and/or practitioner(s), such as a confirmation or rejection.
@class AppointmentResponse
@exports AppointmentResponse as AppointmentResponse
###
class AppointmentResponse extends DomainResource
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
  Parent appointment that this response is replying to.
  @returns {Reference}
  ###
  appointment: -> if @json['appointment'] then new Reference(@json['appointment'])
  
  ###*
  Role of participant in the appointment.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  participantType: ->
    if @json['participantType']
      for item in @json['participantType']
        new CodeableConcept(item)
  
  ###*
  A Person of device that is participating in the appointment, usually Practitioner, Patient, RelatedPerson or Device.
  @returns {Array} an array of {@link Reference} objects
  ###
  individual: ->
    if @json['individual']
      for item in @json['individual']
        new Reference(item)
  
  ###*
  Participation status of the Patient.
  @returns {Array} an array of {@link String} objects
  ###
  participantStatus:-> @json['participantStatus']
  
  ###*
  Additional comments about the appointment.
  @returns {Array} an array of {@link String} objects
  ###
  comment:-> @json['comment']
  
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
  Who recorded the appointment response.
  @returns {Reference}
  ###
  lastModifiedBy: -> if @json['lastModifiedBy'] then new Reference(@json['lastModifiedBy'])
  
  ###*
  Date when the response was recorded or last updated.
  @returns {Array} an array of {@link Date} objects
  ###
  lastModified:-> if @json['lastModified'] then DT.DateTime.parse(@json['lastModified'])
  



module.exports.AppointmentResponse = AppointmentResponse
