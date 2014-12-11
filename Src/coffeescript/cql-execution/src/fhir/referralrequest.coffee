
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
Used to record and send details about a request for referral service or transfer of a patient to the care of another provider or provider organisation.
@class ReferralRequest
@exports ReferralRequest as ReferralRequest
###
class ReferralRequest extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The workflow status of the referral or transfer of care request.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Business Id that uniquely identifies the referral/care transfer request instance.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  An indication of the type of referral (or where applicable the type of transfer of care) request.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Indication of the clinical domain or discipline to which the referral or transfer of care request is sent.
  @returns {CodeableConcept}
  ###
  specialty: -> if @json['specialty'] then new CodeableConcept(@json['specialty'])
  
  ###*
  An indication of the urgency of referral (or where applicable the type of transfer of care) request.
  @returns {CodeableConcept}
  ###
  priority: -> if @json['priority'] then new CodeableConcept(@json['priority'])
  
  ###*
  The patient who is the subject of a referral or transfer of care request.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  The healthcare provider or provider organization who/which initaited the referral/transfer of care request. Can also be  Patient (a self referral).
  @returns {Reference}
  ###
  requester: -> if @json['requester'] then new Reference(@json['requester'])
  
  ###*
  The healthcare provider(s) or provider organization(s) who/which is to receive the referral/transfer of care request.
  @returns {Array} an array of {@link Reference} objects
  ###
  recipient: ->
    if @json['recipient']
      for item in @json['recipient']
        new Reference(item)
  
  ###*
  The encounter at which the request for referral or transfer of care is initiated.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  Date/DateTime the request for referral or transfer of care is sent by the author.
  @returns {Array} an array of {@link Date} objects
  ###
  dateSent:-> if @json['dateSent'] then DT.DateTime.parse(@json['dateSent'])
  
  ###*
  Description of clinical condition indicating why referral/transfer of care is requested.
  @returns {CodeableConcept}
  ###
  reason: -> if @json['reason'] then new CodeableConcept(@json['reason'])
  
  ###*
  The reason gives a short description of why the referral is being made, the description expands on this to support a more complete clinical summary.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  The service(s) that is/are requested to be provided to the patient.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  serviceRequested: ->
    if @json['serviceRequested']
      for item in @json['serviceRequested']
        new CodeableConcept(item)
  
  ###*
  Any additional (administrative, financial or clinical) information required to support request for referral or transfer of care.
  @returns {Array} an array of {@link Reference} objects
  ###
  supportingInformation: ->
    if @json['supportingInformation']
      for item in @json['supportingInformation']
        new Reference(item)
  
  ###*
  The period of time within which the services identified in the referral/transfer of care is specified or required to occur.
  @returns {Period}
  ###
  fulfillmentTime: -> if @json['fulfillmentTime'] then new Period(@json['fulfillmentTime'])
  



module.exports.ReferralRequest = ReferralRequest
