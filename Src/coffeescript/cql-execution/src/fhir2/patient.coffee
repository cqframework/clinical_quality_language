
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
@class ContactComponent
@exports  ContactComponent as ContactComponent
###
class ContactComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The nature of the relationship between the patient and the contact person.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  relationship: ->
    if @json['relationship']
      for item in @json['relationship']
        new CodeableConcept(item)
  
  ###*
  A name associated with the person.
  @returns {HumanName}
  ###
  name: -> if @json['name'] then new HumanName(@json['name'])
  
  ###*
  A contact detail for the person, e.g. a telephone number or an email address.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  Address for the contact person.
  @returns {Address}
  ###
  address: -> if @json['address'] then new Address(@json['address'])
  
  ###*
  Administrative Gender - the gender that the person is considered to have for administration and record keeping purposes.
  @returns {Array} an array of {@link String} objects
  ###
  gender:-> @json['gender']
  
  ###*
  Organization on behalf of which the contact is acting or for which the contact is working.
  @returns {Reference}
  ###
  organization: -> if @json['organization'] then new Reference(@json['organization'])
  
  ###*
  The period during which this person or organisation is valid to be contacted relating to this patient.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  

###* 
Embedded class
@class AnimalComponent
@exports  AnimalComponent as AnimalComponent
###
class AnimalComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the high level categorization of the kind of animal.
  @returns {CodeableConcept}
  ###
  species: -> if @json['species'] then new CodeableConcept(@json['species'])
  
  ###*
  Identifies the detailed categorization of the kind of animal.
  @returns {CodeableConcept}
  ###
  breed: -> if @json['breed'] then new CodeableConcept(@json['breed'])
  
  ###*
  Indicates the current state of the animal's reproductive organs.
  @returns {CodeableConcept}
  ###
  genderStatus: -> if @json['genderStatus'] then new CodeableConcept(@json['genderStatus'])
  

###* 
Embedded class
@class PatientLinkComponent
@exports  PatientLinkComponent as PatientLinkComponent
###
class PatientLinkComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The other patient resource that the link refers to.
  @returns {Reference}
  ###
  other: -> if @json['other'] then new Reference(@json['other'])
  
  ###*
  The type of link between this patient resource and another patient resource.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
###*
Demographics and other administrative information about a person or animal receiving care or other health-related services.
@class Patient
@exports Patient as Patient
###
class Patient extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  An identifier that applies to this person as a patient.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  A name associated with the individual.
  @returns {Array} an array of {@link HumanName} objects
  ###
  name: ->
    if @json['name']
      for item in @json['name']
        new HumanName(item)
  
  ###*
  A contact detail (e.g. a telephone number or an email address) by which the individual may be contacted.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  Administrative Gender - the gender that the patient is considered to have for administration and record keeping purposes.
  @returns {Array} an array of {@link String} objects
  ###
  gender:-> @json['gender']
  
  ###*
  The date and time of birth for the individual.
  @returns {Array} an array of {@link Date} objects
  ###
  birthDate:-> if @json['birthDate'] then DT.DateTime.parse(@json['birthDate'])
  
  ###*
  Indicates if the individual is deceased or not.
  @returns {Array} an array of {@link boolean} objects
  ###
  deceasedBoolean:-> @json['deceasedBoolean']
  ###*
  Indicates if the individual is deceased or not.
  @returns {Array} an array of {@link Date} objects
  ###
  deceasedDateTime:-> if @json['deceasedDateTime'] then DT.DateTime.parse(@json['deceasedDateTime'])
  
  ###*
  Addresses for the individual.
  @returns {Array} an array of {@link Address} objects
  ###
  address: ->
    if @json['address']
      for item in @json['address']
        new Address(item)
  
  ###*
  This field contains a patient's most recent marital (civil) status.
  @returns {CodeableConcept}
  ###
  maritalStatus: -> if @json['maritalStatus'] then new CodeableConcept(@json['maritalStatus'])
  
  ###*
  Indicates whether the patient is part of a multiple or indicates the actual birth order.
  @returns {Array} an array of {@link boolean} objects
  ###
  multipleBirthBoolean:-> @json['multipleBirthBoolean']
  ###*
  Indicates whether the patient is part of a multiple or indicates the actual birth order.
  @returns {Array} an array of {@link Number} objects
  ###
  multipleBirthInteger:-> @json['multipleBirthInteger']
  
  ###*
  Image of the person.
  @returns {Array} an array of {@link Attachment} objects
  ###
  photo: ->
    if @json['photo']
      for item in @json['photo']
        new Attachment(item)
  
  ###*
  A contact party (e.g. guardian, partner, friend) for the patient.
  @returns {Array} an array of {@link ContactComponent} objects
  ###
  contact: ->
    if @json['contact']
      for item in @json['contact']
        new ContactComponent(item)
  
  ###*
  This element has a value if the patient is an animal.
  @returns {AnimalComponent}
  ###
  animal: -> if @json['animal'] then new AnimalComponent(@json['animal'])
  
  ###*
  Languages which may be used to communicate with the patient about his or her health.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  communication: ->
    if @json['communication']
      for item in @json['communication']
        new CodeableConcept(item)
  
  ###*
  Patient's nominated care provider.
  @returns {Array} an array of {@link Reference} objects
  ###
  careProvider: ->
    if @json['careProvider']
      for item in @json['careProvider']
        new Reference(item)
  
  ###*
  Organization that is the custodian of the patient record.
  @returns {Reference}
  ###
  managingOrganization: -> if @json['managingOrganization'] then new Reference(@json['managingOrganization'])
  
  ###*
  Link to another patient resource that concerns the same actual person.
  @returns {Array} an array of {@link PatientLinkComponent} objects
  ###
  link: ->
    if @json['link']
      for item in @json['link']
        new PatientLinkComponent(item)
  
  ###*
  Whether this patient record is in active use.
  @returns {Array} an array of {@link boolean} objects
  ###
  active:-> @json['active']
  



module.exports.Patient = Patient
