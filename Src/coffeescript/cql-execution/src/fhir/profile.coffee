
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
@class ProfileMappingComponent
@exports  ProfileMappingComponent as ProfileMappingComponent
###
class ProfileMappingComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  An Internal id that is used to identify this mapping set when specific mappings are made.
  @returns {Array} an array of {@link String} objects
  ###
  identity:-> @json['identity']
  
  ###*
  A URI that identifies the specification that this mapping is expressed to.
  @returns {Array} an array of {@link String} objects
  ###
  uri:-> @json['uri']
  
  ###*
  A name for the specification that is being mapped to.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Comments about this mapping, including version notes, issues, scope limitations, and other important notes for usage.
  @returns {Array} an array of {@link String} objects
  ###
  comments:-> @json['comments']
  

###* 
Embedded class
@class ConstraintComponent
@exports  ConstraintComponent as ConstraintComponent
###
class ConstraintComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Captures constraints on each element within the resource.
  @returns {Array} an array of {@link ElementDefinition} objects
  ###
  element: ->
    if @json['element']
      for item in @json['element']
        new ElementDefinition(item)
  
###*
A Resource Profile - a statement of use of one or more FHIR Resources.  It may include constraints on Resources and Data Types, Terminology Binding Statements and Extension Definitions.
@class Profile
@exports Profile as Profile
###
class Profile extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The URL at which this profile is (or will be) published, and which is used to reference this profile in extension urls and tag values in operational FHIR systems.
  @returns {Array} an array of {@link String} objects
  ###
  url:-> @json['url']
  
  ###*
  Formal identifier that is used to identify this profile when it is represented in other formats, or referenced in a specification, model, design or an instance  (should be globally unique OID, UUID, or URI), (if it's not possible to use the literal URI).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The identifier that is used to identify this version of the profile when it is referenced in a specification, model, design or instance. This is an arbitrary value managed by the profile author manually.
  @returns {Array} an array of {@link String} objects
  ###
  version:-> @json['version']
  
  ###*
  A free text natural language name identifying the Profile.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Details of the individual or organization who accepts responsibility for publishing the profile.
  @returns {Array} an array of {@link String} objects
  ###
  publisher:-> @json['publisher']
  
  ###*
  Contact details to assist a user in finding and communicating with the publisher.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  A free text natural language description of the profile and its use.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  A set of terms from external terminologies that may be used to assist with indexing and searching of templates.
  @returns {Array} an array of {@link Coding} objects
  ###
  code: ->
    if @json['code']
      for item in @json['code']
        new Coding(item)
  
  ###*
  The status of the profile.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  This profile was authored for testing purposes (or education/evaluation/marketing), and is not intended to be used for genuine usage.
  @returns {Array} an array of {@link boolean} objects
  ###
  experimental:-> @json['experimental']
  
  ###*
  The date that this version of the profile was published.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  The Scope and Usage that this profile was created to meet.
  @returns {Array} an array of {@link String} objects
  ###
  requirements:-> @json['requirements']
  
  ###*
  The version of the FHIR specification on which this profile is based - this is the formal version of the specification, without the revision number, e.g. [publication].[major].[minor], which is 0.3.0 for this version.
  @returns {Array} an array of {@link String} objects
  ###
  fhirVersion:-> @json['fhirVersion']
  
  ###*
  An external specification that the content is mapped to.
  @returns {Array} an array of {@link ProfileMappingComponent} objects
  ###
  mapping: ->
    if @json['mapping']
      for item in @json['mapping']
        new ProfileMappingComponent(item)
  
  ###*
  The Resource or Data type being described.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  The structure that is the base on which this set of constraints is derived from.
  @returns {Array} an array of {@link String} objects
  ###
  base:-> @json['base']
  
  ###*
  A snapshot view is expressed in a stand alone form that can be used and interpreted without considering the base profile.
  @returns {ConstraintComponent}
  ###
  snapshot: -> if @json['snapshot'] then new ConstraintComponent(@json['snapshot'])
  
  ###*
  A differential view is expressed relative to the base profile - a statement of differences that it applies.
  @returns {ConstraintComponent}
  ###
  differential: -> if @json['differential'] then new ConstraintComponent(@json['differential'])
  



module.exports.Profile = Profile
