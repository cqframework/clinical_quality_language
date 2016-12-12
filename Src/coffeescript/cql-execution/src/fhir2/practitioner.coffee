
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
@class PractitionerQualificationComponent
@exports  PractitionerQualificationComponent as PractitionerQualificationComponent
###
class PractitionerQualificationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  An identifier that applies to this person's qualification in this role.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Coded representation of the qualification.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  Period during which the qualification is valid.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  Organization that regulates and issues the qualification.
  @returns {Reference}
  ###
  issuer: -> if @json['issuer'] then new Reference(@json['issuer'])
  
###*
A person who is directly or indirectly involved in the provisioning of healthcare.
@class Practitioner
@exports Practitioner as Practitioner
###
class Practitioner extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  An identifier that applies to this person in this role.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  A name associated with the person.
  @returns {HumanName}
  ###
  name: -> if @json['name'] then new HumanName(@json['name'])
  
  ###*
  A contact detail for the practitioner, e.g. a telephone number or an email address.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  The postal address where the practitioner can be found or visited or to which mail can be delivered.
  @returns {Array} an array of {@link Address} objects
  ###
  address: ->
    if @json['address']
      for item in @json['address']
        new Address(item)
  
  ###*
  Administrative Gender - the gender that the person is considered to have for administration and record keeping purposes.
  @returns {Array} an array of {@link String} objects
  ###
  gender:-> @json['gender']
  
  ###*
  The date and time of birth for the practitioner.
  @returns {Array} an array of {@link Date} objects
  ###
  birthDate:-> if @json['birthDate'] then DT.DateTime.parse(@json['birthDate'])
  
  ###*
  Image of the person.
  @returns {Array} an array of {@link Attachment} objects
  ###
  photo: ->
    if @json['photo']
      for item in @json['photo']
        new Attachment(item)
  
  ###*
  The organization that the practitioner represents.
  @returns {Reference}
  ###
  organization: -> if @json['organization'] then new Reference(@json['organization'])
  
  ###*
  Roles which this practitioner is authorized to perform for the organization.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  role: ->
    if @json['role']
      for item in @json['role']
        new CodeableConcept(item)
  
  ###*
  Specific specialty of the practitioner.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  specialty: ->
    if @json['specialty']
      for item in @json['specialty']
        new CodeableConcept(item)
  
  ###*
  The period during which the person is authorized to act as a practitioner in these role(s) for the organization.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  The location(s) at which this practitioner provides care.
  @returns {Array} an array of {@link Reference} objects
  ###
  location: ->
    if @json['location']
      for item in @json['location']
        new Reference(item)
  
  ###*
  Qualifications obtained by training and certification.
  @returns {Array} an array of {@link PractitionerQualificationComponent} objects
  ###
  qualification: ->
    if @json['qualification']
      for item in @json['qualification']
        new PractitionerQualificationComponent(item)
  
  ###*
  A language the practitioner is able to use in patient communication.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  communication: ->
    if @json['communication']
      for item in @json['communication']
        new CodeableConcept(item)
  



module.exports.Practitioner = Practitioner
