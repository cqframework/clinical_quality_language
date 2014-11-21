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
###*
There is a variety of postal address formats defined around the world. This format defines a superset that is the basis for all addresses around the world.
@class Address
@exports Address as Address
###
class Address 
  constructor: (@json) ->
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
  zip:-> @json['zip']
  
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
For referring to data content defined in other formats.
@class Attachment
@exports Attachment as Attachment
###
class Attachment 
  constructor: (@json) ->
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
A concept that may be defined by a formal reference to a terminology or ontology or may be provided by text.
@class CodeableConcept
@exports CodeableConcept as CodeableConcept
###
class CodeableConcept 
  constructor: (@json) ->
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
A reference to a code defined by a terminology system.
@class Coding
@exports Coding as Coding
###
class Coding 
  constructor: (@json) ->
  ###*
  The identification of the code system that defines the meaning of the symbol in the code.
  @returns {Array} an array of {@link String} objects
  ###
  system:-> @json['system']
  
  ###*
  The version of the code system which was used when choosing this code. Note that a well-maintained code system does not need the version reported, because the meaning of codes is consistent across versions. However this cannot consistently be assured. and When the meaning is not guaranteed to be consistent, the version SHOULD be exchanged.
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
Details for All kinds of technology mediated contact points for a person or organization, including telephone, email, etc.
@class ContactPoint
@exports ContactPoint as ContactPoint
###
class ContactPoint 
  constructor: (@json) ->
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
Optional Extensions Element - found in all resources.
@class Extension
@exports Extension as Extension
###
class Extension 
  constructor: (@json) ->
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
A human's name with the ability to identify parts and usage.
@class HumanName
@exports HumanName as HumanName
###
class HumanName 
  constructor: (@json) ->
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
A technical identifier - identifies some entity uniquely and unambiguously.
@class Identifier
@exports Identifier as Identifier
###
class Identifier 
  constructor: (@json) ->
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
A human-readable formatted text, including images.
@class Narrative
@exports Narrative as Narrative
###
class Narrative 
  constructor: (@json) ->
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
class Period 
  constructor: (@json) ->
  ###*
  The start of the period. The boundary is inclusive.
  @returns {Date}
  ###
  start: -> if @json['start'] then new Date(@json['start'])
  
  ###*
  The end of the period. If the end of the period is missing, it means that the period is ongoing.
  @returns {Date}
  ###
  end: -> if @json['end'] then new Date(@json['end'])
  



module.exports.Period = Period
###*
A measured amount (or an amount that can potentially be measured). Note that measured amounts include amounts that are not precisely quantified, including amounts involving arbitrary units and floating currencies.
@class Quantity
@exports Quantity as Quantity
###
class Quantity 
  constructor: (@json) ->
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
A set of ordered Quantities defined by a low and high limit.
@class Range
@exports Range as Range
###
class Range 
  constructor: (@json) ->
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
A relationship of two Quantity values - expressed as a numerator and a denominator.
@class Ratio
@exports Ratio as Ratio
###
class Ratio 
  constructor: (@json) ->
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
A reference from one resource to another.
@class Reference
@exports Reference as Reference
###
class Reference 
  constructor: (@json) ->
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
A series of measurements taken by a device, with upper and lower limits. There may be more than one dimension in the data.
@class SampledData
@exports SampledData as SampledData
###
class SampledData 
  constructor: (@json) ->
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
 Embedded class
@class TimingRepeatComponent
@exports  TimingRepeatComponent as TimingRepeatComponent
###
class TimingRepeatComponent
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
  @returns {Date}
  ###
  end: -> if @json['end'] then new Date(@json['end'])
  
###*
Specifies an event that may occur multiple times. Timing schedules are used for to record when things are expected or requested to occur.
@class Timing
@exports Timing as Timing
###
class Timing 
  constructor: (@json) ->
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
