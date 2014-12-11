
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
@class ObservationReferenceRangeComponent
@exports  ObservationReferenceRangeComponent as ObservationReferenceRangeComponent
###
class ObservationReferenceRangeComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The value of the low bound of the reference range. If this is omitted, the low bound of the reference range is assumed to be meaningless. E.g. <2.3.
  @returns {Quantity}
  ###
  low: -> if @json['low'] then new Quantity(@json['low'])
  
  ###*
  The value of the high bound of the reference range. If this is omitted, the high bound of the reference range is assumed to be meaningless. E.g. >5.
  @returns {Quantity}
  ###
  high: -> if @json['high'] then new Quantity(@json['high'])
  
  ###*
  Code for the meaning of the reference range.
  @returns {CodeableConcept}
  ###
  meaning: -> if @json['meaning'] then new CodeableConcept(@json['meaning'])
  
  ###*
  The age at which this reference range is applicable. This is a neonatal age (e.g. number of weeks at term) if the meaning says so.
  @returns {Range}
  ###
  age: -> if @json['age'] then new Range(@json['age'])
  
  ###*
  Text based reference range in an observation which may be used when a quantitative range is not appropriate for an observation.  An example would be a reference value of "Negative" or a list or table of 'normals'.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  

###* 
Embedded class
@class ObservationRelatedComponent
@exports  ObservationRelatedComponent as ObservationRelatedComponent
###
class ObservationRelatedComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A code specifying the kind of relationship that exists with the target observation.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  A reference to the observation that is related to this observation.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
###*
Measurements and simple assertions made about a patient, device or other subject.
@class Observation
@exports Observation as Observation
###
class Observation extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Describes what was observed. Sometimes this is called the observation "code".
  @returns {CodeableConcept}
  ###
  name: -> if @json['name'] then new CodeableConcept(@json['name'])
  
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {Quantity}
  ###
  valueQuantity: -> if @json['valueQuantity'] then new Quantity(@json['valueQuantity'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {CodeableConcept}
  ###
  valueCodeableConcept: -> if @json['valueCodeableConcept'] then new CodeableConcept(@json['valueCodeableConcept'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {Attachment}
  ###
  valueAttachment: -> if @json['valueAttachment'] then new Attachment(@json['valueAttachment'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {Ratio}
  ###
  valueRatio: -> if @json['valueRatio'] then new Ratio(@json['valueRatio'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {Array} an array of {@link Date} objects
  ###
  valueDateTime:-> if @json['valueDateTime'] then DT.DateTime.parse(@json['valueDateTime'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {Period}
  ###
  valuePeriod: -> if @json['valuePeriod'] then new Period(@json['valuePeriod'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {SampledData}
  ###
  valueSampledData: -> if @json['valueSampledData'] then new SampledData(@json['valueSampledData'])
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {Array} an array of {@link String} objects
  ###
  valueString:-> @json['valueString']
  ###*
  The information determined as a result of making the observation, if the information has a simple value.
  @returns {time}
  ###
  valueTime: -> if @json['valueTime'] then new time(@json['valueTime'])
  
  ###*
  Provides a reason why the expected value in the element Observation.value[x] is missing.
  @returns {Array} an array of {@link String} objects
  ###
  dataAbsentReason:-> @json['dataAbsentReason']
  
  ###*
  The assessment made based on the result of the observation.
  @returns {CodeableConcept}
  ###
  interpretation: -> if @json['interpretation'] then new CodeableConcept(@json['interpretation'])
  
  ###*
  May include statements about significant, unexpected or unreliable values, or information about the source of the value where this may be relevant to the interpretation of the result.
  @returns {Array} an array of {@link String} objects
  ###
  comments:-> @json['comments']
  
  ###*
  The time or time-period the observed value is asserted as being true. For biological subjects - e.g. human patients - this is usually called the "physiologically relevant time". This is usually either the time of the procedure or of specimen collection, but very often the source of the date/time is not known, only the date/time itself.
  @returns {Array} an array of {@link Date} objects
  ###
  appliesDateTime:-> if @json['appliesDateTime'] then DT.DateTime.parse(@json['appliesDateTime'])
  ###*
  The time or time-period the observed value is asserted as being true. For biological subjects - e.g. human patients - this is usually called the "physiologically relevant time". This is usually either the time of the procedure or of specimen collection, but very often the source of the date/time is not known, only the date/time itself.
  @returns {Period}
  ###
  appliesPeriod: -> if @json['appliesPeriod'] then new Period(@json['appliesPeriod'])
  
  ###*
  The date and time this observation was made available.
  @returns {Array} an array of {@link Date} objects
  ###
  issued:-> if @json['issued'] then DT.DateTime.parse(@json['issued'])
  
  ###*
  The status of the result value.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  An estimate of the degree to which quality issues have impacted on the value reported.
  @returns {Array} an array of {@link String} objects
  ###
  reliability:-> @json['reliability']
  
  ###*
  Indicates the site on the subject's body where the observation was made ( i.e. the target site).
  @returns {CodeableConcept}
  ###
  bodySite: -> if @json['bodySite'] then new CodeableConcept(@json['bodySite'])
  
  ###*
  Indicates the mechanism used to perform the observation.
  @returns {CodeableConcept}
  ###
  method: -> if @json['method'] then new CodeableConcept(@json['method'])
  
  ###*
  A unique identifier for the simple observation.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  The thing the observation is being made about.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The specimen that was used when this observation was made.
  @returns {Reference}
  ###
  specimen: -> if @json['specimen'] then new Reference(@json['specimen'])
  
  ###*
  Who was responsible for asserting the observed value as "true".
  @returns {Array} an array of {@link Reference} objects
  ###
  performer: ->
    if @json['performer']
      for item in @json['performer']
        new Reference(item)
  
  ###*
  The healthcare event  ( e.g. a patient and healthcare provider interaction ) that relates to this observation.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  Guidance on how to interpret the value by comparison to a normal or recommended range.
  @returns {Array} an array of {@link ObservationReferenceRangeComponent} objects
  ###
  referenceRange: ->
    if @json['referenceRange']
      for item in @json['referenceRange']
        new ObservationReferenceRangeComponent(item)
  
  ###*
  Related observations - either components, or previous observations, or statements of derivation.
  @returns {Array} an array of {@link ObservationRelatedComponent} objects
  ###
  related: ->
    if @json['related']
      for item in @json['related']
        new ObservationRelatedComponent(item)
  



module.exports.Observation = Observation
