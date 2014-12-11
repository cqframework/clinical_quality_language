
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
@class SpecimenSourceComponent
@exports  SpecimenSourceComponent as SpecimenSourceComponent
###
class SpecimenSourceComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Whether this relationship is to a parent or to a child.
  @returns {Array} an array of {@link String} objects
  ###
  relationship:-> @json['relationship']
  
  ###*
  The specimen resource that is the target of this relationship.
  @returns {Array} an array of {@link Reference} objects
  ###
  target: ->
    if @json['target']
      for item in @json['target']
        new Reference(item)
  

###* 
Embedded class
@class SpecimenCollectionComponent
@exports  SpecimenCollectionComponent as SpecimenCollectionComponent
###
class SpecimenCollectionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Person who collected the specimen.
  @returns {Reference}
  ###
  collector: -> if @json['collector'] then new Reference(@json['collector'])
  
  ###*
  To communicate any details or issues encountered during the specimen collection procedure.
  @returns {Array} an array of {@link String} objects
  ###
  comment:-> @json['comment']
  
  ###*
  Time when specimen was collected from subject - the physiologically relevant time.
  @returns {Array} an array of {@link Date} objects
  ###
  collectedDateTime:-> if @json['collectedDateTime'] then DT.DateTime.parse(@json['collectedDateTime'])
  ###*
  Time when specimen was collected from subject - the physiologically relevant time.
  @returns {Period}
  ###
  collectedPeriod: -> if @json['collectedPeriod'] then new Period(@json['collectedPeriod'])
  
  ###*
  The quantity of specimen collected; for instance the volume of a blood sample, or the physical measurement of an anatomic pathology sample.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  A coded value specifying the technique that is used to perform the procedure.
  @returns {CodeableConcept}
  ###
  method: -> if @json['method'] then new CodeableConcept(@json['method'])
  
  ###*
  Anatomical location from which the specimen should be collected (if subject is a patient). This element is not used for environmental specimens.
  @returns {CodeableConcept}
  ###
  sourceSite: -> if @json['sourceSite'] then new CodeableConcept(@json['sourceSite'])
  

###* 
Embedded class
@class SpecimenTreatmentComponent
@exports  SpecimenTreatmentComponent as SpecimenTreatmentComponent
###
class SpecimenTreatmentComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Textual description of procedure.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  A coded value specifying the procedure used to process the specimen.
  @returns {CodeableConcept}
  ###
  procedure: -> if @json['procedure'] then new CodeableConcept(@json['procedure'])
  
  ###*
  Material used in the processing step.
  @returns {Array} an array of {@link Reference} objects
  ###
  additive: ->
    if @json['additive']
      for item in @json['additive']
        new Reference(item)
  

###* 
Embedded class
@class SpecimenContainerComponent
@exports  SpecimenContainerComponent as SpecimenContainerComponent
###
class SpecimenContainerComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Id for container. There may be multiple; a manufacturer's bar code, lab assigned identifier, etc. The container ID may differ from the specimen id in some circumstances.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Textual description of the container.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  The type of container associated with the specimen (e.g. slide, aliquot, etc).
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The capacity (volume or other measure) the container may contain.
  @returns {Quantity}
  ###
  capacity: -> if @json['capacity'] then new Quantity(@json['capacity'])
  
  ###*
  The quantity of specimen in the container; may be volume, dimensions, or other appropriate measurements, depending on the specimen type.
  @returns {Quantity}
  ###
  specimenQuantity: -> if @json['specimenQuantity'] then new Quantity(@json['specimenQuantity'])
  
  ###*
  Introduced substance to preserve, maintain or enhance the specimen. examples: Formalin, Citrate, EDTA.
  @returns {CodeableConcept}
  ###
  additiveCodeableConcept: -> if @json['additiveCodeableConcept'] then new CodeableConcept(@json['additiveCodeableConcept'])
  ###*
  Introduced substance to preserve, maintain or enhance the specimen. examples: Formalin, Citrate, EDTA.
  @returns {Reference}
  ###
  additiveReference: -> if @json['additiveReference'] then new Reference(@json['additiveReference'])
  
###*
Sample for analysis.
@class Specimen
@exports Specimen as Specimen
###
class Specimen extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Id for specimen.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Kind of material that forms the specimen.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Parent specimen from which the focal specimen was a component.
  @returns {Array} an array of {@link SpecimenSourceComponent} objects
  ###
  source: ->
    if @json['source']
      for item in @json['source']
        new SpecimenSourceComponent(item)
  
  ###*
  Where the specimen came from. This may be the patient(s) or from the environment or  a device.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The identifier assigned by the lab when accessioning specimen(s). This is not necessarily the same as the specimen identifier, depending on local lab procedures.
  @returns {Identifier}
  ###
  accessionIdentifier: -> if @json['accessionIdentifier'] then new Identifier(@json['accessionIdentifier'])
  
  ###*
  Time when specimen was received for processing or testing.
  @returns {Array} an array of {@link Date} objects
  ###
  receivedTime:-> if @json['receivedTime'] then DT.DateTime.parse(@json['receivedTime'])
  
  ###*
  Details concerning the specimen collection.
  @returns {SpecimenCollectionComponent}
  ###
  collection: -> if @json['collection'] then new SpecimenCollectionComponent(@json['collection'])
  
  ###*
  Details concerning treatment and processing steps for the specimen.
  @returns {Array} an array of {@link SpecimenTreatmentComponent} objects
  ###
  treatment: ->
    if @json['treatment']
      for item in @json['treatment']
        new SpecimenTreatmentComponent(item)
  
  ###*
  The container holding the specimen.  The recursive nature of containers; i.e. blood in tube in tray in rack is not addressed here.
  @returns {Array} an array of {@link SpecimenContainerComponent} objects
  ###
  container: ->
    if @json['container']
      for item in @json['container']
        new SpecimenContainerComponent(item)
  



module.exports.Specimen = Specimen
