class Base
  constructor: (@json) ->
  
module.exports.Base=Base
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
###*
Base definition for all elements in a resource.
@class Element
@exports Element as Element
###
class Element extends Base
  constructor: (@json) ->
    super(@json)
  ###*
  unique id for the element within a resource (for internal references).
  @returns {Array} an array of {@link String} objects
  ###
  id:-> @json['id']
  
  ###*
  May be used to represent additional information that is not part of the basic definition of the element. In order to make the use of extensions safe and manageable, there is a strict set of governance  applied to the definition and use of extensions. Though any implementer is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the extension.
  @returns {Array} an array of {@link Extension} objects
  ###
  extension: ->
    if @json['extension']
      for item in @json['extension']
        new Extension(item)
  



module.exports.Element = Element
###*
Base definition for all elements that are defined inside a resource - but not those in a data type.
@class BackboneElement
@exports BackboneElement as BackboneElement
###
class BackboneElement extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  May be used to represent additional information that is not part of the basic definition of the element, and that modifies the understanding of the element that contains it. Usually modifier elements provide negation or qualification. In order to make the use of extensions safe and manageable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the extension. Applications processing a resource are required to check for modifier extensions.
  @returns {Array} an array of {@link Extension} objects
  ###
  modifierExtension: ->
    if @json['modifierExtension']
      for item in @json['modifierExtension']
        new Extension(item)
  



module.exports.BackboneElement = BackboneElement
###*
Optional Extensions Element - found in all resources.
@class Extension
@exports Extension as Extension
###
class Extension extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Source of the definition for the extension code - a logical name or a URL.
  @returns {Array} an array of {@link String} objects
  ###
  url:-> @json['url']
  
  ###*
  Value of extension - may be a resource or one of a constrained set of the data types (see Extensibility in the spec for list).
  @returns {Array} an array of {@link } objects
  ###
  value:-> @json['value']
  



module.exports.Extension = Extension
###*
A human-readable formatted text, including images.
@class Narrative
@exports Narrative as Narrative
###
class Narrative extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The status of the narrative - whether it's entirely generated (from just the defined data or the extensions too), or whether a human authored it and it may contain additional data.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The actual narrative content, a stripped down version of XHTML.
  @returns {xhtml}
  ###
  div: -> if @json['div'] then new xhtml(@json['div'])
  



module.exports.Narrative = Narrative
###*
A time period defined by a start and end date and optionally time.
@class Period
@exports Period as Period
###
class Period extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The start of the period. The boundary is inclusive.
  @returns {Array} an array of {@link Date} objects
  ###
  start:-> if @json['start'] then DT.DateTime.parse(@json['start'])
  
  ###*
  The end of the period. If the end of the period is missing, it means that the period is ongoing.
  @returns {Array} an array of {@link Date} objects
  ###
  end:-> if @json['end'] then DT.DateTime.parse(@json['end'])
  



module.exports.Period = Period
###*
A reference to a code defined by a terminology system.
@class Coding
@exports Coding as Coding
###
class Coding extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The identification of the code system that defines the meaning of the symbol in the code.
  @returns {Array} an array of {@link String} objects
  ###
  system:-> @json['system']
  
  ###*
  The version of the code system which was used when choosing this code. Note that a well-maintained code system does not need the version reported, because the meaning of codes is consistent across versions. However this cannot consistently be assured. and when the meaning is not guaranteed to be consistent, the version SHOULD be exchanged.
  @returns {Array} an array of {@link String} objects
  ###
  version:-> @json['version']
  
  ###*
  A symbol in syntax defined by the system. The symbol may be a predefined code or an expression in a syntax defined by the coding system (e.g. post-coordination).
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  A representation of the meaning of the code in the system, following the rules of the system.
  @returns {Array} an array of {@link String} objects
  ###
  display:-> @json['display']
  
  ###*
  Indicates that this code was chosen by a user directly - i.e. off a pick list of available items (codes or displays).
  @returns {Array} an array of {@link boolean} objects
  ###
  primary:-> @json['primary']
  
  ###*
  The set of possible coded values this coding was chosen from or constrained by.
  @returns {Reference}
  ###
  valueSet: -> if @json['valueSet'] then new Reference(@json['valueSet'])
  



module.exports.Coding = Coding
###*
A set of ordered Quantities defined by a low and high limit.
@class Range
@exports Range as Range
###
class Range extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The low limit. The boundary is inclusive.
  @returns {Quantity}
  ###
  low: -> if @json['low'] then new Quantity(@json['low'])
  
  ###*
  The high limit. The boundary is inclusive.
  @returns {Quantity}
  ###
  high: -> if @json['high'] then new Quantity(@json['high'])
  



module.exports.Range = Range
###*
A measured amount (or an amount that can potentially be measured). Note that measured amounts include amounts that are not precisely quantified, including amounts involving arbitrary units and floating currencies.
@class Quantity
@exports Quantity as Quantity
###
class Quantity extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The value of the measured amount. The value includes an implicit precision in the presentation of the value.
  @returns {Array} an array of {@link Number} objects
  ###
  value:-> @json['value']
  
  ###*
  How the value should be understood and represented - whether the actual value is greater or less than the stated value due to measurement issues. E.g. if the comparator is "<" , then the real value is < stated value.
  @returns {Array} an array of {@link String} objects
  ###
  comparator:-> @json['comparator']
  
  ###*
  A human-readable form of the units.
  @returns {Array} an array of {@link String} objects
  ###
  units:-> @json['units']
  
  ###*
  The identification of the system that provides the coded form of the unit.
  @returns {Array} an array of {@link String} objects
  ###
  system:-> @json['system']
  
  ###*
  A computer processable form of the units in some unit representation system.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  



module.exports.Quantity = Quantity
###*
For referring to data content defined in other formats.
@class Attachment
@exports Attachment as Attachment
###
class Attachment extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the type of the data in the attachment and allows a method to be chosen to interpret or render the data. Includes mime type parameters such as charset where appropriate.
  @returns {Array} an array of {@link String} objects
  ###
  contentType:-> @json['contentType']
  
  ###*
  The human language of the content. The value can be any valid value according to BCP 47.
  @returns {Array} an array of {@link String} objects
  ###
  language:-> @json['language']
  
  ###*
  The actual data of the attachment - a sequence of bytes. In XML, represented using base64.
  @returns {Array} an array of {@link } objects
  ###
  data:-> @json['data']
  
  ###*
  An alternative location where the data can be accessed.
  @returns {Array} an array of {@link String} objects
  ###
  url:-> @json['url']
  
  ###*
  The number of bytes of data that make up this attachment.
  @returns {Array} an array of {@link Number} objects
  ###
  size:-> @json['size']
  
  ###*
  The calculated hash of the data using SHA-1. Represented using base64.
  @returns {Array} an array of {@link } objects
  ###
  hash:-> @json['hash']
  
  ###*
  A label or set of text to display in place of the data.
  @returns {Array} an array of {@link String} objects
  ###
  title:-> @json['title']
  



module.exports.Attachment = Attachment
###*
A relationship of two Quantity values - expressed as a numerator and a denominator.
@class Ratio
@exports Ratio as Ratio
###
class Ratio extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The value of the numerator.
  @returns {Quantity}
  ###
  numerator: -> if @json['numerator'] then new Quantity(@json['numerator'])
  
  ###*
  The value of the denominator.
  @returns {Quantity}
  ###
  denominator: -> if @json['denominator'] then new Quantity(@json['denominator'])
  



module.exports.Ratio = Ratio
###*
A series of measurements taken by a device, with upper and lower limits. There may be more than one dimension in the data.
@class SampledData
@exports SampledData as SampledData
###
class SampledData extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The base quantity that a measured value of zero represents. In addition, this provides the units of the entire measurement series.
  @returns {Quantity}
  ###
  origin: -> if @json['origin'] then new Quantity(@json['origin'])
  
  ###*
  The length of time between sampling times, measured in milliseconds.
  @returns {Array} an array of {@link Number} objects
  ###
  period:-> @json['period']
  
  ###*
  A correction factor that is applied to the sampled data points before they are added to the origin.
  @returns {Array} an array of {@link Number} objects
  ###
  factor:-> @json['factor']
  
  ###*
  The lower limit of detection of the measured points. This is needed if any of the data points have the value "L" (lower than detection limit).
  @returns {Array} an array of {@link Number} objects
  ###
  lowerLimit:-> @json['lowerLimit']
  
  ###*
  The upper limit of detection of the measured points. This is needed if any of the data points have the value "U" (higher than detection limit).
  @returns {Array} an array of {@link Number} objects
  ###
  upperLimit:-> @json['upperLimit']
  
  ###*
  The number of sample points at each time point. If this value is greater than one, then the dimensions will be interlaced - all the sample points for a point in time will be recorded at once.
  @returns {Array} an array of {@link Number} objects
  ###
  dimensions:-> @json['dimensions']
  
  ###*
  A series of data points which are decimal values separated by a single space (character u20). The special values "E" (error), "L" (below detection limit) and "U" (above detection limit) can also be used in place of a decimal value.
  @returns {Array} an array of {@link String} objects
  ###
  data:-> @json['data']
  



module.exports.SampledData = SampledData
###*
A reference from one resource to another.
@class Reference
@exports Reference as Reference
###
class Reference extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  A reference to a location at which the other resource is found. The reference may a relative reference, in which case it is relative to the service base URL, or an absolute URL that resolves to the location where the resource is found. The reference may be version specific or not. If the reference is not to a FHIR RESTful server, then it should be assumed to be version specific. Internal fragment references (start with '#') refer to contained resources.
  @returns {Array} an array of {@link String} objects
  ###
  reference:-> @json['reference']
  
  ###*
  Plain text narrative that identifies the resource in addition to the resource reference.
  @returns {Array} an array of {@link String} objects
  ###
  display:-> @json['display']
  



module.exports.Reference = Reference
###*
A concept that may be defined by a formal reference to a terminology or ontology or may be provided by text.
@class CodeableConcept
@exports CodeableConcept as CodeableConcept
###
class CodeableConcept extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  A reference to a code defined by a terminology system.
  @returns {Array} an array of {@link Coding} objects
  ###
  coding: ->
    if @json['coding']
      for item in @json['coding']
        new Coding(item)
  
  ###*
  A human language representation of the concept as seen/selected/uttered by the user who entered the data and/or which represents the intended meaning of the user.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  



module.exports.CodeableConcept = CodeableConcept
###*
A technical identifier - identifies some entity uniquely and unambiguously.
@class Identifier
@exports Identifier as Identifier
###
class Identifier extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The purpose of this identifier.
  @returns {Array} an array of {@link String} objects
  ###
  use:-> @json['use']
  
  ###*
  A text string for the identifier that can be displayed to a human so they can recognize the identifier.
  @returns {Array} an array of {@link String} objects
  ###
  label:-> @json['label']
  
  ###*
  Establishes the namespace in which set of possible id values is unique.
  @returns {Array} an array of {@link String} objects
  ###
  system:-> @json['system']
  
  ###*
  The portion of the identifier typically displayed to the user and which is unique within the context of the system.
  @returns {Array} an array of {@link String} objects
  ###
  value:-> @json['value']
  
  ###*
  Time period during which identifier is/was valid for use.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  Organization that issued/manages the identifier.
  @returns {Reference}
  ###
  assigner: -> if @json['assigner'] then new Reference(@json['assigner'])
  



module.exports.Identifier = Identifier

###* 
Embedded class
@class ElementDefinitionSlicingComponent
@exports  ElementDefinitionSlicingComponent as ElementDefinitionSlicingComponent
###
class ElementDefinitionSlicingComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Designates which child elements are used to discriminate between the slices when processing an instance. If one or more discriminators are provided, the value of the child elements in the instance data SHALL completely distinguish which slice the element in the resource matches based on the allowed values for those elements in each of the slices.
  @returns {Array} an array of {@link String} objects
  ###
  discriminator:-> @json['discriminator']
  
  ###*
  A humane readable text description of how the slicing works. If there is no discriminator, this is required to be present to provide whatever information is possible about how the slices can be differentiated.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
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
  Name of Data type or Resource that is a(or the) type used for this element.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  Identifies a profile structure that SHALL hold for resources or datatypes referenced as the type of this element. Can be a local reference - to another structure in this profile, or a reference to a structure in another profile.
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
  An XPath expression of constraint that can be executed to see if this constraint is met.
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
Captures constraints on each element within the resource, profile, or extension.
@class ElementDefinition
@exports ElementDefinition as ElementDefinition
###
class ElementDefinition extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The path identifies the element and is expressed as a "."-separated list of ancestor elements, beginning with the name of the resource or extension.
  @returns {Array} an array of {@link String} objects
  ###
  path:-> @json['path']
  
  ###*
  Codes that define how this element is represented in instances, when the deviation varies from the normal case.
  @returns {Array} an array of {@link String} objects
  ###
  representation:-> @json['representation']
  
  ###*
  The name of this element definition (to refer to it from other element definitions using ElementDefinition.nameReference). This is a unique name referring to a specific set of constraints applied to this element. One use of this is to provide a name to different slices of the same element.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Indicates that the element is sliced into a set of alternative definitions (there are multiple definitions on a single element in the base resource). The set of slices is any elements that come after this in the element sequence that have the same path, until a shorter path occurs (the shorter path terminates the set).
  @returns {ElementDefinitionSlicingComponent}
  ###
  slicing: -> if @json['slicing'] then new ElementDefinitionSlicingComponent(@json['slicing'])
  
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
  type: ->
    if @json['type']
      for item in @json['type']
        new TypeRefComponent(item)
  
  ###*
  Identifies the name of a slice defined elsewhere in the profile whose constraints should be applied to the current element.
  @returns {Array} an array of {@link String} objects
  ###
  nameReference:-> @json['nameReference']
  
  ###*
  The value that should be used if there is no value stated in the instance.
  @returns {Array} an array of {@link } objects
  ###
  defaultValue:-> @json['defaultValue']
  
  ###*
  The Implicit meaning that is to be understood when this element is missing.
  @returns {Array} an array of {@link String} objects
  ###
  meaningWhenMissing:-> @json['meaningWhenMissing']
  
  ###*
  Specifies a value that SHALL be exactly the value  for this element in the instance. For purposes of comparison, non-signficant whitespace is ignored, and all values must be an exact match (case and accent sensitive). Missing elements/attributes must also be missing.
  @returns {Array} an array of {@link } objects
  ###
  fixed:-> @json['fixed']
  
  ###*
  Specifies a value that the value in the instance SHALL follow - that is, any value in the pattern must be found in the instance. Other additional values may be found too. This is effectively constraint by example.  The values of elements present in the pattern must match exactly (case-senstive, accent-sensitive, etc.).
  @returns {Array} an array of {@link } objects
  ###
  pattern:-> @json['pattern']
  
  ###*
  An example value for this element.
  @returns {Array} an array of {@link } objects
  ###
  example:-> @json['example']
  
  ###*
  Indicates the maximum length in characters that is permitted to be present in conformant instances and which is expected to be supported by conformant consumers that support the element.
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
  Whether the element should be included if a client requests a search with the parameter _summary=true.
  @returns {Array} an array of {@link boolean} objects
  ###
  isSummary:-> @json['isSummary']
  
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
  



module.exports.ElementDefinition = ElementDefinition

###* 
Embedded class
@class TimingRepeatComponent
@exports  TimingRepeatComponent as TimingRepeatComponent
###
class TimingRepeatComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates how often the event should occur.
  @returns {Array} an array of {@link Number} objects
  ###
  frequency:-> @json['frequency']
  
  ###*
  Identifies the occurrence of daily life that determines timing.
  @returns {Array} an array of {@link String} objects
  ###
  when:-> @json['when']
  
  ###*
  How long each repetition should last.
  @returns {Array} an array of {@link Number} objects
  ###
  duration:-> @json['duration']
  
  ###*
  The units of time for the duration.
  @returns {Array} an array of {@link String} objects
  ###
  units:-> @json['units']
  
  ###*
  A total count of the desired number of repetitions.
  @returns {Array} an array of {@link Number} objects
  ###
  count:-> @json['count']
  
  ###*
  When to stop repeating the timing schedule.
  @returns {Array} an array of {@link Date} objects
  ###
  end:-> if @json['end'] then DT.DateTime.parse(@json['end'])
  
###*
Specifies an event that may occur multiple times. Timing schedules are used for to record when things are expected or requested to occur.
@class Timing
@exports Timing as Timing
###
class Timing extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies specific time periods when the event should occur.
  @returns {Array} an array of {@link Period} objects
  ###
  event: ->
    if @json['event']
      for item in @json['event']
        new Period(item)
  
  ###*
  Identifies a repeating pattern to the intended time periods.
  @returns {TimingRepeatComponent}
  ###
  repeat: -> if @json['repeat'] then new TimingRepeatComponent(@json['repeat'])
  



module.exports.Timing = Timing
###*
There is a variety of postal address formats defined around the world. This format defines a superset that is the basis for all addresses around the world.
@class Address
@exports Address as Address
###
class Address extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The purpose of this address.
  @returns {Array} an array of {@link String} objects
  ###
  use:-> @json['use']
  
  ###*
  A full text representation of the address.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  
  ###*
  This component contains the house number, apartment number, street name, street direction, 
P.O. Box number, delivery hints, and similar address information.
  @returns {Array} an array of {@link String} objects
  ###
  line:-> @json['line']
  
  ###*
  The name of the city, town, village or other community or delivery center.
  @returns {Array} an array of {@link String} objects
  ###
  city:-> @json['city']
  
  ###*
  Sub-unit of a country with limited sovereignty in a federally organized country. A code may be used if codes are in common use (i.e. US 2 letter state codes).
  @returns {Array} an array of {@link String} objects
  ###
  state:-> @json['state']
  
  ###*
  A postal code designating a region defined by the postal service.
  @returns {Array} an array of {@link String} objects
  ###
  postalCode:-> @json['postalCode']
  
  ###*
  Country - a nation as commonly understood or generally accepted.
  @returns {Array} an array of {@link String} objects
  ###
  country:-> @json['country']
  
  ###*
  Time period when address was/is in use.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  



module.exports.Address = Address
###*
A human's name with the ability to identify parts and usage.
@class HumanName
@exports HumanName as HumanName
###
class HumanName extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the purpose for this name.
  @returns {Array} an array of {@link String} objects
  ###
  use:-> @json['use']
  
  ###*
  A full text representation of the name.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  
  ###*
  The part of a name that links to the genealogy. In some cultures (e.g. Eritrea) the family name of a son is the first name of his father.
  @returns {Array} an array of {@link String} objects
  ###
  family:-> @json['family']
  
  ###*
  Given name.
  @returns {Array} an array of {@link String} objects
  ###
  given:-> @json['given']
  
  ###*
  Part of the name that is acquired as a title due to academic, legal, employment or nobility status, etc. and that appears at the start of the name.
  @returns {Array} an array of {@link String} objects
  ###
  prefix:-> @json['prefix']
  
  ###*
  Part of the name that is acquired as a title due to academic, legal, employment or nobility status, etc. and that appears at the end of the name.
  @returns {Array} an array of {@link String} objects
  ###
  suffix:-> @json['suffix']
  
  ###*
  Indicates the period of time when this name was valid for the named person.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  



module.exports.HumanName = HumanName
###*
Details for All kinds of technology mediated contact points for a person or organization, including telephone, email, etc.
@class ContactPoint
@exports ContactPoint as ContactPoint
###
class ContactPoint extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Telecommunications form for contact point - what communications system is required to make use of the contact.
  @returns {Array} an array of {@link String} objects
  ###
  system:-> @json['system']
  
  ###*
  The actual contact point details, in a form that is meaningful to the designated communication system (i.e. phone number or email address).
  @returns {Array} an array of {@link String} objects
  ###
  value:-> @json['value']
  
  ###*
  Identifies the purpose for the contact point.
  @returns {Array} an array of {@link String} objects
  ###
  use:-> @json['use']
  
  ###*
  Time period when the contact point was/is in use.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  



module.exports.ContactPoint = ContactPoint

###* 
Embedded class
@class ResourceMetaComponent
@exports  ResourceMetaComponent as ResourceMetaComponent
###
class ResourceMetaComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The version specific identifier, as it appears in the version portion of the url. This values changes when the resource is created, updated, or deleted.
  @returns {Array} an array of {@link String} objects
  ###
  versionId:-> @json['versionId']
  
  ###*
  When the resource last changed - e.g. when the version changed.
  @returns {Array} an array of {@link Date} objects
  ###
  lastUpdated:-> if @json['lastUpdated'] then DT.DateTime.parse(@json['lastUpdated'])
  
  ###*
  A list of profiles that this resource claims to conform to. The URL is a reference to Profile.url.
  @returns {Array} an array of {@link String} objects
  ###
  profile:-> @json['profile']
  
  ###*
  Security labels applied to this resource. These tags connect specific resources to the overall security policy and infrastructure.
  @returns {Array} an array of {@link Coding} objects
  ###
  security: ->
    if @json['security']
      for item in @json['security']
        new Coding(item)
  
  ###*
  Tags applied to this resource. Tags are intended to to be used to identify and relate resources to process and workflow, and applications are not required to consider the tags when interpreting the meaning of a resource.
  @returns {Array} an array of {@link Coding} objects
  ###
  tag: ->
    if @json['tag']
      for item in @json['tag']
        new Coding(item)
  
###*
Base Resource for everything.
@class Resource
@exports Resource as Resource
###
class Resource extends Base
  constructor: (@json) ->
    super(@json)
  ###*
  The logical id of the resource, as used in the url for the resoure. Once assigned, this value never changes.
  @returns {Array} an array of {@link String} objects
  ###
  id:-> @json['id']
  
  ###*
  The metadata about the resource. This is content that is maintained by the infrastructure. Changes to the content may not always be associated with version changes to the resource.
  @returns {ResourceMetaComponent}
  ###
  meta: -> if @json['meta'] then new ResourceMetaComponent(@json['meta'])
  
  ###*
  A reference to a set of rules that were followed when the resource was constructed, and which must be understood when processing the content.
  @returns {Array} an array of {@link String} objects
  ###
  implicitRules:-> @json['implicitRules']
  
  ###*
  The base language in which the resource is written.
  @returns {Array} an array of {@link String} objects
  ###
  language:-> @json['language']
  



module.exports.Resource = Resource
###*

@class DomainResource
@exports DomainResource as DomainResource
###
class DomainResource extends Resource
  constructor: (@json) ->
    super(@json)
  ###*
  A human-readable narrative that contains a summary of the resource, and may be used to represent the content of the resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient detail to make it "clinically safe" for a human to just read the narrative. Resource definitions may define what content should be represented in the narrative to ensure clinical safety.
  @returns {Narrative}
  ###
  text: -> if @json['text'] then new Narrative(@json['text'])
  
  ###*
  These resources do not have an independent existence apart from the resource that contains them - they cannot be identified independently, and nor can they have their own independent transaction scope.
  @returns {Array} an array of {@link Resource} objects
  ###
  contained: ->
    if @json['contained']
      for item in @json['contained']
        typeName = @json['contained'].resourceType
        req = require('./'+typeName.toLowerCase())[typeName]
        new req(item)
  
  ###*
  May be used to represent additional information that is not part of the basic definition of the resource. In order to make the use of extensions safe and manageable, there is a strict set of governance  applied to the definition and use of extensions. Though any implementer is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the extension.
  @returns {Array} an array of {@link Extension} objects
  ###
  extension: ->
    if @json['extension']
      for item in @json['extension']
        new Extension(item)
  
  ###*
  May be used to represent additional information that is not part of the basic definition of the resource, and that modifies the understanding of the element that contains it. Usually modifier elements provide negation or qualification. In order to make the use of extensions safe and manageable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the extension. Applications processing a resource are required to check for modifier extensions.
  @returns {Array} an array of {@link Extension} objects
  ###
  modifierExtension: ->
    if @json['modifierExtension']
      for item in @json['modifierExtension']
        new Extension(item)
  



module.exports.DomainResource = DomainResource

###* 
Embedded class
@class ParametersParameterComponent
@exports  ParametersParameterComponent as ParametersParameterComponent
###
class ParametersParameterComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The name of the parameter (reference to the operation definition).
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  If the parameter is a data type.
  @returns {Array} an array of {@link } objects
  ###
  value:-> @json['value']
  
  ###*
  If the parameter is a whole resource.
  @returns {Resource}
  ###
  resource: -> 
    if @json['resource']
      typeName = @json['resource'].resourceType
      req = require('./'+typeName.toLowerCase())[typeName]
      new req(@json['resource'])
  
###*
This special resource type is used to represent [operation](operations.html] request and response. It has no other use, and there is no RESTful end=point associated with it.
@class Parameters
@exports Parameters as Parameters
###
class Parameters extends Resource
  constructor: (@json) ->
    super(@json)
  ###*
  A parameter passed to or received from the operation.
  @returns {Array} an array of {@link ParametersParameterComponent} objects
  ###
  parameter: ->
    if @json['parameter']
      for item in @json['parameter']
        new ParametersParameterComponent(item)
  



module.exports.Parameters = Parameters
