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
@namespacing scoping into the FHIR namespace
###
require './core'
require './element'
require './resource'

###* 
 Embedded class
@class ProfileMappingComponent
@exports  ProfileMappingComponent as ProfileMappingComponent
###
class ProfileMappingComponent extends Element
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
@class ElementSlicingComponent
@exports  ElementSlicingComponent as ElementSlicingComponent
###
class ElementSlicingComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Designates which child element is used to discriminate between the slices when processing an instance. The value of the child element in the instance SHALL completely distinguish which slice the element in the resource matches based on the allowed values for that element in each of the slices.
  @returns {Array} an array of {@link String} objects
  ###
  discriminator:-> @json['discriminator']
  
  ###*
  If the matching elements have to occur in the same order as defined in the profile.
  @returns {Array} an array of {@link boolean} objects
  ###
  ordered:-> @json['ordered']
  
  ###*
  Whether additional slices are allowed or not. When the slices are ordered, profile authors can also say that additional slices are only allowed at the end.
  @returns {Array} an array of {@link String} objects
  ###
  rules:-> @json['rules']
  

###* 
 Embedded class
@class TypeRefComponent
@exports  TypeRefComponent as TypeRefComponent
###
class TypeRefComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Name of Data type or Resource.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  Identifies a profile that SHALL hold for resources or datatypes referenced as the type of this element. Can be a local reference - to another structure in this profile, or a reference to a structure in another profile.
  @returns {Array} an array of {@link String} objects
  ###
  profile:-> @json['profile']
  
  ###*
  If the type is a reference to another resource, how the resource is or can be aggreated - is it a contained resource, or a reference, and if the context is a bundle, is it included in the bundle.
  @returns {Array} an array of {@link String} objects
  ###
  aggregation:-> @json['aggregation']
  

###* 
 Embedded class
@class ElementDefinitionConstraintComponent
@exports  ElementDefinitionConstraintComponent as ElementDefinitionConstraintComponent
###
class ElementDefinitionConstraintComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Allows identification of which elements have their cardinalities impacted by the constraint.  Will not be referenced for constraints that do not affect cardinality.
  @returns {Array} an array of {@link String} objects
  ###
  key:-> @json['key']
  
  ###*
  Used to label the constraint in OCL or in short displays incapable of displaying the full human description.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Identifies the impact constraint violation has on the conformance of the instance.
  @returns {Array} an array of {@link String} objects
  ###
  severity:-> @json['severity']
  
  ###*
  Text that can be used to describe the constraint in messages identifying that the constraint has been violated.
  @returns {Array} an array of {@link String} objects
  ###
  human:-> @json['human']
  
  ###*
  XPath expression of constraint.
  @returns {Array} an array of {@link String} objects
  ###
  xpath:-> @json['xpath']
  

###* 
 Embedded class
@class ElementDefinitionBindingComponent
@exports  ElementDefinitionBindingComponent as ElementDefinitionBindingComponent
###
class ElementDefinitionBindingComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  A descriptive name for this - can be useful for generating implementation artifacts.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
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
  Points to the value set or external definition that identifies the set of codes to be used.
  @returns {Array} an array of {@link String} objects
  ###
  referenceUri:-> @json['referenceUri']
  ###*
  Points to the value set or external definition that identifies the set of codes to be used.
  @returns {Reference}
  ###
  referenceReference: -> if @json['referenceReference'] then new Reference(@json['referenceReference'])
  

###* 
 Embedded class
@class ElementDefinitionMappingComponent
@exports  ElementDefinitionMappingComponent as ElementDefinitionMappingComponent
###
class ElementDefinitionMappingComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  An internal reference to the definition of a mapping.
  @returns {Array} an array of {@link String} objects
  ###
  identity:-> @json['identity']
  
  ###*
  Expresses what part of the target specification corresponds to this element.
  @returns {Array} an array of {@link String} objects
  ###
  map:-> @json['map']
  

###* 
 Embedded class
@class ElementDefinitionComponent
@exports  ElementDefinitionComponent as ElementDefinitionComponent
###
class ElementDefinitionComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  A concise definition that  is shown in the generated XML format that summarizes profiles (used throughout the specification).
  @returns {Array} an array of {@link String} objects
  ###
  short:-> @json['short']
  
  ###*
  The definition SHALL be consistent with the base definition, but convey the meaning of the element in the particular context of use of the resource.
  @returns {Array} an array of {@link String} objects
  ###
  formal:-> @json['formal']
  
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
  The minimum number of times this element SHALL appear in the instance.
  @returns {Array} an array of {@link Number} objects
  ###
  min:-> @json['min']
  
  ###*
  The maximum number of times this element is permitted to appear in the instance.
  @returns {Array} an array of {@link String} objects
  ###
  max:-> @json['max']
  
  ###*
  The data type or resource that the value of this element is permitted to be.
  @returns {Array} an array of {@link TypeRefComponent} objects
  ###
  fhirType: ->
    if @json['fhirType']
      for item in @json['fhirType']
        new TypeRefComponent(item)
  
  ###*
  Identifies the name of a slice defined elsewhere in the profile whose constraints should be applied to the current element.
  @returns {Array} an array of {@link String} objects
  ###
  nameReference:-> @json['nameReference']
  
  ###*
  Specifies a primitive value that SHALL hold for this element in the instance.
  @returns {Array} an array of {@link } objects
  ###
  value:-> @json['value']
  
  ###*
  An example value for this element.
  @returns {Array} an array of {@link } objects
  ###
  example:-> @json['example']
  
  ###*
  Indicates the shortest length that SHALL be supported by conformant instances without truncation.
  @returns {Array} an array of {@link Number} objects
  ###
  maxLength:-> @json['maxLength']
  
  ###*
  A reference to an invariant that may make additional statements about the cardinality or value in the instance.
  @returns {Array} an array of {@link String} objects
  ###
  condition:-> @json['condition']
  
  ###*
  Formal constraints such as co-occurrence and other constraints that can be computationally evaluated within the context of the instance.
  @returns {Array} an array of {@link ElementDefinitionConstraintComponent} objects
  ###
  constraint: ->
    if @json['constraint']
      for item in @json['constraint']
        new ElementDefinitionConstraintComponent(item)
  
  ###*
  If true, conformant resource authors SHALL be capable of providing a value for the element and resource consumers SHALL be capable of extracting and doing something useful with the data element.  If false, the element may be ignored and not supported.
  @returns {Array} an array of {@link boolean} objects
  ###
  mustSupport:-> @json['mustSupport']
  
  ###*
  If true, the value of this element affects the interpretation of the element or resource that contains it, and the value of the element cannot be ignored. Typically, this is used for status, negation and qualification codes. The effect of this is that the element cannot be ignored by systems: they SHALL either recognize the element and process it, and/or a pre-determination has been made that it is not relevant to their particular system.
  @returns {Array} an array of {@link boolean} objects
  ###
  isModifier:-> @json['isModifier']
  
  ###*
  Binds to a value set if this element is coded (code, Coding, CodeableConcept).
  @returns {ElementDefinitionBindingComponent}
  ###
  binding: -> if @json['binding'] then new ElementDefinitionBindingComponent(@json['binding'])
  
  ###*
  Identifies a concept from an external specification that roughly corresponds to this element.
  @returns {Array} an array of {@link ElementDefinitionMappingComponent} objects
  ###
  mapping: ->
    if @json['mapping']
      for item in @json['mapping']
        new ElementDefinitionMappingComponent(item)
  

###* 
 Embedded class
@class ElementComponent
@exports  ElementComponent as ElementComponent
###
class ElementComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The path identifies the element and is expressed as a "."-separated list of ancestor elements, beginning with the name of the resource.
  @returns {Array} an array of {@link String} objects
  ###
  path:-> @json['path']
  
  ###*
  Codes that define how this element is represented in instances, when the deviation varies from the normal case.
  @returns {Array} an array of {@link String} objects
  ###
  representation:-> @json['representation']
  
  ###*
  The name of this element definition (to refer to it from other element definitions using Profile.structure.snapshot.element.definition.nameReference). This is a unique name referring to a specific set of constraints applied to this element. One use of this is to provide a name to different slices of the same element.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Indicates that the element is sliced into a set of alternative definitions (there are multiple definitions on a single element in the base resource). The set of slices is any elements that come after this in the element sequence that have the same path, until a shorter path occurs (the shorter path terminates the set).
  @returns {ElementSlicingComponent}
  ###
  slicing: -> if @json['slicing'] then new ElementSlicingComponent(@json['slicing'])
  
  ###*
  Definition of the content of the element to provide a more specific definition than that contained for the element in the base resource.
  @returns {ElementDefinitionComponent}
  ###
  definition: -> if @json['definition'] then new ElementDefinitionComponent(@json['definition'])
  

###* 
 Embedded class
@class ConstraintComponent
@exports  ConstraintComponent as ConstraintComponent
###
class ConstraintComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Captures constraints on each element within the resource.
  @returns {Array} an array of {@link ElementComponent} objects
  ###
  element: ->
    if @json['element']
      for item in @json['element']
        new ElementComponent(item)
  

###* 
 Embedded class
@class ProfileStructureSearchParamComponent
@exports  ProfileStructureSearchParamComponent as ProfileStructureSearchParamComponent
###
class ProfileStructureSearchParamComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The name of the standard or custom search parameter.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  The type of value a search parameter refers to, and how the content is interpreted.
  @returns {Array} an array of {@link String} objects
  ###
  fhirType:-> @json['fhirType']
  
  ###*
  A specification for search parameters. For standard parameters, provides additional information on how the parameter is used in this solution.  For custom parameters, provides a description of what the parameter does.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  
  ###*
  An XPath expression that returns a set of elements for the search parameter.
  @returns {Array} an array of {@link String} objects
  ###
  xpath:-> @json['xpath']
  
  ###*
  Types of resource (if a resource is referenced).
  @returns {Array} an array of {@link String} objects
  ###
  target:-> @json['target']
  

###* 
 Embedded class
@class ProfileStructureComponent
@exports  ProfileStructureComponent as ProfileStructureComponent
###
class ProfileStructureComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The Resource or Data type being described.
  @returns {Array} an array of {@link String} objects
  ###
  fhirType:-> @json['fhirType']
  
  ###*
  The structure that is the base on which this set of constraints is derived from.
  @returns {Array} an array of {@link String} objects
  ###
  base:-> @json['base']
  
  ###*
  The name of this resource constraint statement (to refer to it from other resource constraints - from Profile.structure.snapshot.element.definition.type.profile).
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  This definition of a profile on a structure is published as a formal statement. Some structural definitions might be defined purely for internal use within the profile, and not intended to be used outside that context.
  @returns {Array} an array of {@link boolean} objects
  ###
  publish:-> @json['publish']
  
  ###*
  Human summary: why describe this resource?.
  @returns {Array} an array of {@link String} objects
  ###
  purpose:-> @json['purpose']
  
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
  
  ###*
  Additional search parameters for implementations to support and/or make use of.
  @returns {Array} an array of {@link ProfileStructureSearchParamComponent} objects
  ###
  searchParam: ->
    if @json['searchParam']
      for item in @json['searchParam']
        new ProfileStructureSearchParamComponent(item)
  

###* 
 Embedded class
@class ProfileExtensionDefnComponent
@exports  ProfileExtensionDefnComponent as ProfileExtensionDefnComponent
###
class ProfileExtensionDefnComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  A unique code (within the profile) used to identify the extension.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  Defined so that applications can use this name when displaying the value of the extension to the user.
  @returns {Array} an array of {@link String} objects
  ###
  display:-> @json['display']
  
  ###*
  Identifies the type of context to which the extension applies.
  @returns {Array} an array of {@link String} objects
  ###
  contextType:-> @json['contextType']
  
  ###*
  Identifies the types of resource or data type elements to which the extension can be applied.
  @returns {Array} an array of {@link String} objects
  ###
  context:-> @json['context']
  
  ###*
  Definition of the extension and its content.
  @returns {Array} an array of {@link ElementComponent} objects
  ###
  element: ->
    if @json['element']
      for item in @json['element']
        new ElementComponent(item)
  
###*
A Resource Profile - a statement of use of one or more FHIR Resources.  It may include constraints on Resources and Data Types, Terminology Binding Statements and Extension Definitions.
@class Profile
@exports Profile as Profile
###
class Profile extends  Resource
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
  The identifier that is used to identify this version of the profile when it is referenced in a specification, model, design or instance. This is an arbitrary value managed by the profile author manually and the value should be a timestamp.
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
  @returns {Date}
  ###
  date: -> if @json['date'] then new Date(@json['date'])
  
  ###*
  The Scope and Usage that this profile was created to meet.
  @returns {Array} an array of {@link String} objects
  ###
  requirements:-> @json['requirements']
  
  ###*
  The version of the FHIR specification on which this profile is based.
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
  A constraint statement about what contents a resource or data type may have.
  @returns {Array} an array of {@link ProfileStructureComponent} objects
  ###
  structure: ->
    if @json['structure']
      for item in @json['structure']
        new ProfileStructureComponent(item)
  
  ###*
  An extension defined as part of the profile.
  @returns {Array} an array of {@link ProfileExtensionDefnComponent} objects
  ###
  extensionDefn: ->
    if @json['extensionDefn']
      for item in @json['extensionDefn']
        new ProfileExtensionDefnComponent(item)
  



module.exports.Profile = Profile
