
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
@class SupplyDispenseComponent
@exports  SupplyDispenseComponent as SupplyDispenseComponent
###
class SupplyDispenseComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifier assigned by the dispensing facility when the dispense occurs.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  A code specifying the state of the dispense event.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Indicates the type of dispensing event that is performed. Examples include: Trial Fill, Completion of Trial, Partial Fill, Emergency Fill, Samples, etc.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The amount of supply that has been dispensed. Includes unit of measure.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  Identifies the medication or substance or device being dispensed. This is either a link to a resource representing the details of the item or a simple attribute carrying a code that identifies the item from a known list.
  @returns {Reference}
  ###
  suppliedItem: -> if @json['suppliedItem'] then new Reference(@json['suppliedItem'])
  
  ###*
  The individual responsible for dispensing the medication, supplier or device.
  @returns {Reference}
  ###
  supplier: -> if @json['supplier'] then new Reference(@json['supplier'])
  
  ###*
  The time the dispense event occurred.
  @returns {Period}
  ###
  whenPrepared: -> if @json['whenPrepared'] then new Period(@json['whenPrepared'])
  
  ###*
  The time the dispensed item was sent or handed to the patient (or agent).
  @returns {Period}
  ###
  whenHandedOver: -> if @json['whenHandedOver'] then new Period(@json['whenHandedOver'])
  
  ###*
  Identification of the facility/location where the Supply was shipped to, as part of the dispense event.
  @returns {Reference}
  ###
  destination: -> if @json['destination'] then new Reference(@json['destination'])
  
  ###*
  Identifies the person who picked up the Supply.
  @returns {Array} an array of {@link Reference} objects
  ###
  receiver: ->
    if @json['receiver']
      for item in @json['receiver']
        new Reference(item)
  
###*
A supply - a  request for something, and provision of what is supplied.
@class Supply
@exports Supply as Supply
###
class Supply extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Category of supply, e.g.  central, non-stock, etc. This is used to support work flows associated with the supply process.
  @returns {CodeableConcept}
  ###
  kind: -> if @json['kind'] then new CodeableConcept(@json['kind'])
  
  ###*
  Unique identifier for this supply request.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  Status of the supply request.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The item that is requested to be supplied.
  @returns {Reference}
  ###
  orderedItem: -> if @json['orderedItem'] then new Reference(@json['orderedItem'])
  
  ###*
  A link to a resource representing the person whom the ordered item is for.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  Indicates the details of the dispense event such as the days supply and quantity of a supply dispensed.
  @returns {Array} an array of {@link SupplyDispenseComponent} objects
  ###
  dispense: ->
    if @json['dispense']
      for item in @json['dispense']
        new SupplyDispenseComponent(item)
  



module.exports.Supply = Supply
