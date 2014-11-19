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
Demographics and other administrative information about a person receiving care or other health-related services.

The data in the element covers the "who" information about the patient: it's attributes are focused on the demographic information necessary to support the administrative, financial and logistic procedures.
 
###
require './Contact'
require './HumanName'
require './Address'
require './EntityCharacteristic'
require './CodeableConcept'
require './Identifier'
###*
@class Patient
@exports  Patient as quick.Patient
###
class QUICK.Patient
  constructor: (@json) ->
 
  ###*
  The place or the name of the place where a person is located or may be reached.
  ### 
  address: -> 
    if @json['address']
      for x in @json['address'] 
        new QUICK.Address(x)
       
  ###*
  The date and time of birth for the individual.
  ### 
  birthTime: ->  @json['birthTime'] 
 
 
  ###*
  The characteristics of this entity.
  ### 
  characteristic: -> 
    if @json['characteristic']
      for x in @json['characteristic'] 
        new QUICK.EntityCharacteristic(x)
       
  ###*
  The person's ethnicity.  An ethnicity or ethnic group is a group of people whose members identify with each other through a common heritage.  E.g., Hispanic.
  ### 
  ethnicity: -> 
    if @json['ethnicity']
      for x in @json['ethnicity'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Administrative Gender - the gender that the patient is considered to have for administration and record keeping purposes.
  ### 
  gender: -> 
    if @json['gender']
      for x in @json['gender'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  <font color="#0f0f0f">The entity's unique identifier.  Used for internal tracking purposes.  It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system.  Does not need to be the entity's "real" identifier. </font>
  ### 
  id: -> 
    if @json['id']
      for x in @json['id'] 
        new QUICK.Identifier(x)
       
  ###*
  Whether the patient is deceased.
  ### 
  isDeceased: ->  @json['isDeceased'] 
 
 
  ###*
  Languages which may be used to communicate with this person.
  ### 
  languages: -> 
    if @json['languages']
      for x in @json['languages'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The patient's most recent marital (civil) status.
  ### 
  maritalStatus: -> 
    if @json['maritalStatus']
      for x in @json['maritalStatus'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  A name by which the patient is known.
  ### 
  name: -> 
    if @json['name']
      for x in @json['name'] 
        new QUICK.HumanName(x)
       
  ###*
  The person's language of preference.  E.g., English.
  ### 
  preferredLanguage: -> 
    if @json['preferredLanguage']
      for x in @json['preferredLanguage'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The identifier of a set of constraints placed on an Entity. If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  The person's race.  Race is a classification of humans into large groups by various factors, such as heritable phenotypic characteristics or geographic ancestry.  E.g., White, Asian.
  ### 
  race: -> 
    if @json['race']
      for x in @json['race'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  A locatable resource of a person such as a web page, a telephone number (voice, fax or some other resource mediated by telecommunication equipment), an e-mail address, or any other locatable resource.
  ### 
  telecom: -> 
    if @json['telecom']
      for x in @json['telecom'] 
        new QUICK.Contact(x)
       
  ###*
  The time when the patient died.
  ### 
  timeOfDeath: ->  @json['timeOfDeath'] 
 
 

module.exports.QUICK = QUICK
