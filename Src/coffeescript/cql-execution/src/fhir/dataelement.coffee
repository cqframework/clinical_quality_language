
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
@class DataElementBindingComponent
@exports  DataElementBindingComponent as DataElementBindingComponent
###
class DataElementBindingComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  If true, then conformant systems may use additional codes or (where the data type permits) text alone to convey concepts not covered by the set of codes identified in the binding.  If false, then conformant systems are constrained to the provided codes alone.
  @returns {Array} an array of {@link boolean} objects
  ###
  isExtensible:-> @json['isExtensible']
  
  ###*
  Indicates the degree of conformance expectations associated with this binding.
  @returns {Array} an array of {@link String} objects
  ###
  conformance:-> @json['conformance']
  
  ###*
  Describes the intended use of this particular set of codes.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Points to the value set that identifies the set of codes to be used.
  @returns {Reference}
  ###
  valueSet: -> if @json['valueSet'] then new Reference(@json['valueSet'])
  

###* 
Embedded class
@class DataElementMappingComponent
@exports  DataElementMappingComponent as DataElementMappingComponent
###
class DataElementMappingComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A URI that identifies the specification that this mapping is expressed to.
  @returns {Array} an array of {@link String} objects
  ###
  uri:-> @json['uri']
  
  ###*
  If true, indicates that the official meaning of the data element is exactly equivalent to the mapped element.
  @returns {Array} an array of {@link boolean} objects
  ###
  definitional:-> @json['definitional']
  
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
  Expresses what part of the target specification corresponds to this element.
  @returns {Array} an array of {@link String} objects
  ###
  map:-> @json['map']
  
###*
The formal description of a single piece of information that can be gathered and reported.
@class DataElement
@exports DataElement as DataElement
###
class DataElement extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The identifier that is used to identify this data element when it is referenced in a Profile, Questionnaire or an instance.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  The identifier that is used to identify this version of the data element when it is referenced in a Profile, Questionnaire or instance. This is an arbitrary value managed by the definition author manually.
  @returns {Array} an array of {@link String} objects
  ###
  version:-> @json['version']
  
  ###*
  Details of the individual or organization who accepts responsibility for publishing the data element.
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
  The status of the data element.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The date that this version of the data element was published.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  The term used by humans to refer to the data element.  Should ideally be unique within the context in which the data element is expected to be used.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  A set of terms from external terminologies that may be used to assist with indexing and searching of data element definitions.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  category: ->
    if @json['category']
      for item in @json['category']
        new CodeableConcept(item)
  
  ###*
  A code that provides the meaning for a data element according to a particular terminology.
  @returns {Array} an array of {@link Coding} objects
  ###
  code: ->
    if @json['code']
      for item in @json['code']
        new Coding(item)
  
  ###*
  The default/suggested phrasing to use when prompting a human to capture the data element.
  @returns {Array} an array of {@link String} objects
  ###
  question:-> @json['question']
  
  ###*
  Provides a complete explanation of the meaning of the data element for human readability.
  @returns {Array} an array of {@link String} objects
  ###
  definition:-> @json['definition']
  
  ###*
  Comments about the use of the element, including notes about how to use the data properly, exceptions to proper use, etc.
  @returns {Array} an array of {@link String} objects
  ###
  comments:-> @json['comments']
  
  ###*
  Explains why this element is needed and why it's been constrained as it has.
  @returns {Array} an array of {@link String} objects
  ###
  requirements:-> @json['requirements']
  
  ###*
  Identifies additional names by which this element might also be known.
  @returns {Array} an array of {@link String} objects
  ###
  synonym:-> @json['synonym']
  
  ###*
  The FHIR data type that is the type for this element.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  An sample value for this element demonstrating the type of information that would typically be captured.
  @returns {Array} an array of {@link } objects
  ###
  example:-> @json['example']
  
  ###*
  Indicates the shortest length that SHALL be supported by conformant instances without truncation.
  @returns {Array} an array of {@link Number} objects
  ###
  maxLength:-> @json['maxLength']
  
  ###*
  Identifies the units of measure in which the data element should be captured or expressed.
  @returns {CodeableConcept}
  ###
  units: -> if @json['units'] then new CodeableConcept(@json['units'])
  
  ###*
  Binds to a value set if this element is coded (code, Coding, CodeableConcept).
  @returns {DataElementBindingComponent}
  ###
  binding: -> if @json['binding'] then new DataElementBindingComponent(@json['binding'])
  
  ###*
  Identifies a concept from an external specification that roughly corresponds to this element.
  @returns {Array} an array of {@link DataElementMappingComponent} objects
  ###
  mapping: ->
    if @json['mapping']
      for item in @json['mapping']
        new DataElementMappingComponent(item)
  



module.exports.DataElement = DataElement
