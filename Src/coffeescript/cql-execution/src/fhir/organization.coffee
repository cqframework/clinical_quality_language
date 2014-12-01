
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
@class OrganizationContactComponent
@exports  OrganizationContactComponent as OrganizationContactComponent
###
class OrganizationContactComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates a purpose for which the contact can be reached.
  @returns {CodeableConcept}
  ###
  purpose: -> if @json['purpose'] then new CodeableConcept(@json['purpose'])
  
  ###*
  A name associated with the contact.
  @returns {HumanName}
  ###
  name: -> if @json['name'] then new HumanName(@json['name'])
  
  ###*
  A contact detail (e.g. a telephone number or an email address) by which the party may be contacted.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  Visiting or postal addresses for the contact.
  @returns {Address}
  ###
  address: -> if @json['address'] then new Address(@json['address'])
  
  ###*
  Administrative Gender - the gender that the person is considered to have for administration and record keeping purposes.
  @returns {Array} an array of {@link String} objects
  ###
  gender:-> @json['gender']
  
###*
A formally or informally recognized grouping of people or organizations formed for the purpose of achieving some form of collective action.  Includes companies, institutions, corporations, departments, community groups, healthcare practice groups, etc.
@class Organization
@exports Organization as Organization
###
class Organization extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Identifier for the organization that is used to identify the organization across multiple disparate systems.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  A name associated with the organization.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  The kind of organization that this is.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  A contact detail for the organization.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  An address for the organization.
  @returns {Array} an array of {@link Address} objects
  ###
  address: ->
    if @json['address']
      for item in @json['address']
        new Address(item)
  
  ###*
  The organization of which this organization forms a part.
  @returns {Reference}
  ###
  partOf: -> if @json['partOf'] then new Reference(@json['partOf'])
  
  ###*
  Contact for the organization for a certain purpose.
  @returns {Array} an array of {@link OrganizationContactComponent} objects
  ###
  contact: ->
    if @json['contact']
      for item in @json['contact']
        new OrganizationContactComponent(item)
  
  ###*
  Location(s) the organization uses to provide services.
  @returns {Array} an array of {@link Reference} objects
  ###
  location: ->
    if @json['location']
      for item in @json['location']
        new Reference(item)
  
  ###*
  Whether the organization's record is still in active use.
  @returns {Array} an array of {@link boolean} objects
  ###
  active:-> @json['active']
  



module.exports.Organization = Organization
