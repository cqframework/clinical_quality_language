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
###*
A formally or informally recognized grouping of people or organizations formed for the purpose of achieving some form of collective action. Includes companies, institutions, corporations, departments, community groups, healthcare practice groups, etc.
 
###
require './Contact'
require './Address'
require './EntityCharacteristic'
require './CodeableConcept'
require './Identifier'
###*
@class Organization
@exports  Organization as Organization
###
class Organization
  constructor: (@json) ->
 
  ###*
  The place or the name of the place where a organization is located or may be reached.
  ### 
  address: -> 
    if @json['address']
      for x in @json['address'] 
        new QUICK.Address(x)
       
  ###*
  The characteristics of this entity.
  ### 
  characteristic: -> 
    if @json['characteristic']
      for x in @json['characteristic'] 
        new QUICK.EntityCharacteristic(x)
       
  ###*
  <font color="#0f0f0f">The entity's unique identifier.  Used for internal tracking purposes.  It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system.  Does not need to be the entity's "real" identifier. </font>
  ### 
  id: -> 
    if @json['id']
      for x in @json['id'] 
        new QUICK.Identifier(x)
       
  ###*
  A name by which the organization is known.
  ### 
  name: ->  @json['name'] 
 
 
  ###*
  The identifier of a set of constraints placed on an Entity. If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  A locatable resource of the organization such as a web page, a telephone number (voice, fax or some other resource mediated by telecommunication equipment), an e-mail address, or any other locatable resource.
  ### 
  telecom: -> 
    if @json['telecom']
      for x in @json['telecom'] 
        new QUICK.Contact(x)
       
  ###*
  The kind of organization that this is., e.g., hospital, long-term care facility, hospital department, government agency, educational institution.
  ### 
  type: -> 
    if @json['type']
      for x in @json['type'] 
        new QUICK.CodeableConcept(x)
       

module.exports.Organization = Organization
