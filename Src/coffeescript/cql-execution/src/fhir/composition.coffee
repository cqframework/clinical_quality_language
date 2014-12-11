
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
@class CompositionAttesterComponent
@exports  CompositionAttesterComponent as CompositionAttesterComponent
###
class CompositionAttesterComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The type of attestation the authenticator offers.
  @returns {Array} an array of {@link String} objects
  ###
  mode:-> @json['mode']
  
  ###*
  When composition was attested by the party.
  @returns {Array} an array of {@link Date} objects
  ###
  time:-> if @json['time'] then DT.DateTime.parse(@json['time'])
  
  ###*
  Who attested the composition in the specified way.
  @returns {Reference}
  ###
  party: -> if @json['party'] then new Reference(@json['party'])
  

###* 
Embedded class
@class CompositionEventComponent
@exports  CompositionEventComponent as CompositionEventComponent
###
class CompositionEventComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  This list of codes represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In some cases, the event is inherent in the typeCode, such as a "History and Physical Report" in which the procedure being documented is necessarily a "History and Physical" act.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  code: ->
    if @json['code']
      for item in @json['code']
        new CodeableConcept(item)
  
  ###*
  The period of time covered by the documentation. There is no assertion that the documentation is a complete representation for this period, only that it documents events during this time.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  Full details for the event(s) the composition/documentation consents.
  @returns {Array} an array of {@link Reference} objects
  ###
  detail: ->
    if @json['detail']
      for item in @json['detail']
        new Reference(item)
  

###* 
Embedded class
@class SectionComponent
@exports  SectionComponent as SectionComponent
###
class SectionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The label for this particular section.  This will be part of the rendered content for the document, and is often used to build a table of contents.
  @returns {Array} an array of {@link String} objects
  ###
  title:-> @json['title']
  
  ###*
  A code identifying the kind of content contained within the section. This must be consistent with the section title.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  A nested sub-section within this section.
  @returns {Array} an array of {@link SectionComponent} objects
  ###
  section: ->
    if @json['section']
      for item in @json['section']
        new SectionComponent(item)
  
  ###*
  The content (narrative and data) associated with the section.
  @returns {Reference}
  ###
  content: -> if @json['content'] then new Reference(@json['content'])
  
###*
A set of healthcare-related information that is assembled together into a single logical document that provides a single coherent statement of meaning, establishes its own context and that has clinical attestation with regard to who is making the statement.
@class Composition
@exports Composition as Composition
###
class Composition extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Logical Identifier for the composition, assigned when created. This identifier stays constant as the composition is changed over time.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  The composition editing time, when the composition was last logically changed by the author.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Specifies the particular kind of composition (e.g. History and Physical, Discharge Summary, Progress Note). This usually equates to the purpose of making the composition.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  A categorization for the type of the composition. This may be implied by or derived from the code specified in the Composition Type.
  @returns {CodeableConcept}
  ###
  class: -> if @json['class'] then new CodeableConcept(@json['class'])
  
  ###*
  Official human-readable label for the composition.
  @returns {Array} an array of {@link String} objects
  ###
  title:-> @json['title']
  
  ###*
  The workflow/clinical status of this composition. The status is a marker for the clinical standing of the document.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The code specifying the level of confidentiality of the Composition.
  @returns {Coding}
  ###
  confidentiality: -> if @json['confidentiality'] then new Coding(@json['confidentiality'])
  
  ###*
  Who or what the composition is about. The composition can be about a person, (patient or healthcare practitioner), a device (I.e. machine) or even a group of subjects (such as a document about a herd of livestock, or a set of patients that share a common exposure).
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Identifies who is responsible for the information in the composition.  (Not necessarily who typed it in.).
  @returns {Array} an array of {@link Reference} objects
  ###
  author: ->
    if @json['author']
      for item in @json['author']
        new Reference(item)
  
  ###*
  A participant who has attested to the accuracy of the composition/document.
  @returns {Array} an array of {@link CompositionAttesterComponent} objects
  ###
  attester: ->
    if @json['attester']
      for item in @json['attester']
        new CompositionAttesterComponent(item)
  
  ###*
  Identifies the organization or group who is responsible for ongoing maintenance of and access to the composition/document information.
  @returns {Reference}
  ###
  custodian: -> if @json['custodian'] then new Reference(@json['custodian'])
  
  ###*
  The clinical service, such as a colonoscopy or an appendectomy, being documented.
  @returns {Array} an array of {@link CompositionEventComponent} objects
  ###
  event: ->
    if @json['event']
      for item in @json['event']
        new CompositionEventComponent(item)
  
  ###*
  Describes the clinical encounter or type of care this documentation is associated with.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  The root of the sections that make up the composition.
  @returns {Array} an array of {@link SectionComponent} objects
  ###
  section: ->
    if @json['section']
      for item in @json['section']
        new SectionComponent(item)
  



module.exports.Composition = Composition
